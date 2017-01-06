package com.neil.client.main;

import com.neil.client.utils.Address;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import u14.UdpxClient;
import udt.test1.PCDUdt;

import java.io.IOException;
import java.net.DatagramSocket;
import java.util.Arrays;

/**
 * 与peer端建立通信
 *
 * @author Neil
 */
public class PCDClientPeerThreadUdpx extends Thread {
    static final Logger LOGGER = LoggerFactory.getLogger(PCDClientPeerThreadUdpx.class);
    private Address peerAddress;
    private int peerPort;
    private DatagramSocket clientSocket;
    private PCDUdt udt;

    public PCDClientPeerThreadUdpx(Address address, int port, DatagramSocket socket) {
        peerAddress = address;
        peerPort = port;
        clientSocket = socket;
    }

    @Override
    public void run() {
        UdpxClient client = new UdpxClient(clientSocket);
        super.run();
    }

}