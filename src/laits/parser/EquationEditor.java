package laits.parser;

import laits.graph.Graph;
import laits.graph.Vertex;
import laits.gui.MessageDialog;
import laits.log.*;
import java.awt.Component;
import java.util.LinkedList;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * EquationEditor
 *
 * @author Javier Gonzalez
 * @author Helen Chavez
 * @author Megan Kearl
 * @version 20100222
 */
public class EquationEditor extends JFrame {

  private Vertex vertex;
  private String eq;
  private Scanner scanner;
  private Parser parser;
  private DefaultListModel jListModel = new DefaultListModel();
  private static Logger logs = Logger.getLogger();
  private Graph graph;
  private String oldEq;
  private boolean changed = false;

  /**
   * EquationEditor Constructor
   *
   * @param v
   * @param variable
   * @param inputList
   * @param enableNumbers
   */
  public EquationEditor(Component frame, Graph g, Vertex v, String variable, LinkedList<String> inputList, boolean enableNumbers, String oldEquation){
    super ("Equation Editor");
    graph = g;
    vertex = v;
    this.oldEq = oldEquation;

    if(v.equation != null)
    {
        eq = v.equation.toString();
    }
    else eq = "";

    initComponents();

    //Build the displayable label of allowed variables in GUI:
    if ((inputList.size()==0) && ((v.type.equals("auxiliary")) || v.type.equals("flow"))){
      jListModel.add(0,"No links come into this node");
      jListModel.add(1,"Please draw at least one link");
      jListVariables.setEnabled(false);
      jListVariables.setOpaque(false);
    }
    else
      for (int i = 0; i < inputList.size(); i++) {
        jListModel.add(i, inputList.get(i));
      }

    this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    NnCheckBox.setVisible(false);
    statusBar.setVisible(false);
    //the following three lines remove the status area, delete them to show status area again
    this.remove(statusArea);
    this.remove(jScrollPane1);
    this.pack();
    if(v.type.equalsIgnoreCase("constant")) {
      jListVariables.setOpaque(false);
      jListVariables.setEnabled(false);
      this.constantEnableButton();
    }
    if(v.type.equalsIgnoreCase("stock")){
      jListVariables.setOpaque(false);
      jListVariables.setEnabled(false);
      NnCheckBox.setVisible(true);
      NnCheckBox.setEnabled(true);
      this.constantEnableButton();
    }
    enableButton(enableNumbers);
    labelVariable.setText(variable + " =");
    scanner = new Scanner(vertex.equation);
    parser = new Parser();
    if (vertex.equation != null)
      updateEquation(true);
    if(vertex.getNonNegative() == true) {
      NnCheckBox.setSelected(true);
    } else
      NnCheckBox.setSelected(false);
    this.setLocationRelativeTo(frame);
    this.setAlwaysOnTop(true);
  }

  /**
   * Enable or disable the number keys at the keypad
   *
   * @param b if it is true the number keys would be abled.
   */
  private void enableButton(boolean b){
    this.ZeroButton.setEnabled(b);
    this.OneButton.setEnabled(b);
    this.TwoButton.setEnabled(b);
    this.ThreeButton.setEnabled(b);
    this.FourButton.setEnabled(b);
    this.FiveButton.setEnabled(b);
    this.SixButton.setEnabled(b);
    this.SevenButton.setEnabled(b);
    this.EightButton.setEnabled(b);
    this.NineButton.setEnabled(b);
    this.PointButton.setEnabled(b);
  }
  private void constantEnableButton(){
    this.MultiplyButton.setEnabled(false);
    this.DivideButton.setEnabled(false);
    this.SubtractButton.setEnabled(false);
    this.ExponentButton.setEnabled(false);
    this.AddButton.setEnabled(false);
    this.RightParenButton.setEnabled(false);
    this.LeftParenButton.setEnabled(false);
  }

  private void exampleDisableButtons()
  {
      this.ZeroButton.setEnabled(false);
      this.OneButton.setEnabled(false);
      this.TwoButton.setEnabled(false);
      this.ThreeButton.setEnabled(false);
      this.FourButton.setEnabled(false);
      this.FiveButton.setEnabled(false);
      this.SixButton.setEnabled(false);
      this.SevenButton.setEnabled(false);
      this.EightButton.setEnabled(false);
      this.NineButton.setEnabled(false);
      this.PointButton.setEnabled(false);
      this.MultiplyButton.setEnabled(false);
      this.DivideButton.setEnabled(false);
      this.SubtractButton.setEnabled(false);
      this.ExponentButton.setEnabled(false);
      this.AddButton.setEnabled(false);
      this.RightParenButton.setEnabled(false);
      this.LeftParenButton.setEnabled(false);
      this.NnCheckBox.setEnabled(false);
      this.jListVariables.setEnabled(false);
      this.DeleteButton.setEnabled(false);
  }

  /**
   * This method is used to revert to the previous equation if the user decides
   * not to save the equation
   */
  private void revertToOldEquation()
  {

      if(eq.equalsIgnoreCase(""))
      {
          vertex.equation = null;
      }
      else
      {
          int length = vertex.equation.toString().length();
          for(int i = 0; i < length; i++)
          {
              scanner.removeInput();
          }
          vertex.equation = null;
          parser.setEquation(null);

          //this line is necesary for this to function correctly, do not delete!
          vertex.equation = scanner.getEquation();

          for(int i = 0; i < eq.length(); i++)
          {
              int temp = (int)eq.charAt(i);
              //PARENTHESIS
              if(eq.charAt(i) == '(' || eq.charAt(i) == ')')
              {
                  updateEquation(scanner.addInput(eq.charAt(i) + "", Scanner.PARENTHESIS));
              }
              //INTEGER
              else if(temp < 58 && temp > 47)
              {
                  updateEquation(scanner.addInput(eq.charAt(i) + "", Scanner.DIGIT));
              }
              //POINT
              else if(eq.charAt(i) == '.')
              {
                  updateEquation(scanner.addInput(eq.charAt(i) + "", Scanner.POINT));
              }
              //OPERATION
              else if(eq.charAt(i) == '*' || eq.charAt(i) == '/' || eq.charAt(i) == '+' || eq.charAt(i) == '-' || eq.charAt(i) == '^')
              {
                  updateEquation(scanner.addInput(eq.charAt(i) + "", Scanner.OPERATION));
              }
              //VARIABLE
              else
              {
                  String var = "";
                  while(i < eq.length())
                  {
                      if(eq.charAt(i) != '+' && eq.charAt(i) != '-' && eq.charAt(i) != '/' && eq.charAt(i) != '*' && eq.charAt(i) != '(' && eq.charAt(i) != ')' && eq.charAt(i) != '^')
                      {
                        var += eq.charAt(i);
                        i++;
                      }
                      else
                      {
                          i--;
                          break;
                      }
                  }
                  updateEquation(scanner.addInput(var.replace(" ", "_"), Scanner.VARIABLE));
              }
          }
      }   
  }

  @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButtonSubstraction1 = new javax.swing.JButton();
        panel = new javax.swing.JPanel();
        labelVariable = new javax.swing.JLabel();
        jScrollPaneVariables = new javax.swing.JScrollPane();
        jListVariables = new javax.swing.JList();
        padPanel = new javax.swing.JPanel();
        LeftParenButton = new javax.swing.JButton();
        ZeroButton = new javax.swing.JButton();
        DeleteButton = new javax.swing.JButton();
        SevenButton = new javax.swing.JButton();
        EightButton = new javax.swing.JButton();
        NineButton = new javax.swing.JButton();
        MultiplyButton = new javax.swing.JButton();
        DivideButton = new javax.swing.JButton();
        FourButton = new javax.swing.JButton();
        FiveButton = new javax.swing.JButton();
        SixButton = new javax.swing.JButton();
        SubtractButton = new javax.swing.JButton();
        ExponentButton = new javax.swing.JButton();
        PointButton = new javax.swing.JButton();
        ThreeButton = new javax.swing.JButton();
        RightParenButton = new javax.swing.JButton();
        TwoButton = new javax.swing.JButton();
        OneButton = new javax.swing.JButton();
        AddButton = new javax.swing.JButton();
        jScrollPaneEquation = new javax.swing.JScrollPane();
        jTextAreaEquation = new javax.swing.JTextArea();
        labelListVariables = new javax.swing.JLabel();
        DoneButton = new javax.swing.JButton();
        statusBar = new javax.swing.JLabel();
        NnCheckBox = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        statusArea = new javax.swing.JTextArea();

        jButtonSubstraction1.setText("-");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        labelVariable.setText("Variable =");

        jListVariables.setModel(jListModel);
        jListVariables.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jListVariables.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jListVariablesMouseClicked(evt);
            }
        });
        jScrollPaneVariables.setViewportView(jListVariables);

        LeftParenButton.setText("(");
        LeftParenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LeftParenButtonActionPerformed(evt);
            }
        });

        ZeroButton.setText("0");
        ZeroButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonActionPerformed(evt);
            }
        });

        DeleteButton.setText("<< Delete");
        DeleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteButtonActionPerformed(evt);
            }
        });

        SevenButton.setText("7");
        SevenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonActionPerformed(evt);
            }
        });

        EightButton.setLabel("8");
        EightButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonActionPerformed(evt);
            }
        });

        NineButton.setLabel("9");
        NineButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonActionPerformed(evt);
            }
        });

        MultiplyButton.setFont(new java.awt.Font("Tahoma", 0, 12));
        MultiplyButton.setText("x");
        MultiplyButton.setActionCommand("*");
        MultiplyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MultiplyButtonActionPerformed(evt);
            }
        });

        DivideButton.setText("/");
        DivideButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonActionPerformed(evt);
            }
        });

        FourButton.setText("4");
        FourButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonActionPerformed(evt);
            }
        });

        FiveButton.setText("5");
        FiveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonActionPerformed(evt);
            }
        });

        SixButton.setText("6");
        SixButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonActionPerformed(evt);
            }
        });

        SubtractButton.setText("-");
        SubtractButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonActionPerformed(evt);
            }
        });

        ExponentButton.setText("^");
        ExponentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonActionPerformed(evt);
            }
        });

        PointButton.setText(".");
        PointButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonActionPerformed(evt);
            }
        });

        ThreeButton.setLabel("3");
        ThreeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonActionPerformed(evt);
            }
        });

        RightParenButton.setText(")");
        RightParenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RightParenButtonActionPerformed(evt);
            }
        });

        TwoButton.setLabel("2");
        TwoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonActionPerformed(evt);
            }
        });

        OneButton.setLabel("1");
        OneButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonActionPerformed(evt);
            }
        });

        AddButton.setText("+");
        AddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout padPanelLayout = new javax.swing.GroupLayout(padPanel);
        padPanel.setLayout(padPanelLayout);
        padPanelLayout.setHorizontalGroup(
            padPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(padPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(padPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(padPanelLayout.createSequentialGroup()
                        .addComponent(FourButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(FiveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(SixButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(SubtractButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ExponentButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(padPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(padPanelLayout.createSequentialGroup()
                            .addGroup(padPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(LeftParenButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(OneButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(padPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(ZeroButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(TwoButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(padPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(padPanelLayout.createSequentialGroup()
                                    .addComponent(ThreeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(AddButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(PointButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(padPanelLayout.createSequentialGroup()
                                    .addComponent(RightParenButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(6, 6, 6)
                                    .addComponent(DeleteButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, padPanelLayout.createSequentialGroup()
                            .addComponent(SevenButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(EightButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(NineButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(MultiplyButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(DivideButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        padPanelLayout.setVerticalGroup(
            padPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(padPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(padPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(padPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(SevenButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(EightButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(NineButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(DivideButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(MultiplyButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(padPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(FourButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(FiveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SixButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SubtractButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ExponentButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(padPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(OneButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TwoButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ThreeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(PointButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(AddButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(padPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ZeroButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(RightParenButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DeleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LeftParenButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTextAreaEquation.setColumns(20);
        jTextAreaEquation.setEditable(false);
        jTextAreaEquation.setLineWrap(true);
        jTextAreaEquation.setRows(5);
        jScrollPaneEquation.setViewportView(jTextAreaEquation);

        labelListVariables.setText("Allowed Variables:");

        DoneButton.setText("Done");
        DoneButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DoneButtonActionPerformed(evt);
            }
        });

        statusBar.setText("Status Bar");
        statusBar.setEnabled(false);

        NnCheckBox.setText("Positive or zero values only");
        NnCheckBox.setEnabled(false);
        NnCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NnCheckBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelLayout.createSequentialGroup()
                        .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelLayout.createSequentialGroup()
                                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(panelLayout.createSequentialGroup()
                                        .addGap(14, 14, 14)
                                        .addComponent(statusBar, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(padPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(DoneButton, javax.swing.GroupLayout.DEFAULT_SIZE, 419, Short.MAX_VALUE)
                                    .addComponent(jScrollPaneVariables, javax.swing.GroupLayout.DEFAULT_SIZE, 419, Short.MAX_VALUE)
                                    .addComponent(labelListVariables)))
                            .addGroup(panelLayout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jScrollPaneEquation, javax.swing.GroupLayout.DEFAULT_SIZE, 684, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addComponent(labelVariable)))
            .addGroup(panelLayout.createSequentialGroup()
                .addComponent(NnCheckBox)
                .addContainerGap(557, Short.MAX_VALUE))
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(NnCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelVariable)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPaneEquation, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelLayout.createSequentialGroup()
                        .addComponent(padPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(statusBar))
                    .addGroup(panelLayout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(labelListVariables)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPaneVariables, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(DoneButton, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(153, Short.MAX_VALUE))
        );

        statusArea.setColumns(20);
        statusArea.setRows(5);
        jScrollPane1.setViewportView(statusArea);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                .addGap(44, 44, 44))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 546, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

  /**
   * Update displayed ecuation in the text area and the error messages
   *
   * @param notError
   * @return
   */
  private boolean updateEquation(boolean notError) {
    jTextAreaEquation.setText(scanner.getEquation().toString());
    if (notError) {
      parser.setEquation(scanner.getEquation());
      if (!parser.parse()) {
        statusBar.setText("sintactic error");
        jTextAreaEquation.setForeground(new java.awt.Color(255, 0, 51));
        disableDone();
        statusArea.append(parser.getError());
      } else {
        statusBar.setText("done!");
        jTextAreaEquation.setForeground(new java.awt.Color(0, 0, 0));
        enableDone();
        statusArea.append(parser.getEquation().getCode());
        return true;
      }
    } else {
      statusBar.setText("lexical error");
      disableDone();
    }
    return false;
 }


    private void ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonActionPerformed
      changed = true;
      updateEquation(scanner.addInput(((javax.swing.JButton)evt.getSource()).getText(), Scanner.DIGIT));
    }//GEN-LAST:event_ButtonActionPerformed

    private void DeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteButtonActionPerformed
      updateEquation(scanner.removeInput());
      changed = true;
    }//GEN-LAST:event_DeleteButtonActionPerformed

    private void DoneButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DoneButtonActionPerformed
      if (updateEquation (true)) {
        vertex.equation = parser.getEquation();
        
        if(!vertex.equation.toString().equalsIgnoreCase(eq))
        {
            Object a[] = graph.getVertexes().toArray();
            for (int j = 0; j < graph.getVertexes().size(); j++)
            {
                ((Vertex) a[j]).setAlreadyRun(false);
            }
        }
        logs.concatOut(Logger.ACTIVITY, "EquationEditor.DoneButtonActionPerformed.1", vertex.label + "");
        logs.concatOut(Logger.ACTIVITY, "EquationEditor.DoneButtonActionPerformed.2", oldEq + " - " + vertex.equation.toString());
      }
      this.setVisible(false);
      vertex.setEditorOpen(false);
      vertex.isSelected = false;
      logs.out(Logger.ACTIVITY, "EquationEditor.DoneButtonActionPerformed.3");
    }//GEN-LAST:event_DoneButtonActionPerformed

    private void jListVariablesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListVariablesMouseClicked
      String s = jListVariables.getSelectedValue().toString();
      updateEquation(scanner.addInput(s.replace(" ", "_"), Scanner.VARIABLE));
      changed = true;
    }//GEN-LAST:event_jListVariablesMouseClicked

    private void formWindowClosing(java.awt.event.WindowEvent evt)//GEN-FIRST:event_formWindowClosing
    {//GEN-HEADEREND:event_formWindowClosing
      if(changed == true)
      {
          String message = "Are you sure you want to exit without saving your equation?";
          MessageDialog.showYesNoDialog(this, true, message, graph);
            //if yes
            if (graph.getN() == 0)
            {
                revertToOldEquation();
                String result = "";
                this.setVisible(false);
                vertex.setEditorOpen(false);
                this.dispose();
            }
            else
            {
    
            }
      }
      else {
          vertex.setEditorOpen(false);
          this.dispose();
      }
    }//GEN-LAST:event_formWindowClosing

    private void NnCheckBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_NnCheckBoxActionPerformed
    {//GEN-HEADEREND:event_NnCheckBoxActionPerformed
      changed = true;
      if(NnCheckBox.isEnabled() == true) {
        if(NnCheckBox.isSelected() == true) {
          vertex.setNonNegative(true);
          Object a[] = graph.getVertexes().toArray();
          for (int j = 0; j < graph.getVertexes().size(); j++)
          {
              ((Vertex) a[j]).setAlreadyRun(false);
          }
        } else 
        {
            vertex.setNonNegative(false);
            Object a[] = graph.getVertexes().toArray();
            for (int j = 0; j < graph.getVertexes().size(); j++)
            {
                ((Vertex) a[j]).setAlreadyRun(false);
            }
        }
      }
    }//GEN-LAST:event_NnCheckBoxActionPerformed

    private void MultiplyButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_MultiplyButtonActionPerformed
    {//GEN-HEADEREND:event_MultiplyButtonActionPerformed
        updateEquation(scanner.addInput("*", Scanner.DIGIT));
        changed = true;
    }//GEN-LAST:event_MultiplyButtonActionPerformed

    private void LeftParenButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LeftParenButtonActionPerformed
        // TODO add your handling code here:
        changed = true;
        updateEquation(scanner.addInput(((javax.swing.JButton)evt.getSource()).getText(), Scanner.PARENTHESIS));
    }//GEN-LAST:event_LeftParenButtonActionPerformed

    private void RightParenButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RightParenButtonActionPerformed
        // TODO add your handling code here:
        changed = true;
        updateEquation(scanner.addInput(((javax.swing.JButton)evt.getSource()).getText(), Scanner.PARENTHESIS));
    }//GEN-LAST:event_RightParenButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddButton;
    private javax.swing.JButton DeleteButton;
    private javax.swing.JButton DivideButton;
    private javax.swing.JButton DoneButton;
    private javax.swing.JButton EightButton;
    private javax.swing.JButton ExponentButton;
    private javax.swing.JButton FiveButton;
    private javax.swing.JButton FourButton;
    private javax.swing.JButton LeftParenButton;
    private javax.swing.JButton MultiplyButton;
    private javax.swing.JButton NineButton;
    private javax.swing.JCheckBox NnCheckBox;
    private javax.swing.JButton OneButton;
    private javax.swing.JButton PointButton;
    private javax.swing.JButton RightParenButton;
    private javax.swing.JButton SevenButton;
    private javax.swing.JButton SixButton;
    private javax.swing.JButton SubtractButton;
    private javax.swing.JButton ThreeButton;
    private javax.swing.JButton TwoButton;
    private javax.swing.JButton ZeroButton;
    private javax.swing.JButton jButtonSubstraction1;
    private javax.swing.JList jListVariables;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPaneEquation;
    private javax.swing.JScrollPane jScrollPaneVariables;
    private javax.swing.JTextArea jTextAreaEquation;
    private javax.swing.JLabel labelListVariables;
    private javax.swing.JLabel labelVariable;
    private javax.swing.JPanel padPanel;
    private javax.swing.JPanel panel;
    private javax.swing.JTextArea statusArea;
    private javax.swing.JLabel statusBar;
    // End of variables declaration//GEN-END:variables

  /**
   * This method disables the done button
   */
  protected void disableDone() {
    DoneButton.setEnabled(false);
  }

  /**
   * This method enables the done button
   */
  protected void enableDone() {
    DoneButton.setEnabled(true);
  }

}
