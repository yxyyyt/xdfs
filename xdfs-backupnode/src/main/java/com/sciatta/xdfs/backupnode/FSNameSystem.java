package com.sciatta.xdfs.backupnode;

import com.sciatta.xdfs.common.fs.FSDirectory;

/**
 * Created by Rain on 2024/2/26<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * 管理元数据的核心组件
 */
public class FSNameSystem {

    private final FSDirectory directory;

    public FSNameSystem() {
        this.directory = new FSDirectory();
    }

    /**
     * 创建目录
     *
     * @param path 目录路径
     * @return 是否成功
     */
    public Boolean mkdir(String path) {
        this.directory.mkdir(path);
        return true;
    }
}
