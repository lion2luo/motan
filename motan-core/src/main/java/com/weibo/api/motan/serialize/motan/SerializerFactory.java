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
    private static final ConcurrentHashMap<Class<?>, Deserializer> deserializers = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Class<?>, Serializer> serializers = new ConcurrentHashMap<>();

    static {
        // register deserializers
        registerDeserializer(byte[].class, new BasicDeserializer(byte[].class));
        registerDeserializer(String.class, new BasicDeserializer(String.class));

        registerDeserializer(boolean.class, new BasicDeserializer(boolean.class));
        registerDeserializer(Boolean.class, new BasicDeserializer(Boolean.class));

        registerDeserializer(byte.class, new BasicDeserializer(byte.class));
        registerDeserializer(Byte.class, new BasicDeserializer(Byte.class));

        registerDeserializer(short.class, new BasicDeserializer(short.class));
        registerDeserializer(Short.class, new BasicDeserializer(Short.class));

        registerDeserializer(int.class, new BasicDeserializer(int.class));
        registerDeserializer(Integer.class, new BasicDeserializer(Integer.class));

        registerDeserializer(long.class, new BasicDeserializer(long.class));
        registerDeserializer(Long.class, new BasicDeserializer(Long.class));

        registerDeserializer(float.class, new BasicDeserializer(float.class));
        registerDeserializer(Float.class, new BasicDeserializer(Float.class));

        registerDeserializer(double.class, new BasicDeserializer(double.class));
        registerDeserializer(Double.class, new BasicDeserializer(Double.class));

        registerDeserializer(Map.class, new MapDeserializer(Map.class));
        registerDeserializer(HashMap.class, new MapDeserializer(HashMap.class));
        registerDeserializer(TreeMap.class, new MapDeserializer(TreeMap.class));

        registerDeserializer(Collection.class, new CollectionDeserializer(Collection.class));
        registerDeserializer(List.class, new CollectionDeserializer(List.class));
        registerDeserializer(ArrayList.class, new CollectionDeserializer(ArrayList.class));
        registerDeserializer(LinkedList.class, new CollectionDeserializer(LinkedList.class));

        registerDeserializer(Set.class, new CollectionDeserializer(Set.class));
        registerDeserializer(HashSet.class, new CollectionDeserializer(HashSet.class));
        registerDeserializer(TreeSet.class, new CollectionDeserializer(TreeSet.class));

        registerDeserializer(GenericMessage.class, new GenericMessageDeserializer());


        // register serializers
        registerSerializer(byte[].class, new BasicSerializer());
        registerSerializer(String.class, new BasicSerializer());

        registerSerializer(boolean.class, new BasicSerializer());
        registerSerializer(Boolean.class, new BasicSerializer());

        registerSerializer(byte.class, new BasicSerializer());
        registerSerializer(Byte.class, new BasicSerializer());

        registerSerializer(short.class, new BasicSerializer());
        registerSerializer(Short.class, new BasicSerializer());

        registerSerializer(int.class, new BasicSerializer());
        registerSerializer(Integer.class, new BasicSerializer());

        registerSerializer(long.class, new BasicSerializer());
        registerSerializer(Long.class, new BasicSerializer());

        registerSerializer(float.class, new BasicSerializer());
        registerSerializer(Float.class, new BasicSerializer());

        registerSerializer(double.class, new BasicSerializer());
        registerSerializer(Double.class, new BasicSerializer());

        registerSerializer(Map.class, new MapSerializer());
        registerSerializer(HashMap.class, new MapSerializer());
        registerSerializer(TreeMap.class, new MapSerializer());

        registerSerializer(List.class, new CollectionSerializer());
        registerSerializer(ArrayList.class, new CollectionSerializer());
        registerSerializer(LinkedList.class, new CollectionSerializer());

        registerSerializer(Set.class, new CollectionSerializer());
        registerSerializer(HashSet.class, new CollectionSerializer());
        registerSerializer(TreeSet.class, new CollectionSerializer());

        registerSerializer(GenericMessage.class, new GenericMessageSerializer());
    }

    public static void registerDeserializer(Class<?> clz, Deserializer deserializer) {
        deserializers.putIfAbsent(clz, deserializer);
    }

    public static Deserializer getDeserializer(Class<?> clz) {
        Deserializer deserializer = deserializers.get(clz);
        if (deserializer != null) {
            return deserializer;
        }
        if (Collection.class.isAssignableFrom(clz)) {
            registerDeserializer(clz, new CollectionDeserializer(clz));
        } else if (Map.class.isAssignableFrom(clz)) {
            registerDeserializer(clz, new MapDeserializer(clz));
        }
        return deserializers.get(clz);
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
            registerSerializer(clz, new CollectionSerializer());
        } else if (Map.class.isAssignableFrom(clz)) {
            registerSerializer(clz, new MapSerializer());
        }
        return serializers.get(clz);
    }
}
