package cool.classhierarchy;

public class If extends Expression{
	Expression cond;
	public Expression getCond() {
		return cond;
	}

	public void setCond(Expression cond) {
		this.cond = cond;
	}

	public Expression getThenBranch() {
		return thenBranch;
	}

	public void setThenBranch(Expression thenBranch) {
		this.thenBranch = thenBranch;
	}

	public Expression getElseBranch() {
		return elseBranch;
	}

	public void setElseBranch(Expression elseBranch) {
		this.elseBranch = elseBranch;
	}

	Expression thenBranch;
	Expression elseBranch;
	String keyword = "if";
	
	public If(Expression cond, Expression thenBranch, Expression elseBranch) {
		this.cond = cond;
		this.thenBranch = thenBranch;
		this.elseBranch = elseBranch;
	}
	
	@Override
	public void printContents(int indent) {
		String indentation = "";
		for (int i = 0; i < indent; i++)
			indentation += " ";
		
		System.out.println(indentation + keyword);
		indentation += "  ";
		indent += 2;
		cond.printContents(indent);
		thenBranch.printContents(indent);
		elseBranch.printContents(indent);
	}
}
