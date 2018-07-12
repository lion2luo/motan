package com.weibo.api.motan.serialize.motan;

import com.weibo.api.motan.exception.MotanServiceException;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Created on 2018/7/12
 *
 * @author: luominggang
 * Description:
 */
public class BasicSerializer implements Serializer {
    private final Class type;

    public BasicSerializer(Class type) {
        this.type = type;
    }

    @Override
    public void serialize(MotanObjectOutput out, Object value) throws IOException {
        Class clz = value.getClass();
        if (clz == String.class) {
            out.writeString((String) value);
        } else if (clz == byte[].class) {
            out.writeBytes((byte[]) value);
        } else if (clz == Byte.class || clz == byte.class) {
            out.writeByte((Byte) value);
        } else if (clz == Boolean.class || clz == boolean.class) {
            out.writeBool((Boolean) value);
        } else if (clz == Short.class || clz == short.class) {
            out.writeShort((Short) value);
        } else if (clz == Integer.class || clz == int.class) {
            out.writeInt((Integer) value);
        } else if (clz == Long.class || clz == long.class) {
            out.writeLong((Long) value);
        } else if (clz == Float.class || clz == float.class) {
            out.writeFloat((Float) value);
        } else if (clz == Double.class || clz == double.class) {
            out.writeDouble((Double) value);
        } else {
            throw new MotanServiceException("Basic type not support " + clz);
        }
    }

    @Override
    public Object deserialize(MotanObjectInput in, Type type) throws IOException {
        if (this.type == String.class) {
            return in.readString();
        } else if (this.type == byte[].class) {
            return in.readBytes();
        } else if (this.type == Boolean.class || this.type == boolean.class) {
            return in.readBool();
        } else if (this.type == Byte.class || this.type == byte.class) {
            return in.readByte();
        } else if (this.type == Short.class || this.type == short.class) {
            return in.readShort();
        } else if (this.type == Integer.class || this.type == int.class) {
            return in.readInt();
        } else if (this.type == Long.class || this.type == long.class) {
            return in.readLong();
        } else if (this.type == Float.class || this.type == float.class) {
            return in.readFloat();
        } else if (this.type == Double.class || this.type == double.class) {
            return in.readDouble();
        } else {
            throw new MotanServiceException("Basic type not support " + this.type);
        }
    }
}
