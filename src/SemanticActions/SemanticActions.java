/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SemanticActions;

import java.util.Stack;
import lexicalanalyzer.*;
import Parser.*;
import SymbolTable.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Cat Morgan
 */
public class SemanticActions {

    //stack of objects which represents tokens to be manipulated by 
    //semantic actions
    private Stack<Object> semanticStack;
    //an array list to keep track of the quadruples being created
    private ArrayList<Quadruples> quadList = new ArrayList();
    //the current quadruple that could be looked at
    private Quadruples quads;
    //the starting index for a quad position within the list
    private int nextQuad = 1;
    //flags insert/search mode in symboltable
    private boolean insert;
    //flag array vs. simple variable
    private boolean isArray;
    //flag global vs local variable
    private boolean global;
    //offset for global memory
    private int globalAlloc = 0;
    private int globalMemory = 0;
    //offset for local memory
    private int localAlloc = 0;
    private int localMemory = 0;
    //flag if something is a parameter
    private boolean isParm;
    //the symbol table of global variables
    private SymbolTable globalTable;
    //the symbol table of local variables
    private SymbolTable localTable;
    //initial table size
    private int tableSize = 27;
    //the current function
    private SymbolTable constantTable;
    //a stack that keeps track of the number of parameters
    private Stack<Integer> parmCount = new Stack<Integer>();
    //size of varibale or array
    private int mSize = 0;
    //used to create a unique, temp ID
    private static int tempIndex = 0;
    //used to create a unique, func id
    private static int funcIndex = 0;
    //used for branching
    private int beginLoop = 0;
    //the current function that is being looked at 
    private SymbolTableEntry currentFunction = null;
    //a stack used for static checking of parameters
    private Stack<ParameterInfo> nextParam = new Stack<ParameterInfo>();
    //internal type for each expression being processed

    private enum ETYPE {

        ARITHEMTIC, RELATIONAL
    };
    //list of labels that need to be backpatched into
    private ArrayList<Integer> ETRUE, EFALSE = new ArrayList();
    //list of integers representing quadruple indexes, used for backpatching
    private ArrayList<Integer> skip_else = new ArrayList();

    /**
     *
     * Constructor, initializes variables
     */
    public SemanticActions() {
        this.globalTable = new SymbolTable(tableSize);
        this.localTable = new SymbolTable(tableSize);
        //add keywords read, write and main into the globalTable
        this.globalTable.insert("read", new ProcedureEntry("read"));
        this.globalTable.insert("write", new ProcedureEntry("write"));
        this.globalTable.insert("main", new ProcedureEntry("main", 0));
        this.semanticStack = new Stack<Object>();
        this.quads = new Quadruples();
        this.insert = true;
        this.isArray = false;
        this.isParm = false;
        this.global = true;
        this.parmCount.add(0);
        this.constantTable = new SymbolTable(tableSize);
    }

    /**
     * generate a quad with one field
     *
     * @param op, the operation of the TVI code
     */
    private void gen(String op) {
        //create a quadruple with only the first field
        Quadruples q = new Quadruples(op, null, null, null);
        //add the quadruple to the global list 
        quadList.add(q);
        //increase quadruple index
        this.nextQuad++;
    }

    /**
     * generate a quad with two fields
     *
     * @param op, the operation for the TVI code
     * @param a1, the first input/register
     */
    private void gen(String op, String a1) {
        //create a quadruple with two fields
        Quadruples q = new Quadruples(op, a1, null, null);
        //add the quadruple to the global list
        quadList.add(q);
        //increase quadruple index
        this.nextQuad++;
    }

    /**
     * generate a quad with three fields
     *
     * @param op, the operation for the TVI code
     * @param a1, the first input/register
     * @param a2, the second input/register
     */
    private void gen(String op, String a1, String a2) {
        //create a quadruple with three fields
        Quadruples q = new Quadruples(op, a1, a2, null);
        //add the quadruple to the global list
        quadList.add(q);
        //increase quadruple index
        this.nextQuad++;
    }

    /**
     * generate a quad with four fields
     *
     * @param op, the operation for the TVI code
     * @param a1, the first input/register
     * @param a2, the second input/register
     * @param a3, the third input/register
     */
    private void gen(String op, String a1, String a2, String a3) {
        //create a quadruple with four fields 
        Quadruples q = new Quadruples(op, a1, a2, a3);
        //add quad to global list
        quadList.add(q);
        //increase quadruple index
        this.nextQuad++;
    }

    /**
     * create a unique temp ID every time
     *
     * @return the string of the temp name
     */
    private String temp() {
        //add a unique number to the end of temp
        String t = "TEMP" + Integer.toString(tempIndex);
        //increase temp index
        tempIndex++;
        //return the name
        return t;
    }

    /**
     * create a unique function ID every time
     *
     * @return the string of the function name
     */
    private String funcName() {
        //add a unique number to the end of func name
        String t = "FUNC_NAME" + Integer.toString(funcIndex);
        //increase func index
        funcIndex++;
        //return func name
        return t;
    }

    /**
     * make a list of the integer input
     *
     * @param i, the input integer to make a list out of
     * @return, the arraylist of integer
     */
    private ArrayList<Integer> makeList(int i) {
        //create a new list
        ArrayList<Integer> temp = new ArrayList<Integer>();
        //add i to the list
        temp.add(i);
        //return the list
        return temp;
    }

    /**
     * merge to lists into one array list
     *
     * @param first, the first list of integers
     * @param second, the second list of integers
     * @return a single list with elements from first and second
     */
    private ArrayList<Integer> mergeLists(ArrayList<Integer> first, ArrayList<Integer> second) {
        //for each integer in the second list 
        for (Integer i : second) {
            //add it to the first list
            first.add(i);
        }
        //return the first list
        return first;
    }

    /**
     *
     * @return the current index of the temp names
     */
    private int getTempNum() {
        return tempIndex;
    }

    /**
     * make a new VariableEntry of a given name and type
     *
     * @param name, name of the VariableEntry
     * @param type, tokentype of the VariableEntry
     * @return the new VariableEntry
     */
    private VariableEntry create(String name, TokenType type) {
        //if in global scope
        if (global) {
            //create a VariableEntry of name and type, 
            //with the name marked by global "_"
            VariableEntry e = new VariableEntry("_" + name, type);
            e.setType(type);
            //set the address to be the current globalMemory
            e.setAddress(globalMemory);
            //increase globalMemory
            globalMemory++;
            //return the VariableEntry
            return e;
        } else {
            //create a VariableEntry of name and type,
            //with the name marked by local indicator "%"
            VariableEntry e = new VariableEntry("%" + name, type);
            //set type to be the tokentype
            e.setType(type);
            //set address to be the local memory
            e.setAddress(localMemory);
            //increase the local memory
            localMemory++;
            //return the VariableEntry
            return e;
        }
    }

    /**
     * compares the types of two tokens
     *
     * @param token1, the first input token
     * @param token2, the second input token
     * @return an integer representation of the type comparison
     */
    private int TypeCheck(Token token1, Token token2) {
        //get the first tokentype
        TokenType id1 = token1.getType();
        //get the second tokentype
        TokenType id2 = token2.getType();
        //set an initial result to -1
        int result = -1;
        //if the tokens are both integers
        if (id1 == TokenType.INTEGER && id2 == TokenType.INTEGER) {
            //set result to 0
            result = 0;
        } //if both tokens are real
        else if (id1 == TokenType.REAL && id2 == TokenType.REAL) {
            //set result to be 1
            result = 1;
        } //if the first token is real and the second is integer
        else if (id1 == TokenType.REAL && id2 == TokenType.INTEGER) {
            //set result to be 2
            result = 2;
        } //if the first token is integer and the second is real
        else if (id1 == TokenType.INTEGER && id2 == TokenType.REAL) {
            //set result to be 3
            result = 3;
        }
        //return result
        return result;
    }

    /**
     * goes through a list of integers that represent line numbers, in which a
     * field needs to be changed to the input "change"
     *
     * @param labels, a list of integers representing line numbers
     * @param change, the int to change a field to
     */
    private void backpatch(ArrayList<Integer> labels, int change) {
        //go through the list
        for (int i = 0; i < labels.size(); i++) {
            //get the quadruple associated with the line number
            Quadruples q = quadList.get(labels.get(i));
            //for the length of the quadruple
            for (int j = 0; j < 4; j++) {
                //if the position equals a blank
                if (q.getPos(j).equals("_")) {
                    //change the position to be the input int
                    q.setPos(j, Integer.toString(change));
                }
            }
        }
    }

    /**
     * set a quadruple in the quadList at a position to be the input integer
     *
     * @param index, the index into the quadlist
     * @param pos, the position within the quadruple
     * @param change, change the position to be this integer
     */
    private void setQuadField(int index, int pos, int change) {
        quadList.get(index - 1).setPos(pos, Integer.toString(change));
    }

    /**
     * goes through switch statements based on which action is being called
     *
     * @param action the semantic action that needs to be called
     * @param token the previous token from parser
     */
    public void Execute(SemanticAction action, Token token) {
        //throws SemanticError {
        //get the action number 
        int actionNumber = action.getIndex();
        //used to help debug and track the stack
        System.out.println("Calling action : " + actionNumber + " with token " + token.getType());

        switch (actionNumber) {
            case 1: {
                //flag the insert to be true
                this.insert = true;
                break;
            }
            case 2: {
                //flag the insert to be false
                this.insert = false;
                break;
            }
            case 3: {
                //pop the top token off the stack
                Token t = (Token) semanticStack.pop();
                //if we are looking at an array
                if (isArray) {
                    //get the upper bound number
                    Token ub = (Token) semanticStack.pop();
                    //pop the lower bound number
                    Token lb = (Token) semanticStack.pop();
                    //the array size is the lower bound - upper bound
                    mSize = (Integer.parseInt(ub.getValue()) - Integer.parseInt(lb.getValue())) + 1;
                    //while the stack is not empty
                    while (!semanticStack.isEmpty()
                            && (semanticStack.peek() instanceof Token)) {
                        //pop the id 
                        Token id = (Token) semanticStack.pop();
                        //if in global scope
                        if (global) {
                            //make a new arrayentry of the current id
                            ArrayEntry curr = new ArrayEntry(id.getValue(), t.getType());
                            //set the address to be global memory
                            curr.setAddress(globalMemory);
                            //increase global memory by the array size
                            globalMemory = globalMemory + mSize;
                            //inset the array entry into the global table
                            globalTable.insert(id.getValue(), curr);
                        } else {
                            //in local scope, create a new arrayentry of the current id
                            ArrayEntry curr = new ArrayEntry(id.getValue(), t.getType());
                            //set the currnt address to be local memory
                            curr.setAddress(localMemory);
                            //add the array size to the local memory
                            localMemory = localMemory + mSize;
                            //insert the array entry into local table
                            localTable.insert(id.getValue(), curr);
                        }
                    }
                } else {
                    //else we are not looking at an array
                    //while the stack is not empty 
                    while (!semanticStack.isEmpty()
                            && (semanticStack.peek() instanceof Token)) {
                        //pop the top token
                        Token id = (Token) semanticStack.pop();
                        //if global scope
                        if (global) {
                            //create a new variable entry from the id
                            VariableEntry curr = new VariableEntry(id.getValue(), t.getType());
                            //set address to be global memory
                            curr.setAddress(globalMemory);
                            //increase global memory by 1
                            globalMemory++;
                            //insert the entry into the global table
                            globalTable.insert(id.getValue(), curr);
                        } else {
                            //else we are in local scope
                            //create a new variable entry of id
                            VariableEntry curr = new VariableEntry(id.getValue(), t.getType());
                            //set address to be local memory
                            curr.setAddress(localMemory);
                            //increase local memory by 1
                            localMemory++;
                            //insert entry into local table
                            localTable.insert(id.getValue(), curr);
                        }
                    }
                }
                //set array flag to false
                isArray = false;
                break;
            }
            case 4: {
                //push the type
                semanticStack.push(token);
                break;
            }
            case 5: {
                //flag insert to false
                insert = false;
                //pop the top token
                Token id = (Token) semanticStack.pop();
                //local memory is the current quad index
                localMemory = nextQuad;
                //create code for TVI
                gen("PROCBEGIN", id.getValue());
                gen("alloc", "_");
                break;
            }
            case 6: {
                //set array flag to true
                isArray = true;
                break;
            }
            case 7: {
                //push constant
                semanticStack.push(token);
                break;
            }
            case 9: {
                //while the stack is not empty
                while (!semanticStack.isEmpty()) {
                    //pop the top token
                    Token id = (Token) semanticStack.pop();
                    //create a new variable entry of the id
                    VariableEntry curr = new VariableEntry(id.getValue(), id.getType());
                    //if in global scope
                    if (global) {
                        //insert in global table
                        globalTable.insert(curr.getName(), curr);
                    } else {
                        //else insert in local table
                        localTable.insert(curr.getName(), curr);
                    }
                }
                //flag insert to false
                insert = false;
                //create quadruples for TVI code
                gen("CODE");
                gen("call", globalTable.lookUp("main").getName(), "0");
                gen("exit");
                break;
            }
            case 11: {
                //set scope to global
                global = true;
                //reset the local table
                localTable = new SymbolTable(tableSize);
                //fill in the quadruple at localAlloc, at position 1, to be local memory
                setQuadField(localAlloc, 1, localMemory);
                //clear current function
                currentFunction = null;
                //create quadruple for TVI code
                gen("free", Integer.toString(localMemory));
                gen("PROCEND");
                break;
            }
            case 13: {
                //push the id
                semanticStack.push(token);
                break;
            }
            case 15: {
                //create a function entry of the current token
                FunctionEntry id = new FunctionEntry(token.getValue(), token.getType());
                //insert it into the global table
                globalTable.insert(id.getName(), id);
                //create a variable entry for the result, with a temp funcName
                VariableEntry result = create("$$" + funcName(), TokenType.INTEGER);
                //set the functionentry result to be the variable entry
                id.result = result;
                // Token rturn = new Token (id.getType(), id.getName());
                //push the function entry
                semanticStack.push(id);
                //set scope to local
                global = false;
                //clear the local table
                localTable = new SymbolTable(tableSize);
                //set local memory to 0
                localMemory = 0;
                break;
            }
            case 16: {
                //pop the top token
                Token t = (Token) semanticStack.pop();
                //look up the current function with the current token
                FunctionEntry func = (FunctionEntry) semanticStack.firstElement();
                //set the function type to be the token type
                func.setType(t.getType());
                //set current function to be the function
                currentFunction = func;
                break;
            }
            case 17: {
                //create a procedure entry out of the current token
                ProcedureEntry id = new ProcedureEntry(token.getValue());
                //insert the procedure into global table
                globalTable.insert(token.getValue(), id);
                //push the id onto the stack
                semanticStack.push(id);
                //set scope to local
                global = false;
                //clear the local table
                localTable = new SymbolTable(tableSize);
                //set local memory to 0
                localMemory = 0;
                break;
            }
            case 19: {
                //add 0 to be the top of parm count
                parmCount.add(0);
                break;
            }
            case 20: {
                //get the number of parameters front he top of the parm stack
                int parm = parmCount.pop();
                //look up the current procedure in the global table
                //ProcedureEntry id = (ProcedureEntry) globalTable.lookUp(token.getValue());
                //FunctionEntry id = (FunctionEntry) semanticStack.firstElement();
                ProcedureEntry pid = new ProcedureEntry();
                FunctionEntry fid = new FunctionEntry();
                if (((SymbolTableEntry) semanticStack.firstElement()).isFunction()) {
                    fid = (FunctionEntry) semanticStack.firstElement();
                    fid.numParam = parm;
                }
                if (((SymbolTableEntry) semanticStack.firstElement()).isProcedure()) {
                    pid = (ProcedureEntry) semanticStack.firstElement();
                    pid.numParam = parm;
                }
                //set the number of parameters to be the top of the param stack
                break;
            }
            case 21: {
//                /**
//                 * FuncEntry n: sum, t: identifier
//                 * a, identifier
//                 * b, identifier
//                 * integer,integer
//                 */
//                Token type = (Token) semanticStack.pop();
//                ProcedureEntry id = (ProcedureEntry) semanticStack.firstElement();
////                if (((SymbolTableEntry)semanticStack.firstElement()).isArray()) {
////                    id = (ArrayEntry) semanticStack.firstElement();
////                } else if (((SymbolTableEntry)semanticStack.firstElement()).isFunction()) {
////                    id = (FunctionEntry) semanticStack.firstElement();
////                }
//                while (((SymbolTableEntry)semanticStack.peek()).getType() == TokenType.IDENTIFIER) {
//                    Token t = (Token) semanticStack.pop();
//                    id.p.addParam(t);
//                    if (isArray) {
//                       // globalTable.insert(null, type);
//                    } else {
//
//                    }
//                  //  id.setAddress(localMemory);
//                  // localMemory++;
//                }
//                isArray = false;
                /*
                 * Incomplete
                 */
                break;
            }
            case 22: {
                //get the etype
                ETYPE e = (ETYPE) semanticStack.pop();
                //if the etyp isn't relational, there is an error
                if (e != ETYPE.RELATIONAL) {
                    System.out.println("ERROR: Action 22. ETYPE not relational");
                }
                //backpatch the list for true label values to be the current 
                //quad index
                ETRUE = (ArrayList<Integer>) semanticStack.pop();
                backpatch(ETRUE, nextQuad);
                break;
            }
            case 24: {
                //begin the loop at the current index in quadruple
                beginLoop = nextQuad;
                //push this index onto the stack
                semanticStack.push(beginLoop);
                break;
            }
            case 25: {
                //pop the etype
                ETYPE e = (ETYPE) semanticStack.pop();
                //if the etype is not relational, there is an error
                if (e != ETYPE.RELATIONAL) {
                    System.out.println("ERROR ACTION 25: ETYPE is not relational.");
                }
                //backpatch the list for true label values to be 
                //the current quad index
                backpatch(ETRUE, nextQuad);
                break;
            }
            case 26: {
                //pop the top, it doesn't matter what it is
                semanticStack.pop();
                //pop efalse from the stack
                EFALSE = (ArrayList<Integer>) semanticStack.pop();
                //pop the integer of where to begin the loop
                beginLoop = (int) semanticStack.pop();
                //create quadruple for TVI code for branching
                gen("goto", Integer.toString(beginLoop));
                break;
            }
            case 27: {
                //set the list of quadruple numbers to be a new list of next quad
                skip_else = makeList(nextQuad);
                //push this list onto the stack
                semanticStack.push(skip_else);
                //create a goto statement, to be filled in later
                gen("goto", "_");
                //backpatch the false list with the current quad index
                backpatch(EFALSE, nextQuad);
                break;
            }
            case 28: {
                //pop the list of quadruple numbers for back patching
                skip_else =
                        (ArrayList<Integer>) semanticStack.pop();
                //pop EFALSE and ETRUE list, we don't really care what they are
                semanticStack.pop();
                semanticStack.pop();
                //backpatch the skip else list with next quad
                backpatch(skip_else, nextQuad);
                break;
            }
            case 29: {
                //pop the top list of efalse values
                EFALSE = (ArrayList<Integer>) semanticStack.pop();
                //pop etrue
                semanticStack.pop();
                //backpatch the efalse list with next quad
                backpatch(EFALSE, nextQuad);
                break;
            }
            case 30: {
                //if in global scrope
                if (global) {
                    //if there is no entry in the global table, there's an error
                    if (globalTable.lookUp(token.getValue()) == null) {
                        System.out.println("ERROR ACTION 30: Undeclared variable");
                    } //else push the SymbolTableEntry look up and push
                    //ETYPE of arithmetic
                    else {
                        semanticStack.push(globalTable.lookUp(token.getValue()));
                        semanticStack.push(ETYPE.ARITHEMTIC);
                    }
                } //else in local scope
                else {
                    //if there is no entry in the local table, there is an error
                    if (localTable.lookUp(token.getValue()) == null) {
                        System.out.println("ERROR ACTION 30: Undeclared variable");
                    } //else push the look up symbol table entry and push
                    //etype arithmetic
                    else {
                        semanticStack.push(localTable.lookUp(token.getValue()));
                        semanticStack.push(ETYPE.ARITHEMTIC);
                    }
                }
                break;
            }
            case 31: {
                //pop the top etype
                ETYPE e1 = (ETYPE) semanticStack.pop();
                //if the etype is not arithmetic there is an error
                if (e1.compareTo(ETYPE.ARITHEMTIC) != 0) {
                    System.out.println("ACTION 31: ERROR. ETYPE is not arithmetic.");
                }
                //initiaize a prefix used for gen functions
                String prefix = "";
                //Token id1 = (Token) semanticStack.pop();
                //create a blank symboltableentry
                SymbolTableEntry id1Entry = new SymbolTableEntry();
                //create blank token
                Token id1 = new Token();
                //if the element on the stack is a constant
                if (((SymbolTableEntry) semanticStack.peek()).isConstant()) {
                    //pop the entry as a constant entry
                    id1Entry = (ConstantEntry) semanticStack.pop();
                    //set the token to be from id1
                    id1 = new Token(id1Entry.getType(), id1Entry.getName());
                    //else if the element on the stack is a variable
                } else if (((SymbolTableEntry) semanticStack.peek()).isVariable()) {
                    //pop the entry to be a variable entry
                    id1Entry = (VariableEntry) semanticStack.pop();
                    //set the token to be the variable entry
                    id1 = new Token(id1Entry.getType(), id1Entry.getName());
                }
                //new VariableEntry(id1.getValue(), id1.getType());
                //make a blank offset
                int offset = 0;
                //if the top of the stack is a symboltableentry
                if (semanticStack.peek() instanceof SymbolTableEntry) {
                    //if it's a constant
                    if (((SymbolTableEntry) semanticStack.peek()).isConstant()) {
                        //pop off the top as a constant entry
                        ConstantEntry c = (ConstantEntry) semanticStack.pop();
                        //set the offset to be the address of the constant (the value
                        //of the constant entry)
                        offset = c.getAddress();
                        //else if it's a variable entry
                    }
                    if (((SymbolTableEntry) semanticStack.peek()).isVariable()) {
                        //pop off stop as variable entry
                        VariableEntry v = (VariableEntry) semanticStack.pop();
                        //set the offset to be the address
                        offset = v.getAddress();
                    }
                } //else the offset was pushed on as an integer
                if (semanticStack.peek() instanceof Integer) {
                    //set the offset to be the top
                    offset = (int) semanticStack.pop();
                }
                //pop the etype
                ETYPE e2 = (ETYPE) semanticStack.pop();
                //create a blank symbol table entry 
                SymbolTableEntry id2Entry = new SymbolTableEntry();
                //if the element on top is an array
                if (((SymbolTableEntry) semanticStack.peek()).isArray()) {
                    //pop off the top as an array entry
                    id2Entry = (ArrayEntry) semanticStack.pop();
                    //if the top is a variable entry
                } else if (((SymbolTableEntry) semanticStack.peek()).isVariable()) {
                    //pop it off as a variable entry
                    id2Entry = (VariableEntry) semanticStack.pop();
                    //else if it's a constant
                } else if (((SymbolTableEntry) semanticStack.peek()).isConstant()) {
                    //pop off as a constant entry
                    id2Entry = (ConstantEntry) semanticStack.pop();
                }
                // VariableEntry id2Entry = (VariableEntry) semanticStack.pop();
                //create a token of the second symbol table entry
                Token id2 = new Token(id2Entry.getType(), id2Entry.getName());

                //if in global scope
                if (global) {
                    //set the prefix to indicate global
                    prefix = "_";
                    //id2Entry =
                    //(VariableEntry) globalTable.lookUp(id2.getValue());
                    //if the current token is a constant
                    if (token.getType() == TokenType.INTCONSTANT
                            || token.getType() == TokenType.REALCONSTANT) {
                        //create a separate move code
                        VariableEntry e = create(temp(), token.getType());
                        gen("move", token.getValue(), prefix + e.getAddress());
                        //increase the entry addresses by one since 
                        //there is now an entry beneath the two ids
                      //  id2Entry.setAddress(id2Entry.getAddress() + 1);
                      //  id1Entry.setAddress(id1Entry.getAddress() + 1);
                    }
                    //else in local scope
                } else {
                    //set prefix to indicate local 
                    prefix = "%";
                    //id2Entry =
                    //(VariableEntry) localTable.lookUp(token.getValue());
                    //if the token is a constant
                    if (token.getType() == TokenType.INTCONSTANT
                            || token.getType() == TokenType.REALCONSTANT) {
                        //create a unique move code
                        VariableEntry e = create(temp(), token.getType());
                        gen("move", token.getValue(), prefix + e.getAddress());
                        //increase id1 and id2 entry by one since now there 
                        //is an entry beneath them
                      //  id2Entry.setAddress(id2Entry.getAddress() + 1);
                      //  id1Entry.setAddress(id1Entry.getAddress() + 1);
                    }
                }
                //if the type check of the two tokens is 3, there is an error
                if (TypeCheck(id1, id2) == 3) {
                    System.out.println("ACTION 31: ERROR. typeCheck is 3.");
                }
                //if typecheck is 2
                if (TypeCheck(id1, id2) == 2) {
                    //create a temp variable entry, with type real
                    VariableEntry temp = create(temp(), TokenType.REAL);
                    //generate long to float code
                    gen("ltof", Integer.toString(temp.getAddress()), "$$" + temp.getName());
                    //if the offset is 0S
                    if (offset == 0) {
                        //create move code
                        gen("move", "$$" + temp.getName(),
                                prefix + Integer.toString(id1Entry.getAddress()));
                    } else {
                        //else create store code
                        gen("stor", "$$" + temp.getName(),
                                prefix + Integer.toString(offset),
                                prefix + Integer.toString(id1Entry.getAddress()));
                    }
                } //else if the offset is 0, but the type check is not 3 or 2
                else if (offset == 0) {
                    //create move code
                    gen("move", prefix + Integer.toString(id2Entry.getAddress()),
                            prefix + Integer.toString(id1Entry.getAddress()));
                } //else create store code with offset
                else {
                    gen("stor", prefix + Integer.toString(id2Entry.getAddress()),
                            prefix + Integer.toString(offset),
                            prefix + Integer.toString(id1Entry.getAddress()));
                }
                break;
            }
            case 32: {
                //look up the id in the global table
                SymbolTableEntry id = globalTable.lookUp(token.getValue());
                //if the id isn't an array, there is an error
                if (!id.isArray()) {
                    System.out.println("ACTION 32: ID is not array");
                }
                break;
            }
            case 33: {
                //pop the etype
                ETYPE e = (ETYPE) semanticStack.pop();
                //if the type is not arithmetic, there is an error
                if (e != ETYPE.ARITHEMTIC) {
                    System.out.println("ACTION 33 ERROR. ETYPE is not arithmetic.");
                }
                //if the top of the stack is not an integer type there is an error
                if (((SymbolTableEntry) semanticStack.peek()).getType() != TokenType.INTEGER) {
                    System.out.println("ACTION 33 ERROR. id type is not integer");
                }
                //create a temp variable entry with type integer
                VariableEntry v = create(temp(), TokenType.INTEGER);
                //pop the symbol table entry 
                SymbolTableEntry temp1 = (SymbolTableEntry) semanticStack.pop();
                //pop an etype
                ETYPE et = (ETYPE) semanticStack.pop();
                //pop the array entry
                ArrayEntry bottom = (ArrayEntry) semanticStack.pop();
                //gen code for sub using array lower bound
                gen("sub", "$$" + temp1.getName(),
                        Integer.toString(bottom.getLowerBound()),
                        temp1.getName());
                //push the temp variable entry
                semanticStack.push(v);
                break;
            }
            case 34: {
                //if the tope of the stack is a function, call action 52 
                if (((SymbolTableEntry) semanticStack.firstElement()).isFunction()) {
                    Execute(SemanticAction.action52, token);
                } else {
                    //and push offset as 0
                    semanticStack.push(0);
                }
                break;
            }
            case 35: {
                //look up the procedure entry in the global table
                ProcedureEntry proc = (ProcedureEntry) globalTable.lookUp(token.getValue());
                //add 0 parameter count to top of stack
                parmCount.add(0);
                //set next parameter to be the procedure's parameter info
                nextParam.add(proc.p);
                break;
            }
            case 36: {
                //pop etype
                semanticStack.pop();
                //po id
                ProcedureEntry id = (ProcedureEntry) semanticStack.pop();
                //if then numer of parameters is not zero, there is an error
                if (id.numParam != 0) {
                    System.out.println("ACTION 36 ERROR. id.numParam is not 0");
                }
                // how to get ID address
                gen("call", id.getName(), "0");
                break;
            }
            case 37: {
//                //pop etype
//                ETYPE e = (ETYPE) semanticStack.pop();
//                //if the etype isn't arithmetic, there is an error
//                if (e != ETYPE.ARITHEMTIC) {
//                    System.out.println("ACTION 37: ERROR ETYPE is not arithmetic.");
//                } 
//                //create a blank symbol table entry
//                SymbolTableEntry id = new SymbolTableEntry();
//                //if the top of the stack is an array
//                if (((SymbolTableEntry) semanticStack.peek()).isArray()) {
//                    //pop off top as array entry
//                    id = (ArrayEntry) semanticStack.pop();
//                } else {
//                    //else pop off as array entry
//                    id = (SymbolTableEntry) semanticStack.pop();
//                }
//                //if the id is not an array, id, function result, or variable
//                //there is an error
//                if (!id.isArray() || !id.isConstant() || !id.isFunctionResult() || 
//                        !id.isVariable()) {
//                    System.out.println
//                            ("ACTION 37: ERROR id is not a variable, array, " +""
//                            + "constant or function result");
//                }
//                //pop the top of the parm count
//                int top = parmCount.pop();
//                //increase the top
//                top++;
//                //push back onto stack
//                parmCount.push(top);
//                //if the bottom of the stack is not read or write
//                if (!(((SymbolTableEntry)semanticStack.firstElement()).getName().equals("read") ||
//                        ((SymbolTableEntry)semanticStack.firstElement()).getName().equals("write"))) {
//                    //if the parm count is greater than the 
//                    //procedure/function num of param, there is an error
//                    if (parmCount.lastElement() > ((SymbolTableEntry)semanticStack.firstElement()).numParam()) {
//                        System.out.println("ACTION 36: ERROR. Top of parmCount is greater than the number of parameters");
//                    }
//                    //if the id type doesn't match the top of param type there is an error
//                    if (id.getType() != nextParam.peek().getType()) {
//                        System.out.println("ACTION 37: ERROR. Next Param is not equal to ID type.");
//                    }
//                    if (nextParam.peek().isArray()) {
//                       ParameterInfo p = nextParam.pop();
//                       Token lbound = p.getParam(0);
//                       Token ubound = p.getParam(1);
//                       
//                    }
//                }
//                nextParam.push()
                /*
                 * Incomplete
                 */
                break;
            }
            case 38: {
                //pop etype
                ETYPE e = (ETYPE) semanticStack.pop();
                //if etype is not arithmetic, there is an error
                if (e != ETYPE.ARITHEMTIC) {
                    System.out.println("ERORR: ETYPE is not arithmetic.");
                }
                //push the operater onto stack
                semanticStack.push(token);
                break;
            }
            case 39: {
                //pop etype
                ETYPE e = (ETYPE) semanticStack.pop();
                //if etype is not arithmetic, there is an error
                if (e != ETYPE.ARITHEMTIC) {
                    System.out.println("ERROR: ETYPE is not arithmetic.");
                }
                //create blank prefix
                String prefix = "";
                //create blank symbol table entry for id1
                SymbolTableEntry id1Entry = new SymbolTableEntry();
                //create blank symbol table entry for id2
                SymbolTableEntry id2Entry = new SymbolTableEntry();
                //blank id1 token
                Token id1 = new Token();
                //blank id2 token
                Token id2 = new Token();
                if (((SymbolTableEntry) semanticStack.peek()).isVariable()) {
                    id1Entry = (VariableEntry) semanticStack.pop();
                } else if (((SymbolTableEntry) semanticStack.peek()).isConstant()) {
                    id1Entry = (ConstantEntry) semanticStack.pop();
                }
                //pop operator, value will be <=, >= etc.
                Token operator = (Token) semanticStack.pop();
                if (((SymbolTableEntry) semanticStack.peek()).isVariable()) {
                    id2Entry = (VariableEntry) semanticStack.pop();
                } else if (((SymbolTableEntry) semanticStack.peek()).isConstant()) {
                    id2Entry = (ConstantEntry) semanticStack.pop();
                }
                id1 = new Token(id1Entry.getType(), id1Entry.getName());
                id2 = new Token(id2Entry.getType(), id2Entry.getName());
                //if global set prefix to indicate global
                if (global) {
                    prefix = "_";
                } else {
                    //else set prefix to indicate local
                    prefix = "%";
                }
                //if type check against two tokens is 2
                if (TypeCheck(id1, id2) == 2) {
                    //create a temp variable entry with type real
                    VariableEntry temp = create(temp(), TokenType.REAL);
                    //generate code based on operator
                    gen("ltof", prefix + Integer.toString(id2Entry.getAddress()),
                            "$$" + temp.getName());
                    //branch if less than
                    if (operator.getValue().equals("<")) {
                        gen("blt", prefix + Integer.toString(id1Entry.getAddress()),
                                "$$" + temp.getName(), "_");
                    }
                    //branch if less than or equal to
                    if (operator.getValue().equals("<=")) {
                        gen("ble", prefix + Integer.toString(id1Entry.getAddress()),
                                "$$" + temp.getName(), "_");
                    }
                    //branch if greater than
                    if (operator.getValue().equals(">")) {
                        gen("bgt", prefix + Integer.toString(id1Entry.getAddress()),
                                "$$" + temp.getName(), "_");
                    }
                    //branch if greater or equal to
                    if (operator.getValue().equals(">=")) {
                        gen("bge", prefix + Integer.toString(id1Entry.getAddress()),
                                "$$" + temp.getName(), "_");
                    }
                    //branch of equal to
                    if (operator.getValue().equals("=")) {
                        gen("beq", prefix + Integer.toString(id1Entry.getAddress()),
                                "$$" + temp.getName(), "_");
                    }
                    //branch if not equal to
                    if (operator.getValue().equals("<>")) {
                        gen("bne", prefix + Integer.toString(id1Entry.getAddress()),
                                "$$" + temp.getName(), "_");
                    }
                }
                //if type check between two tokens is 3
                if (TypeCheck(id1, id2) == 3) {
                    //create temp cariable entry
                    VariableEntry temp = create(temp(), TokenType.REAL);
                    gen("ltof", "$$" + temp.getName(),
                            prefix + Integer.toString(id2Entry.getAddress()));
                    //generate code based on operator
                    //branch if less than
                    if (operator.getValue().equals("<")) {
                        gen("blt", "$$" + temp.getName(),
                                prefix + Integer.toString(id2Entry.getAddress()),
                                "_");
                    }
                    //branch if less than or equal to
                    if (operator.getValue().equals("<=")) {
                        gen("ble", "$$" + temp.getName(),
                                prefix + Integer.toString(id2Entry.getAddress()),
                                "_");
                    }
                    //branch if greater than
                    if (operator.getValue().equals(">")) {
                        gen("bgt", "$$" + temp.getName(),
                                prefix + Integer.toString(id2Entry.getAddress()),
                                "_");
                    }
                    //branch if greater or equal
                    if (operator.getValue().equals(">=")) {
                        gen("bge", "$$" + temp.getName(),
                                prefix + Integer.toString(id2Entry.getAddress()),
                                "_");
                    }
                    //branch if equal
                    if (operator.getValue().equals("=")) {
                        gen("beq", "$$" + temp.getName(),
                                prefix + Integer.toString(id2Entry.getAddress()),
                                "_");
                    }
                    //branch if not equal
                    if (operator.getValue().equals("<>")) {
                        gen("bne", "$$" + temp.getName(),
                                prefix + Integer.toString(id2Entry.getAddress()),
                                "_");
                    }
                } else {
                    //else generate code based on operator
                    //branch if less than
                    if (operator.getValue().equals("<")) {
                        gen("blt", prefix + Integer.toString(id1Entry.getAddress()),
                                prefix + Integer.toString(id2Entry.getAddress()),
                                "_");
                    }
                    //branch if less than or equal to
                    if (operator.getValue().equals("<=")) {
                        gen("ble", prefix + Integer.toString(id1Entry.getAddress()),
                                prefix + Integer.toString(id2Entry.getAddress()),
                                "_");
                    }
                    //branch if greater than
                    if (operator.getValue().equals(">")) {
                        gen("bgt", prefix + Integer.toString(id1Entry.getAddress()),
                                prefix + Integer.toString(id2Entry.getAddress()),
                                "_");
                    }
                    //branch if greater or equal to
                    if (operator.getValue().equals(">=")) {
                        gen("bge", prefix + Integer.toString(id1Entry.getAddress()),
                                prefix + Integer.toString(id2Entry.getAddress()),
                                "_");
                    }
                    //branch if equal
                    if (operator.getValue().equals("=")) {
                        gen("beq", prefix + Integer.toString(id1Entry.getAddress()),
                                prefix + Integer.toString(id2Entry.getAddress()),
                                "_");
                    }
                    //branch if not equal
                    if (operator.getValue().equals("<>")) {
                        gen("bne", prefix + Integer.toString(id1Entry.getAddress()),
                                prefix + Integer.toString(id2Entry.getAddress()),
                                "_");
                    }
                }
                //make a list for quad index minus 2
                ArrayList<Integer> etru = makeList(nextQuad - 2);
                //make a list for quad index minus 1
                ArrayList<Integer> efal = makeList(nextQuad - 1);
                //push the lists
                semanticStack.push(etru);
                semanticStack.push(efal);
                //push etype relational since we are dealing with branches
                semanticStack.push(ETYPE.RELATIONAL);
                break;
            }
            case 40: {
                //push sign
                semanticStack.push(token);
                break;
            }
            case 41: {
                //pop etype
                ETYPE e = (ETYPE) semanticStack.pop();
                //if etype is not arithmetic, there is an error
                if (e.compareTo(e.ARITHEMTIC) != 0) {
                    System.out.println("ACTION 41: ERROR. ETYPE is not arithmetic.");
                }

                //pop sign off of stack
                Token sign = (Token) semanticStack.pop();
                //pop id off of stack
                Token id = (Token) semanticStack.pop();
                //if the sign is a unary minus
                if (sign.getType().equals(TokenType.UNARYMINUS)) {
                    //create a temp
                    VariableEntry temp = create(temp(), id.getType());
                    //generate uminus code
                    gen("uminus",
                            Integer.toString(temp.getAddress()),
                            "$$" + temp.getName());
                    String name = "$$" + temp.getName();
                    //set the temp name to be a new name
                    temp.setName(name);
                    //push the temp variable
                    semanticStack.push(temp);
                } else {
                    //else push the id
                    semanticStack.push(id);
                }
                //push arithmetic etype
                semanticStack.push(ETYPE.ARITHEMTIC);
                break;
            }
            case 42: {
                //pop etype 
                ETYPE e = (ETYPE) semanticStack.pop();
                //if the token is or
                if (token.getValue().toLowerCase().equals("or")) {
                    //if the etype is not relational, error
                    if (e.compareTo(ETYPE.RELATIONAL) != 0) {
                        System.out.println("ACTION 42: ERROR. ETYPE not relational.");
                    }
                    //backpatch efalse with the next quad index
                    backpatch(EFALSE, nextQuad);
                } else {
                    //else if the arithmetic is not 0, there is an error
                    if (e.compareTo(ETYPE.ARITHEMTIC) != 0) {
                        System.out.println("ACTION 42: ERROR. ETYPE is not arithmetic.");
                    }
                }
                //push the token
                semanticStack.push(token);
                break;
            }
            case 43: {
                //pop etype 
                ETYPE e = (ETYPE) semanticStack.pop();
                //if etype is original and the token is "or"
                if (e == ETYPE.RELATIONAL) {
                    if (token.getValue().toLowerCase().equals("or")) {
                        //pop the etrue and efalse lists on the stack
                        ArrayList<Integer> e1true =
                                (ArrayList<Integer>) semanticStack.pop();
                        ArrayList<Integer> e2true =
                                (ArrayList<Integer>) semanticStack.pop();
                        ArrayList<Integer> e1false =
                                (ArrayList<Integer>) semanticStack.pop();
                        ArrayList<Integer> e2false =
                                (ArrayList<Integer>) semanticStack.pop();
                        //pop etype
                        ETYPE et = (ETYPE) semanticStack.pop();
                        //merge the lists and store into etrue
                        ETRUE = mergeLists(e1true, e2true);
                        //set efalse list to be the popped list
                        EFALSE = e2false;
                        //push the lists and etype relational on
                        semanticStack.push(ETRUE);
                        semanticStack.push(EFALSE);
                        semanticStack.push(ETYPE.RELATIONAL);
                    }
                } //else if e type is not arithmetic, there is an error
                else if (e != ETYPE.ARITHEMTIC) {
                    System.out.println("ACTION 43: ERROR. ETYPE is not arithmetic.");
                }
                //pop the variable entry
                VariableEntry id2Entry = (VariableEntry) semanticStack.pop();
                //pop the operator
                Token op = (Token) semanticStack.pop();
                //pop the variable entry
                VariableEntry id1Entry = (VariableEntry) semanticStack.pop();
                //create token 1 and 2 from the variable entries
                Token id1 = new Token(id1Entry.getType(), id1Entry.getName());
                Token id2 = new Token(id2Entry.getType(), id2Entry.getName());
                String prefix = "";
                /**
                 * GLOBAL/LOCAL
                 */
                if (global) {
                    //set prefix to indicate global
                    prefix = "_";
                    //if the token is a constant 
                    if (token.getType() == TokenType.INTCONSTANT
                            || token.getType() == TokenType.REALCONSTANT) {
                        //create a move code, and increment id1 and id2 address
                        //since now there s something beneath
                        VariableEntry v = create(temp(), token.getType());
                        gen("move", token.getValue(), prefix + v.getAddress());
                       // id2Entry.setAddress(id2Entry.getAddress() + 1);
                       // id1Entry.setAddress(id1Entry.getAddress() + 1);
                    }
                } else {
                    //set prefix to indicate local
                    prefix = "%";
                    if (token.getType() == TokenType.INTCONSTANT
                            || token.getType() == TokenType.REALCONSTANT) {
                        //create a move code, and increment id1 and id2 address
                        //since now there s something beneath
                        VariableEntry v = create(temp(), token.getType());
                        gen("move", token.getValue(), prefix + v.getAddress());
                        id2Entry.setAddress(id2Entry.getAddress() + 1);
                        id1Entry.setAddress(id1Entry.getAddress() + 1);
                    }
                }
                //TYPECHECK == 0
                if (TypeCheck(id1, id2) == 0) {
                    //create variable entry temp
                    VariableEntry temp = create(temp(), TokenType.INTEGER);
                    //if operator is +, add
                    if (op.getValue().equals("+")) {
                        gen("add", prefix + Integer.toString(id1Entry.getAddress()),
                                "_" + Integer.toString(id2Entry.getAddress()),
                                Integer.toString(temp.getAddress()));
                    }
                    //if operator is -, subtract
                    if (op.getValue().equals("-")) {
                        gen("sub", prefix + Integer.toString(id1Entry.getAddress()),
                                prefix + Integer.toString(id2Entry.getAddress()),
                                "$$" + Integer.toString(temp.getAddress()));
                    }
                    // Token result = new Token(temp.getType(), temp.getName());
                    semanticStack.push(temp);
                } //TYPECHECK == 1
                else if (TypeCheck(id1, id2) == 1) {
                    //create variable entry temp of real
                    VariableEntry temp = create(temp(), TokenType.REAL);
                    //if operator is +, float add 
                    if (op.getValue().equals("+")) {
                        gen("fadd", prefix + Integer.toString(id1Entry.getAddress()),
                                prefix + Integer.toString(id2Entry.getAddress()),
                                "$$" + Integer.toString(temp.getAddress()));
                    }
                    //if operator is -, float sub
                    if (op.getValue().equals("-")) {
                        gen("fsub", prefix + Integer.toString(id1Entry.getAddress()),
                                prefix + Integer.toString(id2Entry.getAddress()),
                                "$$" + Integer.toString(temp.getAddress()));
                    }
                    //Token result = new Token(temp.getType(), temp.getName());
                    semanticStack.push(temp);
                } //TYPECHECK == 2
                else if (TypeCheck(id1, id2) == 2) {
                    //create temp of real
                    VariableEntry temp1 = create(temp(), TokenType.REAL);
                    gen("ltof", prefix + Integer.toString(id2Entry.getAddress()),
                            "$$" + Integer.toString(temp1.getAddress()));
                    //push result
                    semanticStack.push(temp1);
                    //create temp variable
                    VariableEntry temp2 = create(temp(), TokenType.REAL);
                    //if operator is +, fload add
                    if (op.getValue().equals("+")) {
                        gen("fadd", prefix + Integer.toString(id1Entry.getAddress()),
                                "$$" + Integer.toString(temp1.getAddress()),
                                "$$" + Integer.toString(temp2.getAddress()));
                    }
                    //if operator is -, float subtract
                    if (op.getValue().equals("-")) {
                        gen("fsub", prefix + Integer.toString(id1Entry.getAddress()),
                                "$$" + Integer.toString(temp1.getAddress()),
                                "$$" + Integer.toString(temp2.getAddress()));
                    }
                    //  Token result = new Token(temp2.getType(), temp2.getName());
                    semanticStack.push(temp2);
                } //TYPECHECK == 3
                else if (TypeCheck(id1, id2) == 3) {
                    //create temp variable
                    VariableEntry temp1 = create(temp(), TokenType.REAL);
                    gen("ltof", "_" + Integer.toString(id1Entry.getAddress()),
                            "$$" + Integer.toString(temp1.getAddress()));
                    semanticStack.push(temp1);
                    VariableEntry temp2 = create(temp(), TokenType.REAL);
                    //if operator is +, float add
                    if (op.getValue().equals("+")) {
                        gen("fadd", Integer.toString(temp1.getAddress()),
                                prefix + Integer.toString(id2Entry.getAddress()),
                                "$$" + Integer.toString(temp2.getAddress()));
                    }
                    //if operator is -, float subtract
                    if (op.getValue().equals("-")) {
                        gen("fsub", "$$" + Integer.toString(temp1.getAddress()),
                                prefix + Integer.toString(id2Entry.getAddress()),
                                "$$" + Integer.toString(temp2.getAddress()));
                    }
                    // Token result = new Token(temp2.getType(), temp2.getName());
                    semanticStack.push(temp2);
                }
                //push arithmetic
                semanticStack.push(ETYPE.ARITHEMTIC);
                break;
            }
            case 44: {
                //pop etype
                ETYPE e = (ETYPE) semanticStack.pop();
                //if etop is relational
                if (e.compareTo(e.RELATIONAL) == 0) {
                    //if the operator value is and
                    if (token.getValue().toLowerCase().equals("and")) {
                        //backpatch etrue with quad
                        backpatch(ETRUE, nextQuad);
                    }
                }
                //push operator
                semanticStack.push(token);
                break;
            }
            case 45: {
                if (token.getValue().toLowerCase().equals("add")) {
                    ETYPE etype = (ETYPE) semanticStack.pop();
                    //if etype is original and the token is "or"
                    if (etype != ETYPE.RELATIONAL) {
                        System.out.println("Action 45 ERROR. etype is not relational.");
                    }
                    //pop the etrue and efalse lists on the stack
                    ArrayList<Integer> e1true =
                            (ArrayList<Integer>) semanticStack.pop();
                    ArrayList<Integer> e1false =
                            (ArrayList<Integer>) semanticStack.pop();
                    Token operator = (Token) semanticStack.pop();
                    ArrayList<Integer> e2true =
                            (ArrayList<Integer>) semanticStack.pop();
                    ArrayList<Integer> e2false =
                            (ArrayList<Integer>) semanticStack.pop();
                    //pop etype
                    ETYPE et = (ETYPE) semanticStack.pop();
                    //store into etrue
                    ETRUE = e2true;
                    //merge lsits into efalse
                    EFALSE = mergeLists(e1false, e2false);
                    //push the lists and etype relational on
                    semanticStack.push(ETRUE);
                    semanticStack.push(EFALSE);
                    semanticStack.push(ETYPE.RELATIONAL);

                } else {
                    //pop etype
                    ETYPE et = (ETYPE) semanticStack.pop();
                    //create blank entry
                    SymbolTableEntry id1Entry = new SymbolTableEntry();
                    //creat blank token
                    Token id1 = new Token();
                    //check if the top of the stack is a constant or variable,
                    //and set entries accordingly
                    if (((SymbolTableEntry) semanticStack.peek()).isConstant()) {
                        id1Entry = (ConstantEntry) semanticStack.pop();
                        id1 = new Token(id1Entry.getType(), id1Entry.getName());
                    } else if (((SymbolTableEntry) semanticStack.peek()).isVariable()) {
                        id1Entry = (VariableEntry) semanticStack.pop();
                        id1 = new Token(id1Entry.getType(), id1Entry.getName());
                    }
                    //pop the operator
                    Token op = (Token) semanticStack.pop();
                    //check if the top of the stack is a constant or variable,
                    //and set the entries accordingly
                    SymbolTableEntry id2Entry = new SymbolTableEntry();
                    Token id2 = new Token();
                    if (((SymbolTableEntry) semanticStack.peek()).isConstant()) {
                        id2Entry = (ConstantEntry) semanticStack.pop();
                        id2 = new Token(id2Entry.getType(), id2Entry.getName());
                    } else if (((SymbolTableEntry) semanticStack.peek()).isVariable()) {
                        id2Entry = (VariableEntry) semanticStack.pop();
                        id2 = new Token(id2Entry.getType(), id2Entry.getName());
                    }
                    //blank prefix
                    String prefix = "";
                    /**
                     * GLOBAL/LOCAL
                     */
                    if (global) {
                        //set prefix to indicate global
                        prefix = "_";
                        //check if token is contant
                        if (token.getType() == TokenType.INTCONSTANT
                                || token.getType() == TokenType.REALCONSTANT) {
                            //create move code
                            VariableEntry e = create(temp(), token.getType());
                            gen("move", token.getValue(), prefix + e.getAddress());
                            //adjust addresses
                            id1Entry.setAddress(id1Entry.getAddress() + 1);
                            id2Entry.setAddress(id2Entry.getAddress() + 1);
                        }
                    } else {
                        //set prefix to indicate local
                        prefix = "%";
                        //check if token is constant
                        if (token.getType() == TokenType.INTCONSTANT
                                || token.getType() == TokenType.REALCONSTANT) {
                            //generate move code
                            VariableEntry e = create(temp(), token.getType());
                            gen("move", token.getValue(), prefix + e.getAddress());
                            //adjust entry addresses
                            id2Entry.setAddress(id2Entry.getAddress() + 1);
                            id1Entry.setAddress(id1Entry.getAddress() + 1);
                        }
                    }
                    //if type check is not 0 and the operator is mod, there 
                    //is an error
                    if (TypeCheck(id1, id2) != 0 && op.getValue().equals("mod")) {
                        System.out.println("ERROR. MOD requires integer operands.");
                    }
                    //TYPECHECK == 0
                    if (TypeCheck(id1, id2) == 0) {
                        //IF OP == MOD
                        if (op.getValue().equals("mod")) {
                            VariableEntry temp1 = create(temp(), TokenType.INTEGER);
                            gen("move", prefix + Integer.toString(id1Entry.getAddress()),
                                    Integer.toString(temp1.getAddress()));
                            VariableEntry temp2 = create(temp(), TokenType.INTEGER);
                            gen("move", Integer.toString(temp1.getAddress()),
                                    Integer.toString(temp2.getAddress()));
                            gen("sub", Integer.toString(temp2.getAddress()),
                                    prefix + Integer.toString(id2Entry.getAddress()),
                                    Integer.toString(temp1.getAddress()));
                            gen("bge", Integer.toString(temp1.getAddress()),
                                    prefix + Integer.toString(id2Entry.getAddress()),
                                    Integer.toString(nextQuad - 2));
                            //Token result = new Token(temp1.getType(), temp1.getName());
                            semanticStack.push(temp1);
                        } //ELSE IF OP == /
                        else if (op.getValue().equals("/")) {
                            VariableEntry temp1 = create(temp(), TokenType.REAL);
                            gen("ltof", prefix + Integer.toString(id1Entry.getAddress()),
                                    Integer.toString(temp1.getAddress()));
                            VariableEntry temp2 = create(temp(), TokenType.REAL);
                            gen("ltof", prefix + Integer.toString(id2Entry.getAddress()),
                                    prefix + Integer.toString(temp2.getAddress()));
                            VariableEntry temp3 = create(temp(), TokenType.REAL);
                            gen("fdiv", prefix + Integer.toString(temp1.getAddress()),
                                    prefix + Integer.toString(temp2.getAddress()),
                                    prefix + Integer.toString(temp3.getAddress()));
                            // Token result = new Token(temp3.getType(), temp3.getName());
                            semanticStack.push(temp3);
                        } else {
                            VariableEntry temp1 = create(temp(), TokenType.INTEGER);
                            //div
                            if (op.getValue().equals("div")) {
                                gen("div", prefix + Integer.toString(id1Entry.getAddress()),
                                        prefix + Integer.toString(id2Entry.getAddress()),
                                        "$$" + Integer.toString(temp1.getAddress()));
                                // Token result = new Token(temp1.getType(), temp1.getName());
                                semanticStack.push(temp1);
                            }
                            //mul
                            if (op.getValue().equals("*")) {
                                gen("mul", prefix + Integer.toString(id1Entry.getAddress()),
                                        prefix + Integer.toString(id2Entry.getAddress()),
                                        Integer.toString(temp1.getAddress()));
                                // Token result = new Token(temp1.getType(), temp1.getName());
                                semanticStack.push(temp1);
                            }
                        }
                    }
                    //TYPECHECK ==1
                    if (TypeCheck(id1, id2) == 1) {
                        if (op.getValue().equals("div")) {
                            VariableEntry temp1 = create(temp(), TokenType.INTEGER);
                            gen("ftol", prefix + Integer.toString(id1Entry.getAddress()),
                                    "$$" + Integer.toString(temp1.getAddress()));
                            VariableEntry temp2 = create(temp(), TokenType.INTEGER);
                            gen("ftol", prefix + Integer.toString(id2Entry.getAddress()),
                                    "$$" + Integer.toString(temp2.getAddress()));
                            VariableEntry temp3 = create(temp(), TokenType.INTEGER);
                            gen("div", "$$" + Integer.toString(temp1.getAddress()),
                                    "$$" + Integer.toString(temp2.getAddress()),
                                    "$$" + Integer.toString(temp3.getAddress()));
                            //Token result = new Token(temp3.getType(), temp3.getName());
                            semanticStack.push(temp3);
                        } else {
                            VariableEntry temp1 = create(temp(), TokenType.REAL);
                            //div
                            if (op.getValue().equals("div")) {
                                gen("div", prefix + Integer.toString(id1Entry.getAddress()),
                                        prefix + Integer.toString(id2Entry.getAddress()),
                                        "$$" + Integer.toString(temp1.getAddress()));
                                //Token result = new Token(temp1.getType(), temp1.getName());
                                semanticStack.push(temp1);
                            }
                            //mul
                            if (op.getValue().equals("*")) {
                                gen("mul", prefix + Integer.toString(id1Entry.getAddress()),
                                        prefix + Integer.toString(id2Entry.getAddress()),
                                        Integer.toString(temp1.getAddress()));
                                // Token result = new Token(temp1.getType(), temp1.getName());
                                semanticStack.push(temp1);
                            }
                        }
                    }
                    //TYPECHECK == 2
                    if (TypeCheck(id1, id2) == 2) {
                        if (op.getValue().equals("div")) {
                            VariableEntry temp1 = create(temp(), TokenType.INTEGER);
                            gen("ftol", prefix + Integer.toString(id1Entry.getAddress()),
                                    "$$" + Integer.toString(temp1.getAddress()));
                            VariableEntry temp2 = create(temp(), TokenType.INTEGER);
                            gen("div", "$$" + Integer.toString(temp1.getAddress()),
                                    prefix + Integer.toString(id2Entry.getAddress()),
                                    "$$" + Integer.toString(temp2.getAddress()));
                            // Token result = new Token(temp2.getType(), temp2.getName());
                            semanticStack.push(temp2);
                        } else {
                            VariableEntry temp1 = create(temp(), TokenType.REAL);
                            gen("ltof", prefix + Integer.toString(id2Entry.getAddress()),
                                    "$$" + Integer.toString(temp1.getAddress()));
                            VariableEntry temp2 = create(temp(), TokenType.REAL);
                            //div
                            if (op.getValue().equals("div")) {
                                gen("div", prefix + Integer.toString(id1Entry.getAddress()),
                                        "$$" + prefix + Integer.toString(temp1.getAddress()),
                                        "$$" + Integer.toString(temp2.getAddress()));
                                // Token result = new Token(temp2.getType(), temp2.getName());
                                semanticStack.push(temp2);
                            }
                            //mul
                            if (op.getValue().equals("*")) {
                                gen("mul", prefix + Integer.toString(id1Entry.getAddress()),
                                        "$$" + prefix + Integer.toString(temp1.getAddress()),
                                        "$$" + Integer.toString(temp2.getAddress()));
                                //Token result = new Token(temp2.getType(), temp2.getName());
                                semanticStack.push(temp2);
                            }
                        }
                    }
                    //TYPECHECK ==3
                    if (TypeCheck(id1, id2) == 3) {
                        if (op.getValue().equals("div")) {
                            VariableEntry temp1 = create(temp(), TokenType.INTEGER);
                            gen("ftol", prefix + Integer.toString(id2Entry.getAddress()),
                                    "$$" + Integer.toString(temp1.getAddress()));
                            VariableEntry temp2 = create(temp(), TokenType.INTEGER);
                            gen("div", prefix + Integer.toString(id1Entry.getAddress()),
                                    "$$" + Integer.toString(temp1.getAddress()),
                                    "$$" + Integer.toString(temp2.getAddress()));
                            //Token result = new Token(temp2.getType(), temp2.getName());
                            semanticStack.push(temp2);
                        } else {
                            VariableEntry temp1 = create(temp(), TokenType.REAL);
                            gen("ltof", prefix + Integer.toString(id1Entry.getAddress()),
                                    "$$" + Integer.toString(temp1.getAddress()));
                            VariableEntry temp2 = create(temp(), TokenType.REAL);
                            /* do i ever set anything to be div?? or mul ??
                             */
                            if (op.getValue().equals("div")) {
                                gen("div", "$$" + Integer.toString(temp1.getAddress()),
                                        prefix + Integer.toString(id2Entry.getAddress()),
                                        "$$" + Integer.toString(temp2.getAddress()));
                                //Token result = new Token(temp2.getType(), temp2.getName());
                                semanticStack.push(temp2);
                            }
                            //mul
                            if (op.getValue().equals("*")) {
                                gen("mul", "$$" + Integer.toString(temp1.getAddress()),
                                        prefix + Integer.toString(id2Entry.getAddress()),
                                        "$$" + Integer.toString(temp2.getAddress()));
                                //Token result = new Token(temp2.getType(), temp2.getName());
                                semanticStack.push(temp2);
                            }
                        }
                    }
                }
                //push etype arithmetic
                semanticStack.push(ETYPE.ARITHEMTIC);
                break;
            }
            case 46: {
                //if the token is an identifier
                if (token.getType() == TokenType.IDENTIFIER) {
                    //if in global scope
                    if (global) {
                        //look up in global table
                        if (globalTable.lookUp(token.getValue()) != null) {
                            SymbolTableEntry v = globalTable.lookUp(token.getValue());
                            //Token r = new Token(v.getType(), v.getName());
                            //push the result from the look up
                            semanticStack.push(v);
                        } else {
                            //else print an error
                            System.out.println("ERROR: Undeclared variable in global table.");
                        }
                    } else {
                        //else in local scope, look up.
                        if (localTable.lookUp(token.getValue()) != null) {
                            SymbolTableEntry v = localTable.lookUp(token.getValue());
                            //Token r = new Token(v.getType(), v.getName());
                            //push the result from the look up
                            semanticStack.push(v);
                        } else {
                            //else print an error
                            System.out.println("ERROR: Undeclared variable in local table.");
                        }
                    }
                }
                //if the token is an int of real constant
                if (token.getType() == TokenType.REALCONSTANT
                        || token.getType() == TokenType.INTCONSTANT) {
                    //look up in constant table, if not there,
                    if (constantTable.lookUp(token.getValue()) == null) {
                        //add in a new constant entry
                        ConstantEntry c =
                                new ConstantEntry(token.getValue(), token.getType());
                        //set type to be integer or real
                        if (token.getType() == TokenType.INTCONSTANT) {
                            c.setType(TokenType.INTEGER);
                        } else {
                            c.setType(TokenType.REAL);
                        }
                        //insert into constant table
                        constantTable.insert(token.getValue(), c);
                        // Token r = new Token(c.getType(), c.getName());
                        //push the constant table
                        semanticStack.push(c);
                    } else {
                        //else look up in constant table
                        SymbolTableEntry v = constantTable.lookUp(token.getValue());
                        // Token r = new Token(v.getType(), v.getName());
                        //push the look up result
                        semanticStack.push(v);
                    }
                }
                //push etype arithmetic
                semanticStack.push(ETYPE.ARITHEMTIC);
                break;
            }
            case 47: {
                //pop etrue and efalse list 
                //pop etype 
                ETYPE etrue = (ETYPE) semanticStack.pop();
                ETYPE efalse = (ETYPE) semanticStack.pop();
                ETYPE etype = (ETYPE) semanticStack.pop();
                //if etype is relational, there is an error
                if (etype == ETYPE.RELATIONAL) {
                    System.out.println("ACTION 47 ERROR: E.TYPE is not relational.");
                } //else etrue is efalse and efalse is etrue,
                else {
                    etrue = efalse;
                    efalse = etrue;
                    //push results
                    semanticStack.push(etrue);
                    semanticStack.push(efalse);
                    semanticStack.push(ETYPE.RELATIONAL);
                }
                break;
            }
            case 48: {
                //of the offset is not 0
                if ((int) semanticStack.peek() != 0) {
                    //pop the offset off
                    int offset = (int) semanticStack.pop();
                    //pop the etype
                    ETYPE e = (ETYPE) semanticStack.pop();
                    //pop off variable entry
                    VariableEntry id = (VariableEntry) semanticStack.pop();
                    //blank prefix 
                    String prefix = "";
                    if (global) {
                        //indicate global prefix
                        prefix = "_";
                        //if the token constant
                        if (token.getType() == TokenType.INTCONSTANT
                                || token.getType() == TokenType.REALCONSTANT) {
                            //generate move code
                            VariableEntry v = create(temp(), token.getType());
                            gen("move", id.getName(), prefix + v.getAddress());
                           // id.setAddress(id.getAddress() + 1);
                        }
                    } else {
                        //else indicate local
                        prefix = "%";
                        //token is constant
                        if (token.getType() == TokenType.INTCONSTANT
                                || token.getType() == TokenType.REALCONSTANT) {
                            //generate move code
                            VariableEntry v = create(temp(), token.getType());
                            gen("move", id.getName(), prefix + v.getAddress());
                           // id.setAddress(id.getAddress() + 1);
                        }
                    }
                    VariableEntry c = create(temp(), id.getType());
                    gen("load " + prefix + Integer.toString(c.getAddress()),
                            Integer.toString(offset), Integer.toString(c.getAddress()));
                    // Token p = new Token(c.getType(), c.getName());
                    //push result
                    semanticStack.push(c);
                    //push etype arithmetic
                    semanticStack.push(ETYPE.ARITHEMTIC);
                } else {
                    //else pop
                    semanticStack.pop();
                }
                break;
            }
            case 49: {
                ETYPE e = (ETYPE) semanticStack.pop();
                if (e != ETYPE.ARITHEMTIC) {
                    System.out.println("ACTION 49 ERROR. ETYPE is not arithmetic");
                }
                parmCount.add(0);
                break;
            }
            case 50: {
                    while (!semanticStack.isEmpty() && semanticStack.peek() instanceof Token) {
                        Stack<Token> reverse = new Stack<Token>();
                        reverse.push((Token) semanticStack.pop());
                        while (!reverse.isEmpty()) {
                            Token id = reverse.pop();
                            gen("param", id.getValue());
                            localMemory++;
                        }
                    }
                    /*
                     * Incomplete
                     */
                    break;
                }
            case 51: {
                if (token.getValue().equals("write")) {
                    while (!semanticStack.isEmpty() && semanticStack.peek() instanceof Token) {
                        Stack<Token> reverse = new Stack<Token>();
                        reverse.push((Token) semanticStack.pop());
                        while (!reverse.isEmpty()) {
                            Token id = reverse.pop();
                            gen("print", "'", id.getValue(), "= '");
                            if (id.getType() == TokenType.REAL) {
                                gen("foutp", id.getValue());
                            } else {
                                gen("outp", id.getValue());
                            }
                        }
                    }
                    parmCount.pop();
                    ETYPE e = (ETYPE) semanticStack.pop();
                    semanticStack.pop();
                }
                if (token.getValue().equals("read")) {
                    while (!semanticStack.isEmpty() && semanticStack.peek() instanceof Token) {
                        Stack<Token> reverse = new Stack<Token>();
                        reverse.push((Token) semanticStack.pop());
                        while (!reverse.isEmpty()) {
                            Token id = reverse.pop();
                            if (id.getType() == TokenType.REAL) {
                                gen("finp", id.getValue());
                            } else {
                                gen("inp", id.getValue());
                            }
                        }
                    }
                    parmCount.pop();
                    ETYPE e = (ETYPE) semanticStack.pop();
                    semanticStack.pop();
                }
                /*
                 * Incomplete
                 */
//                else {
//                    if (parmCount.peek() != 
//                }
                break;
            }
            case 52: {
                if (!globalTable.lookUp(token.getValue()).isFunction()) {
                    System.out.println("ACTION 52: ERROR. ID is not function.");
                }
                ETYPE e = (ETYPE) semanticStack.pop();
                FunctionEntry idCopy = (FunctionEntry) globalTable.lookUp(token.getValue());
                if (idCopy.numParam > 0) {
                    System.out.println("ACTION 52: ERROR id has more than 0 param.");
                }
                VariableEntry id = (VariableEntry) semanticStack.pop();
                gen("call", id.getName(), "0");
                VariableEntry temp = create(temp(), id.getType());
                gen("move", idCopy.result.getName(), Integer.toString(temp.getAddress()));
                semanticStack.push(temp);
                semanticStack.push(ETYPE.ARITHEMTIC);
                break;
            }
            case 53: {
                if (globalTable.lookUp(token.getValue()).isFunction()) {
                    if (globalTable.lookUp(token.getValue()) != currentFunction) {
                        System.out.println("ACTION 53: ERROR. ID is not current function.");
                    }
                    //pop the ETYPE
                    semanticStack.pop();
                    VariableEntry id = (VariableEntry) semanticStack.pop();
                    // Token result = (Token) semanticStack.pop();
                    ((FunctionEntry) globalTable.lookUp(token.getValue())).result = new VariableEntry(id.getName(), id.getType());
                    semanticStack.push(((FunctionEntry) globalTable.lookUp(token.getValue())).result);
                    semanticStack.push(ETYPE.ARITHEMTIC);
                }
                break;
            }
            case 54: {
                //if id isn't a procedure, theres an error
                SymbolTableEntry id = globalTable.lookUp(token.getValue());
                if (!id.isProcedure()) {
                    System.out.println("ACTION 54: ERROR. ID not a procedure");
                }
                break;
            }
            case 55: {
                //set the global alloc at field one to be global memory
                setQuadField(globalAlloc, 1, globalMemory);
                //generate free code
                gen("free", Integer.toString(globalMemory));
                gen("PROCEND");
                break;
            }
            case 56: {
                //generate procbegin code
                gen("PROCBEGIN", globalTable.lookUp("main").getName());
                //set the global allocation for the quad index
                globalAlloc = nextQuad;
                gen("alloc", "_");
                break;
            }
            default: {
                break;
            }
        }
    }

    /**
     * prints the semantic stack, starting with the top symbol
     */
    public void printStack() {
        System.out.println("On stack starting from top...");
        for (Object o : semanticStack) {
            System.out.println(o.toString());
        }
        System.out.println("Stack size: " + semanticStack.size());
        System.out.println();
    }

    /**
     * Prints the quadruple list which represents TVI code
     */
    public void printQuads() {
        int index = 0;
        for (Quadruples q : quadList) {
            if (index == 0) {
                q.print();
                System.out.println();
                index++;
            } else {
                System.out.print(index + ":");
                q.print();
                System.out.println();
                index++;
            }
        }
    }
}