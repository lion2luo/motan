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

package com.weibo.motan.demo.service.model;

import com.weibo.api.motan.serialize.motan.GenericMessage;
import com.weibo.api.motan.serialize.motan.MessageTemplate;

import java.io.Serializable;

/**
 * Created by zhanglei28 on 2017/8/30.
 */
public class User implements Serializable {
    private int id;
    private String name;

    public User() {
    }

    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    static {
        MessageTemplate.registerMessageTemplate(User.class, new MessageTemplate<User>() {
            @Override
            public User fromMessage(GenericMessage message) {
                User result = new User();
                result.setId(message.getInt(1, 0));
                result.setName(message.getString(2));
                return result;
            }

            @Override
            public GenericMessage toMessage(User value) {
                GenericMessage message = new GenericMessage();
                message.put(1, value.id);
                message.put(2, value.name);
                return message;
            }
        });
    }
}
