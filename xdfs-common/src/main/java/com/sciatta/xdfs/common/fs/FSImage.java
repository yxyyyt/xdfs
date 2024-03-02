package com.sciatta.xdfs.common.fs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Rain on 2024/2/29<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * 内存文件目录树镜像
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FSImage {
    /**
     * 当前镜像的最大事务日志序号
     */
    private long maxTxid;

    /**
     * 镜像
     */
    private String fsimage;
}
