package com.sciatta.xdfs.namenode;

/**
 * Created by Rain on 2024/2/19<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * 元数据节点
 */
public class NameNode {
    private FSNamesystem fsNamesystem;
    private DataNodeManager dataNodeManager;
    private NameNodeRpcServer nameNodeRpcServer;

    /**
     * 初始化
     */
    private void initialize() {
        this.fsNamesystem = new FSNamesystem();
        this.dataNodeManager = new DataNodeManager();
        this.nameNodeRpcServer = new NameNodeRpcServer(fsNamesystem, dataNodeManager);
    }

    /**
     * 启动
     */
    private void start() throws Exception { // 统一异常处理
        this.nameNodeRpcServer.start();
        this.nameNodeRpcServer.blockUntilShutdown();
    }

    public static void main(String[] args) throws Exception {
        NameNode nameNode = new NameNode();
        nameNode.initialize();
        nameNode.start();
    }
}
