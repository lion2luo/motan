package com.weibo.api.motan.serialize.motan;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * @author sunnights
 */
public class AbstractMessageTemplate<T> implements MessageTemplate<T> {
    @Override
    public GenericMessage toMessage(T t) {
        return null;
    }

    @Override
    public T fromMessage(GenericMessage message) {
        return null;
    }

    protected int setMessage(GenericMessage message, Object obj, Class clazz, int i) {
        for (Field field : clazz.getDeclaredFields()) {
            ++i;
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            try {
                if (field.getType().getClassLoader() == null) {
                    message.putField(i, field.get(obj));
                } else {
                    if (field.get(obj) != null) {
                        if (field.getType().isEnum()) {
                            // TODO
                            continue;
                        }
                        MessageTemplate template = MotanSerialization.getMessageTemplate(field.getType());
                        if (template == null) {
                            System.out.println("missing: " + field.getType().getName());
                        }
                        if (template != null && !(template.getClass() == this.getClass())) {
                            message.putField(i, template.toMessage(field.get(obj)));
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return i;
    }

    protected int setObject(GenericMessage message, Object obj, Class clazz, int i) {
        for (Field field : clazz.getDeclaredFields()) {
            ++i;
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            if (Modifier.isFinal(field.getModifiers())) {
                // TODO
                continue;
            }
            try {
                if (field.getType().getClassLoader() == null) {
                    field.set(obj, message.getField(i));
                } else {
                    if (message.getField(i) != null) {
                        MessageTemplate template = MotanSerialization.getMessageTemplate(field.getType());
                        field.set(obj, template.fromMessage((GenericMessage) message.getField(i)));
                    }
                }
            } catch (Exception e) {
                System.out.println("error " + i);
                e.printStackTrace();
            }
        }
        return i;
    }
}
