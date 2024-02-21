package com.sciatta.xdfs.client;

import com.sciatta.xdfs.namenode.rpc.model.MkdirRequest;
import com.sciatta.xdfs.namenode.rpc.model.MkdirResponse;
import com.sciatta.xdfs.namenode.rpc.service.NameNodeRpcServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NegotiationType;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;

import java.io.IOException;

/**
 * Created by Rain on 2024/2/21<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * 文件系统实现
 */
public class FileSystemImpl implements FileSystem {
    private static final String NAMENODE_HOSTNAME = "localhost";
    private static final Integer NAMENODE_PORT = 50070;

    private final NameNodeRpcServiceGrpc.NameNodeRpcServiceBlockingStub namenode;

    public FileSystemImpl() {
        ManagedChannel channel = NettyChannelBuilder
                .forAddress(NAMENODE_HOSTNAME, NAMENODE_PORT)
                .negotiationType(NegotiationType.PLAINTEXT)
                .build();
        this.namenode = NameNodeRpcServiceGrpc.newBlockingStub(channel);
    }


    @Override
    public void mkdir(String path) throws IOException {
        MkdirRequest request = MkdirRequest.newBuilder()
                .setPath(path)
                .build();

        MkdirResponse response = namenode.mkdir(request);

        System.out.println("创建目录的响应：" + response.getStatus());  // TODO to log
    }
}
