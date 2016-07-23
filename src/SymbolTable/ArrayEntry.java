package SymbolTable;

import lexicalanalyzer.*;

/**
 *
 * @author Cat Morgan
 */
public class ArrayEntry extends SymbolTableEntry {
    
    //address of entry 
    int address;
    //upper bound of the array
    int upperBound;
    //lower bound of the array
    int lowerBound;
    //used to check/set if array is a parameter 
    boolean parm = false;
    //used to check/set if array is a function result
    boolean functionResult = false;
    String name = "";
    Token t = new Token();

    /**
     * Constructor
     */
    public ArrayEntry() {
    }

    /**
     * Constructor
     * @param name, value of token
     */
    public ArrayEntry(String name) {
        super(name);
        this.name = name;
    }

    /**
     * Constructor 
     * @param name, value of token
     * @param type, type of token
     */
    public ArrayEntry(String name, TokenType type) {
        super(name, type);
        this.name = name;
        this.t.setType(type);
    }

    /**
     * @return current address location of array
     */
    public int getAddress() {
        return address;
    }

    /**
     * change the current address to be input address
     * @param address, where you want ArrayEntry address to be
     */
    public void setAddress(int address) {
        this.address = address;
    }

    /**
     * @return upper bound of ArrayEntry
     */
    public int getUpperBound() {
        return upperBound;
    }

    /**
     * change the current upper bound to be input bound
     * @param u, new upper bound
     */
    public void setUpperBound(int u) {
        this.upperBound = u;
    }

    /**
     * @return current lower bound of ArrayEntry
     */
    public int getLowerBound() {
        return lowerBound;
    }

    /**
     * change current lower bound to be input bound
     * @param l, new lower bound
     */
    public void setLowerBound(int l) {
        this.lowerBound = l;
    }

    /**
     * @return true, since it is an ArrayEntry
     */
    public boolean isArray() {
        return true;
    }

    /**
     * @return true if Array is a function result, otherwise false
     */
    public boolean isFunctionResult() {
        return functionResult;
    }

    /**
     * set the Array to be a function result
     */
    public void setFunctionResult() {
        this.functionResult = true;
    }

    /**
     * @return true if Array is a parameter, otherwise false
     */
    public boolean isParameter() {
        return parm;
    }

    /**
     * set the Array to be a parameter
     */
    public void setParm() {
        this.parm = true;
    }

    /**
     * prints the Array Entry
     */
    public void print() {
        System.out.println("Array Entry: ");
        System.out.println();
    }

    @Override
    public String toString() {
        return "Array Entry: " + '\n' + "   Name: " + name + 
                '\n' + "    Type: " + t.getType();
    }
}
