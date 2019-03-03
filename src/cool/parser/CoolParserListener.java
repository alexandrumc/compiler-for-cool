// Generated from CoolParser.g4 by ANTLR 4.7.1

    package cool.parser;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link CoolParser}.
 */
public interface CoolParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link CoolParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(CoolParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link CoolParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(CoolParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link CoolParser#class_def}.
	 * @param ctx the parse tree
	 */
	void enterClass_def(CoolParser.Class_defContext ctx);
	/**
	 * Exit a parse tree produced by {@link CoolParser#class_def}.
	 * @param ctx the parse tree
	 */
	void exitClass_def(CoolParser.Class_defContext ctx);
	/**
	 * Enter a parse tree produced by {@link CoolParser#atribute_def}.
	 * @param ctx the parse tree
	 */
	void enterAtribute_def(CoolParser.Atribute_defContext ctx);
	/**
	 * Exit a parse tree produced by {@link CoolParser#atribute_def}.
	 * @param ctx the parse tree
	 */
	void exitAtribute_def(CoolParser.Atribute_defContext ctx);
	/**
	 * Enter a parse tree produced by {@link CoolParser#param_def}.
	 * @param ctx the parse tree
	 */
	void enterParam_def(CoolParser.Param_defContext ctx);
	/**
	 * Exit a parse tree produced by {@link CoolParser#param_def}.
	 * @param ctx the parse tree
	 */
	void exitParam_def(CoolParser.Param_defContext ctx);
	/**
	 * Enter a parse tree produced by {@link CoolParser#func_def}.
	 * @param ctx the parse tree
	 */
	void enterFunc_def(CoolParser.Func_defContext ctx);
	/**
	 * Exit a parse tree produced by {@link CoolParser#func_def}.
	 * @param ctx the parse tree
	 */
	void exitFunc_def(CoolParser.Func_defContext ctx);
	/**
	 * Enter a parse tree produced by the {@code tilda}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterTilda(CoolParser.TildaContext ctx);
	/**
	 * Exit a parse tree produced by the {@code tilda}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitTilda(CoolParser.TildaContext ctx);
	/**
	 * Enter a parse tree produced by the {@code mul_aritm}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterMul_aritm(CoolParser.Mul_aritmContext ctx);
	/**
	 * Exit a parse tree produced by the {@code mul_aritm}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitMul_aritm(CoolParser.Mul_aritmContext ctx);
	/**
	 * Enter a parse tree produced by the {@code string}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterString(CoolParser.StringContext ctx);
	/**
	 * Exit a parse tree produced by the {@code string}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitString(CoolParser.StringContext ctx);
	/**
	 * Enter a parse tree produced by the {@code isvoid}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterIsvoid(CoolParser.IsvoidContext ctx);
	/**
	 * Exit a parse tree produced by the {@code isvoid}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitIsvoid(CoolParser.IsvoidContext ctx);
	/**
	 * Enter a parse tree produced by the {@code while}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterWhile(CoolParser.WhileContext ctx);
	/**
	 * Exit a parse tree produced by the {@code while}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitWhile(CoolParser.WhileContext ctx);
	/**
	 * Enter a parse tree produced by the {@code func_call}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterFunc_call(CoolParser.Func_callContext ctx);
	/**
	 * Exit a parse tree produced by the {@code func_call}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitFunc_call(CoolParser.Func_callContext ctx);
	/**
	 * Enter a parse tree produced by the {@code not}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterNot(CoolParser.NotContext ctx);
	/**
	 * Exit a parse tree produced by the {@code not}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitNot(CoolParser.NotContext ctx);
	/**
	 * Enter a parse tree produced by the {@code plus_aritm}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterPlus_aritm(CoolParser.Plus_aritmContext ctx);
	/**
	 * Exit a parse tree produced by the {@code plus_aritm}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitPlus_aritm(CoolParser.Plus_aritmContext ctx);
	/**
	 * Enter a parse tree produced by the {@code block}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterBlock(CoolParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by the {@code block}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitBlock(CoolParser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by the {@code let}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterLet(CoolParser.LetContext ctx);
	/**
	 * Exit a parse tree produced by the {@code let}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitLet(CoolParser.LetContext ctx);
	/**
	 * Enter a parse tree produced by the {@code id}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterId(CoolParser.IdContext ctx);
	/**
	 * Exit a parse tree produced by the {@code id}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitId(CoolParser.IdContext ctx);
	/**
	 * Enter a parse tree produced by the {@code if}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterIf(CoolParser.IfContext ctx);
	/**
	 * Exit a parse tree produced by the {@code if}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitIf(CoolParser.IfContext ctx);
	/**
	 * Enter a parse tree produced by the {@code case}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterCase(CoolParser.CaseContext ctx);
	/**
	 * Exit a parse tree produced by the {@code case}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitCase(CoolParser.CaseContext ctx);
	/**
	 * Enter a parse tree produced by the {@code less_rel}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterLess_rel(CoolParser.Less_relContext ctx);
	/**
	 * Exit a parse tree produced by the {@code less_rel}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitLess_rel(CoolParser.Less_relContext ctx);
	/**
	 * Enter a parse tree produced by the {@code new}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterNew(CoolParser.NewContext ctx);
	/**
	 * Exit a parse tree produced by the {@code new}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitNew(CoolParser.NewContext ctx);
	/**
	 * Enter a parse tree produced by the {@code minus_aritm}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterMinus_aritm(CoolParser.Minus_aritmContext ctx);
	/**
	 * Exit a parse tree produced by the {@code minus_aritm}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitMinus_aritm(CoolParser.Minus_aritmContext ctx);
	/**
	 * Enter a parse tree produced by the {@code less_eq_rel}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterLess_eq_rel(CoolParser.Less_eq_relContext ctx);
	/**
	 * Exit a parse tree produced by the {@code less_eq_rel}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitLess_eq_rel(CoolParser.Less_eq_relContext ctx);
	/**
	 * Enter a parse tree produced by the {@code false}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterFalse(CoolParser.FalseContext ctx);
	/**
	 * Exit a parse tree produced by the {@code false}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitFalse(CoolParser.FalseContext ctx);
	/**
	 * Enter a parse tree produced by the {@code int}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterInt(CoolParser.IntContext ctx);
	/**
	 * Exit a parse tree produced by the {@code int}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitInt(CoolParser.IntContext ctx);
	/**
	 * Enter a parse tree produced by the {@code implicit_dispatch}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterImplicit_dispatch(CoolParser.Implicit_dispatchContext ctx);
	/**
	 * Exit a parse tree produced by the {@code implicit_dispatch}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitImplicit_dispatch(CoolParser.Implicit_dispatchContext ctx);
	/**
	 * Enter a parse tree produced by the {@code paranthese_expr}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterParanthese_expr(CoolParser.Paranthese_exprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code paranthese_expr}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitParanthese_expr(CoolParser.Paranthese_exprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code explicit_dispatch}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExplicit_dispatch(CoolParser.Explicit_dispatchContext ctx);
	/**
	 * Exit a parse tree produced by the {@code explicit_dispatch}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExplicit_dispatch(CoolParser.Explicit_dispatchContext ctx);
	/**
	 * Enter a parse tree produced by the {@code atrib}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterAtrib(CoolParser.AtribContext ctx);
	/**
	 * Exit a parse tree produced by the {@code atrib}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitAtrib(CoolParser.AtribContext ctx);
	/**
	 * Enter a parse tree produced by the {@code true}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterTrue(CoolParser.TrueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code true}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitTrue(CoolParser.TrueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code eq_rel}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterEq_rel(CoolParser.Eq_relContext ctx);
	/**
	 * Exit a parse tree produced by the {@code eq_rel}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitEq_rel(CoolParser.Eq_relContext ctx);
	/**
	 * Enter a parse tree produced by the {@code div_aritm}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterDiv_aritm(CoolParser.Div_aritmContext ctx);
	/**
	 * Exit a parse tree produced by the {@code div_aritm}
	 * labeled alternative in {@link CoolParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitDiv_aritm(CoolParser.Div_aritmContext ctx);
	/**
	 * Enter a parse tree produced by {@link CoolParser#case_attr}.
	 * @param ctx the parse tree
	 */
	void enterCase_attr(CoolParser.Case_attrContext ctx);
	/**
	 * Exit a parse tree produced by {@link CoolParser#case_attr}.
	 * @param ctx the parse tree
	 */
	void exitCase_attr(CoolParser.Case_attrContext ctx);
	/**
	 * Enter a parse tree produced by {@link CoolParser#let_attr}.
	 * @param ctx the parse tree
	 */
	void enterLet_attr(CoolParser.Let_attrContext ctx);
	/**
	 * Exit a parse tree produced by {@link CoolParser#let_attr}.
	 * @param ctx the parse tree
	 */
	void exitLet_attr(CoolParser.Let_attrContext ctx);
}