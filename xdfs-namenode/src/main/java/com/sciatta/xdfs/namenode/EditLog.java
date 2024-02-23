package com.sciatta.xdfs.namenode;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Rain on 2024/2/22<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * 事务日志
 */
@Data
@NoArgsConstructor
public class EditLog {
    /**
     * 事务日志序号
     */
    private long txid;

    /**
     * 操作
     */
    private String operate;

    /**
     * 路径
     */
    private String path;
}
