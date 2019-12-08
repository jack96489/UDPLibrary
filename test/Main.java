import Files.Binary.BinaryFileDownloader;
import Files.Binary.BinaryFileUploader;
import Sockets.NoisyStopAndWait.StopAndWaitNoisyRecipient;
import Sockets.NoisyStopAndWait.StopAndWaitNoisySender;
import Sockets.StopAndWait;
import Sockets.UDPSocketUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;

public class Main {

    public static void main(String[] args) throws Exception {
        try {
            UDPSocketUtils s1 = new StopAndWaitNoisyRecipient(5555);
            BinaryFileDownloader downloader = new BinaryFileDownloader(s1, new InetSocketAddress("localhost", 6666));



//            for (long i = 0; i < Long.MAX_VALUE; i++) {

            try {
                downloader.downloadFile("test.iso");
//                    long l = Long.parseLong(s1.receiveString());
//                    System.out.println("MAIN: " + l);
//                    if (l != i) {
//                        System.out.println("received: " + l);
//                        i--;
//                        //throw new Exception("AAAA");
//                    }
            } catch (SocketTimeoutException ignored) {
//                    i--;
                ignored.printStackTrace();
            }

//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
