package com.sciatta.xdfs.namenode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Rain on 2024/2/19<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * 管理集群中的数据节点
 */
public class DataNodeManager {

    /**
     * 集群中所有数据节点
     */
    private final Map<String, DataNodeInfo> dataNodes = new ConcurrentHashMap<>();

    public DataNodeManager() {
        new DataNodeAliveMonitor().start();
    }

    /**
     * 数据节点发起注册
     *
     * @param ip       IP地址
     * @param hostname 主机名
     * @return true，注册成功；否则，注册失败
     */
    public Boolean register(String ip, String hostname) {
        DataNodeInfo dataNodeInfo = new DataNodeInfo(ip, hostname);
        dataNodes.put(ip + "-" + hostname, dataNodeInfo);
        System.out.println("DataNode注册：ip=" + ip + ",hostname=" + hostname);    // TODO to log
        return true;
    }

    /**
     * 数据节点发起心跳
     *
     * @param ip       IP地址
     * @param hostname 主机名
     * @return true，心跳成功；否则，心跳失败
     */
    public Boolean heartbeat(String ip, String hostname) {
        DataNodeInfo dataNodeInfo = dataNodes.get(ip + "-" + hostname);
        dataNodeInfo.setLatestHeartbeatTime(System.currentTimeMillis());
        System.out.println("DataNode发送心跳：ip=" + ip + ",hostname=" + hostname);  // TODO to log
        return true;
    }

    /**
     * 数据节点存活监控线程
     */
    class DataNodeAliveMonitor extends Thread {    // TODO 统一线程调度

        @Override
        public void run() {
            try {
                while (true) {
                    List<String> toRemoveDataNodes = new ArrayList<>();

                    Iterator<DataNodeInfo> iterator = dataNodes.values().iterator();
                    DataNodeInfo dataNodeInfo;
                    while (iterator.hasNext()) {
                        dataNodeInfo = iterator.next();
                        // TODO 参数化配置
                        if (System.currentTimeMillis() - dataNodeInfo.getLatestHeartbeatTime() > 90 * 1000) {
                            toRemoveDataNodes.add(dataNodeInfo.getIp() + "-" + dataNodeInfo.getHostname());
                        }
                    }

                    if (!toRemoveDataNodes.isEmpty()) {
                        for (String dataNode : toRemoveDataNodes) {
                            dataNodes.remove(dataNode);
                        }
                    }

                    Thread.sleep(30 * 1000);    // TODO 参数化
                }
            } catch (Exception ignore) {
            }
        }

    }
}
