package cool.classhierarchy;

import java.util.ArrayList;
import java.util.List;

public class Block extends Expression {
	public List<Expression> expList;
	String keyword = "block";
	
	public Block() {
		expList = new ArrayList<>();
	}
	
	@Override
	public void printContents(int indent) {
		String indentation = "";
		for (int i = 0; i < indent; i++)
			indentation += " ";
		
		System.out.println(indentation + keyword);
		indentation += "  ";
		indent += 2;
		for (Expression e : expList)
			e.printContents(indent);
	}
	
}
