package Files.Binary;

import Files.Downloader;
import Files.FileTransfer;
import Sockets.UDPSocket;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class BinaryFileDownloader extends FileTransfer implements Downloader {
    public BinaryFileDownloader(UDPSocket socket, SocketAddress serverAddress) {
        super(socket, serverAddress);
    }

    @Override
    public void downloadFile(String fileName) throws IOException {

        final short packetSize = 1024;
        byte[] bufferIN = new byte[Long.BYTES];
//        DatagramPacket receivePacket = new DatagramPacket(bufferIN, bufferIN.length);

        //get number of packets
//        socket.receive(receivePacket);
        if (!address.equals(socket.getSocketAddress()))
            throw new IOException("Puoi ricevere da un solo peer alla volta");
//        final int packetsNumber = Integer.parseInt(new String(receivePacket.getData()).substring(0, receivePacket.getLength()));
        final long totalLength = socket.receiveLong();//ByteBuffer.wrap(bufferIN).getLong();
        final long packetsNumber = (long) Math.ceil((double) totalLength / packetSize);
        System.out.println(totalLength);
        System.out.println(packetsNumber);

        final BlockingQueue<byte[]> coda = new LinkedBlockingQueue<>();

        //creo un altro thread che andrà effettivamente a scrivere sul file
        new Thread(() -> {
            final File file = new File("share/" + fileName);
            file.getParentFile().mkdirs();
            try (final BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {
                for (long i = 0; i < packetsNumber; i++) {
                    try {
                        byte[] data = coda.take();
                        byte[] temp = Arrays.copyOfRange(data, 0, Long.BYTES + 1);
                        final long nPacchetto = ByteBuffer.wrap(temp).getLong();
//                        System.out.println("Letto pacchetto " + i + " - " + nPacchetto);
                        if (nPacchetto != i)
                            System.err.println("S'è rooott");

                        bos.write(data, Long.BYTES, data.length - Long.BYTES);
                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();


        //download file
        int errori = 0;
        for (long i = 0; i < packetsNumber; i++) {
            // attesa della ricezione dato dal client
            if (i == packetsNumber - 1) {
                bufferIN = new byte[(int) (totalLength - packetSize * i)];
//                receivePacket = new DatagramPacket(bufferIN, bufferIN.length);
            } else {
                bufferIN = new byte[packetSize];
//                receivePacket = new DatagramPacket(bufferIN, bufferIN.length);
            }
            socket.receive(bufferIN);
            if (!address.equals(socket.getSocketAddress()))
                throw new IOException("Puoi ricevere da un solo peer alla volta");

            byte[] temp = Arrays.copyOfRange(bufferIN, 0, Long.BYTES + 1);
            final long nPacchetto = ByteBuffer.wrap(temp).getLong();

            if (i % 1000 == 0)
                System.out.println("Ricevuto pacchetto " + i + " - " + nPacchetto);
//            StringBuilder sb = new StringBuilder(bufferIN.length * 2);
//            for (byte b : bufferIN)
//                sb.append(String.format("%02x", b)).append(" ");
//            System.out.println(sb.toString() + "\n\n" + i);
            if (nPacchetto != i) {
                errori += nPacchetto - i;
                System.err.println("Errore! Pacchetto " + i + " scoparisciuto! Ricevuto pacchetto " + nPacchetto);
                i = nPacchetto;
            }

//            bos.write(bufferIN, Long.BYTES, bufferIN.length - Long.BYTES);
            coda.add(bufferIN);
//            if (i % 1000 == 0)
//            System.out.println("received packet: " + i/* + " Content: " + Arrays.toString(readData) + "\n\n\n\""*/);
        }
        System.out.println("Pacchetti mancanti: " + errori);

//        bos.close();
        //mando l'ack
//        sendAck();
    }

}