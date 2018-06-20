package com.weibo.api.motan.serialize.motan;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created on 2018/6/19
 *
 * @author: luominggang
 * Description:
 */
public abstract class MessageTemplate<T> {

    private static final Map<Class<?>, MessageTemplate<?>> MESSAGE_TEMPLATES = new ConcurrentHashMap<>();

    public static void registerMessageTemplate(Class clz, MessageTemplate template) {
        MESSAGE_TEMPLATES.put(clz, template);
    }

    public static MessageTemplate getMessageTemplate(Class clz) {
        return MESSAGE_TEMPLATES.get(clz);
    }
    // Convert generic message to java pojo
    public abstract T fromMessage(GenericMessage message);

    // Convert java pojo to generic message
    public abstract GenericMessage toMessage(T value);

}
