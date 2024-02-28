package com.sciatta.xdfs.common.util;


import com.sciatta.xdfs.common.concurrent.executor.NamedThreadFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by Rain on 2023/9/5<br>
 * All Rights Reserved(C) 2017 - 2023 SCIATTA <br> <p/>
 * 并发工具类
 */
public class ConcurrentUtils {

    private ConcurrentUtils() {

    }

    /**
     * 忽略异常，sleep指定时间
     *
     * @param timeout 指定时间。单位：秒
     */
    public static void silentSleepToSeconds(long timeout) {
        try {
            TimeUnit.SECONDS.sleep(timeout);
        } catch (InterruptedException ignore) {
        }
    }

    /**
     * 忽略异常，在一个独立后台线程中sleep指定时间，然后运行任务
     *
     * @param timeout 指定时间。单位：秒
     * @param task    待运行任务
     */
    public static void silentSleepToSeconds(long timeout, Runnable task) {
        silentSleepToSeconds(timeout, task, true);
    }

    /**
     * 忽略异常，在一个独立线程中sleep指定时间，然后运行任务
     *
     * @param timeout 指定时间。单位：秒
     * @param task    待运行任务
     * @param daemon  是否是后台线程，true，是后台线程；false，不是后台线程
     */
    public static void silentSleepToSeconds(long timeout, Runnable task, boolean daemon) {
        new NamedThreadFactory("silent-sleep-task-thread-", daemon)
                .newThread(() -> {
                    silentSleepToSeconds(timeout);
                    task.run();
                }).start();
    }

}
