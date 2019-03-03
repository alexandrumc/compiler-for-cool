package cool.classhierarchy;

import java.util.HashMap;
import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;


public class ClassType extends Program {
	List<ClassStatement> classStatement;
	String className;
	String classParent;
	public void setClassParent(String classParent) {
		this.classParent = classParent;
	}

	Token classNameToken;
	Token parentNameToken;
	ParserRuleContext context;
	HashMap<String, Attribute> classAttributes;
	HashMap<String, Method> classMethods;
	
	
	public ClassType(String className, String classParent, List<ClassStatement> list, ParserRuleContext context) {
		classStatement = list;
		this.className = className;
		this.classParent = classParent;
		this.context = context;
		this.classAttributes = new HashMap<>();
		this.classMethods = new HashMap<>();
	}
	
	public List<ClassStatement> getClassStatement() {
		return classStatement;
	}

	public String getClassName() {
		return className;
	}

	public String getClassParent() {
		return classParent;
	}

	public void setClassNameToken(Token classNameToken) {
		this.classNameToken = classNameToken;
	}

	public void setParentNameToken(Token parentNameToken) {
		this.parentNameToken = parentNameToken;
	}

	public Token getClassNameToken() {
		return classNameToken;
	}

	public Token getParentNameToken() {
		return parentNameToken;
	}

	public ParserRuleContext getContext() {
		return context;
	}

	public HashMap<String, Attribute> getClassAttributes() {
		return classAttributes;
	}

	public HashMap<String, Method> getClassMethods() {
		return classMethods;
	}
	
	@Override
	public void printContents(int indent) {
		String indentation = "";
		for (int i = 0; i < indent; i++)
			indentation += " ";
		
		System.out.println(indentation + "class");
		indentation += "  ";
		indent += 2;
		System.out.println(indentation + className);
		if (!classParent.isEmpty()) {
			System.out.println(indentation + classParent);
		}
		for (ClassStatement stmt : classStatement) {
			stmt.printContents(indent);
		}
	}
		
}
