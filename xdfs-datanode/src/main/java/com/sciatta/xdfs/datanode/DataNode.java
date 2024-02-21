package com.sciatta.xdfs.datanode;

/**
 * Created by Rain on 2024/2/21<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * 数据节点
 */
public class DataNode {
    /**
     * 是否正在运行
     */
    private volatile Boolean shouldRun;

    private NameNodeOfferService offerService;

    /**
     * 初始化
     */
    private void initialize() {
        this.shouldRun = true;
        this.offerService = new NameNodeOfferService();
        this.offerService.start();
    }

    /**
     * 启动
     */
    private void start() {
        try {
            while (shouldRun) {
                Thread.sleep(1000);
            }
        } catch (Exception ignore) {
        }
    }

    public static void main(String[] args) {
        DataNode datanode = new DataNode();
        datanode.initialize();
        datanode.start();
    }
}
