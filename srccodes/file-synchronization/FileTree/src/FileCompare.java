import java.util.HashMap;

public class FileCompare {
	int FileCount = 0;
	HashMap<Object, Node> privateHM = new HashMap<Object, Node>();

	// �Ӹ��ڵ㿪ʼ�ݹ���������ڵ���бȽϣ�node1ΪԴ�ڵ㣬node2ΪĿ�Ľڵ㣬�ȽϽ�������Ϣ����privateHM
	public void NodeCompare(Node node1, Node node2) {
		if (node1.isDirectory == node2.isDirectory) {// �ļ�������ͬ
			if (node1.crc_32 == node2.crc_32) {// �ļ�����ͬ
				if (node1.isDirectory == true) { // �ļ������ͣ��Ƚ��ӽڵ�
					int i, j;
					for (i = 0; i < node1.ChildNode.length; i++) {
						boolean exit1 = false;
						for (j = 0; j < node2.ChildNode.length; j++) {
							if (node1.ChildNode[i].crc_32 == node2.ChildNode[j].crc_32
									&& node1.ChildNode[i].isDirectory == node2.ChildNode[j].isDirectory) {
								NodeCompare(node1.ChildNode[i], node2.ChildNode[j]);
								exit1 = true;
							} else if (node1.ChildNode[i].isDirectory == false
									&& node2.ChildNode[j].isDirectory == false
									&& node1.ChildNode[i].MD5.equals(node2.ChildNode[j].MD5)) {// �ļ�����ͬ���ļ�������ͬ
								node2.ChildNode[j].Operation = "modify";
								privateHM.put(node1.ChildNode[i].crc_32, node2.ChildNode[j]);
								exit1 = true;
							}
						}
						if (exit1 == false) {// node1���ڣ�node2������
							if (node1.ChildNode[i].isDirectory == true) {
								node1.ChildNode[i].Operation = "mkdirs";
							} else {
								node1.ChildNode[i].Operation = "add";
							}
							privateHM.put(node1.ChildNode[i].crc_32, node1.ChildNode[i]);
						}
					}
					for (i = 0; i < node2.ChildNode.length; i++) {
						boolean exit2 = false;
						for (j = 0; j < node1.ChildNode.length; j++) {
							if (node1.ChildNode[j].crc_32 == node2.ChildNode[i].crc_32
									&& node1.ChildNode[j].isDirectory == node2.ChildNode[i].isDirectory) {
								exit2 = true;
							}else if (node1.ChildNode[j].isDirectory == false
									&& node2.ChildNode[i].isDirectory == false
									&& node1.ChildNode[j].MD5.equals(node2.ChildNode[i].MD5)) {// �ļ�����ͬ���ļ�������ͬ
								node2.ChildNode[i].Operation = "modify";
								privateHM.put(node1.ChildNode[j].crc_32, node2.ChildNode[i]);
								exit2 = true;
							}
						}
						if (exit2 == false) {// node1�����ڣ�node2����
							node2.ChildNode[i].Operation = "delete";
							privateHM.put(node2.ChildNode[i].crc_32, node2.ChildNode[i]);
						}
					}
				} else { // �ļ�����
					if (node1.MD5.equals(node2.MD5)) { // �ļ�������ͬ
						node1.Operation = "equal";
					} else { // �ļ����ݲ�ͬ
						node1.Operation = "replace";
					}
					privateHM.put(node1.crc_32, node1);
				}
			}
		}
	}
}
