/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Parser;

import lexicalanalyzer.*;

/**
 *
 * @author Cat Morgan
 */
public class ParserDriver {

    public static void main(String[] args) throws ParserError {
        //initialize a parser with the parse test 
        Parser p =
                new Parser(new LexicalAnalyzer("C:\\Users\\Cat Morgan\\Desktop\\Compiler\\tests\\testinglexical.dat"));
        //call the function that manipulates the stack
        p.parser();
        System.out.println("Parsing completed.");
    }
}
