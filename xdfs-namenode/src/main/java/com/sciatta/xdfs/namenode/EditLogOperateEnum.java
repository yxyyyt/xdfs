package com.sciatta.xdfs.namenode;

import lombok.Getter;

/**
 * Created by Rain on 2024/2/23<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * 事务日志操作枚举
 */
@Getter
public enum EditLogOperateEnum {
    MKDIR("MKDIR");

    private final String operate;

    EditLogOperateEnum(String operate) {
        this.operate = operate;
    }
}
