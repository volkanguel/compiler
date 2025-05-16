import java.io.*;
import java.util.ArrayList;
import java.util.List;
abstract class ASTnode { 
	

	  
    

    public static boolean DEBUG = false;

 
    
    abstract public void decompile(PrintWriter p, int indent);

    public abstract void analyzeType(int params);
		// TODO Automatisch generierter Methodenstub
		
	

	
    protected void doIndent(PrintWriter p, int indent) {
	for (int k=0; k<indent; k++) p.print(" ");
    }
}
class ProgramNode extends ASTnode {
    public ProgramNode(IdNode id, ClassBodyNode classBody) {
	myId = id;
	myClassBody = classBody;
    }

    public void decompile(PrintWriter p, int indent) {
        p.print("public class ");
        myId.decompile(p, 0);
        p.println(" {");
        
        myClassBody.decompile(p, 4);
        p.println("}");
        
    }
    
    
    public  void analyzeName(SymbolTable symTable) {
        // Prüfen, ob die Variable im aktuellen Scope bereits existiert
        if (symTable.lookupInCurrentScope(myId.getIdName()) != null) {
            Errors.fatal(myId.getLineNum(), myId.getCharNum(),
                         "Multiply declared variable: " + myId.getIdName());
        } else {
            // In die Symboltabelle einfügen
        	
            SymbolTable.Sym sym = symTable.new Sym(myId.getIdName(), Types.ClassType);
            symTable.insert(myId.getIdName(), sym);
            myId.setSymbol(sym); // Verknüpfen des Symbols mit der ID
        }
        symTable.enterScope();
        myClassBody.analyzeName(symTable); 
        
        if (!myClassBody.hasMainMethod()) {
            Errors.fatal(0, 0, "Program is missing a main method.");
        }
        
        
        symTable.leaveScope();
    }
    
   
    
    public void codeGen() {
        Codegen.p.println("\t.data");
        Codegen.generateLabeled("_true", ".asciiz", "\"true\"","\"true\"");
        Codegen.generateLabeled("_false", ".asciiz", "\"false\"","\"false\"");
        Codegen.generateLabeled("_.newline", ".asciiz", "\"\\n\"","\"\\n\"");

        /*Codegen.p.println("\t.text");
        Codegen.generate(".globl ", "main");*/

       /* Codegen.genLabel("main", "FUNCTION ENTRY");
        Codegen.generateIndexed("sw", Codegen.RA, Codegen.SP, 0, "PUSH");
        Codegen.generate(Codegen.SUBU, Codegen.SP, Codegen.SP, 4);
        Codegen.generateIndexed("sw", Codegen.FP, Codegen.SP, 0, "PUSH");
        Codegen.generate(Codegen.SUBU, Codegen.SP, Codegen.SP, 4);
        Codegen.generate(Codegen.ADDU, Codegen.FP, Codegen.SP, 8);*/

        myClassBody.codeGen();
      

       /* // Function exit
        Codegen.genLabel(".main_Exit");
        Codegen.generateIndexed("lw", Codegen.RA, Codegen.FP, 0, "restore RA");
        Codegen.generateWithComment(Codegen.MOVE, "restore SP", Codegen.SP, Codegen.FP);
        Codegen.generateIndexed("lw", Codegen.FP, Codegen.FP, -4, "restore FP");
        Codegen.generate(Codegen.JR, Codegen.RA, "return");*/
        
        Codegen.p.flush();
    }
    
    

// In ProgramNode:

     
    
    

    // 2 kids
    private IdNode myId;
    private ClassBodyNode myClassBody;
	@Override
	public void analyzeType(int params) {
		myClassBody.analyzeType(params);
		
	}
}

class ClassBodyNode extends ASTnode {
    public ClassBodyNode(DeclListNode declList) {
	myDeclList = declList;
    }

    public void codeGen() {
        // Kommentar zur Klasseninitialisierung
        Codegen.p.println("# Code generation for class body");
        

        // Codegenerierung für die Deklarationen in der Klasse
        myDeclList.codeGen();

        // Kommentar zum Abschluss der Klassenverarbeitung
        Codegen.p.println("# End of class body");
    }


	

	public void analyzeName(SymbolTable symbolTable) {
		myDeclList.analyzeName(symbolTable);
		
	}

	public void decompile(PrintWriter p, int indent) {
        myDeclList.decompile(p, indent);
    }
	
	

	 public boolean hasMainMethod() {
	        return myDeclList.hasMainMethod();
	    }
    // 1 kid
    private DeclListNode myDeclList;
	@Override
	public void analyzeType(int params) {
		myDeclList.analyzeType(params);
		
	}
}

class DeclListNode extends ASTnode {
    public DeclListNode(Sequence S) {
        if (S == null) {
        	System.err.println("Das Sequence-Argument darf nicht null sein.");
        }
	myDecls = S;
    }

    public void codeGen() {
    
        
        try {
            for (myDecls.start(); myDecls.isCurrent(); myDecls.advance()) {
                // Codegenerierung für jede Deklaration
                DeclNode decl = (DeclNode) myDecls.getCurrent();
                decl.codeGen();
            }
        } catch (NoCurrentException ex) {
            System.err.println("Unexpected NoCurrentException in DeclListNode.codeGen");
            System.exit(-1);
        }
        
       
    }


	public void analyzeName(SymbolTable symbolTable) {
    	 try {
             for (myDecls.start(); myDecls.isCurrent(); myDecls.advance()) {
             ((DeclNode)myDecls.getCurrent()).analyzeName(symbolTable);
             }
         } catch (NoCurrentException ex) {
        	 System.err.println("unexpected NoCurrentException in DeclListNode.print");
             System.exit(-1);
         }
		
	}

	public void decompile(PrintWriter p, int indent) {
        try {
            for (myDecls.start(); myDecls.isCurrent(); myDecls.advance()) {
            ((DeclNode)myDecls.getCurrent()).decompile(p, indent);
            }
        } catch (NoCurrentException ex) {
        	System.err.println("unexpected NoCurrentException in DeclListNode.print");
            System.exit(-1);
        }
    }

    public Sequence getDecls() {
        return myDecls;
    }
    
    public boolean hasMainMethod() {
        try {
            for (myDecls.start(); myDecls.isCurrent(); myDecls.advance()) {
                DeclNode decl = (DeclNode) myDecls.getCurrent();
                if (decl instanceof MethodDeclNode) {
                    MethodDeclNode method = (MethodDeclNode) decl;
                    if (method.isMainMethod()) {
                        return true;
                    }
                }
            }
        } catch (NoCurrentException ex) {
        	System.err.println("unexpected NoCurrentException in DeclListNode.hasMainMethod");
            System.exit(-1);
        }
        return false;
    }

  private Sequence myDecls;



@Override
public void analyzeType(int params) {
	 try {
         for (myDecls.start(); myDecls.isCurrent(); myDecls.advance()) {
         ((DeclNode)myDecls.getCurrent()).analyzeType();
         }
     } catch (NoCurrentException ex) {
    	 System.err.println("unexpected NoCurrentException in DeclListNode.print");
         System.exit(-1);
     }
	
}

}

class FormalsListNode extends ASTnode {
    public FormalsListNode(Sequence S) {
        if (S == null) {
        	System.err.println("Das Sequence-Argument darf nicht null sein.");
        }
        myFormals = S;
    }

    public void decompile(PrintWriter p, int indent) {
        try {
            int totalElements = myFormals.length(); 
            int currentIndex = 0; 
    
            for (myFormals.start(); myFormals.isCurrent(); myFormals.advance()) {
                ((FormalDeclNode) myFormals.getCurrent()).decompile(p, indent);
                currentIndex++;
    
                
                if (currentIndex < totalElements) {
                    p.print(", ");
                }
            }
        } catch (NoCurrentException ex) {
        	System.err.println("unexpected NoCurrentException in DeclListNode.print");
            System.exit(-1);
        }

    }

    private Sequence myFormals;
    private int currentOffset = 0;

	public void analyzeName(SymbolTable symTable) {
		try {
            for (myFormals.start(); myFormals.isCurrent(); myFormals.advance()) {
            ((DeclNode)myFormals.getCurrent()).analyzeName(symTable);
            }
        } catch (NoCurrentException ex) {
        	System.err.println("unexpected NoCurrentException in FormalsListNode.print");
            System.exit(-1);
        }
		
	}

	public boolean isEmpty() {
		if(myFormals.length()==0) {
			return true;
		}
		return false;
	}



	public Sequence getSequence() {
		// TODO Automatisch generierter Methodenstub
		return myFormals;
	}
	
	 public void codeGen() {
		 

	        try {
	            for (myFormals.start(); myFormals.isCurrent(); myFormals.advance()) {
	                // Codegenerierung für jede Deklaration
	                DeclNode decl = (DeclNode) myFormals.getCurrent();
	                decl.codeGen();
	            }
	        } catch (NoCurrentException ex) {
	            System.err.println("Unexpected NoCurrentException in DeclListNode.codeGen");
	            System.exit(-1);
	        }
	    }
	 
	 public int getNumParams() {
		 return myFormals.length();
		 
	 }
	 
	 public int size() {
			int counter = 0;
			  try {
			        for (myFormals.start(); myFormals.isCurrent(); myFormals.advance()) {
			            counter++;
			        }
			    } catch (NoCurrentException ex) {
			    	System.err.println("NoCurrentException in ExpListNode.analyzeType");
			        System.exit(-1);
			    }
			  return counter;
		}
		public FormalDeclNode get(int i) {
			int counter = 0;
			try {
		        for (myFormals.start(); myFormals.isCurrent(); myFormals.advance()) {
		        	
		        	if(counter == i) {
		        		return (FormalDeclNode)myFormals.getCurrent();
		        	}
		        	counter ++;
		        }
		    } catch (NoCurrentException ex) {
		    	System.err.println("NoCurrentException in ExpListNode.analyzeType");
		        System.exit(-1);
		    }
			throw new IndexOutOfBoundsException("Index " + i + " is out of bounds.");
			
		}

		@Override
		public void analyzeType(int params) {
			try {
	            for (myFormals.start(); myFormals.isCurrent(); myFormals.advance()) {
	            FormalDeclNode formal =(FormalDeclNode) myFormals.getCurrent();
	            formal.setOffset(currentOffset);
	            formal.analyzeType();
	           currentOffset+=4;
	            }
	        } catch (NoCurrentException ex) {
	        	System.err.println("unexpected NoCurrentException in FormalsListNode.print");
	            System.exit(-1);
	        }
			
			
		}
	
}

class SwitchGroupListNode extends ASTnode {
    public SwitchGroupListNode(Sequence S) {
        if (S == null) {
        	System.err.println("Das Sequence-Argument darf nicht null sein.");
        }
        mySwitchGroups = S;
    }

    public void decompile(PrintWriter p, int indent) {
        try {
            for (mySwitchGroups.start(); mySwitchGroups.isCurrent(); mySwitchGroups.advance()) {
            ((SwitchGroupNode)mySwitchGroups.getCurrent()).decompile(p, indent);
            }
        } catch (NoCurrentException ex) {
        	System.err.println("unexpected NoCurrentException in DeclListNode.print");
            System.exit(-1);
        }
    }

    

    private Sequence mySwitchGroups;



	public void analyzeName(SymbolTable symTable) {
		// TODO Automatisch generierter Methodenstub
		 try {
	            for (mySwitchGroups.start(); mySwitchGroups.isCurrent(); mySwitchGroups.advance()) {
	            ((SwitchGroupNode)mySwitchGroups.getCurrent()).analyzeName(symTable);;
	            }
	        } catch (NoCurrentException ex) {
	        	System.err.println("unexpected NoCurrentException in DeclListNode.print");
	            System.exit(-1);
	        }
	}

	public void analyzeType() {
		try {
            for (mySwitchGroups.start(); mySwitchGroups.isCurrent(); mySwitchGroups.advance()) {
            ((SwitchGroupNode)mySwitchGroups.getCurrent()).analyzeType();
            }
        } catch (NoCurrentException ex) {
        	System.err.println("unexpected NoCurrentException in DeclListNode.print");
            System.exit(-1);
        }
		
	}

	public int size() {
	    int counter = 0;
	    try {
	        mySwitchGroups.start(); // Beginne die Iteration durch die Gruppe
	        while (mySwitchGroups.isCurrent()) {
	            counter++;
	            mySwitchGroups.advance(); // Weiter zur nächsten Gruppe
	        }
	    } catch (NoCurrentException ex) {
	        System.err.println("unexpected NoCurrentException in SwitchGroupListNode.size");
	        System.exit(-1);
	    }
	    return counter;
	}

	public Sequence getSwitchGroups() {
		return mySwitchGroups;
	}

	protected void codeGen() {
	   
	}



	public ProgramNode getDefaultGroup() {
		// TODO Automatisch generierter Methodenstub
		return null;
	}

	@Override
	public void analyzeType(int params) {
		// TODO Automatisch generierter Methodenstub
		
	}

}

class MethodBodyNode extends ASTnode {
    public MethodBodyNode(DeclListNode declList, StmtListNode stmtList) {
	myDeclList = declList;
	myStmtList = stmtList;
    }
    
    // 2 kids
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;
	private int currentOffset = -8;
    

    public void decompile(PrintWriter p, int indent) {
            p.println("{");
            
            myDeclList.decompile(p, indent+4);
            myStmtList.decompile(p, indent+4);
            doIndent(p,indent);
            p.println("}");
    }

    public void analyzeName(SymbolTable symbolTable) {
       
    	myDeclList.analyzeName(symbolTable);
    	try {
    		for(myDeclList.getDecls().start();myDeclList.getDecls().isCurrent();myDeclList.getDecls().advance()) {
    			if( myDeclList.getDecls().getCurrent() instanceof VarDeclNode) {
    				VarDeclNode var = (VarDeclNode)myDeclList.getDecls().getCurrent();
    				SymbolTable.Sym methodSym = symbolTable.lookupInCurrentScope(var.getName());
    				methodSym.incrementNumLocalVars();
    			}
    		}
    	} catch (NoCurrentException ex) {
	    	 System.err.println("unexpected NoCurrentException in MethodBodyNode.analyzeType");
	         System.exit(-1);
	     }	
    	
    	myStmtList.analyzeName(symbolTable);
    }
    
    @Override
	public void analyzeType(int params) {
		  // Danach die lokalen Variablen analysieren und ihre Offsets setzen
	    if (myDeclList != null) {
	    	 try {
		    	 for(myDeclList.getDecls().start(); myDeclList.getDecls().isCurrent();myDeclList.getDecls().advance()) {
		    		 if(myDeclList.getDecls().getCurrent() instanceof VarDeclNode) {
		    			 VarDeclNode var = (VarDeclNode) myDeclList.getDecls().getCurrent();
			    		 var.setOffset(currentOffset-params*4);
			    		// System.out.println( ", Offset methodbody: " + currentOffset);
			    		 
			    		 currentOffset-=4;
			    		 //System.out.println("plus offset");
			    		 var.analyzeType(params);
		    		 }
		    	 }
		     } catch (NoCurrentException ex) {
		    	 System.err.println("unexpected NoCurrentException in MethodBodyNode.analyzeType");
		         System.exit(-1);
		     }	{
	           
	        }
	    }

	    
	    if (myStmtList != null) {
	        myStmtList.analyzeType(params);
	    }
		
	}
   


	public boolean isReturnTypeCorrect(int inttype) {
		if(myStmtList.hasCorrectReturnType()) {
			return true;
		}
		return false;
	}


	public boolean hasReturnStatement() {
		if(myStmtList.hasReturn()) {
			return true;
		}
		return false;
	}


	

	public int getLocalVarSize() {
		// TODO Automatisch generierter Methodenstub
		return -(2+currentOffset/4);
	}


	
	/*public void codeGen() {
	    // Schritt 1: Speicherplatz für lokale Variablen reservieren
	    int localVarSize = getLocalVarSize(); // Die berechnete Größe für lokale Variablen
	    if (localVarSize > 0) {
	        Codegen.generate("subu", Codegen.SP, Codegen.SP, localVarSize);
	        Codegen.p.println("\t# Reserviere Platz für lokale Variablen (" + localVarSize + " Bytes)");
	    }

	    // Schritt 2: Code für Deklarationen generieren
	    if (myDeclList != null) {
	        myDeclList.codeGen(); // Jede Deklaration kümmert sich um ihren Code
	    }

	    // Schritt 3: Code für Anweisungen generieren
	    if (myStmtList != null) {
	        myStmtList.codeGen(); // Generiert den Code für die Anweisungen
	    }

	    // Schritt 4: Speicherplatz für lokale Variablen freigeben
	    if (localVarSize > 0) {
	        Codegen.generate("addu", Codegen.SP, Codegen.SP, localVarSize);
	        Codegen.p.println("\t# Speicherplatz für lokale Variablen freigegeben");
	    }
	}*/
	public void codeGen(int params) {
	    int localVarSize = getLocalVarSize();
		 Codegen.p.println("\t# Begin method body");
		 
		
		    
		    Codegen.generate("add", Codegen.FP,Codegen.SP,Codegen.ZERO);
		    Codegen.generate("subu",Codegen.SP,Codegen.SP,4);
		    Codegen.generateIndexed("sw",Codegen.RA,Codegen.SP,0);
		    
		    Codegen.generate("subu",Codegen.SP,Codegen.SP,4*(params+localVarSize));
		   // System.out.println("localVarSizeBeginn"+localVarSize);
		 
		
		
		    
		    
	
	        if (myDeclList != null) {
	            myDeclList.codeGen();
	        }
	        
	        if (myStmtList != null) {
	            
	            myStmtList.codeGen(params, localVarSize);
	        }
	    
	        if(Codegen.getCurrentMethod().equals("main")) {
		    	
		    	Codegen.generate(Codegen.LI, Codegen.V0, 10);
	             Codegen.generate("syscall");
		    }
	        else {
	        	 
	        	// System.out.println("paramEnd"+(params));
	        	 Codegen.generate("addu",Codegen.SP,Codegen.SP,4*(params+localVarSize));
	        	 
	 	        Codegen.generateIndexed("lw",Codegen.RA,Codegen.SP,0);
	 	        Codegen.generate("addu",Codegen.SP,Codegen.SP,4);
	  	        
	 	        
	 	        Codegen.generate("addu",Codegen.SP,Codegen.SP,4*(params));
	  	        Codegen.generateIndexed("lw", Codegen.FP,  Codegen.SP,0);
	  	       Codegen.generate("addu",Codegen.SP,Codegen.SP,4);
	  	        Codegen.generate("jr", Codegen.RA);
	        }
	       
	       
		  /*  if (localVarSize > 0) {
		        Codegen.generate("subu", Codegen.SP, Codegen.SP, localVarSize);
		        Codegen.p.println("\t# Reserviere Platz für lokale Variablen (" + localVarSize + " Bytes)");
		    }

	        // Generate code for declarations
	        if (myDeclList != null) {
	            myDeclList.codeGen();
	        }

	        // Generate code for statements
	        if (myStmtList != null) {
	            myStmtList.codeGen();
	        }
	        
	        // Speicherplatz für lokale Variablen freigeben
	      /*  if (localVarSize > 0) {
	            Codegen.generate("addu", Codegen.SP, Codegen.SP, localVarSize);
	            Codegen.p.println("\t# Speicherplatz freigegeben");
	            
	        }*/
	       

	        Codegen.p.println("\t# End method body");
	    
	}


	

}

class StmtListNode extends ASTnode {
    public StmtListNode(Sequence S) {
        if (S == null) {
        	System.err.println("Das Sequence-Argument darf nicht null sein.");
        }
	myStmts = S;
    }
    
	public int size() {
		int counter = 0;
		  try {
		        for (myStmts.start(); myStmts.isCurrent(); myStmts.advance()) {
		            counter++;
		        }
		    } catch (NoCurrentException ex) {
		    	System.err.println("NoCurrentException in ExpListNode.analyzeType");
		        System.exit(-1);
		    }
		  return counter;
	}

  

    public void codeGen(int params, int localVarSize) {
        try {
            for (myStmts.start(); myStmts.isCurrent(); myStmts.advance()) {
                StmtNode stmt = (StmtNode) myStmts.getCurrent();
                stmt.codeGen(params,  localVarSize);
            }
        } catch (NoCurrentException ex) {
            System.err.println("Unexpected NoCurrentException in StmtListNode.codeGen");
            System.exit(-1);
        }
    }

    public StmtNode get(int i) {
		int counter = 0;
		try {
	        for (myStmts.start(); myStmts.isCurrent(); myStmts.advance()) {
	        	
	        	if(counter == i) {
	        		return (StmtNode)myStmts.getCurrent();
	        	}
	        	counter ++;
	        }
	    } catch (NoCurrentException ex) {
	    	System.err.println("NoCurrentException in ExpListNode.analyzeType");
	        System.exit(-1);
	    }
		throw new IndexOutOfBoundsException("Index " + i + " is out of bounds.");
		
	}

	public void decompile(PrintWriter p, int indent) {
        try {
            for (myStmts.start(); myStmts.isCurrent(); myStmts.advance()) {
                ((StmtNode) myStmts.getCurrent()).decompile(p, indent);
            }
        } catch (NoCurrentException ex) {
        	System.err.println("unexpected NoCurrentException in StmtListNode.print");
            System.exit(-1);
        }
    }
   
    public void analyzeName(SymbolTable symTable) {
    	 try {
             for (myStmts.start(); myStmts.isCurrent(); myStmts.advance()) {
                 StmtNode node = (StmtNode) myStmts.getCurrent();
                 if(node == null) {
                	 node.analyzeName(null);
                 }
                 else {
                	 node.analyzeName(symTable);
                 }
             }
         } catch (NoCurrentException ex) {
        	 System.err.println("unexpected NoCurrentException in StmtListNode.print");
             System.exit(-1);
         }
    }
    
  
    
    public boolean hasReturn() {
    	 try {
             for (myStmts.start(); myStmts.isCurrent(); myStmts.advance()) {
                 StmtNode stmt = (StmtNode) myStmts.getCurrent();
                 if(stmt instanceof ReturnStmtNode) {
                	 return true;
                 }
                
             }
         } catch (NoCurrentException ex) {
        	 System.err.println("unexpected NoCurrentException in StmtListNode.print");
             System.exit(-1);
         }
    	 return false;
    }
    
    public boolean hasCorrectReturnType() {
   	 try {
            for (myStmts.start(); myStmts.isCurrent(); myStmts.advance()) {
                StmtNode stmt = (StmtNode) myStmts.getCurrent();
                if(stmt instanceof ReturnStmtNode) {
               	 ReturnStmtNode stmtNode = (ReturnStmtNode) stmt;
               	 return stmtNode.getReturnType()==(Types.IntType);
                }
               
            }
        } catch (NoCurrentException ex) {
        	System.err.println("unexpected NoCurrentException in StmtListNode.print");
            System.exit(-1);
        }
   	 return false;
   }
   

    // sequence of kids (StmtNodes)
    private Sequence myStmts;


	@Override
	public void analyzeType(int params) {
		 try {
	            for (myStmts.start(); myStmts.isCurrent(); myStmts.advance()) {
	                ((StmtNode) myStmts.getCurrent()).analyzeType(params);
	            }
	        } catch (NoCurrentException ex) {
	       	 System.err.println("unexpected NoCurrentException in StmtListNode.print");
	            System.exit(-1);
	        }
		
	}
}

class ExpListNode extends ASTnode {

    public ExpListNode(Sequence S) {
        if (S == null) {
        	System.err.println("Das Sequence-Argument darf nicht null sein.");
        }
	myExps = S;
    }
    
    public void analyzeName(SymbolTable symTable) {
        try {
            for (myExps.start(); myExps.isCurrent(); myExps.advance()) {
                ((ExpNode) myExps.getCurrent()).analyzeName(symTable);
            }
        } catch (NoCurrentException ex) {
        	System.err.println("NoCurrentException in ExpListNode.analyzeName");
            System.exit(-1);
        }
    }
    
   
    
    public void decompile(PrintWriter p, int indent) {
        try {
            for (myExps.start(); myExps.isCurrent(); myExps.advance()) {
                ((ExpNode) myExps.getCurrent()).decompile(p, indent);
                if (myExps.isCurrent()) {
                    p.print(", ");
                }
            }
        } catch (NoCurrentException ex) {
        	System.err.println("unexpected NoCurrentException in ExpListNode.print");
            System.exit(-1);
        }
    }

    // sequence of kids (ExpNodes)
    private Sequence myExps;

	public int size() {
		int counter = 0;
		  try {
		        for (myExps.start(); myExps.isCurrent(); myExps.advance()) {
		            counter++;
		        }
		    } catch (NoCurrentException ex) {
		    	System.err.println("NoCurrentException in ExpListNode.analyzeType");
		        System.exit(-1);
		    }
		  return counter;
	}
	public ExpNode get(int i) {
		int counter = 0;
		try {
	        for (myExps.start(); myExps.isCurrent(); myExps.advance()) {
	        	
	        	if(counter == i) {
	        		return (ExpNode)myExps.getCurrent();
	        	}
	        	counter ++;
	        }
	    } catch (NoCurrentException ex) {
	    	System.err.println("NoCurrentException in ExpListNode.analyzeType");
	        System.exit(-1);
	    }
		throw new IndexOutOfBoundsException("Index " + i + " is out of bounds.");
		
	}
	
	 public void codeGen(int nt) {
	        try {
	            for (myExps.start(); myExps.isCurrent(); myExps.advance()) {
	                ExpNode exp = (ExpNode) myExps.getCurrent();
	                exp.codeGen(nt);
	            }
	        } catch (NoCurrentException ex) {
	            System.err.println("Unexpected NoCurrentException in StmtListNode.codeGen");
	            System.exit(-1);
	        }
	    }

	@Override
	public void analyzeType(int params) {
		try {
            for (myExps.start(); myExps.isCurrent(); myExps.advance()) {
                ((ExpNode) myExps.getCurrent()).analyzeType(params);;
            }
        } catch (NoCurrentException ex) {
        	System.err.println("NoCurrentException in ExpListNode.analyzeType");
            System.exit(-1);
        }
		
	}
	
}

// **********************************************************************
// DeclNode and its subclasses
// **********************************************************************
abstract class DeclNode extends ASTnode
{

	protected abstract void analyzeName(SymbolTable symbolTable);

	protected abstract void codeGen();

	protected abstract void analyzeType();
}

class FieldDeclNode extends DeclNode {
    public FieldDeclNode(TypeNode type, IdNode id) {
	myType = type;
	myId = id;
	 
    }
    
    public void decompile(PrintWriter p, int indent) {
	doIndent(p, indent);
	p.print("static ");
	myType.decompile(p, indent);
	p.print(" ");
	myId.decompile(p, indent);
	p.println(";");
    }

    // 2 kids
    private TypeNode myType;
    private IdNode myId;
    
	@Override
	protected void analyzeName(SymbolTable symbolTable) {
		 if (symbolTable.lookupInCurrentScope(myId.getIdName()) != null) {
	            Errors.fatal(myId.getLineNum(), myId.getCharNum(),
	                         "Multiply declared method: " + myId.getIdName());
	        } else {
	            // Symbol für die Methode erstellen und in die Symboltabelle einfügen
	            SymbolTable.Sym sym = symbolTable.new Sym(myId.getIdName(), myType.getType());
	            sym.setIsLocal(false);
	            symbolTable.insert(myId.getIdName(), sym);
	            myId.setSymbol(sym);
	        }
		
	}

	

	@Override
	protected void codeGen() {
		
		
        
	    // Generate MIPS code for field declaration
        Codegen.p.println("\n\t# Static field declaration: " + myId.getIdName());
        String label = myId.getIdName();
        String type = myType.getTypeName();
        if(Codegen.isReserved(label)) {
			 label = ""+label+"Var";
		 }

        if (type.equals("int")) {
            // Reserve 4 bytes for an int field
            Codegen.p.println(label + ": .word 0");
        } else if (type.equals("boolean")) {
            // Reserve 4 bytes for a boolean field
            Codegen.p.println(label + ": .word 0");
        } else if (type.equals("String")) {
            // Reserve space for a string (handled differently in some cases)
            Codegen.p.println(label + ": .asciiz \"\"");
        } else {
            Errors.fatal(myId.getLineNum(), myId.getCharNum(),
                    "Unsupported type for static field: " + type);
        }
        
       
		
	}

	

	@Override
	public void analyzeType(int params) {
		myType.analyzeType(params);
		
	}

	@Override
	protected void analyzeType() {
		myType.analyzeType();
		
	}
}

class VarDeclNode extends DeclNode {
    public VarDeclNode(TypeNode type, IdNode id) {
	myType = type;
	myId = id;
    }

    public void decompile(PrintWriter p, int indent) {
        doIndent(p,indent);
        myType.decompile(p, indent);
        p.print(" ");
        myId.decompile(p, indent);
        SymbolTable.Sym sym = myId.getSymbol();
       // System.out.println(sym.getOffset());
       p.print("("+sym.getOffset()+")");
        p.println(";");
    }

    // 2 kids
    private TypeNode myType;
    private IdNode myId;
    private int currentOffset;
    @Override
    protected  void analyzeName(SymbolTable symTable) {
        // Prüfen, ob die Variable im aktuellen Scope bereits existiert
        if (symTable.lookupInCurrentScope(myId.getIdName()) != null) {
            Errors.fatal(myId.getLineNum(), myId.getCharNum(),
                         "Multiply declared variable: " + myId.getIdName());
        } else {
            // In die Symboltabelle einfügen
        	
            SymbolTable.Sym sym = symTable.new Sym(myId.getIdName(), myType.getType());
            symTable.insert(myId.getIdName(), sym);
            sym.setIsLocal(true);
            sym.incrementNumLocalVars();
            myId.setSymbol(sym); // Verknüpfen des Symbols mit der ID
        }
    }

	public int getOffset() {
		return currentOffset;
	}
	
	public void setOffset(int value) {
		this.currentOffset = value;
	}

	@Override
	protected void codeGen() {
	    SymbolTable.Sym sym = myId.getSymbol();
	    String id = myId.getIdName();
	    String idReserved = id+"Var";
	    if(Codegen.isReserved(id)) {
	    	id = idReserved;
	    }
	    
	    if (sym == null) {
	        Errors.fatal(myId.getLineNum(), myId.getCharNum(),
	                     "Undefined variable in code generation: " + id);
	        return;
	    }
	 

	    if (sym.isLocal()) {
	    	
	      
	        Codegen.p.println("\n\t# Local variable declaration: " + id);
	        
	  
	        int offset = sym.getOffset();
	        Codegen.generateIndexed(Codegen.SW, Codegen.ZERO, Codegen.FP, offset, "Initialize local variable to 0");
	       // Codegen.generateIndexed(Codegen.SW, Codegen.ZERO, Codegen.SP, 0, "Initialize local variable to 0");
	        //Codegen.generate(Codegen.SUBU,Codegen.SP,Codegen.SP,"4");
	    } else {
	       
	        Errors.fatal(myId.getLineNum(), myId.getCharNum(),
	                     "Global variable handling not supported in VarDeclNode.");
	    }
	}
	
	public String getName() {
		return myId.getIdName();
	}

	@Override
	public void analyzeType(int params) {
		// TODO Automatisch generierter Methodenstub
		myType.analyzeType(params);
		SymbolTable.Sym sym = myId.getSymbol();
		if(sym.isLocal()) {
			sym.setOffset(currentOffset);
		}
		
	}

	@Override
	protected void analyzeType() {
		// TODO Automatisch generierter Methodenstub
				myType.analyzeType();
				SymbolTable.Sym sym = myId.getSymbol();
				if(sym.isLocal()) {
					sym.setOffset(currentOffset);
				}
		
	}
}

class MethodDeclNode extends DeclNode {
    public MethodDeclNode(IdNode id, FormalsListNode formalList,
			  MethodBodyNode body) {
	myId = id;
	myFormalsList = formalList;
	myBody = body;
    }

    public void decompile(PrintWriter p, int indent) {
        myBody.decompile(p,indent);
    }

    // 3 kids
    protected IdNode myId;
    protected FormalsListNode myFormalsList;
    protected MethodBodyNode myBody;
    protected static boolean isFirstMethod = true;
    
	@Override
	protected void analyzeName(SymbolTable symbolTable) {
		// TODO Automatisch generierter Methodenstub
		myBody.analyzeName(symbolTable);
		
	}
	public boolean isMainMethod() {
        return myId.getIdName().equals("main")
            && myFormalsList.isEmpty() // Falls die Signatur keine Argumente erwartet
            && this instanceof VoidMethodDeclNode; // Hauptmethode muss void sein
    }

	@Override
	protected void analyzeType() {
		 if (myFormalsList != null) {
		        myFormalsList.analyzeType(0);
		    }

		    // Typprüfung des Methodenkörpers
		 if (myBody != null) {
			 myBody.analyzeType(myFormalsList.getNumParams());
			 
		 }
	}
	@Override
	public void analyzeType(int params) {
		if (myFormalsList != null) {
	        myFormalsList.analyzeType(0);
	    }

	    // Typprüfung des Methodenkörpers
	 if (myBody != null) {
		 myBody.analyzeType(myFormalsList.getNumParams());
		 
	 }
		
	}
	
	

		@Override
		protected void codeGen() {
			Codegen.currentMethod = myId.getIdName(); // Aktuelle Methode setzen

			int localVarSize = myBody.getLocalVarSize();
			
			String Id = myId.getIdName();
			String IdMethod = ""+Id+"Method";
			String IdExit = ""+Id+"_Exit";
			//System.out.println(Id);
			if (isFirstMethod) {
		     
				
				Codegen.generate(".text");
				//Codegen.generate(".globl ", "start");
		        Codegen.generate(".globl ", "main");
		        
		        /*Codegen.genLabel("start", "Method Entry");
		        Codegen.generate("jal", "main");
		        Codegen.generate(Codegen.LI, Codegen.V0, 10);
	             Codegen.generate("syscall");*/

		        // Status aktualisieren
		        isFirstMethod = false;
		    }
			if(Codegen.isReserved(myId.getIdName())) {
				
				Id = IdMethod;
			}
			if(Id.equals("main")) {
				 Codegen.genLabel(Id, "Method Entry");
				
			}
			else {
				Codegen.genLabel(Id, "Method Entry");
			}
			
				
			
		    
		    
		  /*  Codegen.generateIndexed("sw", Codegen.RA, Codegen.SP, 0, "PUSH");
	        Codegen.generate(Codegen.SUBU, Codegen.SP, Codegen.SP, 4);
	        Codegen.generateIndexed("sw", Codegen.FP, Codegen.SP, 0, "PUSH");
	        Codegen.generate(Codegen.SUBU, Codegen.SP, Codegen.SP, 4);
	        Codegen.generate(Codegen.ADDU, Codegen.FP, Codegen.SP, 8 ) ;*/

		    // Speichern von $ra und $fp
		   /* Codegen.generateIndexed("sw", Codegen.RA, Codegen.SP, 0, "Save return address");
		    Codegen.generateIndexed("sw", Codegen.FP, Codegen.SP, -4, "Save old frame pointer");
		    Codegen.generate("subu", Codegen.SP, Codegen.SP, 8);

		    // Setze neuen Frame-Pointer
		    Codegen.generate("move", Codegen.FP, Codegen.SP);

		    // Reserviere Platz für lokale Variablen
		    SymbolTable.Sym sym = myId.getSymbol();
		    int localVarSize = sym.getNumLocalVars() * 4;  // 4 Bytes pro Variable
		    Codegen.generate("subu", Codegen.SP, Codegen.SP, localVarSize);
*/
		    // Generiere Code für den Methodenkörper
	       // Codegen.generate("subu", Codegen.SP, Codegen.SP, localVarSize);
	        

	        
	        	
	        	
	        	
	        
	        
	        	  myBody.codeGen(myFormalsList.getNumParams());
	        
		  
		    Codegen.currentMethod = null;  
		    
		    // Rückgabewert vorbereiten
		    /*if (myBody.hasReturnStatement()) {
		        Codegen.generate("move", Codegen.V0, Codegen.T0);  // Rückgabewert in $v0 speichern
		    }*/
		    // Speicherplatz für lokale Variablen freigeben
	      /*  if (localVarSize > 0) {
	            Codegen.generate("addu", Codegen.SP, Codegen.SP, localVarSize);
	            Codegen.p.println("\t# Speicherplatz freigegeben");
	            Codegen.generate("j",IdExit);
	        }*/
		    // Function exit
		  
		   /* else {
		    	Codegen.genLabel(IdExit);
		        Codegen.generateIndexed("lw", Codegen.RA, Codegen.FP, 0, "restore RA");
		        Codegen.generateWithComment(Codegen.MOVE, "restore SP", Codegen.SP, Codegen.FP);
		        Codegen.generateIndexed("lw", Codegen.FP, Codegen.FP, -4, "restore FP");
		        Codegen.generateWithComment(Codegen.JR, "return" ,Codegen.RA);
		    }*/
		   
	        

		    // Stack-Frame bereinigen
		  /*  Codegen.generate("move", Codegen.SP, Codegen.FP);       // Stack-Pointer zurücksetzen
		    Codegen.generateIndexed("lw", Codegen.FP, Codegen.SP, -4, "Restore old frame pointer");
		    Codegen.generateIndexed("lw", Codegen.RA, Codegen.SP, 0, "Restore return address");
		    Codegen.generate("addi", Codegen.SP, Codegen.SP, 8);    // Rücksprungadresse und FP freigeben

		    // Rücksprung
		    Codegen.generate("jr", Codegen.RA);     */                   // Springe zur Rücksprungadresse
		}

	


}

class VoidMethodDeclNode extends MethodDeclNode {
    public VoidMethodDeclNode(IdNode id, FormalsListNode formalList,
			  MethodBodyNode body) {
        super(id, formalList, body);
    }

    public void decompile(PrintWriter p, int indent) {
        doIndent(p,indent);
        p.print("public static void ");
        myId.decompile(p, indent);
        p.print("(");
        myFormalsList.decompile(p, indent);
        p.print(")");

        
        if (myBody != null) {
            myBody.decompile(p, indent);
        }
    }
    @Override
    protected void analyzeName(SymbolTable symTable) {
        // Prüfen, ob die Methode im aktuellen Scope bereits existiert
        if (symTable.lookupInCurrentScope(myId.getIdName()) != null) {
            Errors.fatal(myId.getLineNum(), myId.getCharNum(),
                         "Multiply declared method: " + myId.getIdName());
        } else {
        	
        	
        	 List<Integer> paramTypes = new ArrayList<>();
        	 try {
                 for (myFormalsList.getSequence().start(); myFormalsList.getSequence().isCurrent(); myFormalsList.getSequence().advance()) {
                     FormalDeclNode formal = (FormalDeclNode) myFormalsList.getSequence().getCurrent();
                     paramTypes.add(formal.getTypeNode());
                     
                 }
             } catch (Exception e) {
                 Errors.fatal(myId.getLineNum(), myId.getCharNum(), "Error processing method parameters.");
             }
             // Symbol für die Methode erstellen und in die Symboltabelle einfügen
             SymbolTable.Sym sym = symTable.new Sym(myId.getIdName(), Types.MethodType, paramTypes);
             symTable.insert(myId.getIdName(), sym);
             myId.setSymbol(sym);
         }

        // Neuen Scope für die Methode betreten (Parameter und lokale Variablen)
        symTable.enterScope();

        // Analyse der formalen Parameter
        myFormalsList.analyzeName(symTable);

        // Analyse des Methodenkörpers
        if (myBody != null) {
            myBody.analyzeName(symTable);
        }

       symTable.leaveScope();
    }
    
    @Override
    public void analyzeType(int params) {
        // Prüfe Argumenttypen
        myFormalsList.analyzeType(params);
     
        myBody.analyzeType(params);
    }
}
class IntMethodDeclNode extends MethodDeclNode {
    public IntMethodDeclNode(IdNode id, FormalsListNode formalList,
			  MethodBodyNode body) {
        super(id, formalList, body);
    }
    public void decompile(PrintWriter p, int indent) {
        doIndent(p,indent);
        p.print("public static int ");
        myId.decompile(p, indent);
        p.print("(");
        myFormalsList.decompile(p, indent);
        p.print(")");
        if (myBody != null) {
            myBody.decompile(p, indent);
        }
    }
    @Override
	protected void analyzeName(SymbolTable symTable) {
    	
    	 if (symTable.lookupInCurrentScope(myId.getIdName()) != null) {
             Errors.fatal(myId.getLineNum(), myId.getCharNum(),
                          "Multiply declared method: " + myId.getIdName());
         } else {
        	 
        	 List<Integer> paramTypes = new ArrayList<>();
        	 try {
                 for (myFormalsList.getSequence().start(); myFormalsList.getSequence().isCurrent(); myFormalsList.getSequence().advance()) {
                     FormalDeclNode formal = (FormalDeclNode) myFormalsList.getSequence().getCurrent();
                     paramTypes.add(formal.getTypeNode());
                 }
             } catch (Exception e) {
                 Errors.fatal(myId.getLineNum(), myId.getCharNum(), "Error processing method parameters.");
             }
             // Symbol für die Methode erstellen und in die Symboltabelle einfügen
             SymbolTable.Sym sym = symTable.new Sym(myId.getIdName(), Types.MethodType, paramTypes);
             symTable.insert(myId.getIdName(), sym);
             myId.setSymbol(sym);
         }
    	 
    
             if (!myBody.hasReturnStatement()) {
                 Errors.fatal(myId.getLineNum(), myId.getCharNum(),
                              "Method " + myId.getIdName() + " must have a return statement.");
             }

             if (!myBody.isReturnTypeCorrect(Types.IntType)) {
                 Errors.fatal(myId.getLineNum(), myId.getCharNum(),
                              "Method " + myId.getIdName() + " must return a value of type " + Types.ToString(1));
             }
         

         // Neuen Scope für die Methode betreten (Parameter und lokale Variablen)
         symTable.enterScope();

         // Analyse der formalen Parameter
         myFormalsList.analyzeName(symTable);

         // Analyse des Methodenkörpers
         if (myBody != null) {
             myBody.analyzeName(symTable);
         }

        symTable.leaveScope();
		
	}
    
    @Override
    public void analyzeType() {
        // Prüfe Argumenttypen
        myFormalsList.analyzeType(0);

        // Prüfe Rückgabewert
        if (!myBody.hasReturnStatement() || !myBody.isReturnTypeCorrect(Types.IntType)) {
            Errors.fatal(myId.getLineNum(), myId.getCharNum(), "Method must return a value of type int.");
        }
        myBody.analyzeType(0);
    }
}

class FormalDeclNode extends DeclNode {
    public FormalDeclNode(TypeNode type, IdNode id) {
	myType = type;
	myId = id;
    }
    public int getTypeNode() {
		// TODO Automatisch generierter Methodenstub
		return myType.getType();
	}
	public void decompile(PrintWriter p, int indent) {
        
        myType.decompile(p, indent);
        p.print(" ");
        myId.decompile(p, indent);
    }
    private TypeNode myType;
    private IdNode myId;
    private int currentOffset;
    @Override
    protected  void analyzeName(SymbolTable symTable) {
        // Prüfen, ob die Variable im aktuellen Scope bereits existiert
        if (symTable.lookupInCurrentScope(myId.getIdName()) != null) {
            Errors.fatal(myId.getLineNum(), myId.getCharNum(),
                         "Multiply declared variableee: " + myId.getIdName());
        } else {
            // In die Symboltabelle einfügen
        	
            SymbolTable.Sym sym = symTable.new Sym(myId.getIdName(), myType.getType());
            sym.setIsLocal(true);// ist lokale VAriable 
            symTable.insert(myId.getIdName(), sym);
            
            sym.incrementNumParams();
            myId.setSymbol(sym); // Verknüpfen des Symbols mit der ID
        }
    }
	@Override
	protected void analyzeType() {
		myType.analyzeType();
		SymbolTable.Sym sym = myId.getSymbol();
		if(sym.isLocal()) {
			sym.setOffset(currentOffset);
		}
	}
	
	public int getOffset() {
		return currentOffset;
	}
	
	public void setOffset(int value) {
		this.currentOffset = value;
	}
	@Override
	protected void codeGen() {
		/*System.out.println("hhhahahahahh");
	    // Lokale Variable, die eine Offset benötigt
	    SymbolTable.Sym sym = myId.getSymbol();

	    if (sym == null) {
	        Errors.fatal(myId.getLineNum(), myId.getCharNum(),
	                     "Undefined formal parameter: " + myId.getIdName());
	        return;
	    }

	    if (sym.isLocal()) {
	        int offset = sym.getOffset();
	        String type = myType.getTypeName();

	        // Abhängig vom Typ wird der Speicherplatz im Stack reserviert
	        if (type.equals("int")) {
	            // Speicherplatz für Integer auf dem Stack reservieren
	            Codegen.generateIndexed(Codegen.SW, Codegen.ZERO, Codegen.FP, offset-4, "Store integer parameter");
	        } else if (type.equals("boolean")) {
	            // Speicherplatz für Boolean auf dem Stack reservieren
	            Codegen.generateIndexed(Codegen.SW, Codegen.A0, Codegen.FP, offset-4, "Store boolean parameter");
	        } else if (type.equals("string")) {
	            // Speicherplatz für String auf dem Stack reservieren
	            Codegen.generateIndexed(Codegen.SW, Codegen.A0, Codegen.FP, offset-4, "Store string parameter");
	        }
	    }*/
		
		    SymbolTable.Sym sym = myId.getSymbol();
		    
		    if (sym == null) {
		        Errors.fatal(myId.getLineNum(), myId.getCharNum(),
		                     "Undefined formal parameter: " + myId.getIdName());
		        return;
		    }
		    // Offset für Parameter aus Symboltabelle abrufen
		    int offset = sym.getOffset();
		    String type = myType.getTypeName();
		    int numParams = sym.getNumParams();
		    int paramIndex = (offset / 4); // Annahme: Offsets sind -4, -8, ...
		   // System.out.println(myId.getIdName()+" hat offset "+offset);
		   // System.out.println(paramIndex);
		    
		    
		
		   

		   

		    //if (paramIndex < 4) {
		        
		        switch (paramIndex-1) {
		            case 0:
		            /*	Codegen.generateIndexed(Codegen.LW, Codegen.T0, Codegen.SP, 4,
                                "Load parameter " + myId.getIdName());
		            	Codegen.generate(Codegen.ADDU, Codegen.SP, Codegen.SP, 4);
		               Codegen.generateIndexed(Codegen.SW, Codegen.T0, Codegen.FP, offset,
		                                         "Store parameter " + myId.getIdName());*/
		            	Codegen.generateIndexed(Codegen.LW, Codegen.A0, Codegen.FP, offset,
                                "Load parameter " + myId.getIdName());
		                break;
		            case 1:
		            	/*Codegen.generateIndexed(Codegen.LW, Codegen.T1, Codegen.SP, 4,
                                "Load parameter " + myId.getIdName());
		            	 Codegen.generate(Codegen.ADDU, Codegen.SP, Codegen.SP, 4);
		                Codegen.generateIndexed(Codegen.SW, Codegen.T1, Codegen.FP, offset,
		                                         "Store parameter " + myId.getIdName());*/
		            	Codegen.generateIndexed(Codegen.LW, Codegen.A0, Codegen.FP, offset,
                                "Load parameter " + myId.getIdName());
		                break;
		            case 2:
		               /* Codegen.generateIndexed(Codegen.LW, Codegen.T2, Codegen.SP, offset,
		                                         "Store parameter " + myId.getIdName());*/
		            	Codegen.generateIndexed(Codegen.LW, Codegen.A0, Codegen.FP, offset,
                                "Load parameter " + myId.getIdName());
		                break;
		            case 3:
		                /*Codegen.generateIndexed(Codegen.LW, Codegen.T3, Codegen.SP, offset,
		                                         "Store parameter " + myId.getIdName());*/
		            	Codegen.generateIndexed(Codegen.LW, Codegen.A0, Codegen.FP, offset,
                                "Load parameter " + myId.getIdName());
		                break;
		            case 4:
		               /* /*Codegen.generateIndexed(Codegen.LW, Codegen.T4, Codegen.SP, offset,
		                                         "Store parameter " + myId.getIdName());*/
		            	Codegen.generateIndexed(Codegen.LW, Codegen.A0, Codegen.FP, offset,
                                "Load parameter " + myId.getIdName());
		                break;
		            case 5: 
		                /*Codegen.generateIndexed(Codegen.LW, Codegen.T5, Codegen.SP, offset,
		                                         "Store parameter " + myId.getIdName());*/
		            	Codegen.generateIndexed(Codegen.LW, Codegen.A0, Codegen.FP, offset,
                                "Load parameter " + myId.getIdName());
		                break;
		            case 6:
		                /*Codegen.generateIndexed(Codegen.LW, Codegen.T6, Codegen.SP, offset,
		                                         "Store parameter " + myId.getIdName());*/
		            	Codegen.generateIndexed(Codegen.LW, Codegen.A0, Codegen.FP, offset,
                                "Load parameter " + myId.getIdName());
		                break;
		            case 7:
		                /*Codegen.generateIndexed(Codegen.LW, Codegen.T7, Codegen.SP, offset,
		                                         "Store parameter " + myId.getIdName());*/
		            	Codegen.generateIndexed(Codegen.LW, Codegen.A0, Codegen.FP, offset,
                                "Load parameter " + myId.getIdName());
		                break;
		                
		            case 8:
		                /*Codegen.generateIndexed(Codegen.LW, Codegen.T8, Codegen.SP, offset,
		                                         "Store parameter " + myId.getIdName());*/
		            	Codegen.generateIndexed(Codegen.LW, Codegen.A0, Codegen.FP, offset,
                                "Load parameter " + myId.getIdName());
		                break;
		            case 9:
		               /* Codegen.generateIndexed(Codegen.LW, Codegen.T9, Codegen.SP, offset,
		                                         "Store parameter " + myId.getIdName());*/
		            	Codegen.generateIndexed(Codegen.LW, Codegen.A0, Codegen.FP, offset,
                                "Load parameter " + myId.getIdName());
		                break;
		        
		        }/*
		       
		   /* } else {
		        // Weitere Parameter werden direkt aus dem Stack kopiert
		        // Der Aufrufer hat diese bereits auf den Stack gelegt
		        int stackOffset = (paramIndex - 4) * 4; // Position des Arguments auf dem Stack
		        Codegen.generateIndexed(Codegen.LW, Codegen.T0, Codegen.SP, stackOffset,
		                                 "Load parameter " + myId.getIdName() + " from stack");
		        Codegen.generateIndexed(Codegen.SW, Codegen.T0, Codegen.FP, offset,
		                                 "Store parameter " + myId.getIdName() + " in frame");
		    }*/

	}
	@Override
	public void analyzeType(int params) {
		myType.analyzeType();
		SymbolTable.Sym sym = myId.getSymbol();
		if(sym.isLocal()) {
			sym.setOffset(currentOffset);
		}
		
	}
}
abstract class TypeNode extends ASTnode {

	private int type; // Typwert, basierend auf `Types`

    public TypeNode(int type) {
        this.type = type;
    }

    protected abstract void analyzeType();

	public int getType() {
        return type;
    }

    public String getTypeName() {
        return Types.ToString(type); 
    }
}

class IntNode extends TypeNode
{
    public IntNode() {
    	super(Types.IntType);
    }

    public void decompile(PrintWriter p, int indent) {
        p.print("int");
    }
    @Override
	public int getType() {
    	return Types.IntType;
    }

	@Override
	protected void analyzeType() {
		// TODO Automatisch generierter Methodenstub
		
	}

	@Override
	public void analyzeType(int params) {
		// TODO Automatisch generierter Methodenstub
		
	}
}

class BooleanNode extends TypeNode
{
    public BooleanNode() {
    	super(Types.BoolType);
    }

    public void decompile(PrintWriter p, int indent) {
        p.print("boolean");
    }
    
    @Override
	public  int getType() {
    	return Types.BoolType;
    }

	@Override
	protected void analyzeType() {
		// TODO Automatisch generierter Methodenstub
		
	}

	@Override
	public void analyzeType(int params) {
		// TODO Automatisch generierter Methodenstub
		
	}
}

class StringNode extends TypeNode
{
    public StringNode() {
    	super(Types.StringType);
    }
    public void decompile(PrintWriter p, int indent) {
        p.print("String");
    }
    @Override
	public  int getType() {
    	return Types.StringType;
    }
	@Override
	protected void analyzeType() {
		// TODO Automatisch generierter Methodenstub
		
	}
	@Override
	public void analyzeType(int params) {
		// TODO Automatisch generierter Methodenstub
		
	}
}
abstract class StmtNode extends ASTnode {

	protected abstract void analyzeName(SymbolTable symTable);

	protected abstract void codeGen(int params, int localVarSize);

	protected abstract void analyzeType();
	
	protected abstract int computeNt();
		
	
}

class PrintStmtNode extends StmtNode {
    public PrintStmtNode(ExpNode exp) {
	myExp = exp;
	exprType = ""; // initialisieren mit einem leeren Typ
    }
	private int expType;
	private ExpNode myExp;
	private String exprType;

    public void decompile(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("System.out.println(");
        myExp.decompile(p, indent);
        p.println(");");
    }
   
	@Override
	protected void analyzeName(SymbolTable symTable) {
	
		myExp.analyzeName(symTable);
		
	}

	@Override
	protected void analyzeType() {
		   myExp.analyzeType();

		    // Typ des Ausdrucks abrufen
		    expType = myExp.getExpType();
			

		    // Überprüfen, ob der Typ gültig ist (int, boolean oder string)
		    if (expType != Types.IntType && expType != Types.BoolType && expType != Types.StringType) {
		    	Errors.fatal(myExp.getLineNum(),myExp.getColNum(),
		    			
		            "Invalid type for print statement. Expected int, boolean, or string.");
		    }
		    exprType = Types.ToString(myExp.getExpType()); // Setze den Ausdruckstyp basierend auf dem eingebetteten Typ
	}
	  // Getter für den Ausdruckstyp
    public String getExprType() {
        return exprType;
    }

    // Setter für den Ausdruckstyp
    public void setExprType(String type) {
        this.exprType = type;
    }

    /*@Override
    public void codeGen() {
        myExp.codeGen(); // Erzeugt den MIPS-Code für den Ausdruck

        // basierend auf dem Typ des Ausdrucks, wird der MIPS-Systemcall für print
        switch (exprType) {
            case "int":
                Codegen.generate("li", Codegen.V0, 1); // Systemcall zum Drucken einer Zahl
                break;
            case "boolean":
                Codegen.generate("li", Codegen.V0, 4); // Systemcall zum Drucken eines booleschen Wertes
                break;
            case "String":
                Codegen.generate("li", Codegen.V0, 4); // Systemcall zum Drucken einer Zeichenkette
                break;
            default:
                Errors.fatal(myExp.getLineNum(), myExp.getCharNum(),
                             "Unsupported type for print statement: " + exprType);
                break;
        }
        Codegen.generate("syscall"); // Syscall ausführen
    }*/
    @Override
    public void codeGen(int params, int localVarSize) {
    	
        //myExp.codeGen(myExp.computeNT()); // Load the expression result into $a0
        if(myExp.getExpType()==1) {
        	myExp.codeGen(myExp.computeNT()*(-8));
        	 //Codegen.genPop(Codegen.A0);
        	/* myExp.codeGen(myExp.computeNT());
        	 Codegen.generateIndexed("lw", Codegen.A0, Codegen.FP, myExp.computeNT()*(-8));*/
             Codegen.generate(Codegen.LI, Codegen.V0, 1);
             Codegen.generate("syscall");

             Codegen.generate(Codegen.LA, Codegen.A0, "_.newline");
             Codegen.generate(Codegen.LI, Codegen.V0, 4);
             Codegen.generate("syscall");
        }
        else if(myExp.getExpType()==3){
        	myExp.codeGen(myExp.computeNT()*(-8));
        	 //Codegen.genPop(Codegen.A0);
        	/* myExp.codeGen(myExp.computeNT());
        	 Codegen.generateIndexed("lw", Codegen.A0, Codegen.FP, myExp.computeNT()*(-8));*/
             Codegen.generate(Codegen.LI, Codegen.V0, 4);
             Codegen.generate("syscall");

             Codegen.generate(Codegen.LA, Codegen.A0, "_.newline");
             Codegen.generate(Codegen.LI, Codegen.V0, 4);
             Codegen.generate("syscall");
        }
        else if(myExp.getExpType()==2) {
        	
        	myExp.codeGen(myExp.computeNT()*(-8));
        	 String truelabel = Codegen.nextLabel();
             String endlabel = Codegen.nextLabel();
        	
        	Codegen.generate("bne",Codegen.A0,Codegen.FALSE,truelabel);
        	Codegen.generate(Codegen.LA, Codegen.A0, "_false");
        	Codegen.generate("j", endlabel);
            
            
        	Codegen.genLabel(truelabel);
        	Codegen.generate(Codegen.LA, Codegen.A0, "_true");
        	Codegen.generate("j", endlabel);
           
            
        	Codegen.genLabel(endlabel);
        	Codegen.generate(Codegen.LI, Codegen.V0, 4);
        	Codegen.generate("syscall");

            Codegen.generate(Codegen.LA, Codegen.A0, "_.newline");
            Codegen.generate(Codegen.LI, Codegen.V0, 4);
            Codegen.generate("syscall");
        }

       
    }

	@Override
	protected int computeNt() {
		// TODO Automatisch generierter Methodenstub
		return 0;
	}

	@Override
	public void analyzeType(int params) {
		 myExp.analyzeType();

		    // Typ des Ausdrucks abrufen
		    expType = myExp.getExpType();
			

		    // Überprüfen, ob der Typ gültig ist (int, boolean oder string)
		    if (expType != Types.IntType && expType != Types.BoolType && expType != Types.StringType) {
		    	Errors.fatal(myExp.getLineNum(),myExp.getColNum(),
		    			
		            "Invalid type for print statement. Expected int, boolean, or string.");
		    }
		    exprType = Types.ToString(myExp.getExpType()); // Setze den Ausdruckstyp basierend auf
		
	}
}

class AssignStmtNode extends StmtNode {
    public AssignStmtNode(IdNode id, ExpNode exp) {
	myId = id;
	myExp = exp;
    }

    public void decompile(PrintWriter p, int indent) {
        doIndent(p, indent);
        myId.decompile(p, indent);
        p.print(" = ");
        myExp.decompile(p, indent);
        p.println(";");
    }
    
    public void analyzeName(SymbolTable symTable) {
    	myId.analyzeName(symTable);
    	myExp.analyzeName(symTable);
    }
    private IdNode myId;
    private ExpNode myExp;
	@Override
	protected void analyzeType() {
		 myId.analyzeType();

		    // Typanalyse des Ausdrucks (rechte Seite)
		    myExp.analyzeType();

		    // Typen abrufen
		    int idType = myId.getType();
		    int expType = myExp.getExpType();

		    // Überprüfen, ob der Identifier existiert und einen gültigen Typ hat
		    if (idType == Types.ErrorType) {
		        Errors.fatal(myId.getLineNum(), myId.getCharNum(),
		            "Invalid assignment target: " + myId.getIdName());
		    }

		    // Überprüfen, ob die Typen kompatibel sind
		    if (idType != expType) {
		        Errors.fatal(myId.getLineNum(), myId.getCharNum(),
		            "Type mismatch in assignment. Expected " + Types.ToString(idType)
		            + " but found " + Types.ToString(expType) + ".");
		    }
		
	}

	@Override
	protected void codeGen(int params, int localVarSize) {
	    // Typanalyse durchführen
	    myId.analyzeType();
	    myExp.analyzeType();

	    // Typen abrufen
	    int idType = myId.getType();
	    String id = myId.getIdName();
	    String idReserved = id+"Var";
	    if(Codegen.isReserved(id)) {
	    	id = idReserved;
	    }
	    int expType = myExp.getExpType();

	    // Überprüfen, ob das Ziel einen gültigen Typ hat
	    if (idType == Types.ErrorType) {
	        Errors.fatal(myId.getLineNum(), myId.getCharNum(),
	                     "Invalid assignment target: " + id);
	    }

	    // Überprüfen, ob die Typen kompatibel sind
	    if (idType != expType) {
	        Errors.fatal(myId.getLineNum(), myId.getCharNum(),
	                     "Type mismatch in assignment. Expected " + Types.ToString(idType)
	                     + " but found " + Types.ToString(expType) + ".");
	    }

	   

	    // $t0 enthält jetzt den Wert des Ausdrucks
	    int offset = myId.getSymbol().getOffset();
	    
	    myExp.codeGen(offset);
	 	   // myExp.codeGen(myExp.computeNT());  // Code für den Ausdruck erzeugen, Wert in $t0 speichern
	    if(myId.getSymbol().isLocal()) {
	    	

	 	    // Speichern des Werts in die Adresse des Ziels
	 	    Codegen.generateIndexed(Codegen.SW, Codegen.A0, Codegen.FP, offset, "Store value in variable " + id);
	    }
	    else {
	    	Codegen.generateWithComment("la","Load address of global variable " + id, Codegen.T0,id);
	    	 Codegen.generateIndexed(Codegen.SW, Codegen.A0, Codegen.T0, offset, "Store value in variable " + id);
	    }

	    
	}

	@Override
	protected int computeNt() {
		// TODO Automatisch generierter Methodenstub
		return 0;
	}

	@Override
	public void analyzeType(int params) {
		 myId.analyzeType();

		    // Typanalyse des Ausdrucks (rechte Seite)
		    myExp.analyzeType();

		    // Typen abrufen
		    int idType = myId.getType();
		    int expType = myExp.getExpType();

		    // Überprüfen, ob der Identifier existiert und einen gültigen Typ hat
		    if (idType == Types.ErrorType) {
		        Errors.fatal(myId.getLineNum(), myId.getCharNum(),
		            "Invalid assignment target: " + myId.getIdName());
		    }

		    // Überprüfen, ob die Typen kompatibel sind
		    if (idType != expType) {
		        Errors.fatal(myId.getLineNum(), myId.getCharNum(),
		            "Type mismatch in assignment. Expected " + Types.ToString(idType)
		            + " but found " + Types.ToString(expType) + ".");
		    }
	}
}

class IfStmtNode extends StmtNode {
    public IfStmtNode(ExpNode exp, StmtListNode slist) {
	myExp = exp;
	myStmtList = slist;
    }

    public void decompile(PrintWriter p, int indent) {
        doIndent(p,indent);
        p.print("if (");
        myExp.decompile(p, 0);
        p.println(") {");
        
        myStmtList.decompile(p,indent+4);
        doIndent(p,indent);
        p.println("}");
    
    }
    private ExpNode myExp;
    private StmtListNode myStmtList;
	@Override
	protected void analyzeName(SymbolTable symTable) {
		// TODO Automatisch generierter Methodenstub
		myExp.analyzeName(symTable);
		myStmtList.analyzeName(symTable);
	}

	@Override
	protected void analyzeType() {
		 myExp.analyzeType();

		    // Typ der Bedingung abrufen
		    int condType = myExp.getExpType();

		    // Prüfen, ob die Bedingung vom Typ boolean ist
		    if (condType != Types.BoolType) {
		    	Errors.fatal(myExp.getLineNum(), myExp.getColNum(),
		            "If statement condition must be of boolean type. But it was of "+Types.ToString(condType)+" type");
		    }

		    // Typprüfung der Anweisungen im Block
		 
		    myStmtList.analyzeType(0);
		
	}

	@Override
	protected void codeGen(int params, int localVarSize) {
		 // Generiere MIPS-Code für die Bedingung
	    myExp.codeGen(myExp.computeNT()*(-8));
	    
	    // Erzeuge Labels für "dann"- und "sonst"-Zweig
	    String trueLabel = Codegen.nextLabel();  // Label für dann-Zweig
	    String falseLabel = Codegen.nextLabel(); // Label für sonst-Zweig
	    String endLabel = Codegen.nextLabel();   // Ende-Label für if-Anweisung

	    // Bedingung auswerten und bedingten Sprung erzeugen
	    Codegen.generate("beq", Codegen.A0, "1", trueLabel); // Bedingung falsch? Sprung zu elseLabel
	    Codegen.generate("j", endLabel);
	    // Generiere den "then"-Teil
	    //Codegen.genLabel(trueLabel);
	    Codegen.genLabel(trueLabel);
	    for(int i = 0; i<myStmtList.size();i++) {
	    	if(myStmtList.get(i) instanceof ReturnStmtNode) {
	    		myStmtList.get(i).codeGen(params, localVarSize);
	    		Codegen.generate("addu",Codegen.SP,Codegen.SP,4*(params+localVarSize));
	    		Codegen.generateIndexed("lw",Codegen.RA,Codegen.SP,0);
	    		Codegen.generate("addu",Codegen.SP,Codegen.SP,4);
	    		Codegen.generate("addu",Codegen.SP,Codegen.SP,4*params);
	            Codegen.generateIndexed("lw", Codegen.FP,  Codegen.SP,0);
	            Codegen.generate("addu",Codegen.SP,Codegen.SP,4);
	            Codegen.generate("jr", Codegen.RA);
	    	}
	    	else {
	    		myStmtList.get(i).codeGen(params, localVarSize);
	    	}
	    }
	    

	    
	    // Unbedingter Sprung zum Ende des if-Blocks
	    Codegen.genLabel(endLabel);

	}

	@Override
	protected int computeNt() {
		int nt=0; 
		// TODO Automatisch generierter Methodenstub
		 for(int i = 0; i<myStmtList.size();i++) {
			 nt = Math.max(myExp.computeNT(), myStmtList.get(i).computeNt());
		 }
		 return nt;
	}

	@Override
	public void analyzeType(int params) {
		 myExp.analyzeType();

		    // Typ der Bedingung abrufen
		    int condType = myExp.getExpType();

		    // Prüfen, ob die Bedingung vom Typ boolean ist
		    if (condType != Types.BoolType) {
		    	Errors.fatal(myExp.getLineNum(), myExp.getColNum(),
		            "If statement condition must be of boolean type. But it was of "+Types.ToString(condType)+" type");
		    }

		    // Typprüfung der Anweisungen im Block
		 
		    myStmtList.analyzeType(params);
		
	}


}

class IfElseStmtNode extends StmtNode {
    public IfElseStmtNode(ExpNode exp, StmtListNode slist1,
			  StmtListNode slist2) {
	myExp = exp;
	myThenStmtList = slist1;
	myElseStmtList = slist2;
    }

    public void decompile(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("if (");
        myExp.decompile(p, indent);
        p.println(") {");
        
        myThenStmtList.decompile(p, indent+4);
        doIndent(p,indent);
        p.println("} else {");
        
        myElseStmtList.decompile(p, indent+4);
        doIndent(p,indent);
        p.println("}");
    }

    // 3 kids
    private ExpNode myExp;
    private StmtListNode myThenStmtList;
    private StmtListNode myElseStmtList;
	@Override
	protected void analyzeName(SymbolTable symTable) {
		// TODO Automatisch generierter Methodenstub
		myExp.analyzeName(symTable);
		myThenStmtList.analyzeName(symTable);
		myElseStmtList.analyzeName(symTable);
	}

	@Override
	protected void analyzeType() {
		 myExp.analyzeType();

		    // Typ der Bedingung abrufen
		    int condType = myExp.getExpType();

		    // Prüfen, ob die Bedingung vom Typ boolean ist
		    if (condType != Types.BoolType) {
		    	Errors.fatal(myExp.getLineNum(), myExp.getColNum(),
		            "If statement condition must be of boolean type.");
		    }

		    // Typprüfung der Anweisungen im Block
		    myThenStmtList.analyzeType(0);
		    myElseStmtList.analyzeType(0);
		
	}

	@Override
	protected void codeGen(int params, int localVarSize) {
		
	    myExp.codeGen(myExp.computeNT()*(-8));
	    //System.out.println("lol"+myExp.computeNT()*4);
	    String trueLabel = Codegen.nextLabel();  // Label für den "dann"-Zweig
	    String falseLabel = Codegen.nextLabel(); // Label für den "sonst"-Zweig (falls vorhanden)
	    String endLabel = Codegen.nextLabel();   // Ende-Label für if-else-Anweisung

	    Codegen.generate("beq", Codegen.A0, "1", trueLabel); // Wenn Bedingung erfüllt, springe zu True-Zweig

	    // Generiere Code für den Else-Zweig (e4)
	    if (myElseStmtList != null) {
	    	
	    	 for(int i = 0; i<myElseStmtList.size();i++) {
	 	    	if(myElseStmtList.get(i) instanceof ReturnStmtNode) {
	 	    		myElseStmtList.get(i).codeGen(params,  localVarSize);
	 	    		Codegen.generate("addu",Codegen.SP,Codegen.SP,4*(params+ localVarSize));
	 	    		Codegen.generateIndexed("lw",Codegen.RA,Codegen.SP,0);
	 	   	   	 	
	 	 	       	Codegen.generate("addu",Codegen.SP,Codegen.SP,4);
	 	 	      Codegen.generate("addu",Codegen.SP,Codegen.SP,4*params);
	 	            Codegen.generateIndexed("lw", Codegen.FP,  Codegen.SP,0);
	 	            Codegen.generate("addu",Codegen.SP,Codegen.SP,4);
	 	            Codegen.generate("jr", Codegen.RA);
	 	    	}
	 	    	else {
	 	    		myElseStmtList.get(i).codeGen(params,  localVarSize);
	 	    	}
	 	    }
	    }
	    Codegen.generate("j", endLabel); // Springe ans Ende

	    // Generiere Code für den True-Zweig (e3)
	    Codegen.genLabel(trueLabel);
	    myThenStmtList.codeGen(params,  localVarSize);  // Code für e3 generieren

	    // Ende des If-Else-Blocks
	    Codegen.genLabel(endLabel);
	}

	@Override
	protected int computeNt() {
		// TODO Automatisch generierter Methodenstub
		return 4;
	}

	@Override
	public void analyzeType(int params) {
		 myExp.analyzeType();

		    // Typ der Bedingung abrufen
		    int condType = myExp.getExpType();

		    // Prüfen, ob die Bedingung vom Typ boolean ist
		    if (condType != Types.BoolType) {
		    	Errors.fatal(myExp.getLineNum(), myExp.getColNum(),
		            "If statement condition must be of boolean type.");
		    }
		    myThenStmtList.analyzeType(params);
		    myElseStmtList.analyzeType(params);
		
	}

}

class WhileStmtNode extends StmtNode {
    public WhileStmtNode(ExpNode exp, StmtListNode slist) {
	myExp = exp;
	myStmtList = slist;
    }

    public void decompile(PrintWriter p, int indent) {
        doIndent(p,indent);
        p.println("do {");
        myStmtList.decompile(p, indent+4 );
        doIndent(p, indent);
        p.print("} while (");
        myExp.decompile(p, indent);
        p.println(") ");
        
    }

    
    private ExpNode myExp;
    private StmtListNode myStmtList;
	@Override
	protected void analyzeName(SymbolTable symTable) {
		// TODO Automatisch generierter Methodenstub
		myExp.analyzeName(symTable);
		myStmtList.analyzeName(symTable);
	}

	@Override
	protected void analyzeType() {
		 myExp.analyzeType();

		    // Typ der Bedingung abrufen
		    int condType = myExp.getExpType();

		    // Prüfen, ob die Bedingung vom Typ boolean ist
		    if (condType != Types.BoolType) {
		    	Errors.fatal(myExp.getLineNum(), myExp.getColNum(),
		            "While statmyExpement condition must be of boolean type.");
		    }

		    // Typprüfung der Anweisungen im Block
		    myStmtList.analyzeType(0);
		
	}

	@Override
	protected void codeGen(int params, int localVarSize) {
	    // Generiere MIPS-Code für die Bedingung
	    //myExp.codeGen(myExp.computeNT()*(-8));
	    String startLabel = Codegen.nextLabel(); // Label für den Start der Schleife
	    String endLabel = Codegen.nextLabel();   // Label für das Ende der Schleife
	    // Schleifenanfang
	    Codegen.genLabel(startLabel);
	 // Generiere den Schleifeninhalt
	    myStmtList.codeGen(params,  localVarSize);  // Code für Statements im Schleifenblock

	    // Schleifenanfang
	   // Codegen.genLabel(startLabel);
	    myExp.codeGen(myExp.computeNT()*(-8));
	    // Bedingung auswerten und bedingten Sprung erzeugen
	    Codegen.generate("beq", Codegen.A0, Codegen.FALSE, endLabel); // Bedingung falsch? Sprung zu endLabel

	    // Generiere den Schleifeninhalt
	    //myStmtList.codeGen();  // Code für Statements im Schleifenblock

	    // Springe zurück zum Anfang der Schleife
	    Codegen.generate("j", startLabel);

	    // Label für das Ende der Schleife
	    Codegen.genLabel(endLabel);
	}

	@Override
	protected int computeNt() {
		// TODO Automatisch generierter Methodenstub
		return 0;
	}

	@Override
	public void analyzeType(int params) {
		 myExp.analyzeType();

		    // Typ der Bedingung abrufen
		    int condType = myExp.getExpType();

		    // Prüfen, ob die Bedingung vom Typ boolean ist
		    if (condType != Types.BoolType) {
		    	Errors.fatal(myExp.getLineNum(), myExp.getColNum(),
		            "While statmyExpement condition must be of boolean type.");
		    }

		    // Typprüfung der Anweisungen im Block
		    myStmtList.analyzeType(0);
		
		
	}

}

class CallStmtNode extends StmtNode {
    public CallStmtNode(IdNode id, ExpListNode elist) {
	myId = id;
	myExpList = elist;
    }

    public CallStmtNode(IdNode id) {
	myId = id;
	myExpList = new ExpListNode(new Sequence());
    }

    public void decompile(PrintWriter p, int indent) {
        doIndent(p, indent);
        myId.decompile(p, indent);
        p.print("(");
        myExpList.decompile(p, indent);
        p.println(");");
    }

    
    private IdNode myId;
    private ExpListNode myExpList;
	@Override
	protected void analyzeName(SymbolTable symTable) {
		// TODO Automatisch generierter Methodenstub
		myId.analyzeName(symTable);
		myExpList.analyzeName(symTable);
	}

	@Override
	protected void analyzeType() {
		 myId.analyzeType();

		    // Überprüfen, ob der Identifier eine Methode ist
		    SymbolTable.Sym methodSym = myId.getSymbol();
		    if (methodSym == null || methodSym.getSymType() != Types.MethodType) {
		        Errors.fatal(myId.getLineNum(), myId.getCharNum(),
		            "Call to undeclared or invalid method: " + myId.getIdName());
		       
		    }

		    // Holen der Parameterliste der Methode
		    List<Integer> paramTypes = methodSym.getParamTypes();

		    // Überprüfen der Anzahl der Argumente
		    if (paramTypes.size() != myExpList.size()) {
		        Errors.fatal(myId.getLineNum(), myId.getCharNum(),
		            "Method " + myId.getIdName() + " called with incorrect number of arguments.");
		        return;
		    }

		    // Typprüfung der Argumente
		    for (int i = 0; i < paramTypes.size(); i++) {
		        ExpNode arg = myExpList.get(i);
		        arg.analyzeType();
		        if (arg.getExpType() != paramTypes.get(i)) {
		        	Errors.fatal(myId.getLineNum(),myId.getColNum(),
		                "Argument " + (i + 1) + " of method " + myId.getIdName() +
		                " has incorrect type. Expected " + Types.ToString(paramTypes.get(i)) +
		                ", found " + Types.ToString(arg.getExpType()) + ".");
		        }
		    }
		
	}

	@Override
	protected void codeGen(int params, int localVarSize) {
		String Id = myId.getIdName();
		String IdMethod = Id+"Method";
		  // Überprüfen des Namens und des Typs der Methode
	    myId.analyzeType();

	    // Prüfen, ob die Methode rekursiv aufgerufen wird
		boolean isRecursive = Codegen.currentMethod != null && Codegen.currentMethod.equals(myId.getIdName());
	    Codegen.setIsRecursive(isRecursive);
	    SymbolTable.Sym methodSym = myId.getSymbol();
	    
	    methodSym.getNumParams();
	    if (methodSym == null || methodSym.getSymType() != Types.MethodType) {
	        Errors.fatal(myId.getLineNum(), myId.getCharNum(),
	            "Call to undeclared or invalid method: " + myId.getIdName());
	        return;
	    }

	    // Ermitteln der Parameterliste der Methode
	    List<Integer> paramTypes = methodSym.getParamTypes();

	    // Überprüfen der Anzahl der Argumente
	    if (paramTypes.size() != myExpList.size()) {
	        Errors.fatal(myId.getLineNum(), myId.getCharNum(),
	            "Method " + myId.getIdName() + " called with incorrect number of arguments.");
	        return;
	    }
	    Codegen.generate("subu",Codegen.SP,Codegen.SP,4); 
	    Codegen.generateIndexed("sw", Codegen.FP,Codegen.SP,0);
	   
	    // Typprüfung der Argumente
	    for (int i = 0; i < methodSym.getNumParams(); i++) {
	        ExpNode arg = myExpList.get(i);
	        arg.analyzeType();
	        if (arg.getExpType() != paramTypes.get(i)) {
	            Errors.fatal(myId.getLineNum(), myId.getColNum(),
	                "Argument " + (i + 1) + " of method " + myId.getIdName() +
	                " has incorrect type. Expected " + Types.ToString(paramTypes.get(i)) +
	                ", found " + Types.ToString(arg.getExpType()) + ".");
	        }
	    }

	    	for (int i = methodSym.getNumParams()-1 ; i >=0; i--) {
	    		//System.out.println("size"+i);
	        ExpNode arg = myExpList.get(i);
	        arg.codeGen(arg.computeNT()*(-8));
	        
	        Codegen.generate("subu",Codegen.SP,Codegen.SP,4);
	        Codegen.generateIndexed("sw", Codegen.A0, Codegen.SP,0);
			
	        
	        
	    }
	    
	    	
	    
	    	if(Codegen.isReserved(Id)) {
	    		Id = IdMethod;
	    	}
	    	 // Aufruf der Methode
		    Codegen.generate("jal", Id);
	   // System.out.println(myId.getExpType());

	}

	@Override
	protected int computeNt() {
		// TODO Automatisch generierter Methodenstub
		return 0;
	}

	@Override
	public void analyzeType(int params) {
		 myId.analyzeType();

		    // Überprüfen, ob der Identifier eine Methode ist
		    SymbolTable.Sym methodSym = myId.getSymbol();
		    if (methodSym == null || methodSym.getSymType() != Types.MethodType) {
		        Errors.fatal(myId.getLineNum(), myId.getCharNum(),
		            "Call to undeclared or invalid method: " + myId.getIdName());
		       
		    }

		    // Holen der Parameterliste der Methode
		    List<Integer> paramTypes = methodSym.getParamTypes();

		    // Überprüfen der Anzahl der Argumente
		    if (paramTypes.size() != myExpList.size()) {
		        Errors.fatal(myId.getLineNum(), myId.getCharNum(),
		            "Method " + myId.getIdName() + " called with incorrect number of arguments.");
		        return;
		    }

		    // Typprüfung der Argumente
		    for (int i = 0; i < paramTypes.size(); i++) {
		        ExpNode arg = myExpList.get(i);
		        arg.analyzeType();
		        if (arg.getExpType() != paramTypes.get(i)) {
		        	Errors.fatal(myId.getLineNum(),myId.getColNum(),
		                "Argument " + (i + 1) + " of method " + myId.getIdName() +
		                " has incorrect type. Expected " + Types.ToString(paramTypes.get(i)) +
		                ", found " + Types.ToString(arg.getExpType()) + ".");
		        }
		    }
		
	}

}

class ReturnStmtNode extends StmtNode {
    public ReturnStmtNode() {
    }

    public ReturnStmtNode(ExpNode exp) {
        myExp=exp;
    }
 
    public void decompile(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("return");
        if (myExp != null) {
            p.print(" ");
            myExp.decompile(p, indent);
        }
        else {
        	p.print("");
        }
        p.println(";");
    }

    private ExpNode myExp;

	@Override
	protected void analyzeName(SymbolTable symTable) {
	
			// TODO Automatisch generierter Methodenstub
		if(myExp==null) {
			return;
		}
			myExp.analyzeName(symTable);
		
		
	}
	
	public int getReturnType() {
		return Types.IntType;
	}

	@Override
	protected void analyzeType() {
		 
		        // Den Typ des Rückgabewerts prüfen
				if(myExp==null) {
					return;
				}
					
					 myExp.analyzeType();
				
		       
		        
		        // Den erwarteten Rückgabetyp (der von der Methode erwartet wird) abrufen
		        int expectedReturnType = getReturnType();
		        
		        // Wenn der Rückgabetyp des Ausdrucks nicht mit dem erwarteten übereinstimmt, Fehler ausgeben
		        if (myExp.getExpType() != expectedReturnType &&myExp.getExpType()!=Types.VoidType) {
		        	Errors.fatal(myExp.getLineNum(),myExp.getColNum(),
		                "Return type mismatch. Expected " + Types.ToString(expectedReturnType) +
		                ", found " + Types.ToString(myExp.getExpType()) + ".");
		        }
		   
		
	}

	@Override
	protected void codeGen(int params, int localVarSize) {
	    if (myExp != null) {
	        // Wenn ein Ausdruck vorhanden ist, berechne seinen Wert
	        myExp.codeGen(myExp.computeNT()*(-8)); // Berechnet den Ausdruck und speichert den Wert in $v0
	    } else {
	        // Wenn kein Ausdruck vorhanden ist, handle das als Rückgabe ohne Wert (void)
	        Codegen.generate("li", Codegen.V0, 0); // Setze Rückgabewert auf 0
	    }

	    // Rücksprung zu Aufrufer
	    Codegen.generate("move", Codegen.V0, Codegen.A0); 
	    
	    //Codegen.generate("jr", Codegen.RA);  // Rücksprungadresse in $ra verwenden
	}

	@Override
	protected int computeNt() {
		// TODO Automatisch generierter Methodenstub
		return 0;
	}

	@Override
	public void analyzeType(int params) {
		 // Den Typ des Rückgabewerts prüfen
		if(myExp==null) {
			return;
		}
			
			 myExp.analyzeType();
		
        int expectedReturnType = getReturnType();
        
        // Wenn der Rückgabetyp des Ausdrucks nicht mit dem erwarteten übereinstimmt, Fehler ausgeben
        if (myExp.getExpType() != expectedReturnType &&myExp.getExpType()!=Types.VoidType) {
        	Errors.fatal(myExp.getLineNum(),myExp.getColNum(),
                "Return type mismatch. Expected " + Types.ToString(expectedReturnType) +
                ", found " + Types.ToString(myExp.getExpType()) + ".");
        }
		
	}

}

class SwitchStmtNode extends StmtNode {

@Override
protected void analyzeName(SymbolTable symTable) {
	myExp.analyzeName(symTable);
    // Analysiere den Ausdruck (Schlüssel)
    if (myExp != null) {
    	if((myExp.getExpType()!=Types.IntType)) {
    		Errors.fatal(myExp.getLineNum(),myExp.getColNum(),"Switch expression must be of type Integer. but was "+myExp.getExpType());
    	}
       
    }
    // Analysiere die Switch-Gruppen
    if (mySwitchGroupList != null) {
        mySwitchGroupList.analyzeName(symTable);
    }
}
   
    public SwitchStmtNode(ExpNode exp, SwitchGroupListNode sgl) {
        myExp=exp;
        mySwitchGroupList=sgl;
    }
 
    public void decompile(PrintWriter p, int indent) {
        doIndent(p,indent);
        p.print("switch");
        p.print("(");
        myExp.decompile(p,indent);
        p.print(")");
        p.println("{");
        mySwitchGroupList.decompile(p,indent+4);
        doIndent(p,indent);
        p.println("}");
    }

    private ExpNode myExp;
    private SwitchGroupListNode mySwitchGroupList;
	@Override
	protected void analyzeType() {
		if(myExp.getExpType()!=Types.IntType) {
			Errors.fatal(myExp.getLineNum(),myExp.getColNum(),"Switch expression must be of type int.");
		}
		if (mySwitchGroupList != null) {
	        mySwitchGroupList.analyzeType();
	    }
	}

	@Override
	protected void codeGen(int params, int localVarSize) {
	   
	}

	@Override
	protected int computeNt() {
		// TODO Automatisch generierter Methodenstub
		return 0;
	}

	@Override
	public void analyzeType(int params) {
		// TODO Automatisch generierter Methodenstub
		
	}




}

class BlockStmtNode extends StmtNode {


    

    public BlockStmtNode(DeclListNode d, StmtListNode slist) {
        myDeclList=d;
        myStmtList=slist;
    }
 
    public void decompile(PrintWriter p, int indent) {
        doIndent(p,indent);
        p.println("{");
        myDeclList.decompile(p,indent+4);
        myStmtList.decompile(p,indent+4);
        doIndent(p,indent);
        p.println("}");
    }
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;
    private int currentOffset = 0;
    
	@Override
	protected void analyzeName(SymbolTable symTable) {
	    symTable.enterScope(); // Neuer Block -> neuer Scope
	    if (myDeclList != null) {
	        myDeclList.analyzeName(symTable);
	    }
	    if (myStmtList != null) {
	        myStmtList.analyzeName(symTable);
	    }
	    symTable.leaveScope(); // Scope verlassen
	}
	
	@Override
	protected void analyzeType() {
		   
			
		    // Deklarationen analysieren
		if (myDeclList != null) {
		     
		     try {
		    	 for(myDeclList.getDecls().start(); myDeclList.getDecls().isCurrent();myDeclList.getDecls().advance()) {
		    		 if(myDeclList.getDecls().getCurrent() instanceof VarDeclNode) {
		    			 VarDeclNode var = (VarDeclNode) myDeclList.getDecls().getCurrent();
			    		 var.setOffset(currentOffset);
			    		// System.out.println( ", Offset methodbody: " + currentOffset);
			    		 var.analyzeType();
			    		 currentOffset=-4;
		    		 }
		    	 }
		     } catch (NoCurrentException ex) {
		    	 System.err.println("unexpected NoCurrentException in MethodBodyNode.analyzeType");
		         System.exit(-1);
		     }	
		 }

		    // Anweisungen im Block analysieren
		    if (myStmtList != null) {
		        myStmtList.analyzeType(0);  // Anweisungen im Block analysieren
		    }
	}

	@Override
    protected void codeGen(int params, int localVarSize) {
        // Neuen Scope betreten
        Codegen.p.println("# Entering new block");

        // Generiere Code für Deklarationen
        if (myDeclList != null) {
            myDeclList.codeGen();
        }

        // Generiere Code für Statements
        if (myStmtList != null) {
            myStmtList.codeGen(params, localVarSize);
        }

        // Scope verlassen
        Codegen.p.println("# Exiting block");
    }

	@Override
	protected int computeNt() {
		// TODO Automatisch generierter Methodenstub
		return 0;
	}

	@Override
	public void analyzeType(int params) {
	    // Deklarationen analysieren
			if (myDeclList != null) {
			     
			     try {
			    	 for(myDeclList.getDecls().start(); myDeclList.getDecls().isCurrent();myDeclList.getDecls().advance()) {
			    		 if(myDeclList.getDecls().getCurrent() instanceof VarDeclNode) {
			    			 VarDeclNode var = (VarDeclNode) myDeclList.getDecls().getCurrent();
				    		 var.setOffset(currentOffset+params*4);
				    		// System.out.println( ", Offset methodbody: " + currentOffset);
				    		 var.analyzeType();
				    		 currentOffset+=4;
			    		 }
			    	 }
			     } catch (NoCurrentException ex) {
			    	 System.err.println("unexpected NoCurrentException in MethodBodyNode.analyzeType");
			         System.exit(-1);
			     }	
			 }

			    // Anweisungen im Block analysieren
			    if (myStmtList != null) {
			        myStmtList.analyzeType(params);  // Anweisungen im Block analysieren
			    }
	}



	
}

abstract class ExpNode extends ASTnode {

	protected abstract void analyzeName(SymbolTable symTable);

	

	protected int getInt() {
		// TODO Automatisch generierter Methodenstub
		return 0;
	}

	protected String getStrVal() {
		// TODO Automatisch generierter Methodenstub
		return null;
	}

	protected abstract void codeGen(int n);
	

	protected abstract void analyzeType();

	public int getExpType() {
		return Types.ErrorType;
	}

	protected abstract int getLineNum();

	protected abstract int getCharNum();
	protected abstract int getColNum();

	protected abstract int computeNT();
	
}

class SwitchGroupNode extends ASTnode {

public void analyzeName(SymbolTable symTable) {
    if (mySwitchLabel != null) {
        mySwitchLabel.analyzeName(symTable);
    }
    if (myStmtList != null) {
        myStmtList.analyzeName(symTable);
    }
}



protected void codeGen() {
    // Setze das Label für diesen Block
    if (mySwitchLabel != null) {
       mySwitchLabel.codeGen();
    }
}




	public void analyzeType() {
    	  if (mySwitchLabel != null) {
    	        mySwitchLabel.analyzeType();  // Überprüfen des Switch-Labels (case oder default)
    	    }

    	    // Überprüfen der Statements (falls vorhanden)
    	    if (myStmtList != null) {
    	        myStmtList.analyzeType(0);  // Überprüfen der Statements im Fall
    	    }
	
}
	public SwitchGroupNode() {
	
    }

    public SwitchGroupNode(SwitchLabelNode sln, StmtListNode slist) {
	mySwitchLabel = sln;
    myStmtList = slist;
    }

    public void decompile(PrintWriter p, int indent) {
        doIndent(p,indent);
        mySwitchLabel.decompile(p,indent);
        myStmtList.decompile(p,indent);

    }
    
    
    private SwitchLabelNode mySwitchLabel;
    private StmtListNode myStmtList;
	@Override
	public void analyzeType(int params) {
		// TODO Automatisch generierter Methodenstub
		
	}
}

class SwitchLabelNode extends ASTnode {

public void analyzeName(SymbolTable symTable) {
    if (myExp != null) {
    	if(!(myExp instanceof IntLitNode)) {
    		Errors.fatal(myExp.getLineNum(),myExp.getColNum(),
                    "Switch case label must be an integer constant.");
    	}
        myExp.analyzeName(symTable);
    }
}
   public ExpNode getExp() {
	   return myExp;
   }

protected void codeGen() {
    String caseLabel = Codegen.nextLabel();

    // Generiere Label für den Case
    Codegen.genLabel(caseLabel);

    // Generiere Code für Statements
    if (myExp != null) {
        myExp.codeGen(myExp.computeNT()*(-8));
    }
}


	public void analyzeType() {
    	if (myExp != null) {
            if (myExp.getExpType() != Types.IntType) {
                Errors.fatal(myExp.getLineNum(),myExp.getColNum(),
                        "Switch case label must be of type int.");
            }
        }
	
}

	public SwitchLabelNode(ExpNode exp) {
	myExp = exp;
    }

    public void decompile(PrintWriter p, int indent) {

        if(myExp != null){
            p.print("case ");
            myExp.decompile(p,indent);
            p.println(":"); 
        }
        else{
            p.print("default");
            p.println(":");
        }
    }
    private ExpNode myExp;
	@Override
	public void analyzeType(int params) {
		// TODO Automatisch generierter Methodenstub
		
	}
}

class CallExpNode extends ExpNode {
    public CallExpNode(IdNode id, ExpListNode elist) {
	myId = id;
	myExpList = elist;
    }

    public CallExpNode(IdNode id) {
	myId = id;
	myExpList = new ExpListNode(new Sequence());
    }

    public void decompile(PrintWriter p, int indent) {
        
        myId.decompile(p, indent);
        p.print("(");
        myExpList.decompile(p, indent);
        p.print(")");
    }
    public void analyzeName(SymbolTable symbolTable) {
    	myId.analyzeName(symbolTable);
    	myExpList.analyzeName(symbolTable);
    }
    
    private IdNode myId;
    private ExpListNode myExpList;
    
    
	@Override
	protected void analyzeType() {
		myId.analyzeType();  // Der Identifier muss analysiert werden, um sicherzustellen, dass es sich um eine Methode handelt
		
	    SymbolTable.Sym methodSym = myId.getSymbol();
	    
	    // Wenn der Identifier keine Methode ist, Fehler werfen
	    if (methodSym == null || methodSym.getSymType() != Types.MethodType) {
	        Errors.fatal(myId.getLineNum(), myId.getCharNum(),
	            "Call to undeclared or invalid method: " + myId.getIdName());
	        return;
	    }

	    myExpList.analyzeType(0);
	    // 2. Überprüfen der Argumenttypen
	    List<Integer> paramTypes = methodSym.getParamTypes();
	    int numParams = paramTypes.size();
	    int numArgs = myExpList.size();

	    // Überprüfen der Anzahl der Argumente
	    if (numParams != numArgs) {
	        Errors.fatal(myId.getLineNum(), myId.getCharNum(),
	            "Method " + myId.getIdName() + " called with incorrect number of arguments."+numParams+""+numArgs+" ,"+myId.getSymbol());
	        return;
	    }
	    
	    // Überprüfen der Argumenttypen
	    for (int i = 0; i < numParams; i++) {
	        ExpNode arg = myExpList.get(i);
	        arg.analyzeType();  // Argumenttyp prüfen
	        if (arg.getExpType() != paramTypes.get(i)) {
	            Errors.fatal(myId.getLineNum(), myId.getCharNum(),
	                "Argument " + (i + 1) + " of method " + myId.getIdName() +
	                " has incorrect type. Expected " + Types.ToString(paramTypes.get(i)) +
	                ", found " + Types.ToString(arg.getExpType()) + ".");
	        }
	       
	    }
		
	}

	@Override
	protected int getLineNum() {
		// TODO Automatisch generierter Methodenstub
		return 0;
	}

	@Override
	protected int getCharNum() {
		// TODO Automatisch generierter Methodenstub
		return 0;
	}

	@Override
	protected int getColNum() {
		// TODO Automatisch generierter Methodenstub
		return 0;
	}
	
	public int getExpType() {
		return Types.IntType;
	}

	 @Override
	protected void codeGen(int nt) {
		 String Id = myId.getIdName();
		 String IdMethod = Id+"Method";
		 // Prüfen, ob die Methode rekursiv aufgerufen wird
			boolean isRecursive = Codegen.currentMethod != null && Codegen.currentMethod.equals(myId.getIdName());
		    Codegen.setIsRecursive(isRecursive);
		   
		    SymbolTable.Sym methodSym = myId.getSymbol();
		    
		    methodSym.getNumParams();
		    if (methodSym == null || methodSym.getSymType() != Types.MethodType) {
		        Errors.fatal(myId.getLineNum(), myId.getCharNum(),
		            "Call to undeclared or invalid method: " + myId.getIdName());
		        return;
		    }

		    // Ermitteln der Parameterliste der Methode
		    List<Integer> paramTypes = methodSym.getParamTypes();

		    // Überprüfen der Anzahl der Argumente
		    if (paramTypes.size() != myExpList.size()) {
		        Errors.fatal(myId.getLineNum(), myId.getCharNum(),
		            "Method " + myId.getIdName() + " called with incorrect number of arguments.");
		        return;
		    }

		    // Typprüfung der Argumente
		    for (int i = 0; i < methodSym.getNumParams(); i++) {
		        ExpNode arg = myExpList.get(i);
		        arg.analyzeType();
		        if (arg.getExpType() != paramTypes.get(i)) {
		            Errors.fatal(myId.getLineNum(), myId.getColNum(),
		                "Argument " + (i + 1) + " of method " + myId.getIdName() +
		                " has incorrect type. Expected " + Types.ToString(paramTypes.get(i)) +
		                ", found " + Types.ToString(arg.getExpType()) + ".");
		        }
		    }
		    Codegen.generate("subu",Codegen.SP,Codegen.SP,4); 
		    Codegen.generateIndexed("sw", Codegen.FP,Codegen.SP,0);
		   
		    // Argumente in die richtigen Register legen oder auf den Stack schreiben
		    	for (int i = methodSym.getNumParams()-1 ; i >=0; i--) {
		    		//System.out.println("size"+i);
		        ExpNode arg = myExpList.get(i);
		        arg.codeGen(arg.computeNT()*(-8));
		        
		        // Argument ins passende Register ($a0 - $a3) legen oder auf den Stack schieben
		       
		       /* case 0: Codegen.generate("move", Codegen.T0, Codegen.A0); break;
	            case 1: Codegen.generate("move", Codegen.T1, Codegen.A0); break;
	            case 2: Codegen.generate("move", Codegen.T2, Codegen.A0); break;
	            case 3: Codegen.generate("move", Codegen.T3, Codegen.A0); break;
	            case 4: Codegen.generate("move", Codegen.T4, Codegen.A0); break;
	            case 5: Codegen.generate("move", Codegen.T5, Codegen.A0); break;
	            case 6: Codegen.generate("move", Codegen.T6, Codegen.A0); break;
	            case 7: Codegen.generate("move", Codegen.T7, Codegen.A0); break;
	            case 8: Codegen.generate("move", Codegen.T8, Codegen.A0); break;
	            case 9: Codegen.generate("move", Codegen.T9, Codegen.A0); break;*/
		        
		        Codegen.generate("subu",Codegen.SP,Codegen.SP,4); 
		       Codegen.generateIndexed("sw", Codegen.A0, Codegen.SP,0);
		     
		    	}		
		    	
		    	if(Codegen.isReserved(Id)) {
		    		Id = IdMethod;
		    	}
		    	 // Aufruf der Methode
			    Codegen.generate("jal", Id);
			  //  System.out.println(myId.getExpType());
		        
	 }

	 @Override
	 protected int computeNT() {
	     int maxNT = 0; // Initialisiere den maximalen Wert

	     for(int i =0; i<myExpList.size();i++) {
	    	 ExpNode arg = myExpList.get(i);
	    	 int currentNT = arg.computeNT();
	    	 maxNT = Math.max(maxNT, currentNT);
	     }
	   

	     // Gib das Maximum zurück
	     return maxNT;
	 }

	@Override
	public void analyzeType(int params) {
myId.analyzeType();  // Der Identifier muss analysiert werden, um sicherzustellen, dass es sich um eine Methode handelt
		
	    SymbolTable.Sym methodSym = myId.getSymbol();
	    
	    // Wenn der Identifier keine Methode ist, Fehler werfen
	    if (methodSym == null || methodSym.getSymType() != Types.MethodType) {
	        Errors.fatal(myId.getLineNum(), myId.getCharNum(),
	            "Call to undeclared or invalid method: " + myId.getIdName());
	        return;
	    }

	    myExpList.analyzeType(params);
	    // 2. Überprüfen der Argumenttypen
	    List<Integer> paramTypes = methodSym.getParamTypes();
	    int numParams = paramTypes.size();
	    int numArgs = myExpList.size();

	    // Überprüfen der Anzahl der Argumente
	    if (numParams != numArgs) {
	        Errors.fatal(myId.getLineNum(), myId.getCharNum(),
	            "Method " + myId.getIdName() + " called with incorrect number of arguments."+numParams+""+numArgs+" ,"+myId.getSymbol());
	        return;
	    }
	    
	    // Überprüfen der Argumenttypen
	    for (int i = 0; i < numParams; i++) {
	        ExpNode arg = myExpList.get(i);
	        arg.analyzeType();  // Argumenttyp prüfen
	        if (arg.getExpType() != paramTypes.get(i)) {
	            Errors.fatal(myId.getLineNum(), myId.getCharNum(),
	                "Argument " + (i + 1) + " of method " + myId.getIdName() +
	                " has incorrect type. Expected " + Types.ToString(paramTypes.get(i)) +
	                ", found " + Types.ToString(arg.getExpType()) + ".");
	        }
	       
	    }
		
		
	}


	
}

class IntLitNode extends ExpNode {
    public IntLitNode(int lineNum, int colNum, int intVal) {
	myLineNum = lineNum;
	myColNum = colNum;
	myIntVal = intVal;
    }

    public void decompile(PrintWriter p, int indent) {
        p.print(myIntVal);
    }

    private int myLineNum;
    private int myColNum;
    private int myIntVal;
	@Override
	protected void analyzeName(SymbolTable symTable) {
		// TODO Automatisch generierter Methodenstub
		
	}

	@Override
	protected void analyzeType() {
		// TODO Automatisch generierter Methodenstub
		
	}

	@Override
	protected int getLineNum() {
		// TODO Automatisch generierter Methodenstub
		return myLineNum;
	}

	@Override
	protected int getCharNum() {
		// TODO Automatisch generierter Methodenstub
		return myColNum;
	}

	@Override
	protected int getColNum() {
		// TODO Automatisch generierter Methodenstub
		return myColNum;
	}
	
	public int getExpType() {
		return Types.IntType;
	}
	@Override
	protected int getInt() {
		return myIntVal;
	}

	 @Override
	    protected void codeGen(int nt) {
	        // Lade den Literalwert in $t0
	        Codegen.generate("li", Codegen.A0, myIntVal);
	        
	        //Codegen.genPush(Codegen.T0);
	    }

	@Override
	protected int computeNT() {
		// TODO Automatisch generierter Methodenstub
		return 0;
	}

	@Override
	public void analyzeType(int params) {
		// TODO Automatisch generierter Methodenstub
		
	}

	
}

class StringLitNode extends ExpNode {
    public StringLitNode(int lineNum, int colNum, String strVal) {
	myLineNum = lineNum;
	myColNum = colNum;
	myStrVal = strVal;
    }

    public void decompile(PrintWriter p, int indent) {
        p.print("" + myStrVal + "");
    }

    private int myLineNum;
    private int myColNum;
    private String myStrVal;
	@Override
	protected void analyzeName(SymbolTable symTable) {
		// TODO Automatisch generierter Methodenstub
		
	}

	@Override
	protected void analyzeType() {
		// TODO Automatisch generierter Methodenstub
		
	}
	public int getColNum() {
		return myColNum;
	}
	
	public int getLineNum() {
		return myLineNum;
	}

	@Override
	protected int getCharNum() {
		// TODO Automatisch generierter Methodenstub
		return myColNum;
	}
	public int getExpType() {
		return Types.StringType;
	}

	 @Override
	    protected void codeGen(int nt) {
	        // Erzeuge ein Label für den String und lade die Adresse in $t0
	        String stringLabel = Codegen.nextLabel();
	        Codegen.p.println("\t.data");
	        Codegen.generateLabeled(stringLabel, ".asciiz", "", "" + myStrVal + "");
	        Codegen.p.println("\t.text");
	        Codegen.generate("la", Codegen.A0, stringLabel);
	        //Codegen.genPush(Codegen.T0);
	    }

	@Override
	protected int computeNT() {
		// TODO Automatisch generierter Methodenstub
		return 0;
	}

	@Override
	public void analyzeType(int params) {
		// TODO Automatisch generierter Methodenstub
		
	}
}

class TrueNode extends ExpNode {

@Override
protected void analyzeName(SymbolTable symTable) {
    // Keine Analyse erforderlich für Literale
}
    public TrueNode(int lineNum, int colNum) {
	myLineNum = lineNum;
	myColNum = colNum;
    }

    public int getColNum() {
		return myColNum;
	}
	
	public int getLineNum() {
		return myLineNum;
	}
    
    public void decompile(PrintWriter p, int indent) {
        p.print("true");
    }

    private int myLineNum;
    private int myColNum;
	@Override
	protected void analyzeType() {
		// TODO Automatisch generierter Methodenstub
		
	}
	@Override
	protected int getCharNum() {
		// TODO Automatisch generierter Methodenstub
		return myColNum;
	}
	public int getExpType() {
		return Types.BoolType;
	}
	@Override
    protected void codeGen(int nt) {
        // Lade den Wert für true in $t0
        Codegen.generate("li", Codegen.A0, Codegen.TRUE);
    }
	@Override
	protected int computeNT() {
		// TODO Automatisch generierter Methodenstub
		return 0;
	}
	@Override
	public void analyzeType(int params) {
		// TODO Automatisch generierter Methodenstub
		
	}
	
}

class FalseNode extends ExpNode {

@Override
protected void analyzeName(SymbolTable symTable) {
    // Keine Analyse erforderlich für Literale
}
    public FalseNode(int lineNum, int colNum) {
	myLineNum = lineNum;
	myColNum = colNum;
    }

    public void decompile(PrintWriter p, int indent) {
        p.print("false");
    }

    private int myLineNum;
    private int myColNum;
	@Override
	protected void analyzeType() {
		// TODO Automatisch generierter Methodenstub
		
	}
	public int getColNum() {
		return myColNum;
	}
	
	public int getLineNum() {
		return myLineNum;
	}
	@Override
	protected int getCharNum() {
		// TODO Automatisch generierter Methodenstub
		return myColNum;
	}
	public int getExpType() {
		return Types.BoolType;
	}
	@Override
    protected void codeGen(int nt) {
        // Lade den Wert für true in $t0
        Codegen.generate("li", Codegen.A0, Codegen.FALSE);
    }
	@Override
	protected int computeNT() {
		// TODO Automatisch generierter Methodenstub
		return 0;
	}
	@Override
	public void analyzeType(int params) {
		// TODO Automatisch generierter Methodenstub
		
	}
}

class IdNode extends ExpNode
{
    public IdNode(int lineNum, int charNum, String idVal) {
	myLineNum = lineNum;
	myCharNum = charNum;
	myStrVal = idVal;
    }
  
	public int getCharNum() {
		return myCharNum;
	}
	
	public int getLineNum() {
		return myLineNum;
	}
    
    public void analyzeName(SymbolTable symbolTable) {
        // Suchen des Bezeichners in der Symboltabelle
    	//System.out.println("Looking up: " + myStrVal + " in symbol table.");
    	SymbolTable.Sym symbol = symbolTable.lookup(myStrVal);
    	/*if (symbol == null) {
    	    System.out.println("Not found: " + myStrVal);
    	} else {
    	    System.out.println("Found: " + myStrVal + " -> " + symbol);
    	}*/

        this.sym = symbol;  // Symbol zuweisen
        
    }
    
    
   	public void decompile(PrintWriter p, int indent) {
   		p.print(myStrVal);
   		if(sym != null&& sym.isLocal()== false){
      		 p.print(" (" + sym + ")");
   		}
    }
    
    public String getIdName() {
    	return myStrVal;
    }
    
    public int getType() {
        if (sym == null) {
            return Types.ErrorType;
        }
        return sym.getSymType();
    }
    
    public int getExpType() {
        if (sym == null) {
            return Types.ErrorType;
        }
        return sym.getSymType();
    }
    
    @Override
    protected void analyzeType() {
        if (sym == null) {
            Errors.fatal(myLineNum, myCharNum, "Cannot determine type of undeclared identifier: " + myStrVal);
        }
    }
    
    public SymbolTable.Sym getSymbol() {
		
		return sym;
	}
	public void setSymbol(SymbolTable.Sym sym) {
		
		this.sym = sym;
	}
	

    private int myLineNum;
    private int myCharNum;
    private String myStrVal;
    private SymbolTable.Sym sym;
	
	@Override
	protected int getColNum() {
		// TODO Automatisch generierter Methodenstub
		return myCharNum;
	}

	@Override
	protected void codeGen(int nt) {
		  String id = myStrVal;
		    String idReserved = id+"Var";
		    if(Codegen.isReserved(id)) {
		    	id = idReserved;
		    }
	    // Hole das Symbol für diese Variable
	    SymbolTable.Sym sym = getSymbol();
	    
	    if (sym == null) {
	        Errors.fatal(getLineNum(), getCharNum(),
	                     "Undefined identifier: " + getIdName());
	        return;
	    }

	    // Prüfe, ob die Variable lokal oder global ist
	    if (sym.isLocal()) {
	        // Lokale Variable: Lade den Wert basierend auf dem Offset
	        int offset = sym.getOffset();
	        Codegen.generateIndexed("lw", Codegen.A0, Codegen.FP, offset,
	                                 "Load local variable " + id);
	    } else {
	    	//System.out.println("hi");
	        // Globale Variable: Lade den Wert basierend auf dem Label oder Adresse
	        String label = id;
	        Codegen.generateWithComment("la","Load address of global variable " + id, Codegen.T0, label);
	        Codegen.generateIndexed("lw", Codegen.A0, Codegen.T0, 0,
	                                 "Load global variable " + id);
	    }
	}


	@Override
	protected int computeNT() {
		// TODO Automatisch generierter Methodenstub
		return 0;
	}
	@Override
	protected String getStrVal() {
		return myStrVal;
	}

	@Override
	public void analyzeType(int params) {
		 if (sym == null) {
	            Errors.fatal(myLineNum, myCharNum, "Cannot determine type of undeclared identifier: " + myStrVal);
	        }
		
	}
}

abstract class UnaryExpNode extends ExpNode {
    public UnaryExpNode(ExpNode exp) {
	myExp = exp;
    }
    protected ExpNode myExp;
}

abstract class BinaryExpNode extends ExpNode
{
    public BinaryExpNode(ExpNode exp1, ExpNode exp2) {
	myExp1 = exp1;
	myExp2 = exp2;
    }
    @Override
    protected void analyzeName(SymbolTable symTable) {
        if (myExp1 != null) {
            myExp1.analyzeName(symTable);
        }
        if (myExp2 != null) {
            myExp2.analyzeName(symTable);
        }
    }
    
   /* @Override
    protected void codeGen() {
        myExp1.codeGen(); // Wert des linken Operanden in $t0
        Codegen.genPush(Codegen.T0); // Push $t0 auf den Stack

        myExp2.codeGen(); // Wert des rechten Operanden in $t0
        Codegen.genPop(Codegen.T1); // Pop $t1 vom Stack

        
		switch (operator) {
            case '+':
                Codegen.generate("add", Codegen.T0, Codegen.T1, Codegen.T0);
                break;
            case '-':
                Codegen.generate("sub", Codegen.T0, Codegen.T1, Codegen.T0);
                break;
            default:
                Errors.fatal(0, 0, "Unsupported binary operator.");
        }
    }*/

    
    
    
    protected ExpNode myExp1;
    protected ExpNode myExp2;
    private int operator;
    
}

class UnaryMinusNode extends UnaryExpNode
{
    public UnaryMinusNode(ExpNode exp) {
	super(exp);
    }

    public void decompile(PrintWriter p, int indent) {
        
        myExp.decompile(p,indent);
    }

	@Override
	protected void analyzeName(SymbolTable symTable) {
		myExp.analyzeName(symTable);
		
	}

	@Override
	protected void analyzeType() {
		 myExp.analyzeType();

		    // Prüfe, ob der Operand vom Typ int ist
		    if (myExp.getExpType() != Types.IntType) {
		    	Errors.fatal(myExp.getLineNum(), myExp.getCharNum(),
		                "Unary minus operator applied to non-numeric operand.");
		    }
		
	}

	@Override
	protected int getLineNum() {
	    // Die Line-Nummer von myExp zurückgeben
	    return myExp.getLineNum();
	}

	@Override
	protected int getCharNum() {
	    // Die Char-Nummer von myExp zurückgeben
	    return myExp.getCharNum();
	}

	@Override
	protected int getColNum() {
	    // Die Col-Nummer von myExp zurückgeben
	    return myExp.getColNum();
	}
	
	public int getExpType() {
		return Types.IntType;
	}

	 @Override
	    public void codeGen(int nt) {
	        // Generiere den Code für den untergeordneten Ausdruck
	        myExp.codeGen(myExp.computeNT()*(-8)); 
	        
	        // Der Wert des Ausdrucks ist jetzt in $t0
	        // Negiere den Wert und speichere ihn wieder in $t0
	        Codegen.generate("neg", Codegen.A0, Codegen.A0);
	        
	        // Der negierte Wert kann bei Bedarf auf den Stack gelegt werden
	       // Codegen.genPush(Codegen.T0);
	    }

	@Override
	protected int computeNT() {
		return myExp.computeNT();
	}

	@Override
	public void analyzeType(int params) {
		// myExp.analyzeType();

	    // Prüfe, ob der Operand vom Typ int ist
	    if (myExp.getExpType() != Types.IntType) {
	    	Errors.fatal(myExp.getLineNum(), myExp.getCharNum(),
	                "Unary minus operator applied to non-numeric operand.");
	    }
	
	}
}

class NotNode extends UnaryExpNode
{
	public int getExpType() {
		return Types.BoolType;
	}
    public NotNode(ExpNode exp) {
	super(exp);
    }

    public void decompile(PrintWriter p, int indent) {
        p.print("!");
        myExp.decompile(p,indent);
    }

	@Override
	protected void analyzeName(SymbolTable symTable) {
		myExp.analyzeName(symTable);
		
	}

	@Override
	protected void analyzeType() {
		myExp.analyzeType();

	    // Prüfe, ob der Operand vom Typ int ist
	    if (myExp.getExpType() != Types.BoolType) {
	        Errors.fatal(myExp.getLineNum(),myExp.getColNum(),
	        		"Logical NOT operator applied to non-boolean operand.");
	    }
		
	}

	@Override
	protected int getLineNum() {
		// TODO Automatisch generierter Methodenstub
		return myExp.getLineNum();
	}

	@Override
	protected int getCharNum() {
		// TODO Automatisch generierter Methodenstub
		return myExp.getCharNum();
	}

	@Override
	protected int getColNum() {
		// TODO Automatisch generierter Methodenstub
		return myExp.getColNum();
	}
	   @Override
	    public void codeGen(int tn) {
	        // Generiere den Code für den untergeordneten Ausdruck
	        myExp.codeGen(myExp.computeNT()*(-8)); 
	        
	        // Der Wert des Ausdrucks ist jetzt in $t0
	        // Negiere den Wert logischerweise: NOT ($t0 == FALSE ? TRUE : FALSE)
	        
	        Codegen.generate("seq", Codegen.A0, Codegen.A0, Codegen.FALSE);
	       
	        
	        // Der negierte Wert kann auf den Stack gelegt werden
	        //Codegen.genPush(Codegen.T0);
	    }
	@Override
	protected int computeNT() {
		// TODO Automatisch generierter Methodenstub
		return myExp.computeNT();
	}
	@Override
	public void analyzeType(int params) {
		myExp.analyzeType();

	    // Prüfe, ob der Operand vom Typ int ist
	    if (myExp.getExpType() != Types.BoolType) {
	        Errors.fatal(myExp.getLineNum(),myExp.getColNum(),
	        		"Logical NOT operator applied to non-boolean operand.");
	    }
		
	}
}

class PowerNode extends BinaryExpNode
{
    public PowerNode(ExpNode exp1, ExpNode exp2) {
	super(exp1, exp2);
    }
    public int getExpType() {
		return Types.IntType;
	}
    
    public void decompile(PrintWriter p, int indent) {
        myExp1.decompile(p,indent);
        p.print(" ");
        p.print("**");
        p.print(" ");
        myExp2.decompile(p,indent);
    }

	@Override
	protected void analyzeName(SymbolTable symTable) {
		// TODO Automatisch generierter Methodenstub
		myExp1.analyzeName(symTable);
		myExp2.analyzeName(symTable);
	}

	@Override
	protected void analyzeType() {
		myExp1.analyzeType();
	    myExp2.analyzeType();

	    
	    int type1 = myExp1.getExpType();
	    int type2 = myExp2.getExpType();

	    if (type1 != Types.IntType) {
            Errors.fatal(myExp1.getLineNum(), myExp1.getColNum(),
                "Left operand of exponentiation operator must be an integer.");
        }

        if (type2 != Types.IntType) {
            Errors.fatal(myExp2.getLineNum(), myExp2.getColNum(),
                "Right operand of exponentiation operator must be an integer.");
        }
		
	}

	@Override
	protected int getLineNum() {
		// TODO Automatisch generierter Methodenstub
		return myExp1.getLineNum();
	}

	@Override
	protected int getCharNum() {
		// TODO Automatisch generierter Methodenstub
		return myExp1.getCharNum();
	}

	@Override
	protected int getColNum() {
		// TODO Automatisch generierter Methodenstub
		return myExp1.getColNum();
	}
	@Override
    public void codeGen(int nt) {
		Codegen.generate("li",Codegen.T1,Codegen.TRUE);
        // Generiere Code für die Basis
        myExp1.codeGen(nt);
      //  Codegen.generateIndexed("sw", Codegen.A0, Codegen.FP, nt, "Store result of e1");
        Codegen.genPush(Codegen.A0); // Basis in $t0

        // Generiere Code für den Exponenten
        myExp2.codeGen(nt+4);
       // Codegen.generateIndexed("lw", Codegen.A1, Codegen.FP, nt, "Load result of e1");
        Codegen.genPop(Codegen.A1); // Exponent in $t1
        

        // Initialisiere das Ergebnis auf 1
        Codegen.generate("li", Codegen.A2, 1); // Ergebnis (res) = 1

        // Label für die Schleife
        String loopLabel = Codegen.nextLabel();
        String truelabel = Codegen.nextLabel();
       
        String endLabel = Codegen.nextLabel();

        // Schleife starten: Prüfen, ob Exponent > 0
        Codegen.genLabel(loopLabel);
        Codegen.generate("bne",Codegen.A0,Codegen.ZERO,truelabel); // Wenn Exponent <= 0, beende Schleife
        Codegen.genPush(Codegen.ZERO);
        Codegen.generate("j", endLabel);
        // Multipliziere Ergebnis mit der Basis
        Codegen.genLabel(truelabel);
        Codegen.genPush(Codegen.ZERO);
        Codegen.generate("mul", Codegen.A2, Codegen.A2, Codegen.A1);

        // Verringere den Exponenten um 1
        Codegen.generate("sub", Codegen.A0, Codegen.A0, 1);
        

        // Springe zurück zum Schleifenbeginn
        Codegen.generate("j", loopLabel);

        // Schleifenende
       Codegen.genLabel(endLabel);
       Codegen.generate("move",Codegen.A0, Codegen.A2);

        // Ergebnis auf den Stack legen
        //Codegen.genPush(Codegen.T2);
    }
	@Override
	protected int computeNT() {
		 int ntLeft = myExp1.computeNT();  // NT für linken Ausdruck (e1)
	     int ntRight = myExp2.computeNT(); // NT für rechten Ausdruck (e2)
	     return Math.max(ntLeft, 1 + ntRight);

	}
	@Override
	public void analyzeType(int params) {
		myExp1.analyzeType();
	    myExp2.analyzeType();

	    
	    int type1 = myExp1.getExpType();
	    int type2 = myExp2.getExpType();

	    if (type1 != Types.IntType) {
            Errors.fatal(myExp1.getLineNum(), myExp1.getColNum(),
                "Left operand of exponentiation operator must be an integer.");
        }

        if (type2 != Types.IntType) {
            Errors.fatal(myExp2.getLineNum(), myExp2.getColNum(),
                "Right operand of exponentiation operator must be an integer.");
        }
		
	}
	
	
	
}

class PlusNode extends BinaryExpNode
{
    public PlusNode(ExpNode exp1, ExpNode exp2) {
	super(exp1, exp2);
    }
   
    
    public void decompile(PrintWriter p, int indent) {
        p.print("(");
        myExp1.decompile(p,indent);
        p.print(" ");
        p.print("+");
        p.print(" ");
        myExp2.decompile(p,indent);
        p.print(")");
    }

	@Override
	protected void analyzeName(SymbolTable symTable) {
		myExp1.analyzeName(symTable);
		myExp2.analyzeName(symTable);
		
	}
	
	 @Override
	    public void analyzeType() {
	        myExp1.analyzeType();
	        myExp2.analyzeType();

	        int type1 = myExp1.getExpType();
	        int type2 = myExp2.getExpType();

	        if (type1 != Types.IntType) {
	            Errors.fatal(myExp1.getLineNum(), myExp1.getColNum(),
	                "Left operand of plus operator must be an integer. But it was "+Types.ToString(type1));
	        }

	        if (type2 != Types.IntType) {
	            Errors.fatal(myExp2.getLineNum(), myExp2.getColNum(),
	                "Right operand of plus operator must be an integer. But it was "+Types.ToString(type2));
	        }
	    }

	 @Override
		protected int getLineNum() {
			// TODO Automatisch generierter Methodenstub
			return myExp1.getLineNum();
		}

		@Override
		protected int getCharNum() {
			// TODO Automatisch generierter Methodenstub
			return myExp1.getCharNum();
		}

		@Override
		protected int getColNum() {
			// TODO Automatisch generierter Methodenstub
			return myExp1.getColNum();
		}
		
		 public int getExpType() {
				return Types.IntType;
			}
		 @Override
		 protected int computeNT() {
		     int ntLeft = myExp1.computeNT();  // NT für linken Ausdruck (e1)
		     int ntRight = myExp2.computeNT(); // NT für rechten Ausdruck (e2)
		     return Math.max(ntLeft, 1 + ntRight);
		 }


		  @Override
		  protected void codeGen(int nt) {
			  
			// Generiere Code für den linken Ausdruck
			    myExp1.codeGen(nt); // Speicherplatz `nt` verwenden
			/*  if(myExp2 instanceof CallExpNode) {
				  Codegen.generateIndexed("sw", Codegen.A0, Codegen.FP, nt-nt+4, "Store resulttt of e1");
			  }
			  else {
				  Codegen.generateIndexed("sw", Codegen.A0, Codegen.FP, nt, "Store result of e1");
			  }*/
			    Codegen.genPush(Codegen.A0);
			    
			    //Codegen.generate(Codegen.SUBU, Codegen.SP, Codegen.SP, 4); // Stack anpassen

			    // Generiere Code für den rechten Ausdruck
			    myExp2.codeGen(nt + 4); // Nächster temporärer Speicherplatz für e2
			    Codegen.genPop(Codegen.T1);
			    // Lade den Wert von e1 aus dem Speicher
			   /* if(myExp2 instanceof CallExpNode) {
			    	 Codegen.generateIndexed("lw", Codegen.T1, Codegen.FP, nt-nt+4, "Load resulttt of e1");
			    }
			    else {
			    	 Codegen.generateIndexed("lw", Codegen.T1, Codegen.FP, nt, "Load result of e1");
			    }*/
			   

			    // Addiere e1 (in $t1) und e2 (in $a0)
			    Codegen.generate("add", Codegen.A0, Codegen.T1, Codegen.A0);

			    // Rückgabe: Ergebnis ist in $a0
			    //Codegen.generate(Codegen.ADDU, Codegen.SP, Codegen.SP, 4); // Stack zurücksetzen
			}


		@Override
		public void analyzeType(int params) {
			 myExp1.analyzeType();
		        myExp2.analyzeType();

		        int type1 = myExp1.getExpType();
		        int type2 = myExp2.getExpType();

		        if (type1 != Types.IntType) {
		            Errors.fatal(myExp1.getLineNum(), myExp1.getColNum(),
		                "Left operand of plus operator must be an integer. But it was "+Types.ToString(type1));
		        }

		        if (type2 != Types.IntType) {
		            Errors.fatal(myExp2.getLineNum(), myExp2.getColNum(),
		                "Right operand of plus operator must be an integer. But it was "+Types.ToString(type2));
		        }
			
		}

}

class MinusNode extends BinaryExpNode
{
    public MinusNode(ExpNode exp1, ExpNode exp2) {
	super(exp1, exp2);
    }

    public int getExpType() {
		return Types.IntType;
	}
    
    public void decompile(PrintWriter p, int indent) {
        p.print("(");
        myExp1.decompile(p,indent);
        p.print(" ");
        p.print("-");
        p.print(" ");
        myExp2.decompile(p,indent);
        p.print(")");
    }

	@Override
	protected void analyzeName(SymbolTable symTable) {
		// TODO Automatisch generierter Methodenstub
		myExp1.analyzeName(symTable);
		myExp2.analyzeName(symTable);
	}

	@Override
	protected void analyzeType() {
		    myExp1.analyzeType();
	        myExp2.analyzeType();

	        int type1 = myExp1.getExpType();
	        int type2 = myExp2.getExpType();

	        if (type1 != Types.IntType || type2 != Types.IntType) {
	        	Errors.fatal(myExp1.getLineNum(),myExp1.getColNum(), "Minus operator applied to non-numeric operand.");
	        }
		
	}
	@Override
	protected int getLineNum() {
		// TODO Automatisch generierter Methodenstub
		return myExp1.getLineNum();
	}

	@Override
	protected int getCharNum() {
		// TODO Automatisch generierter Methodenstub
		return myExp1.getCharNum();
	}

	@Override
	protected int getColNum() {
		// TODO Automatisch generierter Methodenstub
		return myExp1.getColNum();
	}

	  @Override
	    public void codeGen(int nt) {
		  // Generiere Code für den linken Ausdruck
		    myExp1.codeGen(nt); // Speicherplatz `nt` verwenden
		   // Codegen.generateIndexed("sw", Codegen.A0, Codegen.FP, nt, "Store result of e1");
		    //Codegen.generate(Codegen.SUBU, Codegen.SP, Codegen.SP, 4); // Stack anpassen
		    Codegen.genPush(Codegen.A0);
		    // Generiere Code für den rechten Ausdruck
		    myExp2.codeGen(nt + 4); // Nächster temporärer Speicherplatz für e2
		    Codegen.genPop(Codegen.T1);
		    // Lade den Wert von e1 aus dem Speicher
		   // Codegen.generateIndexed("lw", Codegen.T1, Codegen.FP, nt, "Load result of e1");

		    // Addiere e1 (in $t1) und e2 (in $a0)
		    Codegen.generate("sub", Codegen.A0, Codegen.T1, Codegen.A0);

		    // Rückgabe: Ergebnis ist in $a0
		    //Codegen.generate(Codegen.ADDU, Codegen.SP, Codegen.SP, 4); // Stack zurücksetzen
	    }

	@Override
	protected int computeNT() {
		 int ntLeft = myExp1.computeNT();  // NT für linken Ausdruck (e1)
	     int ntRight = myExp2.computeNT(); // NT für rechten Ausdruck (e2)
	     return Math.max(ntLeft, 1 + ntRight);
	}

	@Override
	public void analyzeType(int params) {
		 myExp1.analyzeType();
	        myExp2.analyzeType();

	        int type1 = myExp1.getExpType();
	        int type2 = myExp2.getExpType();

	        if (type1 != Types.IntType || type2 != Types.IntType) {
	        	Errors.fatal(myExp1.getLineNum(),myExp1.getColNum(), "Minus operator applied to non-numeric operand.");
	        }
		
		
	}

	
	
}

class TimesNode extends BinaryExpNode
{
    public TimesNode(ExpNode exp1, ExpNode exp2) {
	super(exp1, exp2);
    }
    
    public int getExpType() {
		return Types.IntType;
	}

    public void decompile(PrintWriter p, int indent) {
        p.print("(");
        myExp1.decompile(p,indent);
        p.print(" ");
        p.print("*");
        p.print(" ");
        myExp2.decompile(p,indent);
        p.print(")");
    }

	@Override
	protected void analyzeName(SymbolTable symTable) {
		// TODO Automatisch generierter Methodenstub
		myExp1.analyzeName(symTable);
		myExp2.analyzeName(symTable);
	}

	@Override
	protected void analyzeType() {
	    myExp1.analyzeType();
        myExp2.analyzeType();

        int type1 = myExp1.getExpType();
        int type2 = myExp2.getExpType();

        if (type1 != Types.IntType || type2 != Types.IntType) {
        	Errors.fatal(myExp1.getLineNum(),myExp1.getColNum(), "Times operator applied to non-numeric operand.");
        }
		
	}

	@Override
	protected int getLineNum() {
		// TODO Automatisch generierter Methodenstub
		return myExp1.getLineNum();
	}

	@Override
	protected int getCharNum() {
		// TODO Automatisch generierter Methodenstub
		return myExp1.getCharNum();
	}

	@Override
	protected int getColNum() {
		// TODO Automatisch generierter Methodenstub
		return myExp1.getColNum();
	}

	@Override
	public void codeGen(int nt) {
		
		
		// Multipliziere e1 (in $t1) und e2 (in $a0)
	    if(myExp2 instanceof CallExpNode) {
	    	  myExp2.codeGen(nt); 

	  	    // Überprüfe, ob der linke Ausdruck (e1) ein globaler Identifier ist
	  	    if (myExp2 instanceof IdNode && !((IdNode) myExp2).getSymbol().isLocal()) {
	  	        String varName = ((IdNode) myExp2).getIdName();
	  	      
	  	    String idReserved = varName+"Var";
	  	    if(Codegen.isReserved(varName)) {
	  	    	varName = idReserved;
	  	    }
	  	        Codegen.generateWithComment("la", "Load address of global variable " + varName, Codegen.T0, varName);
	  	        Codegen.generateIndexed("lw", Codegen.T1, Codegen.T0, 0, "Load value of global variable " + varName);
	  	    } else {
	  	        // Lokale Variable: Lade das Ergebnis von e1 in $t1
	  	       // Codegen.generateIndexed("lw", Codegen.T1, Codegen.FP, nt, "Loadee result of e1");
	  	    	//Codegen.generateIndexed("sw", Codegen.A0, Codegen.FP, nt, "Store result of e1");
	  	    	Codegen.genPush(Codegen.A0);
	  	    }

	  	    // Generiere Code für den rechten Ausdruck (e2)
	  	    myExp1.codeGen(nt + 4); 

	  	    // Überprüfe, ob der rechte Ausdruck (e2) ein globaler Identifier ist
	  	    if (myExp1 instanceof IdNode && !((IdNode) myExp1).getSymbol().isLocal()) {
	  	        String varName = ((IdNode) myExp2).getIdName();
	  	        String idReserved = varName+"Var";
		  	    if(Codegen.isReserved(varName)) {
		  	    	varName = idReserved;
		  	    }
	  	        Codegen.generateWithComment("la", "Load address of global variable " + varName, Codegen.T0, varName);
	  	        Codegen.generateIndexed("lw", Codegen.A0, Codegen.T0, 0, "Load value of global variable " + varName);
	  	    }

	  	    // Lade das Ergebnis von e1 in $t1 (falls es nicht schon geladen wurde)
	  	    if (!(myExp1 instanceof IdNode && !((IdNode) myExp1).getSymbol().isLocal())) {
	  	    	 
	  	    		 //Codegen.generateIndexed("lw", Codegen.T1, Codegen.FP, nt, "Load result of e1");
	  	    	 Codegen.genPop(Codegen.T1);
	  	       
	  	    }
	    }
	    else {
	    	// Generiere Code für den linken Ausdruck (e1)
		    myExp1.codeGen(nt); 

		    // Überprüfe, ob der linke Ausdruck (e1) ein globaler Identifier ist
		    if (myExp1 instanceof IdNode && !((IdNode) myExp1).getSymbol().isLocal()) {
		        String varName = ((IdNode) myExp1).getIdName();
		        String idReserved = varName+"Var";
		  	    if(Codegen.isReserved(varName)) {
		  	    	varName = idReserved;
		  	    }
		        Codegen.generateWithComment("la", "Load address of global variable " + varName, Codegen.T0, varName);
		        Codegen.generateIndexed("lw", Codegen.T1, Codegen.T0, 0, "Load value of global variable " + varName);
		    } else {
		        // Lokale Variable: Lade das Ergebnis von e1 in $t1
		       // Codegen.generateIndexed("lw", Codegen.T1, Codegen.FP, nt, "Loadee result of e1");
		    	//Codegen.generateIndexed("sw", Codegen.A0, Codegen.FP, nt, "Store result of e1");
		    	Codegen.genPush(Codegen.A0);
		    }

		    // Generiere Code für den rechten Ausdruck (e2)
		    myExp2.codeGen(nt + 4); 

		    // Überprüfe, ob der rechte Ausdruck (e2) ein globaler Identifier ist
		    if (myExp2 instanceof IdNode && !((IdNode) myExp2).getSymbol().isLocal()) {
		        String varName = ((IdNode) myExp2).getIdName();
		        String idReserved = varName+"Var";
		  	    if(Codegen.isReserved(varName)) {
		  	    	varName = idReserved;
		  	    }
		        Codegen.generateWithComment("la", "Load address of global variable " + varName, Codegen.T0, varName);
		        Codegen.generateIndexed("lw", Codegen.A0, Codegen.T0, 0, "Load value of global variable " + varName);
		    }

		    // Lade das Ergebnis von e1 in $t1 (falls es nicht schon geladen wurde)
		    if (!(myExp1 instanceof IdNode && !((IdNode) myExp1).getSymbol().isLocal())) {
		    	 
		    		// Codegen.generateIndexed("lw", Codegen.T1, Codegen.FP, nt, "Load result of e1");
		    	 Codegen.genPop(Codegen.T1);
		       
		    }
	    }
	    

	    
	    Codegen.generate("mul", Codegen.A0, Codegen.T1, Codegen.A0);

	    // Das Ergebnis bleibt in $a0
	}


	@Override
	protected int computeNT() {
		 int ntLeft = myExp1.computeNT();  // NT für linken Ausdruck (e1)
	     int ntRight = myExp2.computeNT(); // NT für rechten Ausdruck (e2)
	     return Math.max(ntLeft, 1 + ntRight);
	}

	@Override
	public void analyzeType(int params) {
		 myExp1.analyzeType();
	        myExp2.analyzeType();

	        int type1 = myExp1.getExpType();
	        int type2 = myExp2.getExpType();

	        if (type1 != Types.IntType || type2 != Types.IntType) {
	        	Errors.fatal(myExp1.getLineNum(),myExp1.getColNum(), "Times operator applied to non-numeric operand.");
	        }
		
	}
}

class DivideNode extends BinaryExpNode
{
    public DivideNode(ExpNode exp1, ExpNode exp2) {
	super(exp1, exp2);
    }

    public void decompile(PrintWriter p, int indent) {
        p.print("(");
        myExp1.decompile(p,indent);
        p.print(" ");
        p.print("/");
        p.print(" ");
        myExp2.decompile(p,indent);
        p.print(")");
    }

	@Override
	protected void analyzeName(SymbolTable symTable) {
		// TODO Automatisch generierter Methodenstub
		myExp1.analyzeName(symTable);
		myExp2.analyzeName(symTable);
	}

	@Override
	protected void analyzeType() {
	    myExp1.analyzeType();
        myExp2.analyzeType();

        int type1 = myExp1.getExpType();
        int type2 = myExp2.getExpType();

        if (type1 != Types.IntType || type2 != Types.IntType) {
        	Errors.fatal(myExp1.getLineNum(),myExp1.getColNum(), "Divide operator applied to non-numeric operand.");
        }
		
	}

	@Override
	protected int getLineNum() {
		// TODO Automatisch generierter Methodenstub
		return myExp1.getLineNum();
	}

	@Override
	protected int getCharNum() {
		// TODO Automatisch generierter Methodenstub
		return myExp1.getCharNum();
	}

	@Override
	protected int getColNum() {
		// TODO Automatisch generierter Methodenstub
		return myExp1.getColNum();
	}
	public int getExpType() {
		return Types.IntType;
	}

	 @Override
	 public void codeGen(int nt) {
		 
		 // Generiere Code für den linken Ausdruck (e1)
		    myExp1.codeGen(nt); 

		    // Überprüfe, ob der linke Ausdruck (e1) ein globaler Identifier ist
		    if (myExp1 instanceof IdNode && !((IdNode) myExp1).getSymbol().isLocal()) {
		        String varName = ((IdNode) myExp1).getIdName();
		        String idReserved = varName+"Var";
		  	    if(Codegen.isReserved(varName)) {
		  	    	varName = idReserved;
		  	    }
		        Codegen.generateWithComment("la", "Load address of global variable " + varName, Codegen.T0, varName);
		        Codegen.generateIndexed("lw", Codegen.T1, Codegen.T0, 0, "Load value of global variable " + varName);
		    } else {
		        // Lokale Variable: Lade das Ergebnis von e1 in $t1
		       // Codegen.generateIndexed("lw", Codegen.T1, Codegen.FP, nt, "Loadee result of e1");
		    	Codegen.genPush(Codegen.A0);
		    	//Codegen.generateIndexed("sw", Codegen.A0, Codegen.FP, nt, "Store result of e1");
		    }

		    // Generiere Code für den rechten Ausdruck (e2)
		    myExp2.codeGen(nt + 4); 

		    // Überprüfe, ob der rechte Ausdruck (e2) ein globaler Identifier ist
		    if (myExp2 instanceof IdNode && !((IdNode) myExp2).getSymbol().isLocal()) {
		        String varName = ((IdNode) myExp2).getIdName();
		        String idReserved = varName+"Var";
		  	    if(Codegen.isReserved(varName)) {
		  	    	varName = idReserved;
		  	    }
		        Codegen.generateWithComment("la", "Load address of global variable " + varName, Codegen.T0, varName);
		        Codegen.generateIndexed("lw", Codegen.A0, Codegen.T0, 0, "Load value of global variable " + varName);
		    }

		    // Lade das Ergebnis von e1 in $t1 (falls es nicht schon geladen wurde)
		    if (!(myExp1 instanceof IdNode && !((IdNode) myExp1).getSymbol().isLocal())) {
		       Codegen.genPop(Codegen.T1);
		    	// Codegen.generateIndexed("lw", Codegen.T1, Codegen.FP, nt, "Load result of e1");
		    }

		    // Multipliziere e1 (in $t1) und e2 (in $a0)
		    Codegen.generate("div", Codegen.A0, Codegen.T1, Codegen.A0);

		    // Das Ergebnis bleibt in $a0
	    }

	@Override
	protected int computeNT() {
		 int ntLeft = myExp1.computeNT();  // NT für linken Ausdruck (e1)
	     int ntRight = myExp2.computeNT(); // NT für rechten Ausdruck (e2)
	     return Math.max(ntLeft, 1 + ntRight);
	}

	@Override
	public void analyzeType(int params) {
		  myExp1.analyzeType();
	        myExp2.analyzeType();

	        int type1 = myExp1.getExpType();
	        int type2 = myExp2.getExpType();

	        if (type1 != Types.IntType || type2 != Types.IntType) {
	        	Errors.fatal(myExp1.getLineNum(),myExp1.getColNum(), "Divide operator applied to non-numeric operand.");
	        }
	}
}

class AndNode extends BinaryExpNode
{
    public AndNode(ExpNode exp1, ExpNode exp2) {
	super(exp1, exp2);
    }

    public void decompile(PrintWriter p, int indent) {
        
        myExp1.decompile(p,indent);
        p.print(" ");
        p.print("&&");
        p.print(" ");
        myExp2.decompile(p,indent);
    }

	@Override
	protected void analyzeName(SymbolTable symTable) {
		// TODO Automatisch generierter Methodenstub
		myExp1.analyzeName(symTable);
		myExp2.analyzeName(symTable);
	}

	@Override
	protected void analyzeType() {
	    myExp1.analyzeType();
        myExp2.analyzeType();

        int type1 = myExp1.getExpType();
        int type2 = myExp2.getExpType();

        if (type1 != Types.BoolType || type2 != Types.BoolType) {
        	Errors.fatal(myExp1.getLineNum(),myExp1.getColNum(), "AND operator applied to non-boolean operand.");
        }
		
	}

	@Override
	protected int getLineNum() {
		// TODO Automatisch generierter Methodenstub
		return myExp1.getLineNum();
	}

	@Override
	protected int getCharNum() {
		// TODO Automatisch generierter Methodenstub
		return myExp1.getCharNum();
	}

	@Override
	protected int getColNum() {
		// TODO Automatisch generierter Methodenstub
		return myExp1.getColNum();
	}
	public int getExpType() {
		return Types.BoolType;
	}

	@Override
	public void codeGen(int nt) {
		// Generiere Code für den linken Ausdruck (e1)
	    myExp1.codeGen(nt); 

	    // Überprüfe, ob der linke Ausdruck (e1) ein globaler Identifier ist
	    if (myExp1 instanceof IdNode && !((IdNode) myExp1).getSymbol().isLocal()) {
	        String varName = ((IdNode) myExp1).getIdName();
	        String idReserved = varName+"Var";
	  	    if(Codegen.isReserved(varName)) {
	  	    	varName = idReserved;
	  	    }
	        Codegen.generateWithComment("la", "Load address of global variable " + varName, Codegen.T0, varName);
	        Codegen.generateIndexed("lw", Codegen.T1, Codegen.T0, 0, "Load value of global variable " + varName);
	    } else {
	        // Lokale Variable: Lade das Ergebnis von e1 in $t1
	       // Codegen.generateIndexed("lw", Codegen.T1, Codegen.FP, nt, "Loadee result of e1");
	    	Codegen.genPush(Codegen.A0);
	    	//Codegen.generateIndexed("sw", Codegen.A0, Codegen.FP, nt, "Store result of e1");
	    }

	    // Generiere Code für den rechten Ausdruck (e2)
	    myExp2.codeGen(nt + 4); 

	    // Überprüfe, ob der rechte Ausdruck (e2) ein globaler Identifier ist
	    if (myExp2 instanceof IdNode && !((IdNode) myExp2).getSymbol().isLocal()) {
	        String varName = ((IdNode) myExp2).getIdName();
	        String idReserved = varName+"Var";
	  	    if(Codegen.isReserved(varName)) {
	  	    	varName = idReserved;
	  	    }
	        Codegen.generateWithComment("la", "Load address of global variable " + varName, Codegen.T0, varName);
	        Codegen.generateIndexed("lw", Codegen.A0, Codegen.T0, 0, "Load value of global variable " + varName);
	    }

	    // Lade das Ergebnis von e1 in $t1 (falls es nicht schon geladen wurde)
	    if (!(myExp1 instanceof IdNode && !((IdNode) myExp1).getSymbol().isLocal())) {
	       Codegen.genPop(Codegen.T1);
	    	//Codegen.generateIndexed("lw", Codegen.T1, Codegen.FP, nt, "Load result of e1");
	    }

	    // Multipliziere e1 (in $t1) und e2 (in $a0)
	    Codegen.generate("and", Codegen.A0, Codegen.T1, Codegen.A0);

	    // Das Ergebnis bleibt in $a0
	}

	@Override
	protected int computeNT() {
		 int ntLeft = myExp1.computeNT();  // NT für linken Ausdruck (e1)
	     int ntRight = myExp2.computeNT(); // NT für rechten Ausdruck (e2)
	     return Math.max(ntLeft, 1 + ntRight);
	}

	@Override
	public void analyzeType(int params) {
		 myExp1.analyzeType();
	        myExp2.analyzeType();

	        int type1 = myExp1.getExpType();
	        int type2 = myExp2.getExpType();

	        if (type1 != Types.BoolType || type2 != Types.BoolType) {
	        	Errors.fatal(myExp1.getLineNum(),myExp1.getColNum(), "AND operator applied to non-boolean operand.");
	        }
		
	}

}

class OrNode extends BinaryExpNode
{
    public OrNode(ExpNode exp1, ExpNode exp2) {
	super(exp1, exp2);
    }

    public void decompile(PrintWriter p, int indent) {
        myExp1.decompile(p,indent);
        p.print(" ");
        p.print("||");
        p.print(" ");
        myExp2.decompile(p,indent);
    }

	@Override
	protected void analyzeName(SymbolTable symTable) {
		// TODO Automatisch generierter Methodenstub
		myExp1.analyzeName(symTable);
		myExp2.analyzeName(symTable);
	}

	@Override
	protected void analyzeType() {
		 myExp1.analyzeType();
	        myExp2.analyzeType();

	        int type1 = myExp1.getExpType();
	        int type2 = myExp2.getExpType();

	        if (type1 != Types.BoolType || type2 != Types.BoolType) {
	        	Errors.fatal(myExp1.getLineNum(),myExp1.getColNum(), "OR operator applied to non-boolean operand.");
	        }
	
	}
	@Override
	protected int getLineNum() {
		// TODO Automatisch generierter Methodenstub
		return myExp1.getLineNum();
	}

	@Override
	protected int getCharNum() {
		// TODO Automatisch generierter Methodenstub
		return myExp1.getCharNum();
	}

	@Override
	protected int getColNum() {
		// TODO Automatisch generierter Methodenstub
		return myExp1.getColNum();
	}
	
	public int getExpType() {
		return Types.BoolType;
	}

	@Override
	public void codeGen(int nt) {
		// Generiere Code für den linken Ausdruck (e1)
	    myExp1.codeGen(nt); 

	    // Überprüfe, ob der linke Ausdruck (e1) ein globaler Identifier ist
	    if (myExp1 instanceof IdNode && !((IdNode) myExp1).getSymbol().isLocal()) {
	    	
	        String varName = ((IdNode) myExp1).getIdName();
	        String idReserved = varName+"Var";
	  	    if(Codegen.isReserved(varName)) {
	  	    	varName = idReserved;
	  	    }
	        Codegen.generateWithComment("la", "Load address of global variable " + varName, Codegen.T0, varName);
	        Codegen.generateIndexed("lw", Codegen.T1, Codegen.T0, 0, "Load value of global variable " + varName);
	    } else {
	        // Lokale Variable: Lade das Ergebnis von e1 in $t1
	       // Codegen.generateIndexed("lw", Codegen.T1, Codegen.FP, nt, "Loadee result of e1");
	    	Codegen.genPush(Codegen.A0);
	    	//Codegen.generateIndexed("sw", Codegen.A0, Codegen.FP, nt, "Store result of e1");
	    }

	    // Generiere Code für den rechten Ausdruck (e2)
	    myExp2.codeGen(nt + 4); 

	    // Überprüfe, ob der rechte Ausdruck (e2) eine globale Variable ist
	    if (myExp2 instanceof IdNode && !((IdNode) myExp2).getSymbol().isLocal()) {
	    	
	        String varName = ((IdNode) myExp2).getIdName();
	        String idReserved = varName+"Var";
	  	    if(Codegen.isReserved(varName)) {
	  	    	varName = idReserved;
	  	    }
	        Codegen.generateWithComment("la", "Load address of global variable " + varName, Codegen.T0, varName);
	        Codegen.generateIndexed("lw", Codegen.A0, Codegen.T0, 0, "Load value of global variable " + varName);
	    }

	    // Lade das Ergebnis von e1 in $t1 (falls es nicht schon geladen wurde)
	    if (!(myExp1 instanceof IdNode && !((IdNode) myExp1).getSymbol().isLocal())) {
	       Codegen.genPop(Codegen.T1);
	    	//Codegen.generateIndexed("lw", Codegen.T1, Codegen.FP, nt, "Load result of e1");
	    }

	    // Multipliziere e1 (in $t1) und e2 (in $a0)
	    Codegen.generate("or", Codegen.A0, Codegen.T1, Codegen.A0);

	    // Das Ergebnis bleibt in $a0
	}

	@Override
	protected int computeNT() {
		 int ntLeft = myExp1.computeNT();  // NT für linken Ausdruck (e1)
	     int ntRight = myExp2.computeNT(); // NT für rechten Ausdruck (e2)
	     return Math.max(ntLeft, 1 + ntRight);
	}

	@Override
	public void analyzeType(int params) {
		 myExp1.analyzeType();
	        myExp2.analyzeType();

	        int type1 = myExp1.getExpType();
	        int type2 = myExp2.getExpType();

	        if (type1 != Types.BoolType || type2 != Types.BoolType) {
	        	Errors.fatal(myExp1.getLineNum(),myExp1.getColNum(), "OR operator applied to non-boolean operand.");
	        }
	
	}
}

class EqualsNode extends BinaryExpNode
{
    public EqualsNode(ExpNode exp1, ExpNode exp2) {
	super(exp1, exp2);
    }

    public void decompile(PrintWriter p, int indent) {
        myExp1.decompile(p,indent);
        p.print(" ");
        p.print("==");
        p.print(" ");
        myExp2.decompile(p,indent);
    }

	@Override
	protected void analyzeName(SymbolTable symTable) {
		// TODO Automatisch generierter Methodenstub
		myExp1.analyzeName(symTable);
		myExp2.analyzeName(symTable);
	}

	@Override
	protected void analyzeType() {
		 myExp1.analyzeType();
	        myExp2.analyzeType();

	        int type1 = myExp1.getExpType();
	        int type2 = myExp2.getExpType();
	        

	        if (type1 == Types.BoolType && type2 != Types.BoolType) {
	        	Errors.fatal(myExp1.getLineNum(),myExp1.getColNum(), "Equals operator applied to not the same operands.");
	        }
	        else if (type1 == Types.IntType && type2 != Types.IntType) {
	        	Errors.fatal(myExp1.getLineNum(),myExp1.getColNum(), "Equals operator applied to not the same operands.");
	        }
	        else {
				if (type1 == Types.StringType && type2 != Types.StringType) {
					Errors.fatal(myExp1.getLineNum(),myExp1.getColNum(), "Equals operator applied to not the same operands.");
				}

	        }


	}

	@Override
	protected int getLineNum() {
		// TODO Automatisch generierter Methodenstub
		return myExp1.getLineNum();
	}

	@Override
	protected int getCharNum() {
		// TODO Automatisch generierter Methodenstub
		return myExp1.getCharNum();
	}

	@Override
	protected int getColNum() {
		// TODO Automatisch generierter Methodenstub
		return myExp1.getColNum();
	}
	
	public int getExpType() {
		return Types.BoolType;
	}

	@Override
    public void codeGen(int nt) {
       
		// Generiere Code für den linken Ausdruck (e1)
	    myExp1.codeGen(nt); 

	    // Überprüfe, ob der linke Ausdruck (e1) ein globaler Identifier ist
	    if (myExp1 instanceof IdNode && !((IdNode) myExp1).getSymbol().isLocal()) {
	        String varName = ((IdNode) myExp1).getIdName();
	        String idReserved = varName+"Var";
	  	    if(Codegen.isReserved(varName)) {
	  	    	varName = idReserved;
	  	    }
	        Codegen.generateWithComment("la", "Load address of global variable " + varName, Codegen.T0, varName);
	        Codegen.generateIndexed("lw", Codegen.T1, Codegen.T0, 0, "Load value of global variable " + varName);
	    } else {
	        // Lokale Variable: Lade das Ergebnis von e1 in $t1
	       // Codegen.generateIndexed("lw", Codegen.T1, Codegen.FP, nt, "Loadee result of e1");
	    	Codegen.genPush(Codegen.A0);
	    	//Codegen.generateIndexed("sw", Codegen.A0, Codegen.FP, nt, "Store result of e1");
	    }

	    // Generiere Code für den rechten Ausdruck (e2)
	    myExp2.codeGen(nt + 4); 

	    // Überprüfe, ob der rechte Ausdruck (e2) ein globaler Identifier ist
	    if (myExp2 instanceof IdNode && !((IdNode) myExp2).getSymbol().isLocal()) {
	        String varName = ((IdNode) myExp2).getIdName();
	        String idReserved = varName+"Var";
	  	    if(Codegen.isReserved(varName)) {
	  	    	varName = idReserved;
	  	    }
	        Codegen.generateWithComment("la", "Load address of global variable " + varName, Codegen.T0, varName);
	        Codegen.generateIndexed("lw", Codegen.A0, Codegen.T0, 0, "Load value of global variable " + varName);
	    }

	    // Lade das Ergebnis von e1 in $t1 (falls es nicht schon geladen wurde)
	    if (!(myExp1 instanceof IdNode && !((IdNode) myExp1).getSymbol().isLocal())) {
	       Codegen.genPop(Codegen.T1);
	    	//Codegen.generateIndexed("lw", Codegen.T1, Codegen.FP, nt, "Load result of e1");
	    }

	    // Multipliziere e1 (in $t1) und e2 (in $a0)
	    //Codegen.generate("div", Codegen.A0, Codegen.T1, Codegen.A0);

	    // Das Ergebnis bleibt in $a0

	  

	    // Rückgabe: Ergebnis ist in $a0
	    //Codegen.generate(Codegen.ADDU, Codegen.SP, Codegen.SP, 4); // Stack zurücksetzen
        if (myExp1.getExpType() == Types.StringType && myExp2.getExpType() == Types.StringType) {
            // String-Vergleich basierend auf Speicheradressen
            Codegen.generate("seq", Codegen.A0, Codegen.T1, Codegen.A0);
        } else {
            // Vergleich für andere Typen (z. B. int, boolean)
            Codegen.generate("seq", Codegen.A0, Codegen.T1, Codegen.A0);
        }
        
        
    }

	@Override
	protected int computeNT() {
		 int ntLeft = myExp1.computeNT();  // NT für linken Ausdruck (e1)
	     int ntRight = myExp2.computeNT(); // NT für rechten Ausdruck (e2)
	     return Math.max(ntLeft, 1 + ntRight);
	}

	@Override
	public void analyzeType(int params) {
		// TODO Automatisch generierter Methodenstub
		
	}
}

class NotEqualsNode extends BinaryExpNode
{
    public NotEqualsNode(ExpNode exp1, ExpNode exp2) {
	super(exp1, exp2);
    }

    public void decompile(PrintWriter p, int indent) {
        myExp1.decompile(p,indent);
        p.print(" ");
        p.print("!=");
        p.print(" ");
        myExp2.decompile(p,indent);
    }

	@Override
	protected void analyzeName(SymbolTable symTable) {
		// TODO Automatisch generierter Methodenstub
		myExp1.analyzeName(symTable);
		myExp2.analyzeName(symTable);
	}

	@Override
	protected void analyzeType() {
		 myExp1.analyzeType();
	        myExp2.analyzeType();

	        int type1 = myExp1.getExpType();
	        int type2 = myExp2.getExpType();

	        if (type1 == Types.BoolType && type2 != Types.BoolType) {
	        	Errors.fatal(myExp1.getLineNum(),myExp1.getColNum(), "Equals operator applied to not the same operands.");
	        }
	        else if (type1 == Types.IntType && type2 != Types.IntType) {
	        	Errors.fatal(myExp1.getLineNum(),myExp1.getColNum(), "Equals operator applied to not the same operands.");
	        }
	        else {
	        	  if (type1 == Types.StringType || type2 == Types.StringType) {
	  	        	Errors.fatal(myExp1.getLineNum(),myExp1.getColNum(), "Equals operator must not apply to String operand.");
	  	        }

	        }
		
	}

	@Override
	protected int getLineNum() {
		// TODO Automatisch generierter Methodenstub
		return myExp1.getLineNum();
	}

	@Override
	protected int getCharNum() {
		// TODO Automatisch generierter Methodenstub
		return myExp1.getCharNum();
	}

	@Override
	protected int getColNum() {
		// TODO Automatisch generierter Methodenstub
		return myExp1.getColNum();
	}
	
	public int getExpType() {
		return Types.BoolType;
	}

	 @Override
	    public void codeGen(int nt) {
			// Generiere Code für den linken Ausdruck (e1)
		    myExp1.codeGen(nt); 

		    // Überprüfe, ob der linke Ausdruck (e1) ein globaler Identifier ist
		    if (myExp1 instanceof IdNode && !((IdNode) myExp1).getSymbol().isLocal()) {
		        String varName = ((IdNode) myExp1).getIdName();
		        String idReserved = varName+"Var";
		  	    if(Codegen.isReserved(varName)) {
		  	    	varName = idReserved;
		  	    }
		        Codegen.generateWithComment("la", "Load address of global variable " + varName, Codegen.T0, varName);
		        Codegen.generateIndexed("lw", Codegen.T1, Codegen.T0, 0, "Load value of global variable " + varName);
		    } else {
		        // Lokale Variable: Lade das Ergebnis von e1 in $t1
		       // Codegen.generateIndexed("lw", Codegen.T1, Codegen.FP, nt, "Loadee result of e1");
		    	Codegen.genPush(Codegen.A0);
		    	//Codegen.generateIndexed("sw", Codegen.A0, Codegen.FP, nt, "Store result of e1");
		    }

		    // Generiere Code für den rechten Ausdruck (e2)
		    myExp2.codeGen(nt + 4); 

		    // Überprüfe, ob der rechte Ausdruck (e2) ein globaler Identifier ist
		    if (myExp2 instanceof IdNode && !((IdNode) myExp2).getSymbol().isLocal()) {
		        String varName = ((IdNode) myExp2).getIdName();
		        String idReserved = varName+"Var";
		  	    if(Codegen.isReserved(varName)) {
		  	    	varName = idReserved;
		  	    }
		        Codegen.generateWithComment("la", "Load address of global variable " + varName, Codegen.T0, varName);
		        Codegen.generateIndexed("lw", Codegen.A0, Codegen.T0, 0, "Load value of global variable " + varName);
		    }

		    // Lade das Ergebnis von e1 in $t1 (falls es nicht schon geladen wurde)
		    if (!(myExp1 instanceof IdNode && !((IdNode) myExp1).getSymbol().isLocal())) {
		        Codegen.genPop(Codegen.T1);
		    	//Codegen.generateIndexed("lw", Codegen.T1, Codegen.FP, nt, "Load result of e1");
		    }

		    // Multipliziere e1 (in $t1) und e2 (in $a0)
		    //Codegen.generate("or", Codegen.A0, Codegen.T1, Codegen.A0);
		   

		    // Rückgabe: Ergebnis ist in $a0
		    //Codegen.generate(Codegen.ADDU, Codegen.SP, Codegen.SP, 4); // Stack zurücksetzen

	        if (myExp1.getExpType() == Types.StringType && myExp2.getExpType() == Types.StringType) {
	            // String-Vergleich auf Speicheradressen
	            Codegen.generate("sne", Codegen.A0, Codegen.T1, Codegen.A0);
	        } else {
	            // Vergleich für andere Typen (int, boolean)
	            Codegen.generate("sne", Codegen.A0, Codegen.T1, Codegen.A0);
	        }

	        
	    }

	@Override
	protected int computeNT() {
		 int ntLeft = myExp1.computeNT();  // NT für linken Ausdruck (e1)
	     int ntRight = myExp2.computeNT(); // NT für rechten Ausdruck (e2)
	     return Math.max(ntLeft, 1 + ntRight);
	}

	@Override
	public void analyzeType(int params) {
		// TODO Automatisch generierter Methodenstub
		
	}
}

class LessNode extends BinaryExpNode
{
    public LessNode(ExpNode exp1, ExpNode exp2) {
	super(exp1, exp2);
	
    }
    
    

    public void decompile(PrintWriter p, int indent) {
        myExp1.decompile(p,indent);
        p.print(" ");
        p.print("<");
        p.print(" ");
        myExp2.decompile(p,indent);
    }

	@Override
	protected void analyzeName(SymbolTable symTable) {
		// TODO Automatisch generierter Methodenstub
		myExp1.analyzeName(symTable);
		myExp2.analyzeName(symTable);
	}

	@Override
	protected void analyzeType() {
		 myExp1.analyzeType();
	        myExp2.analyzeType();

	        int type1 = myExp1.getExpType();
	        int type2 = myExp2.getExpType();

	        if (type1 != Types.IntType || type2 != Types.IntType) {
	        	Errors.fatal(myExp1.getLineNum(),myExp1.getColNum(), "LESS operator applied to non-integer operand.");
	        }	
	}
	@Override
	protected int getLineNum() {
		// TODO Automatisch generierter Methodenstub
		return myExp1.getLineNum();
	}

	@Override
	protected int getCharNum() {
		// TODO Automatisch generierter Methodenstub
		return myExp1.getCharNum();
	}

	@Override
	protected int getColNum() {
		// TODO Automatisch generierter Methodenstub
		return myExp1.getColNum();
	}
	
	public int getExpType() {
		return Types.BoolType;
	}

	  @Override
	    public void codeGen(int nt) {
		// Generiere Code für den linken Ausdruck (e1)
		    myExp1.codeGen(nt); 

		    // Überprüfe, ob der linke Ausdruck (e1) ein globaler Identifier ist
		    if (myExp1 instanceof IdNode && !((IdNode) myExp1).getSymbol().isLocal()) {
		    	
		        String varName = ((IdNode) myExp1).getIdName();
		        String idReserved = varName+"Var";
		  	    if(Codegen.isReserved(varName)) {
		  	    	varName = idReserved;
		  	    }
		        Codegen.generateWithComment("la", "Load address of global variable " + varName, Codegen.T0, varName);
		        Codegen.generateIndexed("lw", Codegen.T1, Codegen.T0, 0, "Load value of global variable " + varName);
		    } else {
		        // Lokale Variable: Lade das Ergebnis von e1 in $t1
		       // Codegen.generateIndexed("lw", Codegen.T1, Codegen.FP, nt, "Loadee result of e1");
		    	Codegen.genPush(Codegen.A0);
		    	//Codegen.generateIndexed("sw", Codegen.A0, Codegen.FP, nt, "Store result of e1");
		    }

		    // Generiere Code für den rechten Ausdruck (e2)
		    myExp2.codeGen(nt + 4); 

		    // Überprüfe, ob der rechte Ausdruck (e2) eine globale Variable ist
		    if (myExp2 instanceof IdNode && !((IdNode) myExp2).getSymbol().isLocal()) {
		    	
		        String varName = ((IdNode) myExp2).getIdName();
		        String idReserved = varName+"Var";
		  	    if(Codegen.isReserved(varName)) {
		  	    	varName = idReserved;
		  	    }
		        Codegen.generateWithComment("la", "Load address of global variable " + varName, Codegen.T0, varName);
		        Codegen.generateIndexed("lw", Codegen.A0, Codegen.T0, 0, "Load value of global variable " + varName);
		    }

		    // Lade das Ergebnis von e1 in $t1 (falls es nicht schon geladen wurde)
		    if (!(myExp1 instanceof IdNode && !((IdNode) myExp1).getSymbol().isLocal())) {
		        Codegen.genPop(Codegen.T1);
		    	//Codegen.generateIndexed("lw", Codegen.T1, Codegen.FP, nt, "Load result of e1");
		    }

		    // Multipliziere e1 (in $t1) und e2 (in $a0)
		    Codegen.generate("slt", Codegen.A0, Codegen.T1, Codegen.A0);
	    }

	@Override
	protected int computeNT() {
		 int ntLeft = myExp1.computeNT();  // NT für linken Ausdruck (e1)
	     int ntRight = myExp2.computeNT(); // NT für rechten Ausdruck (e2)
	     return Math.max(ntLeft, 1 + ntRight);
	}



	@Override
	public void analyzeType(int params) {
		// TODO Automatisch generierter Methodenstub
		
	}
}

class GreaterNode extends BinaryExpNode
{
    public GreaterNode(ExpNode exp1, ExpNode exp2) {
	super(exp1, exp2);
    }

    public void decompile(PrintWriter p, int indent) {
        myExp1.decompile(p,indent);
        p.print(" ");
        p.print(">");
        p.print(" ");
        myExp2.decompile(p,indent);
    }

	@Override
	protected void analyzeName(SymbolTable symTable) {
		// TODO Automatisch generierter Methodenstub
		myExp1.analyzeName(symTable);
		myExp2.analyzeName(symTable);
	}

	@Override
	protected void analyzeType() {
		 myExp1.analyzeType();
	        myExp2.analyzeType();

	        int type1 = myExp1.getExpType();
	        int type2 = myExp2.getExpType();

	        if (type1 != Types.IntType || type2 != Types.IntType) {
	        	Errors.fatal(myExp1.getLineNum(),myExp1.getColNum(), "GREATER operator applied to non-integer operand.");
	        }
		
	}
	@Override
	protected int getLineNum() {
		// TODO Automatisch generierter Methodenstub
		return myExp1.getLineNum();
	}

	@Override
	protected int getCharNum() {
		// TODO Automatisch generierter Methodenstub
		return myExp1.getCharNum();
	}

	@Override
	protected int getColNum() {
		// TODO Automatisch generierter Methodenstub
		return myExp1.getColNum();
	}
	
	public int getExpType() {
		return Types.BoolType;
	}

	@Override
    public void codeGen(int nt) {
		// Generiere Code für den linken Ausdruck (e1)
	    myExp1.codeGen(nt); 

	    // Überprüfe, ob der linke Ausdruck (e1) ein globaler Identifier ist
	    if (myExp1 instanceof IdNode && !((IdNode) myExp1).getSymbol().isLocal()) {
	    	
	        String varName = ((IdNode) myExp1).getIdName();
	        String idReserved = varName+"Var";
	  	    if(Codegen.isReserved(varName)) {
	  	    	varName = idReserved;
	  	    }
	        Codegen.generateWithComment("la", "Load address of global variable " + varName, Codegen.T0, varName);
	        Codegen.generateIndexed("lw", Codegen.T1, Codegen.T0, 0, "Load value of global variable " + varName);
	    } else {
	        // Lokale Variable: Lade das Ergebnis von e1 in $t1
	       // Codegen.generateIndexed("lw", Codegen.T1, Codegen.FP, nt, "Loadee result of e1");
	    	Codegen.genPush(Codegen.A0);
	    	//Codegen.generateIndexed("sw", Codegen.A0, Codegen.FP, nt, "Store result of e1");
	    }

	    // Generiere Code für den rechten Ausdruck (e2)
	    myExp2.codeGen(nt + 4); 

	    // Überprüfe, ob der rechte Ausdruck (e2) eine globale Variable ist
	    if (myExp2 instanceof IdNode && !((IdNode) myExp2).getSymbol().isLocal()) {
	    	
	        String varName = ((IdNode) myExp2).getIdName();
	        String idReserved = varName+"Var";
	  	    if(Codegen.isReserved(varName)) {
	  	    	varName = idReserved;
	  	    }
	        Codegen.generateWithComment("la", "Load address of global variable " + varName, Codegen.T0, varName);
	        Codegen.generateIndexed("lw", Codegen.A0, Codegen.T0, 0, "Load value of global variable " + varName);
	    }

	    // Lade das Ergebnis von e1 in $t1 (falls es nicht schon geladen wurde)
	    if (!(myExp1 instanceof IdNode && !((IdNode) myExp1).getSymbol().isLocal())) {
	       Codegen.genPop(Codegen.T1);
	    	//Codegen.generateIndexed("lw", Codegen.T1, Codegen.FP, nt, "Load result of e1");
	    }

	    // Multipliziere e1 (in $t1) und e2 (in $a0)
	    Codegen.generate("slt", Codegen.A0, Codegen.A0, Codegen.T1);
    }

	@Override
	protected int computeNT() {
		 int ntLeft = myExp1.computeNT();  // NT für linken Ausdruck (e1)
	     int ntRight = myExp2.computeNT(); // NT für rechten Ausdruck (e2)
	     return Math.max(ntLeft, 1 + ntRight);
	}

	@Override
	public void analyzeType(int params) {
		// TODO Automatisch generierter Methodenstub
		
	}
}

class LessEqNode extends BinaryExpNode
{
    public LessEqNode(ExpNode exp1, ExpNode exp2) {
	super(exp1, exp2);
    }

    public void decompile(PrintWriter p, int indent) {
        myExp1.decompile(p,indent);
        p.print(" ");
        p.print("<=");
        p.print(" ");
        myExp2.decompile(p,indent);
    }

	@Override
	protected void analyzeName(SymbolTable symTable) {
		// TODO Automatisch generierter Methodenstub
		myExp1.analyzeName(symTable);
		myExp2.analyzeName(symTable);
	}

	@Override
	protected void analyzeType() {
		 myExp1.analyzeType();
	        myExp2.analyzeType();

	        int type1 = myExp1.getExpType();
	        int type2 = myExp2.getExpType();

	        if (type1 != Types.IntType || type2 != Types.IntType) {
	        	Errors.fatal(myExp1.getLineNum(),myExp1.getColNum(), "LessEq operator applied to non-integer operand.");
	        }
		
	}
	@Override
	protected int getLineNum() {
		// TODO Automatisch generierter Methodenstub
		return myExp1.getLineNum();
	}

	@Override
	protected int getCharNum() {
		// TODO Automatisch generierter Methodenstub
		return myExp1.getCharNum();
	}

	@Override
	protected int getColNum() {
		// TODO Automatisch generierter Methodenstub
		return myExp1.getColNum();
	}
	public int getExpType() {
		return Types.BoolType;
	}

	 @Override
	    public void codeGen(int nt) {
		// Generiere Code für den linken Ausdruck (e1)
		    myExp1.codeGen(nt); 

		    // Überprüfe, ob der linke Ausdruck (e1) ein globaler Identifier ist
		    if (myExp1 instanceof IdNode && !((IdNode) myExp1).getSymbol().isLocal()) {
		    	
		        String varName = ((IdNode) myExp1).getIdName();
		        String idReserved = varName+"Var";
		  	    if(Codegen.isReserved(varName)) {
		  	    	varName = idReserved;
		  	    }
		        Codegen.generateWithComment("la", "Load address of global variable " + varName, Codegen.T0, varName);
		        Codegen.generateIndexed("lw", Codegen.T1, Codegen.T0, 0, "Load value of global variable " + varName);
		    } else {
		        // Lokale Variable: Lade das Ergebnis von e1 in $t1
		       // Codegen.generateIndexed("lw", Codegen.T1, Codegen.FP, nt, "Loadee result of e1");
		    	Codegen.genPush(Codegen.A0);
		    	//Codegen.generateIndexed("sw", Codegen.A0, Codegen.FP, nt, "Store result of e1");
		    }

		    // Generiere Code für den rechten Ausdruck (e2)
		    myExp2.codeGen(nt + 4); 

		    // Überprüfe, ob der rechte Ausdruck (e2) eine globale Variable ist
		    if (myExp2 instanceof IdNode && !((IdNode) myExp2).getSymbol().isLocal()) {
		    	
		        String varName = ((IdNode) myExp2).getIdName();
		        String idReserved = varName+"Var";
		  	    if(Codegen.isReserved(varName)) {
		  	    	varName = idReserved;
		  	    }
		        Codegen.generateWithComment("la", "Load address of global variable " + varName, Codegen.T0, varName);
		        Codegen.generateIndexed("lw", Codegen.A0, Codegen.T0, 0, "Load value of global variable " + varName);
		    }

		    // Lade das Ergebnis von e1 in $t1 (falls es nicht schon geladen wurde)
		    if (!(myExp1 instanceof IdNode && !((IdNode) myExp1).getSymbol().isLocal())) {
		      Codegen.genPop(Codegen.T1);
		    	//Codegen.generateIndexed("lw", Codegen.T1, Codegen.FP, nt, "Load result of e1");
		    }

		    // Multipliziere e1 (in $t1) und e2 (in $a0)
		    Codegen.generate("sle", Codegen.A0, Codegen.T1, Codegen.A0);
	    }

	@Override
	protected int computeNT() {
		 int ntLeft = myExp1.computeNT();  // NT für linken Ausdruck (e1)
	     int ntRight = myExp2.computeNT(); // NT für rechten Ausdruck (e2)
	     return Math.max(ntLeft, 1 + ntRight);
	}

	@Override
	public void analyzeType(int params) {
		// TODO Automatisch generierter Methodenstub
		
	}
}

class GreaterEqNode extends BinaryExpNode
{
    public GreaterEqNode(ExpNode exp1, ExpNode exp2) {
	super(exp1, exp2);
    }

    public void decompile(PrintWriter p, int indent) {
        myExp1.decompile(p,indent);
        p.print(" ");
        p.print(">=");
        p.print(" ");
        myExp2.decompile(p,indent);
    }

	@Override
	protected void analyzeName(SymbolTable symTable) {
		// TODO Automatisch generierter Methodenstub
		myExp1.analyzeName(symTable);
		myExp2.analyzeName(symTable);
	}

	@Override
	protected void analyzeType() {
		 myExp1.analyzeType();
	        myExp2.analyzeType();

	        int type1 = myExp1.getExpType();
	        int type2 = myExp2.getExpType();

	        if (type1 != Types.IntType || type2 != Types.IntType) {
	        	Errors.fatal(myExp1.getLineNum(),myExp1.getColNum(), "GreaterEq operator applied to non-integer operand.");
	        }
	}
	@Override
	protected int getLineNum() {
		// TODO Automatisch generierter Methodenstub
		return myExp1.getLineNum();
	}

	@Override
	protected int getCharNum() {
		// TODO Automatisch generierter Methodenstub
		return myExp1.getCharNum();
	}

	@Override
	protected int getColNum() {
		// TODO Automatisch generierter Methodenstub
		return myExp1.getColNum();
	}
	
	public int getExpType() {
		return Types.BoolType;
	}

	 @Override
	    public void codeGen(int nt) {
			// Generiere Code für den linken Ausdruck (e1)
		    myExp1.codeGen(nt); 

		    // Überprüfe, ob der linke Ausdruck (e1) ein globaler Identifier ist
		    if (myExp1 instanceof IdNode && !((IdNode) myExp1).getSymbol().isLocal()) {
		    	
		        String varName = ((IdNode) myExp1).getIdName();
		        String idReserved = varName+"Var";
		  	    if(Codegen.isReserved(varName)) {
		  	    	varName = idReserved;
		  	    }
		        Codegen.generateWithComment("la", "Load address of global variable " + varName, Codegen.T0, varName);
		        Codegen.generateIndexed("lw", Codegen.T1, Codegen.T0, 0, "Load value of global variable " + varName);
		    } else {
		        // Lokale Variable: Lade das Ergebnis von e1 in $t1
		       // Codegen.generateIndexed("lw", Codegen.T1, Codegen.FP, nt, "Loadee result of e1");
		    	Codegen.genPush(Codegen.A0);
		    	//Codegen.generateIndexed("sw", Codegen.A0, Codegen.FP, nt, "Store result of e1");
		    }

		    // Generiere Code für den rechten Ausdruck (e2)
		    myExp2.codeGen(nt + 4); 

		    // Überprüfe, ob der rechte Ausdruck (e2) eine globale Variable ist
		    if (myExp2 instanceof IdNode && !((IdNode) myExp2).getSymbol().isLocal()) {
		    	
		        String varName = ((IdNode) myExp2).getIdName();
		        String idReserved = varName+"Var";
		  	    if(Codegen.isReserved(varName)) {
		  	    	varName = idReserved;
		  	    }
		        Codegen.generateWithComment("la", "Load address of global variable " + varName, Codegen.T0, varName);
		        Codegen.generateIndexed("lw", Codegen.A0, Codegen.T0, 0, "Load value of global variable " + varName);
		    }

		    // Lade das Ergebnis von e1 in $t1 (falls es nicht schon geladen wurde)
		    if (!(myExp1 instanceof IdNode && !((IdNode) myExp1).getSymbol().isLocal())) {
		       Codegen.genPop(Codegen.T1);
		    	//Codegen.generateIndexed("lw", Codegen.T1, Codegen.FP, nt, "Load result of e1");
		    }

		    // Multipliziere e1 (in $t1) und e2 (in $a0)
		    Codegen.generate("sle", Codegen.A0, Codegen.A0, Codegen.T1);

		    // Rückgabe: Ergebnis ist in $a0
		    //Codegen.generate(Codegen.ADDU, Codegen.SP, Codegen.SP, 4); // Stack zurücksetzen
	    }

	@Override
	protected int computeNT() {
		 int ntLeft = myExp1.computeNT();  // NT für linken Ausdruck (e1)
	     int ntRight = myExp2.computeNT(); // NT für rechten Ausdruck (e2)
	     return Math.max(ntLeft, 1 + ntRight);
	}

	@Override
	public void analyzeType(int params) {
		// TODO Automatisch generierter Methodenstub
		
	}
	
	
}


