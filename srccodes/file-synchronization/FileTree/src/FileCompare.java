import java.util.HashMap;

public class FileCompare {
	int FileCount = 0;
	HashMap<Object, Node> privateHM = new HashMap<Object, Node>();

	// 从根节点开始递归遍历两个节点进行比较，node1为源节点，node2为目的节点，比较结束后将信息存入privateHM
	public void NodeCompare(Node node1, Node node2) {
		if (node1.isDirectory == node2.isDirectory) {// 文件类型相同
			if (node1.crc_32 == node2.crc_32) {// 文件名相同
				if (node1.isDirectory == true) { // 文件夹类型，比较子节点
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
									&& node1.ChildNode[i].MD5.equals(node2.ChildNode[j].MD5)) {// 文件名不同，文件内容相同
								node2.ChildNode[j].Operation = "modify";
								privateHM.put(node1.ChildNode[i].crc_32, node2.ChildNode[j]);
								exit1 = true;
							}
						}
						if (exit1 == false) {// node1存在，node2不存在
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
									&& node1.ChildNode[j].MD5.equals(node2.ChildNode[i].MD5)) {// 文件名不同，文件内容相同
								node2.ChildNode[i].Operation = "modify";
								privateHM.put(node1.ChildNode[j].crc_32, node2.ChildNode[i]);
								exit2 = true;
							}
						}
						if (exit2 == false) {// node1不存在，node2存在
							node2.ChildNode[i].Operation = "delete";
							privateHM.put(node2.ChildNode[i].crc_32, node2.ChildNode[i]);
						}
					}
				} else { // 文件类型
					if (node1.MD5.equals(node2.MD5)) { // 文件内容相同
						node1.Operation = "equal";
					} else { // 文件内容不同
						node1.Operation = "replace";
					}
					privateHM.put(node1.crc_32, node1);
				}
			}
		}
	}
}
