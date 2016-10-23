package server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * 服务器
 * @author Neil
 *
 */
public class UDPServer {
	public static void main(String[] args) {
		try {
			DatagramSocket server = new DatagramSocket(2008);
			
			byte[] buf = new byte[1024];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			
			String clientAaddrportInfo = "";
			String clientBaddrportInfo = "";
			
			int portA = 0;
			int portB = 0;
			
			InetAddress addressA = null;
			InetAddress addressB = null;
			
			for (;;) {
				server.receive(packet);

				String receiveMessage = new String(packet.getData(), 0,
						packet.getLength());
				
				System.out.println(receiveMessage);

				// 接收到clientA
				if (receiveMessage.contains("UDPClientA")) {
					portA = packet.getPort();
					addressA = packet.getAddress();
					clientAaddrportInfo = "host:" + addressA.getHostAddress()
							+ ",port:" + portA;
					System.out.println("Client A:"+clientAaddrportInfo);
				}

				// 接收到clientB
				if (receiveMessage.contains("UDPClientB")) {
					portB = packet.getPort();
					addressB = packet.getAddress();
					clientBaddrportInfo = "host:" + addressB.getHostAddress()
							+ ",port:" + portB;
					System.out.println("Client B:"+clientBaddrportInfo);
				}

				// 两个都接收到后分别A、B地址交换互发
				if (!clientAaddrportInfo.equals("") && !clientBaddrportInfo.equals("")) {
					send2A(clientBaddrportInfo, portA, addressA, server);
					send2B(clientAaddrportInfo, portB, addressB, server);
					clientAaddrportInfo = "";
					clientBaddrportInfo = "";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void send2B(String sendMessage132, int port132,
			InetAddress address132, DatagramSocket server) {
		try {
			byte[] sendBuf = sendMessage132.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendBuf,
					sendBuf.length, address132, port132);
			server.send(sendPacket);
			System.out.println("消息发送成功");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void send2A(String sendMessage129, int port129,
			InetAddress address129, DatagramSocket server) {
		try {
			byte[] sendBuf = sendMessage129.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendBuf,
					sendBuf.length, address129, port129);
			server.send(sendPacket);
			System.out.println("消息发送成功");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
