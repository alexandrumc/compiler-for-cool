package cool.classhierarchy;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;


public class Param extends Program {
	String paramName;
	public String getParamName() {
		return paramName;
	}

	public String getParamType() {
		return paramType;
	}

	String paramType;
	Token paramNameToken;
	Token paramTypeToken;
	ParserRuleContext context;
	
	public Param(String paramName, String paramType) {
		this.paramName = paramName;
		this.paramType = paramType;
	}
	
	public Token getParamNameToken() {
		return paramNameToken;
	}

	public void setParamNameToken(Token paramNameToken) {
		this.paramNameToken = paramNameToken;
	}

	public Token getParamTypeToken() {
		return paramTypeToken;
	}

	public void setParamTypeToken(Token paramTypeToken) {
		this.paramTypeToken = paramTypeToken;
	}

	public ParserRuleContext getContext() {
		return context;
	}

	public void setContext(ParserRuleContext context) {
		this.context = context;
	}
	
	@Override
	public void printContents(int indent) {
		String indentation = "";
		for (int i = 0; i < indent; i++)
			indentation += " ";
		
		System.out.println(indentation + "formal");
		indentation += "  ";
		indent += 2;
		System.out.println(indentation + paramName);
		System.out.println(indentation + paramType);
	}
}
