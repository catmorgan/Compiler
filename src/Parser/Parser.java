/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Parser;

import java.util.Stack;
import lexicalanalyzer.*;
import SemanticActions.*;

/**
 *
 * @author Cat Morgan
 */
public class Parser {

    //the lexer to get tokens from
    private LexicalAnalyzer lexer;
    //executes semantic actions
    private SemanticActions actions;
    //the curren token
    private Token token = new Token();
    private Token previousToken = new Token();
    //the stack of the grammar symbols
    private Stack<GrammarSymbol> stack = new Stack<GrammarSymbol>();
    //initializes the parse table
    private ParseTable parseTable = new ParseTable();
    //the top symbol on the stack, representing the predicted symbol
    private GrammarSymbol predicted;
    //initializes a new right-hand side table
    private RHSTable RHS = new RHSTable();

    /**
     * initializes the parser
     *
     * @param d, the input lexicalanalyzer to parse
     */
    public Parser(LexicalAnalyzer d) {
        this.lexer = d;
        this.actions = new SemanticActions();
    }

    /**
     * clears the stack
     */
    private void clear() {
        stack = new Stack<GrammarSymbol>();
    }

    /**
     * processes the tokens to make sure that the symbols are in the proper
     * grammar order
     *
     * @throws ParserError, if an error occurs calls the ParseError class
     */
    public void parser() throws ParserError {
        //token gets the next token from the lexer
        token = lexer.getNextToken();
        //prints the token
        //token.print();
        //clear the stack
        clear();
        //push EOF onto the stack
        stack.push(token.getType().ENDOFFILE);
        //push the start symbol to the stack
        stack.push(NonTerminal.Goal);

        //while the stack is not empty
        while (!stack.isEmpty()) {
            //predicted is the top symbol on the stack
            predicted = stack.pop();
            //Print out the predicted symbol
            //System.out.println("Predicted symbol: " + predicted.toString());
            //if predicted is a token
            if (predicted.isToken()) {
                //if predicted equals the current token's type
                if (predicted.equals(token.getType())) {
                    //set the previous token to be the current token 
                    previousToken = new Token(token.getType(), token.getValue());
                    //get the next token
                    token = lexer.getNextToken();
                    //token.print();
                } //else if predicted doesn't equal the current token's type
                else if (!predicted.equals(token.getType())) {
                    try {
                        //throw an error because of a mismatch in tokens
                        throw ParserError.Mismatched(token, predicted);
                    } catch (ParserError ex) {
                        System.out.println(ex);
                    }
                }
            } //if predicted is an action
            else if (predicted.isAction()) {
                //call a semantic action on using predicted, and the previous
                //token
                previousToken.print();
                System.out.println("predicted token(token after): <" + token.getValue() 
                        + ", " + token.getType() + ">");
                actions.Execute((SemanticAction) predicted, previousToken);
                //print the semantic action stack
                actions.printStack();
            }  //else if the predicted is a nonterminal
            else if (predicted.isNonTerminal()) {
                //get the int value associated with the [predicted, token] 
                //position in the parse table
                int check = parseTable.search(predicted.getIndex(), token.getType().getIndex());
                //if the int is 999, it's an error
                if (check == 999) {
                    try {
                        //throw the error because it was unexpected token
                        throw ParserError.UnexpectedToken(token);
                    } catch (ParserError ex) {
                        System.out.println(ex);
                    }
                }   //if the int value is negative, do nothing
                else if (check < 0) {
                } //else find the corresponding rules in the right-hand side table
                //and push them onto the stack in reverse order
                else {
                    //prints the rules being pushed to the stack      
                    //System.out.print("Pushing rule: ");
                    //RHS.printrule(check);
                    for (int i = RHS.getRule(check).length - 1; i >= 0; i--) {
                        stack.push(RHS.getRule(check)[i]); 
                    }
                }
            }
        }
        System.out.println("---------TVI----------");
        actions.printQuads();
    }
}
