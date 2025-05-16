import java.io.*;
import java_cup.runtime.*;

// Main program to test the simple parser.
@SuppressWarnings("deprecation")
public class p4 {
    public static void main(String[] args) throws IOException {
        // Check for command-line args
        if (args.length != 2) {
            System.err.println("Please supply name of file to be parsed and name of file for decompiled version.");
            System.exit(-1);
        }

        // Open input file
        FileReader inFile = null;
        try {
            inFile = new FileReader(args[0]);
        } catch (FileNotFoundException ex) {
            System.err.println("File " + args[0] + " not found.");
            System.exit(-1);
        }

        // Open output file
        PrintWriter outFile = null;
        try {
            outFile = IO.openOutputFile(args[1]);
        } catch (IOException ex) {
            System.err.println("File " + args[1] + " could not be opened.");
            System.exit(-1);
        }

        // Create a new parser
        parser P = new parser();
        P.setScanner(new Yylex(inFile));

        Symbol root = null; // The parser will return a Symbol whose value field's type is associated with the root nonterminal

        try {
            root = P.parse(); // Perform the parsing
            System.out.println("Simple program parsed correctly.");
        } catch (Exception ex) {
            System.out.println(ex);
            System.exit(0);
        }

        // Step 1: Name Analysis
        // Get the root of the AST and perform name analysis
        ProgramNode astRoot = (ProgramNode) root.value;
        
        // Global symbol table for name analysis
        SymbolTable globalSymbolTable = new SymbolTable(); // Global symbol table for name analysis
        
        if (astRoot != null) {
            astRoot.analyzeName(globalSymbolTable); // Perform name analysis
            // Print the symbol table after name analysis, if needed
           
        }
        if (astRoot != null) {
            astRoot.analyzeType(0); // Perform name analysis
            // Print the symbol table after name analysis, if needed
           
        }

        // Step 3: Decompiling the AST
        astRoot.decompile(outFile, 0);
        
        
        outFile.close();
    }
}
