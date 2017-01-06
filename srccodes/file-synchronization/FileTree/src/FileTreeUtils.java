import java.io.File;
import java.util.HashMap;

public class FileTreeUtils {
	// API: ����HashMap
	public static HashMap<Object, Node> CreateHM(File rootFile) {
		return FileTree.FileHashMap_API(rootFile);
	}

	// API: ��HashMapת��Ϊbyte����
	public byte[] Encode(HashMap<Object, Node> HM) {
		return DataTypeConvertor.ObjectToByte(HM);
	}

	// API: ��byte����ת��ΪHashMap
	@SuppressWarnings("unchecked")
	public HashMap<Object, Node> Decode(byte[] bytes) {
		return (HashMap<Object, Node>) DataTypeConvertor.ByteToObject(bytes);
	}

	// API: �Ƚ�Ŀ¼
	public byte[] Compare(HashMap<Object, Node> HMsource, HashMap<Object, Node> HMdestination) {
		FileCompare fc = new FileCompare();
		fc.NodeCompare(FileTree.GetRootNode(FileTree.GetValue(HMsource)),
				FileTree.GetRootNode(FileTree.GetValue(HMdestination)));
		return DataTypeConvertor.ObjectToByte(fc.privateHM);
	}
	
	// API: ͬ������
	public void Synchronization(HashMap<Object, Node> HM){
		DataParsing.Parsing(HM);
	}
}
