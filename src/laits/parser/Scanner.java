package laits.parser;

/**
 * Scanner Class
 * This is a Lexical Analyzer
 *
 * @author Javier Gonzalez
 * @author zpwn
 * @version 20100222
 */
public class Scanner {
  
  public static final int DIGIT = 1;
  public static final int POINT  =2;
  public static final int VARIABLE =3;
  public static final int OPERATION = 4;
  public static final int PARENTHESIS = 5;
  private Equation equation;

  /**
   *
   * @param equation
   */
  public Scanner(Equation equation) {
    if (equation == null)
      this.equation = equation=new Equation();
    else
      this.equation = equation;
 }

  /**
   *
   * @return
   */
  public Equation getEquation() {
    return this.equation;
  }

  /**
   *
   * @param letter that we add to the current lexema
   * @param type of the letter (parenthesis, operation, variable, point, digit)
   * @return
   */
  public boolean addInput(String letter, int type) {
    boolean status = true;
    Token token;
    switch (type) {
      case PARENTHESIS:
        equation.tokenList.add (new Token(letter, Token.PARENTHESIS));
        break;
      case OPERATION:
          if (equation.tokenList.size() > 0){
        token = equation.tokenList.getLast();
          if (token.getType() == Token.OPERATION){
            status = false;
          } else {
            equation.tokenList.add (new Token(letter, Token.OPERATION));
          }
          } else {
            equation.tokenList.add (new Token(letter, Token.OPERATION));
          }
        break;
      case VARIABLE:
        equation.tokenList.add (new Token(letter, Token.VARIABLE));
        break;
      case POINT:
        if (equation.tokenList.size() > 0){
          token = equation.tokenList.getLast();
          if (token.getType() == Token.INTEGER){
            token.setType(Token.REAL);
            token.setLexeme(token.getLexeme().concat(letter));
          } else if (token.getType() == Token.REAL){
            status = false;
          } else {
            equation.tokenList.add(new Token(letter, Token.REAL));
          }
        } else {          
          equation.tokenList.add(new Token(letter, Token.REAL));
        }
        break;
      case DIGIT:
        if (equation.tokenList.size() > 0) {
          token = equation.tokenList.getLast();
          if (token.getType() == Token.INTEGER || token.getType() == Token.REAL) {
            token.setLexeme(token.getLexeme().concat(letter));
          } else {
            equation.tokenList.add(new Token(letter, Token.INTEGER));
          }
        } else {          
          equation.tokenList.add(new Token(letter, Token.INTEGER));
        }
        break;
    }
    equation.setIsCorrect(status);
    return status;
  }

  /**
   *
   * @return
   */
  public boolean removeInput() {
    int last;

    if (equation.tokenList.size() > 0) {
      /** zpwn: set the last to zero (no effect) to avoid IndexOutOfBoundException*/
      if(equation.tokenList.getLast().getLexeme().length()>0)
        last = equation.tokenList.getLast().getLexeme().length() - 1;
      else
        last = 0;
      /**zpwn: done**/
      
      if (equation.tokenList.getLast().getType() == Token.VARIABLE && last > 0) {
        equation.tokenList.removeLast();
      } else if(equation.tokenList.getLast().getType() == Token.OPERATION && last > 0) {
        equation.tokenList.removeLast();
      } else if(equation.tokenList.getLast().getType() == Token.PARENTHESIS && last > 0) {
        equation.tokenList.removeLast();
      } else if (equation.tokenList.getLast().getType() == Token.REAL && equation.tokenList.getLast().getLexeme().substring(last).equals(".")) {
        equation.tokenList.getLast().setType(Token.INTEGER);
        if (last == 0)
          equation.tokenList.removeLast();
        else
          equation.tokenList.getLast().setLexeme(equation.tokenList.getLast().getLexeme().substring(0, last));
      } else {
        if (last == 0)
          equation.tokenList.removeLast();
        else
          equation.tokenList.getLast().setLexeme(equation.tokenList.getLast().getLexeme().substring(0, last));
      }        
    }
    else return false;
    equation.setIsCorrect(true);
    return true;
  }

  public void newEquation(boolean trueFalse) {
    if (trueFalse == true) {
      equation = new Equation();
    }
  }
  
}