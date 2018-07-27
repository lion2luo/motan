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

import com.weibo.api.motan.config.springsupport.annotation.MotanService;
import com.weibo.api.motan.serialize.motan.*;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhanglei28 on 2017/8/30.
 */
public class User implements Serializable {
    static {
        MotanSerialization.registerMessageTemplate(User.class, new UserMessageTemplate());
        SerializerFactory.registerSerializer(User.class, new AbstractMessageSerializer<User>() {
            @Override
            public Map<Integer, Object> getFields(User value) {
                Map<Integer, Object> fields = new HashMap<>();
                fields.put(1, value.id);
                fields.put(2, value.name);
                return fields;
            }

            @Override
            public User newInstance() {
                return new User();
            }

            @Override
            public void readField(MotanObjectInput in, int fieldNumber, User result) throws IOException {
                switch (fieldNumber) {
                    case 1:
                        result.setId(in.readInt());
                        break;
                    case 2:
                        result.setName(in.readString());
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private int id;
    private String name;

    public User() {
    }

    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static void main(String[] args) {
        MessageTemplateUtils.generate(User.class, System.getProperty("user.dir") + "/motan-demo/motan-demo-api/src/main/java/");
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
}
