package com.sciatta.xdfs.backupnode;

import com.sciatta.xdfs.common.fs.EditLog;
import com.sciatta.xdfs.common.fs.EditLogOperateEnum;
import com.sciatta.xdfs.common.util.ConcurrentUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Created by Rain on 2024/2/26<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * 从主节点同步事务日志的核心组件
 */
@Slf4j
public class EditLogFetcher extends Thread {
    public static final Integer BACKUP_NODE_FETCH_SIZE = 10;    // TODO to config

    private final NameNodeRpcClient nameNodeRpcClient;
    private final FSNameSystem nameSystem;

    /**
     * 已同步事务日志序号
     */
    private long syncedTxid;

    public EditLogFetcher(FSNameSystem nameSystem, NameNodeRpcClient nameNodeRpcClient) {
        this.nameNodeRpcClient = nameNodeRpcClient;
        this.nameSystem = nameSystem;
        // 初始化上一次生成镜像时的最大事务日志序号
        this.syncedTxid = this.nameSystem.getCheckpointTxid();
    }

    @Override
    public void run() {
        while (true) {
            if (!this.nameSystem.isRecover()) {
                log.debug("NameSystem is recovering");
                ConcurrentUtils.silentSleepToSeconds(1);
                continue;
            }

            List<EditLog> editLogList = this.nameNodeRpcClient.fetchEditLog(syncedTxid);

            if (editLogList == null || editLogList.isEmpty()) {
                log.debug("no fetch any EditLog");
                ConcurrentUtils.silentSleepToSeconds(1);
                continue;
            }

            // 避免请求过于频繁
            if (editLogList.size() < BACKUP_NODE_FETCH_SIZE) {
                log.debug("fetch actual size {} < config size {}", editLogList.size(), BACKUP_NODE_FETCH_SIZE);
                ConcurrentUtils.silentSleepToSeconds(1);
            }

            for (EditLog editLog : editLogList) {
                log.debug("fetch {}", editLog);

                if (editLog.getOperate().equals(EditLogOperateEnum.MKDIR.getOperate())) {
                    this.nameSystem.mkdir(editLog.getTxid(), editLog.getPath());
                } else if (editLog.getOperate().equals(EditLogOperateEnum.TOUCH.getOperate())) {
                    this.nameSystem.touch(editLog.getTxid(), editLog.getPath());
                }

                this.syncedTxid = editLog.getTxid();
            }
        }
    }

}
