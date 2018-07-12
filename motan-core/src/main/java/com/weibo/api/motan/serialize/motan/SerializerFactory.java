package com.weibo.api.motan.serialize.motan;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created on 2018/7/11
 *
 * @author: luominggang
 * Description:
 */
public final class SerializerFactory {
    private static final ConcurrentHashMap<Class<?>, Serializer> serializers = new ConcurrentHashMap<>();

    static {
        // register serializers
        registerSerializer(byte[].class, new BasicSerializer(byte[].class));
        registerSerializer(String.class, new BasicSerializer(String.class));

        registerSerializer(boolean.class, new BasicSerializer(boolean.class));
        registerSerializer(Boolean.class, new BasicSerializer(Boolean.class));

        registerSerializer(byte.class, new BasicSerializer(byte.class));
        registerSerializer(Byte.class, new BasicSerializer(Byte.class));

        registerSerializer(short.class, new BasicSerializer(short.class));
        registerSerializer(Short.class, new BasicSerializer(Short.class));

        registerSerializer(int.class, new BasicSerializer(int.class));
        registerSerializer(Integer.class, new BasicSerializer(Integer.class));

        registerSerializer(long.class, new BasicSerializer(long.class));
        registerSerializer(Long.class, new BasicSerializer(Long.class));

        registerSerializer(float.class, new BasicSerializer(float.class));
        registerSerializer(Float.class, new BasicSerializer(Float.class));

        registerSerializer(double.class, new BasicSerializer(double.class));
        registerSerializer(Double.class, new BasicSerializer(Double.class));

        registerSerializer(Map.class, new MapSerializer(Map.class));
        registerSerializer(HashMap.class, new MapSerializer(HashMap.class));
        registerSerializer(TreeMap.class, new MapSerializer(TreeMap.class));

        registerSerializer(List.class, new CollectionSerializer(List.class));
        registerSerializer(Collection.class, new CollectionSerializer(Collection.class));
        registerSerializer(ArrayList.class, new CollectionSerializer(ArrayList.class));
        registerSerializer(LinkedList.class, new CollectionSerializer(LinkedList.class));

        registerSerializer(Set.class, new CollectionSerializer(Set.class));
        registerSerializer(HashSet.class, new CollectionSerializer(HashSet.class));
        registerSerializer(TreeSet.class, new CollectionSerializer(TreeSet.class));

        registerSerializer(GenericMessage.class, new GenericMessageSerializer());
    }

    public static void registerSerializer(Class<?> clz, Serializer serializer) {
        serializers.putIfAbsent(clz, serializer);
    }

    public static Serializer getSerializer(Class<?> clz) {
        Serializer serializer = serializers.get(clz);
        if (serializer != null) {
            return serializer;
        }
        if (Collection.class.isAssignableFrom(clz)) {
            registerSerializer(clz, new CollectionSerializer(clz));
        } else if (Map.class.isAssignableFrom(clz)) {
            registerSerializer(clz, new MapSerializer(clz));
        }
        return serializers.get(clz);
    }
}
