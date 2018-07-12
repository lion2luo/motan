package com.weibo.api.motan.serialize.motan;

import java.io.IOException;

/**
 * Created on 2018/7/12
 *
 * @author: luominggang
 * Description:
 */
public class GenericMessageDeserializer extends AbstractMessageDeserializer<GenericMessage> {
    @Override
    public void readField(MotanObjectInput in, int fieldNumber, GenericMessage result) throws IOException {
        result.putField(fieldNumber, in.readObject());
    }

    @Override
    public GenericMessage newInstance() {
        return new GenericMessage();
    }
}
