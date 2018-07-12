package com.weibo.api.motan.serialize.motan;

import java.io.IOException;
import java.util.Map;

/**
 * Created on 2018/7/12
 *
 * @author: luominggang
 * Description:
 */
public class GenericMessageSerializer extends AbstractMessageSerializer<GenericMessage> {

    @Override
    public GenericMessage newInstance() {
        return new GenericMessage();
    }

    @Override
    public void readField(MotanObjectInput in, int fieldNumber, GenericMessage result) throws IOException {
        result.putField(fieldNumber, in.readObject());
    }

    @Override
    public Map<Integer, Object> getFields(GenericMessage value) {
        return value.getFields();
    }

}
