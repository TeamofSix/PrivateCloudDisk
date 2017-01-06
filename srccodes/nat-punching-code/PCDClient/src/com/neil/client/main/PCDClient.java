package com.neil.client.main;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neil.client.attr.MappedAddress;
import com.neil.client.attr.ExchangedAddress;
import com.neil.client.callback.ReceivedPeerIPInfoCallback;
import com.neil.client.main.PCDClient;

public class PCDClient {
	static final Logger LOGGER = LoggerFactory.getLogger(PCDClient.class);

	private DatagramSocket socket;
	private int clientPort;
	private InetAddress serverInetAddress;
	
	public PCDClient(int clientPort, InetAddress serverInetAddress) {
		this.serverInetAddress = serverInetAddress;
		this.clientPort = clientPort;
	}

	public void start() throws SocketException {
		socket = new DatagramSocket(clientPort);
		
		// 用于处理接收到来自服务器发送的Peer端的IP消息的回调
		ReceivedPeerIPInfoCallback callback = new ReceivedPeerIPInfoCallback(){
			public void onReceivedPeerIPInfo(MappedAddress ma,ExchangedAddress ea) {
				System.out.println("call back: MA<"+ma.toString()+">,EA<"+ea.toString()+">");
				PCDClientPeerThreadUdt peerThread = new PCDClientPeerThreadUdt(ma, ea, socket);
				peerThread.start();
			}
		};
		
		PCDClientReceiverThread pcd = new PCDClientReceiverThread(socket,serverInetAddress, callback);
		pcd.start();
	}
}
