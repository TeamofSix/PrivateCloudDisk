import java.io.File;
import java.util.zip.CRC32;

// 文件夹和文件节点，用类的形式模仿结构体和链表
public class Node extends Object{
	String NodeName = "";
	String NodePath = "";
	String MD5 = null;
	// 每个节点的唯一标识符，为绝对路径的CRC32值
	long crc_32 = 0xFFFF;
	File[] fileArray = null;
	Node ParentNode = null;
	boolean isDirectory;
	CRC32 crc32 = new CRC32();
	Node[] ChildNode = null;
	String Operation = "";// 操作类型：不变equal，增加add，替换replace，删除delete，修改文件名modify，创建文件夹mkdirs
	String testStr = "";
	
	// 设置为文件夹节点
	public void setDirectoryNode(File f) {
		NodeName = f.getName();
		NodePath = f.getPath();
		String s = "res\\test1\\";
		String S = "res\\test2\\";
		int index;
		if(!((index=f.getPath().indexOf(s))<0)){
			testStr = f.getPath().substring(index+s.length()-1);
		}else if(!((index=f.getPath().indexOf(S))<0)){
			testStr = f.getPath().substring(index+s.length()-1);
		}
		crc32.update(testStr.getBytes());
		crc_32 = crc32.getValue();
		fileArray = f.listFiles();
		ChildNode = new Node[fileArray.length];
		isDirectory = true;
	}

	// 设置为文件节点
	public void setFileNode(File f) {
		NodeName = f.getName();
		NodePath = f.getPath();
		String s = "res\\test1\\";
		String S = "res\\test2\\";
		int index;
		if(!((index=f.getPath().indexOf(s))<0)){
			testStr = f.getPath().substring(index+s.length()-1);
		}else if(!((index=f.getPath().indexOf(S))<0)){
			testStr = f.getPath().substring(index+s.length()-1);
		}
		crc32.update(testStr.getBytes());
		crc_32 = crc32.getValue();
		MD5 = Md5Util.MD5_API(NodePath);
		isDirectory = false;
	}
}