package com.sciatta.xdfs.common.concurrent.executor;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * Created by Rain on 2023/2/27<br>
 * All Rights Reserved(C) 2017 - 2023 SCIATTA <br> <p/>
 * SchedulerTests
 */
@Slf4j
public class SchedulerTests {

    @Test
    public void testSchedule() {
        DefaultScheduler scheduler = new DefaultScheduler("default-scheduler-");
        scheduler.schedule("output number", new Runnable() {
            private int i;

            @Override
            public void run() {
                log.debug("current " + ++i);
            }
        }, 1000, 1000, TimeUnit.MILLISECONDS);

        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException ignore) {
        }

        scheduler.shutdown();
    }

}
