package com.weibo.api.motan.serialize.motan;

import com.weibo.api.motan.exception.MotanServiceException;
import com.weibo.api.motan.protocol.v2motan.GrowableByteBuffer;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Created on 2018/7/11
 *
 * @author: luominggang
 * Description:
 */
public class MotanObjectInput {
    private static final int DEFAULT_MAP_SIZE = 16;
    private static final int DEFAULT_ARRAY_SIZE = 16;

    public static Type[] resolveType(Type type) {
        if (type instanceof Class) {
            // for containers (List Map Set), we need generic parameter, here just set to null, it means we don't know
            return new Type[]{type, null};
        } else if (type instanceof ParameterizedType) {
            return new Type[]{((ParameterizedType) type).getRawType(), type};
        } else {
            // TODO: for WildType (<?>), GenericArrayType, TypeVariable <T extends ObjectClass>
            throw new MotanServiceException("MotanSerialization unsupported type " + type);
        }
    }

    private GrowableByteBuffer buffer;

    public MotanObjectInput(GrowableByteBuffer buffer) {
        this.buffer = buffer;
    }

    public byte readTypeTag() {
        return buffer.get();
    }

    public byte[] readBytes() throws IOException {
        byte typeTag = readTypeTag();
        if (typeTag == MotanType.NULL) {
            return null;
        } else if (typeTag == MotanType.BYTE_ARRAY) {
            return readBytesNoTag();
        } else {
            throw new MotanServiceException(typeTag + " can not as bytes array");
        }
    }

    public String readString() throws IOException {
        byte typeTag = readTypeTag();
        if (typeTag == MotanType.NULL) {
            return null;
        } else if (typeTag == MotanType.STRING) {
            return new String(readBytesNoTag(), "UTF-8");
        } else {
            throw new MotanServiceException(typeTag + " can not as String");
        }
    }

    public Boolean readBool() throws IOException {
        byte typeTag = readTypeTag();
        if (typeTag == MotanType.NULL) {
            return null;
        } else if (typeTag == MotanType.TRUE) {
            return true;
        } else if (typeTag == MotanType.FALSE) {
            return false;
        } else {
            throw new MotanServiceException(typeTag + " can not as boolean");
        }
    }

    private Number readNumber() throws IOException {
        byte typeTag = readTypeTag();
        switch (typeTag) {
            case MotanType.NULL:
                return null;
            case MotanType.BYTE:
                return buffer.get();
            case MotanType.INT16:
                return buffer.getShort();
            case MotanType.INT32:
                return buffer.getZigZag32();
            case MotanType.INT64:
                return buffer.getZigZag64();
            case MotanType.FLOAT32:
                return buffer.getFloat();
            case MotanType.FLOAT64:
                return buffer.getDouble();
            default:
                throw new MotanServiceException(typeTag + " can not as Number");
        }
    }

    public Byte readByte() throws IOException {
        Number number = readNumber();
        if (number == null) {
            return null;
        }
        return number.byteValue();
    }

    public Short readShort() throws IOException {
        Number number = readNumber();
        if (number == null) {
            return null;
        }
        return number.shortValue();
    }

    public Integer readInt() throws IOException {
        Number number = readNumber();
        if (number == null) {
            return null;
        }
        return number.intValue();
    }

    public Long readLong() throws IOException {
        Number number = readNumber();
        if (number == null) {
            return null;
        }
        return number.longValue();
    }

    public Float readFloat() throws IOException {
        Number number = readNumber();
        if (number == null) {
            return null;
        }
        return number.floatValue();
    }

    public Double readDouble() throws IOException {
        Number number = readNumber();
        if (number == null) {
            return null;
        }
        return number.doubleValue();
    }

    public Map readUnpackedMap(Type type, Map result) throws IOException {
        byte typeTag = readTypeTag();
        if (typeTag == MotanType.NULL) {
            return null;
        }
        if (typeTag != MotanType.UNPACKED_MAP) {
            throw new MotanServiceException(typeTag + " can not as Map");
        }
        Type[] classAndType = resolveType(type);
        Type genericType = classAndType[1];
        Type keyType = Object.class; // for unknown generic parameter type
        Type valueType = Object.class;
        if (genericType instanceof ParameterizedType) {
            Type[] actualTypeArguments = ((ParameterizedType) genericType).getActualTypeArguments();
            if (actualTypeArguments.length == 2) {
                keyType = actualTypeArguments[0];
                valueType = actualTypeArguments[1];
            }
        }
        while (buffer.get() != MotanType.UNPACKED_MAP_END) {
            buffer.position(buffer.position() - 1);
            result.put(readObject(keyType), readObject(valueType));
        }
        return result;
    }

    public Object[] readUnpackedArray(Type type) throws IOException {
        byte typeTag = readTypeTag();
        if (typeTag == MotanType.NULL) {
            return null;
        }
        if (typeTag != MotanType.UNPACKED_ARRAY) {
            throw new MotanServiceException(typeTag + " can not as Array");
        }
        List result = new ArrayList<>(DEFAULT_ARRAY_SIZE);
        Object[] arrayObj = new Object[result.size()];
        while (buffer.get() != MotanType.UNPACKED_ARRAY_END) {
            buffer.position(buffer.position() - 1);
            result.add(readObject(((Class) type).getComponentType()));
        }
        return result.toArray(arrayObj);
    }

    public Collection readUnpackedCollection(Type type, Collection collection) throws IOException {
        byte typeTag = readTypeTag();
        if (typeTag == MotanType.NULL) {
            return null;
        }
        if (typeTag != MotanType.UNPACKED_ARRAY) {
            throw new MotanServiceException(typeTag + " can not as Array");
        }
        Type elementType = Object.class; // for unknown generic parameter type
        if (type instanceof ParameterizedType) {
            Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
            if (actualTypeArguments.length == 1) {
                elementType = actualTypeArguments[0];
            }
        }
        while (buffer.get() != MotanType.UNPACKED_ARRAY_END) {
            buffer.position(buffer.position() - 1);
            collection.add(readObject(elementType));
        }
        return collection;
    }

    public Object readObject() throws IOException {
        byte typeTag = readTypeTag();
        if (typeTag == MotanType.TRUE) {
            return Boolean.TRUE;
        } else if (typeTag == MotanType.FALSE) {
            return Boolean.FALSE;
        }

        buffer.position(buffer.position() - 1);
        switch (typeTag) {
            case MotanType.BYTE:
                return readByte();
            case MotanType.NULL:
                return null;
            case MotanType.STRING:
                return readString();
            case MotanType.BYTE_ARRAY:
                return readBytes();
            case MotanType.INT16:
                return readShort();
            case MotanType.INT32:
                return readInt();
            case MotanType.INT64:
                return readLong();
            case MotanType.FLOAT32:
                return readFloat();
            case MotanType.FLOAT64:
                return readDouble();
            case MotanType.UNPACKED_MAP:
                return readUnpackedMap(HashMap.class, new HashMap(DEFAULT_MAP_SIZE));
            case MotanType.UNPACKED_ARRAY:
                return readUnpackedCollection(ArrayList.class, new ArrayList(DEFAULT_ARRAY_SIZE));
            case MotanType.MESSAGE:
                return readObject(GenericMessage.class);
            default:
                throw new MotanServiceException("MotanSerialization unsupported type: " + typeTag);
        }
    }

    public Object readObject(Type type) throws IOException {
        if (type == null || type == Object.class) {
            return readObject();
        }
        Type[] classAndType = resolveType(type);
        Class<?> clz = (Class<?>) classAndType[0];
        if (clz.isArray()) {
            if (clz == byte[].class) {
                return readBytes();
            }
            return readUnpackedArray(clz);
        }
        Serializer serializer = SerializerFactory.getSerializer(clz);
        if (serializer == null) {
            throw new MotanServiceException("MotanSerialization unsupported type: " + type);
        }
        return serializer.deserialize(this, type);
    }

    private byte[] readBytesNoTag() throws IOException {
        int size = getAndCheckZigZagSize(buffer);
        byte[] b = new byte[size];
        buffer.get(b);
        return b;
    }

    // The following package visible methods are for message deserialize. See AbstractMessageDeserializer
    GrowableByteBuffer getBuffer() {
        return buffer;
    }

    static int getAndCheckZigZagSize(GrowableByteBuffer buffer) throws IOException {
        int size = buffer.getZigZag32();
        if (size > buffer.remaining()) {
            throw new MotanServiceException("MotanSerialization deserialize fail! buffer not enough!need size:" + size);
        }
        return size;
    }

    static int getAndCheckSize(GrowableByteBuffer buffer) throws IOException {
        int size = buffer.getInt();
        if (size > buffer.remaining()) {
            throw new MotanServiceException("MotanSerialization deserialize fail! buffer not enough!need size:" + size);
        }
        return size;
    }
}
