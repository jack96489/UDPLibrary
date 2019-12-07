package Sockets;


import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Random;

public class ReliableUDPSocket extends UDPSocket {
    private static final String ACK_SEQUENCE = "OK";

    public ReliableUDPSocket() throws SocketException {
        socket.setSoTimeout(500);
    }

    public ReliableUDPSocket(int port) throws SocketException {
        super(port);
        socket.setSoTimeout(500);
    }

    @Override
    public void send(byte[] buffer, SocketAddress address) throws IOException {
        super.send(buffer, address);
        if (!isAck(buffer)) {
            System.out.println("ASPETTO ACKKK");
            while (!receiveAck()) {
                System.out.println("AAAAAAA RIMANDO");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                super.send(buffer, address);
            }
        }

    }

    @Override
    protected byte[] receive() throws IOException {
        byte[] buff = super.receive();
        if (!isAck(buff) && new Random().nextBoolean()) {
            System.out.println("MANDO ACKKK");
            sendAck(getSocketAddress());
        }
        return buff;
    }

    private void sendAck(SocketAddress address) throws IOException {
        sendString("OK", address);
        System.out.println("Mandato");
    }

    private boolean receiveAck() throws IOException {
        try {
            final String s = receiveString();
            System.out.println("ACK: " + s);
            return s.equals("OK");
        } catch (SocketTimeoutException e) {
            return false;
        }
    }

    private boolean isAck(byte[] buff) {
        String b = new String(buff);
        if (b.length() < 2)
            return false;
        return b.substring(0, 2).equals(ACK_SEQUENCE);
    }

}
