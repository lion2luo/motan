package com.weibo.api.motan.serialize.motan;

import com.weibo.api.motan.exception.MotanServiceException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created on 2018/7/12
 *
 * @author: luominggang
 * Description:
 */
public class MapSerializer implements Serializer {
    private final Class type;

    public MapSerializer(Class type) {
        this.type = type;
    }

    @Override
    public void serialize(MotanObjectOutput out, Object value) throws IOException {
        out.writeUnpackedMap((Map<?, ?>) value);
    }

    @Override
    public Object deserialize(MotanObjectInput in, Type genericType) throws IOException {
        Map result;
        if (type.isAssignableFrom(HashMap.class)) {
            // default use HashMap for Map
            result = new HashMap<>();
        } else if (type.isAssignableFrom(TreeMap.class)) {
            result = new TreeMap<>();
        } else {
            try {
                result = (Map) type.newInstance();
            } catch (Exception e) {
                throw new MotanServiceException("MotanSerialization unsupported type " + type);
            }
        }
        return in.readUnpackedMap(genericType, result);
    }
}
