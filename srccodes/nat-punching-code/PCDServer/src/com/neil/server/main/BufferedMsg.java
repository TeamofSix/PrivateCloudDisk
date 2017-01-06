package com.neil.server.main;

import com.neil.server.attr.LocalAddress;
import com.neil.server.attr.MappedAddress;
import com.neil.server.attr.Password;
import com.neil.server.attr.Username;

/**
 * 服务器运行时，用于缓存连接信息的类
 * @author Neil
 *
 */
public class BufferedMsg {
	
	// 将username和password进行摘要，生成唯一的用于验证两个请求是否要配对的依据
	byte[] HMAC;
	
	MappedAddress mappedAddress;
	LocalAddress localAddress;
	byte[] transactionID;

	BufferedMsg(Username username,Password pwd, MappedAddress ma,LocalAddress la,byte[] transactionID){
		HMAC = (username.getUsername() + pwd.getPassword()).getBytes();
		this.transactionID = transactionID;
		mappedAddress = ma;
		localAddress = la;
	}
	
	public byte[] getHMAC() {
		return HMAC;
	}

	public void setHMAC(byte[] hMAC) {
		HMAC = hMAC;
	}

	public MappedAddress getMappedAddress() {
		return mappedAddress;
	}

	public void setMappedAddress(MappedAddress mappedAddress) {
		this.mappedAddress = mappedAddress;
	}

	public LocalAddress getLocalAddress() {
		return localAddress;
	}

	public void setLocalAddress(LocalAddress localAddress) {
		this.localAddress = localAddress;
	}

	public byte[] getTransactionID() {
		return transactionID;
	}

	public void setTransactionID(byte[] transactionID) {
		this.transactionID = transactionID;
	}
}