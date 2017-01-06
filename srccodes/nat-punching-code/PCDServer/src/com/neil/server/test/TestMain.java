package com.neil.server.test;

import java.util.ArrayList;
import java.util.List;

import com.neil.server.attr.ExchangedAddress;
import com.neil.server.attr.LocalAddress;
import com.neil.server.attr.MappedAddress;
import com.neil.server.attr.MessageAttributeException;
import com.neil.server.attr.MessageAttributeInterface.MessageAttributeType;
import com.neil.server.attr.MessageAttributeParsingException;
import com.neil.server.attr.Password;
import com.neil.server.attr.Username;
import com.neil.server.header.MessageHeader;
import com.neil.server.header.MessageHeaderInterface.MessageHeaderType;
import com.neil.server.header.MessageHeaderParsingException;
import com.neil.server.utils.Address;
import com.neil.server.utils.UtilityException;

public class TestMain {
	public static void main(String[] args) {
		test1();
		test2();
		try {
			test3();
		} catch (UtilityException | MessageAttributeException e) {
			e.printStackTrace();
		}
	}

	private static void test2() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("Hello");
		list.add("Hello1");
		list.add("Hello2");
		list.add("Hello3");
		System.out.println(contains(list,"Hello2"));
	}

	private static void test1() {
		MessageHeader mh = new MessageHeader(MessageHeaderType.BindingRequest);
		try {
			mh.generateTransactionID();
			Username un = new Username("Neil");
			Password password = new Password("pwd123");
			LocalAddress la = new LocalAddress();
			la.setAddress(new Address("127.0.0.1"));
			la.setPort(7777);
			MappedAddress ma = new MappedAddress();
			ma.setAddress(new Address("127.0.0.1"));
			ma.setPort(7777);
			mh.addMessageAttribute(un);
			mh.addMessageAttribute(password);
			mh.addMessageAttribute(la);
			decoding(mh.getBytes());
		} catch (UtilityException e) {
			e.printStackTrace();
		} catch (MessageAttributeException e) {
			e.printStackTrace();
		}
	}

	static void decoding(byte[] buff) {
		try {
			MessageHeader de = MessageHeader.parseHeader(buff);
			System.out.println("type："+de.getType());
			de.parseAttributes(buff);
			Username username= (Username) de.getMessageAttribute(MessageAttributeType.Username);
			if(username!=null)System.out.println("username：|"+username.getUsername()+"|");
			Password password= (Password) de.getMessageAttribute(MessageAttributeType.Password);
			if(password!=null)System.out.println("password：|"+password.getPassword()+"|");
			LocalAddress la= (LocalAddress) de.getMessageAttribute(MessageAttributeType.LocalAddress);
			if(la!=null)System.out.println("localaddress：|"+la.getAddress()+":"+la.getPort()+"|");
			MappedAddress ma= (MappedAddress) de.getMessageAttribute(MessageAttributeType.MappedAddress);
			if(ma!=null)System.out.println("mappedAddress：|"+ma.getAddress()+":"+ma.getPort()+"|");
			ExchangedAddress ea= (ExchangedAddress) de.getMessageAttribute(MessageAttributeType.ExchangedAddress);
			if(ea!=null)System.out.println("ExchangedAddress：|"+ea.getAddress()+":"+ea.getPort()+"|"+ea.getLength());
		} catch (MessageHeaderParsingException e) {
			e.printStackTrace();
		} catch (MessageAttributeParsingException e) {
			e.printStackTrace();
		} catch (UtilityException e) {
			e.printStackTrace();
		}
	}
	
	private static boolean contains(List<String> msgList,String msg){
		for (String bufferedMsg : msgList) {
			if (bufferedMsg.getBytes().equals(msg.getBytes())) {
				return true;
			}
		}
		return false;
	}
	
	private static void test3() throws UtilityException, MessageAttributeException{
		// 配对成功的两个缓存消息对象中最先缓存的
//		BufferedMsg prebuffMsg = gets(linkMaps,postbuffMsg);
		
		MessageHeader sendMH2pre = new MessageHeader(MessageHeaderType.BindingResponse);
		sendMH2pre.generateTransactionID();
		MappedAddress mapre = new MappedAddress();
		mapre.setAddress(new Address("127.0.0.1"));
		mapre.setPort(1000);
		MappedAddress mapost = new MappedAddress();
		mapost.setAddress(new Address("127.0.0.2"));
		mapost.setPort(2000);
		// set MappedAddress of pre
		sendMH2pre.addMessageAttribute(mapre);
		// set ExchangedAddress of pre
		ExchangedAddress ea2pre = new ExchangedAddress();
		ea2pre.setPort(mapost.getPort());
		ea2pre.setAddress(mapost.getAddress());
		sendMH2pre.addMessageAttribute(ea2pre);
		byte[] data2pre = sendMH2pre.getBytes();
		decoding(data2pre);
	}		
}
