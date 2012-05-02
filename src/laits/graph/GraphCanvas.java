package laits.graph;

import amt.ApplicationVersion;
import laits.BlockSocket;
import laits.cover.Cover;
import laits.cover.Clock;
import laits.cover.Avatar;
import laits.data.Task;
import laits.Main;
import laits.MetaTutorMsg;
import laits.TaskView;
import laits.comm.CommException;
import laits.data.DataException;
import laits.gui.CongratulationsDialog;
import laits.gui.MessageDialog;
import laits.gui.PlotDialog;
import laits.gui.QuizDialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import laits.log.*;
import laits.parser.Equation;
import laits.parser.EquationEditor;
import laits.data.Quiz;
import laits.data.TaskFactory;
import java.util.LinkedList;
import java.net.URL;
import java.util.Random;
import laits.*;
import laits.version2.TabbedGUI;

/**
 * Class Facade of the application. This class calls the Graph class.
 *
 * @author Javier Gonzalez Sanchez
 * @author Lakshmi Sudha Marothu
 * @author Patrick Lu
 * @author Quanwei Zhao
 * @author Megan Kearl
 * @version 20100223
 */
public class GraphCanvas extends JPanel implements FocusListener, ActionListener, KeyListener, MouseListener, MouseMotionListener, ComponentListener, Scrollable {

    private boolean changeShape = false;
    private static Logger logs = Logger.getLogger();
    private Graph graph;
    private Image image = null;
    //private Image iconInfo = null;
    private Dimension imageSize = new Dimension(0, 0);
    // When all objects are being moved, this gives the last base point.
    private transient Point moveAllFrom = null;
    // When allEdges label is being moved, this is the offset of the from the labelPoint to the mouse point.
    private Point labelOffset = null;
    private Dimension area;
    //Get JFrame from Main to pass to the equation dialog
    private JFrame frame;
    private Cover cover;
    private Clock clock;
    public boolean paintDescriptionText = false;
    protected Task task;
    //fonts used for the description:
    public Font header = new Font("Arial", Font.BOLD, 16);
    public Font normal = new Font("Arial", Font.PLAIN, 14);
    //font metrics:
    public FontMetrics headerFontMetrics = Toolkit.getDefaultToolkit().getFontMetrics(header);
    public FontMetrics normalFontMetrics = Toolkit.getDefaultToolkit().getFontMetrics(normal);
    //yIndent is the indentation from the top of the screen
    int yIndent = 20;
    int xIndent = 50;
    //the diameter of the info circle
    int d = 50;
    //descriptionIndent is the amount of indent for the description
    int descriptionIndent = xIndent + xIndent / 2 + d;
    //if there is an invalid connection error, the animation will wave
    public static boolean errorAnimation = false;
    // task description
    public String taskDescription = "";
    //holds the lines of text for the task for the info icon
    LinkedList<String> taskLinesInfo = new LinkedList<String>();
    //vertex menu
    private int distance = 8;
    private int iconWidth = 20;
    private int iconHeight = 20;
    private int borderWidth = 24;
    private int borderHeight = 24;
    int widthDif = borderWidth - iconWidth;
    private Image edgeImage = null;
    private Image graphImage = null;
    private Image graphDisabledImage = null;
    private Image equationImage = null;
    private Image equationDisabledImage = null;
    //Version 2 images
    private Image calculationsNoStatus = null;
    private Image calculationsCorrect = null;
    private Image calculationsGaveUp = null;
    private Image calculationsWrong = null;
    private Image graphsNoStatus = null;
    private Image graphsCorrect = null;
    private Image graphsGaveUp = null;
    private Image graphsWrong = null;
    private Image inputsNoStatus = null;
    private Image inputsCorrect = null;
    private Image inputsGaveUp = null;
    private Image inputsWrong = null;
    private Vertex menuVertex = new Vertex();
    //Enable the menu
    private boolean enableMenu = false;
    private boolean enableEdge = false;
    public boolean quiz = false;
    private boolean modelChanged = false;
    private Point descriptionPos = new Point(0, 0);
    private int descriptionWidth = 0;
    private int descriptionHeight = 0;
    private int index = 0; //index of the largest line
    //the amount of curvature for the rounded corners of the rectangular task description box
    private int rectArc = 15;
    //added by quanwei
    private JLabel buttomLabel;
    private JMenuItem run;
    private JMenuItem takeQuiz;
    private boolean hitDescrip;
    private boolean menuOpen = false;
    private boolean enableEdgeError = false;
    private Vertex menuOpenVertex = null;
    private boolean practice = false;
    private boolean passed = false;
    private boolean giveUp = false;
    private boolean continues = false;
    private boolean edgeErrorDisplayed = false;
    private boolean studentReceivedLevelPoint = false;
    private boolean quizOpen = false;
    private int currentLevel = -1;
    private int problemIndex = 0;
    private LinkedList<Integer> problemsCompleted = new LinkedList<Integer>();
    private TaskFactory server;
    private LinkedList<int[]> problemList;
    private LinkedList<Double> scoreList = new LinkedList<Double>();
    private TaskView taskView;
    private TaskView instructionView;
    private JTabbedPane tabPane;
    private int currentLevelPoints;
    private JButton runButton, takeQuizButton, shortDescriptionButton;
    public LinkedList<EquationEditor> ee = new LinkedList<EquationEditor>();
    private LinkedList<Avatar> avatarList = new LinkedList<Avatar>();
    public static LinkedList<TabbedGUI> openTabs = new LinkedList<TabbedGUI>();
    private Logger logger = Logger.getLogger();
    private Random rand = new Random();
    //IMPORTANT NOTE: To move the speech bubble's x position, just change the following variable
    //making the value negative will make it go to the right, positive will go to the left
    int xBubbleOffset = 225;
    //IMPORTANT NOTE: To move the speech bubble's y position, just change the following variable
    //making the value negative will make it go to up, positive will go down
    int yBubbleOffset = 0;
    private static int professorVersion = 1;
    //For Version 2
    public boolean modelHasBeenRun = false;
    //The following variable is used to tell whether the inputs, calculations, and
    //graph panels are all correct for all nodes in the graph
    private boolean allCorrect = true;
    public LinkedList<String> listOfVertexes = null;
    public LinkedList<String> extraNodes = null;
    private BlockSocket blockSocket = BlockSocket.getBlockSocket();

    /**
     * Constructor Creates the main frame
     *
     * @param frame is the main frame
     */
    //public GraphCanvas(JFrame jf, Graph g, JMenuItem run, JMenuItem takeQuiz, LinkedList<int[]> tasksPerLevel, /*Database server,*/ ProblemView problemView,ProblemView instructionView ,JTabbedPane tabPane) {
    public GraphCanvas(Main jf) {
        super();
        setFocusable(true);
        area = new Dimension(0, 0);
        this.graph = jf.getGraph();
        this.frame = jf;
        this.professorVersion = jf.professorVersion;
        this.run = jf.getMenuItemRun();
        this.takeQuiz = jf.getMenuItemTakeQuiz();
        // this.tasksPerLevel = jf.tasksPerLevel;
        setLayout(null);


        try {
            this.server = TaskFactory.getInstance();
            this.problemList = server.getTasksPerLevel();
        } catch (CommException de) {
            // do something
            System.out.println("GraphCanvas.GraphCanvas.1");
        }
        this.taskView = jf.taskView;
        this.instructionView = jf.instructionView;
        this.tabPane = jf.getTabPane();
        try {
            initListen();
        } catch (NullPointerException e) {
            logs.concatOut(Logger.DEBUG, "GraphCanvas.GraphCanvas.1", e.toString());
        }
        initIcons();
        initRunButton();
        initTakeQuizButton();
        initShortDescriptionButton();
        initScoreList();
        cover = new Cover(this, graph, professorVersion, frame);
        taskView.setCover(cover);
        instructionView.setCover(cover);
        cover.setFont(normal);

        if (ApplicationEnvironment.applicationMode == 2) {
            // Mode 2 is for Author
            initAuthorProblem();
        } else {
            initFirstProblem();
        }
    }

    public JFrame getFrame() {
        return frame;
    }

    public Task getTask() {
        return this.server.getActualTask();
    }

    public Graph getGraph() {
        return this.graph;
    }

    /**
     * For every level in the system, add 0.0 as the initial score for that
     * level
     */
    private void initScoreList() {
        for (int i = 0; i < problemList.size(); i++) {
            scoreList.add(0.0);
        }
    }

    /**
     * This method gets the problem list
     *
     * @return the list of problems and levels for the system
     */
    public LinkedList<int[]> getProblemList() {
        return problemList;
    }

    /**
     * This method gets the list of scores for all levels
     *
     * @return the list of scores for all levels
     */
    public LinkedList<Double> getScoreList() {
        return scoreList;
    }

    /**
     * This method returns the list of avatars
     *
     * @return the avatar list
     */
    public LinkedList<Avatar> getAvatarList() {
        return avatarList;
    }

    /**
     * This method gets the short description button
     *
     * @return the short description button
     */
    public JButton getShortDescriptionButton() {
        return shortDescriptionButton;
    }

    /**
     * This method gets the run button
     *
     * @return the run button
     */
    public JButton getRunButton() {
        return runButton;
    }

    /**
     * This method gets the take quiz button
     *
     * @return the take quiz button
     */
    public JButton getTakeQuizButton() {
        return takeQuizButton;
    }

    public void setCurrentLevelPoints(int points) {
        currentLevelPoints = points;
    }

    /**
     * This method initializes a button to show the short description of the
     * problem
     */
    private void initShortDescriptionButton() {
        shortDescriptionButton = new JButton("Task Summary");
        shortDescriptionButton.setFont(normal.deriveFont(11));
        shortDescriptionButton.setLayout(new BorderLayout());
        shortDescriptionButton.setBackground(Color.WHITE);
        final GraphCanvas gc = this;
        shortDescriptionButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shortDescriptionActionPerformed(evt, gc);
            }

            private void shortDescriptionActionPerformed(ActionEvent evt, GraphCanvas gc) {
                logs.out(Logger.ACTIVITY, "GraphCanvas.initShortDescriptionButton.1");
                if (paintDescriptionText == false) {
                    paintDescriptionText = true;
                } else {
                    paintDescriptionText = false;
                }
            }
        });
    }

    /**
     * initialize run button
     */
    private void initRunButton() {
        runButton = new JButton("Run Model");
        runButton.setFont(normal.deriveFont(11));
        runButton.setLayout(new BorderLayout());
        runButton.setBackground(Color.WHITE);
        final GraphCanvas gc = this;
        runButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runButtonActionPerformed(evt, gc);
            }

            private void runButtonActionPerformed(ActionEvent evt, GraphCanvas gc) {
                logs.out(Logger.ACTIVITY, "GraphCanvas.initRunButton.1");
                if (run.getForeground() != Color.GRAY) {
                    if (graph.run(server, gc)) {
                        takeQuiz.setForeground(Color.BLACK);
                    } else {
                        takeQuiz.setForeground(Color.GRAY);
                    }
                } else {
                    //JOptionPane.showMessageDialog(null, "All nodes should be connected and have an equation before the model can be run");
                    MessageDialog.showMessageDialog(frame, true, "All nodes should be connected and have an equation before the model can be run", graph);
                    logs.out(Logger.ACTIVITY, "GraphCanvas.initRunButton.2");
                }
            }
        });
    }

    /**
     * initialize take quiz button
     */
    private void initTakeQuizButton() {
        takeQuizButton = new JButton("Take Quiz");
        takeQuizButton.setFont(normal.deriveFont(11));
        takeQuizButton.setLayout(new BorderLayout());
        takeQuizButton.setBackground(Color.WHITE);
        final GraphCanvas gc = this;
        takeQuizButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                takeQuizButtonActionPerformed(evt, gc);
            }

            private void takeQuizButtonActionPerformed(ActionEvent evt, GraphCanvas gc) {
                // STEP 0. If there is not a Task selected and a Model already runned do not allow to take the quiz
                // STEP 1. We need to indentify which task we are doing in order to open that quiz
                if (!quizOpen) {
                    if (getModelChanged() == true || takeQuiz.getForeground() == Color.GRAY) {
                        System.out.println(getModelChanged());
                        MessageDialog.showMessageDialog(frame, true, "The model needs to be run before the quiz can be taken", graph);
                    } else {
                        logs.out(Logger.ACTIVITY, "GraphCanvas.initTakeQuizButton.1");
                        quizOpen = true;
                        int id = server.getActualTask().getId();
                        Quiz quiz = new Quiz(id);
                        System.out.println("TASK ID = " + id);

                        // STEP 2. PUT QUIZ IN QUIZ DIALOG
                        //first check if there are more levels
                        QuizDialog quizDialog = null;
                        int level = Integer.parseInt(server.getActualTask().getLevel());
                        level += 1; //This is the only way not to receive an error when we have level -1
                        int[] currentLevelList = problemList.get(level);
                        if (server.getActualTask().getId() == currentLevelList[currentLevelList.length - 1]) {
                            quizDialog = new QuizDialog(frame, false, quiz, server.getActualTask().getTitle(), graph, gc, true);
                            logs.out(Logger.ACTIVITY, "GraphCanvas.initTakeQuizButton.2");
                        } else {
                            quizDialog = new QuizDialog(frame, false, quiz, server.getActualTask().getTitle(), graph, gc, false);
                            logs.out(Logger.ACTIVITY, "GraphCanvas.initTakeQuizButton.2");
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
                }
            }
        });
    }

    /**
     * This method opens the application on the first problem
     */
    private void initFirstProblem() {
        int firstLevel = 0;
        //find the first level
        for (int i = 0; i < problemList.size(); i++) {
            if (problemList.get(i) != null) {
                firstLevel = i;
                currentLevel = i;
                //if(VERSIONID.equals("112"))
                if (professorVersion != Main.VERSION2 && professorVersion != Main.DEMO_VERSION2) {
                    cover.getPointsAndLevel().setCurrentLevel(currentLevel);
                }
                break;
            }
        }
        //load the first problem
        loadLevel(problemList.get(firstLevel)[0]);
    }

    /**
     * This method Create the basic setup for a Blank problem in Author mode
     * 
     */
    private void initAuthorProblem() {
        practice = true;
        tabPane.setSelectedIndex(1);
        this.deleteAll();
        task = new Task();
        if (task != null) {
            //taskView.updateTask(task);
            instructionView.updateInstruction(task);
            //this.updateTask(server.getActualTask());

            listOfVertexes = task.getVertexNames();
            extraNodes = task.getExtraNodes();
            cover.getMenuBar().getNewNodeButton().setEnabled(true);
        }
        problemIndex = 0;
        run.setForeground(Color.GRAY);
    }

    /**
     * This method initializes all of the icons for the mouseover vertex menu
     */
    public void initIcons() {
        Toolkit toolkit = java.awt.Toolkit.getDefaultToolkit();
        if (Main.professorVersion != Main.VERSION2 && Main.professorVersion != Main.DEMO_VERSION2) {
            URL graphURL = Main.class.getResource("/amt/images/graph.png");
            graphImage = (toolkit.createImage(graphURL));
            URL graphDisabledURL = Main.class.getResource("/amt/images/graphDisabled.png");
            graphDisabledImage = toolkit.createImage(graphDisabledURL);
            URL edgeURL = Main.class.getResource("/amt/images/arrow.png");
            edgeImage = (toolkit.createImage(edgeURL));
            URL equationURL = Main.class.getResource("/amt/images/math.png");
            equationImage = (toolkit.createImage(equationURL));
            URL equationDisabledURL = Main.class.getResource("/amt/images/mathDisabled.png");
            equationDisabledImage = toolkit.createImage(equationDisabledURL);
        } else {
            URL calcWrongURL = Main.class.getResource("/amt/images/CalculationsWrongStatus.png");
            calculationsWrong = (toolkit.createImage(calcWrongURL));
            URL calcCorrectURL = Main.class.getResource("/amt/images/CalculationsCorrectStatus.png");
            calculationsCorrect = toolkit.createImage(calcCorrectURL);
            URL calcGaveUpURL = Main.class.getResource("/amt/images/CalculationsGaveUpStatus.png");
            calculationsGaveUp = (toolkit.createImage(calcGaveUpURL));
            URL calcNoStatusURL = Main.class.getResource("/amt/images/CalculationsNoStatus.png");
            calculationsNoStatus = (toolkit.createImage(calcNoStatusURL));
            URL graphsWrongURL = Main.class.getResource("/amt/images/GraphsWrongStatus.png");
            graphsWrong = (toolkit.createImage(graphsWrongURL));
            URL graphsCorrectURL = Main.class.getResource("/amt/images/GraphsCorrectStatus.png");
            graphsCorrect = toolkit.createImage(graphsCorrectURL);
            URL graphsGaveUpURL = Main.class.getResource("/amt/images/GraphsGaveUpStatus.png");
            graphsGaveUp = (toolkit.createImage(graphsGaveUpURL));
            URL graphsNoStatusURL = Main.class.getResource("/amt/images/GraphsNoStatus.png");
            graphsNoStatus = (toolkit.createImage(graphsNoStatusURL));
            URL inputsWrongURL = Main.class.getResource("/amt/images/InputsWrongStatus.png");
            inputsWrong = (toolkit.createImage(inputsWrongURL));
            URL inputsCorrectURL = Main.class.getResource("/amt/images/InputsCorrectStatus.png");
            inputsCorrect = toolkit.createImage(inputsCorrectURL);
            URL inputsGaveUpURL = Main.class.getResource("/amt/images/InputsGaveUpStatus.png");
            inputsGaveUp = (toolkit.createImage(inputsGaveUpURL));
            URL inputsNoStatusURL = Main.class.getResource("/amt/images/InputsNoStatus.png");
            inputsNoStatus = (toolkit.createImage(inputsNoStatusURL));
        }
    }

    /**
     *
     * Pass the label to the button
     *
     * @param l is the text of the label
     */
    public void setButtomLabel(JLabel l) {
        this.buttomLabel = l;
    }

    /**
     * Set allEdges string as the label to the button
     *
     * @param s is the string to set the label to
     */
    public void setButtonLabel(String s) {
        this.buttomLabel.setText(s);
    }

    /**
     * Set whether the quiz has been opened
     */
    public void setQuizOpen(boolean o) {
        this.quizOpen = o;
    }

    /**
     * This method returns the cover
     *
     * @return cover
     */
    public Cover getCover() {
        return cover;
    }

    /**
     * Sets whether the model has been run
     *
     * @param runnable
     */
    public void setModelHasBeenRun(boolean runnable) {
        modelHasBeenRun = runnable;
    }

    /**
     * Returns whether the model can be run
     *
     * @return
     */
    public boolean getModelHasBeenRun() {
        return modelHasBeenRun;
    }

    public boolean getAllCorrect() {
        return allCorrect;
    }

    /**
     * This method returns the number of tabbedGUI windows open
     *
     * @return
     */
    public static LinkedList<TabbedGUI> getOpenTabs() {
        return openTabs;
    }

    /**
     * Method to set whether the model has been changed
     *
     * @param c is true if the model has been changed
     */
    public void setModelChanged(boolean c) {
        modelChanged = c;
    }

    /**
     * Method that returns whether the model has been changed
     *
     * @return whether the model has been changed
     */
    public boolean getModelChanged() {
        return modelChanged;
    }

    /**
     * this method returns whether the user has given up on the problem
     *
     * @return whether the user gives up
     */
    public boolean getGiveUp() {
        return giveUp;
    }

    /**
     * this method sets whether the user gives up on the problem
     *
     * @param g is whether the user gives up
     */
    public void setGiveUp(boolean g) {
        this.giveUp = g;
    }

    /**
     * this method returns whether the user continues
     *
     * @return whether the user continues
     */
    public boolean getContinues() {
        return continues;
    }

    /**
     * this method sets whether the user continues in the same level
     *
     * @param g is whether the user continues
     */
    public void setContinues(boolean g) {
        this.continues = g;
    }

    /**
     * this method returns whether the user passed the task and moves on to the
     * last level
     *
     * @return whether the user passes
     */
    public boolean getPassed() {
        return passed;
    }

    /**
     * this method sets whether the user has passed the current level
     *
     * @param p is whether the user passed
     */
    public void setPassed(boolean p) {
        this.passed = p;
    }

    /**
     * this method returns whether a practice problem is open
     *
     * @return whether a practice problem is open
     */
    public boolean getPractice() {
        return practice;
    }

    /**
     * this method sets whether a practice problem is open
     *
     * @param p is whether a practice problem is open
     */
    public void setPractice(boolean p) {
        this.practice = p;
    }

    /**
     * this method returns the current level
     *
     * @return the current level
     */
    public int getCurrentLevel() {
        return currentLevel;
    }

    /**
     * this method sets the current level
     *
     * @return the current level
     */
    public void setCurrentLevel(int g) {
        currentLevel = g;
    }

    /**
     * This method returns a list of each problem that has been completed in a
     * level. If the size of this list equals
     * problemList.get(currentLevel).length than we know that all problems
     * within a task has been completed. - Used for VERSION2
     *
     * @return
     */
    public LinkedList<Integer> getProblemsCompleted() {
        return problemsCompleted;
    }

    /**
     * This method sets whether the user received a point for the level
     *
     * @param point is true if the student received a point
     */
    public void setStudentReceivedLevelPoint(boolean point) {
        studentReceivedLevelPoint = point;
    }

    /**
     * This method increments the user's points
     *
     * @param point is whether the student received a point
     */
    public void setUserPoints(int point) {
        cover.getPointsAndLevel().setUserPoints(cover.getPointsAndLevel().getUserPoints() + point);
    }

    /**
     * Method to set all the Listeners on the frame
     */
    private final void initListen() {
        addFocusListener((FocusListener) this);
        addKeyListener((KeyListener) this);
        addMouseListener((MouseListener) this);
        addMouseMotionListener((MouseMotionListener) this);
        addComponentListener((ComponentListener) this);
    }

    /**
     * Method to fetch the task
     *
     * @param t is the task
     */
    public void updateTask(Task t) {
        task = t;
        getTaskInformation(task.getTitle(), task.getSummary());
    }

    /**
     * Method to fetch the screen size
     *
     * @return imageSize
     */
    public Dimension getImageSize() {
        return imageSize;
    }

    /**
     * This method paints the version 2 buttons over the vertex v
     *
     * @param g is the graphics
     * @param v is the vertex
     */
    public void paintMenu(Graphics g, Vertex v) {
        Point pos = v.position;
        int x = pos.x;
        int y = pos.y;
        //Paint inputs panel button
        if (v.getInputsButtonStatus() == v.NOSTATUS) {
            g.drawImage(inputsNoStatus, x + v.width / 2 - distance - iconWidth * 3 / 2, y + v.height / 2 - iconHeight / 2, iconWidth, iconHeight, this);
        } else if (v.getInputsButtonStatus() == v.CORRECT) {
            g.drawImage(inputsCorrect, x + v.width / 2 - distance - iconWidth * 3 / 2, y + v.height / 2 - iconHeight / 2, iconWidth, iconHeight, this);
        } else if (v.getInputsButtonStatus() == v.GAVEUP) {
            g.drawImage(inputsGaveUp, x + v.width / 2 - distance - iconWidth * 3 / 2, y + v.height / 2 - iconHeight / 2, iconWidth, iconHeight, this);
        } else if (v.getInputsButtonStatus() == v.WRONG) {
            g.drawImage(inputsWrong, x + v.width / 2 - distance - iconWidth * 3 / 2, y + v.height / 2 - iconHeight / 2, iconWidth, iconHeight, this);
        }
        //Paint calculations panel button
        if (v.getCalculationsButtonStatus() == v.NOSTATUS) {
            g.drawImage(calculationsNoStatus, x + v.width / 2 - iconWidth / 2, y + v.height / 2 - iconHeight / 2, iconWidth, iconHeight, this);
        } else if (v.getCalculationsButtonStatus() == v.CORRECT) {
            g.drawImage(calculationsCorrect, x + v.width / 2 - iconWidth / 2, y + v.height / 2 - iconHeight / 2, iconWidth, iconHeight, this);
        } else if (v.getCalculationsButtonStatus() == v.GAVEUP) {
            g.drawImage(calculationsGaveUp, x + v.width / 2 - iconWidth / 2, y + v.height / 2 - iconHeight / 2, iconWidth, iconHeight, this);
        } else if (v.getCalculationsButtonStatus() == v.WRONG) {
            g.drawImage(calculationsWrong, x + v.width / 2 - iconWidth / 2, y + v.height / 2 - iconHeight / 2, iconWidth, iconHeight, this);
        }
        //Paint graphs panel button
        if (v.getGraphsButtonStatus() == v.NOSTATUS) {
            g.drawImage(graphsNoStatus, x + v.width / 2 + iconWidth / 2 + distance, y + v.height / 2 - iconHeight / 2, iconWidth, iconHeight, this);
        } else if (v.getGraphsButtonStatus() == v.CORRECT) {
            g.drawImage(graphsCorrect, x + v.width / 2 + iconWidth / 2 + distance, y + v.height / 2 - iconHeight / 2, iconWidth, iconHeight, this);
        } else if (v.getGraphsButtonStatus() == v.GAVEUP) {
            g.drawImage(graphsGaveUp, x + v.width / 2 + iconWidth / 2 + distance, y + v.height / 2 - iconHeight / 2, iconWidth, iconHeight, this);
        } else if (v.getGraphsButtonStatus() == v.WRONG) {
            g.drawImage(graphsWrong, x + v.width / 2 + iconWidth / 2 + distance, y + v.height / 2 - iconHeight / 2, iconWidth, iconHeight, this);
        }
    }

    /**
     * Method to paint the images
     *
     * @param g
     */
    @Override
    public final void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image == null) {
            imageSize = getSize();
            image = createImage(imageSize.width, imageSize.height);
        }
        Graphics bg = image.getGraphics();
        paintParts(bg);

        g.drawImage(image, 0, 0, null);
        cover.paint(g);

        Graphics2D g2d = (Graphics2D) g;

        int componentWidth = this.getParent().getWidth();

        if (paintDescriptionText == false) {
            //do nothing
        } else {
            if (!taskLinesInfo.isEmpty()) {
                // the location of the x-coord of the rectangle if the first line is the longest
                int taskHeaderX = (int) descriptionPos.x - descriptionWidth - xBubbleOffset;
                // the location of the x-coord of the rectangle if any other line is longest
                int taskNormalX = (int) descriptionPos.x - descriptionWidth - xBubbleOffset;
                int yPlacement = (int) descriptionPos.y - descriptionHeight - yBubbleOffset;
                // background of text box
                g.setColor(Color.white);
                if (index == 0) {
                    g2d.fillRoundRect(taskHeaderX, yPlacement, headerFontMetrics.stringWidth(taskLinesInfo.get(index)) + xIndent / 2, headerFontMetrics.getHeight() * (taskLinesInfo.size() + 1), rectArc, rectArc);
                } else {
                    g2d.fillRoundRect(taskNormalX, yPlacement, normalFontMetrics.stringWidth(taskLinesInfo.get(index)) + xIndent / 2, normalFontMetrics.getHeight() * (taskLinesInfo.size() + 1), rectArc, rectArc);
                }

                // foreground of text area and text
                g2d.setColor(Color.black);
                if (index == 0) {
                    g2d.drawRoundRect(taskHeaderX, yPlacement, headerFontMetrics.stringWidth(taskLinesInfo.get(index)) + xIndent / 2, headerFontMetrics.getHeight() * (taskLinesInfo.size() + 1), rectArc, rectArc);
                    for (int i = 0; i < taskLinesInfo.size(); i++) {
                        if (i == 0) {
                            g.setFont(header);
                            g.drawString(taskLinesInfo.get(i), taskHeaderX + xIndent / 4, yPlacement + headerFontMetrics.getHeight());
                        } else {
                            g.setFont(normal);
                            g.drawString(taskLinesInfo.get(i), taskNormalX + xIndent / 4, yPlacement + normalFontMetrics.getHeight() * i + headerFontMetrics.getHeight());
                        }
                    }
                } else {
                    g2d.drawRoundRect(taskNormalX, yPlacement, normalFontMetrics.stringWidth(taskLinesInfo.get(index)) + xIndent / 2, normalFontMetrics.getHeight() * (taskLinesInfo.size() + 1), rectArc, rectArc);
                    for (int i = 0; i < taskLinesInfo.size(); i++) {
                        if (i == 0) {
                            g.setFont(header);
                            g.drawString(taskLinesInfo.get(i), taskHeaderX + xIndent / 4, yPlacement + headerFontMetrics.getHeight());
                        } else {
                            g.setFont(normal);
                            g.drawString(taskLinesInfo.get(i), taskNormalX + xIndent / 4, yPlacement + normalFontMetrics.getHeight() * i + headerFontMetrics.getHeight());
                        }
                    }
                }

                // this happens for any size task description bubble
                g.setColor(Color.white);
                int polygonXpts[] = {(int) descriptionPos.x + descriptionWidth - xBubbleOffset, (int) descriptionPos.x + descriptionWidth - xIndent / 2 - xBubbleOffset, (int) descriptionPos.x + descriptionWidth - xIndent / 2 - xBubbleOffset};
                int polygonYpts[] = {yPlacement + d / 4, yPlacement + d / 2, yPlacement + d / 4};
                g.fillPolygon(polygonXpts, polygonYpts, polygonXpts.length);
                g.setColor(Color.black);
                g.drawLine((int) descriptionPos.x + descriptionWidth - xBubbleOffset, yPlacement + d / 4, (int) descriptionPos.x + descriptionWidth - xIndent / 2 - xBubbleOffset, yPlacement + d / 2);
                g.drawLine((int) descriptionPos.x + descriptionWidth - xBubbleOffset, yPlacement + d / 4, (int) descriptionPos.x + descriptionWidth - xIndent / 2 - xBubbleOffset, yPlacement + d / 4);
            }
        }

        if (enableMenu == true && menuVertex != new Vertex() && menuVertex != null) {
            Point pos = menuVertex.position;
            int x = pos.x;
            int y = pos.y;

            //if(VERSIONID.equals("112"))
            if (professorVersion != Main.VERSION2 && professorVersion != Main.DEMO_VERSION2) {
                //find displacement for icons
                g.setColor(Color.white);
                if ((menuVertex.equation == null) || (menuVertex.equation.getIsCorrect() == false) || (menuVertex.getAlreadyRun() == false) || modelChanged == true) {
                    g.setColor(Color.lightGray);
                    g.fillRect(x + menuVertex.width / 2 - distance + widthDif - borderWidth * 3 / 2, y + menuVertex.height / 2 - borderHeight / 2, borderWidth, borderHeight);
                    g.drawImage(graphDisabledImage, x + menuVertex.width / 2 - distance - iconWidth * 3 / 2, y + menuVertex.height / 2 - iconHeight / 2, iconWidth, iconHeight, this);
                } else {
                    g.setColor(Color.white);
                    g.fillRect(x + menuVertex.width / 2 - distance + widthDif - borderWidth * 3 / 2, y + menuVertex.height / 2 - borderHeight / 2, borderWidth, borderHeight);
                    g.drawImage(graphImage, x + menuVertex.width / 2 - distance - iconWidth * 3 / 2, y + menuVertex.height / 2 - iconHeight / 2, iconWidth, iconHeight, this);
                }
                if (menuVertex.type.equals("none")) {
                    g.setColor(Color.lightGray);
                    g.fillRect(x + menuVertex.width / 2 + borderWidth / 2 + distance - widthDif, y + menuVertex.height / 2 - borderHeight / 2, borderWidth, borderHeight);
                    g.drawImage(equationDisabledImage, x + menuVertex.width / 2 + iconWidth / 2 + distance, y + menuVertex.height / 2 - iconHeight / 2, iconWidth, iconHeight, this);

                    g.setColor(Color.lightGray);
                    g.fillRect(x + menuVertex.width / 2 - borderWidth / 2, y + menuVertex.height / 2 - borderHeight / 2, borderWidth, borderHeight);
                    g.drawImage(edgeImage, x + menuVertex.width / 2 - iconWidth / 2, y + menuVertex.height / 2 - iconHeight / 2, iconWidth, iconHeight, this);
                    enableEdge = false;
                } else {
                    g.setColor(Color.white);
                    g.fillRect(x + menuVertex.width / 2 + borderWidth / 2 + distance - widthDif, y + menuVertex.height / 2 - borderHeight / 2, borderWidth, borderHeight);
                    g.drawImage(equationImage, x + menuVertex.width / 2 + iconWidth / 2 + distance, y + menuVertex.height / 2 - iconHeight / 2, iconWidth, iconHeight, this);

                    g.setColor(Color.white);
                    g.fillRect(x + menuVertex.width / 2 - borderWidth / 2, y + menuVertex.height / 2 - borderHeight / 2, borderWidth, borderHeight);
                    g.drawImage(edgeImage, x + menuVertex.width / 2 - iconWidth / 2, y + menuVertex.height / 2 - iconHeight / 2, iconWidth, iconHeight, this);
                }
                g.setColor(Color.black);
                g2d.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
                g2d.drawRect(x + menuVertex.width / 2 - distance + widthDif - borderWidth * 3 / 2, y + menuVertex.height / 2 - borderHeight / 2, borderWidth, borderHeight);
                g2d.drawRect(x + menuVertex.width / 2 - borderWidth / 2, y + menuVertex.height / 2 - borderHeight / 2, borderWidth, borderHeight);
                g2d.drawRect(x + menuVertex.width / 2 + borderWidth / 2 + distance - widthDif, y + menuVertex.height / 2 - borderHeight / 2, borderWidth, borderHeight);
            }
        }

        boolean complete = false;
        //determine if the user has passed a level
        if (passed == true) {
            //increment points if they were earned
            if (!studentReceivedLevelPoint && Integer.parseInt(server.getActualTask().getLevel()) > 0 && practice == false) {
                cover.getPointsAndLevel().setUserPoints(cover.getPointsAndLevel().getUserPoints() + currentLevelPoints);
                studentReceivedLevelPoint = true;
            }
            currentLevel = Integer.parseInt(server.getActualTask().getLevel());
            currentLevel += 1;
            for (int i = 0; i < problemList.size(); i++) {
                if (currentLevel < i && problemList.get(i) != null) {
                    currentLevel = i;
                    if (Main.professorVersion != Main.VERSION2 && Main.professorVersion != Main.DEMO_VERSION2) {
                        cover.getPointsAndLevel().setCurrentLevel(currentLevel);
                    }
                    passed = false;
                    break;
                } else if (currentLevel == problemList.size() - 1) {
                    passed = false;
                    complete = true;
                    CongratulationsDialog congrats = new CongratulationsDialog(frame, true);
                    congrats.setVisible(true);
                    congrats.setAlwaysOnTop(true);
                    break;
                }
            }
            if (!complete) {
                loadLevel(problemList.get(currentLevel)[0]);
                if (Main.professorVersion != Main.VERSION2 && Main.professorVersion != Main.DEMO_VERSION2) {
                    cover.getPointsAndLevel().setCurrentLevel(currentLevel);
                }
            }
            logger.concatOut(Logger.ACTIVITY, "MenuBar.doneButtonActionPerformed.1", Integer.toString(problemList.get(currentLevel)[0]));
        } else if (continues == true) {
            loadNextTask();
            continues = false;
        }
        //determine whether the model can be run
        if (canRun() == true) {
            //run.setEnabled(true);
            run.setForeground(Color.BLACK);
        } else {
            //run.setEnabled(false);
            run.setForeground(Color.GRAY);
            //takeQuiz.setEnabled(false);
            takeQuiz.setForeground(Color.GRAY);
        }

        //change color of run and take quiz buttons
        if (run.getForeground() == Color.GRAY) {
            runButton.setForeground(Color.GRAY);
        } else {
            runButton.setForeground(Color.BLACK);
        }

        if (takeQuiz.getForeground() == Color.GRAY) {
            takeQuizButton.setForeground(Color.GRAY);
        } else {
            takeQuizButton.setForeground(Color.BLACK);
        }

        //Version 2 addition
        if (Main.professorVersion == Main.VERSION2 || Main.professorVersion == Main.DEMO_VERSION2) {
            for (int i = 0; i < graph.getVertexes().size(); i++) {
                paintMenu(g, (Vertex) graph.getVertexes().get(i));
            }
        }
    }

    public void loadLevel(int taskNum) {
        int problemsCompletedSize = problemsCompleted.size();

        for (int i = 0; i < problemsCompletedSize; i++) {
            problemsCompleted.remove();
        }

        for (int i = 0; i < problemList.size(); i++) {
            if (problemList.get(i) != null) {
                for (int j = 0; j < problemList.get(i).length; j++) {
                    if (taskNum == problemList.get(i)[j]) {
                        problemsCompleted.add(j);
                    }
                }
            }
        }

        closeWindows();
        //Do not show the score panel when loading the first level
        cover.getMenuBar().setUsedHint(false);
        paintDescriptionText = false;
        studentReceivedLevelPoint = false;


        if (Integer.parseInt(server.getActualTask().getLevel()) > 0) {
            currentLevelPoints = 1;
        } else {
            currentLevelPoints = 0;
        }
        practice = true;
        tabPane.setSelectedIndex(1);

        this.deleteAll();
        //cover.getMenuBar().vertexCount=0;

        int id = taskNum;
        try {
            task = server.getTasks(id);
        } catch (DataException de) {
            // do something
        }

        if (task != null) {
            //if(VERSIONID.equals("112"))
            if (professorVersion != Main.VERSION2 && professorVersion != Main.DEMO_VERSION2) {
                currentLevel = Integer.parseInt(server.getActualTask().getLevel());
                cover.getPointsAndLevel().setCurrentLevel(currentLevel);
            } else {
                currentLevel = Integer.parseInt(server.getActualTask().getLevel());
                currentLevel += 1;
            }
            taskView.updateTask(task);
            instructionView.updateInstruction(task);
            this.updateTask(server.getActualTask());
            //Gets the label of the vertexes for the selected task.
            listOfVertexes = server.getActualTask().getVertexNames();
            // Show the vertex of the problem in shuffled order

            //Gets the label of the extranodes for the selected task.
            extraNodes = server.getActualTask().getExtraNodes();


            //System.out.println(task.listOfVertexes.size()+"..."+listOfVertexes.size()+"..."+graph.getVertexes().size());           
            if (task.listOfVertexes != null && task.listOfVertexes.size() == listOfVertexes.size()) {
                cover.getMenuBar().getNewNodeButton().setEnabled(false);
            } else {
                cover.getMenuBar().getNewNodeButton().setEnabled(true);
            }



///Deleted amd moved to MenuBar


            // Change the title of the window to include the name of the current task
            if (server.getActualTask().getTitle() == null) {
                this.setFont(this.normal);
                frame.setTitle("Affective Meta Tutor");
            } else {
                this.setFont(this.normal);
                frame.setTitle("Affective Meta Tutor - " + server.getActualTask().getTitle());
            }

//            //Show the avatar for 5 seconds with a message
//            if(avatarList.isEmpty() == false && currentLevel != -1)
//            {
//                avatarList.get(0).setVisible(true);
//                avatarList.get(0).setMessage("Congratulations! You  have made it to the next level!");
//                avatarList.get(0).setTimer(5);
//            }
        }
        problemIndex = 0;
        run.setForeground(Color.GRAY);
    }

    private void closeWindows() {
        //close equationEditor
        if (ee != null) {
            for (int i = 0; i < ee.size(); i++) {
                ee.get(i).dispose();
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

        takeQuiz.setForeground(Color.GRAY);
    }

    private void loadNextTask() {
        closeWindows();
        cover.getMenuBar().setUsedHint(false);
        paintDescriptionText = false;
        //the following determines whether the level is practice or not
        if (Integer.parseInt(server.getActualTask().getLevel()) > 0) {
            currentLevelPoints = 1;
            practice = false;
        } else {
            practice = true;
            currentLevelPoints = 0;
        }
        tabPane.setSelectedIndex(1);

        if (professorVersion != Main.VERSION2 && professorVersion != Main.DEMO_VERSION2) {
            currentLevel = Integer.parseInt(server.getActualTask().getLevel());
            currentLevel += 1; //This is to prevent errors when we have level -1
            problemIndex++;
        } else {
            // If there is more than one task in the same level, choose randomly the next task

            boolean validRandomNum;

            do {
                problemIndex = rand.nextInt(problemList.get(currentLevel).length);
                validRandomNum = true;
                for (int i = 0; i < problemsCompleted.size(); i++) {
                    if (problemIndex == problemsCompleted.get(i)) {
                        validRandomNum = false;
                    }
                }
            } while (!validRandomNum);
        }

        //if problem index is greater than the number of indices in the level, start over
        if (problemIndex > problemList.get(currentLevel).length - 1) {
            problemIndex = 0;
        }

        Vertex v = new Vertex();
        this.deleteAll();
        int id = problemList.get(currentLevel)[problemIndex];

        problemsCompleted.add(problemIndex);

        try {
            task = server.getTasks(id);
        } catch (DataException de) {
            //do something
        }

        if (task != null) {
            if (Main.professorVersion != Main.VERSION2 && Main.professorVersion != Main.DEMO_VERSION2) {
                cover.getPointsAndLevel().setCurrentLevel(currentLevel);
            }
            taskView.updateTask(task);
            this.updateTask(server.getActualTask());
            //Gets the label of the vertexes for the selected task.
            listOfVertexes = server.getActualTask().getVertexNames();
            // Show the vertex of the problem in shuffled order

            /*
             * commented out by zpwn: we do not need in New Node feature int[]
             * indices = suffledIndexes(listOfVertexes); int height =
             * this.getParent().getHeight(); this.setFont(this.normal); for (int
             * i = 0; i < listOfVertexes.size(); i++) { if(Math.floor(i/6) > 0)
             * { this.newVertex(100 + i%6 * 125, height -
             * (int)(v.paintNoneHeight * 2 * (Math.floor(i/6) + 1)),
             * listOfVertexes.get(indices[i])); } else this.newVertex(100 + i *
             * 125, height - v.paintNoneHeight * 2,
             * listOfVertexes.get(indices[i])); }
             *
             */
            // Change the title of the window to include the name of the current task
            if (server.getActualTask().getTitle() == null) {
                this.setFont(this.normal);
                frame.setTitle("Affective Meta Tutor");
            } else {
                this.setFont(this.normal);
                frame.setTitle("Affective Meta Tutor - " + server.getActualTask().getTitle());
            }
        }
        run.setForeground(Color.GRAY);
        logger.concatOut(Logger.ACTIVITY, "MenuBar.doneButtonActionPerformed.1", Integer.toString(id));
    }

    /**
     * Return an array with the indexes of the vertex shuffled
     *
     * @return
     */
    public int[] suffledIndexes(LinkedList<String> listOfVertexes) {
        int tempIndex = 0;
        int tempHolder = 0;
        Random generator = new Random();
        //loaded is a list of which vertexes have already been loaded
        int indices[] = new int[listOfVertexes.size()];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = i;
        }
        //shuffle the numbers around using Fisher-Yates shuffle
        if (indices.length > 1) {
            for (int i = indices.length - 1; i >= 0; i--) {
                tempIndex = generator.nextInt(indices.length - 1);
                tempHolder = indices[tempIndex];
                indices[tempIndex] = indices[i];
                indices[i] = tempHolder;
            }
        }
        return indices;
    }

    public void runModelFromDebug() {
        try {
            System.out.println("CURT: type is debug");
            graph.run(TaskFactory.getInstance(), this);
            //MessageDialog.showMessageDialog(null, true, "Model run complete!", graph);
            Vertex current;
            boolean allRight = true;

            System.out.println("CURT: #ofVertices: " + graph.getVertexes().size());
            for (int i = 0; i < graph.getVertexes().size(); i++) {
                current = (Vertex) graph.getVertexes().get(i);
                System.out.println("CURT: " + current.nodeName);
                if (!current.equation.value.isEmpty() && !current.correctValues.isEmpty()) {
                    for (int j = 0; j < current.equation.value.size(); j++) {
                        if (Double.compare(current.equation.value.get(j), current.correctValues.get(j)) == 0) {
                            allRight = true;
                        } else {
                            allRight = false;
                        }
                    }
                } else {
                    allRight = false;
                }
                //Set whether the graphs panel is correct
                if (allRight) {
                    current.setGraphsButtonStatus(current.CORRECT);
                    logger.concatOut(Logger.ACTIVITY, "No message", "The color of the graph for the node--" + current.label + " is: green");
                } else {
                    current.setGraphsButtonStatus(current.WRONG);
                    logger.concatOut(Logger.ACTIVITY, "No message", "The color of the graph for the node--" + current.label + " is: red");
                }
            }
            logger.concatOut(Logger.ACTIVITY, "No message", "All the node's information has been sent.");

            repaint(0);
        } catch (CommException ce) {
            // catch exception
        }
    }

    public boolean canRun() {
        Vertex v;
        int noneCount = 0; //counts the vertices that fail the runnable test
        boolean runnable = false;
        int n = graph.getVertexes().size();
        Object a[] = graph.getVertexes().toArray();
        for (int j = 0; j < n; j++) {

            v = (Vertex) a[j];
            if (v.getEditorOpen() == false && !v.type.equalsIgnoreCase("none") && !v.type.equalsIgnoreCase("constant") /*
                     * && v.equation != null
                     */) {
                // do nothing
            } else if (v.type.equalsIgnoreCase("constant") || v.type.equalsIgnoreCase("stock")) {
                if (v.initialValueGiven == true) {
                    // do nothing
                } else {
                    noneCount++;
                }
            } else {
                noneCount++;
                break;
            }
        }

        if (noneCount > 0/*
                 * noneCount == graph.getVertexes().size()
                 */) {
            runnable = false;
        } else if (graph.getVertexes().size() == 0) {
            runnable = false;
        } else {
            runnable = true;
        }
        a = null;
        return runnable;
    }

    /**
     * This method returns true of the description text box is hit
     *
     * @param e is the mouse event
     * @return is true if the user clicked inside the description text box
     */
    public boolean hitDescription(MouseEvent e) {
        if (paintDescriptionText == false) {
            //do nothing
            hitDescrip = false;
        } else {
            int x = e.getX();
            int y = e.getY();
            int centX = descriptionPos.x - xBubbleOffset;
            int centY = descriptionPos.y;

            //four points that are rectArc distance away from each corner
            Point p1 = new Point(centX - descriptionWidth + rectArc, centY - descriptionHeight + rectArc);
            Point p2 = new Point(centX - descriptionWidth + rectArc, centY + descriptionHeight - rectArc);
            Point p3 = new Point(centX + descriptionWidth - rectArc, centY - descriptionHeight + rectArc);
            Point p4 = new Point(centX + descriptionWidth - rectArc, centY + descriptionHeight - rectArc);

            if (((x <= centX + descriptionWidth - rectArc && x >= centX - descriptionWidth + rectArc)
                    && (y <= centY + descriptionHeight && y >= centY - descriptionHeight))
                    || ((x <= centX + descriptionWidth && x >= centX - descriptionWidth)
                    && (y <= centY + descriptionHeight - rectArc && y >= centY - descriptionHeight + rectArc))
                    || (e.getPoint().distance(p1) <= rectArc) || (e.getPoint().distance(p2) <= rectArc)
                    || (e.getPoint().distance(p3) <= rectArc) || (e.getPoint().distance(p4) <= rectArc)) {
                hitDescrip = true;
            } else if (hitDescrip == true) {
                //do nothing
            } else {
                hitDescrip = false;
            }
        }
        return hitDescrip;
    }

    /**
     * This method separates the description string into separate lines
     *
     * @param title is the title of the task
     * @param description is the description of the task
     */
    public void getTaskInformation(String title, String description) {
        char currentChar;
        //this is the task description for the info icon
        taskLinesInfo = new LinkedList<String>();
        String tempInfo = "";
        String tempHolder = "";
        int maxChars = 75;
        int numberChars = 0;
        taskLinesInfo.add(title);
        for (int i = 0; i < description.length(); i++) {
            currentChar = description.charAt(i);
            if (numberChars < maxChars && currentChar != '\\') {
                if (currentChar == ' ') {
                    tempHolder += currentChar;
                    tempInfo += tempHolder;
                    tempHolder = "";
                    numberChars++;
                } else {
                    tempHolder += currentChar;
                    numberChars++;
                }
            } else if (currentChar == '\\') {
                i++;
                if (i < description.length()) {
                    currentChar = description.charAt(i);
                    if (currentChar == 'n') {
                        tempInfo += tempHolder;
                        taskLinesInfo.add(tempInfo);
                        tempHolder = "";
                        tempInfo = "";
                        numberChars = 0;
                    }
                } else {
                    tempHolder += currentChar;
                    numberChars++;
                }
            } else {
                //add the full line
                taskLinesInfo.add(tempInfo);
                tempHolder += currentChar;
                tempInfo = "";
                numberChars = 0;
            }
        }
        tempInfo += tempHolder;
        taskLinesInfo.add(tempInfo);
        //get the initial position of the description center
        int posX = 0;
        int posY = 0;
        int maxWidth = 0;
        int otherWidth = 0;
        int componentWidth;
        if (this.getParent() != null) {
            componentWidth = this.getParent().getWidth();
        } else {
            componentWidth = (int) frame.getToolkit().getDefaultToolkit().getScreenSize().getWidth();
        }

        //find the longest line
        if (!taskLinesInfo.isEmpty()) {
            otherWidth = headerFontMetrics.stringWidth(taskLinesInfo.get(0));
            if (otherWidth > maxWidth) {
                maxWidth = otherWidth;
                index = 0;
            }
            for (int j = 1; j < taskLinesInfo.size(); j++) {
                otherWidth = normalFontMetrics.stringWidth(taskLinesInfo.get(j));
                if (otherWidth > maxWidth) {
                    maxWidth = otherWidth;
                    index = j;
                }
            }
        }
        // the location of the x-coord of the rectangle if the first line is the longest
        int taskHeaderX = componentWidth - descriptionIndent - (headerFontMetrics.stringWidth(taskLinesInfo.get(index))) - xIndent / 2;
        // the location of the x-coord of the rectangle if any other line is longest
        int taskNormalX = componentWidth - descriptionIndent - (normalFontMetrics.stringWidth(taskLinesInfo.get(index))) - xIndent / 2;
        if (index == 0) {
            posX = taskHeaderX + (headerFontMetrics.stringWidth(taskLinesInfo.get(index))) / 2 + xIndent / 2;
            posY = yIndent + (headerFontMetrics.getHeight() * (taskLinesInfo.size() + 1)) / 2 - yBubbleOffset;
            descriptionWidth = posX - taskHeaderX;
            descriptionHeight = posY - yIndent;
            descriptionPos = new Point(posX, posY);
        } else {
            posX = taskNormalX + (normalFontMetrics.stringWidth(taskLinesInfo.get(index))) / 2 + xIndent / 2;
            posY = yIndent + (normalFontMetrics.getHeight() * (taskLinesInfo.size() + 1)) / 2 - yBubbleOffset;
            descriptionWidth = posX - taskNormalX;
            descriptionHeight = posY - yIndent;
            descriptionPos = new Point(posX, posY);
        }
    }

    /**
     * Method to paint the parts of the graph.
     *
     * @param g
     */
    private final void paintParts(Graphics g) {
        g.setColor(Color.white);
        g.fillRect(0, 0, imageSize.width, imageSize.height);
        g.setColor(new Color(230, 230, 230));
        for (int j = 0; j < imageSize.width; j += 10) {
            g.drawLine(j, 0, j, imageSize.height);
        }
        for (int i = 0; i < imageSize.height; i += 10) {
            g.drawLine(0, i, imageSize.width, i);
        }
        graph.paint(g);
    }

    /**
     * Method to refresh the graph when the window gets the focus
     *
     * @param e
     */
    @Override
    public void focusGained(FocusEvent e) {
        repaint(0);
    }

    public void setMenuOpen(boolean o) {
        this.menuOpen = o;
    }

    /**
     * Method to implement actions if the mouse is dragged. Notice that now it
     * does not do anything
     *
     * @param e MouseEvent
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        if (y < 0) {
            y = 0;
        }
        if (x < 0) {
            x = 0;
        }
        if (moveAllDrag(x, y, e)) {
            return;
        }
        if (mouseDraggedDescription(x, y, e)) {
            return;
        }
        if (mouseDraggedAvatar(x, y, e)) {
            return;
        }
        if (mouseDraggedVertex(x, y, e) && !mouseDraggedDescription(x, y, e)) {
            return;
        }
        if (mouseDraggedEdge(x, y, e) && !mouseDraggedDescription(x, y, e)) {
            return;
        }
    }

    /**
     * Method to drag the avatar
     *
     * @param x Mouse x position
     * @param y Mouse y position
     * @param e Mouse event
     */
    public final boolean mouseDraggedAvatar(int x, int y, MouseEvent e) {
        if (graph.getSelected() instanceof Avatar) {
            Avatar a = (Avatar) (graph.getSelected());
            a.move(x, y);
            repaint(0);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Method to handle the possibility that we are dragging allEdges Vertex or
     * allEdges Vertex label.
     *
     * @param x Mouse x position
     * @param y Mouse y position
     * @param e Mouse event
     */
    public final boolean mouseDraggedVertex(int x, int y, MouseEvent e) {
        if (graph.getSelected() instanceof Vertex) {
            Vertex v = (Vertex) (graph.getSelected());
            if (labelOffset == null) {
                v.move(x, y);
            } else {
                v.moveLabel(x - labelOffset.x, y - labelOffset.y);
            }
            repaint(0);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Method to handle the possibility that we are dragging the task
     * description.
     *
     * @param x Mouse x position
     * @param y Mouse y position
     * @param e Mouse event
     */
    public final boolean mouseDraggedDescription(int x, int y, MouseEvent e) {
        if (hitDescription(e) == true) {
            graph.unselect();
            descriptionPos.x = x + xBubbleOffset;
            descriptionPos.y = y;

            repaint(0);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Method to handle the possibility that we are dragging an Edge or an Edge
     * label.
     *
     * @param x Mouse x position
     * @param y Mouse y position
     * @param e Mouse event
     */
    public final boolean mouseDraggedEdge(int x, int y, MouseEvent e) {
        if (graph.getSelected() instanceof Edge) {
            Edge ed = (Edge) (graph.getSelected());
            Vertex v = ed.end;
            ed.start.isSelected = true;
            if (labelOffset == null) {
                if (!graph.getVertexes().contains(v)) // setCursor(Cursor.getPredefinedCursor(CROSSHAIR_CURSOR));
                {
                    v.move(x + v.width / 2, y + v.height / 2);
                } else {
                    return false;
                }
            } else {
                ed.moveLabel(x - labelOffset.x, y - labelOffset.y);
            }
            repaint(0);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Method to handle the actions when the mouse is out of the window.
     *
     * @param e
     */
    @Override
    public void mouseExited(MouseEvent e) {
        labelOffset = null;
        moveAllFrom = null;
    }

    /**
     * Method to verify if we are hitting the mouse over allEdges vertex.
     *
     * @param x Mouse x position
     * @param y Mouse y position
     * @return If exist the hit Vertex, null otherwise.
     */
    public final Vertex hitVertex(int x, int y) {
        Vertex v;
        int n = graph.getVertexes().size();
        Object a[] = graph.getVertexes().toArray();
        for (int j = 0; j < n; j++) {
            v = (Vertex) a[j];
            if (v.hit(x, y)) {
                return v;
            } else if (enableMenu && v.hitMenu(x, y, v.position.x, v.position.y)) {
                return v;
            }
        }
        return null;
    }

    /**
     * Method to verify if the mouse hits the avatar
     *
     * @param x Mouse x position
     * @param y Mouse y position
     * @return If exist the hit avatar, null otherwise.
     */
    public final Avatar hitAvatar(int x, int y) {
        Avatar a;
        int n = avatarList.size();
        for (int j = n - 1; j >= 0; j--) {
            a = avatarList.get(j);
            if (a.hit(x, y)) {
                return a;
            }
        }
        return null;
    }

    /**
     * Method to verify if we are hitting the mouse over allEdges curved link
     *
     * @param x Mouse x position
     * @param y Mouse y position
     * @return If exist the hitted curved edge, otherwise keep checking with
     * hitEdge
     */
    public final Edge hitCurvedEdge(int x, int y) {
        //log.out(LogType.DEBUG_LOCAL, "hitCurvedEdge");
        Object e[] = graph.getEdges().toArray();
        Edge edge;
        for (int i = 0; i < graph.getEdges().size(); i++) {
            edge = (Edge) e[i];
            if (edge.contains(x, y)) {
                return edge;
            }
        }
        e = null;
        //The edge is not curved, but it may be straight,
        //Keep checking with hitEdge method and return
        //whatever found in hitEdge method
        //TODO: Use similar methodology to check non-curved link as well
        return hitEdge(x, y);
    }

    /**
     * Method to verify if we are hiting the mouse over an edge.
     *
     * @param x Mouse x position
     * @param y Mouse y position
     * @return If exist the hited Edge, null otherwise.
     */
    public final Edge hitEdge(int x, int y) {
        System.out.println("Entra hitEdge");

        Edge e;
        // Review all the edges
        for (int j = 0; j < graph.getEdges().size(); j++) {
            e = (Edge) graph.getEdges().toArray()[j];
            if (e.near(x, y, 5)) {
                return (Edge) e;
            }

        }
        return null;
    }

    /**
     * Method to verify if we are hiting the mouse over the label of allEdges
     * vertex.
     *
     * @param x Mouse x position
     * @param y Mouse y position
     * @return If exist the hited Vertex, null otherwise.
     */
    public final Vertex hitVertexLabel(int x, int y) {
        Vertex v;
        int n = graph.getVertexes().size();
        Object a[] = graph.getVertexes().toArray();
        for (int j = 0; j < n; j++) {
            v = (Vertex) a[j];
            if (v.hitLabel(x, y)) {
                return v;
            }
        }
        a = null;
        return null;
    }

    /**
     * Method to verify if we are hiting the mouse over the label of an edge.
     *
     * @param x Mouse x position
     * @param y Mouse y position
     * @return If exist the hited Edge, null otherwise.
     */
    public final Edge hitEdgeLabel(int x, int y) {
        Edge e;
        int n = graph.getEdges().size();
        Object a[] = graph.getEdges().toArray();
        for (int j = 0; j < n; j++) {
            e = (Edge) a[j];
            if (e.hitLabel(x, y)) {
                return e;
            }
        }
        return null;
    }

    /**
     * Make v be the selected item.
     *
     * @param the item to set as selected
     */
    private final void select(Selectable v) {
        graph.select(v);
    }

    /**
     * Method to verify that allEdges Edge was pressed.
     *
     * @param ed the Edge
     * @param e mouse events
     * @return true if the mouse was pressed on the edge, false otherwise.
     */
    public boolean pressedOnEdge(Edge ed, MouseEvent e) {
        //log.out(LogType.DEBUG_LOCAL, "pressedOnEdge");
        if (ed == null) {
            return false;
        }
        // int x = e.getX();
        // int y = e.getY();
        // if (ed.nearStart(x,y)) ed.toggleStart();
        // if (ed.nearEnd(x,y)) ed.toggleEnd();
        select(ed);
        // System.out.println("repaint by pressedOnEdge");
        repaint(0);
        return true;
    }

    /**
     * Method to process that allEdges Vertex was pressed.
     *
     * @param v the Vertex
     * @param e mouse event
     * @return true if the mouse was pressed on the vertex, false otherwise.
     */
    public final boolean pressedOnAvatar(Avatar a, MouseEvent e) {
        if (a == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Method to process that allEdges Vertex was pressed.
     *
     * @param v the Vertex
     * @param e mouse event
     * @return true if the mouse was pressed on the vertex, false otherwise.
     */
    public final boolean pressedOnVertex(Vertex v, MouseEvent e) {
        if (v == null) {
            return false;
        }
        if (v.equals(graph.getSelected()) && e.getButton() != MouseEvent.BUTTON3) {
            //if(VERSIONID.equals("112"))
            if (professorVersion != Main.VERSION2 && professorVersion != Main.DEMO_VERSION2) {
                if (enableEdge == true) {
                    // Start edge only with click left over the edge button
                    Vertex v2 = new Vertex(e.getX(), e.getY());
                    v2.size = 1;
                    Edge ed = graph.addEdge(v, v2);
                    // System.out.println("NUEVO EDGE ENTRE v1: " + v.label + "y v2: " + v2.label);
                    select(ed);
                } else if (enableEdgeError == true) {
                    if (edgeErrorDisplayed == false) {
                        MessageDialog.showMessageDialog(frame, true, "Use the right mouse button or double click to select a node type", graph);
                        edgeErrorDisplayed = true;
                    } else {
                        edgeErrorDisplayed = false;
                    }
                }
            } else {
                // Just select the node
                v.alter();
                v.isSelected = true;
            }
            return true;
        } //ELSE IF THE VERSIONID = 2
        else {
            v.alter();
            v.isSelected = true;
            return true;
        }
    }

    /**
     * Method to process that allEdges Vertex was pressed.
     *
     * @param v the Vertex
     * @param e mouse event
     * @return true if the mouse was pressed on the vertex, false otherwise.
     */
    public final boolean movedOnVertex(Vertex v, MouseEvent e) {
        if (v == null) {
            enableMenu = false;
            return false;
        } else if (v.hitMenu(e.getX(), e.getY(), v.position.x, v.position.y) == true) {
            enableMenu = true;
            select(v);
            return true;
        } else {
            enableMenu = true;
            select(v);
            return true;
        }
    }

    /**
     * This method is used to handle mouse press event.
     *
     * @param e Mouse event
     */
    @Override
    public void mousePressed(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        //if(VERSIONID == "112")
        if (professorVersion != Main.VERSION2 && professorVersion != Main.DEMO_VERSION2) {
            if (professorVersion == Main.PROFESSORVERSION && pressedOnAvatar(hitAvatar(x, y), e)) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    graph.unselect();
                    Avatar a = hitAvatar(x, y);
                    graph.select(a);
                    PopUpAvatarMenu avatarMenu = new PopUpAvatarMenu(graph.getSelected());
                    add(avatarMenu);
                    avatarMenu.show(this, x, y);
                } else if (e.getButton() == MouseEvent.BUTTON1) {
                    graph.unselect();
                    Avatar a = hitAvatar(x, y);
                    graph.select(a);
                }
            } else if (pressedOnVertex(hitVertex(x, y), e)) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    menuOpen = false;
                    graph.unselect();
                    //unselect all vertexes
                    int n = graph.getVertexes().size();
                    Object a[] = graph.getVertexes().toArray();
                    for (int j = 0; j < n; j++) {
                        ((Vertex) a[j]).isSelected = false;
                    }
                    if (menuOpenVertex != null) {
                        menuOpenVertex.isSelected = false;
                    }
                    Vertex v = hitVertex(x, y);
                    graph.select(v);
                    PopupCanvasMenu pcMenu = new PopupCanvasMenu(graph.getSelected(), this, graph, task, run, takeQuiz, frame);
                    logs.concatOut(Logger.ACTIVITY, "GraphCanvas.mousePressed.1", v.label);
                    menuOpen = true;
                    menuOpenVertex = v;
                    //System.out.println("menuOpenVertex in mousepressed " + menuOpenVertex);
                    //pcMenu.setBorder(javax.swing.BorderFactory.createTitledBorder(v.label));
                    //pcMenu.setBorder(new CompoundBorder(new TitledBorder(v.label), new EmptyBorder(10, 10, 10, 10)));

                    add(pcMenu);
                    v.isSelected = true;
                    //        System.out.println("v in mousePressed is " + v.label);
                    pcMenu.show(this, e.getX(), e.getY());
                } else if (e.getButton() == MouseEvent.BUTTON1) {
                    if (pressedOnVertex(hitVertex(x, y), e)) {
                        if (graph.getSelected() instanceof Vertex) {
                            Vertex v = (Vertex) graph.getSelected();
                            v.isSelected = true;
                        }
                        menuOpen = false;
                        if (menuOpenVertex != null) {
                            menuOpenVertex.isSelected = false;
                        }
                    }
                }
            } else if (pressedOnEdge(hitCurvedEdge(x, y), e)) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    Edge ed = (Edge) graph.getSelected();
                    PopupEdgeMenu peMenu = new PopupEdgeMenu(graph.getSelected(), this, frame, graph, run, takeQuiz);
                    add(peMenu);
                    ed.isSelected = true;
                    peMenu.show(this, e.getX(), e.getY());
                }
            } else {
                if (moveAllStart(x, y, e)) {
                    return;
                }
                changeShape = false;

                //The following prevents the user from being able to draw edges to nothing
                if (graph.getSelected() != null) {
                    if (graph.getSelected() instanceof Edge) {
                        //do nothing
                    } else {
                        select(null);
                    }
                } else {
                    select(null);
                }
                // JGS: clean selection
                //select(null);
            }
        }
    }

    /**
     * @param x is the x-coordinate where the whole graph would be repainted
     * @param y is the y-coordinate where the whole graph would be repainted
     * @param e is the object with the mouse event
     * @return true if it was possible to move the whole graph
     */
    public boolean moveAllStart(int x, int y, MouseEvent e) {
        if (0 != (e.getModifiers() & MouseEvent.CTRL_MASK)) {
            moveAllFrom = new Point(x, y);
            // setCursor(Cursor.getPredefinedCursor(HAND_CURSOR));
            return true;
        } else {
            return false;
        }
    }

    /**
     * Method to handle the continuous movement of the entire graph
     *
     * @param x is the new x-coordinate
     * @param y is the new y-coordinate
     * @param e
     * @return true if the graph was moved
     */
    public boolean moveAllDrag(int x, int y, MouseEvent e) {
        if (moveAllFrom == null) {
            return false;
        }
        graph.moveRelative(x - moveAllFrom.x, y - moveAllFrom.y);
        moveAllFrom.x = x;
        moveAllFrom.y = y;
        // System.out.println("repaint by moveAllDrag");
        repaint(0);
        return true;
    }

    /**
     * Method to finish doing the movement of allEdges graph
     */
    public void moveAllEnd() {
        moveAllFrom = null;
        // setCursor(Cursor.getPredefinedCursor(DEFAULT_CURSOR));
    }

    /**
     * Method to handle the actions when the mouse is released
     *
     * @param e
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        //log.out(LogType.DEBUG_LOCAL, "mouseReleased begin");
        Vertex v = hitVertex(e.getX(), e.getY());
        enableEdge = false;
        hitDescrip = false;
        //Modified. by Patrick
        if (labelOffset != null) {
            mouseDragged(e);
            labelOffset = null;
        }
        if (moveAllFrom != null) {
            mouseDragged(e);
            moveAllEnd();
            return;
        }
        if (graph.getSelected() instanceof Vertex) {
            //if(VERSIONID.equals("112"))
            if (professorVersion != Main.VERSION2 && professorVersion != Main.DEMO_VERSION2) {
                logs.concatOut(Logger.ACTIVITY, "GraphCanvas.mouseReleased.1", v.label);
            }
        }
        if (graph.getSelected() instanceof Edge) {
            Edge ed = (Edge) (graph.getSelected());
            if ((v != null) && (v != ed.start)) {
                //log.out(LogType.ACTIVITY, "Create edge:" + ed.edgetype);
                //log.out(LogType.ACTIVITY, "Create edge between:" + ed.start.label + " and " + ed.end.label);
                ed.end = v;
                //logs.out(ACTIVITY, "GraphCanvas Create "+ ed.edgetype +" edge between:" + ed.start.label + " and " + ed.end.label);
                //Added by Quanwei
                this.buttomLabel.setText("");
                if (validate(ed)) {
                    ed.edgetype = ed.edgeType(ed.start, v);
                    //We add the edge in both vertexes start and end, as output and input respectively
                    ed.start.addOutEdge(ed);
                    //We don't need to delete the equation of the starting vertex
                    v.addInEdge(ed);
                    if (!v.type.equals("stock") && !ed.edgetype.equalsIgnoreCase("flowlink")) {
                        v.delEquation();
                    }
                    quiz = false;
                    setModelChanged(true);
                    run.setForeground(Color.GRAY);
                    takeQuiz.setForeground(Color.GRAY);
                    if (menuOpenVertex != null) {
                        menuOpenVertex.isSelected = false;
                    }
                    menuVertex.isSelected = false;
                    logs.concatOut(Logger.ACTIVITY, "GraphCanvas.mouseReleased.2", ed.edgetype + "-" + ed.start.label + "-" + ed.end.label);
                } else {
                    // HELEN
                    ed.edgetype = "invalid";
                    menuVertex.isSelected = false;
                    graph.delEdge(ed);
                    // HELEN
                    //this.buttomLabel.setText("Invalid connection");
                    //JOptionPane.showMessageDialog(null, "invalid connection");
                    MessageDialog.showMessageDialog(frame, true, "Invalid connection", graph);
                }

                repaint(0);
            } else if (!graph.getVertexes().contains(ed.end)) {
                //log.out(LogType.ACTIVITY, "Edge does not have end vertex, delete it.");
                logs.concatOut(Logger.ACTIVITY, "GraphCanvas.mouseReleased.3", ed.start.label);
                menuVertex.isSelected = false;
                graph.delEdge(ed);
                // System.out.println("repaint by mouseReleased2");
                repaint(0);
            }
        }
        // log.out(LogType.DEBUG_LOCAL, "mouseReleased end");
    }

    /**
     * Delete the whole graph, it would be show all the graph area empty.
     */
    public void deleteAll() {
        //graph = new Graph();
        graph.setVertexes(new LinkedList());
        graph.setEdges(new LinkedList());
        repaint(0);
    }

    /**
     * Method to delete the selected item from the graph
     */
    public void deleteObject() {
        if (graph.getSelected() != null) {
            if (graph.getSelected() instanceof Edge) {
                Edge edge = (Edge) graph.getSelected();
                setModelChanged(true);
                run.setForeground(Color.GRAY);
                takeQuiz.setForeground(Color.GRAY);
                //log.out(LogType.ACTIVITY, "Edge between " + edge.start.label + " and " + edge.end.label + " deleted");
                logs.concatOut(Logger.ACTIVITY, "GraphCanvas.deleteObject.1", edge.start.label + "-" + edge.end.label);
                graph.delEdge(edge);
            }/*
             * else if (Graph.selected instanceof Vertex) {
             * graph.delVertex((Vertex) (Graph.selected)); }
             */
            // System.out.println("repaint by deleteObject1");
            repaint(0);
        } else {
            graph = new Graph(professorVersion);
            // System.out.println("repaint by deleteObject2");
            repaint(0);
        }
    }

    /**
     * Modify the size ot the selecte item by d.
     *
     * @param d is the value to add to the current size.
     */
    public void adjustSize(int d) {
        if (graph.getSelected() != null) {
            graph.getSelected().adjustSize(d);
            // System.out.println("repaint by adjustSize");
            repaint(0);
        }
    }

    /**
     * Method to alter the size (by d) of all items of the same type as the
     * selected item.
     */
    public void adjustSizes(int d) {
        if ((graph.getSelected() == null) || (graph.getSelected() instanceof Vertex)) {
            int n = graph.getVertexes().size();
            for (int j = 0; j < n; j++) {
                graph.vertex(j).adjustSize(d);
            }
        }
        if ((graph.getSelected() == null) || (graph.getSelected() instanceof Edge)) {
            int n = graph.getEdges().size();
            for (int j = 0; j < n; j++) {
                graph.edge(j).adjustSize(d);
            }
        }
        // System.out.println("repaint by adjustSizes");
        repaint(0);
    }

    /**
     * Method to alter the font size of the selected object
     */
    public void adjustFont(int d) {
        if (graph.getSelected() != null) {
            graph.getSelected().adjustFont(d);
        } else {
            graph.adjustFont(d);
        }
        // System.out.println("repaint by adjusFont");
        repaint(0);
    }

    /**
     * Method to alter the font size of all items of the same kind
     *
     * @param d the value to add to the current font
     */
    public void adjustFonts(int d) {
        int n = graph.getVertexes().size();
        Object a[] = graph.getVertexes().toArray();
        for (int j = 0; j < n; j++) {
            ((Vertex) a[j]).adjustFont(d);
        }
        n = graph.getEdges().size();
        a = graph.getEdges().toArray();
        for (int j = 0; j < n; j++) {
            ((Edge) a[j]).adjustFont(d);
        }
        // System.out.println("repaint by adjustFonts");
        a = null;
        repaint(0);
    }

    /**
     * Method to implemente the keylistener
     *
     * @param e
     */
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_BACK_SPACE:
            case KeyEvent.VK_DELETE: // 127 delete
                if (e.getModifiers() == 0) {
                    if (graph.getSelected() instanceof Edge) {
                        Edge edge = (Edge) graph.getSelected();
                        graph.delEdge(edge);
                    } else {
                        //deleteChar();
                    }
                }
                break;
            case 37: // left
                if (e.getModifiers() == 0) {
                    adjustSize(-1);
                } else {
                    adjustSizes(-1);
                }
                break;
            case 39: // right
                if (e.getModifiers() == 0) {
                    adjustSize(+1);
                } else {
                    adjustSizes(+1);
                }
                break;
            case 38: // up
                if (e.getModifiers() == 0) {
                    adjustFont(+1);
                } else {
                    adjustFonts(+1);
                }
                break;
            case 40: // down
                if (e.getModifiers() == 0) {
                    adjustFont(-1);
                } else {
                    adjustFonts(-1);
                }
                break;
        }
    }

    /**
     * Method to implemente the keylistener
     *
     * @param e
     */
    @Override
    public void keyReleased(KeyEvent e) {
    }

    /**
     * Method to implemente the keylistener
     *
     * @param e
     */
    @Override
    public void keyTyped(KeyEvent e) {
        /*
         * if (13 == e.getKeyCode()) { // newline graph.unselect(); //
         * System.out.println("repaint by keyTyped1"); repaint(0); return; } if
         * (0 != (e.getModifiers() & KeyEvent.CTRL_MASK)) return; if
         * (graph.selected != null) {
         * graph.selected.extendLabel(e.getKeyChar()); //
         * System.out.println("repaint by keyTyped2"); repaint(0);
        }
         */
    }

    /**
     * Methods to handle actions to different mouse events.
     *
     * @param e
     */
    @Override
    public void componentResized(ComponentEvent e) {
        image = null;
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    public void enableChangeShape() {
        changeShape = true;
    }

    /**
     * If changeShape is set by the popup menu, reset the control point with
     * current mouse position.
     *
     * @param e Mouse event
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        if (changeShape) {
            if (graph.getSelected() instanceof Edge) {
                Edge ed = (Edge) (graph.getSelected());
                ed.control.x = e.getX();
                ed.control.y = e.getY();
                // System.out.println("repaint by mouseMoved");
                repaint(0);
            }
        } else if (movedOnVertex(hitVertex(e.getX(), e.getY()), e) && menuOpen == false) {
            if (menuOpen == false) {
                menuVertex = findVertex(e);
            } else {
                menuVertex = menuOpenVertex;
            }

            //if(VERSIONID.equals("112"))
            if (professorVersion != Main.VERSION2 && professorVersion != Main.DEMO_VERSION2) {
                if (menuVertex != new Vertex() && menuVertex != null) {
                    Point pos = menuVertex.position;
                    int posX = pos.x;
                    int posY = pos.y;
                    //if the edge button is moved on
                    if (e.getX() >= posX + menuVertex.width / 2 - borderWidth / 2
                            && e.getX() <= posX + menuVertex.width / 2 + borderWidth / 2
                            && e.getY() >= posY + menuVertex.height / 2 - borderHeight / 2
                            && e.getY() <= posY + menuVertex.height / 2 + borderHeight / 2
                            && !menuVertex.type.equalsIgnoreCase("none")) {
                        enableEdge = true;
                        enableEdgeError = false;
                    } else if (e.getX() >= posX + menuVertex.width / 2 - borderWidth / 2
                            && e.getX() <= posX + menuVertex.width / 2 + borderWidth / 2
                            && e.getY() >= posY + menuVertex.height / 2 - borderHeight / 2
                            && e.getY() <= posY + menuVertex.height / 2 + borderHeight / 2
                            && menuVertex.type.equalsIgnoreCase("none")) {
                        enableEdgeError = true;
                    } else {
                        enableEdge = false;
                        enableEdgeError = false;
                    }
                    menuVertex.isSelected = true;
                    enableMenu = true;
                }
            }
            /*
             * else { menuVertex.isSelected = false; enableMenu = false;
                }
             */
        } else {
            if (graph.getSelected() instanceof Vertex) {
                Vertex v = (Vertex) (graph.getSelected());
                if (menuOpen == false) {
                    int n = graph.getVertexes().size();
                    Object a[] = graph.getVertexes().toArray();
                    for (int j = 0; j < n; j++) {
                        ((Vertex) a[j]).isSelected = false;
                    }
                }
                v.isSelected = false;
                enableMenu = false;
                // System.out.println("repaint by mouseMoved");
                repaint(0);
            }
        }

        //this is necessary to keep the vertex selected when the menu is open
        if (menuOpen == true) {
            menuOpenVertex.isSelected = true;
        }
    }

    /**
     * The method checks whether the edge obeys the connection rules, if not
     * delete the edge.
     *
     * @param ed Edge to be checked
     */
    private boolean validate(Edge ed) {
        Vertex start = ed.start;
        Vertex end = ed.end;
        boolean valid = false;
        //LinkedList<Edge> currentEdges = edgesBetween(start, end);

        //ed.edgeType(start, end);

        //log.out(LogType.ACTIVITY, "exist an edge between" + ed.start.label + " and " + ed.end.label + ": " + !existEdgeBetween(start,end));

        //NONE TYPE. Inputs: None. Outputs: None.
        if (start.type.equals("none") || end.type.equals("none")) {
            valid = false;
            //} else if (!existEdgeBetween(start, end)){
        } else if (existEdgeBetween(start, end) == 0) {
            //Review that there is not allEdges current edge
            if (start.type.equals("constant") && (end.type.equals("flow") || end.type.equals("auxiliary"))) {
                //CONSTANT TYPE. Output: Flow and Auxiliary. Inputs: None.
                valid = true;
            } else if (start.type.equals("auxiliary") && (end.type.equals("flow") || end.type.equals("auxiliary"))) {
                //AUXILIARY TYPE. Output: Flow, Auxiliary. Inputs: Constants, Auxiliary, Stock, Flow.
                valid = true;
            } else if (start.type.equals("flow") && end.type.equals("stock")) {
                //FLOW TYPE. Outputs: Stock (flowlink), Auxiliary. Inputs: Stock (flowlink / regularlink), auxiliary, constant.
                valid = true;
            } else if (start.type.equals("flow") && end.type.equals("auxiliary")) {
                valid = true;
            } else if (start.type.equals("stock") && (end.type.equals("auxiliary") || end.type.equals("stock"))) {
                //STOCK TYPE. Outputs: Flow, Auxiliary, Stock. Inputs: Flow, Stock.
                valid = true;
            } else if (start.type.equals("stock") && end.type.equals("flow")) {
                valid = true;
            } else {
                //ANY OTHER CONNECTION IS INVALID
                valid = false;
            }
        } else {
            // There is already an edge between this two nodes
            if (existEdgeBetween(start, end) == 1) {
                Edge edge;
                if ((start.type.equals("stock") && end.type.equals("flow"))) {
                    for (int i = 0; i < graph.getEdges().size(); i++) {
                        edge = (Edge) graph.getEdges().toArray()[i];
                        if (start.label.equals(edge.start.label) && end.label.equals(edge.end.label)) {
                            valid = true;
                        }
                    }
                } else if ((start.type.equals("flow") && end.type.equals("stock"))) {
                    for (int i = 0; i < graph.getEdges().size(); i++) {
                        edge = (Edge) graph.getEdges().toArray()[i];
                        if (start.label.equals(edge.end.label) && end.label.equals(edge.start.label)) {
                            if (edge.edgetype.equals("regularlink")) {
                                valid = true;
                                break;
                            } else {
                                valid = false;
                                break;
                            }
                        }
                    }
                } else {
                    valid = false;
                }
            } else {
                valid = false;
            }
        }
        return valid;
    }

    /**
     * Method that review if there is an edge between Vertex allEdges and Vertex
     * b
     *
     * @param allEdges is allEdges Vertex
     * @param b is allEdges Vertex
     * @return true if there is an edge, false otherwise
     */
    private int existEdgeBetween(Vertex a, Vertex b) {
//    private boolean existEdgeBetween(Vertex allEdges, Vertex b){

        Object[] edges = graph.getEdges().toArray();
        int lenE = graph.getEdges().size();
        Edge edge;
        boolean exist = false;
        int cont = 0;

        for (int i = 0; i < lenE; i++) {
            edge = (Edge) edges[i];
            // There is an edge between these two nodes
            if ((a.label.equals(edge.start.label) && b.label.equals(edge.end.label)) || (a.label.equals(edge.end.label) && b.label.equals(edge.start.label))) {
                cont++;
                exist = true;
            }
        }
        if (exist && cont == 1) {
            //log.out(LogType.ACTIVITY, "There is NOT an edge between " + allEdges.label + " y " + b.label);
            cont = 0;
            exist = false;
        } else {
            //log.out(LogType.ACTIVITY, "There is an edge between " + allEdges.label + " y " + b.label);
            cont--;
            exist = true;
        }
        //return exist;
        //System.out.println("GRAPHCANVAS Cont: " + cont);
        edges = null;
        return cont;
    }

    public boolean newEdge(Edge e) {
        graph.addEdge(e);
        repaint(0);
        return true;
    }

    // HELEN - MAY 10TH -- THIS METHODS IS NOT USED AT ALL
    public boolean newEdge(Vertex start, Vertex end) {
        //to have the last drawn vertex selected
        //select(graph.addVertex(new Vertex(x, y, name)));
        graph.addEdge(start, end);
        repaint(0);
        return true;
    }

    /**
     * Method to create allEdges new Vertex, in an x,y position and with
     * allEdges label name. This methods is only used to do the initialization.
     *
     * @param x is the x-coordinate
     * @param y is the y-coordinate
     * @param name is the label
     * @return true as acknowledge
     */
    public boolean newVertex(int x, int y, String name) {
        //to have the last drawn vertex selected
        //select(graph.addVertex(new Vertex(x, y, name)));
        graph.addVertex(new Vertex(x, y, name));
        repaint(0);
        return true;
    }

    public boolean newVertex(Vertex v) {
        //to have the last drawn vertex selected
        //select(graph.addVertex(new Vertex(x, y, name)));
        graph.addVertex(v);
        repaint(0);
        return true;
    }

    /**
     * Method to paint (repaint) the frame
     *
     * @param g
     */
    @Override
    public final void update(Graphics g) {
        paint(g);
    }

    /**
     * This method finds the vertex the mouse is on
     *
     * @param e is the mouse event
     * @return
     */
    public Vertex findVertex(MouseEvent e) {
        for (int i = 0; i < graph.getVertexes().size(); i++) {
            Vertex v = (Vertex) graph.getVertexes().toArray()[i];
            if (v.hit(e.getX(), e.getY())) {
                //System.out.println(v.label + " returned!");
                return v;
            } else if (enableMenu == true && v.hitMenu(e.getX(), e.getY(), v.position.x, v.position.y)) {
                return v;
            }
        }
        //System.out.println("error");
        return null;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        //if(VERSIONID.equals("112"))
        if (professorVersion != Main.VERSION2 && professorVersion != Main.DEMO_VERSION2) {
            //mouse clicked actions for the vertex menu
            if (enableMenu == true && e.getButton() != MouseEvent.BUTTON3) {
                Point pos = menuVertex.position;
                int posX = pos.x;
                int posY = pos.y;

                //the graph button (left button)
                //if(e.getClickCount() == 1 ){
                if (x >= posX + menuVertex.width / 2 - borderWidth * 3 / 2 - distance + widthDif
                        && x <= posX + menuVertex.width / 2 - borderWidth / 2 - distance + widthDif
                        && y >= posY + menuVertex.height / 2 - borderHeight / 2
                        && y <= posY + menuVertex.height / 2 + borderHeight / 2) {
                    if ((menuVertex.equation == null) || (menuVertex.equation.getIsCorrect() == false) || (menuVertex.getAlreadyRun() == false) || (modelChanged == true)) {
                        MessageDialog.showMessageDialog(frame, true, "The model needs to be run before a graph can be displayed.", graph);
                        logs.out(Logger.ACTIVITY, "GraphCanvas.mouseClicked.1");
                    } else if (menuVertex.getGraphOpen() == true) {
                        menuVertex.setGraphOpen(true);
                        //bring the graph to the front
                        for (int i = 0; i < graph.getPlots().size(); i++) {
                            if (graph.getPlots().get(i).getVertex() == menuVertex) {
                                graph.getPlots().get(i).toFront();
                            }
                        }
                    } else {
                        menuVertex.setGraphOpen(true);
                        logs.concatOut(Logger.ACTIVITY, "GraphCanvas.mouseClicked.2", menuVertex.label);
                        PlotDialog gd = new PlotDialog(frame, this.getParent(), false, menuVertex, graph, task);
                        gd.setVisible(true);
                    }
                    menuVertex.isSelected = true;
                } //the equation button (right button)
                else if (x >= posX + menuVertex.width / 2 + borderWidth / 2 + distance - widthDif
                        && x <= posX + menuVertex.width / 2 + borderWidth * 3 / 2 + distance - widthDif
                        && y >= posY + menuVertex.height / 2 - borderHeight / 2
                        && y <= posY + menuVertex.height / 2 + borderHeight / 2) {
                    if (menuVertex.type.equals("none")) {
                        MessageDialog.showMessageDialog(frame, true, "Use the right mouse button or double click to select a node type", graph);
                    } //do nothing
                    else {
                        EquationEditor equationEditor = getEquationEditorForVertex(menuVertex);
                        ee.add(equationEditor);
                        if (equationEditor != null && menuVertex.getEditorOpen() == false) {
                            equationEditor.setVisible(true);
                            menuVertex.setEditorOpen(true);
                        } else {
                            logs.out(Logger.ACTIVITY, "GraphCanvas.mouseClicked.3");
                        }
                    }
                    menuVertex.isSelected = true;
                } else if (e.getClickCount() > 1 && (graph.getSelected() instanceof Vertex)) {
                    menuOpen = false;

                    graph.unselect();
                    //unselect all vertexes
                    int n = graph.getVertexes().size();
                    Object a[] = graph.getVertexes().toArray();
                    for (int j = 0; j < n; j++) {
                        ((Vertex) a[j]).isSelected = false;
                    }
                    if (menuOpenVertex != null) {
                        menuOpenVertex.isSelected = false;
                    }
                    Vertex v = hitVertex(x, y);
                    graph.select(v);
                    PopupCanvasMenu pcMenu = new PopupCanvasMenu(graph.getSelected(), this, graph, task, run, takeQuiz, frame);
                    logs.concatOut(Logger.ACTIVITY, "GraphCanvas.mouseClicked.4", v.label);
                    menuOpen = true;
                    menuOpenVertex = v;
                    //System.out.println("menuOpenVertex in mousepressed " + menuOpenVertex);
                    //pcMenu.setBorder(javax.swing.BorderFactory.createTitledBorder(v.label));
                    //pcMenu.setBorder(new CompoundBorder(new TitledBorder(v.label), new EmptyBorder(10, 10, 10, 10)));

                    add(pcMenu);
                    v.isSelected = true;
                    pcMenu.show(this, e.getX(), e.getY());
                }
            } else {
                if (graph.getSelected() instanceof Vertex && e.getButton() != MouseEvent.BUTTON3 && e.getClickCount() < 2) {
                    Vertex v = (Vertex) graph.getSelected();
                    v.isSelected = true;
                    menuOpen = false;
                    menuOpenVertex.isSelected = false;
                } else if (graph.getSelected() instanceof Vertex && e.getButton() == MouseEvent.BUTTON3) {
                } else if (graph.getSelected() instanceof Vertex && e.getClickCount() > 1) {
                    menuOpen = false;
                    graph.unselect();
                    //unselect all vertexes
                    int n = graph.getVertexes().size();
                    Object a[] = graph.getVertexes().toArray();
                    for (int j = 0; j < n; j++) {
                        ((Vertex) a[j]).isSelected = false;
                    }
                    if (menuOpenVertex != null) {
                        menuOpenVertex.isSelected = false;
                    }
                    Vertex v = hitVertex(x, y);
                    graph.select(v);
                    PopupCanvasMenu pcMenu = new PopupCanvasMenu(graph.getSelected(), this, graph, task, run, takeQuiz, frame);
                    logs.concatOut(Logger.ACTIVITY, "GraphCanvas.mouseClicked.4", v.label);
                    menuOpen = true;
                    menuOpenVertex = v;
                    //System.out.println("menuOpenVertex in mousepressed " + menuOpenVertex);
                    //pcMenu.setBorder(javax.swing.BorderFactory.createTitledBorder(v.label));
                    //pcMenu.setBorder(new CompoundBorder(new TitledBorder(v.label), new EmptyBorder(10, 10, 10, 10)));

                    add(pcMenu);
                    v.isSelected = true;
                    pcMenu.show(this, e.getX(), e.getY());
                } else {
                    menuOpen = false;
                    if (menuOpenVertex != null) {
                        menuOpenVertex.isSelected = false;
                    }
                }
            }
        } else {
            if (e.getButton() != MouseEvent.BUTTON3 && pressedOnVertex(hitVertex(x, y), e)) {
                if (Main.dialogIsShowing) {
                    Window[] dialogs = Dialog.getWindows();
                    for (int i = 0; i < dialogs.length; i++) {
                        if (dialogs[i].getName().equals("tutorMsg") || dialogs[i].getName().equals("tutorQues")) {
                            dialogs[i].setVisible(true);
                            dialogs[i].setAlwaysOnTop(true);
                            JOptionPane.showMessageDialog(dialogs[i], "Please finish the dialog.");
                            break;
                        }
                    }
                    return;
                }
                //OPEN A WINDOW FOR THE TABBED GUI
                String name = "New Node";
                if (!hitVertex(x, y).nodeName.equals("")) {
                    name = hitVertex(x, y).nodeName;
                }

                if (openTabs.size() > 0 && !hitVertex(x, y).isOpen) {
                    openTabs.get(0).setVisible(true);
                    MessageDialog.showMessageDialog(null, true, "Please close the current Node Editor.", graph);
                } else if (!hitVertex(x, y).isOpen) {
                    logs.concatOut(Logger.ACTIVITY, "No message", "Open a node try--" + name);
                    String returnMsg = blockSocket.blockQuery(frame, "Open a node");
                    if (returnMsg.equals("allow")) {
                        logs.concatOut(Logger.ACTIVITY, "GraphCanvas.mouseClicked.5", name);
                        TabbedGUI openWindow = new TabbedGUI(hitVertex(x, y), graph, this, true, false);
                        hitVertex(x, y).isOpen = true;
                        openWindow.setVisible(true);
                        openTabs.add(openWindow);
                    } else {
                        new MetaTutorMsg(returnMsg.split(":")[1], false).setVisible(true);
                    }
                } else {
                    for (int i = 0; i < openTabs.size(); i++) {
                        if (openTabs.get(i).getCurrentVertex().nodeName.equals(name)) {
                            openTabs.get(i).toFront();
                            openTabs.get(i).requestFocus();
                        }
                    }
                }
            }
        }
    }

    /**
     * getEquationEditorForVertex
     *
     * @param v
     * @return EquationEditor
     */
    public EquationEditor getEquationEditorForVertex(Vertex v) {
        Equation eq = v.equation;
        EquationEditor equationEditor;
        String variable = v.label;
        String oldEq = null;
        if (v.equation != null) {
            oldEq = v.equation.toString();
        }
        LinkedList<String> inputList = new LinkedList<String>();
        if (v.type.equals("stock") || v.type.equals("constant")) {
            if (v.type.equals("stock")) {
                variable = "INITIAL(" + variable + ")";
                //THIS CYCLE IS ONLY FOR TESTING
                for (int i = 0; i < v.outedges.size(); i++) {
                    Edge edge = (Edge) v.outedges.toArray()[i];
                    System.out.println("Link from Stock to :" + edge.end.label + ", " + edge.edgetype);
                }
                //HERE ENDS THE TESTING CYCLE
            } else if (v.type.equals("constant")) {
                variable = "CONSTANT(" + variable + ")";
            }
            equationEditor = new EquationEditor(this.getParent(), graph, v, variable, inputList, true, oldEq);
            //setModelChanged(true);
            //run.setEnabled(true);
            run.setForeground(Color.BLACK);
            //takeQuiz.setEnabled(false);
            takeQuiz.setForeground(Color.GRAY);
        } else if (v.type.equals("flow")) {
            //If an flow has as input an stock this should not appears in the list
            for (int i = 0; i < v.inedges.size(); i++) {
                Edge edge = (Edge) v.inedges.toArray()[i];
                System.out.println("Incoming edges in the flow: " + v.inedges.size());
                System.out.println("Edge " + i + ", " + edge.start.type + ", " + edge.edgetype);
                if (edge.start.type.equals("stock")) {
                    if (edge.edgetype.equals("regularlink")) {
                        inputList.add(edge.start.label);
                    }
                } else {
                    inputList.add(edge.start.label);
                }
            }
            // HELEN - It might be not the best solution. Review
            //Review all edges to find those from stock to this flow
            if (v.inedges.toArray().length > 0) {
                Edge edge = (Edge) v.inedges.toArray()[0];
                if (edge != null) {
                    for (int j = 0; j < edge.getAllEdges().size(); j++) {
                        Edge e = (Edge) edge.getAllEdges().toArray()[j];
                        if ((e.end == v) && e.start.type.equals("stock") && e.edgetype.equals("regularlink")) {
                            boolean exist = false;
                            for (int k = 0; k < inputList.size(); k++) {
                                if (e.start.label.equals(inputList.get(k))) {
                                    exist = true;
                                    break;
                                }
                            }
                            if (!exist) {
                                inputList.add(e.start.label);
                            }
                        }
                    }
                }
            }
            // HELEN MAY 10th

            equationEditor = new EquationEditor(this.getParent(), graph, v, variable, inputList, false, oldEq);
            run.setForeground(Color.BLACK);
            takeQuiz.setForeground(Color.GRAY);
        } else if (v.type.equals("auxiliary")) {
            for (int i = 0; i < v.inedges.size(); i++) {
                Edge edge = (Edge) v.inedges.toArray()[i];
                inputList.add(edge.start.label);
            }
            equationEditor = new EquationEditor(this.getParent(), graph, v, variable, inputList, false, oldEq);
            run.setForeground(Color.BLACK);
            takeQuiz.setForeground(Color.GRAY);
        } else {
            equationEditor = null;
        }
        return equationEditor;
    }

    @Override
    public Dimension getPreferredSize() {
        if (imageSize.equals(area)) {
            return this.getParent().getSize();
        } else {
            return imageSize.getSize();
        }
    }

    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        int current = 0;
        if (orientation == SwingConstants.VERTICAL) {
            current = visibleRect.y;
        } else {
            current = visibleRect.x;
        }
        return current;
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        int current = 0;
        if (orientation == SwingConstants.VERTICAL) {
            current = visibleRect.y;
        } else {
            current = visibleRect.x;
        }
        return current;
    }

    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    public boolean checkNodeForCorrectCalculations(int vertexIndex) {
        TabbedGUI nodeGUI = new TabbedGUI((Vertex) graph.getVertexes().get(vertexIndex), graph, this, false, true);
        return nodeGUI.getCalculationsPanel().checkForCorrectCalculations();
    }

    public boolean checkNodeForCorrectInputs(int vertexIndex) {
        TabbedGUI nodeGUI = new TabbedGUI((Vertex) graph.getVertexes().get(vertexIndex), graph, this, false, true);
        return nodeGUI.getInputsPanel().checkForCorrectInputs();
    }

    public boolean checkNodeForCorrectInputSyntactics(int vertexIndex) {
        TabbedGUI nodeGUI = new TabbedGUI((Vertex) graph.getVertexes().get(vertexIndex), graph, this, false, true);
        return !(nodeGUI.getInputsPanel().checkForSyntaxErrors());
    }

    public boolean checkNodeForCorrectCalculationSyntactics(int vertexIndex) {
        TabbedGUI nodeGUI = new TabbedGUI((Vertex) graph.getVertexes().get(vertexIndex), graph, this, false, true);
        return !(nodeGUI.getCalculationsPanel().checkForSyntaxErrors());
    }

    @Override
    public void componentShown(ComponentEvent e) {
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }

    @Override
    public void focusLost(FocusEvent e) {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }
}
