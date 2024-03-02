package com.sciatta.xdfs.namenode;

/**
 * Created by Rain on 2024/2/19<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * 主节点
 */
public class NameNode {
    private FSNameSystem nameSystem;
    private DataNodeManager dataNodeManager;
    private NameNodeRpcServer nameNodeRpcServer;
    private FSImageUploadServer imageUploadServer;

    /**
     * 初始化
     */
    private void initialize() {
        this.nameSystem = new FSNameSystem();
        this.dataNodeManager = new DataNodeManager();
        this.nameNodeRpcServer = new NameNodeRpcServer(nameSystem, dataNodeManager);
        this.imageUploadServer = new FSImageUploadServer();
    }

    /**
     * 启动
     */
    private void start() throws Exception { // 统一异常处理
        this.imageUploadServer.start();
        this.nameNodeRpcServer.start();
        this.nameNodeRpcServer.blockUntilShutdown();
    }

    public static void main(String[] args) throws Exception {
        NameNode nameNode = new NameNode();
        nameNode.initialize();
        nameNode.start();
    }
}
