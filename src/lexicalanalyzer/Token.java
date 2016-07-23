/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lexicalanalyzer;

/**
 *
 * @author Cat Morgan
 */

public class Token {
    //the token type
   private  TokenType type;
    //the token's value
   private  String value;

    /**
     * create a token of type and value
     * @param type, a TokenType
     * @param value, the value of the token as a string
     */
    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }
    
    //for identifier, need to make another constructor so its 
    //<TokenType, SymbolTableEntry>
    
    //or coudl change general constructor to be <TokenType, SymbolTableEntry>
    //and ake a keyword table. thne check if identifier is in keyword table,
    //otherwise identifier
    
    public Token() {
        this.type = null;
        this.value = "";
    }
    /**
     * sets the type of a token
     * @param t, the new token type
     */
    public void setType(TokenType t) {
        this.type = t;
    }
    
    /**
     * 
     * @return the TokenType of the token
     */
    public TokenType getType() {
        return this.type;
    }
    /**
     * sets the value of a token
     * @param v, the new value of the token as a string
     */
    public void setValue(String v) {
        this.value = v;
    }

    /**
     * 
     * @return the string value of a token
     */
    public String getValue() {
        return this.value;
    }
    /**
     * clears the token to be a "blank" token
     */
    public void clear() {
        this.type = null;
        this.value = "";
    }
    /**
     * prints the token
     */
    public void print() {
        System.out.println();
        System.out.println("Current token: <" + this.type + "," +  this.value + ">");
    }
    
    @Override
    public String toString() {
        return "(" + this.value + "," +  this.type + ")";
    }
}
