package com.sciatta.xdfs.backupnode;

import com.sciatta.xdfs.common.fs.FSDirectory;
import com.sciatta.xdfs.common.fs.FSImage;
import com.sciatta.xdfs.common.util.FastJsonUtils;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Rain on 2024/3/7<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * 备份节点管理内存中的文件目录树的核心组件
 */
@Setter
@Getter
public class BackupNodeDirectory extends FSDirectory {
    /**
     * 当前文件目录树对应的最大事务日志序号
     */
    private long maxTxid;

    /**
     * 获取内存文件目录树镜像
     *
     * @return 文件目录树镜像
     */
    public FSImage getFSImage() {
        readLock();
        try {
            String fsimageJson = FastJsonUtils.formatObjectToJsonStringWithClassName(dirTree);
            return new FSImage(maxTxid, fsimageJson);
        } finally {
            readUnlock();
        }
    }

    /**
     * 创建层级目录；若当前目录不存在，则创建
     *
     * @param txid 事务日志序号
     * @param path 目录路径
     * @return true，创建成功；false，创建失败
     */
    public boolean mkdir(long txid, String path) {
        writeLock();
        try {
            mkdir(path);
            this.maxTxid = txid;
        } finally {
            writeUnlock();
        }
        return true;
    }

    /**
     * 创建文件；如果文件所属的层级目录不存在，则先创建
     *
     * @param txid 事务日志序号
     * @param path 文件路径
     * @return true，创建成功；false，创建失败
     */
    public boolean touch(long txid, String path) { // TODO  区分创建失败，重复
        writeLock();
        try {
            if (touch(path)) {
                this.maxTxid = txid;
                return true;
            }
            return false;
        } finally {
            writeUnlock();
        }
    }

}
