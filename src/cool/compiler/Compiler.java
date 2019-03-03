package cool.compiler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;
import java.util.Stack;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import cool.classhierarchy.Attribute;
import cool.classhierarchy.Attribution;
import cool.classhierarchy.Block;
import cool.classhierarchy.BoolType;
import cool.classhierarchy.Case;
import cool.classhierarchy.CaseBranch;
import cool.classhierarchy.ClassStatement;
import cool.classhierarchy.ClassType;
import cool.classhierarchy.DivOp;
import cool.classhierarchy.EqRel;
import cool.classhierarchy.ExplicitDispatch;
import cool.classhierarchy.Expression;
import cool.classhierarchy.FuncCall;
import cool.classhierarchy.IDType;
import cool.classhierarchy.If;
import cool.classhierarchy.IntegerType;
import cool.classhierarchy.IsvoidOp;
import cool.classhierarchy.LessEqRel;
import cool.classhierarchy.LessRel;
import cool.classhierarchy.Let;
import cool.classhierarchy.LetAttribute;
import cool.classhierarchy.Method;
import cool.classhierarchy.MulOp;
import cool.classhierarchy.NewOp;
import cool.classhierarchy.NotOp;
import cool.classhierarchy.Param;
import cool.classhierarchy.ParenthesesExpression;
import cool.classhierarchy.PlusOp;
import cool.classhierarchy.Program;
import cool.classhierarchy.StringType;
import cool.classhierarchy.SubstOp;
import cool.classhierarchy.TildaOp;
import cool.classhierarchy.While;
import cool.lexer.CoolLexer;
import cool.parser.CoolParser;
import cool.parser.CoolParser.ExprContext;
import cool.parser.CoolParserBaseVisitor;
import cool.structures.ClassScope;
import cool.structures.DefaultScope;
import cool.structures.IdSymbol;
import cool.structures.MethodSymbol;
import cool.structures.Pair;
import cool.structures.ProgramBaseVisitor;
import cool.structures.Scope;
import cool.structures.Symbol;
import cool.structures.SymbolTable;
import cool.structures.TypeSymbol;


public class Compiler {
    // Annotates class nodes with the names of files where they are defined.
    public static ParseTreeProperty<String> fileNames = new ParseTreeProperty<>();

    /**
     * @param args
     * @throws IOException
     */
    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println("No file(s) given");
            return;
        }
  
        
        CoolLexer lexer = null;
        CommonTokenStream tokenStream = null;
        CoolParser parser = null;
        ParserRuleContext globalTree = null;
        
        // True if any lexical or syntax errors occur.
        boolean lexicalSyntaxErrors = false;
        
        // Parse each input file and build one big parse tree out of
        // individual parse trees.
        for (var fileName : args) {
            var input = CharStreams.fromFileName(fileName);
            
            // Lexer
            if (lexer == null)
                lexer = new CoolLexer(input);
            else
                lexer.setInputStream(input);

            // Token stream
            if (tokenStream == null)
                tokenStream = new CommonTokenStream(lexer);
            else
                tokenStream.setTokenSource(lexer);
                
            /*
            // Test lexer only.
            tokenStream.fill();
            List<Token> tokens = tokenStream.getTokens();
            tokens.stream().forEach(token -> {
                var text = token.getText();
                var name = CoolLexer.VOCABULARY.getSymbolicName(token.getType());
                
                System.out.println(text + " : " + name);
                //System.out.println(token);
            });
            */
            
            // Parser
            if (parser == null)
                parser = new CoolParser(tokenStream);
            else
                parser.setTokenStream(tokenStream);
            
            // Customized error listener, for including file names in error
            // messages.
            var errorListener = new BaseErrorListener() {
                public boolean errors = false;
                
                @Override
                public void syntaxError(Recognizer<?, ?> recognizer,
                                        Object offendingSymbol,
                                        int line, int charPositionInLine,
                                        String msg,
                                        RecognitionException e) {
                    String newMsg = "\"" + new File(fileName).getName() + "\", line " +
                                        line + ":" + (charPositionInLine + 1) + ", ";
                    
                    Token token = (Token)offendingSymbol;
                    if (token.getType() == CoolLexer.ERROR)
                        newMsg += "Lexical error: " + token.getText();
                    else
                        newMsg += "Syntax error: " + msg;
                    
                    System.err.println(newMsg);
                    errors = true;
                }
            };
            
            parser.removeErrorListeners();
            parser.addErrorListener(errorListener);
            
            // Actual parsing
            var tree = parser.program();
            if (globalTree == null)
                globalTree = tree;
            else
                // Add the current parse tree's children to the global tree.
                for (int i = 0; i < tree.getChildCount(); i++)
                    globalTree.addAnyChild(tree.getChild(i));
                    
            // Annotate class nodes with file names, to be used later
            // in semantic error messages.
            for (int i = 0; i < tree.getChildCount(); i++) {
                var child = tree.getChild(i);
                // The only ParserRuleContext children of the program node
                // are class nodes.
                if (child instanceof ParserRuleContext)
                    fileNames.put(child, fileName);
            }
            
            // Record any lexical or syntax errors.
            lexicalSyntaxErrors |= errorListener.errors;
        }

        // Stop before semantic analysis phase, in case errors occurred.
        if (lexicalSyntaxErrors) {
            System.err.println("Compilation halted");
            return;
        }
        
        HashMap<ParserRuleContext, Program> ctxToNodeMapping = new HashMap<>();
        
        var visitor = new CoolParserBaseVisitor<Program>() {
            
        	@Override
        	public Program visitProgram(CoolParser.ProgramContext ctx) { 
        		Program root = new Program();
        		
        		for (var cls : ctx.classList) {
        			ClassType classNode = (ClassType)visit(cls);
        			root.classList.add(classNode);
        		}
        		ctxToNodeMapping.put(ctx, root);
        		return root;
        	}
        	
        	public Program visitClass_def(CoolParser.Class_defContext ctx) {
        		List<ClassStatement> classStatement = new ArrayList<>();
        		for (var stmt : ctx.atribute_def()) {
        			ClassStatement a = (Attribute)visit(stmt);
        			classStatement.add(a);
        		}
        		
        		for (var stmt : ctx.func_def()) {
        			ClassStatement m = (Method)visit(stmt);
        			classStatement.add(m);
        		}
        		
        		if (ctx.TYPE_NAME().size() > 1) {
        			ClassType cls = new ClassType(ctx.className.getText(), ctx.parentName.getText(), classStatement, ctx);
        			cls.setClassNameToken(ctx.className);
        			cls.setParentNameToken(ctx.parentName);
        			return cls;
        		}
        		ClassType cls = new ClassType(ctx.className.getText(), "", classStatement, ctx);
    			cls.setClassNameToken(ctx.className);
    			cls.setParentNameToken(ctx.parentName);
    			ctxToNodeMapping.put(ctx, cls);
    			return cls;
        	}
        	
        	public Program visitAtribute_def(CoolParser.Atribute_defContext ctx) {
        		Attribute atrib;
        		if (ctx.atribInit == null) {
        			atrib =  new Attribute(ctx.atribName.getText(), ctx.atribType.getText(), null);
        			atrib.setAtribContext(ctx);
        			atrib.setAtribTokenName(ctx.atribName);
        			atrib.setAtribTokenType(ctx.atribType);
        			return atrib;
        		}
        		Expression exp = (Expression)visit(ctx.atribInit);
				atrib =  new Attribute(ctx.atribName.getText(), ctx.atribType.getText(), exp);
				atrib.setAtribContext(ctx);
    			atrib.setAtribTokenName(ctx.atribName);
    			atrib.setAtribTokenType(ctx.atribType);
    			ctxToNodeMapping.put(ctx, atrib);
    			return atrib;
        	}
        	
        	public Program visitFunc_def(CoolParser.Func_defContext ctx) { 
        		Method m = new Method(ctx.methodName.getText(), ctx.methodReturnType.getText());
        		for (var param : ctx.parametersList) {
        			Param p = (Param) visit(param);
        			m.parametersList.add(p);
        		}
        		for (var body_item : ctx.body) {
        			Expression e = (Expression)visit(body_item);
        			m.body.add(e);
        		}
        		m.setContext(ctx);
        		m.setMethodNameToken(ctx.methodName);
        		m.setMethodReturnTypeToken(ctx.methodReturnType);
        		ctxToNodeMapping.put(ctx, m);
        		return m;
        	}
        	
        	public Program visitParam_def(CoolParser.Param_defContext ctx) { 
        		Param p =  new Param(ctx.paramName.getText(), ctx.paramType.getText());
        		p.setContext(ctx);
        		p.setParamNameToken(ctx.paramName);
        		p.setParamTypeToken(ctx.paramType);
        		ctxToNodeMapping.put(ctx, p);
        		return p;
        	}
        	
        	public Program visitInt(CoolParser.IntContext ctx) { 
        		IntegerType it = new IntegerType(ctx.INT_LITERAL().getText()); 
        		it.setContext(ctx);
        		it.setToken(ctx.start);
        		ctxToNodeMapping.put(ctx, it);
        		return it;
        	}
        	
        	public Program visitString(CoolParser.StringContext ctx) { 
        		StringType st = new StringType(ctx.STRING().getText());
        		st.setContext(ctx);
        		st.setToken(ctx.start);
        		ctxToNodeMapping.put(ctx, st);
        		return st;
        	}
        	
        	public Program visitFalse(CoolParser.FalseContext ctx) { 
        		BoolType bt = new BoolType(ctx.FALSE().getText());
        		bt.setContext(ctx);
        		bt.setToken(ctx.start);
        		ctxToNodeMapping.put(ctx, bt);
        		return bt;
        	}
        	
        	public Program visitTrue(CoolParser.TrueContext ctx) { 
        		BoolType bt = new BoolType(ctx.TRUE().getText());
        		bt.setContext(ctx);
        		bt.setToken(ctx.start);
        		ctxToNodeMapping.put(ctx, bt);
        		return bt;
        	}
        	
        	public Program visitId(CoolParser.IdContext ctx) { 
        		IDType id = new IDType(ctx.ID_NAME().getText()); 
        		id.setContext(ctx);
        		id.setToken(ctx.start);
        		ctxToNodeMapping.put(ctx, id);
        		return id;
        	}
        	
        	public Program visitMinus_aritm(CoolParser.Minus_aritmContext ctx) { 
        		Expression leftOp = (Expression)visit(ctx.leftOp);
        		Expression rightOp = (Expression)visit(ctx.rightOp);
        		SubstOp so = new SubstOp(leftOp, rightOp);
        		so.setContext(ctx);
        		so.setToken(ctx.start);
        		ctxToNodeMapping.put(ctx, so);
        		return so;
        	}
        	
        	public Program visitPlus_aritm(CoolParser.Plus_aritmContext ctx) { 
        		Expression leftOp = (Expression)visit(ctx.leftOp);
        		Expression rightOp = (Expression)visit(ctx.rightOp);
        		PlusOp po = new PlusOp(leftOp, rightOp);
        		po.setContext(ctx);
        		po.setToken(ctx.start);
        		ctxToNodeMapping.put(ctx, po);
        		return po;
        	}
        	
        	public Program visitDiv_aritm(CoolParser.Div_aritmContext ctx) { 
        		Expression leftOp = (Expression)visit(ctx.leftOp);
        		Expression rightOp = (Expression)visit(ctx.rightOp);
        		DivOp doo = new DivOp(leftOp, rightOp);
        		doo.setContext(ctx);
        		doo.setToken(ctx.start);
        		ctxToNodeMapping.put(ctx, doo);
        		return doo;
        	}
        	
        	public Program visitMul_aritm(CoolParser.Mul_aritmContext ctx) { 
        		Expression leftOp = (Expression)visit(ctx.leftOp);
        		Expression rightOp = (Expression)visit(ctx.rightOp);
        		MulOp mo = new MulOp(leftOp, rightOp);
        		mo.setContext(ctx);
        		mo.setToken(ctx.start);
        		ctxToNodeMapping.put(ctx, mo);
        		return mo;
        	}
        	
        	public Program visitTilda(CoolParser.TildaContext ctx) { 
        		Expression exp = (Expression)visit(ctx.exp);
        		TildaOp to = new TildaOp(exp);
        		to.setContext(ctx);
        		to.setToken(ctx.start);
        		ctxToNodeMapping.put(ctx, to);
        		return to;
        	}
        	
        	public Program visitParanthese_expr(CoolParser.Paranthese_exprContext ctx) {
        		Expression exp = (Expression)visit(ctx.exp);
        		ParenthesesExpression pe = new ParenthesesExpression(exp);
        		pe.setToken(ctx.start);
        		pe.setContext(ctx);
        		ctxToNodeMapping.put(ctx, pe);
        		return pe;
        	}
        	
        	public Program visitLess_eq_rel(CoolParser.Less_eq_relContext ctx) { 
        		Expression leftOp = (Expression)visit(ctx.leftOp);
        		Expression rightOp = (Expression)visit(ctx.rightOp);
        		LessEqRel ler = new LessEqRel(leftOp, rightOp);
        		ler.setContext(ctx);
        		ler.setToken(ctx.start);
        		ctxToNodeMapping.put(ctx, ler);
        		return ler;
        	}
        	
        	public Program visitEq_rel(CoolParser.Eq_relContext ctx) { 
        		Expression leftOp = (Expression)visit(ctx.leftOp);
        		Expression rightOp = (Expression)visit(ctx.rightOp);
        		EqRel er = new EqRel(leftOp, rightOp);
        		er.setToken(ctx.sign);
        		er.setContext(ctx);
        		ctxToNodeMapping.put(ctx, er);
        		return er;
        	}
        	
        	public Program visitLess_rel(CoolParser.Less_relContext ctx) { 
        		Expression leftOp = (Expression)visit(ctx.leftOp);
        		Expression rightOp = (Expression)visit(ctx.rightOp);
        		LessRel lr = new LessRel(leftOp, rightOp);
        		lr.setToken(ctx.start);
        		lr.setContext(ctx);
        		ctxToNodeMapping.put(ctx, lr);
        		return lr;
        	}
        	
        	public Program visitNot(CoolParser.NotContext ctx) { 
        		Expression exp = (Expression)visit(ctx.exp);
        		NotOp nop = new NotOp(exp); 
        		nop.setContext(ctx);
        		nop.setToken(ctx.start);
        		ctxToNodeMapping.put(ctx, nop);
        		return nop;
        	}
        	
        	public Program visitAtrib(CoolParser.AtribContext ctx) { 
        		Expression exp = (Expression)visit(ctx.exp);
        		
        		IDType id = new IDType(ctx.varName.getText());
        		id.setContext(ctx);
        		id.setToken(ctx.varName);
        		
        		Attribution atrib = new Attribution(id, exp);
        		atrib.setContext(ctx);
        		atrib.setToken(ctx.start);
        		ctxToNodeMapping.put(ctx, atrib);
        		return atrib;
        	}
        	
        	public Program visitIsvoid(CoolParser.IsvoidContext ctx) { 
        		Expression exp = (Expression)visit(ctx.exp);
        		IsvoidOp ivop = new IsvoidOp(exp);
        		ivop.setToken(ctx.start);
        		ivop.setContext(ctx);
        		ctxToNodeMapping.put(ctx, ivop);
        		return ivop;
        	}
        	
        	public Program visitNew(CoolParser.NewContext ctx) { 
        		NewOp no = new NewOp(ctx.var.getText());
        		no.setContext(ctx);
        		no.setVarToken(ctx.var);
        		no.setToken(ctx.start);
        		ctxToNodeMapping.put(ctx, no);
        		return no;
        	}
        	
        	public Program visitImplicit_dispatch(CoolParser.Implicit_dispatchContext ctx) { 
        		Expression obj = (Expression)visit(ctx.obj);
        		ExplicitDispatch ed = new ExplicitDispatch(ctx.className.getText(), ctx.methodName.getText(), obj);
        		for (var p : ctx.paramsList) {
        			Expression e = (Expression)visit(p);
        			ed.paramsList.add(e);
        		}
        		ed.setMethodNameToken(ctx.methodName);
        		ed.setContext(ctx);
        		ed.setToken(ctx.start);
        		ed.setClassNameToken(ctx.className);
        		ctxToNodeMapping.put(ctx, ed);
        		return ed;
        	}
        	
        	public Program visitExplicit_dispatch(CoolParser.Explicit_dispatchContext ctx) { 
        		Expression obj = (Expression)visit(ctx.obj);
        		ExplicitDispatch ed = new ExplicitDispatch("", ctx.methodName.getText(), obj);
        		for (var p : ctx.paramsList) {
        			Expression e = (Expression)visit(p);
        			ed.paramsList.add(e);
        		}
        		ed.setMethodNameToken(ctx.methodName);
        		ed.setContext(ctx);
        		ed.setToken(ctx.start);
        		ctxToNodeMapping.put(ctx, ed);
        		return ed;
        	}
        	
        	public Program visitFunc_call(CoolParser.Func_callContext ctx) { 
        		FuncCall fc = new FuncCall(ctx.funcName.getText());
        		for (var p : ctx.paramsList) {
        			Expression e = (Expression)visit(p);
        			fc.paramsList.add(e);
        		}
        		fc.setToken(ctx.funcName);
        		fc.setContext(ctx);
        		ctxToNodeMapping.put(ctx, fc);
        		return fc;
        	}
        	
        	public Program visitIf(CoolParser.IfContext ctx) {
        		Expression cond = (Expression)visit(ctx.cond);
        		Expression thenBranch = (Expression)visit(ctx.thenBranch);
        		Expression elseBranch = (Expression)visit(ctx.elseBranch);
        		If in = new If(cond, thenBranch, elseBranch);
        		in.setContext(ctx);
        		in.setToken(ctx.start);
        		ctxToNodeMapping.put(ctx, in);
        		return in;
        	}
        	
        	public Program visitWhile(CoolParser.WhileContext ctx) { 
        		Expression cond = (Expression)visit(ctx.cond);
        		Expression body = (Expression)visit(ctx.body);
        		While wh = new While(cond, body);
        		wh.setContext(ctx);
        		wh.setToken(ctx.start);
        		ctxToNodeMapping.put(ctx, wh);
        		return wh;
        	}
        	
        	public Program visitLet(CoolParser.LetContext ctx) { 
        		Expression e = (Expression)visit(ctx.body);
        		Let l = new Let(e);
        		l.setContext(ctx);
        		l.setToken(ctx.start);
        		for (var let : ctx.letAttrib) {
        			LetAttribute ep = (LetAttribute)visit(let);
        			l.localDefs.add(ep);
        		}
        		ctxToNodeMapping.put(ctx, l);
        		return l;
        	}
        	
        	public Program visitLet_attr(CoolParser.Let_attrContext ctx) { 
        		LetAttribute la;
        		if (ctx.atribInit == null) {
        			la = new LetAttribute(ctx.atribName.getText(), ctx.atribType.getText(), null);
        			la.setAtribNameToken(ctx.atribName);
        			la.setAtribTypeToken(ctx.atribType);
        			la.setContext(ctx);
        			la.setToken(ctx.start);
        			ctxToNodeMapping.put(ctx, la);
        			return la;
        		}
        		Expression exp = (Expression)visit(ctx.atribInit);
				la = new LetAttribute(ctx.atribName.getText(), ctx.atribType.getText(), exp);
				la.setAtribNameToken(ctx.atribName);
    			la.setAtribTypeToken(ctx.atribType);
    			la.setContext(ctx);
    			la.setToken(ctx.start);
    			ctxToNodeMapping.put(ctx, la);
				return la;
        	}
        	
        	public Program visitCase(CoolParser.CaseContext ctx) {
        		Expression e = (Expression)visit(ctx.cond);
        		Case c = new Case(e);
        		for (var v : ctx.caseBranches) {
        			CaseBranch cb = (CaseBranch)visit(v);
        			c.branches.add(cb);
        		}
        		c.setContext(ctx);
        		c.setToken(ctx.start);
        		ctxToNodeMapping.put(ctx, c);
        		return c;
        	}
        	
        	public Program visitCase_attr(CoolParser.Case_attrContext ctx) { 
        		Expression e = (Expression)visit(ctx.exp);
        		CaseBranch cb = new CaseBranch(ctx.varName.getText(), ctx.varType.getText(), e);
        		cb.setContext(ctx);
        		cb.setVarNameToken(ctx.varName);
        		cb.setVarTypeToken(ctx.varType);
        		ctxToNodeMapping.put(ctx, cb);
        		return cb;
        	}
        	
        	public Program visitBlock(CoolParser.BlockContext ctx) { 
        		Block b = new Block();
        		for (var v : ctx.blockList) {
        			Expression e = (Expression)visit(v);
        			b.expList.add(e);
        		}
        		b.setContext(ctx);
        		b.setToken(ctx.start);
        		ctxToNodeMapping.put(ctx, b);
        		return b;
        	}
        	
        };
       
        Program astTree = visitor.visit(globalTree);
        
        SymbolTable.defineBasicClasses();
        HashMap<String, ClassType> mapping = new HashMap<>();
        HashMap<Program, String> types = new HashMap<Program, String>();
        
        for (MethodSymbol ms : SymbolTable.methodMap.keySet()) {
        	types.put(SymbolTable.methodMap.get(ms), SymbolTable.methodMap.get(ms).getMethodReturnType());
        }
        

        ProgramBaseVisitor astVisitor = new ProgramBaseVisitor() {
        	HashMap<String, ClassType> map = mapping;
        	
        	@Override
        	public void visitProgram(Program node) {
        		for (ClassType cls : node.classList) {
        			visit(cls);
        		}
        	}
        	
        	@Override
        	public void visitClassType(ClassType node) {
        		String parentName = node.getClassParent();
        		ClassScope classScope = new ClassScope(node.getClassName());
        		Boolean ok = false;
        		if (node.getClassName().equals("SELF_TYPE")) {
        			SymbolTable.error(node.getContext(), node.getClassNameToken(), "Class has illegal name SELF_TYPE");
        			ok = true;
        		}
        		else if (parentName.equals("Int") || parentName.equals("String")
        				|| parentName.equals("Bool") || parentName.equals("SELF_TYPE")) {
        			String msg = String.format("Class %s has illegal parent %s", node.getClassName(), parentName);
        			SymbolTable.error(node.getContext(), node.getParentNameToken(), msg);
        			ok = true;
        		}
        		if (SymbolTable.globals.add(classScope) == false) {
        			String msg = String.format("Class %s is redefined", node.getClassName());
        			SymbolTable.error(node.getContext(), node.getClassNameToken(), msg);
        			ok = true;
        		}
        		
        		if(!ok) {
        			map.put(node.getClassName(), node);
        		}
        		
        	}
        	
        };
        
        astVisitor.visit(astTree);
        
        ArrayList<String> toDelete = new ArrayList<>();
        
        for (String className : mapping.keySet()) {
        	String parentName = mapping.get(className).getClassParent();
        	if(!parentName.equals("") && SymbolTable.globals.contains(parentName) == false) {
    			String msg = String.format("Class %s has undefined parent %s", mapping.get(className).getClassName(), mapping.get(className).getClassParent());
    			SymbolTable.error(mapping.get(className).getContext(), mapping.get(className).getParentNameToken(), msg);
    			toDelete.add(className);
    		}
        }
        
        for (String cls : toDelete) {
        	mapping.remove(cls);
        }
        
        HashMap<String, HashSet<String>> inheritanceChains = new HashMap<String, HashSet<String>>();
        
        for (String cls : mapping.keySet()) {
        	HashSet<String> set = new HashSet<String>();
        	set.add(cls);
        	inheritanceChains.put(cls, set);
        }
        
        HashSet<String> hasCycle = new HashSet<String>();
        
        for (String cls : mapping.keySet()) {
        	ClassType c = mapping.get(cls);
        	boolean cycle = false;
        	HashSet<String> container = inheritanceChains.get(c.getClassName());
        	while (!c.equals(null) && !c.getClassParent().equals("") && !c.getClassParent().equals("IO")) {
        		if (container.contains(c.getClassParent()) == true) {
        			cycle = true;
        			break;
        		} else {
        			container.add(c.getClassParent());
        			c = mapping.get(c.getClassParent());
        		}
        	}
        	if (cycle == true) {
        		for (Object classn : container.toArray()) {
        			if (!hasCycle.contains((String)classn)) {
        				String msg = String.format("Inheritance cycle for class %s", (String)classn);
        				SymbolTable.error(mapping.get((String)classn).getContext(), mapping.get((String)classn).getClassNameToken(), msg);
        				hasCycle.add((String)classn);
        			}
        		}
        	}
        }
        
        if (SymbolTable.hasSemanticErrors()) {
            System.err.println("Compilation halted");
            return;
        }
        
        DefaultScope globals = (DefaultScope)SymbolTable.globals;
        for (String classScopeName : globals.getSymbolsNames()) {
        	ClassScope cs = (ClassScope)SymbolTable.globals.get(classScopeName);
        	if (!classScopeName.equals("Object") && cs.getParent() == null) {
        		if (mapping.get(classScopeName).getClassParent().equals("")) {
        			cs.setParent((Scope)SymbolTable.globals.get("Object"));
        			SymbolTable.heights.put(classScopeName, 1);
        		}
        		else {
        			cs.setParent((Scope)SymbolTable.globals.get(mapping.get(classScopeName).getClassParent()));
        			SymbolTable.heights.put(classScopeName, SymbolTable.heights.get(mapping.get(classScopeName).getClassParent()) + 1);
        		}
        	}
        }
        
        
        Queue<String> q = new LinkedList<String>();
        List<ClassType> newClassList = new ArrayList<>();
        astTree.classList.add(new ClassType("IO", "", null, null));
        
        q.add("Object");
        while (!q.isEmpty()) {
        	String cls = q.poll();
        	if (!cls.equals("Object") && !cls.equals("IO"))
        		newClassList.add(mapping.get(cls));
        	for (ClassType ct : astTree.classList) {
        		Scope classScope = 	(Scope)SymbolTable.globals.get(ct.getClassName());
        		Symbol parentSymbol = (Symbol)classScope.getParent();
        		if (parentSymbol.getName().equals(cls))
        			q.add(ct.getClassName());
        	}
        }
        
        astTree.classList = newClassList;
        HashSet<Object> smth = new HashSet<>(); 
        HashSet<Object> changedNodes = new HashSet<>();
        HashMap<Program, Scope> nodeToScopeMapping = new HashMap<>();
        
//        System.out.println(astTree);
        
        astVisitor = new ProgramBaseVisitor() {
        	Scope currentScope;
        	
        	@Override
        	public void visitProgram(Program node) {
        		for (ClassType cls : node.classList) {
        			currentScope = (Scope)SymbolTable.globals.get(cls.getClassName());
        			IdSymbol selfSym = new IdSymbol("self");
        			selfSym.setType(new TypeSymbol(cls.getClassName()));
        			currentScope.add(selfSym);
        			visit(cls);
        		}
        	}
        	
        	@Override
        	public void visitClassType(ClassType node) {
        		for (var stmt : node.getClassStatement()) {
        			visit(stmt);
        		}
        	}
        	
        	@Override
        	public void visitAttribute(Attribute node) {
        		Symbol scope = (Symbol)currentScope;
        		String scopeName = scope.getName();
        		
        		if (node.getAtribName().equals("self")) {
        			String msg = String.format("Class %s has attribute with illegal name self", scopeName);
    				SymbolTable.error(node.getAtribContext(), node.getAtribTokenName(), msg);
                    return;
        		}
        		
        		IdSymbol atrib = new IdSymbol(node.getAtribName());
        		if (node.getAtribType().equals("SELF_TYPE")) {
        			atrib.setType(new TypeSymbol(scopeName));
        			changedNodes.add(atrib);
        		}
        		else {
        			atrib.setType(new TypeSymbol(node.getAtribType()));
        		}
        		
        		if (currentScope.inheritanceLookupAttribute(node.getAtribName()) != null) {
        			String msg = String.format("Class %s redefines inherited attribute %s", scopeName, node.getAtribName());
    				SymbolTable.error(node.getAtribContext(), node.getAtribTokenName(), msg);
                    return;
        		}
        		
        		if (!currentScope.add(atrib)) {
        			String msg = String.format("Class %s redefines attribute %s", scopeName, node.getAtribName());
    				SymbolTable.error(node.getAtribContext(), node.getAtribTokenName(), msg);
                    return;
                }
        		
        		if (!node.getAtribType().equals("SELF_TYPE")) {
	        		var typeSym = SymbolTable.globals.lookup(node.getAtribType());
	                
	                if (typeSym == null) {
	                	String msg = String.format("Class %s has attribute %s with undefined type %s", scopeName, node.getAtribName(), node.getAtribType());
	    				SymbolTable.error(node.getAtribContext(), node.getAtribTokenType(), msg);
	                    return;
	                }
        		}
        		if (!node.getAtribType().equals("SELF_TYPE"))
        			types.put(node, node.getAtribType());
        		else
        			types.put(node, scopeName);
        	}
        	
        	
        	@Override
        	public void visitMethod(Method node) {
        		Symbol scope = (Symbol)currentScope;
        		String scopeName = scope.getName();
        		
        		MethodSymbol methodScope = new MethodSymbol(node.getMethodName(), currentScope);
        		methodScope.setBelongingClass(scopeName);
            	
        		if (!currentScope.add(methodScope)) {
        			String msg = String.format("Class %s redefines method %s", scopeName, node.getMethodName());
    				SymbolTable.error(node.getContext(), node.getMethodNameToken(), msg);
                    return;
                }
        		
        		if (!node.getMethodReturnType().equals("SELF_TYPE")) {
        			var typeSym = SymbolTable.globals.lookup(node.getMethodReturnType());
                
	                if (typeSym == null) {
	                	String msg = String.format("Class %s has method %s with undefined return type %s", scopeName, node.getMethodName(), node.getMethodReturnType());
	    				SymbolTable.error(node.getContext(), node.getMethodReturnTypeToken(), msg);
	                    return;
	                }
        		}
        		
                MethodSymbol inheritedMethod = (MethodSymbol) currentScope.inheritanceLookupMethod(node.getMethodName());
                
//                System.out.println("Scope name " + scopeName);
//                System.out.println("Method name " + node.getMethodName());
                
                if (inheritedMethod != null && !inheritedMethod.getType().getName().equals(node.getMethodReturnType())) {
                	String msg = String.format("Class %s overrides method %s but changes return type from %s to %s", scopeName, node.getMethodName(), inheritedMethod.getType().getName(), node.getMethodReturnType());
                	smth.add(true);
                	SymbolTable.error(node.getContext(), node.getMethodReturnTypeToken(), msg);
    				return;
                }
                
            	methodScope.setType(new TypeSymbol(node.getMethodReturnType()));
            	types.put(node, node.getMethodReturnType());
            	
            	int offset = 12;
            	for (Param p : node.parametersList) {
            		if (p.getParamName().equals("self")) {
            			String msg = String.format("Method %s of class %s has formal parameter with illegal name self", node.getMethodName(), scopeName);
        				SymbolTable.error(node.getContext(), p.getParamNameToken(), msg);
        				continue;
            		}
            		if (p.getParamType().equals("SELF_TYPE")) {
            			String msg = String.format("Method %s of class %s has formal parameter %s with illegal type SELF_TYPE", node.getMethodName(), scopeName, p.getParamName());
        				SymbolTable.error(node.getContext(), p.getParamTypeToken(), msg);
        				continue;
            		}
            		if (SymbolTable.globals.get(p.getParamType()) == null) {
            			String msg = String.format("Method %s of class %s has formal parameter %s with undefined type %s", node.getMethodName(), scopeName, p.getParamName(), p.getParamType());
        				SymbolTable.error(node.getContext(), p.getParamTypeToken(), msg);
        				continue;
            		}
            		
            		IdSymbol sym = new IdSymbol(p.getParamName());
            		sym.setOffset(offset);
            		sym.setLocation("fp");
            		offset += 4;
                	if (!methodScope.add(sym)) {
                		String msg = String.format("Method %s of class %s redefines formal parameter %s", node.getMethodName(), scopeName, p.getParamName());
        				SymbolTable.error(node.getContext(), p.getParamNameToken(), msg);
        				continue;
                	}
                    types.put(p, p.getParamType());
                	sym.setType(new TypeSymbol(p.getParamType()));
            	}
            	
            	if (inheritedMethod != null) {
            		if (methodScope.countSymbols() != inheritedMethod.countSymbols()) {
            			String msg = String.format("Class %s overrides method %s with different number of formal parameters", scopeName, node.getMethodName());
            			smth.add(true);
            			SymbolTable.error(node.getContext(), node.getMethodNameToken(), msg);
        				return;
            		}
            		else if (!inheritedMethod.getBelongingClass().equals("Object") && 
            				!inheritedMethod.getBelongingClass().equals("IO")) {
            			ClassType cls = mapping.get(scopeName);
            			Method child = null;
            			Method parent = null;
            			for (ClassStatement m : cls.getClassStatement()) {
            				if (m.getClass().equals(Method.class)) {
            					Method aux = (Method)m;
            					if (aux.getMethodName().equals(methodScope.getName())) {
            						child = aux;
            						break;
            					}
            				}
            			}
            			
            			
            			cls = mapping.get(inheritedMethod.getBelongingClass());
            			for (ClassStatement m : cls.getClassStatement()) {
            				if (m.getClass().equals(Method.class)) {
            					Method aux = (Method)m;
            					if (aux.getMethodName().equals(inheritedMethod.getName())) {
            						parent = aux;
            						break;
            					}
            				}
            			}
            			
            			for (int i = 0; i < child.parametersList.size(); i++) {
            				Param p1 = child.parametersList.get(i);
            				Param p2 = parent.parametersList.get(i);
            				if (!p1.getParamType().equals(p2.getParamType())) {
            					String msg = String.format("Class %s overrides method %s but changes type of formal parameter %s from %s to %s", scopeName, node.getMethodName(), p1.getParamName() , p2.getParamType(), p1.getParamType());
            					smth.add(true);
            					SymbolTable.error(node.getContext(), p1.getParamTypeToken(), msg);
            				}
            			}
            		}
            	}
            	
            	SymbolTable.methodMap.put(methodScope, node);
        	}
        	
        };
        
        astVisitor.visit(astTree);
        
        if (smth.size() > 0) {
            System.err.println("Compilation halted");
            return;
        }
        
        mapping.put("Object", new ClassType("Object", "", null, null));
        mapping.put("Int", new ClassType("Int", "Object", null, null));
        mapping.put("String", new ClassType("String", "Object", null, null));
        mapping.put("Bool", new ClassType("Bool", "Object", null, null));
        mapping.put("IO", new ClassType("IO", "Object", null, null));
        
        astVisitor = new ProgramBaseVisitor() {
        	Scope currentScope;
        	ClassType currentClass;
        	boolean err = false;
        	boolean changedSELF = false;
        	Integer last_offset = 0;
        	
        	@Override
        	public void visitProgram(Program node) {
        		for (ClassType cls : node.classList) {
        			currentClass = cls;
        			currentScope = (Scope)SymbolTable.globals.get(cls.getClassName());
        			visit(cls);
        		}
        	}
        	
        	@Override
        	public void visitClassType(ClassType node) {
        		for (var stmt : node.getClassStatement()) {
        			visit(stmt);
        		}
        	}
        	
        	public void visitAttribute(Attribute node) {
        		if (node.getAtribInit() != null) {
        			changedSELF = false;
        			visit(node.getAtribInit());
        			
        			if (!SymbolTable.compatible(types.get(node.getAtribInit()), types.get(node), mapping)) {
        				String msg;
        				if (!changedSELF) {
        					msg = String.format("Type %s of initialization expression of attribute %s is incompatible with declared type %s", types.get(node.getAtribInit()), node.getAtribName(), types.get(node));
        				}
        				else {
        					msg = String.format("Type SELF_TYPE of initialization expression of attribute %s is incompatible with declared type %s", node.getAtribName(), types.get(node));
        				}
        				SymbolTable.error(node.getAtribContext(), node.getAtribInit().getToken(), msg);
        			}
        		}
        	}
        	
        	@Override
        	public void visitMethod(Method node) {
        		Scope oldScope = currentScope;
        		
        		currentScope = (Scope)currentScope.get(node.getMethodName());
        		MethodSymbol oldSym = (MethodSymbol)currentScope;
        		for (Expression exp : node.body) {
        			visit(exp);
        		}
        		
        		if (types.get(node.body.get(node.body.size() - 1)) != null) {
        			if (types.get(node).equals("SELF_TYPE")) {
        				if (!SymbolTable.compatible(types.get(node.body.get(node.body.size() - 1)), currentClass.getClassName(), mapping)) {
        					String msg = String.format("Type %s of the body of method %s is incompatible with declared return type %s", 
        							node.body.get(node.body.size() - 1).getType(), node.getMethodName(), oldSym.getType().getName());
        					SymbolTable.error(node.getContext(), node.body.get(node.body.size() - 1).getToken(), msg);
        				}
        			}
        			else {
        				if (!SymbolTable.compatible(types.get(node.body.get(node.body.size() - 1)), types.get(node), mapping)) {
        					String msg = String.format("Type %s of the body of method %s is incompatible with declared return type %s", 
        							node.body.get(node.body.size() - 1).getType(), node.getMethodName(), oldSym.getType().getName());
        					SymbolTable.error(node.getContext(), node.body.get(node.body.size() - 1).getToken(), msg);
        				}
        			}
        		}
        		
        		currentScope = oldScope;
        	}
        	
        	@Override
        	public void visitLet(Let node) {
        		Scope oldScope = currentScope;
        		DefaultScope letScope = new DefaultScope(currentScope);
        		
        		currentScope = letScope;
        		int offset = -4;
        		
        		nodeToScopeMapping.put(node, letScope);
        		
        		for (LetAttribute la : node.localDefs) {
        			
        			if (la.getAtribName().equals("self")) {
        				String msg = String.format("Let variable has illegal name self");
        				SymbolTable.error(node.getContext(), la.getAtribNameToken(), msg);
        				continue;
        			}
        			
        			if (!la.getAtribType().equals("SELF_TYPE")) {
	        			if (SymbolTable.globals.get(la.getAtribType()) == null) {
	        				String msg = String.format("Let variable %s has undefined type %s", la.getAtribName(), la.getAtribType());
	        				SymbolTable.error(node.getContext(), la.getAtribTypeToken(), msg);
	        				continue;
	        			}
	        			
	        			types.put(la, la.getAtribType());
	        			la.setType(la.getAtribType());
        			}
        			else {
        				types.put(la, currentClass.getClassName());
        				la.setType(currentClass.getClassName());
        			}
        			
        			if (la.getAtribInit() != null) {
        				err = false;
        				visit(la.getAtribInit());
        				
        				if (!err && !SymbolTable.compatible(types.get(la.getAtribInit()), types.get(la), mapping)) {
        					String msg = String.format("Type %s of initialization expression of identifier %s is incompatible with declared type %s", la.getAtribInit().getType(), la.getAtribName(), la.getAtribType());
        					SymbolTable.error(node.getContext(), la.getAtribInit().getToken(), msg);
        					err = true;
        					continue;
        				}
        				
        			}
        			
        			IdSymbol is = new IdSymbol(la.getAtribName());
        			is.setType(new TypeSymbol(la.getAtribType()));
        			is.setOffset(offset);
        			offset -= 4;
        			is.setLocation("fp");
        			currentScope.add(is);
        		}
        		
        		if(!err) {
        			visit(node.body);
        		
        			node.setType(types.get(node.body));
        		
        			types.put(node, types.get(node.body));
        		}
        		
        		currentScope = oldScope;
        	}
        	
        	@Override
        	public void visitCase(Case node) {
        		Scope oldScope = currentScope;
        		DefaultScope caseScope = new DefaultScope(currentScope);
        		
        		currentScope = caseScope;
        		
        		last_offset = currentScope.lookupAttribute(node.getCond().getToken().getText()).getOffset();
        		
        		nodeToScopeMapping.put(node, caseScope);
        		
        		visit(node.getCond());
        		
        		for (CaseBranch cb : node.branches) {
        			if (cb.getVarName().equals("self")) {
        				String msg = String.format("Case variable has illegal name self");
        				SymbolTable.error(node.getContext(), cb.getVarNameToken(), msg);
        				continue;
        			}
        			if (cb.getVarType().equals("SELF_TYPE")) {
        				String msg = String.format("Case variable %s has illegal type SELF_TYPE", cb.getVarName());
        				SymbolTable.error(node.getContext(), cb.getVarTypeToken(), msg);
        				continue;
        			}
        			if (SymbolTable.globals.get(cb.getVarType()) == null) {
        				String msg = String.format("Case variable %s has undefined type %s", cb.getVarName(), cb.getVarType());
        				SymbolTable.error(node.getContext(), cb.getVarTypeToken(), msg);
        				continue;
        			}
        			
        			visit(cb);
        		}
        		
        		String firstType = types.get(node.branches.get(0).getExp());
        		
        		
        		for (int i = 1; i < node.branches.size(); i++) {
        			CaseBranch cb = node.branches.get(i);
        			if (cb.getVarName().equals("self")) {
        				continue;
        			}
        			if (cb.getVarType().equals("SELF_TYPE")) {
        				continue;
        			}
        			if (SymbolTable.globals.get(cb.getVarType()) == null) {
        				continue;
        			}
        			firstType = SymbolTable.getLCA(firstType, types.get(cb.getExp()), mapping);
        		}
        		
        		
        		types.put(node, firstType);
        		node.setType(firstType);
        		currentScope = oldScope;
        	}
        	
        	@Override
        	public void visitCaseBranch(CaseBranch node) {
        		DefaultScope caseBranchScope = new DefaultScope(currentScope);
        		
        		currentScope = caseBranchScope;
        		
        		nodeToScopeMapping.put(node, caseBranchScope);
        		
        		IdSymbol sym = new IdSymbol(node.getVarName());
        		sym.setType(new TypeSymbol(node.getVarType()));
        		sym.setLocation("fp");
        		sym.setOffset(last_offset);
        		
        		currentScope.add(sym);
        		types.put(node, node.getVarType());
        		
        		visit(node.getExp());
        		types.put(node.getExp(), node.getExp().getType());
        		
        	}
        	
        	@Override
        	public void visitAttribution(Attribution node) {
        		err = false;
        		changedSELF = false;
        		
        		if (node.getVar().getIdName().equals("self")) {
        			String msg = String.format("Cannot assign to self");
    				SymbolTable.error(node.getContext(), node.getVar().getToken(), msg);
                    return;
        		}
        		visit(node.getVar());
        		visit(node.getExp());
        		
        		if (!err && !SymbolTable.compatible(types.get(node.getExp()), types.get(node.getVar()), mapping)) {
        			String msg;
        			if (changedSELF) {
        				msg = String.format("Type SELF_TYPE of assigned expression is incompatible with declared type %s of identifier %s", 
            					types.get(node.getVar()), node.getVar().getIdName());
        			}
        			else  if (!changedNodes.contains(node.getVar())){
        				msg = String.format("Type %s of assigned expression is incompatible with declared type %s of identifier %s", 
            					types.get(node.getExp()), node.getVar().getType(), node.getVar().getIdName());
        			}
        			else {
        				msg = String.format("Type %s of assigned expression is incompatible with declared type SELF_TYPE of identifier %s", 
            					types.get(node.getExp()), node.getVar().getIdName());
        			}

        			SymbolTable.error(node.getContext(), node.getExp().getToken(), msg);
    				return;
        		}
        		
        		node.setType(node.getExp().getType());
        		types.put(node, types.get(node.getExp()));
        	}
        	
        	@Override
        	public void visitIDType(IDType node) {
        		
        		var sym = currentScope.lookupAttribute(node.getIdName());
                
                if (sym == null) {
                	String msg = String.format("Undefined identifier %s", node.getIdName());
    				SymbolTable.error(node.getContext(), node.getToken(), msg);
    				err = true;
                    return;
                }
                
                if (changedNodes.contains(sym))
                	changedNodes.add(node);
                
                node.setType(((IdSymbol)sym).getType().getName());
                types.put(node, ((IdSymbol)sym).getType().getName());
        	}
        	
        	@Override
        	public void visitIntegerType(IntegerType node) {
        		node.setType("Int");
        		types.put(node, "Int");
        	}
        	
        	@Override
        	public void visitStringType(StringType node) {
        		node.setType("String");
        		types.put(node, "String");
        	}
        	
        	@Override
        	public void visitBoolType(BoolType node) {
        		node.setType("Bool");
        		types.put(node, "Bool");
        	}
        	
        	@Override
        	public void visitEqRel(EqRel node) {
        		visit(node.getLeftOp());
        		visit(node.getRightOp());
        		
        		node.setType("Bool");
        		types.put(node, "Bool");
        		
        		String leftType = types.get(node.getLeftOp());
        		String rightType = types.get(node.getRightOp());
        		
        		if (leftType.equals("Int") || leftType.equals("String") || leftType.equals("Bool")) {
        			if (!rightType.equals(leftType)) {
        				String msg = String.format("Cannot compare %s with %s", leftType, rightType);
        				SymbolTable.error(node.getContext(), node.getToken(), msg);
                        return;
        			}
        		}
        		
        		if (rightType.equals("Int") || rightType.equals("String") || rightType.equals("Bool")) {
        			if (!leftType.equals(rightType)) {
        				String msg = String.format("Cannot compare %s with %s", leftType, rightType);
        				SymbolTable.error(node.getContext(), node.getToken(), msg);
                        return;
        			}
        		}
        		
        		
        	}
        	
        	@Override
        	public void visitLessEqRel(LessEqRel node) {
        		visit(node.getLeftOp());
        		visit(node.getRightOp());
        		
        		node.setType("Bool");
        		types.put(node, "Bool");
        		
        		if (!types.get(node.getLeftOp()).equals("Int")) {
        			String msg = String.format("Operand of <= has type %s instead of Int", types.get(node.getLeftOp()));
    				SymbolTable.error(node.getLeftOp().getContext(), node.getLeftOp().getToken(), msg);
                    return;
        		}
        		if (!types.get(node.getRightOp()).equals("Int")) {
        			String msg = String.format("Operand of <= has type %s instead of Int", types.get(node.getRightOp()));
    				SymbolTable.error(node.getRightOp().getContext(), node.getRightOp().getToken(), msg);
                    return;
        		}
        		
        	}

        	@Override
        	public void visitLessRel(LessRel node) {
        		visit(node.getLeftOp());
        		visit(node.getRightOp());
        		
        		node.setType("Bool");
        		types.put(node, "Bool");
        		
        		if (!types.get(node.getLeftOp()).equals("Int")) {
        			String msg = String.format("Operand of < has type %s instead of Int", types.get(node.getLeftOp()));
    				SymbolTable.error(node.getLeftOp().getContext(), node.getLeftOp().getToken(), msg);
                    return;
        		}
        		if (!types.get(node.getRightOp()).equals("Int")) {
        			String msg = String.format("Operand of < has type %s instead of Int", types.get(node.getRightOp()));
    				SymbolTable.error(node.getRightOp().getContext(), node.getRightOp().getToken(), msg);
                    return;
        		}
        		
        	}
        	
        	@Override
        	public void visitDivOp(DivOp node) {
        		visit(node.getLeftOp());
        		visit(node.getRightOp());
        		
        		node.setType("Int");
        		types.put(node, "Int");
        		
        		if (!types.get(node.getLeftOp()).equals("Int")) {
        			String msg = String.format("Operand of / has type %s instead of Int", types.get(node.getLeftOp()));
    				SymbolTable.error(node.getLeftOp().getContext(), node.getLeftOp().getToken(), msg);
                    return;
        		}
        		if (!types.get(node.getRightOp()).equals("Int")) {
        			String msg = String.format("Operand of / has type %s instead of Int", types.get(node.getRightOp()));
    				SymbolTable.error(node.getRightOp().getContext(), node.getRightOp().getToken(), msg);
                    return;
        		}
        		
        	}
        	
        	@Override
        	public void visitPlusOp(PlusOp node) {
        		visit(node.getLeftOp());
        		visit(node.getRightOp());
        		
        		node.setType("Int");
        		types.put(node, "Int");
        		
        		if (!types.get(node.getLeftOp()).equals("Int")) {
        			String msg = String.format("Operand of + has type %s instead of Int", types.get(node.getLeftOp()));
    				SymbolTable.error(node.getLeftOp().getContext(), node.getLeftOp().getToken(), msg);
                    return;
        		}
        		if (!types.get(node.getRightOp()).equals("Int")) {
        			String msg = String.format("Operand of + has type %s instead of Int", types.get(node.getRightOp()));
    				SymbolTable.error(node.getRightOp().getContext(), node.getRightOp().getToken(), msg);
                    return;
        		}
        		
        	}
        	
        	@Override
        	public void visitSubstOp(SubstOp node) {
        		visit(node.getLeftOp());
        		visit(node.getRightOp());
        		
        		node.setType("Int");
        		types.put(node, "Int");
        		
        		if (!types.get(node.getLeftOp()).equals("Int")) {
        			String msg = String.format("Operand of - has type %s instead of Int", types.get(node.getLeftOp()));
    				SymbolTable.error(node.getLeftOp().getContext(), node.getLeftOp().getToken(), msg);
                    return;
        		}
        		if (!types.get(node.getRightOp()).equals("Int")) {
        			String msg = String.format("Operand of - has type %s instead of Int", types.get(node.getRightOp()));
    				SymbolTable.error(node.getRightOp().getContext(), node.getRightOp().getToken(), msg);
                    return;
        		}
        		
        	}
        	
        	@Override
        	public void visitMulOp(MulOp node) {
        		visit(node.getLeftOp());
        		visit(node.getRightOp());
        		
        		node.setType("Int");
        		types.put(node, "Int");
        		
        		if (!types.get(node.getLeftOp()).equals("Int")) {
        			String msg = String.format("Operand of * has type %s instead of Int", types.get(node.getLeftOp()));
    				SymbolTable.error(node.getLeftOp().getContext(), node.getLeftOp().getToken(), msg);
                    return;
        		}
        		if (!types.get(node.getRightOp()).equals("Int")) {
        			String msg = String.format("Operand of * has type %s instead of Int", types.get(node.getRightOp()));
    				SymbolTable.error(node.getRightOp().getContext(), node.getRightOp().getToken(), msg);
                    return;
        		}
        		
        	}
        	
        	@Override
        	public void visitTildaOp(TildaOp node) {
        		visit(node.getExp());
        		
        		node.setType("Int");
        		types.put(node, "Int");
        		
        		if (!types.get(node.getExp()).equals("Int")) {
        			String msg = String.format("Operand of ~ has type %s instead of Int", types.get(node.getExp()));
    				SymbolTable.error(node.getExp().getContext(), node.getExp().getToken(), msg);
                    return;
        		}
        		
        	}
        	
        	@Override
        	public void visitParenthesesExpression(ParenthesesExpression node) {
        		visit(node.getExp());
        		node.setType(types.get(node.getExp()));
        		types.put(node, types.get(node.getExp()));
        	}
        	
        	@Override
        	public void visitNotOp(NotOp node) {
        		visit(node.getExp());
        		
        		node.setType("Bool");
        		types.put(node, "Bool");
        		
        		if (!types.get(node.getExp()).equals("Bool")) {
        			String msg = String.format("Operand of not has type %s instead of Bool", types.get(node.getExp()));
    				SymbolTable.error(node.getExp().getContext(), node.getExp().getToken(), msg);
                    return;
        		}
        		
        	}
        	
        	@Override
        	public void visitNewOp(NewOp node) {
        		if(!node.getVar().equals("SELF_TYPE") && SymbolTable.globals.get(node.getVar()) == null)  {
        			String msg = String.format("new is used with undefined type %s", node.getVar());
        			err = true;
    				SymbolTable.error(node.getContext(), node.getVarToken(), msg);
                    return;
        		}
        		if (node.getVar().equals("SELF_TYPE")) {
        			node.setType(currentClass.getClassName());
        			types.put(node, currentClass.getClassName());
        			changedSELF = true;
        		}
        		else {
        			node.setType(node.getVar());
        			types.put(node, node.getVar());
        			changedSELF = false;
        		}
        	}
        	
        	@Override
        	public void visitIsvoidOp(IsvoidOp node) {
        		node.setType("Bool");
        		types.put(node, "Bool");
        	}
        	
        	@Override
        	public void visitWhile(While node) {
        		visit(node.getCond());
        		
        		node.setType("Object");
        		types.put(node, "Object");
        		
        		if (!types.get(node.getCond()).equals("Bool")) {
        			String msg = String.format("While condition has type %s instead of Bool", types.get(node.getCond()));
    				SymbolTable.error(node.getContext(), node.getCond().getToken(), msg);
                    return;
        		}
        		
        		visit(node.getBody());
        	}
        	
        	@Override
        	public void visitIf(If node) {
        		visit(node.getCond());
        		
        		visit(node.getThenBranch());
        		visit(node.getElseBranch());

        		String type = SymbolTable.getLCA(types.get(node.getThenBranch()), types.get(node.getElseBranch()), mapping);
        		
        		types.put(node, type);
        		node.setType(type);
        		
        		if (!types.get(node.getCond()).equals("Bool")) {
        			String msg = String.format("If condition has type %s instead of Bool", types.get(node.getCond()));
    				SymbolTable.error(node.getContext(), node.getCond().getToken(), msg);
                    return;
        		}
        		
        		
        	}
        	
        	@Override
        	public void visitBlock(Block node) {
        		for (Expression exp : node.expList) {
        			visit(exp);
        		}
        		types.put(node, types.get(node.expList.get(node.expList.size() - 1)));
        		node.setType(types.get(node.expList.get(node.expList.size() - 1)));
        	}
        	
        	@Override
        	public void visitFuncCall(FuncCall node) {
        		IdSymbol self = (IdSymbol)currentScope.lookupAttribute("self");
        		
        		ClassScope cs = (ClassScope)SymbolTable.globals.get(self.getType().getName());
        		var sym	= cs.lookupMethod(node.getFuncName());
        		
        		if (sym == null) {
                	String msg = String.format("Undefined method %s in class %s", node.getFuncName(), currentClass.getClassName());
    				SymbolTable.error(node.getContext(), node.getToken(), msg);
                    return;
                }
        		
        		Method m = SymbolTable.methodMap.get(sym);
        		
        		if (!types.get(m).equals("SELF_TYPE")) {
	        		node.setType(types.get(m));
	        		types.put(node, types.get(m));
	        		changedSELF = false;
        		} else {
        			changedSELF = true;
        			node.setType(self.getType().getName());
	        		types.put(node, self.getType().getName());
        		}
        		
        		if (node.paramsList.size() != m.parametersList.size()) {
        			String msg = String.format("Method %s of class %s is applied to wrong number of arguments", node.getFuncName(), currentClass.getClassName());
    				SymbolTable.error(node.getContext(), node.getToken(), msg);
                    return;
        		}
        		
        		for (int i = 0; i < node.paramsList.size(); i++) {
        			visit(node.paramsList.get(i));
        			if(!SymbolTable.compatible(node.paramsList.get(i).getType(), m.parametersList.get(i).getParamType(), mapping)) {
        				String msg = String.format("In call to method %s of class %s, actual type %s of formal parameter %s is incompatible with declared type %s", 
        						node.getFuncName(), currentClass.getClassName(), node.paramsList.get(i).getType(), m.parametersList.get(i).getParamName(), m.parametersList.get(i).getParamType());
        				SymbolTable.error(node.getContext(), node.paramsList.get(i).getToken(), msg);
                        return;
        			}
        		}
        		
        		
        		
        	}
        	
        	@Override
        	public void visitExplicitDispatch(ExplicitDispatch node) {
        		if (!node.getClassName().equals("")) {
        			if (node.getClassName().equals("SELF_TYPE")) {
        				String msg = String.format("Type of static dispatch cannot be SELF_TYPE");
        				SymbolTable.error(node.getContext(), node.getClassNameToken(), msg);
                        return;
        			}
        			
        			if (SymbolTable.globals.get(node.getClassName()) == null) {
        				String msg = String.format("Type %s of static dispatch is undefined", node.getClassName());
        				SymbolTable.error(node.getContext(), node.getClassNameToken(), msg);
                        return;
        			}
        			
        			ClassScope cs = (ClassScope)SymbolTable.globals.get(node.getClassName());
        			MethodSymbol ms = (MethodSymbol)cs.lookupMethod(node.getMethodName());
        			
        			if (ms == null) {
        				String msg = String.format("Undefined method %s in class %s", node.getMethodName(), cs.getName());
        				SymbolTable.error(node.getContext(), node.getMethodNameToken(), msg);
                        return;
        			}
        			
        			visit(node.getObj());
        			
        			if(!SymbolTable.compatible(types.get(node.getObj()), node.getClassName(), mapping)) {
        				String msg = String.format("Type %s of static dispatch is not a superclass of type %s", node.getClassName(), types.get(node.getObj()));
        				SymbolTable.error(node.getContext(), node.getClassNameToken(), msg);
                        return;
        			}
        			
        			Method m = SymbolTable.methodMap.get(ms);
        			
        			if (!types.get(m).equals("SELF_TYPE")) {
        				node.setType(types.get(m));
        				types.put(node, types.get(m));
        				changedSELF = false;
        			} else {
        				changedSELF = true;
        				node.setType(types.get(node.getObj()));
        				types.put(node, types.get(node.getObj()));
        			}
        			
        			if (node.paramsList.size() != m.parametersList.size()) {
            			String msg = String.format("Method %s of class %s is applied to wrong number of arguments", node.getMethodName(), cs.getName());
        				SymbolTable.error(node.getContext(), node.getMethodNameToken(), msg);
                        return;
            		}
            		
            		for (int i = 0; i < node.paramsList.size(); i++) {
            			visit(node.paramsList.get(i));
            			if(!SymbolTable.compatible(node.paramsList.get(i).getType(), m.parametersList.get(i).getParamType(), mapping)) {
            				String msg = String.format("In call to method %s of class %s, actual type %s of formal parameter %s is incompatible with declared type %s", 
            						node.getMethodName(), cs.getName(), node.paramsList.get(i).getType(), m.parametersList.get(i).getParamName(), m.parametersList.get(i).getParamType());
            				SymbolTable.error(node.getContext(), node.paramsList.get(i).getToken(), msg);
                            return;
            			}
            		}
        			
        		}
        		else {
        			visit(node.getObj());
        			ClassScope cs = (ClassScope)SymbolTable.globals.get(types.get(node.getObj()));
        			Symbol ms = cs.lookupMethod(node.getMethodName());
        			
        			if (ms == null) {
        				String msg = String.format("Undefined method %s in class %s", node.getMethodName(), cs.getName());
        				SymbolTable.error(node.getContext(), node.getMethodNameToken(), msg);
                        return;
        			}
        			
        			Method m = SymbolTable.methodMap.get(ms);
        			
        			if (!types.get(m).equals("SELF_TYPE")) {
        				node.setType(types.get(m));
        				types.put(node, types.get(m));
        				changedSELF = false;
        			} else {
        				changedSELF = true;
        				node.setType(types.get(node.getObj()));
        				types.put(node, types.get(node.getObj()));
        			}
            		
            		if (node.paramsList.size() != m.parametersList.size()) {
            			String msg = String.format("Method %s of class %s is applied to wrong number of arguments", node.getMethodName(), cs.getName());
        				SymbolTable.error(node.getContext(), node.getMethodNameToken(), msg);
                        return;
            		}
            		
            		for (int i = 0; i < node.paramsList.size(); i++) {
            			visit(node.paramsList.get(i));
            			if(!SymbolTable.compatible(node.paramsList.get(i).getType(), m.parametersList.get(i).getParamType(), mapping)) {
            				String msg = String.format("In call to method %s of class %s, actual type %s of formal parameter %s is incompatible with declared type %s", 
            						node.getMethodName(), cs.getName(), node.paramsList.get(i).getType(), m.parametersList.get(i).getParamName(), m.parametersList.get(i).getParamType());
            				SymbolTable.error(node.getContext(), node.paramsList.get(i).getToken(), msg);
                            return;
            			}
            		}
            		
            		
            		
        		}
        	}
        	
        };
        
        astVisitor.visit(astTree);
        
        
        if (SymbolTable.hasSemanticErrors()) {
            System.err.println("Compilation halted");
            return;
        }
        
        var templates = new STGroupFile("templates.stg");
        
        HashMap<Integer, String> intConstMapping = new HashMap<>();
        HashSet<String> stringConstSet = new HashSet<>();
        ArrayList<ST> intConstantsList = new ArrayList<>();
        HashMap<String, String> classTagMapping = new HashMap<>();
        HashMap<String, String> tagClassMapping = new HashMap<>();
        ArrayList<String> classTags = new ArrayList<>();
        HashMap<String, Integer> classNameIndexMapping = new HashMap<>();
        
        ST program_template = templates.getInstanceOf("program");
        
        
        
        HashMap<String, ArrayList<String>> adjList = new HashMap<>();
        
        ArrayList<String> adj = new ArrayList<>();
        
        adj.add("Bool");
        adj.add("String");
        adj.add("Int");
        adj.add("IO");
        
        for (ClassType c: astTree.classList) {
        	if (c.getClassParent().equals(""))
        		c.setClassParent("Object");
        }
        
        for (ClassType c : astTree.classList) {
        	if (c.getClassParent().equals("Object")) {
        		adj.add(c.getClassName());
        	}
        }
        
        
        adjList.put("Object", adj);
        
        adj = new ArrayList<>();
        for (ClassType c : astTree.classList) {
        	if (c.getClassParent().equals("IO")) {
        		adj.add(c.getClassName());
        	}
        }
        
        adjList.put("IO", adj);
        adjList.put("Int", new ArrayList<>());
        adjList.put("String", new ArrayList<>());
        adjList.put("Bool", new ArrayList<>());
        
        for (ClassType c : astTree.classList) {
        	adj = new ArrayList<>();
        	for (ClassType d : astTree.classList) {
        		if (d.getClassParent().equals(c.getClassName())) {
        			adj.add(d.getClassName());
        		}
        	}
        	adjList.put(c.getClassName(), adj);
        }
        
        
        HashSet<String> visited = new HashSet<>();
        int tag = 0;
        Stack<String> stack = new Stack<>();
        stack.push("Object");
        
        while (!stack.isEmpty()) {
        	String s = stack.pop();
        	if (!visited.contains(s)) {
        		classNameIndexMapping.put(s, tag);
        		tag++;
        		visited.add(s);
        	}
        	for (var e : adjList.get(s)) {
        		if (!visited.contains(e))
        			stack.push(e);
        	}
        }
        
        ST str_constant = templates.getInstanceOf("str_const");
        str_constant.add("index", 0);
        str_constant.add("word_dim", 5);
        str_constant.add("int_const_name", "int_const0");
        str_constant.add("text", "");
        str_constant.add("tag", classNameIndexMapping.get("String"));
        program_template.add("d", str_constant);
        classTagMapping.put("", "str_const0");
        tagClassMapping.put("str_const0", "");
        
        
        ST int_constant = templates.getInstanceOf("int_const");
        int_constant.add("index", 0);
        int_constant.add("value", 0);
        int_constant.add("tag", classNameIndexMapping.get("Int"));
        intConstantsList.add(int_constant);
        intConstMapping.put(0, "int_const0");
        
        str_constant = templates.getInstanceOf("str_const");
        str_constant.add("index", 1);
        str_constant.add("word_dim", 6);
        str_constant.add("int_const_name", "int_const1");
        str_constant.add("text", "Object");
        str_constant.add("tag", classNameIndexMapping.get("String"));
        program_template.add("d", str_constant);
        classTagMapping.put("Object", "str_const1");
        tagClassMapping.put("str_const1", "Object");
        classTags.add("str_const1");
        
        int_constant = templates.getInstanceOf("int_const");
        int_constant.add("index", 1);
        int_constant.add("value", 6);
        int_constant.add("tag", classNameIndexMapping.get("Int"));
        intConstantsList.add(int_constant);
        intConstMapping.put(6, "int_const1");
        
        str_constant = templates.getInstanceOf("str_const");
        str_constant.add("index", 2);
        str_constant.add("word_dim", 5);
        str_constant.add("int_const_name", "int_const2");
        str_constant.add("text", "IO");
        str_constant.add("tag", classNameIndexMapping.get("String"));
        program_template.add("d", str_constant);
        classTagMapping.put("IO", "str_const2");
        tagClassMapping.put("str_const2", "IO");
        classTags.add("str_const2");
        
        int_constant = templates.getInstanceOf("int_const");
        int_constant.add("index", 2);
        int_constant.add("value", 2);
        int_constant.add("tag", classNameIndexMapping.get("Int"));
        intConstantsList.add(int_constant);
        intConstMapping.put(2, "int_const2");
        
        str_constant = templates.getInstanceOf("str_const");
        str_constant.add("index", 3);
        str_constant.add("word_dim", 5);
        str_constant.add("int_const_name", "int_const3");
        str_constant.add("text", "Int");
        str_constant.add("tag", classNameIndexMapping.get("String"));
        program_template.add("d", str_constant);
        classTagMapping.put("Int", "str_const3");
        tagClassMapping.put("str_const3", "Int");
        classTags.add("str_const3");
        
        int_constant = templates.getInstanceOf("int_const");
        int_constant.add("index", 3);
        int_constant.add("value", 3);
        int_constant.add("tag", classNameIndexMapping.get("Int"));
        intConstantsList.add(int_constant);
        intConstMapping.put(3, "int_const3");
        
        str_constant = templates.getInstanceOf("str_const");
        str_constant.add("index", 4);
        str_constant.add("word_dim", 6);
        str_constant.add("int_const_name", "int_const1");
        str_constant.add("text", "String");
        str_constant.add("tag", classNameIndexMapping.get("String"));
        program_template.add("d", str_constant);
        classTagMapping.put("String", "str_const4");
        tagClassMapping.put("str_const4", "String");
        classTags.add("str_const4");
        
        str_constant = templates.getInstanceOf("str_const");
        str_constant.add("index", 5);
        str_constant.add("word_dim", 6);
        str_constant.add("int_const_name", "int_const4");
        str_constant.add("text", "Bool");
        str_constant.add("tag", classNameIndexMapping.get("String"));
        program_template.add("d", str_constant);
        classTagMapping.put("Bool", "str_const5");
        tagClassMapping.put("str_const5", "Bool");
        classTags.add("str_const5");
        
        int_constant = templates.getInstanceOf("int_const");
        int_constant.add("index", 4);
        int_constant.add("value", 4);
        int_constant.add("tag", classNameIndexMapping.get("Int"));
        intConstantsList.add(int_constant);
        intConstMapping.put(4, "int_const4");
        
        var codeGenerator = new CoolParserBaseVisitor<ST>() {
        	ST program = program_template;
        	Integer int_index = 5;
        	Integer str_index = 6;
        	boolean ok = false;

            @Override
            public ST visitInt(CoolParser.IntContext ctx) {
            	if (!intConstMapping.containsKey(Integer.parseInt(ctx.INT_LITERAL().getText()))) {
                	
                
	                ST int_const =  templates.getInstanceOf("int_const")
	                        .add("value", ctx.INT_LITERAL().getText())
	                		.add("index", int_index)
	                		.add("tag", classNameIndexMapping.get("Int"));
	                
	                intConstMapping.put(Integer.parseInt(ctx.INT_LITERAL().getText()), "int_const" + int_index.toString());
                
	                int_index++;
	                
	                intConstantsList.add(int_const);
            	}
            	return null;
            }
            
            
            @Override
            public ST visitProgram(CoolParser.ProgramContext ctx) {
            	
                for (var e : ctx.class_def()) {
                	visit(e);
                }
				return null;
               
            }
            
            @Override
            public ST visitClass_def(CoolParser.Class_defContext ctx) {
            	if(!ok) {
            		int len_file = Compiler.fileNames.get(ctx).length();
            		ST str_constant = templates.getInstanceOf("str_const");
                    str_constant.add("index", str_index);
                    str_constant.add("tag", classNameIndexMapping.get("String"));
                    
                    File f = new File(Compiler.fileNames.get(ctx));
                    str_constant.add("text", f.getName());
                    
                    str_index++;
                    
                    int word_dim = 4;
    				word_dim += (len_file + 1) / 4;
            		if ((len_file + 1 ) % 4 > 0)
            			word_dim += 1;
            		
            		str_constant.add("word_dim", word_dim);
                    
                	if (intConstMapping.containsKey(len_file)) {
                		str_constant.add("int_const_name", intConstMapping.get(len_file));
                	} else {
                		ST int_constant = templates.getInstanceOf("int_const");
            	        int_constant.add("index", int_index);
            	        int_constant.add("value", len_file);
            	        int_constant.add("tag", classNameIndexMapping.get("Int"));
            	        intConstantsList.add(int_constant);
            	        intConstMapping.put(len_file, "int_const" + int_index.toString());
            	        int_index++;
            	        str_constant.add("int_const_name", intConstMapping.get(len_file));
                	}
                	
                	stringConstSet.add(Compiler.fileNames.get(ctx));
                	
                	
                    program.add("d", str_constant);
                	
                	ok = true;
                    
            	}
            	
            	
            	ST string_const = templates.getInstanceOf("str_const")
            			.add("text", ctx.className.getText())
            			.add("index", str_index)
            			.add("tag", classNameIndexMapping.get("String"));
            	
            	int word_dim = 4;
        		
            	classTagMapping.put(ctx.className.getText(), "str_const" + str_index.toString());
            	tagClassMapping.put("str_const" + str_index.toString(), ctx.className.getText());
            	classTags.add(classTagMapping.get(ctx.className.getText()));
            	
        		int length = ctx.className.getText().length();
				word_dim += (length + 1) / 4;
        		if ((length + 1 ) % 4 > 0)
        			word_dim += 1;
            			
        		string_const.add("word_dim", word_dim);
        		
        		if (intConstMapping.containsKey(length))
        			string_const.add("int_const_name", intConstMapping.get(length));
        		else {
        			ST int_constant = templates.getInstanceOf("int_const");
        	        int_constant.add("index", int_index);
        	        int_constant.add("value", length);
        	        int_constant.add("tag", classNameIndexMapping.get("Int"));
        	        intConstantsList.add(int_constant);
        	        intConstMapping.put(length, "int_const" + int_index.toString());
        	        int_index++;
        	        string_const.add("int_const_name", intConstMapping.get(length));
        		}
            	
            	stringConstSet.add(ctx.className.getText());
            	
            	
                program.add("d", string_const);
                
                str_index++;
                
                for (var a : ctx.atribute_def())
                	visit(a);
                
                for (var a : ctx.func_def())
                	visit(a);
                
                return null;
            }
            
            
            public ST visitFunc_def(CoolParser.Func_defContext ctx) {
            	
            	for (var e : ctx.body)
            		visit(e);
            	
            	return null;
            }
            
            
            public ST visitBlock(CoolParser.BlockContext ctx) {
            	
            	for (var e : ctx.blockList)
            		visit(e);
            	
            	return null;
            }
            
            public ST visitFunc_call(CoolParser.Func_callContext ctx) {
            	for (var e : ctx.paramsList)
            		visit(e);
            	return null;
            }
            
            public ST visitExplicit_dispatch(CoolParser.Explicit_dispatchContext ctx) {
            	visit(ctx.obj);
            	for (var e : ctx.paramsList)
            		visit(e);
            	return null;
            }
            
            public ST visitImplicit_dispatch(CoolParser.Implicit_dispatchContext ctx) {
            	visit(ctx.obj);
            	for (var e : ctx.paramsList)
            		visit(e);
            	return null;
            }
            
            public ST visitAtrib(CoolParser.AtribContext ctx) {
            	visit(ctx.exp);
            	return null;
            }
            
            
            @Override
            public ST visitString(CoolParser.StringContext ctx) {
            	if (!stringConstSet.contains(ctx.STRING().getText())) {
            		ST string_const = templates.getInstanceOf("str_const")
                			.add("text", ctx.STRING().getText())
                			.add("index", str_index)
            				.add("tag", classNameIndexMapping.get("String"));
            		
            		
            		classTagMapping.put(ctx.STRING().getText(), "str_const" + str_index.toString());
            		tagClassMapping.put("str_const" + str_index.toString(), ctx.STRING().getText());
            		
            		int word_dim = 4;
            		int length = ctx.STRING().getText().length();
            		
            		word_dim += (length + 1) / 4;
            		if ((length + 1) % 4 > 0)
            			word_dim += 1;
                			
            		string_const.add("word_dim", word_dim);
            		
            		if (intConstMapping.containsKey(length))
            			string_const.add("int_const_name", intConstMapping.get(length));
            		else {
            			ST int_constant = templates.getInstanceOf("int_const");
            	        int_constant.add("index", int_index);
            	        int_constant.add("value", length);
            	        int_constant.add("tag", classNameIndexMapping.get("Int"));
            	        intConstantsList.add(int_constant);
            	        intConstMapping.put(length, "int_const" + int_index.toString());
            	        int_index++;
            	        string_const.add("int_const_name", intConstMapping.get(length));
            		}
            		
                	
                	stringConstSet.add(ctx.STRING().getText());
                	
                	str_index++;
                	
                	program.add("d", string_const);
            	}
            	return null;
            }
            
            @Override
            public ST visitAtribute_def(CoolParser.Atribute_defContext ctx) {
            	if (ctx.atribInit != null)
            		visit(ctx.atribInit);
            	return null;
            }
   
        };
        
        codeGenerator.visit(globalTree);
        for (var e : intConstantsList) {
        	program_template.add("d", e);
        }
        
        program_template.add("d", templates.getInstanceOf("bool_const").add("tag", classNameIndexMapping.get("Bool")));
        
        ST classtab = templates.getInstanceOf("classnametab");
        String[] order = new String[classTags.size()];
        order[classNameIndexMapping.get("Object")] = classTagMapping.get("Object");
        order[classNameIndexMapping.get("IO")] = classTagMapping.get("IO");
        order[classNameIndexMapping.get("Int")] = classTagMapping.get("Int");
        order[classNameIndexMapping.get("Bool")] = classTagMapping.get("Bool");
        order[classNameIndexMapping.get("Main")] = classTagMapping.get("Main");
        order[classNameIndexMapping.get("String")] = classTagMapping.get("String");
        for (ClassType c : astTree.classList) {
        	order[classNameIndexMapping.get(c.getClassName())] = classTagMapping.get(c.getClassName());
        }
        
        for (int i = 0; i < order.length; i++) {
        	ST word_declr = templates.getInstanceOf("word_declr");
        	word_declr.add("e", order[i]);
        	classtab.add("tag", word_declr);
        }
        
        
        program_template.add("d", classtab);
        
        
        ST classobjtab = templates.getInstanceOf("classobjtab");
        for (int i = 0; i < order.length; i++) {
        	ST word_declr = templates.getInstanceOf("word_declr");
        	word_declr.add("e", tagClassMapping.get(order[i]) + "_protObj");
        	classobjtab.add("tag", word_declr);
        	
        	word_declr = templates.getInstanceOf("word_declr");
        	word_declr.add("e", tagClassMapping.get(order[i]) + "_init");
        	classobjtab.add("tag", word_declr);
        }
        
        program_template.add("d", classobjtab);
    
        ST protObjBasic = templates.getInstanceOf("protObjBasic");
        protObjBasic.add("io", classNameIndexMapping.get("IO"));
        protObjBasic.add("int", classNameIndexMapping.get("Int"));
        protObjBasic.add("string", classNameIndexMapping.get("String"));
        protObjBasic.add("bool", classNameIndexMapping.get("Bool"));
        program_template.add("d", protObjBasic);
        
        
        program_template.add("int", classNameIndexMapping.get("Int"));
        program_template.add("bool", classNameIndexMapping.get("Bool"));
        program_template.add("string", classNameIndexMapping.get("String"));
        
        
        
        
        for (ClassType ct : astTree.classList) {
        	
        	ST classObjProt = templates.getInstanceOf("protObj");
        	classObjProt.add("name", ct.getClassName());
        	classObjProt.add("index", classNameIndexMapping.get(ct.getClassName()));
        	
        	ClassScope cs = (ClassScope)SymbolTable.globals.get(ct.getClassName());
        	cs.fillAttributes();
        	classObjProt.add("dim", String.valueOf(3 + cs.allAttributes.size()));
        	
        	ST attr_seq = templates.getInstanceOf("sequence");
        	for (IdSymbol is : cs.allAttributes) {
        		ST word_declr = templates.getInstanceOf("word_declr");
        		if (is.getType().getName().equals("String")) {
        			word_declr.add("e", "str_const0");
        		}
        		else if (is.getType().getName().equals("Bool")) {
        			word_declr.add("e", "bool_const0");
        		}
        		else if (is.getType().getName().equals("Int")) {
        			word_declr.add("e", "int_const0");
        		}
        		else {
        			word_declr.add("e", "0");
        		}
        		attr_seq.add("e", word_declr);
        	}
        	classObjProt.add("e", attr_seq);
        	
        	program_template.add("d", classObjProt);
        }
        
        ST basicDispTab = templates.getInstanceOf("dispTabBasic");
        program_template.add("d", basicDispTab);
        
        for (ClassType ct : astTree.classList) {
        	ST classDispTab = templates.getInstanceOf("dispTab");
        	classDispTab.add("name", ct.getClassName());
        	
        	ClassScope cs = (ClassScope)SymbolTable.globals.get(ct.getClassName());
        	cs.fillMethods();
        	
        	ST method_seq = templates.getInstanceOf("sequence");
        	for (Pair p  : cs.allMethods) {
        		ST word_declr = templates.getInstanceOf("word_declr");
        		
        		word_declr.add("e", p.getParentName() + "." + p.getMs().getName());
        		method_seq.add("e", word_declr);
        	}
        	
        	classDispTab.add("e", method_seq);
        	program_template.add("d", classDispTab);
        	
        	
        }
        
        program_template.add("d", templates.getInstanceOf("text_begin"));
        
       
        
        var codeGenerator1 = new CoolParserBaseVisitor<ST>() {

            @Override
            public ST visitProgram(CoolParser.ProgramContext ctx) {
            	
                for (var e : ctx.class_def()) {
                	ST obj_init = templates.getInstanceOf("obj_init");
                	ClassScope cs = (ClassScope)SymbolTable.globals.get(e.className.getText());
                	ClassScope parent = (ClassScope)cs.getParent();
                	obj_init.add("name", cs.getName());
                	obj_init.add("parent", parent.getName());
                	
                	if (cs.attributes.size() == 1) {
                		obj_init.add("e", "");
                		program_template.add("d", obj_init);
                		continue;
                	} else {
                		obj_init.add("e", visit(e));
                		program_template.add("d", obj_init);
                	}
                }
				return null;
            }
            
            @Override
            public ST visitClass_def(CoolParser.Class_defContext ctx) {
            	ST seq = templates.getInstanceOf("sequence");
            	ClassScope cs = (ClassScope)SymbolTable.globals.get(ctx.className.getText());
            	
                for (var a : ctx.atribute_def()) {
                	ST res = visit(a);
                	int offset = cs.attributes.get(a.atribName.getText()).getOffset();
                	if (res != null) {
                		seq.add("e", visit(a));
                		seq.add("e", templates.getInstanceOf("atrib_init").add("offset", offset));
                	}
                }
                return seq;
            }
            
            @Override
            public ST visitAtribute_def(CoolParser.Atribute_defContext ctx) {
            	if (ctx.atribInit != null)
            		return visit(ctx.atribInit);
            	return null;
            }
            
            
            @Override
            public ST visitInt(CoolParser.IntContext ctx) {
            	String tag = intConstMapping.get(Integer.parseInt(ctx.INT_LITERAL().getText()));
            	
            	ST int_load = templates.getInstanceOf("const_load");
            	int_load.add("tag", tag);
            	
            	return int_load;
            }
            
            @Override
            public ST visitString(CoolParser.StringContext ctx) {
            	String tag = classTagMapping.get(ctx.STRING().getText());
            	
            	ST string_load = templates.getInstanceOf("const_load");
            	string_load.add("tag", tag);
            	
            	return string_load;
            }
            
            @Override
            public ST visitTrue(CoolParser.TrueContext ctx) {
            	ST bool_load = templates.getInstanceOf("const_load");
            	bool_load.add("tag", "bool_const1");
            	return bool_load;
            }

            @Override
            public ST visitFalse(CoolParser.FalseContext ctx) {
            	ST bool_load = templates.getInstanceOf("const_load");
            	bool_load.add("tag", "bool_const0");
            	return bool_load;
            }

        };
        
        codeGenerator1.visit(globalTree);
        
        //program_template.add("d", templates.getInstanceOf("main_method"));
        
        
        var codeGenerator2 = new CoolParserBaseVisitor<ST>() {
        	ClassScope curentCS = null;
        	Scope oldScope = null;
        	Scope currentScope = null;
        	Integer local_vars = 0;
        	ST program = program_template;
        	boolean is_self = false;
        	Integer dispatch_index = 0;
        	Integer else_index = 0;
        	Integer end_index = 0;
        	Integer isvoid_index = 0;
        	Integer not_index = 0;
        	Integer eq_index = 0;
        	Integer lesseq_index = 0;
        	Integer less_index = 0;
        	Integer while_cond_index = 0;
        	Integer while_end_index = 0;

        	
            @Override
            public ST visitProgram(CoolParser.ProgramContext ctx) {
                for (var e : ctx.class_def()) {
                	curentCS = (ClassScope)SymbolTable.globals.get(e.className.getText());
                	currentScope = curentCS;
                	visit(e);
                	
                }
				return null;
            }
            
            @Override
            public ST visitClass_def(CoolParser.Class_defContext ctx) {
            	
                for (var a : ctx.func_def()) {
                	program.add("d", visit(a));
                }
                return null;
            }
            
            public ST visitFunc_def(CoolParser.Func_defContext ctx) {
            	ST func = templates.getInstanceOf("gen_method");
            	func.add("name", ctx.methodName.getText());
            	func.add("class", curentCS.getName());
            	
            	MethodSymbol ms = (MethodSymbol) curentCS.lookupMethod(ctx.methodName.getText());
            	oldScope = currentScope;
            	currentScope = ms;
            	Method m = SymbolTable.methodMap.get(ms);
            	
            	if (m.parametersList.size() > 0 ) {
            		func.add("param_inc", templates.getInstanceOf("stack_inc").add("val", 4 * m.parametersList.size()));
            	} else {
            		func.add("param_inc", "");
            	}
            	
            	local_vars = 0;
            	
            	ST body = templates.getInstanceOf("sequence");
            	
            	for (var e : ctx.body) {
            		body.add("e", visit(e));
            	}
            	
            	func.add("body", body);
            	
            	if (local_vars > 0) {
            		func.add("var_dec", templates.getInstanceOf("stack_dec").add("val", 4 * local_vars));
            		func.add("var_inc", templates.getInstanceOf("stack_inc").add("val", 4 * local_vars));
            	}
            	else {
            		func.add("var_dec", "");
            		func.add("var_inc", "");
            	}
            	currentScope = oldScope;
            	return func;
            }
            
            
            public ST visitExplicit_dispatch(CoolParser.Explicit_dispatchContext ctx) {
            	ST explicit_disp = templates.getInstanceOf("dynamic_disp");
            	ListIterator<ExprContext> li = ctx.paramsList.listIterator(ctx.paramsList.size());
            	while(li.hasPrevious()) {
            		ST param = templates.getInstanceOf("load_param");
            		param.add("param", visit(li.previous()));
            		explicit_disp.add("param", param);
            	}
            	
            	explicit_disp.add("obj", visit(ctx.obj));
            	
            	explicit_disp.add("label", "dispatch" + String.valueOf(dispatch_index));
            	
            	MethodSymbol ms;
            	if (is_self) {
            		ms = (MethodSymbol)curentCS.lookupMethod(ctx.methodName.getText());
            		explicit_disp.add("m_offset", ms.getOffset());
            	} else {
            		ExplicitDispatch ed = (ExplicitDispatch) ctxToNodeMapping.get(ctx);
                	ClassScope cs = (ClassScope) SymbolTable.globals.get(types.get(ed.getObj()));
                	ms = (MethodSymbol)cs.lookupMethod(ed.getMethodName());
                	explicit_disp.add("m_offset", ms.getOffset());
            	}
            	
            	explicit_disp.add("file", "str_const6");
            	explicit_disp.add("line", ctx.methodName.getLine());
            	
            	is_self = false;
            	dispatch_index += 1 ;
            	return explicit_disp;
            }
            
            
            public ST visitImplicit_dispatch(CoolParser.Implicit_dispatchContext ctx) {
            	ST explicit_disp = templates.getInstanceOf("static_disp");
            	ListIterator<ExprContext> li = ctx.paramsList.listIterator(ctx.paramsList.size());
            	while(li.hasPrevious()) {
            		ST param = templates.getInstanceOf("load_param");
            		param.add("param", visit(li.previous()));
            		explicit_disp.add("param", param);
            	}
            	
            	explicit_disp.add("obj", visit(ctx.obj));
            	explicit_disp.add("class", ctx.className.getText());
            	explicit_disp.add("label", "dispatch" + String.valueOf(dispatch_index));
            	
            	MethodSymbol ms;
            	if (is_self) {
            		ms = (MethodSymbol)curentCS.lookupMethod(ctx.methodName.getText());
            		explicit_disp.add("m_offset", ms.getOffset());
            	} else {
            		
            		ExplicitDispatch ed = (ExplicitDispatch) ctxToNodeMapping.get(ctx);
                	ClassScope cs = (ClassScope) SymbolTable.globals.get(types.get(ed.getObj()));
                	ms = (MethodSymbol)cs.lookupMethod(ed.getMethodName());
                	explicit_disp.add("m_offset", ms.getOffset());
            	}
            	
            	explicit_disp.add("file", "str_const6");
            	explicit_disp.add("line", ctx.methodName.getLine());
            	
            	is_self = false;
            	dispatch_index += 1 ;
            	return explicit_disp;
            }
            
            public ST visitFunc_call(CoolParser.Func_callContext ctx) {
            	ST explicit_disp = templates.getInstanceOf("dynamic_disp");
            	ListIterator<ExprContext> li = ctx.paramsList.listIterator(ctx.paramsList.size());
            	while(li.hasPrevious()) {
            		ST param = templates.getInstanceOf("load_param");
            		param.add("param", visit(li.previous()));
            		explicit_disp.add("param", param);
            	}
            	
            	explicit_disp.add("obj", templates.getInstanceOf("self"));
            	
            	explicit_disp.add("label", "dispatch" + String.valueOf(dispatch_index));
            	
            	MethodSymbol ms = (MethodSymbol)curentCS.lookupMethod(ctx.funcName.getText());
            	explicit_disp.add("m_offset", ms.getOffset());
            	
            	explicit_disp.add("file", "str_const6");
            	explicit_disp.add("line", ctx.funcName.getLine());
            	
            	is_self = false;
            	dispatch_index += 1 ;
            	return explicit_disp;
            }
            
            @Override
            public ST visitInt(CoolParser.IntContext ctx) {
            	String tag = intConstMapping.get(Integer.parseInt(ctx.INT_LITERAL().getText()));
            	
            	ST int_load = templates.getInstanceOf("const_load");
            	int_load.add("tag", tag);
            	
            	return int_load;
            }
            
            @Override
            public ST visitString(CoolParser.StringContext ctx) {
            	String tag = classTagMapping.get(ctx.STRING().getText());
            	
            	ST string_load = templates.getInstanceOf("const_load");
            	string_load.add("tag", tag);
            	
            	return string_load;
            }
            
            @Override
            public ST visitTrue(CoolParser.TrueContext ctx) {
            	ST bool_load = templates.getInstanceOf("const_load");
            	bool_load.add("tag", "bool_const1");
            	return bool_load;
            }

            @Override
            public ST visitFalse(CoolParser.FalseContext ctx) {
            	ST bool_load = templates.getInstanceOf("const_load");
            	bool_load.add("tag", "bool_const0");
            	return bool_load;
            }
            
            @Override
            public ST visitId(CoolParser.IdContext ctx) { 
            	ST id = null;
            	if (!ctx.ID_NAME().getText().equals("self")) {
            		
            		id = templates.getInstanceOf("id");
            		IdSymbol is = (IdSymbol) currentScope.lookupAttribute(ctx.ID_NAME().getText());
            		id.add("offset", is.getOffset());
            		id.add("location", is.getLocation());
            	} else {
            		id = templates.getInstanceOf("self");
            		is_self = true;
            	}
            	return id;
            }
            
            @Override
            public ST visitAtrib(CoolParser.AtribContext ctx) {
            	ST atrib = templates.getInstanceOf("assign");
            	atrib.add("expr", visit(ctx.exp));
            	IdSymbol is = (IdSymbol) currentScope.lookupAttribute(ctx.varName.getText());
            	atrib.add("location", is.getLocation());
            	atrib.add("offset", is.getOffset());
            	return atrib;
            }
            
            @Override
            public ST visitBlock(CoolParser.BlockContext ctx) { 
        		ST block = templates.getInstanceOf("sequence");
        		for (var e : ctx.blockList)
        			block.add("e", visit(e));
        		return block;
        	}
            
            @Override
            public ST visitLet(CoolParser.LetContext ctx) {
            	ST let = templates.getInstanceOf("let");
            	oldScope = currentScope;
            	currentScope = nodeToScopeMapping.get(ctxToNodeMapping.get(ctx));
            	for (var e : ctx.letAttrib)
            		let.add("var", visit(e));
            	
            	let.add("body", visit(ctx.body));
            	
            	
            	currentScope = oldScope;
            	return let;
            }
            
            @Override
            public ST visitLet_attr(CoolParser.Let_attrContext ctx) {
            	local_vars++;
            	ST letAtt = templates.getInstanceOf("assign");
            	
            	
            	IdSymbol is = (IdSymbol)currentScope.lookupAttribute(ctx.atribName.getText());
            	letAtt.add("offset", is.getOffset());
            	letAtt.add("location", is.getLocation());
            	
            	if (ctx.atribInit != null)
            		letAtt.add("expr", visit(ctx.atribInit));
            	else {
            		ST const_declr = templates.getInstanceOf("const_load");
            		if (is.getType().getName().equals("String")) {
            			const_declr.add("tag", "str_const0");
            		}
            		else if (is.getType().getName().equals("Bool")) {
            			const_declr.add("tag", "bool_const0");
            		}
            		else if (is.getType().getName().equals("Int")) {
            			const_declr.add("tag", "int_const0");
            		}
            		else {
            			const_declr.add("tag", "0");
            		}
            		letAtt.add("expr", const_declr);
            	}
            	
            	return letAtt;
            }
            
            @Override
            public ST visitNew(CoolParser.NewContext ctx) {
            	ST newt = null;
            	if (!ctx.TYPE_NAME().getText().equals("SELF_TYPE")) {
            		newt = templates.getInstanceOf("new");
            		newt.add("class", ctx.TYPE_NAME().getText());
            	} else {
            		newt = templates.getInstanceOf("selftype");
            	}
            	
            	return newt;
            }
            
            @Override
            public ST visitIsvoid(CoolParser.IsvoidContext ctx) {
            	ST isvoid = templates.getInstanceOf("isvoid");
            	isvoid.add("expr", visit(ctx.exp));
            	isvoid.add("label", "isvoid" + isvoid_index);
            	isvoid_index++;
            	return isvoid;
            }
            
            @Override
            public ST visitIf(CoolParser.IfContext ctx) {
            	ST ift = templates.getInstanceOf("if");
            	ift.add("cond", visit(ctx.cond));
            	ift.add("else_label", "else" + else_index);
            	else_index++;
            	ift.add("end_label", "end" + end_index);
            	end_index++;
            	ift.add("thenBranch", visit(ctx.thenBranch));
            	ift.add("elseBranch", visit(ctx.elseBranch));
            	return ift;
            }
            
            @Override
            public ST visitNot(CoolParser.NotContext ctx) {
            	ST not = templates.getInstanceOf("not");
            	not.add("expr", visit(ctx.exp));
            	not.add("label", "not" + not_index);
            	not_index++;
            	
            	return not;
            }
            
            @Override
            public ST visitPlus_aritm(CoolParser.Plus_aritmContext ctx) {
            	ST plus = templates.getInstanceOf("operation");
            	plus.add("left_op", visit(ctx.leftOp));
            	plus.add("right_op", visit(ctx.rightOp));
            	plus.add("op", "add");
            	return plus;
            }
            
            @Override
            public ST visitMul_aritm(CoolParser.Mul_aritmContext ctx) {
            	ST mul = templates.getInstanceOf("operation");
            	mul.add("left_op", visit(ctx.leftOp));
            	mul.add("right_op", visit(ctx.rightOp));
            	mul.add("op", "mul");
            	return mul;
            }
            
            @Override
            public ST visitMinus_aritm(CoolParser.Minus_aritmContext ctx) {
            	ST minus = templates.getInstanceOf("operation");
            	minus.add("left_op", visit(ctx.leftOp));
            	minus.add("right_op", visit(ctx.rightOp));
            	minus.add("op", "sub");
            	return minus;
            }
            
            @Override
            public ST visitDiv_aritm(CoolParser.Div_aritmContext ctx) {
            	ST div = templates.getInstanceOf("operation");
            	div.add("left_op", visit(ctx.leftOp));
            	div.add("right_op", visit(ctx.rightOp));
            	div.add("op", "div");
            	return div;
            }
            
            @Override
            public ST visitParanthese_expr(CoolParser.Paranthese_exprContext ctx) {
            	return visit(ctx.exp);
            }
            
            @Override
            public ST visitTilda(CoolParser.TildaContext ctx) {
            	ST tilda = templates.getInstanceOf("tilda");
            	tilda.add("expr", visit(ctx.exp));
            	return tilda;
            }
            
            @Override
            public ST visitEq_rel(CoolParser.Eq_relContext ctx) {
            	ST eq = templates.getInstanceOf("eq");
            	eq.add("left_op", visit(ctx.leftOp));
            	eq.add("right_op", visit(ctx.rightOp));
            	eq.add("label", "eq" + eq_index);
            	eq_index++;
            	return eq;
            }
            
            @Override
            public ST visitLess_eq_rel(CoolParser.Less_eq_relContext ctx) {
            	ST lesseq = templates.getInstanceOf("comp");
            	lesseq.add("left_op", visit(ctx.leftOp));
            	lesseq.add("right_op", visit(ctx.rightOp));
            	lesseq.add("label", "lesseq" + lesseq_index);
            	lesseq.add("op", "ble");
            	lesseq_index++;
            	return lesseq;
            }
            
            @Override
            public ST visitLess_rel(CoolParser.Less_relContext ctx) {
            	ST less = templates.getInstanceOf("comp");
            	less.add("left_op", visit(ctx.leftOp));
            	less.add("right_op", visit(ctx.rightOp));
            	less.add("label", "less" + less_index);
            	less.add("op", "blt");
            	less_index++;
            	return less;
            }
            
            @Override
            public ST visitWhile(CoolParser.WhileContext ctx) {
            	ST whilet = templates.getInstanceOf("while");
            	whilet.add("cond", visit(ctx.cond));
            	whilet.add("cond_label", "while_cond" + while_cond_index);
            	while_cond_index++;
            	whilet.add("body", visit(ctx.body));
            	whilet.add("end_label", "while_end" + while_end_index);
            	while_end_index++;
            	return whilet;
            }
            
            /*
            @Override
            public ST visitCase(CoolParser.CaseContext ctx) {
            	local_vars++;
            	ST caset = templates.getInstanceOf("case");
            	
            	Case c = (Case) ctxToNodeMapping.get(ctx);
            	oldScope = currentScope;
            	currentScope = nodeToScopeMapping.get(c);
            	
            	caset.add("expr", visit(ctx.cond));
            	caset.add("file", "str_const6");
            	caset.add("line", ctx.start.getLine());
            	
            	caset.add("label", "case" + case_index);
            	case_index++;
            	
            	IdSymbol is = (IdSymbol)currentScope.lookupAttribute(ctx.cond.getText());
            	caset.add("location", "fp");
            	caset.add("offset", -4);
            	caset.add("end_label", "case_end" + case_end_index);
            	case_end = "case_end" + case_end_index;
            	case_end_index++;
            	
            	for (var e : ctx.caseBranches) {
            		caset.add("branch", visit(e));
            	}
            	
            	currentScope = oldScope;
            	return caset;
            }
            
            @Override
            public ST visitCase_attr(CoolParser.Case_attrContext ctx) {
            	ST case_attr = templates.getInstanceOf("case_branch");
            	
            	CaseBranch cb = (CaseBranch) ctxToNodeMapping.get(ctx);
            	oldScope = currentScope;
            	currentScope = nodeToScopeMapping.get(cb);
            	
            	case_attr.add("body", visit(ctx.exp));
            	case_attr.add("label", "case_branch" + case_branch_index);
            	case_branch_index++;
            	case_attr.add("end_label", case_end);
            	
            	String varType = ctx.varType.getText();
            	if (varType.equals("Int") || varType.equals("String") || varType.equals("Main")) {
            		case_attr.add("last_index", classNameIndexMapping.get(varType));
            		case_attr.add("first_index", classNameIndexMapping.get(varType));
            	} else if (varType.equals("Object")) {
            		case_attr.add("last_index", 0);
            		case_attr.add("first_index", max_tag);
            	} else {
            		case_attr.add("last_index", classNameIndexMapping.get(varType));
            		case_attr.add("first_index", 5);
            	}
            	
            	currentScope = oldScope;
            	return case_attr;
            	
            }
            */
            
        };
        
        codeGenerator2.visit(globalTree);
        
        
        System.out.println(program_template.render());
    }
}
