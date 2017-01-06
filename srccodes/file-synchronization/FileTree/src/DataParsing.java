import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map.Entry;

public class DataParsing {
	public static void Parsing(HashMap<Object, Node> HM) {
		java.util.Iterator<Entry<Object, Node>> iter = HM.entrySet().iterator();
		while (iter.hasNext()) {
			@SuppressWarnings("rawtypes")
			HashMap.Entry entry = (HashMap.Entry) iter.next();
			Object key = entry.getKey();
			Node val = (Node) entry.getValue();
			System.out.println("\nkey: "+ key +"\tval: " + val);
			System.out.println("Path: "+ val.NodePath +"\nOperation: " + val.Operation);
			// 按照解析出来的操作值进行选择性操作
			switch (val.Operation) {
			case "equal":
				break;
			case "add":
				//
				break;
//			case "replace":
//				String s;
//				File fileSource = (File) key;
//				File fileDestination = new File(val.NodePath);
//				InputStreamReader isr = null;
//				try {
//					isr = new InputStreamReader(new FileInputStream(fileSource), "GBK");
//				} catch (UnsupportedEncodingException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				} catch (FileNotFoundException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//				OutputStreamWriter osw = null;
//				try {
//					osw = new OutputStreamWriter(new FileOutputStream(fileDestination), "GBK");
//				} catch (UnsupportedEncodingException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				} catch (FileNotFoundException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//				try {
//					BufferedReader br = new BufferedReader(isr);
//					BufferedWriter bw = new BufferedWriter(osw);
//					s = br.readLine();
//					while (s != null) {
//						bw.write(s);
//						bw.newLine();
//						s = br.readLine();
//					}
//					bw.flush();
//					bw.close();
//					br.close();
//				} catch (FileNotFoundException e) {
//					e.printStackTrace();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				break;
			case "modify":
				String strTemp = val.NodeAbsolutePath.substring(0,val.NodeAbsolutePath.indexOf(val.NodePath))+key.toString();
				File file1 = new File(val.NodeAbsolutePath);
				File file2 = new File(strTemp);
				file1.renameTo(file2);
				break;
			case "delete":
				new File(val.NodeAbsolutePath).delete();
				break;
			case "makdirs":
				new File(val.NodePath).getParentFile().mkdirs();
				break;
			default:
				break;
			}
		}
	}
}
