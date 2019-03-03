package cool.classhierarchy;


public class IDType extends Expression {
	String idName;

 	public String getIdName() {
		return idName;
	}

	public IDType(String idName) {
		this.idName = idName;
	}
	
	@Override
	public void printContents(int indent) {
		String indentation = "";
		for (int i = 0; i < indent; i++)
			indentation += " ";
		
		System.out.println(indentation + idName);
	}

}
