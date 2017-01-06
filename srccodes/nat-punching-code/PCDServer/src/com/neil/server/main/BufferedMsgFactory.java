package com.neil.server.main;

import java.net.DatagramPacket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neil.server.attr.LocalAddress;
import com.neil.server.attr.MappedAddress;
import com.neil.server.attr.MessageAttributeException;
import com.neil.server.attr.MessageAttributeParsingException;
import com.neil.server.attr.Password;
import com.neil.server.attr.Username;
import com.neil.server.attr.MessageAttributeInterface.MessageAttributeType;
import com.neil.server.header.MessageHeader;
import com.neil.server.header.MessageHeaderParsingException;
import com.neil.server.header.MessageHeaderInterface.MessageHeaderType;
import com.neil.server.utils.Address;
import com.neil.server.utils.UtilityException;

/**
 * 服务器运行时，用于缓存连接信息的类
 * @author Neil
 *
 */
public class BufferedMsgFactory {
	static final Logger LOGGER = LoggerFactory.getLogger(PCDServer.class);
	
	public static BufferedMsg buildBufferedMsg(DatagramPacket recvpkt){
		BufferedMsg bufferedMsg = null;
		try {
			MessageHeader receiveMH = MessageHeader.parseHeader(recvpkt.getData());
			// 绑定请求报文
			if (receiveMH.getType() == MessageHeaderType.BindingRequest) {
				LOGGER.debug("Binding Request received from " + recvpkt.getAddress().getHostAddress() + ":" + recvpkt.getPort());
				receiveMH.parseAttributes(recvpkt.getData());
				// 解析来自PCDClient的数据
				byte[] id = receiveMH.getTransactionID();
				Username usrn = (Username) receiveMH.getMessageAttribute(MessageAttributeType.Username);
				Password pwd = (Password) receiveMH.getMessageAttribute(MessageAttributeType.Password);
				LocalAddress la = (LocalAddress) receiveMH.getMessageAttribute(MessageAttributeType.LocalAddress);
				MappedAddress ma = new MappedAddress();
				try {
					ma.setAddress(new Address(recvpkt.getAddress().getAddress()));
					ma.setPort(recvpkt.getPort());
				} catch (UtilityException e) {
					e.printStackTrace();
				} catch (MessageAttributeException e) {
					e.printStackTrace();
				}
				bufferedMsg = new BufferedMsg(usrn, pwd, ma, la,id);
			}
		} catch (MessageHeaderParsingException | MessageAttributeParsingException e) {
			e.printStackTrace();
		}
		return bufferedMsg;
	}
}