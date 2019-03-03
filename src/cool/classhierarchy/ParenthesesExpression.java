package cool.classhierarchy;

public class ParenthesesExpression extends Expression {
	Expression exp;

	public ParenthesesExpression(Expression exp) {
		this.exp = exp;
	}
	
	public Expression getExp() {
		return exp;
	}
	
	@Override
	public void printContents(int indent) {
		exp.printContents(indent);
	}
}
