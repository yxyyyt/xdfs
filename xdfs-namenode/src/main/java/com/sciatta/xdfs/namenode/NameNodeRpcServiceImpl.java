package com.sciatta.xdfs.namenode;

import com.sciatta.xdfs.namenode.rpc.model.*;
import com.sciatta.xdfs.namenode.rpc.service.NameNodeRpcServiceGrpc;
import io.grpc.stub.StreamObserver;

/**
 * Created by Rain on 2024/2/20<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * 名称节点的Rpc服务实现
 */
public class NameNodeRpcServiceImpl extends NameNodeRpcServiceGrpc.NameNodeRpcServiceImplBase {
    /**
     * 成功
     */
    public static final Integer STATUS_SUCCESS = 0;

    /**
     * 失败
     */
    public static final Integer STATUS_FAILURE = 1;

    private final FSNamesystem namesystem;

    private final DataNodeManager dataNodeManager;

    public NameNodeRpcServiceImpl(FSNamesystem namesystem, DataNodeManager dataNodeManager) {
        this.namesystem = namesystem;
        this.dataNodeManager = dataNodeManager;
    }

    @Override
    public void register(RegisterRequest request, StreamObserver<RegisterResponse> responseObserver) {
        this.dataNodeManager.register(request.getIp(), request.getHostname());

        RegisterResponse response = RegisterResponse.newBuilder()
                .setStatus(STATUS_SUCCESS)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void heartbeat(HeartbeatRequest request, StreamObserver<HeartbeatResponse> responseObserver) {
        this.dataNodeManager.heartbeat(request.getIp(), request.getHostname());

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
        System.out.println("创建目录, path=" + request.getPath());

        MkdirResponse response = MkdirResponse.newBuilder()
                .setStatus(STATUS_SUCCESS)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
