package Sockets.NoisyStopAndWait;

import Sockets.StopAndWait;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class StopAndWaitNoisyRecipient extends StopAndWait {
    private long sequenceNumber;

    public StopAndWaitNoisyRecipient(DatagramSocket socket) {
        super(socket);
//        sequenceNumber = new AtomicLong();
    }

    public StopAndWaitNoisyRecipient() throws SocketException {
//        sequenceNumber = new AtomicLong();
        socket.setSoTimeout(1000);
    }

    public StopAndWaitNoisyRecipient(int port) throws SocketException {
        super(port);
//        sequenceNumber = new AtomicLong();
        socket.setSoTimeout(1000);
    }

    @Override
    public void send(byte[] buffer, SocketAddress address) throws IOException {
//        System.out.println("SENDO ACK:" + Arrays.toString(buffer));
        if (isAck(buffer) || isNAck(buffer))
            super.send(buffer, address);
        else throw new UnsupportedOperationException("Recepient can't send :/");
    }

    protected long getSeqNumber(byte[] buffer) {
        return ByteBuffer.wrap(buffer).getLong();
    }

    protected byte[] removeSeqNumber(byte[] buffer) {
        return Arrays.copyOfRange(buffer, Long.BYTES, buffer.length);
    }


    @Override
    public void receive(byte[] buffer) throws IOException {
        byte[] buff = new byte[buffer.length + Long.BYTES];
        System.out.println("RICEVO");
        super.receive(buff, false);
//        System.out.println(sequenceNumber + " - " + getSeqNumber(buff));
        if (sequenceNumber == getSeqNumber(buff)) {
//            System.out.println("RICEVUTO");
//            if (new Random().nextInt(1000) != 5)        //test
            sendAck(getSocketAddress());
            lastReceivedPacket.setLength(lastReceivedPacket.getLength() - Long.BYTES);        //Hack to remove the sequence number
            System.arraycopy(buff, Long.BYTES, buffer, 0, buffer.length);
            sequenceNumber++;
//            System.out.println(Arrays.toString(buffer));
        } else if (sequenceNumber > getSeqNumber(buff)) {
            //il mittente è tornato indietro -> Si è perso un ack -> chicchibio -> mando un nuovo ack e rifaccio la receive
            System.err.println("Rimando l'ack scomparso");
            sendAck(getSocketAddress());
            receive(buffer);
//            sendNAck(getSocketAddress());
//            throw new IOException("Error during comunication");
        } else if (sequenceNumber < getSeqNumber(buff))
            //unreachable: Un pacchetto non è stato ricevuto -> l'ack non è stato mandato -> il pacchetto è stato reinviato
            throw new IOException("Error during communication");
    }

}
