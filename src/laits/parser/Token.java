package laits.parser;

import java.io.Serializable;

/**
 * Token
 *
 * @author Javier Gonzalez Sanchez
 * @version 20100223
 */
public class Token implements Serializable{
  
  public static final int INTEGER = 1;  
  public static final int REAL = 2;
  public static final int VARIABLE = 3;
  public static final int OPERATION = 4;    
  public static final int PARENTHESIS = 5;
  private String lexeme;
  private int type;

  /**
   *
   * @param s
   * @param t
   */
  public Token(String s, int t) {
    lexeme = s;
    type = t;
  }

  public String getLexeme() {
    return lexeme;
  }

  public void setLexeme(String lexeme) {
    this.lexeme = lexeme;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return "(" + lexeme + ":" + type + ")";
  }
    
}