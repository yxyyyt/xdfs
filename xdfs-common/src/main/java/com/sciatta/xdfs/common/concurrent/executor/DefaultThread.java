package com.sciatta.xdfs.common.concurrent.executor;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by Rain on 2023/3/10<br>
 * All Rights Reserved(C) 2017 - 2023 SCIATTA <br> <p/>
 * 默认线程；当有未捕获异常时，打印异常日志
 */
@Slf4j
public class DefaultThread extends Thread {
    public DefaultThread(final String name, boolean daemon) {
        super(name);
        configureThread(name, daemon);
    }

    public DefaultThread(final String name, Runnable runnable, boolean daemon) {
        super(runnable, name);
        configureThread(name, daemon);
    }

    /**
     * 配置线程
     * @param name 线程名
     * @param daemon 是否是守护线程；true，守护线程；否则，不是守护线程
     */
    private void configureThread(String name, boolean daemon) {
        setDaemon(daemon);
        setUncaughtExceptionHandler((t, e) -> log.error("Uncaught exception in thread '{}':", name, e));
    }
}
