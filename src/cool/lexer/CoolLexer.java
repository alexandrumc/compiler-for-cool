// Generated from CoolLexer.g4 by ANTLR 4.7.1

    package cool.lexer;	

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class CoolLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		ERROR=1, WS=2, MULTI_COMM_BEGIN=3, LESS_EQ=4, EQ_MORE=5, DIV=6, ATRB=7, 
		EQL=8, COMMA=9, MUL=10, O_RPAR=11, C_RPAR=12, O_ACOL=13, C_ACOL=14, TWO_DOTS=15, 
		LESS=16, PLUS=17, MINUS=18, DOT_COL=19, DOT=20, TILDA=21, IF=22, THEN=23, 
		ELSE=24, FI=25, TRUE=26, FALSE=27, CLASS=28, INHERITS=29, ISVOID=30, LET=31, 
		LOOP=32, POOL=33, WHILE=34, CASE=35, ESAC=36, NEW=37, OF=38, NOT=39, A_ROUND=40, 
		IN=41, SIMPLE_COMM=42, ID_NAME=43, TYPE_NAME=44, INT_LITERAL=45, STRING=46, 
		STUFF=47, STUFF1=48, WHATEV=49;
	public static final int
		INSIDE_COMM=1;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE", "INSIDE_COMM"
	};

	public static final String[] ruleNames = {
		"SIMPLE_COMM_BEGIN", "SMALL_LETTER", "BIG_LETTER", "DIGIT", "MULTI_COMM_END", 
		"SELF_TYPE", "WS", "MULTI_COMM_BEGIN", "LESS_EQ", "EQ_MORE", "DIV", "ATRB", 
		"EQL", "COMMA", "MUL", "O_RPAR", "C_RPAR", "O_ACOL", "C_ACOL", "TWO_DOTS", 
		"LESS", "PLUS", "MINUS", "DOT_COL", "DOT", "TILDA", "IF", "THEN", "ELSE", 
		"FI", "TRUE", "FALSE", "CLASS", "INHERITS", "ISVOID", "LET", "LOOP", "POOL", 
		"WHILE", "CASE", "ESAC", "NEW", "OF", "NOT", "A_ROUND", "IN", "SIMPLE_COMM", 
		"ID_NAME", "TYPE_NAME", "INT_LITERAL", "STRING", "STUFF", "STUFF1", "WHATEV"
	};

	private static final String[] _LITERAL_NAMES = {
		null, null, null, "'(*'", "'<='", "'=>'", "'/'", "'<-'", "'='", "','", 
		"'*'", "'('", "')'", "'{'", "'}'", "':'", "'<'", "'+'", "'-'", "';'", 
		"'.'", "'~'", "'if'", "'then'", "'else'", "'fi'", "'true'", "'false'", 
		"'class'", "'inherits'", "'isvoid'", "'let'", "'loop'", "'pool'", "'while'", 
		"'case'", "'esac'", "'new'", "'of'", "'not'", "'@'", "'in'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "ERROR", "WS", "MULTI_COMM_BEGIN", "LESS_EQ", "EQ_MORE", "DIV", 
		"ATRB", "EQL", "COMMA", "MUL", "O_RPAR", "C_RPAR", "O_ACOL", "C_ACOL", 
		"TWO_DOTS", "LESS", "PLUS", "MINUS", "DOT_COL", "DOT", "TILDA", "IF", 
		"THEN", "ELSE", "FI", "TRUE", "FALSE", "CLASS", "INHERITS", "ISVOID", 
		"LET", "LOOP", "POOL", "WHILE", "CASE", "ESAC", "NEW", "OF", "NOT", "A_ROUND", 
		"IN", "SIMPLE_COMM", "ID_NAME", "TYPE_NAME", "INT_LITERAL", "STRING", 
		"STUFF", "STUFF1", "WHATEV"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	    
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


	public CoolLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "CoolLexer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
		switch (ruleIndex) {
		case 50:
			STRING_action((RuleContext)_localctx, actionIndex);
			break;
		}
	}
	private void STRING_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0:
			 buildString(getText()); 
			break;
		}
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\63\u015f\b\1\b\1"+
		"\4\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t"+
		"\n\4\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4"+
		"\22\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4"+
		"\31\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4"+
		" \t \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4"+
		"+\t+\4,\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4"+
		"\64\t\64\4\65\t\65\4\66\t\66\4\67\t\67\3\2\3\2\3\2\3\3\3\3\3\4\3\4\3\5"+
		"\3\5\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\b\6\b\u0088"+
		"\n\b\r\b\16\b\u0089\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\13\3"+
		"\13\3\13\3\f\3\f\3\r\3\r\3\r\3\16\3\16\3\17\3\17\3\20\3\20\3\21\3\21\3"+
		"\22\3\22\3\23\3\23\3\24\3\24\3\25\3\25\3\26\3\26\3\27\3\27\3\30\3\30\3"+
		"\31\3\31\3\32\3\32\3\33\3\33\3\34\3\34\3\34\3\35\3\35\3\35\3\35\3\35\3"+
		"\36\3\36\3\36\3\36\3\36\3\37\3\37\3\37\3 \3 \3 \3 \3 \3!\3!\3!\3!\3!\3"+
		"!\3\"\3\"\3\"\3\"\3\"\3\"\3#\3#\3#\3#\3#\3#\3#\3#\3#\3$\3$\3$\3$\3$\3"+
		"$\3$\3%\3%\3%\3%\3&\3&\3&\3&\3&\3\'\3\'\3\'\3\'\3\'\3(\3(\3(\3(\3(\3("+
		"\3)\3)\3)\3)\3)\3*\3*\3*\3*\3*\3+\3+\3+\3+\3,\3,\3,\3-\3-\3-\3-\3.\3."+
		"\3/\3/\3/\3\60\3\60\7\60\u011c\n\60\f\60\16\60\u011f\13\60\3\60\3\60\3"+
		"\60\3\60\3\61\3\61\3\61\3\61\3\61\7\61\u012a\n\61\f\61\16\61\u012d\13"+
		"\61\3\62\3\62\3\62\3\62\3\62\7\62\u0134\n\62\f\62\16\62\u0137\13\62\3"+
		"\62\5\62\u013a\n\62\3\63\6\63\u013d\n\63\r\63\16\63\u013e\3\64\3\64\3"+
		"\64\3\64\3\64\3\64\3\64\3\64\3\64\7\64\u014a\n\64\f\64\16\64\u014d\13"+
		"\64\3\64\3\64\3\64\3\65\3\65\3\65\3\65\3\65\3\66\3\66\3\66\3\66\3\66\3"+
		"\67\3\67\3\67\3\67\4\u011d\u014b\28\4\2\6\2\b\2\n\2\f\2\16\2\20\4\22\5"+
		"\24\6\26\7\30\b\32\t\34\n\36\13 \f\"\r$\16&\17(\20*\21,\22.\23\60\24\62"+
		"\25\64\26\66\278\30:\31<\32>\33@\34B\35D\36F\37H J!L\"N#P$R%T&V\'X(Z)"+
		"\\*^+`,b-d.f/h\60j\61l\62n\63\4\2\3\6\3\2c|\3\2C\\\3\2\62;\5\2\13\f\16"+
		"\17\"\"\2\u0166\2\20\3\2\2\2\2\22\3\2\2\2\2\24\3\2\2\2\2\26\3\2\2\2\2"+
		"\30\3\2\2\2\2\32\3\2\2\2\2\34\3\2\2\2\2\36\3\2\2\2\2 \3\2\2\2\2\"\3\2"+
		"\2\2\2$\3\2\2\2\2&\3\2\2\2\2(\3\2\2\2\2*\3\2\2\2\2,\3\2\2\2\2.\3\2\2\2"+
		"\2\60\3\2\2\2\2\62\3\2\2\2\2\64\3\2\2\2\2\66\3\2\2\2\28\3\2\2\2\2:\3\2"+
		"\2\2\2<\3\2\2\2\2>\3\2\2\2\2@\3\2\2\2\2B\3\2\2\2\2D\3\2\2\2\2F\3\2\2\2"+
		"\2H\3\2\2\2\2J\3\2\2\2\2L\3\2\2\2\2N\3\2\2\2\2P\3\2\2\2\2R\3\2\2\2\2T"+
		"\3\2\2\2\2V\3\2\2\2\2X\3\2\2\2\2Z\3\2\2\2\2\\\3\2\2\2\2^\3\2\2\2\2`\3"+
		"\2\2\2\2b\3\2\2\2\2d\3\2\2\2\2f\3\2\2\2\2h\3\2\2\2\3j\3\2\2\2\3l\3\2\2"+
		"\2\3n\3\2\2\2\4p\3\2\2\2\6s\3\2\2\2\bu\3\2\2\2\nw\3\2\2\2\fy\3\2\2\2\16"+
		"|\3\2\2\2\20\u0087\3\2\2\2\22\u008d\3\2\2\2\24\u0093\3\2\2\2\26\u0096"+
		"\3\2\2\2\30\u0099\3\2\2\2\32\u009b\3\2\2\2\34\u009e\3\2\2\2\36\u00a0\3"+
		"\2\2\2 \u00a2\3\2\2\2\"\u00a4\3\2\2\2$\u00a6\3\2\2\2&\u00a8\3\2\2\2(\u00aa"+
		"\3\2\2\2*\u00ac\3\2\2\2,\u00ae\3\2\2\2.\u00b0\3\2\2\2\60\u00b2\3\2\2\2"+
		"\62\u00b4\3\2\2\2\64\u00b6\3\2\2\2\66\u00b8\3\2\2\28\u00ba\3\2\2\2:\u00bd"+
		"\3\2\2\2<\u00c2\3\2\2\2>\u00c7\3\2\2\2@\u00ca\3\2\2\2B\u00cf\3\2\2\2D"+
		"\u00d5\3\2\2\2F\u00db\3\2\2\2H\u00e4\3\2\2\2J\u00eb\3\2\2\2L\u00ef\3\2"+
		"\2\2N\u00f4\3\2\2\2P\u00f9\3\2\2\2R\u00ff\3\2\2\2T\u0104\3\2\2\2V\u0109"+
		"\3\2\2\2X\u010d\3\2\2\2Z\u0110\3\2\2\2\\\u0114\3\2\2\2^\u0116\3\2\2\2"+
		"`\u0119\3\2\2\2b\u0124\3\2\2\2d\u0139\3\2\2\2f\u013c\3\2\2\2h\u0140\3"+
		"\2\2\2j\u0151\3\2\2\2l\u0156\3\2\2\2n\u015b\3\2\2\2pq\7/\2\2qr\7/\2\2"+
		"r\5\3\2\2\2st\t\2\2\2t\7\3\2\2\2uv\t\3\2\2v\t\3\2\2\2wx\t\4\2\2x\13\3"+
		"\2\2\2yz\7,\2\2z{\7+\2\2{\r\3\2\2\2|}\7U\2\2}~\7G\2\2~\177\7N\2\2\177"+
		"\u0080\7H\2\2\u0080\u0081\7a\2\2\u0081\u0082\7V\2\2\u0082\u0083\7[\2\2"+
		"\u0083\u0084\7R\2\2\u0084\u0085\7G\2\2\u0085\17\3\2\2\2\u0086\u0088\t"+
		"\5\2\2\u0087\u0086\3\2\2\2\u0088\u0089\3\2\2\2\u0089\u0087\3\2\2\2\u0089"+
		"\u008a\3\2\2\2\u008a\u008b\3\2\2\2\u008b\u008c\b\b\2\2\u008c\21\3\2\2"+
		"\2\u008d\u008e\7*\2\2\u008e\u008f\7,\2\2\u008f\u0090\3\2\2\2\u0090\u0091"+
		"\b\t\3\2\u0091\u0092\b\t\2\2\u0092\23\3\2\2\2\u0093\u0094\7>\2\2\u0094"+
		"\u0095\7?\2\2\u0095\25\3\2\2\2\u0096\u0097\7?\2\2\u0097\u0098\7@\2\2\u0098"+
		"\27\3\2\2\2\u0099\u009a\7\61\2\2\u009a\31\3\2\2\2\u009b\u009c\7>\2\2\u009c"+
		"\u009d\7/\2\2\u009d\33\3\2\2\2\u009e\u009f\7?\2\2\u009f\35\3\2\2\2\u00a0"+
		"\u00a1\7.\2\2\u00a1\37\3\2\2\2\u00a2\u00a3\7,\2\2\u00a3!\3\2\2\2\u00a4"+
		"\u00a5\7*\2\2\u00a5#\3\2\2\2\u00a6\u00a7\7+\2\2\u00a7%\3\2\2\2\u00a8\u00a9"+
		"\7}\2\2\u00a9\'\3\2\2\2\u00aa\u00ab\7\177\2\2\u00ab)\3\2\2\2\u00ac\u00ad"+
		"\7<\2\2\u00ad+\3\2\2\2\u00ae\u00af\7>\2\2\u00af-\3\2\2\2\u00b0\u00b1\7"+
		"-\2\2\u00b1/\3\2\2\2\u00b2\u00b3\7/\2\2\u00b3\61\3\2\2\2\u00b4\u00b5\7"+
		"=\2\2\u00b5\63\3\2\2\2\u00b6\u00b7\7\60\2\2\u00b7\65\3\2\2\2\u00b8\u00b9"+
		"\7\u0080\2\2\u00b9\67\3\2\2\2\u00ba\u00bb\7k\2\2\u00bb\u00bc\7h\2\2\u00bc"+
		"9\3\2\2\2\u00bd\u00be\7v\2\2\u00be\u00bf\7j\2\2\u00bf\u00c0\7g\2\2\u00c0"+
		"\u00c1\7p\2\2\u00c1;\3\2\2\2\u00c2\u00c3\7g\2\2\u00c3\u00c4\7n\2\2\u00c4"+
		"\u00c5\7u\2\2\u00c5\u00c6\7g\2\2\u00c6=\3\2\2\2\u00c7\u00c8\7h\2\2\u00c8"+
		"\u00c9\7k\2\2\u00c9?\3\2\2\2\u00ca\u00cb\7v\2\2\u00cb\u00cc\7t\2\2\u00cc"+
		"\u00cd\7w\2\2\u00cd\u00ce\7g\2\2\u00ceA\3\2\2\2\u00cf\u00d0\7h\2\2\u00d0"+
		"\u00d1\7c\2\2\u00d1\u00d2\7n\2\2\u00d2\u00d3\7u\2\2\u00d3\u00d4\7g\2\2"+
		"\u00d4C\3\2\2\2\u00d5\u00d6\7e\2\2\u00d6\u00d7\7n\2\2\u00d7\u00d8\7c\2"+
		"\2\u00d8\u00d9\7u\2\2\u00d9\u00da\7u\2\2\u00daE\3\2\2\2\u00db\u00dc\7"+
		"k\2\2\u00dc\u00dd\7p\2\2\u00dd\u00de\7j\2\2\u00de\u00df\7g\2\2\u00df\u00e0"+
		"\7t\2\2\u00e0\u00e1\7k\2\2\u00e1\u00e2\7v\2\2\u00e2\u00e3\7u\2\2\u00e3"+
		"G\3\2\2\2\u00e4\u00e5\7k\2\2\u00e5\u00e6\7u\2\2\u00e6\u00e7\7x\2\2\u00e7"+
		"\u00e8\7q\2\2\u00e8\u00e9\7k\2\2\u00e9\u00ea\7f\2\2\u00eaI\3\2\2\2\u00eb"+
		"\u00ec\7n\2\2\u00ec\u00ed\7g\2\2\u00ed\u00ee\7v\2\2\u00eeK\3\2\2\2\u00ef"+
		"\u00f0\7n\2\2\u00f0\u00f1\7q\2\2\u00f1\u00f2\7q\2\2\u00f2\u00f3\7r\2\2"+
		"\u00f3M\3\2\2\2\u00f4\u00f5\7r\2\2\u00f5\u00f6\7q\2\2\u00f6\u00f7\7q\2"+
		"\2\u00f7\u00f8\7n\2\2\u00f8O\3\2\2\2\u00f9\u00fa\7y\2\2\u00fa\u00fb\7"+
		"j\2\2\u00fb\u00fc\7k\2\2\u00fc\u00fd\7n\2\2\u00fd\u00fe\7g\2\2\u00feQ"+
		"\3\2\2\2\u00ff\u0100\7e\2\2\u0100\u0101\7c\2\2\u0101\u0102\7u\2\2\u0102"+
		"\u0103\7g\2\2\u0103S\3\2\2\2\u0104\u0105\7g\2\2\u0105\u0106\7u\2\2\u0106"+
		"\u0107\7c\2\2\u0107\u0108\7e\2\2\u0108U\3\2\2\2\u0109\u010a\7p\2\2\u010a"+
		"\u010b\7g\2\2\u010b\u010c\7y\2\2\u010cW\3\2\2\2\u010d\u010e\7q\2\2\u010e"+
		"\u010f\7h\2\2\u010fY\3\2\2\2\u0110\u0111\7p\2\2\u0111\u0112\7q\2\2\u0112"+
		"\u0113\7v\2\2\u0113[\3\2\2\2\u0114\u0115\7B\2\2\u0115]\3\2\2\2\u0116\u0117"+
		"\7k\2\2\u0117\u0118\7p\2\2\u0118_\3\2\2\2\u0119\u011d\5\4\2\2\u011a\u011c"+
		"\13\2\2\2\u011b\u011a\3\2\2\2\u011c\u011f\3\2\2\2\u011d\u011e\3\2\2\2"+
		"\u011d\u011b\3\2\2\2\u011e\u0120\3\2\2\2\u011f\u011d\3\2\2\2\u0120\u0121"+
		"\7\f\2\2\u0121\u0122\3\2\2\2\u0122\u0123\b\60\2\2\u0123a\3\2\2\2\u0124"+
		"\u012b\5\6\3\2\u0125\u012a\5\6\3\2\u0126\u012a\5\b\4\2\u0127\u012a\5\n"+
		"\5\2\u0128\u012a\7a\2\2\u0129\u0125\3\2\2\2\u0129\u0126\3\2\2\2\u0129"+
		"\u0127\3\2\2\2\u0129\u0128\3\2\2\2\u012a\u012d\3\2\2\2\u012b\u0129\3\2"+
		"\2\2\u012b\u012c\3\2\2\2\u012cc\3\2\2\2\u012d\u012b\3\2\2\2\u012e\u0135"+
		"\5\b\4\2\u012f\u0134\5\b\4\2\u0130\u0134\5\6\3\2\u0131\u0134\5\n\5\2\u0132"+
		"\u0134\7a\2\2\u0133\u012f\3\2\2\2\u0133\u0130\3\2\2\2\u0133\u0131\3\2"+
		"\2\2\u0133\u0132\3\2\2\2\u0134\u0137\3\2\2\2\u0135\u0133\3\2\2\2\u0135"+
		"\u0136\3\2\2\2\u0136\u013a\3\2\2\2\u0137\u0135\3\2\2\2\u0138\u013a\5\16"+
		"\7\2\u0139\u012e\3\2\2\2\u0139\u0138\3\2\2\2\u013ae\3\2\2\2\u013b\u013d"+
		"\5\n\5\2\u013c\u013b\3\2\2\2\u013d\u013e\3\2\2\2\u013e\u013c\3\2\2\2\u013e"+
		"\u013f\3\2\2\2\u013fg\3\2\2\2\u0140\u014b\7$\2\2\u0141\u0142\7\61\2\2"+
		"\u0142\u0143\7\61\2\2\u0143\u014a\7$\2\2\u0144\u0145\7\61\2\2\u0145\u0146"+
		"\7\61\2\2\u0146\u0147\7\61\2\2\u0147\u014a\7\61\2\2\u0148\u014a\13\2\2"+
		"\2\u0149\u0141\3\2\2\2\u0149\u0144\3\2\2\2\u0149\u0148\3\2\2\2\u014a\u014d"+
		"\3\2\2\2\u014b\u014c\3\2\2\2\u014b\u0149\3\2\2\2\u014c\u014e\3\2\2\2\u014d"+
		"\u014b\3\2\2\2\u014e\u014f\7$\2\2\u014f\u0150\b\64\4\2\u0150i\3\2\2\2"+
		"\u0151\u0152\5\22\t\2\u0152\u0153\3\2\2\2\u0153\u0154\b\65\3\2\u0154\u0155"+
		"\b\65\2\2\u0155k\3\2\2\2\u0156\u0157\5\f\6\2\u0157\u0158\3\2\2\2\u0158"+
		"\u0159\b\66\5\2\u0159\u015a\b\66\2\2\u015am\3\2\2\2\u015b\u015c\13\2\2"+
		"\2\u015c\u015d\3\2\2\2\u015d\u015e\b\67\2\2\u015eo\3\2\2\2\16\2\3\u0089"+
		"\u011d\u0129\u012b\u0133\u0135\u0139\u013e\u0149\u014b\6\b\2\2\7\3\2\3"+
		"\64\2\6\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}