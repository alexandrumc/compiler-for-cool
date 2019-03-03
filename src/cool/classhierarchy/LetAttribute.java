package cool.classhierarchy;

import org.antlr.v4.runtime.Token;

public class LetAttribute extends Expression {
	String atribName;
	String atribType;
	Expression atribInit;
	Token atribNameToken;
	Token atribTypeToken;
	String keyword = "local";
	
	public LetAttribute(String atribName, String atribType, Expression atribInit) {
		this.atribName = atribName;
		this.atribType = atribType;
		this.atribInit = atribInit;
	}
	
	public String getAtribName() {
		return atribName;
	}

	public String getAtribType() {
		return atribType;
	}

	public Expression getAtribInit() {
		return atribInit;
	}
	
	public Token getAtribNameToken() {
		return atribNameToken;
	}

	public void setAtribNameToken(Token atribNameToken) {
		this.atribNameToken = atribNameToken;
	}

	public Token getAtribTypeToken() {
		return atribTypeToken;
	}

	public void setAtribTypeToken(Token atribTypeToken) {
		this.atribTypeToken = atribTypeToken;
	}

	
	@Override
	public void printContents(int indent) {
		String indentation = "";
		for (int i = 0; i < indent; i++)
			indentation += " ";
		
		System.out.println(indentation + keyword);
		indentation += "  ";
		indent += 2;
		System.out.println(indentation + atribName);
		System.out.println(indentation + atribType);
		if (atribInit != null)
			atribInit.printContents(indent);
		
	}
}
