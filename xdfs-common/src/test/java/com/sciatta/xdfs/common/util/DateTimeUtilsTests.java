package com.sciatta.xdfs.common.util;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.Date;

/**
 * Created by Rain on 2023/8/20<br>
 * All Rights Reserved(C) 2017 - 2023 SCIATTA <br> <p/>
 * DateTimeUtilsTests
 */
public class DateTimeUtilsTests {

    @Test
    public void testNow() {
        String now = DateTimeUtils.now();
        System.out.println(now);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignore) {
        }

        now = DateTimeUtils.now();
        System.out.println(now);
    }

    @Test
    public void testCurrentTimeMillis() {
        long l = DateTimeUtils.currentTimeMillis();
        System.out.println(l);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignore) {
        }

        l = DateTimeUtils.currentTimeMillis();
        System.out.println(l);
    }

    @Test
    public void testDate() {
        String now = DateTimeUtils.now();
        Date date = DateTimeUtils.parseDateTime(now);
        String ans = DateTimeUtils.formatDate(date);

        System.out.println(ans);
    }

    @Test
    public void testDateTime() {
        String now = DateTimeUtils.now();
        Date date = DateTimeUtils.parseDateTime(now);
        String ans = DateTimeUtils.formatDateTime(date);

        System.out.println(ans);
    }

    @Test
    public void testAddDays() {
        Date date = DateTimeUtils.addDays(new Date(), 1);
        String s = DateTimeUtils.formatDate(date);

        System.out.println(s);
    }

    @Test
    public void testGetDateBefore() {
        Date date = DateTimeUtils.addDays(new Date(), -1);
        String s = DateTimeUtils.formatDate(date);

        System.out.println(s);
    }

    @Test
    public void testMsToS() {
        long s = DateTimeUtils.msToS(1000);
        assertEquals(1, s);
    }

}
