package com.weibo.api.motan.serialize.motan;

import com.weibo.api.motan.exception.MotanServiceException;

import java.io.IOException;

/**
 * Created on 2018/7/12
 *
 * @author: luominggang
 * Description:
 */
public class BasicSerializer implements Serializer {
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
}
