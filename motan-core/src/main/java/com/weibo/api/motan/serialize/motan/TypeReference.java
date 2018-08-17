package com.weibo.api.motan.serialize.motan;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created on 2018/7/12
 *
 * @author: luominggang
 * Description:
 */
public class TypeReference<T> {
    private final Type type;

    protected TypeReference() {
        Type superClass = getClass().getGenericSuperclass();

        type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
    }

    public Type getType() {
        return type;
    }
}
