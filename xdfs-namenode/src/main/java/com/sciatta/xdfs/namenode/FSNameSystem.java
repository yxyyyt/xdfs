package com.sciatta.xdfs.namenode;

import com.sciatta.xdfs.common.fs.EditLog;
import com.sciatta.xdfs.common.fs.EditLogOperateEnum;
import com.sciatta.xdfs.common.fs.FSDirectory;
import com.sciatta.xdfs.common.util.FastJsonUtils;
import com.sciatta.xdfs.common.util.PathUtils;
import com.sciatta.xdfs.common.util.SystemUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

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
     * 最新检查点的最大事务日志序号
     */
    @Getter
    @Setter
    private long checkpointTxid;

    /**
     * 每个线程自己本地的内存目录树操作状态
     */
    private final ThreadLocal<Boolean> localDirectoryOperateStatus = new ThreadLocal<>();

    public FSNameSystem() {
        this.directory = new FSDirectory();
        this.editlog = new FSEditLog();
        recoverNameSystem();
    }

    /**
     * 创建目录
     *
     * @param path 目录路径
     * @return true，成功；false，失败
     */
    public boolean mkdir(String path) {
        this.editlog.logEdit(txid -> {
            boolean ans = this.directory.mkdir(txid, path);
            localDirectoryOperateStatus.set(ans);

            EditLog editLog = new EditLog();
            editLog.setTxid(txid);
            editLog.setPath(path);
            editLog.setOperate(EditLogOperateEnum.MKDIR.getOperate());

            return editLog;
        });

        boolean ans = localDirectoryOperateStatus.get();
        localDirectoryOperateStatus.remove();
        return ans;
    }

    /**
     * 创建文件
     *
     * @param path 文件路径
     * @return true，成功；false，失败
     */
    public boolean touch(String path) {
        this.editlog.logEdit(txid -> {
            boolean ans = this.directory.touch(txid, path);
            localDirectoryOperateStatus.set(ans);

            if (!ans) {
                return null;
            }

            EditLog editLog = new EditLog();
            editLog.setTxid(txid);
            editLog.setPath(path);
            editLog.setOperate(EditLogOperateEnum.TOUCH.getOperate());

            return editLog;
        });

        boolean ans = localDirectoryOperateStatus.get();
        localDirectoryOperateStatus.remove();
        return ans;
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
     * @param syncedTxid     备份节点已同步的事务日志序号
     * @param fetchedEditLog 拉取到的事务日志
     */
    public void fetchEditLog(long syncedTxid, List<EditLog> fetchedEditLog) {
        List<String> flushedTxids = this.editlog.getFlushedTxids();

        if (flushedTxids.isEmpty()) {
            // 没有磁盘文件，从内存双缓存中拉取
            fetchFromBufferedEditLog(syncedTxid, fetchedEditLog);
        } else {
            // 有磁盘文件，并且之前缓存过
            if (bufferedFlushedTxid != null) {
                if (existInFlushedFile(syncedTxid, bufferedFlushedTxid)) {
                    // 缓存的磁盘文件还没有拉取完
                    fetchFromCurrentBuffer(syncedTxid, fetchedEditLog);
                } else {
                    // 缓存的磁盘文件已经拉取完，从下一个尝试拉取
                    String nextFlushedTxid = getNextFlushedTxid(flushedTxids, bufferedFlushedTxid);
                    if (nextFlushedTxid != null) {
                        // 从下一个磁盘文件拉取并缓存
                        fetchFromFlushedFile(syncedTxid, nextFlushedTxid, fetchedEditLog);
                    } else {
                        // 没有下一个磁盘文件，从内存双缓存中拉取
                        fetchFromBufferedEditLog(syncedTxid, fetchedEditLog);
                    }
                }
            } else {
                // 有磁盘文件，没有缓存过
                boolean fechedFromFlushedFile = false;
                for (String flushedTxid : flushedTxids) {
                    if (existInFlushedFile(syncedTxid, flushedTxid)) {
                        // 缓存的磁盘文件存在未拉取的数据
                        fetchFromFlushedFile(syncedTxid, flushedTxid, fetchedEditLog);
                        fechedFromFlushedFile = true;
                        break;
                    }
                }

                // 所有磁盘文件的数据全部被拉取完成
                if (!fechedFromFlushedFile) {
                    fetchFromBufferedEditLog(syncedTxid, fetchedEditLog);
                }
            }
        }
    }

    /**
     * 持久化最新检查点的最大事务日志序号
     */
    public void saveCheckpointTxid() {
        String path = PathUtils.getNameNodeCheckpointTxidFile();
        RandomAccessFile raf = null;
        FileOutputStream out = null;
        FileChannel channel = null;

        try {
            File file = new File(path);
            if (file.exists()) {
                file.delete();  // 删除旧文件
            }

            ByteBuffer buffer = ByteBuffer.wrap(String.valueOf(checkpointTxid).getBytes());

            raf = new RandomAccessFile(path, "rw");
            out = new FileOutputStream(raf.getFD());
            channel = out.getChannel();

            channel.write(buffer);
            channel.force(false);
        } catch (Exception e) {
            log.error("save checkpoint txid {} catch exception {}", checkpointTxid, e.getMessage());
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (raf != null) {
                    raf.close();
                }
                if (channel != null) {
                    channel.close();
                }
            } catch (Exception ignore) {
            }
        }
    }

    /**
     * 从内存双缓存中拉取事务日志
     *
     * @param syncedTxid     备份节点已同步的事务日志序号
     * @param fetchedEditLog 拉取到的事务日志
     */
    private void fetchFromBufferedEditLog(long syncedTxid, List<EditLog> fetchedEditLog) {
        long fetchTxid = syncedTxid + 1;

        if (fetchTxid <= currentBufferedMaxTxid) {
            fetchFromCurrentBuffer(syncedTxid, fetchedEditLog);
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

        fetchFromCurrentBuffer(syncedTxid, fetchedEditLog);
    }

    /**
     * 从指定磁盘文件中拉取事务日志
     *
     * @param syncedTxid     备份节点已同步的事务日志序号
     * @param flushedTxid    指定磁盘文件事务序号
     * @param fetchedEditLog 拉取到的事务日志
     */
    private void fetchFromFlushedFile(long syncedTxid, String flushedTxid, List<EditLog> fetchedEditLog) {
        try {
            String[] splitFlushedTxid = flushedTxid.split("_");
            long startTxid = Long.parseLong(splitFlushedTxid[0]);
            long endTxid = Long.parseLong(splitFlushedTxid[1]);

            String currentEditLogFile = PathUtils.getNameNodeEditLogFile(startTxid, endTxid);

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

            fetchFromCurrentBuffer(syncedTxid, fetchedEditLog);
        } catch (IOException e) {
            log.error("fetch from flushed file {} catch exception {}", flushedTxid, e.getMessage());
        }
    }

    /**
     * 待拉取的日志是否存在于指定的磁盘文件事务日志序号范围中
     *
     * @param syncedTxid  备份节点已同步的事务日志序号
     * @param flushedTxid 指定的磁盘文件事务日志序号范围
     * @return true，存在；false，不存在
     */
    private Boolean existInFlushedFile(long syncedTxid, String flushedTxid) {
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
     * @param syncedTxid     备份节点已同步的事务日志序号
     * @param fetchedEditLog 拉取到的事务日志
     */
    private void fetchFromCurrentBuffer(long syncedTxid, List<EditLog> fetchedEditLog) {
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

    /**
     * 恢复元数据
     */
    private void recoverNameSystem() {
        try {
            loadFSImage();
            loadCheckpointTxid();
            loadEditLog();
        } catch (Exception e) {
            log.error("recover NameSystem catch exception {}", e.getMessage());
            SystemUtils.normalExit();
        }
    }

    /**
     * 加载镜像
     *
     * @throws IOException IO异常
     */
    private void loadFSImage() throws IOException {
        FileInputStream in = null;
        FileChannel channel = null;
        try {
            String path = PathUtils.getNameNodeImageFile();
            File file = new File(path);
            if (!file.exists()) {
                log.debug("load FSImage but not exist");
                return;
            }

            in = new FileInputStream(path);
            channel = in.getChannel();

            ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);   // TODO 收到镜像后，缓存大小；然后在这里分配
            int count = channel.read(buffer);

            buffer.flip();
            String fsimageJson = new String(buffer.array(), 0, count);
            FSDirectory.INodeDirectory dirTree = FastJsonUtils.parseJsonStringToObject(fsimageJson,
                    FSDirectory.INodeDirectory.class);
            directory.setDirTree(dirTree);
            log.debug("load FSImage success, size {}", fsimageJson.getBytes().length);
        } finally {
            if (in != null) {
                in.close();
            }
            if (channel != null) {
                channel.close();
            }
        }
    }

    /**
     * 加载镜像的检查点事务日志序号
     *
     * @throws IOException IO异常
     */
    private void loadCheckpointTxid() throws IOException {
        FileInputStream in = null;
        FileChannel channel = null;
        try {
            String path = PathUtils.getNameNodeCheckpointTxidFile();
            File file = new File(path);
            if (!file.exists()) {
                log.debug("load checkpoint txid but not exist");
                return;
            }

            in = new FileInputStream(path);
            channel = in.getChannel();

            ByteBuffer buffer = ByteBuffer.allocate(1024); // TODO 大小 持久化
            int count = channel.read(buffer);

            buffer.flip();
            this.checkpointTxid = Long.parseLong(new String(buffer.array(), 0, count));
            this.directory.setMaxTxid(this.checkpointTxid);
            log.debug("load checkpoint txid {} success", this.checkpointTxid);
        } finally {
            if (in != null) {
                in.close();
            }
            if (channel != null) {
                channel.close();
            }
        }
    }

    /**
     * 加载事务日志并重放，恢复文件目录树和当前事务日志序号
     *
     * @throws IOException IO异常
     */
    private void loadEditLog() throws IOException {
        File dir = new File(PathUtils.getNameNodeEditLogPath());

        List<File> files = new ArrayList<>(Arrays.asList(dir.listFiles()));

        files.removeIf(file -> !PathUtils.isNameNodeEditLogFile(file.getName()));

        files.sort((o1, o2) -> {
            Integer o1StartTxid = Integer.valueOf(o1.getName().split("-")[1]);
            Integer o2StartTxid = Integer.valueOf(o2.getName().split("-")[1]);
            return o1StartTxid - o2StartTxid;
        });

        if (files.isEmpty()) {
            log.debug("load EditLog but not exist");
            return;
        }

        for (File file : files) {
            String[] splitedName = file.getName().split("-");
            long startTxid = Long.parseLong(splitedName[1]);
            long endTxid = Long.parseLong(splitedName[2].split("[.]")[0]);

            if (endTxid > this.directory.getMaxTxid()) {
                String currentEditsLogFile = PathUtils.getNameNodeEditLogFile(startTxid, endTxid);
                List<String> editLogList = Files.readAllLines(Paths.get(currentEditsLogFile), StandardCharsets.UTF_8);
                for (String editLogJson : editLogList) {
                    EditLog editLog = FastJsonUtils.parseJsonStringToObject(editLogJson, EditLog.class);
                    if (editLog != null && editLog.getTxid() > this.directory.getMaxTxid()) {
                        String op = editLog.getOperate();
                        // 回放事务日志
                        if (op.equals(EditLogOperateEnum.MKDIR.getOperate())) {
                            directory.mkdir(editLog.getTxid(), editLog.getPath());
                        }
                        log.debug("load {} and rewind EditLog {}", currentEditsLogFile, editLog.getTxid());
                    }
                }
            }
        }

        // 恢复当前事务日志序号
        this.editlog.setTxidSeq(this.directory.getMaxTxid());
    }
}
