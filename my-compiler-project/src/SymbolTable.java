import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class SymbolTable {
    private static List<Hashtable<String, Sym>> scopeStack;
    
    public class Sym {
        private String name;
        private int type;
        private List<Integer> paramTypes;  // Liste der Parametertypen
        private int offset;
        private boolean isLocal;
        private int numLocalVars = 0;
        private int numParams = 0;

        // Konstruktor für Sym, der Parametertypen optional hinzufügt
        public Sym(String name, int type) {
            this.name = name;
            this.type = type;
            this.paramTypes = new ArrayList<>();
            this.numLocalVars = 0; // Standardwert
            this.numParams = 0;    // Standardwert
        }
        
        public Sym() {
        	
        }

        public Sym(String name, int type, List<Integer> paramTypes) {
            this.name = name;
            this.type = type;
            this.paramTypes = paramTypes;
            this.numLocalVars = 0;
            this.numParams = paramTypes.size(); // Setze Parameteranzahl basierend auf Liste
        }

        @Override
        public String toString() {
            return Types.ToString(type);
        }

        public int getSymType() {
            return type;
        }

        public List<Integer> getParamTypes() {
            return paramTypes;
        }
        
        public boolean isLocal() {
        	return isLocal;
        }
        public void setIsLocal(boolean value) {
        	this.isLocal = value;
        }
        
        public void setOffset( int value) {
        	this.offset = value;
        }
        public int getOffset() {
        	return offset;
        }
        
        public int getNumParams() {
        	return numParams;
        }
        public void setNumParams(int numParams) {
            this.numParams = numParams;
        }
        
        public int getNumLocalVars() {
        	return numLocalVars;
        }
        
        public void setNumLocalVars(int numLocalVars) {
            this.numLocalVars = numLocalVars;
        }
       

		public void incrementNumLocalVars() {
			numLocalVars++;
			
		}
		public void incrementNumParams() {
			numParams++;
			
		}
    }
   

    public SymbolTable() {
        scopeStack = new ArrayList<>();
        // Globale Symboltabelle für den äußersten Scope erstellen
        enterScope();
    }

    
    public void enterScope() {
        scopeStack.add(new Hashtable<>());
        //System.out.println("Entered new scope. Total scopes: " + scopeStack.size());
        
    }

    
    public void leaveScope() {
        if (scopeStack.isEmpty()) {
            throw new IllegalStateException("No scope to leave.");
        }
        scopeStack.remove(scopeStack.size() - 1);
       //
       //System.out.println("Left scope. Remaining scopes: " + scopeStack.size());
      
    }

    
    public void insert(String name, Sym sym) {
        if (scopeStack.isEmpty()) {
            throw new IllegalStateException("No scope available.");
        }
        Hashtable<String, Sym> currentScope = scopeStack.get(scopeStack.size() - 1);
        if (currentScope.containsKey(name)) {
            throw new IllegalArgumentException("Multiply declared identifier: " + name);
        }
        currentScope.put(name, sym);
        //System.out.println("Inserted " + name + " into scope level " + (scopeStack.size() - 1));
       
    }


    public static Sym lookup(String name) {
        for (int i = scopeStack.size() - 1; i >= 0; i--) {
            Hashtable<String, Sym> scope = scopeStack.get(i);
            if (scope.containsKey(name)) {
                return scope.get(name);
            }
           
        }
        return null; // Symbol nicht gefunden
    }

    
    public Sym lookupInCurrentScope(String name) {
        if (scopeStack.isEmpty()) {
            throw new IllegalStateException("No scope available.");
        }
        Hashtable<String, Sym> currentScope = scopeStack.get(scopeStack.size() - 1);
        return currentScope.get(name);
    }

    // Debugging-Methode zum Drucken der gesamten Symboltabelle
    public void printSymbolTable() {
       // System.out.println("Current Symbol Table:");
        for (int i = 0; i < scopeStack.size(); i++) {
            System.out.println("Scope Level " + i + ": " + scopeStack.get(i));
        }
    }

  

    
   
    
}
