package cool.classhierarchy;

public class While extends Expression {
	Expression cond;
	public Expression getCond() {
		return cond;
	}

	public void setCond(Expression cond) {
		this.cond = cond;
	}

	public Expression getBody() {
		return body;
	}

	public void setBody(Expression body) {
		this.body = body;
	}

	Expression body;
	String keyword = "while";
	
	public While(Expression cond, Expression body) {
		this.cond = cond;
		this.body = body;
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
		body.printContents(indent);
	}
	
}
