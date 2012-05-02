package laits.parser;

import java.io.Serializable;
import laits.graph.Graph;
import java.util.LinkedList;
import java.util.Stack;
import java.util.StringTokenizer;

/**
 * Equation
 *
 * @author Javier Gonzalez Sanchez
 * @version 20100430
 */
public class Equation implements Serializable{

  public LinkedList<String> codeList;
  public LinkedList<Token> tokenList;
  private boolean isCorrect;
  public LinkedList<Double> value;

  public Equation(){
    this(new LinkedList<String>(), new LinkedList<Token>());
  }

  public Equation(LinkedList<String> codeList, LinkedList<Token> equation) {
    //this(codeList, tokenList, false);
    this.codeList = codeList;
    this.tokenList = equation;
    this.isCorrect = false;
    this.value = new LinkedList<Double>();
  }

  public void setIsCorrect(boolean isCorrect) {
    this.isCorrect = isCorrect;
  }

  public boolean getIsCorrect(){
    return isCorrect;
  }

  /**
   * Error Messages
   *
   * @return all error messages
   */
  public String getCode() {
    String s = "";
    for (int i=0; i<this.codeList.size(); i++) {
      s = s + this.codeList.get(i) + "\n";
    }
    return s;
  }

  @Override
  public String toString() {
    String s = "";
    for (int i = 0; i<tokenList.size(); i++) {
      s += tokenList.get(i).getLexeme();
    }
    return s;
  }

  public double execute(Graph graph) {
    String line, instruction, param;
    double result = 0.0;
    double finalValue = 0.0;
    Stack<String> stack = new Stack<String>();
    for (int i = 0; i<codeList.size(); i++) {
      line = codeList.get(i);
      StringTokenizer st = new StringTokenizer(line, " ");
      instruction = st.nextToken();
      param = st.nextToken();      
      if (instruction.equals("LIT")){
        stack.push(param);
      }
      else if (instruction.equals("LOD")){
        // 1 recorre lista e vertex en graph buscando param
        for (int ii=0; ii<graph.getVertexes().size(); ii++) {
          if (graph.vertex(ii).label!=null && graph.vertex(ii).label.equals(param.replace('_', ' '))) {
            if (graph.vertex(ii).equation != null) {
              //graph.vertex(ii).equation.execute(graph);
              //graph.vertex(ii).execute(graph);
              if (graph.vertex(ii).equation.value.isEmpty()){
                if (graph.vertex(ii).type.equals("stock")) {
                  stack.push(graph.vertex(ii).equation.toString());
                } else{
                  System.out.println("ERROR");
                }
              } else {
                stack.push(graph.vertex(ii).equation.value.getLast() + "");
              }
              //System.out.println(" encontrado y vale: " + graph.vertex(ii).equation.value.getLast());
            } else {
              //System.out.println(" encontrado y es null");
              stack.push("-1");
            }
          }
        }
      } else if (instruction.equals("OPR")){
        double param1=Double.parseDouble(stack.pop());
        double param2=Double.parseDouble(stack.pop());

        if (param.equals("+")){
          result = param2 + param1;
        } else if (param.equals("-")){
          result = param2 - param1;
        } else if (param.equals("*")){
          result = param2 * param1;
        } else if (param.equals("/")){
          result = param2 / param1;
        } else if (param.equals("^")){
          result = Math.pow(param2,param1);
        }          
        stack.push(""+result);
      }
    }

    try {
      finalValue =  Double.parseDouble(stack.pop());
    } catch (Exception e) {
      // Catch exception
    }
    
    return finalValue;
  }

}
