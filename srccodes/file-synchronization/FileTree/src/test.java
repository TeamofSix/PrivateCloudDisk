import java.io.File;

public class test {
	public static void Print(Node rootNode) {
		int i;
		if (rootNode.isDirectory == false) {
			
		} else {
			for (i = 0; i < rootNode.ChildNode.length; i++) {
				Print(rootNode.ChildNode[i]);
			}
		}
	}

	public static void main(String[] args) {
		FileCompare fc = new FileCompare();
		fc.NodeCompare(FileTree.GetRootNode(FileTree.GetValue(FileTreeUtils.CreateHM(new File("res/test1")))),
				FileTree.GetRootNode(FileTree.GetValue(FileTreeUtils.CreateHM(new File("res/test2")))));
		DataParsing.Parsing(fc.privateHM);
	}
}
