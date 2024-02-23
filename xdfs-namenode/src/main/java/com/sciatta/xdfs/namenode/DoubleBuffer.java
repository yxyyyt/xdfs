package com.sciatta.xdfs.namenode;

import com.sciatta.xdfs.common.util.FastJsonUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by Rain on 2024/2/22<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * 内存双缓存
 */
@Slf4j
public class DoubleBuffer {
    /**
     * 单缓存容量最大值，单位：字节
     */
    public static final int EDIT_LOG_BUFFER_LIMIT = 25 * 1024;  // TODO to config

    /**
     * 事务日志保存路径
     */
    public static final String EDIT_LOG_FILE_PATH = "D:\\data\\project\\xdfs\\editlog\\";   // TODO to config

    /**
     * 写入事务日志缓存
     */
    private EditLogBuffer currentBuffer = new EditLogBuffer();

    /**
     * 同步事务日志缓存
     */
    private EditLogBuffer syncBuffer = new EditLogBuffer();

    /**
     * 刷写缓存最小的事务日志序号
     */
    private long startTxid = 1L;

    /**
     * 写入事务日志
     *
     * @param editLog 事务日志
     */
    public void write(EditLog editLog) throws IOException {
        currentBuffer.write(editLog);
    }

    /**
     * 是否超过同步缓存最大阈值
     *
     * @return true，需要同步事务日志；false，不需要
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
     * 刷写清空同步事务日志缓存
     */
    public void flush() throws IOException {
        syncBuffer.flush();
        syncBuffer.clear();
    }

    /**
     * 事务日志缓存
     */
    private class EditLogBuffer {
        /**
         * 持有缓存中的字节数组
         */
        private final ByteArrayOutputStream buffer;

        /**
         * 刷写缓存最大的事务日志序号
         */
        long endTxid = 0L;

        public EditLogBuffer() {
            this.buffer = new ByteArrayOutputStream(EDIT_LOG_BUFFER_LIMIT * 2);
        }

        /**
         * 写入事务日志
         *
         * @param editLog 事务日志
         */
        public void write(EditLog editLog) throws IOException {
            endTxid = editLog.getTxid();
            buffer.write(FastJsonUtils.formatObjectToJsonString(editLog).getBytes());
            buffer.write("\n".getBytes());

            log.debug("write a EditLog {}, current buffer size is {}", editLog, size());
        }

        /**
         * 获取当前缓存数据大小
         *
         * @return 当前缓存数据大小
         */
        public int size() {
            return buffer.size();
        }

        /**
         * 刷写缓存
         */
        public void flush() throws IOException {
            byte[] data = buffer.toByteArray();
            ByteBuffer dataBuffer = ByteBuffer.wrap(data);

            String editsLogFilePath = EDIT_LOG_FILE_PATH + "edits-"
                    + startTxid + "-" + endTxid + ".log";

            try (RandomAccessFile file = new RandomAccessFile(editsLogFilePath, "rw");
                 FileOutputStream out = new FileOutputStream(file.getFD());
                 FileChannel editsLogFileChannel = out.getChannel()) {
                editsLogFileChannel.write(dataBuffer);
                editsLogFileChannel.force(false);
            }

            startTxid = endTxid + 1;
        }

        /**
         * 清空缓存
         */
        public void clear() {
            buffer.reset();
        }
    }
}
