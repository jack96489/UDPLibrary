package Files.Binary;

import Files.FileTransfer;
import Files.Uploader;
import Sockets.UDPSocketUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketAddress;

public class BinaryFileUploader extends FileTransfer implements Uploader {

    public BinaryFileUploader(UDPSocketUtils socket, SocketAddress address) {
        super(socket, address);
    }

    @Override
    public void uploadFile(String fileName) throws IOException {
        final File file = new File("share/" + fileName);
        final BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        final int packetSize = 1024;

        //mando la lunghezza del file
        final long nPackets = (long) Math.ceil((double) file.length() / (packetSize - Long.BYTES));
        System.out.println("File length: " + (double) file.length() / (packetSize - Long.BYTES));
//        final String n = Integer.toString(nPackets);
        final long totalLength = file.length() + Long.BYTES * nPackets;
//        byte[] bufferOUT = ByteBuffer.allocate(Long.BYTES).putLong(totalLength).array();
//        final DatagramPacket packetNumber = new DatagramPacket(bufferOUT, bufferOUT.length, address);
//        socket.send(packetNumber);
        socket.sendLong(totalLength, address);
//        System.out.println(ByteBuffer.wrap(bufferOUT).getLong());

//        try {       //aspetto che il client sia pronto
//            Thread.sleep(100);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        byte[] bufferOUT = new byte[packetSize];
//        DatagramPacket dp = new DatagramPacket(bufferOUT, bufferOUT.length, address);
        for (long sentPackets = 0; sentPackets < nPackets; sentPackets++) {
            if (sentPackets == nPackets - 1) {           //Se è l'ultimo è più corto
                bufferOUT = new byte[(int) (totalLength - packetSize * sentPackets)];
//                dp = new DatagramPacket(bufferOUT, bufferOUT.length, address);
            }
            bis.read(bufferOUT, 0, bufferOUT.length);
            socket.send(bufferOUT, address);

            //Sleep di 1 ms ogni 10 pacchetti perchè altrimenti spariscono i pacchetti
            if (sentPackets % 10 == 0)
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            if (sentPackets % 1000 == 0)
                System.out.println("Inviato pacchetto " + sentPackets);
        }
        bis.close();
    }
}
