package com.weibo.api.motan.serialize.motan;

import java.lang.Override;

public class TestObjectMessageTemplate extends AbstractMessageTemplate<MotanSerializationTest.TestObject> {
  @Override
  public GenericMessage toMessage(MotanSerializationTest.TestObject obj) {
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
  public MotanSerializationTest.TestObject fromMessage(GenericMessage message) {
    MotanSerializationTest.TestObject obj = new MotanSerializationTest.TestObject();
    Class clazz = obj.getClass();
    int i = 0;
    while (clazz != Object.class) {
      i = setObject(message, obj, clazz, i);
      clazz = clazz.getSuperclass();
    }
    return obj;
  }
}
