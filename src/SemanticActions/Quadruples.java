/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SemanticActions;

import java.util.ArrayList;
import SymbolTable.*;

/**
 *
 * @author Cat Morgan
 */
public class Quadruples {
    //sets up the datastructre for quad
    ArrayList<String> quad = new ArrayList<String>();
    //the four fields that can be in a quad
    String op, a1, a2, a3;

    /**
     * Constructor
     */
    public Quadruples() {
    }
    
    /**
     * Constructor
     * @param op, opcode
     * @param a1 field 1    
     * @param a2 field 2
     * @param a3 field 3
     */
    public Quadruples(String op, String a1, String a2, String a3) {
        this.op = op;
        quad.add(op);
        this.a1 = a1;
        quad.add(a1);
        this.a2 = a2;
        quad.add(a2);
        this.a3 = a3;
        quad.add(a3);
    }
    
    /**
     * get the string at a given position
     * @param i, position within quad
     * @return the string at that field
     */
    public String getPos(int i) {
       return quad.get(i);
    }
    
    /**
     * set the position in quad to be string
     * @param i position within quad
     * @param s string to set position as
     */
    public void setPos(int i, String s) {
        quad.set(i, s);
    }

    /**
     * print the quad array
     */
    public void print() {
        int i = 0;
        for (String n : quad) {
            if (n != null) {
                if (i > 1) {
                System.out.print("," + n);
                i++;
            } else {
                    System.out.print(" " + n);
                    i++;
                }
            }
        }
    }
}




