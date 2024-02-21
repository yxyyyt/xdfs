package com.sciatta.xdfs.datanode;

import com.sciatta.xdfs.namenode.rpc.model.HeartbeatRequest;
import com.sciatta.xdfs.namenode.rpc.model.HeartbeatResponse;
import com.sciatta.xdfs.namenode.rpc.model.RegisterRequest;
import com.sciatta.xdfs.namenode.rpc.model.RegisterResponse;
import com.sciatta.xdfs.namenode.rpc.service.NameNodeRpcServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NegotiationType;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;

/**
 * Created by Rain on 2024/2/21<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * 同元数据节点通信的核心组件
 */
public class NameNodeServiceActor {
    private static final String NAMENODE_HOSTNAME = "localhost";
    private static final Integer NAMENODE_PORT = 50070;

    private NameNodeRpcServiceGrpc.NameNodeRpcServiceBlockingStub namenode;

    public NameNodeServiceActor() {
        ManagedChannel channel = NettyChannelBuilder
                .forAddress(NAMENODE_HOSTNAME, NAMENODE_PORT)
                .negotiationType(NegotiationType.PLAINTEXT)
                .build();
        this.namenode = NameNodeRpcServiceGrpc.newBlockingStub(channel);
    }

    /**
     * 向元数据节点发起注册
     */
    public void register() throws Exception {
        Thread registerThread = new RegisterThread();
        registerThread.start();
        registerThread.join();
    }

    /**
     * 向元数据节点发起心跳
     */
    public void heartbeat() {
        new HeartbeatThread().start();
    }

    /**
     * 向元数据节点发起注册的线程
     */
    class RegisterThread extends Thread {

        @Override
        public void run() {
            try {
                String ip = "127.0.0.1";
                String hostname = "dfs-data-01";

                RegisterRequest request = RegisterRequest.newBuilder()
                        .setIp(ip)
                        .setHostname(hostname)
                        .build();
                RegisterResponse response = namenode.register(request);
                System.out.println("接收到NameNode返回的注册响应：" + response.getStatus());   // TODO to log
            } catch (Exception ignore) {
            }
        }

    }

    /**
     * 向元数据节点发起心跳的线程
     */
    class HeartbeatThread extends Thread {

        @Override
        public void run() {
            try {
                while (true) {
                    String ip = "127.0.0.1";
                    String hostname = "dfs-data-01";

                    HeartbeatRequest request = HeartbeatRequest.newBuilder()
                            .setIp(ip)
                            .setHostname(hostname)
                            .build();
                    HeartbeatResponse response = namenode.heartbeat(request);
                    System.out.println("接收到NameNode返回的心跳响应：" + response.getStatus());   // TODO to log

                    Thread.sleep(30 * 1000);
                }
            } catch (Exception ignore) {
            }
        }

    }
}
