package SymbolTable;

import lexicalanalyzer.*;

/**
 *
 * @author Cat Morgan
 */
public class SymbolTableDriver {

    /**
     * Constructor
     */
    public SymbolTableDriver() {
        super();
    }

    /**
     * the run method 
     */
    protected void run() {
        //table to hold constants
        SymbolTable ConstantTable = new SymbolTable(17);
        //table to hold global variables
        SymbolTable GlobalTable = new SymbolTable(37);
        //table to hold local variables
        SymbolTable LocalTable = new SymbolTable(37);

        //create a new lexical analyzer object, from the test file
        LexicalAnalyzer t =
                new LexicalAnalyzer
                ("C:\\Users\\Cat Morgan\\Desktop\\Compiler\\tests\\symtabtest.dat");
        //create a new token object
        Token token = new Token();
        //while the token is not the end of the file
        while (token.getType() != TokenType.ENDOFFILE) {
            //if the token is an identifier
            if (token.getType() == TokenType.IDENTIFIER) {
                //insert it into the local table with the token value as the key, 
                //and a new VariableEntry of value,type
                LocalTable.insert(token.getValue(), 
                        new VariableEntry(token.getValue(), token.getType()));
            }
            //if the token is an intconstant or realconstant
            if (token.getType() == TokenType.INTCONSTANT
                    || token.getType() == TokenType.REALCONSTANT) {
                //insert it into the constant table with the token value as the
                //key, and a new ConstantEntry of value,type
                ConstantTable.insert(token.getValue(), 
                        new ConstantEntry(token.getValue(), token.getType()));
            }
            //get the next token
            token = t.getNextToken();
        }

        //insert into the global table read as the key, and a new ProcedureEntry
        //of name "read" as the value
        GlobalTable.insert("read", new ProcedureEntry("read"));
        //insert into the global table write as the key, and a new 
        //ProcedureEntry of name "read" as the value
        GlobalTable.insert("write", new ProcedureEntry("write"));

        //heading for constant table dump
        System.out.println("---------------------------------");
        System.out.println("        Constant Table");
        System.out.println("---------------------------------");
        //print all entries in constant table
        ConstantTable.dumpTable();
        //heading for local table dump
        System.out.println("---------------------------------");
        System.out.println("        Local Table");
        System.out.println("---------------------------------");
        //print all entries in local table
        LocalTable.dumpTable();
        //heading for global table dump
        System.out.println("---------------------------------");
        System.out.println("        Global Table");
        System.out.println("---------------------------------");
        //print all entries in global table
        GlobalTable.dumpTable();
    }

    /**
     * main method
     * @param args 
     */
//    public static void main(String[] args) {
//        //create new symboltabledriver object 
//        SymbolTableDriver test = new SymbolTableDriver();
//        //call the run method
//        test.run();
//    }
}
