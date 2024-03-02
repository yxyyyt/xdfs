package com.sciatta.xdfs.backupnode;

/**
 * Created by Rain on 2024/2/26<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * 备份节点
 */
public class BackupNode {
    private EditLogFetcher editLogFetcher;
    private FSImageCheckpointer imageCheckpointer;
    private FSNameSystem nameSystem;
    private NameNodeRpcClient nameNodeRpcClient;

    /**
     * 初始化
     */
    private void initialize() {
        this.nameSystem = new FSNameSystem();
        this.nameNodeRpcClient = new NameNodeRpcClient();
        this.editLogFetcher = new EditLogFetcher(this.nameSystem, this.nameNodeRpcClient);
        this.imageCheckpointer = new FSImageCheckpointer(this.nameSystem, this.nameNodeRpcClient);
    }

    /**
     * 启动
     */
    private void start() {
        this.editLogFetcher.start();
        this.imageCheckpointer.start();
    }

    public static void main(String[] args) throws Exception {
        BackupNode backupNode = new BackupNode();
        backupNode.initialize();
        backupNode.start();
    }
}
