package com.sciatta.xdfs.common.util;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;

/**
 * Created by Rain on 2023/11/17<br>
 * All Rights Reserved(C) 2017 - 2023 SCIATTA <br> <p/>
 * ConcurrentUtilsTests
 */
public class ConcurrentUtilsTests {
    @Test
    public void testSilentSleepToSeconds() {
        ConcurrentUtils.silentSleepToSeconds(5);
    }

    @Test
    public void testSilentSleepToSecondsWithTask() throws InterruptedException {
        ConcurrentUtils.silentSleepToSeconds(5, SystemUtils::normalExit);

        CountDownLatch latch = new CountDownLatch(1);
        latch.await();
    }
}
