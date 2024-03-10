package com.sciatta.xdfs.namenode;

import com.sciatta.xdfs.common.fs.FSDirectory;
import com.sciatta.xdfs.common.fs.INodeDirectory;

/**
 * Created by yangxiaoyu on 2024/3/10<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA<br><p/>
 * 主节点管理内存中的文件目录树的核心组件
 */
public class NameNodeDirectory extends FSDirectory {
    @Override
    protected INodeDirectory mkdir(String path) {
        writeLock();
        try {
            return super.mkdir(path);
        } finally {
            writeUnlock();
        }
    }

    @Override
    protected boolean touch(String path) {
        writeLock();
        try {
            return super.touch(path);
        } finally {
            writeUnlock();
        }
    }
}
