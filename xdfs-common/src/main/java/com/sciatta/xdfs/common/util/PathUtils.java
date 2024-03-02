package com.sciatta.xdfs.common.util;

/**
 * Created by Rain on 2024/3/1<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * 路径工具栏
 */
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
     * 获取主节点事务日志持久化路径
     *
     * @return 主节点事务日志持久化路径
     */
    public static String getNameNodeEditLogPath() {
        return NAMENODE_EDITLOG_PATH;
    }

    /**
     * 获取主节点事务日志持久化路径
     *
     * @param startTxid 开始事务标识序号
     * @param endTxid   结束事务标识序号
     * @return 主节点事务日志持久化路径
     */
    public static String getNameNodeEditLogPath(long startTxid, long endTxid) {
        return NAMENODE_EDITLOG_PATH + "edit-" + startTxid + "-" + endTxid + ".log";
    }

    /**
     * 是否是主节点事务日志
     *
     * @param path 主节点事务日志
     * @return true，是；false，不是
     */
    public static boolean isNameNodeEditLog(String path) {
        return path != null && path.startsWith("edit-") && path.endsWith(".log");

    }

    /**
     * 获取主节点镜像持久化路径
     *
     * @return 主节点镜像持久化路径
     */
    public static String getNameNodeImagePath() {
        return NAMENODE_IMAGE_PATH + "fsimage.meta";
    }

    /**
     * 获取主节点检查点事务日志序号路径
     *
     * @return 主节点检查点事务日志序号路径
     */
    public static String getNameNodeCheckpointTxidPath() {
        return NAMENODE_CHECKPOINT_TXID_PATH + "checkpoint-txid.meta";
    }

    /**
     * 获取备份节点镜像持久化路径
     *
     * @param txid 当前镜像的最大事务日志序号
     * @return 备份节点镜像持久化路径
     */
    public static String getBackupNodeImagePath(long txid) {
        return BACKUPNODE_IMAGE_PATH + "fsimage-" + txid + ".meta";
    }

    /**
     * 获取备份节点检查点信息路径
     *
     * @return 备份节点检查点信息路径
     */
    public static String getBackupNodeCheckpointInfoPath() {
        return BACKUPNODE_CHECKPOINT_INFO_PATH + "checkpoint-info.meta";
    }
}
