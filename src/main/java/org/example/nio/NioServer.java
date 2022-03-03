package org.example.nio;

import org.example.nio.custom.FancyChannelReadHandler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;

public class NioServer {

    public static void main(String[] args) throws Exception {

        int port = 1234;

        ServerSocketChannel sc = ServerSocketChannel.open();
        sc.bind(new InetSocketAddress((InetAddress) null, port));

        Selector acceptSelector = Selector.open();
        Selector clientSelector = Selector.open();

        sc.configureBlocking(false);
        sc.register(acceptSelector, SelectionKey.OP_ACCEPT);

        new Thread(() -> handleClients(clientSelector)).start();

        while(true) {
            handle(acceptSelector, clientSelector);
        }

    }

    private static void handleClients(Selector clientSelector) {

        try {
            byte[] buffer = new byte[1024];
            while (true) {
                int res = clientSelector.select(0);
                // TODO handle res == 0
                for(SelectionKey key : clientSelector.selectedKeys()) {
                    MyChannel myChannel = (MyChannel) key.attachment();
                    SocketChannel channel = (SocketChannel) key.channel();
                    if(key.isReadable()) {
                        ByteBuffer tmp = ByteBuffer.wrap(buffer);
                        channel.read(tmp);
                        myChannel.addRead(buffer, 0, tmp.position());

                    }
                    int numOfBytesToWrite = myChannel.getNumOfBytesToWrite();
                    if(numOfBytesToWrite > 0) {
                        ByteBuffer tmp = myChannel.getBytesToWrite();
                        int written = channel.write(tmp);
                        if(written > 0) {
                            myChannel.notifyBytesWritten(written);
                            if(numOfBytesToWrite == written) {
                                key.interestOpsAnd(~SelectionKey.OP_WRITE);
                            }
                        } else {
                            key.interestOpsAnd(SelectionKey.OP_WRITE);
                        }
                    }
                }



            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    private static void handle(Selector acceptSelector, Selector clientSelector) throws IOException {

        acceptSelector.select(0);

        for(SelectionKey key : acceptSelector.selectedKeys()) {
            ServerSocketChannel channel = (ServerSocketChannel) key.channel();
            SocketChannel clientChannel = channel.accept();
            clientChannel.configureBlocking(false);
            clientChannel.register(clientSelector, SelectionKey.OP_READ, new MyChannel(new FancyChannelReadHandler()));
            clientSelector.wakeup();
        }


    }

}
