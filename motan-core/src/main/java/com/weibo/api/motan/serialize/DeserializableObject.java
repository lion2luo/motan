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
import com.weibo.api.motan.codec.TypeSerialization;
import com.weibo.api.motan.util.ReflectUtil;

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

    public <T> T deserialize(Type type) throws IOException {
        if (serialization instanceof TypeSerialization) {
            return ((TypeSerialization) serialization).deserializeByType(objBytes, type);
        }
        return (T) serialization.deserialize(objBytes, ReflectUtil.getClassOfType(type));
    }

    public Object[] deserializeMulti(Type[] types) throws IOException {
        if (types != null && types.length > 0) {
            if (serialization instanceof TypeSerialization) {
                return ((TypeSerialization) serialization).deserializeMultiByType(objBytes, types);
            } else {
                Class<?>[] classes = new Class[types.length];
                for (int i = 0; i < types.length; i++) {
                    classes[i] = ReflectUtil.getClassOfType(types[i]);
                }
                return serialization.deserializeMulti(objBytes, classes);
            }
        }
        return null;
    }
}
