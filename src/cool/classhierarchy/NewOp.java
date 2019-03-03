package cool.classhierarchy;

import org.antlr.v4.runtime.Token;

public class NewOp extends Expression {
	String var;
	String keyword = "new";
	Token varToken;
	
	public String getVar() {
		return var;
	}
	
	public void setVarToken(Token t) {
		this.varToken = t;
	}
	
	public Token getVarToken() {
		return this.varToken;
	}

	public void setVar(String var) {
		this.var = var;
	}

	public NewOp(String var) {
		this.var = var;
	}
	
	@Override
	public void printContents(int indent) {
		String indentation = "";
		for (int i = 0; i < indent; i++)
			indentation += " ";
		
		System.out.println(indentation + keyword);
		indentation += "  ";
		indent += 2;
		System.out.println(indentation + var);
	}
}
