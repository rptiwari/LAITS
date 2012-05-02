package laits.graph;

import laits.BlockSocket;
import laits.cover.Avatar;
import laits.gui.HintDialog;
import laits.gui.MessageDialog;
import laits.log.Logger;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URI;
import javax.swing.JButton;
import javax.swing.JPanel;
import laits.Main;
import laits.MetaTutorMsg;
import laits.comm.CommException;
import laits.data.TaskFactory;
import laits.gui.ScoresDialog;
import laits.version2.TabbedGUI;
import java.awt.Dialog;
import javax.swing.JLabel;
import java.awt.Frame;
import java.awt.Window;
import java.util.LinkedList;
import javax.swing.JOptionPane;

/**
 * This class is a side bar containing all of the buttons on the graph
 * @author Megan Kearl
 */
public class MenuBar {

    //private Movie movie;
    private JPanel buttonPanel;
    private GraphCanvas gc;
    private Graph graph;
    private Font n;
    private int startPosition = 0, hintLocation = 0;
    private boolean usedHint = false;
    private int professorVersion = 1;
    private static Desktop desktop = null;
    private static Logger logs = Logger.getLogger();
    private JButton diagramButton, equationButton, glossaryButton, nodesDemoButton, linksDemoButton, equationsDemoButton, finishingDemoButton, newAvatarButton;
    private JButton predictButton;
    private JButton newNodeButton;
    private JButton doneButton;
    private JLabel scoreLabel = new JLabel("");
    private Frame parent;
    private int runBtnClickCount = 0;
    private Logger logger = Logger.getLogger();
    private BlockSocket blockSocket = BlockSocket.getBlockSocket();

    public MenuBar(GraphCanvas gc, Graph graph, Font n, int professorVersion, Frame frame) {
        this.gc = gc;
        this.graph = graph;
        this.n = n;
        this.professorVersion = professorVersion;
        this.parent = frame;

        //if(gc.VERSIONID.equals("112"))
        if (professorVersion != Main.VERSION2 && professorVersion != Main.DEMO_VERSION2) {
            initHintButtons();
            initHelpButtons();
//          initNewAvatarButton();
            initButtonFormat();
        } else {
            initVersionTwoButtons();
        }
    }

    private void initVersionTwoButtons() {

        initNewNodeButton();
        initPredictButton();
        initDoneButton();
//        if(professorVersion == Main.PROFESSORVERSION)
//          initNewAvatarButton();

        buttonPanel = new JPanel();

        GridLayout buttonLayout = new GridLayout(7, 1);
        buttonPanel.setLayout(buttonLayout);

        JButton button1 = new JButton("");
        JButton button2 = new JButton("");
        JButton button3 = new JButton("");
        button1.setVisible(false);
        button3.setVisible(false);
        button2.setVisible(false);

        buttonPanel.add(button1);
        buttonPanel.add(button2);
        buttonPanel.add(newNodeButton);
        buttonPanel.add(predictButton);
        buttonPanel.add(doneButton);
        buttonPanel.add(button3);
        //Added FeedBack Button by zpwn
        JButton sendFeedBack_btn = Main.getTicketButton();
        sendFeedBack_btn.setVisible(true);
        sendFeedBack_btn.setBackground(Color.WHITE);
        Font normal = new Font("Arial", Font.PLAIN, 16);
        sendFeedBack_btn.setFont(normal);
        buttonPanel.add(sendFeedBack_btn);
        //**End**//

        //Helen: For the pilot testing we are not showing the Score to the student
        //We need to calculate and show it for the Summer 2011
        //scoreLabel.setFont(n);
        //scoreLabel.setText("Score: 100%");
        //buttonPanel.add(scoreLabel);

//  if(professorVersion == Main.PROFESSORVERSION)
//    buttonPanel.add(newAvatarButton);

        FlowLayout f = new FlowLayout(FlowLayout.RIGHT, 18, startPosition);
        gc.setLayout(f);
        buttonPanel.setOpaque(false);
        gc.add(buttonPanel);
    }

    private void initButtonFormat() {
        buttonPanel = new JPanel();
        //create placeholders
        JButton placeHolder1 = new JButton("");
        JButton placeHolder2 = new JButton("");
        JButton placeHolder3 = new JButton("");
        JButton placeHolder4 = new JButton("");
        JButton placeHolder5 = new JButton("");
        placeHolder1.setVisible(false);
        placeHolder2.setVisible(false);
        placeHolder3.setVisible(false);
        placeHolder4.setVisible(false);
        placeHolder5.setVisible(false);

        GridLayout buttonLayout = new GridLayout(16, 2);
        buttonPanel.setLayout(buttonLayout);
        buttonPanel.add(gc.getTakeQuizButton());
        buttonPanel.add(placeHolder1);

        for (int i = 0; i < 30; i++) {
//            if (professorVersion == Main.PROFESSORVERSION && i == 2) {
//                buttonPanel.add(newAvatarButton);
//            } else
            if (i == 5) {
                buttonPanel.add(gc.getShortDescriptionButton());
            } else if (i == 7) {
                buttonPanel.add(gc.getRunButton());
            } else if (i == 13) {
                buttonPanel.add(glossaryButton);
            } else if (i == 15) {
                buttonPanel.add(nodesDemoButton);
            } else if (i == 17) {
                buttonPanel.add(linksDemoButton);
            } else if (i == 19) {
                buttonPanel.add(equationsDemoButton);
            } else if (i == 21) {
                buttonPanel.add(finishingDemoButton);
            } else if (i == 27) {
                buttonPanel.add(diagramButton);
            } else if (i == 29) {
                buttonPanel.add(equationButton);
            } //else add a placeholder button
            else {
                JButton button = new JButton("");
                button.setVisible(false);
                buttonPanel.add(button);
            }
        }

        startPosition = Toolkit.getDefaultToolkit().getFontMetrics(n).stringWidth("00:00:00:000") - 10;

        FlowLayout f = new FlowLayout(FlowLayout.RIGHT, 18, startPosition);
        gc.setLayout(f);
        buttonPanel.setOpaque(false);
        gc.add(buttonPanel);
        //cannot get height of buttonPanel or glossaryButton at this time
        hintLocation = startPosition;
    }

    /**
     * This method initializes the hint buttons
     */
    private void initHintButtons() {
        initDiagramButton();
        initEquationsButton();
    }

    /**
     * This method initializes the help buttons
     */
    private void initHelpButtons() {
        initGlossaryButton();
        initNodesDemoButton();
        initLinksDemoButton();
        initEquationsDemoButton();
        initFinishingDemoButton();
    }

    /**
     * Added by zaw
     * This method initializes the new node buttons
     */
    private void initNewNodeButton() {
        newNodeButton = new JButton("Create Node");
        newNodeButton.setBackground(Color.WHITE);
        Font normal = new Font("Arial", Font.PLAIN, 16);
        newNodeButton.setFont(normal);

        newNodeButton.addActionListener(new java.awt.event.ActionListener() {

            boolean flag = false;
            LinkedList<String> listOfVertexes = null;
            int[] indices = null;

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (Main.dialogIsShowing) {
                    return;
                }

                if (gc.openTabs.size() > 0) {
                    gc.openTabs.get(0).setVisible(true);
                    MessageDialog.showMessageDialog(null, true, "You are currently creating or editing a node. If you want to create a new node, please close this node editor.", graph);
                    return;
                }
                newNodeButtonActionPerformed(evt, gc);
               // logger.concatOut(Logger.ACTIVITY, "No message", "Create a node try");
              //  String returnMsg = blockSocket.blockQuery(parent, "Create a node");
              //  if (returnMsg.equals("allow")) {
              //      newNodeButtonActionPerformed(evt, gc);  //the action is allowed by meta tutor
             //   } else {
             //       new MetaTutorMsg(returnMsg.split(":")[1], false).setVisible(true); //the action is denied by meta tutor
            //    }
            }

            private void newNodeButtonActionPerformed(ActionEvent evt, GraphCanvas gc) {
                /*
                if (!flag) {
                listOfVertexes = gc.listOfVertexes;
                indices = gc.suffledIndexes(listOfVertexes);
                flag = true;
                }
                String vertexName = listOfVertexes.get(indices[vertexCount]);
                 * 
                 */



                String vertexName = "";

                int height;
                Vertex v = new Vertex();
                if (gc.getParent() != null) {
                    height = gc.getParent().getHeight();
                } else {
                    height = (int) gc.getFrame().getToolkit().getDefaultToolkit().getScreenSize().getHeight() - 200;
                }
                gc.setFont(gc.normal);
                
                //System.out.println("Vertex name: "+vertexName);
                int vertexCount = graph.getVertexes().size();
                if (Math.floor(vertexCount / 6) > 0) {
                        gc.newVertex(100 + vertexCount % 6 * 125, height - (int) (v.paintNoneHeight * 2 * (Math.floor(vertexCount / 6) + 1)), vertexName);
                        //System.out.println(vertexCount);
                    } else {
                        //System.out.println("else "+vertexCount);
                        gc.newVertex(100 + vertexCount * 125, height - v.paintNoneHeight * 2, vertexName);
                    }
                System.out.println(vertexCount);
          /*
                if (vertexCount < gc.listOfVertexes.size()) {
                    if (Math.floor(vertexCount / 6) > 0) {
                        gc.newVertex(100 + vertexCount % 6 * 125, height - (int) (v.paintNoneHeight * 2 * (Math.floor(vertexCount / 6) + 1)), vertexName);
                        //System.out.println(vertexCount);
                    } else {
                        //System.out.println("else "+vertexCount);
                        gc.newVertex(100 + vertexCount * 125, height - v.paintNoneHeight * 2, vertexName);
                    }
                    vertexCount = graph.getVertexes().size();
                    if (vertexCount == gc.listOfVertexes.size()) {
                        newNodeButton.setEnabled(false);
                    }
                }  */  // Commenting to allow the creation of many nodes
                
               // Get the recently created vertex and show it in the Tabbed GUI
                v = ((Vertex) graph.getVertexes().getFirst());
               
                TabbedGUI openWindow = new TabbedGUI(v, graph, gc, true, false);
                openWindow.setVisible(true);
                gc.getOpenTabs().add(openWindow);
                
                if (gc.modelHasBeenRun == true) {
                    gc.modelHasBeenRun = false;
                }
                //openWindow.getDescriptionPanel().in.initButtonOnTask();
                openWindow.getCalculationsPanel().initButtonOnTask();
                openWindow.getInputsPanel().initButtonOnTask();
            }
        });
    }

    public void resetRunBtnClickCount() {
        runBtnClickCount = 0;
    }

    public boolean isMissingNode() {
        boolean isMissingNode = true;
        int actualSize = gc.listOfVertexes.size() - gc.extraNodes.size();
        System.out.println("actualSize " + actualSize);
        System.out.println("graph.getVertexes().size() " + graph.getVertexes().size());
        int count = 0;
        if (graph.getVertexes().size() >= actualSize && gc.extraNodes.size() > 0) {
            for (int v = 0; v < graph.getVertexes().size(); v++) {
                for (String s : gc.extraNodes) {
                    Vertex vertex = (Vertex) graph.getVertexes().get(v);
                    System.out.println(s + " ... . " + vertex.nodeName);
                    if (!s.equals(vertex.nodeName)) {
                        count++;
                        System.out.println("count " + count);
                        if (count == actualSize) {
                            isMissingNode = false;
                        }
                        break;
                    }

                }
            }
        }
        if (gc.extraNodes.size() == 0 && graph.getVertexes().size() == actualSize) {
            isMissingNode = false;
        }

        System.out.println("MISSING: " + isMissingNode);
        return isMissingNode;
    }
//  private boolean isExtraNode(String nodename)
//  {
//    boolean isextranode = false;
//    for(String s:gc.extraNodes)
//    {
//      if(s.equals(nodename))
//         isextranode = true;
//    }
//    return isextranode;
//  }

    /**
     * FOR VERSION 2, this button predicts how the user is doing on the level
     */
    private void initPredictButton() {
        predictButton = new JButton("Run Model");
        predictButton.setBackground(Color.WHITE);

        Font normal = new Font("Arial", Font.PLAIN, 16);
        predictButton.setFont(normal);

        predictButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                predictButtonActionPerformed(evt, gc);
            }

            private void predictButtonActionPerformed(ActionEvent evt, GraphCanvas gc) {
                if (Main.dialogIsShowing) {
                    Window[] dialogs = Dialog.getWindows();
                    for (int i = 0; i < dialogs.length; i++) {
                        if (dialogs[i].getName().equals("tutorMsg") || dialogs[i].getName().equals("tutorQues")) {
                            dialogs[i].setVisible(true);
                            dialogs[i].setAlwaysOnTop(true);
                            JOptionPane.showMessageDialog(dialogs[i], "Please finish the dialog before running the model.");
                            break;
                        }
                    }
                    return;
                }
                if (GraphCanvas.openTabs.size() > 0) {
                    GraphCanvas.openTabs.get(0).setVisible(true);
                    GraphCanvas.openTabs.get(0).setAlwaysOnTop(true);
                    JOptionPane.showMessageDialog(GraphCanvas.openTabs.get(0), "Please close this node editor before running the model.");
                    return;
                }

                boolean aWrongDescription = false;
                boolean aMissingNode = isMissingNode();
                boolean aDuplicateNode = false;
                boolean errorInModel = false;
                //boolean inputsError = false;
                //boolean calculationsError = false;
                boolean syntacticErrors = false;
                boolean allRight = true;

                int[] inputsError = new int[graph.getVertexes().size()];
                int[] calculationsError = new int[graph.getVertexes().size()];
                Vertex current;
                runBtnClickCount++;

                // Initialize every element in the inputsError and calculationsError arrays to -1.
                // These arrays will let us know which vertices have input or calculation errors, and -1
                // indicates that the inputs or calculations have already been checked (whether correct or incorrect)
                for (int i = 0; i < graph.getVertexes().size(); i++) {
                    inputsError[i] = -1;
                    calculationsError[i] = -1;
                }

                logs.out(Logger.ACTIVITY, "GraphCanvas.initRunButton.1");
                // if(gc.listOfVertexes.size() == graph.getVertexes().size())
                //doneButton.setEnabled(true);
                //TO DO: IMPLEMENT PREDICT BUTTON
                if (predictButton.isEnabled()) {
                    try {
                        String previousDescription = "previous";
                        for (int i = 0; i < graph.getVertexes().size(); i++) {
                            current = (Vertex) graph.getVertexes().get(i);
                            String currentDescription = current.selectedDescription;
                            // Check to see if there is node whose description does not match the description in the solution file
                            if (currentDescription.equals(previousDescription)) {
                                aDuplicateNode = true;
                                previousDescription = currentDescription;
                            } else if (!currentDescription.equals(previousDescription)) {
                                previousDescription = currentDescription;
                            }

                            if (current.getDescriptionButtonStatus() == current.NOSTATUS || !current.selectedDescription.equals(current.correctDescription)) {
                                aWrongDescription = true;
                            } // Else, check to see if there are any syntax errors
                            else if (!aWrongDescription) {
                                if (gc.checkNodeForCorrectInputSyntactics(i) != true && gc.checkNodeForCorrectCalculationSyntactics(i) != true) {
                                    syntacticErrors = true;
                                } // Finally, check to see if either the input or calculation tabs have the correct equation...only if the those tabs are set to NOSTATUS
                                else {
                                    if (current.getInputsButtonStatus() == current.NOSTATUS) {
                                        if (gc.checkNodeForCorrectInputs(i) != true) {
                                            inputsError[i] = 1;
                                            logger.concatOut(Logger.ACTIVITY, "No message", "Inputs tab of the node--" + current.nodeName + " is: wrong");
                                        } else {
                                            inputsError[i] = 0;
                                            logger.concatOut(Logger.ACTIVITY, "No message", "Inputs tab of the node--" + current.nodeName + " is: correct");
                                        }
                                        if ((current.correctType.equals("constant") && current.type.equals("constant"))
                                                || (!current.correctType.equals("constant") && (!current.correctType.equals("constant")))) {
                                            logger.concatOut(Logger.ACTIVITY, "No message", "The type of the node--" + current.nodeName + " is: correct");
                                        } else {
                                            logger.concatOut(Logger.ACTIVITY, "No message", "The type of the node--" + current.nodeName + " is: wrong");
                                        }
                                    }

                                    if (current.getCalculationsButtonStatus() == current.NOSTATUS) {
                                        if (gc.checkNodeForCorrectCalculations(i) != true) {
                                            calculationsError[i] = 1;
                                            logger.concatOut(Logger.ACTIVITY, "No message", "Calculation tab of the node-" + current.nodeName + " is: wrong");
                                        } else {
                                            calculationsError[i] = 0;
                                            logger.concatOut(Logger.ACTIVITY, "No message", "Calculation tab of the node-" + current.nodeName + " is: correct");
                                        }
                                    }

                                    if (inputsError[i] == 1 || calculationsError[i] == 1) {
                                        errorInModel = true;
                                    }
                                }
                            }
                        }

                        // There is a duplicate node somewhere in the graph
                        if (aDuplicateNode) {
                            MessageDialog.showMessageDialog(null, true, "There is a repeated node description somewhere in your graph.", graph);
                        } // There is a wrong descriptions somewhere in the graph
                        else if (aWrongDescription) {
                            MessageDialog.showMessageDialog(null, true, "Before leaving this tab, please use the Check button to make sure your description is correct (green).", graph);
                            System.out.println("runBtnClickCount:" + runBtnClickCount);
//                if(runBtnClickCount>1)
//                {
//                  getWrongDescriptionNodes();//highlight the wrong description nodez
//                  MessageDialog.showMessageDialog(null, true, "I'm turning red the names of the nodes that need to have their descriptions change.", graph);
//                }
//                else
//                {
//                  getWrongDescriptionNodes();//highlight the wrong description nodez
//                  MessageDialog.showMessageDialog(null, true, "The descriptions of at least one node are incorrect.", graph);
//                }

                        } // The are too many or tew few nodes in the graph
                        else if (aMissingNode && (TaskFactory.getInstance().getActualTask().getType().equalsIgnoreCase("Construct")
                                || TaskFactory.getInstance().getActualTask().getType().equalsIgnoreCase("Model"))) {

                            MessageDialog.showMessageDialog(null, true, "Because this is an early problem, you get a free hint: at least one node is missing from your model.", graph);
                        } // There are some syntactic erors in either the Inputs or Calculations panel
                        else if (syntacticErrors) {
                            //MessageDialog.showMessageDialog(null, true, "Your model is incomplete, review your inputs and calculations.", graph);
                            MessageDialog.showMessageDialog(null, true, "In order to run the model, every node must have inputs and calculations defined, which is indicated by a blue border.", graph);
                        } // There is an error somewhere in the model with some vertex whose calculations or inputs haven't be checked yet
                        else if (errorInModel && (TaskFactory.getInstance().getActualTask().getType().equalsIgnoreCase("Construct")
                                || TaskFactory.getInstance().getActualTask().getType().equalsIgnoreCase("Model"))) {
                            
                            MessageDialog.showMessageDialog(null, true, "Because this is an early problem, you get a free hint: You've got all the nodes you need, but some have incorrect inputs and/or calculations, so I'm marking those.", graph);
                            graph.run(TaskFactory.getInstance(), gc);
                            for (int i = 0; i < graph.getVertexes().size(); i++) {
                                current = (Vertex) graph.getVertexes().get(i);

                                if (inputsError[i] == 1) {
                                    current.setInputsButtonStatus(current.WRONG);
                                } else if (inputsError[i] == 0) {
                                    current.setInputsButtonStatus(current.CORRECT);
                                }

                                if (calculationsError[i] == 1) {
                                    current.setCalculationsButtonStatus(current.WRONG);
                                } else if (calculationsError[i] == 0) {
                                    current.setCalculationsButtonStatus(current.CORRECT);
                                }

                            }
                        } // Every thing seems correct, begin checking whether every calculation and input matches
                        // what's in the solution file
                        else {
                            graph.run(TaskFactory.getInstance(), gc);
                            MessageDialog.showMessageDialog(null, true, "Model run complete!", graph);
                            gc.setModelHasBeenRun(true);
                            Main.alreadyRan = true;
//                            for (int i = 0; i < graph.getVertexes().size(); i++) {
//                                current = (Vertex) graph.getVertexes().get(i);
//                                if (!current.equation.value.isEmpty() && !current.correctValues.isEmpty()) {
//                                    for (int j = 0; j < current.equation.value.size(); j++) {
//                                        if (Double.compare(current.equation.value.get(j), current.correctValues.get(j)) == 0) {
//                                            allRight = true;
//                                        } else {
//                                            allRight = false;
//                                        }
//                                    }
//                                } else {
//                                    allRight = false;
//                                }
//                                //Set whether the graphs panel is correct
//                                if (allRight) {
//                                    current.setGraphsButtonStatus(current.CORRECT);
//                                    logger.out(Logger.ACTIVITY, "MenuBar.predictButtonActionPerformed.1", current.label + ":green");
//                                    logger.concatOut(Logger.ACTIVITY, "No message", "The color of the graph for the node--" + current.label + " is: green");
//                                } else {
//                                    current.setGraphsButtonStatus(current.WRONG);
//                                    logger.out(Logger.ACTIVITY, "MenuBar.predictButtonActionPerformed.1", current.label + ":red");
//                                    logger.concatOut(Logger.ACTIVITY, "No message", "The color of the graph for the node--" + current.label + " is: red");
//                                }
//                            }
                            for (int i = 0; i < graph.getVertexes().size(); i++) {
                                    current = (Vertex) graph.getVertexes().get(i);
                                    
                                    if (!current.equation.value.isEmpty()){
                                        current.setGraphsButtonStatus(current.CORRECT);
                                    }
                            }
                        }
                        logger.concatOut(Logger.ACTIVITY, "No message", "All the node's information has been sent.");
                    } catch (CommException ex) {
                        //ADD LOGGER
                    }
                } else {
                    MessageDialog.showMessageDialog(null, true, "All nodes should be connected and have an equation before the model can be run", graph);
                    logs.out(Logger.ACTIVITY, "GraphCanvas.initRunButton.2");
                }


            }

            public void getWrongDescriptionNodes() {
                for (int v = 0; v < graph.getVertexes().size(); v++) {
                    if (!((Vertex) graph.getVertexes().get(v)).selectedDescription.equals(((Vertex) graph.getVertexes().get(v)).correctDescription)) {
                        ((Vertex) graph.getVertexes().get(v)).setDescriptionButtonStatus(((Vertex) graph.getVertexes().get(v)).WRONG);
                    }

                }
            }
        });
    }

    /**
     * This method returns the predict button from version 2
     * @return the predict button
     */
    public JButton getNewNodeButton() {
        return newNodeButton;
    }

    public JButton getPredictButton() {
        return predictButton;
    }

    public JButton getDoneButton() {
        return doneButton;
    }

    /**
     * FOR VERSION 2, this button lets the user notify the system that he is done with the level
     */
    private void initDoneButton() {
        doneButton = new JButton("Done");
        doneButton.setBackground(Color.WHITE);

        Font normal = new Font("Arial", Font.PLAIN, 16);
        doneButton.setFont(normal);
        doneButton.setEnabled(false);

        doneButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doneButtonActionPerformed(evt);
            }

            private void doneButtonActionPerformed(ActionEvent evt) {
                if (Main.dialogIsShowing) {
                    Window[] dialogs = Dialog.getWindows();
                    for (int i = 0; i < dialogs.length; i++) {
                        if (dialogs[i].getName().equals("tutorMsg") || dialogs[i].getName().equals("tutorQues")) {
                            dialogs[i].setVisible(true);
                            dialogs[i].setAlwaysOnTop(true);
                            JOptionPane.showMessageDialog(dialogs[i], "Please finish the dialog before moving to the next task.");
                            break;
                        }
                    }
                    return;
                }
                if (GraphCanvas.openTabs.size() > 0) {
                    GraphCanvas.openTabs.get(0).setVisible(true);
                    GraphCanvas.openTabs.get(0).setAlwaysOnTop(true);
                    JOptionPane.showMessageDialog(GraphCanvas.openTabs.get(0), "Please close this node editor before moving to the next task.");
                    return;
                }

// Disabling User Interest popup
//       PromptDialog promptDialog = new PromptDialog(gc.getFrame(),true);
//       promptDialog.popup();
//       promptDialog.setGC(gc);
//       promptDialog.setLocationRelativeTo(gc);
//        doneAction();
            }
        });
    }

    public void doneAction() {
        // Modify javiergs
        int num1 = -1, num2 = -1;
        TaskFactory server;
        LinkedList<String> extraNodes = new LinkedList<String>();
        try {
            server = TaskFactory.getInstance();
            extraNodes = server.getActualTask().getExtraNodes();
            //Helen: For the pilot testing we are not showing the Score to the student
            //We need to calculate and show it for the Summer 2011
            //ScoresDialog sd = new ScoresDialog(parent, true, gc);
            //sd.setVisible(true);
        } catch (CommException ex) {
            //NOTE: Add correct logger later
            //java.util.logging.Logger.getLogger(MenuBar.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (professorVersion != Main.VERSION2 && professorVersion != Main.DEMO_VERSION2) {
            try {
                num1 = Integer.parseInt(TaskFactory.getInstance().getActualTask().getLevel());
                num2 = Integer.parseInt(TaskFactory.getInstance().getActualTask().getLevel());
            } catch (CommException de) {
            }
            // end javier

            doneButton.setEnabled(false);
            newNodeButton.setEnabled(true);

            System.out.println("Done: ExtraNode: " + extraNodes.size());
            System.out.println("Done: gc.listOfVertexes.size():" + gc.listOfVertexes.size());
            System.out.println("Done: graph.getVertexes().size():" + graph.getVertexes().size());

//          if((gc.listOfVertexes.size() == graph.getVertexes().size() || gc.listOfVertexes.size()-extraNodes.size() == graph.getVertexes().size()) && !graph.errorRun) {
            if (gc.getAllCorrect() && gc.getPractice() == false) {
                gc.setPassed(true);
                //GIVE THE USER A POINT
                //gc.setUserPoints(1);
                gc.setStudentReceivedLevelPoint(true);
//              logger.concatOut(Logger.ACTIVITY, "MenuBar.doneButtonActionPerformed.1",Integer.toString(num1));
            } else if (gc.getAllCorrect() && gc.getPractice() == true && num1 > 0) {
                gc.setContinues(true);
//              logger.concatOut(Logger.ACTIVITY, "MenuBar.doneButtonActionPerformed.1",Integer.toString(num1));
            } else if (gc.getAllCorrect() && gc.getPractice() == true && num2 <= 0) {
                gc.setPassed(true);

            }
//          } else {
//            doneButton.setEnabled(false);
//          }
        } else {
            try {
                num1 = Integer.parseInt(TaskFactory.getInstance().getActualTask().getLevel());
                System.out.println("TEST num1:" + num1);
            } catch (CommException de) {
            }
            // end javier

            doneButton.setEnabled(false);
            newNodeButton.setEnabled(true);

            System.out.println("gc.listOfVertexes.size():" + gc.listOfVertexes.size());
            System.out.println("graph.getVertexes().size():" + graph.getVertexes().size());

//          if((gc.listOfVertexes.size() == graph.getVertexes().size() || gc.listOfVertexes.size()-extraNodes.size() == graph.getVertexes().size()) && !graph.errorRun) {
            // Check whether all problems have been completed in the current level
            if (gc.getAllCorrect() && gc.getProblemsCompleted().size() == gc.getProblemList().get(gc.getCurrentLevel()).length) {
                gc.setPassed(true);
                //GIVE THE USER A POINT
                //gc.setUserPoints(1);
                gc.setStudentReceivedLevelPoint(true);
//              logger.concatOut(Logger.ACTIVITY, "MenuBar.doneButtonActionPerformed.1",Integer.toString(num1));
            } else if (gc.getAllCorrect() && num1 > 0) {
                gc.setContinues(true);
//              logger.concatOut(Logger.ACTIVITY, "MenuBar.doneButtonActionPerformed.1",Integer.toString(num1));
            }
//          } else {
//           doneButton.setEnabled(false);
//          }
        }
    }

    /**
     * This method initializes the Finishing Demo button
     */
    private void initNewAvatarButton() {
        newAvatarButton = new JButton("New Avatar");
        newAvatarButton.setBackground(Color.WHITE);

        Font normal = new Font("Arial", Font.PLAIN, 16);
        newAvatarButton.setFont(normal);

        newAvatarButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newAvatarButtonActionPerformed(evt);
            }

            private void newAvatarButtonActionPerformed(ActionEvent evt) {
                logs.out(Logger.ACTIVITY, "GraphCanvas.initNewAvatarButton.1");
                Avatar avatar = new Avatar(100, 100, gc, n, false, true);
                gc.getAvatarList().add(avatar);
            }
        });
    }

    /**
     * This method initializes the Finishing Demo button
     */
    private void initGlossaryButton() {
        glossaryButton = new JButton("Glossary");
        glossaryButton.setBackground(Color.WHITE);

        glossaryButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                glossaryButtonActionPerformed(evt);
            }

            private void glossaryButtonActionPerformed(ActionEvent evt) {
                logs.out(Logger.ACTIVITY, "MenuBar.initGlossaryButton.1");
                if (Desktop.isDesktopSupported()) {
                    desktop = Desktop.getDesktop();
                }
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    URI uri = null;
                    //Process p = new ProcessBuilder()
                    File f = new File("localhtml/i-glossary.html");
                    String path = f.getAbsolutePath().toString();
                    String url = path.replace("\\", "/");
                    try {
                        uri = new URI("file:///" + url);
                        desktop.browse(uri);
                    } catch (Exception ioe) {
                        ioe.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * This method initializes the Nodes Demo button
     */
    private void initNodesDemoButton() {
        nodesDemoButton = new JButton("Nodes Demo");
        nodesDemoButton.setBackground(Color.WHITE);

        nodesDemoButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                /*
                try {
                nodesDemoButtonActionPerformed(evt);
                } catch (QTException ex) {
                java.util.logging.Logger.getLogger(MenuBar.class.getName()).log(Level.SEVERE, null, ex);
                }
                 */
            }
            /*
            private void nodesDemoButtonActionPerformed(ActionEvent evt) throws QTException {
            logs.out(Logger.ACTIVITY, "MenuBar.initNodesDemoButton.1");
            
            Movie m;
            try {
            QTSessionCheck.check();
            } catch (QTException ex) {
            java.util.logging.Logger.getLogger(MenuBar.class.getName()).log(Level.SEVERE, null, ex);
            }
            QTFile file = null;
            URL nodesDemoURL = Main.class.getResource("videos/NodesDemoMovie.f4v");
            file = new QTFile(nodesDemoURL.getPath());
            
            OpenMovieFile omFile = null;
            try {
            omFile = OpenMovieFile.asRead(file);
            } catch (QTException ex) {
            java.util.logging.Logger.getLogger(MenuBar.class.getName()).log(Level.SEVERE, null, ex);
            }
            m = Movie.fromFile(omFile);
            DemoPlayer nd = new DemoPlayer(m, null, true);
            nd.setVisible(true);
            }*/
        });
    }

    /**
     * This method initializes the Finishing Demo button
     */
    private void initLinksDemoButton() {
        linksDemoButton = new JButton("Links Demo");
        linksDemoButton.setBackground(Color.WHITE);

        linksDemoButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                /*
                try {
                linksDemoButtonActionPerformed(evt);
                } catch (QTException ex) {
                java.util.logging.Logger.getLogger(MenuBar.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                 */
            }
            /*
            private void linksDemoButtonActionPerformed(ActionEvent evt) throws QTException {
            logs.out(Logger.ACTIVITY, "MenuBar.initLinksDemoButton.1");
            
            Movie m;
            try {
            QTSessionCheck.check();
            } catch (QTException ex) {
            java.util.logging.Logger.getLogger(MenuBar.class.getName()).log(Level.SEVERE, null, ex);
            }
            QTFile file = null;
            URL nodesDemoURL = Main.class.getResource("videos/LinksDemoMovie.f4v");
            file = new QTFile(nodesDemoURL.getPath());
            
            OpenMovieFile omFile = null;
            try {
            omFile = OpenMovieFile.asRead(file);
            } catch (QTException ex) {
            java.util.logging.Logger.getLogger(MenuBar.class.getName()).log(Level.SEVERE, null, ex);
            }
            m = Movie.fromFile(omFile);
            DemoPlayer nd = new DemoPlayer(m, null, true);
            nd.setVisible(true);
            }*/
        });
    }

    /**
     * This method initializes the Finishing Demo button
     */
    private void initEquationsDemoButton() {
        equationsDemoButton = new JButton("Equations Demo");
        equationsDemoButton.setBackground(Color.WHITE);

        equationsDemoButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                /*
                try {
                equationsDemoButtonActionPerformed(evt);
                } catch (QTException ex) {
                java.util.logging.Logger.getLogger(MenuBar.class.getName()).log(Level.SEVERE, null, ex);
                }
                 */
            }

            /*
            private void equationsDemoButtonActionPerformed(ActionEvent evt) throws QTException {
            logs.out(Logger.ACTIVITY, "MenuBar.initEquationsDemoButton.1");
            
            Movie m;
            try {
            QTSessionCheck.check();
            } catch (QTException ex) {
            java.util.logging.Logger.getLogger(MenuBar.class.getName()).log(Level.SEVERE, null, ex);
            }
            QTFile file = null;
            URL nodesDemoURL = Main.class.getResource("videos/EquationDemoMovie.f4v");
            file = new QTFile(nodesDemoURL.getPath());
            
            OpenMovieFile omFile = null;
            try {
            omFile = OpenMovieFile.asRead(file);
            } catch (QTException ex) {
            java.util.logging.Logger.getLogger(MenuBar.class.getName()).log(Level.SEVERE, null, ex);
            }
            m = Movie.fromFile(omFile);
            DemoPlayer nd = new DemoPlayer(m, null, true);
            nd.setVisible(true);
            }*/
        });
    }

    /**
     * This method initializes the Finishing Demo button
     */
    private void initFinishingDemoButton() {
        finishingDemoButton = new JButton("Finishing Demo");
        finishingDemoButton.setBackground(Color.WHITE);

        finishingDemoButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                /*
                try {
                finishingDemoButtonActionPerformed(evt);
                } catch (QTException ex) {
                java.util.logging.Logger.getLogger(MenuBar.class.getName()).log(Level.SEVERE, null, ex);
                }
                 */
            }

            /*
            private void finishingDemoButtonActionPerformed(ActionEvent evt) throws QTException {
            logs.out(Logger.ACTIVITY, "MenuBar.initFinishingDemoButton.1");
            Movie m;
            try {
            QTSessionCheck.check();
            } catch (QTException ex) {
            java.util.logging.Logger.getLogger(MenuBar.class.getName()).log(Level.SEVERE, null, ex);
            }
            QTFile file = null;
            URL nodesDemoURL = Main.class.getResource("videos/FinishingDemoMovie.f4v");
            file = new QTFile(nodesDemoURL.getPath());
            
            OpenMovieFile omFile = null;
            try {
            omFile = OpenMovieFile.asRead(file);
            } catch (QTException ex) {
            java.util.logging.Logger.getLogger(MenuBar.class.getName()).log(Level.SEVERE, null, ex);
            }
            m = Movie.fromFile(omFile);
            DemoPlayer nd = new DemoPlayer(m, null, true);
            nd.setVisible(true);
            }*/
        });
    }

    /**
     * This method initializes the diagram hint button
     */
    private void initDiagramButton() {
        diagramButton = new JButton("Diagram");
        diagramButton.setBackground(Color.WHITE);

        diagramButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                diagramButtonActionPerformed(evt);
            }

            private void diagramButtonActionPerformed(ActionEvent evt) {

                logs.out(Logger.ACTIVITY, "MenuBar.initDiagramButton.1");
                if (usedHint == false && gc.getPractice() == false) {
                    String message = "<html>Remember, your student will only get a point for a level when you teach her without asking for hints (including the Instruction Tab).  However, once you read a hint, you can read it or any other hint as often as you’d like.  Thus, you should learn as much as you can from the hints as you teach your student about this situation.  When she’s passed the quiz, you can teach her about another situation on the same level.  You will probably be able to teach it quickly, without asking for hints.  Do you want to read hints or change to the Instruction Tab for this situation?</html>";
                    MessageDialog.showYesNoDialog(null, true, message, graph);

                    //if yes
                    if (graph.getN() == 0) {
                        //show the pop up
                        try {
                            HintDialog hd = new HintDialog(null, true, TaskFactory.getInstance().getActualTask(), 1, graph);
                            hd.setVisible(true);
                        } catch (CommException de) {
                            //do something
                        }


                        logs.out(Logger.ACTIVITY, "MenuBar.initDiagramButton.2");
                        usedHint = true;
                        graph.setN(-1);
                        gc.setCurrentLevelPoints(0);
                    } else {
                        //do nothing
                        logs.out(Logger.ACTIVITY, "MenuBar.initDiagramButton.3");
                        graph.setN(-1);
                    }
                } else {
                    try {
                        HintDialog hd = new HintDialog(null, true, TaskFactory.getInstance().getActualTask(), 1, graph);
                        hd.setVisible(true);
                    } catch (CommException de) {
                        // do something
                    }
                }
            }
        });
    }

    /**
     * This method initializes the equations hint button
     */
    private void initEquationsButton() {
        equationButton = new JButton("Equations");
        equationButton.setBackground(Color.WHITE);

        equationButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                equationButtonActionPerformed(evt);
            }

            private void equationButtonActionPerformed(ActionEvent evt) {

                logs.out(Logger.ACTIVITY, "MenuBar.initEquationsButton.1");
                if (usedHint == false && gc.getPractice() == false) {
                    String message = "<html>Remember, your student will only get a point for a level when you teach her without asking for hints (including the Instruction Tab).  However, once you read a hint, you can read it or any other hint as often as you’d like.  Thus, you should learn as much as you can from the hints as you teach your student about this situation.  When she’s passed the quiz, you can teach her about another situation on the same level.  You will probably be able to teach it quickly, without asking for hints.  Do you want to read hints or change to the Instruction Tab for this situation ?</html>";
                    MessageDialog.showYesNoDialog(null, true, message, graph);

                    //if yes
                    if (graph.getN() == 0) {
                        //show the pop up
                        try {
                            HintDialog hd = new HintDialog(null, true, TaskFactory.getInstance().getActualTask(), 3, graph);
                            hd.setVisible(true);
                        } catch (CommException de) {
                            // do something
                        }
                        logs.out(Logger.ACTIVITY, "MenuBar.initEquationsButton.2");
                        usedHint = true;
                        graph.setN(-1);
                        gc.setCurrentLevelPoints(0);
                    } else {
                        //do nothing
                        logs.out(Logger.ACTIVITY, "MenuBar.initEquationsButton.3");
                        graph.setN(-1);
                    }
                } else {
                    try {
                        HintDialog hd = new HintDialog(null, true, TaskFactory.getInstance().getActualTask(), 3, graph);
                        hd.setVisible(true);
                    } catch (CommException de) {
                        // do something
                    }
                }
            }
        });
    }

    /**
     * This method draws the box around the hints
     */
    public void drawHintBox(Graphics g, Font f) {
        int componentWidth = gc.getParent().getWidth();
        int clockBorder = 5;
        if (hintLocation == startPosition && glossaryButton.getHeight() > 0) {
            hintLocation = hintLocation + glossaryButton.getHeight() * 14;
        }
        g.setColor(Color.BLACK);
        g.setFont(f);
        g.drawRect(componentWidth - clockBorder * 4 - diagramButton.getWidth(), hintLocation - diagramButton.getHeight() / 2, diagramButton.getWidth() + clockBorder, diagramButton.getHeight() * 5 / 2 + clockBorder);
        g.setColor(Color.WHITE);
        g.drawLine(componentWidth - clockBorder * 4 - diagramButton.getWidth() / 2 - (Toolkit.getDefaultToolkit().getFontMetrics(f)).stringWidth("HINTS") / 2, hintLocation - diagramButton.getHeight() / 2, componentWidth - clockBorder * 3 - diagramButton.getWidth() / 2 + (Toolkit.getDefaultToolkit().getFontMetrics(f)).stringWidth("HINTS") / 2, hintLocation - diagramButton.getHeight() / 2);
        g.setColor(Color.BLACK);
        g.drawString("HINTS", componentWidth - clockBorder * 3 - diagramButton.getWidth() / 2 - Toolkit.getDefaultToolkit().getFontMetrics(f).stringWidth("HINTS") / 2, hintLocation - diagramButton.getHeight() / 2 + Toolkit.getDefaultToolkit().getFontMetrics(f).getAscent() / 2);
        g.setFont(n);
    }

    /**
     * This method returns a button, it is used to calculate where to paint other features of the page
     * @return the width of a button
     */
    public JButton getButton() {
        return equationButton;
    }

    /**
     * This method draws the box around the help
     */
    public void drawHelpBox(Graphics g, Font f) {
        int componentWidth = gc.getParent().getWidth();
        int clockBorder = 5;
        int height = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        g.setColor(Color.BLACK);
        g.setFont(f);
        g.drawRect(componentWidth - clockBorder * 4 - diagramButton.getWidth(), hintLocation - diagramButton.getHeight() * 15 / 2, diagramButton.getWidth() + clockBorder, diagramButton.getHeight() * 11 / 2 + clockBorder);
        g.setColor(Color.WHITE);
        g.drawLine(componentWidth - clockBorder * 4 - diagramButton.getWidth() / 2 - (Toolkit.getDefaultToolkit().getFontMetrics(f)).stringWidth("FREE HELP") / 2, hintLocation - diagramButton.getHeight() * 15 / 2, componentWidth - clockBorder * 3 - diagramButton.getWidth() / 2 + (Toolkit.getDefaultToolkit().getFontMetrics(f)).stringWidth("FREE HELP") / 2, hintLocation - diagramButton.getHeight() * 15 / 2);
        g.setColor(Color.BLACK);
        g.drawString("FREE HELP", componentWidth - clockBorder * 3 - diagramButton.getWidth() / 2 - Toolkit.getDefaultToolkit().getFontMetrics(f).stringWidth("FREE HELP") / 2, hintLocation - diagramButton.getHeight() * 15 / 2 + Toolkit.getDefaultToolkit().getFontMetrics(f).getAscent() / 2);
        g.setFont(n);
    }

    /**
     * This method sets whether the user used a hint
     * @param used
     */
    public void setUsedHint(boolean used) {
        usedHint = used;
    }

    /**
     * This method returns whether the user used a hint
     */
    public boolean getUsedHint() {
        return usedHint;
    }
}