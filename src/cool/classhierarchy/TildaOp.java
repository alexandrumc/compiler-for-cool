package cool.classhierarchy;

public class TildaOp extends Expression{
	String sign = "~";
	Expression exp;

	public Expression getExp() {
		return exp;
	}

	public TildaOp(Expression exp) {
		this.exp = exp;
	}
	
	@Override
	public void printContents(int indent) {
		String indentation = "";
		for (int i = 0; i < indent; i++)
			indentation += " ";
		
		System.out.println(indentation + sign);
		indentation += "  ";
		indent += 2;
		exp.printContents(indent);
	}
	
}
