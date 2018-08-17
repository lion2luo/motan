package com.weibo.api.motan.serialize.motan;

import com.weibo.api.motan.codec.Serialization;
import com.weibo.api.motan.codec.TypeSerialization;
import com.weibo.api.motan.core.extension.SpiMeta;
import com.weibo.api.motan.protocol.v2motan.GrowableByteBuffer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;

/**
 * Created on 2018/7/12
 *
 * @author: luominggang
 * Description:
 */
@SpiMeta(name = "motan")
public class MotanObjectSerialization implements TypeSerialization {
    @Override
    public byte[] serialize(Object obj) throws IOException {
        GrowableByteBuffer buffer = new GrowableByteBuffer(4096);
        MotanObjectOutput out = new MotanObjectOutput(buffer);
        out.writeObject(obj);
        buffer.flip();
        byte[] result = new byte[buffer.remaining()];
        buffer.get(result);
        return result;
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clz) throws IOException {
        return deserializeByType(bytes, clz);
    }

    @Override
    public byte[] serializeMulti(Object[] data) throws IOException {
        GrowableByteBuffer buffer = new GrowableByteBuffer(4096);
        MotanObjectOutput out = new MotanObjectOutput(buffer);
        for (Object o : data) {
            out.writeObject(o);
        }
        buffer.flip();
        byte[] result = new byte[buffer.remaining()];
        buffer.get(result);
        return result;
    }

    @Override
    public Object[] deserializeMulti(byte[] data, Class<?>[] classes) throws IOException {
        return deserializeMultiByType(data, classes);
    }

    @Override
    public int getSerializationNumber() {
        return 8;
    }

    @Override
    public <T> T deserializeByType(byte[] bytes, Type type) throws IOException {
        return (T) new MotanObjectInput(new GrowableByteBuffer(ByteBuffer.wrap(bytes))).readObject(type);
    }

    @Override
    public Object[] deserializeMultiByType(byte[] data, Type[] types) throws IOException {
        GrowableByteBuffer buffer = new GrowableByteBuffer(ByteBuffer.wrap(data));
        MotanObjectInput in = new MotanObjectInput(buffer);
        Object[] result = new Object[types.length];
        for (int i = 0; i < types.length; i++) {
            result[i] = in.readObject(types[i]);
        }
        return result;
    }
}
