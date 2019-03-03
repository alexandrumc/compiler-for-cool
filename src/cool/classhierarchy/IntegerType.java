package cool.classhierarchy;

public class IntegerType extends Expression{
	String integer;
	
	public IntegerType(String integer) {
		this.integer = integer;
	}
	
	public void printContents(int indent) {
		String indentation = "";
		for (int i = 0; i < indent; i++)
			indentation += " ";
		System.out.println(indentation + integer);
	}
	
}
