package cool.classhierarchy;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.Token;

public class ExplicitDispatch extends Expression {
	String keyword = ".";
	public Expression getObj() {
		return obj;
	}

	public String getMethodName() {
		return methodName;
	}

	public String getClassName() {
		return className;
	}

	Expression obj;
	String methodName;
	String className;
	Token methodNameToken;
	Token classNameToken;
	public Token getMethodNameToken() {
		return methodNameToken;
	}

	public void setMethodNameToken(Token methodNameToken) {
		this.methodNameToken = methodNameToken;
	}

	public Token getClassNameToken() {
		return classNameToken;
	}

	public void setClassNameToken(Token classNameToken) {
		this.classNameToken = classNameToken;
	}

	public List<Expression> paramsList;
	
	public ExplicitDispatch(String className, String methodName, Expression obj) {
		this.className = className;
		this.methodName = methodName;
		this.obj = obj;
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
		obj.printContents(indent);
		if (!className.isEmpty())
			System.out.println(indentation + className);
		System.out.println(indentation + methodName);
		for (Expression e : paramsList) {
			e.printContents(indent);
		}
		
	}
}
