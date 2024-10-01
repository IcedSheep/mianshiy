package com.sheep.mianshiy.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Json工具类
 */
public class JsonUtils {

    private JsonUtils() {}
    private static final  Gson gson = new GsonBuilder().serializeNulls().create();

    /**
     * 将对象转成 json
     */

    public static String toJson (Object object) {
        return gson.toJson(object);
    }

    /***
     * 将json 转成对象
     */

    public static <T> T fromJson(String jsonStr, Type type) {
        return gson.fromJson(jsonStr,type);
    }

    public static <T> T fromJson(String json, Class<T> tClass) {
        return gson.fromJson(json, tClass);
    }

    /**
     * json 转成 List
     * @param jsonStr
     * @param clazz
     * @return
     * @param <T>
     */
    public static <T> List<T> jsonToList(String jsonStr, Class<T> clazz) {
        Type type = TypeToken.getParameterized(List.class, clazz).getType();
        return gson.fromJson(jsonStr, type);
    }

    /**
     * json 转成 Map
     * @param jsonStr
     * @param keyClass
     * @param valueClass
     * @return
     * @param <K>
     * @param <V>
     */
    public static <K, V> Map<K, V> jsonToMap(String jsonStr, Class<K> keyClass, Class<V> valueClass) {
        Type type = TypeToken.getParameterized(Map.class, keyClass, valueClass).getType();
        return gson.fromJson(jsonStr, type);
    }



}
