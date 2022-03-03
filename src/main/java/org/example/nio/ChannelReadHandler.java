package org.example.nio;

import org.example.common.MyByteBuffer;

public interface ChannelReadHandler {
    void handleRead(MyChannel channel, MyByteBuffer bytes);
}
