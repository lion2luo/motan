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
import com.weibo.api.motan.serialize.motan.MotanSerialization;

import java.io.IOException;
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

    public <T> T deserialize(Class<T> clz, Type type) throws IOException {
        if (serialization instanceof MotanSerialization) {
            return ((MotanSerialization) serialization).deserialize(objBytes, clz, type);
        }
        return serialization.deserialize(objBytes, clz);
    }

    public Object[] deserializeMulti(Class<?>[] classes, Type[] paramTypes) throws IOException {
        if (classes != null && classes.length > 0) {
            if (serialization instanceof MotanSerialization) {
                return ((MotanSerialization) serialization).deserializeMulti(objBytes, classes, paramTypes);
            } else {
               return serialization.deserializeMulti(objBytes, classes);
            }
        }
        return null;
    }
}
