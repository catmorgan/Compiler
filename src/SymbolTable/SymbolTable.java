package SymbolTable;

import java.util.Collection;
import java.util.HashMap;

/**
 *
 * @author Cat Morgan
 */
public class SymbolTable {
    //declares the symbol table to be a HashMap with String as key and 
    //SymbolTableEntry as value
    private HashMap<String, SymbolTableEntry> symbolTable;

    /**
     *Constructor
     * @param size, size of the symbol table
     */
    public SymbolTable(int size) {
        //symbol table is a new HashMap of input size
        this.symbolTable = new HashMap(size);
        //initializes all key and values to be null
        for (int i = 0; i < symbolTable.size(); i++) {
            symbolTable.put(null, null);
        }
    }

    /**
     * Inserts a key and value mapping into symbol table
     * @param key, the value of the token
     * @param value, the SymbolTableEntry corresponding to the type of token
     * will be updated later with Semantic Actions
     */
    public void insert(String key, SymbolTableEntry value) {
        //if the key is already in the table
        if (symbolTable.containsKey(key)) {
            //remove the mapping
            symbolTable.remove(key);
            //and replace it with the new input key, value mapping
            symbolTable.put(key, value);
            //maybe return conflict or-?
        } else {
            //else just add the key, value mapping
            symbolTable.put(key, value);
        }
    }

    /**
     * Return the value associated with the key 
     * @param key, value of token used to find the corresponding value
     * @return the SymbolTableEntry value of the key
     */
    public SymbolTableEntry lookUp(String key) {
        return symbolTable.get(key);
        //what if the key isn't in the table
    }
    
    /**
     * checks if the input key is already in the table
     * @param key, value of token to check in the table
     * @return true if the key is already present, otherwise false
     */
    public boolean containsKey(String key) {
        return symbolTable.containsKey(key);
    }

    /**
     * the size of the table
     * @return the integer size of the table
     */
    public int size() {
        return symbolTable.size();
    }

    /**
     * prints all the entries in the table
     */
    public void dumpTable() {
        //create a collection of SymbolTableEntry from the table values
        Collection<SymbolTableEntry> value = symbolTable.values();
        //for each entry in the collection
        for (SymbolTableEntry tab : value) {
            //print the entry
            tab.print();
        }
    }
    
}
