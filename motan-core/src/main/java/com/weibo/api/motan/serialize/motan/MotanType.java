package com.weibo.api.motan.serialize.motan;

/**
 * Created on 2018/7/11
 *
 * @author: luominggang
 * Description:
 */
public final class MotanType {
    public static final byte FALSE = 0;
    public static final byte TRUE = 1;
    public static final byte NULL = 2;
    public static final byte BYTE = 3;
    public static final byte STRING = 4;
    public static final byte BYTE_ARRAY = 5;
    public static final byte INT16 = 6;
    public static final byte INT32 = 7;
    public static final byte INT64 = 8;
    public static final byte FLOAT32 = 9;
    public static final byte FLOAT64 = 10;
    public static final byte UNPACKED_ARRAY = 20;
    public static final byte UNPACKED_ARRAY_END = 21;
    public static final byte UNPACKED_MAP = 22;
    public static final byte UNPACKED_MAP_END = 23;
    public static final byte PACKED_ARRAY = 24;
    public static final byte PACKED_MAP = 25;
    public static final byte MESSAGE = 26;
}
