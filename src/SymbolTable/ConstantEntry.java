package SymbolTable;
import lexicalanalyzer.TokenType;

/**
 *
 * @author Cat Morgan
 */
public class ConstantEntry extends SymbolTableEntry {

    //used to check/set if constant is parameter
    boolean parm = false;
    //used to check/set if constant is function result
    boolean functionResult = false;
    int address = 0;

    /**
     * Constructor
     */
    public ConstantEntry() {
    }

    /**
     * Constructor
     * @param name, value of token
     */
    public ConstantEntry(String name) {
        super(name);
        this.address = Integer.parseInt(name);
    }

    /**
     * Constructor
     * @param name, value of token
     * @param type, type of token
     */
    public ConstantEntry(String name, TokenType type) {
        super(name, type);
        
        this.address = Integer.parseInt(name);
    }
    
    public int getAddress() {
        return address;
    }
    
    public void setAddress(int i) {
        this.address = i;
    }

    /**
     * @return true if constant is a function result, otherwise false
     */
    public boolean isFunctionResult() {
        return functionResult;
    }

    /**
     * set the constant to be a function result
     */
    public void setFunctionResult() {
        this.functionResult = true;
    }

    /**
     * @return, true if constant is a parameter, otherwise false
     */
    public boolean isParameter() {
        return parm;
    }

    /**
     * set the constant to be a parameter
     */
    public void setParm() {
        this.parm = true;
    }
    
    public boolean isConstant(){
    return true;
    }

    /**
     * prints the constant entry with name and type
     */
    public void print() {
        System.out.println("Constant Entry:");
        System.out.println("    Name    : " + this.getName());
        System.out.println("    Type    : " + this.getType());
        System.out.println();
    }
    
    @Override
    public String toString() {
        return "Constant Entry: " + '\n' + "    Name    : " + this.getName() 
                + '\n' + "    Type    : " + this.getType();
    }
}
