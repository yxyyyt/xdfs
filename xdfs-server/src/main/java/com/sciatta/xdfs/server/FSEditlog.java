package com.sciatta.xdfs.server;

import java.util.LinkedList;

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
    private DoubleBuffer editLogBuffer = new DoubleBuffer();

    /**
     * 当前是否有线程正在同步操作日志
     */
    private volatile Boolean isSyncRunning = false;

    /**
     * 当前是否有线程正在等待同步下一批操作日志
     */
    private volatile Boolean isWaitSync = false;

    /**
     * 同步缓存中最大的操作日志序号
     */
    private volatile Long syncMaxTxid = 0L;

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
            txidSeq++;
            long txid = txidSeq;
            localTxid.set(txid); // 本地线程副本

            EditLog log = new EditLog(txid, content);
            editLogBuffer.write(log);
        }

        // TODO 同步加入阈值判断
        logSync();
    }

    /**
     * 同步操作日志
     */
    private void logSync() {
        synchronized (this) {
            if (isSyncRunning) {    // 有线程正在同步

                long txid = localTxid.get();

                if (txid <= syncMaxTxid) {  // 当前线程记录的操作日志已经存在线程正在同步
                    return;
                }

                if (isWaitSync) {   // 已经存在等待同步下一批操作日志的线程
                    return;
                }

                // 当前线程正在等待同步下一批操作日志
                isWaitSync = true;
                while (isSyncRunning) {
                    try {
                        wait(2000);
                    } catch (Exception ignore) {
                    }
                }
                isWaitSync = false;
            }

            editLogBuffer.setReadyToSync();
            syncMaxTxid = editLogBuffer.getSyncMaxTxid();   // 记录正在同步的最大操作日志序号
            isSyncRunning = true;
        }

        editLogBuffer.flush();

        synchronized (this) {
            isSyncRunning = false;
            notifyAll();
        }
    }

    /**
     * 操作日志
     */
    class EditLog {
        /**
         * 操作日志序号
         */
        long txid;

        /**
         * 操作日志内容
         */
        String content;

        public EditLog(long txid, String content) {
            this.txid = txid;
            this.content = content;
        }

        @Override
        public String toString() {
            return "txid=" + txid + ", content=" + content;
        }
    }

    /**
     * 内存双缓存
     */
    class DoubleBuffer {

        /**
         * 写入操作日志缓存
         */
        LinkedList<EditLog> currentBuffer = new LinkedList<>();

        /**
         * 同步操作日志缓存
         */
        LinkedList<EditLog> syncBuffer = new LinkedList<>();

        /**
         * 写入操作日志
         *
         * @param log 操作日志
         */
        public void write(EditLog log) {
            currentBuffer.add(log);
        }

        /**
         * 交换写入和同步缓存
         */
        public void setReadyToSync() {
            LinkedList<EditLog> tmp = currentBuffer;
            currentBuffer = syncBuffer;
            syncBuffer = tmp;
        }

        /**
         * 获取同步缓存中最大的操作日志序号
         *
         * @return 同步缓存中最大的操作日志序号
         */
        public Long getSyncMaxTxid() {
            return syncBuffer.getLast().txid;
        }

        /**
         * 持久化同步缓存
         */
        public void flush() {
            for (EditLog log : syncBuffer) {
                // TODO 持久化操作日志
                System.out.println("flush log: " + log);
            }
            syncBuffer.clear();
        }

    }
}
