/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Parser;

/**
 *
 * @author Cat Morgan
 */
public enum SemanticAction implements GrammarSymbol {
    //defines all the actions
    action1(1), action2(2), action3(3), action4(4), action5(5), action6(6),
    action7(7), action8(8), action9(9), action10(10), action11(11), action12(12),
    action13(13), action14(14), action15(15), action16(16), action17(17),
    action18(18), action19(19), action20(20), action21(21), action22(22),
    action23(23), action24(24), action25(25), action26(26), action27(27),
    action28(28), action29(29), action30(30), action31(31), action32(32),
    action33(33), action34(34), action35(35), action36(36), action37(37),
    action38(38), action39(39), action40(40), action41(41), action42(42),
    action43(43), action44(44), action45(45), action46(46), action47(47),
    action48(48), action49(49), action50(50), action51(51), action52(52),
    action53(53), action54(54), action55(55), action56(56);
    
    //used to indicate the number associated with the tokentype
    private int n;

    /**
     * @param i, takes in a number and finds the SemanticAction associated with
     * that number
     */
    private SemanticAction(int i) {
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

    //returns false since the GrammarSymbol is not a NonTerminal
    public boolean isNonTerminal() {
        return false;
    }

    //returns true if the GrammarSymbol is an Action
    public boolean isAction() {
        return true;
    }
}
