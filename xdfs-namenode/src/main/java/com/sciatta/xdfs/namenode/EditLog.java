package com.sciatta.xdfs.namenode;

/**
 * Created by Rain on 2024/2/22<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * 操作日志
 */
public class EditLog {
    /**
     * 操作日志序号
     */
    private long txid;

    /**
     * 操作日志内容
     */
    private String content;

    public EditLog(long txid, String content) {
        this.txid = txid;
        this.content = content;
    }

    public long getTxid() {
        return txid;
    }
    public void setTxid(long txid) {
        this.txid = txid;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "EditLog [txid=" + txid + ", content=" + content + "]";
    }

}
