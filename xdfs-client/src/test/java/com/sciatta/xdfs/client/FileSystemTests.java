package com.sciatta.xdfs.client;

import org.junit.Test;

import java.io.IOException;

/**
 * Created by Rain on 2024/2/21<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * FileSystemTests
 */
public class FileSystemTests {
    private final FileSystem fileSystem = new FileSystemImpl();

    @Test
    public void testMkdir() {
        fileSystem.mkdir("/root/app");
    }

    @Test
    public void testMkdirUsingSingleThread() {
        int number = 10;
        for (int i = 0; i < number; i++) {
            fileSystem.mkdir("/root/app/file" + i);
        }
    }

    @Test
    public void testShutdown() {
        fileSystem.shutdown();
    }
}
