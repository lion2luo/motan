/*
 *
 *   Copyright 2009-2016 Weibo, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */

package com.weibo.api.motan.serialize.motan;

import com.weibo.api.motan.codec.Serialization;
import com.weibo.api.motan.codec.TypeDeserializer;
import com.weibo.api.motan.core.extension.SpiMeta;
import com.weibo.api.motan.exception.MotanServiceException;
import com.weibo.api.motan.protocol.v2motan.GrowableByteBuffer;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@SpiMeta(name = "motan")
public class MotanSerialization implements Serialization, TypeDeserializer {

    public static final byte FALSE = 0;
    public static final byte TRUE = 1;
    public static final byte NULL = 2;
    public static final byte BYTE = 3;
    public static final byte STRING = 4;
    public static final byte BYTE_ARRAY = 5;
    public static final byte INT16 = 6;
    public static final byte INT32 = 7;
    public static final byte INT64 = 8;
    public static final byte FLOAT32 = 9;
    public static final byte FLOAT64 = 10;
    public static final byte UNPACKED_ARRAY = 20;
    public static final byte UNPACKED_ARRAY_END = 21;
    public static final byte UNPACKED_MAP = 22;
    public static final byte UNPACKED_MAP_END = 23;
    public static final byte PACKED_ARRAY = 24;
    public static final byte PACKED_MAP = 25;
    public static final byte MESSAGE = 26;
    private static final Map<Class<?>, MessageTemplate<?>> MESSAGE_TEMPLATES = new ConcurrentHashMap<>();
    private static final int DEFAULT_MAP_SIZE = 16;
    private static final int DEFAULT_ARRAY_SIZE = 16;

    public static void registerMessageTemplate(Class clz, MessageTemplate template) {
        MESSAGE_TEMPLATES.put(clz, template);
    }

    public static MessageTemplate getMessageTemplate(Class clz) {
        return MESSAGE_TEMPLATES.get(clz);
    }

    public static <T> T toJavaPojo(Object obj, Type type) {
        Class<?> clz;
        Type genericType = null;
        if (type instanceof Class) {
            clz = (Class<?>) type;
            genericType = null; // for containers (List Map Set), we need generic parameter, here just set to null, it means we don't know
        } else if (type instanceof ParameterizedType) {
            clz = (Class<?>) ((ParameterizedType) type).getRawType();
            genericType = type;
        } else {
            // TODO: for WildType (<?>), GenericArrayType, TypeVariable <T extends ObjectClass>
            throw new MotanServiceException("MotanSerialization unsupported type " + genericType);
        }

        if (obj == null) {
            return null;
        }

        if (clz == Object.class) {
            return (T) obj;
        }

        if (clz == String.class && obj instanceof String) {
            return (T) obj;
        }

        if ((clz == boolean.class || clz == Boolean.class) && obj instanceof Boolean) {
            return (T) obj;
        }

        if ((clz == byte.class || clz == Byte.class) && obj instanceof Byte) {
            return (T) obj;
        }

        if ((clz == short.class || clz == Short.class)) {
            if (obj instanceof Short) {
                return (T) obj;
            }
            if (obj instanceof Number) {
                return (T) Short.valueOf(((Number) obj).shortValue());
            }
        }

        if ((clz == int.class || clz == Integer.class)) {
            if (obj instanceof Integer) {
                return (T) obj;
            }
            if (obj instanceof Number) {
                return (T) Integer.valueOf(((Number) obj).intValue());
            }
        }

        if ((clz == long.class || clz == Long.class)) {
            if (obj instanceof Long) {
                return (T) obj;
            }
            if (obj instanceof Number) {
                return (T) Long.valueOf(((Number) obj).longValue());
            }
        }

        if ((clz == float.class || clz == Float.class)) {
            if (obj instanceof Float) {
                return (T) obj;
            }
            if (obj instanceof Number) {
                return (T) Float.valueOf(((Number) obj).floatValue());
            }
        }

        if ((clz == double.class || clz == Double.class)) {
            if (obj instanceof Double) {
                return (T) obj;
            }
            if (obj instanceof Number) {
                return (T) Double.valueOf(((Number) obj).doubleValue());
            }
        }

        if (clz == byte[].class && obj.getClass() == byte[].class) {
            return (T) obj;
        }

        if (clz.isArray() && obj instanceof List) {
            if (!(obj instanceof List)) {
                throw new MotanServiceException("MotanSerialization not support " + obj.getClass() + " as Array");
            }
            List<?> objects = new ArrayList<>(((List) obj).size());
            toJavaPojoCollection((List) obj, clz.getComponentType(), objects);
            Object[] arrayObj = new Object[objects.size()];
            return (T) objects.toArray(arrayObj);
        }

        if (List.class.isAssignableFrom(clz)) {
            if (!(obj instanceof List)) {
                throw new MotanServiceException("MotanSerialization not support " + obj.getClass() + " as List");
            }
            List<?> result;
            List objects = (List) obj;
            if (clz.isAssignableFrom(LinkedList.class)) {
                result = new LinkedList<>();
            } else if (clz.isAssignableFrom(ArrayList.class)) {
                result = new ArrayList<>(objects.size());
            } else {
                throw new MotanServiceException("MotanSerialization not support " + clz + " with generic parameter type " + genericType + ", value type " + obj.getClass());
            }
            toJavaPojoCollection(objects, genericType, result);
            return (T) result;
        }

        if (Set.class.isAssignableFrom(clz)) {
            if (!(obj instanceof List)) {
                throw new MotanServiceException("MotanSerialization not support " + obj.getClass() + " as Set");
            }
            Set<?> result;
            List objects = (List) obj;
            if (clz.isAssignableFrom(TreeSet.class)) {
                result = new TreeSet<>();
            } else if (clz.isAssignableFrom(HashSet.class)) {
                result = new HashSet<>(objects.size());
            } else {
                throw new MotanServiceException("MotanSerialization not support " + clz + " with generic parameter type " + genericType + ", value type " + obj.getClass());
            }
            toJavaPojoCollection(objects, genericType, result);
            return (T) result;
        }

        if (Map.class.isAssignableFrom(clz)) {
            // TODO: for performance we should do more type check to skip object copy
            if (!(obj instanceof Map)) {
                throw new MotanServiceException("MotanSerialization not support " + obj.getClass() + " as Map");
            }
            Map result;
            if (clz.isAssignableFrom(TreeMap.class)) {
                result = new TreeMap<>();
            } else if (clz.isAssignableFrom(HashMap.class)) {
                result = new HashMap<>(((Map) obj).size());
            } else {
                throw new MotanServiceException("MotanSerialization not support " + clz + " with generic parameter type " + genericType + ", value type " + obj.getClass());
            }
            Type keyType = Object.class; // for unknown generic parameter type
            Type valueType = Object.class;
            if (genericType instanceof ParameterizedType) {
                Type[] actualTypeArguments = ((ParameterizedType) genericType).getActualTypeArguments();
                if (actualTypeArguments.length == 2) {
                    keyType = actualTypeArguments[0];
                    valueType = actualTypeArguments[1];
                }
            }
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) obj).entrySet()) {
                result.put(toJavaPojo(entry.getKey(), keyType), toJavaPojo(entry.getValue(), valueType));
            }
            return (T) result;
        }

        if (obj instanceof GenericMessage) {
            if (clz == GenericMessage.class) {
                return (T) obj;
            }
            MessageTemplate messageTemplate = getMessageTemplate(clz);
            if (messageTemplate == null) {
                throw new MotanServiceException("MotanSerialization not support " + clz);
            }
            return (T) messageTemplate.fromMessage((GenericMessage) obj);
        }

        throw new MotanServiceException("MotanSerialization not support " + clz + " with generic parameter type " + genericType + ", value type " + obj.getClass());
    }

    private static void toJavaPojoCollection(List objects, Type type, Collection target) {
        // TODO: for performance we should do more type check to skip object copy
        Type parameterType = Object.class; // for unknown generic parameter type
        if (type instanceof ParameterizedType) {
            Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
            if (actualTypeArguments.length == 1) {
                parameterType = actualTypeArguments[0];
            }
        }
        for (Object object : objects) {
            target.add(toJavaPojo(object, parameterType));
        }
    }

    @Override
    public byte[] serialize(Object obj) throws IOException {
        GrowableByteBuffer buffer = new GrowableByteBuffer(4096);
        serialize(obj, buffer);
        buffer.flip();
        byte[] result = new byte[buffer.remaining()];
        buffer.get(result);
        return result;
    }

    @Override
    public byte[] serializeMulti(Object[] data) throws IOException {
        GrowableByteBuffer buffer = new GrowableByteBuffer(4096);
        for (Object o : data) {
            serialize(o, buffer);
        }
        buffer.flip();
        byte[] result = new byte[buffer.remaining()];
        buffer.get(result);
        return result;
    }

    private void serialize(Object obj, GrowableByteBuffer buffer) throws IOException {
        if (obj == null) {
            buffer.put(NULL);
            return;
        }

        Class<?> clz = obj.getClass();
        if (clz == String.class) {
            writeString(buffer, (String) obj);
            return;
        }

        if (clz == Byte.class || clz == byte.class) {
            writeByte(buffer, (Byte) obj);
            return;
        }

        if (clz == Boolean.class || clz == boolean.class) {
            writeBool(buffer, (Boolean) obj);
            return;
        }

        if (clz == Short.class || clz == short.class) {
            writeInt16(buffer, (Short) obj);
            return;
        }

        if (clz == Integer.class || clz == int.class) {
            writeInt32(buffer, (Integer) obj);
            return;
        }

        if (clz == Long.class || clz == long.class) {
            writeInt64(buffer, (Long) obj);
            return;
        }

        if (clz == Float.class || clz == float.class) {
            writeFloat32(buffer, (Float) obj);
            return;
        }

        if (clz == Double.class || clz == double.class) {
            writeFloat64(buffer, (Double) obj);
            return;
        }

        if (clz == GenericMessage.class) {
            writeMessage(buffer, (GenericMessage) obj);
            return;
        }

        if (obj instanceof Map) {
            writeUnpackedMap(buffer, (Map) obj);
            return;
        }

        if (clz.isArray()) {
            if (clz.getComponentType() == byte.class) {
                writeBytes(buffer, (byte[]) obj);
            } else {
                writeUnpackedArray(buffer, (Object[]) obj);
            }
            return;
        }

        if (obj instanceof List || obj instanceof Set) {
            writeUnpackedArray(buffer, (Collection) obj);
            return;
        }

        // ok, if it is not a basic type, use message template converter
        MessageTemplate messageTemplate = getMessageTemplate(clz);
        if (messageTemplate != null) {
            writeMessage(buffer, messageTemplate.toMessage(obj));
            return;
        }
        throw new MotanServiceException("MotanSerialization unsupported type: " + clz);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clz) throws IOException {
        return deserializeByType(bytes, clz);
    }

    @Override
    public Object[] deserializeMulti(byte[] bytes, Class<?>[] classes) throws IOException {
        return deserializeMulti(bytes, classes);
    }

    @Override
    public <T> T deserializeByType(byte[] bytes, Type type) throws IOException {
        GrowableByteBuffer buffer = new GrowableByteBuffer(ByteBuffer.wrap(bytes));
        return toJavaPojo(deserialize(buffer), type);
    }

    @Override
    public Object[] deserializeMultiByType(byte[] data, Type[] types) throws IOException {
        GrowableByteBuffer buffer = new GrowableByteBuffer(ByteBuffer.wrap(data));
        Object[] result = new Object[types.length];
        for (int i = 0; i < types.length; i++) {
            result[i] = toJavaPojo(deserialize(buffer), types[i]);
        }
        return result;
    }

    private Object deserialize(GrowableByteBuffer buffer) throws IOException {
        byte type = buffer.get();
        switch (type) {
            default:
                break;
            case TRUE:
                return Boolean.TRUE;
            case FALSE:
                return Boolean.FALSE;
            case BYTE:
                return readByte(buffer);
            case NULL:
                return null;
            case STRING:
                return readString(buffer);
            case BYTE_ARRAY:
                return readBytes(buffer);
            case INT16:
                return readInt16(buffer);
            case INT32:
                return readInt32(buffer);
            case INT64:
                return readInt64(buffer);
            case FLOAT32:
                return readFloat32(buffer);
            case FLOAT64:
                return readFloat64(buffer);
            case UNPACKED_MAP:
                return readUnpackedMap(buffer);
            case UNPACKED_ARRAY:
                return readUnpackedList(buffer);
            case MESSAGE:
                return readMessage(buffer);
        }
        throw new MotanServiceException("MotanSerialization not support " + type);
    }

    @Override
    public int getSerializationNumber() {
        return 8;
    }

    private void putString(GrowableByteBuffer buffer, String str) throws IOException {
        byte[] b = str.getBytes("UTF-8");
        buffer.putZigzag32(b.length);
        buffer.put(b);
    }

    private void writeString(GrowableByteBuffer buffer, String str) throws IOException {
        buffer.put(STRING);
        putString(buffer, str);
    }

    private void writeBytes(GrowableByteBuffer buffer, byte[] value) {
        buffer.put(BYTE_ARRAY);
        buffer.putZigzag32(value.length);
        buffer.put(value);
    }

    private void writeBool(GrowableByteBuffer buffer, boolean value) {
        if (value) {
            buffer.put(TRUE);
        } else {
            buffer.put(FALSE);
        }
    }

    private void writeByte(GrowableByteBuffer buffer, byte value) {
        buffer.put(BYTE);
        buffer.put(value);
    }

    private void writeInt16(GrowableByteBuffer buffer, short value) {
        buffer.put(INT16);
        buffer.putShort(value);
    }

    private void writeInt32(GrowableByteBuffer buffer, int value) {
        buffer.put(INT32);
        buffer.putZigzag32(value);
    }

    private void writeInt64(GrowableByteBuffer buffer, long value) {
        buffer.put(INT64);
        buffer.putZigzag64(value);
    }

    private void writeFloat32(GrowableByteBuffer buffer, float value) {
        buffer.put(FLOAT32);
        buffer.putFloat(value);
    }

    private void writeFloat64(GrowableByteBuffer buffer, double value) {
        buffer.put(FLOAT64);
        buffer.putDouble(value);
    }

    private void writeUnpackedArray(GrowableByteBuffer buffer, Object[] value) throws IOException {
        buffer.put(UNPACKED_ARRAY);
        for (int i = 0; i < value.length; i++) {
            serialize(value[i], buffer);
        }
        buffer.put(UNPACKED_ARRAY_END);
    }

    private void writeUnpackedArray(GrowableByteBuffer buffer, Collection<?> value) throws IOException {
        buffer.put(UNPACKED_ARRAY);
        for (Object v : value) {
            serialize(v, buffer);
        }
        buffer.put(UNPACKED_ARRAY_END);
    }

    private void writeUnpackedMap(GrowableByteBuffer buffer, Map<?, ?> value) throws IOException {
        buffer.put(UNPACKED_MAP);
        for (Map.Entry<?, ?> entry : value.entrySet()) {
            serialize(entry.getKey(), buffer);
            serialize(entry.getValue(), buffer);
        }
        buffer.put(UNPACKED_MAP_END);
    }

    private void writeMessage(GrowableByteBuffer buffer, GenericMessage message) throws IOException {
        buffer.put(MESSAGE);
        int pos = buffer.position();
        buffer.position(pos + 4);
        for (Map.Entry<Integer, Object> entry : message.getFields().entrySet()) {
            buffer.putZigzag32(entry.getKey());
            serialize(entry.getValue(), buffer);
        }
        int npos = buffer.position();
        buffer.position(pos);
        buffer.putInt(npos - pos - 4);
        buffer.position(npos);
    }

    private int getAndCheckZigZagSize(GrowableByteBuffer buffer) {
        int size = buffer.getZigZag32();
        if (size > buffer.remaining()) {
            throw new MotanServiceException("MotanSerialization deserialize fail! buffer not enough!need size:" + size);
        }
        return size;
    }

    private int getAndCheckSize(GrowableByteBuffer buffer) {
        int size = buffer.getInt();
        if (size > buffer.remaining()) {
            throw new MotanServiceException("MotanSerialization deserialize fail! buffer not enough!need size:" + size);
        }
        return size;
    }

    private String readString(GrowableByteBuffer buffer) throws IOException {
        return new String(readBytes(buffer), "UTF-8");
    }

    private byte[] readBytes(GrowableByteBuffer buffer) {
        int size = getAndCheckZigZagSize(buffer);
        byte[] b = new byte[size];
        buffer.get(b);
        return b;
    }

    private Byte readByte(GrowableByteBuffer buffer) {
        return buffer.get();
    }

    private Short readInt16(GrowableByteBuffer buffer) {
        return buffer.getShort();
    }

    private Integer readInt32(GrowableByteBuffer buffer) {
        return buffer.getZigZag32();
    }

    private Long readInt64(GrowableByteBuffer buffer) {
        return buffer.getZigZag64();
    }

    private Float readFloat32(GrowableByteBuffer buffer) {
        return buffer.getFloat();
    }

    private Double readFloat64(GrowableByteBuffer buffer) {
        return buffer.getDouble();
    }

    private Map readUnpackedMap(GrowableByteBuffer buffer) throws IOException {
        Map<Object, Object> map = new HashMap<>(DEFAULT_MAP_SIZE);
        while (buffer.get() != UNPACKED_MAP_END) {
            buffer.position(buffer.position() - 1);
            map.put(deserialize(buffer), deserialize(buffer));
        }
        return map;
    }

    private List<Object> readUnpackedList(GrowableByteBuffer buffer) throws IOException {
        List<Object> result = new ArrayList<>(DEFAULT_ARRAY_SIZE);
        return readUnpackedCollection(buffer, result);
    }

    private <T extends Collection> T readUnpackedCollection(GrowableByteBuffer buffer, T collection) throws IOException {
        while (buffer.get() != UNPACKED_ARRAY_END) {
            buffer.position(buffer.position() - 1);
            collection.add(deserialize(buffer));
        }
        return collection;
    }

    private GenericMessage readMessage(GrowableByteBuffer buffer) throws IOException {
        GenericMessage message = new GenericMessage(DEFAULT_MAP_SIZE);
        int size = getAndCheckSize(buffer);
        int startPos = buffer.position();
        int endPos = startPos + size;
        while (buffer.position() < endPos) {
            message.putField(buffer.getZigZag32(), deserialize(buffer));
        }
        if (buffer.position() != endPos) {
            throw new MotanServiceException("MotanSerialization deserialize wrong message size, except: " + size + " actual: " + (buffer.position() - startPos));
        }
        return message;
    }
}
