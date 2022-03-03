package org.example.nio;

import org.example.common.MyByteBuffer;

import java.nio.ByteBuffer;

public class MyChannel {

    private final MyByteBuffer inBuffer;
    private final MyByteBuffer outBuffer;

    private final ChannelReadHandler readHandler;

    public MyChannel(ChannelReadHandler readHandler) {
        inBuffer = new MyByteBuffer();
        outBuffer = new MyByteBuffer();
        this.readHandler = readHandler;
    }

    public void addRead(byte[] bytes, int start, int end) {
        inBuffer.add(bytes, start, end);
        readHandler.handleRead(this, inBuffer);
    }

    public void write(byte[] bytes, int start, int end) {
        outBuffer.add(bytes, start, end);
    }

    public int getNumOfBytesToWrite() {
        return outBuffer.getSize();
    }

    public ByteBuffer getBytesToWrite() {
        return ByteBuffer.wrap(outBuffer.getBuffer(), 0, outBuffer.getSize());
    }

    public void notifyBytesWritten(int writtenBytes) {
        outBuffer.clear(writtenBytes);
    }
}
