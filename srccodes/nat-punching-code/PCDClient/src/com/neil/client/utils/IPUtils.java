package com.neil.client.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * IP处理工具类
 * 
 * @author Neil
 *
 */
public class IPUtils {
	/**
	 * 获取当前IP
	 * 
	 * @return IP
	 */
	public static String getCurrentIP() {
		String[] ips = getIpArray(getLocalIPList());
		String curr_ip = "0.0.0.0";

		if (ips != null) {
			curr_ip = ips[0];
		} else {
			try {
				InetAddress net = InetAddress.getLocalHost();
				if (net != null) {
					curr_ip = net.getHostAddress();
				}
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}

		return curr_ip;
	}

	/**
	 * 获取本地IP列表
	 * 
	 * @return 返回本地IP列表
	 */
	public static List<String> getLocalIPList() {
		List<String> ipList = new ArrayList<String>();
		try {
			Enumeration<NetworkInterface> networkInterfaces = NetworkInterface
					.getNetworkInterfaces();
			NetworkInterface networkInterface;
			Enumeration<InetAddress> inetAddresses;
			InetAddress inetAddress;
			String ip;
			while (networkInterfaces.hasMoreElements()) {
				networkInterface = networkInterfaces.nextElement();
				inetAddresses = networkInterface.getInetAddresses();
				while (inetAddresses.hasMoreElements()) {
					inetAddress = inetAddresses.nextElement();
					if (inetAddress != null
							&& inetAddress instanceof Inet4Address) { // IPV4
						ip = inetAddress.getHostAddress();
						ipList.add(ip);
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		System.out.println(ipList);
		return ipList;
	}

	/**
	 * 将ip列表转换成数组
	 * 
	 * @param list
	 *            用List记录的ip列表
	 * @return 返回用数组形式的ip列表
	 */
	public static String[] getIpArray(List<String> list) {
		if (list == null) {
			return null;
		}
		String[] arr = new String[list.size()];
		int i = 0, j = 0;
		for (String s : list) {
			arr[i++] = s;
			if (s.matches("192\\.168\\.10*.\\d{1,3}")) {
				j = i - 1;
			}
		}
		String tem = arr[0];
		arr[0] = arr[j];
		arr[j] = tem;
		return arr;
	}

	/**
	 * 将ip从String类型转化为InetAddress类型
	 * 
	 * @param strIp String类型的ip地址
	 * @return 返回InetAddress类型的ip地址
	 */
	public static InetAddress stringToInetAddress(String strIp) {
		int[] intAddr = new int[4];
		int[] intAddrOK = new int[4];
		String[] strAddrOK = new String[4];
		byte[] byteAddrOK = new byte[4];
		InetAddress ia = null;
		// 将String用正则表达式进行分解
		String[] strArray = strIp.split("\\.");
		for (int i = 0; i < 4; i++) {
			intAddr[i] = Integer.valueOf(strArray[i]).intValue();
			if ((intAddr[i] <= 127) && (intAddr[i] >= -127))
				intAddrOK[i] = intAddr[i];
			else
				intAddrOK[i] = intAddr[i] - 256;
			// int 转换成 string
			strAddrOK[i] = String.valueOf(intAddrOK[i]);
			// string 转换到byte
			byteAddrOK[i] = Byte.parseByte(strAddrOK[i]);
		}
		try {
			ia = InetAddress.getByAddress(byteAddrOK);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return ia;
	}
}
