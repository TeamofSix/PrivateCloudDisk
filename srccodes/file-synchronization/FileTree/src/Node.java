import java.io.File;
import java.util.zip.CRC32;

// �ļ��к��ļ��ڵ㣬�������ʽģ�½ṹ�������
public class Node extends Object{
	String NodeName = "";
	String NodePath = "";
	String MD5 = null;
	// ÿ���ڵ��Ψһ��ʶ����Ϊ����·����CRC32ֵ
	long crc_32 = 0xFFFF;
	File[] fileArray = null;
	Node ParentNode = null;
	boolean isDirectory;
	CRC32 crc32 = new CRC32();
	Node[] ChildNode = null;
	String Operation = "";// �������ͣ�����equal������add���滻replace��ɾ��delete���޸��ļ���modify�������ļ���mkdirs
	String testStr = "";
	
	// ����Ϊ�ļ��нڵ�
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

	// ����Ϊ�ļ��ڵ�
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