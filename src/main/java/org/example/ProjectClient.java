package org.example;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ProjectClient {

    public static void main(String[] args) throws Exception {

        Socket s = new Socket();
        s.connect(new InetSocketAddress("localhost", 1234));

        OutputStream out = s.getOutputStream();
        BufferedInputStream in = new BufferedInputStream(s.getInputStream());

        BufferedReader sin = new BufferedReader(new InputStreamReader(System.in));

        while(true) {
            String ss = sin.readLine();

            System.out.println("sending to server: " + ss);
            out.write((ss + "\n").getBytes(StandardCharsets.UTF_8));

            StringBuilder builder = new StringBuilder();

            int b;
            while((b = in.read()) != -1) {
                builder.append((char) b);
                if(b == '\n') {
                    System.out.print("read from server: " + builder);
                    break;
                }
            }


        }

    }
}
