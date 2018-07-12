package com.weibo.api.motan.serialize.motan;

import java.util.Map;

/**
 * Created on 2018/7/12
 *
 * @author: luominggang
 * Description:
 */
public class GenericMessageSerializer extends AbstractMessageSerializer<GenericMessage> {
    @Override
    public Map<Integer, Object> getFields(GenericMessage value) {
        return value.getFields();
    }
}
