package com.sciatta.xdfs.server;

/**
 * Created by Rain on 2024/2/19<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * 存储节点信息
 */
public class StoreNodeInfo {
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

    public StoreNodeInfo(String ip, String hostname) {
        this.ip = ip;
        this.hostname = hostname;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public long getLatestHeartbeatTime() {
        return latestHeartbeatTime;
    }

    public void setLatestHeartbeatTime(long latestHeartbeatTime) {
        this.latestHeartbeatTime = latestHeartbeatTime;
    }
}
