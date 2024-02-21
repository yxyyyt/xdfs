package com.sciatta.xdfs.namenode;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

/**
 * Created by Rain on 2024/2/20<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * 名称节点的Rpc服务，基于Grpc
 */
public class NameNodeRpcServer {
    private static final int DEFAULT_PORT = 50070;

    private Server server = null;

    private final FSNamesystem fsNamesystem;

    private final DataNodeManager dataNodeManager;

    public NameNodeRpcServer(FSNamesystem fsNamesystem, DataNodeManager dataNodeManager) {
        this.fsNamesystem = fsNamesystem;
        this.dataNodeManager = dataNodeManager;
    }

    /**
     * 启动Rpc服务
     *
     * @throws IOException IO异常
     */
    public void start() throws IOException {
        server = ServerBuilder
                .forPort(DEFAULT_PORT)
                .addService(new NameNodeRpcServiceImpl(fsNamesystem, dataNodeManager))
                .build()
                .start();

        // TODO to log
        System.out.println("NameNodeRpcServer启动，监听端口号：" + DEFAULT_PORT);

        Runtime.getRuntime().addShutdownHook(new Thread(NameNodeRpcServer.this::stop));
    }

    /**
     * 停止Rpc服务
     */
    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    /**
     * Rpc服务阻塞等待停止
     *
     * @throws InterruptedException 中断异常
     */
    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
}
