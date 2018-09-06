package com.fsix.mqtt.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * ————————————————————————————————
 * Copyright (C) 2014-2016, by het, Shenzhen, All rights reserved.
 * ————————————————————————————————
 * <p>
 * <p>描述：</p>
 * 名称:  <br>
 * 作者: uuxia<br>
 * 版本: 1.0<br>
 * 日期: 2016/11/2 10:03<br>
 **/
public class GsonUtil {
    private static GsonUtil instance = null;
    private Gson gson = null;

    public GsonUtil() {
        gson = getGson();
    }

    public static GsonUtil getInstance() {
        if (instance == null) {
            synchronized (GsonUtil.class) {
                if (null == instance) {
                    instance = new GsonUtil();
                }
            }
        }
        return instance;
    }


    public Gson getGson() {
        if (gson == null) {
//            gson = new Gson();
//            gson = new GsonBuilder()
//                    .registerTypeAdapter(
//                            new TypeToken<TreeMap<String, Object>>() {
//                            }.getType(),
//                            new JsonDeserializer<TreeMap<String, Object>>() {
//                                @Override
//                                public TreeMap<String, Object> deserialize(
//                                        JsonElement json, Type typeOfT,
//                                        JsonDeserializationContext context) throws JsonParseException {
//
//                                    TreeMap<String, Object> treeMap = new TreeMap<>();
//                                    JsonObject jsonObject = json.getAsJsonObject();
//                                    Set<Map.Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
//                                    for (Map.Entry<String, JsonElement> entry : entrySet) {
//                                        treeMap.put(entry.getKey(), entry.getValue());
//                                    }
//                                    return treeMap;
//                                }
//                            }).create();

            gson = new GsonBuilder().registerTypeAdapter(Double.class, new JsonSerializer<Double>() {
                @Override
                public JsonElement serialize(Double src, Type typeOfSrc, JsonSerializationContext context) {
                    if (src == src.longValue())
                        return new JsonPrimitive(src.longValue());
                    return new JsonPrimitive(src);
                }
            }).excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC).serializeNulls().create();
        }
        return gson;
    }

    public <T> T toObject(String json, Class<T> cls) {
        return gson.fromJson(json, cls);
    }

    public <T> T toObject(String json, Type type) {
        return gson.fromJson(json, type);
    }

    public String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public Map toLongMap(String json) {
        Type type = new TypeToken<Map<String, Long>>() {
        }.getType();
        return gson.fromJson(json, type);
    }
}
