package cool.structures;

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

public class ProgramBaseVisitor implements ProgramVisitor{

	@Override
	public void visit(Program node) {
		if (node.getClass() == Attribute.class) {
			visitAttribute((Attribute)node);
		} else if (node.getClass() == Attribution.class) {
			visitAttribution((Attribution)node);
		} else if (node.getClass() == Block.class) {
			visitBlock((Block)node);
		} else if (node.getClass() == BoolType.class) {
			visitBoolType((BoolType)node);
		} else if (node.getClass() == Case.class) {
			visitCase((Case)node);
		} else if (node.getClass() == CaseBranch.class) {
			visitCaseBranch((CaseBranch)node);
		} else if (node.getClass() == ClassStatement.class) {
			visitClassStatement((ClassStatement)node);
		} else if (node.getClass() == ClassType.class) {
			visitClassType((ClassType)node);
		} else if (node.getClass() == DivOp.class) {
			visitDivOp((DivOp)node);
		} else if (node.getClass() == EqRel.class) {
			visitEqRel((EqRel)node);
		} else if (node.getClass() == ExplicitDispatch.class) {
			visitExplicitDispatch((ExplicitDispatch)node);
		} else if (node.getClass() == Expression.class) {
			visitExpression((Expression)node);
		} else if (node.getClass() == FuncCall.class) {
			visitFuncCall((FuncCall)node);
		} else if (node.getClass() == IDType.class) {
			visitIDType((IDType)node);
		} else if (node.getClass() == If.class) {
			visitIf((If)node);
		} else if (node.getClass() == IntegerType.class) {
			visitIntegerType((IntegerType)node);
		} else if (node.getClass() == IsvoidOp.class) {
			visitIsvoidOp((IsvoidOp)node);
		} else if (node.getClass() == LessEqRel.class) {
			visitLessEqRel((LessEqRel)node);
		} else if (node.getClass() == LessRel.class) {
			visitLessRel((LessRel)node);
		} else if (node.getClass() == Let.class) {
			visitLet((Let)node);
		} else if (node.getClass() == LetAttribute.class) {
			visitLetAttribute((LetAttribute)node);
		} else if (node.getClass() == Method.class) {
			visitMethod((Method)node);
		} else if (node.getClass() == MulOp.class) {
			visitMulOp((MulOp)node);
		} else if (node.getClass() == NewOp.class) {
			visitNewOp((NewOp)node);
		} else if (node.getClass() == NotOp.class) {
			visitNotOp((NotOp)node);
		} else if (node.getClass() == Param.class) {
			visitParam((Param)node);
		} else if (node.getClass() == ParenthesesExpression.class) {
			visitParenthesesExpression((ParenthesesExpression)node);
		} else if (node.getClass() == PlusOp.class) {
			visitPlusOp((PlusOp)node);
		} else if (node.getClass() == Program.class) {
			visitProgram((Program)node);
		} else if (node.getClass() == StringType.class) {
			visitStringType((StringType)node);
		} else if (node.getClass() == SubstOp.class) {
			visitSubstOp((SubstOp)node);
		} else if (node.getClass() == TildaOp.class) {
			visitTildaOp((TildaOp)node);
		} else if (node.getClass() == While.class) {
			visitWhile((While)node);
		}
	}

	@Override
	public void visitAttribute(Attribute node) {}

	@Override
	public void visitAttribution(Attribution node) {}

	@Override
	public void visitBlock(Block node) {}

	@Override
	public void visitBoolType(BoolType node) {}

	@Override
	public void visitCase(Case node) {}

	@Override
	public void visitCaseBranch(CaseBranch node) {}

	@Override
	public void visitClassStatement(ClassStatement node) {}
	
	@Override
	public void visitClassType(ClassType node) {}

	@Override
	public void visitDivOp(DivOp node) {}

	@Override
	public void visitEqRel(EqRel node) {}

	@Override
	public void visitExplicitDispatch(ExplicitDispatch node) {}

	@Override
	public void visitExpression(Expression node) {}

	@Override
	public void visitFuncCall(FuncCall node) {}

	@Override
	public void visitIDType(IDType node) {}

	@Override
	public void visitIf(If node) {}

	@Override
	public void visitIntegerType(IntegerType node) {}

	@Override
	public void visitIsvoidOp(IsvoidOp node) {}

	@Override
	public void visitLessEqRel(LessEqRel node) {}

	@Override
	public void visitLessRel(LessRel node) {}

	@Override
	public void visitLet(Let node) {}

	@Override
	public void visitLetAttribute(LetAttribute node) {}

	@Override
	public void visitMethod(Method node) {}

	@Override
	public void visitMulOp(MulOp node) {}

	@Override
	public void visitNewOp(NewOp node) {}

	@Override
	public void visitNotOp(NotOp node) {}

	@Override
	public void visitParam(Param node) {}

	@Override
	public void visitParenthesesExpression(ParenthesesExpression node) {}

	@Override
	public void visitPlusOp(PlusOp node) {}

	@Override
	public void visitProgram(Program node) {}

	@Override
	public void visitStringType(StringType node) {}

	@Override
	public void visitSubstOp(SubstOp node) {}

	@Override
	public void visitTildaOp(TildaOp node) {}

	@Override
	public void visitWhile(While node) {}
	
}
