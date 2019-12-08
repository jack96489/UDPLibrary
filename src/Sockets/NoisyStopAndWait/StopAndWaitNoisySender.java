package Sockets.NoisyStopAndWait;

import Sockets.StopAndWait;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLong;

public class StopAndWaitNoisySender extends StopAndWait {
    private AtomicLong sequenceNumber;

    public StopAndWaitNoisySender(DatagramSocket socket) {
        super(socket);
        sequenceNumber = new AtomicLong();
    }

    public StopAndWaitNoisySender() throws SocketException {
        sequenceNumber = new AtomicLong();
    }

    public StopAndWaitNoisySender(int port) throws SocketException {
        super(port);
        sequenceNumber = new AtomicLong();
    }

    @Override
    public void send(byte[] buffer, SocketAddress address) throws IOException {
        final byte[] buff = addSeqNumber(buffer);
        super.send(buff, address);
    }

    protected byte[] addSeqNumber(byte[] buffer) {
        ByteArrayOutputStream buff = new ByteArrayOutputStream();
        byte[] seq = ByteBuffer.allocate(Long.BYTES).putLong(sequenceNumber.getAndIncrement()).array();
        try {
            buff.write(seq);
            buff.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buff.toByteArray();
    }


    @Override
    public void receive(byte[] buffer) throws IOException {
        super.receive(buffer, false);
//        System.out.println(Arrays.toString(buffer));
        if (!isAck(buffer) && !isNAck(buffer))
            throw new UnsupportedOperationException("Sender can't receive :/");
    }

}
