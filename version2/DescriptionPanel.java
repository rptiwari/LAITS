/*
 * DescriptionPanel.java
 *
 * Created on Nov 21, 2010, 10:23:38 AM
 */
package laits.version2;

import laits.BlockSocket;
import laits.MetaTutorMsg;
import laits.comm.CommException;
import laits.data.DecisionTreeNode;
import laits.data.TaskFactory;
import laits.graph.GraphCanvas;
import laits.data.Task;
import laits.graph.Vertex;
import laits.graph.Graph;
import laits.gui.MessageDialog;
import laits.log.Logger;
import laits.parser.Equation;
import java.awt.Color;
import java.util.LinkedList;
import java.util.StringTokenizer;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.border.Border;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 *
 * @author Megan
 * @author Helen
 */
public class DescriptionPanel extends javax.swing.JPanel implements TreeSelectionListener {

  Vertex currentVertex;
  TaskFactory server;
  GraphCanvas gc;
  Graph graph;
  private String undoName = "";
  private LinkedList<TreePath> treePaths = new LinkedList<TreePath>();
  private boolean undoFlag = false;
  TabbedGUI parent;

  Logger logger = Logger.getLogger();
  private boolean initializing = false;
  private boolean treeSelected = true;

  BlockSocket blockSocket=BlockSocket.getBlockSocket();
  /** Creates new form DescriptionPanel */
  public DescriptionPanel(TabbedGUI parent, Vertex v, Graph g, GraphCanvas gc) {
    initComponents();
    this.currentVertex = v;
    this.parent = parent;
    this.graph = g;
    this.gc = gc;


    try {
      this.server = TaskFactory.getInstance();
    } catch (CommException ex) {
      //Add appropriate logger
      logger.concatOut(Logger.DEBUG, "InputsPanel.InputsPanel.1", ex.toString());
    }

    nodeNameTextField.setText(currentVertex.nodeName);
    System.out.println("StartD: "+currentVertex.correctDescription);
    if(!currentVertex.correctDescription.equals(""))
    {
      this.parent.getCorrectDescriptionFromFile();   
      //descriptionAreaLabel.setText(currentVertex.correctDescription);
    }
    
      initValues();
      initDescription();
      initTree(parent.task);
      initTreeSelectionListener();
      TreePath treePath = currentVertex.getTreePath();
//      if(treePath!=null)
//      {
//        decisionTree.scrollPathToVisible(treePath);
//        System.out.println("init treepath: "+treePath);
//      }

      quantityDescriptionTextField.setText(currentVertex.selectedDescription);   
    
//    if(currentVertex.getTreePath()!=null)
//    {
//      System.out.println("currentVertex.getTreePath():"+currentVertex.getTreePath());
//      expandTreePath(decisionTree,currentVertex.getTreePath());
//    }
      if(getSelectedTreeNode()==null)
      {
        treeSelected=false;
        checkButton.setEnabled(treeSelected);
      }
    

    hintButton.setVisible(false);
    hintLabel.setVisible(false);
    
    
    
  }

  
  public DefaultMutableTreeNode getSelectedTreeNode() {
    DefaultMutableTreeNode node = (DefaultMutableTreeNode) decisionTree.getLastSelectedPathComponent();
    return node;
  }

  /**
   * This method initializes the panel if the user has already chosen a description, checked, given up, or chose a wrong answer
   */
  public void initValues() {
    initializing = true;
    initButtonOnTask();

    
    //HELEN SET THE UNDO BUTTON INVISIBLE UNTIL IT WORKS PROPERLY FOR ALL TABS.
    undoButton.setVisible(false);
    //undoButton.setEnabled(undoFlag);
    
    decisionTree.setEnabled(false);
    decisionTree.setBackground(new Color(240, 240, 240));
    quantityDescriptionTextField.setEditable(true);
    nodeNameTextField.setEditable(true);
    descriptPanel.setBackground(new Color(240, 240, 240));

   /* if (currentVertex.getDescriptionButtonStatus() == currentVertex.CORRECT) {
      quantityDescriptionTextField.setBackground(new Color(155,250,140));
      quantityDescriptionTextField.setEnabled(false);
      nodeNameTextField.setEnabled(false);
      nodeNameTextField.setBackground(new Color(155,250,140));
      decisionTree.setEditable(false);
      decisionTree.setEnabled(false);
      nodeNameTextField.setEnabled(false);
      checkButton.setEnabled(treeSelected);
      giveUpButton.setEnabled(false);
      undoButton.setEnabled(false);
      hintButton.setEnabled(false);
    } else if (currentVertex.getDescriptionButtonStatus() == currentVertex.GAVEUP) {
      quantityDescriptionTextField.setBackground(new Color(252,252,130));
      nodeNameTextField.setBackground(new Color(252,252,130));
      quantityDescriptionTextField.setEnabled(false);
      nodeNameTextField.setEnabled(false);
      decisionTree.setEditable(false);
      decisionTree.setEnabled(false);
      nodeNameTextField.setEnabled(false);
      checkButton.setEnabled(treeSelected);
      giveUpButton.setEnabled(false);
      undoButton.setEnabled(false);
      hintButton.setEnabled(false);
    } else if (currentVertex.getDescriptionButtonStatus() == currentVertex.WRONG) {
      quantityDescriptionTextField.setBackground(Color.pink);
      nodeNameTextField.setBackground(Color.pink);
    }
*/
    
    initializing = false;
    
  }

  public void initButtonOnTask() {
    // Depending on what type the current task is, checkButton oand giveUpButton should either be
    // disabled or enabled
    if(server.getActualTask().getType().equalsIgnoreCase("Intro") ||
       server.getActualTask().getType().equalsIgnoreCase("Debug") ||
       server.getActualTask().getType().equalsIgnoreCase("Construct") ||
       server.getActualTask().getType().equalsIgnoreCase("Whole")) {
          //checkButton.setEnabled(treeSelected);
          //giveUpButton.setEnabled(true);
        
        // - Disabling the Check and Give Up button for author mode - Ram
            checkButton.setEnabled(false);
            giveUpButton.setEnabled(false);
            
    } else if (server.getActualTask().getType().equalsIgnoreCase("Test")) {
      checkButton.setEnabled(treeSelected);
      giveUpButton.setEnabled(false);
    }
/*            
      else if (server.getActualTask().getType().equalsIgnoreCase("Whole") && gc.modelHasBeenRun == true) {
      checkButton.setEnabled(treeSelected);
      giveUpButton.setEnabled(true);
    }     
*/

  }

  public void initDescription(){
    descriptionAreaLabel.setText(currentVertex.situationDescription);
  }

  /**
   * This method parse the decision tree of the task contained in the LinkedList 
   * @param task
   */
  public void initTree(Task task){
    DefaultMutableTreeNode root = null;
    DefaultMutableTreeNode level1 = null;
    DefaultMutableTreeNode level2 = null;
    DefaultMutableTreeNode level3 = null;
    DefaultMutableTreeNode level4 = null;
    LinkedList <String> tree = new LinkedList<String>();
    DecisionTreeNode dTreeNode;

    tree = task.getTree();
    if (tree != null){
    root = new DefaultMutableTreeNode("Node");
    String leaf = "";
    StringTokenizer st;
    String answer = "";

    //Goes throught the whole list (tree)
    //If the entry has a "period" it is a leaf and the word after the period is the label for the node
    //The number of "-" indicates the level of the entry in the tree
    //The current code allows four levels in depth in the tree if we need more we need to add extra if's
    for (int i=0; i<tree.size(); i++){
      leaf = tree.get(i);
      if (leaf.startsWith("-:")){
        if (leaf.contains(".")){
          st = new StringTokenizer(leaf, ".");
          leaf = st.nextToken();
          answer = st.nextToken();
        }
        leaf=leaf.replace("-:", "");
        dTreeNode = new DecisionTreeNode();
        dTreeNode.setLabel(leaf);
        dTreeNode.setAnswer(answer);
        level1 = new DefaultMutableTreeNode(dTreeNode.getLabel());
        level1.setUserObject(dTreeNode);
        root.add(level1);
      } else if (leaf.startsWith("--:")){
          if (leaf.contains(".")){
           st = new StringTokenizer(leaf, ".");
           leaf = st.nextToken();
           answer = st.nextToken();
          }
          leaf=leaf.replace("--:", "");
          dTreeNode = new DecisionTreeNode();
          dTreeNode.setLabel(leaf);
          dTreeNode.setAnswer(answer);
          level2= new DefaultMutableTreeNode(dTreeNode);
          level1.add(level2);
      } else if (leaf.startsWith("---:")){
          if (leaf.contains(".")){
           st = new StringTokenizer(leaf, ".");
           leaf = st.nextToken();
           answer = st.nextToken();
          }
          leaf=leaf.replace("---:", "");
          dTreeNode = new DecisionTreeNode();
          dTreeNode.setLabel(leaf);
          dTreeNode.setAnswer(answer);
          level3= new DefaultMutableTreeNode(dTreeNode);
          level2.add(level3);
      } else if (leaf.startsWith("----:")){
          if (leaf.contains(".")){
           st = new StringTokenizer(leaf, ".");
           leaf = st.nextToken();
           answer = st.nextToken();
          }
          leaf=leaf.replace("----:", "");
          dTreeNode = new DecisionTreeNode();
          dTreeNode.setLabel(leaf);
          dTreeNode.setAnswer(answer);
          level4= new DefaultMutableTreeNode(dTreeNode);
          level3.add(level4);
        }
    }
    decisionTree.setModel(new javax.swing.tree.DefaultTreeModel(root));
    jScrollPane2.setViewportView(decisionTree);
    }
  }

  public void initTreeSelectionListener() {
    decisionTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    decisionTree.addTreeSelectionListener(this);
  }

  public void valueChanged(TreeSelectionEvent e) {
    DefaultMutableTreeNode node = getSelectedTreeNode();
    DecisionTreeNode n;

    try{
    if (node != null) {
      treeSelected = true;
      checkButton.setEnabled(false);
      n = (DecisionTreeNode)node.getUserObject();
      
//      System.out.println(node.getPath()[node.getPath().length-1].toString());
//      treeNode = node.getPath();
      //change input button status so that the (g) graphic on the vertex turns white
      currentVertex.setDescriptionButtonStatus(currentVertex.NOSTATUS);
      //reset background colors
      quantityDescriptionTextField.setBackground(new Color(240, 240, 240));
      nodeNameTextField.setBackground(new Color(240, 240, 240));
      quantityDescriptionTextField.setEnabled(true);
      nodeNameTextField.setEnabled(true);

      if (node == null) {
        return;
      }

      if (node.isLeaf()) {
        
//        TreeNode treeNodePath = node.getPath()[node.getPath().length-1];
//        System.out.println(treeNodePath.toString());
//        System.out.println("decisionTree.getSelectionPath():"+decisionTree.getSelectionPath().toString());
        
        //zpwn: to get & update vertex decision tree path.
        treePaths.add(decisionTree.getSelectionPath());       
        if(currentVertex.getTreePath()==null)
        {
          currentVertex.setTreePath(decisionTree.getSelectionPath());
          System.out.println("currentVertex.getTreePath() is null");
        }
        else
        {
          if(decisionTree.getSelectionPath()!=currentVertex.getTreePath())
          {
            currentVertex.setTreePath(decisionTree.getSelectionPath());
            System.out.println("currentVertex.getTreePath() is new");
          }
        }
        //done
        
        //Get the data from the tree to fill the nodeName. A leaf node has a label and an answer.
        nodeNameTextField.setText(n.getAnswer());
        currentVertex.correctDescription="";
        currentVertex.situationDescription="";
        undoName = currentVertex.nodeName;
        undoFlag=true;
        undoButton.setEnabled(undoFlag);
        currentVertex.nodeName = nodeNameTextField.getText();
        currentVertex.label = currentVertex.nodeName;
        currentVertex.correctEquation = null;
        parent.getCorrectAnswers();
        this.parent.getCorrectDescriptionFromFile();
        descriptionAreaLabel.setText(currentVertex.situationDescription);
        descriptionScrollPane.getVerticalScrollBar().setValue(0);
        System.out.println(currentVertex.correctDescription);
        currentVertex.defaultLabel();
        //nodeNameTextField.setBackground(Color.LIGHT_GRAY);
        //quantityDescriptionTextFiereld.setBackground(Color.LIGHT_GRAY);

        //Populate the quantity description label
        String description = "";
        TreeNode[] ancestors = node.getPath();
        for (int i = 0; i < ancestors.length; i++) {
          if (!ancestors[i].toString().equalsIgnoreCase("Node")) {
            if (!description.equals("")) {
              description += " " + ancestors[i].toString();
            } else {
              description = ancestors[i].toString();
            }
          }
        }
        quantityDescriptionTextField.setText(description);
        currentVertex.selectedDescription = description;
        parent.getInputsPanel().updateDescription();
        parent.getGraphsPanel().updateDescription();

        if(currentVertex.correctDescription.trim().equals(description)){
          logger.concatOut(Logger.ACTIVITY, "DescriptionPanel.valueChanged.1", "legal_"+currentVertex.nodeName);
        }
        else{
          logger.concatOut(Logger.ACTIVITY, "DescriptionPanel.valueChanged.1", "!legal");
        }
      }
    }
    else
    {
      treeSelected = false;
      checkButton.setEnabled(treeSelected);
    }
  }catch(ClassCastException cce){}
  }

  public void setquantityDescriptionTextField(String desc){
    this.quantityDescriptionTextField.setText(desc);
    quantityDescriptionTextField.setBackground(new Color(155,250,140));
    nodeNameTextField.setBackground(new Color(155,250,140));
    decisionTree.setEnabled(false);
    undoButton.setEnabled(false);
    checkButton.setEnabled(false);
    giveUpButton.setEnabled(false);
    hintButton.setEnabled(false);
    currentVertex.setDescriptionButtonStatus(currentVertex.CORRECT);
    parent.setTitle(currentVertex.label);
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        contentPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        decisionTree = new javax.swing.JTree();
        quantityLabel = new javax.swing.JLabel();
        evenMorePreciseLabel = new javax.swing.JLabel();
        descriptionScrollPane = new javax.swing.JScrollPane();
        descriptPanel = new javax.swing.JPanel();
        descriptionAreaLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        quantityDescriptionTextField = new javax.swing.JTextArea();
        referencesLabel = new javax.swing.JLabel();
        nodeNameTextField = new javax.swing.JTextField();
        NodeNameLabel = new javax.swing.JLabel();
        buttonPanel = new javax.swing.JPanel();
        hintButton = new javax.swing.JButton();
        checkButton = new javax.swing.JButton();
        undoButton = new javax.swing.JButton();
        giveUpButton = new javax.swing.JButton();
        hintPanel = new javax.swing.JPanel();
        hintLabel = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        javax.swing.tree.DefaultMutableTreeNode treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("A count of");
        javax.swing.tree.DefaultMutableTreeNode treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("rabbits in the population");
        javax.swing.tree.DefaultMutableTreeNode treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("at the beginning of the year");
        javax.swing.tree.DefaultMutableTreeNode treeNode5 = new javax.swing.tree.DefaultMutableTreeNode("and it is constant from year to year");
        treeNode4.add(treeNode5);
        treeNode5 = new javax.swing.tree.DefaultMutableTreeNode("and it varies from year to year");
        treeNode4.add(treeNode5);
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("totaled up across all years");
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("averaged across all years");
        treeNode3.add(treeNode4);
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("rabbits born into the population");
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("during a year");
        treeNode5 = new javax.swing.tree.DefaultMutableTreeNode("and it is constant from year to year");
        treeNode4.add(treeNode5);
        treeNode5 = new javax.swing.tree.DefaultMutableTreeNode("and it varies from year to year");
        treeNode4.add(treeNode5);
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("across all years");
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("per year on average");
        treeNode3.add(treeNode4);
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("A ratio of");
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("the number of rabbits in the population at the beginning of the year, divided by");
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("the number of rabbits added to the population during that same year");
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("the total number of rabbits added up across all years");
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("the average number of rabbits across all years");
        treeNode3.add(treeNode4);
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("the number of rabbits added to the population during the year, divided by");
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("the number of rabbits in the population at the beginning of that same year");
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("the total number of rabbits added to the population across all years");
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("the average number of rabbits added to the population each year");
        treeNode3.add(treeNode4);
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        decisionTree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jScrollPane2.setViewportView(decisionTree);

        quantityLabel.setText("References to the quantities are underlined:");

        evenMorePreciseLabel.setText("Choose the more precise description for the quantity:");

        descriptionScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        descriptPanel.setBackground(new java.awt.Color(255, 255, 255));
        descriptPanel.setPreferredSize(new java.awt.Dimension(669, 290));

        descriptionAreaLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout descriptPanelLayout = new javax.swing.GroupLayout(descriptPanel);
        descriptPanel.setLayout(descriptPanelLayout);
        descriptPanelLayout.setHorizontalGroup(
            descriptPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(descriptPanelLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(descriptionAreaLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 603, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(64, Short.MAX_VALUE))
        );
        descriptPanelLayout.setVerticalGroup(
            descriptPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(descriptPanelLayout.createSequentialGroup()
                .addComponent(descriptionAreaLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(290, 290, 290))
        );

        descriptionScrollPane.setViewportView(descriptPanel);

        quantityDescriptionTextField.setWrapStyleWord(true);
        quantityDescriptionTextField.setColumns(20);
        quantityDescriptionTextField.setLineWrap(true);
        quantityDescriptionTextField.setRows(2);
        quantityDescriptionTextField.setDisabledTextColor(new java.awt.Color(102, 102, 102));
        quantityDescriptionTextField.setMargin(new java.awt.Insets(2, 3, 2, 3));
        jScrollPane1.setViewportView(quantityDescriptionTextField);

        referencesLabel.setText("Precise description of the quantity:");

        nodeNameTextField.setEditable(false);
        nodeNameTextField.setDisabledTextColor(new java.awt.Color(102, 102, 102));
        nodeNameTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nodeNameTextFieldActionPerformed(evt);
            }
        });

        NodeNameLabel.setText("Node Name:");

        buttonPanel.setMaximumSize(new java.awt.Dimension(540, 92));
        buttonPanel.setMinimumSize(new java.awt.Dimension(540, 92));

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

        hintLabel.setBackground(new java.awt.Color(0, 0, 0));
        hintLabel.setText("Hint");

        javax.swing.GroupLayout hintPanelLayout = new javax.swing.GroupLayout(hintPanel);
        hintPanel.setLayout(hintPanelLayout);
        hintPanelLayout.setHorizontalGroup(
            hintPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(hintPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(hintLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 526, Short.MAX_VALUE)
                .addContainerGap())
        );
        hintPanelLayout.setVerticalGroup(
            hintPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(hintLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
        );

        jButton1.setText("Ok");

        jButton2.setText("Cancel");

        javax.swing.GroupLayout buttonPanelLayout = new javax.swing.GroupLayout(buttonPanel);
        buttonPanel.setLayout(buttonPanelLayout);
        buttonPanelLayout.setHorizontalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(buttonPanelLayout.createSequentialGroup()
                        .addComponent(hintButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(undoButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(checkButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(giveUpButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2))
                    .addComponent(hintPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(267, Short.MAX_VALUE))
        );
        buttonPanelLayout.setVerticalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(hintButton)
                            .addComponent(checkButton)
                            .addComponent(giveUpButton)
                            .addComponent(undoButton))
                        .addComponent(jButton2))
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(hintPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout contentPanelLayout = new javax.swing.GroupLayout(contentPanel);
        contentPanel.setLayout(contentPanelLayout);
        contentPanelLayout.setHorizontalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(contentPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 823, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(contentPanelLayout.createSequentialGroup()
                        .addComponent(evenMorePreciseLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 363, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(contentPanelLayout.createSequentialGroup()
                        .addComponent(buttonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, contentPanelLayout.createSequentialGroup()
                        .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, contentPanelLayout.createSequentialGroup()
                                .addComponent(NodeNameLabel)
                                .addGap(18, 18, 18)
                                .addComponent(nodeNameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 703, Short.MAX_VALUE))
                            .addComponent(referencesLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 780, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 780, Short.MAX_VALUE))
                        .addGap(53, 53, 53))
                    .addGroup(contentPanelLayout.createSequentialGroup()
                        .addComponent(quantityLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 323, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(510, Short.MAX_VALUE))
                    .addGroup(contentPanelLayout.createSequentialGroup()
                        .addComponent(descriptionScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 620, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(213, Short.MAX_VALUE))))
        );
        contentPanelLayout.setVerticalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(evenMorePreciseLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                .addComponent(quantityLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(descriptionScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(referencesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nodeNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(NodeNameLabel))
                .addGap(27, 27, 27)
                .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(contentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(156, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void nodeNameTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nodeNameTextFieldActionPerformed
      currentVertex.nodeName = nodeNameTextField.getText();
    }//GEN-LAST:event_nodeNameTextFieldActionPerformed

    private void hintButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hintButtonActionPerformed
      if (!initializing) {
        hintLabel.setVisible(true);
        if (hintLabel.getForeground().equals(Color.BLACK)) {
          hintLabel.setForeground(new Color(240, 240, 240));
          hintPanel.setBackground(new Color(240, 240, 240));
          logger.out(Logger.ACTIVITY, "DescriptionPanel.hintButtonActionPerformed.1");
        } else {
          hintLabel.setForeground(Color.BLACK);
          hintPanel.setBackground(new Color(255, 204, 0));
          logger.out(Logger.ACTIVITY, "DescriptionPanel.hintButtonActionPerformed.2");
        }
      }
    }//GEN-LAST:event_hintButtonActionPerformed

    /**
     * &author curt
     * @param evt
     */
    private void checkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkButtonActionPerformed
      if (!initializing) {
        if (!duplicatedNode(currentVertex.nodeName)) {
          logger.concatOut(Logger.ACTIVITY, "No message", "Click check button try");
          String returnMsg=blockSocket.blockQuery(this,"Click check button");
          if(returnMsg.startsWith("deny:")) //the action is not allowed by meta tutor
          {
            new MetaTutorMsg(returnMsg.split(":")[1],false).setVisible(true);
            return;
          }

          logger.out(Logger.ACTIVITY, "DescriptionPanel.checkButtonActionPerformed.1");

          if(currentVertex.getTreePath()!=null && getSelectedTreeNode() == null) {
            decisionTree.scrollPathToVisible(currentVertex.getTreePath());
            decisionTree.setSelectionPath(currentVertex.getTreePath());
          }
          if (getSelectedTreeNode() != null)
          {
            if(getSelectedTreeNode().isLeaf()) { 
              currentVertex.nodeName = nodeNameTextField.getText();
              currentVertex.selectedDescription = quantityDescriptionTextField.getText();
              currentVertex.defaultLabel();

              System.out.println(duplicatedNode(currentVertex.nodeName));
              if (currentVertex.correctDescription.trim().equals(quantityDescriptionTextField.getText().trim())) {
                if(!duplicatedNode(currentVertex.nodeName)) {
                  logger.out(Logger.ACTIVITY, "DescriptionPanel.checkButtonActionPerformed.2");
                  quantityDescriptionTextField.setBackground(new Color(155,250,140));
                  nodeNameTextField.setBackground(new Color(155,250,140));
                  decisionTree.setEnabled(false);
                  undoButton.setEnabled(false);
                  treeSelected = false;
                  checkButton.setEnabled(treeSelected);
                  giveUpButton.setEnabled(false);
                  hintButton.setEnabled(false);
                  currentVertex.setDescriptionButtonStatus(currentVertex.CORRECT);
                  parent.setTitle(currentVertex.label);
                } else {
                  quantityDescriptionTextField.setBackground(Color.pink);
                  nodeNameTextField.setBackground(Color.pink);
                  currentVertex.setDescriptionButtonStatus(currentVertex.WRONG);
                }
              } else {
                  logger.out(Logger.ACTIVITY, "DescriptionPanel.checkButtonActionPerformed.3");
                  quantityDescriptionTextField.setBackground(Color.pink);
                  nodeNameTextField.setBackground(Color.pink);
                  currentVertex.setDescriptionButtonStatus(currentVertex.WRONG);
                 parent.setTitle(currentVertex.nodeName);
                 undoButton.setEnabled(true);
              }
            } else {
              MessageDialog.showMessageDialog(null, true, "This is not a leaf node. Please expand it to get to the leaf node.", graph);
            }
          }
        } else {
            MessageDialog.showMessageDialog(null, true, "This node is the same as another node you've already defined, please choose a different description.", graph);
        }
      }
    }//GEN-LAST:event_checkButtonActionPerformed

public boolean duplicatedNode(String nodeName)
{
      boolean duplicate = false;
      for (int z = 0; z < gc.getGraph().getVertexes().size(); z++) {
        if (((Vertex) gc.getGraph().getVertexes().get(z)).label.equals(nodeName)&& ((Vertex) gc.getGraph().getVertexes().get(z))!=currentVertex) {
          duplicate = true;
          break;
        }
    }
      return duplicate;
}
    /**
     * @author curt
     * @param evt
     */
    private void giveUpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_giveUpButtonActionPerformed
      if (!initializing) {
        boolean duplicate = duplicatedNode(currentVertex.label);

        boolean legalNode=true;
        if(!currentVertex.label.equals("")){
          if(currentVertex.correctDescription.trim().equals(""))
            legalNode=false;
        }

        if(duplicate || currentVertex.label.equals("") || !legalNode)
        {
          for(int i=0;i< gc.listOfVertexes.size();i++)
          {           
            duplicate = duplicatedNode(gc.listOfVertexes.get(i));
            System.out.println(duplicate);
            if(!duplicate) {
              currentVertex.label = gc.listOfVertexes.get(i);
              break;
            }
          }
        }

        logger.concatOut(Logger.ACTIVITY, "No message", "Click giveup button try--"+currentVertex.label);
        String returnMsg=blockSocket.blockQuery(this,"Click giveup button");
        if(returnMsg.trim().startsWith("allow--")){
          currentVertex.label=returnMsg.split("--")[1]; 
        }
        else if(!returnMsg.equals("allow")) //the action is not allowed by meta tutor
        {
          new MetaTutorMsg(returnMsg.split(":")[1],false).setVisible(true);
          return;
        }

        logger.out(Logger.ACTIVITY, "DescriptionPanel.giveUpButtonActionPerformed.1");

        currentVertex.correctEquation = null;
        this.parent.getCorrectAnswers();
        this.parent.getCorrectDescriptionFromFile();
        descriptionAreaLabel.setText(currentVertex.situationDescription);

        nodeNameTextField.setText(currentVertex.label);
        currentVertex.nodeName = currentVertex.label;
        quantityDescriptionTextField.setText(currentVertex.correctDescription);
        currentVertex.selectedDescription = currentVertex.correctDescription;
        quantityDescriptionTextField.setBackground(new Color(252,252,130));
        nodeNameTextField.setBackground(new Color(252,252,130));
        nodeNameTextField.setEnabled(false);
        quantityDescriptionTextField.setEnabled(false);
        decisionTree.setEnabled(false);
        undoButton.setEnabled(false);
        checkButton.setEnabled(false);
        giveUpButton.setEnabled(false);
        hintButton.setEnabled(false);
        parent.getInputsPanel().updateDescription();
        parent.getGraphsPanel().updateDescription();
        currentVertex.setDescriptionButtonStatus(currentVertex.GAVEUP);
        currentVertex.defaultLabel();
        parent.setTitle(currentVertex.label);
      }
    }//GEN-LAST:event_giveUpButtonActionPerformed
    public void collapseAll(javax.swing.JTree tree) {
    int row = tree.getRowCount() - 1;
    while (row >= 1) {
      tree.collapseRow(row);
      row--;
    }
  }

  public void expandTreePath(javax.swing.JTree tree, TreePath treepath) {
    collapseAll(tree);
    decisionTree.scrollPathToVisible(treepath);
    decisionTree.setSelectionPath(treepath);
  }
    private void undoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undoButtonActionPerformed
      //Undo should clear the currentVertex.nodeName field, and the nodeNameTextField, and the currentVertex.correctDescription, AND quantityDescriptionTextField.
      //Basically, the title and the text box where we enter the title.
      if (!initializing) {
        logger.out(Logger.ACTIVITY, "DescriptionPanel.undoButtonActionPerformed.1");
        currentVertex.nodeName = undoName;
//        nodeNameTextField.setText("");
//        currentVertex.selectedDescription = "";
//        quantityDescriptionTextField.setText("");
        
        nodeNameTextField.setBackground(Color.WHITE);       
        quantityDescriptionTextField.setBackground(Color.WHITE);
        decisionTree.setEnabled(true);

        TreePath tp = treePaths.removeLast();
          try
          {
            expandTreePath(decisionTree,treePaths.getLast());
          }
          catch(java.util.NoSuchElementException nee)
          {
//            expandTreePath(decisionTree,tp.getParentPath().getParentPath());
            collapseAll(decisionTree);
          }

//        currentVertex.setTreePath(treePaths.getLast());
        
        undoFlag = false;
        undoButton.setEnabled(undoFlag);
      }
    }//GEN-LAST:event_undoButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel NodeNameLabel;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton checkButton;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JTree decisionTree;
    private javax.swing.JPanel descriptPanel;
    private javax.swing.JLabel descriptionAreaLabel;
    private javax.swing.JScrollPane descriptionScrollPane;
    private javax.swing.JLabel evenMorePreciseLabel;
    private javax.swing.JButton giveUpButton;
    private javax.swing.JButton hintButton;
    private javax.swing.JLabel hintLabel;
    private javax.swing.JPanel hintPanel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField nodeNameTextField;
    private javax.swing.JTextArea quantityDescriptionTextField;
    private javax.swing.JLabel quantityLabel;
    private javax.swing.JLabel referencesLabel;
    private javax.swing.JButton undoButton;
    // End of variables declaration//GEN-END:variables
}
