package com.sciatta.xdfs.namenode;

/**
 * Created by Rain on 2024/2/19<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * 管理元数据的核心组件
 */
public class FSNameSystem {
    private final FSDirectory directory;
    private final FSEditLog editlog;

    public FSNameSystem() {
        this.directory = new FSDirectory();
        this.editlog = new FSEditLog();
    }

    /**
     * 创建目录
     *
     * @param path 目录路径
     * @return 是否成功
     */
    public Boolean mkdir(String path) {
        this.directory.mkdir(path);
        this.editlog.logEdit(txid -> {
            EditLog editLog = new EditLog();
            editLog.setTxid(txid);
            editLog.setPath(path);
            editLog.setOperate(EditLogOperateEnum.MKDIR.getOperate());
            return editLog;
        });
        return true;
    }

    /**
     * 强制刷写清空同步事务日志缓存
     */
    public void flush() {
        this.editlog.flush();
    }
}
