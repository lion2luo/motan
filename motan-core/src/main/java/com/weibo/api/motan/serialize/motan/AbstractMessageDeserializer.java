package com.weibo.api.motan.serialize.motan;

import com.weibo.api.motan.exception.MotanServiceException;
import com.weibo.api.motan.protocol.v2motan.GrowableByteBuffer;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Created on 2018/7/12
 *
 * @author: luominggang
 * Description:
 */
public abstract class AbstractMessageDeserializer<T> implements Deserializer {
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
