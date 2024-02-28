package com.sciatta.xdfs.common.concurrent.executor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Rain on 2023/3/10<br>
 * All Rights Reserved(C) 2017 - 2023 SCIATTA <br> <p/>
 * 具有线程标识的线程工厂
 */
public class NamedThreadFactory implements ThreadFactory {
    /**
     * 是否是守护线程
     */
    private final boolean daemon;

    /**
     * 线程名称前缀
     */
    private final String prefix;

    /**
     * 线程标识
     */
    private final AtomicInteger threadId = new AtomicInteger();

    public NamedThreadFactory(String prefix) {
        this(prefix, true);
    }

    public NamedThreadFactory(String prefix, boolean daemon) {
        this.prefix = prefix;
        this.daemon = daemon;
    }

    @Override
    public Thread newThread(Runnable r) {
        return new DefaultThread(prefix + threadId.getAndIncrement(), r, daemon);
    }
}
