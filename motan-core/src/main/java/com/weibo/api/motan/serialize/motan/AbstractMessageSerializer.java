package com.weibo.api.motan.serialize.motan;

import com.weibo.api.motan.protocol.v2motan.GrowableByteBuffer;

import java.io.IOException;
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
}
