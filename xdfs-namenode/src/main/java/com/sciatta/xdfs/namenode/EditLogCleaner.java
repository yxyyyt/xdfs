package com.sciatta.xdfs.namenode;

import com.sciatta.xdfs.common.util.ConcurrentUtils;
import com.sciatta.xdfs.common.util.PathUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;

/**
 * Created by Rain on 2024/3/1<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * 自动清理已经镜像化的事务日志
 */
@Slf4j
public class EditLogCleaner extends Thread {
    private static final int EDIT_LOG_CLEAN_INTERVAL = 30; // TODO to log

    private final FSNameSystem nameSystem;

    public EditLogCleaner(FSNameSystem nameSystem) {
        this.nameSystem = nameSystem;
    }

    @Override
    public void run() {
        while (true) {
            ConcurrentUtils.silentSleepToSeconds(EDIT_LOG_CLEAN_INTERVAL);

            List<String> flushedTxids = this.nameSystem.getEditlog().getFlushedTxids();
            if (flushedTxids != null && !flushedTxids.isEmpty()) {
                long checkpointTxid = this.nameSystem.getCheckpointTxid();

                for (String flushedTxid : flushedTxids) {
                    long startTxid = Long.parseLong(flushedTxid.split("_")[0]);
                    long endTxid = Long.parseLong(flushedTxid.split("_")[1]);

                    if (checkpointTxid >= endTxid) {
                        String path = PathUtils.getNameNodeEditLogPath(startTxid, endTxid);
                        File file = new File(path);
                        if (file.exists()) {
                            boolean delete = file.delete();
                            log.debug("clean EditLog path {}, status {}", path, delete);
                        }
                    }
                }
            }
        }
    }
}
