package com.sciatta.xdfs.common.concurrent.executor;

import com.sciatta.xdfs.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Rain on 2023/3/10<br>
 * All Rights Reserved(C) 2017 - 2023 SCIATTA <br> <p/>
 * 默认调度器，简化调用方式
 */
@Slf4j
public class DefaultScheduler {
    /**
     * 调度器
     */
    private ScheduledThreadPoolExecutor executor;

    /**
     * 停止状态
     */
    private final AtomicBoolean shutdown = new AtomicBoolean(true);

    public DefaultScheduler(String threadNamePrefix) {
        this(threadNamePrefix, Runtime.getRuntime().availableProcessors() * 2, true);
    }

    public DefaultScheduler(String threadNamePrefix, int threads) {
        this(threadNamePrefix, threads, true);
    }

    public DefaultScheduler(String threadNamePrefix, int threads, boolean daemon) {
        if (shutdown.compareAndSet(true, false)) {
            executor = new ScheduledThreadPoolExecutor(threads, new NamedThreadFactory(threadNamePrefix, daemon));
            executor.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
            executor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        }
    }

    /**
     * 调度任务执行一次
     *
     * @param name 任务名
     * @param r    任务
     */
    public void scheduleOnce(String name, Runnable r) {
        scheduleOnce(name, r, 0);
    }

    /**
     * 延迟调度任务执行一次
     *
     * @param name  任务名
     * @param r     任务
     * @param delay 延迟时间；单位毫秒
     */
    public void scheduleOnce(String name, Runnable r, long delay) {
        schedule(name, r, delay, 0, TimeUnit.MILLISECONDS);
    }

    /**
     * 调度执行任务
     *
     * @param name     任务名
     * @param r        任务
     * @param delay    延迟时间
     * @param period   运行周期
     * @param timeUnit 时间单位
     */
    public void schedule(String name, Runnable r, long delay, long period, TimeUnit timeUnit) {
        if (log.isDebugEnabled()) {
            log.debug("Scheduling task {} with initial delay {} ms and period {} ms.", name, delay, period);
        }

        // 加入MDC，处理业务层未处理异常
        Runnable delegate = () -> {
            try {
                if (log.isTraceEnabled()) {
                    log.trace("Beginning execution of scheduled task {}.", name);
                }
                String loggerId = StringUtils.md5("" + System.nanoTime() + new Random().nextInt());
                MDC.put("logger_id", loggerId);
                r.run();
            } catch (Throwable e) {
                log.error("Uncaught exception in scheduled task {} :", name, e);
            } finally {
                if (log.isTraceEnabled()) {
                    log.trace("Completed execution of scheduled task {}.", name);
                }
                MDC.remove("logger_id");
            }
        };

        // 停止状态检查
        if (shutdown.get()) {
            return;
        }

        // 提交任务
        if (period > 0) {
            executor.scheduleWithFixedDelay(delegate, delay, period, timeUnit);
        } else {
            executor.schedule(delegate, delay, timeUnit);
        }
    }

    /**
     * 停止调度
     */
    public void shutdown() {
        if (shutdown.compareAndSet(false, true)) {
            log.info("Shutdown DefaultScheduler.");
            executor.shutdown();
        }
    }
}
