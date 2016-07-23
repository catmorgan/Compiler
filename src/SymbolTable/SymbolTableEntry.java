package SymbolTable;

import lexicalanalyzer.TokenType;

/**
 *
 * @author Cat Morgan
 */
public class SymbolTableEntry {

    //value of token
    String name;
    //type of token
    TokenType type;

    /**
     * Constructor
     */
    public SymbolTableEntry() {
    }

    /**
     * Constructor
     * @param name, value of token
     */
    public SymbolTableEntry(String name) {
        this.name = name;
    }

    /**
     * Constructor
     * @param name, value of token
     * @param type, type of token
     */
    public SymbolTableEntry(String name, TokenType type) {
        this.name = name;
        this.type = type;
    }

    /**
     * @return value of the token
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param name, change the current value of the token to be input name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the type of the token
     */
    public TokenType getType() {
        return this.type;
    }

    /**
     * @param type, change the current type of the token to be input type
     */
    public void setType(TokenType type) {
        this.type = type;
    }

    /*
     * Functions below are necessary in all classes that extend this class
     */
    public boolean isVariable() {return false;}
    public boolean isKeyword() {return false;}
    public boolean isProcedure() {return false;}
    public boolean isFunction() {return false;}
    public boolean isConstant(){return false;}
    public boolean isFunctionResult() {return false;}
    public boolean isParameter() {return false;}
    public boolean isArray() {return false;}
    public int getAddress() {return -1;}
    public void setAddress(int i){}
    protected void print() {}
    public int numParam(){return 0;}
    @Override
    public String toString(){return "";}
    
}
