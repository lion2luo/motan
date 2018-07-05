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
