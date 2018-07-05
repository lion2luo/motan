/*
 *  Copyright 2009-2016 Weibo, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.weibo.api.motan.serialize.motan;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 2018/6/12
 *
 * @author: luominggang
 * Description:
 */
public class GenericMessage extends HashMap<Integer, Object> {

    public GenericMessage() {
    }

    public GenericMessage(int capacity) {
        super(capacity);
    }

    public boolean getBool(int field, boolean defaultValue) {
        Object value = get(field);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Boolean) {
            return ((Boolean) value).booleanValue();
        }
        throw new IllegalArgumentException("Unexpected type [" + value.getClass() + "] for field " + field);
    }

    public byte getByte(int field, byte defaultValue) {
        Object value = get(field);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Byte) {
            return ((Byte) value).byteValue();
        }
        throw new IllegalArgumentException("Unexpected type [" + value.getClass() + "] for field " + field);
    }

    public String getString(int field) {
        Object value = get(field);
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return (String) value;
        }
        throw new IllegalArgumentException("Unexpected type [" + value.getClass() + "] for field " + field);
    }

    public byte[] getBytes(int field, byte[] defaultValue) {
        Object value = get(field);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof byte[]) {
            return (byte[]) value;
        }
        throw new IllegalArgumentException("Unexpected type [" + value.getClass() + "] for field " + field);
    }

    public short getShort(int field, short defaultValue) {
        Object value = get(field);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Number) {
            return ((Number) value).shortValue();
        }
        throw new IllegalArgumentException("Unexpected type [" + value.getClass() + "] for field " + field);
    }

    public int getInt(int field, int defaultValue) {
        Object value = get(field);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        throw new IllegalArgumentException("Unexpected type [" + value.getClass() + "] for field " + field);
    }

    public long getLong(int field, long defaultValue) {
        Object value = get(field);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        throw new IllegalArgumentException("Unexpected type [" + value.getClass() + "] for field " + field);
    }

    public float getFloat(int field, float defaultValue) {
        Object value = get(field);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }
        throw new IllegalArgumentException("Unexpected type [" + value.getClass() + "] for field " + field);
    }

    public double getDouble(int field, double defaultValue) {
        Object value = get(field);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        throw new IllegalArgumentException("Unexpected type [" + value.getClass() + "] for field " + field);
    }

    public <T> List<T> getList(int field) {
        Object value = get(field);
        if (value == null) {
            return null;
        }
        if (value instanceof List) {
            return (List<T>) value;
        }
        throw new IllegalArgumentException("Unexpected type [" + value.getClass() + "] for field " + field);
    }

    public <T> T[] getArray(int field) {
        Object value = get(field);
        if (value == null) {
            return null;
        }
        if (value instanceof List) {
            Object[] result = new Object[((List) value).size()];
            return (T[]) ((List) value).toArray(result);
        }
        if (value.getClass().isArray()) {
            return (T[]) value;
        }
        throw new IllegalArgumentException("Unexpected type [" + value.getClass() + "] for field " + field);
    }

    public <K, V> Map<K, V> getMap(int field) {
        Object value = get(field);
        if (value == null) {
            return null;
        }
        if (value instanceof Map) {
            return (Map<K, V>) value;
        }
        throw new IllegalArgumentException("Unexpected type [" + value.getClass() + "] for field " + field);
    }

    public GenericMessage getMessage(int field) {
        Object value = get(field);
        if (value == null) {
            return null;
        }
        if (value instanceof GenericMessage) {
            return (GenericMessage) value;
        }
        throw new IllegalArgumentException("Unexpected type [" + value.getClass() + "] for field " + field);
    }
}
