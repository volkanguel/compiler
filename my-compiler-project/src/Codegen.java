import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class Codegen {
    // file into which generated code is written
    public static PrintWriter p;
    static {
        try {
            // Initialisiere die Ausgabedatei
            p = new PrintWriter(new FileWriter("test.out"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1); // Beende das Programm bei Fehlern
        }
    }

    // Schließe den PrintWriter (wird am Ende der Codegenerierung aufgerufen)
    public static void close() {
        if (p != null) {
            p.close(); // Wichtig, um die Datei sauber zu schließen
        }
    }

    public static String currentMethod = null; // Name der aktuellen Methode
    public static String getCurrentMethod() {
    	return currentMethod;
    }
    public static boolean isRecursive = false;
    public static void setIsRecursive(boolean bool) {
    	isRecursive = bool;
    }
    
    public static boolean getIsRecursive() {
    	return isRecursive;
    }
    // values of true and false
    public static final String TRUE = "-1"; 
    public static final String FALSE = "0";
    
    private static final Set<String> RESERVED_NAMES = new HashSet<>();
    

    // registers
    public static final String FP = "$fp";
    public static final String SP = "$sp";
    public static final String ZERO = "$zero";
    public static final String T0 = "$t0"; 
    public static final String T1 = "$t1";
    public static final String T2 = "$t2";
    public static final String T3 = "$t3";
    public static final String T4 = "$t4";
    public static final String T5 = "$t5";
    public static final String T6 = "$t6";
    public static final String T7 = "$t7";
    public static final String T8 = "$t8";
    public static final String T9 = "$t9";
    public static final String S0 = "$s0"; 
    public static final String S1 = "$s1";
    public static final String S2 = "$s2";
    public static final String S3 = "$s3";
    public static final String S4 = "$s4";
    public static final String S5 = "$s5";
    public static final String S6 = "$s6";
    public static final String S7 = "$s7";
    public static final String A0 = "$a0";
    public static final String A1 = "$a1";
    public static final String A2 = "$a2";
    public static final String A3 = "$a3";
    public static final String V0 = "$v0";
    public static final String V1 = "$v1";
    public static final String RA = "$ra";
    public static final String JR = "jr";
    public static final String LW = "lw";
    public static final String LI = "li";
    public static final String LA = "la";
    public static final String SW = "sw";
    public static final String ADD = "add";
    public static final String ADDI = "addi";
    public static final String ADDU = "addu";
    public static final String AND = "and";
    public static final String OR = "or";
    public static final String SUB = "sub";
    public static final String SUBU = "subu";
    public static final String MUL = "mul";
    public static final String DIV = "divide";
    public static final String BEQ = "beq";
    public static final String BNE = "bne";
    public static final String BLEZ = "blez";
    public static final String SEQ = "seq";
    public static final String SNE = "sne";
    public static final String SLT = "slt";
    public static final String SLE = "sle";
    public static final String NEG = "neg";
    public static final String JAL = "jal";
    public static final String J = "j";
    public static final String MOVE = "move";
    
    static {
    	// Register
        RESERVED_NAMES.add("$fp");
        RESERVED_NAMES.add("$sp");
        RESERVED_NAMES.add("$zero");
        RESERVED_NAMES.add("$t0");
        RESERVED_NAMES.add("$t1");
        RESERVED_NAMES.add("$t2");
        RESERVED_NAMES.add("$t3");
        RESERVED_NAMES.add("$t4");
        RESERVED_NAMES.add("$t5");
        RESERVED_NAMES.add("$t6");
        RESERVED_NAMES.add("$t7");
        RESERVED_NAMES.add("$s0");
        RESERVED_NAMES.add("$s1");
        RESERVED_NAMES.add("$s2");
        RESERVED_NAMES.add("$s3");
        RESERVED_NAMES.add("$s4");
        RESERVED_NAMES.add("$s5");
        RESERVED_NAMES.add("$s6");
        RESERVED_NAMES.add("$s7");
        RESERVED_NAMES.add("$a0");
        RESERVED_NAMES.add("$a1");
        RESERVED_NAMES.add("$a2");
        RESERVED_NAMES.add("$a3");
        RESERVED_NAMES.add("$v0");
        RESERVED_NAMES.add("$v1");
        RESERVED_NAMES.add("$ra");

        // Befehle
        RESERVED_NAMES.add("jr");
        RESERVED_NAMES.add("lw");
        RESERVED_NAMES.add("li");
        RESERVED_NAMES.add("la");
        RESERVED_NAMES.add("sw");
        RESERVED_NAMES.add("add");
        RESERVED_NAMES.add("addi");
        RESERVED_NAMES.add("addu");
        RESERVED_NAMES.add("and");
        RESERVED_NAMES.add("or");
        RESERVED_NAMES.add("sub");
        RESERVED_NAMES.add("subu");
        RESERVED_NAMES.add("mul");
        RESERVED_NAMES.add("divide");
        RESERVED_NAMES.add("beq");
        RESERVED_NAMES.add("bne");
        RESERVED_NAMES.add("blez");
        RESERVED_NAMES.add("seq");
        RESERVED_NAMES.add("sne");
        RESERVED_NAMES.add("slt");
        RESERVED_NAMES.add("sle");
        RESERVED_NAMES.add("neg");
        RESERVED_NAMES.add("jal");
        RESERVED_NAMES.add("j");
        RESERVED_NAMES.add("move");
    }
    
    public static boolean isReserved(String s) {
    	return RESERVED_NAMES.contains(s);
    }

    
    // for pretty printing generated code
    private static final int MAXLEN = 4;

    // for generating labels
    private static int currLabel = 0;

    // **********************************************************************
    // Method Implementations
    // **********************************************************************

    public static void generateWithComment(String opcode, String comment,
                                           String arg1, String arg2,
                                           String arg3) {
        int space = MAXLEN - opcode.length() + 2;

        p.print("\t" + opcode);
        if (!arg1.isEmpty()) {
            for (int k = 1; k <= space; k++) p.print(" ");
            p.print(arg1);
            if (!arg2.isEmpty()) {
                p.print(", " + arg2);
                if (!arg3.isEmpty()) p.print(", " + arg3);
            }
        }           
        if (!comment.isEmpty()) p.print("\t\t#" + comment);
        p.println();
    }

    public static void generateWithComment(String opcode, String comment,
                                           String arg1, String arg2) {
        generateWithComment(opcode, comment, arg1, arg2, "");
    }

    public static void generateWithComment(String opcode, String comment,
                                           String arg1) {
        generateWithComment(opcode, comment, arg1, "", "");
    }

    public static void generateWithComment(String opcode, String comment) {
        generateWithComment(opcode, comment, "", "", "");
    }

    public static void generate(String opcode, String arg1, String arg2,
                                String arg3) {
        generateWithComment(opcode, "", arg1, arg2, arg3);
    }

    public static void generate(String opcode, String arg1, String arg2) {
        generateWithComment(opcode, "", arg1, arg2);
    }

    public static void generate(String opcode, String arg1) {
        generateWithComment(opcode, "", arg1);
    }

    public static void generate(String opcode) {
        generateWithComment(opcode, "");
    }

    public static void generate(String opcode, String arg1, String arg2,
                                int arg3) {
        generateWithComment(opcode, "", arg1, arg2, Integer.toString(arg3));
    }

    public static void generate(String opcode, String arg1, int arg2) {
        generateWithComment(opcode, "", arg1, Integer.toString(arg2));
    }

    public static void generateIndexed(String opcode, String arg1,
                                      String arg2, int arg3, String comment) {
        generateWithComment(opcode, comment, arg1, arg3 + "(" + arg2 + ")");
    }

    public static void generateIndexed(String opcode, String arg1,
                                       String arg2, int arg3) {
        generateIndexed(opcode, arg1, arg2, arg3, "");
    }

    public static void generateLabeled(String label, String opcode,
                                       String comment, String arg1) {
        p.print(label + ":\t" + opcode);
        if (!arg1.isEmpty()) p.print(" " + arg1);
        if (!comment.isEmpty()) p.print("\t#" + comment);
        p.println();
    }

    public static void generateLabeled(String label, String opcode,
                                       String comment) {
        generateLabeled(label, opcode, comment, "");
    }

    public static void genPush(String s) {
    	 generate(SUBU, SP, SP, 4);
        generateIndexed("sw", s, SP, 0, "PUSH");
        
                              
    }

    public static void genPop(String s) {
    
        generateIndexed("lw", s, SP, 0, "POP");
    	generate(ADDU, SP, SP, 4);
    }

    public static void genCompare(String op) {
    	generate("li",T0,FALSE);
        generate("li",T1,TRUE);
        String trueLabel = nextLabel();
        String falseLabel = nextLabel();
        
        generate(op, A0, ZERO, trueLabel);  // Branch to true label
        genPush(T0);                   // Push false
        generate(J, falseLabel);          // Jump to false label
        genLabel(trueLabel);              // True label
        genPush(T1);                    // Push true
        genLabel(falseLabel);             // False label
    }
   /* public static void genCompare(String op) {
        // Generiere Labels für den Wahrheits- und Falschfall
        String truelab = nextLabel();
        String falselab = nextLabel();

        // Generiere den Vergleichsbefehl je nach übergebenem Operator
        if (op.equals(BEQ)) {
            // Wenn T0 == T1, springe zu truelab
            generate(BEQ, T0, T1, truelab);
        } else if (op.equals(BNE)) {
            // Wenn T0 != T1, springe zu truelab
            generate(BNE, T0, T1, truelab);
        } else if (op.equals(SLT)) {
            // Wenn T0 < T1, springe zu truelab
            generate(SLT, T0, T1, truelab);
        } else if (op.equals(SLE)) {
            // Wenn T0 <= T1, springe zu truelab
            generate(SLE, T0, T1, truelab);
        } else {
            throw new IllegalArgumentException("Unsupported comparison operator: " + op);
        }

        // Wenn der Vergleich nicht wahr war, springe zum falselab
        generate(J, falselab);

        // truelab: TRUE auf das Register T2 laden und auf den Stack pushen
        genLabel(truelab);
        generate(LI, T2, TRUE);  // Lade den Wert TRUE (als -1) in $t2
        genPush(T2);  // Push TRUE (T2)

        // falselab: FALSE auf das Register T2 laden und auf den Stack pushen
        genLabel(falselab);
        generate(LI, T2, FALSE); // Lade den Wert FALSE (als 0) in $t2
        genPush(T2);  // Push FALSE (T2)
    }*/


    public static void genLabel(String label, String comment) {
        p.println(label + ":\t#" + comment);
    }

    public static void genLabel(String label) {
        genLabel(label, "");
    }

    public static String nextLabel() {
        return "._L" + currLabel++;
    }
}
