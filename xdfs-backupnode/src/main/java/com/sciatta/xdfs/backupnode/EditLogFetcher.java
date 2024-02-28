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
    private final NameNodeRpcClient nameNodeRpcClient;
    private final FSNameSystem nameSystem;

    public EditLogFetcher(FSNameSystem nameSystem) {
        this.nameNodeRpcClient = new NameNodeRpcClient();
        this.nameSystem = nameSystem;
    }

    @Override
    public void run() {
        while (true) {
            List<EditLog> editLogList = this.nameNodeRpcClient.fetchEditLog();

            if (editLogList == null || editLogList.isEmpty()) {
                log.debug("no fetch any EditLog");
                ConcurrentUtils.silentSleepToSeconds(1);
                continue;
            }

            for (EditLog editLog : editLogList) {
                log.debug("fetch {}", editLog);
                if (editLog.getOperate().equals(EditLogOperateEnum.MKDIR.getOperate())) {
                    String path = editLog.getPath();
                    this.nameSystem.mkdir(path);
                }
            }
        }
    }

}
