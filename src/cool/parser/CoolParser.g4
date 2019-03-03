parser grammar CoolParser;

options {
    tokenVocab = CoolLexer;
}

@header{
    package cool.parser;
}

program: (classList+=class_def)+ EOF;
  
class_def: CLASS className=TYPE_NAME (INHERITS parentName=TYPE_NAME)? O_ACOL (atribute_def | func_def)* C_ACOL DOT_COL;

atribute_def: atribName=ID_NAME TWO_DOTS atribType=TYPE_NAME (ATRB atribInit=expr)? DOT_COL
	;

param_def: paramName=ID_NAME TWO_DOTS paramType=TYPE_NAME
	; 

func_def: methodName=ID_NAME O_RPAR C_RPAR TWO_DOTS methodReturnType=TYPE_NAME O_ACOL body+=expr+ C_ACOL DOT_COL
		|	methodName=ID_NAME O_RPAR (parametersList+=param_def COMMA)* parametersList+=param_def C_RPAR TWO_DOTS methodReturnType=TYPE_NAME O_ACOL body+=expr+ C_ACOL DOT_COL
	;
 
expr: obj=expr DOT methodName=ID_NAME O_RPAR (paramsList+=expr (COMMA paramsList+=expr)*)? C_RPAR 	#explicit_dispatch
	| obj=expr A_ROUND className=TYPE_NAME DOT methodName=ID_NAME O_RPAR (paramsList+=expr (COMMA paramsList+=expr)*)? C_RPAR 	#implicit_dispatch
	| funcName=ID_NAME O_RPAR (paramsList+=expr (COMMA paramsList+=expr)*)? C_RPAR				 # func_call 
    | IF cond=expr THEN thenBranch=expr ELSE elseBranch=expr FI  # if
    | WHILE cond=expr LOOP body=expr POOL						 # while
    | O_ACOL (blockList+=expr DOT_COL)+ C_ACOL								 # block
    | LET letAttrib+=let_attr (COMMA letAttrib+=let_attr)* IN body=expr 	#let
    | CASE cond=expr OF caseBranches+=case_attr+ ESAC		#case
    | NEW var=TYPE_NAME												 # new
    | TILDA exp=expr												 # tilda
    | ISVOID exp=expr												 # isvoid
    | leftOp=expr MUL rightOp=expr												 # mul_aritm
    | leftOp=expr DIV rightOp=expr												 # div_aritm
    | leftOp=expr PLUS rightOp=expr											 # plus_aritm
    | leftOp=expr MINUS rightOp=expr											 # minus_aritm
    | leftOp=expr LESS_EQ rightOp=expr											 # less_eq_rel
    | leftOp=expr LESS rightOp=expr											 # less_rel
    | leftOp=expr sign=EQL rightOp=expr												 # eq_rel
    | NOT exp=expr													 # not
    | varName=ID_NAME ATRB exp=expr											 # atrib
    | O_RPAR exp=expr C_RPAR										 # paranthese_expr
    | ID_NAME                                                    # id
    | INT_LITERAL                                                # int
    | STRING												  	 # string
    | TRUE													     # true
    | FALSE													     # false
    ;

case_attr: varName=ID_NAME TWO_DOTS varType=TYPE_NAME EQ_MORE exp=expr DOT_COL;
let_attr: atribName=ID_NAME TWO_DOTS atribType=TYPE_NAME (ATRB atribInit=expr)?;
