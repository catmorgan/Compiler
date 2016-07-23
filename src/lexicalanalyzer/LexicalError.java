                             /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lexicalanalyzer;

/**
 *
 * @author Cat Morgan
 */
/** Exception class thrown when a lexical error is encountered. */
public class LexicalError extends CompilerError
{
   public LexicalError(Type errorNumber, String message)
   {
      super(errorNumber, message);
   }

   // Factory methods to generate the lexical exception types.

   public static LexicalError BadComment(int lineNumber)
   {
      return new LexicalError(Type.BAD_COMMENT,
                              ">>> ERROR: Cannont include { inside a comment. "
              + "line: " + lineNumber);
   }

   public static LexicalError IllegalCharacter(char c, int lineNumber)
   {
      return new LexicalError(Type.ILLEGAL_CHARACTER,
                              ">>> ERROR: Illegal character: " 
              + c + ". line: " + lineNumber);
   }

   public static LexicalError UnterminatedComment(int lineNumber)
   {
      return new LexicalError(Type.UNTERMINATED_COMMENT,
                              ">>> ERROR: Unterminated comment. line: "
              + lineNumber);
   }
   
      public static LexicalError longIdentifier(int lineNumber)
   {
      return new LexicalError(Type.LONGIDENTIFIER,
                              ">>> ERROR: Identifier is too long. line: "
              + lineNumber);
   }
      
   public static LexicalError longNumber(int lineNumber)
   {
      return new LexicalError(Type.LONGNUMBER,
                              ">>> ERROR: Number is too long. line: "
              + lineNumber);
   }
}