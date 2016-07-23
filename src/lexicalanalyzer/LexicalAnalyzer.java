/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lexicalanalyzer;

import java.util.Stack;
import static lexicalanalyzer.CharStream.BLANK;
import static lexicalanalyzer.CharStream.EOF;

/**
 *
 * @author Cat Morgan
 */
public class LexicalAnalyzer {
    //the stream to read characters from
    private CharStream cStream;
    //the maximum identifier length
    private int MAX_IDENT_LENGTH = 64;
    //creates new buffer for the characters
    private char[] buffer = new char[MAX_IDENT_LENGTH];
    private char[] intbuffer = new char[MAX_IDENT_LENGTH];
    private char[] buff = new char[MAX_IDENT_LENGTH];
    //the token to be returned
    private Token token = new Token(null, "");
    //the previous token that was created
    private Token previousToken = new Token(null, "");

    /**
     *initializes the character stream
     * @param c, pass in a character stream to get characters to be processed
     */
    public LexicalAnalyzer(String filename) {
        
        this.cStream = new CharStream(filename);
    }

    /**
     * clears a buffer
     * @param buffer, pass in a buffer to clear it by making all elements be the
     * null character
     */
    private void clear(char[] buffer) {
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = '\u0000';
        }
    }

    /**
     * uses the next character to produce to next token
     *
     * @return a token of TokenType and value
     */
    public Token getNextToken() {
        try {
            //reset the buffer
            clear(buffer);
            clear(intbuffer);
            clear(buff);
            //reset the token to a defined base token of type null, and value ""
            token.clear();
            //the current characer = the next one
            char c = cStream.currentChar();
            //if the character is blank
            while (c == BLANK) {
                //get the next character
                c = cStream.currentChar();
            }
            //if the character is a letter
            if (Character.isLetter(c)) {
                //read the identifier 
                token = readIdentifier(c);
                //once the token is assigned, print token
               // token.print();
            } //if the character is a digit
            else if (Character.isDigit(c)) {
                //read number 
                token = readNumber(c);
                //print assigned token
               //token.print();
            } else {
                //read the symbol
                token = readSymbol(c);
                //print assigned token
               //token.print();
            }
        } catch (LexicalError ex) {
            //otherwise print print error
            System.out.println(ex);
        }
        //assign the previousToken to be the current token, 
        //so when the token is cleared, the old value is saved
        previousToken.setType(token.getType());
        previousToken.setValue(token.getValue());
        return token;
    }

    /**
     * reads a character a builds a buffer out of them
     *
     * @param nextChar, the character that is being processed
     * @return a token based on the results of readKeyword
     * @throws LexicalError, if an error occurs, calls the LexicalError class to
     * throw it
     */
    private Token readIdentifier(char nextChar) throws LexicalError {
        //buffer index
        int index = 0;
        //Read the characters iff they are a letter or digit
        while (Character.isLetterOrDigit(nextChar)) {
            //if the index is at the end of the buffer, the buffer
            //is full and the indentifier is too long
         if (index > buffer.length - 1) {
                //throw an error that the number is too long
                clear(buffer);
                index = 0;
                if (Character.isLetterOrDigit(cStream.currentChar())) {
                    nextChar = cStream.currentChar();
                    while (Character.isLetterOrDigit(nextChar)) {
                        //otherwise add the number into the buffer
                       // buffer[index] = nextChar;
                        //get the next character
                        nextChar = cStream.currentChar();
                        //increase index
                       // index++;
                    }
                } else {
                    cStream.pushBack(cStream.currentChar());
                }
                throw LexicalError.longIdentifier(cStream.lineNumber);
            } else {
                //set the current position in buffer to be the character
                buffer[index] = nextChar;
                //get the next character
                nextChar = cStream.currentChar();
                //move forward in the buffer
                index++;
            }
        }
        //if the character is not a blank, push it back to the stack
        if (!(nextChar == CharStream.BLANK)) {
            cStream.pushBack(nextChar);
        }
        //give the buffer to readKeyword to be processed
        return readKeyword(buffer);
    }
    
    
    /**
     * reads through an array of character to find what keyword it matches
     *
     * @param buffer, the input global buffer that is formed in readIdentifier
     * @return the token with TokenType and value
     * @throws LexicalError, if an error occurs, run it through Lexical Error
     * class
     */
    private Token readKeyword(char[] buffer) throws LexicalError {
        //turn the array buffer into a string-we want to keep the 
        //cases to be returned 
        String value = new String(buffer).trim();
        //copy the string buffer and make it lowercase
        String keyWord = new String(buffer).toLowerCase().trim();

        /**
         * The following IF statements take the keyWord string, compares it to
         * keywords in the language and sets the TokenType accordingly
         */
        //PROGRAM
        if (keyWord.equals("program")) {
            token.setType(TokenType.PROGRAM);
            token.setValue(value);
            return token;
        }
        //BEGIN
        if (keyWord.equals("begin")) {
            token.setType(TokenType.BEGIN);
            token.setValue(value);
            return token;
        }
        //END
        if (keyWord.equals("end")) {
            token.setType(TokenType.END);
            token.setValue(value);
            return token;
        }
        //VAR
        if (keyWord.equals("var")) {
            token.setType(TokenType.VAR);
            token.setValue(value);
            return token;
        }
        //FUNCTION
        if (keyWord.equals("function")) {
            token.setType(TokenType.FUNCTION);
            token.setValue(value);
            return token;
        }
        //PROCEDURE
        if (keyWord.equals("procedure")) {
            token.setType(TokenType.PROCEDURE);
            token.setValue(value);
            return token;
        }
        //RESULT
        if (keyWord.equals("result")) {
            token.setType(TokenType.RESULT);
            token.setValue(value);
            return token;
        }
        //INTEGER
        if (keyWord.equals("integer")) {
            token.setType(TokenType.INTEGER);
            token.setValue(value);
            return token;
        }
        //REAL
        if (keyWord.equals("real")) {
            token.setType(TokenType.REAL);
            token.setValue(value);
            return token;
        }
        //ARRAY
        if (keyWord.equals("array")) {
            token.setType(TokenType.ARRAY);
            token.setValue(value);
            return token;
        }
        //OF
        if (keyWord.equals("of")) {
            token.setType(TokenType.OF);
            token.setValue(value);
            return token;
        }
        //IF
        if (keyWord.equals("if")) {
            token.setType(TokenType.IF);
            token.setValue(value);
            return token;
        }
        //THEN
        if (keyWord.equals("then")) {
            token.setType(TokenType.THEN);
            token.setValue(value);
            return token;
        }
        //ELSE
        if (keyWord.equals("else")) {
            token.setType(TokenType.ELSE);
            token.setValue(value);
            return token;
        }
        //WHILE
        if (keyWord.equals("while")) {
            token.setType(TokenType.WHILE);
            token.setValue(value);
            return token;
        }
        //DO
        if (keyWord.equals("do")) {
            token.setType(TokenType.DO);
            token.setValue(value);
            return token;
        }
        //NOT
        if (keyWord.equals("not")) {
            token.setType(TokenType.NOT);
            token.setValue(value);
            return token;
        }
        //MULOP
        if (keyWord.equals("div")
                || keyWord.equals("mod")
                || keyWord.equals("and")) {
            token.setType(TokenType.MULOP);
            token.setValue(value);
            return token;
        }
        //ADDOP
        if (keyWord.equals("or")) {
            token.setType(TokenType.ADDOP);
            token.setValue(value);
            return token;
        } //IDENTIFIER
        else {
            token.setType(TokenType.IDENTIFIER);
            token.setValue(value);
            return token;
        }

    }

    /**
     * reads a number character and creates a buffer out of them
     *
     * @param nextChar, the current character being processed
     * @return a token of TokenType and value
     * @throws LexicalError, if error occurs, calls LexicalError class to handle
     * it
     */
    private Token readNumber(char nextChar) throws LexicalError {
        //buffer index
        int index = 0;
        //creates a new buffers specifcally for numbers
    //    char[] intbuffer = new char[64];
        //while a number is being read
        while (Character.isDigit(nextChar)) {
            //if the buffer is full
            if (index > intbuffer.length - 1) {
                //throw an error that the number is too long
                clear(intbuffer);
                index = 0;
                if (Character.isDigit(cStream.currentChar())) {
                    nextChar = cStream.currentChar();
                    while (Character.isDigit(nextChar)) {
                        //otherwise add the number into the buffer
                      //  intbuffer[index] = nextChar;
                        //get the next character
                        nextChar = cStream.currentChar();
                        //increase index
                      //  index++;
                    }
                } else {
                    cStream.pushBack(cStream.currentChar());
                }
                throw LexicalError.longNumber(cStream.lineNumber);
            }
            //otherwise add the number into the buffer
            intbuffer[index] = nextChar;
            //get the next character
            nextChar = cStream.currentChar();
            //increase index
            index++;
            //set the Token to be an INTCONSTANT since no decimals
            //or 'e' has been read
            token.setType(TokenType.INTCONSTANT);
            //set the value to be the buffer
            token.setValue(new String(intbuffer).trim());
        }
        //if there is a decimal
        if (nextChar == '.') {
            //save the decimal in a temp variable
            char temp = nextChar;
            //get the next character
            nextChar = cStream.currentChar();
            //if the next character is a number
            if (Character.isDigit(nextChar)) {
                //add the decimal to the buffer
                intbuffer[index] = temp;
                //increase index
                index++;
                //then call the function that reads a demical, passing 
                //in the current characters, the buffer, and the current index
                //then set token to be the result of the function
                token = readDecimal(nextChar, intbuffer, index);
            } else {
                //otherwise return the character and the '.' to the 
                //buffer
                cStream.pushBack(nextChar);
                cStream.pushBack(temp);
                //return token
                return token;
            }
        }
        //if an e is read
        if (nextChar == 'e' || nextChar == 'E') {
            //save the e in a temp variable
            char temp = nextChar;
            //get the next character
            nextChar = cStream.currentChar();
            //if the next character is a number, +, or -
            if (Character.isDigit(nextChar) || nextChar == '+'
                    || nextChar == '-') {
                //add the e to the buffer
                intbuffer[index] = temp;
                //increase index
                index++;
                //call function readE and pass it the character, 
                //the buffer and the current index. then set the token to be
                //the result of the function
                token = readE(nextChar, intbuffer, index);
            } else {
                //otherwise push back the character that was just looked at
                //and the e
                cStream.pushBack(nextChar);
                cStream.pushBack(temp);
            }
        } //if the character isn't a digit, push it back to the stack
        else if (!(Character.isDigit(nextChar))) {
            cStream.pushBack(nextChar);
        }

        return token;
    }

    /**
     * continuing from readNumber, processes a number with a decimal in it
     *
     * @param nextChar, the character being processed
     * @param intbuffer, the buffer of numbers created in readNumber
     * @param index, the current location within the buffer
     * @return, a token of TokenType and value
     * @throws LexicalError, if an error happens call this class to handle it
     */
    private Token readDecimal(char nextChar, char[] intbuffer, int index) 
            throws LexicalError {
        //if the character is a digit 
        while (Character.isDigit(nextChar)) {
            //if the buffer is full
            if (index > intbuffer.length - 1) {
                //throw an error that the number is too long
                throw LexicalError.longNumber(cStream.lineNumber);
            }
            //otherwise add the digit to the buffer
            intbuffer[index] = nextChar;
            //get the next character
            nextChar = cStream.currentChar();
            //increase the index
            index++;
        }

        //if the character is an e
        if (nextChar == 'e' || nextChar == 'E') {
            //temp character to hold the 'e'
            char temp = nextChar;
            //get next character
            nextChar = cStream.currentChar();
            //if the next character is a digit
            if (Character.isDigit(nextChar)) {
                //add the 'e' to the buffer
                intbuffer[index] = temp;
                //increase index
                index++;
                //call readE, passing in the character, the intbuffer
                //and the current index. then set token to be the result
                //of readE
                token = readE(nextChar, intbuffer, index);
            } else {
                //else add the characer back to the stack as long as it's not 
                //a blank
                if (!(nextChar == BLANK)) {
                    cStream.pushBack(nextChar);
                }
                //add the 'e' back to the stack to be processed
                cStream.pushBack(temp);
            }
        }
        //set the token type to be a REALCONSTANT
        token.setType(TokenType.REALCONSTANT);
        //set the value of the token to be the buffer
        token.setValue(new String(intbuffer).trim());
        return token;
    }

    /**
     * continuing from readNumber or readDecimal, the number contains 'e'
     *
     * @param nextChar, the character being processed
     * @param intbuffer, the number buffer created in readNumber
     * @param index, the current position in the buffer
     * @return, a token of TokenType and value
     * @throws LexicalError, if an error occurs, call LexicalError class to
     * handle it
     */
    private Token readE(char nextChar, char[] intbuffer, int index) 
            throws LexicalError {
        //while the character is a number
        while (Character.isDigit(nextChar)) {
            //if the buffer is full
            if (index > intbuffer.length - 1) {
                //throw an error that the number is too long
                throw LexicalError.longNumber(cStream.lineNumber);
            }
            //otherwise add the character to the buffer
         //   intbuffer[index] = nextChar;
            //get the next character
            nextChar = cStream.currentChar();
            //increase index
           // index++;
            //set the type to be REALCONSTANT
            token.setType(TokenType.REALCONSTANT);
            //set token value to be the buffer
            token.setValue(new String(intbuffer).trim());
        }
        //if the next character isn't blank, put it back on the stack
        if (!(nextChar == CharStream.BLANK)) {
            cStream.pushBack(nextChar);
        }
        return token;
    }

    /**
     * processes a character that is a symbol
     *
     * @param nextChar, the current character being looked at
     * @return a token of TokenType and value
     * @throws LexicalError, if error occurs, calls LexicalError class to handle
     * it
     */
    private Token readSymbol(char nextChar) throws LexicalError {
        /**
         * The following IF statements compare the character being looked at to
         * characters to see if it matches, and sets the Type and Value
         * accordingly, then returns the token.
         */
        //RELOP
        if (nextChar == '=') {
            token.setType(TokenType.RELOP);
            token.setValue(String.valueOf(nextChar));
            return token;
        }
        //RELOP w/ look ahead, <, <>, <=
        if (nextChar == '<') {
            //look ahead to the next character
            nextChar = cStream.currentChar();
            //if the next character is '>' or '=', so the symbol
            //is <> or <=, return then value accordingly
            if (nextChar == '>' || nextChar == '=') {
                token.setType(TokenType.RELOP);
                token.setValue("<" + String.valueOf(nextChar));
                return token;
            } else {
                //else the next character wasn't  '>' or '=' 
                //so push it back on the stack as long as it's no a blank
                if (!(nextChar == BLANK)) {
                    cStream.pushBack(nextChar);
                }
                //since it's not <> or <=, it must be a '<', so set
                //value accordingly
                token.setType(TokenType.RELOP);
                token.setValue("<");
                return token;
            }
        }
        //RELOP w/ look ahead, >, >=
        if (nextChar == '>') {
            //look ahead at next character
            nextChar = cStream.currentChar();
            //if the next character is '=', so it matches symbol >=
            if (nextChar == '=') {
                token.setType(TokenType.RELOP);
                //set the value to >=
                token.setValue(">" + String.valueOf(nextChar));
                return token;
            } else {
                //else the next character isn't '=' so push it back 
                //to the stack if it's not a blank
                if (!(nextChar == BLANK)) {
                    cStream.pushBack(nextChar);
                }
                //it must be a ">" so set value accordingly
                token.setType(TokenType.RELOP);
                token.setValue(">");
                return token;
            }
        }
        //MULOP
        if (nextChar == '*'
                || nextChar == '/') {
            token.setType(TokenType.MULOP);
            token.setValue(String.valueOf(nextChar));
            return token;
        }
        //ADDOP
        //if the current character is + or -, and the previous
        //token was either (, [, CONSTANT, or IDENTIFIER, then
        //it must be an addop
        if ((nextChar == '+'
                || nextChar == '-')
                && (previousToken.getType() == TokenType.RIGHTPAREN
                || previousToken.getType() == TokenType.RIGHTBRACKET
                || previousToken.getType() == TokenType.IDENTIFIER
                || previousToken.getType() == TokenType.INTCONSTANT
                || previousToken.getType() == TokenType.REALCONSTANT)) {
            token.setType(TokenType.ADDOP);
            token.setValue(String.valueOf(nextChar));
            return token;
        }
        //UNARYPLUS
        if (nextChar == '+') {
            //look ahead to next character
            nextChar = cStream.currentChar();
         //   System.out.println("nextChar: " + nextChar);
            //if the next character is a letter

                //buffer index
                int index = 0;
                //creates a new buffers specifcally for numbers
            //    char[] buff = new char[64];
                buff[index] = '+';
                index++;
                boolean intc = false;
         //       System.out.println("buffer: " + new String(buff));
            
                //while a number is being read
                while (Character.isDigit(nextChar)) {
                    //if the buffer is full
                    if (index > buff.length - 1) {
                        //throw an error that the number is too long
                        clear(buff);
                        index = 0;
                        if (Character.isDigit(cStream.currentChar())) {
                            nextChar = cStream.currentChar();
                            while (Character.isDigit(nextChar)) {
                                //otherwise add the number into the buffer
                               // buff[index] = nextChar;
                                //get the next character
                                nextChar = cStream.currentChar();
                                //increase index
                              // index++;
                            }
                        } else {
                            cStream.pushBack(cStream.currentChar());
                        }
                        throw LexicalError.longNumber(cStream.lineNumber);
                    }
                    //otherwise add the number into the buffer
                    buff[index] = nextChar;
                 //   System.out.println("buffer: " + new String(buff));
                    //get the next character
                    nextChar = cStream.currentChar();
                 //   System.out.println("nextChar:" + nextChar);
                    //increase index
                    index++;
                
                    //the + must be part of a constant, so assign
                    //accordingly
                    token.setType(TokenType.INTCONSTANT);
                    token.setValue(String.valueOf(new String(buff).trim()));
                    intc = true;
               //     return token;
                }
            
                //else pushBack the character if it's not a blank
                if (!(nextChar == BLANK)) {
                    cStream.pushBack(nextChar);
                }
                //the plus must be attached to a number 
//                token.setType(TokenType.UNARYPLUS);
//                token.setValue("+");
                if (!intc) {
                    token.setType(TokenType.UNARYPLUS);
                    token.setValue(String.valueOf(new String(buff).trim()));   
                }
                return token;
            }

            //ASSIGNOP and COLON
            if (nextChar == ':') {
                //look ahead to next character
                nextChar = cStream.currentChar();
                //if the next character is '=', so the symbol is ':='
                if (nextChar == '=') {
                    //the symbol is an ASSIGNOP
                    token.setType(TokenType.ASSIGNOP);
                    token.setValue(String.valueOf(":="));
                    return token;
                } else {
                    //else push the character to a stack if it's not a blank
                    if (!(nextChar == BLANK)) {
                        cStream.pushBack(nextChar);
                    }
                    //the character is a colon 
                    token.setType(TokenType.COLON);
                    token.setValue(":");
                    return token;
                }
            }
            //COMMA
            if (nextChar == ',') {
                token.setType(TokenType.COMMA);
                token.setValue(String.valueOf(nextChar));
                return token;
            }
            //SEMICOLON
            if (nextChar == ';') {
                token.setType(TokenType.SEMICOLON);
                token.setValue(String.valueOf(nextChar));
                return token;
            }
            //LEFTPARAN
            if (nextChar == '(') {
                token.setType(TokenType.LEFTPARAN);
                token.setValue(String.valueOf(nextChar));
                return token;
            }
            //RIGHTPARAN
            if (nextChar == ')') {
                token.setType(TokenType.RIGHTPAREN);
                token.setValue(String.valueOf(nextChar));
                return token;
            }
            //LEFTBRACKET
            if (nextChar == '[') {
                token.setType(TokenType.LEFTBRACKET);
                token.setValue(String.valueOf(nextChar));
                return token;
            }
            //RIGHTBRACKET
            if (nextChar == ']') {
                token.setType(TokenType.RIGHTBRACKET);
                token.setValue(String.valueOf(nextChar));
                return token;
            }
        //UNARYPLUS
        if (nextChar == '-') {
            //look ahead to next character
            nextChar = cStream.currentChar();
         //   System.out.println("nextChar: " + nextChar);
            //if the next character is a letter

                //buffer index
                int index = 0;
                //creates a new buffers specifcally for numbers
               // char[] buff = new char[64];
                buff[index] = '-';
                index++;
                boolean intc = false;
         //       System.out.println("buffer: " + new String(buff));
            
                //while a number is being read
                while (Character.isDigit(nextChar)) {
                    //if the buffer is full
                    if (index > buff.length - 1) {
                        //throw an error that the number is too long
                        clear(buff);
                        index = 0;
                        if (Character.isDigit(cStream.currentChar())) {
                            nextChar = cStream.currentChar();
                            while (Character.isDigit(nextChar)) {
                                //otherwise add the number into the buffer
                                buff[index] = nextChar;
                                //get the next character
                                nextChar = cStream.currentChar();
                                //increase index
                                index++;
                            }
                        } else {
                            cStream.pushBack(cStream.currentChar());
                        }
                        throw LexicalError.longNumber(cStream.lineNumber);
                    }
                    //otherwise add the number into the buffer
                    buff[index] = nextChar;
                 //   System.out.println("buffer: " + new String(buff));
                    //get the next character
                    nextChar = cStream.currentChar();
                 //   System.out.println("nextChar:" + nextChar);
                    //increase index
                    index++;
                
                    //the + must be part of a constant, so assign
                    //accordingly
                    token.setType(TokenType.INTCONSTANT);
                    token.setValue(String.valueOf(new String(buff).trim()));
                    intc = true;
               //     return token;
                }
            
                //else pushBack the character if it's not a blank
                if (!(nextChar == BLANK)) {
                    cStream.pushBack(nextChar);
                }
                //the plus must be attached to a number 
//                token.setType(TokenType.UNARYPLUS);
//                token.setValue("+");
                if (!intc) {
                    token.setType(TokenType.UNARYMINUS);
                    token.setValue(String.valueOf(new String(buff).trim()));   
                }
                return token;
            }
            //DOUBLEDOT and ENDMARKER
        if (nextChar == '.') {
            //look ahead to next character
            nextChar = cStream.currentChar();
            if (nextChar == '.') {
                //if the next character is '.' so the symbol is '..'
                //assign accordingly
                token.setType(TokenType.DOUBLEDOT);
                token.setValue(String.valueOf(".."));
                return token;
            } else {
                //push the character back to the stack to be processed
                if (!(nextChar == BLANK)) {
                    cStream.pushBack(nextChar);
                }
                //the '.' must be an endmarker then
                token.setType(TokenType.ENDMARKER);
                token.setValue(".");
                return token;
            }
        }
        //ENDOFFILE
        if (nextChar == EOF) {
            token.setType(TokenType.ENDOFFILE);
            token.setValue(String.valueOf(nextChar));
            return token;
        } else {
            //the character made it through the entire method without 
            //being caught by the LexicalError, so print what the character is
            //and return a blank token
            throw LexicalError.IllegalCharacter(nextChar, cStream.lineNumber);
        }
    }
}
