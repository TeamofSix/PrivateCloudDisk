package com.neil.client.main;

import com.neil.client.attr.ExchangedAddress;
import com.neil.client.attr.MappedAddress;
import com.neil.client.attr.MessageAttributeException;
import com.neil.client.attr.MessageAttributeInterface.MessageAttributeType;
import com.neil.client.attr.Password;
import com.neil.client.attr.Username;
import com.neil.client.header.MessageHeader;
import com.neil.client.header.MessageHeaderInterface.MessageHeaderType;
import com.neil.client.header.MessageHeaderParsingException;
import com.neil.client.utils.Address;
import com.neil.client.utils.UtilityException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * 与peer端建立通信
 *
 * @author Neil
 *
 */
public class PCDClientPeerThreadUdt extends Thread {
	static final Logger LOGGER = LoggerFactory
			.getLogger(PCDClientPeerThreadUdt.class);
	private Address peerAddress;
	private int peerPort;
	private Address mappedAddress;
	private int mappedPort;
	private DatagramSocket clientSocket;

	public PCDClientPeerThreadUdt(MappedAddress ma,ExchangedAddress ea,
								  DatagramSocket socket) {
		peerAddress = ea.getAddress();
		peerPort = ea.getPort();
		mappedAddress = ma.getAddress();
		mappedPort = ma.getPort();
		clientSocket = socket;
	}

	@Override
	public void run() {
		try {
            for (int i = 0; i < 5; i++) {
                // 发送消息
                MessageHeader messageHeader = new MessageHeader(MessageHeaderType.SharedSecretRequest);
                Username usrn = new Username("neil-pcd");
                Password pwd = new Password("pwd-pcd");
                ExchangedAddress ea = new ExchangedAddress();
                ea.setAddress(mappedAddress);
                ea.setPort(mappedPort);
                messageHeader.addMessageAttribute(usrn);
                messageHeader.addMessageAttribute(pwd);
                messageHeader.addMessageAttribute(ea);
                byte[] data = messageHeader.getBytes();
                DatagramPacket send = new DatagramPacket(data, data.length);
                send.setAddress(peerAddress.getInetAddress());
                send.setPort(peerPort);
                clientSocket.send(send);
                System.out.println("sending SharedSecretRequest to "+send.getAddress()+":"+send.getPort());

                try {
                    sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // 接收消息
                DatagramPacket receive = new DatagramPacket(new byte[200], 200);
                clientSocket.receive(receive);
                MessageHeader msgHeaderRecv = MessageHeader.parseHeader(receive.getData());
                if (msgHeaderRecv.getType().equals(MessageHeaderType.SharedSecretResponse)) {// 成功获取peer端的响应
                    msgHeaderRecv.parseAttributes(receive.getData());
                    ExchangedAddress earecv = (ExchangedAddress) msgHeaderRecv
                            .getMessageAttribute(MessageAttributeType.ExchangedAddress);
                    System.out.println("recved SharedSecretResponse from "+earecv.getAddress()+":"+earecv.getPort());
                    break;
                }
            }
		} catch (IOException | UtilityException | MessageAttributeException e) {
			e.printStackTrace();
		} catch (MessageHeaderParsingException e) {
			e.printStackTrace();
		}
		super.run();
	}
	// 127.0.0.1:8888 ben di
	public static void main(String[] args) {
		try {
			MappedAddress ma = new MappedAddress();
			ma.setAddress(new Address("127.0.0.1"));
			ma.setPort(8888);
			ExchangedAddress ea = new ExchangedAddress();
			ea.setAddress(new Address("127.0.0.1"));ea.setPort(7777);
			DatagramSocket socket = new DatagramSocket(7777);
			PCDClientPeerThreadUdt test = new PCDClientPeerThreadUdt(ma,ea,socket);
			test.run();
		} catch (UtilityException | MessageAttributeException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
}