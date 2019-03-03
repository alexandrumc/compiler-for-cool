package cool.classhierarchy;

public class StringType extends Expression{
	String stringName;

	public StringType(String stringName) {
		this.stringName = stringName;
	}
	
	@Override
	public void printContents(int indent) {
		String indentation = "";
		for (int i = 0; i < indent; i++)
			indentation += " ";
		
		System.out.println(indentation + stringName);
	}
}
