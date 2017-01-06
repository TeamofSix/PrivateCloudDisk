import java.io.File;
import java.util.HashMap;

public class FileTreeUtils {
	// API: 生成HashMap
	public static HashMap<Object, Node> CreateHM(File rootFile) {
		return FileTree.FileHashMap_API(rootFile);
	}

	// API: 将HashMap转换为byte数组
	public byte[] Encode(HashMap<Object, Node> HM) {
		return DataTypeConvertor.ObjectToByte(HM);
	}

	// API: 将byte数组转换为HashMap
	@SuppressWarnings("unchecked")
	public HashMap<Object, Node> Decode(byte[] bytes) {
		return (HashMap<Object, Node>) DataTypeConvertor.ByteToObject(bytes);
	}

	// API: 比较目录
	public byte[] Compare(HashMap<Object, Node> HMsource, HashMap<Object, Node> HMdestination) {
		FileCompare fc = new FileCompare();
		fc.NodeCompare(FileTree.GetRootNode(FileTree.GetValue(HMsource)),
				FileTree.GetRootNode(FileTree.GetValue(HMdestination)));
		return DataTypeConvertor.ObjectToByte(fc.privateHM);
	}
	
	// API: 同步操作
	public void Synchronization(HashMap<Object, Node> HM){
		DataParsing.Parsing(HM);
	}
}
