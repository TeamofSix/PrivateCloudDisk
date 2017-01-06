package com.neil.client.main;

import udt.util.ReceiveFile;
import udt.util.SendFile;
import udt.util.UDTThreadFactory;

import java.net.DatagramSocket;

public class SendAndReceive {

    volatile boolean serverStarted = false;

    public void recv(DatagramSocket serverSocket, String serverHost, int serverPort, String remoteFile, String localFile) throws Exception {
        ReceiveFile.main(serverSocket, serverHost, Integer.toString(serverPort), remoteFile, localFile);
    }

    public void runServer(DatagramSocket socket) {
        Runnable r = new Runnable() {
            public void run() {
                try {
                    serverStarted = true;
                    SendFile.main(socket);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        Thread t = UDTThreadFactory.get().newThread(r);
        t.start();
    }
}
