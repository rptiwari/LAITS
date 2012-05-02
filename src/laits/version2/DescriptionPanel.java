/*
 * DescriptionPanel.java
 *
 * Created on Nov 21, 2010, 10:23:38 AM
 */
package laits.version2;

import laits.BlockSocket;
import laits.MetaTutorMsg;
import laits.comm.CommException;
import laits.graph.DecisionTreeNode;
import laits.data.TaskFactory;
import laits.graph.GraphCanvas;
import laits.data.Task;
import laits.graph.Vertex;
import laits.graph.Graph;
import laits.gui.MessageDialog;
import laits.log.Logger;
import java.awt.Color;
import java.util.Enumeration;
import java.util.LinkedList;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import laits.graph.*;

/**
 *
 * @author Megan
 * @author Helen
 * @author Ram
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
    BlockSocket blockSocket = BlockSocket.getBlockSocket();
    DefaultMutableTreeNode root = null;
    DefaultTreeModel model = null;
    DecisionTree dTree,savedDecisionTree;
    String prevNodeName;
    
    /**
     * Creates new form DescriptionPanel
     */
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
        System.out.println("StartD: " + currentVertex.correctDescription);
        if (!currentVertex.correctDescription.equals("")) {
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
        if (getSelectedTreeNode() == null) {
            treeSelected = false;
            checkButton.setEnabled(treeSelected);
        }

        // Saving previous values
        parent.savedNodeName = currentVertex.nodeName;
        parent.savedDescription = currentVertex.correctDescription;
        prevNodeName = currentVertex.nodeName;
        
    }

    public DefaultMutableTreeNode getSelectedTreeNode() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) decisionTree.getLastSelectedPathComponent();
        return node;
    }

    /**
     * This method initializes the panel if the user has already chosen a
     * description, checked, given up, or chose a wrong answer
     */
    public void initValues() {
        initializing = true;

        //decisionTree.setEnabled(false);
        decisionTree.setBackground(new Color(240, 240, 240));
        quantityDescriptionTextField.setEditable(true);
        nodeNameTextField.setEditable(true);
        //descriptPanel.setBackground(new Color(240, 240, 240));

        initializing = false;

    }

    public void initDescription() {
        //descriptionAreaLabel.setText(currentVertex.situationDescription);
    }

    /**
     * This method parse the decision tree of the task contained in the
     * LinkedList
     *
     * @param task
     */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = DescriptionPanel.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
    
    public void initTree(Task task) {
        savedDecisionTree = DecisionTree.getDecisionTree();
        dTree = DecisionTree.getDecisionTree();
        root = dTree.getRoot();   
        ImageIcon correctLeafIcon = createImageIcon("/amt/images/correct.gif");
        ImageIcon inCorrectLeafIcon = createImageIcon("/amt/images/incorrect.gif");
        
        if (correctLeafIcon != null && inCorrectLeafIcon != null) {
            decisionTree.setCellRenderer(new DecisionTreeRenderer(correctLeafIcon, inCorrectLeafIcon));
        }
        model = new DefaultTreeModel(root);
        decisionTree.setModel(model);
        
        jScrollPane2.setViewportView(decisionTree);
        
        TreeNode root = (TreeNode) decisionTree.getModel().getRoot();
        expandAll(decisionTree, new TreePath(root));
        
    }

    public void initTreeSelectionListener() {
        decisionTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        decisionTree.addTreeSelectionListener(this);
    }

    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = getSelectedTreeNode();
        DecisionTreeNode n;
        
        try {
            if (node != null && node.isLeaf()) {
                    
                    //Populate the quantity description label
                    String description = "";
                    TreeNode[] ancestors = node.getPath();
                    for (int i = 0; i < ancestors.length; i++) {
                        
                        if (!ancestors[i].toString().equalsIgnoreCase("Node")) {
                            if (!description.equals("")) {
                                description += " "+ancestors[i].toString();
                            } else {
                                description = ancestors[i].toString();
                            }
                        }
                    }
                    
                    quantityDescriptionTextField.setText(description);
                    currentVertex.selectedDescription = description;
                    parent.getInputsPanel().updateDescription();
                    parent.getGraphsPanel().updateDescription();

                    
                }
           
        } catch (ClassCastException cce) {
        }
    }

    public void setquantityDescriptionTextField(String desc) {
        this.quantityDescriptionTextField.setText(desc);
        quantityDescriptionTextField.setBackground(new Color(155, 250, 140));
        nodeNameTextField.setBackground(new Color(155, 250, 140));
        decisionTree.setEnabled(false);

        checkButton.setEnabled(false);
        giveUpButton.setEnabled(false);

        currentVertex.setDescriptionButtonStatus(currentVertex.CORRECT);
        parent.setTitle(currentVertex.label);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        descGroup = new javax.swing.ButtonGroup();
        contentPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        decisionTree = new javax.swing.JTree();
        evenMorePreciseLabel = new javax.swing.JLabel();
        buttonPanel = new javax.swing.JPanel();
        nodeNameTextField = new javax.swing.JTextField();
        NodeNameLabel = new javax.swing.JLabel();
        jRadioCorrect = new javax.swing.JRadioButton();
        jRadioInCorrect = new javax.swing.JRadioButton();
        buttonAddDesc = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        quantityDescriptionTextField = new javax.swing.JTextArea();
        checkButton = new javax.swing.JButton();
        giveUpButton = new javax.swing.JButton();
        buttonOk = new javax.swing.JButton();
        buttonCancel = new javax.swing.JButton();
        buttonDelete = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

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
        decisionTree.setEditable(true);
        decisionTree.setScrollsOnExpand(true);
        jScrollPane2.setViewportView(decisionTree);

        evenMorePreciseLabel.setText("Choose the more precise description for the quantity:");

        buttonPanel.setMaximumSize(new java.awt.Dimension(540, 92));
        buttonPanel.setMinimumSize(new java.awt.Dimension(540, 92));

        nodeNameTextField.setEditable(false);
        nodeNameTextField.setDisabledTextColor(new java.awt.Color(102, 102, 102));
        nodeNameTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nodeNameTextFieldActionPerformed(evt);
            }
        });
        nodeNameTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                nodeNameTextFieldKeyReleased(evt);
            }
        });

        NodeNameLabel.setText("Node Name:");

        javax.swing.GroupLayout buttonPanelLayout = new javax.swing.GroupLayout(buttonPanel);
        buttonPanel.setLayout(buttonPanelLayout);
        buttonPanelLayout.setHorizontalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, buttonPanelLayout.createSequentialGroup()
                .addComponent(NodeNameLabel)
                .addGap(35, 35, 35)
                .addComponent(nodeNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 417, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        buttonPanelLayout.setVerticalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nodeNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(NodeNameLabel))
                .addContainerGap(36, Short.MAX_VALUE))
        );

        descGroup.add(jRadioCorrect);
        jRadioCorrect.setText("Correct");

        descGroup.add(jRadioInCorrect);
        jRadioInCorrect.setText("InCorrect");
        jRadioInCorrect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioInCorrectActionPerformed(evt);
            }
        });

        buttonAddDesc.setText("Add");
        buttonAddDesc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddDescActionPerformed(evt);
            }
        });

        quantityDescriptionTextField.setColumns(20);
        quantityDescriptionTextField.setRows(3);
        jScrollPane1.setViewportView(quantityDescriptionTextField);

        checkButton.setText("Check");
        checkButton.setEnabled(false);
        checkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkButtonActionPerformed(evt);
            }
        });

        giveUpButton.setText("Give Up");
        giveUpButton.setEnabled(false);
        giveUpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                giveUpButtonActionPerformed(evt);
            }
        });

        buttonOk.setText("Ok");
        buttonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOkActionPerformed(evt);
            }
        });

        buttonCancel.setText("Cancel");
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });

        buttonDelete.setText("Delete Node");
        buttonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDeleteActionPerformed(evt);
            }
        });

        jLabel1.setText("Precise description of the quantity:");

        javax.swing.GroupLayout contentPanelLayout = new javax.swing.GroupLayout(contentPanel);
        contentPanel.setLayout(contentPanelLayout);
        contentPanelLayout.setHorizontalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentPanelLayout.createSequentialGroup()
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(contentPanelLayout.createSequentialGroup()
                        .addGap(92, 92, 92)
                        .addComponent(checkButton)
                        .addGap(18, 18, 18)
                        .addComponent(giveUpButton)
                        .addGap(18, 18, 18)
                        .addComponent(buttonOk, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(buttonCancel)
                        .addGap(18, 18, 18)
                        .addComponent(buttonDelete))
                    .addGroup(contentPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 620, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(contentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(contentPanelLayout.createSequentialGroup()
                        .addComponent(buttonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(contentPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jRadioCorrect)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jRadioInCorrect)
                        .addGap(31, 31, 31)
                        .addComponent(buttonAddDesc, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32))
                    .addGroup(contentPanelLayout.createSequentialGroup()
                        .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 620, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(evenMorePreciseLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 515, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())))
        );
        contentPanelLayout.setVerticalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(evenMorePreciseLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 51, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(13, 13, 13)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioCorrect)
                    .addComponent(jRadioInCorrect)
                    .addComponent(buttonAddDesc))
                .addGap(22, 22, 22)
                .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkButton)
                    .addComponent(giveUpButton)
                    .addComponent(buttonOk)
                    .addComponent(buttonCancel)
                    .addComponent(buttonDelete))
                .addGap(44, 44, 44))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(contentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 11, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void nodeNameTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nodeNameTextFieldActionPerformed
        currentVertex.nodeName = nodeNameTextField.getText();
    }//GEN-LAST:event_nodeNameTextFieldActionPerformed

    /**
     * &author curt
     *
     * @param evt
     */
    private void checkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkButtonActionPerformed
        
    }//GEN-LAST:event_checkButtonActionPerformed

    public boolean duplicatedNode(String nodeName) {
        boolean duplicate = false;
        for (int z = 0; z < gc.getGraph().getVertexes().size(); z++) {
            if (((Vertex) gc.getGraph().getVertexes().get(z)).label.equals(nodeName) && ((Vertex) gc.getGraph().getVertexes().get(z)) != currentVertex) {
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

            boolean legalNode = true;
            if (!currentVertex.label.equals("")) {
                if (currentVertex.correctDescription.trim().equals("")) {
                    legalNode = false;
                }
            }

            if (duplicate || currentVertex.label.equals("") || !legalNode) {
                for (int i = 0; i < gc.listOfVertexes.size(); i++) {
                    duplicate = duplicatedNode(gc.listOfVertexes.get(i));
                    System.out.println(duplicate);
                    if (!duplicate) {
                        currentVertex.label = gc.listOfVertexes.get(i);
                        break;
                    }
                }
            }

            logger.concatOut(Logger.ACTIVITY, "No message", "Click giveup button try--" + currentVertex.label);
            String returnMsg = blockSocket.blockQuery(this, "Click giveup button");
            if (returnMsg.trim().startsWith("allow--")) {
                currentVertex.label = returnMsg.split("--")[1];
            } else if (!returnMsg.equals("allow")) //the action is not allowed by meta tutor
            {
                new MetaTutorMsg(returnMsg.split(":")[1], false).setVisible(true);
                return;
            }

            logger.out(Logger.ACTIVITY, "DescriptionPanel.giveUpButtonActionPerformed.1");

            currentVertex.correctEquation = null;
            this.parent.getCorrectAnswers();
            this.parent.getCorrectDescriptionFromFile();
            //descriptionAreaLabel.setText(currentVertex.situationDescription);

            nodeNameTextField.setText(currentVertex.label);
            currentVertex.nodeName = currentVertex.label;
            quantityDescriptionTextField.setText(currentVertex.correctDescription);
            currentVertex.selectedDescription = currentVertex.correctDescription;
            quantityDescriptionTextField.setBackground(new Color(252, 252, 130));
            nodeNameTextField.setBackground(new Color(252, 252, 130));
            nodeNameTextField.setEnabled(false);
            quantityDescriptionTextField.setEnabled(false);
            decisionTree.setEnabled(false);

            checkButton.setEnabled(false);
            giveUpButton.setEnabled(false);

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
    private void buttonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOkActionPerformed
        // This button will read the value of name and description and store then in current vertex

        // Validating Input
        if (nodeNameTextField.getText().isEmpty()) {
            MessageDialog.showMessageDialog(null, true, "Node name can not be empty", graph);
            return;
        }
        if (quantityDescriptionTextField.getText().isEmpty()) {
            MessageDialog.showMessageDialog(null, true, "Quantity Description can not be empty", graph);
            return;
        }
        
        // Check for duplicate node name
        currentVertex.label = nodeNameTextField.getText();
        currentVertex.correctDescription = quantityDescriptionTextField.getText();
        currentVertex.correctEquation = null;
        currentVertex.selectedDescription = currentVertex.correctDescription;
        currentVertex.nodeName = currentVertex.label;

        if (duplicatedNode(currentVertex.nodeName)) {
            MessageDialog.showMessageDialog(null, true, "A node with name " + currentVertex.nodeName + "already exists in the graph", graph);
            nodeNameTextField.setText("");
            currentVertex.label = "";
            currentVertex.nodeName = currentVertex.label;
            return;
        }
        
        if(!prevNodeName.isEmpty() && prevNodeName.compareTo(currentVertex.nodeName)!=0){
            System.out.println("Updating nodeName with Prev = "+prevNodeName +"  and new= "+currentVertex.nodeName);            
            dTree.updateNodeName(prevNodeName, currentVertex.nodeName);
        }
        // Check for description tree
        if(!dTree.hasTreeCreated(nodeNameTextField.getText())){
            MessageDialog.showMessageDialog(null, true, "Please add the correct description for this node - use Add button", graph);
            return;
        }
        
        currentVertex.defaultLabel();
        parent.setTitle(currentVertex.label);
        if ((!currentVertex.nodeName.isEmpty()) && (!currentVertex.correctDescription.isEmpty())) {
            currentVertex.setDescriptionButtonStatus(currentVertex.CORRECT);
        } else {
            currentVertex.setDescriptionButtonStatus(currentVertex.NOSTATUS);
        }
        
        
        
        // Printing whole decision tree
        dTree.getAll();
        
        parent.processOkAction();

    }//GEN-LAST:event_buttonOkActionPerformed

    private void buttonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDeleteActionPerformed

        graph.delVertex(currentVertex);
        
        // Removing corrospoding nodes from the Decision Tree
        dTree.deleteLeavesOfNode(nodeNameTextField.getText());
        
        currentVertex.nodeName = "";
        parent.isDeleteAction = true;
        parent.windowClosing(null);
        
    }//GEN-LAST:event_buttonDeleteActionPerformed

    private void nodeNameTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nodeNameTextFieldKeyReleased
    }//GEN-LAST:event_nodeNameTextFieldKeyReleased

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
        DecisionTree.undoDecisionTreeChanges();
        parent.processCancelAction();
    }//GEN-LAST:event_buttonCancelActionPerformed

    private void jRadioInCorrectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioInCorrectActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioInCorrectActionPerformed

    private void buttonAddDescActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddDescActionPerformed
        // TODO add your handling code here:
        // Check that nodeName should be entered.
        if(nodeNameTextField.getText().isEmpty()){
            MessageDialog.showMessageDialog(null, true, "Please enter the node name.", graph);
            return;
        }else{
            prevNodeName = nodeNameTextField.getText();
        }
        // Check for duplicate node name
        if (duplicatedNode(currentVertex.nodeName)) {
            MessageDialog.showMessageDialog(null, true, "A node with name " + currentVertex.nodeName + "already exists in the graph", graph);
            return;
        }
        
        // Check if Correct or Incorrect option button is selected
        if(jRadioCorrect.isSelected()){
            if(dTree.add(quantityDescriptionTextField.getText(), nodeNameTextField.getText(),true)==1){
                MessageDialog.showMessageDialog(null, true, "Node "+nodeNameTextField.getText()+" already has a correct description.", graph);
            }
        }else if(jRadioInCorrect.isSelected()){
            dTree.add(quantityDescriptionTextField.getText(), nodeNameTextField.getText(),false);
        }else{
            MessageDialog.showMessageDialog(null, true, "Please select the type", graph);
        }
        
        model.reload();
        
        TreeNode root = (TreeNode) decisionTree.getModel().getRoot();
        expandAll(decisionTree, new TreePath(root));
        
    }//GEN-LAST:event_buttonAddDescActionPerformed

    private boolean validateNodeName() {

        return false;
    }
    
    private void expandAll(JTree tree, TreePath parent) {
    TreeNode node = (TreeNode) parent.getLastPathComponent();
    if (node.getChildCount() >= 0) {
      for (Enumeration e = node.children(); e.hasMoreElements();) {
        TreeNode n = (TreeNode) e.nextElement();
        TreePath path = parent.pathByAddingChild(n);
        expandAll(tree, path);
      }
    }
    tree.expandPath(parent);
    // tree.collapsePath(parent);
  }

      
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel NodeNameLabel;
    private javax.swing.JButton buttonAddDesc;
    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonDelete;
    private javax.swing.JButton buttonOk;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton checkButton;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JTree decisionTree;
    private javax.swing.ButtonGroup descGroup;
    private javax.swing.JLabel evenMorePreciseLabel;
    private javax.swing.JButton giveUpButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JRadioButton jRadioCorrect;
    private javax.swing.JRadioButton jRadioInCorrect;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField nodeNameTextField;
    private javax.swing.JTextArea quantityDescriptionTextField;
    // End of variables declaration//GEN-END:variables
}
