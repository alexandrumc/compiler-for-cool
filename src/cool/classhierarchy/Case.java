package cool.classhierarchy;

import java.util.ArrayList;
import java.util.List;

public class Case extends Expression {
	String keyword = "case";
	public List<CaseBranch> branches;
	Expression cond;
	
	public Expression getCond() {
		return cond;
	}

	public Case(Expression cond) {
		this.cond = cond;
		this.branches = new ArrayList<>();
	}
	
	@Override
	public void printContents(int indent) {
		String indentation = "";
		for (int i = 0; i < indent; i++)
			indentation += " ";
		
		System.out.println(indentation + keyword);
		indentation += "  ";
		indent += 2;
		cond.printContents(indent);
		for (CaseBranch c : branches)
			c.printContents(indent);
	}
}
