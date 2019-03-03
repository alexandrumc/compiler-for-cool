lexer grammar CoolLexer;

tokens { ERROR } 

@header{
    package cool.lexer;	
}

@members{    
    private void raiseError(String msg) {
        setText(msg);
        setType(ERROR);
    }
    
    public void buildString(String t) {
		String text = t;

		StringBuilder buffer = new StringBuilder(0);

		for(int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == '\0') {
				raiseError("String contains null character");
				return;
			} else if(text.charAt(i) == '\\') {
				if(text.charAt(i+1) == 'n')
					buffer.append('\n');
				else if(text.charAt(i+1) == 'f')
					buffer.append('\f');
				else if(text.charAt(i+1) == 't')
					buffer.append('\t');
				else if(text.charAt(i+1) == 'b')
					buffer.append('\t');
				else if(text.charAt(i+1) == '\"')
					buffer.append('\"');
				else if(text.charAt(i+1) == '\\')
					buffer.append('\\');
				else
					buffer.append(text.charAt(i+1));
				i++;
			}
			else {
				buffer.append(text.charAt(i));
			}
		}
		String newText = buffer.toString();
		if(newText.length() > 1024) {
			raiseError("String constant too long");
			return;
		}
		setText(newText.substring(1, newText.length()-1));
		return;
	}
}

fragment SIMPLE_COMM_BEGIN: '--';
fragment SMALL_LETTER: [a-z];
fragment BIG_LETTER: [A-Z];
fragment DIGIT : [0-9];
fragment MULTI_COMM_END: '*)';
fragment SELF_TYPE:'SELF_TYPE';

WS: [ \n\f\r\t]+ -> skip; 
MULTI_COMM_BEGIN: '(*' -> pushMode (INSIDE_COMM), skip;
LESS_EQ: '<=';
EQ_MORE: '=>';
DIV: '/';
ATRB: '<-';
EQL: '=';
COMMA: ',';
MUL: '*';
O_RPAR: '(';
C_RPAR: ')';
O_ACOL: '{';
C_ACOL: '}';
TWO_DOTS: ':';
LESS: '<';
PLUS: '+';
MINUS: '-';
DOT_COL: ';';
DOT:'.';
TILDA:'~';

IF: 'if';
THEN: 'then';
ELSE: 'else';
FI: 'fi';
TRUE: 'true';
FALSE: 'false';
CLASS: 'class';
INHERITS: 'inherits';
ISVOID: 'isvoid';
LET: 'let';
LOOP: 'loop';
POOL: 'pool';
WHILE: 'while';
CASE: 'case';
ESAC: 'esac';
NEW: 'new';
OF: 'of';
NOT: 'not';
A_ROUND: '@';
IN: 'in';

SIMPLE_COMM: (SIMPLE_COMM_BEGIN).*?('\n') -> skip;
ID_NAME: (SMALL_LETTER)(SMALL_LETTER | BIG_LETTER | DIGIT | '_')*;
TYPE_NAME: ((BIG_LETTER)(BIG_LETTER | SMALL_LETTER | DIGIT | '_')*) | (SELF_TYPE);
INT_LITERAL: DIGIT+;
STRING: '"'('//"' | '////' | . )*?'"' { buildString(getText()); } ;

mode INSIDE_COMM;
STUFF: MULTI_COMM_BEGIN -> pushMode (INSIDE_COMM), skip;
STUFF1: MULTI_COMM_END -> popMode, skip;
WHATEV: . -> skip;

