package cool.classhierarchy;

import java.util.ArrayList;
import java.util.List;


public class Let extends Expression {
	public Expression body;
	public List<LetAttribute> localDefs;
	String keyword = "let";
	
	public Let(Expression body) {
		this.body = body;
		this.localDefs = new ArrayList<>();
	}
	
	@Override
	public void printContents(int indent) {
		String indentation = "";
		for (int i = 0; i < indent; i++)
			indentation += " ";
		
		System.out.println(indentation + keyword);
		indentation += "  ";
		indent += 2;
		for (Expression e : localDefs)
			e.printContents(indent);
		body.printContents(indent);
		
	}
}
