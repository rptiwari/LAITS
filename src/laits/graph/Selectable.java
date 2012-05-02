package laits.graph;

import laits.Main;
import laits.log.*;
import laits.parser.Equation;
import java.awt.*;
import java.io.Serializable;
import java.util.LinkedList;

/**
 * This class defines parent class for Vertex, Edge and Graph classes.
 *
 * @author Javier Gonzalez Sanchez
 * @author Lakshmi Sudha Marothu
 * @author Patrick Lu 
 * @version 20100215
 */
public class Selectable implements Serializable{

  public final static Color COLOR_SELECTED = Color.green.darker();
  public final static Color COLOR_SELECTED_V2 = Color.BLACK;
  public final static Color COLOR_DEFAULT = Color.black;
  protected static final int INVALID = -1000;
  public boolean isSelected = false;
  public Color color = COLOR_DEFAULT;
  public int size = 10;
  public String label = "";
  public Point labelPoint = null;
  protected Font labelFont = new Font("Arial", Font.PLAIN, 12);
  protected Rectangle labelBounds = new Rectangle(INVALID, INVALID, INVALID, INVALID);
  protected FontMetrics labelFontMetrics = Toolkit.getDefaultToolkit().getFontMetrics(labelFont);
  private static Logger logs = Logger.getLogger();
  //These values are used in the version 2 of the system
  public String nodeName = "";
  public String selectedDescription = "";
  public String correctDescription = "";
  //This is the inflows - outflows equation in the jTextAreaEquation of version 2
  public String stockEquation = "";
  //This is the description of the task with the underlined sentences according with the node
  public String situationDescription = "";
  //The correct answers for the node
  //public String correctDescription = "";
  public String correctType = "";
  public String correctInputs = "";
  public String correctOutputs = "";
  public LinkedList<Double> correctValues = new LinkedList<Double>();
  public Equation correctEquation = null;
  //Button Variables
  public final int NOSTATUS = 0;
  public final int CORRECT = 1;
  public final int GAVEUP = 2;
  public final int WRONG = 3;

  public final static int static_nostatus = 0;
  
  private int descriptionButtonStatus = 0;
  private int inputsButtonStatus = 0;
  private int calculationsButtonStatus = 0;
  private int graphsButtonStatus = 0;

  /**
   * Setter method to define the font to be used in the label of the vertexes.
   * Notice that the default value for the font is TimesRoman, PLAIN, size 14.
   * @param f is the font type to be used in the labels.
   */
  public final void setFont(Font f) {
    labelFont = f;
    labelFontMetrics = Toolkit.getDefaultToolkit().getFontMetrics(f);
    labelBounds.y = INVALID;
    labelBounds.width = INVALID;
    labelBounds.height = INVALID;
  }

  /**
   * Getter method to get the font we are using in the vertexes's labels.
   * @return the current font.
   */
  public final Font getFont() {
    return labelFont;
  }

  /**
   * Method to increment by sizeIncrement the size of the font.
   *
   * @param sizeIncrement is an integer value to increment the font size.
   */
  public final void adjustFont(int sizeIncrement) {
    int fontSize = labelFont.getSize();
    int style = labelFont.getStyle();
    String name = labelFont.getName();
    Font f = new Font(name, style, fontSize + sizeIncrement);
    setFont(f);
  }

  /**
   * Method to define the position of the vertex label.
   *
   * @param x is the x coordinate where the vertex is going to be drawed
   * @param y is the y coordinate where the vertex is going to be drawed
   */
  public final synchronized void moveLabel(int x, int y) {
    if (labelPoint == null) {
      labelPoint = new Point(x, y);
      labelBounds.x = x;
      labelBounds.y = y - labelFontMetrics.getAscent();
    } else {
      labelBounds.x += x - labelPoint.x;
      labelBounds.y += y - labelPoint.y;
      labelPoint.x = x;
      labelPoint.y = y;
    }
  }

  /**
   * Method to define the position of the label having an initial point of reference
   * @param x is the coordinate where the vertex is going to be drawed
   * @param y is the coordinate where the vertex is going to be drawed
   */
  public final synchronized void moveLabelRelative(int x, int y) {
    if (labelPoint == null) {
      moveLabel(x, y);
    } else {
      moveLabel(x + labelPoint.x, y + labelPoint.y);
    }
  }

  /**
   * Method to define the bounds of the label
   */
  public final synchronized void validateLabelBounds() {
    if (labelPoint == null) {
      defaultLabel();
    }
    if (labelBounds.x == INVALID) {
      labelBounds.x = labelPoint.x;
    }
    if (labelBounds.y == INVALID) {
      labelBounds.y = labelPoint.y - labelFontMetrics.getAscent();
    }
    if (labelBounds.width == INVALID) {
      labelBounds.width = labelFontMetrics.stringWidth(label);
    }
    if (labelBounds.height == INVALID) {
      labelBounds.height = labelFontMetrics.getAscent();
      labelBounds.height += labelFontMetrics.getDescent();
    }
  }

  /**
   * Method to modify the label adding a new character c.
   * @param c the character to be added into the vertex label.
   */
  public final synchronized void extendLabel(char c) {
    if (Character.isISOControl(c)) {
      return;
    }
    if (label == null) {
      label = "";
    }
    label += c;
    labelBounds.width = INVALID;
  }

  /**
   * Method to reduce in one the label length and content.
   */
  public final synchronized void shortenLabel() {
    if ((label != null) && (label.length() > 1)) {
      label = label.substring(0, label.length() - 1);

    } else {
      label = "";
    }
    labelBounds.width = INVALID;
  }

  /**
   * Getter method to get the color we are using for drawing
   * 
   * @param COLOR_DEFAULT is the default value
   * @return a new Color object with the current color.
   */
  protected final Color getColor(Color color) {

    if (isSelected) {
      if (Main.professorVersion == Main.VERSION2) {
        return COLOR_SELECTED_V2;
      } else {
        return COLOR_SELECTED;
      }
    } else {
      return color;
    }
  }

  /**
   * This method retrieves the status of the descriptions panel button for version 2
   * @return the inputs button status
   */
  public int getDescriptionButtonStatus() {
    return descriptionButtonStatus;
  }

  /**
   * This method set sets the descriptions button status to the parameter status
   * @param status is the inputs button status
   */
  public void setDescriptionButtonStatus(int status) {
    descriptionButtonStatus = status;
  }

  /**
   * This method retrieves the status of the inputs panel button for version 2
   * @return the inputs button status
   */
  public int getInputsButtonStatus() {
    return inputsButtonStatus;
  }

  /**
   * This method set sets the inputs button status to the parameter status
   * @param status is the inputs button status
   */
  public void setInputsButtonStatus(int status) {
    inputsButtonStatus = status;
  }

  /**
   * This method retrieves the status of the calculations panel button for version 2
   * @return the calculations button status
   */
  public int getCalculationsButtonStatus() {
    return calculationsButtonStatus;
  }

  /**
   * This method set sets the calculations button status to the parameter status
   * @param status is the calculations button status
   */
  public void setCalculationsButtonStatus(int status) {
    calculationsButtonStatus = status;
  }

  /**
   * This method retrieves the status of the graphs panel button for version 2
   * @return the graphs button status
   */
  public int getGraphsButtonStatus() {
    return graphsButtonStatus;
  }

  /**
   * This method set sets the graphs button status to the parameter status
   * @param status is the graphs button status
   */
  public void setGraphsButtonStatus(int status) {
    graphsButtonStatus = status;
  }


  /**
   * Method to verify if the hitted position by the mouse is inside the label.
   *
   * @param x is the current x-coordinate from the mouse while hit on the label
   * @param y is the current y-coordinate from the mouse while hit on the label.
   * @return a boolean value. True if the user hit inside the label, false otherwise.
   *
   */
  public final boolean hitLabel(int x, int y) {
    return labelBounds.contains(x, y);
  }

  /**
   * Method to validate the type of values for distance
   *
   * @param x value for the x-axis
   * @param y value for the y-axis
   * @return
   */
  public double distance(double x, double y) {
    //log.out(LogType.DEBUG_TO_SERVER,"Override distance(double,double).");
    //logs.out(DEBUG, "Override distance(double,double).");
    return 0;
  }

  /**
   * Methods that define if the current x,y position is inside the limit
   *
   * @param x
   * @param y
   * @param limit
   * @return true or false
   */
  public boolean near(int x, int y, double limit) {
    return distance(x, y) < limit;
  }

  /**
   * Set the default position for the label
   */
  public void defaultLabel() {
    moveLabel(50, 50);
  }

  /**
   * Method to paint the label of a vertex
   *
   * @param g
   */
  public final synchronized void paintLabel(Graphics g) {
    if (label == null || "".equals(label)) {
      return;
    }
    validateLabelBounds();
    //g.drawRect(labelBounds.x,labelBounds.y,labelBounds.width,labelBounds.height);
    g.setFont(labelFont);
    //g.drawString(label,labelPoint.x+25,labelPoint.y);
    if (Main.professorVersion != Main.VERSION2 && Main.professorVersion != Main.DEMO_VERSION2)//.equals("112"))
    {
      // begin shadow
      g.setColor(Color.white);
      g.drawString(label, labelPoint.x - 1, labelPoint.y);
      g.drawString(label, labelPoint.x + 1, labelPoint.y);
      g.drawString(label, labelPoint.x, labelPoint.y - 1);
      g.drawString(label, labelPoint.x, labelPoint.y + 1);
      // end shadow
      // begin text
      g.setColor(getColor(Color.black));
      g.drawString(label, labelPoint.x, labelPoint.y);
      // end text
    } else {
      // begin shadow
      //g.setColor(Color.WHITE);
      if (descriptionButtonStatus == CORRECT) {
        g.setColor(new Color(155,250,140));
      } else if (descriptionButtonStatus == WRONG) {
        g.setColor(Color.pink);
      } else if (descriptionButtonStatus == GAVEUP) {
        g.setColor(new Color(252,252,130));
      } else if (descriptionButtonStatus == NOSTATUS) {
        g.setColor(Color.WHITE);
      }

      g.drawString(nodeName, labelPoint.x - 1, labelPoint.y);
      g.drawString(nodeName, labelPoint.x + 1, labelPoint.y);
      g.drawString(nodeName, labelPoint.x, labelPoint.y - 1);
      g.drawString(nodeName, labelPoint.x, labelPoint.y + 1);
      // end shadow
      // begin text


      g.setColor(getColor(Color.black));
      g.drawString(nodeName, labelPoint.x, labelPoint.y);
      // end text
    }
  }

  /**
   * General paint method
   * @param g
   */
  public void paint(Graphics g) {
    paintLabel(g);
  }

  /**
   * Method to increment the size of the object
   * @param d is the increment
   */
  public void adjustSize(int d) {
    size += d;
    if (size < 1) {
      size = 1;
    }
  }

  /**
   * Method to generate a String with all the information of the object
   * @return a string with all the information
   */
  @Override
  public String toString() {
    String s = "\"" + label + "\"";
    s += " " + size;
    return "(Selectable " + s + " )";
  }
}
