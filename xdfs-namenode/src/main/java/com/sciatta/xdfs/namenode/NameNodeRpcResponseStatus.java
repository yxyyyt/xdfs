package com.sciatta.xdfs.namenode;

import lombok.Getter;

/**
 * Created by Rain on 2024/2/24<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * 主节点的Rpc服务响应状态
 */
@Getter
public enum NameNodeRpcResponseStatus {
    /**
     * 成功
     */
    SUCCESS(0),

    /**
     * 关闭
     */
    SHUTDOWN(1);

    private final int value;

    NameNodeRpcResponseStatus(int value) {
        this.value = value;
    }
}
