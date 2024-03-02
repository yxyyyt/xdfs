package com.sciatta.xdfs.backupnode;

import com.sciatta.xdfs.common.fs.FSImage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * Created by Rain on 2024/2/29<br>
 * All Rights Reserved(C) 2017 - 2024 SCIATTA <br> <p/>
 * 镜像上传客户端
 */
@Slf4j
public class FSImageUploader extends Thread {

    private FSImage fsimage;

    public FSImageUploader(FSImage fsimage) {
        this.fsimage = fsimage;
    }

    @Override
    public void run() {
        SocketChannel channel = null;
        Selector selector = null;
        try {
            channel = SocketChannel.open();
            channel.configureBlocking(false);
            channel.connect(new InetSocketAddress("localhost", 9000));

            selector = Selector.open();
            channel.register(selector, SelectionKey.OP_CONNECT);

            boolean uploading = true;

            while (uploading) {
                selector.select();

                Iterator<SelectionKey> keysIterator = selector.selectedKeys().iterator();
                while (keysIterator.hasNext()) {
                    SelectionKey key = keysIterator.next();
                    keysIterator.remove();

                    if (key.isConnectable()) {
                        channel = (SocketChannel) key.channel();
                        if (channel.isConnectionPending()) {
                            channel.finishConnect();
                            ByteBuffer buffer = ByteBuffer.wrap(fsimage.getFsimage().getBytes());
                            log.debug("upload FSImage size {}", buffer.capacity());
                            channel.write(buffer);
                        }
                        channel.register(selector, SelectionKey.OP_READ);
                    } else if (key.isReadable()) {
                        ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
                        channel = (SocketChannel) key.channel();
                        int count = channel.read(buffer);
                        if (count > 0) {
                            log.debug("get upload server response {}", new String(buffer.array(), 0, count));
                            channel.close();
                            uploading = false;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("upload FSImage catch exception {}", e.getMessage());
        } finally {
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException ignore) {
                }
            }

            if (selector != null) {
                try {
                    selector.close();
                } catch (IOException ignore) {
                }
            }
        }
    }
}
