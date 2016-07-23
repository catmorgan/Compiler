/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lexicalanalyzer;

import Parser.*;

/**
 *
 * @author Cat Morgan
 */
public enum TokenType implements GrammarSymbol {
    //the different types of token
    PROGRAM(0), BEGIN(1), END(2), VAR(3), FUNCTION(4), PROCEDURE(5), RESULT(6),
    INTEGER(7), REAL(8), ARRAY(9), OF(10), IF(11), THEN(12), ELSE(13),
    WHILE(14), DO(15), NOT(16),
    IDENTIFIER(17), INTCONSTANT(18), REALCONSTANT(19),
    RELOP(20), MULOP(21), ADDOP(22), ASSIGNOP(23), COMMA(24),
    SEMICOLON(25), COLON(26), RIGHTPAREN(27), LEFTPARAN(28),
    RIGHTBRACKET(29), LEFTBRACKET(30), UNARYMINUS(31), UNARYPLUS(32),
    DOUBLEDOT(33), ENDMARKER(34), ENDOFFILE(35);

    //returns true if the GrammarSymbol is a token
    public boolean isToken() {
        return true;
    }
    
    //returns false since the GrammarSymbol is not a NonTerminal
    public boolean isNonTerminal() {
        return false;
    }
    
    //returns false since the GrammarSymbol is not an Action
    public boolean isAction() {
        return false;
    }
    
    //used to indicate the number associated with the tokentype
    private int n;
    
    /**
     * @param i, takes in a number and finds the tokentype associated
     * with that number
     */
    private TokenType(int i) {
        n = i;
    }
    
    /**
     * @return the appropriate number
     */
    public int getIndex() {
        return n;
    }
}