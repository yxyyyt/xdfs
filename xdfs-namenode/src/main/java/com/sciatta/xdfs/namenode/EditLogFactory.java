package com.sciatta.xdfs.namenode;

import com.sciatta.xdfs.common.fs.EditLog;

/**
 * Created by Rain on 2024/2/23<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * 事务日志工厂
 */
public interface EditLogFactory {
    /**
     * 创建事务日志
     *
     * @param txid 事务日志序号
     * @return 事务日志
     */
    EditLog create(long txid);
}
