import java.io.File;
import java.util.zip.CRC32;

// �ļ��к��ļ��ڵ㣬�������ʽģ�½ṹ�������
public class Node extends Object {
	String NodeName = ""; // �ڵ���
	String NodeParentName = ""; // ���ڵ���
	String NodePath = ""; // �ڵ����·��
	String NodeAbsolutePath = ""; // �ڵ����·��
	String MD5 = null; // �ڵ�Ϊ�ļ�ʱ�����ݵ�MD5ֵ
	long crc_32 = 0xFFFF; // �ڵ�crc32ֵ��Ϊ���·����CRC32ֵ
	File[] fileArray = null; // �ڵ�Ϊ�ļ���ʱ�����ļ�����
	Node ParentNode = null;// ���ڵ�
	boolean isDirectory;// �ļ�����
	CRC32 crc32 = new CRC32();
	Node[] ChildNode = null;// �ڵ�Ϊ�ļ���ʱ���ӽڵ�����
	String Operation = "";// �������ͣ�����equal������add���滻replace��ɾ��delete���޸��ļ���modify�������ļ���mkdirs

	// ����Ϊ�ļ��нڵ�
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

	// ����Ϊ�ļ��ڵ�
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