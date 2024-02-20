package com.sciatta.xdfs.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

/**
 * Created by Rain on 2024/2/20<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * 服务节点的Rpc服务
 */
public class ServerNodeRpcServer {
    private static final int DEFAULT_PORT = 50070;

    private Server server = null;

    private final FSNamesystem fsNamesystem;

    private final StoreNodeManager storeNodeManager;

    public ServerNodeRpcServer(FSNamesystem fsNamesystem, StoreNodeManager storeNodeManager) {
        this.fsNamesystem = fsNamesystem;
        this.storeNodeManager = storeNodeManager;
    }

    /**
     * 启动服务节点的Rpc服务
     *
     * @throws IOException IO异常
     */
    public void start() throws IOException {
        server = ServerBuilder
                .forPort(DEFAULT_PORT)
                .addService(new ServerNodeRpcServiceImpl(fsNamesystem, storeNodeManager))
                .build()
                .start();

        // TODO to log
        System.out.println("ServerNodeRpcServer启动，监听端口号：" + DEFAULT_PORT);

        Runtime.getRuntime().addShutdownHook(new Thread(ServerNodeRpcServer.this::stop));
    }

    /**
     * 停止服务节点的Rpc服务
     */
    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    /**
     * 服务节点的Rpc服务阻塞等待停止
     *
     * @throws InterruptedException 中断异常
     */
    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
}
