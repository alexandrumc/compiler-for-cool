package cool.structures;

import java.util.*;

public class DefaultScope implements Scope {
    
    private Map<String, Symbol> symbols = new LinkedHashMap<>();
    
    private Scope parent;
    
    public DefaultScope(Scope parent) {
        this.parent = parent;
    }

    @Override
    public boolean add(Symbol sym) {
        if (symbols.containsKey(sym.getName()))
            return false;
        
        symbols.put(sym.getName(), sym);
        

        return true;
    }
    
    public Symbol getSymbol(String symName) {
    	return symbols.get(symName);
    }
    
    public Set<String> getSymbolsNames() {
    	return symbols.keySet();
    }

    @Override
    public Symbol lookup(String name) {
        var sym = symbols.get(name);
        
        if (sym != null)
            return sym;
        
        if (parent != null)
            return parent.lookup(name);
        
        return null;
    }

    @Override
    public Scope getParent() {
        return parent;
    }
    
    @Override
    public String toString() {
        return symbols.values().toString();
    }

	@Override
	public boolean contains(String sym) {
		return symbols.containsKey(sym);
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
