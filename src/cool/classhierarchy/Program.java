package cool.classhierarchy;

import java.util.ArrayList;
import java.util.List;

public class Program {
	public List<ClassType> classList;
	
	public Program() {
		classList = new ArrayList<>();
	}
	
	public void printContents(int indent) {
		System.out.println("program");
		for (ClassType cls : classList) {
			cls.printContents(indent + 2);
		}
	}
}
