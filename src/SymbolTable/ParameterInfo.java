package SymbolTable;
import java.util.ArrayList;
import lexicalanalyzer.*;

/**
 *
 * @author Cat Morgan
 */
public class ParameterInfo extends SymbolTableEntry {
    //static checking for what type like array or standard, how many parameters, 
    //what types they are 
    
    //arraylist of symboltableentries or variableentry 
    //or token types 
    //think about what i want stored and why
    
    //arraylist to keep track of parameter info
    ArrayList<Token> param;
    Token currentParm = new Token();
    //current index of parameter
    int index;
    
    //need to keep track of number of parameters and type to static check to make sure
    //that the types match

    /**
     * Constructor
     */
    public ParameterInfo() {
        //initialize param to be new ArrayList, which is automatically size 10
        param = new ArrayList<Token>();
        //index is 0
        index = 0;
    }

    /**
     * Constructor
     * @param size, size of the ArrayList
     */
    public ParameterInfo(int size) {
        //intialize param to be a new ArrayList of input size
        param = new ArrayList(size);
        //index is 0
        index = 0;
    }
    
    /**
     * add a parameter to the list
     * @param t add this token to the list
     */
    public void addParam(Token t) {
        param.add(t);
    }
    
    /**
     * set the current parameter to be a certain token
     * @param t the token
     */
    public void setCurrentParam (Token t) {
        currentParm = t;
    }
    
    /**
     * return the parameter at the given index
     * @param index, input index to find parameter
     * @return value at index
     */  
    public Token getParam(int index) {
        return param.get(index);
    }

    /**
     * the number of parameters currently stored
     * @return int size of arraylist
     */
    public int numParam() {
        return param.size();
    }

}
