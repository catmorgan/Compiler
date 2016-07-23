package SymbolTable;

/**
 *
 * @author Cat Morgan
 */
public class ProcedureEntry extends SymbolTableEntry {

    //number of parameters in procedure
    public int numParam = 0;
    //used to check/set if procedure is parameter
    boolean parm = false;
    //used to check/set if procedure is function result
    boolean functionResult = false;
    //declares a ParameterInfo object to keep track of parameter information
    public ParameterInfo p = new ParameterInfo();
    //name of the procedure
    String n;

    /**
     * Constructor
     */
    public ProcedureEntry() {
    }

    /**
     * Constructors
     * @param name, value of token
     */
    public ProcedureEntry(String name) {
        super(name);
        //save the name of the procedure
        this.n = name;
    }

    public ProcedureEntry(String name, int numParam) {
        super(name);
        //save the name of the procedure
        this.n = name;
        this.p = new ParameterInfo(numParam);
    }
    /**
     * @return true since it is a procedure
     */
    public boolean isProcedure() {
        return true;
    }

    /**
     * @return true if procedure is a function result, otherwise false
     */
    public boolean isFunctionResult() {
        return functionResult;
    }

    /**
     * set the procedure to be a function result
     */
    public void setFunctionResult() {
        this.functionResult = true;
    }

    /**
     * @return true if procedure is a parameter, otherwise false
     */
    public boolean isParameter() {
        return parm;
    }

    /**
     * set the procedure to be a parameter
     */
    public void setParm() {
        this.parm = true;
    }
    
    public int numParam() {
        return numParam;
    }

    /**
     * prints the procedure with name
     */
    public void print() {
        System.out.println("Procedure Entry: ");
        System.out.println("    Name: " + n);
        System.out.println();
    }
    
    @Override
    public String toString() {
        return "Procedure Entry: " + '\n'+ "  Name: " + n;
    }
}
