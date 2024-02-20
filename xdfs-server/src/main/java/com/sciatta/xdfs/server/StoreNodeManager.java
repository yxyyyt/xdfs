package com.sciatta.xdfs.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Rain on 2024/2/19<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * 管理集群中的存储节点
 */
public class StoreNodeManager {

    /**
     * 集群中所有的存储节点
     */
    private final Map<String, StoreNodeInfo> storeNodes = new ConcurrentHashMap<>();

    public StoreNodeManager() {
        new StoreNodeAliveMonitor().start();
    }

    /**
     * 存储节点注册
     *
     * @param ip       IP地址
     * @param hostname 主机名
     * @return true，注册成功；否则，注册失败
     */
    public Boolean register(String ip, String hostname) {
        StoreNodeInfo storeNodeInfo = new StoreNodeInfo(ip, hostname);
        storeNodes.put(ip + "-" + hostname, storeNodeInfo);
        System.out.println("DataNode注册：ip=" + ip + ",hostname=" + hostname);    // TODO to log
        return true;
    }

    /**
     * 存储节点心跳
     *
     * @param ip       IP地址
     * @param hostname 主机名
     * @return true，心跳成功；否则，心跳失败
     */
    public Boolean heartbeat(String ip, String hostname) {
        StoreNodeInfo storeNodeInfo = storeNodes.get(ip + "-" + hostname);
        storeNodeInfo.setLatestHeartbeatTime(System.currentTimeMillis());
        System.out.println("DataNode发送心跳：ip=" + ip + ",hostname=" + hostname);  // TODO to log
        return true;
    }

    /**
     * 存储节点存活监控线程
     */
    class StoreNodeAliveMonitor extends Thread {    // TODO 统一线程调度

        @Override
        public void run() {
            try {
                while (true) {
                    List<String> toRemoveStoreNodes = new ArrayList<>();

                    Iterator<StoreNodeInfo> iterator = storeNodes.values().iterator();
                    StoreNodeInfo storeNodeInfo;
                    while (iterator.hasNext()) {
                        storeNodeInfo = iterator.next();
                        // TODO 参数化配置
                        if (System.currentTimeMillis() - storeNodeInfo.getLatestHeartbeatTime() > 90 * 1000) {
                            toRemoveStoreNodes.add(storeNodeInfo.getIp() + "-" + storeNodeInfo.getHostname());
                        }
                    }

                    if (!toRemoveStoreNodes.isEmpty()) {
                        for (String toRemoveStoreNode : toRemoveStoreNodes) {
                            storeNodes.remove(toRemoveStoreNode);
                        }
                    }

                    Thread.sleep(30 * 1000);    // TODO 参数化
                }
            } catch (Exception ignore) {
            }
        }

    }
}
