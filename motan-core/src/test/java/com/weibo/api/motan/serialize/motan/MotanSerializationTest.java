package com.weibo.api.motan.serialize.motan;

import com.google.common.collect.Sets;
import com.weibo.api.motan.codec.Serialization;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Created on 2018/6/19
 *
 * @author: luominggang
 * Description:
 */
public class MotanSerializationTest {
    public static class TestObject {
        public boolean f1;
        public byte f2;
        public byte[] f3;
        public String f4;
        public short f5;
        public int f6;
        public long f7;
        public float f8;
        public double f9;
        public List<String> f10;
        public Map<String, String> f11;
        public TestObject f12;
    }

    @BeforeClass
    public static void setUp() {
        MessageTemplate.registerMessageTemplate(TestObject.class, new MessageTemplate<TestObject>() {
            @Override
            public TestObject fromMessage(GenericMessage message) {
                TestObject result = new TestObject();
                result.f1 = message.getBool(1, false);
                result.f2 = message.getByte(2, (byte) 0);
                result.f3 = message.getBytes(3, null);
                result.f4 = message.getString(4, null);
                result.f5 = message.getShort(5, (short) 0);
                result.f6 = message.getInt(6, 0);
                result.f7 = message.getLong(7, 0L);
                result.f8 = message.getFloat(8, 0F);
                result.f9 = message.getDouble(9, 0D);
                result.f10 = message.getList(10);
                result.f11 = message.getMap(11);
                GenericMessage genericMessage = message.getMessage(12);
                if (genericMessage != null) {
                    MessageTemplate<TestObject> messageTemplate = MessageTemplate.getMessageTemplate(TestObject.class);
                    if (messageTemplate != null) {
                        result.f12 = messageTemplate.fromMessage(genericMessage);
                    }
                }
                return result;
            }

            @Override
            public GenericMessage toMessage(TestObject value) {
                GenericMessage message = new GenericMessage();
                message.put(1, value.f1);
                message.put(2, value.f2);
                message.put(3, value.f3);
                message.put(4, value.f4);
                message.put(5, value.f5);
                message.put(6, value.f6);
                message.put(7, value.f7);
                message.put(8, value.f8);
                message.put(9, value.f9);
                message.put(10, value.f10);
                message.put(11, value.f11);
                if (value.f12 != null) {
                    MessageTemplate<TestObject> messageTemplate = MessageTemplate.getMessageTemplate(TestObject.class);
                    if (messageTemplate != null) {
                        message.put(12, messageTemplate.toMessage(value.f12));
                    }
                }
                return message;
            }
        });
    }

    public static TestObject createDefaultTestObject() {
        TestObject testObject = new TestObject();
        testObject.f1 = true;
        testObject.f2 = (byte)2;
        testObject.f3 = new byte[]{1, 2, 3};
        testObject.f4 = "test";
        testObject.f5 = (short) 2;
        testObject.f6 = 2;
        testObject.f7 = 2L;
        testObject.f8 = 2F;
        testObject.f9 = 2D;
        testObject.f10 = Arrays.asList("1", "2", "3");
        Map<String, String> f11 = new HashMap<>();
        f11.put("test", "test");
        testObject.f11 = f11;
        return testObject;
    }

    @Test
    public void serialize() throws Exception {
        String s = "hello";
        Serialization serialization = new MotanSerialization();
        byte[] b = serialization.serialize(s);
        assertNotNull(b);
        assertTrue(b.length > 0);

        String result = serialization.deserialize(b, String.class);
        assertEquals(s, result);

        Map<String, String> map = new HashMap<>();
        map.put("name", "ray");
        map.put("code", "xxx");
        b = serialization.serialize(map);
        assertNotNull(b);
        assertTrue(b.length > 0);
        Map m2 = serialization.deserialize(b, Map.class);
        assertEquals(map.size(), m2.size());
        for (Map.Entry entry : map.entrySet()) {
            assertEquals(entry.getValue(), m2.get(entry.getKey()));
        }

        byte[] bytes = new byte[]{2, 34, 12, 24};
        b = serialization.serialize(bytes);
        assertNotNull(b);
        assertTrue(b.length > 0);
        assertTrue(b[0] == MotanSerialization.BYTE_ARRAY);
        byte[] nbytes = serialization.deserialize(b, byte[].class);
        assertEquals(bytes.length, nbytes.length);

        for (int i = 0; i < nbytes.length; i++) {
            assertEquals(nbytes[i], bytes[i]);
        }

        TestObject testObject = createDefaultTestObject();
        testObject.f12 = createDefaultTestObject();
        TestObject dv = serialization.deserialize(serialization.serialize(testObject), TestObject.class);
        System.out.println(dv);

    }

    @Test
    public void testSerializeMulti() throws Exception {
        Serialization serialization = new MotanSerialization();
        Object[] objects = new Object[3];
        objects[0] = "teststring";
        Map<String, String> map = new HashMap<>();
        map.put("name", "ray");
        map.put("code", "xxx");
        objects[1] = map;
        byte[] bytes = new byte[]{2, 34, 12, 24};
        objects[2] = bytes;

        byte[] b = serialization.serializeMulti(objects);
        assertNotNull(b);
        assertTrue(b.length > 0);

        Object[] result = serialization.deserializeMulti(b, new Class[]{String.class, Map.class, byte[].class});
        assertEquals(3, result.length);
        assertTrue(result[0] instanceof String);
        assertEquals(result[0], objects[0]);
        assertTrue(result[1] instanceof Map);
        Map<String, String> map2 = (Map<String, String>) result[1];
        for (Map.Entry entry : map.entrySet()) {
            assertEquals(entry.getValue(), map2.get(entry.getKey()));
        }
        assertTrue(result[2] instanceof byte[]);

        byte[] nbytes = (byte[]) result[2];
        for (int i = 0; i < nbytes.length; i++) {
            assertEquals(nbytes[i], bytes[i]);
        }
    }

    @Test
    public void testBaseType() throws Exception {
        verify(true);
        verify(false);

        verify((byte) 16);
        verify((byte) 0);
        verify((byte) 255);

        verify((short) -16);
        verify((short) 0);
        verify((short) 16);
        verify((short) 127);
        verify((short) 128);
        verify((short) 300);
        verify(Short.MAX_VALUE);
        verify(Short.MIN_VALUE);

        verify(-16);
        verify(0);
        verify(16);
        verify(127);
        verify(128);
        verify(300);
        verify(Integer.MAX_VALUE);
        verify(Integer.MIN_VALUE);

        verify(-16L);
        verify(0L);
        verify(16L);
        verify(127L);
        verify(128L);
        verify(300L);
        verify(Long.MAX_VALUE);
        verify(Long.MIN_VALUE);

        verify(3.141592653f);
        verify(-3.141592653f);
        verify(0f);
        verify(Float.MAX_VALUE);
        verify(Float.MIN_VALUE);

        verify(3.141592653d);
        verify(-3.141592653d);
        verify(0d);
        verify(Double.MAX_VALUE);
        verify(Double.MIN_VALUE);
    }

    @Test
    public void testArray() throws Exception {
        List<String> sList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            sList.add("testString" + i);
        }

        String[] sArray = new String[sList.size()];
        sArray = sList.toArray(sArray);
        verify(sList);
        verify(new HashSet<>(sList));
        verify(sArray);
    }

    @Test
    public void testMap() throws Exception {
        Serialization serialization = new MotanSerialization();
        Map<String, String> v = new HashMap<>();
        v.put("1", "1");
        v.put("2", "2");
        Map<Object, Object> dv = serialization.deserialize(serialization.serialize(v), Map.class);
        assertEquals(v.size(), dv.size());
        Map<Object, Object> ov = new HashMap<>();
        ov.put("a", 1);
        ov.put("b", true);
        ov.put("c", 1L);
        ov.put("d", 1f);
        ov.put("e", 1d);
        ov.put("f", (short) 1);
        ov.put("g", (byte) 1);
        ov.put("h", "1");
        ov.put("i", new String[]{"1", "2", "3", "4"});
        ov.put("j", Arrays.asList("1", "2", "3", "4"));
        ov.put("k", Sets.newHashSet("1", "2", "3", "4"));
        dv = serialization.deserialize(serialization.serialize(ov), Map.class);
        assertEquals(ov.size(), dv.size());
        for (Map.Entry<Object, Object> entry : ov.entrySet()) {
            if (entry.getValue().getClass().isArray()) {
                List<Object> values = Arrays.asList((Object[]) entry.getValue());
                assertEquals(values, dv.get(entry.getKey()));
            } else if (entry.getValue() instanceof Collection) {
                ArrayList excepted = new ArrayList((Collection) entry.getValue());
                ArrayList actual = new ArrayList((Collection) dv.get(entry.getKey()));
                Collections.sort(excepted);
                Collections.sort(actual);
                assertEquals(excepted, actual);
            } else {
                assertEquals(entry.getValue(), dv.get(entry.getKey()));
            }
        }
    }

    private void verify(Object v) throws Exception {
        Serialization serialization = new MotanSerialization();
        byte[] bytes = serialization.serialize(v);
        Object dv = serialization.deserialize(bytes, v.getClass());
        if (v.getClass().isArray()) {
            assertArrayEquals((Object[]) v, (Object[]) dv);
        } else {
            assertEquals(v, dv);
        }
    }

}