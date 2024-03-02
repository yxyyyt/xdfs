package com.sciatta.xdfs.backupnode;

import com.sciatta.xdfs.common.fs.FSDirectory;
import com.sciatta.xdfs.common.fs.FSImage;
import lombok.Getter;

/**
 * Created by Rain on 2024/2/26<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * 管理元数据的核心组件
 */
public class FSNameSystem {

    private final FSDirectory directory;

    @Getter
    private long syncedTxid;

    public FSNameSystem() {
        this.directory = new FSDirectory();
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
        this.syncedTxid = txid;
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
}
