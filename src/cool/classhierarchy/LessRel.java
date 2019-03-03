package cool.classhierarchy;

public class LessRel extends Expression {
	Expression leftOp;
	Expression rightOp;

	public Expression getLeftOp() {
		return leftOp;
	}

	public Expression getRightOp() {
		return rightOp;
	}

	String sign = "<";
	
	public LessRel(Expression leftOp, Expression rightOp) {
		this.leftOp = leftOp;
		this.rightOp = rightOp;
	}
	
	@Override
	public void printContents(int indent) {
		String indentation = "";
		for (int i = 0; i < indent; i++)
			indentation += " ";
		
		System.out.println(indentation + sign);
		indentation += "  ";
		indent += 2;
		leftOp.printContents(indent);
		rightOp.printContents(indent);
	}

	
}
