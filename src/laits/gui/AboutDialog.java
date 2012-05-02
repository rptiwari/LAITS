package laits.gui;

/**
 * Dialog box About
 *
 * @author Javier Gonzalez Sanchez
 * @author Maria Elena Chavez Echeagaray
 * @version 20090817
 */
public class AboutDialog extends javax.swing.JDialog {

  /**
   * Constructor
   *
   * @param parent
   * @param modal
   */
  public AboutDialog(java.awt.Frame parent, boolean modal) {
    super(parent, modal);
    this.setTitle("About...");
    initComponents();
    this.versionLabel.setText(laits.Main.VERSION);
    this.setLocationRelativeTo(parent);
    this.pack();
  }

  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    Panel = new javax.swing.JPanel();
    amtLabel = new javax.swing.JLabel();
    asuLogoLabel = new javax.swing.JLabel();
    author1Label = new javax.swing.JLabel();
    author2Label = new javax.swing.JLabel();
    author3Label = new javax.swing.JLabel();
    author4Label = new javax.swing.JLabel();
    author5Label = new javax.swing.JLabel();
    author6Label = new javax.swing.JLabel();
    versionLabel = new javax.swing.JLabel();
    copyRightLabel = new javax.swing.JLabel();
    separator = new javax.swing.JSeparator();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setResizable(false);

    Panel.setBackground(new java.awt.Color(255, 255, 255));

    amtLabel.setFont(new java.awt.Font("Lucida Grande", 1, 13));
    amtLabel.setText("Affective Meta Tutor");

    asuLogoLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/amt/gui/asu.jpeg"))); // NOI18N
    asuLogoLabel.setToolTipText("ASU");

    author1Label.setFont(new java.awt.Font("Lucida Grande", 0, 10));
    author1Label.setText("Javier Gonzalez-Sanchez");

    author2Label.setFont(new java.awt.Font("Lucida Grande", 0, 10));
    author2Label.setText("Maria Elena Chavez-Echeagaray");

    author3Label.setFont(new java.awt.Font("Lucida Grande", 0, 10));
    author3Label.setText("Patrick Lu");

    author4Label.setFont(new java.awt.Font("Lucida Grande", 0, 10));
    author4Label.setText("Sudha Marothu");

    author5Label.setFont(new java.awt.Font("Lucida Grande", 0, 10));
    author5Label.setText("Megan Kearl ");

    author6Label.setFont(new java.awt.Font("Lucida Grande", 0, 10));
    author6Label.setText("Quanwei Zhao");

    versionLabel.setFont(new java.awt.Font("Lucida Grande", 0, 12));
    versionLabel.setText("Version");

    copyRightLabel.setFont(new java.awt.Font("Lucida Grande", 0, 10));
    copyRightLabel.setText("Copyright (c) 2010 ");

    javax.swing.GroupLayout PanelLayout = new javax.swing.GroupLayout(Panel);
    Panel.setLayout(PanelLayout);
    PanelLayout.setHorizontalGroup(
      PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(PanelLayout.createSequentialGroup()
        .addGap(45, 45, 45)
        .addGroup(PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
          .addComponent(amtLabel)
          .addComponent(versionLabel)
          .addComponent(copyRightLabel)
          .addComponent(separator, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(author1Label)
          .addComponent(author2Label)
          .addComponent(author3Label)
          .addComponent(author4Label)
          .addComponent(author5Label)
          .addComponent(author6Label)
          .addComponent(asuLogoLabel))
        .addContainerGap(51, Short.MAX_VALUE))
    );
    PanelLayout.setVerticalGroup(
      PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelLayout.createSequentialGroup()
        .addContainerGap(20, Short.MAX_VALUE)
        .addComponent(asuLogoLabel)
        .addGap(26, 26, 26)
        .addComponent(amtLabel)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(versionLabel)
        .addGap(12, 12, 12)
        .addComponent(copyRightLabel)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(separator, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(author1Label, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(4, 4, 4)
        .addComponent(author2Label, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(author3Label, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(author4Label, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(author5Label, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(author6Label, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(21, 21, 21))
    );

    PanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {author1Label, author2Label, author3Label, author4Label, author5Label, author6Label});

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(Panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(Panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JPanel Panel;
  private javax.swing.JLabel amtLabel;
  private javax.swing.JLabel asuLogoLabel;
  private javax.swing.JLabel author1Label;
  private javax.swing.JLabel author2Label;
  private javax.swing.JLabel author3Label;
  private javax.swing.JLabel author4Label;
  private javax.swing.JLabel author5Label;
  private javax.swing.JLabel author6Label;
  private javax.swing.JLabel copyRightLabel;
  private javax.swing.JSeparator separator;
  private javax.swing.JLabel versionLabel;
  // End of variables declaration//GEN-END:variables

}
