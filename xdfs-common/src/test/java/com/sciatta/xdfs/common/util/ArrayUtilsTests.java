package com.sciatta.xdfs.common.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Rain on 2023/8/27<br>
 * All Rights Reserved(C) 2017 - 2023 SCIATTA <br> <p/>
 * ArrayUtilsTests
 */
public class ArrayUtilsTests {

    @Test
    public void testGetLength() {
        Integer[] i = new Integer[]{1, 2};
        int length = ArrayUtils.getLength(i);

        assertEquals(2, length);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNotArray() {
        ArrayUtils.getLength(new Object());
    }

    @Test
    public void testIsEmpty() {
        boolean ans = ArrayUtils.isEmpty(new int[]{1, 2});
        assertFalse(ans);

        ans = ArrayUtils.isEmpty(new int[]{});
        assertTrue(ans);
    }

    @Test
    public void testIndexOf() {
        int i = ArrayUtils.indexOf(null, null);
        assertEquals(-1, i);

        i = ArrayUtils.indexOf(new Integer[]{1, 2}, 1);
        assertEquals(0, i);
    }

    @Test
    public void testLastIndexOf() {
        int i = ArrayUtils.lastIndexOf(null, null);
        assertEquals(-1, i);

        i = ArrayUtils.lastIndexOf(new Integer[]{1, 2, 2}, 2);
        assertEquals(2, i);
    }

    @Test
    public void testContains() {
        boolean ans = ArrayUtils.contains(null, null);
        assertFalse(ans);

        ans = ArrayUtils.contains(new Integer[]{1, 2, 2}, 3);
        assertFalse(ans);

        ans = ArrayUtils.contains(new Integer[]{1, 2, 2}, 2);
        assertTrue(ans);
    }

}
