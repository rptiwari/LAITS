package laits;

import laits.data.Task;
import laits.cover.Cover;
import laits.cover.Slide;
import laits.log.Logger;
import java.awt.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.LinkedList;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;

/**
 * Cover description of the problem
 * 
 * @author Javier Gonzalez Sanchez
 * @author Megan Kearl
 * @version 20100505
 */
public class TaskView extends JPanel implements ActionListener {

  private Dimension imageSize = new Dimension(0, 0);
  private static Logger logger = Logger.getLogger();
  private JLabel taskDescriptionLabel;
  private JButton videoButton;
  private JButton startButton;
  private JEditorPane editorPane;
  private JScrollPane editorScrollPane;
  private Image image = null;
  private Dimension sizeOfImage = new Dimension(0, 0);
  private Slide slide;
  private JToolBar toolBar;
  private Cover cover = null;
  private Task task;
  Desktop desktop = null;
  static final private String PREVIOUS = "previous";
  static final private String NEXT = "next";
  static final private String FIRST = "first";
  static final private String LAST = "last";

  static final private int toolBarWidth = 183;
  static final private int toolBarHeight = 33;
  private int index;

  private JPanel toolBarPanel;
  /**
   * Constructor
   */
  public TaskView() {
    super();
    FlowLayout f = new FlowLayout(FlowLayout.CENTER, 5, 20);
    this.setLayout(f);
    taskDescriptionLabel = new JLabel("", JLabel.RIGHT);
    editorPane = new JEditorPane();
    editorScrollPane = new JScrollPane(editorPane);
    editorScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    editorScrollPane.setPreferredSize(new Dimension(800, 600));
    editorScrollPane.setMinimumSize(new Dimension(50, 50));
    taskDescriptionLabel.setVerticalTextPosition(JLabel.TOP);
    taskDescriptionLabel.setHorizontalTextPosition(JLabel.RIGHT);
    taskDescriptionLabel.setBorder(BorderFactory.createTitledBorder(""));
    taskDescriptionLabel.setBackground(Color.WHITE);
    taskDescriptionLabel.setOpaque(true);
    initVideoButton();
    toolBar = new JToolBar();
    toolBarPanel = new JPanel();
    toolBar.setFloatable(false);
    if (Main.professorVersion == Main.VERSION2 || Main.professorVersion == Main.DEMO_VERSION2) {
      addButtons();
    }
//    else {
//      videoButton.addActionListener(this);
//      add(videoButton);
//    }

    this.setPreferredSize(new Dimension((int)(this.getToolkit().getScreenSize().getWidth() / 2) - 200 / 2 - 300, 140));

    toolBar.setBounds(290, 5, toolBarWidth, toolBarHeight);    
    toolBarPanel.setPreferredSize(new Dimension(1300, 40));
    toolBarPanel.setOpaque(false);
    toolBarPanel.setLayout(null);
    toolBarPanel.add(toolBar);
    add(toolBarPanel);
    add(taskDescriptionLabel);
   
  }

  /**
   * This method initializes the video button
   */
  public void initVideoButton() {
    videoButton = new JButton("View intro & job aid");
    videoButton.setVerticalTextPosition(JButton.TOP);
    videoButton.setHorizontalTextPosition(JButton.CENTER);
  }

  /**
   * This method sets the cover
   * @param cover
   */
  public void setCover(Cover cover) {
    this.cover = cover;
  }

  /**
   * This method updates the task.
   *
   * @param t
   */
  public void updateTask(Task task) {
    this.remove(toolBarPanel);
    boolean error = false;
    Image img = null;
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    InputStreamReader in = null;

    // Remove button VideoButton
    videoButton.setVisible(false);

    // choose image
    try {

//System.out.println("Cargando imagen");
      URL testURL = new URL(task.getImageUrl());
      img = toolkit.createImage(new URL(task.getImageUrl()));
      //input stream reader tests whether there is an internet connection
      //if there is no connection then error is true and a local image loads
      in = new InputStreamReader(testURL.openStream());
//System.out.println("Imagen cargada");
      in.close();
    } catch (Exception ex) {
      error = true;
//System.out.print("Hubo error al cargar la imagen: " + ex);
      //ensures in is closed
      try {
        in.close();
      } catch (Exception ex2) {
      }
      logger.concatOut(Logger.DEBUG, "TaskView.updateTask.1", ex.toString());
    }

    if (error == true) {
      URL imageURL = Main.class.getResource("/amt/images/asu.jpg");
      img = toolkit.createImage(imageURL);
    }
//System.out.println("Ajustando tamaño imagen");
    ImageIcon icon = new ImageIcon();
    try {
      img = img.getScaledInstance(400, 300, Image.SCALE_DEFAULT);
      icon = new ImageIcon(img);
    } catch (Exception ex3) {
      System.out.println("Error ajustando imagen" + ex3.toString());
    }
//System.out.println("Imagen lista");
    // split text
    String html = "";
    LinkedList<String> lines = splitLines(task.getDescription());
    for (int i = 0; i < lines.size(); i++) {
      html += lines.get(i) + "<br>";
    }
    taskDescriptionLabel.setText("<html><h1>" + task.getTitle() + "</h1><span style='font-size:15;font-weight:400'>" + html + "</span></html>");
    taskDescriptionLabel.setIcon(icon);
    taskDescriptionLabel.setHorizontalAlignment(JLabel.RIGHT);
  }

  /**
   * This method updates the instructionView panel.
   *
   * @param t
   */
  public void updateInstruction(Task task) {
    //setLayout(null);
    if (Main.professorVersion == Main.VERSION2 || Main.professorVersion == Main.DEMO_VERSION2) {
      this.remove(taskDescriptionLabel);
//      index = 0;
      slide = new Slide(20 /*(this.getToolkit().getScreenSize().width) / 2 - 720 / 2*/, 60, this);
    } else {
      this.remove(taskDescriptionLabel);
      editorPane.setEditorKit(new HTMLEditorKit());
      editorPane.setEditable(false);
      try {
        URL url = new URL("http://amt.asu.edu/rmchris3/instruction/" + task.getLevel() + "-concepts.html");
        try {
          editorPane.setPage(url);
        } catch (IOException ex) {
          //Logger.getLogger(TaskView.class.getName()).log(Level.SEVERE, null, ex);
        }
      } catch (MalformedURLException ex) {
        //Logger.getLogger(TaskView.class.getName()).log(Level.SEVERE, null, ex);
      }

      videoButton.setBounds((int) (this.getToolkit().getScreenSize().getWidth() / 2) - 200 / 2, 60, 200, 23);
      editorScrollPane.setBounds((int) (this.getToolkit().getScreenSize().getWidth() / 2) - 200 / 2 - 300, 140, 800, 500);
      //this.add(videoButton);
      this.add(editorScrollPane);
      HyperlinkListener hyperlinkListener = new HyperlinkListener() {

        public void hyperlinkUpdate(HyperlinkEvent e) {
          if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            if (Desktop.isDesktopSupported()) {
              desktop = Desktop.getDesktop();
            }
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
              URI uri = null;
              try {
                uri = new URI(e.getURL().toString());
                desktop.browse(uri);
              } catch (Exception ioe) {
                ioe.printStackTrace();
              }
            }

          } else if (e.getEventType() == HyperlinkEvent.EventType.EXITED) {
          }
        }
      };
      editorPane.addHyperlinkListener(hyperlinkListener);
    }
  }

  /**
   * Paint a grid
   *
   * @param g
   */
  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    imageSize = this.getParent().getSize();
    // white background
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, imageSize.width, imageSize.height);
    // gray grid
    g.setColor(new Color(230, 230, 230));
    for (int j = 0; j < imageSize.width; j += 10) {
      g.drawLine(j, 0, j, imageSize.height);
    }
    for (int i = 0; i < imageSize.height; i += 10) {
      g.drawLine(0, i, imageSize.width, i);
    }

    if (image == null) {
      sizeOfImage = getSize();
      image = createImage(sizeOfImage.width, sizeOfImage.height);
    }
    cover.paint(g);


    if (slide != null && (Main.professorVersion == Main.VERSION2 || Main.professorVersion == Main.DEMO_VERSION2)) {
      slide.paintSlide(g, index);
    }
    
    repaint();
  }

  /**
   * add Buttons
   *
   * @param g
   */
  protected void addButtons() {
    JButton button = null;

    //first button

    button = makeNavigationButton("navigate_beginning_icon", FIRST,
            "Back to first slide",
            "Up");
    toolBar.add(button);
    button.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(java.awt.event.ActionEvent evt) {
        firstButtonActionPerformed(evt);
      }

      private void firstButtonActionPerformed(ActionEvent evt) {
        index = 0;
        logger.out(Logger.ACTIVITY, "TaskView.firstButtonActionPerformed.1");
        repaint();
      }
    });

    //second button

    button = makeNavigationButton("navigate_left_icon", PREVIOUS,
            "Back to previous slide",
            "Previous");
    toolBar.add(button);
    button.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(java.awt.event.ActionEvent evt) {
        previousButtonActionPerformed(evt);
      }

      private void previousButtonActionPerformed(ActionEvent evt) {
        if (index != 0) {
          index--;
          repaint();
        }
        
        logger.concatOut(Logger.ACTIVITY, "TaskView.previousButtonActionPerformed.1",Integer.toString(index+1));
      }
    });

    //third button
    button = makeNavigationButton("navigate_right_icon", NEXT,
            "Forward to next slide",
            "Next");
    toolBar.add(button);
    button.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(java.awt.event.ActionEvent evt) {
        nextButtonActionPerformed(evt);
      }

      private void nextButtonActionPerformed(ActionEvent evt) {
        //if (index != 14) {
        if (index != Slide.getFrames()-1){
          index++;
          repaint();
        }
        logger.concatOut(Logger.ACTIVITY, "TaskView.nextButtonActionPerformed.1",Integer.toString(index+1));
      }
    });


    //fourth button

    button = makeNavigationButton("navigate_end_icon", LAST,
            "Forward to last slide",
            "Next");
    toolBar.add(button);
    button.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(java.awt.event.ActionEvent evt) {
        lastButtonActionPerformed(evt);
      }

      private void lastButtonActionPerformed(ActionEvent evt) {
        //index = 14;
        logger.out(Logger.ACTIVITY, "TaskView.lastButtonActionPerformed.1");
        index = Slide.getFrames() - 1;
        repaint();
      }
    });
  }

  /**
   * Button creator
   *
   * @param g
   */
  protected JButton makeNavigationButton(String imageName,
          String actionCommand,
          String toolTipText,
          String altText) {
    //Look for the image.
    String imgLocation = "/amt/images/"
            + imageName
            + ".png";
    URL imageURL = Main.class.getResource(imgLocation);

    //Create and initialize the button.
    JButton button = new JButton();
    button.setActionCommand(actionCommand);
    button.setToolTipText(toolTipText);
    button.addActionListener(this);
    if (imageURL != null) {                      //image found
      button.setIcon(new ImageIcon(imageURL, altText));
    } else {                                     //no image found
      button.setText(altText);
      System.err.println("Resource not found: "
              + imgLocation);
    }

    return button;
  }

  /**
   * This method split the task description in several lines
   *
   * @param description is the task description
   */
  private LinkedList<String> splitLines(String description) {
    Font titleFont = new Font("Arial", Font.BOLD, 30);
    Font textFont = new Font("Arial", Font.PLAIN, 18);
    FontMetrics textfm = Toolkit.getDefaultToolkit().getFontMetrics(textFont);
    int pictureWidth = (int) imageSize.getSize().getWidth() / 2;
    int margin = 50;
    LinkedList<String> taskLinesProbView = new LinkedList<String>();
    String tempProblemView = ""; //holds the current line
    String tempHolder = ""; //holds the current word
    int problemViewSize = 0;
    char currentChar;
    //necessary for the calculation to be correct
    if (this.getParent() != null) {
      imageSize = this.getParent().size();
    } else {
      //this calculation is only necessary for the first problem to load
      imageSize = Toolkit.getDefaultToolkit().getScreenSize();
      imageSize.setSize(imageSize.getHeight(), imageSize.getWidth() - 24);
    }
    //size of an image MUST be factored in to include it to the left or right of the text
    //this is for a task description when a picture is involved
    for (int i = 0; i < description.length(); i++) {
      currentChar = description.charAt(i);
      problemViewSize = textfm.stringWidth(tempProblemView) + textfm.stringWidth(tempHolder);

      if (problemViewSize < imageSize.width - pictureWidth - margin * 3 / 2 && currentChar != '\\') {
        if (currentChar == ' ') {
          tempHolder += currentChar;
          tempProblemView += tempHolder;
          tempHolder = "";
        } else {
          tempHolder += currentChar;
        }
      } else if (currentChar == '\\') {
        i++;
        if (i < description.length()) {
          currentChar = description.charAt(i);
          if (currentChar == 'n') {
            tempProblemView += tempHolder;
            taskLinesProbView.add(tempProblemView);
            tempHolder = "";
            tempProblemView = "";
          }
        } else {
          tempHolder += currentChar;
        }
      } else {
        taskLinesProbView.add(tempProblemView);
        tempHolder += currentChar;
        tempProblemView = "";
      }
    }
    tempProblemView += tempHolder;
    taskLinesProbView.add(tempProblemView);
    return taskLinesProbView;
  }

  /**
   * This method active the desktop browser to show starting page //Quanwei Zhao
   * @return
   */
  public void actionPerformed(ActionEvent e) {
    //throw new UnsupportedOperationException("Not supported yet.");
    //---
        /*if (Desktop.isDesktopSupported()) {
    desktop = Desktop.getDesktop();
    }

    // PUT THIS CODE IN A BUTTON -----
    if (desktop.isSupported(Desktop.Action.BROWSE)) {
    URI uri = null;
    try {
    uri = new URI("http://amt.asu.edu/rmchris3/instruction/start.html");
    desktop.browse(uri);
    } catch (Exception ioe) {
    // PRINT
    }
    }*/
  }
}
