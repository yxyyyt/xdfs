package com.sciatta.xdfs.namenode;

import com.sciatta.xdfs.common.fs.EditLog;
import com.sciatta.xdfs.common.fs.EditLogOperateEnum;
import com.sciatta.xdfs.common.fs.FSDirectory;
import com.sciatta.xdfs.common.util.ClassUtils;
import com.sciatta.xdfs.common.util.FastJsonUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rain on 2024/2/19<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * 管理元数据的核心组件
 */
@Slf4j
public class FSNameSystem {

    /**
     * 备份节点一次拉取事务日志的最大数量
     */
    public static final Integer BACKUP_NODE_FETCH_SIZE = 10;    // TODO fetch可以从备份节点传入，主节点可以界定一个有效范围

    /**
     * 事务日志保存路径
     */
    public static final String EDIT_LOG_FILE_PATH = "D:\\data\\project\\xdfs\\editlog\\";   // TODO to config

    private final FSDirectory directory;

    @Getter
    private final FSEditLog editlog;

    /**
     * 当前缓存的一批事务日志
     */
    private final List<EditLog> currentBufferedEditLog = new ArrayList<>();

    /**
     * 当前缓存的一批事务日志的最大序号
     */
    private long currentBufferedMaxTxid;

    /**
     * 当前缓存的磁盘事务日志序号
     */
    private String bufferedFlushedTxid;

    /**
     * 当前备份节点已经同步的事务日志序号
     */
    private long syncedTxid = 0L;   // TODO 保存到备份节点

    public FSNameSystem() {
        this.directory = new FSDirectory();
        this.editlog = new FSEditLog();
    }

    /**
     * 创建目录
     *
     * @param path 目录路径
     * @return 是否成功
     */
    public Boolean mkdir(String path) {
        this.directory.mkdir(path);
        this.editlog.logEdit(txid -> {
            EditLog editLog = new EditLog();
            editLog.setTxid(txid);
            editLog.setPath(path);
            editLog.setOperate(EditLogOperateEnum.MKDIR.getOperate());
            return editLog;
        });
        return true;
    }

    /**
     * 强制刷写清空同步事务日志缓存
     */
    public void flush() {
        this.editlog.flush();
    }

    /**
     * 备份节点向主节点请求拉取事务日志
     *
     * @param fetchedEditLog 拉取到的事务日志
     */
    public void fetchEditLog(List<EditLog> fetchedEditLog) {
        List<String> flushedTxids = this.editlog.getFlushedTxids();

        if (flushedTxids.isEmpty()) {
            // 没有磁盘文件，从内存双缓存中拉取
            fetchFromBufferedEditLog(fetchedEditLog);
        } else {
            // 有磁盘文件，并且之前缓存过
            if (bufferedFlushedTxid != null) {
                if (existInFlushedFile(bufferedFlushedTxid)) {
                    // 缓存的磁盘文件还没有拉取完
                    fetchFromCurrentBuffer(fetchedEditLog);
                } else {
                    // 缓存的磁盘文件已经拉取完，从下一个尝试拉取
                    String nextFlushedTxid = getNextFlushedTxid(flushedTxids, bufferedFlushedTxid);
                    if (nextFlushedTxid != null) {
                        // 从下一个磁盘文件拉取并缓存
                        fetchFromFlushedFile(nextFlushedTxid, fetchedEditLog);
                    } else {
                        // 没有下一个磁盘文件，从内存双缓存中拉取
                        fetchFromBufferedEditLog(fetchedEditLog);
                    }
                }
            } else {
                // 有磁盘文件，没有缓存过
                boolean fechedFromFlushedFile = false;
                for (String flushedTxid : flushedTxids) {
                    if (existInFlushedFile(flushedTxid)) {
                        // 缓存的磁盘文件存在未拉取的数据
                        fetchFromFlushedFile(flushedTxid, fetchedEditLog);
                        fechedFromFlushedFile = true;
                        break;
                    }
                }

                // 所有磁盘文件的数据全部被拉取完成
                if (!fechedFromFlushedFile) {
                    fetchFromBufferedEditLog(fetchedEditLog);
                }
            }
        }
    }

    /**
     * 从内存双缓存中拉取事务日志
     *
     * @param fetchedEditLog 拉取到的事务日志
     */
    private void fetchFromBufferedEditLog(List<EditLog> fetchedEditLog) {
        long fetchTxid = syncedTxid + 1;

        if (fetchTxid <= currentBufferedMaxTxid) {
            fetchFromCurrentBuffer(fetchedEditLog);
            return;
        }

        currentBufferedEditLog.clear();

        String[] bufferedEditLog = this.editlog.getBufferedEditLog();

        for (String editLog : bufferedEditLog) {
            EditLog el = FastJsonUtils.parseJsonStringToObject(editLog, EditLog.class);
            if (el != null) {
                currentBufferedEditLog.add(el);
                currentBufferedMaxTxid = el.getTxid();
            }
        }

        bufferedFlushedTxid = null;

        fetchFromCurrentBuffer(fetchedEditLog);
    }

    /**
     * 从指定磁盘文件中拉取事务日志
     *
     * @param flushedTxid    指定磁盘文件事务序号
     * @param fetchedEditLog 拉取到的事务日志
     */
    private void fetchFromFlushedFile(String flushedTxid, List<EditLog> fetchedEditLog) {
        try {
            String[] splitFlushedTxid = flushedTxid.split("_");
            long startTxid = Long.parseLong(splitFlushedTxid[0]);
            long endTxid = Long.parseLong(splitFlushedTxid[1]);

            String currentEditLogFile = EDIT_LOG_FILE_PATH + "edit-"
                    + startTxid + "-" + endTxid + ".log";

            currentBufferedEditLog.clear();

            List<String> editLogList = Files.readAllLines(Paths.get(currentEditLogFile),
                    StandardCharsets.UTF_8);


            for (String editLog : editLogList) {
                EditLog el = FastJsonUtils.parseJsonStringToObject(editLog, EditLog.class);
                if (el != null) {
                    currentBufferedEditLog.add(el);
                    currentBufferedMaxTxid = el.getTxid();
                }
            }

            bufferedFlushedTxid = flushedTxid;

            fetchFromCurrentBuffer(fetchedEditLog);
        } catch (IOException e) {
            log.error("fetch from flushed file {} catch exception {}", flushedTxid, e.getMessage());
        }
    }

    /**
     * 待拉取的日志是否存在于指定的磁盘文件事务日志序号范围中
     *
     * @param flushedTxid 指定的磁盘文件事务日志序号范围
     * @return true，存在；false，不存在
     */
    private Boolean existInFlushedFile(String flushedTxid) {
        String[] splitFlushedTxid = flushedTxid.split("_");

        long startTxid = Long.parseLong(splitFlushedTxid[0]);
        long endTxid = Long.parseLong(splitFlushedTxid[1]);
        long fetchTxid = syncedTxid + 1;

        return fetchTxid >= startTxid && fetchTxid <= endTxid;
    }

    /**
     * 获取指定事务日志序号范围的下一个事务日志序号范围
     *
     * @param flushedTxids        指定事务日志序号范围
     * @param bufferedFlushedTxid 全部缓存的事务日志序号范围
     * @return 下一个事务日志序号范围；如果不存在，返回null
     */
    private String getNextFlushedTxid(List<String> flushedTxids, String bufferedFlushedTxid) {
        for (int i = 0; i < flushedTxids.size(); i++) {
            if (flushedTxids.get(i).equals(bufferedFlushedTxid)) {
                if (i + 1 < flushedTxids.size()) {
                    return flushedTxids.get(i + 1);
                }
            }
        }
        return null;
    }

    /**
     * 从当前缓存中拉取事务日志
     *
     * @param fetchedEditLog 拉取到的事务日志
     */
    private void fetchFromCurrentBuffer(List<EditLog> fetchedEditLog) {
        int fetchCount = 0;
        for (EditLog editLog : currentBufferedEditLog) {
            if (editLog.getTxid() == syncedTxid + 1) {
                fetchedEditLog.add(editLog);
                syncedTxid = editLog.getTxid();
                fetchCount++;
            }
            if (fetchCount == BACKUP_NODE_FETCH_SIZE) {
                break;
            }
        }
    }
}
