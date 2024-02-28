package com.sciatta.xdfs.common.util;

import org.junit.Test;

/**
 * Created by Rain on 2023/8/19<br>
 * All Rights Reserved(C) 2017 - 2023 SCIATTA <br> <p/>
 * AssertUtilsTests
 */
public class AssertUtilsTests {

    @Test(expected = IllegalArgumentException.class)
    public void testEmpty() {
        AssertUtils.notEmpty("", "empty");
    }

    @Test
    public void testNotEmpty() {
        AssertUtils.notEmpty("test", "not empty");
    }

    @Test
    public void testNotBlank() {
        AssertUtils.notBlank("test", "not blank");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBlank() {
        AssertUtils.notBlank(" ", "blank");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNotNull() {
        AssertUtils.notNull(null, "not null");
    }

    @Test
    public void testIsTrue() {
        AssertUtils.isTrue(true, "is true");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsFalse() {
        AssertUtils.isTrue(false, "is false");
    }

}
