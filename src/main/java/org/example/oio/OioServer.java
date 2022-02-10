package org.example.oio;

import org.example.common.MyByteBuffer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class OioServer {

    public static void main(String[] args) throws Exception {

        int port = 1234;

        ServerSocket ss = new ServerSocket();
        ss.bind(new InetSocketAddress((InetAddress) null, port));

        while(true) {
            Socket s = ss.accept();

            InputStream in = s.getInputStream();
            OutputStream out = s.getOutputStream();

            byte[] inBuffer = new byte[1024];
            MyByteBuffer readBytes = new MyByteBuffer();

            while(true) {

                int read = in.read(inBuffer);

                if(read == -1) {
                    break;
                }

                int idx = 0;
                while(idx < read && inBuffer[idx] != '\n') {
                    idx++;
                }

                readBytes.add(inBuffer, 0, idx);

                if(idx < read) {
                    writeResult(out, readBytes);
                    readBytes.reset();
                    readBytes.add(inBuffer, idx + 1, read);
                }

            }

            s.close();


        }

    }

    private static void writeResult(OutputStream out, MyByteBuffer readBytes) throws IOException {
        String s = new String(readBytes.getBuffer(), 0, readBytes.getSize());
        String res = s + s + '\n';
        System.out.print("sending to client: " + res);
        out.write(res.getBytes(StandardCharsets.UTF_8));
    }

}
