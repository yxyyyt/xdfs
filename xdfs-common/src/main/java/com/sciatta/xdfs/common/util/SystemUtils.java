package com.sciatta.xdfs.common.util;

import io.netty.channel.epoll.Epoll;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;

import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;

/**
 * Created by Rain on 2023/8/21<br>
 * All Rights Reserved(C) 2017 - 2023 SCIATTA <br> <p/>
 * 系统工具类
 */
@Slf4j
public class SystemUtils {

    private static final SystemInfo si = new SystemInfo();
    private static final HardwareAbstractionLayer hardware = si.getHardware();

    private static final DecimalFormat df;
    private static final String TWO_DECIMAL_PATTERN = "0.00";

    public static final String OS_NAME = System.getProperty("os.name");
    public static final String DEFAULT_CHARSET = "UTF-8";
    public static final String CLASS_EXTENSION = ".class";
    public static final String JAVA_EXTENSION = ".java";

    static {
        df = new DecimalFormat(TWO_DECIMAL_PATTERN);
        df.setRoundingMode(RoundingMode.HALF_UP);
    }

    private SystemUtils() {

    }

    /**
     * 是否是Windows平台
     *
     * @return true，是Windows平台；false，不是Windows平台
     */
    public static boolean isWindowsPlatform() {
        return OS_NAME != null && OS_NAME.toLowerCase().contains(PlatformEnum.WINDOWS.getValue());
    }

    /**
     * 是否是Linux平台
     *
     * @return true，是Linux平台；false，不是Linux平台
     */
    public static boolean isLinuxPlatform() {
        return OS_NAME != null && OS_NAME.toLowerCase().contains(PlatformEnum.LINUX.getValue());
    }

    /**
     * 是否使用EPoll
     *
     * @param enable 启用状态；true，启用；false，不启用
     * @return true，使用EPoll；false，不使用EPoll
     */
    public static boolean isUseEPoll(boolean enable) {
        return enable && SystemUtils.isLinuxPlatform() && Epoll.isAvailable();
    }

    /**
     * 获取pid
     *
     * @return pid
     */
    public static String getPid() {
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        String name = runtime.getName();

        int index = name.indexOf("@");
        if (index != -1) {
            return name.substring(0, index);
        }

        return null;
    }

    /**
     * 获取未使用物理内存大小
     *
     * @return 未使用物理内存大小
     */
    public static double unusedPhysicalMemorySize() {
        double availablePhysicalMemorySize = hardware.getMemory().getAvailable() / 1024.0 / 1024 / 1024;

        return Double.parseDouble(df.format(availablePhysicalMemorySize));
    }

    /**
     * 获取已使用物理内存大小比例
     *
     * @return 已使用物理内存大小比例
     */
    public static double usedPhysicalMemorySizeRatio() {
        double availableRatio = unusedPhysicalMemorySize() / (hardware.getMemory().getTotal() / 1024.0 / 1024 / 1024);

        return Double.parseDouble(df.format(1 - availableRatio));
    }

    /**
     * 获取CPU负载
     *
     * @return CPU负载
     */
    public static double cpuLoad() {
        double cpuLoad = hardware.getProcessor().getSystemCpuLoad();

        return Double.parseDouble(df.format(cpuLoad));
    }

    /**
     * 检查资源状态
     *
     * @param reservedCPURatio    预留CPU比例
     * @param reservedMemoryRatio 预留内存比例
     * @return true，系统处于正常负载状态；false，系统处于异常过载状态
     */
    public static boolean checkResourceState(double reservedCPURatio, double reservedMemoryRatio) {
        double usedCPURation = cpuLoad();
        double unusedCPURation = 1 - usedCPURation;

        double usedMemoryRatio = usedPhysicalMemorySizeRatio();
        double unusedMemoryRatio = 1 - usedMemoryRatio;

        if (unusedCPURation < reservedCPURatio || unusedMemoryRatio < reservedMemoryRatio) {
            log.warn("The system is overloaded, CPU usage is {}% (Reserved to {}%), memory usage is {}% (Reserved to {}%)",
                    (usedCPURation * 100), (reservedCPURatio * 100),
                    (usedMemoryRatio * 100), (reservedMemoryRatio * 100));
            return false;
        } else {
            return true;
        }
    }

    /**
     * 正常退出jvm
     */
    public static void normalExit() {
        System.exit(0);
    }

    /**
     * 通过文件名在类路径加载资源
     *
     * @param fileName 文件名
     * @return 资源
     * @throws IOException IO异常
     */
    public static Resource getResourceFromClassPath(String fileName) throws IOException {
        URI uri = null;
        InputStream inputStream;

        try {
            URL url = ClassUtils.getClassLoader().getResource(fileName);
            AssertUtils.notNull(url, "file name " + fileName + " cannot get resource to url");
            uri = url.toURI();
            inputStream = ClassUtils.getClassLoader().getResourceAsStream(fileName);
        } catch (Exception e) {
            throw new IOException(String.format("Not found resource %s from class path %s", fileName, uri == null ? fileName : uri));
        }

        if (inputStream == null) {
            throw new IOException(String.format("Not found resource %s from class path %s", fileName, uri));
        }

        return new Resource(inputStream, uri);
    }

    /**
     * 通过文件名在外部文件路径加载资源
     *
     * @param fileName 文件名
     * @return 资源
     * @throws IOException IO异常
     */
    public static Resource getResourceFromFilePath(String fileName) throws IOException {
        URI uri = null;
        InputStream inputStream;

        try {
            Path path = Paths.get(fileName);
            uri = path.toUri();
            inputStream = Files.newInputStream(path);
        } catch (Exception e) {
            throw new IOException(String.format("Not found resource %s from file path %s", fileName, uri == null ? fileName : uri));
        }

        return new Resource(inputStream, uri);
    }

    @Data
    @AllArgsConstructor
    public static class Resource {
        private InputStream inputStream;
        private URI uri;
    }

    // private

    /**
     * 平台枚举
     */
    @Getter
    private enum PlatformEnum {

        WINDOWS("windows", "windows"),
        LINUX("linux", "linux");

        private final String value;
        private final String desc;

        PlatformEnum(String value, String desc) {
            this.value = value;
            this.desc = desc;
        }

    }
}
