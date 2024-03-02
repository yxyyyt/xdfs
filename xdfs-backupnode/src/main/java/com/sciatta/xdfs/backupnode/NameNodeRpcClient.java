package com.sciatta.xdfs.backupnode;

import com.sciatta.xdfs.common.fs.EditLog;
import com.sciatta.xdfs.common.util.FastJsonUtils;
import com.sciatta.xdfs.namenode.rpc.model.FetchEditLogRequest;
import com.sciatta.xdfs.namenode.rpc.model.FetchEditLogResponse;
import com.sciatta.xdfs.namenode.rpc.model.UpdateCheckpointTxidRequest;
import com.sciatta.xdfs.namenode.rpc.model.UpdateCheckpointTxidResponse;
import com.sciatta.xdfs.namenode.rpc.service.NameNodeRpcServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NegotiationType;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Created by Rain on 2024/2/26<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * 和主节点通讯的Rpc客户端，基于Grpc
 */
@Slf4j
public class NameNodeRpcClient {
    private static final String NAMENODE_HOSTNAME = "localhost";
    private static final Integer NAMENODE_PORT = 50070;

    private final NameNodeRpcServiceGrpc.NameNodeRpcServiceBlockingStub nameNodeRpcService;

    public NameNodeRpcClient() {
        ManagedChannel channel = NettyChannelBuilder
                .forAddress(NAMENODE_HOSTNAME, NAMENODE_PORT)
                .negotiationType(NegotiationType.PLAINTEXT)
                .build();
        this.nameNodeRpcService = NameNodeRpcServiceGrpc.newBlockingStub(channel);
    }

    /**
     * 从主节点同步事务日志
     *
     * @param syncedTxid 备份节点已同步的事务日志序号
     * @return 事务日志
     */
    public List<EditLog> fetchEditLog(long syncedTxid) {
        FetchEditLogRequest request = FetchEditLogRequest.newBuilder()
                .setSyncedTxid(syncedTxid)
                .build();

        FetchEditLogResponse response = nameNodeRpcService.fetchEditLog(request);
        String editLog = response.getEditLog();

        List<EditLog> editLogList = FastJsonUtils.parseJsonStringToObjects(editLog, EditLog.class);

        log.debug("fetch EditLog size {} from NameNode, response status {}",
                editLogList == null ? 0 : editLogList.size(), response.getStatus());

        return editLogList;
    }

    /**
     * 向主节点更新检查点事务日志序号
     *
     * @param txid 事务日志序号
     */
    public void updateCheckpointTxid(long txid) {
        UpdateCheckpointTxidRequest request = UpdateCheckpointTxidRequest.newBuilder()
                .setTxid(txid)
                .build();

        UpdateCheckpointTxidResponse response = this.nameNodeRpcService.updateCheckpointTxid(request);

        log.debug("update checkpoint txid {}, response status {}", txid, response.getStatus());
    }
}
