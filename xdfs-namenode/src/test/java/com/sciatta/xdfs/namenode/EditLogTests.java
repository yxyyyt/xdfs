package com.sciatta.xdfs.namenode;

import com.sciatta.xdfs.common.fs.EditLog;
import com.sciatta.xdfs.common.fs.EditLogOperateEnum;
import com.sciatta.xdfs.common.util.FastJsonUtils;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Rain on 2024/2/23<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * EditLogTests
 */
public class EditLogTests {
    @Test
    public void testDeserialize() {
        String path = "/a/b";
        long txid = 1;

        String jsonString = FastJsonUtils.formatObjectToJsonString(new EditLogFactory() {
            @Override
            public EditLog create(long txid) {
                EditLog editLog = new EditLog();
                editLog.setTxid(txid);
                editLog.setPath(path);
                editLog.setOperate(EditLogOperateEnum.MKDIR.getOperate());
                return editLog;
            }
        }.create(txid));

        EditLog editLog = FastJsonUtils.parseJsonStringToObject(jsonString, EditLog.class);
        assertNotNull(editLog);
        assertEquals(txid, editLog.getTxid());
        assertEquals(path, editLog.getPath());
        assertEquals(EditLogOperateEnum.MKDIR.getOperate(), editLog.getOperate());
    }
}
