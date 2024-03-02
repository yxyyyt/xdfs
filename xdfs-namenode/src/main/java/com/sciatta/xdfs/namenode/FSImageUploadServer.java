package com.sciatta.xdfs.namenode;

import com.sciatta.xdfs.common.util.PathUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * Created by Rain on 2024/2/29<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * 镜像上传服务
 */
@Slf4j
public class FSImageUploadServer extends Thread {
    private static final int LISTEN_PORT = 9000;    // TODO to log

    private Selector selector;

    public FSImageUploadServer() {
        init();
    }

    /**
     * 初始化服务
     */
    private void init() {
        ServerSocketChannel serverSocketChannel;
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(LISTEN_PORT), 100);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            log.debug("FSImage upload server init success, listen port {}", LISTEN_PORT);
        } catch (IOException e) {
            log.error("FSImage upload server init catch exception {}", e.getMessage());
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                selector.select();
                Iterator<SelectionKey> keysIterator = selector.selectedKeys().iterator();
                while (keysIterator.hasNext()) {
                    SelectionKey key = keysIterator.next();
                    keysIterator.remove();
                    handleRequest(key);
                }
            } catch (IOException e) {
                log.error("FSImage upload server handle request catch exception {}", e.getMessage());
            }
        }
    }

    /**
     * 处理请求
     *
     * @param key 选择键
     * @throws IOException IO异常
     */
    private void handleRequest(SelectionKey key) throws IOException {
        if (key.isAcceptable()) {
            handleConnectRequest(key);
        } else if (key.isReadable()) {
            handleReadableRequest(key);
        } else if (key.isWritable()) {
            handleWritableRequest(key);
        }
    }

    /**
     * 处理连接请求
     *
     * @param key 选择键
     * @throws IOException IO异常
     */
    private void handleConnectRequest(SelectionKey key) throws IOException {
        SocketChannel channel = null;
        try {
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
            channel = serverSocketChannel.accept();
            if (channel != null) {
                channel.configureBlocking(false);
                channel.register(selector, SelectionKey.OP_READ);
            }
        } catch (IOException e) {
            if (channel != null) {
                channel.close();
            }
            throw e;
        }
    }

    /**
     * 处理读请求
     *
     * @param key 选择键
     * @throws IOException IO异常
     */
    private void handleReadableRequest(SelectionKey key) throws IOException {
        SocketChannel channel = null;

        String fsimageFilePath = PathUtils.getNameNodeImagePath();

        RandomAccessFile fsimageImageRAF = null;
        FileOutputStream fsimageOut = null;
        FileChannel fsimageFileChannel = null;

        try {
            channel = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);  // 缓存1KB

            int total = 0;
            int count;

            // 首次读
            if ((count = channel.read(buffer)) > 0) {
                File file = new File(fsimageFilePath);
                if (file.exists()) {    // 删除已存在的镜像
                    file.delete();
                }

                fsimageImageRAF = new RandomAccessFile(fsimageFilePath, "rw");
                fsimageOut = new FileOutputStream(fsimageImageRAF.getFD());
                fsimageFileChannel = fsimageOut.getChannel();

                total += count;

                buffer.flip();
                fsimageFileChannel.write(buffer);
                buffer.clear();
            } else {
                channel.close();
            }

            // 持续读
            while ((count = channel.read(buffer)) > 0) {
                total += count;
                buffer.flip();
                fsimageFileChannel.write(buffer);
                buffer.clear();
            }

            if (total > 0) {
                fsimageFileChannel.force(false);
                channel.register(selector, SelectionKey.OP_WRITE);
                log.debug("flush FSImage file success, total {} bytes", total);
            }
        } catch (IOException e) {
            channel.close();
            throw e;
        } finally {
            if (fsimageFileChannel != null) {
                fsimageFileChannel.close();
            }
            if (fsimageOut != null) {
                fsimageOut.close();
            }
            if (fsimageImageRAF != null) {
                fsimageImageRAF.close();
            }
        }
    }

    /**
     * 处理写请求
     *
     * @param key 选择键
     * @throws IOException IO异常
     */
    private void handleWritableRequest(SelectionKey key) throws IOException {
        SocketChannel channel = null;
        String ans = "SUCCESS";

        try {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            buffer.put(ans.getBytes());
            buffer.flip();

            channel = (SocketChannel) key.channel();
            channel.write(buffer);

            log.debug("write response {}", ans);

            channel.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            channel.close();
            throw e;
        }
    }
}
