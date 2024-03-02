package com.sciatta.xdfs.backupnode;

import com.sciatta.xdfs.common.fs.FSDirectory;
import com.sciatta.xdfs.common.fs.FSImage;
import com.sciatta.xdfs.common.util.FastJsonUtils;
import com.sciatta.xdfs.common.util.PathUtils;
import com.sciatta.xdfs.common.util.SystemUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by Rain on 2024/2/26<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * 管理元数据的核心组件
 */
@Slf4j
public class FSNameSystem {

    private final FSDirectory directory;

    /**
     * 上一次同步的事务日志序号
     */
    @Getter
    @Setter
    private long syncedTxid;

    /**
     * 上一次检查点时间
     */
    @Getter
    @Setter
    private long checkpointTime;

    /**
     * 元数据是否恢复完成
     */
    @Getter
    private volatile boolean recover = false;

    public FSNameSystem() {
        this.directory = new FSDirectory();
        recoverNameSystem();
    }

    /**
     * 创建目录
     *
     * @param txid 当前事务日志序号
     * @param path 目录路径
     * @return 是否成功
     */
    public Boolean mkdir(long txid, String path) {
        this.directory.mkdir(txid, path);
        return true;
    }

    /**
     * 获取内存文件目录树镜像
     *
     * @return 文件目录树镜像
     */
    public FSImage getFSImage() {
        return directory.getFSImage();
    }

    /**
     * 恢复元数据
     */
    private void recoverNameSystem() {
        try {
            loadCheckpointInfo();
            loadFSImage();
            recover = true;
        } catch (Exception e) {
            SystemUtils.normalExit();
        }
    }

    /**
     * 加载镜像
     *
     * @throws IOException IO异常
     */
    private void loadFSImage() throws IOException {
        FileInputStream in = null;
        FileChannel channel = null;
        try {
            String path = PathUtils.getBackupNodeImagePath(this.syncedTxid);
            File file = new File(path);
            if (!file.exists()) {
                log.debug("load FSImage but not exist");
                return;
            }

            in = new FileInputStream(path);
            channel = in.getChannel();

            ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
            int count = channel.read(buffer);

            buffer.flip();
            String fsimageJson = new String(buffer.array(), 0, count);
            FSDirectory.INodeDirectory iNodeDirectory = FastJsonUtils.parseJsonStringToObject(fsimageJson, FSDirectory.INodeDirectory.class);

            this.directory.setDirTree(iNodeDirectory);
            this.directory.setMaxTxid(this.syncedTxid);

            log.debug("load FSImage success, maxTxid {}", this.syncedTxid);
        } finally {
            if (in != null) {
                in.close();
            }
            if (channel != null) {
                channel.close();
            }
        }
    }

    /**
     * 加载检查点信息
     *
     * @throws IOException IO异常
     */
    private void loadCheckpointInfo() throws IOException {
        FileInputStream in = null;
        FileChannel channel = null;
        try {
            String path = PathUtils.getBackupNodeCheckpointInfoPath();

            File file = new File(path);
            if (!file.exists()) {
                log.debug("load checkpoint info but not exist");
                return;
            }

            in = new FileInputStream(path);
            channel = in.getChannel();

            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int count = channel.read(buffer);

            buffer.flip();

            String checkpointInfo = new String(buffer.array(), 0, count);
            long checkpointTime = Long.parseLong(checkpointInfo.split("_")[0]);
            long syncedTxid = Long.parseLong(checkpointInfo.split("_")[1]);

            this.checkpointTime = checkpointTime;
            this.syncedTxid = syncedTxid;
            log.debug("load checkpoint time {}, synced txid {}", this.checkpointTime, this.syncedTxid);
        } finally {
            if (in != null) {
                in.close();
            }
            if (channel != null) {
                channel.close();
            }
        }
    }
}
