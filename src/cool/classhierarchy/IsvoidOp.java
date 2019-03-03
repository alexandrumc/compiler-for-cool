package cool.classhierarchy;

public class IsvoidOp extends Expression {
	Expression exp;
	String keyword = "isvoid";

	public Expression getExp() {
		return exp;
	}

	public void setExp(Expression exp) {
		this.exp = exp;
	}

	public IsvoidOp(Expression exp) {
		this.exp = exp;
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
