package server;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.*;

public class UDPClientA {

	private static  final int TIMEOUT = 5000;
	private static final int MAX_PACKET_SIZE = 1024;
	private static final int MAX_RETRY_TIMES = 3;
	private DatagramSocket client;
	private SocketAddress targetDes;
	String filename = "Q.txt";

	public UDPClientA(String ip,int port){
		Initial(ip, port);
	}

	public void Initial(String ip,int port){
		try{
			SocketAddress target = new InetSocketAddress(ip, port);
			client = new DatagramSocket();

			String localIP = IPUtils.getCurrenIP();
			String message = "I am UDPClientA,"+localIP;

			System.out.println(message);
			byte[] sendbuf = message.getBytes();
			DatagramPacket pack = new DatagramPacket(sendbuf, sendbuf.length,
					target);
			client.send(pack);
			receive();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		UDPClientA udpClientA = null;
		udpClientA = new UDPClientA("210.41.101.41", 2008);

	}

	// 接收请求内容
	private void receive() {
		try {
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
				System.out.println(Bhost+":"+Bport);
				// 获取接收到请求内容后并取到地址与端口，然后用获取到的地址与端口回复
				sendMessageToServer(reportMessage, port, address);
			    targetDes = new InetSocketAddress(Bhost, Integer.parseInt(Bport));
				sendCheckMessage();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**reply server*/
	private void sendMessageToServer(String reportMessage, int port,
			InetAddress address) {
		try {
			byte[] sendBuf = reportMessage.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendBuf,
					sendBuf.length, address, port);
			client.send(sendPacket);
			System.out.println("消息发送成功!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**to check the connection with ClientB*/
	private void sendCheckMessage() {

		boolean flag = false;
		int retryTimes = 0;
		Header SYNheader;
		Header header;
		byte[] buf = new byte[MAX_PACKET_SIZE];
		DatagramPacket datagramPacket = new DatagramPacket(buf,buf.length);

	    try {
			/*the method receive() will be blocked for 5000 milliseconds*/
			client.setSoTimeout(TIMEOUT);
		}catch (SocketException sc){
			System.err.println(sc.getMessage());
			System.exit(1);
		}

		SYNheader = new Header();
		SYNheader.setSynFlag(true);

		DatagramPacket pack = new DatagramPacket(SYNheader.getData(),
				SYNheader.getData().length, targetDes);

        do {
			try{
				client.send(pack);
				client.receive(datagramPacket);
				if(datagramPacket != null){
					header = new Header(datagramPacket.getData());
					if(header.getAckFlag() && header.getSynFlag() && !header.getReqFlag()){
						flag = true;
					}else{
						retryTimes ++;
					}
				}else{
					retryTimes ++;
				}
			}catch (SocketTimeoutException sto){
				retryTimes ++;
				continue;
			}catch (IOException e){
				System.err.println(e.getMessage());
				System.exit(0);
			}
		}while(retryTimes < MAX_RETRY_TIMES && flag == false);

		if(flag){
			sendRequestMessage();
		}else{
			System.out.println("Can not connect to ClientB");
			System.exit(1);
		}
	}

    /**send REQ request to clientB.Expect filename(or anything can tell changes of
	 * files) from clientB*/
	private void sendRequestMessage(){

		boolean flag = false;
		int retryTimes = 0;
		Header REQheader = new Header();
		Header header;
		byte[] buf = new byte[MAX_PACKET_SIZE];
		byte[] data = new byte[MAX_PACKET_SIZE];
		REQheader.setReqFlag(true);

		DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
		DatagramPacket pack = new DatagramPacket(REQheader.getData(),
				REQheader.getData().length, targetDes);

		do {
			try{
				client.send(pack);
				client.receive(datagramPacket);
				if(datagramPacket != null){
					header = new Header(datagramPacket.getData());
					data = header.getData();
					if(header.getAckFlag() && !header.getSynFlag() && header.getReqFlag()){
						flag = true;
					}else{
						retryTimes ++;
					}
				}else{
					retryTimes ++;
				}
			}catch (SocketTimeoutException sto){
				retryTimes ++;
				continue;
			}catch (IOException e){
				System.err.println(e.getMessage());
				System.exit(1);
			}
		}while(retryTimes < MAX_RETRY_TIMES && flag == false );

		if(flag){
			/**调用函数分析两个客户端各自需要从对方那里同步哪些文件
			 * 分析完成后
			 *
			 * 将clientB需要同步的文件的名称（或其他信息）发送给clientB
			 * clientB回复ACK REQ
			 *
			 * 收到clientB的确认报文后，clientA应该调用fileReq()，对需要同步的文件逐个进行同步
			 *
			 * 前面有个问题，如果文件的同步信息一个包装不下的处理*/
			fileReq();

		}else{
			System.out.println("Error occurred in sendRequestMessage");
			System.exit(1);
		}


	}

	/**simple realization of fileReq*/
	private void fileReq(){

		boolean flag = false;
		int retryTimes = 0;
		Header header = new Header();
		Header recvHeader = null;
		header.setReqFlag(true);
		String name = filename;
		byte[] file = name.getBytes();
		byte[] buf = new byte[MAX_PACKET_SIZE];
		byte[] recvBuf = new byte[MAX_PACKET_SIZE];
		DatagramPacket datagramPacket;
		DatagramPacket recvPacket = new DatagramPacket(recvBuf, recvBuf.length);

		for(int i = 0; i < 5; i++){
			buf[i] = header.getData()[i];
		}
		for(int i = 0; i < file.length; i ++){
			buf[Header.HEADER_SIZE + i] = file[i];
		}
		buf[Header.HEADER_SIZE + file.length ] = ' ';

		datagramPacket = new DatagramPacket(buf,buf.length,targetDes);

		do {
			try {
				client.send(datagramPacket);
				client.receive(recvPacket);
				if(recvPacket != null){
					recvHeader = new Header(recvPacket.getData());
					if(recvHeader.getAckFlag() && !recvHeader.getSynFlag() && !recvHeader.getReqFlag()){
						flag = true;
					}else{
						retryTimes ++;
					}
				}else{
					retryTimes ++;
				}
			} catch (SocketTimeoutException se) {
				retryTimes++;
			} catch (IOException e) {
				System.err.println(e.getMessage());
				System.exit(1);
			}

		}while(retryTimes < MAX_RETRY_TIMES && flag == false);

		if(flag){
			statusPacketAnalysis(recvHeader.getData());
		}else{
			System.out.println("Error occurred in fileReq");
			System.exit(1);
		}
	}

	/**to analyze the statusPacket*/
	private void statusPacketAnalysis(byte[] data) {

		int numPackets, numBytes;
		if (data[Header.HEADER_SIZE] == (byte) (0 << 7)) {
			System.out.println("error occurred in finding specific file");
			System.exit(1);
		} else {
			numPackets = (int) ((data[Header.HEADER_SIZE + 1] & 0xFF) << 24 |
					(data[Header.HEADER_SIZE + 2] & 0xFF) << 16 |
					(data[Header.HEADER_SIZE + 3] & 0xFF) << 8 |
					(data[Header.HEADER_SIZE + 4] & 0xFF));

			numBytes = (int) ((data[Header.HEADER_SIZE + 5] & 0xFF) << 24 |
					(data[Header.HEADER_SIZE + 6] & 0xFF) << 16 |
					(data[Header.HEADER_SIZE + 7] & 0xFF) << 8 |
					(data[Header.HEADER_SIZE + 8] & 0xFF));
			try {
				acceptFile(numPackets, numBytes);
			} catch (IOException e) {
				System.err.println(e.getMessage());
				System.exit(1);
			}

		}
	}

	/**accept specific file from clientB*/
	private void acceptFile(int numPackets, int numBytes) throws IOException {

		String path = "F:\\test\\hahaha";
		File file = new File(path);
		file.createNewFile();

		FileOutputStream fos = new FileOutputStream(path, true);

		boolean flag = false;
		int retryTimes = 0;
		int lastReceivedPacket = 0;
		int lastReceivedByte = 0;
		Header header = new Header();
		Header recvHeader;
		byte[] buf = new byte[MAX_PACKET_SIZE];
		header.setAckFlag(true);
		header.setSequenceNum(1);
		DatagramPacket datagramPacket =
				new DatagramPacket(header.getData(),header.getData().length,targetDes);
		DatagramPacket recvDatagramPacket = new DatagramPacket(buf,buf.length);

		do{
			try{
				client.send(datagramPacket);
				client.receive(recvDatagramPacket);
				if(recvDatagramPacket != null){
                    recvHeader = new Header(recvDatagramPacket.getData());
					/*the first packet from clientB*/
					if(recvHeader.getSequenceNum() == 1){
						flag = true;
						break;
					}else{
						retryTimes ++;
					}
				}else{
					retryTimes ++;
				}
			}catch (SocketTimeoutException sc){
				retryTimes ++;
			}catch (IOException e){
				System.err.println(e.getMessage());
				System.exit(1);
			}
		}while(retryTimes < MAX_RETRY_TIMES);

		if(flag){
			lastReceivedPacket = 1;
			byte[] bytes = recvDatagramPacket.getData();
			if(numPackets == 1){
				fos.write(bytes,Header.HEADER_SIZE,numBytes);
			}else{
				fos.write(bytes,Header.HEADER_SIZE,MAX_PACKET_SIZE - Header.HEADER_SIZE);
				lastReceivedByte += MAX_PACKET_SIZE - Header.HEADER_SIZE;
			}

			while(lastReceivedPacket != numPackets){

				flag = false;
				retryTimes = 0;
				buf = null;
				buf = new byte[MAX_PACKET_SIZE];
				recvDatagramPacket = new DatagramPacket(buf,buf.length);

				do{
					try{
						header.setSequenceNum(lastReceivedPacket + 1);
						datagramPacket =
								new DatagramPacket(header.getData(),header.getData().length,targetDes);
						client.send(datagramPacket);
						client.receive(recvDatagramPacket);
						if(recvDatagramPacket != null){
							recvHeader = new Header(recvDatagramPacket.getData());
							if(recvHeader.getSequenceNum() == lastReceivedPacket + 1){
								flag = true;
								break;
							}else{
								retryTimes ++;
							}
						}else{
							retryTimes ++;
						}
					}catch (SocketTimeoutException se){
						retryTimes ++;
					}catch (IOException e){
						System.err.println(e.getMessage());
						System.exit(1);
					}
				}while (retryTimes < MAX_RETRY_TIMES);

				if(flag){
					lastReceivedPacket ++;
					bytes = null;
					bytes = recvDatagramPacket.getData();
					if(lastReceivedPacket == numPackets){
						fos.write(bytes, Header.HEADER_SIZE, numBytes - lastReceivedByte);
					}else{
						fos.write(bytes, Header.HEADER_SIZE, MAX_PACKET_SIZE - Header.HEADER_SIZE);
						lastReceivedByte = lastReceivedByte + MAX_PACKET_SIZE - Header.HEADER_SIZE;
					}
				}else {
					System.out.println("error occurred in acceptFile");
					System.exit(1);
				}
			}
		}else{
			System.out.println("error occurred in acceptFiles.");
			System.exit(1);
		}
	}
}
