package Sockets;


import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Random;

public class StopAndWait extends UDPSocketUtils {
    private static final String ACK_SEQUENCE = "OK";
    private static final String NACK_SEQUENCE = "KO";

    public StopAndWait(DatagramSocket socket) {
        super(socket);
    }

    public StopAndWait() throws SocketException {
        socket.setSoTimeout(200);
    }

    public StopAndWait(int port) throws SocketException {
        super(port);
        socket.setSoTimeout(200);
    }

    @Override
    public void send(byte[] buffer, SocketAddress address) throws IOException {
        super.send(buffer, address);
        if (!isAck(buffer) && !isNAck(buffer)) {
//            System.out.println("ASPETTO ACKKK");
            while (!receiveAck()) {
                System.err.println("ACK NON RICEVUTO! RIMANDO IL PACCHETTO (tra 50 ms)");
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                super.send(buffer, address);
            }
        }

    }

    @Override
    public void receive(byte[] buffer) throws IOException {
        receive(buffer, true);
    }

    /**
     * Receive a packet and send an ack if sendAck is true
     *
     * @param buffer  buffer to receive
     * @param sendAck true for sending ack after receiving packet
     * @throws IOException
     */
    protected void receive(byte[] buffer, boolean sendAck) throws IOException {
        super.receive(buffer);
        if (!isAck(buffer) && sendAck && new Random().nextBoolean()) {
//            System.out.println("MANDO ACKKK");
            sendAck(getSocketAddress());
        }
    }

    protected void sendAck(SocketAddress address) throws IOException {
        sendString(ACK_SEQUENCE, address);
//        System.out.println("Mandato OK");
    }

    protected void sendNAck(SocketAddress address) throws IOException {
        sendString("KO", address);
        System.err.println("Mandato KO");
    }

    private boolean receiveAck() throws IOException {
        try {
            final String s = receiveString();
//            System.out.println("ACK: " + s);
            if (s.equals(NACK_SEQUENCE))
                throw new IOException("Error during comunication");
            return s.equals(ACK_SEQUENCE);
        } catch (SocketTimeoutException e) {
            return false;
        }
    }

    protected boolean isAck(byte[] buff) {
        String b = new String(buff);
        if (b.length() < 2)
            return false;
        return b.substring(0, 2).equals(ACK_SEQUENCE);
    }


    protected boolean isNAck(byte[] buff) {
        String b = new String(buff);
        if (b.length() < 2)
            return false;
        return b.substring(0, 2).equals(NACK_SEQUENCE);
    }

}
