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
import com.weibo.api.motan.core.extension.SpiMeta;
import com.weibo.api.motan.exception.MotanServiceException;
import com.weibo.api.motan.protocol.v2motan.GrowableByteBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

@SpiMeta(name = "motan")
public class MotanSerialization implements Serialization {
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


    private static final int DEFAULT_MAP_SIZE = 16;
    private static final int DEFAULT_ARRAY_SIZE = 16;

    public static boolean isBasicSerializationType(Class<?> clz) {
        if (clz == boolean.class || clz == Boolean.class
                || clz == byte.class || clz == Byte.class
                || clz == short.class || clz == Short.class
                || clz == int.class || clz == Integer.class
                || clz == long.class || clz == Long.class
                || clz == float.class || clz == Float.class
                || clz == double.class || clz == Double.class
                || clz == String.class || clz == byte[].class) {
            return true;
        }
        return false;
    }

    public static Object toMotanSerializableValue(Object obj) {
        if (obj == null) {
            return null;
        }

        Class clz = obj.getClass();
        if (isBasicSerializationType(clz)) {
            return obj;
        }

        if (clz.isArray() || List.class.isAssignableFrom(clz) || Set.class.isAssignableFrom(clz)
                || Map.class.isAssignableFrom(clz) || clz == GenericMessage.class) {
            // TODO: make sure the element in these containers can be serialized
            return obj;
        }

        MessageTemplate messageTemplate = MessageTemplate.getMessageTemplate(obj.getClass());
        if (messageTemplate == null) {
            throw new MotanServiceException("MotanSerialization not support " + obj.getClass());
        }
        return messageTemplate.toMessage(obj);

    }

    public static <T> T toJavaPojo(Object obj, Class<T> clz) {
        Object result = obj;
        if (obj == null) {
            return null;
        }

        if (isBasicSerializationType(clz) || clz == Object.class) {
            return (T) result;
        }

        if (clz.isArray() || List.class.isAssignableFrom(clz) || Set.class.isAssignableFrom(clz)
                || Map.class.isAssignableFrom(clz) || clz == GenericMessage.class) {
            // TODO: make sure the element deserialize as excepted type
            return (T) result;
        }

        MessageTemplate messageTemplate = MessageTemplate.getMessageTemplate(clz);
        if (messageTemplate == null) {
            throw new MotanServiceException("MotanSerialization not support " + clz);
        }
        return (T) messageTemplate.fromMessage((GenericMessage) obj);
    }

    @Override
    public byte[] serialize(Object obj) throws IOException {
        GrowableByteBuffer buffer = new GrowableByteBuffer(4096);
        serialize(toMotanSerializableValue(obj), buffer);
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

        // this should do before map, because GenericMessage inherit HashMap
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
        throw new MotanServiceException("MotanSerialization unsupported type: " + clz);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clz) throws IOException {
        GrowableByteBuffer buffer = new GrowableByteBuffer(ByteBuffer.wrap(bytes));
        return toJavaPojo(deserialize(buffer, clz), clz);
    }

    private <T> T deserialize(GrowableByteBuffer buffer, Class<T> clz) throws IOException {
        byte type = buffer.get();
        switch (type) {
            default:
                break;
            case TRUE:
                if (clz == boolean.class || clz == Boolean.class || clz == Object.class) {
                    return (T) Boolean.TRUE;
                }
                break;
            case FALSE:
                if (clz == boolean.class || clz == Boolean.class || clz == Object.class) {
                    return (T) Boolean.FALSE;
                }
                break;
            case BYTE:
                if (clz == byte.class || clz == Byte.class || clz == Object.class) {
                    return (T) readByte(buffer);
                }
            case NULL:
                return null;
            case STRING:
                if (clz == String.class || clz == Object.class) {
                    return (T) readString(buffer);
                }
                break;
            case BYTE_ARRAY:
                if (clz == byte[].class || clz == Object.class) {
                    return (T) readBytes(buffer);
                }
                break;
            case INT16:
                if (clz == short.class || clz == Short.class || clz == Object.class) {
                    return (T) readInt16(buffer);
                }
                break;
            case INT32:
                if (clz == int.class || clz == Integer.class || clz == Object.class) {
                    return (T) readInt32(buffer);
                }
                break;
            case INT64:
                if (clz == long.class || clz == Long.class || clz == Object.class) {
                    return (T) readInt64(buffer);
                }
                break;
            case FLOAT32:
                if (clz == float.class || clz == Float.class || clz == Object.class) {
                    return (T) readFloat32(buffer);
                }
                break;
            case FLOAT64:
                if (clz == double.class || clz == Double.class || clz == Object.class) {
                    return (T) readFloat64(buffer);
                }
                break;
            case UNPACKED_MAP:
                if (clz.isAssignableFrom(HashMap.class)) {
                    return (T) readUnpackedMap(buffer);
                }
                break;
            case UNPACKED_ARRAY:
                if (clz.isArray()) {
                    return (T) readUnpackedArray(buffer);
                } else if (clz.isAssignableFrom(ArrayList.class)) {
                    return (T) readUnpackedList(buffer);
                } else if (clz.isAssignableFrom(HashSet.class)) {
                    return (T) readUnpackedSet(buffer);
                }
                break;
            case MESSAGE:
                return (T) readMessage(buffer);
        }
        throw new MotanServiceException("MotanSerialization not support " + type + " with receiver type:" + clz);
    }

    @Override
    public byte[] serializeMulti(Object[] data) throws IOException {
        GrowableByteBuffer buffer = new GrowableByteBuffer(4096);
        for (Object o : data) {
            serialize(toMotanSerializableValue(o), buffer);
        }
        buffer.flip();
        byte[] result = new byte[buffer.remaining()];
        buffer.get(result);
        return result;
    }

    @Override
    public Object[] deserializeMulti(byte[] data, Class<?>[] classes) throws IOException {
        GrowableByteBuffer buffer = new GrowableByteBuffer(ByteBuffer.wrap(data));
        Object[] result = new Object[classes.length];
        for (int i = 0; i < classes.length; i++) {
            result[i] = toJavaPojo(deserialize(buffer, classes[i]), classes[i]);
        }
        return result;
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
        for (Map.Entry<Integer, Object> entry : message.entrySet()) {
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
        if (size == 0) {
            return new byte[]{};
        } else {
            byte[] b = new byte[size];
            buffer.get(b);
            return b;
        }
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
            map.put(deserialize(buffer, Object.class), deserialize(buffer, Object.class));
        }
        return map;
    }

    private Object[] readUnpackedArray(GrowableByteBuffer buffer) throws IOException {
        List<Object> values = readUnpackedList(buffer);
        Object[] result = new Object[values.size()];
        return values.toArray(result);
    }

    private List<Object> readUnpackedList(GrowableByteBuffer buffer) throws IOException {
        List<Object> result = new ArrayList<>(DEFAULT_ARRAY_SIZE);
        return readUnpackedCollection(buffer, result);
    }

    private Set<Object> readUnpackedSet(GrowableByteBuffer buffer) throws IOException {
        Set<Object> result = new HashSet<>(DEFAULT_ARRAY_SIZE);
        return readUnpackedCollection(buffer, result);
    }

    private <T extends Collection> T readUnpackedCollection(GrowableByteBuffer buffer, T collection) throws IOException {
        while (buffer.get() != UNPACKED_ARRAY_END) {
            buffer.position(buffer.position() - 1);
            collection.add(deserialize(buffer, Object.class));
        }
        return collection;
    }

    private GenericMessage readMessage(GrowableByteBuffer buffer) throws IOException {
        GenericMessage message = new GenericMessage(DEFAULT_MAP_SIZE);
        int size = getAndCheckSize(buffer);
        int startPos = buffer.position();
        int endPos = startPos + size;
        while (buffer.position() < endPos) {
            message.put(buffer.getZigZag32(), deserialize(buffer, Object.class));
        }
        if (buffer.position() != endPos) {
            throw new MotanServiceException("MotanSerialization deserialize wrong message size, except: " + size + " actual: " + (buffer.position() - startPos));
        }
        return message;
    }
}
