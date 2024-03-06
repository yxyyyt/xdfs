package com.sciatta.xdfs.common.util;

import org.junit.Test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

/**
 * Created by Rain on 2024/3/4<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * PathUtilsTests
 */
public class PathUtilsTests {
    @Test
    public void testGetNameNodeEditLogPath() throws IOException {
        String nameNodeEditLogPath = PathUtils.getNameNodeEditLogPath();
        System.out.println(nameNodeEditLogPath);
    }

    @Test
    public void testGetNameNodeEditLogFile() {
        String nameNodeEditLogFile = PathUtils.getNameNodeEditLogFile(1, 2);
        System.out.println(nameNodeEditLogFile);
    }

    @Test
    public void testIsNameNodeEditLogFile() {
        String nameNodeEditLogFile = PathUtils.getNameNodeEditLogFile(1, 2);
        File file = new File(nameNodeEditLogFile);
        boolean ans = PathUtils.isNameNodeEditLogFile(file.getName());
        assertTrue(ans);

        ans = PathUtils.isNameNodeEditLogFile("edit-1-2.log1");
        assertFalse(ans);
    }

    @Test
    public void testGetNameNodeImagePath() throws IOException {
        String nameNodeImagePath = PathUtils.getNameNodeImagePath();
        System.out.println(nameNodeImagePath);
    }

    @Test
    public void testGetNameNodeImageFile() {
        String nameNodeImageFile = PathUtils.getNameNodeImageFile();
        System.out.println(nameNodeImageFile);
    }

    @Test
    public void testGetNameNodeCheckpointTxidPath() throws IOException {
        String nameNodeCheckpointTxidPath = PathUtils.getNameNodeCheckpointTxidPath();
        System.out.println(nameNodeCheckpointTxidPath);
    }

    @Test
    public void testGetNameNodeCheckpointTxidFile() {
        String nameNodeCheckpointTxidFile = PathUtils.getNameNodeCheckpointTxidFile();
        System.out.println(nameNodeCheckpointTxidFile);
    }

    @Test
    public void testGetBackupNodeImagePath() throws IOException {
        String backupNodeImagePath = PathUtils.getBackupNodeImagePath();
        System.out.println(backupNodeImagePath);
    }

    @Test
    public void testGetBackupNodeImageFile() {
        String backupNodeImageFile = PathUtils.getBackupNodeImageFile(1);
        System.out.println(backupNodeImageFile);
    }

    @Test
    public void testGetBackupNodeCheckpointInfoPath() throws IOException {
        String backupNodeCheckpointInfoPath = PathUtils.getBackupNodeCheckpointInfoPath();
        System.out.println(backupNodeCheckpointInfoPath);
    }

    @Test
    public void testGetBackupNodeCheckpointInfoFile() {
        String backupNodeCheckpointInfoFile = PathUtils.getBackupNodeCheckpointInfoFile();
        System.out.println(backupNodeCheckpointInfoFile);
    }
}
