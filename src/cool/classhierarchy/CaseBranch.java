package cool.classhierarchy;

import org.antlr.v4.runtime.Token;

public class CaseBranch extends Expression {
	String keyword = "case branch";
	Expression exp;
	String varName;
	String varType;
	Token varNameToken;
	Token varTypeToken;
	
	public CaseBranch(String varName, String varType, Expression exp) {
		this.varName = varName;
		this.varType = varType;
		this.exp = exp;
	}
	
	public Expression getExp() {
		return exp;
	}

	public void setExp(Expression exp) {
		this.exp = exp;
	}

	public String getVarName() {
		return varName;
	}

	public void setVarName(String varName) {
		this.varName = varName;
	}

	public String getVarType() {
		return varType;
	}

	public void setVarType(String varType) {
		this.varType = varType;
	}
	
	public Token getVarNameToken() {
		return varNameToken;
	}

	public void setVarNameToken(Token varNameToken) {
		this.varNameToken = varNameToken;
	}

	public Token getVarTypeToken() {
		return varTypeToken;
	}

	public void setVarTypeToken(Token varTypeToken) {
		this.varTypeToken = varTypeToken;
	}
	
	@Override
	public void printContents(int indent) {
		String indentation = "";
		for (int i = 0; i < indent; i++)
			indentation += " ";
		
		System.out.println(indentation + keyword);
		indentation += "  ";
		indent += 2;
		System.out.println(indentation + varName);
		System.out.println(indentation + varType);
		exp.printContents(indent);
	}
}
