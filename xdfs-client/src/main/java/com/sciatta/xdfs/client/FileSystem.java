package com.sciatta.xdfs.client;

import java.io.IOException;

/**
 * Created by Rain on 2024/2/21<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * 文件系统接口
 */
public interface FileSystem {
    /**
     * 创建目录
     *
     * @param path 目录路径
     * @throws IOException IO异常
     */
    void mkdir(String path) throws IOException;
}
