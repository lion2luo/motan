package com.weibo.api.motan.serialize.motan;

import com.weibo.api.motan.exception.MotanServiceException;
import com.weibo.api.motan.protocol.v2motan.GrowableByteBuffer;
import com.weibo.api.motan.util.LoggerUtil;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on 2018/7/12
 *
 * @author: luominggang
 * Description:
 */
public abstract class AbstractMessageSerializer<T> implements Serializer {
    public Map<Integer, Field> getIdTypeMap() {
        return Collections.emptyMap();
    }

    protected void setIdTypeMap(Class clazz) {
        int i = 0;
        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isTransient(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            getIdTypeMap().put(++i, field);
        }
    }

    @Override
    public void serialize(MotanObjectOutput out, Object value) throws IOException {
        GrowableByteBuffer buffer = out.getBuffer();
        buffer.put(MotanType.MESSAGE);
        int pos = buffer.position();
        buffer.position(pos + 4);
        for (Map.Entry<Integer, Object> entry : getFields((T) value).entrySet()) {
            if (entry.getValue() != null) {
                buffer.putZigzag32(entry.getKey());
                out.writeObject(entry.getValue());
            }
        }
        int nPos = buffer.position();
        buffer.position(pos);
        buffer.putInt(nPos - pos - 4);
        buffer.position(nPos);
    }

    public Map<Integer, Object> getFields(T obj) {
        Map<Integer, Object> fields = new HashMap<>();
        for (Map.Entry<Integer, Field> entry : getIdTypeMap().entrySet()) {
            try {
                fields.put(entry.getKey(), entry.getValue().get(obj));
            } catch (IllegalAccessException e) {
                LoggerUtil.warn("fail to serialize field, e=" + e.getMessage());
            }
        }
        return fields;
    }

    /** Another way to serialize, the instance can be like follow
     *  Model class
     *  User {
     *      private String name;
     *      private int age;
     *  }
     *
     *  Serializer class
     *  UserSerializer extends AbstractMessageSerializer<User> {
     *     public static int[] FIELD_NUMBERS = {1, 2};
     *
     *     public int[] getFieldNumbers() {
     *        return  FIELD_NUMBERS;
     *     }
     *
     *     public Object getField(int fieldNumber, User value) {
     *          switch (fieldNumber) {
     *              case 1: return value.name;
     *              case 2: return value.age;
     *              default: return null;
     *          }
     *     }
     *  }
     *
     */

    /**
    public void serialize(MotanObjectOutput out, Object value) throws IOException {
        GrowableByteBuffer buffer = out.getBuffer();
        buffer.put(MotanType.MESSAGE);
        int pos = buffer.position();
        buffer.position(pos + 4);
        for (int fieldNumber : getFieldNumbers()) {
            Object fieldValue = getField(fieldNumber, (T)value);
            if (fieldValue != null) {
                buffer.putZigzag32(fieldNumber);
                out.writeObject(fieldValue);
            }
        }
        int nPos = buffer.position();
        buffer.position(pos);
        buffer.putInt(nPos - pos - 4);
        buffer.position(nPos);
    }

    public abstract int[] getFieldNumbers();

    public abstract Object getField(int fieldNumber, T value);

     **/

    @Override
    public T deserialize(MotanObjectInput in, Type type) throws IOException {
        byte typeTag = in.readTypeTag();
        if (typeTag == MotanType.NULL) {
            return null;
        }
        if (typeTag != MotanType.MESSAGE) {
            throw new MotanServiceException(typeTag + " can not as generic message");
        }
        GrowableByteBuffer inBuffer = in.getBuffer();
        int size = MotanObjectInput.getAndCheckSize(inBuffer);
        int startPos = inBuffer.position();
        int endPos = startPos + size;
        T result = newInstance();
        while (inBuffer.position() < endPos) {
            readField(in, inBuffer.getZigZag32(), result);
        }
        if (inBuffer.position() != endPos) {
            throw new MotanServiceException("MotanSerialization deserialize wrong message size, except: " + size + " actual: " + (inBuffer.position() - startPos));
        }
        return result;
    }

    public abstract T newInstance();

    public void readField(MotanObjectInput in, int fieldNumber, T result) throws IOException {
        try {
            Field field = getIdTypeMap().get(fieldNumber);
            if (field == null) {
                // for unknown field we need read the value for compatible, such as Class add a new field
                in.readObject();
                return;
            }
            field.set(result, in.readObject(field.getGenericType()));
        } catch (IllegalAccessException e) {
            LoggerUtil.error("fail to read field, class:" + result.getClass().getSimpleName() + ", e=" + e.getMessage());
        }
    }

}
