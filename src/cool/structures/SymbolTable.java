package cool.structures;

import java.io.File;
import java.util.HashMap;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import cool.classhierarchy.ClassType;
import cool.classhierarchy.Method;
import cool.classhierarchy.Param;
import cool.compiler.Compiler;
import cool.parser.CoolParser;

public class SymbolTable {
    public static Scope globals;
    public static HashMap<String, Integer> heights = new HashMap<>();
    public static HashMap<MethodSymbol, Method> methodMap = new HashMap<>();
    
    private static boolean semanticErrors;
    
    public static void defineBasicClasses() {
        globals = new DefaultScope(null);
        semanticErrors = false;
        
        ClassScope objectClass = new ClassScope("Object");
        objectClass.setParent(null);
        MethodSymbol ms = new MethodSymbol("abort", objectClass);
        ms.offset = 0;
        ms.setType(new TypeSymbol(objectClass.getName()));
        ms.setParent(objectClass);
        ms.setBelongingClass("Object");
        objectClass.add(ms);
        
        Method m = new Method("abort", "Object");
        methodMap.put(ms, m);
        
        
        ms = new MethodSymbol("type_name", objectClass);
        ms.offset = 4;
        ms.setType(new TypeSymbol("String"));
        ms.setParent(objectClass);
        ms.setBelongingClass("Object");
        objectClass.add(ms);
        
        m = new Method("type_name", "String");
        methodMap.put(ms, m);
        
        ms = new MethodSymbol("copy", objectClass);
        ms.offset = 8;
        ms.setType(new TypeSymbol("SELF_TYPE"));
        ms.setParent(objectClass);
        ms.setBelongingClass("Object");
        objectClass.add(ms);
        
        m = new Method("copy", "SELF_TYPE");
        methodMap.put(ms, m);
        
        heights.put("Object", 0);
        
        ClassScope IOClass = new ClassScope("IO");
        IOClass.setParent(objectClass);
        
        ms = new MethodSymbol("out_string", IOClass);
        ms.offset = 12;
        IdSymbol par = new IdSymbol("x");
        par.offset = 12;
        par.setLocation("fp");
        par.setType(new TypeSymbol("String"));
        ms.add(par);
        ms.setType(new TypeSymbol("SELF_TYPE"));
        ms.setParent(IOClass);
        ms.setBelongingClass("IO");
        IOClass.add(ms);
        
        m = new Method("out_string", "SELF_TYPE");
        m.parametersList.add(new Param("x", "String"));
        methodMap.put(ms, m);
        
        ms = new MethodSymbol("out_int", IOClass);
        ms.offset = 16;
        par = new IdSymbol("x");
        par.offset = 12;
        par.setLocation("fp");
        par.setType(new TypeSymbol("Int"));
        ms.add(par);
        ms.setType(new TypeSymbol("SELF_TYPE"));
        ms.setParent(IOClass);
        ms.setBelongingClass("IO");
        IOClass.add(ms);
        
        m = new Method("out_int", "SELF_TYPE");
        m.parametersList.add(new Param("x", "Int"));
        methodMap.put(ms, m);
        
        ms = new MethodSymbol("in_string", IOClass);
        ms.offset = 20;
        ms.setType(new TypeSymbol("String"));
        ms.setParent(IOClass);
        ms.setBelongingClass("IO");
        IOClass.add(ms);
        
        m = new Method("in_string", "String");
        methodMap.put(ms, m);
        
        ms = new MethodSymbol("in_int", IOClass);
        ms.offset = 24;
        ms.setType(new TypeSymbol("Int"));
        ms.setParent(IOClass);
        ms.setBelongingClass("IO");
        IOClass.add(ms);
        
        m = new Method("in_int", "Int");
        methodMap.put(ms, m);
        
        heights.put("IO", 1);
        
        ClassScope stringClass = new ClassScope("String");
        stringClass.setParent(objectClass);
        ms = new MethodSymbol("length", stringClass);
        ms.offset = 12;
        ms.setType(new TypeSymbol("Int"));
        ms.setParent(stringClass);
        ms.setBelongingClass("String");
        stringClass.add(ms);
        
        m = new Method("length", "Int");
        methodMap.put(ms, m);
        
        ms = new MethodSymbol("concat", stringClass);
        ms.offset = 16;
        par = new IdSymbol("s");
        par.offset = 12;
        par.setLocation("fp");
        par.setType(new TypeSymbol("String"));
        ms.add(par);
        ms.setType(new TypeSymbol("String"));
        ms.setParent(stringClass);
        ms.setBelongingClass("String");
        stringClass.add(ms);
        
        m = new Method("concat", "String");
        m.parametersList.add(new Param("s", "String"));
        methodMap.put(ms, m);
        
        ms = new MethodSymbol("substr", stringClass);
        ms.offset = 20;
        par = new IdSymbol("i");
        par.offset = 12;
        par.setLocation("fp");
        par.setType(new TypeSymbol("Int"));
        ms.add(par);
        par = new IdSymbol("l");
        par.offset = 16;
        par.setLocation("fp");
        par.setType(new TypeSymbol("Int"));
        ms.add(par);
        ms.setType(new TypeSymbol("String"));
        ms.setParent(stringClass);
        ms.setBelongingClass("String");
        stringClass.add(ms);
        
        m = new Method("substr", "String");
        m.parametersList.add(new Param("i", "Int"));
        m.parametersList.add(new Param("l", "Int"));
        methodMap.put(ms, m);
        
        heights.put("String", 1);
        
        ClassScope intClass = new ClassScope("Int");
        intClass.setParent(objectClass);
        ClassScope boolClass = new ClassScope("Bool");
        boolClass.setParent(objectClass);
        
        heights.put("Int", 1);
        heights.put("Bool", 1);
        //CHECK!!
//        ClassScope selftypeClass = new ClassScope("SELF_TYPE");
        
        
        globals.add(objectClass);
        globals.add(IOClass);
        globals.add(stringClass);
        globals.add(intClass);
        globals.add(boolClass);
        //globals.add(selftypeClass);
    }
    
    
    /**
     * Displays a semantic error message.
     * 
     * @param ctx Used to determine the enclosing class context of this error,
     *            which knows the file name in which the class was defined.
     * @param info Used for line and column information.
     * @param str The error message.
     */
    public static void error(ParserRuleContext ctx, Token info, String str) {
        while (! (ctx.getParent() instanceof CoolParser.ProgramContext))
            ctx = ctx.getParent();
        
        String message = "\"" + new File(Compiler.fileNames.get(ctx)).getName()
                + "\", line " + info.getLine()
                + ":" + (info.getCharPositionInLine() + 1)
                + ", Semantic error: " + str;
        
        System.err.println(message);
        
        semanticErrors = true;
    }
    
    public static void error(String str) {
        String message = "Semantic error: " + str;
        
        System.err.println(message);
        
        semanticErrors = true;
    }
    
    public static boolean hasSemanticErrors() {
        return semanticErrors;
    }
    
    public static String getLCA(String a, String b, HashMap<String, ClassType> mapping) {
//    	System.err.println("LCA " + a + " " + b);
		if(a.equals(b)) 
			return a;
		
		else if(heights.get(a) < heights.get(b))
			return getLCA(b, a, mapping);
		
		else {
			if (!a.equals("Object") && mapping.get(a).getClassParent().equals(""))
				return getLCA("Object", b, mapping);
			return getLCA(mapping.get(a).getClassParent(), b, mapping);
		}
	}
    
    public static boolean compatible(String a, String b, HashMap<String, ClassType> mapping) {
		if(a.equals(b)) {
//			System.err.println("Am iesit pe true");
			return true;
		}
		else {
			if (!a.equals("Object") && mapping.get(a).getClassParent().equals(""))
				return compatible("Object", b, mapping);
			if(a.equals("Object"))  {
//				System.err.println("AM iesit pe false");
				return false;
			}
			else 
				return compatible(mapping.get(a).getClassParent(), b, mapping);
		}
	}
}
