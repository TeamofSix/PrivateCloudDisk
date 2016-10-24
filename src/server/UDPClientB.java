package server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class UDPClientB {
	public static void main(String[] args){
		try{
			//向server发起请求
			SocketAddress target = new InetSocketAddress("115.159.34.11",2008);
			System.out.println(target);
			DatagramSocket client = new DatagramSocket();
			String localIP = IPUtils.getCurrenIP();
			String message = "I am UDPClientB,"+localIP;
			byte[] sendbuf = message.getBytes();
			DatagramPacket pack = new DatagramPacket(sendbuf,sendbuf.length,target);
			client.send(pack);
			//接受server的回复内容
			byte[] buf = new byte[1024];
			DatagramPacket recpack = new DatagramPacket(buf,buf.length);
			client.receive(recpack);
			//处理server回复的内容，向内容中的地址与端口发起请求（打洞）
			String receiveMessage = new String(recpack.getData(),0,recpack.getLength());
			String[] params = receiveMessage.split(",");
			String host = params[0].substring(5);
			String port = params[1].substring(5);
			System.out.println(host + ":" + port);
			int myport = client.getLocalPort();
			client.close();
			DatagramSocket client2 = new DatagramSocket(myport);
			sendMessage(host,port,client2);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//向UDPClientA发起请求(在NAT上打孔)
	private static void sendMessage(String host,String port,DatagramSocket client){
		try{
			SocketAddress target = new InetSocketAddress(host,Integer.parseInt(port));
			System.out.println(target);
			for(;;){
				//String message = "I am master 192.168.85.129 count test";
				String message = "first time I am Client B" + host + port;
				byte[] sendbuf = message.getBytes();
				DatagramPacket pack = new DatagramPacket(sendbuf,sendbuf.length,target);
				client.send(pack);
				//接受UDPClientA的回复
				receive(client);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	//收到ClientA的回复内容，穿透完成
	private static void receive(DatagramSocket client) {
		try{
			for(;;){
				byte[] buf = new byte[1024];
				DatagramPacket recpack = new DatagramPacket(buf, buf.length);
				client.receive(recpack);
				String receiveMessage = new String(recpack.getData(),0,recpack.getLength());
				System.out.println("receive A:" + receiveMessage);
				
				//记得重新收地址与端口，然后在以新地址发送内容到ClientA
				int port = recpack.getPort();
				InetAddress address = recpack.getAddress();
				//String reportMessage = "I am master 192.168.85.129 count test";
				String reportMessage = "second time I am Client B" + address + port;
				//发送消息
				sendMessage(reportMessage,port,address,client);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private static void sendMessage(String reportMessage, int port,
			InetAddress address, DatagramSocket client) {
		try{
			byte[] sendBuf = reportMessage.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length,address,port);
			client.send(sendPacket);
			System.out.println("send success");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
