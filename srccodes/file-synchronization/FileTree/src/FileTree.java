import java.io.*;
import java.util.HashMap;
import java.util.Map.Entry;

public class FileTree {
	// 生成HashMap，调用接口 FileHashMap_API(File rootFile)
	public static HashMap<Object, Node> FileHashMap_API(File rootFile) {
		// 采用散列图来存放信息，键为绝对路径的校验和，值为节点类的实例
		HashMap<Object, Node> HM = new HashMap<Object, Node>();
		Node rootNode = new Node();
		TraverseFile(rootFile, HM, rootNode);
		return HM;
	}

	// 递归遍历文件
	public static void TraverseFile(File f, HashMap<Object, Node> HM, Node node) {
		if (f != null) {
			if (f.isDirectory()) {
				// 创建文件夹节点
				node.setDirectoryNode(f);
				if (node.fileArray != null) {
					for (int i = 0; i < node.fileArray.length; i++) {
						// 递归调用
						node.ChildNode[i] = new Node();
						node.ChildNode[i].ParentNode = node;
						TraverseFile(node.fileArray[i], HM, node.ChildNode[i]);
					}
				}
			} else if (f.isFile()) {
				// 创建文件节点
				node.setFileNode(f);
			}
			HM.put(node.crc_32, node);
		}
	}

	// 取出HashMap的键值对，返回Node数组
	public static Node[] GetValue(HashMap<Object, Node> HM) {
		Node[] ArrayNode = new Node[HM.size()];
		java.util.Iterator<Entry<Object, Node>> iter = HM.entrySet().iterator();
		int count = 0;
		while (iter.hasNext()) {
			@SuppressWarnings("rawtypes")
			HashMap.Entry entry = (HashMap.Entry) iter.next();
			Object key = entry.getKey();
			Node val = (Node) entry.getValue();
			ArrayNode[count] = val;
			count ++;
		}
		return ArrayNode;
	}

	// 从Node数组中得到根节点
	public static Node GetRootNode(Node[] ArrayNode) {
		Node RootNode = new Node();
		int i;
		for (i = 0; i < ArrayNode.length; i++) {
			if (ArrayNode[i].ParentNode == null) {
				RootNode = ArrayNode[i];
			}
		}
		return RootNode;
	}
}
