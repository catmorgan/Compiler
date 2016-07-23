package SymbolTable;
import SemanticActions.*;
import lexicalanalyzer.TokenType;

/**
 *
 * @author Cat Morgan
 */
public class VariableEntry extends SymbolTableEntry {

    //address of variable
    int address;
    //used to check/set if variable is a parameter
    boolean parm = false;
    //used to check/set if variable is function result
    boolean functionResult = false;

    /**
     * Constructor
     */
    public VariableEntry() {
    }

    /**
     * Constructor
     *
     * @param name, value of token
     */
    public VariableEntry(String name) {
        super(name);
    }

    /**
     * Constructor
     * @param name, value of token
     * @param type, type of token
     */
    public VariableEntry(String name, TokenType type) {
        super(name, type);
    }

    /**
     * @return current address location of variable
     */
    public int getAddress() {
        return address;
    }

    /**
     * change the current address to be input address
     * @param address, where you want Variable address to be
     */
    public void setAddress(int address) {
        this.address = address;
    }

    /**
     * @return true, since this is a variable
     */
    public boolean isVariable() {
        return true;
    }

    /**
     * @return true if variable is a function result, otherwise false
     */
    public boolean isFunctionResult() {
        return functionResult;
    }

    /**
     * set the variable to be a function result
     */
    public void setFunctionResult() {
        this.functionResult = true;
    }

    /**
     * @return, true if variable is a parameter, otherwise false
     */
    public boolean isParameter() {
        return parm;
    }

    /**
     * set the variable to be a parameter
     */
    public void setParm() {
        this.parm = true;
    }

    /**
     * prints the variable with name and type
     */
    public void print() {
        System.out.println("Variable Entry:");
        System.out.println("   Name    : " + this.getName());
        System.out.println("   Type    : " + this.getType());
        //System.out.println("   Address : " + this.getAddress());
        System.out.println();
    }
    
    @Override
    public String toString() {
        return "Variable Entry: " + '\n' + "    Name: " + this.getName()
                + '\n' + "  Type: " + this.getType();
    }
    
}
