package amt.parser;

import java.util.LinkedList;

/**
 * Parser
 * Recursive, predictive, top-down sintactical analyzer
 *
 * @author Javier Gonzalez
 * @version 20100222
 */
public class Parser {

    private LinkedList<String> errorList;
    private int pointer;
    private Equation equation;

    public Parser() {

        this.pointer = 0;
        this.errorList = new LinkedList<String>();
    }

    public void setEquation(Equation equation) {
        this.equation = equation;
    }

    public Equation getEquation() {
        return equation;
    }

    /**
     * Main method
     *
     * @param e tokenList to analyze
     * @return
     */
    public boolean parse() {
        //tokenList.tokenList = e;
        pointer = 0;
        errorList = new LinkedList<String>();
        equation.codeList = new LinkedList<String>();
        if (equation.tokenList.size() > 0) {
            expresion();
            if (hasMoreToken()) {
                errorList.add("expresion::bad ending");
            }
            if (errorList.size() == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Error Messages
     *
     * @return all error messages
     */
    public String getCode() {
        String s = "";
        for (int i = 0; i < equation.codeList.size(); i++) {
            s = s + equation.codeList.get(i) + "\n";
        }
        return s;
    }

    /**
     * Error Messages
     *
     * @return all error messages
     */
    public String getError() {
        String s = "";
        for (int i = 0; i < errorList.size(); i++) {
            s = s + errorList.get(i) + "\n";
        }
        return s;
    }

    /**
     *
     *
     * @return true if there is a next token
     */
    private boolean nextToken() {
        if (pointer < equation.tokenList.size()) {
            pointer++;
        } else {
            return false;
        }
        return true;
    }

    /**
     *
     *
     * @return true if there is more tokens
     */
    private boolean hasMoreToken() {
        if (pointer < equation.tokenList.size()) {
            return true;
        }
        return false;
    }

    /**
     * Compare a string with the current token
     *
     * @param s
     * @return
     */
    private boolean isCurrentToken(String s) {
        if (hasMoreToken() && equation.tokenList.get(pointer).getLexeme().equals(s)) {
            return true;
        }
        return false;
    }

    /**
     * Compare a type with the type of the current token
     *
     * @param t
     * @return
     */
    private boolean isCurrentToken(int t) {
        if (hasMoreToken() && equation.tokenList.get(pointer).getType() == t) {
            return true;
        }
        return false;
    }

    /**
     * Parse the low level of priority (operators + and -)
     */
    private void expresion() {
        int loop = 0;
        String operator = "";
        do {
            if (loop > 0) {
                if (hasMoreToken()) {
                    operator = equation.tokenList.get(pointer).getLexeme();
                }
                nextToken();
            }
            multi();
            if (loop > 0) {
                equation.codeList.add("OPR " + operator + " AX");
            }
            loop++;
        } while (hasMoreToken() && (isCurrentToken("+") || isCurrentToken("-")));
    }

    private void multi() {
        int loop = 0;
        String operator = "";
        do {
            if (loop > 0) {
                if (hasMoreToken()) {
                    operator = equation.tokenList.get(pointer).getLexeme();
                }
                nextToken();
            }
            power();
            if (loop > 0) {
                equation.codeList.add("OPR " + operator + " AX");
            }
            loop++;
        } while (hasMoreToken() && (isCurrentToken("*") || isCurrentToken("/")));
    }

    private void power() {
        int loop = 0;
        String operator = "";
        do {
            if (loop != 0) {
                nextToken();
            }
            term();
            if (loop > 0) {
                equation.codeList.add("OPR ^ AX");
            }
            loop++;
        } while (hasMoreToken() && isCurrentToken("^"));
    }

    private void term() {
        if (isCurrentToken("(")) {
            nextToken();
            expresion();
            if (!isCurrentToken(")")) {
                errorList.add("term::missing parenthesis");
            } else {
                nextToken();
            }
        } else {
            if (isCurrentToken(Token.VARIABLE) || isCurrentToken(Token.REAL) || isCurrentToken(Token.INTEGER)) {
                if (isCurrentToken(Token.VARIABLE)) {
                    equation.codeList.add("LOD " + equation.tokenList.get(pointer).getLexeme() + " AX");
                } else {
                    equation.codeList.add("LIT " + equation.tokenList.get(pointer).getLexeme() + " AX");
                }
                nextToken();
                return;
            } else {
                errorList.add("term::missing variable or value");
            }
        }
    }
}
