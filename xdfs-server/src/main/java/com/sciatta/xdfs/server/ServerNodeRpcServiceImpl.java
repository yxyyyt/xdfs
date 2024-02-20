package com.sciatta.xdfs.server;

import com.sciatta.xdfs.server.rpc.model.*;
import com.sciatta.xdfs.server.rpc.service.ServerNodeRpcServiceGrpc;
import io.grpc.stub.StreamObserver;

/**
 * Created by Rain on 2024/2/20<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * 服务节点的Rpc服务实现
 */
public class ServerNodeRpcServiceImpl extends ServerNodeRpcServiceGrpc.ServerNodeRpcServiceImplBase {
    /**
     * 成功
     */
    public static final Integer STATUS_SUCCESS = 1;

    /**
     * 失败
     */
    public static final Integer STATUS_FAILURE = 2;

    private final FSNamesystem namesystem;

    private final StoreNodeManager storeNodeManager;

    public ServerNodeRpcServiceImpl(FSNamesystem namesystem, StoreNodeManager storeNodeManager) {
        this.namesystem = namesystem;
        this.storeNodeManager = storeNodeManager;
    }

    @Override
    public void register(RegisterRequest request, StreamObserver<RegisterResponse> responseObserver) {
        this.storeNodeManager.register(request.getIp(), request.getHostname());

        RegisterResponse response = RegisterResponse.newBuilder()
                .setStatus(STATUS_SUCCESS)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void heartbeat(HeartbeatRequest request, StreamObserver<HeartbeatResponse> responseObserver) {
        this.storeNodeManager.heartbeat(request.getIp(), request.getHostname());

        HeartbeatResponse response = HeartbeatResponse.newBuilder()
                .setStatus(STATUS_SUCCESS)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void mkdir(MkdirRequest request, StreamObserver<MkdirResponse> responseObserver) {
        this.namesystem.mkdir(request.getPath());

        // TODO to log
        System.out.println("创建目录：path" + request.getPath());

        MkdirResponse response = MkdirResponse.newBuilder()
                .setStatus(STATUS_SUCCESS)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
