package SymbolTable;

/**
 *
 * @author Cat Morgan
 */
public class KeywordEntry extends SymbolTableEntry{
    
    /**
     * Constructor
     */
    public KeywordEntry() {}
    
    /**
     * Constructor
     * @param name, value of token
     */
    public KeywordEntry(String name) {
        super(name);
    }
    
    /**
     * @return true since it is a keyword
     */
    public boolean isKeyword() {
        return true;
    }
    
    /**
     * prints the keyword with name
     */
    public void print() {
        System.out.println("Keyword Entry:");
        System.out.println("    Name: " + this.getName());
        System.out.println();
    }
    
    @Override
    public String toString() {
        return "Keyword Entry: " + '\n' + "     Name: " + this.getName();
    }
}
