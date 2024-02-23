package com.sciatta.xdfs.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rain on 2023/8/20<br>
 * All Rights Reserved(C) 2017 - 2023 SCIATTA <br> <p/>
 * Json序列化工具类；基于fastjson，序列化的模型需要有getter方法，反序列化的模型需要有默认构造函数
 */
@Slf4j
public class FastJsonUtils {

    private static final SerializerFeature[] featuresWithNullValue = {
            SerializerFeature.WriteMapNullValue,    // Map为null或value为null时，序列化为null
            SerializerFeature.WriteNullBooleanAsFalse,  // Boolean为null时，序列化为false
            SerializerFeature.WriteNullListAsEmpty, // List为null时，序列化为空list
            SerializerFeature.WriteNullNumberAsZero,    // Number为null时，序列化为0
            SerializerFeature.WriteNullStringAsEmpty    // String为null时，序列化为空串
    };

    private static final SerializerFeature[] featuresWithClassName = {
            SerializerFeature.WriteClassName    // 反序列化时不丢失子类类型
    };

    private FastJsonUtils() {

    }

    /**
     * 解析json字符串为指定类型的对象
     *
     * @param jsonString json字符串
     * @param clazz      指定类型
     * @param <T>        指定类型泛型
     * @return 对象
     */
    public static <T> T parseJsonStringToObject(String jsonString, Class<T> clazz) {
        try {
            return JSON.parseObject(jsonString, clazz);
        } catch (Exception e) {
            log.error("Failed to parse json into an object, exception is {}", e.getMessage());
            return null;
        }
    }

    /**
     * 解析json字符串为指定类型的对象集合
     *
     * @param jsonString json字符串
     * @param clazz      指定类型
     * @param <T>        指定类型泛型
     * @return 对象集合
     */
    public static <T> List<T> parseJsonStringToObjects(String jsonString, Class<T> clazz) {
        try {
            return JSON.parseArray(jsonString, clazz);
        } catch (Exception e) {
            log.error("Failed to parse json into a list, exception is {}", e.getMessage());
            return null;
        }
    }

    /**
     * 解析json对象为指定类型的对象
     *
     * @param jsonObject json对象
     * @param clazz      指定类型
     * @param <T>        指定类型泛型
     * @return 对象
     */
    public static <T> T parseJsonObjectToObject(JSONObject jsonObject, Class<T> clazz) {
        try {
            return JSONObject.toJavaObject(jsonObject, clazz);
        } catch (Exception e) {
            log.error("Failed to parse json object into an object, exception is {}", e.getMessage());
            return null;
        }
    }

    /**
     * 解析json对象集合为指定类型的对象集合
     *
     * @param jsonObjectList json对象集合
     * @param clazz          指定类型
     * @param <T>            指定类型泛型
     * @return 对象集合
     */
    public static <T> List<T> parseJsonObjectsToObjects(List<JSONObject> jsonObjectList, Class<T> clazz) {
        try {
            List<T> t = new ArrayList<>();
            for (JSONObject jsonObject : jsonObjectList) {
                t.add(parseJsonObjectToObject(jsonObject, clazz));
            }
            return t;
        } catch (Exception e) {
            log.error("Failed to parse json objects into a list, exception is {}", e.getMessage());
            return null;
        }
    }

    /**
     * 格式化指定对象为json字符串
     *
     * @param object 指定对象
     * @return json字符串
     */
    public static String formatObjectToJsonString(Object object) {
        try {
            return JSON.toJSONString(object);
        } catch (Exception e) {
            log.error("Failed to format object into a json, exception is {}", e.getMessage());
            return null;
        }
    }

    /**
     * 格式化指定对象为json字符串。处理null值规则如下：<br>
     * 1. Map为null或value为null时，序列化为null<br>
     * 2. Boolean为null时，序列化为false<br>
     * 3. List为null时，序列化为空list<br>
     * 4. Number为null时，序列化为0<br>
     * 5. String为null时，序列化为空串
     *
     * @param obj 指定对象
     * @return json字符串
     */
    public static String formatObjectToJsonStringWithNullValue(Object obj) {
        try {
            return JSON.toJSONString(obj, featuresWithNullValue);
        } catch (Exception e) {
            log.error("Failed to format object into a json with null value, exception is {}", e.getMessage());
            return null;
        }
    }

    /**
     * 格式化指定对象为json字符串。其中json字符串包含@type，用于反序列化时不丢失子类类型
     *
     * @param obj 指定对象
     * @return json字符串
     */
    public static String formatObjectToJsonStringWithClassName(Object obj) {
        try {
            return JSON.toJSONString(obj, featuresWithClassName);
        } catch (Exception e) {
            log.error("Failed to format object into a json with class name, exception is {}", e.getMessage());
            return null;
        }
    }

    /**
     * 格式化指定对象为json对象
     *
     * @param object 指定对象
     * @return json对象
     */
    public static JSONObject formatObjectToJsonObject(Object object) {
        try {
            return (JSONObject) JSONObject.toJSON(object);
        } catch (Exception e) {
            log.error("Failed to format object into a json object, exception is {}", e.getMessage());
            return null;
        }
    }

}
