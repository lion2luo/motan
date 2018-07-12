package com.weibo.api.motan.serialize.motan;

import com.google.common.collect.Sets;
import com.weibo.api.motan.codec.Serialization;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created on 2018/6/19
 *
 * @author: luominggang
 * Description:
 */
public class MotanSerializationTest {

    public static TestObject createDefaultTestObject() {
        TestObject testObject = new TestObject();
        testObject.f1 = true;
        testObject.f2 = (byte) 2;
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
        MotanObjectSerialization serialization = new MotanObjectSerialization();
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
        Map m2 = serialization.deserializeByType(b, new TypeReference<Map<String, String>>() {
        }.getType());
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
        MotanObjectSerialization serialization = new MotanObjectSerialization();
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

        Object[] result = serialization.deserializeMultiByType(b,
                new Type[]{String.class, new TypeReference<Map<String, String>>() {
                }.getType(), byte[].class});
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
        verifyBasic(true);
        verifyBasic(false);

        verifyBasic((byte) 16);
        verifyBasic((byte) 0);
        verifyBasic((byte) 255);

        verifyBasic((short) -16);
        verifyBasic((short) 0);
        verifyBasic((short) 16);
        verifyBasic((short) 127);
        verifyBasic((short) 128);
        verifyBasic((short) 300);
        verifyBasic(Short.MAX_VALUE);
        verifyBasic(Short.MIN_VALUE);

        verifyBasic(-16);
        verifyBasic(0);
        verifyBasic(16);
        verifyBasic(127);
        verifyBasic(128);
        verifyBasic(300);
        verifyBasic(Integer.MAX_VALUE);
        verifyBasic(Integer.MIN_VALUE);

        verifyBasic(-16L);
        verifyBasic(0L);
        verifyBasic(16L);
        verifyBasic(127L);
        verifyBasic(128L);
        verifyBasic(300L);
        verifyBasic(Long.MAX_VALUE);
        verifyBasic(Long.MIN_VALUE);

        verifyBasic(3.141592653f);
        verifyBasic(-3.141592653f);
        verifyBasic(0f);
        verifyBasic(Float.MAX_VALUE);
        verifyBasic(Float.MIN_VALUE);

        verifyBasic(3.141592653d);
        verifyBasic(-3.141592653d);
        verifyBasic(0d);
        verifyBasic(Double.MAX_VALUE);
        verifyBasic(Double.MIN_VALUE);
    }

    @Test
    public void testArray() throws Exception {
        List<String> sList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            sList.add("testString" + i);
        }

        String[] sArray = new String[sList.size()];
        sArray = sList.toArray(sArray);
        Set<String> sSet = new HashSet<>(sList);
        verifyBasic(sList);
        verifyBasic(sSet);
        verifyBasic(sArray);

        MotanObjectSerialization serialization = new MotanObjectSerialization();
        List dList = serialization.deserializeByType(serialization.serialize(sList), new TypeReference<List<String>>() {
        }.getType());
        assertEquals(sList, dList);

        Set dSet = serialization.deserializeByType(serialization.serialize(sList), new TypeReference<Set<String>>() {
        }.getType());
        assertEquals(sSet, dSet);
    }

    @Test
    public void testMap() throws Exception {
        MotanObjectSerialization serialization = new MotanObjectSerialization();
        Map<String, String> v = new HashMap<>();
        v.put("1", "1");
        v.put("2", "2");
        Map<Object, Object> dv = serialization.deserializeByType(serialization.serialize(v), new TypeReference<Map<String, String>>() {
        }.getType());
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
        dv = serialization.deserializeByType(serialization.serialize(ov), new TypeReference<Map<Object, Object>>() {
        }.getType());
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

    @Test
    public void testListMapGenerics() throws Exception {
        Map<String, List<String>> mapList = new HashMap<>();
        mapList.put("a", Arrays.asList("1", "2"));
        mapList.put("b", Arrays.asList("3", "4"));

        Map<String, Set<String>> mapSet = new HashMap<>();
        mapSet.put("a", Sets.newHashSet("1", "2"));
        mapSet.put("b", Sets.newHashSet("3", "4"));

        List<Map<String, String>> listMap = new ArrayList<>();
        listMap.add(new HashMap<String, String>() {
            {
                put("a", "a");
            }
        });
        listMap.add(new HashMap<String, String>() {
            {
                put("b", "b");
            }
        });

        MotanObjectSerialization serialization = new MotanObjectSerialization();
        assertEquals(mapList,
                serialization.deserializeByType(serialization.serialize(mapList),
                        new TypeReference<Map<String, List<String>>>() {
                        }.getType()));

        assertEquals(mapSet,
                serialization.deserializeByType(serialization.serialize(mapSet),
                        new TypeReference<Map<String, Set<String>>>() {
                        }.getType()));

        assertEquals(listMap,
                serialization.deserializeByType(serialization.serialize(listMap),
                        new TypeReference<List<Map<String, String>>>() {
                        }.getType()));
    }

    private void verifyBasic(Object v) throws Exception {
        Serialization serialization = new MotanObjectSerialization();
        byte[] bytes = serialization.serialize(v);
        Object dv = serialization.deserialize(bytes, v.getClass());
        if (v.getClass().isArray()) {
            assertArrayEquals((Object[]) v, (Object[]) dv);
        } else {
            assertEquals(v, dv);
        }
    }

    public static class TestObject {
        static {
            MotanSerialization.registerMessageTemplate(TestObject.class, new TestObjectMessageTemplate());
            SerializerFactory.registerSerializer(TestObject.class, new AbstractMessageSerializer<TestObject>() {
                @Override
                public Map<Integer, Object> getFields(TestObject value) {
                    Map<Integer, Object> fields = new HashMap<>();
                    fields.put(1, value.f1);
                    fields.put(2, value.f2);
                    fields.put(3, value.f3);
                    fields.put(4, value.f4);
                    fields.put(5, value.f5);
                    fields.put(6, value.f6);
                    fields.put(7, value.f7);
                    fields.put(8, value.f8);
                    fields.put(9, value.f9);
                    fields.put(10, value.f10);
                    fields.put(11, value.f11);
                    fields.put(12, value.f12);
                    return fields;
                }

                @Override
                public void readField(MotanObjectInput in, int fieldNumber, TestObject result) throws IOException {
                    switch (fieldNumber) {
                        case 1:
                            result.f1 = in.readBool();
                            break;
                        case 2:
                            result.f2 = in.readByte();
                            break;
                        case 3:
                            result.f3 = in.readBytes();
                            break;
                        case 4:
                            result.f4 = in.readString();
                            break;
                        case 5:
                            result.f5 = in.readShort();
                            break;
                        case 6:
                            result.f6 = in.readInt();
                            break;
                        case 7:
                            result.f7 = in.readLong();
                            break;
                        case 8:
                            result.f8 = in.readFloat();
                            break;
                        case 9:
                            result.f9 = in.readDouble();
                            break;
                        case 10:
                            result.f10 = (List<String>) in.readObject(new TypeReference<List<String>>() {
                            }.getType());
                            break;
                        case 11:
                            result.f11 = (Map<String, String>) in.readObject(new TypeReference<Map<String, String>>() {
                            }.getType());
                            break;
                        case 12:
                            result.f12 = (TestObject) in.readObject(TestObject.class);
                    }
                }

                @Override
                public TestObject newInstance() {
                    return new TestObject();
                }
            });
        }

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

        public static void main(String[] args) {
            MessageTemplateUtils.generate(TestObject.class, System.getProperty("user.dir") + "/motan-core/src/test/java");
        }
    }
}