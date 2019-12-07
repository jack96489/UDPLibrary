package test;

import Sockets.ReliableUDPSocket;
import Sockets.UDPSocket;

import java.io.IOException;
import java.net.SocketTimeoutException;

public class Main {

    public static void main(String[] args) throws Exception {
        try {
            UDPSocket s = new ReliableUDPSocket();
            UDPSocket s1 = new ReliableUDPSocket(5555);
            new Thread(() -> {
                for (long i = 0; i < Long.MAX_VALUE; i++) {
                    try {
                        s.sendString(Long.toString(i), 5555, "localhost");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            for (long i = 0; i < Long.MAX_VALUE; i++) {

                try {

                    long l = Long.parseLong(s1.receiveString());
                    System.out.println(l);
                    if (l != i) {
                        System.out.println("received: " + l);
                        i--;
                        //throw new Exception("AAAA");
                    }
                } catch (SocketTimeoutException ignored) {
                    i--;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
