/*
 * CalculationsPanel.java
 *
 * Created on Nov 21, 2010, 10:24:08 AM
 */
package amt.version2;

import amt.comm.CommException;
import amt.data.TaskFactory;
import amt.BlockSocket;
import amt.MetaTutorMsg;
import amt.graph.Edge;
import amt.graph.Graph;
import amt.graph.GraphCanvas;
import amt.graph.Vertex;
import amt.gui.MessageDialog;
import amt.log.Logger;
import amt.parser.Equation;
import amt.parser.Parser;
import amt.parser.Scanner;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.text.DefaultFormatter;

/**
 *
 * @author Megana
 * @author Curt
 * @author zpwn
 */
public class CalculationsPanel extends javax.swing.JPanel implements PropertyChangeListener {

  Vertex currentVertex;
  TabbedGUI parent;
  private TaskFactory server;
  private Logger logger = Logger.getLogger();
  private String eq;
  private Scanner scanner;
  private Parser parser;
  private DefaultListModel jListModel = new DefaultListModel();
  private static Logger logs = Logger.getLogger();
  private Graph graph;
  private GraphCanvas gc;
  private String oldEq;
  private boolean changed = false;
  private boolean initializing = true;
  private boolean undoing = false;
  private boolean typeCorrect = true;
  private boolean givenValueCorrect = false;
  private boolean userEquationCorrect = false;
  private boolean givenValueButtonPreviouslySelected = false;
  private boolean accumulatesButtonPreviouslySelected = false;
  private boolean functionButtonPreviouslySelected = false;
  private ButtonGroup group;
  //private boolean givenValueCorrect = false;
  boolean jListVariablesNotEmpty = false;
  //this is the only way to prevent the equation from deleting
  private String status = "none";
  private LinkedList<String> changes = new LinkedList<String>();
  private LinkedList<String> previousEquationList = new LinkedList<String>();
  private BlockSocket blockSocket = BlockSocket.getBlockSocket();
  private final DecimalFormat inputDecimalFormat = new DecimalFormat("###0.###");

  /** Creates new form CalculationsPanel */
  public CalculationsPanel(TabbedGUI parent, Vertex v, Graph g, GraphCanvas gc) {
    initializing = true;
    initComponents();
    parent.addWindowListener(new java.awt.event.WindowAdapter() {

      public void windowClosing(java.awt.event.WindowEvent evt) {
        formWindowClosing(evt);
      }
    });
    this.currentVertex = v;
    this.parent = parent;
    this.graph = g;
    this.gc = gc;

    try {
      this.server = TaskFactory.getInstance();
    } catch (CommException ex) {
      //Add appropriate logger
//      logger.concatOut(Logger.DEBUG, "InputsPanel.InputsPanel.1", ex.toString());
      logger.out(Logger.DEBUG, "InputsPanel.InputsPanel.1", ex.toString());
    }


    if (v.equation != null) {
      eq = v.equation.toString();
      //System.out.println("initial eq = " + currentVertex.equation.toString());
    } else {
      eq = "";
    }
    //hintLabel.setForeground(new Color(240, 240, 240));
    hintButton.setVisible(false);
    hintLabel.setVisible(false);

    //HELEN SET THE UNDO BUTTON INVISIBLE UNTIL IT WORKS PROPERLY FOR ALL TABS.
    undoButton.setVisible(false);


    group = new ButtonGroup();
    group.add(givenValueButton);
    group.add(accumulatesButton);
    group.add(functionButton);

    scanner = new Scanner(currentVertex.equation);
    parser = new Parser();
    initValues();
    //This method is already inside the initValues
    //updateInputs();

    if (currentVertex.equation != null) {
      updateEquation(true);
    }
    if (currentVertex.getNonNegative() == true) {
      positiveValuesButton.setSelected(true);
    } else {
      positiveValuesButton.setSelected(false);
    }
    initializing = false;
  }

  public void initButtonOnTask() {
    // Depending on what type the current task is, checkButton oand giveUpButton should either be
    // disabled or enabled
    if (server.getActualTask().getType().equalsIgnoreCase("Intro")
            || server.getActualTask().getType().equalsIgnoreCase("Debug")
            || server.getActualTask().getType().equalsIgnoreCase("Construct")
            || server.getActualTask().getType().equalsIgnoreCase("Whole")) {
      enableButtons(true);
    } else if (server.getActualTask().getType().equalsIgnoreCase("Test")) {
      enableButtons(false);
    }
    /*            
    else if (server.getActualTask().getType().equalsIgnoreCase("Whole") && gc.modelHasBeenRun == true) {
    checkButton.setEnabled(true);
    giveUpButton.setEnabled(true);
    }     
     */
    undoButton.setEnabled(undoing);
  }

  public void setEquation(Equation vEq) {
    if (eq != null && currentVertex.type.equals("flow")) {
      eq = vEq.toString();
      updateEquation(true);
    } else {
      this.givenValueTextField.setText(vEq.toString());
    }
  }

  /**
   * author: Curt
   * @return DefaultListModel
   */
  public DefaultListModel getjListModel() {
    return jListModel;
  }

  /*added by zpwn: to make it reuseable*/
  public void enableButtons(boolean flag) {
    checkButton.setEnabled(flag);
    giveUpButton.setEnabled(flag);
//      undoButton.setEnabled(flag);
  }

  /***/
  public void initValues() {
    updateInputs();
    initButtonOnTask();

    //Helen - These two lines were added in the mean time we get the button to work
    //right now the button is not working. 20110607
    positiveValuesButton.setEnabled(false);
    positiveValuesButton.setVisible(false);


    if (currentVertex.type.equals("constant")) {
      //positiveValuesButton.setEnabled(false);
      givenValueButton.setSelected(true);
      if (functionButton.isSelected()) {
        functionButton.setSelected(false);
      }
      if (accumulatesButton.isSelected()) {
        accumulatesButton.setSelected(false);
      }
      givenValueButton.setEnabled(false); //zpwn: added to make sure it's uneditable
      accumulatesButton.setEnabled(false);
      functionButton.setEnabled(false);
      calculatorPanel.setVisible(false);

      //HELEN - TO AVOID THAT USER CHECKS OR GIVEUP IN CALCULATION PANEL
      //IF HE HAS NOT CHECKED OR GIVE IN INPUT PANEL FIRST
      //if (currentVertex.getInputsButtonStatus() == currentVertex.NOSTATUS || currentVertex.getInputsButtonStatus() == currentVertex.WRONG) {
      //  System.out.println("NOSTATUS");
      //  enableButtons(false);
      //} else {
      //  enableButtons(true);
      //}
      //System.out.println("check equation is null");
      //updateInputs();
      if (currentVertex.equation != null) {
        givenValueTextField.setText(currentVertex.equation.toString());
      }

    } else if (currentVertex.type.equals("flow")) {
      givenValueButton.setEnabled(false);
      givenValueLabel.setVisible(false);
      givenValueTextField.setVisible(false);
      accumulatesButton.setEnabled(true);
      functionButton.setEnabled(true);
      functionButton.setSelected(true);
      if (givenValueButton.isSelected()) {
        givenValueButton.setSelected(false);
      }
      if (accumulatesButton.isSelected()) {
        accumulatesButton.setSelected(false);
      }
      calculatorPanel.setVisible(true);
      positiveValuesButton.setEnabled(false);

      //HELEN - TO AVOID THAT USER CHECKS OR GIVEUP IN CALCULATION PANEL
      //IF HE HAS NOT CHECKED OR GIVE IN INPUT PANEL FIRST
      //if (currentVertex.getInputsButtonStatus() == currentVertex.NOSTATUS || currentVertex.getInputsButtonStatus() == currentVertex.WRONG) {
      //  System.out.println("NOSTATUS");
      //  enableButtons(false);
      //} else {
      //  enableButtons(true);
      //}

      //HELEN - updateInputs();
      if (currentVertex.equation != null) {
        jTextAreaEquation.setText(currentVertex.equation.toString());
      }
    } else if (currentVertex.type.equals("stock")) {
      System.out.println("Staring Stock");
      givenValueButton.setEnabled(false);
      givenValueLabel.setText("Initial Value = ");
      accumulatesButton.setEnabled(true);
      accumulatesButton.setSelected(true);
      if (givenValueButton.isSelected()) {
        givenValueButton.setSelected(false);
      }
      if (functionButton.isSelected()) {
        functionButton.setSelected(false);
      }
      functionButton.setEnabled(true);
      calculatorPanel.setVisible(true);
      //positiveValuesButton.setEnabled(true);
      positiveValuesButton.setEnabled(false);

      //HELEN - TO AVOID THAT USER CHECKS OR GIVEUP IN CALCULATION PANEL
      //IF HE HAS NOT CHECKED OR GIVE IN INPUT PANEL FIRST
      //if (currentVertex.getInputsButtonStatus() == currentVertex.NOSTATUS || currentVertex.getInputsButtonStatus() == currentVertex.WRONG) {
      //  System.out.println("NOSTATUS");
      //  enableButtons(false);
      //} else {
      //  enableButtons(true);
      //}

      disableKeyPad();
      addButton.setEnabled(true);
      subtractButton.setEnabled(true);
      //showAllFlows();
      jTextAreaEquation.setText(currentVertex.stockEquation);
      //HELEN - updateInputs();
      if (currentVertex.equation != null) {
        givenValueTextField.setText(currentVertex.equation.toString());
      }
    } else if (currentVertex.type.equals("none") && (!currentVertex.inputsSelected)) {
      //If the user does not define the inputs first he or she could not define any calculation
      givenValueButton.setEnabled(false);
      accumulatesButton.setEnabled(false);
      functionButton.setEnabled(false);

      calculatorPanel.setVisible(false);
      positiveValuesButton.setEnabled(false);
      positiveValuesButton.setSelected(false);

      if (currentVertex.getCalculationsButtonStatus() != currentVertex.GAVEUP) {
        givenValueTextField.setVisible(false);
        givenValueLabel.setVisible(false);
      }


      //enableButtons(false);

      /*} else if (currentVertex.getInputsButtonStatus() == 0){
      //Add here that if the user do not define a type of inputs
      givenValueButton.setEnabled(false);
      accumulatesButton.setEnabled(false);
      functionButton.setEnabled(false);
      
      calculatorPanel.setVisible(false);
      positiveValuesButton.setVisible(false);
      givenValueTextField.setText("Please define a type of input first.");
      givenValueTextField.setVisible(true);
      givenValueLabel.setVisible(false);
      checkButton.setEnabled(false);
      undoButton.setEnabled(false);
      giveUpButton.setEnabled(false);
      }*/
    } else if (currentVertex.type.equals("none") && (currentVertex.inputsSelected)) {

      //if (!currentVertex.isCalculationTypeCorrect) {
      givenValueButton.setEnabled(false);
      accumulatesButton.setEnabled(true);
      functionButton.setEnabled(true);

      calculatorPanel.setVisible(false);
      positiveValuesButton.setEnabled(false);
      positiveValuesButton.setSelected(false);
      givenValueTextField.setVisible(false);
      givenValueLabel.setVisible(false);
      //}

      //HELEN - TO AVOID THAT USER CHECKS OR GIVEUP IN CALCULATION PANEL
      //IF HE HAS NOT CHECKED OR GIVE IN INPUT PANEL FIRST
      //if (currentVertex.getInputsButtonStatus() == currentVertex.NOSTATUS || currentVertex.getInputsButtonStatus() == currentVertex.WRONG) {
      //  System.out.println("NOSTATUS");
      //  enableButtons(false);
      //} else {
      //  enableButtons(true);
      //}

    }

    //Initialize state if the calculations panel is correct, wrong, or the user gave up
    if (currentVertex.GAVEUP == currentVertex.getCalculationsButtonStatus()) {
      System.out.println("Given up");

      //Set the color and disable the elements
      radioButtonPanel.setBackground(new Color(252, 252, 130));
      jTextAreaEquation.setBackground(new Color(252, 252, 130));
      givenValueTextField.setBackground(new Color(252, 252, 130));
      accumulatesButton.setBackground(new Color(252, 252, 130));
      givenValueButton.setBackground(new Color(252, 252, 130));
      functionButton.setBackground(new Color(252, 252, 130));
      givenValueButton.setEnabled(false);
      accumulatesButton.setEnabled(false);
      functionButton.setEnabled(false);
      givenValueTextField.setEnabled(false);
      jListVariables.setEnabled(false);
      jTextAreaEquation.setEnabled(false);
      enableButtons(false);
      hintButton.setEnabled(false);
      disableKeyPad();
      deleteButton.setEnabled(false);
      if (currentVertex.equation != null) {
        givenValueTextField.setText(currentVertex.equation.toString());
      }
    } else if (currentVertex.CORRECT == currentVertex.getCalculationsButtonStatus()) {
      radioButtonPanel.setBackground(new Color(155, 250, 140));
      jTextAreaEquation.setBackground(new Color(155, 250, 140));
      givenValueTextField.setBackground(new Color(155, 250, 140));
      accumulatesButton.setBackground(new Color(155, 250, 140));
      givenValueButton.setBackground(new Color(155, 250, 140));
      functionButton.setBackground(new Color(155, 250, 140));
      givenValueButton.setEnabled(false);
      accumulatesButton.setEnabled(false);
      functionButton.setEnabled(false);
      givenValueTextField.setEnabled(false);
      jListVariables.setEnabled(false);
      jTextAreaEquation.setEnabled(false);
      enableButtons(false);
      hintButton.setEnabled(false);
      disableKeyPad();
      deleteButton.setEnabled(false);
      if (currentVertex.equation != null) {
        givenValueTextField.setText(currentVertex.equation.toString());
      }
    } else if (currentVertex.WRONG == currentVertex.getCalculationsButtonStatus()) {
      if (currentVertex.type.equals(currentVertex.correctType)) {
        radioButtonPanel.setBackground(new Color(155, 250, 140));
        accumulatesButton.setBackground(new Color(155, 250, 140));
        givenValueButton.setBackground(new Color(155, 250, 140));
        functionButton.setBackground(new Color(155, 250, 140));
        givenValueButton.setEnabled(false);
        accumulatesButton.setEnabled(false);
        functionButton.setEnabled(false);
      } else {
        radioButtonPanel.setBackground(Color.pink);
        accumulatesButton.setBackground(Color.pink);
        givenValueButton.setBackground(Color.pink);
        functionButton.setBackground(Color.pink);
      }

      if (!currentVertex.isGivenValueCorrect) {
        givenValueTextField.setBackground(Color.pink);
      } else {
        givenValueTextField.setBackground(new Color(155, 250, 140));
        givenValueTextField.setEnabled(false);
      }

      if (!currentVertex.isEquationCorrect) {
        jTextAreaEquation.setBackground(Color.pink);
      } else {
        jTextAreaEquation.setBackground(new Color(155, 250, 140));
        jTextAreaEquation.setEnabled(false);
      }
    }

    if (accumulatesButton.isSelected()) {
      valuesLabel.setText("Next Value = Current Value +");
    } else if (functionButton.isSelected()) {
      valuesLabel.setText("Next Value = ");
    }

    givenValueTextField.addPropertyChangeListener(this);
  }

  /**
   * This method is used to initialize the jTextAreaEquation for a stock
   */
  private void showAllFlows() {
    String currentFlows = "";
    
    for (int i = 0; i < currentVertex.inedges.size(); i++) {

      if (currentVertex.inedges.get(i).edgetype.equals("flowlink")) {
        System.out.println(">>>inlinks");
        if (currentFlows.equals("")) {
          currentFlows = currentVertex.inedges.get(i).start.nodeName.replace(" ", "_");
        } else {
          if(!jTextAreaEquation.getText().contains("+"))
          {
             System.out.println("currentflows: "+currentFlows);
             currentVertex.inedges.get(i).edgetype = "regularlink";
             currentVertex.inedges.get(i).showInListModel = true;
             updateInputs();
             System.out.println(">>>"+currentVertex.inedges.get(i).start.nodeName);
          }
          else
            currentFlows += " + " + currentVertex.inedges.get(i).start.nodeName.replace(" ", "_");
        }
      }
    }
    if(currentVertex.correctType.equals(currentVertex.type))
    {
    for (int i = 0; i < currentVertex.outedges.size(); i++) {
//      System.out.println(">>>outlinksstart:" + currentVertex.outedges.get(i).start.nodeName);
//      System.out.println(">>>outlinkscc:" + currentVertex.outedges.get(i).end.nodeName);
      if (currentVertex.outedges.get(i).edgetype.equals("flowlink") && !currentVertex.nodeName.equals(currentVertex.outedges.get(i).start.nodeName)) {
//        System.out.println(">>>outlinks:" + currentVertex.outedges.get(i).end.nodeName);
        if (currentFlows.equals("")) {
          
          currentFlows = "- " + currentVertex.outedges.get(i).end.nodeName.replace(" ", "_");
        } else {
          if(!jTextAreaEquation.getText().contains("+"))
          {
             currentVertex.outedges.get(i).edgetype = "regularlink";
             currentVertex.outedges.get(i).showInListModel = true;
             updateInputs();
          }
          else
            currentFlows += " - " + currentVertex.outedges.get(i).end.nodeName.replace(" ", "_");
            
        }
      }
    }
    }
    else
    {
      System.out.println("NOT EQUAL:"+jTextAreaEquation.getText());
    }
    //System.out.println("currentFlows:"+currentFlows);
//    if(currentVertex.correctType.equals(currentVertex.type))
    jTextAreaEquation.setText(currentFlows);
  }

  /**
   * Update displayed equation in the text area and the error messages
   *
   * @param notError
   * @return
   */
  public boolean updateEquation(boolean notError) {
    //System.out.println("scanner.getEquation().toString() "+scanner.getEquation().toString());
    if (!currentVertex.type.equals("stock") && !currentVertex.type.equals("constant")) {
      jTextAreaEquation.setText(scanner.getEquation().toString());
    } else {
      //System.out.println("no stock");
      if (!accumulatesButton.isSelected()) {
        givenValueTextField.setText(scanner.getEquation().toString());
      }
    }
    if (notError) {
      parser.setEquation(scanner.getEquation());
      if (parser.parse() || jTextAreaEquation.getText().equals("")) {
        jTextAreaEquation.setForeground(Color.BLACK);
        currentVertex.equation = parser.getEquation();
        return true;
      }
    }
    return false;
  }

  public void update() {
    //if (currentVertex.getInputsButtonStatus() == currentVertex.NOSTATUS) {
    //  enableButtons(false);
    //}
    //else
    //{
    //  enableButtons(true);
    //}
    //if (currentVertex.getCalculationsButtonStatus() != currentVertex.GAVEUP && currentVertex.getCalculationsButtonStatus() != currentVertex.CORRECT) {
    if (parent.getInputsPanel().getValueButtonSelected() == true) {
      givenValueButton.setSelected(true);
      givenValueLabel.setText("Given value = ");
      givenValueTextField.setVisible(true);
      givenValueLabel.setVisible(true);
      positiveValuesButton.setSelected(false);
      positiveValuesButton.setEnabled(false);
      accumulatesButton.setEnabled(false);
      accumulatesButton.setSelected(false);
      functionButton.setEnabled(false);
      functionButton.setSelected(false);
      calculatorPanel.setVisible(false);

    } else if (parent.getInputsPanel().getInputsButtonSelected() == true) {
      if (!currentVertex.isCalculationTypeCorrect) {
        group.clearSelection();
        givenValueButton.setEnabled(false);
        accumulatesButton.setEnabled(true);
        functionButton.setEnabled(true);
        calculatorPanel.setVisible(false);
        givenValueTextField.setVisible(false);
        givenValueLabel.setVisible(false);
      }
    }
    //}

  }

  public void resetColors(boolean typeChange) {
    jTextAreaEquation.setBackground(Color.WHITE);
    jTextAreaEquation.setEnabled(true);
    jListVariables.setBackground(Color.WHITE);
    // We want to allow JTextAreaEquations to be able to be changed, even if it has first been
    // checked and found to be correct. That way, if the user goes back and changes the inputs
    // the current equation will need to be reset
    currentVertex.isEquationCorrect = false;
    System.out.println((currentVertex.isGivenValueCorrect) ? "true" : "false");

    if (!currentVertex.isGivenValueCorrect || typeChange) {
      givenValueTextField.setBackground(Color.WHITE);
      givenValueTextField.setEnabled(true);
      currentVertex.isGivenValueCorrect = false;
    } else {
      givenValueTextField.setBackground(new Color(155, 250, 140));
    }

    if (!currentVertex.isCalculationTypeCorrect || typeChange) {
      radioButtonPanel.setBackground(new Color(238, 238, 238));
      currentVertex.isinputsTypeCorrect = false;
      if (currentVertex.type.equalsIgnoreCase("stock") || currentVertex.type.equalsIgnoreCase("flow")) {
        givenValueButton.setSelected(false);
        givenValueButton.setEnabled(false);
        accumulatesButton.setEnabled(true);
        functionButton.setEnabled(true);
        accumulatesButton.setSelected(false);
        functionButton.setSelected(false);
        currentVertex.isCalculationTypeCorrect = false;

        if (currentVertex.type.equalsIgnoreCase("flow")) {
          enableKeyPad();
          deleteButton.setEnabled(true);
        } else if (currentVertex.type.equalsIgnoreCase("stock")) {
          disableKeyPad();
          addButton.setEnabled(true);
          subtractButton.setEnabled(true);
          deleteButton.setEnabled(true);
        }
      }
    } else {
      radioButtonPanel.setBackground(new Color(155, 250, 140));
    }

    currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);
  }

  public void resetColors() {
    jTextAreaEquation.setBackground(Color.WHITE);
    jTextAreaEquation.setEnabled(true);
    jListVariables.setBackground(Color.WHITE);

    //givenValueTextField.setBackground(Color.WHITE);
    //radioButtonPanel.setBackground(Color.WHITE);

    // We want to allow JTextAreaEquations to be able to be changed, even if it has first been
    // checked and found to be correct. That way, if the user goes back and changes the inputs
    // the current equation will need to be reset
    currentVertex.isEquationCorrect = false;
    System.out.println((currentVertex.isGivenValueCorrect) ? "true" : "false");

    if (currentVertex.getCalculationsButtonStatus() != currentVertex.NOSTATUS) {
      if (!currentVertex.isGivenValueCorrect) {
        givenValueTextField.setBackground(Color.WHITE);
        currentVertex.isGivenValueCorrect = false;
      } else {
        givenValueTextField.setBackground(new Color(155, 250, 140));
      }

      if (!currentVertex.isCalculationTypeCorrect) {
        radioButtonPanel.setBackground(new Color(238, 238, 238));
        currentVertex.isinputsTypeCorrect = false;
      } else {
        radioButtonPanel.setBackground(new Color(155, 250, 140));
      }

      currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);
    }
  }

  private void resetGraphStatus() {
    Vertex v = new Vertex();
    int firstNodeWithNoStatus = -1;
    int firstIndexOfNoStatus = -1;
    boolean restart = true;
    int[] nodeStatus = new int[graph.getVertexes().size()];

    logger.concatOut(Logger.ACTIVITY, "No message", "Reset colors.");
    while (restart) {
      currentVertex.setGraphsButtonStatus(v.NOSTATUS);
      for (int a = 0; a < graph.getVertexes().size(); a++) {
        v = (Vertex) graph.getVertexes().get(a);
//        System.out.println(v.nodeName);
        if (v.getGraphsButtonStatus() == v.NOSTATUS) {
          if (firstNodeWithNoStatus == -1) {
            firstNodeWithNoStatus = a;
          }
          if (v.type.equals("constant") || v.type.equals("flow")) {
            for (int i = 0; i < v.inedges.size(); i++) {
              Vertex current = (Vertex) v.inedges.get(i).end;
              current.setGraphsButtonStatus(current.NOSTATUS);
            }
            for (int i = 0; i < v.outedges.size(); i++) {
              Vertex current = (Vertex) v.outedges.get(i).end;
              current.setGraphsButtonStatus(current.NOSTATUS);
            }
          } else if (v.type.equals("stock")) {
            for (int i = 0; i < v.outedges.size(); i++) {
              Vertex current = (Vertex) v.outedges.get(i).end;
              current.setGraphsButtonStatus(current.NOSTATUS);
            }
          }
        }
      }

      for (int i = 0; i < graph.getVertexes().size(); i++) {
        nodeStatus[i] = ((Vertex) graph.getVertexes().get(i)).getGraphsButtonStatus();
        if (nodeStatus[i] == currentVertex.NOSTATUS) {
          if (firstIndexOfNoStatus == -1) {
            firstIndexOfNoStatus = i;
          }
        }
      }

      restart = (firstIndexOfNoStatus != firstNodeWithNoStatus) ? true : false;

      firstIndexOfNoStatus = -1;
      firstNodeWithNoStatus = -1;
    }
  }

  public void clearEquationArea(boolean typeChange) {
    jTextAreaEquation.setText("");
    if (!currentVertex.isGivenValueCorrect || typeChange) {
      givenValueTextField.setText("");
    }
    scanner.newEquation(true);
  }

  public void clearEquationArea() {
    jTextAreaEquation.setText("");
    if (!currentVertex.isGivenValueCorrect) {
      givenValueTextField.setText("");
    }
    scanner.newEquation(true);
  }

  public void showThatJListModelHasNoInputs() {
    jListModel.clear();
    jListModel.add(0, "This node does not have any inputs, please");
    jListModel.add(1, "go back to the Inputs Tab and choose at");
    jListModel.add(2, "least one input.");
  }

  public void repaintJListVariables() {
    jListVariables.repaint();
  }

  public void updateInputs() {
    LinkedList<String> inputList = new LinkedList<String>();
    if (currentVertex.getCalculationsButtonStatus() != currentVertex.GAVEUP && currentVertex.getCalculationsButtonStatus() != currentVertex.CORRECT) {
      //display the inflows - used for both stock and flow
      for (int i = 0; i < currentVertex.inedges.size(); i++) {
        if (currentVertex.inedges.get(i).showInListModel != false) {
          inputList.add(currentVertex.inedges.get(i).start.nodeName);
          jListVariablesNotEmpty = true;
        }
      }
      jListModel.clear();
      //Build the displayable label of allowed variables in GUI:
      if (currentVertex.inedges.size() == 0) {
        showThatJListModelHasNoInputs();
        jListVariables.setEnabled(false);
        jListVariables.setOpaque(false);
      } else {
        for (int j = 0; j < inputList.size(); j++) {
          jListModel.add(j, inputList.get(j));
        }

        jListVariables.setEnabled(true);
      }

      jListVariables.repaint();
    }
  }

  //This is used when the node is a stock
  private void disableKeyPad() {
    multiplyButton.setEnabled(false);
    leftParenthesisButton.setEnabled(false);
    subtractButton.setEnabled(false);
    divideButton.setEnabled(false);
    addButton.setEnabled(false);
    rightParenthesisButton.setEnabled(false);
  }

  //This is used when the node is a flow
  private void enableKeyPad() {
    multiplyButton.setEnabled(true);
    leftParenthesisButton.setEnabled(true);
    subtractButton.setEnabled(true);
    divideButton.setEnabled(true);
    addButton.setEnabled(true);
    rightParenthesisButton.setEnabled(true);
  }

  private void updateEquations() {
    if (currentVertex.type.equals("constant") || currentVertex.type.equals("stock")) {
      parser.setEquation(null);
      try {
        givenValueTextField.commitEdit();
      } catch (ParseException ex) {
        //Add correct logger later
      }
      if (!initializing) {
        //String previousEquation = currentVertex.equation.toString();
        //Removes all input from scanner
        if (currentVertex.equation != null) {
          if (scanner.getEquation() != null && !currentVertex.equation.tokenList.isEmpty()) {
            while (scanner.removeInput());
          }
        } else {
          System.out.println("NULL");
        }
        updateEquation(scanner.addInput(givenValueTextField.getText(), Scanner.DIGIT));
        if (currentVertex.equation != null) {
          logger.concatOut(Logger.ACTIVITY, "CalculationsPanel.propertyChange.1", currentVertex.equation.toString());
        }
        gc.repaint();
        //changes.add("Equation changed from: " + previousEquation);
      }

      if (currentVertex.type.equals("stock")) {
        currentVertex.stockEquation = jTextAreaEquation.getText();
      }
    }
  }

  private void formWindowClosing(java.awt.event.WindowEvent evt) {
    status = "closing";
    updateEquations();
    gc.repaint();
  }

  private boolean checkEquation() {
    boolean isCorrect = false;

    userEquationCorrect = false;
    givenValueCorrect = false;

    //Verify the vertex is the correct type
    try {
      if (currentVertex.equation != null) {
        if (currentVertex.type.equals(currentVertex.correctType)) {
          //Verify the vertex has the correct equation
          if (currentVertex.type.equals("constant") && !currentVertex.equation.toString().equals("")) {
            String userEquation = currentVertex.equation.toString();
            double userAnswer = Double.parseDouble(userEquation);
            double correctAnswer = 0.0;
            if (currentVertex.correctEquation != null) {
              correctAnswer = Double.parseDouble(currentVertex.correctEquation.toString());
            }

            if (userAnswer == correctAnswer) {
              isCorrect = true;
              givenValueCorrect = true;
            }
            //System.out.println("WTF?");
          } else if (currentVertex.type.equals("flow") || currentVertex.type.equals("auxiliary")) {
//            System.out.println("flow");
            LinkedList<String> userAnswer = splitEquation(currentVertex.equation.toString());
            //System.out.println("CORRECT EQUATION: " + currentVertex.correctEquation.toString());
            LinkedList<String> correctAnswer = splitEquation(currentVertex.correctEquation.toString());

            /***********************************************
             * NOTE: Add functionality for parenthesis later
             ***********************************************/
            String userAnswerString = "";
            String correctAnswerString = "";
//            System.out.println("correctAnswer:"+correctAnswer.toString());
            if (userAnswer.size() == correctAnswer.size()) {
              while (!userAnswer.isEmpty()) {
                //System.out.println("hjj "+correctC)
                //If both contain the element being looked at, add this element to each string and remove it from the linked list
                if (userAnswer.contains("/") || userAnswer.contains("-")) {
                  System.out.println("correct answer contains /-");
                  if (correctAnswer.contains(userAnswer.get(0))) {
                    String added = userAnswer.get(0);
                    String correct = correctAnswer.get(0);
                    userAnswerString += added;
                    correctAnswerString += correct;
                    userAnswer.remove(added);
                    correctAnswer.remove(correct);
                    userEquationCorrect = true;
//                  System.out.println("1userAnswerString:"+userAnswerString);
//              System.out.println("1correctAnswerString:"+correctAnswerString);
                  } else {
                    //Both equations do not contain all of the same elements, so the user's is not correct
                    userEquationCorrect = false;
//                  System.out.println("userAnswerString:"+userAnswerString);
//                  System.out.println("correctAnswerString:"+correctAnswerString);
                    return false;
                  }
                } else {
                  if (correctAnswer.contains(userAnswer.get(0))) {
                    String added = userAnswer.get(0);
//                  String correct = correctAnswer.get(0);
                    userAnswerString += added;
                    correctAnswerString += added;
                    userAnswer.remove(added);
                    correctAnswer.remove(added);
                    userEquationCorrect = true;
//                  System.out.println("1userAnswerString:"+userAnswerString);
//              System.out.println("1correctAnswerString:"+correctAnswerString);
                  } else {
                    //Both equations do not contain all of the same elements, so the user's is not correct
                    userEquationCorrect = false;
//                  System.out.println("userAnswerString:"+userAnswerString);
//                  System.out.println("correctAnswerString:"+correctAnswerString);
                    return false;
                  }
                }
              }
              
              //Verify that both strings are the same
              if (userAnswerString.equals(correctAnswerString)) {
                userEquationCorrect = true;
                isCorrect = true;
              } else {
                userEquationCorrect = false;
              }
            }
            
          } else if (currentVertex.type.equals("stock")) {
            //Check that the equation is correct
            if (!currentVertex.equation.toString().equals("")) {
              String userEquation = currentVertex.equation.toString();
              //System.out.println(" mmcurrentVertex.equation.toString() :"+currentVertex.equation.toString());
              //currentVertex.equation.toString();
              double userAnswer = -1;
              try {
                userAnswer = Double.parseDouble(userEquation);
                System.out.println("userAnswer: " + userAnswer);
              } catch (NumberFormatException nfe) {
                System.out.println("Invalid Initial value");
              }
              double correctAnswer = 0.0;
              if (currentVertex.correctEquation != null) {
                correctAnswer = Double.parseDouble(currentVertex.correctEquation.toString());
                System.out.println("correctAnswer: " + correctAnswer);
              }
              /*added by zpwn: to check the given value is correct*/
              if (userAnswer == correctAnswer) {
                givenValueCorrect = true;
                //isCorrect = true;
              } else {
                givenValueCorrect = false;
              }
              /* done*/
            }

            //Check whether the stock's flows are correct
            //System.out.println("Full user equation: " + jTextAreaEquation.getText().trim());
            String[] stockEquation = jTextAreaEquation.getText().trim().split(" ");
            //Find the correct flows
            LinkedList<String> correctFlows = new LinkedList<String>();
            String[] inputs = currentVertex.correctInputs.split(",");
            String[] outputs = currentVertex.correctOutputs.split(",");
            for (int i = 0; i < inputs.length; i++) {
              if (inputs[i].trim().startsWith("flowlink")) {
                String toAdd = inputs[i].trim().replace("flowlink - ", "");
                correctFlows.add(toAdd);
              }
            }
            for (int i = 0; i < outputs.length; i++) {
              if (outputs[i].trim().startsWith("flowlink")) {
                String toAdd = outputs[i].trim().replace("flowlink - ", "");
                correctFlows.add("- " + toAdd);
              }
            }

            boolean allAdded = true; //used after to verify that all the correct values were added
            boolean inflow = true;
            LinkedList<String> userFlows = new LinkedList<String>();
            //Verify that the user's flows are supposed to be there
            for (int i = 0; i < stockEquation.length; i++) {
              if (inflow == true) {
                if (stockEquation[i].equals("+")) {
                  continue;
                } else if (stockEquation[i].equals("-")) {
                  inflow = false;
                  continue;
                } else {
                  //the value in userflow is a node
                  if (correctFlows.contains(stockEquation[i].replace("_", " "))) {
                    userFlows.add(stockEquation[i].replace("_", " "));
                    continue;
                  } else {
                    //System.out.println("Problem occurred where inflow = " + inflow + " and stockEquation[i] was " + stockEquation[i]);
                    allAdded = false;
                    break;
                  }
                }
              } else if (inflow == false) {
                if (stockEquation[i].equals("-")) {
                  continue;
                } else if (stockEquation[i].equals("+")) {
                  inflow = true;
                  continue;
                } else {
                  //the value in userflow is a node
                  if (correctFlows.contains("- " + stockEquation[i].replace("_", " "))) {
                    userFlows.add("- " + stockEquation[i].replace("_", " "));
                    continue;
                  } else {
                    //System.out.println("Problem occurred where inflow = " + inflow + " and stockEquation[i] was " + stockEquation[i]);
                    allAdded = false;
                    break;
                  }
                }
              }
            }

            //Ensure that all flows in the correct equation
            for (int i = 0; i < correctFlows.size(); i++) {
              if (!userFlows.contains(correctFlows.get(i))) {
                allAdded = false;
                userEquationCorrect = false;
                isCorrect = false;
              }
            }
            if (allAdded == false) {
              //System.out.println("Problem with the user's stock equation");
              isCorrect = false;
            } else {
              //isCorrect = true; //zpwn : it is not right to set it true;
              userEquationCorrect = true;
            }
          }
          /*added by zpwn: to make sure all correct and then set isCorrect true*/
          if (givenValueCorrect && userEquationCorrect) {
            isCorrect = true;
          }
          /*done*/
        } else {
          typeCorrect = false;
        }
      }

      if (!currentVertex.type.equals(currentVertex.correctType)) {
        typeCorrect = false;
      }

    } catch (Exception e) {
      // Catch any exception that might come up
    }

    currentVertex.isGivenValueCorrect = givenValueCorrect;
    currentVertex.isCalculationTypeCorrect = typeCorrect;
    currentVertex.isEquationCorrect = userEquationCorrect;

    return isCorrect;
  }

  public LinkedList<String> splitEquation(String equation) {
    LinkedList<String> parsed = new LinkedList<String>();
    String variable = "";
    for (int i = 0; i < equation.length(); i++) {
      char currentChar = equation.charAt(i);
      if (currentChar == '+' || currentChar == '*' || currentChar == '-' || currentChar == '/' || currentChar == '^') {
        if (!variable.equals("")) {
          parsed.add(variable);
          //System.out.println(variable);
          variable = "";
        }
        parsed.add(currentChar + "");
        //System.out.println(currentChar);
      } else {
        variable += currentChar;
      }
    }
    if (!variable.equals("")) {
      parsed.add(variable);
      //System.out.println(variable);
      variable = "";
    }
    return parsed;
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonPanel = new javax.swing.JPanel();
        hintButton = new javax.swing.JButton();
        checkButton = new javax.swing.JButton();
        undoButton = new javax.swing.JButton();
        giveUpButton = new javax.swing.JButton();
        contentPanel = new javax.swing.JPanel();
        givenValueLabel = new javax.swing.JLabel();
        givenValueTextField = new javax.swing.JFormattedTextField();
        radioButtonPanel = new javax.swing.JPanel();
        givenValueButton = new javax.swing.JRadioButton();
        accumulatesButton = new javax.swing.JRadioButton();
        functionButton = new javax.swing.JRadioButton();
        quantityLabel = new javax.swing.JLabel();
        needInputLabel = new javax.swing.JLabel();
        positiveValuesButton = new javax.swing.JCheckBox();
        calculatorPanel = new javax.swing.JPanel();
        valuesLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaEquation = new javax.swing.JTextArea();
        availableInputsLabel = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jListVariables = new javax.swing.JList();
        deleteButton = new javax.swing.JButton();
        addButton = new javax.swing.JButton();
        subtractButton = new javax.swing.JButton();
        multiplyButton = new javax.swing.JButton();
        divideButton = new javax.swing.JButton();
        leftParenthesisButton = new javax.swing.JButton();
        rightParenthesisButton = new javax.swing.JButton();
        hintPanel = new javax.swing.JPanel();
        hintLabel = new javax.swing.JLabel();

        buttonPanel.setMaximumSize(new java.awt.Dimension(577, 92));
        buttonPanel.setMinimumSize(new java.awt.Dimension(577, 92));

        hintButton.setText("Hint");
        hintButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hintButtonActionPerformed(evt);
            }
        });

        checkButton.setText("Check");
        checkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkButtonActionPerformed(evt);
            }
        });

        undoButton.setText("Undo");
        undoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                undoButtonActionPerformed(evt);
            }
        });

        giveUpButton.setText("Give Up");
        giveUpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                giveUpButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout buttonPanelLayout = new javax.swing.GroupLayout(buttonPanel);
        buttonPanel.setLayout(buttonPanelLayout);
        buttonPanelLayout.setHorizontalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(hintButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkButton)
                .addGap(5, 5, 5)
                .addComponent(giveUpButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(undoButton)
                .addContainerGap(587, Short.MAX_VALUE))
        );
        buttonPanelLayout.setVerticalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkButton)
                    .addComponent(giveUpButton)
                    .addComponent(undoButton)
                    .addComponent(hintButton))
                .addContainerGap(38, Short.MAX_VALUE))
        );

        givenValueLabel.setText("<html><b>Given value = </b></html>");

        givenValueTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(inputDecimalFormat)));
        givenValueTextField.setDisabledTextColor(new java.awt.Color(102, 102, 102));
        givenValueTextField.setFocusLostBehavior(javax.swing.JFormattedTextField.PERSIST);
        ((DefaultFormatter)givenValueTextField.getFormatter()).setAllowsInvalid( true );
        givenValueTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                givenValueTextFieldKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                givenValueTextFieldKeyTyped(evt);
            }
        });

        radioButtonPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        givenValueButton.setBackground(new java.awt.Color(255, 255, 255));
        givenValueButton.setText("has a given value");
        givenValueButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                givenValueButtonActionPerformed(evt);
            }
        });

        accumulatesButton.setBackground(new java.awt.Color(255, 255, 255));
        accumulatesButton.setText("accumulates the values of its inputs");
        accumulatesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                accumulatesButtonActionPerformed(evt);
            }
        });

        functionButton.setBackground(new java.awt.Color(255, 255, 255));
        functionButton.setText("is a function of its inputs values");
        functionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                functionButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout radioButtonPanelLayout = new javax.swing.GroupLayout(radioButtonPanel);
        radioButtonPanel.setLayout(radioButtonPanelLayout);
        radioButtonPanelLayout.setHorizontalGroup(
            radioButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(radioButtonPanelLayout.createSequentialGroup()
                .addGroup(radioButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(givenValueButton)
                    .addComponent(functionButton)
                    .addComponent(accumulatesButton))
                .addContainerGap(333, Short.MAX_VALUE))
        );
        radioButtonPanelLayout.setVerticalGroup(
            radioButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(radioButtonPanelLayout.createSequentialGroup()
                .addComponent(givenValueButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(accumulatesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(functionButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        quantityLabel.setText("The node's quantity:");

        positiveValuesButton.setText("positive values only");

        javax.swing.GroupLayout contentPanelLayout = new javax.swing.GroupLayout(contentPanel);
        contentPanel.setLayout(contentPanelLayout);
        contentPanelLayout.setHorizontalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentPanelLayout.createSequentialGroup()
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(quantityLabel)
                    .addComponent(needInputLabel)
                    .addComponent(positiveValuesButton)
                    .addGroup(contentPanelLayout.createSequentialGroup()
                        .addComponent(givenValueLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(givenValueTextField))
                    .addComponent(radioButtonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        contentPanelLayout.setVerticalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(quantityLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addComponent(positiveValuesButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(givenValueLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(givenValueTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1, 1, 1)
                .addComponent(needInputLabel)
                .addGap(261, 261, 261))
        );

        positiveValuesButton.getAccessibleContext().setAccessibleName("positiveValuesButton");

        valuesLabel.setText("Next Value = Current Value +");

        jTextAreaEquation.setColumns(20);
        jTextAreaEquation.setEditable(false);
        jTextAreaEquation.setLineWrap(true);
        jTextAreaEquation.setRows(5);
        jTextAreaEquation.setDisabledTextColor(new java.awt.Color(102, 102, 102));
        jScrollPane1.setViewportView(jTextAreaEquation);

        availableInputsLabel.setText("Available Inputs:");

        jListVariables.setModel(jListModel);
        jListVariables.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jListVariables.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jListVariablesMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jListVariables);

        deleteButton.setText("<< Delete");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        addButton.setText("+");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonActionPerformed(evt);
            }
        });

        subtractButton.setText("-");
        subtractButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonActionPerformed(evt);
            }
        });

        multiplyButton.setFont(new java.awt.Font("Tahoma", 0, 12));
        multiplyButton.setText("x");
        multiplyButton.setActionCommand("*");
        multiplyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                multiplyButtonActionPerformed(evt);
            }
        });

        divideButton.setText("/");
        divideButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonActionPerformed(evt);
            }
        });

        leftParenthesisButton.setText("(");
        leftParenthesisButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonActionPerformed(evt);
            }
        });

        rightParenthesisButton.setText(")");
        rightParenthesisButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout calculatorPanelLayout = new javax.swing.GroupLayout(calculatorPanel);
        calculatorPanel.setLayout(calculatorPanelLayout);
        calculatorPanelLayout.setHorizontalGroup(
            calculatorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(calculatorPanelLayout.createSequentialGroup()
                .addGroup(calculatorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(availableInputsLabel))
                .addGap(32, 32, 32)
                .addGroup(calculatorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(valuesLabel)
                    .addGroup(calculatorPanelLayout.createSequentialGroup()
                        .addGroup(calculatorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(multiplyButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(calculatorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(subtractButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(divideButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(calculatorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(calculatorPanelLayout.createSequentialGroup()
                                .addComponent(leftParenthesisButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rightParenthesisButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(deleteButton, 0, 0, Short.MAX_VALUE)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(69, Short.MAX_VALUE))
        );
        calculatorPanelLayout.setVerticalGroup(
            calculatorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(calculatorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(calculatorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(availableInputsLabel)
                    .addComponent(valuesLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(calculatorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(calculatorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(subtractButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(calculatorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(multiplyButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(divideButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(leftParenthesisButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rightParenthesisButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        hintPanel.setMaximumSize(new java.awt.Dimension(567, 45));
        hintPanel.setMinimumSize(new java.awt.Dimension(567, 45));

        hintLabel.setBackground(new java.awt.Color(0, 0, 0));
        hintLabel.setText("Hint");

        javax.swing.GroupLayout hintPanelLayout = new javax.swing.GroupLayout(hintPanel);
        hintPanel.setLayout(hintPanelLayout);
        hintPanelLayout.setHorizontalGroup(
            hintPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(hintPanelLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(hintLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 531, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(314, Short.MAX_VALUE))
        );
        hintPanelLayout.setVerticalGroup(
            hintPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(hintPanelLayout.createSequentialGroup()
                .addComponent(hintLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(hintPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(buttonPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(calculatorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(contentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(976, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(contentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addComponent(calculatorPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(hintPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void multiplyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_multiplyButtonActionPerformed
      if (!initializing) {
        //change the calculation and graph status so that the (c) and (g) circles on the vertex turns white
        currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);
        resetGraphStatus();
        //reset background colors
        resetColors();
        if (currentVertex.equation != null) {
          previousEquationList.add(currentVertex.equation.toString());
        } else {
          previousEquationList.add("");
        }
        changes.add("Changed equation from previous");
        //String previous = currentVertex.equation.toString();
        updateEquation(scanner.addInput("*", Scanner.DIGIT));
        //changes.add("Changed equation from: " + previous);
        changed = true;
      }
}//GEN-LAST:event_multiplyButtonActionPerformed

    private void ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonActionPerformed
      if (!initializing) {
        changed = true;
        //change the calculation and graph status so that the (c) and (g) circles on the vertex turns white
        currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);
        resetGraphStatus();
        //reset background colors
        resetColors();
        undoButton.setEnabled(true);

        if (!currentVertex.type.equalsIgnoreCase("Stock")) {
          if (currentVertex.equation != null) {
            previousEquationList.add(currentVertex.equation.toString());
          } else {
            previousEquationList.add("");
          }
          changes.add("Changed equation from previous");
          //String previous = currentVertex.equation.toString();
          updateEquation(scanner.addInput(((javax.swing.JButton) evt.getSource()).getText(), Scanner.DIGIT));

        } else {
          String[] split = jTextAreaEquation.getText().split(" ");
          if(split[split.length-1].equals("-") ||split[split.length-1].equals("+"))
          {
//            MessageDialog.showMessageDialog(parent, true, "", graph);
          }
          else
          {
//          System.out.println(jTextAreaEquation.getText() + " " + ((javax.swing.JButton) evt.getSource()));
          jTextAreaEquation.setText(jTextAreaEquation.getText() + " " + ((javax.swing.JButton) evt.getSource()).getText() + " ");
          jTextAreaEquation.setForeground(Color.BLACK);

          changes.add("Stock input operation added: " + (((javax.swing.JButton) evt.getSource()).getText()));
          }
          }
        //changes.add("Added: " + ((javax.swing.JButton) evt.getSource()).getText());
      }
}//GEN-LAST:event_ButtonActionPerformed
  /**
   * @author curt
   * @param evt
   */
    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
      if (!initializing) {
        //change the calculation and graph status so that the (c) and (g) circles on the vertex turns white
        currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);
        resetGraphStatus();
        //reset background colors
        //resetColors();

        undoButton.setEnabled(true);
        jTextAreaEquation.setBackground(Color.WHITE);
        jListVariables.setBackground(Color.WHITE);

        if (!currentVertex.type.equalsIgnoreCase("stock")) {
          //String previous = currentVertex.equation.toString();
          if (undoing == false) {
            if (currentVertex.equation != null) {
              previousEquationList.add(currentVertex.equation.toString());
//              System.out.println("previousEquationList.add(currentVertex.equation.toString()):" + currentVertex.equation.toString());
            } else {
              previousEquationList.add("");
//              System.out.println("previousEquationList.add(\"\")");
            }
            changes.add("Changed equation from previous");
          }
//          System.out.println("000000: "+jTextAreaEquation.getText());
          String currentEquation = jTextAreaEquation.getText();

          if (!currentEquation.equals("")) {
            for (int i = 0; i < currentVertex.inedges.size(); i++) {
              if (currentVertex.inedges.get(i).start.nodeName.equals(currentVertex.equation.tokenList.getLast().getLexeme().replace("_", " "))) {
                currentVertex.inedges.get(i).showInListModel = true;
              }
            }
//            System.out.println("before updateInputs: "+jTextAreaEquation.getText());
            updateInputs();
//            System.out.println("VVV: "+jTextAreaEquation.getText());
          }

          if (!jTextAreaEquation.getText().equals("")) {
            updateEquation(scanner.removeInput());
          }
//          System.out.println("IDS: "+jTextAreaEquation.getText());
          //changes.add("Changed equation from: " + previous);
        } else {
          String currentText = jTextAreaEquation.getText();
          String operator1 = "";
          String operator = "";
          //String temp ="";
          if (!currentText.equals("")) {
            String[] splitText = currentText.trim().split(" ");
            //When there's only one positive inflow
            if (splitText.length == 1) {
              for (int i = 0; i < currentVertex.inedges.size(); i++) {
                splitText[splitText.length - 1] = splitText[splitText.length - 1].replace("_", " ");
                if (currentVertex.inedges.get(i).start.nodeName.equals(splitText[splitText.length - 1])) {
                  currentVertex.inedges.get(i).edgetype = "regularlink";
//                  System.out.println("...."+currentVertex.inedges.get(i).nodeName);
                  currentVertex.inedges.get(i).showInListModel = true;
                  if (undoing == false) {
                    changes.add("Changed inflow to regularlink: " + currentVertex.inedges.get(i).start.nodeName + " " + currentVertex.inedges.get(i).end.nodeName);
                  }
                }
                //showAllFlows();
                //gc.repaint(0);
                //updateInputs();
              }
              updateInputs();
          
              //do not call this if there is no text in jTextAreaEquation. If you do,
              //it will add links to the text area, even though that should remain empty.
              if (!jTextAreaEquation.getText().equals("")) {
                showAllFlows();
              }

              if (!operator.equals("")) {
                jTextAreaEquation.setText(jTextAreaEquation.getText() + " " + operator);
              }

              operator = "";
            } else if (splitText.length > 1) {
              //When it's an inflow
              if (splitText.length==3 && (splitText[splitText.length - 1].equals("+") || splitText[splitText.length - 1].equals("-"))) {
//                for (int i = 0; i < currentVertex.inedges.size(); i++) {
//                  splitText[splitText.length - 2] = splitText[splitText.length - 2].replace("_", " ");
//                  if (currentVertex.inedges.get(i).start.nodeName.equals(splitText[splitText.length - 2])) {
//                    currentVertex.inedges.get(i).edgetype = "regularlink";
//                    currentVertex.inedges.get(i).showInListModel = true;
//                    
//                  }
//                  //showAllFlows();
//                  //gc.repaint(0);
//                  //updateInputs();
//                }
                operator1 = splitText[splitText.length - 3];
//                System.out.println("Operator1:"+operator1);
                
              } 
               if (splitText[splitText.length - 2].equals("+") || splitText[splitText.length - 2].equals("-")) {
                for (int i = 0; i < currentVertex.inedges.size(); i++) {
                  splitText[splitText.length - 1] = splitText[splitText.length - 1].replace("_", " ");
                  if (currentVertex.inedges.get(i).start.nodeName.equals(splitText[splitText.length - 1])) {
                    currentVertex.inedges.get(i).edgetype = "regularlink";
                    currentVertex.inedges.get(i).showInListModel = true;
                    if (undoing == false && splitText[splitText.length - 2].equals("+")) {
                      changes.add("Changed inflow to regularlink: " + currentVertex.inedges.get(i).start.nodeName.replace(" ", "_") + " " + currentVertex.inedges.get(i).end.nodeName.replace(" ", "_"));
                    } else if (undoing == false && splitText[splitText.length - 2].equals("-")) {
                      changes.add("Changed outflow to regularlink: " + currentVertex.inedges.get(i).start.nodeName.replace(" ", "_") + " " + currentVertex.inedges.get(i).end.nodeName.replace(" ", "_"));
                    }
                  }
                 
                  //showAllFlows();
                  //gc.repaint(0);
                  //updateInputs();
                }
                operator = splitText[splitText.length - 2];
//                System.out.println("Operator2:"+operator);
                if(splitText.length==4)
                {
                  operator1 = splitText[splitText.length - 4];
//                  System.out.println("4Operator1:"+operator1);
                }

              }
               //If it's a + or -
//              else {
//                currentText = "";
//                if (undoing == false) {
//                  changes.add("Removed: " + splitText[splitText.length - 1]);
//                }
//                for (int i = 0; i < splitText.length - 1; i++) {
//                  splitText[i] = splitText[i].replace(" ", "_");
//                  if (currentText.equals("")) {
//                    currentText = splitText[i];
//                  } else {
//                    currentText += " " + splitText[i];
//                  }
//                }
//              }
              
//              System.out.println("current text:" + currentText);
              jTextAreaEquation.setText(currentText);
              jTextAreaEquation.setBackground(Color.WHITE);
              updateInputs();
          
            //do not call this if there is no text in jTextAreaEquation. If you do,
            //it will add links to the text area, even though that should remain empty.
            if (!jTextAreaEquation.getText().equals("")) {
              showAllFlows();
            }
            
            if(!operator.equals("") && !operator1.equals(""))
            {
              jTextAreaEquation.setText(" "+operator1+" "+jTextAreaEquation.getText()+ " " + operator);
            }
            else if (!operator.equals("")) {
              jTextAreaEquation.setText(jTextAreaEquation.getText() + " " + operator);
            }
            else if(!operator1.equals("")) {
              jTextAreaEquation.setText(" "+operator1+" "+jTextAreaEquation.getText());
              
            }

            operator = "";
            operator1="";
            }
            
              //System.out.println("current text:"+ currentText);
            }
            //temp = jTextAreaEquation.getText();
  //          showAllFlows();


        }
        gc.repaint();
        changed = true;
      }
}//GEN-LAST:event_deleteButtonActionPerformed

    private void accumulatesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_accumulatesButtonActionPerformed
      // TODO add your handling code here:
      if (!initializing) {

        if (!currentVertex.type.equalsIgnoreCase("stock")) {
          //change the calculation and graph status so that the (c) and (g) circles on the vertex turns white
          currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);
          resetGraphStatus();

          jTextAreaEquation.setBackground(Color.WHITE);
          jTextAreaEquation.setEnabled(true);
          jListVariables.setBackground(Color.WHITE);
          // We want to allow JTextAreaEquations to be able to be changed, even if it has first been
          // checked and found to be correct. That way, if the user goes back and changes the inputs
          // the current equation will need to be reset
          currentVertex.isEquationCorrect = false;
          givenValueTextField.setBackground(Color.WHITE);
          radioButtonPanel.setBackground(new Color(238, 238, 238));
          currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);

          if (scanner.getEquation() != null) {
            while (scanner.removeInput());
          }

          parser.setEquation(null);
          currentVertex.equation = null;
          previousEquationList = new LinkedList<String>();
          jTextAreaEquation.setText("");
          if (!givenValueCorrect) {
            givenValueTextField.setText("");
          }
          if (undoing == false) {
            changes.add("Radio Button Clicked: accumulatesButton");
          }
          //Reformat the page
          disableKeyPad();
          addButton.setEnabled(true);
          subtractButton.setEnabled(true);
          positiveValuesButton.setSelected(false);
          positiveValuesButton.setEnabled(true);
          calculatorPanel.setVisible(true);

          if (functionButton.isSelected()) {
            functionButton.setSelected(false);
          }

          //if (currentVertex.getInputsButtonStatus() == currentVertex.NOSTATUS) {
          //  enableButtons(false);
          //} else {
          //  enableButtons(true);
          //}

          givenValueLabel.setText("Initial Value = ");
          givenValueLabel.setVisible(true);
          valuesLabel.setText("Next Value = Current Value +");
          givenValueTextField.setVisible(true);

          if (!givenValueCorrect) {
            givenValueTextField.setText("");
          }

          currentVertex.type = "stock";
          changed = true;

          if (currentVertex.inedges.size() == 0) {
            //needInputLabel.setText("Need Inputs!");
          } else {
            for (int i = 0; i < currentVertex.inedges.size(); i++) {
              //if (currentVertex.inedges.get(i).start.nodeName.equals(currentVertex.equation.tokenList.getLast().getLexeme().replace("_", " "))) {
              currentVertex.inedges.get(i).showInListModel = true;
              currentVertex.inedges.get(i).edgetype = "regularlink";
              //}
            }
            updateInputs();
            gc.repaint(0);
          }
          logger.out(Logger.ACTIVITY, "CalculationsPanel.accumulatesButtonActionPerformed.1");
        }
      }
    }//GEN-LAST:event_accumulatesButtonActionPerformed

    private void functionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_functionButtonActionPerformed
      //Delete the equation
      if (!initializing) {

        if (!currentVertex.type.equalsIgnoreCase("flow")) {
          //change the calculation and graph status so that the (c) and (g) circles on the vertex turns white
          currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);
          resetGraphStatus();
          //reset background colors

          jTextAreaEquation.setBackground(Color.WHITE);
          jTextAreaEquation.setEnabled(true);
          jListVariables.setBackground(Color.WHITE);
          // We want to allow JTextAreaEquations to be able to be changed, even if it has first been
          // checked and found to be correct. That way, if the user goes back and changes the inputs
          // the current equation will need to be reset
          currentVertex.isEquationCorrect = false;
          givenValueTextField.setBackground(Color.WHITE);
          radioButtonPanel.setBackground(new Color(238, 238, 238));
          currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);


          if (scanner.getEquation() != null) {
            while (scanner.removeInput());
          }

          parser.setEquation(null);
          currentVertex.equation = null;
          previousEquationList = new LinkedList<String>();
          if (!givenValueCorrect) {
            givenValueTextField.setText("");
          }

          jTextAreaEquation.setText("");
          if (undoing == false) {
            changes.add("Radio Button Clicked: functionButton");
          }

          //Reformat the page
          enableKeyPad();
          positiveValuesButton.setSelected(false);
          positiveValuesButton.setEnabled(false);
          givenValueLabel.setVisible(false);

          //if (currentVertex.getInputsButtonStatus() == currentVertex.NOSTATUS)
          //  enableButtons(false);
          //else
          //  enableButtons(true);

          if (!givenValueCorrect) {
            givenValueTextField.setText("");
          }

          givenValueTextField.setVisible(false);
          valuesLabel.setText("Next Value = ");
          calculatorPanel.setVisible(true);

          if (accumulatesButton.isSelected()) {
            accumulatesButton.setSelected(false);
          }

          currentVertex.type = "flow";
          changed = true;

          if (currentVertex.inedges.size() == 0) {
            //needInputLabel.setText("Need Inputs!");
          } else {
            for (int i = 0; i < currentVertex.inedges.size(); i++) {
              //if (currentVertex.inedges.get(i).start.nodeName.equals(currentVertex.equation.tokenList.getLast().getLexeme().replace("_", " "))) {
              currentVertex.inedges.get(i).showInListModel = true;
              currentVertex.inedges.get(i).edgetype = "regularlink";
              //}
            }
            updateInputs();
            gc.repaint(0);
          }

          logger.out(Logger.ACTIVITY, "CalculationsPanel.functionButtonActionPerformed.1");
        }
      }
    }//GEN-LAST:event_functionButtonActionPerformed

  /**
   * @author curt
   * @param evt
   */
    private void jListVariablesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListVariablesMouseClicked
      String s;
      if (jListVariables.isEnabled()) {
        if (jListVariables.getSelectedIndex() != -1) {
          s = jListVariables.getSelectedValue().toString();
          s = s.replace(" ", "_");
        } else {
          s = "";
        }
        
//        jTextAreaEquation.getText()+s;
        
        //change the calculation and graph status so that the (c) and (g) circles on the vertex turns white
        currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);
        resetGraphStatus();
        //reset background colors
        resetColors();
        undoButton.setEnabled(true);


        if (currentVertex.type.equals("flow")) {
          if (currentVertex.equation != null) {
            previousEquationList.add(currentVertex.equation.toString());
          } else {
            previousEquationList.add("");
          }
          changes.add("Changed equation from previous");
          //String previous = currentVertex.equation.toString();
          updateEquation(scanner.addInput(s, Scanner.VARIABLE));

          //changes.add("Changed equation from: " + previous);
          if (currentVertex.equation != null) {
            logger.concatOut(Logger.ACTIVITY, "CalculationsPanel.jListVariablesMouseClicked.1", currentVertex.equation.toString());
          }

          for (int i = 0; i < currentVertex.inedges.size(); i++) {
            if (currentVertex.inedges.get(i).start.nodeName.equals(s.replace("_", " "))) {
              String currentText = jTextAreaEquation.getText();
              ((Edge) currentVertex.inedges.get(i)).showInListModel = false;
            }
          }
          System.out.println("jListVariablesMouseClicked: " + jTextAreaEquation.getText());

          updateInputs();
        } else if (currentVertex.type.equals("stock")) {
//          for (int i = 0; i < currentVertex.inedges.size(); i++) {
//                System.out.println("beforeOOOOOOOOOOO:"+currentVertex.inedges.get(i).start.nodeName);
//              }
          String[] allFlows = jTextAreaEquation.getText().split(" ");
          if (allFlows.length > 1) {           
            //All links will appear as inflows, direction will never change
            if (allFlows[allFlows.length - 1].equals("+")) {
              //If the edge is already an inedge
              for (int i = 0; i < currentVertex.inedges.size(); i++) {
                if (currentVertex.inedges.get(i).start.nodeName.equals(s.replace("_", " "))) {
                  String currentText = jTextAreaEquation.getText();
                  ((Edge) currentVertex.inedges.get(i)).edgetype = "flowlink";
                  ((Edge) currentVertex.inedges.get(i)).showInListModel = false;
                  changes.add("Changed regularlink to inflow: " + ((Edge) currentVertex.inedges.get(i)).start.nodeName.replace(" ", "_") + " " + ((Edge) currentVertex.inedges.get(i)).end.nodeName.replace(" ", "_"));
                  if (currentText.equals("")) {
//                    s = s.replace(" ", "_");
                    jTextAreaEquation.setText(s);                    
                  } else {
//                    s = s.replace(" ", "_");
                    jTextAreaEquation.setText(currentText + s);
                    if(i>0)
                    {
                      Edge temp_Edge=((Edge) currentVertex.inedges.remove(i));
                      Edge firstEdge = ((Edge) currentVertex.inedges.remove(0));
                      currentVertex.inedges.add(0, temp_Edge) ;
                      currentVertex.inedges.add(i, firstEdge) ;
                    }
                  }
                  jTextAreaEquation.setForeground(Color.BLACK);
                }
              }
              updateInputs();
            } //if the newly added link is an outedge
            else if (allFlows[allFlows.length - 1].equals("-")) {
              for (int j = 0; j < currentVertex.inedges.size(); j++) {
                if (currentVertex.inedges.get(j).start.nodeName.equals(s.replace("_", " "))) {
                  String currentText = jTextAreaEquation.getText();
                  ((Edge) currentVertex.inedges.get(j)).edgetype = "flowlink";
                  ((Edge) currentVertex.inedges.get(j)).showInListModel = false;
                  changes.add("Changed regularlink to inflow: " + ((Edge) currentVertex.inedges.get(j)).start.nodeName.replace(" ", "_") + " " + ((Edge) currentVertex.inedges.get(j)).end.nodeName.replace(" ", "_"));
                  if (currentText.equals("")) {
//                    s = s.replace(" ", "_");
                    jTextAreaEquation.setText(s);
                    if(j>0)
                    {
                      Edge temp_Edge=((Edge) currentVertex.inedges.remove(j));
                      Edge firstEdge = ((Edge) currentVertex.inedges.remove(0));
                      currentVertex.inedges.add(0, temp_Edge) ;
                      currentVertex.inedges.add(j, firstEdge) ;
                    }
                  } else {
//                    s = s.replace(" ", "_");
                    jTextAreaEquation.setText(currentText + s);
                  }
                  jTextAreaEquation.setForeground(Color.BLACK);
                  continue;
                }
              }
              updateInputs();
            }            
          } else {
            //The edge is already an inedge
              for (int i = 0; i < currentVertex.inedges.size(); i++) {
                if (currentVertex.inedges.get(i).start.nodeName.equals(s.replace("_", " "))) {
                  String currentText = jTextAreaEquation.getText();
                  ((Edge) currentVertex.inedges.get(i)).edgetype = "flowlink";
                  ((Edge) currentVertex.inedges.get(i)).showInListModel = false;
                  changes.add("Changed regularlink to inflow: " + ((Edge) currentVertex.inedges.get(i)).start.nodeName.replace(" ", "_") + " " + ((Edge) currentVertex.inedges.get(i)).end.nodeName.replace(" ", "_"));
                  if (currentText.equals("")) {
//                    s = s.replace(" ", "_");
                    jTextAreaEquation.setText(s);
                    if(i>0)
                    {
                      Edge temp_Edge=((Edge) currentVertex.inedges.remove(i));
                      Edge firstEdge = ((Edge) currentVertex.inedges.remove(0));
                      currentVertex.inedges.add(0, temp_Edge) ;
                      currentVertex.inedges.add(i, firstEdge) ;
                    }
                  } else {
//                    s = s.replace(" ", "_");
                    jTextAreaEquation.setText(currentText + s);
                  }
                  jTextAreaEquation.setForeground(Color.BLACK);
                }
              }  
//              for (int i = 0; i < currentVertex.inedges.size(); i++) {
//                System.out.println("afterOOOOOOOOOOO:"+currentVertex.inedges.get(i).start.nodeName);
//              }
              updateInputs();           
          }

          changed = true;        
        }
      
      }
}//GEN-LAST:event_jListVariablesMouseClicked

    private void givenValueButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_givenValueButtonActionPerformed
      // TODO add your handling code here:
      //Delete the equation
      if (!initializing) {

        if (!givenValueButtonPreviouslySelected) {
          //change the calculation and graph status so that the (c) and (g) circles on the vertex turns white
          currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);
          resetGraphStatus();
          //reset background colors
          resetColors();

          givenValueButtonPreviouslySelected = true;
          accumulatesButtonPreviouslySelected = false;
          functionButtonPreviouslySelected = false;

          parser.setEquation(null);
          currentVertex.equation = null;
          //givenValueTextField.setText("");
          jTextAreaEquation.setText("");
          //Ensure there are no inedges to constants
          for (int i = 0; i < currentVertex.inedges.size(); i++) {
            graph.delEdge(currentVertex.inedges.get(i));
          }
          givenValueLabel.setText("Given value = ");
          //Reformat the page
          positiveValuesButton.setSelected(false);
          positiveValuesButton.setEnabled(false);

          //if(currentVertex.inedges.size() == 0)
          needInputLabel.setText("");
          gc.repaint();
          changes.add("Radio Button Clicked: givenValueButton");
        }
      }
    }//GEN-LAST:event_givenValueButtonActionPerformed

    private void hintButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hintButtonActionPerformed
      if (!initializing) {
        if (hintLabel.getForeground().equals(Color.BLACK)) {
          hintLabel.setForeground(new Color(240, 240, 240));
          hintPanel.setBackground(new Color(240, 240, 240));
          logger.out(Logger.ACTIVITY, "CalculationsPanel.hintButtonActionPerformed.1");
        } else {
          hintLabel.setForeground(Color.BLACK);
          hintPanel.setBackground(new Color(255, 204, 0));
          logger.out(Logger.ACTIVITY, "CalculationsPanel.hintButtonActionPerformed.2");
        }
        changes.add("Button Clicked: Hint");
      }
}//GEN-LAST:event_hintButtonActionPerformed

  /**
   * @author Curt Tyler
   * @return boolean
   */
  public boolean checkForCorrectCalculations() {
    return checkEquation();
  }

  /**
   * This method checks for any syntax errors within Calculations entered by the user
   *
   * @author Curt Tyler
   * @return boolean
   *
   */
  public boolean checkForSyntaxErrors() {
    boolean syntaxError = false;
    LinkedList<String> userAnswer;

    if (!currentVertex.type.equalsIgnoreCase("flow")) {
      try {
        double x = Double.parseDouble(givenValueTextField.getText());
      } catch (NumberFormatException e) {
        // Text entered in givenValueTextField is not a number
        syntaxError = true;
      }
    }

    if (!currentVertex.type.equalsIgnoreCase("constant")) {
      if (jTextAreaEquation.getText().equals("")) {
        syntaxError = true;
      } else if (givenValueTextField.getText().equals("") && !currentVertex.type.equalsIgnoreCase("flow")) {
        syntaxError = true;
      } else {

        if (currentVertex.type.equalsIgnoreCase("flow")) {
          userAnswer = splitEquation(currentVertex.equation.toString());
        } else {
          String[] stockEquation = jTextAreaEquation.getText().trim().split(" ");
          List list = Arrays.asList(stockEquation);
          userAnswer = new LinkedList(list);
        }

        if (!syntaxError) {
          if (userAnswer.get(userAnswer.size() - 1).equals("+") || userAnswer.get(userAnswer.size() - 1).equals("-")
                  || userAnswer.get(userAnswer.size() - 1).equals("*") || userAnswer.get(userAnswer.size() - 1).equals("/")) {
            syntaxError = true;
          }
        }

        if (!syntaxError) {
          boolean operatorFound = false;
          boolean inputFound = false;

          for (int i = 0; i < userAnswer.size() - 1; i++) {
            // Check for an operator
            if (userAnswer.get(i).equals("+") || userAnswer.get(i).equals("-")
                    || userAnswer.get(i).equals("*") || userAnswer.get(i).equals("/")) {
              // An operator was found, if one wasn't found before, indicate that we have found one
              if (!operatorFound) {
                operatorFound = true;
                inputFound = false;
              } // An operator was found, but another one was already found immediately before it, set
              // syntaxError to true since two operators have been found together wih no input in between
              else {
                syntaxError = true;
              }
            } // an input has been found, so reset operatorFound. We only want that to be set high if
            // more than one operator is found together with nothing in between
            else {
              // An input was found, if one wasn't found before, indicate that we have found one
              if (!inputFound) {
                operatorFound = false;
                inputFound = true;
              } // and input was found, but another one was already found immediately before it, set
              // syntaxError to true since two inputs have been found together with no input in between
              else {
                syntaxError = true;
              }
            }
          }
        }
      }
    }

    return syntaxError;
  }

    private void checkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkButtonActionPerformed
      updateEquations();

      if (initializing == false) {
        if (!currentVertex.type.equalsIgnoreCase("constant") && jListModel.size() > 0) {
          MessageDialog.showMessageDialog(null, true, "You must use all of the inputs to this node in your equation before you can check for correctness.", graph);
        } else // Check to make sure certain elements are populated before even performing the solution check
        if (!((!givenValueButton.isSelected() && !accumulatesButton.isSelected() && !functionButton.isSelected())
                || (givenValueButton.isSelected() && givenValueTextField.getText().equals(""))
                || (accumulatesButton.isSelected() && givenValueTextField.getText().equals("") && jTextAreaEquation.getText().equals(""))
                || (functionButton.isSelected() && jTextAreaEquation.getText().equals("")))) {
          if (!parent.getDescriptionPanel().duplicatedNode(currentVertex.nodeName)) {
            logger.concatOut(Logger.ACTIVITY, "No message", "Click check button try");
            String returnMsg = blockSocket.blockQuery(this, "Click check button");
            if (!returnMsg.equals("allow")) //the action is not allowed by meta tutor
            {
              new MetaTutorMsg(returnMsg.split(":")[1], false).setVisible(true);
              return;
            }

            logger.out(Logger.ACTIVITY, "CalculationsPanel.checkButtonActionPerformed.1");
            initializing = true;

            if (givenValueButton.isSelected()) {
              givenValueButtonPreviouslySelected = true;
              accumulatesButtonPreviouslySelected = false;
              functionButtonPreviouslySelected = false;
            } else if (accumulatesButton.isSelected()) {
              givenValueButtonPreviouslySelected = false;
              accumulatesButtonPreviouslySelected = true;
              functionButtonPreviouslySelected = false;
            } else if (functionButton.isSelected()) {
              givenValueButtonPreviouslySelected = false;
              accumulatesButtonPreviouslySelected = false;
              functionButtonPreviouslySelected = true;
            } else {
              givenValueButtonPreviouslySelected = false;
              accumulatesButtonPreviouslySelected = false;
              functionButtonPreviouslySelected = false;
            }

            //if (!givenValueTextField.getText().equals("")) {
            //  if (Double.parseDouble(givenValueTextField.getText()) == currentVertex.correctValues.get(0)) {
            //    givenValueCorrect = true;
            //
            //  } else {
            //    givenValueCorrect = false;
            //  }
            //}


            if (!accumulatesButton.isSelected() && !functionButton.isSelected() && !givenValueButton.isSelected()) {
              //The answer is wrong
              logger.out(Logger.ACTIVITY, "CalculationsPanel.checkButtonActionPerformed.3");
              radioButtonPanel.setBackground(Color.pink);
              givenValueButton.setBackground(Color.pink);
              accumulatesButton.setBackground(Color.pink);
              functionButton.setBackground(Color.pink);
              //JOptionPane.showMessageDialog(contentPanel, "Please select one of the radio button");
            } else {
              if (currentVertex.correctType.equals("stock") && currentVertex.equation != null) {
                givenValueTextField.setText(currentVertex.equation.toString());
              }
              //JOptionPane.showMessageDialog(contentPanel, "Please input the Initial Value!");
              //System.out.println("currentVertex.stockEquation1: "+currentVertex.stockEquation);
              //System.out.println("correct eq: "+currentVertex.correctEquation.toString());
              //System.out.println("userEquationCorrect///: "+userEquationCorrect);
              /***added by zpwn: to make sure checking performed without initial value**/
              if (givenValueTextField.getText().equals("") && !currentVertex.type.equals("flow")) {
                updateEquation(scanner.addInput("", Scanner.DIGIT));
              } else if (!givenValueTextField.getText().equals("") && !currentVertex.type.equals("flow") && givenValueCorrect) {
                if (currentVertex.equation == null) {
                  updateEquation(scanner.addInput(givenValueTextField.getText(), Scanner.DIGIT));
                }
              }

              boolean checkequation = checkEquation();

              /**done**/
              // HELEN ADD THIS LINE
              if (currentVertex.equation != null) {
                if (currentVertex.equation.toString().equals("")) {
                  givenValueTextField.setText("");
                } else {
                  givenValueTextField.setText(currentVertex.equation.toString());
                }
              }

              if (typeCorrect == true) {
                givenValueButton.setEnabled(false);
                accumulatesButton.setEnabled(false);
                functionButton.setEnabled(false);
                radioButtonPanel.setEnabled(false);
              }

              //HELEN
              if (checkequation) {
                System.out.println("all correct");
                //The answer is correct
                logger.out(Logger.ACTIVITY, "CalculationsPanel.checkButtonActionPerformed.2");
                radioButtonPanel.setBackground(new Color(155, 250, 140));
                jTextAreaEquation.setBackground(new Color(155, 250, 140));
                givenValueTextField.setBackground(new Color(155, 250, 140));
                accumulatesButton.setBackground(new Color(155, 250, 140));
                givenValueButton.setBackground(new Color(155, 250, 140));
                functionButton.setBackground(new Color(155, 250, 140));

                givenValueButton.setEnabled(false);
                accumulatesButton.setEnabled(false);
                functionButton.setEnabled(false);
                givenValueTextField.setEnabled(false);
                jListVariables.setEnabled(false);
                jTextAreaEquation.setEnabled(false);
                disableKeyPad();
                deleteButton.setEnabled(false);
                giveUpButton.setEnabled(false);
                enableButtons(false);
                hintButton.setVisible(false);
                currentVertex.setCalculationsButtonStatus(currentVertex.CORRECT);
              } else {
                //The answer is wrong
                logger.out(Logger.ACTIVITY, "CalculationsPanel.checkButtonActionPerformed.3");
                //System.out.println("currentVertex.stockEquation2: "+currentVertex.stockEquation);
                //if (typeCorrect) { //it's not getting right
                if (currentVertex.correctType.equals(currentVertex.type)) { //zpwn: added to get the correct one.
                  //The answer is correct
                  //logger.out(Logger.ACTIVITY, "CalculationsPanel.checkButtonActionPerformed.2");

                  hintButton.setEnabled(false);
                  hintButton.setVisible(false);
                  radioButtonPanel.setBackground(new Color(155, 250, 140));
                  accumulatesButton.setBackground(new Color(155, 250, 140));
                  givenValueButton.setBackground(new Color(155, 250, 140));
                  functionButton.setBackground(new Color(155, 250, 140));

                  //System.out.println("usereqcorrect: "+userEquationCorrect);
                  if (givenValueCorrect) {
                    givenValueTextField.setBackground(new Color(155, 250, 140));
                    givenValueTextField.setEnabled(false);
                  } else {
                    givenValueTextField.setBackground(Color.pink);
                  }
                  if (userEquationCorrect) {
                    jTextAreaEquation.setBackground(new Color(155, 250, 140));
                  } else {
                    jTextAreaEquation.setBackground(Color.pink);
                  }

                } else {

                  //calculatorPanel.setVisible(false);
                  radioButtonPanel.setBackground(Color.pink);
                  accumulatesButton.setBackground(Color.pink);
                  givenValueButton.setBackground(Color.pink);
                  functionButton.setBackground(Color.pink);
                  jTextAreaEquation.setBackground(Color.pink);
                  givenValueTextField.setBackground(Color.pink);
                  jTextAreaEquation.setForeground(Color.BLACK);
                }
                currentVertex.setCalculationsButtonStatus(currentVertex.WRONG);
              }

              if (!givenValueTextField.getText().equals("")) {
                currentVertex.initialValueGiven = true;
              } else {
                currentVertex.initialValueGiven = false;
              }
            }

            initializing = false;
          } else {
            MessageDialog.showMessageDialog(null, true, "This node is the same as another node you've already defined, please choose a different description.", graph);
          }
        }
      }
}//GEN-LAST:event_checkButtonActionPerformed

    private void giveUpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_giveUpButtonActionPerformed

      if (initializing == false /*&& parent.getInputsPanel().correctinput*/) {
        if (!parent.getDescriptionPanel().duplicatedNode(currentVertex.nodeName)) {
          boolean incorrectInputFound = false;
          boolean incorrectOutputFound = false;
          boolean incorrectNodeTypeDefined = false;

          // Check through each check box, if any are selected, add it to currentVertex.inputNodesSelected (a Linked List)
          currentVertex.inputNodesSelected.clear();
          for (int i = 0; i < parent.getInputsPanel().boxList.size(); i++) {
            if (parent.getInputsPanel().boxList.get(i).isSelected()) {
              currentVertex.inputNodesSelected.add(parent.getInputsPanel().boxList.get(i));
            }
          }

          if (currentVertex.correctType.equalsIgnoreCase("flow") || currentVertex.type.equalsIgnoreCase("auxiliary")) {
            //if (currentVertex.correctInputs.split(",").length != currentVertex.inputNodesSelected.size()) {
            //  incorrectInputFound = true;
            //} else {
            //for (int i = 0; i < currentVertex.correctInputs.split(",").length; i++) {
            //  boolean temp = false;
            //  for (int j = 0; j < currentVertex.inputNodesSelected.size(); j++) {
            //    if (currentVertex.correctInputs.split(",")[i].contains(currentVertex.inputNodesSelected.get(j).getText())) {
            //      temp = true;
            //    }
            //  }

            //  if (!temp) {
            //    incorrectInputFound = true;
            //  }
            //}
            //}

            boolean[] correctInputsSelected = new boolean[currentVertex.inputNodesSelected.size()];

            if ((currentVertex.correctInputs.split(",").length) != currentVertex.inputNodesSelected.size()) {
              incorrectInputFound = true;
            } else {
              for (int i = 0; i < currentVertex.correctInputs.split(",").length; i++) {
                correctInputsSelected[i] = false;
                for (int j = 0; j < currentVertex.inputNodesSelected.size(); j++) {
                  if (currentVertex.correctInputs.split(",")[j].contains(currentVertex.inputNodesSelected.get(i).getText())) {
                    correctInputsSelected[i] = true;
                  }
                }
              }

              for (int i = 0; i < correctInputsSelected.length; i++) {
                if (!correctInputsSelected[i]) {
                  incorrectInputFound = true;
                }
              }
            }
          } else if (currentVertex.correctType.equalsIgnoreCase("stock")) {
            boolean temp = false;
            boolean[] correctInputsSelected = new boolean[currentVertex.inputNodesSelected.size()];
            boolean[] correctOutputsSelected = new boolean[currentVertex.inputNodesSelected.size()];
            int count = 0;

            // find the correct number inputs that should be selected to for this node
            for (int i = 0; i < currentVertex.correctInputs.split(",").length; i++) {
              if (currentVertex.correctInputs.split(",")[i].contains("flowlink")) {
                count++;
              }
            }
            for (int i = 0; i < currentVertex.correctOutputs.split(",").length; i++) {
              if (currentVertex.correctOutputs.split(",")[i].contains("flowlink")) {
                count++;
              }
            }

            if (count != currentVertex.inputNodesSelected.size()) {
              incorrectInputFound = true;
            } else {
              for (int i = 0; i < currentVertex.inputNodesSelected.size(); i++) {
                correctOutputsSelected[i] = false;
                for (int j = 0; j < currentVertex.correctOutputs.split(",").length; j++) {
                  if (currentVertex.correctOutputs.split(",")[j].contains("flowlink - " + currentVertex.inputNodesSelected.get(i).getText())) {
                    correctOutputsSelected[i] = true;
                  }
                }
              }

              for (int i = 0; i < currentVertex.inputNodesSelected.size(); i++) {
                correctInputsSelected[i] = false;
                for (int j = 0; j < currentVertex.correctInputs.split(",").length; j++) {
                  if (currentVertex.correctInputs.split(",")[j].contains(currentVertex.inputNodesSelected.get(i).getText())) {
                    correctInputsSelected[i] = true;
                  }
                }
              }

              for (int i = 0; i < currentVertex.inputNodesSelected.size(); i++) {
                if (!(correctOutputsSelected[i] | correctInputsSelected[i])) {
                  incorrectInputFound = true;
                }
              }
            }
          }

          if ((parent.getInputsPanel().getInputsButtonSelected() && (currentVertex.correctType.equalsIgnoreCase("constant")))
                  || (!parent.getInputsPanel().getInputsButtonSelected() && (currentVertex.correctType.equalsIgnoreCase("stock") || (currentVertex.correctType.equalsIgnoreCase("flow"))))
                  || !parent.getInputsPanel().getInputsButtonSelected() && !this.givenValueButton.isSelected()) {
            incorrectNodeTypeDefined = true;
          }

          if (!(incorrectInputFound | incorrectNodeTypeDefined)) {
            if (parent.getInputsPanel().areAllCorrectInputsAvailable() != false) {
              resetGraphStatus();

              System.out.println("parent.getInputsPanel().correctinput: " + parent.getInputsPanel().correctinput);
              logger.concatOut(Logger.ACTIVITY, "No message", "Click giveup button try");
              String returnMsg = blockSocket.blockQuery(this, "Click giveup button");
              if (!returnMsg.equals("allow")) //the action is not allowed by meta tutor
              {
                new MetaTutorMsg(returnMsg.split(":")[1], false).setVisible(true);
                return;
              }

              logger.out(Logger.ACTIVITY, "CalculationsPanel.giveUpButtonActionPerformed.1");
              //Clear existing answer
              initializing = true;

              //reset the flags that tell which radio button was selected last
              givenValueButtonPreviouslySelected = false;
              accumulatesButtonPreviouslySelected = false;
              functionButtonPreviouslySelected = false;

              for (int i = 0; i < currentVertex.inedges.size(); i++) {
                currentVertex.inedges.get(i).showInListModel = false;
              }

              parser.setEquation(null);
              //givenValueTextField.setText("");

              if (currentVertex.equation != null) {
                if (scanner.getEquation() != null && !currentVertex.equation.tokenList.isEmpty()) {
                  while (scanner.removeInput());
                }
              } else {
//                System.out.println("currentVertex.equation is null");
                //JOptionPane.showMessageDialog(this, "Please enter initial value");
              }

              //Add correct Answer
              currentVertex.equation = currentVertex.correctEquation;
//              System.out.println(currentVertex.equation.toString());

              if (currentVertex.correctType.equalsIgnoreCase("constant")) {
                //System.out.println("cc");
                givenValueButton.setSelected(true);
                if (accumulatesButton.isSelected()) {
                  accumulatesButton.setSelected(false);
                }
                if (functionButton.isSelected()) {
                  functionButton.setSelected(false);
                }
                givenValueTextField.setText(currentVertex.equation.toString());
                givenValueTextField.setVisible(true);
                givenValueLabel.setText("Given value = ");
                givenValueLabel.setVisible(true);
                calculatorPanel.setVisible(false);

                /*modified by zpwn: delete all inputs*/
                int size = currentVertex.inedges.size();
//                System.out.println("size: " + size);
                for (int i = size - 1; i >= 0; i--) {
                  if (currentVertex.inedges.get(i) != null) {
                    graph.delEdge(currentVertex.inedges.get(i));
                  }
                }
                /*done*/

                currentVertex.delEquation();
                //Reformat the page
                positiveValuesButton.setSelected(false);
                positiveValuesButton.setEnabled(false);
                needInputLabel.setText("");
                gc.repaint();
              } else if (currentVertex.correctType.equalsIgnoreCase("flow") || currentVertex.correctType.equalsIgnoreCase("auxiliary")) {
                currentVertex.type = "flow";
                givenValueButton.setEnabled(false);
                givenValueButton.setSelected(false);
                givenValueLabel.setVisible(false);
                givenValueTextField.setVisible(false);
                accumulatesButton.setEnabled(false);
                if (accumulatesButton.isSelected()) {
                  accumulatesButton.setSelected(false);
                }
                functionButton.setEnabled(false);
                functionButton.setSelected(true);
                calculatorPanel.setVisible(true);
                positiveValuesButton.setEnabled(false);
                updateInputs();
                if (currentVertex.equation != null) {
                  jTextAreaEquation.setText(currentVertex.equation.toString());
                }
                givenValueLabel.setText("Initial Value = ");
                positiveValuesButton.setVisible(true);
                positiveValuesButton.setSelected(false);
                disableKeyPad();
                deleteButton.setEnabled(false);
                //showAllFlows();
                jTextAreaEquation.setText(currentVertex.stockEquation);
                updateInputs();
              } else if (currentVertex.correctType.equalsIgnoreCase("stock")) {
                currentVertex.type = "stock";
                givenValueButton.setEnabled(false);
                givenValueLabel.setText("Initial Value = ");
                givenValueLabel.setVisible(true);
                givenValueTextField.setEnabled(false);
                givenValueTextField.setVisible(true);
                accumulatesButton.setEnabled(false);

                functionButton.setEnabled(false);
                calculatorPanel.setVisible(true);
                positiveValuesButton.setEnabled(false);
                disableKeyPad();
                deleteButton.setEnabled(false);
                updateInputs();
                currentVertex.stockEquation = "";
                accumulatesButton.setSelected(true);
                if (functionButton.isSelected()) {
                  functionButton.setSelected(false);
                }
                givenValueButton.setSelected(false);
                givenValueTextField.setText(currentVertex.equation.toString());
                //Add the correct flows to the jTextAreaEquation
                LinkedList<String> correctFlows = new LinkedList<String>();
                String[] inputs = currentVertex.correctInputs.split(",");
                String[] outputs = currentVertex.correctOutputs.split(",");
                //Ensure all inflows are present
                for (int i = 0; i < inputs.length; i++) {
                  if (inputs[i].trim().startsWith("flowlink")) {
                    //Ensure the edge exists
                    boolean edgeExists = false;
                    // Check every link for the inputs[i] link
                    for (int j = 0; j < currentVertex.inedges.size(); j++) {
                      if (currentVertex.inedges.get(j).start.label.equals(inputs[i].trim().replace("flowlink - ", ""))) {
                        if (currentVertex.inedges.get(j).edgetype.equals("flowlink")) {
                          //The edge exists
                          edgeExists = true;
                          continue;
                        } else {
                          edgeExists = true;
                          currentVertex.inedges.get(j).edgetype = "flowlink";
                          gc.repaint(0);
                          continue;
                        }
                      }
                    }
                    // if the inputs[i] link is not found as an edge, add it.
                    if (edgeExists == false) {
                      //Find the start node in the graph
                      Vertex v = null;
                      for (int k = 0; k < graph.getVertexes().size(); k++) {
                        Vertex node = (Vertex) graph.getVertexes().get(k);
                        if (node.label.equals(inputs[i].trim().replace("flowlink - ", ""))) {
                          //Found the start node
                          v = node;
                          break;
                        }
                      }
                      Edge ed = graph.addEdge(v, currentVertex, "flowlink");
                      currentVertex.addInEdge(ed);
                      v.addOutEdge(ed);
                      gc.repaint(0);
                    }
                    //Ensure the edge is added to the stock equation
                    String toAdd = inputs[i].trim().replace("flowlink - ", "");
                    if (correctFlows.isEmpty()) {
                      correctFlows.add(toAdd.replace(" ", "_"));
                    } else {
                      correctFlows.add(" + " + toAdd.replace(" ", "_"));
                    }
                  }
                }
                for (int i = 0; i < outputs.length; i++) {
                  if (outputs[i].trim().startsWith("flowlink")) {
                    //Ensure the edge exists
                    boolean edgeExists = false;
                    for (int j = 0; j < currentVertex.inedges.size(); j++) {
                      if (currentVertex.inedges.get(j).start.label.equals(outputs[i].trim().replace("flowlink - ", ""))) {
                        if (currentVertex.inedges.get(j).edgetype.equals("flowlink")) {
                          //The edge exists
                          edgeExists = true;
                          continue;
                        } else {
                          edgeExists = true;
                          currentVertex.inedges.get(j).edgetype = "flowlink";
                          gc.repaint(0);
                          continue;
                        }
                      }
                    }
                    if (edgeExists == false) {
                      //Find the start node in the graph
                      Vertex v = null;
                      for (int k = 0; k < graph.getVertexes().size(); k++) {
                        Vertex node = (Vertex) graph.getVertexes().get(k);
                        if (node.label.equals(outputs[i].trim().replace("flowlink - ", ""))) {
                          //Found the start node
                          v = node;
                          break;
                        }
                      }
                      Edge ed = graph.addEdge(v, currentVertex, "flowlink");

                      // -- Change by Curt, is this correct? Should currentVertext add an InEdge
                      // and v add an OutEdge like the first for loop above?
                      currentVertex.addOutEdge(ed);
                      v.addInEdge(ed);
                      // ---
                      gc.repaint(0);
                    }
                    String toAdd = outputs[i].trim().replace("flowlink - ", "");
                    if (correctFlows.isEmpty()) {
                      correctFlows.add("- " + toAdd.replace(" ", "_"));
                    } else {
                      correctFlows.add(" - " + toAdd.replace(" ", "_"));
                    }
                  }
                }
                for (int i = 0; i < correctFlows.size(); i++) {
                  if (currentVertex.stockEquation.equals("")) {
                    currentVertex.stockEquation = correctFlows.get(i);
                  } else {
                    currentVertex.stockEquation += correctFlows.get(i);
                  }
                }
                System.out.println("Stock equation: " + currentVertex.stockEquation);
                jTextAreaEquation.setText(currentVertex.stockEquation);
              }

              currentVertex.setCalculationsButtonStatus(currentVertex.GAVEUP);
              //Set the color and disable the elements
              initValues();
              buttonPanel.setVisible(true);
              giveUpButton.setEnabled(false);
              checkButton.setEnabled(false);
              undoButton.setEnabled(false);
              hintButton.setEnabled(false);
              initializing = false;

              if (!givenValueTextField.getText().equals("")) {
                currentVertex.initialValueGiven = true;
              } else {
                currentVertex.initialValueGiven = false;
              }

              jListVariables.setEnabled(false);
              jTextAreaEquation.setEnabled(false);
              this.parent.setAlwaysOnTop(true);
            } else {
              this.parent.setAlwaysOnTop(false);
              MessageDialog.showMessageDialog(null, true, "Sorry, you cannot give up until you have all the neccesary nodes defined that are needed as inputs for this node.", graph);
            }
          } else {
            MessageDialog.showMessageDialog(null, true, "Sorry, you cannot give up in this tab until you have defined the correct type and/or the correct inputs for this node in the Inputs Tab.", graph);
          }
        } else {
          MessageDialog.showMessageDialog(null, true, "This node is the same as another node you've already defined, please choose a different description.", graph);
        }
      }
}//GEN-LAST:event_giveUpButtonActionPerformed

    private void undoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undoButtonActionPerformed
      if (initializing == false) {
        //System.out.println("changes size: "+changes.size());
        if (changes.size() > 0) {
          undoing = true;
          //Revert all colored panels to white
          radioButtonPanel.setBackground(Color.WHITE);
          jTextAreaEquation.setBackground(Color.WHITE);
          givenValueTextField.setBackground(Color.WHITE);
          accumulatesButton.setBackground(Color.WHITE);
          givenValueButton.setBackground(Color.WHITE);
          functionButton.setBackground(Color.WHITE);
          String[] change = changes.get(changes.size() - 1).split(": ");
          //System.out.println("changes : "+changes.get(changes.size() - 1));
          if (change[0].trim().equalsIgnoreCase("Added")) {
            //Remove the added variable, evt is not used in the deleteButtonActionPerformed method
            deleteButtonActionPerformed(evt);
          } else if (change[0].trim().equalsIgnoreCase("Radio Button Clicked")) {
//            System.out.println("Radio Button Clicked");
            if (change[1].equalsIgnoreCase("functionButton") || change[1].equalsIgnoreCase("accumulatesButton")) {
              //Reset to default
              initializing = true;
              givenValueButton.setEnabled(false);
              functionButton.setSelected(false);
              accumulatesButton.setSelected(false);
              calculatorPanel.setVisible(false);
              positiveValuesButton.setEnabled(false);
              positiveValuesButton.setSelected(false);
              givenValueTextField.setVisible(false);
              givenValueLabel.setVisible(false);
              currentVertex.equation = null;
              currentVertex.type = "none";
              initializing = false;
            }
          } else if (change[0].trim().equalsIgnoreCase("Changed equation from previous")) {
            //Set the equation to the previous equation
            System.out.println("Before undo: " + currentVertex.equation.toString());
            parser.setEquation(null);
            //Removes all input from scanner
            if (currentVertex.equation != null) //zpwn: not to get Null Pointer exception
            {
              if (scanner.getEquation() != null && currentVertex.equation != null && !currentVertex.equation.tokenList.isEmpty()) {
                while (scanner.removeInput());
              }
            }

            for (String s : previousEquationList) {
              System.out.println("s:" + s);
            }
            if (currentVertex.type.equals("stock") || currentVertex.type.equals("constant")) {
              if (!previousEquationList.isEmpty()) {
                givenValueTextField.setText(previousEquationList.getLast());
                previousEquationList.removeLast();
                System.out.println(previousEquationList.removeLast());
              }
            } else if (currentVertex.type.equals("flow")) {
              if (!previousEquationList.isEmpty()) {
                String currentWord = "";
                boolean operator = false;
//                try{
                int j = 0;
                for (String s : previousEquationList) {
                  System.out.println(j + " after one removed s:" + s);
                  j++;
                }
                String correct = previousEquationList.getLast();
                previousEquationList.removeLast();
//                if (!correct.equals("")) {
//                  updateEquation(scanner.addInput(correct, Scanner.VARIABLE));
//                  for (int i = 0; i < currentVertex.inedges.size(); i++) {
//                  if (currentVertex.inedges.get(i).start.nodeName.equals(correct.replace("_", " "))) {
//                      ((Edge) currentVertex.inedges.get(i)).showInListModel = true;
//                    }
//                  }
//                 updateInputs();
                System.out.println("flow: " + correct);
//                previousEquationList.removeLast();

                for (int i = 0; i < correct.length(); i++) {
                  char currentChar = correct.charAt(i);
                  if (Character.isDigit(currentChar)) {
                    if (!currentWord.equals("")) {
                      updateEquation(scanner.addInput(currentWord, Scanner.VARIABLE));
                      currentWord = "";
                    }
                    updateEquation(scanner.addInput(currentChar + "", Scanner.DIGIT));
                  } else if (currentChar == '+' || currentChar == '-' || currentChar == '*' || currentChar == '/') {
                    operator = true;
                    System.out.println("CurrentChar: " + operator);
                    if (!currentWord.equals("")) {
                      updateEquation(scanner.addInput(currentWord, Scanner.VARIABLE));
                      currentWord = "";
                    }
                    updateEquation(scanner.addInput(currentChar + "", Scanner.OPERATION));
                  } else if (currentChar == ')' || currentChar == '(') {
                    updateEquation(scanner.addInput(currentChar + "", Scanner.PARENTHESIS));
                  } else {
                    currentWord += currentChar;
                  }
                }
                if (!currentWord.equals("")) {
                  System.out.println("CURRENTWPRD: " + currentWord + "opeartor " + operator);

//                  try
//                  {
//
//                  }catch(NoSuchElementException nsee)
//                  {
//
//                  }
//                  if(scanner.getEquation().tokenList.size()>0)
//                  System.out.println("scanner.getEquation().toString(): "+scanner.getEquation().tokenList.getLast().getType());
                  for (int i = 0; i < currentVertex.inedges.size(); i++) {

                    if (currentVertex.inedges.get(i).start.nodeName.equals(currentWord.replace("_", " "))) {
                      if (!jListModel.contains(currentWord.replace("_", " ")) && operator) {
                        ((Edge) currentVertex.inedges.get(i)).showInListModel = true;
                      } else {
                        ((Edge) currentVertex.inedges.get(i)).showInListModel = false;
                      }
//                      System.out.println(currentVertex.inedges.get(i).start.nodeName);
                    }
//                    else
//                    {
//                      ((Edge) currentVertex.inedges.get(i)).showInListModel = true;
//                    }
                  }

                } else {
//                  System.out.println("BKUR");

                }
                if (currentVertex.equation != null) //zpwn: not to get Null Pointer exception
                {
                  if (scanner.getEquation() != null && currentVertex.equation != null && !currentVertex.equation.tokenList.isEmpty()) {
                    while (scanner.removeInput());
                  }
                }
                currentWord = previousEquationList.getLast();
                if (!currentWord.equals("")) {
                  System.out.println("currentWord: " + currentWord + " equation:" + currentVertex.equation.toString());
//                  for (int i = 0; i < currentVertex.inedges.size(); i++) {
//                    if (currentVertex.inedges.get(i).start.nodeName.equals(currentWord.replace("_", " ")) &&
//                            !jListModel.contains(currentWord.replace("_", " "))) {
////                    if(!jListModel.contains(currentWord.replace("_", " ")))
////                        {
//                          ((Edge) currentVertex.inedges.get(i)).showInListModel = true;
////                        }
//                    }
//                  }
//                  for (int i = 0; i < currentVertex.inedges.size(); i++) {
//                  if (currentVertex.inedges.get(i).start.nodeName.equals(currentWord.replace("_", " "))) {
//                      ((Edge) currentVertex.inedges.get(i)).showInListModel = true;
//                    }
//                    else
//                    {
//                      ((Edge) currentVertex.inedges.get(i)).showInListModel = false;
//                    }
//                  }
                  updateEquation(scanner.addInput(currentWord, Scanner.VARIABLE));
                  updateInputs();
                  currentWord = "";
                  jTextAreaEquation.setText(currentVertex.equation.toString());
                } else {
                  updateEquation(scanner.addInput("", Scanner.VARIABLE));
                  for (int i = 0; i < currentVertex.inedges.size(); i++) {
//                  if (currentVertex.inedges.get(i).start.nodeName.equals(currentWord.replace("_", " "))) {
                    ((Edge) currentVertex.inedges.get(i)).showInListModel = true;
//                    }
                  }
                  updateInputs();
                  jTextAreaEquation.setText(currentVertex.equation.toString());
                }

//                }
//                else
//                {
//                  System.out.println("correct is empty");
////                  updateEquation(scanner.addInput("", Scanner.VARIABLE));
////                  for (int i = 0; i < currentVertex.inedges.size(); i++) {
//////                  if (currentVertex.inedges.get(i).start.nodeName.equals(correct.replace("_", " "))) {
////                      ((Edge) currentVertex.inedges.get(i)).showInListModel = true;
//////                    }
////                  }
////                 updateInputs();
////                  jTextAreaEquation.setText(currentVertex.equation.toString());
//                }
//              }catch(NoSuchElementException e){
////                e.printStackTrace();
//                System.err.println("NO SUCH ELEMENT: "+e.getMessage() +" :"+currentWord);
//                  updateEquation(scanner.addInput("", Scanner.VARIABLE));
//                  for (int i = 0; i < currentVertex.inedges.size(); i++) {
//                  if (currentVertex.inedges.get(i).start.nodeName.equals(currentWord.replace("_", " "))) {
//                      ((Edge) currentVertex.inedges.get(i)).showInListModel = true;
//                    }
//                  }
//                 updateInputs();
//                  jTextAreaEquation.setText(currentVertex.equation.toString());
//              }
              }
            }

//            System.out.println("After undo: " + currentVertex.equation.toString());
//
//            if (currentVertex.type.equals("constant") || currentVertex.type.equals("stock")) {
//              givenValueTextField.setText(currentVertex.equation.toString());
//            } else {
//              jTextAreaEquation.setText(currentVertex.equation.toString());
//            }
//              if (!initializing) {
//                parser.setEquation(null);
//                //Removes all input from scanner
//                while (scanner.removeInput());
//                //String previousEquation = currentVertex.equation.toString();
//                currentVertex.equation = null;
//                if (change.length > 1) {
//                  updateEquation(scanner.addInput(change[1], Scanner.DIGIT));
//                } else {
//                  givenValueTextField.setText("");
//                }
//              }
//            }
          } else if (change[0].trim().equalsIgnoreCase("Changed inflow to regularlink")) {
            //The inedge was previously a flowlink
            String[] inedge = change[1].split(" ");
            for (int i = 0; i < currentVertex.inedges.size(); i++) {
              if (currentVertex.inedges.get(i).start.nodeName.equals(inedge[0]) && currentVertex.inedges.get(i).end.nodeName.equals(inedge[1]) && currentVertex.inedges.get(i).edgetype.equals("regularlink")) {
                currentVertex.inedges.get(i).edgetype = "flowlink";
                if (currentVertex.stockEquation.equals("")) {
                  currentVertex.stockEquation = currentVertex.inedges.get(i).start.nodeName.replace(" ", "_");
                } else {
                  currentVertex.stockEquation = currentVertex.stockEquation + " + " + currentVertex.inedges.get(i).start.nodeName.replace(" ", "_");
                }
                showAllFlows();
                updateInputs();
                gc.repaint(0);
              }
            }
          } else if (change[0].trim().equalsIgnoreCase("Changed outflow to regularlink")) {
            //The inedge was previously a flowlink
            String[] inedge = change[1].split(" ");
            for (int i = 0; i < currentVertex.inedges.size(); i++) {
              if (currentVertex.inedges.get(i).start.nodeName.equals(inedge[0]) && currentVertex.inedges.get(i).end.nodeName.equals(inedge[1]) && currentVertex.inedges.get(i).edgetype.equals("regularlink")) {
                currentVertex.inedges.get(i).edgetype = "flowlink";
                if (currentVertex.stockEquation.equals("")) {
                  currentVertex.stockEquation = "- " + currentVertex.inedges.get(i).start.nodeName.replace(" ", "_");
                } else {
                  currentVertex.stockEquation = currentVertex.stockEquation + " - " + currentVertex.inedges.get(i).start.nodeName.replace(" ", "_");
                }
                showAllFlows();
                updateInputs();
                gc.repaint(0);
              }
            }
          } else if (change[0].trim().equalsIgnoreCase("Changed regularlink to inflow")) {
            //Change the edge back to a regularlink
            deleteButtonActionPerformed(evt);
            gc.repaint(0);
            //System.out.println("testinjtxtarea: "+jTextAreaEquation.getText());
          } else if (change[0].trim().equalsIgnoreCase("Removed")) {
            if (jTextAreaEquation.getText().equals("")) {
              System.out.println("change 1: " + change[1]);
              jTextAreaEquation.setText(change[1]);
            } else {
              System.out.println("gettext: " + jTextAreaEquation.getText());
              jTextAreaEquation.setText(jTextAreaEquation.getText() + " " + change[1]);
            }
            System.out.println("Removed");
          }
          //System.out.println("chages get: "+changes.get(changes.size() - 1));
          /** zpwn: easy fix for the operator remover*/
          if (changes.get(changes.size() - 1).contains("Stock input operation added:")) {
            String[] operator = changes.get(changes.size() - 1).split(":");
            System.out.println(operator[1]);
            if (jTextAreaEquation.getText().contains(operator[1])) {
              jTextAreaEquation.setText(jTextAreaEquation.getText().replace(operator[1], ""));
            }
            //System.out.println("fount");
          }
          /*done*/
          changes.remove(changes.get(changes.size() - 1));
          //System.out.println("changes size now: "+changes.size());
          //jTextAreaEquation.setText(jTextAreaEquation.getText());
          undoing = false;
          undoButton.setEnabled(undoing);
//          changes.clear();
//          previousEquationList.clear();
        }
        logger.out(Logger.ACTIVITY, "CalculationsPanel.undoButtonActionPerformed.1");
      }
    }//GEN-LAST:event_undoButtonActionPerformed

    private void givenValueTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_givenValueTextFieldKeyReleased
      currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);
      resetGraphStatus();
      givenValueTextField.setBackground(Color.WHITE);
      try {
        givenValueTextField.commitEdit();
      } catch (ParseException ex) {
        if (scanner.getEquation() != null && currentVertex.equation != null && !currentVertex.equation.tokenList.isEmpty()) {
          while (scanner.removeInput());
        }
        if(!givenValueTextField.getText().equals("."))
          givenValueTextField.setText("");
        //java.util.logging.Logger.getLogger(CalculationsPanel.class.getName()).log(Level.SEVERE, null, ex);
      }

      if (!givenValueTextField.getText().equals("")) {
        currentVertex.initialValueGiven = true;
      } else {
        currentVertex.initialValueGiven = false;
      }
      gc.repaint(0);
    }//GEN-LAST:event_givenValueTextFieldKeyReleased

    private void givenValueTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_givenValueTextFieldKeyTyped
      // TODO add your handling code here:
//      if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
//        givenValueTextField.setText(givenValueTextField.getText().trim());
//      } else {
      try {
        givenValueTextField.commitEdit();
      } catch (ParseException ex) {
        if (scanner.getEquation() != null && currentVertex.equation != null && !currentVertex.equation.tokenList.isEmpty()) {
          while (scanner.removeInput());
        }
        if(!givenValueTextField.getText().equals("."))
          givenValueTextField.setText("");
        //java.util.logging.Logger.getLogger(CalculationsPanel.class.getName()).log(Level.SEVERE, null, ex);
      }
//      }

    }//GEN-LAST:event_givenValueTextFieldKeyTyped
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton accumulatesButton;
    private javax.swing.JButton addButton;
    private javax.swing.JLabel availableInputsLabel;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JPanel calculatorPanel;
    private javax.swing.JButton checkButton;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton divideButton;
    private javax.swing.JRadioButton functionButton;
    private javax.swing.JButton giveUpButton;
    private javax.swing.JRadioButton givenValueButton;
    private javax.swing.JLabel givenValueLabel;
    private javax.swing.JFormattedTextField givenValueTextField;
    private javax.swing.JButton hintButton;
    private javax.swing.JLabel hintLabel;
    private javax.swing.JPanel hintPanel;
    private javax.swing.JList jListVariables;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextAreaEquation;
    private javax.swing.JButton leftParenthesisButton;
    private javax.swing.JButton multiplyButton;
    private javax.swing.JLabel needInputLabel;
    private javax.swing.JCheckBox positiveValuesButton;
    private javax.swing.JLabel quantityLabel;
    private javax.swing.JPanel radioButtonPanel;
    private javax.swing.JButton rightParenthesisButton;
    private javax.swing.JButton subtractButton;
    private javax.swing.JButton undoButton;
    private javax.swing.JLabel valuesLabel;
    // End of variables declaration//GEN-END:variables

  public void propertyChange(PropertyChangeEvent evt) {
    Object source = evt.getSource();
    //Helen Add the log when user gives up and the equation was set
    if (currentVertex.getCalculationsButtonStatus() == currentVertex.GAVEUP && source == jTextAreaEquation) {
      logger.concatOut(Logger.ACTIVITY, "CalculationsPanel.jListVariablesMouseClicked.1", jTextAreaEquation.getText());
    }
    if (source == givenValueTextField && !status.equalsIgnoreCase("closing") && !evt.getPropertyName().equals("ancestor") && !initializing) {
      try {
        if (undoing == false) {
          if (currentVertex.equation != null) {
            previousEquationList.add(currentVertex.equation.toString());
            undoButton.setEnabled(true);
          } else {
            previousEquationList.add("");
          }
          changes.add("Changed equation from previous");
          //System.out.println(changes.get(changes.size() - 1));
        }
//        else {
//          if (undoing == false) {
//            previousEquationList.push(currentVertex.equation);
//            changes.add("Changed equation from previous");
//            System.out.println("equation pushed: " + currentVertex.equation);
//          }
//        }
        parser.setEquation(null);

        givenValueTextField.commitEdit();
//        givenValueTextField.setText(givenValueTextField.getFormatter().valueToString(givenValueTextField.getValue()));

        //Removes all input from scanner
        if (scanner.getEquation() != null && currentVertex.equation != null && !currentVertex.equation.tokenList.isEmpty()) {
          while (scanner.removeInput());
        }
        if (currentVertex.type.equals("constant") || currentVertex.type.equals("stock")) {
          //String previousEquation = currentVertex.equation.toString();
          currentVertex.equation = null;
          System.out.println("givenValueTextField.getText():" + givenValueTextField.getText());
          //System.out.println(".l.");
          updateEquation(scanner.addInput(givenValueTextField.getText(), Scanner.DIGIT));
          System.out.println("currentVertex.equation.toString():" + currentVertex.equation.toString());
          //System.out.println("mmm "+currentVertex.stockEquation);
          //System.out.println(givenValueTextField.getText());
          if (currentVertex.equation != null) {
            logger.concatOut(Logger.ACTIVITY, "CalculationsPanel.propertyChange.1", currentVertex.equation.toString());
          }
          gc.repaint();
          //changes.add("Equation changed from: " + previousEquation);
        }
      } //The exception will be caught if the user tries to enter an invalid value
      catch (ParseException ex) {
        System.err.println("PARSE EXCEPTION");
        if (scanner.getEquation() != null && currentVertex.equation != null && !currentVertex.equation.tokenList.isEmpty()) {
          while (scanner.removeInput());
        }
        if(!givenValueTextField.getText().equals("."))
          givenValueTextField.setText("");
        logger.concatOut(Logger.DEBUG, "CalculationsPanel.propertyChange.2", ex.toString());
      }
    }
  }
}
