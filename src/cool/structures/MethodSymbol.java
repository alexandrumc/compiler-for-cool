package cool.structures;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class MethodSymbol extends IdSymbol implements Scope {

	protected Map<String, Symbol> symbols = new LinkedHashMap<>();
    
    protected Scope parent;
    
    public String belongingClass;
    

	public void setParent(Scope parent) {
		this.parent = parent;
	}

	public MethodSymbol(String name, Scope parent) {
		super(name);
		this.parent = parent;
	}
	
	
	public String getBelongingClass() {
		return belongingClass;
	}
	
	public void setBelongingClass(String str) {
		this.belongingClass = str;
	}
	
	public int countSymbols() {
		return symbols.size();
	}
	
	public Set<String> getSymbolsNames() {
		return symbols.keySet();
	}

	@Override
	public boolean add(Symbol s) {
		if (symbols.containsKey(s.getName()))
            return false;
        
        symbols.put(s.getName(), s);
        
        return true;
	}

	@Override
	public Symbol lookup(String s) {
		var sym = symbols.get(s);
        
        if (sym != null)
            return sym;
        
        if (parent != null)
            return parent.lookup(s);
        
        return null;
	}

	@Override
	public Scope getParent() {
		return parent;
	}

	@Override
	public boolean contains(String sym) {
		return false;
	}

	@Override
	public Symbol get(String sym) {
		return symbols.get(sym);
	}

	@Override
	public Symbol lookupMethod(String str) {
		return null;
	}

	@Override
	public Symbol lookupAttribute(String str) {
		var sym = symbols.get(str);
		
        if (sym != null)
            return sym;
        
        if (parent != null)
            return parent.lookupAttribute(str);
        
        return null;
	}

	@Override
	public Symbol inheritanceLookupMethod(String str) {
		return null;
	}

	@Override
	public Symbol inheritanceLookupAttribute(String str) {
		return null;
	}

}