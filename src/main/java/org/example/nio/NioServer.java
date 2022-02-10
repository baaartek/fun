package org.example.nio;

import org.example.common.MyByteBuffer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;

class PairOfBuffers {
    public MyByteBuffer in = new MyByteBuffer();
    public MyByteBuffer out = new MyByteBuffer();
}

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

    // TODO sprawdzania nie trzeba zaczynac ciagle od poczatku
    private static boolean readDone(MyByteBuffer buffer) {
        for(int i=0; i<buffer.getSize(); i++) {
            if(buffer.getBuffer()[i] == '\n') {
                return true;
            }
        }
        return false;
    }

    private static void prepareResponseAndClearRead(MyByteBuffer in, MyByteBuffer out) {
        int idx = 0;
        while(in.getBuffer()[idx] != '\n') {
            idx++;
        }

        out.add(in.getBuffer(), 0, idx);
        out.add(in.getBuffer(), 0, idx);
        out.add(new byte[]{'\n'}, 0, 1);

        in.clear(idx + 1);


    }

    private static void handleClients(Selector clientSelector) {

        try {
            byte[] buffer = new byte[1024];
            while (true) {
                int res = clientSelector.select(0);
                // TODO handle res == 0
                for(SelectionKey key : clientSelector.selectedKeys()) {
                    PairOfBuffers buffers = (PairOfBuffers) key.attachment();
                    SocketChannel channel = (SocketChannel) key.channel();
                    if(key.isReadable()) {
                        ByteBuffer tmp = ByteBuffer.wrap(buffer);
                        channel.read(tmp);
                        buffers.in.add(buffer, 0, tmp.position());
                        if (readDone(buffers.in)) {
                            prepareResponseAndClearRead(buffers.in, buffers.out);
                        }
                    }
                    if(buffers.out.getSize() > 0) {
                        ByteBuffer tmp = ByteBuffer.wrap(buffers.out.getBuffer(), 0, buffers.out.getSize());
                        int written = channel.write(tmp);
                        if(written > 0) {
                            buffers.out.clear(written);
                            if(buffers.out.getSize() == 0) {
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
            clientChannel.register(clientSelector, SelectionKey.OP_READ, new PairOfBuffers());
            clientSelector.wakeup();
        }


    }

}
