package cool.classhierarchy;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class Attribute extends ClassStatement{
	String atribName;
	String atribType;
	Expression atribInit;
	Token atribTokenName;
	Token atribTokenType;
	ParserRuleContext atribContext;
	
	public Attribute(String atribName, String atribType, Expression atribInit) {
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
	
	public Token getAtribTokenName() {
		return atribTokenName;
	}

	public void setAtribTokenName(Token atribToken) {
		this.atribTokenName = atribToken;
	}

	public Token getAtribTokenType() {
		return atribTokenType;
	}

	public void setAtribTokenType(Token atribToken) {
		this.atribTokenType = atribToken;
	}
	
	public ParserRuleContext getAtribContext() {
		return atribContext;
	}

	public void setAtribContext(ParserRuleContext atribContext) {
		this.atribContext = atribContext;
	}
	
	@Override
	public void printContents(int indent) {
		String indentation = "";
		for (int i = 0; i < indent; i++)
			indentation += " ";
		
		System.out.println(indentation + "attribute");
		indentation += "  ";
		indent += 2;
		System.out.println(indentation + atribName);
		System.out.println(indentation + atribType);
		if (atribInit != null)
			atribInit.printContents(indent);
		
	}
}
