package com.sciatta.xdfs.namenode;

/**
 * Created by Rain on 2024/2/22<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * 内存双缓存
 */
public class DoubleBuffer {
    /**
     * 单缓存最大值；默认512KB
     */
    public static final Long EDIT_LOG_BUFFER_LIMIT = 512 * 1024L;

    /**
     * 写入操作日志缓存
     */
    private EditLogBuffer currentBuffer = new EditLogBuffer();

    /**
     * 同步操作日志缓存
     */
    private EditLogBuffer syncBuffer = new EditLogBuffer();

    /**
     * 写入操作日志
     *
     * @param log 操作日志
     */
    public void write(EditLog log) {
        currentBuffer.write(log);
    }

    /**
     * 是否超过同步缓存最大阈值
     *
     * @return true，需要同步操作日志；false，不需要
     */
    public boolean shouldSyncToDisk() {
        return currentBuffer.size() >= EDIT_LOG_BUFFER_LIMIT;
    }

    /**
     * 交换写入和同步缓存
     */
    public void setReadyToSync() {
        EditLogBuffer tmp = currentBuffer;
        currentBuffer = syncBuffer;
        syncBuffer = tmp;
    }

    /**
     * 刷写清空同步操作日志缓存
     */
    public void flush() {
        syncBuffer.flush();
        syncBuffer.clear();
    }

    /**
     * 操作日志缓存
     */
    static class EditLogBuffer {
        /**
         * 写入操作日志
         *
         * @param log 操作日志
         */
        public void write(EditLog log) {

        }

        /**
         * 获取当前缓存数据大小
         *
         * @return 当前缓存数据大小
         */
        public Long size() {
            return 0L;
        }

        /**
         * 刷写缓存
         */
        public void flush() {

        }

        /**
         * 清空缓存
         */
        public void clear() {

        }
    }
}
