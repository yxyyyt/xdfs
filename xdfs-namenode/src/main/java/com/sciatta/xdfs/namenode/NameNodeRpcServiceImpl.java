package com.sciatta.xdfs.namenode;

import com.sciatta.xdfs.common.fs.EditLog;
import com.sciatta.xdfs.common.util.FastJsonUtils;
import com.sciatta.xdfs.namenode.rpc.model.*;
import com.sciatta.xdfs.namenode.rpc.service.NameNodeRpcServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rain on 2024/2/20<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * 主节点的Rpc服务实现
 */
@Slf4j
public class NameNodeRpcServiceImpl extends NameNodeRpcServiceGrpc.NameNodeRpcServiceImplBase {

    private final FSNameSystem nameSystem;

    private final DataNodeManager dataNodeManager;

    /**
     * 主节点是否正在运行
     */
    private volatile Boolean isRunning = true;

    public NameNodeRpcServiceImpl(FSNameSystem nameSystem, DataNodeManager dataNodeManager) {
        this.nameSystem = nameSystem;
        this.dataNodeManager = dataNodeManager;
    }

    @Override
    public void register(RegisterRequest request, StreamObserver<RegisterResponse> responseObserver) {
        RegisterResponse response;

        if (!isRunning) {
            response = RegisterResponse.newBuilder()
                    .setStatus(NameNodeRpcResponseStatus.SHUTDOWN.getValue())
                    .build();
        } else {
            this.dataNodeManager.register(request.getIp(), request.getHostname());
            response = RegisterResponse.newBuilder()
                    .setStatus(NameNodeRpcResponseStatus.SUCCESS.getValue())
                    .build();
        }

        log.debug("register request ip {}, hostname {}, response status {}",
                request.getIp(), request.getHostname(), response.getStatus());

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void heartbeat(HeartbeatRequest request, StreamObserver<HeartbeatResponse> responseObserver) {
        HeartbeatResponse response;

        if (!isRunning) {
            response = HeartbeatResponse.newBuilder()
                    .setStatus(NameNodeRpcResponseStatus.SHUTDOWN.getValue())
                    .build();
        } else {
            this.dataNodeManager.heartbeat(request.getIp(), request.getHostname());
            response = HeartbeatResponse.newBuilder()
                    .setStatus(NameNodeRpcResponseStatus.SUCCESS.getValue())
                    .build();
        }

        log.debug("heartbeat request ip {}, hostname {}, response status {}",
                request.getIp(), request.getHostname(), response.getStatus());

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void mkdir(MkdirRequest request, StreamObserver<MkdirResponse> responseObserver) {
        MkdirResponse response;

        if (!isRunning) {
            response = MkdirResponse.newBuilder()
                    .setStatus(NameNodeRpcResponseStatus.SHUTDOWN.getValue())
                    .build();
        } else {
            this.nameSystem.mkdir(request.getPath());
            response = MkdirResponse.newBuilder()
                    .setStatus(NameNodeRpcResponseStatus.SUCCESS.getValue())
                    .build();
        }

        log.debug("mkdir path {}, response status {}", request.getPath(), response.getStatus());

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void shutdown(ShutdownRequest request, StreamObserver<ShutdownResponse> responseObserver) {
        ShutdownResponse response;

        if (isRunning) {
            this.isRunning = false;
            this.nameSystem.flush();
            this.nameSystem.saveCheckpointTxid();
            response = ShutdownResponse.newBuilder()
                    .setStatus(NameNodeRpcResponseStatus.SUCCESS.getValue())
                    .build();
        } else {
            response = ShutdownResponse.newBuilder()
                    .setStatus(NameNodeRpcResponseStatus.SHUTDOWN.getValue())
                    .build();
        }

        log.debug("shutdown, response status {}", response.getStatus());

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void fetchEditLog(FetchEditLogRequest request, StreamObserver<FetchEditLogResponse> responseObserver) {
        FetchEditLogResponse response;
        List<EditLog> fetchedEditLog = new ArrayList<>();

        if (!isRunning) {
            response = FetchEditLogResponse.newBuilder()
                    .setStatus(NameNodeRpcResponseStatus.SHUTDOWN.getValue())
                    .build();
        } else {
            this.nameSystem.fetchEditLog(request.getSyncedTxid(), fetchedEditLog);
            response = FetchEditLogResponse.newBuilder()
                    .setStatus(NameNodeRpcResponseStatus.SUCCESS.getValue())
                    .setEditLog(FastJsonUtils.formatObjectToJsonString(fetchedEditLog))
                    .build();
        }

        log.debug("fetch EditLog size {}, response status {}", fetchedEditLog.size(), response.getStatus());

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void updateCheckpointTxid(UpdateCheckpointTxidRequest request, StreamObserver<UpdateCheckpointTxidResponse> responseObserver) {
        UpdateCheckpointTxidResponse response;

        if (!isRunning) {
            response = UpdateCheckpointTxidResponse.newBuilder()
                    .setStatus(NameNodeRpcResponseStatus.SHUTDOWN.getValue())
                    .build();
        } else {
            this.nameSystem.setCheckpointTxid(request.getTxid());
            response = UpdateCheckpointTxidResponse.newBuilder()
                    .setStatus(NameNodeRpcResponseStatus.SUCCESS.getValue())
                    .build();
        }

        log.debug("update checkpoint txid {}, response status {}", request.getTxid(), response.getStatus());

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void create(CreateFileRequest request, StreamObserver<CreateFileResponse> responseObserver) {
        CreateFileResponse response;

        if (!isRunning) {
            response = CreateFileResponse.newBuilder()
                    .setStatus(NameNodeRpcResponseStatus.SHUTDOWN.getValue())
                    .build();
        } else {
            if (this.nameSystem.touch(request.getFilename())) {
                response = CreateFileResponse.newBuilder()
                        .setStatus(NameNodeRpcResponseStatus.SUCCESS.getValue())
                        .build();
            } else {
                response = CreateFileResponse.newBuilder()
                        .setStatus(NameNodeRpcResponseStatus.FAIL.getValue())
                        .build();
            }
        }

        log.debug("create file path {}, response status {}", request.getFilename(), response.getStatus());

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
