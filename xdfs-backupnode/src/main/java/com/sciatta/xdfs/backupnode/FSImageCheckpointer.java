package com.sciatta.xdfs.backupnode;

import com.sciatta.xdfs.common.fs.FSImage;
import com.sciatta.xdfs.common.util.ConcurrentUtils;
import com.sciatta.xdfs.common.util.PathUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by Rain on 2024/2/29<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * 生成镜像检查点组件
 */
@Slf4j
public class FSImageCheckpointer extends Thread {

    private static final String FSIMAGE_FILE_PATH = "D:\\data\\project\\xdfs\\backupnode\\";

    /**
     * 检查点操作的时间间隔
     */
    private static final int CHECKPOINT_INTERVAL = 30;

    private final FSNameSystem nameSystem;
    private final NameNodeRpcClient nameNodeRpcClient;
    private String lastFSImage;

    public FSImageCheckpointer(FSNameSystem nameSystem, NameNodeRpcClient nameNodeRpcClient) {
        this.nameSystem = nameSystem;
        this.nameNodeRpcClient = nameNodeRpcClient;
    }

    @Override
    public void run() {
        while (true) {
            ConcurrentUtils.silentSleepToSeconds(CHECKPOINT_INTERVAL);
            try {
                doCheckpoint();
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
    }

    /**
     * 删除最近一个镜像
     */
    private void removeLastFSImageFile() {
        File file = new File(lastFSImage);
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

        String fsimageFilePath = PathUtils.getBackupNodeImagePath(fsimage.getMaxTxid() );
        this.lastFSImage = fsimageFilePath;

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
}
