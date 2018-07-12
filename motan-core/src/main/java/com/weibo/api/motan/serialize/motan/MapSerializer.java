package com.weibo.api.motan.serialize.motan;

import java.io.IOException;
import java.util.Map;

/**
 * Created on 2018/7/12
 *
 * @author: luominggang
 * Description:
 */
public class MapSerializer implements Serializer {
    @Override
    public void serialize(MotanObjectOutput out, Object value) throws IOException {
        out.writeUnpackedMap((Map<?, ?>) value);
    }
}
