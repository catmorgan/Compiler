package SymbolTable;
import lexicalanalyzer.TokenType;

/**
 *
 * @author Cat Morgan
 */
public class FunctionEntry extends SymbolTableEntry {

    //number of parameters
    public int numParam = 0;
    //used to check/set if function is a parameter
    boolean parm = false;
    //used to check/set if function is a function result
    boolean functionResult = false;
    //declares a ParameterInfo object to keep track of parameter information
    ParameterInfo p = new ParameterInfo();
    //pointer to variable result
    public VariableEntry result;
    //name of function entry 
    String n;
    //type of function entry 
    TokenType t;
    
    /**
     * Constructor
     */
    public FunctionEntry() {}
    
    /**
     * Constructor
     * @param name value of token
     */
    public FunctionEntry(String name) {
        super(name);
        //save name of function entry
        this.n = name;
    }
    
    /**
     * Constructor
     * @param name, value of token
     * @param type, type of token
     */
    public FunctionEntry(String name, TokenType type) {
        super(name,type);
        //save name of function entry
        this.n = name;
        //save type of function entry
        this.t = type;
    }

    /**
     * @return true since this is a function
     */
    public boolean isFunction() {
        return true;
    }

    /**
     * @return true if function is a function result, otherwise false
     */
    public boolean isFunctionResult() {
        return functionResult;
    }

    /**
     * set the function to be a function result
     */
    public void setFunctionResult() {
        this.functionResult = true;
    }

    /**
     * @return, true if function is a parameter, otherwise false
     */
    public boolean isParameter() {
        return parm;
    }

    /**
     * set the function to be a parameter
     */
    public void setParm() {
        this.parm = true;
    }
    
    public int numParam(){
        return numParam;
    }

    /**
     * prints the function entry with name and type
     */
    public void print() {
        System.out.println("Function Entry: ");
        System.out.println("    Name: " + n);
        System.out.println("    Type: " + t);
        System.out.println();
    }
    
    @Override
    public String toString() {
        return "Function Entry: " + '\n' + "    Name: " + n +
                '\n' + "    Type: " + t;
                
    }
}
