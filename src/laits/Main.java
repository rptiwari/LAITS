package laits;

import laits.comm.CommException;
import laits.cover.Cover;
import laits.graph.Edge;
import laits.log.Logger;
import laits.graph.Graph;
import laits.graph.GraphCanvasScroll;
import laits.graph.Vertex;
import laits.gui.AboutDialog;
import laits.gui.ExitDialog;
import laits.gui.HelpDialog;
import laits.gui.MessageDialog;
import laits.gui.QuizDialog;
import laits.gui.SendTicketDialog;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import laits.data.*;
import laits.graph.GraphCanvas;
import laits.version2.TabbedGUI;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Window;
import java.awt.event.ActionListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import laits.gui.ErrorDialog;
import laits.gui.TaskCreator;
import laits.version2.GraphRangeEditor;
import org.dom4j.DocumentException;

public class Main extends JFrame implements WindowListener, ActionListener {

    public static final String VERSION = "Version 1.12 release July 21th, 2010";
    public static String VERSIONID = "112";
    private TaskFactory taskFactory;
    private Logger logger = Logger.getLogger();
    private BlockSocket blockSocket = BlockSocket.getBlockSocket();
    private Graph graph = null;
    private SocketServer socketServer;
    private Cover cover = null;
    private GraphCanvasScroll graphCanvasScroll;
    public TaskView taskView, instructionView;
    public static int PROFESSORVERSION = 0;
    public static int STUDENTVERSION = 1;
    public static int VERSION2 = 2;
    public static int DEMO = 3;
    public static int DEMO_VERSION2 = 4;
    public static int professorVersion = STUDENTVERSION;
    public static boolean MetaTutorIsOn = true;
    public static boolean ReadModelFromFile = true;
    public static boolean alreadyRan = false;
    private static JTextArea memo = null;
    public static boolean dialogIsShowing = false;
    public static boolean windowIsClosing = false;
    JButton openButton;
    JFileChooser fc;

    public int getTaskID() {
        return taskFactory.getActualTask().getId();
    }

    /**
     * 
     * @return
     */
    public Graph getGraph() {
        return graph;
    }

    public Main() {

        try {
            taskFactory = TaskFactory.getInstance();
            taskFactory.getTasks();
        } catch (CommException dbe1) {
            logger.concatOut(Logger.DEBUG, "Main.Main.1", dbe1.getMessage());
        } catch (DataException dbe2) {
            logger.concatOut(Logger.DEBUG, "Main.Main.2", dbe2.getMessage());
        }
        graph = new Graph(professorVersion);

        initComponents();
        ticketButton.setVisible(false);
        menuItemOpenTask.setEnabled(true);
        menuItemSaveTask.setEnabled(true);


        menuItemExit.setEnabled(true);
        menuItemUpdate.setEnabled(false);
        menuItemFeedback.setEnabled(false);


        taskView = new TaskView();
        instructionView = new TaskView();
        graphCanvasScroll = new GraphCanvasScroll(this);


        initFonts();
        this.setFont(graphCanvasScroll.getGraphCanvas().normal);
        graphCanvasScroll.setButtonLabel(this.statusBarLabel);
        graphCanvasScrollPane.add(graphCanvasScroll);

        problemPanel.add(new TaskCreator());

        addWindowListener(this);
        logger.out(Logger.ACTIVITY, "Main.Main.1");
        menuModel.setVisible(false);
        // new Panel
        instructionPanel.setLayout(new java.awt.GridLayout(1, 1));
        instructionPanel.add(instructionView);
        setTabListener();
        this.socketServer = new SocketServer(graphCanvasScroll.getGraphCanvas());
        this.setTitle("LAITS Authoring Tool");

    }

    //get memo
    public static JTextArea getMemo() {
        return memo;
    }

    /**
     * This method adds logger to each of the tabs.
     */
    private void setTabListener() {
        ChangeListener changeListener = new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                int n = tabPane.getSelectedIndex();
                String tab = "";
                if (n == 0) {
                    if (GraphCanvas.getOpenTabs().size() > 0) {
                        TabbedGUI opentab;
                        opentab = GraphCanvas.getOpenTabs().get(0);
                        opentab.setVisible(true);
                    }
                    if (Main.dialogIsShowing && GraphCanvas.getOpenTabs().size() > 0) { //put the window to the right side of the screen
                        TabbedGUI opentab;
                        opentab = GraphCanvas.getOpenTabs().get(0);
                        opentab.setVisible(true);
                        opentab.setBounds(opentab.getToolkit().getScreenSize().width - 662, 100, opentab.getPreferredSize().width, opentab.getPreferredSize().height);
                        Window[] dialogs = Dialog.getWindows();
                        for (int i = 0; i < dialogs.length; i++) {
                            if (dialogs[i].getName().equals("tutorMsg") || dialogs[i].getName().equals("tutorQues")) {
                                dialogs[i].setVisible(true);
                                dialogs[i].setAlwaysOnTop(true);
                            }
                        }
                    }

                    tab = "Student clicked on the Instructions tab.";
                    if (graphCanvasScroll.getGraphCanvas().getCover().getMenuBar().getUsedHint() == false && graphCanvasScroll.getGraphCanvas().getPractice() == false) {
                        tabPane.setSelectedIndex(2);
                        String message = "<html>Remember, your student will only get a point for a level when you teach her without asking for hints (including the Instruction Tab).  However, once you read a hint, you can read it or any other hint as often as you’d like.  Thus, you should learn as much as you can from the hints as you teach your student about this situation.  When she’s passed the quiz, you can teach her about another situation on the same level.  You will probably be able to teach it quickly, without asking for hints.  Do you want to read hints or change to the Instruction Tab for this situation?</html>";
                        MessageDialog.showYesNoDialog(null, true, message, graph);
                        //if yes
                        if (graph.getN() == 0) {
                            //show the instructions tab
                            logger.out(Logger.ACTIVITY, "Main.setTabListener.1");
                            graphCanvasScroll.getGraphCanvas().getCover().hideSomeComponents(true);
                            graphCanvasScroll.getGraphCanvas().getCover().getMenuBar().setUsedHint(true);
                            graph.setN(-1);
                            graphCanvasScroll.getGraphCanvas().setCurrentLevelPoints(0);
                            graphCanvasScroll.getGraphCanvas().getCover().getMenuBar().setUsedHint(true);
                            tabPane.setSelectedIndex(0);
                        } else {
                            //do nothing
                            logger.out(Logger.ACTIVITY, "Main.setTabListener.2");
                            graph.setN(-1);
                        }
                    } else {
                        logger.out(Logger.ACTIVITY, "Main.setTabListener.3");
                        graphCanvasScroll.getGraphCanvas().getCover().hideSomeComponents(true);
                    }
                } else if (n == 1) {
                    if (Main.dialogIsShowing && GraphCanvas.getOpenTabs().size() > 0) { //put the window to the right side of the screen
                        TabbedGUI opentab;
                        opentab = GraphCanvas.getOpenTabs().get(0);
                        opentab.setVisible(false);

                    }
                    if (!Main.dialogIsShowing && GraphCanvas.getOpenTabs().size() > 0) {
                        TabbedGUI opentab;
                        opentab = GraphCanvas.getOpenTabs().get(0);
                        opentab.setVisible(true);
                        opentab.setAlwaysOnTop(true);
                    }
                    logger.out(Logger.ACTIVITY, "Main.setTabListener.4");
                    graphCanvasScroll.getGraphCanvas().getCover().hideSomeComponents(true);
                } else {
                    if (GraphCanvas.getOpenTabs().size() > 0) {
                        TabbedGUI opentab;
                        opentab = GraphCanvas.getOpenTabs().get(0);
                        opentab.setVisible(true);
                    }
                    if (Main.dialogIsShowing && GraphCanvas.getOpenTabs().size() > 0) { //put the window to the right side of the screen
                        Window[] dialogs = Dialog.getWindows();
                        for (int i = 0; i < dialogs.length; i++) {
                            if (dialogs[i].getName().equals("tutorMsg") || dialogs[i].getName().equals("tutorQues")) {
                                dialogs[i].setVisible(true);
                                dialogs[i].setAlwaysOnTop(true);
                            }
                        }
                    }
                    /*
                    try {
                    if (TaskFactory.getInstance().getActualTask().listOfVertexes != null) {
                    graph.setVertexes(TaskFactory.getInstance().getActualTask().listOfVertexes);
                    }
                    if (ReadModelFromFile && !alreadyRan) {
                    graphCanvasScroll.getGraphCanvas().runModelFromDebug();
                    alreadyRan = true;
                    }
                    if (TaskFactory.getInstance().getActualTask().alledges != null) {
                    graph.setEdges(TaskFactory.getInstance().getActualTask().alledges);
                    }
                    } catch (CommException ex) {
                    java.util.logging.Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                     */
                    logger.out(Logger.ACTIVITY, "Main.setTabListener.5");
                    graphCanvasScroll.getGraphCanvas().getCover().hideSomeComponents(false);
                }

                if (Main.dialogIsShowing) {
                    Window[] dialogs = Dialog.getWindows();
                    for (int i = 0; i < dialogs.length; i++) {
                        if (dialogs[i].getName().equals("tutorMsg") || dialogs[i].getName().equals("tutorQues")) {
                            dialogs[i].setVisible(true);
                            dialogs[i].setAlwaysOnTop(true);
                        }
                    }
                }

            }
        };
        tabPane.addChangeListener(changeListener);
    }

    /* PRIVATE AUXILIAR METHODS ----------------------------------------------- */
    /**
     * Ask for the version of software to use.
     */
    private static void setProfessorVersion() {
        Object[] options = {"Student Version", "Professor Version", "Version 2", "Student Version Demo", "Version 2 Demo"};
        Object selectedValue = JOptionPane.showInputDialog(null, "Please choose a version", "Choose Version", JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        if (selectedValue == "Student Version") {
            professorVersion = STUDENTVERSION;

        } else if (selectedValue == "Professor Version") {
            professorVersion = PROFESSORVERSION;
        } else if (selectedValue == "Version 2") {
            professorVersion = VERSION2;
        } else if (selectedValue == "Student Version Demo") {
            professorVersion = DEMO;
        } else if (selectedValue == "Version 2 Demo") {
            professorVersion = DEMO_VERSION2;
        } else {
            System.exit(0);
        }

        if (professorVersion == VERSION2) {
            VERSIONID = "2";
        } else {
            VERSIONID = "112";
        }
    }

    /**
     * This method update the content of the menu Task.
     * With this cycle we get the data of the different available tasks from the
     * database object and complete the drop-down menu "Task" at the menu bar.
     */
    private void loadMenuTask() {

        JMenuItem mt;
        // FY if (database.getTasks() != null) {
        try {
            if (taskFactory.getTasks() != null) {
                // FY String level = database.getTasks().get(0).getLevel();
                // FY for (Task i : database.getTasks()) {
                String level = taskFactory.getTasks().get(0).getLevel();

                // The tabPane should initially show the Situation Tab
                tabPane.setSelectedIndex(1);
                for (Task i : taskFactory.getTasks()) {

                    // add separators in the menu
                    if (!level.equals(i.getLevel())) {
                        level = i.getLevel();
                    }
                    mt = new JMenuItem(i.getTitle());
                    mt.setFont(graphCanvasScroll.getGraphCanvas().normal);
                    mt.setActionCommand(i.getId() + "");

                    final Main m = this;
                    mt.addActionListener(new java.awt.event.ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent evt) {
                            //Ask for a pwd in order to be able to change to a different task
                            JPasswordField password = new JPasswordField();
                            final JComponent[] inputs = new JComponent[]{
                                new JLabel("Password"),
                                password
                            };
                            JOptionPane.showMessageDialog(null, inputs, "New Task", JOptionPane.PLAIN_MESSAGE);
                            if (!password.getText().equals("amt22amt")) {
                                return;
                            }

                            //Vertex.resetGraphStatus();

                            if (taskFactory.getActualTask().getTitle() != null) {
                                // FY if (database.getActualTask().getTitle() != null) {
                                String msg = "<html>You are about to change to a different task. You will lose your work. Do you agree?</html>";
                                MessageDialog.showYesNoDialog(m, true, msg, graph);

                                if (graph.getN() == 0) {
                                    graphCanvasScroll.getGraphCanvas().loadLevel(Integer.parseInt(evt.getActionCommand()));
                                    //loadScreenTask(Integer.parseInt(evt.getActionCommand()));
                                    logger.out(1, "Main.loadMenuTask.1", evt.getActionCommand());
                                    tabPane.setSelectedIndex(1);
                                    graphCanvasScroll.getGraphCanvas().getCover().getMenuBar().getDoneButton().setEnabled(false);
                                    graphCanvasScroll.getGraphCanvas().getCover().getMenuBar().resetRunBtnClickCount();

                                    for (int i = 0; i < GraphCanvas.openTabs.size(); i++) {
                                        GraphCanvas.openTabs.get(i).dispose();
                                        GraphCanvas.openTabs.clear();
                                    }
                                    Main.dialogIsShowing = false;
                                }
                            } else {
                                graphCanvasScroll.getGraphCanvas().loadLevel(Integer.parseInt(evt.getActionCommand()));
                                //loadScreenTask(Integer.parseInt(evt.getActionCommand()));
                                tabPane.setSelectedIndex(1);
                            }
                            //close equationEditor
                            if (graphCanvasScroll.getGraphCanvas().ee != null) {
                                for (int i = 0; i < graphCanvasScroll.getGraphCanvas().ee.size(); i++) {
                                    graphCanvasScroll.getGraphCanvas().ee.get(i).dispose();
                                }
                            }
                            //close plotDialog
                            if (graph.getPlots() != null) {
                                for (int i = 0; i < graph.getPlots().size(); i++) {
                                    graph.getPlots().get(i).dispose();
                                }
                            }
                            //close QuizDialog
                            if (graph.getQuiz() != null) {
                                for (int i = 0; i < graph.getQuiz().size(); i++) {
                                    graph.getQuiz().get(i).dispose();
                                }
                            }
                            //close HintDialog
                            if (graph.getHint() != null) {
                                for (int i = 0; i < graph.getHint().size(); i++) {
                                    graph.getHint().get(i).dispose();
                                }
                            }
                            menuItemTakeQuiz.setForeground(Color.GRAY);
                        }
                    });
                }
            } else {
                System.exit(0);
            }
        } catch (DataException de) {
            //PRINT ERRRORT Y LOG
            System.out.println("Main.loadMenuTask.1");
            System.exit(0);
        }
    }

    /**
     * Returns the current version id
     * @return the version ID
     */
    public String getVersionID() {
        if (professorVersion == VERSION2) {
            return "2";
        } else {
            return "112";
        }
    }

    /**
     * This method initializes all of the fonts to a standard type
     */
    private void initFonts() {
        statusBar.setFont(graphCanvasScroll.getGraphCanvas().normal);
        menuFile.setFont(graphCanvasScroll.getGraphCanvas().normal);
        newTask.setFont(graphCanvasScroll.getGraphCanvas().normal);
        menuItemSaveTask.setFont(graphCanvasScroll.getGraphCanvas().normal);
        menuItemGenerateSolution.setFont(graphCanvasScroll.getGraphCanvas().normal);
        menuItemOpenTask.setFont(graphCanvasScroll.getGraphCanvas().normal);
        menuModel.setFont(graphCanvasScroll.getGraphCanvas().normal);
        menuItemRun.setFont(graphCanvasScroll.getGraphCanvas().normal);
        menuHelp.setFont(graphCanvasScroll.getGraphCanvas().normal);
        menuItemAbout.setFont(graphCanvasScroll.getGraphCanvas().normal);
        menuItemHelp.setFont(graphCanvasScroll.getGraphCanvas().normal);
        menuItemUpdate.setFont(graphCanvasScroll.getGraphCanvas().normal);
        menuItemFeedback.setFont(graphCanvasScroll.getGraphCanvas().normal);
        ticketButton.setFont(graphCanvasScroll.getGraphCanvas().normal);
        menuItemExit.setFont(graphCanvasScroll.getGraphCanvas().normal);
        statusBarLabel.setFont(graphCanvasScroll.getGraphCanvas().normal);
        tabPane.setFont(graphCanvasScroll.getGraphCanvas().normal);
        menuItemTakeQuiz.setFont(graphCanvasScroll.getGraphCanvas().normal);
        menuEditGraphRange.setFont(graphCanvasScroll.getGraphCanvas().normal);
    }


    /* NETBEANS CODE  --------------------------------------------------------- */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        statusBar = new javax.swing.JPanel();
        ticketPanel = new javax.swing.JPanel();
        statusBarLabel = new javax.swing.JLabel();
        ticketButton = new javax.swing.JButton();
        tabPane = new javax.swing.JTabbedPane();
        instructionPanel = new javax.swing.JPanel();
        problemPanel = new javax.swing.JPanel();
        mainPanel = new javax.swing.JPanel();
        centerPanel = new javax.swing.JPanel();
        graphCanvasScrollPane = new javax.swing.JPanel();
        menuBar = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        newTask = new javax.swing.JMenuItem();
        menuItemOpenTask = new javax.swing.JMenuItem();
        menuItemSaveTask = new javax.swing.JMenuItem();
        menuEditGraphRange = new javax.swing.JMenuItem();
        menuItemGenerateSolution = new javax.swing.JMenuItem();
        menuItemExit = new javax.swing.JMenuItem();
        menuModel = new javax.swing.JMenu();
        menuItemRun = new javax.swing.JMenuItem();
        menuItemTakeQuiz = new javax.swing.JMenuItem();
        menuHelp = new javax.swing.JMenu();
        menuItemAbout = new javax.swing.JMenuItem();
        menuItemHelp = new javax.swing.JMenuItem();
        separator = new javax.swing.JPopupMenu.Separator();
        menuItemUpdate = new javax.swing.JMenuItem();
        menuItemFeedback = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Affective Meta Tutor");

        statusBar.setLayout(new java.awt.BorderLayout());

        ticketPanel.setLayout(new java.awt.BorderLayout());

        statusBarLabel.setText("*");
        ticketPanel.add(statusBarLabel, java.awt.BorderLayout.CENTER);

        ticketButton.setText("Send Feedback");
        ticketButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ticketButtonActionPerformed(evt);
            }
        });
        ticketPanel.add(ticketButton, java.awt.BorderLayout.EAST);

        statusBar.add(ticketPanel, java.awt.BorderLayout.CENTER);

        getContentPane().add(statusBar, java.awt.BorderLayout.SOUTH);

        tabPane.addTab("Instructions", instructionPanel);
        tabPane.addTab("Situation", problemPanel);

        mainPanel.setLayout(new java.awt.BorderLayout());

        centerPanel.setLayout(new java.awt.BorderLayout());

        graphCanvasScrollPane.setLayout(new java.awt.GridLayout(1, 0));
        centerPanel.add(graphCanvasScrollPane, java.awt.BorderLayout.CENTER);

        mainPanel.add(centerPanel, java.awt.BorderLayout.CENTER);

        tabPane.addTab("Model", mainPanel);

        getContentPane().add(tabPane, java.awt.BorderLayout.CENTER);

        menuFile.setBorder(null);
        menuFile.setMnemonic('f');
        menuFile.setText("File");

        newTask.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        newTask.setText("New Task");
        newTask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newTaskActionPerformed(evt);
            }
        });
        menuFile.add(newTask);

        menuItemOpenTask.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        menuItemOpenTask.setText("Open Task...");
        menuItemOpenTask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemOpenTaskActionPerformed(evt);
            }
        });
        menuFile.add(menuItemOpenTask);

        menuItemSaveTask.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        menuItemSaveTask.setText("Save Task...");
        menuItemSaveTask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemSaveTaskActionPerformed(evt);
            }
        });
        menuFile.add(menuItemSaveTask);

        menuEditGraphRange.setText("Edit Time Range");
        menuEditGraphRange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuEditGraphRangeActionPerformed(evt);
            }
        });
        menuFile.add(menuEditGraphRange);

        menuItemGenerateSolution.setText("Generate Solution");
        menuItemGenerateSolution.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemGenerateSolutionActionPerformed(evt);
            }
        });
        menuFile.add(menuItemGenerateSolution);

        menuItemExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        menuItemExit.setText("Exit");
        menuItemExit.setActionCommand("ExitCommand");
        menuItemExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemExitActionPerformed(evt);
            }
        });
        menuFile.add(menuItemExit);

        menuBar.add(menuFile);

        menuModel.setText("Model");

        menuItemRun.setText("Run");
        menuItemRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemRunActionPerformed(evt);
            }
        });
        menuModel.add(menuItemRun);

        menuItemTakeQuiz.setText("Take a Quiz");
        menuItemTakeQuiz.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemTakeQuizActionPerformed(evt);
            }
        });
        menuModel.add(menuItemTakeQuiz);

        menuBar.add(menuModel);

        menuHelp.setText("Help");

        menuItemAbout.setText("About...");
        menuItemAbout.setActionCommand("menuItemAbout");
        menuItemAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemAboutActionPerformed(evt);
            }
        });
        menuHelp.add(menuItemAbout);

        menuItemHelp.setText("Help");
        menuItemHelp.setActionCommand("menuItemAbout");
        menuItemHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemHelpActionPerformed(evt);
            }
        });
        menuHelp.add(menuItemHelp);
        menuHelp.add(separator);

        menuItemUpdate.setText("Search for Updates");
        menuItemUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                systemUpdateActionPerformed(evt);
            }
        });
        menuHelp.add(menuItemUpdate);

        menuItemFeedback.setText("Send Feedback...");
        menuItemFeedback.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemFeedbackActionPerformed(evt);
            }
        });
        menuHelp.add(menuItemFeedback);

        menuBar.add(menuHelp);

        setJMenuBar(menuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Method to display the dialog box of "About..." from the "Help" menu at the menu bar
     *
     * @param evt
     */
  private void menuItemAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemAboutActionPerformed
      AboutDialog aad = new AboutDialog(this, false);
      aad.setVisible(true);
  }//GEN-LAST:event_menuItemAboutActionPerformed

    /**
     * Method to display the dialog box of "Send Ticket..." from the "Help" menu at the menu bar
     *
     * @param evt
     */
  private void menuItemFeedbackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemFeedbackActionPerformed
      SendTicketDialog std = new SendTicketDialog(this, false);
      std.setVisible(true);
  }//GEN-LAST:event_menuItemFeedbackActionPerformed

    /**
     * Method to display the dialog box of "Update Activity..." from the "Help" menu at the menu bar
     *
     * @param evt
     */
  private void systemUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_systemUpdateActionPerformed
      // TODO: This method should review if at the database is any new task or a new version of
      // the current tasks. If any, then it should download them from the database.
      String fileName = "menuTask.txt";
      String fileName1 = "backup.txt";
      HashMap<Integer, String> taskMap = new HashMap<Integer, String>();
      try {
          BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
          String line = null;
          while ((line = br.readLine()) != null) {
              int tabIndex = line.indexOf('\t');
              if (tabIndex <= 0) {
                  continue;
              }
              int id = Integer.parseInt(line.substring(0, tabIndex));
              String name = line.substring(tabIndex + 1);
              taskMap.put(id, name);
          }
          br.close();
      } catch (Exception e) {
      }
      // FY LinkedList<Task> tmp = database.getTasks();
      try {
          LinkedList<Task> tmp = taskFactory.getTasks();
          if (tmp != null) {
              for (Task t : tmp) {
                  int id = t.getId();
                  String name = t.getTitle();
                  if (!taskMap.containsKey(id) || !name.equals(taskMap.get(id))) {
                      loadMenuTask();
                      graphCanvasScroll.getGraphCanvas().loadLevel(Integer.parseInt(evt.getActionCommand()));
                      //loadScreenTask(Integer.parseInt(evt.getActionCommand()));
                      tabPane.setSelectedIndex(0);
                  }
              }
          }

      } catch (DataException de) {
          //REPROTE DE ERROR
      }
  }//GEN-LAST:event_systemUpdateActionPerformed

    /**
     * Method to display the dialog box of "Send Feedback..." from the "Help" menu at the menu bar
     *
     * @param evt
     */
  private void ticketButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ticketButtonActionPerformed
      logger.out(Logger.ACTIVITY, "Main.ticketButtonActionPerformed.1");
      SendTicketDialog std = new SendTicketDialog(this, true);
      std.setFont(graphCanvasScroll.getGraphCanvas().header);
      std.setVisible(true);
  }//GEN-LAST:event_ticketButtonActionPerformed
    public static JButton getTicketButton() {
        return ticketButton;
    }

    /**
     * Method to display the dialog box of "Help..." from the "Help" menu at the menu bar
     *
     * @param evt
     */
  private void menuItemHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemHelpActionPerformed
      HelpDialog chd = new HelpDialog(this, false);
      chd.setFont(graphCanvasScroll.getGraphCanvas().header);
      chd.setVisible(true);
      logger.out(Logger.ACTIVITY, "Main.menuItemHelpActionPerformed");
  }//GEN-LAST:event_menuItemHelpActionPerformed

    /**
     * This method determines whether the model can be run
     *
     * @param evt
     */
  private void menuItemRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemRunActionPerformed

      if (menuItemRun.getForeground() != Color.GRAY) {
          //FYif (graph.run(database, graphCanvasScroll.getGraphCanvas())) {
          if (graph.run(taskFactory, graphCanvasScroll.getGraphCanvas())) {
              menuItemTakeQuiz.setForeground(Color.BLACK);
          } else {
              menuItemTakeQuiz.setForeground(Color.GRAY);
          }
      } else {
          MessageDialog.showMessageDialog(this, true, "All nodes should be connected and have an equation before the model can be run", graph);
          logger.out(Logger.ACTIVITY, "Main.menuItemRunActionPerformed.1");
      }
  }//GEN-LAST:event_menuItemRunActionPerformed

    /**
     * Method to display the dialog box of "Exit" from the "File" menu at the menu bar
     * @param evt
     */
  private void menuItemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemExitActionPerformed
      ExitDialog ed = new ExitDialog(this, false);
      ed.setVisible(true);
  }//GEN-LAST:event_menuItemExitActionPerformed

    /**
     * Method to display the dialog box of "Save Task..." from the "File" menu at the menu bar
     * @param evt
     */
  private void menuItemSaveTaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemSaveTaskActionPerformed
      JFileChooser fc = new JFileChooser();
      String extension = ".laits";
      File newFile = null;
      FileNameExtensionFilter fnef = new FileNameExtensionFilter("LAITS file", "laits");
      fc.addChoosableFileFilter(fnef);
      fc.setFont(graphCanvasScroll.getGraphCanvas().normal);
      int rc = fc.showSaveDialog(this);
      fc.setDialogTitle("Save File");
      if (rc == JFileChooser.APPROVE_OPTION) {
          File savedFile = fc.getSelectedFile();
          newFile = new File(savedFile.getAbsolutePath() + extension);
          try {
              graph.save(newFile);
              logger.concatOut(Logger.ACTIVITY, "Main.menuItemSaveTaskActionPerformed.1", fc.getSelectedFile().getName());
          } catch (IOException ex) {
              //Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
              logger.concatOut(Logger.ACTIVITY, "Main.menuItemSaveTaskActionPerformed.2", ex.toString());
          }
      }
  }//GEN-LAST:event_menuItemSaveTaskActionPerformed

    /**
     * This method controls whether the student can take a quiz
     * @param evt
     */
  private void menuItemTakeQuizActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemTakeQuizActionPerformed
      // STEP 0. If there is not a Task selected and a Model already runned do not allow to take the quiz
      // STEP 1. We need to indentify which task we are doing in order to open that quiz
      if (graphCanvasScroll.getGraphCanvas().getModelChanged() == true || menuItemTakeQuiz.getForeground() == Color.GRAY) {
          MessageDialog.showMessageDialog(this, true, "The model needs to be run before the quiz can be taken", graph);
      } else {
          logger.out(Logger.ACTIVITY, "Main.menuItemTakeQuizActionPerformed.1");
          // FY int id = database.getActualTask().getId();
          int id = taskFactory.getActualTask().getId();
          Quiz quiz = new Quiz(id);

          // STEP 2. PUT QUIZ IN QUIZ DIALOG
          QuizDialog quizDialog = null;

          //FY int[] currentLevelList = tasksPerLevel.get(Integer.parseInt(database.getActualTask().getLevel()));
          int[] currentLevelList = taskFactory.getTasksPerLevel().get(Integer.parseInt(taskFactory.getActualTask().getLevel()));
          // FY if (database.getActualTask().getId() == currentLevelList[currentLevelList.length - 1]) {
          if (taskFactory.getActualTask().getId() == currentLevelList[currentLevelList.length - 1]) {
              // FY quizDialog = new QuizDialog(this, false, quiz, database.getActualTask().getTitle(), graph, graphCanvasScroll.getGraphCanvas(), true);
              quizDialog = new QuizDialog(this, false, quiz, taskFactory.getActualTask().getTitle(), graph, graphCanvasScroll.getGraphCanvas(), true);
          } else {
              // quizDialog = new QuizDialog(this, false, quiz, database.getActualTask().getTitle(), graph, graphCanvasScroll.getGraphCanvas(), false);
              quizDialog = new QuizDialog(this, false, quiz, taskFactory.getActualTask().getTitle(), graph, graphCanvasScroll.getGraphCanvas(), false);
          }
          // STEP 3. THIS METHOD SOLVE THE QUIZ. COMPARE QUIZ VS USER.
          quiz.solve(graph);
          // STEP 4. SHOW IN THE QUIZDIALOG THE RESULTS OF THE QUIZ
          for (int i = 0; i < quiz.getUserAnswer().size(); i++) {
              quizDialog.addAnswer(i, quiz.getUserAnswer().get(i).toUpperCase());
          }
          for (int i = 0; i < quiz.getAnswer().size(); i++) {
              quizDialog.addResult(i, quiz.getAnswer().get(i).isEvaluateCorrect());
          }
          quizDialog.setVisible(true);
      }
  }//GEN-LAST:event_menuItemTakeQuizActionPerformed

    /**
     * Method to display the dialog box of "Open File..." from the "File" menu at the menu bar
     * @param evt
     */
  private void menuItemOpenTaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemOpenTaskActionPerformed
      if(graphCanvasScroll.getGraphCanvas().getGraph().getVertexes().size()>0){
          int n = JOptionPane.showConfirmDialog(
              this,
              "All the unsaved changes will be lost, Do you want to continue?",
              "Confirm Open Task",
              JOptionPane.YES_NO_OPTION);
          if (n == 1) {
                return;
          }
      }
      
          JFileChooser fc = new JFileChooser();
          File newFile = null;
          fc.setFont(graphCanvasScroll.getGraphCanvas().normal);
          int rc = fc.showOpenDialog(this);
          fc.setDialogTitle("Open File");
          if (rc == JFileChooser.APPROVE_OPTION) {
              File openFile = fc.getSelectedFile();
              try {
                  graphCanvasScroll.getGraphCanvas().deleteAll();
                  graph.load(openFile);
                  graphCanvasScroll.getGraphCanvas().setModelChanged(true);
                  LinkedList l = graph.getVertexes();
                  LinkedList<String> list = new LinkedList<String>();
                  for (int i = 0; i < l.size(); i++) {
                      list.add(((Vertex) l.toArray()[i]).label);
                  }
                  LinkedList e = graph.getEdges();
                  graphCanvasScroll.getGraphCanvas().deleteAll();
                  for (int i = 0; i < l.size(); i++) {
                      graphCanvasScroll.getGraphCanvas().newVertex(((Vertex) l.toArray()[i]));
                  }
                  for (int i = 0; i < e.size(); i++) {
                      graphCanvasScroll.getGraphCanvas().newEdge(((Edge) e.toArray()[i]));
                  }
                  //FY database.setActualTask(database.searchTask(graph.taskID));
                  // FY taskView.updateTask(database.getActualTask());

                  taskFactory.setActualTask(taskFactory.searchTask(graph.taskID));
                  taskView.updateTask(taskFactory.getActualTask());
                  try {
                      //taskView.updateTask(database.getTasks(graph.taskID));
                      //instructionView.updateInstruction(database.getTasks(graph.taskID));
                      taskView.updateTask(taskFactory.getTasks(graph.taskID));
                      instructionView.updateInstruction(taskFactory.getTasks(graph.taskID));
                  } catch (DataException de) {
                  }

                  //FYgraphCanvasScroll.getGraphCanvas().updateTask(database.getActualTask());
                  //FYif (database.getActualTask().getTitle() == null) {
                  graphCanvasScroll.getGraphCanvas().updateTask(taskFactory.getActualTask());


                  String key = "Student opened " + fc.getSelectedFile().getName();
                  logger.out(Logger.ACTIVITY, key);
              } catch (DocumentException ex) {
                  MessageDialog.showMessageDialog(this, true, "The file you are intent to open does not have the correct format", graph);
                  //Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
              }
          
      }


  }//GEN-LAST:event_menuItemOpenTaskActionPerformed

    private void menuItemGenerateSolutionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemGenerateSolutionActionPerformed
        // Check if Model has been run
        if (Main.alreadyRan) {
            JFileChooser fc = new JFileChooser();
            String extension = ".txt";
            File newFile = null;
            FileNameExtensionFilter fnef = new FileNameExtensionFilter("LAITS Solution file", "txt");
            fc.addChoosableFileFilter(fnef);
            fc.setFont(graphCanvasScroll.getGraphCanvas().normal);
            int rc = fc.showSaveDialog(this);
            fc.setDialogTitle("Save File");
            if (rc == JFileChooser.APPROVE_OPTION) {
                File savedFile = fc.getSelectedFile();
                newFile = new File(savedFile.getAbsolutePath() + extension);
                try {
                    graph.saveSolution(newFile);
                    System.out.println("Solution file saved - Main.java");
                } catch (IOException ex) {
                    System.out.println("Error in Saving file- Main.java");
                }
            }
        } else {
            ErrorDialog err = new ErrorDialog(this, true, "Solution Generation Error", "Please run the model before generating solution.");
            err.setVisible(true);
        }
    }//GEN-LAST:event_menuItemGenerateSolutionActionPerformed

    private void menuEditGraphRangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuEditGraphRangeActionPerformed
        // Display Graph Range Editor
        GraphRangeEditor rangeEditor = new GraphRangeEditor(this, true);
        rangeEditor.setVisible(true);
    }//GEN-LAST:event_menuEditGraphRangeActionPerformed

    private void newTaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newTaskActionPerformed
        
        if(graphCanvasScroll.getGraphCanvas().getGraph().getVertexes().size()>0){
          int n = JOptionPane.showConfirmDialog(
              this,
              "All the unsaved changes will be lost, Do you want to continue?",
              "Confirm Open Task",
              JOptionPane.YES_NO_OPTION);
          if (n == 1) {
                return;
          }
      }
       graphCanvasScroll.getGraphCanvas().deleteAll();
        
    }//GEN-LAST:event_newTaskActionPerformed

    public static boolean windowIsClosing() {
        return windowIsClosing;
    }

    /**
     * Method to close project
     * @param e
     */
    public void windowClosing(WindowEvent e) {
        windowIsClosing = true;
        ExitDialog ed = new ExitDialog(this, false);
        ed.setVisible(true);
    }

    public void windowOpened(WindowEvent e) {
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel centerPanel;
    private javax.swing.JPanel graphCanvasScrollPane;
    private javax.swing.JPanel instructionPanel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem menuEditGraphRange;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenu menuHelp;
    private javax.swing.JMenuItem menuItemAbout;
    private javax.swing.JMenuItem menuItemExit;
    private javax.swing.JMenuItem menuItemFeedback;
    private javax.swing.JMenuItem menuItemGenerateSolution;
    private javax.swing.JMenuItem menuItemHelp;
    private javax.swing.JMenuItem menuItemOpenTask;
    private javax.swing.JMenuItem menuItemRun;
    private javax.swing.JMenuItem menuItemSaveTask;
    private javax.swing.JMenuItem menuItemTakeQuiz;
    private javax.swing.JMenuItem menuItemUpdate;
    private javax.swing.JMenu menuModel;
    private javax.swing.JMenuItem newTask;
    private javax.swing.JPanel problemPanel;
    private javax.swing.JPopupMenu.Separator separator;
    private javax.swing.JPanel statusBar;
    private javax.swing.JLabel statusBarLabel;
    private javax.swing.JTabbedPane tabPane;
    private static javax.swing.JButton ticketButton;
    private javax.swing.JPanel ticketPanel;
    // End of variables declaration//GEN-END:variables
    private JScrollPane scroller;

    public JTabbedPane getTabPane() {
        return tabPane;
    }

    public JMenuItem getMenuItemRun() {
        return menuItemRun;
    }

    public JMenuItem getMenuItemTakeQuiz() {
        return menuItemTakeQuiz;
    }

    public void actionPerformed(ActionEvent e) {

        //Handle open button action.
        if (e.getSource() == openButton) {
            int returnVal = fc.showOpenDialog(Main.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                //This is where a real application would open the file.

            }

            //Handle save button action.
        }
    }
}
