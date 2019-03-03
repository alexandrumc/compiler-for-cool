package cool.classhierarchy;


public class Attribution extends Expression {
	IDType var;
	String sign = "<-";
	Expression exp;
	
	public IDType getVar() {
		return var;
	}

	public void setVar(IDType var) {
		this.var = var;
	}

	public Expression getExp() {
		return exp;
	}

	public void setExp(Expression exp) {
		this.exp = exp;
	}

	public Attribution(IDType var, Expression exp) {
		this.var = var;
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
		var.printContents(indent);
		exp.printContents(indent);
	}
	
	
}
