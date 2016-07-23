/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Parser;

/**
 *
 * @author Cat Morgan
 */
public interface GrammarSymbol 
{
        //checks if GrammarSymbol is a token
	boolean isToken();
        //checks if GrammarSymbol is a NonTerminal
	boolean isNonTerminal();
        //checks if GrammarSymbol is an Action
	boolean isAction();
        //get the corresponding number to the GrammarSymbol
        int getIndex();
}
