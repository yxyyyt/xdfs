package com.sciatta.xdfs.common.fs;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Rain on 2024/3/7<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * 文件目录树中的一个目录
 */
@Data
@NoArgsConstructor
public class INodeDirectory implements INode {
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
