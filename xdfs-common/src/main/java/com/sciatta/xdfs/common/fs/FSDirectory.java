package com.sciatta.xdfs.common.fs;

import java.util.LinkedList;
import java.util.List;

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
    private final INodeDirectory dirTree;

    public FSDirectory() {
        this.dirTree = new INodeDirectory(ROOT_PATH); // 初始化根目录
    }

    /**
     * 创建层级目录；若当前目录不存在，则创建
     *
     * @param path 目录路径
     */
    public void mkdir(String path) {
        synchronized (dirTree) {
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
     * 文件目录树中的一个节点
     */
    private interface INode {

    }

    /**
     * 文件目录树中的一个目录
     */
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

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public List<INode> getChildren() {
            return children;
        }

        public void setChildren(List<INode> children) {
            this.children = children;
        }

    }

    /**
     * 文件目录树中的一个文件
     */
    public static class INodeFile implements INode {

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

}
