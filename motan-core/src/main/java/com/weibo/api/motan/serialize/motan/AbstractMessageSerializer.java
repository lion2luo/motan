package com.weibo.api.motan.serialize.motan;

import com.weibo.api.motan.exception.MotanServiceException;
import com.weibo.api.motan.protocol.v2motan.GrowableByteBuffer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created on 2018/7/12
 *
 * @author: luominggang
 * Description:
 */
public abstract class AbstractMessageSerializer<T> implements Serializer {
    @Override
    public void serialize(MotanObjectOutput out, Object value) throws IOException {
        GrowableByteBuffer buffer = out.getBuffer();
        buffer.put(MotanType.MESSAGE);
        int pos = buffer.position();
        buffer.position(pos + 4);
        for (Map.Entry<Integer, Object> entry : getFields((T) value).entrySet()) {
            if (entry.getValue() != null) {
                buffer.putZigzag32(entry.getKey());
                out.writeObject(entry.getValue());
            }
        }
        int nPos = buffer.position();
        buffer.position(pos);
        buffer.putInt(nPos - pos - 4);
        buffer.position(nPos);
    }

    public abstract Map<Integer, Object> getFields(T value);


    @Override
    public T deserialize(MotanObjectInput in, Type type) throws IOException {
        byte typeTag = in.readTypeTag();
        if (typeTag == MotanType.NULL) {
            return null;
        }
        if (typeTag != MotanType.MESSAGE) {
            throw new MotanServiceException(typeTag + " can not as generic message");
        }
        GrowableByteBuffer inputBuffer = in.getBuffer();
        int size = MotanObjectInput.getAndCheckSize(inputBuffer);
        int startPos = in.getBuffer().position();
        int endPos = startPos + size;
        T result = newInstance();
        while (inputBuffer.position() < endPos) {
            readField(in, inputBuffer.getZigZag32(), result);
        }
        if (inputBuffer.position() != endPos) {
            throw new MotanServiceException("MotanSerialization deserialize wrong message size, except: " + size + " actual: " + (inputBuffer.position() - startPos));
        }
        return result;
    }

    public abstract void readField(MotanObjectInput in, int fieldNumber, T result) throws IOException;

    public abstract T newInstance();
}
