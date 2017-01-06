package com.neil.client.main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.neil.client.attr.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neil.client.attr.MessageAttributeInterface.MessageAttributeType;
import com.neil.client.callback.ReceivedPeerIPInfoCallback;
import com.neil.client.header.MessageHeader;
import com.neil.client.header.MessageHeaderParsingException;
import com.neil.client.header.MessageHeaderInterface.MessageHeaderType;
import com.neil.client.utils.Address;
import com.neil.client.utils.Constant;
import com.neil.client.utils.UtilityException;

/**
 * 与 server端建立通信
 * @author Neil
 *
 */
public class PCDClientReceiverThread extends Thread {
	private DatagramSocket clientSocket;
	static final Logger LOGGER = LoggerFactory.getLogger(PCDClientReceiverThread.class);
	private int serverPort;
	private InetAddress serverInetAddress;
	private ReceivedPeerIPInfoCallback callback;
	
	PCDClientReceiverThread(DatagramSocket socket,InetAddress serverInetAddress, ReceivedPeerIPInfoCallback callback) {
		clientSocket = socket;
		this.callback = callback;
		this.serverPort = Constant.SERVER_PORT;
		this.serverInetAddress = serverInetAddress;
	}

	@Override
	public void run() {
		try {
			// 构建向服务器发送的消息
			MessageHeader sendMH2Server = new MessageHeader();
			sendMH2Server.generateTransactionID();
			sendMH2Server.setType(MessageHeaderType.BindingRequest);
			// set LocalAddress
			LocalAddress la = new LocalAddress();
			la.setAddress(new Address("127.0.0.1"));//IPUtils.getCurrentIP()
			la.setPort(clientSocket.getLocalPort());
			sendMH2Server.addMessageAttribute(la);
			// set Username
			Username un = new Username();
			un.setUsername("neil");
			sendMH2Server.addMessageAttribute(un);
			// set Password
			Password pwd = new Password();
			pwd.setPassword("pwd");
			sendMH2Server.addMessageAttribute(pwd);
			
			// 向服务器发送请求
			byte[] buf = sendMH2Server.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(buf, buf.length);
			sendPacket.setPort(serverPort);
			sendPacket.setAddress(serverInetAddress);
			clientSocket.send(sendPacket);
			LOGGER.debug(clientSocket.getInetAddress() + ":"
					+ clientSocket.getPort() + "send binding request to "
					+ sendPacket.getAddress() + ":" + sendPacket.getPort());
			// 接收消息
			while(true){
				DatagramPacket receive = new DatagramPacket(new byte[200], 200);
				clientSocket.receive(receive);
				try {
					MessageHeader recvMH = MessageHeader.parseHeader(receive.getData());
					recvMH.parseAttributes(receive.getData());
					MappedAddress ma = (MappedAddress) recvMH.getMessageAttribute(MessageAttributeType.MappedAddress);
					ExchangedAddress ea = (ExchangedAddress) recvMH.getMessageAttribute(MessageAttributeType.ExchangedAddress);
					if (ma != null) {
						// 接收到Peer端的IP信息后发生的回调
						callback.onReceivedPeerIPInfo(ma,ea);
						break;
					}
				} catch (MessageHeaderParsingException e) {
					e.printStackTrace();
				}
			}
		} catch (UtilityException e) {
			e.printStackTrace();
		} catch (MessageAttributeException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}