package org.example.nio.custom;

import org.example.common.MyByteBuffer;
import org.example.nio.ChannelReadHandler;
import org.example.nio.MyChannel;

public class FancyChannelReadHandler implements ChannelReadHandler {

    @Override
    public void handleRead(MyChannel channel, MyByteBuffer bytes) {

        boolean done = false;
        while(!done) {
            done = true;
            byte[] buffer = bytes.getBuffer();
            for (int i = 0; i < bytes.getSize(); i++) {
                if (buffer[i] == '\n') {
                    channel.write(bytes.getBuffer(), 0, i);
                    channel.write(bytes.getBuffer(), 0, i);
                    channel.write(new byte[]{'\n'}, 0, 1);
                    bytes.clear(i+1);
                    done = false;
                    break;
                }
            }

        }

    }
}
