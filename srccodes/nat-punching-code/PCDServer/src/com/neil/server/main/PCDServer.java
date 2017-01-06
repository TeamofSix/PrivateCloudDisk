package com.neil.server.main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neil.server.attr.ExchangedAddress;
import com.neil.server.attr.LocalAddress;
import com.neil.server.attr.MappedAddress;
import com.neil.server.attr.MessageAttributeException;
import com.neil.server.header.MessageHeader;
import com.neil.server.header.MessageHeaderInterface.MessageHeaderType;
import com.neil.server.utils.Constant;
import com.neil.server.utils.Utility;
import com.neil.server.utils.UtilityException;

public class PCDServer {
	static final Logger LOGGER = LoggerFactory.getLogger(PCDServer.class);
	
	private DatagramSocket serverSocket;
	private Map<byte[],BufferedMsg> linkMaps;

	public PCDServer() throws SocketException {
		serverSocket = new DatagramSocket(Constant.SERVER_PORT);
	}

	public void start() throws SocketException {
		linkMaps = new HashMap<byte[],BufferedMsg>();
		serverSocket.setReceiveBufferSize(2000);
		ServerReceiverThread receiverThread = new ServerReceiverThread();
		receiverThread.start();
//		ServerReceiverThread srt = new ServerReceiverThread();
//		srt.start();
	}
	
	/**
	 * 服务器中用于接收请求的线程
	 * @author Neil
	 *
	 */
	class ServerReceiverThread extends Thread {
		@Override
		public void run() {
			while(true){
				// 接收报文
				DatagramPacket receive = new DatagramPacket(new byte[200], 200);
				try {
					serverSocket.receive(receive);
					handle_request(receive);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}

		private void handle_request(DatagramPacket receive){
			LOGGER.debug(serverSocket.getLocalAddress().getHostAddress() + ":" + serverSocket.getLocalPort() +
					" datagram received from " + receive.getAddress().getHostAddress() + ":" + receive.getPort());
			try{
				BufferedMsg buffMsg = BufferedMsgFactory.buildBufferedMsg(receive);
				// 若username、password计算得到的HMAC在缓存中存在，则说明已有IP交换对象，进行IP交换
				// 反之，则放入缓存中，继续等待
				BufferedMsg bufferedMsg = contains(linkMaps,buffMsg);
				if (bufferedMsg == null) {
					linkMaps.put(buffMsg.getHMAC(), buffMsg);
				}else{
					doExchange(buffMsg, bufferedMsg);
				}
			}catch(IOException e){
				e.printStackTrace();
			} catch (MessageAttributeException e) {
				e.printStackTrace();
			} catch (UtilityException e) {
				e.printStackTrace();
			} 
		}
		
		private BufferedMsg contains(Map<byte[],BufferedMsg> linkMaps,BufferedMsg msg){
			for (byte[] bufferedMsgHMAC : linkMaps.keySet()) {
				if (Utility.equalTwoByteArrays(bufferedMsgHMAC, msg.getHMAC())) {
					return linkMaps.get(bufferedMsgHMAC);
				}
			}
			return null;
		}
		
		/**
		 * 进行IP信息交换
		 * @param buffMsg1 配对成功的两个缓存消息对象中更迟缓存的
		 * @param buffMsg2 
		 * @throws MessageAttributeException
		 * @throws UtilityException
		 * @throws UnknownHostException
		 * @throws IOException
		 */
		private void doExchange(BufferedMsg buffMsg1, BufferedMsg buffMsg2)
				throws MessageAttributeException, UtilityException,
				UnknownHostException, IOException {
			ExchangedAddress ea1 = new ExchangedAddress();
			ExchangedAddress ea2 = new ExchangedAddress();
			MappedAddress ma1 = buffMsg1.getMappedAddress();
			MappedAddress ma2 = buffMsg2.getMappedAddress();
			LocalAddress la1 = buffMsg1.getLocalAddress();
			LocalAddress la2 = buffMsg2.getLocalAddress();
			byte[] id1 = buffMsg1.getTransactionID();
			byte[] id2 = buffMsg2.getTransactionID();
			
			if(ma1.getAddress().equals(ma2.getAddress())){
				ea1.setAddress(la2.getAddress());
				ea1.setPort(la2.getPort());
				ea2.setAddress(la1.getAddress());
				ea2.setPort(la1.getPort());
			}else{
				ea1.setAddress(ma2.getAddress());
				ea1.setPort(ma2.getPort());
				ea2.setAddress(ma1.getAddress());
				ea2.setPort(ma1.getPort());
			}

			sendBindingResponse(id1,ma1,ea1);
			sendBindingResponse(id2,ma2,ea2);
		}
		
		public void sendBindingResponse(byte id[],MappedAddress ma,ExchangedAddress ea) {
			try{
				MessageHeader sendMHto1 = new MessageHeader(MessageHeaderType.BindingResponse);	
				sendMHto1.setTransactionID(id);
				sendMHto1.addMessageAttribute(ma);
				sendMHto1.addMessageAttribute(ea);
				byte[] data1 = sendMHto1.getBytes();
				// 构建并发送报文给缓存的Client
				DatagramPacket pkt1 = new DatagramPacket(data1,data1.length);
				pkt1.setAddress(ma.getAddress().getInetAddress());
				pkt1.setPort(ma.getPort());
				serverSocket.send(pkt1);
				LOGGER.debug(serverSocket.getLocalAddress().getHostAddress() + ":" + serverSocket.getLocalPort() 
						+ " send Binding Response to " + pkt1.getAddress().getHostAddress() + ":" + pkt1.getPort());
			}catch(IOException | UtilityException e){
				e.printStackTrace();
			}
		}
	}
}
