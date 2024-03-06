package com.sciatta.xdfs.namenode;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Rain on 2024/2/19<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * 数据节点信息
 */
@Setter
@Getter
public class DataNodeInfo {
    /**
     * IP地址
     */
    private String ip;

    /**
     * 主机名
     */
    private String hostname;

    /**
     * 最近一次心跳时间
     */
    private long latestHeartbeatTime = System.currentTimeMillis();

    public DataNodeInfo(String ip, String hostname) {
        this.ip = ip;
        this.hostname = hostname;
    }

}
