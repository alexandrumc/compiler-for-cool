package cool.structures;

public class Pair {
	String parentName;
	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public MethodSymbol getMs() {
		return ms;
	}

	public void setMs(MethodSymbol ms) {
		this.ms = ms;
	}

	MethodSymbol ms;
	
	public Pair(String parentName, MethodSymbol ms) {
		this.parentName = parentName;
		this.ms = ms;
	}
	
	
}
