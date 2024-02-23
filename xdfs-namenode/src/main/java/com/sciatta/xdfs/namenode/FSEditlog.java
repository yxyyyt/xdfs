package com.sciatta.xdfs.namenode;

/**
 * Created by Rain on 2024/2/19<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * 管理操作日志的核心组件
 */
public class FSEditlog {
    /**
     * 当前操作日志序号
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
     * 同步中最大的操作日志序号
     */
    private volatile Long syncTxid = 0L;

    /**
     * 每个线程自己本地的操作日志序号
     */
    private ThreadLocal<Long> localTxid = new ThreadLocal<>();

    /**
     * 记录操作日志
     *
     * @param content 操作日志内容
     */
    public void logEdit(String content) {
        synchronized (this) {
            waitSchedulingSync();

            txidSeq++;
            long txid = txidSeq;
            localTxid.set(txid); // 本地线程副本

            EditLog log = new EditLog(txid, content);
            doubleBuffer.write(log);

            if (!doubleBuffer.shouldSyncToDisk()) {
                return;
            }

            isSchedulingSync = true;    // 写入缓存需要刷盘，开始调度双缓存
        }

        logSync();
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
     * 同步操作日志
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
                throw new RuntimeException("当前线程记录的操作日志已经存在线程同步");  // TODO 为什么走到这里？
            }

            doubleBuffer.setReadyToSync();
            syncTxid = txid;   // 记录正在同步的最大操作日志序号
            isSchedulingSync = false;
            isSyncRunning = true;
            notifyAll();
        }

        doubleBuffer.flush();

        synchronized (this) {
            isSyncRunning = false;
            notifyAll();
        }
    }
}
