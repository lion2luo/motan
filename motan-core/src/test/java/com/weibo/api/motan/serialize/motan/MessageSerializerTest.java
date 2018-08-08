package com.weibo.api.motan.serialize.motan;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;

/**
 * @author sunnights
 */
public class MessageSerializerTest {
    @Test
    public void serialize() throws IOException {
        SerializerFactory.registerSerializer(TestObject.class, new TestObject.TestObjectMsgSerializer());
        SerializerFactory.registerSerializer(TestObject.TestSubObject.class, new TestObject.TestSubObject.TestSubObjectMsgSerializer());

        HashMap<String, String> tmap = new HashMap<>(2);
        tmap.put("xxx", "YYY");
        tmap.put("zzz", "www");

        TestObject tmp = new TestObject();
        tmp.setF1(100);

        TestObject.TestSubObject testSubObject = new TestObject.TestSubObject();
        testSubObject.setF1("tso");
        testSubObject.setF2(Arrays.asList("xxx", "yyy"));

        TreeSet<Integer> treeSet = new TreeSet<>();
        treeSet.add(1);
        treeSet.add(2);
        treeSet.add(3);
        treeSet.add(4);
        treeSet.add(5);

        TestObject testObject = new TestObject();
        testObject.setF1(123);
        testObject.setF2(123.3f);
//        testObject.setF3(null);     // todo
        testObject.setF4(tmap);
        testObject.setF5(testSubObject);
//        testObject.setF6(Arrays.asList("zzz", tmp));    // todo
        testObject.setF7(new ArrayList<>(Arrays.asList("000", "111")));
        testObject.setF8(tmap);
//        testObject.setF9(new BigInteger("123456789012345678901234567890"));
//        testObject.setF10(new BigDecimal("123456.789012345678901234567890123456789012345678901234567890"));
        testObject.setF11(treeSet);
//        testObject.setF12(Arrays.asList(new TestObject())); // todo
//        testObject.setF13(Arrays.asList(new User(1,"aaa")));

        MotanObjectSerialization serialization = new MotanObjectSerialization();
        byte[] bytes = serialization.serialize(testObject);
        TestObject newTestObject = serialization.deserialize(bytes, TestObject.class);
        String expect = JSON.toJSONString(testObject, SerializerFeature.DisableCircularReferenceDetect);
        String actual = JSON.toJSONString(newTestObject);
        assertEquals(expect, actual);
    }
}
