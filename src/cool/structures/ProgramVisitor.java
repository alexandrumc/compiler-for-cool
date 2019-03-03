package cool.structures;

import cool.classhierarchy.*;

public interface ProgramVisitor {
	void visit(Program node);
	void visitAttribute(Attribute node);
	void visitAttribution(Attribution node);
	void visitBlock(Block node);
	void visitBoolType(BoolType node);
	void visitCase(Case node);
	void visitCaseBranch(CaseBranch node);
	void visitClassStatement(ClassStatement node);
	void visitClassType(ClassType node);
	void visitDivOp(DivOp node);
	void visitEqRel(EqRel node);
	void visitExplicitDispatch(ExplicitDispatch node);
	void visitExpression(Expression node);
	void visitFuncCall(FuncCall node);
	void visitIDType(IDType node);
	void visitIf(If node);
	void visitIntegerType(IntegerType node);
	void visitIsvoidOp(IsvoidOp node);
	void visitLessEqRel(LessEqRel node);
	void visitLessRel(LessRel node);
	void visitLet(Let node);
	void visitLetAttribute(LetAttribute node);
	void visitMethod(Method node);
	void visitMulOp(MulOp node);
	void visitNewOp(NewOp node);
	void visitNotOp(NotOp node);
	void visitParam(Param node);
	void visitParenthesesExpression(ParenthesesExpression node);
	void visitPlusOp(PlusOp node);
	void visitProgram(Program node);
	void visitStringType(StringType node);
	void visitSubstOp(SubstOp node);
	void visitTildaOp(TildaOp node);
	void visitWhile(While node);
}
