import java.io.File;
import java.util.zip.CRC32;

// 文件夹和文件节点，用类的形式模仿结构体和链表
public class Node extends Object {
	String NodeName = ""; // 节点名
	String NodeParentName = ""; // 父节点名
	String NodePath = ""; // 节点相对路径
	String NodeAbsolutePath = ""; // 节点绝对路径
	String MD5 = null; // 节点为文件时，内容的MD5值
	long crc_32 = 0xFFFF; // 节点crc32值，为相对路径的CRC32值
	File[] fileArray = null; // 节点为文件夹时，子文件数组
	Node ParentNode = null;// 父节点
	boolean isDirectory;// 文件类型
	CRC32 crc32 = new CRC32();
	Node[] ChildNode = null;// 节点为文件夹时，子节点数组
	String Operation = "";// 操作类型：不变equal，增加add，替换replace，删除delete，修改文件名modify，创建文件夹mkdirs

	// 设置为文件夹节点
	public void setDirectoryNode(File f) {
		NodeName = f.getName();
		NodePath = NodeParentName + File.separator  + NodeName;
		NodeAbsolutePath = f.getAbsolutePath();
		fileArray = f.listFiles();
		ChildNode = new Node[fileArray.length];
		isDirectory = true;
		crc32.update(NodePath.getBytes());
		crc_32 = crc32.getValue();
	}

	// 设置为文件节点
	public void setFileNode(File f) {
		NodeName = f.getName();
		NodePath = NodeParentName + File.separator + NodeName;
		NodeAbsolutePath = f.getAbsolutePath();
		MD5 = Md5Util.MD5_API(NodeAbsolutePath);
		isDirectory = false;
		crc32.update(NodePath.getBytes());
		crc_32 = crc32.getValue();
	}
}