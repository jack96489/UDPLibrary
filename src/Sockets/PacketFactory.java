package Sockets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLong;

public class PacketFactory {
    private AtomicLong sequenceNumber;

    public DatagramPacket createPacket(String msg, SocketAddress address) {
        return createPacket(msg.getBytes(), address);
    }

    public DatagramPacket createPacket(String msg, String serverAddress, int port) {
        return createPacket(msg, new InetSocketAddress(serverAddress, port));
    }

    public DatagramPacket createPacket(byte[] msg, SocketAddress address) {
        ByteArrayOutputStream buff = new ByteArrayOutputStream();
        byte[] seq = ByteBuffer.allocate(Long.BYTES).putLong(sequenceNumber.getAndIncrement()).array();
        try {
            buff.write(seq);
            buff.write(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final byte[] buffer = buff.toByteArray();
        return new DatagramPacket(buffer, buffer.length, address);
    }

    public DatagramPacket createPacket(byte[] msg, String serverAddress, int port) {
        return createPacket(msg, new InetSocketAddress(serverAddress, port));
    }

}
