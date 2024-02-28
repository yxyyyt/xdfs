package com.sciatta.xdfs.common.fs;

import com.sciatta.xdfs.common.fs.FSDirectory;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Rain on 2024/2/19<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * FSDirectoryTests
 */
public class FSDirectoryTests {
    @Test
    public void testMkdir_Root() {
        FSDirectory directory = new FSDirectory();
        assertNotNull(directory);
    }

    @Test
    public void testMkdir_OneDir() {
        FSDirectory directory = new FSDirectory();
        directory.mkdir("/a");
        assertNotNull(directory);
    }

    @Test
    public void testMkdir_MoreDir() {
        FSDirectory directory = new FSDirectory();
        directory.mkdir("/a/b/c");
        assertNotNull(directory);
    }

    @Test
    public void testMkdir_ExistsDir() {
        FSDirectory directory = new FSDirectory();
        directory.mkdir("/a/b/c");
        directory.mkdir("/a/f/d");
        assertNotNull(directory);
    }

}
