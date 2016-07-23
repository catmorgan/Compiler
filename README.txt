README.txt 
Catherine Morgan
_____________________________________________________________________________________
/** Phase 1: Submitted 2/19/14 **/
_____________________________________________________________________________________
-test file is called lextest within the test folder

CharStreamDriver class
-----------------------------------------------------------
-creates a new CharStream and LexicalAnalyzer in main
-prints tokens until end of the file

Token Class
-----------------------------------------------------------
-type, the TokenType of the token
-value, the String of the token
-creates a token object that has (TokenType type, String value)
-setType(TokenType t) and setValue(String v) to change the type and value
-clear() to return the token to a "blank" token (null, "")
-print() prints the token 

TokenType Class
-----------------------------------------------------------
-creates TokenType enum for all the different types a token can be

LexicalError Class
-----------------------------------------------------------
-implemented an error method for identifiers and numbers that are 
	too long

LexicalAnalyzer Class
-----------------------------------------------------------
-char[] buffer - the buffer of characters
-cStream, the character stream being processed
-previousToken, the last made token
-token, the current token being made and returned
-clear(char[] buffer) takes in a buffer to clear it (set all values to null)
-getNextToken() uses the current character and returns a token
-readIdentifier(char nextChar) takes in a character, creates a buffer from 
		a bunch of characters and gives it to readKeyword to determine 
		what type it is, returns a token.
-readKeyWord(char[] buffer) reads through a buffer of characters and 
		determines what keyword it matches, then returns a token of 
		the type of keyword and value of buffer
-readNumber(char nextChar) takes in a character and adds it to a number
		buffer as long as the characters are numbers. If a decimal or 
		e is read, it passes it to different methods to process the rest of the 
		stream. Returns a token.
-readDecimal(char nextChar, char[] intbuffer, int index) takes the character
		to be read, the number buffer, and the position in the buffer, then keeps
		adding to the buffer if the characters are numbers. If it reads an e, it calls a method to process the rest of the stream. returns a token
-readE (char nextChar, char[] intbuffer, int index) takes the character
		to be read, the number buffer, and the position in the buffer, then keeps
		adding to the buffer if the characters are numbers. returns a token
-readSymbol (char nextChar) takes a character and determines what symbol it is. 
		returns a token


_____________________________________________________________________________________
/** Phase 2: Submitted 3/6/14 **/
_____________________________________________________________________________________
-test file is called parsetest within the test folder

Modifications to LexicalAnalyzer
-----------------------------------------------------------
-LexicalAnalyzer takes in the test file
-processes unary plus/minus, intconstant and realconstants properly
-no longer calls getNextToken() while the character is a white space, 
	instead just gets the next character
-now does correct output for identifiers and numbers that are too long
-Tokens have specified fields and have get-er methods
-TokenType class modified as described below
-created a new test file named lextest2.dat, which should tests every case and
	it really expands the way that the symbols are combined to make sure
	that the lexical analyzer is behaving properly. too long integers (real and intconstants) and identifiers are tested, all the symbols (unless I missed one somehow), and more characters that are errors have been added. 


TokenType class
-----------------------------------------------------------
-modified to implement GrammarSymbol 
-each TokenType has a number associated with it
-added functions and variables:
	-isToken(), returns true since GrammarSymbol is a token
	-isNonTerminal(), returns false since GrammarSymbol is not a NonTerminal
	-isAction(); returns false since GrammarSymbol is not an action
	-int n, the number associated with the tokentype
	-TokenType(int i), sets n to be the TokenType at i
	-getIndec(), returns n associated with the TokenType

GrammarSymbol class
-----------------------------------------------------------
-interface GrammarSymbol tells the other classes that implement
	GrammarSymbol what functions they need
		-boolean isToken()
		-boolean isNonTerminal()
		-boolean isAction()
		-int getIndex()

NonTerminal class
-----------------------------------------------------------
-declares all the NonTermial types
-implements GrammarSymbol
	-isToken(), returns false since GrammarSymbol is not a token
	-isNonTerminal(), returns true since GrammarSymbol is a NonTerminal
	-isAction(); returns false since GrammarSymbol is not an action
	-int n, the number associated with the nonterminal
	-NonTerminal(int i), sets n to be the nonterminal at i
	-getIndex(), returns n associated with the nonterminal

ParseTable class
-----------------------------------------------------------
-int[][] parseTable, makes a 2D array for the parse table
-ParseTable(), calls init() to initialize the parseTable
-init(), initializes the parseTable to be the given parse_table
-search(int predicted, int token), returns the int in the position
	[predicted, token] in the parseTable

Parser class
-----------------------------------------------------------
-does stack manipulation to check if the tokens from the lexical analyzer
	match the grammar order
-LexicalAnalyzer lexer, the lexer to get tokens from
-Token token, the current token
-Stack<GrammarSymbol>, the stack of grammar symbols
-ParseTable parseTable, initializes the parse table
-GrammarSymbol predicted, the top symbol from the stack
-RHSTable RHS, initializes the right-hand side table
-Parse(LexicalAnalyzer d), sets lexer to be the input LexicalAnalyzer
-clear(), clears the stack
-pushDownAutomaton(), adds the ENDOFFILE and the start symbol to the stack and 		sets predicted to be the top symbol on the stack. It checks if the 
	 predicted symbol is a token or nonterminal. If it's a token, it will 
	 get the next token or throw an error, and if it's a nonterminal it will
	 search through the parsetable and either return an error, do nothing (for now), or push the rules from the RHSTable to the stack

ParserDriver class
-----------------------------------------------------------
-contains the main class
-initializes a new parser to take in a lexicalanalyzer of the parsetest then
	calls pushDownAutomaton function in Parser class

ParserError class
-----------------------------------------------------------
-implements methods for throwing errors related to unexpected tokens and 
	a mismatch between predicted symbol and current token

RHSTable class
-----------------------------------------------------------
-GrammarSymbol[][] rules, declares a 2D array of GrammarSymbols
-RHSTable(), calls init() to initialize rules
-init(), initializes rules to be the given right-hand side table of 
	productions/rules
-getRule(int n), will return the array of GrammarSymbols that consist of 
	the productions at that position
-dumpTable(), printes all the rules in the table
-printrule(int n), prints a specific rule

SemanticAction class
-----------------------------------------------------------
-declares all the actions
-implements GrammarSymbol
	-isToken(), returns false since GrammarSymbol is not a token
	-isNonTerminal(), returns false since GrammarSymbol is not a NonTerminal
	-isAction(); returns true since GrammarSymbol is an action
	-int n, the number associated with the action
	-SemanticAction(int i), sets n to be the action at i
	-getIndex(), returns n associated with the action

KNOWN ERRORS
-----------------------------------------------------------
-none


_____________________________________________________________________________________
/** Phase 3: Submitted 4/9/14 **/
_____________________________________________________________________________________
-test file is called symtabtest within the tests folder

ArrayEntry class
-----------------------------------------------------------
-creates a new SymbolTableEntry for an Array
-extends SymbolTableEntry
	-ArrayEntry(), blank constructor
	-ArrayEntry(String name), construct an array entry with a given name
	-ArrayEntry(String name, TokenType type), constructor with token value and type
	-getAddress(), return int current address of array
	-setAddress(int address), change current address to be input address
	-getUpperBound(), return int upper bound of the array
	-setUpperBound(int u), change current upper bound to be input bound
	-getLowerBound(), return int lowerbound of the array
	-setLowerBound(int l), change current lower bound to be input bound
	-isArray(), return boolean true since it is an array
	-isFunctionResult(), return true if array is a function result, otherwise false
	-setFunctionResult(), set the array to be a function result
	-isParameter(), return true if array is a parameter, otherwise false
	-setParm(), set the array to be a parameter
	-print(), prints the array entry

ConstantEntry class
-----------------------------------------------------------
-creates a new SymbolTableEntry for a constant
-extends SymbolTableEntry
	-ConstantEntry(), blank constructor
	-ConstantEntry(String name), constructs a constant entry with given name
	-ConstantEntry(String name, TokenType type), constructs a constant entry 
		with given token value and type
	-isFunctionResult(), returns true if constant is a function result otherwise 
		false
	-setFunctionResult(), set the constant to be a function result
	-isParameter(), return true if constant is a parameter otherwise false
	-setParm(), set constant to be a parameter
	-print(), print the constant entry with name and type

	FunctionEntry class
-----------------------------------------------------------
-creates a new SymbolTableEntry for a function
-extends SymbolTableEntry
	-FunctionEntry(), blank constructor
	-FunctionEntry(String name), constructs a function entry with given name
	-FunctionEntry(String name, TokenType type), constructs a function entry
		with given token value and type
	-isFunction(), returns true since it's a function
	-isFunctionResult(), return true if it's a function otherwise false
	-setFunctionResult(), set the function to be a function result
	-isParameter(), return true if the function is a parameter otherwise false
	-setParm(), set the function to be a parameter
	-print(), prints the function entry with name and type

	KeywordEntry class
-----------------------------------------------------------
-creates a new SymbolTableEntry for a keyword
-extends SymbolTableEntry
	-KeywordEntry(), blank constructor
	-KeywordEntry(String name), constructs a keyword entry with given name
	-isKeyword(), returns true since it's a keyword
	-print(), prints the keyword with name

	ParameterInfo class
-----------------------------------------------------------
-used to keep track of parameter info from function and procedure
-extends SymbolTableEntry
	-ParameterInfo(), blank constructor, which creates an ArrayList of size 10
	-ParameterInof(int size), constructor to make ArrayList of input size
	-getParam(int index), will return the parameter at the given index
	-getNumOfParam(), return number of parameters that is stored

	ProcedureEntry class
-----------------------------------------------------------
-creates a new SymbolTableEntry for a procedure
-extends SymbolTableEntry
	-ProcedureEntry(), blank constructor
	-ProcedureEntry(String name), constructor for procedure entry with 
		given name
	-isProcedure(), returns true since it's a procedure
	-isFunctionResult(), return true if it's a function otherwise false
	-setFunctionResult(), set the procedure to be a function result
	-isParameter(), return true if the procedure is a parameter otherwise false
	-setParm(), set the procedure to be a parameter
	-print(), prints the procedure entry with name

	SymbolTable class
-----------------------------------------------------------
-creates a HashMap with the keys being String and the values
	 being SymbolTableEntry
	 -SymbolTable(int size), construct a new symboltable of input size
	 -insert(String key, SymbolTableEntry value), input a key-value
	 	mapping into the symboltable
	 -lookUp(String key), return the SymbolTableEntry value associated 
	 	with the input key
	 -containsKey(String key), returns true if the key is present in 
	 	the symboltable otherwise false
	 -size(), returns the size of the symboltable
	 -dumpTable(), prints all entry values in the table

	SymbolTableDriver class
-----------------------------------------------------------
-sets up the test symboltables and then prints all the entries
	-run(), creates three symboltables (constant, global, local) 
		and a lexicalanalyzer from the test file. Token the file 
		and as long as there are tokens, put constants in the constanttable, 
		read and write in the global table, and identifiers in the local table..
		then print the tables
	-main(String[] args), creates a new SymbolTableDriver object to run test
		method

	SymbolTableEntry class
-----------------------------------------------------------
-makes a new SymbolTableEntry object and sets up what functions 
	the classes that extend this class will need
	-SymbolTableEntry(), blank constructor
	-SymbolTableEntry(String name), creates a symboltable entry with 
		given name
	-SymbolTableEntry(String name, TokenType type), create a symboltable
		entry with given value of token and type of token
	-getName(), return the name of the symboltable entry
	-setName(String name), set the name of the symboltable entry to be the
		input name
	-getType(), return the token type of the symboltable entry 
	-setType(TokenType type), change the current type of the 
		token to be input type
-Functions below are necessary for classes that extend this one
	-isVariable(), intialized to false
	-isKeyword(), intialzied to false
	-isProcedure(), intialzied to false
	-isFunction(), intialzied to false
	-isFunctionResult, intialzied to false
	-isParameter(), intialzied to false
	-isArray(), intialzied to false
	-print(), prints the SymbolTableEntry

	VariableEntry class
-----------------------------------------------------------
-creates a new SymbolTableEntry for a variable
-extends SymbolTableEntry
	-VariableEntry(), blank constructor
	-VariableEntry(String name), constructs a variable entry with given 
		name
	-VariableEntry(String name, TokenType type), constructs a variable
		entry with given name and type
	-getAddress(), return the current address of the variable
	-setAddress(int address), change the current address to the input one
	-isVariable(), return true since it's a variable
	-isFunctionResult(), return true if it's a function otherwise false
	-setFunctionResult(), set the variable to be a function result
	-isParameter(), return true if the variable is a parameter otherwise false
	-setParm(), set the variable to be a parameter
	-print(), prints the variable entry with name and type
 
KNOWN ERRORS
-----------------------------------------------------------
-none

_____________________________________________________________________________________
/** Phase 4: Submitted 4/13/14 **/
_____________________________________________________________________________________
The SemanticAction package takes in actions from the parser to perform actions based on the given action and what tokens have been seen on the SemanticAction stack

The Quadruples class creates an ArrayList of up to 4 strings, which will represent op codes and field for TVI code. Part of this class involves printing the Quadruple, setting a position within the Quadruple, and getting a field within the Quadruple.

The main class is SemanticActions that has the Execute(SemanticAction actionNumber, Token token) which performs a giant switch statement. The actions are based on the grammar provided. 

There are many errors within the code, and some actions which have't been fully implemented yet. 

One thing I would for sure change is for the gen function to take in SymbolTableEntries instead of Strings or have a way to check if the ID is a constant, that way instead of checking if the id's were a constant in each action, the gen function could handle that. In addition, I would change gen function to also deal with global/local prefixes and the $$ markers for temp/func names. 

I would implement the SemanticError class to have more effective error handling and printing.