/*
 *
 *   Copyright 2009-2016 Weibo, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */

package com.weibo.api.motan.serialize;

import com.weibo.api.motan.codec.Serialization;
import com.weibo.api.motan.codec.TypeDeserializer;
import com.weibo.api.motan.exception.MotanServiceException;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by zhanglei28 on 2017/5/9.
 */
public class DeserializableObject {
    private Serialization serialization;
    private byte[] objBytes;

    public DeserializableObject(Serialization serialization, byte[] objBytes) {
        this.serialization = serialization;
        this.objBytes = objBytes;
    }

    public <T> T deserialize(Class<T> clz) throws IOException {
        return serialization.deserialize(objBytes, clz);
    }

    public Object[] deserializeMulti(Class<?>[] paramTypes) throws IOException {
        Object[] ret = null;
        if (paramTypes != null && paramTypes.length > 0) {
            ret = serialization.deserializeMulti(objBytes, paramTypes);
        }
        return ret;
    }

    public <T> T deserializeByType(Type type) throws IOException {
        if (serialization instanceof TypeDeserializer) {
            return ((TypeDeserializer) serialization).deserializeByType(objBytes, type);
        }
        return (T) serialization.deserialize(objBytes, getClassOfType(type));
    }

    public Object[] deserializeMultiByType(Type[] paramTypes) throws IOException {
        if (paramTypes != null && paramTypes.length > 0) {
            if (serialization instanceof TypeDeserializer) {
                return ((TypeDeserializer) serialization).deserializeMultiByType(objBytes, paramTypes);
            } else {
                Class<?>[] classes = new Class[paramTypes.length];
                for (int i = 0; i < paramTypes.length; i++) {
                    classes[i] = getClassOfType(paramTypes[i]);
                }
                return serialization.deserializeMulti(objBytes, classes);
            }
        }
        return null;
    }

    public static Class getClassOfType(Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getRawType();
        } else {
            throw new MotanServiceException("Motan unsupported type " + type);
        }
    }
}
