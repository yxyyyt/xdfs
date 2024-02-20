package com.sciatta.xdfs.server;

/**
 * Created by Rain on 2024/2/19<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * 服务节点
 */
public class ServerNode {
    private FSNamesystem fsNamesystem;
    private StoreNodeManager storeNodeManager;
    private ServerNodeRpcServer serverNodeRpcServer;

    /**
     * 初始化服务节点
     */
    private void initialize() {
        this.fsNamesystem = new FSNamesystem();
        this.storeNodeManager = new StoreNodeManager();
        this.serverNodeRpcServer = new ServerNodeRpcServer(fsNamesystem, storeNodeManager);
    }

    /**
     * 启动服务节点
     */
    private void start() throws Exception { // 统一异常处理
        this.serverNodeRpcServer.start();
        this.serverNodeRpcServer.blockUntilShutdown();
    }

    public static void main(String[] args) throws Exception {
        ServerNode serverNode = new ServerNode();
        serverNode.initialize();
        serverNode.start();
    }
}
