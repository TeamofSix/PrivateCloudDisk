package com.neil.client.callback;

import com.neil.client.attr.ExchangedAddress;
import com.neil.client.attr.MappedAddress;

/**
 * 用于客户端接收到来自服务器发送的对等端IP信息后进行的回调
 * @author Neil
 *
 */
public interface ReceivedPeerIPInfoCallback {
	void onReceivedPeerIPInfo(MappedAddress ma,ExchangedAddress ea);
}
