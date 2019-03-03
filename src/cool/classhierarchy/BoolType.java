package cool.classhierarchy;


public class BoolType extends Expression{
	String boolValue;
	
	public BoolType(String boolValue) {
		this.boolValue = boolValue;
	}
	
	@Override
	public void printContents(int indent) {
		String indentation = "";
		for (int i = 0; i < indent; i++)
			indentation += " ";
		
		System.out.println(indentation + boolValue);
	}
}
