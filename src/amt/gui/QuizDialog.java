package amt.gui;

import amt.Main;
import amt.comm.CommException;
import amt.graph.Graph;
import amt.graph.GraphCanvas;
import amt.log.Logger;
import amt.data.Quiz;
import amt.data.TaskFactory;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.ImageIcon;

/**
 * Dialog Box for the Quiz
 *
 * @author Javier Gonzalez Sanchez
 * @author Megan Kearl
 */
public class QuizDialog extends javax.swing.JDialog {
  Graph graph;
  GraphCanvas graphCanvas;
  private int iconSize = 30;
  private String title = "";
  private int correct = 0;
  private int questions = 0;
  private boolean lastProb = false;
  static Logger logs = Logger.getLogger();

  public QuizDialog(Frame parent, boolean modal, Quiz quiz, String title,Graph g, GraphCanvas gc, boolean lastProblemInLevel) {
    super(parent, modal);
    this.title = title;
    this.graph = g;
    this.graphCanvas = gc;
    this.lastProb = lastProblemInLevel;
    initComponents();
    initLabelPreferences();
    advanceButton.setVisible(false);
    for (int i = 0; i < quiz.getQuestion().size(); i++) {
      addQuestion(i, quiz.getQuestion().get(i));
      if(i == 0)
      {
          question1.setVisible(true);
          answer1.setVisible(true);
          result1.setVisible(true);
          correctAns1.setVisible(true);
      }
      else if(i == 1)
      {
          questionTwo.setVisible(true);
          answerTwo.setVisible(true);
          resultTwo.setVisible(true);
          correctAns2.setVisible(true);
      }
      else if(i == 2)
      {
          questionThree.setVisible(true);
          answerThree.setVisible(true);
          resultThree.setVisible(true);
          correctAns3.setVisible(true);
      }
      else if(i == 3)
      {
          questionFour.setVisible(true);
          answerFour.setVisible(true);
          resultFour.setVisible(true);
          correctAns4.setVisible(true);
      }
    }
    for (int i = 0; i < quiz.getCorrectAnswer().size(); i ++)
    {
        addCorrectAnswer(i, quiz.getCorrectAnswer().get(i));
    }
    for(int i = 0; i < quiz.getQuestion().size(); i++)
    {

    }
    this.setLocationRelativeTo(parent);
    graph.getQuiz().add(this);
    this.pack();
  }

  private void initLabelPreferences()
  {
      question1.setVisible(false);
      answer1.setVisible(false);
      result1.setVisible(false);
      correctAns1.setVisible(false);
      questionTwo.setVisible(false);
      answerTwo.setVisible(false);
      resultTwo.setVisible(false);
      correctAns2.setVisible(false);
      questionThree.setVisible(false);
      answerThree.setVisible(false);
      resultThree.setVisible(false);
      correctAns3.setVisible(false);
      questionFour.setVisible(false);
      answerFour.setVisible(false);
      resultFour.setVisible(false);
      correctAns4.setVisible(false);
  }

  @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        question1 = new javax.swing.JLabel();
        questionTwo = new javax.swing.JLabel();
        questionThree = new javax.swing.JLabel();
        tryAgainButton = new javax.swing.JButton();
        answer1 = new javax.swing.JLabel();
        answerTwo = new javax.swing.JLabel();
        answerThree = new javax.swing.JLabel();
        result1 = new javax.swing.JLabel();
        resultTwo = new javax.swing.JLabel();
        resultThree = new javax.swing.JLabel();
        correctAns1 = new javax.swing.JLabel();
        correctAns2 = new javax.swing.JLabel();
        correctAns3 = new javax.swing.JLabel();
        questionFour = new javax.swing.JLabel();
        answerFour = new javax.swing.JLabel();
        correctAns4 = new javax.swing.JLabel();
        resultFour = new javax.swing.JLabel();
        continueButton = new javax.swing.JButton();
        advanceButton = new javax.swing.JButton();
        giveUpLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 1, 24));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Quiz - " + title);

        question1.setFont(new java.awt.Font("Lucida Grande", 0, 12));
        question1.setText("Question 1");

        questionTwo.setFont(new java.awt.Font("Lucida Grande", 0, 12));
        questionTwo.setText("Question 2");

        questionThree.setFont(new java.awt.Font("Lucida Grande", 0, 12));
        questionThree.setText("Question 3");

        tryAgainButton.setText("More practice, same level");
        tryAgainButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tryAgainButtonActionPerformed(evt);
            }
        });

        answer1.setFont(new java.awt.Font("Lucida Grande", 0, 12));
        answer1.setText("Answer 1");

        answerTwo.setFont(new java.awt.Font("Lucida Grande", 0, 12));
        answerTwo.setText("Answer 2");

        answerThree.setFont(new java.awt.Font("Lucida Grande", 0, 12));
        answerThree.setText("Answer 3");

        result1.setText("Result 1");

        resultTwo.setText("Result 2");

        resultThree.setText("Result 3");

        correctAns1.setFont(new java.awt.Font("Lucida Grande", 0, 12));
        correctAns1.setText("correctAns");

        correctAns2.setFont(new java.awt.Font("Lucida Grande", 0, 12));
        correctAns2.setText("correctAns");

        correctAns3.setFont(new java.awt.Font("Lucida Grande", 0, 12));
        correctAns3.setText("correctAns");

        questionFour.setFont(new java.awt.Font("Lucida Grande", 0, 12));
        questionFour.setText("Question 4");

        answerFour.setFont(new java.awt.Font("Lucida Grande", 0, 12));
        answerFour.setText("Answer 4");

        correctAns4.setFont(new java.awt.Font("Lucida Grande", 0, 12));
        correctAns4.setText("correctAns");

        resultFour.setText("Result 4");

        continueButton.setText("Continue");
        continueButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                continueButtonActionPerformed(evt);
            }
        });

        advanceButton.setText("Level up");
        advanceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                advanceButtonActionPerformed(evt);
            }
        });

        giveUpLabel.setFont(new java.awt.Font("Lucida Grande", 0, 12));
        giveUpLabel.setText("Would you like to give up?");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 436, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(108, Short.MAX_VALUE))
            .add(layout.createSequentialGroup()
                .add(28, 28, 28)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(correctAns4)
                        .addContainerGap())
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(correctAns3)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(correctAns2)
                                    .add(correctAns1)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                            .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                                                .add(answerFour)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 279, Short.MAX_VALUE)
                                                .add(resultFour))
                                            .add(layout.createSequentialGroup()
                                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                    .add(answer1)
                                                    .add(answerThree))
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 279, Short.MAX_VALUE)
                                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                                    .add(result1)
                                                    .add(resultTwo)
                                                    .add(resultThree))))
                                        .add(89, 89, 89))))
                            .add(org.jdesktop.layout.GroupLayout.LEADING, giveUpLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 443, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(52, 52, 52))
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, questionThree, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, questionFour, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 336, Short.MAX_VALUE))
                        .addContainerGap())
                    .add(layout.createSequentialGroup()
                        .add(question1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 386, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
            .add(layout.createSequentialGroup()
                .add(154, 154, 154)
                .add(advanceButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(continueButton)
                    .add(tryAgainButton))
                .addContainerGap(155, Short.MAX_VALUE))
            .add(layout.createSequentialGroup()
                .add(28, 28, 28)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(questionTwo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 370, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(answerTwo))
                .addContainerGap(158, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .add(13, 13, 13)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(2, 2, 2)
                .add(question1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 49, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(result1)
                    .add(answer1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(8, 8, 8)
                .add(correctAns1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(questionTwo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 43, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(answerTwo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(resultTwo))
                .add(9, 9, 9)
                .add(correctAns2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(questionThree, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 46, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(answerThree, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(resultThree))
                .add(5, 5, 5)
                .add(correctAns3)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(questionFour, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 41, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(answerFour, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(resultFour))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(correctAns4)
                .add(18, 18, 18)
                .add(giveUpLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 67, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(continueButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(tryAgainButton)
                    .add(advanceButton))
                .addContainerGap(48, Short.MAX_VALUE))
        );

        question1.getAccessibleContext().setAccessibleName("questionOne");
        questionTwo.getAccessibleContext().setAccessibleName("questionTwo");
        questionThree.getAccessibleContext().setAccessibleName("questionThree");
        answer1.getAccessibleContext().setAccessibleName("answerOne");
        answerTwo.getAccessibleContext().setAccessibleName("answerTwo");
        answerThree.getAccessibleContext().setAccessibleName("answerThree");

        pack();
    }// </editor-fold>//GEN-END:initComponents

  private void tryAgainButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tryAgainButtonActionPerformed
     graphCanvas.setContinues(true);
     graphCanvas.setQuizOpen(false);
     logs.out(Logger.ACTIVITY, "QuizDialog.tryAgainButtonActionPerformed.1");
     this.dispose();

  }//GEN-LAST:event_tryAgainButtonActionPerformed

  private void continueButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_continueButtonActionPerformed
      logs.out(Logger.ACTIVITY, "QuizDialog.continueButtonActionPerformed.1");
      graphCanvas.setQuizOpen(false);
      this.dispose();
  }//GEN-LAST:event_continueButtonActionPerformed

  private void advanceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_advanceButtonActionPerformed
      logs.out(Logger.ACTIVITY, "QuizDialog.advanceButtonActionPerformed.1");
      graphCanvas.setPassed(true);
      graphCanvas.setQuizOpen(false);
      this.dispose();
  }//GEN-LAST:event_advanceButtonActionPerformed

  private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
      // TODO add your handling code here:
      graphCanvas.setQuizOpen(false);
  }//GEN-LAST:event_formWindowClosing

  public void addQuestion (int n, String question) {
    if (n == 0) question1.setText(question);
    if (n == 1) questionTwo.setText(question);
    if (n == 2) questionThree.setText(question);
    if (n == 3) questionFour.setText(question);
    if(n == 0 || n == 1 || n == 2 || n == 3)
    {
        questions++;
    }
  }

  public void addAnswer (int n, String answer) {
    if(!answer.equalsIgnoreCase("")){
       if (n == 0) answer1.setText("" + answer);
       if (n == 1) answerTwo.setText("" + answer);
       if (n == 2) answerThree.setText("" + answer);
       if (n == 3) answerFour.setText("" + answer);
    } else {
       if (n == 0) answer1.setText("Unable to answer question without more model detail");
       if (n == 1) answerTwo.setText("Unable to answer question without more model detail");
       if (n == 2) answerThree.setText("Unable to answer question without more model detail");
       if (n == 3) answerFour.setText("Unable to answer question without more model detail");
    }
  }

  public void addCorrectAnswer (int n, String answer) {
     if (n == 0) correctAns1.setText("Correct Answer: " + answer);
     if (n == 1) correctAns2.setText("Correct Answer: " + answer);
     if (n == 2) correctAns3.setText("Correct Answer: " + answer);
     if (n == 3) correctAns4.setText("Correct Answer: " + answer);
  }

  public void addResult (int n, boolean result) {
    Toolkit toolkit = getToolkit();
    boolean usedHints = graphCanvas.getCover().getMenuBar().getUsedHint();
    //right image
    java.net.URL rightURL = Main.class.getResource("images/right.png");
    Image rightImg = toolkit.createImage(rightURL);
    rightImg = rightImg.getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
    ImageIcon right = new ImageIcon(rightImg);
    //wrong image
    java.net.URL wrongURL = Main.class.getResource("images/wrong.png");
    Image wrongImg = toolkit.createImage(wrongURL);
    wrongImg = wrongImg.getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
    ImageIcon wrong = new ImageIcon(wrongImg);
    Color colorWrong = Color.red;
    Color colorRight = new Color(47, 79, 47); //Dark grenn

    if (n == 0) {
       if(result == false) {
          result1.setText("");
          result1.setIcon(wrong);
          answer1.setForeground(colorWrong);
       } else {
          result1.setText("");
          result1.setIcon(right);
          answer1.setForeground(colorRight);
          correct++;
       }
    }
    if (n == 1) {
        if(result == false) {
           resultTwo.setText("");
           resultTwo.setIcon(wrong);
           answerTwo.setForeground(colorWrong);
        } else {
           resultTwo.setText("");
           resultTwo.setIcon(right);
           answerTwo.setForeground(colorRight);
           correct++;
        }
    }
    if (n == 2) {
       if(result == false){
          resultThree.setText("");
          resultThree.setIcon(wrong);
          answerThree.setForeground(colorWrong);
       } else {
          resultThree.setText("");
          resultThree.setIcon(right);
          answerThree.setForeground(colorRight);
          correct++;
       }
    }
    if (n == 3) {
       if(result == false){
          resultFour.setText("");
          resultFour.setIcon(wrong);
          answerFour.setForeground(colorWrong);
       } else {
          resultFour.setText("");
          resultFour.setIcon(right);
          answerFour.setForeground(colorRight);
          correct++;
       }
    }

    // Modify javiergs
    int num1 = -1, num2=-1;
    try {
      num1 = Integer.parseInt(TaskFactory.getInstance().getActualTask().getLevel());
      num2 = Integer.parseInt(TaskFactory.getInstance().getActualTask().getLevel());
    } catch(CommException de) {
    }
    // end javier

    if (questions == correct && graphCanvas.getPractice() == false && usedHints == false) {
      logs.out(Logger.ACTIVITY, "QuizDialog.addResult.1");
      advanceButton.setVisible(true);
      if (Main.professorVersion == Main.PROFESSORVERSION) {
        graph.createProfessorOutput();
      }
      giveUpLabel.setText("<html>Your student passed the level test and is ready to level up. "
              + "Moreover, because you taught her without using any hints, she will get a point.</html>");
      this.setDefaultCloseOperation(javax.swing.JDialog.DO_NOTHING_ON_CLOSE);
      continueButton.setVisible(false);
      tryAgainButton.setVisible(false);
      //GIVE THE USER A POINT
      graphCanvas.setUserPoints(1);
      graphCanvas.setStudentReceivedLevelPoint(true);
    }
    else if (questions == correct && graphCanvas.getPractice() == false && usedHints == true) {
      logs.out(Logger.ACTIVITY, "QuizDialog.addResult.2");
      advanceButton.setVisible(true);
      if (Main.professorVersion == Main.PROFESSORVERSION) {
        graph.createProfessorOutput();
      }
      giveUpLabel.setText("<html>Your student passed the level test.  If you press the ‘Level up’ button, your "
              + "student won’t get a point, because your student asked for hints or use the Instruction tab. "
              + "If you want her to get a point, press the ‘More practice, same level’ button and teach her a new model without asking for hints.</html>");
      this.setDefaultCloseOperation(javax.swing.JDialog.DO_NOTHING_ON_CLOSE);
      continueButton.setVisible(false);
      tryAgainButton.setVisible(true);
    }
    else if (questions == correct && graphCanvas.getPractice() == true && num1 > 0) {
      tryAgainButton.setVisible(true);
      tryAgainButton.setText("Continue");
      if (Main.professorVersion == Main.PROFESSORVERSION) {
        graph.createProfessorOutput();
      }
      continueButton.setVisible(false);
      giveUpLabel.setVisible(true);
      giveUpLabel.setText("<html>Your student passed the level test. Because this was the first task of the level, "
              + "it was practice task and you could ask for as many hints as you want. On the other tasks on this level, "
              + "you can ask for hints if you want to learn more. However, if you don’t ask for hints, "
              + "your student will score a point when she level ups</html>");
      advanceButton.setVisible(false);
    }
    else if (questions == correct && graphCanvas.getPractice() == true && num2 <= 0) {
      tryAgainButton.setVisible(false);
      advanceButton.setVisible(true);
      if (Main.professorVersion == Main.PROFESSORVERSION) {
        graph.createProfessorOutput();
      }
      advanceButton.setText("Continue");
      continueButton.setVisible(false);
      giveUpLabel.setVisible(true);
      giveUpLabel.setText("<html>Your student passed the level test. Because this was a practice level, "
              + "you could ask for as many hints as you want. On the other tasks in future levels, "
              + "you can ask for hints if you want to learn more. However, if you don’t ask for hints, "
              + "your student will score a point when she level ups</html>");
    }
    else
    {
        giveUpLabel.setText("<html>Your student was unable to pass the level test and needs remediation.</html>");
        advanceButton.setVisible(false);
        tryAgainButton.setVisible(false);
        continueButton.setVisible(true);
    }

  }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton advanceButton;
    private javax.swing.JLabel answer1;
    private javax.swing.JLabel answerFour;
    private javax.swing.JLabel answerThree;
    private javax.swing.JLabel answerTwo;
    private javax.swing.JButton continueButton;
    private javax.swing.JLabel correctAns1;
    private javax.swing.JLabel correctAns2;
    private javax.swing.JLabel correctAns3;
    private javax.swing.JLabel correctAns4;
    private javax.swing.JLabel giveUpLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel question1;
    private javax.swing.JLabel questionFour;
    private javax.swing.JLabel questionThree;
    private javax.swing.JLabel questionTwo;
    private javax.swing.JLabel result1;
    private javax.swing.JLabel resultFour;
    private javax.swing.JLabel resultThree;
    private javax.swing.JLabel resultTwo;
    private javax.swing.JButton tryAgainButton;
    // End of variables declaration//GEN-END:variables

}
