package com.weibo.api.motan.serialize.motan;

import com.weibo.api.motan.exception.MotanServiceException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Created on 2018/7/12
 *
 * @author: luominggang
 * Description:
 */
public class CollectionSerializer implements Serializer {
    private final Class type;

    public CollectionSerializer(Class type) {
        this.type = type;
    }

    @Override
    public void serialize(MotanObjectOutput out, Object value) throws IOException {
        out.writeCollection((Collection<?>) value);
    }

    @Override
    public Object deserialize(MotanObjectInput in, Type genericType) throws IOException {
        Collection result;
        if (type.isAssignableFrom(ArrayList.class)) {
            // default use ArrayList for List
            result = new ArrayList<>();
        } else if (type.isAssignableFrom(LinkedList.class)) {
            result = new LinkedList<>();
        } else if (type.isAssignableFrom(HashSet.class)) {
            // default use HashSet for Set
            result = new HashSet<>();
        } else if (type.isAssignableFrom(TreeSet.class)) {
            result = new TreeSet<>();
        } else if (type.isAssignableFrom(LinkedHashSet.class)) {
            result = new LinkedHashSet<>();
        } else {
            try {
                result = (Collection) type.newInstance();
            } catch (Exception e) {
                throw new MotanServiceException("MotanSerialization unsupported type " + type);
            }
        }
        return in.readCollection(genericType, result);
    }
}
