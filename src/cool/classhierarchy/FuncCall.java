package cool.classhierarchy;

import java.util.ArrayList;
import java.util.List;

public class FuncCall extends Expression {
	String funcName;
	public String getFuncName() {
		return funcName;
	}

	public void setFuncName(String funcName) {
		this.funcName = funcName;
	}

	String keyword = "implicit dispatch";
	public List<Expression> paramsList;
	
	public FuncCall(String funcName) {
		this.funcName = funcName;
		paramsList = new ArrayList<>();
	}
	
	@Override
	public void printContents(int indent) {
		String indentation = "";
		for (int i = 0; i < indent; i++)
			indentation += " ";
		
		System.out.println(indentation + keyword);
		indentation += "  ";
		indent += 2;
		System.out.println(indentation + funcName);
		for (Expression e : paramsList) {
			e.printContents(indent);
		}
		
	}
	
}
