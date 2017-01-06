package com.neil.server.main;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

import com.neil.server.utils.Constant;

/**
 * This is the entry of the server program.
 * 
 * @author Neil
 *
 */
public class Main {
//	private static final int ARGS_LEN = 2;

	public static void main(String[] args) {
		try{
//			if (args.length != ARGS_LEN) {
//				System.out.println("usage: java com.neil.server.PCDServer PORT IP");
//				System.out.println();
//				System.out.println(" PORT - the port that should be used by the SERVER");
//				System.out.println("   IP - the ip address that should be used by the SERVER");
//				System.exit(0);
//			}
			Handler fh = new FileHandler("logging_server"+Constant.SERVER_PORT+".txt");
			fh.setFormatter(new SimpleFormatter());
			java.util.logging.Logger.getLogger("com.neil.server").addHandler(fh);
			java.util.logging.Logger.getLogger("com.neil.server").setLevel(Level.ALL);
			
			PCDServer pcdserver = new PCDServer();
			pcdserver.start();
		}catch(IOException e){
			e.printStackTrace();
		}
	}

}
