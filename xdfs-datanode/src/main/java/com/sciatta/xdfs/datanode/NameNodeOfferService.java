package com.sciatta.xdfs.datanode;

/**
 * Created by Rain on 2024/2/21<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * 同主节点业务处理的核心组件
 */
public class NameNodeOfferService {
    private final NameNodeServiceActor nameNodeServiceActor;

    public NameNodeOfferService() {
        this.nameNodeServiceActor = new NameNodeServiceActor();
    }

    /**
     * 启动
     */
    public void start() {
        register();
        heartbeat();
    }

    /**
     * 向主节点发起注册
     */
    private void register() {
        try {
            this.nameNodeServiceActor.register();
        } catch (Exception ignore) {
        }
    }

    /**
     * 向主节点发起心跳
     */
    private void heartbeat() {
        this.nameNodeServiceActor.heartbeat();
    }

}
