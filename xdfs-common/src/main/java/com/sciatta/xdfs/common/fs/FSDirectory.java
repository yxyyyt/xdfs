package com.sciatta.xdfs.common.fs;

import com.sciatta.xdfs.common.util.PathUtils;
import com.sciatta.xdfs.common.util.StringUtils;
import lombok.Setter;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Rain on 2024/2/19<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * 管理内存中的文件目录树的核心组件，非线程安全，线程安全由子类保证
 */
public abstract class FSDirectory {
    /**
     * 根目录
     */
    private static final String ROOT_PATH = "/";

    /**
     * 内存中的文件目录树
     */
    @Setter
    protected INodeDirectory dirTree;

    /**
     * 内存中的文件目录树操作读写锁
     */
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public FSDirectory() {
        this.dirTree = new INodeDirectory(ROOT_PATH); // 初始化根目录
    }

    /**
     * 创建层级目录；若当前目录不存在，则创建
     *
     * @param path 目录路径
     * @return 最后一级目录
     */
    protected INodeDirectory mkdir(String path) {
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
        return parent;
    }

    /**
     * 创建文件；如果文件所属的层级目录不存在，则先创建
     *
     * @param path 文件路径
     * @return true，创建成功；false，创建失败
     */
    protected boolean touch(String path) { // TODO  区分创建失败，重复
        String filePath = PathUtils.getFilePath(path);
        String filename = PathUtils.getFileName(path);

        if (filePath == null) return false; // 路径可以为空，如果为空时，则创建文件到根路径

        if (StringUtils.isBlank(filename)) return false;

        // 创建目录
        INodeDirectory currentDir = mkdir(filePath);
        if (isFileExist(currentDir, filename)) return false;

        return currentDir.getChildren().add(new INodeFile(filename));
    }

    /**
     * 指定目录下文件是否存在
     *
     * @param directory 指定目录
     * @param filename  文件名
     * @return true，存在；false，不存在
     */
    private boolean isFileExist(INodeDirectory directory, String filename) {
        if (directory != null && !directory.getChildren().isEmpty()) {
            for (INode node : directory.getChildren()) {
                if (node instanceof INodeFile && ((INodeFile) node).getName().equals(filename)) {
                    return true;
                }
            }
        }

        return false;
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
    protected void writeLock() {
        lock.writeLock().lock();
    }

    /**
     * 写锁释放锁
     */
    protected void writeUnlock() {
        lock.writeLock().unlock();
    }

    /**
     * 读锁加锁
     */
    protected void readLock() {
        lock.readLock().lock();
    }

    /**
     * 读锁释放锁
     */
    protected void readUnlock() {
        lock.readLock().unlock();
    }
}
