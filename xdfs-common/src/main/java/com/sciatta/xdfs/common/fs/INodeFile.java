package com.sciatta.xdfs.common.fs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Rain on 2024/3/7<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * 文件目录树中的一个文件
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class INodeFile implements INode {
    private String name;
}
