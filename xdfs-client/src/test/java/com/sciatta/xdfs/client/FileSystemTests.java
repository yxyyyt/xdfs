package com.sciatta.xdfs.client;

import org.junit.Test;

import java.io.IOException;

/**
 * Created by Rain on 2024/2/21<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * FileSystemTests
 */
public class FileSystemTests {
    @Test
    public void testMkdir() throws IOException {
        FileSystem fileSystem = new FileSystemImpl();
        fileSystem.mkdir("/root/app");
        fileSystem.mkdir("/root/app/1");
    }
}
