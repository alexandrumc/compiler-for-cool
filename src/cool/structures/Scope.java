package cool.structures;

public interface Scope {
    public boolean add(Symbol sym);
    
    public Symbol lookup(String str);
    
    public Scope getParent();
    
    public Symbol lookupMethod(String str);
    
    public Symbol lookupAttribute(String str);
    
    public Symbol inheritanceLookupMethod(String str);
    
    public Symbol inheritanceLookupAttribute(String str);
    
    public boolean contains(String sym);
    
    public Symbol get(String sym);
}
