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
public class ParserError extends CompilerError {

    public ParserError(Type errorNumber, String message) {
        super(errorNumber, message);
    }

    /**
     *
     * @param t, the current token
     * @return an error of the unexpected token
     */
    public static ParserError UnexpectedToken(Token t) {
        return new ParserError(Type.UNEXPECTED,
                ">>> ERROR: Unexpected token: <"
                + t.getType() + ", " + t.getValue() + ">");
    }

    /**
     *
     * @param t, the current token
     * @param predicted, the grammarsymbol on top of the stack
     * @return an error of a mismatch type between the predicted symbol and the
     * current token
     */
    public static ParserError Mismatched(Token t, GrammarSymbol predicted) {
        return new ParserError(Type.MISMATCH,
                ">>> ERROR: Expecting <"
                + predicted.getIndex() + ", " + ">, <" + t.getType() + ", "
                + t.getValue() + "> found");
    }
}
