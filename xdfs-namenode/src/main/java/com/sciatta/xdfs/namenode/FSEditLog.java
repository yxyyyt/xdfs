package com.sciatta.xdfs.namenode;

import com.sciatta.xdfs.common.fs.EditLog;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

/**
 * Created by Rain on 2024/2/19<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * 管理事务日志的核心组件
 */
@Slf4j
public class FSEditLog {
    /**
     * 当前事务日志序号
     */
    private long txidSeq = 0L;

    /**
     * 内存双缓存
     */
    private final DoubleBuffer doubleBuffer = new DoubleBuffer();

    /**
     * 当前是否有线程正在刷写双缓存
     */
    private volatile Boolean isSyncRunning = false;

    /**
     * 当前是否有线程正在调度同步操作
     */
    private volatile Boolean isSchedulingSync = false;

    /**
     * 同步中最大的事务日志序号
     */
    private volatile Long syncTxid = 0L;

    /**
     * 每个线程自己本地的事务日志序号
     */
    private final ThreadLocal<Long> localTxid = new ThreadLocal<>();

    /**
     * 记录事务日志
     *
     * @param editLogFactory 事务日志工厂
     */
    public void logEdit(EditLogFactory editLogFactory) {
        synchronized (this) {
            waitSchedulingSync();

            txidSeq++;
            long txid = txidSeq;
            localTxid.set(txid); // 本地线程副本

            EditLog editLog = editLogFactory.create(txid);
            try {
                doubleBuffer.write(editLog);
            } catch (IOException e) {
                log.error("write {} to double buffer catch exception {}", editLog, e.getMessage());
            }

            if (!doubleBuffer.shouldSyncToDisk()) {
                return;
            }

            isSchedulingSync = true;    // 写入缓存需要刷盘，开始调度双缓存
        }

        logSync();
    }

    /**
     * 强制刷写清空同步事务日志缓存
     */
    public void flush() {
        try {
            doubleBuffer.setReadyToSync();
            doubleBuffer.flush();
        } catch (IOException e) {
            log.error("force flush double buffer catch exception {}", e.getMessage());
        }
    }

    /**
     * 获取已经同步成功的事务日志序号
     *
     * @return 同步成功的事务日志序号
     */
    public List<String> getFlushedTxids() {
        synchronized (this) {
            return doubleBuffer.getFlushedTxids();
        }
    }

    /**
     * 获取当前内存中缓存的事务日志
     *
     * @return 当前内存中缓存的事务日志
     */
    public String[] getBufferedEditLog() {
        synchronized (this) {
            return doubleBuffer.getBufferedEditLog();
        }
    }

    /**
     * 等待其他线程切换双缓存完成，避免写入缓存过载
     */
    private void waitSchedulingSync() {
        try {
            while (isSchedulingSync) {
                wait(1000);
            }
        } catch (Exception ignore) {    // TODO 异常统一处理
        }
    }

    /**
     * 同步事务日志
     */
    private void logSync() {
        synchronized (this) {
            long txid = localTxid.get();
            localTxid.remove();

            while (txid > syncTxid && isSyncRunning) {   // 有线程正在同步，同时当前线程需要同步未同步的数据
                try {
                    wait(1000);
                } catch (InterruptedException ignore) { // TODO 异常统一处理
                }
            }

            if (txid <= syncTxid) {
                throw new RuntimeException("当前线程记录的事务日志已经存在线程同步");  // TODO 为什么走到这里？
            }

            doubleBuffer.setReadyToSync();
            syncTxid = txid;   // 记录正在同步的最大事务日志序号
            isSchedulingSync = false;
            isSyncRunning = true;
            notifyAll();
        }

        try {
            doubleBuffer.flush();
        } catch (IOException e) {
            log.error("flush double buffer catch exception {}", e.getMessage());
        }

        synchronized (this) {
            isSyncRunning = false;
            notifyAll();
        }
    }
}
