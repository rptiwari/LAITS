/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * TabbedGUI.java
 *
 * Created on Nov 21, 2010, 10:22:44 AM
 */
package laits.version2;

import laits.comm.CommException;
import laits.data.Task;
import laits.data.TaskFactory;
import laits.graph.Graph;
import laits.graph.GraphCanvas;
import laits.graph.Vertex;
import laits.parser.Parser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import laits.log.Logger;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.logging.Level;
import javax.swing.JFrame;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import laits.BlockSocket;
import laits.Main;
import laits.MetaTutorMsg;
import laits.comm.DataArchive;
import laits.gui.MessageDialog;
import java.awt.Component;
import java.awt.Container;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

/**
 * This method contains a singleton instance of the main window
 * @author Megana
 */
public class TabbedGUI extends javax.swing.JFrame implements WindowListener {

  private Logger logger = Logger.getLogger();
  private DescriptionPanel dPanel;
  private InputsPanel iPanel;
  private CalculationsPanel cPanel;
  private GraphsPanel gPanel;
  private Graph graph;
  private Vertex currentVertex;
  private Parser parser;
  private GraphCanvas graphCanvas;
  private boolean turnOfLogMessagesForThisInstance = false;
  private int currentIndex = 0;
  //Tab Pane Indexes
  int DESCRIPTION = 0;
  int INPUTS = 1;
  int CALCULATIONS = 2;
  int GRAPHS = 3;
  Task task;

  private BlockSocket blockSocket=BlockSocket.getBlockSocket();

  /** 
   * Creates new form TabbedGUI
   */
  public TabbedGUI(Vertex v, Graph g, GraphCanvas gc, boolean show, boolean turnOffLogging) {
    graph = g;
    graphCanvas = gc;
    currentVertex = v;
    turnOfLogMessagesForThisInstance = turnOffLogging;

    try {
      this.task = TaskFactory.getInstance().getActualTask();
    } catch (CommException ex) {
    }

    initComponents();
    initTabs(currentVertex, graph, gc);
    setTabListener();
    System.out.println("KKKKKKKKKKKKK");
    addWindowListener(this);

    if (currentVertex.nodeName.equals("") || currentVertex.nodeName.equals("New Node")) {
      this.setTitle("New Node");
    } else {
      this.setTitle(currentVertex.nodeName);
    }

    if (vertexHasName()) {
      if (allVertexesHaveEquations() && !gc.getModelHasBeenRun()) {
        tabPane.setSelectedIndex(CALCULATIONS);
        if (!turnOfLogMessagesForThisInstance) {
          logger.concatOut(Logger.ACTIVITY, "TabbedGUI.TabbedGUI.10",this.getTitle());
          logger.out(Logger.ACTIVITY, "TabbedGUI.TabbedGUI.3");
        }
      } else if (allVertexesHaveEquations() && gc.getModelHasBeenRun()) {
        tabPane.setSelectedIndex(GRAPHS);
        if (!turnOfLogMessagesForThisInstance) {
          logger.concatOut(Logger.ACTIVITY, "TabbedGUI.TabbedGUI.10",this.getTitle());
          logger.out(Logger.ACTIVITY, "TabbedGUI.TabbedGUI.4");
        }
      } else {
        tabPane.setSelectedIndex(INPUTS);
        if (!turnOfLogMessagesForThisInstance) {
          logger.concatOut(Logger.ACTIVITY, "TabbedGUI.TabbedGUI.10",this.getTitle());
          logger.out(Logger.ACTIVITY, "TabbedGUI.TabbedGUI.2");
        }
      }
    } else {
      tabPane.setSelectedIndex(DESCRIPTION);
      if (!turnOfLogMessagesForThisInstance) {
        logger.concatOut(Logger.ACTIVITY, "TabbedGUI.TabbedGUI.10",this.getTitle());
        logger.out(Logger.ACTIVITY, "TabbedGUI.TabbedGUI.1");
      }
    }

    //Retrieve the correct equation from a file if the correct equation isn't already set
    if (currentVertex.correctEquation == null && !currentVertex.label.equals("")) {
      getCorrectAnswers();
    }
//    if(Main.ReadModelFromFile)
//      filloutTabs();
    
    this.pack();
    
    if (show != false) {
      this.setVisible(true);
      this.setAlwaysOnTop(true);
    }
    
//    this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    this.setResizable(false);
//    this.setLocationRelativeTo(this.getParent());

    this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

    this.requestFocus(true);
    this.setFocusable(true);

    this.setBounds(this.getToolkit().getScreenSize().width - 662, 100, this.getPreferredSize().width, this.getPreferredSize().height);
  }

//  private void filloutTabs(){
//    if(currentVertex.getDescriptionButtonStatus()==currentVertex.CORRECT)
//      this.dPanel.setquantityDescriptionTextField(currentVertex.correctDescription); //fill out description tab
//    if(!this.currentVertex.setEquationyet){
//      this.setEquationsBasedonFile(); //fill out equation tab
//      this.cPanel.setEquation(currentVertex.equation);
//      System.out.println("eq: "+currentVertex.equation.toString());
//      this.currentVertex.setEquationyet=true;
//    }
//    System.out.println("vertex type: "+this.currentVertex.type);
//  }

  public void initTabs(Vertex v, Graph g, GraphCanvas gc) {
    dPanel = new DescriptionPanel(this, v, g, gc);
    descriptionPanel.setLayout(new java.awt.GridLayout(1, 1));
    descriptionPanel.add(dPanel);
    cPanel = new CalculationsPanel(this, v, g, gc);
    calculationPanel.setLayout(new java.awt.GridLayout(1, 1));
    calculationPanel.add(cPanel);

    iPanel = new InputsPanel(this, v, g, gc);
    inputsPanel.setLayout(new java.awt.GridLayout(1, 1));
    inputsPanel.add(iPanel);
   
    if(v.label!=null)
    {
      gPanel = new GraphsPanel(this, v, g, gc);
      graphsPanel.setLayout(new java.awt.GridLayout(1, 1));
      graphsPanel.add(gPanel);
    }
    
  }

  private boolean allVertexesDefined() {
    boolean allDefined = true;
    for (int i = 0; i < graph.getVertexes().size(); i++) {
      Vertex v = (Vertex) graph.getVertexes().get(i);
      if (v.nodeName.equals("") || v.getDescriptionButtonStatus()==v.WRONG || (!v.nodeName.equals("") && v.getDescriptionButtonStatus()==v.NOSTATUS)) {
        allDefined = false;
        continue;
      }
    }
    return allDefined;
  }

  private boolean vertexHasName() {
    if (!currentVertex.nodeName.equals("")) {
      return true;
    } else {
      return false;
    }
  }

  private boolean allVertexesHaveEquations() {
    boolean allHaveEquations = true;
    for (int i = 0; i < graph.getVertexes().size(); i++) {
      Vertex v = (Vertex) graph.getVertexes().get(i);
      if (v.equation == null || v.equation.toString().equals("")) {
        allHaveEquations = false;
        continue;
      }
    }
    return allHaveEquations;
  }

  public Vertex getCurrentVertex() {
    return this.currentVertex;
  }
  

  /**
   * Gets the correct description for the node
   *
   */
    public void getCorrectDescriptionFromFile() {
    String fileName = "";
    try {
      TaskFactory server = TaskFactory.getInstance();
      fileName = server.getActualTask().getLevel() + "-" + server.getActualTask().getTitle().replace(" ", "") + "Solution.txt";
    } catch (CommException ex) {
      //Add correct logger
    }

    String fileLine = "";
    BufferedReader TempReader = null;
    FileInputStream fi;
    try {
      fi = new FileInputStream(fileName);
      TempReader = new BufferedReader(new InputStreamReader(fi));
      while ((fileLine = TempReader.readLine()) != null) {
        if (fileLine.startsWith("SituationDescription: " + currentVertex.label)){
          fileLine = fileLine.replace("SituationDescription: " + currentVertex.label + " = ", "");
          currentVertex.situationDescription = fileLine;
        }
      }
      fi.close();
      TempReader.close();
    } catch (Exception ex) {
      //Add log
    }
  }
    
    public void getCorrectAnswers()
    {
    try {
      DataArchive.getInstance().setVertexInfoBasedOnTaskFile(this.currentVertex);
    } catch (CommException ex) {
      java.util.logging.Logger.getLogger(TabbedGUI.class.getName()).log(Level.SEVERE, null, ex);
    }
    }


  private void setTabListener() {
    final JFrame f= this;
    ChangeListener changeListener = new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        //Only let the user see the descriptions tab
          
          if (allVertexesDefined() == false) {
          if (tabPane.getSelectedIndex() == GRAPHS) {
            tabPane.setSelectedIndex(currentIndex);            
          }
          currentIndex = tabPane.getSelectedIndex();
        }
//          
//        if (allVertexesDefined() == false) {
//          if (tabPane.getSelectedIndex() != DESCRIPTION) {
//            tabPane.setSelectedIndex(DESCRIPTION);
//            MessageDialog.showMessageDialog(f,true, "Before leaving this tab, please use the Check button to make sure your description is correct (green).",graph);
//            if (!turnOfLogMessagesForThisInstance) {
//              logger.out(Logger.ACTIVITY, "TabbedGUI.TabbedGUI.5");
//            }
//            currentIndex = tabPane.getSelectedIndex();
//          }
//        } else {
//          if (tabPane.getSelectedIndex() != GRAPHS) {
//            if (tabPane.getSelectedIndex() != currentIndex) {
//              
//              //Print the appropriate logger
//              if (tabPane.getSelectedIndex() == DESCRIPTION) {
//                currentIndex = tabPane.getSelectedIndex();
//                if (!turnOfLogMessagesForThisInstance) {
//                  logger.out(Logger.ACTIVITY, "TabbedGUI.TabbedGUI.5");
//                }
//              } else if (tabPane.getSelectedIndex() == INPUTS) {
//                if (!turnOfLogMessagesForThisInstance) {
//                  logger.concatOut(Logger.ACTIVITY, "No message", "Go to inputs tab try");
//                  String returnMsg=blockSocket.blockQuery(graphCanvas, "Go to inputs tab");
//                  if(returnMsg.equals("allow")) {
//                    currentIndex = tabPane.getSelectedIndex();
//                    if (!turnOfLogMessagesForThisInstance) {
//                      logger.out(Logger.ACTIVITY, "TabbedGUI.TabbedGUI.6");
//                    }
//                  } else {                  
//                    tabPane.setSelectedIndex(currentIndex);
//                    new MetaTutorMsg(returnMsg.split(":")[1],false).setVisible(true);
//                  }
//                }
//              } else if (tabPane.getSelectedIndex() == CALCULATIONS) {
//                if (!turnOfLogMessagesForThisInstance) {
//                  logger.concatOut(Logger.ACTIVITY, "No message", "Go to calculations tab try");
//                  String returnMsg=blockSocket.blockQuery(graphCanvas, "Go to calculations tab");
//                  if(returnMsg.equals("allow")) {
//                    currentIndex = tabPane.getSelectedIndex();
//                    getCalculationsPanel().updateInputs();
//                    logger.out(Logger.ACTIVITY, "TabbedGUI.TabbedGUI.7");
//                  } else {
//                    tabPane.setSelectedIndex(currentIndex);
//                    new MetaTutorMsg(returnMsg.split(":")[1],false).setVisible(true);
//                  }
//                }
//              }
//            }
//
//          } else {
//            if(currentVertex.getGraphsButtonStatus() == currentVertex.NOSTATUS) {
//              tabPane.setSelectedIndex(currentIndex);
//            }
//          }
//        }
        }

    };
    tabPane.addChangeListener(changeListener);
  }

  public Graph getGraph() {
    return graph;
  }

  public CalculationsPanel getCalculationsPanel() {
    return cPanel;
  }

public DescriptionPanel getDescriptionPanel() {
    return dPanel;
  }
  public InputsPanel getInputsPanel() {
    return iPanel;
  }

  public GraphsPanel getGraphsPanel() {
    return gPanel;
  }

  public void windowClosing(WindowEvent e) {
    System.out.println("currentVertex.getDescriptionButtonStatus():"+currentVertex.getDescriptionButtonStatus());
    if (currentVertex.nodeName.equals("") || (!currentVertex.nodeName.equals("") && (currentVertex.getDescriptionButtonStatus() == 1 || currentVertex.getDescriptionButtonStatus() == 2))) {
      logger.concatOut(Logger.ACTIVITY, "No message", "Close the node try--" + currentVertex.nodeName);
      String returnMsg = blockSocket.blockQuery(this, "Close the node");
      if (returnMsg.equals("allow")) {
        //if (this.getDescriptionPanel().duplicatedNode(currentVertex.nodeName)) {
        //  MessageDialog.showMessageDialog(null, true, "Since this is node is the same as another node you've already defined, it will be deleted.", graph);
        //}

        currentVertex.isOpen = false;
        for (int i = 0; i < graphCanvas.getOpenTabs().size(); i++) {
          if (graphCanvas.getOpenTabs().get(i).currentVertex.nodeName.equals(this.currentVertex.nodeName)) {
            graphCanvas.getOpenTabs().remove(i);
          }
        }
        logger.out(Logger.ACTIVITY, "TabbedGUI.TabbedGUI.9");
        this.setVisible(false);
        System.out.println(currentVertex.nodeName);
        if (currentVertex.nodeName.equals("")) {
          graph.delVertex(currentVertex);
          if (graph.getVertexes().size() != graphCanvas.listOfVertexes.size()) {
            graphCanvas.getCover().getMenuBar().getNewNodeButton().setEnabled(true);
          }
        }
        //if (this.getDescriptionPanel().duplicatedNode(currentVertex.nodeName)) {
        //  graph.delVertex(currentVertex);
        //  if (graph.getVertexes().size() != graphCanvas.listOfVertexes.size()) {
        //    graphCanvas.getCover().getMenuBar().getNewNodeButton().setEnabled(true);
        //  }
        //}
      } else {
        new MetaTutorMsg(returnMsg.split(":")[1], false).setVisible(true);
      }
    } else {
      this.setAlwaysOnTop(true);
      MessageDialog.showMessageDialog(this,true, "Before leaving this tab, please use the Check button to make sure your description is correct (green).",graph);
      
    }
  }
  public void windowOpened(WindowEvent e) {
  }

  public void windowClosed(WindowEvent e) {
    //logger.out(Logger.ACTIVITY, "TabbedGUI.TabbedGUI.9");
  }

  public void windowIconified(WindowEvent e) {
  }

  public void windowDeiconified(WindowEvent e) {
  }

  public void windowActivated(WindowEvent e) {
  }

  public void windowDeactivated(WindowEvent e) {
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabPane = new javax.swing.JTabbedPane();
        descriptionPanel = new javax.swing.JPanel();
        inputsPanel = new javax.swing.JPanel();
        calculationPanel = new javax.swing.JPanel();
        graphsPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        setResizable(false);
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
                formWindowGainedFocus(evt);
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
            }
        });

        tabPane.setMinimumSize(new java.awt.Dimension(500, 500));
        tabPane.setPreferredSize(new java.awt.Dimension(500, 400));
        tabPane.setRequestFocusEnabled(false);
        tabPane.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabPaneMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout descriptionPanelLayout = new javax.swing.GroupLayout(descriptionPanel);
        descriptionPanel.setLayout(descriptionPanelLayout);
        descriptionPanelLayout.setHorizontalGroup(
            descriptionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 645, Short.MAX_VALUE)
        );
        descriptionPanelLayout.setVerticalGroup(
            descriptionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 585, Short.MAX_VALUE)
        );

        tabPane.addTab("Description", descriptionPanel);

        javax.swing.GroupLayout inputsPanelLayout = new javax.swing.GroupLayout(inputsPanel);
        inputsPanel.setLayout(inputsPanelLayout);
        inputsPanelLayout.setHorizontalGroup(
            inputsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 645, Short.MAX_VALUE)
        );
        inputsPanelLayout.setVerticalGroup(
            inputsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 585, Short.MAX_VALUE)
        );

        tabPane.addTab("Inputs", inputsPanel);

        javax.swing.GroupLayout calculationPanelLayout = new javax.swing.GroupLayout(calculationPanel);
        calculationPanel.setLayout(calculationPanelLayout);
        calculationPanelLayout.setHorizontalGroup(
            calculationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 645, Short.MAX_VALUE)
        );
        calculationPanelLayout.setVerticalGroup(
            calculationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 585, Short.MAX_VALUE)
        );

        tabPane.addTab("Calculations", calculationPanel);

        javax.swing.GroupLayout graphsPanelLayout = new javax.swing.GroupLayout(graphsPanel);
        graphsPanel.setLayout(graphsPanelLayout);
        graphsPanelLayout.setHorizontalGroup(
            graphsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 645, Short.MAX_VALUE)
        );
        graphsPanelLayout.setVerticalGroup(
            graphsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 585, Short.MAX_VALUE)
        );

        tabPane.addTab("Graphs", graphsPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabPane, javax.swing.GroupLayout.DEFAULT_SIZE, 650, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabPane, javax.swing.GroupLayout.DEFAULT_SIZE, 613, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

  private void formWindowGainedFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowGainedFocus
    if((Main.professorVersion == Main.VERSION2 || Main.professorVersion == Main.DEMO_VERSION2) && Main.dialogIsShowing)
      this.setEnabled(false);
    else
      this.setEnabled(true);
  }//GEN-LAST:event_formWindowGainedFocus

  private void tabPaneMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabPaneMouseClicked
    // TODO add your handling code here:
  }//GEN-LAST:event_tabPaneMouseClicked
//    /**
//    * @param args the command line arguments
//    */
//    public static void main(String args[]) {
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new TabbedGUI().setVisible(true);
//            }
//        });
//    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel calculationPanel;
    private javax.swing.JPanel descriptionPanel;
    private javax.swing.JPanel graphsPanel;
    private javax.swing.JPanel inputsPanel;
    private javax.swing.JTabbedPane tabPane;
    // End of variables declaration//GEN-END:variables
}
