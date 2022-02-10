package org.example.common;

import java.nio.ByteBuffer;

public class MyByteBuffer {

    private byte[] buffer = new byte[1024];
    private int size = 0;

    public int getSize() {
        return size;
    }

    public void add(byte[] bytes, int start, int end) {
        int bytesSize = end - start;
        if(size + bytesSize > buffer.length) {
            byte[] tmp = new byte[2 * buffer.length];
            System.arraycopy(buffer, start, tmp, 0, buffer.length);
            buffer = tmp;
        }
        System.arraycopy(bytes, start, buffer, size, bytesSize);
        size += bytesSize;

    }

    public byte[] getBuffer() {
        return buffer;
    }

    public void reset() {
        size = 0;
    }

    // usuwamy wszystko od 0 do idx-1
    public void clear(int idx) {
        System.arraycopy(buffer, idx, buffer, 0, size - idx);
        size -= idx;
    }


}
