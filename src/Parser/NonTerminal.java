/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Parser;

/**
 *
 * @author Cat Morgan
 */
public enum NonTerminal implements GrammarSymbol {	
        //declares all the NonTerminals
        program(0), identifier_list(1), declarations(2), sub_declarations(3),
    compound_statement(4), identifier_list_tail(5), declaration_list(6),
    type(7), declaration_list_tail(8), standard_type(9), array_type(10),
    subprogram_declaration(11), subprogram_head(12), arguments(13),
    parameter_list(14), parameter_list_tail(15), statement_list(16),
    statement(17), statement_list_tail(18), elementary_statement(19),
    expression(20), else_clause(21), es_tail(22), subscript(23),
    parameters(24), expression_list(25), expression_list_tail(26),
    simple_expression(27), expression_tail(28), term(29),
    simple_expression_tail(30), sign(31), factor(32), term_tail(33),
    factor_tail(34), actual_parameters(35), Goal(36), constant(37);
    //used to indicate the number associated with the NonTerminal    
    private int n;

    /**
     * @param i, takes in a number and finds the NonTerminal associated with
     * that number
     */
    private NonTerminal(int i) {
        n = i;
    }

    /**
     * @return the appropriate number
     */
    public int getIndex() {
        return n;
    }

    //returns false since the GrammarSymbol is not a token
    public boolean isToken() {
        return false;
    }

    //returns true if the GrammarSymbol is a NonTerminal
    public boolean isNonTerminal() {
        return true;
    }

    //returns false since the GrammarSymbol is not an Action
    public boolean isAction() {
        return false;
    }
}
