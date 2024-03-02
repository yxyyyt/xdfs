package com.sciatta.xdfs.backupnode;

import com.sciatta.xdfs.common.fs.FSImage;
import com.sciatta.xdfs.common.util.ConcurrentUtils;
import com.sciatta.xdfs.common.util.DateTimeUtils;
import com.sciatta.xdfs.common.util.PathUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by Rain on 2024/2/29<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * 生成镜像检查点组件
 */
@Slf4j
public class FSImageCheckpointer extends Thread {
    /**
     * 检查点操作的时间间隔
     */
    private static final int CHECKPOINT_INTERVAL = 30;

    private final FSNameSystem nameSystem;
    private final NameNodeRpcClient nameNodeRpcClient;

    public FSImageCheckpointer(FSNameSystem nameSystem, NameNodeRpcClient nameNodeRpcClient) {
        this.nameSystem = nameSystem;
        this.nameNodeRpcClient = nameNodeRpcClient;
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (!this.nameSystem.isRecover()) {
                    log.debug("NameSystem is recovering");
                    ConcurrentUtils.silentSleepToSeconds(1);
                    continue;
                }

                long now = DateTimeUtils.currentTimeMillis();
                if (now - this.nameSystem.getCheckpointTime() > CHECKPOINT_INTERVAL) {
                    doCheckpoint();
                }

                ConcurrentUtils.silentSleepToSeconds(1);
            } catch (IOException e) {
                log.error("do checkpoint catch exception {}", e.getMessage());
            }
        }
    }

    /**
     * 执行检查点操作
     *
     * @throws IOException IO异常
     */
    private void doCheckpoint() throws IOException {
        FSImage fsimage = this.nameSystem.getFSImage();
        removeLastFSImageFile();
        writeFSImageFile(fsimage);
        uploadFSImageFile(fsimage);
        updateCheckpointTxid(fsimage);
        saveCheckpointInfo(fsimage);
    }

    /**
     * 删除最近一个镜像
     */
    private void removeLastFSImageFile() {
        File file = new File(PathUtils.getBackupNodeImagePath(this.nameSystem.getSyncedTxid()));
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 持久化最新的镜像
     *
     * @param fsimage 镜像
     * @throws IOException IO异常
     */
    private void writeFSImageFile(FSImage fsimage) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(fsimage.getFsimage().getBytes());

        String fsimageFilePath = PathUtils.getBackupNodeImagePath(fsimage.getMaxTxid());

        this.nameSystem.setSyncedTxid(fsimage.getMaxTxid());
        this.nameSystem.setCheckpointTime(DateTimeUtils.currentTimeMillis());

        try (RandomAccessFile file = new RandomAccessFile(fsimageFilePath, "rw");
             FileOutputStream out = new FileOutputStream(file.getFD());
             FileChannel channel = out.getChannel()) {
            channel.write(buffer);
            channel.force(false);
        }
    }

    /**
     * 上传镜像到主节点
     *
     * @param fsimage 镜像
     */
    private void uploadFSImageFile(FSImage fsimage) {
        FSImageUploader fsimageUploader = new FSImageUploader(fsimage);
        fsimageUploader.start();
    }


    /**
     * 向主节点更新检查点事务日志序号
     *
     * @param fsimage 镜像
     */
    private void updateCheckpointTxid(FSImage fsimage) {
        this.nameNodeRpcClient.updateCheckpointTxid(fsimage.getMaxTxid());
    }

    /**
     * 保存检查点信息
     *
     * @param fsimage 镜像
     */
    private void saveCheckpointInfo(FSImage fsimage) throws IOException {
        String path = PathUtils.getBackupNodeCheckpointInfoPath();

        RandomAccessFile raf = null;
        FileOutputStream out = null;
        FileChannel channel = null;

        try {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }

            String checkpointInfo = this.nameSystem.getCheckpointTime() + "_" + this.nameSystem.getSyncedTxid();

            ByteBuffer buffer = ByteBuffer.wrap(checkpointInfo.getBytes());

            raf = new RandomAccessFile(path, "rw");
            out = new FileOutputStream(raf.getFD());
            channel = out.getChannel();

            channel.write(buffer);
            channel.force(false);

            log.debug("save checkpoint info {}", checkpointInfo);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (raf != null) {
                    raf.close();
                }
                if (channel != null) {
                    channel.close();
                }
            } catch (Exception ignore) {
            }
        }
    }
}
