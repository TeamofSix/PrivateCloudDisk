package server;

import java.io.IOException;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UDPClientB {

	private static final int MAX_PACKET_SIZE = 1024;
	private static final String PATH = "F:\\Q.docx";
	private DatagramSocket client2;
	private SocketAddress targetDes;

	public UDPClientB(String ip,int port){
		Initial(ip, port);
	}

	public void Initial(String ip,int Bport){
		try{
			//向server发起请求
			SocketAddress target = new InetSocketAddress(ip, Bport);
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

			client2 = new DatagramSocket(myport);
			targetDes = new InetSocketAddress(host, Integer.parseInt(port));

			receiveSYN();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void main(String[] args){
		UDPClientB udpClientB = new UDPClientB("210.41.101.41",2008);
	}

	private void receiveSYN(){

		Header header = null;
		byte[] buf;
		DatagramPacket recvPacket = null;

		do {
			try {
				buf = new byte[MAX_PACKET_SIZE];
				recvPacket = new DatagramPacket(buf, buf.length);
				client2.receive(recvPacket);
				header = new Header(recvPacket.getData());
				if(header.getSynFlag() && !header.getAckFlag() && !header.getReqFlag()){
					break;
				}
			} catch (IOException e) {
				System.err.println(e.getMessage());
				System.exit(1);
			}
		}while(true);

		targetDes = new InetSocketAddress(recvPacket.getAddress(),recvPacket.getPort());

		Header ACKheader = new Header();
		ACKheader.setAckFlag(true);
		ACKheader.setSynFlag(true);
		DatagramPacket datagramPacket =
				new DatagramPacket(ACKheader.getData(),ACKheader.getData().length,targetDes);
		buf = new byte[MAX_PACKET_SIZE];
		recvPacket = new DatagramPacket(buf, buf.length);
		do {
			try {
				buf = new byte[MAX_PACKET_SIZE];
				recvPacket = new DatagramPacket(buf, buf.length);
				client2.send(datagramPacket);
				client2.receive(recvPacket);
				header = new Header(recvPacket.getData());
				if(header.getReqFlag() && !header.getSynFlag() && !header.getAckFlag()){
					break;
				}
			} catch (IOException e) {
				System.err.println(e.getMessage());
				System.exit(1);
			}
		}while(true);
		/**req ack and directory*/
		sentReq();

	}

	private void sentReq(){
		/**send ACK and REQ message which contains information about file updating to
		 * clientA.
		 *
		 * clientA will reply a message to tell files that clientB needs to synchronize
		 *
		 * then clientB needs to reply a ACK and REQ message
		 *
		 * call function to receive information
		 */
		Header header = new Header();
		header.setAckFlag(true);
		header.setReqFlag(true);
		DatagramPacket datagramPacket = new DatagramPacket(header.getData(), header.getData().length, targetDes);
		try {
			client2.send(datagramPacket);
		}catch (IOException e){
			System.err.println(e.getMessage());
			System.exit(1);
		}
		fileUpdate();
	}

	private byte[] fileToBytes(String path) throws IOException {
		Path p = Paths.get(PATH);
		return Files.readAllBytes(p);
	}

	private void fileUpdate(){

        String filename;
		Header header = null;
		byte[] file = new byte[100];
        byte[] buf = new byte[MAX_PACKET_SIZE];
		byte[] toSend = null;
        DatagramPacket recvPacket = new DatagramPacket(buf, buf.length);

		do{
			try{
				client2.receive(recvPacket);
				header = new Header(recvPacket.getData());
				if(!header.getAckFlag() && header.getReqFlag() && !header.getSynFlag()){
					break;
				}
			}catch (IOException e){
				System.err.println(e.getMessage());
				System.exit(1);
			}
		}while(true);

		for(int i = Header.HEADER_SIZE; recvPacket.getData()[i] != ' '; i ++){
			file[i - Header.HEADER_SIZE] = recvPacket.getData()[i];
		}

		if(file == null){
			System.out.println("nothing to be synchronized");
			System.exit(1);
		}else{
			/**find specific file to transmit*/
			byte[] data = null;
			filename = new String(file);
			try {
				data = fileToBytes(filename);
			    }catch (IOException e){
				System.err.println(e.getMessage());
				System.exit(1);
			}

			int numPacket = (int) Math.ceil(((double) data.length) /
					((double) MAX_PACKET_SIZE - Header.HEADER_SIZE));

			toSend = Header.createStatusPacket(true, numPacket, data.length);

			receiveData(toSend, numPacket, data.length, data);

		}
    }

    private boolean sendFilePacket(byte[] buf, int lastSendPcket) throws IOException{
		Header header;
		byte[] recv = new byte[MAX_PACKET_SIZE];
		DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length, targetDes);
		DatagramPacket recvPacket = new DatagramPacket(recv, recv.length);
		client2.send(datagramPacket);
		client2.receive(recvPacket);
		header = new Header(recvPacket.getData());
		if(header.getAckFlag() && header.getSequenceNum() == lastSendPcket + 2){
			return true;
		}
		return false;
	}

    private void receiveData(byte[] buf, int numPacket, int Bytes, byte[] data){

		byte[] recv = null;
		Header header = new Header();
		Header recvHeader = null;
		header.setAckFlag(true);
		DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length, targetDes);
		DatagramPacket recvPacket = null;

		do{
			try{
				client2.send(datagramPacket);
				recv = new byte[MAX_PACKET_SIZE];
				recvPacket = new DatagramPacket(recv, recv.length);
				client2.receive(recvPacket);
				recvHeader = new Header(recvPacket.getData());
				if(recvHeader.getAckFlag() && recvHeader.getSequenceNum() == 1){
					break;
				}
			}catch (IOException e){
				System.err.println(e.getMessage());
				System.exit(1);
			}
		}while(true);

		int lastSendPacket = 0;
		int MAX_DATA_SIZE = MAX_PACKET_SIZE - Header.HEADER_SIZE;
		int leftToSend = Bytes;

		do{
			Header sendHeader = new Header();
			sendHeader.setSequenceNum(lastSendPacket + 1);
			leftToSend = Bytes - lastSendPacket * MAX_DATA_SIZE;
			System.out.println(lastSendPacket);
			System.out.println(leftToSend);
			//剩余的数据一个包发不完
			if(leftToSend > MAX_DATA_SIZE){
				byte[] packetData = new byte[MAX_PACKET_SIZE];
				for (int k = Header.HEADER_SIZE; k < MAX_PACKET_SIZE; k++) {
					packetData[k] =
							data[(MAX_DATA_SIZE * lastSendPacket) + (k - Header.HEADER_SIZE)];
				}
				System.arraycopy(sendHeader.getData(), 0, packetData, 0,
						Header.HEADER_SIZE);
				do{
					try{
						if(sendFilePacket(packetData, lastSendPacket)){
							lastSendPacket ++;
							break;
						}
					}catch (IOException e){
						System.err.println(e.getMessage());
						System.exit(1);
					}
				}while(true);
			}else{
				byte[] packetData = new byte[leftToSend + Header.HEADER_SIZE];
				for (int k = Header.HEADER_SIZE; k < leftToSend; k++) {
					packetData[k] =
							data[(MAX_DATA_SIZE * lastSendPacket) + (k - Header.HEADER_SIZE)];
				}
				System.arraycopy(sendHeader.getData(), 0, packetData, 0,
						Header.HEADER_SIZE);
				do {
					try{
						if(sendFilePacket(packetData, lastSendPacket)){
							break;
						}
					}catch (IOException e){
						System.err.println(e.getMessage());
						System.exit(1);
					}
				}while (true);
				break;
			}

		}while(true);

	}

}
