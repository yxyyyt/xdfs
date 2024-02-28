package com.sciatta.xdfs.common.util;

import java.lang.reflect.Array;

/**
 * Created by Rain on 2023/8/27<br>
 * All Rights Reserved(C) 2017 - 2023 SCIATTA <br> <p/>
 * 数组工具类
 */
public class ArrayUtils {

    public static final Class<?>[] EMPTY_CLASS_ARRAY = new Class[0];

    public static final String[] EMPTY_STRING_ARRAY = new String[0];

    public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    public static final long[] EMPTY_LONG_ARRAY = new long[0];
    public static final Long[] EMPTY_LONG_OBJECT_ARRAY = new Long[0];

    public static final int[] EMPTY_INT_ARRAY = new int[0];
    public static final Integer[] EMPTY_INTEGER_OBJECT_ARRAY = new Integer[0];

    public static final short[] EMPTY_SHORT_ARRAY = new short[0];
    public static final Short[] EMPTY_SHORT_OBJECT_ARRAY = new Short[0];

    public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    public static final Byte[] EMPTY_BYTE_OBJECT_ARRAY = new Byte[0];

    public static final double[] EMPTY_DOUBLE_ARRAY = new double[0];
    public static final Double[] EMPTY_DOUBLE_OBJECT_ARRAY = new Double[0];

    public static final float[] EMPTY_FLOAT_ARRAY = new float[0];
    public static final Float[] EMPTY_FLOAT_OBJECT_ARRAY = new Float[0];

    public static final boolean[] EMPTY_BOOLEAN_ARRAY = new boolean[0];
    public static final Boolean[] EMPTY_BOOLEAN_OBJECT_ARRAY = new Boolean[0];

    public static final char[] EMPTY_CHAR_ARRAY = new char[0];
    public static final Character[] EMPTY_CHARACTER_OBJECT_ARRAY = new Character[0];

    private ArrayUtils() {

    }

    /**
     * 获取指定数组的长度；如果不是数组，则抛出IllegalArgumentException异常
     *
     * @param array 数组
     * @return 数组的长度
     */
    public static int getLength(Object array) {
        return array == null ? 0 : Array.getLength(array);
    }

    /**
     * 指定数组是否为空
     *
     * @param array 数组
     * @return true，为空；false，不为空
     */
    public static boolean isEmpty(Object[] array) {
        return getLength(array) == 0;
    }

    /**
     * 指定数组是否为空
     *
     * @param array 数组
     * @return true，为空；false，不为空
     */
    public static boolean isEmpty(long[] array) {
        return getLength(array) == 0;
    }

    /**
     * 指定数组是否为空
     *
     * @param array 数组
     * @return true，为空；false，不为空
     */
    public static boolean isEmpty(int[] array) {
        return getLength(array) == 0;
    }

    /**
     * 指定数组是否为空
     *
     * @param array 数组
     * @return true，为空；false，不为空
     */
    public static boolean isEmpty(short[] array) {
        return getLength(array) == 0;
    }

    /**
     * 指定数组是否为空
     *
     * @param array 数组
     * @return true，为空；false，不为空
     */
    public static boolean isEmpty(char[] array) {
        return getLength(array) == 0;
    }

    /**
     * 指定数组是否为空
     *
     * @param array 数组
     * @return true，为空；false，不为空
     */
    public static boolean isEmpty(byte[] array) {
        return getLength(array) == 0;
    }

    /**
     * 指定数组是否为空
     *
     * @param array 数组
     * @return true，为空；false，不为空
     */
    public static boolean isEmpty(double[] array) {
        return getLength(array) == 0;
    }

    /**
     * 指定数组是否为空
     *
     * @param array 数组
     * @return true，为空；false，不为空
     */
    public static boolean isEmpty(float[] array) {
        return getLength(array) == 0;
    }

    /**
     * 指定数组是否为空
     *
     * @param array 数组
     * @return true，为空；false，不为空
     */
    public static boolean isEmpty(boolean[] array) {
        return getLength(array) == 0;
    }

    /**
     * 在指定数组中，从开始位置查找元素
     *
     * @param array        数组
     * @param objectToFind 查找元素
     * @return 查找元素所在数组的位置
     */
    public static int indexOf(Object[] array, Object objectToFind) {
        return indexOf(array, objectToFind, 0);
    }

    /**
     * 在指定数组中，从指定位置查找元素
     *
     * @param array        数组
     * @param objectToFind 查找元素
     * @param startIndex   指定位置
     * @return 查找元素所在数组的位置
     */
    public static int indexOf(Object[] array, Object objectToFind, int startIndex) {
        if (array != null) {
            if (startIndex < 0) {
                startIndex = 0;
            }

            int i;
            if (objectToFind == null) {
                for (i = startIndex; i < array.length; ++i) {
                    if (array[i] == null) {
                        return i;
                    }
                }
            } else {
                for (i = startIndex; i < array.length; ++i) {
                    if (objectToFind.equals(array[i])) {
                        return i;
                    }
                }
            }

        }
        return -1;
    }

    /**
     * 在指定数组中，从开始位置查找元素
     *
     * @param array       数组
     * @param valueToFind 查找元素
     * @return 查找元素所在数组的位置
     */
    public static int indexOf(long[] array, long valueToFind) {
        return indexOf(array, valueToFind, 0);
    }

    /**
     * 在指定数组中，从指定位置查找元素
     *
     * @param array       数组
     * @param valueToFind 查找元素
     * @param startIndex  指定位置
     * @return 查找元素所在数组的位置
     */
    public static int indexOf(long[] array, long valueToFind, int startIndex) {
        if (array == null) {
            return -1;
        } else {
            if (startIndex < 0) {
                startIndex = 0;
            }

            for (int i = startIndex; i < array.length; ++i) {
                if (valueToFind == array[i]) {
                    return i;
                }
            }

            return -1;
        }
    }

    /**
     * 在指定数组中，从开始位置查找元素
     *
     * @param array       数组
     * @param valueToFind 查找元素
     * @return 查找元素所在数组的位置
     */
    public static int indexOf(int[] array, int valueToFind) {
        return indexOf((int[]) array, (int) valueToFind, 0);
    }

    /**
     * 在指定数组中，从指定位置查找元素
     *
     * @param array       数组
     * @param valueToFind 查找元素
     * @param startIndex  指定位置
     * @return 查找元素所在数组的位置
     */
    public static int indexOf(int[] array, int valueToFind, int startIndex) {
        if (array == null) {
            return -1;
        } else {
            if (startIndex < 0) {
                startIndex = 0;
            }

            for (int i = startIndex; i < array.length; ++i) {
                if (valueToFind == array[i]) {
                    return i;
                }
            }

            return -1;
        }
    }

    /**
     * 在指定数组中，从开始位置查找元素
     *
     * @param array       数组
     * @param valueToFind 查找元素
     * @return 查找元素所在数组的位置
     */
    public static int indexOf(short[] array, short valueToFind) {
        return indexOf((short[]) array, (short) valueToFind, 0);
    }

    /**
     * 在指定数组中，从指定位置查找元素
     *
     * @param array       数组
     * @param valueToFind 查找元素
     * @param startIndex  指定位置
     * @return 查找元素所在数组的位置
     */
    public static int indexOf(short[] array, short valueToFind, int startIndex) {
        if (array == null) {
            return -1;
        } else {
            if (startIndex < 0) {
                startIndex = 0;
            }

            for (int i = startIndex; i < array.length; ++i) {
                if (valueToFind == array[i]) {
                    return i;
                }
            }

            return -1;
        }
    }

    /**
     * 在指定数组中，从开始位置查找元素
     *
     * @param array       数组
     * @param valueToFind 查找元素
     * @return 查找元素所在数组的位置
     */
    public static int indexOf(char[] array, char valueToFind) {
        return indexOf((char[]) array, (char) valueToFind, 0);
    }

    /**
     * 在指定数组中，从指定位置查找元素
     *
     * @param array       数组
     * @param valueToFind 查找元素
     * @param startIndex  指定位置
     * @return 查找元素所在数组的位置
     */
    public static int indexOf(char[] array, char valueToFind, int startIndex) {
        if (array == null) {
            return -1;
        } else {
            if (startIndex < 0) {
                startIndex = 0;
            }

            for (int i = startIndex; i < array.length; ++i) {
                if (valueToFind == array[i]) {
                    return i;
                }
            }

            return -1;
        }
    }

    /**
     * 在指定数组中，从开始位置查找元素
     *
     * @param array       数组
     * @param valueToFind 查找元素
     * @return 查找元素所在数组的位置
     */
    public static int indexOf(byte[] array, byte valueToFind) {
        return indexOf((byte[]) array, (byte) valueToFind, 0);
    }

    /**
     * 在指定数组中，从指定位置查找元素
     *
     * @param array       数组
     * @param valueToFind 查找元素
     * @param startIndex  指定位置
     * @return 查找元素所在数组的位置
     */
    public static int indexOf(byte[] array, byte valueToFind, int startIndex) {
        if (array == null) {
            return -1;
        } else {
            if (startIndex < 0) {
                startIndex = 0;
            }

            for (int i = startIndex; i < array.length; ++i) {
                if (valueToFind == array[i]) {
                    return i;
                }
            }

            return -1;
        }
    }

    /**
     * 在指定数组中，从开始位置查找元素
     *
     * @param array       数组
     * @param valueToFind 查找元素
     * @return 查找元素所在数组的位置
     */
    public static int indexOf(double[] array, double valueToFind) {
        return indexOf(array, valueToFind, 0);
    }

    /**
     * 在指定数组中，从指定位置查找元素
     *
     * @param array       数组
     * @param valueToFind 查找元素
     * @param tolerance   容忍度，查找元素正负范围（加容忍度，或减容忍度）
     * @return 查找元素所在数组的位置
     */
    public static int indexOf(double[] array, double valueToFind, double tolerance) {
        return indexOf(array, valueToFind, 0, tolerance);
    }

    /**
     * 在指定数组中，从指定位置查找元素
     *
     * @param array       数组
     * @param valueToFind 查找元素
     * @param startIndex  指定位置
     * @return 查找元素所在数组的位置
     */
    public static int indexOf(double[] array, double valueToFind, int startIndex) {
        if (isEmpty(array)) {
            return -1;
        } else {
            if (startIndex < 0) {
                startIndex = 0;
            }

            for (int i = startIndex; i < array.length; ++i) {
                if (valueToFind == array[i]) {
                    return i;
                }
            }

            return -1;
        }
    }

    /**
     * 在指定数组中，从指定位置查找元素
     *
     * @param array       数组
     * @param valueToFind 查找元素
     * @param startIndex  指定位置
     * @param tolerance   容忍度，查找元素正负范围（加容忍度，或减容忍度）
     * @return 查找元素所在数组的位置
     */
    public static int indexOf(double[] array, double valueToFind, int startIndex, double tolerance) {
        if (isEmpty(array)) {
            return -1;
        } else {
            if (startIndex < 0) {
                startIndex = 0;
            }

            double min = valueToFind - tolerance;
            double max = valueToFind + tolerance;

            for (int i = startIndex; i < array.length; ++i) {
                if (array[i] >= min && array[i] <= max) {
                    return i;
                }
            }

            return -1;
        }
    }

    /**
     * 在指定数组中，从开始位置查找元素
     *
     * @param array       数组
     * @param valueToFind 查找元素
     * @return 查找元素所在数组的位置
     */
    public static int indexOf(float[] array, float valueToFind) {
        return indexOf(array, valueToFind, 0);
    }

    /**
     * 在指定数组中，从指定位置查找元素
     *
     * @param array       数组
     * @param valueToFind 查找元素
     * @param startIndex  指定位置
     * @return 查找元素所在数组的位置
     */
    public static int indexOf(float[] array, float valueToFind, int startIndex) {
        if (isEmpty(array)) {
            return -1;
        } else {
            if (startIndex < 0) {
                startIndex = 0;
            }

            for (int i = startIndex; i < array.length; ++i) {
                if (valueToFind == array[i]) {
                    return i;
                }
            }

            return -1;
        }
    }

    /**
     * 在指定数组中，从开始位置查找元素
     *
     * @param array       数组
     * @param valueToFind 查找元素
     * @return 查找元素所在数组的位置
     */
    public static int indexOf(boolean[] array, boolean valueToFind) {
        return indexOf(array, valueToFind, 0);
    }

    /**
     * 在指定数组中，从指定位置查找元素
     *
     * @param array       数组
     * @param valueToFind 查找元素
     * @param startIndex  指定位置
     * @return 查找元素所在数组的位置
     */
    public static int indexOf(boolean[] array, boolean valueToFind, int startIndex) {
        if (isEmpty(array)) {
            return -1;
        } else {
            if (startIndex < 0) {
                startIndex = 0;
            }

            for (int i = startIndex; i < array.length; ++i) {
                if (valueToFind == array[i]) {
                    return i;
                }
            }

            return -1;
        }
    }

    /**
     * 在指定数组中，从最后位置向前查找元素
     *
     * @param array        数组
     * @param objectToFind 查找元素
     * @return 查找元素所在数组的位置
     */
    public static int lastIndexOf(Object[] array, Object objectToFind) {
        return lastIndexOf(array, objectToFind, Integer.MAX_VALUE);
    }

    /**
     * 在指定数组中，从指定位置向前查找元素
     *
     * @param array        数组
     * @param objectToFind 查找元素
     * @param startIndex   指定位置
     * @return 查找元素所在数组的位置
     */
    public static int lastIndexOf(Object[] array, Object objectToFind, int startIndex) {
        if (array == null) {
            return -1;
        } else if (startIndex < 0) {
            return -1;
        } else {
            if (startIndex >= array.length) {
                startIndex = array.length - 1;
            }

            int i;
            if (objectToFind == null) {
                for (i = startIndex; i >= 0; --i) {
                    if (array[i] == null) {
                        return i;
                    }
                }
            } else if (array.getClass().getComponentType().isInstance(objectToFind)) {
                for (i = startIndex; i >= 0; --i) {
                    if (objectToFind.equals(array[i])) {
                        return i;
                    }
                }
            }

            return -1;
        }
    }

    /**
     * 在指定数组中，从最后位置向前查找元素
     *
     * @param array       数组
     * @param valueToFind 查找元素
     * @return 查找元素所在数组的位置
     */
    public static int lastIndexOf(long[] array, long valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    /**
     * 在指定数组中，从指定位置向前查找元素
     *
     * @param array       数组
     * @param valueToFind 查找元素
     * @param startIndex  指定位置
     * @return 查找元素所在数组的位置
     */
    public static int lastIndexOf(long[] array, long valueToFind, int startIndex) {
        if (array == null) {
            return -1;
        } else if (startIndex < 0) {
            return -1;
        } else {
            if (startIndex >= array.length) {
                startIndex = array.length - 1;
            }

            for (int i = startIndex; i >= 0; --i) {
                if (valueToFind == array[i]) {
                    return i;
                }
            }

            return -1;
        }
    }

    /**
     * 在指定数组中，从最后位置向前查找元素
     *
     * @param array       数组
     * @param valueToFind 查找元素
     * @return 查找元素所在数组的位置
     */
    public static int lastIndexOf(int[] array, int valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    /**
     * 在指定数组中，从指定位置向前查找元素
     *
     * @param array       数组
     * @param valueToFind 查找元素
     * @param startIndex  指定位置
     * @return 查找元素所在数组的位置
     */
    public static int lastIndexOf(int[] array, int valueToFind, int startIndex) {
        if (array == null) {
            return -1;
        } else if (startIndex < 0) {
            return -1;
        } else {
            if (startIndex >= array.length) {
                startIndex = array.length - 1;
            }

            for (int i = startIndex; i >= 0; --i) {
                if (valueToFind == array[i]) {
                    return i;
                }
            }

            return -1;
        }
    }

    /**
     * 在指定数组中，从最后位置向前查找元素
     *
     * @param array       数组
     * @param valueToFind 查找元素
     * @return 查找元素所在数组的位置
     */
    public static int lastIndexOf(short[] array, short valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    /**
     * 在指定数组中，从指定位置向前查找元素
     *
     * @param array       数组
     * @param valueToFind 查找元素
     * @param startIndex  指定位置
     * @return 查找元素所在数组的位置
     */
    public static int lastIndexOf(short[] array, short valueToFind, int startIndex) {
        if (array == null) {
            return -1;
        } else if (startIndex < 0) {
            return -1;
        } else {
            if (startIndex >= array.length) {
                startIndex = array.length - 1;
            }

            for (int i = startIndex; i >= 0; --i) {
                if (valueToFind == array[i]) {
                    return i;
                }
            }

            return -1;
        }
    }

    /**
     * 在指定数组中，从最后位置向前查找元素
     *
     * @param array       数组
     * @param valueToFind 查找元素
     * @return 查找元素所在数组的位置
     */
    public static int lastIndexOf(char[] array, char valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    /**
     * 在指定数组中，从指定位置向前查找元素
     *
     * @param array       数组
     * @param valueToFind 查找元素
     * @param startIndex  指定位置
     * @return 查找元素所在数组的位置
     */
    public static int lastIndexOf(char[] array, char valueToFind, int startIndex) {
        if (array == null) {
            return -1;
        } else if (startIndex < 0) {
            return -1;
        } else {
            if (startIndex >= array.length) {
                startIndex = array.length - 1;
            }

            for (int i = startIndex; i >= 0; --i) {
                if (valueToFind == array[i]) {
                    return i;
                }
            }

            return -1;
        }
    }

    /**
     * 在指定数组中，从最后位置向前查找元素
     *
     * @param array       数组
     * @param valueToFind 查找元素
     * @return 查找元素所在数组的位置
     */
    public static int lastIndexOf(byte[] array, byte valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    /**
     * 在指定数组中，从指定位置向前查找元素
     *
     * @param array       数组
     * @param valueToFind 查找元素
     * @param startIndex  指定位置
     * @return 查找元素所在数组的位置
     */
    public static int lastIndexOf(byte[] array, byte valueToFind, int startIndex) {
        if (array == null) {
            return -1;
        } else if (startIndex < 0) {
            return -1;
        } else {
            if (startIndex >= array.length) {
                startIndex = array.length - 1;
            }

            for (int i = startIndex; i >= 0; --i) {
                if (valueToFind == array[i]) {
                    return i;
                }
            }

            return -1;
        }
    }

    /**
     * 在指定数组中，从最后位置向前查找元素
     *
     * @param array       数组
     * @param valueToFind 查找元素
     * @return 查找元素所在数组的位置
     */
    public static int lastIndexOf(double[] array, double valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    /**
     * 在指定数组中，从指定位置向前查找元素
     *
     * @param array       数组
     * @param valueToFind 查找元素
     * @param tolerance   容忍度，查找元素正负范围（加容忍度，或减容忍度）
     * @return 查找元素所在数组的位置
     */
    public static int lastIndexOf(double[] array, double valueToFind, double tolerance) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE, tolerance);
    }

    /**
     * 在指定数组中，从指定位置向前查找元素
     *
     * @param array       数组
     * @param valueToFind 查找元素
     * @param startIndex  指定位置
     * @return 查找元素所在数组的位置
     */
    public static int lastIndexOf(double[] array, double valueToFind, int startIndex) {
        if (isEmpty(array)) {
            return -1;
        } else if (startIndex < 0) {
            return -1;
        } else {
            if (startIndex >= array.length) {
                startIndex = array.length - 1;
            }

            for (int i = startIndex; i >= 0; --i) {
                if (valueToFind == array[i]) {
                    return i;
                }
            }

            return -1;
        }
    }

    /**
     * 在指定数组中，从指定位置向前查找元素
     *
     * @param array       数组
     * @param valueToFind 查找元素
     * @param startIndex  指定位置
     * @param tolerance   容忍度，查找元素正负范围（加容忍度，或减容忍度）
     * @return 查找元素所在数组的位置
     */
    public static int lastIndexOf(double[] array, double valueToFind, int startIndex, double tolerance) {
        if (isEmpty(array)) {
            return -1;
        } else if (startIndex < 0) {
            return -1;
        } else {
            if (startIndex >= array.length) {
                startIndex = array.length - 1;
            }

            double min = valueToFind - tolerance;
            double max = valueToFind + tolerance;

            for (int i = startIndex; i >= 0; --i) {
                if (array[i] >= min && array[i] <= max) {
                    return i;
                }
            }

            return -1;
        }
    }

    /**
     * 在指定数组中，从最后位置向前查找元素
     *
     * @param array       数组
     * @param valueToFind 查找元素
     * @return 查找元素所在数组的位置
     */
    public static int lastIndexOf(float[] array, float valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    /**
     * 在指定数组中，从指定位置向前查找元素
     *
     * @param array       数组
     * @param valueToFind 查找元素
     * @param startIndex  指定位置
     * @return 查找元素所在数组的位置
     */
    public static int lastIndexOf(float[] array, float valueToFind, int startIndex) {
        if (isEmpty(array)) {
            return -1;
        } else if (startIndex < 0) {
            return -1;
        } else {
            if (startIndex >= array.length) {
                startIndex = array.length - 1;
            }

            for (int i = startIndex; i >= 0; --i) {
                if (valueToFind == array[i]) {
                    return i;
                }
            }

            return -1;
        }
    }

    /**
     * 在指定数组中，从最后位置向前查找元素
     *
     * @param array       数组
     * @param valueToFind 查找元素
     * @return 查找元素所在数组的位置
     */
    public static int lastIndexOf(boolean[] array, boolean valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    /**
     * 在指定数组中，从指定位置向前查找元素
     *
     * @param array       数组
     * @param valueToFind 查找元素
     * @param startIndex  指定位置
     * @return 查找元素所在数组的位置
     */
    public static int lastIndexOf(boolean[] array, boolean valueToFind, int startIndex) {
        if (isEmpty(array)) {
            return -1;
        } else if (startIndex < 0) {
            return -1;
        } else {
            if (startIndex >= array.length) {
                startIndex = array.length - 1;
            }

            for (int i = startIndex; i >= 0; --i) {
                if (valueToFind == array[i]) {
                    return i;
                }
            }

            return -1;
        }
    }

    /**
     * 在指定数组中查找指定元素是否存在
     *
     * @param array        数组
     * @param objectToFind 查找元素
     * @return true，存在；false 不存在
     */
    public static boolean contains(Object[] array, Object objectToFind) {
        return indexOf(array, objectToFind) != -1;
    }

    /**
     * 在指定数组中查找指定元素是否存在
     *
     * @param array       数组
     * @param valueToFind 查找元素
     * @return true，存在；false 不存在
     */
    public static boolean contains(long[] array, long valueToFind) {
        return indexOf(array, valueToFind) != -1;
    }

    /**
     * 在指定数组中查找指定元素是否存在
     *
     * @param array       数组
     * @param valueToFind 查找元素
     * @return true，存在；false 不存在
     */
    public static boolean contains(int[] array, int valueToFind) {
        return indexOf(array, valueToFind) != -1;
    }

    /**
     * 在指定数组中查找指定元素是否存在
     *
     * @param array       数组
     * @param valueToFind 查找元素
     * @return true，存在；false 不存在
     */
    public static boolean contains(short[] array, short valueToFind) {
        return indexOf(array, valueToFind) != -1;
    }

    /**
     * 在指定数组中查找指定元素是否存在
     *
     * @param array       数组
     * @param valueToFind 查找元素
     * @return true，存在；false 不存在
     */
    public static boolean contains(char[] array, char valueToFind) {
        return indexOf(array, valueToFind) != -1;
    }

    /**
     * 在指定数组中查找指定元素是否存在
     *
     * @param array       数组
     * @param valueToFind 查找元素
     * @return true，存在；false 不存在
     */
    public static boolean contains(byte[] array, byte valueToFind) {
        return indexOf(array, valueToFind) != -1;
    }

    /**
     * 在指定数组中查找指定元素是否存在
     *
     * @param array       数组
     * @param valueToFind 查找元素
     * @return true，存在；false 不存在
     */
    public static boolean contains(double[] array, double valueToFind) {
        return indexOf(array, valueToFind) != -1;
    }

    /**
     * 在指定数组中查找指定元素是否存在
     *
     * @param array       数组
     * @param valueToFind 查找元素
     * @param tolerance   容忍度，查找元素正负范围（加容忍度，或减容忍度）
     * @return true，存在；false 不存在
     */
    public static boolean contains(double[] array, double valueToFind, double tolerance) {
        return indexOf(array, valueToFind, 0, tolerance) != -1;
    }

    /**
     * 在指定数组中查找指定元素是否存在
     *
     * @param array       数组
     * @param valueToFind 查找元素
     * @return true，存在；false 不存在
     */
    public static boolean contains(float[] array, float valueToFind) {
        return indexOf(array, valueToFind) != -1;
    }

    /**
     * 在指定数组中查找指定元素是否存在
     *
     * @param array       数组
     * @param valueToFind 查找元素
     * @return true，存在；false 不存在
     */
    public static boolean contains(boolean[] array, boolean valueToFind) {
        return indexOf(array, valueToFind) != -1;
    }

}
