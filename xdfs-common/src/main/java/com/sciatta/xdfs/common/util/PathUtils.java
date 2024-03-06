package com.sciatta.xdfs.common.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Rain on 2024/3/1<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * 路径工具栏
 */
@Slf4j
public class PathUtils {

    /**
     * 根路径
     */
    private static final String ROOT_PATH = "D:\\data\\project\\xdfs\\";

    /**
     * 主节点路径
     */
    private static final String NAMENODE_PATH = ROOT_PATH + "namenode\\";

    /**
     * 主节点事务日志路径
     */
    private static final String NAMENODE_EDITLOG_PATH = NAMENODE_PATH + "editlog\\";

    /**
     * 主节点镜像路径
     */
    private static final String NAMENODE_IMAGE_PATH = NAMENODE_PATH;

    /**
     * 主节点检查点事务日志序号路径
     */
    private static final String NAMENODE_CHECKPOINT_TXID_PATH = NAMENODE_PATH;

    /**
     * 备份节点路径
     */
    private static final String BACKUPNODE_PATH = ROOT_PATH + "backupnode\\";

    /**
     * 备份节点镜像路径
     */
    private static final String BACKUPNODE_IMAGE_PATH = BACKUPNODE_PATH;

    /**
     * 备份节点检查点信息路径
     */
    private static final String BACKUPNODE_CHECKPOINT_INFO_PATH = BACKUPNODE_PATH;


    private PathUtils() {

    }

    /**
     * 获取主节点事务日志路径
     *
     * @return 主节点事务日志路径
     * @throws IOException IO异常
     */
    public static String getNameNodeEditLogPath() throws IOException {
        Path path = Paths.get(NAMENODE_EDITLOG_PATH);
        if (Files.notExists(path)) {
            Files.createDirectories(path);
        }
        return NAMENODE_EDITLOG_PATH;
    }

    /**
     * 获取主节点事务日志文件
     *
     * @param startTxid 开始事务标识序号
     * @param endTxid   结束事务标识序号
     * @return 主节点事务日志文件
     */
    public static String getNameNodeEditLogFile(long startTxid, long endTxid) {
        try {
            return getNameNodeEditLogPath() + "edit-" + startTxid + "-" + endTxid + ".log";
        } catch (IOException e) {
            log.error("get NameNode EditLog file, startTxid {}, endTxid {} catch exception {}",
                    startTxid, endTxid, e.getMessage());
            SystemUtils.normalExit();
        }
        return null;
    }

    /**
     * 是否是主节点事务日志文件
     *
     * @param path 主节点事务日志文件
     * @return true，是；false，不是
     */
    public static boolean isNameNodeEditLogFile(String path) {
        return path != null && path.startsWith("edit-") && path.endsWith(".log");
    }

    /**
     * 获取主节点镜像路径
     *
     * @return 主节点镜像路径
     * @throws IOException IO异常
     */
    public static String getNameNodeImagePath() throws IOException {
        Path path = Paths.get(NAMENODE_IMAGE_PATH);
        if (Files.notExists(path)) {
            Files.createDirectories(path);
        }
        return NAMENODE_IMAGE_PATH;
    }

    /**
     * 获取主节点镜像文件
     *
     * @return 获取主节点镜像文件
     */
    public static String getNameNodeImageFile() {
        try {
            return getNameNodeImagePath() + "fsimage.meta";
        } catch (IOException e) {
            log.error("get NameNode image file catch exception {}", e.getMessage());
            SystemUtils.normalExit();
        }

        return null;
    }

    /**
     * 获取主节点检查点事务日志序号路径
     *
     * @return 主节点检查点事务日志序号路径
     * @throws IOException IO异常
     */
    public static String getNameNodeCheckpointTxidPath() throws IOException {
        Path path = Paths.get(NAMENODE_CHECKPOINT_TXID_PATH);
        if (Files.notExists(path)) {
            Files.createDirectories(path);
        }
        return NAMENODE_CHECKPOINT_TXID_PATH;
    }

    /**
     * 获取主节点检查点事务日志序号文件
     *
     * @return 主节点检查点事务日志序号文件
     */
    public static String getNameNodeCheckpointTxidFile() {
        try {
            return getNameNodeCheckpointTxidPath() + "checkpoint-txid.meta";
        } catch (IOException e) {
            log.error("get NameNode checkpoint txid file catch exception {}", e.getMessage());
            SystemUtils.normalExit();
        }

        return null;
    }

    /**
     * 获取备份节点镜像路径
     *
     * @return 获取备份节点镜像路径
     * @throws IOException IO异常
     */
    public static String getBackupNodeImagePath() throws IOException {
        Path path = Paths.get(BACKUPNODE_IMAGE_PATH);
        if (Files.notExists(path)) {
            Files.createDirectories(path);
        }
        return BACKUPNODE_IMAGE_PATH;
    }

    /**
     * 获取备份节点镜像文件
     *
     * @param txid 当前镜像的最大事务日志序号
     * @return 备份节点镜像文件
     */
    public static String getBackupNodeImageFile(long txid) {
        try {
            return getBackupNodeImagePath() + "fsimage-" + txid + ".meta";
        } catch (IOException e) {
            log.error("get BackupNode image file txid {} catch exception {}", txid, e.getMessage());
            SystemUtils.normalExit();
        }
        return null;
    }

    /**
     * 获取备份节点检查点信息路径
     *
     * @return 备份节点检查点信息路径
     * @throws IOException IO异常
     */
    public static String getBackupNodeCheckpointInfoPath() throws IOException {
        Path path = Paths.get(BACKUPNODE_CHECKPOINT_INFO_PATH);
        if (Files.notExists(path)) {
            Files.createDirectories(path);
        }
        return BACKUPNODE_CHECKPOINT_INFO_PATH;
    }

    /**
     * 获取备份节点检查点信息文件
     *
     * @return 备份节点检查点信息文件
     */
    public static String getBackupNodeCheckpointInfoFile() {
        try {
            return getBackupNodeCheckpointInfoPath() + "checkpoint-info.meta";
        } catch (IOException e) {
            log.error("get BackupNode checkpoint info file catch exception {}", e.getMessage());
            SystemUtils.normalExit();
        }
        return null;
    }
}
