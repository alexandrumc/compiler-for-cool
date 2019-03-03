package cool.classhierarchy;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class Method extends ClassStatement{
	String methodName;
	String methodReturnType;
	Token methodNameToken;
	Token methodReturnTypeToken;
	ParserRuleContext context;
	public List<Param> parametersList;
	public List<Expression> body;
	
	public Method(String methodName, String methodType) {
		this.methodName = methodName;
		this.methodReturnType = methodType;
		parametersList = new ArrayList<>();
		body = new ArrayList<>();
	}
	
	public void setMethodNameToken(Token methodNameToken) {
		this.methodNameToken = methodNameToken;
	}

	public void setMethodReturnTypeToken(Token methodReturnTypeToken) {
		this.methodReturnTypeToken = methodReturnTypeToken;
	}

	public void setContext(ParserRuleContext context) {
		this.context = context;
	}

	
	public String getMethodName() {
		return methodName;
	}

	public String getMethodReturnType() {
		return methodReturnType;
	}

	public Token getMethodNameToken() {
		return methodNameToken;
	}

	public Token getMethodReturnTypeToken() {
		return methodReturnTypeToken;
	}

	public ParserRuleContext getContext() {
		return context;
	}

	public List<Param> getParametersList() {
		return parametersList;
	}

	public List<Expression> getBody() {
		return body;
	}
	
	@Override
	public void printContents(int indent) {
		String indentation = "";
		for (int i = 0; i < indent; i++)
			indentation += " ";
		System.out.println(indentation + "method");
		indentation += "  ";
		indent += 2;
		System.out.println(indentation + methodName);
		for (Param p : parametersList) {
			p.printContents(indent);
		}
		System.out.println(indentation + methodReturnType);
		for (Expression e : body) {
			e.printContents(indent);
		}
		
		
	}
}
