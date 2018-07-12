package com.weibo.api.motan.serialize.motan;

import java.io.IOException;

/**
 * Created on 2018/7/11
 *
 * @author: luominggang
 * Description:
 */
public interface Serializer {
    void serialize(MotanObjectOutput out, Object value) throws IOException;
}
