/*
 *
 *   Copyright 2009-2016 Weibo, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */

package com.weibo.api.motan.serialize.motan;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class TestObject {
    public String f3 = "f3";
//    char f0 = 'a';
    int f1 = 1;
    Map<String, String> f4;
    TestSubObject f5;
    List<Object> f6;
    ArrayList<String> f7;
    HashMap<String, String> f8;
    BigInteger f9;
    BigDecimal f10;
    TreeSet<Integer> f11;
    List<TestObject> f12;
    private float f2;

    /*public char getF0() {
        return f0;
    }

    public void setF0(char f0) {
        this.f0 = f0;
    }*/

    public int getF1() {
        return f1;
    }

    public void setF1(int f1) {
        this.f1 = f1;
    }

    public float getF2() {
        return f2;
    }

    public void setF2(float f2) {
        this.f2 = f2;
    }

    public String getF3() {
        return f3;
    }

    public void setF3(String f3) {
        this.f3 = f3;
    }

    public Map<String, String> getF4() {
        return f4;
    }

    public void setF4(Map<String, String> f4) {
        this.f4 = f4;
    }

    public TestSubObject getF5() {
        return f5;
    }

    public void setF5(TestSubObject f5) {
        this.f5 = f5;
    }

    public List<Object> getF6() {
        return f6;
    }

    public void setF6(List<Object> f6) {
        this.f6 = f6;
    }

    public ArrayList<String> getF7() {
        return f7;
    }

    public void setF7(ArrayList<String> f7) {
        this.f7 = f7;
    }

    public HashMap<String, String> getF8() {
        return f8;
    }

    public void setF8(HashMap<String, String> f8) {
        this.f8 = f8;
    }

    public BigInteger getF9() {
        return f9;
    }

    public void setF9(BigInteger f9) {
        this.f9 = f9;
    }

    public BigDecimal getF10() {
        return f10;
    }

    public void setF10(BigDecimal f10) {
        this.f10 = f10;
    }

    public TreeSet<Integer> getF11() {
        return f11;
    }

    public void setF11(TreeSet<Integer> f11) {
        this.f11 = f11;
    }

    public List<TestObject> getF12() {
        return f12;
    }

    public void setF12(List<TestObject> f12) {
        this.f12 = f12;
    }

    public static class TestSubObject {
        List<String> f2;
        private String f1;

        public String getF1() {
            return f1;
        }

        public void setF1(String f1) {
            this.f1 = f1;
        }

        public List<String> getF2() {
            return f2;
        }

        public void setF2(List<String> f2) {
            this.f2 = f2;
        }

        public static class TestSubObjectMsgSerializer extends AbstractMessageSerializer<TestSubObject> {
            private static Map<Integer, Field> idTypeMap = new HashMap<>();
            public TestSubObjectMsgSerializer() {
                setIdTypeMap(TestSubObject.class);
            }

            @Override
            public Map<Integer, Field> getIdTypeMap() {
                return idTypeMap;
            }

            @Override
            public TestSubObject newInstance() {
                return new TestSubObject();
            }
        }
    }

    public static class TestObjectMsgSerializer extends AbstractMessageSerializer<TestObject> {
        private static Map<Integer, Field> idTypeMap = new HashMap<>();
        public TestObjectMsgSerializer() {
            setIdTypeMap(TestObject.class);
        }

        @Override
        public Map<Integer, Field> getIdTypeMap() {
            return idTypeMap;
        }

        @Override
        public TestObject newInstance() {
            return new TestObject();
        }
    }
}
