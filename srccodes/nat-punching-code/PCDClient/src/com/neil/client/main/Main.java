package com.neil.client.main;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

import com.neil.client.main.PCDClient;

public class Main {
	// 参数长度
	final static int LEN_OF_ARGS = 2;

	public static void main(String[] args) {
		
		// 判断参数的合法性
		if (args.length < LEN_OF_ARGS) {
			System.out.println("usage: java com.neil.client.PCDClient PORT IP");
			System.out.println();
			System.out.println(" PORT - the port that should be used by the CLIENT");
			System.out.println("   IP - the ip address that should be used by the SERVER");
			System.exit(0);
		}
		
		try {
			// 构建Log信息
			Handler fh = new FileHandler("logging_client"+Integer.parseInt(args[0])+".txt");
			fh.setFormatter(new SimpleFormatter());
			java.util.logging.Logger.getLogger("com.neil.client").addHandler(fh);
			java.util.logging.Logger.getLogger("com.neil.client").setLevel(Level.ALL);

			System.out.println("IP：" + InetAddress.getByName(args[1]) + ",port：" + Integer.parseInt(args[0]));
			PCDClient pcdclient = new PCDClient(Integer.parseInt(args[0]), InetAddress.getByName(args[1]));
			pcdclient.start();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
