package amt.gui;

import amt.data.Task;
import amt.graph.Graph;
import amt.log.Logger;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;
import javax.swing.ImageIcon;

/**
 * Dialog box About
 *
 * @author Javier Gonzalez Sanchez
 * @author Maria Elena Chavez Echeagaray
 * @author Megan Kearl
 * @version 20090817
 */
public class HintDialog extends javax.swing.JFrame{

  Task task;
  int hintNumber;
  Graph graph;
  /**
   * Constructor
   *
   * @param parent
   * @param modal
   */
  public HintDialog(java.awt.Frame parent, boolean modal, Task t, int hintNum, Graph g) {
    //super(parent, modal);
    this.task = t;
    this.hintNumber = hintNum;
    if(hintNum == 1)
    {
        this.setTitle("Diagram Hint");
    }
    else this.setTitle("Equations Hint");
    initComponents();
    initImage();
    this.graph = g;
    graph.getHint().add(this);
    this.pack();
    this.setResizable(false);
    this.setAlwaysOnTop(true);
    Dimension size = new Dimension(850, 650);
    this.setSize(size);
    this.setLocationRelativeTo(parent);
  }

  private void initImage()
  {
      Toolkit toolkit = this.getToolkit();
      String level = task.getLevel();
      String hintName = "";
      if(hintNumber == 1)
      {
          hintName = "diagram";
      }
      else hintName = "equations";
      Image img = null;
      if(Integer.parseInt(task.getLevel()) == -1)
      {
          //do nothing
      }
      else if(Integer.parseInt(task.getLevel()) < 10)
      {
         level = "0" + level;
      }
      //get the image
      String taskName = task.getTitle().toLowerCase().replace(" ", "");
      taskName = taskName.replaceAll(",", "");
      taskName = taskName.replaceAll(":", "");
      String hint = "http://amt.asu.edu/rmchris3/hints/" + level + "-" + taskName + "-" + hintName + ".jpg";
      try
      {
          URL testURL = new URL(hint);
          img = toolkit.createImage(testURL);
      }
      catch (Exception ex)
      {
          Logger.getLogger().out(Logger.DEBUG, "HintDialog.initImage.1");
      }

      //resize the image
      ImageIcon icon = new ImageIcon();
      try {
          img = img.getScaledInstance(800, 600, Image.SCALE_DEFAULT);
          icon = new ImageIcon(img);
      } catch (Exception ex3) {
          System.out.println("Error adjusting image" + ex3.toString());
      }

      imageLabel.setIcon(icon);
  }

  @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Panel = new javax.swing.JPanel();
        imageLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        Panel.setBackground(new java.awt.Color(255, 255, 255));
        Panel.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        javax.swing.GroupLayout PanelLayout = new javax.swing.GroupLayout(Panel);
        Panel.setLayout(PanelLayout);
        PanelLayout.setHorizontalGroup(
            PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(imageLabel)
                .addContainerGap(224, Short.MAX_VALUE))
        );
        PanelLayout.setVerticalGroup(
            PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelLayout.createSequentialGroup()
                .addComponent(imageLabel)
                .addContainerGap(324, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Panel;
    private javax.swing.JLabel imageLabel;
    // End of variables declaration//GEN-END:variables

}
