package com.sciatta.xdfs.common.fs;

import com.sciatta.xdfs.common.util.FastJsonUtils;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Rain on 2024/2/19<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * 管理内存中的文件目录树的核心组件
 */
public class FSDirectory {
    /**
     * 根目录
     */
    private static final String ROOT_PATH = "/";

    /**
     * 内存中的文件目录树
     */
    @Setter
    private INodeDirectory dirTree;

    /**
     * 内存中的文件目录树操作读写锁
     */
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * 当前文件目录树对应的最大事务日志序号
     */
    @Setter
    @Getter
    private long maxTxid;

    public FSDirectory() {
        this.dirTree = new INodeDirectory(ROOT_PATH); // 初始化根目录
    }

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
     * @param txid 当前事务日志序号
     * @param path 目录路径
     */
    public void mkdir(long txid, String path) {
        writeLock();
        try {
            this.maxTxid = txid;
            String[] paths = path.split(ROOT_PATH);
            INodeDirectory parent = dirTree;

            for (String splitPath : paths) {
                if (splitPath.trim().isEmpty()) {
                    continue;
                }

                INodeDirectory dir = findDirectory(parent, splitPath);
                if (dir != null) {
                    parent = dir;
                    continue;
                }

                // 子目录不存在
                INodeDirectory child = new INodeDirectory(splitPath);
                parent.addChild(child);
                parent = child;
            }
        } finally {
            writeUnlock();
        }
    }

    /**
     * 从指定父目录树查找子目录路径是否存在
     *
     * @param parent    父目录树
     * @param childPath 子目录路径
     * @return 若存在，返回子目录树，否则，返回null
     */
    private INodeDirectory findDirectory(INodeDirectory parent, String childPath) {
        if (parent.getChildren().isEmpty()) {
            return null;
        }

        for (INode child : parent.getChildren()) {
            if (child instanceof INodeDirectory) {
                INodeDirectory childDir = (INodeDirectory) child;

                if ((childDir.getPath().equals(childPath))) {
                    return childDir;
                }
            }
        }

        return null;
    }

    /**
     * 写锁加锁
     */
    private void writeLock() {
        lock.writeLock().lock();
    }

    /**
     * 写锁释放锁
     */
    private void writeUnlock() {
        lock.writeLock().unlock();
    }

    /**
     * 读锁加锁
     */
    private void readLock() {
        lock.readLock().lock();
    }

    /**
     * 读锁释放锁
     */
    private void readUnlock() {
        lock.readLock().unlock();
    }

    /**
     * 文件目录树中的一个节点
     */
    public interface INode {

    }

    /**
     * 文件目录树中的一个目录
     */
    @Data
    @NoArgsConstructor
    public static class INodeDirectory implements INode {
        private String path;
        private List<INode> children;

        public INodeDirectory(String path) {
            this.path = path;
            this.children = new LinkedList<>();
        }

        public void addChild(INode inode) {
            this.children.add(inode);
        }
    }

    /**
     * 文件目录树中的一个文件
     */
    @Data
    @NoArgsConstructor
    public static class INodeFile implements INode {
        private String name;
    }
}
