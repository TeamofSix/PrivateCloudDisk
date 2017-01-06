package com.neil.client.main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neil.client.attr.MessageAttributeInterface.MessageAttributeType;
import com.neil.client.attr.MessageAttributeParsingException;
import com.neil.client.attr.Password;
import com.neil.client.attr.Username;
import com.neil.client.header.MessageHeader;
import com.neil.client.header.MessageHeaderParsingException;
import com.neil.client.header.MessageHeaderInterface.MessageHeaderType;
import com.neil.client.utils.Address;
import com.neil.client.utils.UtilityException;

/**
 * 与peer端建立通信
 * 
 * @author Neil
 *
 */
public class PCDClientPeerThread extends Thread {
	static final Logger LOGGER = LoggerFactory.getLogger(PCDClientPeerThread.class);
	private Address peerAddress;
	private int peerPort;
	private DatagramSocket clientSocket;

	public PCDClientPeerThread(Address address, int port, DatagramSocket socket) {
		peerAddress = address;
		peerPort = port;
		clientSocket = socket;
	}

	@Override
	public void run() {
		MessageHeader send2peerMH = new MessageHeader();
		try {
			send2peerMH.generateTransactionID();
			send2peerMH.setType(MessageHeaderType.BindingRequest);
			Username usrname = new Username("neil-send");
			Password password = new Password("pwd-send");
			send2peerMH.addMessageAttribute(usrname);
			send2peerMH.addMessageAttribute(password);
			byte[] data = send2peerMH.getBytes();
			
			DatagramPacket send2peer = new DatagramPacket(data, data.length);
			send2peer.setAddress(peerAddress.getInetAddress());
			send2peer.setPort(peerPort);
			clientSocket.send(send2peer);
			System.out.println("send!!!");
			LOGGER.debug(clientSocket.getInetAddress() + ":"
					+ clientSocket.getPort() + "send binding request to "
					+ send2peer.getAddress() + ":" + send2peer.getPort());
			
			// 发下一个请求
			clientSocket.send(send2peer);
			LOGGER.debug(clientSocket.getInetAddress() + ":"
					+ clientSocket.getPort() + "send binding request to "
					+ send2peer.getAddress() + ":" + send2peer.getPort());
			
			// 接收消息
			while (true) {
				DatagramPacket receive = new DatagramPacket(new byte[200], 200);
				clientSocket.receive(receive);
				try {
					MessageHeader recvMH = MessageHeader.parseHeader(receive.getData());
					recvMH.parseAttributes(receive.getData());
					Username un = (Username) recvMH.getMessageAttribute(MessageAttributeType.Username);
					Password pwd = (Password) recvMH.getMessageAttribute(MessageAttributeType.Password);
					System.out.println("username:"+un.getUsername()+", pwd："+pwd.getPassword());
				} catch (MessageHeaderParsingException | MessageAttributeParsingException e) {
					e.printStackTrace();
				}
			}
		} catch (UtilityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}