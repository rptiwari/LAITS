/*
 * GraphsPanel.java
 *
 * Created on Nov 21, 2010, 10:24:20 AM
 */
package laits.version2;

import laits.comm.CommException;
import laits.data.Task;
import laits.data.TaskFactory;
import laits.graph.Graph;
import laits.graph.GraphCanvas;
import laits.graph.Vertex;
import laits.log.Logger;
import laits.plot.PlotPanel;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Megana
 */
public class GraphsPanel extends javax.swing.JPanel {

  JPanel grafica;
  Vertex currentVertex;
  Graph graph;
  GraphCanvas gc;
  Image correctAnswer = null;
  Task t;
  Logger logger = Logger.getLogger();
  //the width and height of the panel
  int width, height;

  /** Creates new form GraphsPanel */
  public GraphsPanel(TabbedGUI parent, Vertex v, Graph g, GraphCanvas gc) {
    TaskFactory server = null;
    try {
      server = TaskFactory.getInstance();
    } catch (CommException ex) {
      //ADD LOGGER
    }
    t = server.getActualTask();
    this.gc = gc;
    this.currentVertex = v;
    this.graph = g;
    initComponents();
    //correctAnswerPanel = new CorrectAnswerPanel(this);
    //nodeDescriptionLabel.setText("<html>" + node.correctDescription + "</html>");
    hintLabel.setForeground(new Color(240, 240, 240));
    hintButton.setVisible(false);
    hintLabel.setVisible(false);

    // Drawing Author's graph
    graphPanel = new PlotPanel(this.currentVertex, t.getStartTime(), t.getEndTime(), graph, t.getUnitTime());
    userGraphLabel.setVisible(false);
    correctGraphLabel.setVisible(false);
    correctAnswerPanel.setVisible(false);
    // Code disabled by Ram
//    //correctAnswerPanel.repaint(0);
//    correctAnswerPanel = new PlotPanel(this.currentVertex, t.getStartTime(), t.getTitle(), t.getUnitTime());
//    if (grafica != null) {
//      graphPanel.remove(grafica);
//    }
//    if ((gc.getModelHasBeenRun() == true) && (!v.type.equalsIgnoreCase("None")) && (v.equation != null)) {
//      graphPanel = new PlotPanel(this.currentVertex, t.getStartTime(), t.getEndTime(), graph, t.getUnitTime());
//    }

    updateDescription();
    testResetLayout();
  }

  public void testResetLayout() {

    allGraphsPanel.removeAll();
    allGraphsPanel.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    //Only display the correct graph once the user has assigned the correct name and description to the node
    if (currentVertex.nodeName.equals(currentVertex.label)) {
      //Add the correct graph label
      c.fill = GridBagConstraints.HORIZONTAL;
      c.weighty = 1;
      c.gridx = 0;
      c.gridy = 0;
      c.weightx = 0.0;
      allGraphsPanel.add(userGraphLabel, c);
      //Add the correct graph
      c.fill = GridBagConstraints.HORIZONTAL;
      c.gridx = 0;
      c.gridy = 1;
      c.weightx = 0.0;
      allGraphsPanel.add(graphPanel, c);
    }
    //Add the predicted values label
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weighty = 1;
    c.gridx = 0;
    c.gridy = 2;
    c.weightx = 0.0;
    //allGraphsPanel.add(predictedValuesLabel, c);
    //Add the predicted values item box
    c.fill = GridBagConstraints.HORIZONTAL;
    //c.weighty = 1;
    c.gridx = 0;
    c.gridy = 3;
    c.weightx = 0.0;
    //allGraphsPanel.add(itemBox, c);
    //Add the user graph label
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weighty = 1;
    c.gridx = 0;
    c.gridy = 4;
    c.weightx = 0.0;
    allGraphsPanel.add(correctGraphLabel, c);
    //Add the user graph
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.gridy = 5;
    c.weightx = 0.0;
    allGraphsPanel.add(correctAnswerPanel, c);
  }

  /**
   * This method is used when the user selects a name and description for the node
   * currently being edited
   */
  public void updateDescription() {
    descriptionLabel.setText("<html><b>Description:</b> " + currentVertex.correctDescription + "</html>");
  }

  public void resetLayout() {
    this.removeAll();
    graphPanel.setPreferredSize(new java.awt.Dimension(300, 200));
    correctAnswerPanel.setPreferredSize(new java.awt.Dimension(300, 200));
    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(buttonPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(userGraphLabel).addContainerGap(522, Short.MAX_VALUE)).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(graphPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(correctAnswerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addGroup(layout.createSequentialGroup().addComponent(correctGraphLabel).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(nodeDescriptionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 41, Short.MAX_VALUE).addComponent(descriptionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))).addContainerGap()));
    layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(correctGraphLabel).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(correctAnswerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(userGraphLabel).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(graphPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addComponent(nodeDescriptionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)).addGroup(layout.createSequentialGroup().addComponent(descriptionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(150, 150, 150))).addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)));
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        nodeDescriptionLabel = new javax.swing.JLabel();
        buttonPanel = new javax.swing.JPanel();
        hintButton = new javax.swing.JButton();
        hintPanel = new javax.swing.JPanel();
        hintLabel = new javax.swing.JLabel();
        allGraphsPanel = new javax.swing.JPanel();
        correctGraphLabel = new javax.swing.JLabel();
        correctAnswerPanel = new PlotPanel(this.currentVertex, t.getStartTime(), t.getTitle(), t.getUnitTime());
        userGraphLabel = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(15, 0), new java.awt.Dimension(15, 0), new java.awt.Dimension(15, 32767));
        graphPanel = new javax.swing.JPanel();
        correctGraphLabel1 = new javax.swing.JLabel();
        correctGraphLabel2 = new javax.swing.JLabel();
        descriptionLabel = new javax.swing.JLabel();

        nodeDescriptionLabel.setText("<html></html>");

        buttonPanel.setMaximumSize(new java.awt.Dimension(577, 92));
        buttonPanel.setMinimumSize(new java.awt.Dimension(577, 92));

        hintButton.setText("Hint");
        hintButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hintButtonActionPerformed(evt);
            }
        });

        hintPanel.setMaximumSize(new java.awt.Dimension(567, 45));
        hintPanel.setMinimumSize(new java.awt.Dimension(567, 45));

        hintLabel.setBackground(new java.awt.Color(0, 0, 0));
        hintLabel.setText("Hint");

        javax.swing.GroupLayout hintPanelLayout = new javax.swing.GroupLayout(hintPanel);
        hintPanel.setLayout(hintPanelLayout);
        hintPanelLayout.setHorizontalGroup(
            hintPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(hintPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(hintLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 531, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        hintPanelLayout.setVerticalGroup(
            hintPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(hintLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout buttonPanelLayout = new javax.swing.GroupLayout(buttonPanel);
        buttonPanel.setLayout(buttonPanelLayout);
        buttonPanelLayout.setHorizontalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(hintButton)
                .addGap(51, 51, 51)
                .addComponent(hintPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 503, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        buttonPanelLayout.setVerticalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(hintPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, buttonPanelLayout.createSequentialGroup()
                .addContainerGap(62, Short.MAX_VALUE)
                .addComponent(hintButton)
                .addContainerGap())
        );

        correctGraphLabel.setText("Correct Graph:");

        correctAnswerPanel.setMaximumSize(new java.awt.Dimension(286, 99));

        javax.swing.GroupLayout correctAnswerPanelLayout = new javax.swing.GroupLayout(correctAnswerPanel);
        correctAnswerPanel.setLayout(correctAnswerPanelLayout);
        correctAnswerPanelLayout.setHorizontalGroup(
            correctAnswerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 417, Short.MAX_VALUE)
        );
        correctAnswerPanelLayout.setVerticalGroup(
            correctAnswerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 165, Short.MAX_VALUE)
        );

        userGraphLabel.setText("User's Graph:");

        graphPanel.setMaximumSize(new java.awt.Dimension(286, 99));
        graphPanel.setPreferredSize(new java.awt.Dimension(286, 99));

        javax.swing.GroupLayout graphPanelLayout = new javax.swing.GroupLayout(graphPanel);
        graphPanel.setLayout(graphPanelLayout);
        graphPanelLayout.setHorizontalGroup(
            graphPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 409, Short.MAX_VALUE)
        );
        graphPanelLayout.setVerticalGroup(
            graphPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 109, Short.MAX_VALUE)
        );

        correctGraphLabel1.setText("     ");

        correctGraphLabel2.setText("                   ");

        javax.swing.GroupLayout allGraphsPanelLayout = new javax.swing.GroupLayout(allGraphsPanel);
        allGraphsPanel.setLayout(allGraphsPanelLayout);
        allGraphsPanelLayout.setHorizontalGroup(
            allGraphsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(allGraphsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(allGraphsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(correctAnswerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(graphPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 409, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(userGraphLabel)
                    .addComponent(correctGraphLabel)
                    .addComponent(correctGraphLabel1)
                    .addComponent(correctGraphLabel2))
                .addContainerGap(61, Short.MAX_VALUE))
            .addGroup(allGraphsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(allGraphsPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, 447, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(31, Short.MAX_VALUE)))
        );
        allGraphsPanelLayout.setVerticalGroup(
            allGraphsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(allGraphsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(userGraphLabel)
                .addGap(18, 18, 18)
                .addComponent(graphPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 218, Short.MAX_VALUE)
                .addComponent(correctGraphLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(correctGraphLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(correctGraphLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(correctAnswerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(allGraphsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(allGraphsPanelLayout.createSequentialGroup()
                    .addGap(303, 303, 303)
                    .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(262, Short.MAX_VALUE)))
        );

        descriptionLabel.setText("<html><b>Description:</b></html>");
        descriptionLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(descriptionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(allGraphsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(287, 287, 287)
                        .addComponent(nodeDescriptionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(descriptionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(allGraphsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(nodeDescriptionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(286, 286, 286)))
                .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

  private void hintButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hintButtonActionPerformed
    if (hintLabel.getForeground().equals(Color.BLACK)) {
      hintLabel.setForeground(new Color(240, 240, 240));
      hintPanel.setBackground(new Color(240, 240, 240));
      logger.out(Logger.ACTIVITY, "GraphsPanel.hintButtonActionPerformed.1");
    } else {
      hintLabel.setForeground(Color.BLACK);
      hintPanel.setBackground(new Color(255, 204, 0));
      logger.out(Logger.ACTIVITY, "GraphsPanel.hintButtonActionPerformed.2");
    }
}//GEN-LAST:event_hintButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel allGraphsPanel;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JPanel correctAnswerPanel;
    private javax.swing.JLabel correctGraphLabel;
    private javax.swing.JLabel correctGraphLabel1;
    private javax.swing.JLabel correctGraphLabel2;
    private javax.swing.JLabel descriptionLabel;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JPanel graphPanel;
    private javax.swing.JButton hintButton;
    private javax.swing.JLabel hintLabel;
    private javax.swing.JPanel hintPanel;
    private javax.swing.JLabel nodeDescriptionLabel;
    private javax.swing.JLabel userGraphLabel;
    // End of variables declaration//GEN-END:variables
}
