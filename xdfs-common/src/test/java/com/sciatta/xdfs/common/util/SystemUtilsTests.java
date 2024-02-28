package com.sciatta.xdfs.common.util;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by Rain on 2023/8/21<br>
 * All Rights Reserved(C) 2017 - 2023 SCIATTA <br> <p/>
 * SystemUtilsTests
 */
public class SystemUtilsTests {

    @Test
    public void testIsWindowsPlatform() {
        boolean ans = SystemUtils.isWindowsPlatform();
        assertTrue(ans);

        ans = SystemUtils.isLinuxPlatform();
        assertFalse(ans);
    }

    @Test
    public void testGetPid() {
        String pid = SystemUtils.getPid();
        System.out.println(pid);
    }

    @Test
    public void testUnusedPhysicalMemorySize() {
        double v = SystemUtils.unusedPhysicalMemorySize();
        System.out.println(v);
    }

    @Test
    public void testUsedPhysicalMemorySizeRatio() {
        double v = SystemUtils.usedPhysicalMemorySizeRatio();
        System.out.println(v);
    }

    @Test
    public void testCpuLoad() {
        double v = SystemUtils.cpuLoad();
        System.out.println(v);
    }

    @Test
    public void testCheckResource() {
        boolean b = SystemUtils.checkResourceState(0.2, 0.4);
        System.out.println(b);
    }

    @Test
    public void testGetResourceFromClassPath() throws IOException {
        SystemUtils.Resource resourceFromClassPath = SystemUtils.getResourceFromClassPath("META-INF/test");
        assertNotNull(resourceFromClassPath);
    }

}
