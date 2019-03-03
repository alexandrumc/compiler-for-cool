package cool.structures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;


public class ClassScope extends Symbol implements Scope{
	public HashMap<String, IdSymbol> attributes;
	public HashMap<String, MethodSymbol> methods;
	Scope parent = null;
	public ArrayList<IdSymbol> allAttributes;
	public ArrayList<Pair> allMethods;
	public ArrayList<ClassScope> parents;
	
	public ClassScope(String name) {
		super(name);
		this.attributes = new LinkedHashMap<>();
		this.methods = new LinkedHashMap<>();
		this.allAttributes = new ArrayList<>();
		this.allMethods = new ArrayList<>();
		this.parents = new ArrayList<>();
	}
	
	public void setParent(Scope parent) {
		this.parent = parent;
	}

	public String getName() {
		return this.name;
	}
	
	public void print() {
		String res = "Metodele prezente: ";
		for (String s : methods.keySet()) {
			res += s;
			res += " ";
		}
		System.err.println(res);
	}
	
	public void fillAttributes() {
		for (String s : attributes.keySet()) {
			if (attributes.get(s).getName() != "self")
				this.allAttributes.add(attributes.get(s));
		}
		Collections.reverse(allAttributes);
		ClassScope parentcs = (ClassScope) parent;
		while (parentcs != null) {
			ArrayList<IdSymbol> aux = new ArrayList<IdSymbol>();
			for (String s : parentcs.attributes.keySet()) {
				if (parentcs.attributes.get(s).getName() != "self") {
					aux.add(parentcs.attributes.get(s));
				}
			}
			Collections.reverse(aux);
			this.allAttributes.addAll(aux);
			parentcs = (ClassScope)parentcs.parent;
		}
		Collections.reverse(allAttributes);
		int offset = 12;
		for (IdSymbol is : allAttributes) {
			if (is.getOffset() == -1) {
				is.setOffset(offset);
			} else {
				offset = is.getOffset() + 4;
			}
			is.setLocation("s0");
		}
	}
	
	public void fillMethods() {
		parents.add(this);
		ClassScope parentcs = (ClassScope) parent;

		while (parentcs != null) {
			parents.add(parentcs);
			parentcs = (ClassScope)parentcs.parent;
		}
		
		Collections.reverse(parents);
		
		HashMap<String, Pair> stringToPair = new HashMap<>();
		for (ClassScope p : parents) {
			for (String s : p.methods.keySet()) {
				MethodSymbol o = p.methods.get(s);
				if (!stringToPair.containsKey(o.getName())) {
					Pair pair = new Pair(p.getName(), o);
					allMethods.add(pair);
					stringToPair.put(o.getName(), pair);
				} else {
					Pair pair = new Pair(p.getName(), o);
					int index = allMethods.indexOf(stringToPair.get(o.getName()));
					allMethods.set(index, pair);
					stringToPair.put(o.getName(), pair);
				}
			}
		}
		
		int offset = 0;
		
		for (Pair p : allMethods) {
			
			if (p.getMs().getOffset() == -1) {
				p.getMs().setOffset(offset);
				offset += 4;
			} else {
				offset = p.getMs().getOffset() + 4;
			}
		}
		
	}
	
	@Override
	public boolean add(Symbol sym) {
		if (sym.getClass() == MethodSymbol.class) {
			if (methods.containsKey(sym.getName()))
				return false;
			methods.put(sym.getName(), (MethodSymbol) sym);
		} else if (sym.getClass() == IdSymbol.class) {
			if (attributes.containsKey(sym.getName()))
				return false;
			attributes.put(sym.getName(), (IdSymbol) sym);
		}
        return true;
	}

	@Override
	public Symbol lookup(String str) {
		return null;
	}
	
	@Override
	public Symbol lookupMethod(String str) {
		var sym = methods.get(str);
        
        if (sym != null) {
            return sym;
        }
        
        if (parent != null)
            return parent.lookupMethod(str);
        
        return null;
	}
	
	@Override
	public Symbol lookupAttribute(String str) {
		var sym = attributes.get(str);
		
        if (sym != null)
            return sym;
        
        if (parent != null)
            return parent.lookupAttribute(str);
        
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
		return methods.get(sym);
	}

	@Override
	public Symbol inheritanceLookupMethod(String str) {
		if (parent != null)
            return parent.lookupMethod(str);
		return null;
	}


	@Override
	public Symbol inheritanceLookupAttribute(String str) {
		if (parent != null) {
            return parent.lookupAttribute(str);
		}
		return null;
	}
	
	
}
