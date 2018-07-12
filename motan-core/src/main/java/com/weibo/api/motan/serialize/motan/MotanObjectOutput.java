package com.weibo.api.motan.serialize.motan;

import com.weibo.api.motan.exception.MotanServiceException;
import com.weibo.api.motan.protocol.v2motan.GrowableByteBuffer;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * Created on 2018/7/11
 *
 * @author: luominggang
 * Description:
 */
public class MotanObjectOutput {
    private GrowableByteBuffer buffer;

    public MotanObjectOutput(GrowableByteBuffer buffer) {
        this.buffer = buffer;
    }

    public void writeNull() {
        buffer.put(MotanType.NULL);
    }

    public void writeString(String str) throws IOException {
        if (str == null) {
            writeNull();
            return;
        }
        buffer.put(MotanType.STRING);
        byte[] b = str.getBytes("UTF-8");
        buffer.putZigzag32(b.length);
        buffer.put(b);
    }

    public void writeBytes(byte[] value) {
        if (value == null) {
            writeNull();
            return;
        }
        buffer.put(MotanType.BYTE_ARRAY);
        buffer.putZigzag32(value.length);
        buffer.put(value);
    }

    public void writeBool(boolean value) {
        if (value) {
            buffer.put(MotanType.TRUE);
        } else {
            buffer.put(MotanType.FALSE);
        }
    }

    public void writeByte(byte value) {
        buffer.put(MotanType.BYTE);
        buffer.put(value);
    }

    public void writeShort(short value) {
        buffer.put(MotanType.INT16);
        buffer.putShort(value);
    }

    public void writeInt(int value) {
        buffer.put(MotanType.INT32);
        buffer.putZigzag32(value);
    }

    public void writeLong(long value) {
        buffer.put(MotanType.INT64);
        buffer.putZigzag64(value);
    }

    public void writeFloat(float value) {
        buffer.put(MotanType.FLOAT32);
        buffer.putFloat(value);
    }

    public void writeDouble(double value) {
        buffer.put(MotanType.FLOAT64);
        buffer.putDouble(value);
    }

    public void writeUnpackedArray(Object[] value) throws IOException {
        if (value == null) {
            writeNull();
            return;
        }
        buffer.put(MotanType.UNPACKED_ARRAY);
        for (int i = 0; i < value.length; i++) {
            writeObject(value[i]);
        }
        buffer.put(MotanType.UNPACKED_ARRAY_END);
    }

    public void writeUnpackedArray(Collection<?> value) throws IOException {
        if (value == null) {
            writeNull();
            return;
        }
        buffer.put(MotanType.UNPACKED_ARRAY);
        for (Object v : value) {
            writeObject(v);
        }
        buffer.put(MotanType.UNPACKED_ARRAY_END);
    }

    public void writeUnpackedMap(Map<?, ?> value) throws IOException {
        if (value == null) {
            writeNull();
            return;
        }
        buffer.put(MotanType.UNPACKED_MAP);
        for (Map.Entry<?, ?> entry : value.entrySet()) {
            writeObject(entry.getKey());
            writeObject(entry.getValue());
        }
        buffer.put(MotanType.UNPACKED_MAP_END);
    }

    public void writeObject(Object value) throws IOException {
        if (value == null) {
            writeNull();
            return;
        }
        Class clz = value.getClass();
        if (clz == byte[].class) {
            writeBytes((byte[]) value);
            return;
        } else if (clz.isArray()) {
            writeUnpackedArray((Object[]) value);
            return;
        }
        Serializer serializer = SerializerFactory.getSerializer(clz);
        if (serializer == null) {
            throw new MotanServiceException("MotanSerialization unsupported type: " + clz);
        }
        serializer.serialize(this, value);
    }

    GrowableByteBuffer getBuffer() {
        return buffer;
    }
}
