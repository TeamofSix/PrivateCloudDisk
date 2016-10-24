package server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class UDPClientA {
	public static void main(String[] args) {
		try {
			// 向server发起请求
			SocketAddress target = new InetSocketAddress("115.159.34.11", 2008);
			DatagramSocket client = new DatagramSocket();
			String localIP = IPUtils.getCurrenIP();
			String message = "I am UDPClientA,"+localIP;
			byte[] sendbuf = message.getBytes();
			DatagramPacket pack = new DatagramPacket(sendbuf, sendbuf.length,
					target);
			client.send(pack);
			// 接收请求的回复，可能不是server的回复，可能来自ClientB的请求
			receive(client);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 接收请求内容
	private static void receive(DatagramSocket client) {
		try {
			for (;;) {
				byte[] buf = new byte[1024];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				client.receive(packet);
				String receiveMessage = new String(packet.getData(), 0,
						packet.getLength());
				System.out.println(receiveMessage);
				int port = packet.getPort();
				InetAddress address = packet.getAddress();
				String reportMessage = "tks";

				String[] params = receiveMessage.split(",");
				String Bhost = params[0].substring(5);
				String Bport = params[1].substring(5);

				// 获取接收到请求内容后并取到地址与端口，然后用获取到的地址与端口回复
				sendMessage(reportMessage, port, address, client);
				sendMessage(Bhost, Bport, client);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 回复内容
	private static void sendMessage(String reportMessage, int port,
			InetAddress address, DatagramSocket client) {
		try {
			// try{
			// // DatagramSocket server = new DatagramSocket(6666);
			// byte[] buf = new byte[1024];
			// DatagramPacket packet = new DatagramPacket(buf,buf.length);
			// for(;;){
			// client.receive(packet);
			// String receiveMessage = new
			// String(packet.getData(),0,packet.getLength());
			// System.out.println(receiveMessage);
			// }
			// }catch(Exception e){
			// e.printStackTrace();
			// }
			byte[] sendBuf = reportMessage.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendBuf,
					sendBuf.length, address, port);
			client.send(sendPacket);
			System.out.println("消息发送成功!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 向UDPClientA发起请求(在NAT上打孔)
	private static void sendMessage(String host, String port,
			DatagramSocket client) {
		try {
			SocketAddress target = new InetSocketAddress(host,
					Integer.parseInt(port));
//			for (;;) {
				String message = "I am clientA count test";
				byte[] sendbuf = message.getBytes();
				DatagramPacket pack = new DatagramPacket(sendbuf,
						sendbuf.length, target);
				System.out.println(pack);
				client.send(pack);
				// 接受UDPClientA的回复
				receive(client);
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
