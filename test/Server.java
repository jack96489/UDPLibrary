import Files.Binary.BinaryFileUploader;
import Sockets.NoisyStopAndWait.StopAndWaitNoisySender;
import Sockets.UDPSocketUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class Server {

    public static void main(String[] args) {


        UDPSocketUtils s = null;
        try {
            s = new StopAndWaitNoisySender(6666);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        BinaryFileUploader uploader = new BinaryFileUploader(s, new InetSocketAddress("localhost", 5555));
            try {
                uploader.uploadFile("test.iso");
//                    for (long i = 0; i < Long.MAX_VALUE; i++) {
//                        s.sendString(Long.toString(i), 5555, "localhost");
//                    }
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}
