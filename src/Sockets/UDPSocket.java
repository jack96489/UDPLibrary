/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Sockets;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;

public class UDPSocket {
    protected final DatagramSocket socket;
    private DatagramPacket lastReceivedPacket;

    public UDPSocket() throws SocketException {
        socket = new DatagramSocket();
    }

    public UDPSocket(int port) throws SocketException {
        socket = new DatagramSocket(port);
    }

    public void sendString(String msg, int port, String ip) throws IOException {
        sendString(msg, new InetSocketAddress(ip, port));
    }

    public void sendString(String msg, SocketAddress address) throws IOException {
        send(msg.getBytes(), address);
    }

    public String receiveString() throws IOException {
        String ricevuto = new String(receive());

        // elaborazione dei dati ricevuti eliminando i caratteri in eccesso
        return ricevuto.substring(0, lastReceivedPacket.getLength());
    }

    public void send(byte[] buffer, SocketAddress address) throws IOException {
        //Creo il pacchetto da inviare
        DatagramPacket sendPacket = new DatagramPacket(buffer, buffer.length, address);
        //Invio
        socket.send(sendPacket);
    }

    public void send(byte[] buffer, int port, String ip) throws IOException {
        send(buffer, new InetSocketAddress(ip, port));
    }

    protected byte[] receive() throws IOException {
        //Creo l'allocazione del pacchetto da ricevere
        byte[] bufferIN = new byte[1024];
        lastReceivedPacket = new DatagramPacket(bufferIN, bufferIN.length);

        //Aspetto il pacchetto
        socket.receive(lastReceivedPacket);

        return bufferIN;
    }

    public void receive(byte[] bufferIN) throws IOException {
        lastReceivedPacket = new DatagramPacket(bufferIN, bufferIN.length);

        //Aspetto il pacchetto
        socket.receive(lastReceivedPacket);
    }

    public void sendByte(byte b, SocketAddress address) throws IOException {
        send(new byte[]{b}, address);
    }

    public void sendByte(byte b, int port, String ip) throws IOException {
        send(new byte[]{b}, new InetSocketAddress(ip, port));
    }

    public void sendInteger(int i, SocketAddress address) throws IOException {
        send(ByteBuffer.allocate(Integer.BYTES).putInt(i).array(), address);
    }

    public void sendInteger(int i, int port, String ip) throws IOException {
        sendInteger(i, new InetSocketAddress(ip, port));
    }

    public void sendLong(long i, SocketAddress address) throws IOException {
        send(ByteBuffer.allocate(Long.BYTES).putLong(i).array(), address);
    }

    public void sendLong(int i, int port, String ip) throws IOException {
        sendLong(i, new InetSocketAddress(ip, port));
    }

    public byte receiveByte() throws IOException {
        return receive()[0];
    }

    public int receiveInteger() throws IOException {
        byte[] buff = new byte[Integer.BYTES];
        lastReceivedPacket = new DatagramPacket(buff, buff.length);

        //Aspetto il pacchetto
        socket.receive(lastReceivedPacket);

        return ByteBuffer.wrap(buff).getInt();
    }

    public long receiveLong() throws IOException {
        byte[] buff = new byte[Long.BYTES];
        lastReceivedPacket = new DatagramPacket(buff, buff.length);

        //Aspetto il pacchetto
        socket.receive(lastReceivedPacket);

        return ByteBuffer.wrap(buff).getLong();
    }

    public int getPort() {
        return lastReceivedPacket.getPort();
    }

    public String getIP() {
        return lastReceivedPacket.getAddress().toString();
    }

    public SocketAddress getSocketAddress() {
        return lastReceivedPacket.getSocketAddress();
    }

    public void close() {
        socket.close();
    }
}
