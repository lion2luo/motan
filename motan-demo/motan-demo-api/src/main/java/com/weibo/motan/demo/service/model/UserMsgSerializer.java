package com.weibo.motan.demo.service.model;

import com.weibo.api.motan.serialize.motan.AbstractMessageSerializer;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sunnights
 */
public class UserMsgSerializer extends AbstractMessageSerializer<User> {
    private static Map<Integer, Field> idTypeMap = new HashMap<>();

    public UserMsgSerializer() {
        setIdTypeMap(User.class);
    }

    @Override
    public Map<Integer, Field> getIdTypeMap() {
        return idTypeMap;
    }

    @Override
    public User newInstance() {
        return new User();
    }
}
