package com.sciatta.xdfs.common.util;

/**
 * Created by Rain on 2023/8/17<br>
 * All Rights Reserved(C) 2017 - 2023 SCIATTA <br> <p/>
 * 断言工具类
 */
public class AssertUtils {

    private AssertUtils() {
    }

    /**
     * 指定字符串不能为空
     *
     * @param string  指定字符串
     * @param message 若为空时的提示信息
     */
    public static void notEmpty(String string, String message) {
        if (StringUtils.isEmpty(string)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 指定字符串不能包含空白字符
     *
     * @param string  指定字符串
     * @param message 若包含空白字符时的提示信息
     */
    public static void notBlank(String string, String message) {
        if (StringUtils.isBlank(string)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 指定对象不能是null
     *
     * @param object  指定对象
     * @param message 若是null时的提示信息
     */
    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 指定布尔值必须是true
     *
     * @param value   指定布尔值
     * @param message 若不是true时的提示信息
     */
    public static void isTrue(boolean value, String message) {
        if (!value) {
            throw new IllegalArgumentException(message);
        }
    }

}
