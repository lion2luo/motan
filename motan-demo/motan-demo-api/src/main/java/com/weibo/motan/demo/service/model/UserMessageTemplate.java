package com.weibo.motan.demo.service.model;

import com.weibo.api.motan.serialize.motan.AbstractMessageTemplate;
import com.weibo.api.motan.serialize.motan.GenericMessage;
import java.lang.Override;

public class UserMessageTemplate extends AbstractMessageTemplate<User> {
  @Override
  public GenericMessage toMessage(User obj) {
    GenericMessage message = new GenericMessage();
    Class clazz = obj.getClass();
    int i = 0;
    while (clazz != Object.class) {
      i = setMessage(message, obj, clazz, i);
      clazz = clazz.getSuperclass();
    }
    return message;
  }

  @Override
  public User fromMessage(GenericMessage message) {
    User obj = new User();
    Class clazz = obj.getClass();
    int i = 0;
    while (clazz != Object.class) {
      i = setObject(message, obj, clazz, i);
      clazz = clazz.getSuperclass();
    }
    return obj;
  }
}
