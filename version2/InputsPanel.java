/*
 * InputsPanel.java
 *
 * Created on Nov 21, 2010, 10:23:54 AM
 */
package laits.version2;

import laits.BlockSocket;
import laits.MetaTutorMsg;
import laits.comm.CommException;
import laits.data.TaskFactory;
import laits.graph.Edge;
import laits.graph.Graph;
import laits.graph.GraphCanvas;
import laits.graph.GraphCanvasScroll;
import laits.log.Logger;
import laits.graph.Vertex;
import laits.gui.MessageDialog;
import laits.parser.Equation;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.LinkedList;
import java.util.Stack;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;

/**
 *
 * @author Megana
 * @author zpwn
 */
public class InputsPanel extends javax.swing.JPanel implements ItemListener {

  TaskFactory server;
  Graph g;
  GraphCanvas gc;
  public LinkedList<JCheckBox> boxList = new LinkedList<JCheckBox>();
  Stack undoStack=new Stack();
  boolean undoFlag = false;
  Vertex currentVertex = null;
  TabbedGUI parent;
  Logger logger = Logger.getLogger();
  boolean initializing = true;
  public String itemChanged;
  private boolean valueButtonPreviouslySelected = false;
  private boolean inputsButtonPreviouslySelected = false;
  public boolean correctinput = false;
  private final boolean TYPE_CHANGE = true;
  private final boolean NO_TYPE_CHANGE = false;
  private boolean giveUpPressed = false;
  //Iterator inputStack =
  BlockSocket blockSocket=BlockSocket.getBlockSocket();

  /** Creates new form InputsPanel */
  public InputsPanel(TabbedGUI parent, Vertex v, Graph g, GraphCanvas gc) {
    initComponents();
    this.parent = parent;
    this.g = g;
    this.gc = gc;
    this.currentVertex = v;
    hintButton.setVisible(false);
    hintLabel.setVisible(false);
    currentInputPanel.setVisible(false);

    try {
      server = TaskFactory.getInstance();
    } catch (CommException ex) {
      //Add appropriate logger
      logger.concatOut(Logger.DEBUG, "InputsPanel.InputsPanel.1", ex.toString());
    }
    undoStack.setSize(1);
    currentInputPanel.setLayout(new GridLayout(g.getVertexes().size(), 1));

    for (int i = 0; i < g.getVertexes().size(); i++) {
      Vertex vertex = (Vertex) (g.getVertexes().get(i));
      if (!vertex.nodeName.equals("") && !vertex.label.equals(currentVertex.label)) {
        JCheckBox checkbox = new JCheckBox();
        checkbox.setText(vertex.nodeName);
        checkbox.addItemListener(this);
        currentInputPanel.add(checkbox);
        boxList.add(checkbox);
      }
    }
    initValues();
    initInitialState();
    updateDescription();
    initializing = false;
    
  }

  /**
   * This method initializes the panel if the user has already chosen a description, checked, given up, or chose a wrong answer
   */
  public void initValues() {
    initButtonOnTask();

    if (currentVertex.getInputsButtonStatus() == currentVertex.CORRECT) {
      checkButton.setEnabled(false);
      giveUpButton.setEnabled(false);
      hintButton.setEnabled(false);
      undoButton.setEnabled(false);
      valueButton.setEnabled(false);
      inputsButton.setEnabled(false);
      radioPanel.setBackground(new Color(155,250,140));
      valueButton.setBackground(new Color(155,250,140));
      inputsButton.setBackground(new Color(155,250,140));
 
      for (JCheckBox box : boxList) {
        box.setBackground(new Color(155,250,140));
        box.setEnabled(false);
      }
      //if (currentVertex.correctType.equalsIgnoreCase("constant")) {
      if (currentVertex.type.equalsIgnoreCase("constant")) {
        valueButton.setSelected(true);
        currentInputPanel.setVisible(false);
      } else {
        inputsButton.setSelected(true);
        currentInputPanel.setVisible(true);
        currentInputPanel.setBackground(new Color(155,250,140));
      }
    } else if (currentVertex.getInputsButtonStatus() == currentVertex.GAVEUP) {
      checkButton.setEnabled(false);
      giveUpButton.setEnabled(false);
      hintButton.setEnabled(false);
      undoButton.setEnabled(false);
      valueButton.setEnabled(false);
      inputsButton.setEnabled(false);
      radioPanel.setBackground(new Color(252,252,130));
      valueButton.setBackground(new Color(252,252,130));
      inputsButton.setBackground(new Color(252,252,130));
      currentInputPanel.setBackground(new Color(252,252,130));
      for (JCheckBox box : boxList) {
        box.setBackground(new Color(252,252,130));
        box.setEnabled(false);
      }
      //if (currentVertex.correctType.equalsIgnoreCase("constant")) {
      System.out.println("YYYYYYYYY " + currentVertex.type);
      if (currentVertex.type.equalsIgnoreCase("constant")) {
        valueButton.setSelected(true);
        if (inputsButton.isSelected()) {
          inputsButton.setSelected(false);
        }

        currentInputPanel.setVisible(false);
      } else {
        inputsButton.setSelected(true);
        currentInputPanel.setVisible(true);
      }
    }
    else if (currentVertex.getInputsButtonStatus() == currentVertex.WRONG) {
      valueButton.setBackground(Color.pink);
      inputsButton.setBackground(Color.pink);
      currentInputPanel.setBackground(Color.pink);
      for (JCheckBox box : boxList) {
        box.setBackground(Color.pink);
      }
      if ((currentVertex.correctType.equals("flow") || currentVertex.correctType.equals("stock")) && currentVertex.inputsSelected) {
        radioPanel.setBackground(new Color(155,250,140));
        valueButton.setBackground(new Color(155,250,140));
        inputsButton.setBackground(new Color(155,250,140));
      } else {
        radioPanel.setBackground(Color.pink);
        valueButton.setBackground(Color.pink);
        inputsButton.setBackground(Color.pink);
      }
      //for (JCheckBox box : boxList) {
      //  box.setBackground(Color.pink);
      //  box.setSelected(true);
      //}
      //if (currentVertex.correctType.equalsIgnoreCase("constant")) {
//      if (currentVertex.type.equalsIgnoreCase("constant")) {
//        valueButton.setSelected(true);
//        currentInputPanel.setVisible(false);
//      } else {
//        inputsButton.setSelected(true);
//        currentInputPanel.setVisible(true);
//      }
    }
    undoButton.setEnabled(undoFlag);
    
    //HELEN SET THE UNDO BUTTON INVISIBLE UNTIL IT WORKS PROPERLY FOR ALL TABS.
    undoButton.setVisible(false);
    
    /*else { // The user do not check yet the inputs -- HELEN
      checkButton.setEnabled(true);
      giveUpButton.setEnabled(true);
      hintButton.setEnabled(false);
      undoButton.setEnabled(true);
      hintLabel.setVisible(false);

      if (inputsButton.isSelected()){
        currentInputPanel.setVisible(true);
      }

    }*/
}

  public void initButtonOnTask() {
    // Depending on what type the current task is, checkButton oand giveUpButton should either be
    // disabled or enabled
    if(server.getActualTask().getType().equalsIgnoreCase("Intro") ||
       server.getActualTask().getType().equalsIgnoreCase("Debug") ||
       server.getActualTask().getType().equalsIgnoreCase("Construct") ||
       server.getActualTask().getType().equalsIgnoreCase("Whole")) {
          //checkButton.setEnabled(true);
          //giveUpButton.setEnabled(true);
        // - Disabling the Check and Give Up button for author mode
            checkButton.setEnabled(false);
            giveUpButton.setEnabled(false);
    } else if (server.getActualTask().getType().equalsIgnoreCase("Test")) {
      checkButton.setEnabled(false);
      giveUpButton.setEnabled(false);
    }
/*            
      else if (server.getActualTask().getType().equalsIgnoreCase("Whole") && gc.modelHasBeenRun == true) {
      checkButton.setEnabled(true);
      giveUpButton.setEnabled(true);
    }     
*/
  }

  public void initInitialState() {
    if (currentVertex.type.equals("constant")) {
      valueButton.setSelected(true);
      valueButtonPreviouslySelected =true;
    } else if (currentVertex.type.equals("flow") || currentVertex.type.equals("stock") || currentVertex.inputsSelected == true) {
      inputsButton.setSelected(true);
      inputsButtonPreviouslySelected =true;
      currentInputPanel.setVisible(true);
    } else if (currentVertex.type.equals("none") && inputsButton.isSelected()){
      currentInputPanel.setVisible(true);
    }
 
    //Check the box if the vertex already has it as an input
    if (!currentVertex.inedges.isEmpty()) {
      inputsButton.setSelected(true);
      inputsButtonPreviouslySelected =true;
      currentInputPanel.setVisible(true);
      for (int i = 0; i < currentVertex.inedges.size(); i++) {
        for (int j = 0; j < boxList.size(); j++) {
          //If the node is not a stock, don't check the box if the inedge is a flowlink
          if (boxList.get(j).getText().equals(currentVertex.inedges.get(i).start.nodeName) && !currentVertex.type.equals("stock") && !currentVertex.inedges.get(i).edgetype.equals("flowlink")) {
            boxList.get(j).setSelected(true);
            //System.out.println("Inedge for flow: " + currentVertex.inedges.get(i).start.nodeName);
          } else if (boxList.get(j).getText().equals(currentVertex.inedges.get(i).start.nodeName) && currentVertex.type.equals("stock")) {
            boxList.get(j).setSelected(true);
            //System.out.println("Inedge for stock: " + currentVertex.inedges.get(i).start.nodeName + " of type " + currentVertex.inedges.get(i).edgetype);
          }
        }
      }
      //Check the boxes for the outflows
      if (currentVertex.type.equals("stock") && !currentVertex.outedges.isEmpty()) {
        for (int i = 0; i < currentVertex.outedges.size(); i++) {
          for (int j = 0; j < boxList.size(); j++) {
            if (boxList.get(j).getText().equals(currentVertex.outedges.get(i).end.nodeName) && currentVertex.outedges.get(i).edgetype.equals("flowlink")) {
              boxList.get(j).setSelected(true);
              //System.out.println("Outedge for stock: " + currentVertex.outedges.get(i).end.nodeName);
            }
          }
        }
      }
    }
    //updateDescription();
  }

  public void resetColors() {
    currentInputPanel.setBackground(new Color(238, 238, 238));
    for (int j = 0; j < boxList.size(); j++) {
        boxList.get(j).setBackground(new Color(238, 238, 238));
    }
    radioPanel.setBackground(new Color(238, 238, 238));
    valueButton.setBackground(new Color(238, 238, 238));
    inputsButton.setBackground(new Color(238, 238, 238));
  }

  private void displayCurrentInputsPanel(boolean flag) {
    currentInputPanel.setVisible(flag);
      for (JCheckBox box : boxList) {
        box.setVisible(flag);
     }
  }

  private void resetGraphStatus()
  {
    Vertex v = new Vertex();
    int firstNodeWithNoStatus = -1;
    int firstIndexOfNoStatus = -1;
    boolean restart = true;
    int [] nodeStatus = new int[g.getVertexes().size()];

    logger.concatOut(Logger.ACTIVITY, "No message", "Reset colors.");
    while (restart) {
      currentVertex.setGraphsButtonStatus(v.NOSTATUS);
      for (int a = 0; a < g.getVertexes().size(); a++) {
        v = (Vertex)g.getVertexes().get(a);
        System.out.println(v.nodeName);
        if (v.getGraphsButtonStatus() == v.NOSTATUS) {
          if (firstNodeWithNoStatus == -1) {
            firstNodeWithNoStatus = a;
          }
          if (v.type.equals("constant") || v.type.equals("flow")) {
            for (int i = 0; i < v.inedges.size(); i++) {
              Vertex current = (Vertex)v.inedges.get(i).end;
              current.setGraphsButtonStatus(current.NOSTATUS);
            }
            for (int i = 0; i < v.outedges.size(); i++) {
              Vertex current = (Vertex)v.outedges.get(i).end;
              current.setGraphsButtonStatus(current.NOSTATUS);
            }
          } else if (v.type.equals("stock")) {
            for (int i = 0; i < v.outedges.size(); i++) {
              Vertex current = (Vertex)v.outedges.get(i).end;
              current.setGraphsButtonStatus(current.NOSTATUS);
            }
          }
        }
      }

      for (int i = 0; i < g.getVertexes().size(); i++) {
        nodeStatus[i] = ((Vertex)g.getVertexes().get(i)).getGraphsButtonStatus();
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

  public void clearInputs(boolean trueFalse) {
    if (trueFalse) {
      valueButton.setSelected(false);
      inputsButton.setSelected(false);

    }
  }

  public void itemStateChanged(ItemEvent e) {
    boolean skip = false;

    if (initializing == false) {
      //change the input and graph status so that the (i), and (g) (possibly the (c)) circles
      //on the vertex turns white
      currentVertex.setInputsButtonStatus(currentVertex.NOSTATUS);
      if (currentVertex.getCalculationsButtonStatus() == currentVertex.WRONG) {
        if (!currentVertex.isCalculationTypeCorrect) {
          currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);
        }
      }

      // Check through each check box, if any are selected, add it to currentVertex.inputNodesSelected (a Linked List)
      currentVertex.inputNodesSelected.clear();
      for (int i = 0; i < boxList.size(); i++) {
        if (boxList.get(i).isSelected()) {
          currentVertex.inputNodesSelected.add(boxList.get(i));
        }
      }

      //reset background colors
      resetColors();
      //if (skip) {
      //  parent.getCalculationsPanel().getjListModel().clear();
      //  parent.getCalculationsPanel().repaintJListVariables();
      //  parent.getCalculationsPanel().repaint();
      //}

      if (this.giveUpPressed) {
        if ((currentVertex.getCalculationsButtonStatus() == currentVertex.GAVEUP || currentVertex.getCalculationsButtonStatus() == currentVertex.CORRECT)) {
          skip = true;
        }
      }

      if (!skip) {
        resetGraphStatus();
        parent.getCalculationsPanel().resetColors(TYPE_CHANGE);
        parent.getCalculationsPanel().clearEquationArea(TYPE_CHANGE);
        parent.getCalculationsPanel().enableButtons(true);
        parent.getCalculationsPanel().initButtonOnTask();
        parent.getCalculationsPanel().updateEquation(true);
      }

      /*zpwn: push to undoStack*/

      //if (!undoFlag) {
        if (!valueButton.isSelected()) {
          for (int i = 0; i < boxList.size(); i++) {
            if (e.getSource() == boxList.get(i) && boxList.get(i).isSelected()) {
              itemChanged = boxList.get(i).getText();
              System.out.println("push: " + boxList.get(i).getText());
              undoStack.push(boxList.get(i));
            } else {
            }
          }
          System.out.println("...");
        }
        undoFlag = true;
        undoButton.setEnabled(undoFlag);
      //
      /*done*/

    if (!skip || (skip && currentVertex.type.equalsIgnoreCase("flow"))) {
      //Find the box which had the state change
      for (int i = 0; i < boxList.size(); i++) {
        if (e.getSource() == boxList.get(i)) {
          //Find the vertex associated with the check box
          Vertex v = null;
          for (int n = 0; n < g.getVertexes().size(); n++) {
            if ((((Vertex) g.getVertexes().get(n)).nodeName).equals(boxList.get(i).getText())) {
              v = (Vertex) (g.getVertexes().get(n));
              continue;
            }
          }
          //Verify that the edge does not already exist
          boolean edgeAlreadyExists = false;
          if (e.getStateChange() == e.SELECTED) {
            for (int j = 0; j < currentVertex.inedges.size(); j++) {
              if (currentVertex.inedges.get(j).start == v && !currentVertex.inedges.get(j).edgetype.equals("flowlink")) {
                edgeAlreadyExists = true;
                System.out.println("EDGE ALREADY EXISTS!");
                continue;
              }
            }
            //If the edge doesn't already exist, add a new regular inedge
            if (!edgeAlreadyExists) {
              Edge ed = g.addEdge(v, currentVertex, "regularlink");
              //g.select(ed);
              //ed.edgetype = "regularlink";
              currentVertex.addInEdge(ed);
              v.addOutEdge(ed);
              gc.repaint(0);
              //parent.getCalculationsPanel().updateInputs(); // moved to TabbedGUI.java
              valueButton.setSelected(false);
              inputsButton.setSelected(true);
              logger.concatOut(Logger.ACTIVITY, "InputsPanel.itemStateChanged.1", v.nodeName + "-" + currentVertex.nodeName);
              if(this.correctnessOfInputs()) //the current inputs are correct
                logger.concatOut(Logger.ACTIVITY, "InputsPanel.itemStateChanged.3", "correct");
              else //the current inputs are wrong
                logger.concatOut(Logger.ACTIVITY, "InputsPanel.itemStateChanged.3", "wrong");
            }
          } else {
          //If the checkbox is for an inedge
            boolean edgeRemoved = false;
            for (int j = 0; j < currentVertex.inedges.size(); j++) {
              Edge edge = currentVertex.inedges.get(j);
              if (edge.start == v && edge.end == currentVertex) { //&& !skip) {
                g.delEdge(edge);
                gc.repaint();
                edgeRemoved = true;
                //parent.getCalculationsPanel().updateInputs();// moved to TabbedGUI.java
                logger.concatOut(Logger.ACTIVITY, "InputsPanel.itemStateChanged.2", v.nodeName + "-" + currentVertex.nodeName);
                if(this.correctnessOfInputs()) //the current inputs are correct
                  logger.concatOut(Logger.ACTIVITY, "InputsPanel.itemStateChanged.3", "correct");
                else //the current inputs are wrong
                  logger.concatOut(Logger.ACTIVITY, "InputsPanel.itemStateChanged.3", "wrong");
                continue;
              }
            }
            //If the checkbox is for an outedge
            if (edgeRemoved == false) {
              for (int j = 0; j < currentVertex.outedges.size(); j++) {
                Edge edge = currentVertex.outedges.get(j);
                if (edge.start == currentVertex && edge.end == v && edge.edgetype.equals("flowlink")) {
                  g.delEdge(edge);
                  gc.repaint();
                  //parent.getCalculationsPanel().updateInputs();// moved to TabbedGUI.java
                  logger.concatOut(Logger.ACTIVITY, "InputsPanel.itemStateChanged.2", v.nodeName + "-" + currentVertex.nodeName);
                  System.out.println("removeing the outedge");
                  if(this.correctnessOfInputs()) //the current inputs are correct
                    logger.concatOut(Logger.ACTIVITY, "InputsPanel.itemStateChanged.3", "correct");
                  else //the current inputs are wrong
                    logger.concatOut(Logger.ACTIVITY, "InputsPanel.itemStateChanged.3", "wrong");
                  continue;
                }
              }
            }
          }
        }
      }
    }

    if (!skip) {
      for (int i = 0; i < currentVertex.inedges.size(); i++) {
        currentVertex.inedges.get(i).showInListModel = true;
        currentVertex.inedges.get(i).edgetype = "regularlink";
      }
      parent.getCalculationsPanel().updateInputs();
    }
  }
}

  /**
   * This method returns true if the value button is selected, false if it isn't
   * @return whether the value button is selected
   */
  public boolean getValueButtonSelected() {
    if (valueButton.isSelected() == true) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * This method returns true if the inputs button is selected, false if it isn't
   * @return whether the inputs button is selected
   */
  public boolean getInputsButtonSelected() {
    if (inputsButton.isSelected() == true) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * This method is called when the user selects a name and description for the
   * node currently being edited.
   */
  public void updateDescription() {
    currentVertexDescriptionLabel.setText("<html>" + "Description: <br/>" + currentVertex.correctDescription + "</html>");
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonPanel = new javax.swing.JPanel();
        hintButton = new javax.swing.JButton();
        checkButton = new javax.swing.JButton();
        giveUpButton = new javax.swing.JButton();
        hintPanel = new javax.swing.JPanel();
        hintLabel = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        undoButton = new javax.swing.JButton();
        contentPanel = new javax.swing.JPanel();
        checkBoxScrollPane = new javax.swing.JScrollPane();
        currentInputPanel = new javax.swing.JPanel();
        radioPanel = new javax.swing.JPanel();
        inputsButton = new javax.swing.JRadioButton();
        valueButton = new javax.swing.JRadioButton();
        currentVertexDescriptionLabel = new javax.swing.JLabel();

        buttonPanel.setMaximumSize(new java.awt.Dimension(466, 92));
        buttonPanel.setMinimumSize(new java.awt.Dimension(466, 92));

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

        giveUpButton.setText("Give Up");
        giveUpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                giveUpButtonActionPerformed(evt);
            }
        });

        hintLabel.setBackground(new java.awt.Color(0, 0, 0));
        hintLabel.setText("Hint");

        javax.swing.GroupLayout hintPanelLayout = new javax.swing.GroupLayout(hintPanel);
        hintPanel.setLayout(hintPanelLayout);
        hintPanelLayout.setHorizontalGroup(
            hintPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(hintPanelLayout.createSequentialGroup()
                .addGap(98, 98, 98)
                .addComponent(hintLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE)
                .addContainerGap())
        );
        hintPanelLayout.setVerticalGroup(
            hintPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(hintLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE)
        );

        jButton1.setText("Ok");

        jButton2.setText("Cancel");

        undoButton.setText("Undo");
        undoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                undoButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout buttonPanelLayout = new javax.swing.GroupLayout(buttonPanel);
        buttonPanel.setLayout(buttonPanelLayout);
        buttonPanelLayout.setHorizontalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(hintPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(buttonPanelLayout.createSequentialGroup()
                        .addComponent(hintButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(undoButton)
                        .addGap(11, 11, 11)
                        .addComponent(checkButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(giveUpButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)))
                .addContainerGap(28, Short.MAX_VALUE))
        );
        buttonPanelLayout.setVerticalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(hintButton)
                    .addComponent(jButton2)
                    .addComponent(jButton1)
                    .addComponent(checkButton)
                    .addComponent(giveUpButton)
                    .addComponent(undoButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(hintPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        checkBoxScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        checkBoxScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        currentInputPanel.setMaximumSize(new java.awt.Dimension(32767, 500));

        javax.swing.GroupLayout currentInputPanelLayout = new javax.swing.GroupLayout(currentInputPanel);
        currentInputPanel.setLayout(currentInputPanelLayout);
        currentInputPanelLayout.setHorizontalGroup(
            currentInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 408, Short.MAX_VALUE)
        );
        currentInputPanelLayout.setVerticalGroup(
            currentInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 248, Short.MAX_VALUE)
        );

        checkBoxScrollPane.setViewportView(currentInputPanel);

        buttonGroup1.add(inputsButton);
        inputsButton.setText("Inputs:");
        inputsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputsButtonActionPerformed(evt);
            }
        });

        buttonGroup1.add(valueButton);
        valueButton.setText("Value is given, so no inputs");
        valueButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                valueButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout radioPanelLayout = new javax.swing.GroupLayout(radioPanel);
        radioPanel.setLayout(radioPanelLayout);
        radioPanelLayout.setHorizontalGroup(
            radioPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(radioPanelLayout.createSequentialGroup()
                .addGroup(radioPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(valueButton)
                    .addComponent(inputsButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        radioPanelLayout.setVerticalGroup(
            radioPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(radioPanelLayout.createSequentialGroup()
                .addComponent(valueButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(inputsButton))
        );

        currentVertexDescriptionLabel.setText("Description");
        currentVertexDescriptionLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout contentPanelLayout = new javax.swing.GroupLayout(contentPanel);
        contentPanel.setLayout(contentPanelLayout);
        contentPanelLayout.setHorizontalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(contentPanelLayout.createSequentialGroup()
                        .addComponent(checkBoxScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 327, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(currentVertexDescriptionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(radioPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(355, Short.MAX_VALUE))
        );
        contentPanelLayout.setVerticalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentPanelLayout.createSequentialGroup()
                .addComponent(radioPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(currentVertexDescriptionLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                    .addComponent(checkBoxScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(contentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(contentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void valueButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_valueButtonActionPerformed
      boolean skip = false;
      
      if (this.giveUpPressed) {
        if ((currentVertex.getCalculationsButtonStatus() == currentVertex.GAVEUP || currentVertex.getCalculationsButtonStatus() == currentVertex.CORRECT)) {
          skip = true;
        }
      }
      

      inputsButton.setSelected(false);
      valueButtonPreviouslySelected=true;
//    inputsButtonPreviouslySelected = false;
      System.out.println("inputsButtonPreviouslySelected:"+inputsButtonPreviouslySelected);
      displayCurrentInputsPanel(false);
      /**zpwn: push to undoStack*/   
            undoFlag = true;
            undoStack.push(valueButton);
            System.out.println("Push ValueButton");
            undoButton.setEnabled(undoFlag);      
          /*done*/
            
            
      if(this.currentVertex.correctType.equals("constant")) //the selecion is right
        logger.concatOut(Logger.ACTIVITY, "InputsPanel.valueButtonActionPerformed.1","correct");
      else //the selction is wrong
        logger.concatOut(Logger.ACTIVITY, "InputsPanel.valueButtonActionPerformed.1","wrong");

      if (!currentVertex.type.equalsIgnoreCase("constant")) {
        //change the input and graph status so that the (i), and (g) (possibly the (c)) circles
        //on the vertex turns white
        currentVertex.setInputsButtonStatus(currentVertex.NOSTATUS);
        if (currentVertex.getCalculationsButtonStatus() == currentVertex.WRONG) {
          if (!currentVertex.isCalculationTypeCorrect) {
            currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);
          }
        }

        currentVertex.inputsSelected = false;

        //reset background colors
        resetColors();

        if (!skip) {
          resetGraphStatus();
          parent.getCalculationsPanel().resetColors(TYPE_CHANGE);
          parent.getCalculationsPanel().clearEquationArea(TYPE_CHANGE);
        }

        currentVertex.type = "constant";

        parent.getCalculationsPanel().enableButtons(true);
        parent.getCalculationsPanel().update();
        parent.getCalculationsPanel().initButtonOnTask();

//        currentInputPanel.setVisible(false);
        gc.repaint(0);

//        if (valueButton.isSelected()) {
          
          
          if(this.currentVertex.correctType.equals("constant")) //the selecion is right
            logger.concatOut(Logger.ACTIVITY, "InputsPanel.valueButtonActionPerformed.1","correct");
          else //the selction is wrong
            logger.concatOut(Logger.ACTIVITY, "InputsPanel.valueButtonActionPerformed.1","wrong");
          for (JCheckBox box : boxList) {
            box.setSelected(false);
          }
//        }
      }
    }//GEN-LAST:event_valueButtonActionPerformed

    private void inputsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputsButtonActionPerformed
      boolean skip = false;

      // TODO add your handling code here:
      valueButton.setSelected(false);
      displayCurrentInputsPanel(true);
      //valueButtonPreviouslySelected=false;
      inputsButtonPreviouslySelected = true;
      
      System.out.println("valueButtonPreviouslySelected:"+valueButtonPreviouslySelected);
      /*zpwn: push to the undoStack*/
            undoFlag = true;
            undoStack.push(inputsButton);
            System.out.println("Push InputsButton");
            undoButton.setEnabled(undoFlag);    
      /*done*/
      //System.out.println("push input");

      if (this.giveUpPressed) {
        if ((currentVertex.getCalculationsButtonStatus() == currentVertex.GAVEUP || currentVertex.getCalculationsButtonStatus() == currentVertex.CORRECT)) {
          skip = true;
        }
      }

      if(!currentVertex.type.equalsIgnoreCase("flow") && !currentVertex.type.equals("stock")) {
        //change the input and graph status so that the (i), and (g) (possibly the (c)) circles
        //on the vertex turns white
        currentVertex.setInputsButtonStatus(currentVertex.NOSTATUS);
        if (currentVertex.getCalculationsButtonStatus() == currentVertex.WRONG) {
          if (!currentVertex.isCalculationTypeCorrect) {
            currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);
          }
        }

       
        currentVertex.inputsSelected = true;

        //reset background colors
        resetColors();

        if (!skip) {
          resetGraphStatus();
          parent.getCalculationsPanel().resetColors(TYPE_CHANGE);
          parent.getCalculationsPanel().clearEquationArea(TYPE_CHANGE);
        }

        /*zpwn:added to avoid getting green or red not to make user confused.*/
        //currentInputPanel.setBackground(contentPanel.getBackground());
        //for (JCheckBox box : boxList) {
        //  box.setBackground(contentPanel.getBackground());
        //  box.setVisible(true);
        //}
        /*zpwn:done*/

        //currentInputPanel.setBackground(contentPanel.getBackground());
        // for (JCheckBox box : boxList) {
        //        box.setBackground(contentPanel.getBackground());
        //        box.setVisible(true);
        //      }
        /*zpwn:done*/

       
//       else
//       {
//         valueButtonPreviouslySelected = false;
//       }

        if (this.giveUpPressed && currentVertex.isCalculationTypeCorrect) {
          // Do nothing
        } else {
          currentVertex.type = "none";
        }

        parent.getCalculationsPanel().enableButtons(true);
        parent.getCalculationsPanel().update();
        parent.getCalculationsPanel().initButtonOnTask();

        //set the message to show that the node doesn't have any inputs
        boolean hasInputs = false;
        for (int i = 0; i < g.getVertexes().size(); i++) {
          Vertex vertex = (Vertex) (g.getVertexes().get(i));
          if (!vertex.nodeName.equals("") && !vertex.label.equals(currentVertex.label)) {
            hasInputs = true;
            continue;
          }
        }
        if (hasInputs == false) {
          JTextArea txt=new JTextArea("Create some more nodes, and they will appear here.  You have created only one node, and it cannot be its own input, so there is nothing to display here.");
          txt.setLineWrap(true);
          txt.setEditable(false);
          txt.setBackground(new java.awt.Color(240, 240, 240));
          txt.setWrapStyleWord(true);
          txt.setFont(new Font("Arial", Font.PLAIN, 14));
          txt.setMargin(new java.awt.Insets(50, 5, 0, 0));
          currentInputPanel.add(txt);
          parent.repaint(0);
        }       

//        if (inputsButton.isSelected()) {
          
          if(!this.currentVertex.correctType.equals("constant")) //the selection is correct
            logger.concatOut(Logger.ACTIVITY, "InputsPanel.inputsButtonActionPerformed.1","correct");
          else
            logger.concatOut(Logger.ACTIVITY, "InputsPanel.inputsButtonActionPerformed.1","wrong");
//        }
        gc.repaint(0);
      }
    }//GEN-LAST:event_inputsButtonActionPerformed

    private void hintButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hintButtonActionPerformed
      if (!initializing) {
        undoFlag = true;
        if (hintLabel.getForeground().equals(Color.BLACK)) {
          hintLabel.setForeground(new Color(240, 240, 240));
          hintPanel.setBackground(new Color(240, 240, 240));
          logger.out(Logger.ACTIVITY, "InputsPanel.hintButtonActionPerformed.1");
        } else {
          hintLabel.setForeground(Color.BLACK);
          hintPanel.setBackground(new Color(255, 204, 0));
          logger.out(Logger.ACTIVITY, "InputsPanel.hintButtonActionPerformed.2");
        }
      }
}//GEN-LAST:event_hintButtonActionPerformed

private boolean correctnessOfInputs() {
  boolean correctInput=true;

  if (currentVertex.correctType.equals("constant") != valueButton.isSelected()) {
    correctInput = false;
  } else {
    LinkedList<Edge> inputEdges= currentVertex.inedges;
    if(currentVertex.correctType.equals("flow") || currentVertex.correctType.equals("auxiliary")) {
      String[] correctinput = currentVertex.correctInputs.split(",");

      if(correctinput.length==inputEdges.size()) {
        for(int i=0; i<correctinput.length;i++) {
          if (!currentVertex.correctInputs.contains(inputEdges.get(i).start.label)) {
             correctInput = false;
          }
        }
      } else {
        correctInput = false;
      }
    } else if(currentVertex.correctType.equals("stock")) {
      String[] correctoutput = currentVertex.correctOutputs.split(",");
      String[] correctinput = currentVertex.correctInputs.split(",");
      int numOutputs = 0;
      int numInputs = 0;

      //find the number of output flowlinks for this stock node
      for (int i = 0; i < correctoutput.length; i++) {
        if (correctoutput[i].contains("flowlink - ")) {
          numOutputs++;
        }
      }

      //find the number of input flowlinks for this stock node
      for (int i = 0; i < correctinput.length; i++) {
        if (correctinput[i].contains("flowlink - ")) {
          numInputs++;
        }
      }

      //make sure there are the correct number of inEdges to the stock node
      if(inputEdges.size() == (numOutputs + numInputs)) {
        //check whether each inEdge agrees with the problem solution
        for(int i=0; i<inputEdges.size();i++) {
          //make sure inEdge is present as a flowlink in the solution file
          if (!(currentVertex.correctOutputs.contains("flowlink - " + inputEdges.get(i).start.label)) &&
              !(currentVertex.correctInputs.contains("flowlink - " + inputEdges.get(i).start.label))) {
             correctInput = false;
          }
        }
      } else {
        correctInput = false;
      }
    }
  }

  return correctInput;
}

/**
 * This function checks for any syntax errors in the inputsTab, and returns true if there are
 * @author Curt Tyler
 * @return boolean
 */
public boolean checkForSyntaxErrors() {
  boolean syntaxError = false;

  if (this.getValueButtonSelected() != true || this.getInputsButtonSelected() != true) {
    syntaxError = true;
  } else if (this.getInputsButtonSelected() == true) {
    syntaxError = true;
    for (JCheckBox box : boxList) {
      // If there is at least one inputs check box selected, then there is no error
      if (box.isSelected() != false) {
        syntaxError = false;
      }
    }
  }

  return syntaxError;
}

/**
 * This is a modified version of checkButtonActionPerformed. This is to be used when
 * needing to know if the user has chosen correct inputs, and not needing this to be
 * done only if the user clicks the checkButton
 *
 * @author Curt Tyler
 * @return boolean
 */
public boolean checkForCorrectInputs() {
  boolean inputsCorrect = true;
  boolean correct = true, correctType = true, correctInput = true;

  if (!valueButton.isSelected() && !inputsButton.isSelected()) {
    correct = false;
    correctType = false;
    correctInput = false;
  } else {
    if(!valueButton.isSelected() && !inputsButton.isSelected()) {
      correct = false; correctType = false; correctInput = false;
    } else {
      if (currentVertex.correctType.equals("constant") != valueButton.isSelected()) {
        correct = false;
        correctType = false;
        correctInput = false;
      } else {
        LinkedList<Edge> inputEdges= currentVertex.inedges;
        if(currentVertex.correctType.equals("flow")) {
          String[] correctinput = currentVertex.correctInputs.split(",");

          if(correctinput.length==inputEdges.size()) {
            for(int i=0; i<correctinput.length;i++) {
              if (!currentVertex.correctInputs.contains(inputEdges.get(i).start.label)) {
                 correct = false;
                 correctInput = false;
              }
            }
          } else {
            correct = false;
            correctInput = false;
          }
        } else if(currentVertex.correctType.equals("stock")) {
          String[] correctoutput = currentVertex.correctOutputs.split(",");
          String[] correctinput = currentVertex.correctInputs.split(",");
          int numOutputs = 0;
          int numInputs = 0;

          //find the number of output flowlinks for this stock node
          for (int i = 0; i < correctoutput.length; i++) {
            if (correctoutput[i].contains("flowlink - ")) {
              numOutputs++;
            }
          }

          //find the number of input flowlinks for this stock node
          for (int i = 0; i < correctinput.length; i++) {
            if (correctinput[i].contains("flowlink - ")) {
              numInputs++;
            }
          }

          //make sure there are the correct number of inEdges to the stock node
          if(inputEdges.size() == (numOutputs + numInputs)) {
            //check whether each inEdge agrees with the problem solution
            for(int i=0; i<inputEdges.size();i++) {
              //make sure inEdge is present as a flowlink in the solution file
              if (!(currentVertex.correctOutputs.contains("flowlink - "+inputEdges.get(i).start.label)) && !(currentVertex.correctInputs.contains("flowlink - "+inputEdges.get(i).start.label))) {
                 correct = false;
                 correctInput = false;
              }
            }
          } else {
            correct = false;
            correctInput = false;
          }
        }
      }
    }
  }

  inputsCorrect = correct & correctType & correctInput;

  return inputsCorrect;
}

    private void checkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkButtonActionPerformed
      if (!initializing) {

        logger.concatOut(Logger.ACTIVITY, "No message", "Click check button try");
        String returnMsg=blockSocket.blockQuery(this,"Click check button");
        if(!returnMsg.equals("allow")) //the action is not allowed by meta tutor
        {
          new MetaTutorMsg(returnMsg.split(":")[1],false).setVisible(true);
          return;
        }

        if (!parent.getDescriptionPanel().duplicatedNode(currentVertex.nodeName)) {
          parent.getCalculationsPanel().enableButtons(true);
          logger.out(Logger.ACTIVITY, "InputsPanel.checkButtonActionPerformed.1");

          boolean correct = true, correctType = true, correctInput = true;

          //if(currentVertex.getCalculationsButtonStatus()==currentVertex.GAVEUP)
          //{
          //  correct = false; correctType = false; correctInput = false;
          //}


          if (!valueButton.isSelected() && !inputsButton.isSelected()) {
            correct = false;
            correctType = false;
            correctInput = false;
          } else {
            System.out.println("**888*" + currentVertex.correctType);
            if (currentVertex.correctType.equals("constant") != valueButton.isSelected()) {
              correct = false;
              correctType = false;
              correctInput = false;
            } else {
              correctInput = correctnessOfInputs();
              correct = correct & correctInput;
            }
            /**done**/
            if (correct) {
              logger.out(Logger.ACTIVITY, "InputsPanel.checkButtonActionPerformed.2");

              radioPanel.setBackground(new Color(155,250,140));
              valueButton.setBackground(new Color(155,250,140));
              inputsButton.setBackground(new Color(155,250,140));
              currentInputPanel.setBackground(new Color(155,250,140));
              for (JCheckBox box : boxList) {
                box.setBackground(new Color(155,250,140));
              }
              currentVertex.setInputsButtonStatus(currentVertex.CORRECT);
              // disable access to radioPanel, valueButton, inputsButton, currentInputPanel,
              // and bottom buttons
              radioPanel.setEnabled(false);
              valueButton.setEnabled(false);
              inputsButton.setEnabled(false);
              for (JCheckBox box : boxList) {
                box.setEnabled(false);
              }
              checkButton.setEnabled(false);
              giveUpButton.setEnabled(false);
              undoButton.setEnabled(false);
              hintButton.setEnabled(false);
            }  else {
            /* checking efficiently modified by zpwn*/
                logger.out(Logger.ACTIVITY, "InputsPanel.checkButtonActionPerformed.3");

                if (!correctType) {
                  radioPanel.setBackground(Color.pink);
                  valueButton.setBackground(Color.pink);
                  inputsButton.setBackground(Color.pink);
                  currentVertex.setInputsButtonStatus(currentVertex.WRONG);
                } if (!correctinput && correctType) {
                  currentInputPanel.setBackground(Color.pink);
                  for (JCheckBox box : boxList) {
                    box.setBackground(Color.pink);
                  }
                  radioPanel.setBackground(new Color(155,250,140));
                  valueButton.setBackground(new Color(155,250,140));
                  inputsButton.setBackground(new Color(155,250,140));
                  currentVertex.setInputsButtonStatus(currentVertex.WRONG);
                }if (!correctinput && !correctType) {
                  currentInputPanel.setBackground(Color.pink);
                  for (JCheckBox box : boxList) {
                    box.setBackground(Color.pink);
                  }
                  radioPanel.setBackground(Color.pink);
                  valueButton.setBackground(Color.pink);
                  inputsButton.setBackground(Color.pink);
                  currentVertex.setInputsButtonStatus(currentVertex.WRONG);
                }
                /**done**/
            }
          }
        } else {
          MessageDialog.showMessageDialog(null, true, "This node is the same as another node you've already defined, please choose a different description.", g);
        }
      }
}//GEN-LAST:event_checkButtonActionPerformed


public boolean areAllCorrectInputsAvailable() {
  String [] correctInputs = currentVertex.correctInputs.split(",");
  String [] correctOutputs = currentVertex.correctOutputs.split(",");

  boolean yesNo = false;

  if (!currentVertex.correctType.equalsIgnoreCase("constant")) {
    boolean notAllInputsThere = false;
    boolean notAllOutputsThere = false;

    for (int i = 0; i < correctInputs.length; i++) {
      boolean correctInputFound = false;

      if (!correctInputs[0].equals("")) {
        for (int j = 0; j < g.getVertexes().size(); j++) {
          if (correctInputs[i].trim().equals("regularlink - " + ((Vertex)g.getVertexes().get(j)).nodeName) ||
              correctInputs[i].trim().equals("flowlink - " + ((Vertex)g.getVertexes().get(j)).nodeName)) {
            correctInputFound = true;
          }
        }

        if (!correctInputFound) {
          notAllInputsThere = true;
        }
      }
    }

    for (int i = 0; i < correctOutputs.length; i++) {
      boolean correctOutputFound = false;

      if (!correctOutputs[0].equals("")) {
        for (int j = 0; j < g.getVertexes().size(); j++) {
          if (correctOutputs[i].trim().equals("regularlink - " + ((Vertex)g.getVertexes().get(j)).nodeName) ||
              correctOutputs[i].trim().equals("flowlink - " + ((Vertex)g.getVertexes().get(j)).nodeName)) {
            correctOutputFound = true;
          }
        }

        if (!correctOutputFound) {
          notAllOutputsThere = true;
        }
      }
    }

    yesNo = !(notAllInputsThere | notAllOutputsThere);

  } else {
    yesNo = true;
  }

  return yesNo;
}
  
    private void giveUpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_giveUpButtonActionPerformed
      if (!initializing) {
        if (!parent.getDescriptionPanel().duplicatedNode(currentVertex.nodeName)) {
          if (areAllCorrectInputsAvailable() != false) {

            this.giveUpPressed = true;

            logger.concatOut(Logger.ACTIVITY, "No message", "Click giveup button try");
            String returnMsg = blockSocket.blockQuery(this,"Click giveup button");
            if(!returnMsg.equals("allow")) //the action is not allowed by meta tutor
            {
              new MetaTutorMsg(returnMsg.split(":")[1],false).setVisible(true);
              return;
            }

            correctinput = false;
            System.out.println("g.getVertexes().size():"+g.getVertexes().size());

            undoFlag = true;

            //reset the flags that tell which radio button was selected last
            valueButtonPreviouslySelected = false;
            inputsButtonPreviouslySelected = false;

            undoFlag=true;
            //Clear existing answer
            //parent.getCalculationsPanel().enableButtons(true);
            for (JCheckBox box : boxList) {
                if (box.isSelected()) {
                  box.setSelected(false);
                }
              }
            valueButton.setSelected(false);
            inputsButton.setSelected(false);

            if (currentVertex.correctType.equalsIgnoreCase("constant")) {
              //currentVertex.type = "constant";
              valueButton.setSelected(true);
              inputsButton.setSelected(false);
              currentVertex.type = "constant";
              parent.getCalculationsPanel().update();
              currentVertex.inputsSelected = false;
              currentInputPanel.setVisible(false);
              gc.repaint(0);
              correctinput=true;
            } else {
              if (currentVertex.getCalculationsButtonStatus() != currentVertex.GAVEUP && currentVertex.getCalculationsButtonStatus() != currentVertex.CORRECT) {
                inputsButton.setSelected(true);
                currentInputPanel.setVisible(true);
                valueButton.setSelected(false);

                if (!currentVertex.isCalculationTypeCorrect) {
                  currentVertex.type = "none";
                }

                parent.getCalculationsPanel().enableButtons(true);
                parent.getCalculationsPanel().update();
              } else if (currentVertex.getCalculationsButtonStatus() == currentVertex.GAVEUP || currentVertex.getCalculationsButtonStatus() == currentVertex.CORRECT) {
                parent.getCalculationsPanel().enableButtons(false);
                
                currentInputPanel.setVisible(true);
                currentVertex.type = currentVertex.correctType;
              }

              currentVertex.inputsSelected = true;
              correctinput=true;
            }

            if(correctinput)
            {
              //currentVertex.setInputsButtonStatus(currentVertex.GAVEUP);
              parent.getCalculationsPanel().enableButtons(true);
              for (JCheckBox box : boxList) {
                box.setVisible(true);
                if (currentVertex.correctInputs.contains("regularlink - " + box.getText())) {
                  box.setSelected(true);
                } else if (currentVertex.correctType.equals("stock") && currentVertex.correctOutputs.contains("flowlink - " + box.getText())) {
                  box.setSelected(true);
                } else if (currentVertex.correctInputs.contains("flowlink - " + box.getText()) && currentVertex.correctType.equals("stock")) {
                  box.setSelected(true);
                }
              }

              // change the background color of radioPanel, valueButton, inputsButton, and currentInputPanel
              // to yellow
              radioPanel.setBackground(new Color(252,252,130));
              valueButton.setBackground(new Color(252,252,130));
              inputsButton.setBackground(new Color(252,252,130));
              currentInputPanel.setBackground(new Color(252,252,130));
              for (JCheckBox box : boxList) {
                box.setBackground(new Color(252,252,130));
              }

              // disable access to radioPanel, valueButton, inputsButton, currentInputPanel,
              // and bottom buttons
              radioPanel.setEnabled(false);
              valueButton.setEnabled(false);
              inputsButton.setEnabled(false);
              for (JCheckBox box : boxList) {
                box.setEnabled(false);
              }
              checkButton.setEnabled(false);
              giveUpButton.setEnabled(false);
              undoButton.setEnabled(false);
              hintButton.setEnabled(false);

              gc.repaint(0);

              currentVertex.setInputsButtonStatus(currentVertex.GAVEUP);
              logger.out(Logger.ACTIVITY, "InputsPanel.giveUpButtonActionPerformed.1");
              this.parent.setAlwaysOnTop(true);

            }

            //if (currentVertex.getCalculationsButtonStatus() == currentVertex.GAVEUP) {

              //String[] inputs = currentVertex.correctInputs.split(",");
              //for  (int i = 0; i < inputs.length; i++) {
              //  Edge ed = g.addEdge(, currentVertex, "regularlink");
                //g.select(ed);
                //ed.edgetype = "regularlink";
              //  currentVertex.addInEdge(ed);
              //  v.addOutEdge(ed);
             // }

            //}
          } else {
            this.parent.setAlwaysOnTop(false);
            MessageDialog.showMessageDialog(null, true, "Sorry, you cannot give up until you have all the neccesary nodes defined that are needed as inputs for this node.", g);
          }
        } else {
          MessageDialog.showMessageDialog(null, true, "This node is the same as another node you've already defined, please choose a different description.", g);
        }
      }

      this.giveUpPressed = false;

}//GEN-LAST:event_giveUpButtonActionPerformed

    private void undoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undoButtonActionPerformed
      if (!initializing) {
        /* zpwn: added code to trace undoStack*/
        try {
          System.out.println("undoStack Size: " + undoStack.size());
          if (!undoStack.isEmpty()) {
            undoFlag = true;
            Object obj = undoStack.pop();
            if (obj.getClass().equals(JCheckBox.class)) {
              System.out.println("UndoStack pop: JcheckBox");
              if (((JCheckBox) obj).isSelected()) {
                ((JCheckBox) obj).setSelected(false);
              }

              if (this.correctnessOfInputs()) //the current inputs are correct
              {
                logger.concatOut(Logger.ACTIVITY, "InputsPanel.itemStateChanged.3", "correct");
              } else //the current inputs are wrong
              {
                logger.concatOut(Logger.ACTIVITY, "InputsPanel.itemStateChanged.3", "wrong");
              }

            } else if (obj.getClass().equals(JRadioButton.class)) {
              JRadioButton jbutton = ((JRadioButton) obj);
              buttonGroup1.clearSelection();

              if (inputsButton == jbutton) {
                obj = undoStack.lastElement();
                if (obj != null) {
                  if (obj.getClass().equals(JRadioButton.class) && ((JRadioButton) obj) == inputsButton) {
                    inputsButton.setSelected(true);
                    currentVertex.type = "none";
                    currentVertex.inputsSelected = true;
                    displayCurrentInputsPanel(true);
                  } else //if(valueButtonPreviouslySelected ==true)
                  {
                    valueButton.setSelected(true);
                    displayCurrentInputsPanel(false);
                    currentVertex.type = "constant";
                  }
                } else {
                  buttonGroup1.clearSelection();
                  displayCurrentInputsPanel(false);
                }
                if (this.currentVertex.correctType.equals("constant")) //the selection is correct
                {
                  logger.concatOut(Logger.ACTIVITY, "InputsPanel.valueButtonActionPerformed.1", "correct");
                } else {
                  logger.concatOut(Logger.ACTIVITY, "InputsPanel.valueButtonActionPerformed.1", "wrong");
                }
              } else if (valueButton == jbutton) {
                obj = undoStack.lastElement();
                if (obj != null) {
                  if (obj.getClass().equals(JRadioButton.class) && ((JRadioButton) obj) == valueButton) {
                    valueButton.setSelected(true);
                    currentVertex.type = "constant";
                    displayCurrentInputsPanel(false);
                  } else //if(inputsButtonPreviouslySelected ==true)
                  {
                    inputsButton.setSelected(true);
                    displayCurrentInputsPanel(true);
                    currentVertex.type = "none";
                    currentVertex.inputsSelected = true;
                    System.out.println("4");
                  }
                } else {
                  buttonGroup1.clearSelection();
                  displayCurrentInputsPanel(false);
                }

                if (!this.currentVertex.correctType.equals("constant")) //the selection is correct
                {
                  logger.concatOut(Logger.ACTIVITY, "InputsPanel.inputsButtonActionPerformed.1", "correct");
                } else {
                  logger.concatOut(Logger.ACTIVITY, "InputsPanel.inputsButtonActionPerformed.1", "wrong");
                }

              }
            }
            undoFlag = false;

          }

          resetGraphStatus();
          resetColors();
          parent.getCalculationsPanel().enableButtons(true);
          parent.getCalculationsPanel().update();
          parent.getCalculationsPanel().initButtonOnTask();
          gc.repaint(0);
          undoButton.setEnabled(undoFlag);
        } catch (java.lang.NullPointerException npe) {
          System.out.println("Why null?: " + npe.getMessage());
        } catch (java.util.EmptyStackException etyst) {
          System.out.println("Stack is empty: " + etyst.getMessage());
          //undoFlag=false;
        }
        /* done*/

        logger.out(Logger.ACTIVITY, "InputsPanel.undoButtonActionPerformed.1");
      }
    }//GEN-LAST:event_undoButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JScrollPane checkBoxScrollPane;
    private javax.swing.JButton checkButton;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JPanel currentInputPanel;
    private javax.swing.JLabel currentVertexDescriptionLabel;
    private javax.swing.JButton giveUpButton;
    private javax.swing.JButton hintButton;
    private javax.swing.JLabel hintLabel;
    private javax.swing.JPanel hintPanel;
    private javax.swing.JRadioButton inputsButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JPanel radioPanel;
    private javax.swing.JButton undoButton;
    private javax.swing.JRadioButton valueButton;
    // End of variables declaration//GEN-END:variables

}
