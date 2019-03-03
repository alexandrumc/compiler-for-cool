package cool.classhierarchy;

public class NotOp extends Expression {
	Expression exp;
	String keyword = "not";

	public NotOp(Expression exp) {
		this.exp = exp;
	}
	
	public Expression getExp() {
		return exp;
	}
	
	@Override
	public void printContents(int indent) {
		String indentation = "";
		for (int i = 0; i < indent; i++)
			indentation += " ";
		
		System.out.println(indentation + keyword);
		indentation += "  ";
		indent += 2;
		exp.printContents(indent);
	}
}
