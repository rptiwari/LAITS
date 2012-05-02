package amt.graph;

import amt.Main;
import amt.parser.Equation;
import java.awt.*;
import java.util.LinkedList;
import javax.swing.JCheckBox;
import javax.swing.tree.TreePath;

/**
 * Class to define vertexes for the graph. Vertexes stands for "Nodes".
 * This class also deals with drawing different shapes of the vertices
 *
 * @author Javier Gonzalez Sanchez
 * @author Lakshmi Sudha Marothu
 * @author Maria Elena Chavez Echeagaray
 * @auther Patrick Lu
 * @author Megan Kearl
 * @version 20100215
 */
public class Vertex extends Selectable {

  public final static int NONE = 1;
  public final static int STOCK = 2;
  public final static int FLOW = 3;
  public final static int AUXILIARY = 4;
  public final static int CONSTANT = 5;
  public int loop = 1;
  // edges is a list of edges that touch this vertex.
  public LinkedList<Edge> inedges = new LinkedList<Edge>();
  public LinkedList<Edge> outedges = new LinkedList<Edge>();
  // position gives the coordinates of the center of the vertex.
  public Point position;
  // Vertices are drawn as circles or else as disks, depending on this flag.
  // public boolean solid = true;
  public Graph graph;
  public String type = "none";

  public boolean isOpen = false;

  // public Information tokenList = null;
  //public LinkedList<Token> tokenList = new LinkedList<Token>();
  public Equation equation=null;
  public boolean inputsSelected = false;
  public boolean initialValueGiven = false;
  public boolean isinputsTypeCorrect = false;
  public boolean isCalculationTypeCorrect = false;
  public boolean isGivenValueCorrect = false;
  public boolean isEquationCorrect = false;
  public boolean isGraphCorrect = false;

  //so the height of the none vertex can be accessed by main
  public int paintNoneHeight = 6*size;
  private boolean alreadyRun = false;
  private boolean nonNegative = false;
  private boolean editorOpen = false;
  private boolean graphOpen = false;
  int width  = 11 * size;
  int height = 6 * size;

  //for debug phase
  public boolean setEquationyet=false;

  public LinkedList<JCheckBox> inputNodesSelected = new LinkedList<JCheckBox>();

  //tree path for description panel
  private TreePath treePath;
  public void setTreePath(TreePath tp)
  {
    this.treePath=tp;
  }
  public TreePath getTreePath()
  {
    return this.treePath;
  }

  //JGS TEST
  public double execute(Graph g) {
    int in = inedges.size();
    Object inEdges [] = inedges.toArray();
    int out = outedges.size();
    Object outEdges [] = outedges.toArray();
    double inputs = 0.0;
    double outputs = 0.0;
    double temp = 0.0;

    if (this.type.equals("stock")) {
      if (Main.professorVersion != Main.VERSION2 && Main.professorVersion != Main.DEMO_VERSION2) {
        //Stock is equal to Stock + inflows - outflows
        for (int j = 0; j < in; j++) {
          Edge et = ((Edge) inEdges[j]);
          if (et.start.type.equals("flow") && et.edgetype.equals("flowlink")) //inputs = inputs + et.start.execute(g);
          {
            inputs = inputs + et.start.equation.value.getLast();
          }
        }
        for (int k = 0; k < out; k++) {
          Edge et = ((Edge) outEdges[k]);
          if (et.end.type.equals("flow") && et.edgetype.equals("flowlink")) //outputs = outputs + et.end.execute(g);
          {
            outputs = outputs + et.end.equation.value.getLast();
          }
        }

        if (this.equation.value.isEmpty()) {
          temp = Double.parseDouble(this.equation.toString());
          //this.equation.value.add(Double.parseDouble (this.equation.toString()));
        } else {
          //System.out.println("inputs: " + inputs + "outputs: " + outputs);
          temp = this.equation.value.getLast() + inputs - outputs;
          //this.equation.value.add(this.equation.value.getLast() + inputs - outputs);
        }
      } else {
        //Using the Version 2 of the tool
        String splitStock[] = this.stockEquation.split(" ");
        boolean add = true;
        for (int i = 0; i < splitStock.length; i++) {
          if (!splitStock[i].equals("+") && !splitStock[i].equals("-")) {
            String node = splitStock[i].replace("_", " ");
            //find the vertex used in the stock equation
            for (int j = 0; j < in; j++) {
              Edge et = ((Edge) inEdges[j]);
              /*
               * When using the Version2 of the tool, we can have flows or constants connected to
               * Stocks.
               * 
               * HELEN 20110314
               */
              if ((et.start.type.equals("flow")||(et.start.type.equals("constant"))) && et.edgetype.equals("flowlink") && node.equals(et.start.label)) //inputs = inputs + et.start.execute(g);
              {
                 if(add) {
                    inputs = inputs + et.start.equation.value.getLast();
                    break;
                } else {
                   outputs = outputs + et.start.equation.value.getLast();
                   break;
                }
              }
            }
          } else if (splitStock[i].equals("+")) {
            add = true;
          } else if (splitStock[i].equals("-")) {
            add = false;
          }
        }

        if (this.equation.value.isEmpty()) {
          temp = Double.parseDouble(this.equation.toString());
        } else {
          temp = this.equation.value.getLast() + inputs - outputs;
          //this.equation.value.add(this.equation.value.getLast() + inputs - outputs);
        }
      }
      //return this.equation.value.getLast();
      return temp;
    }

    //this.value = this.tokenList.execute(g);
    //this.equation.value.add(this.equation.value.getLast());
    this.equation.value.add(this.equation.execute(g));
    return this.equation.value.getLast();
  }

  /**
   * This method sets whether the equation for the vertex has already been run
   */
  public void setAlreadyRun(boolean run)
  {
      alreadyRun = run;
  }

  /**
   * This method determines whether the equation for the vertex has already been run
   * @return true if the equation has been run
   */
  public boolean getAlreadyRun()
  {
      return alreadyRun;
  }

  /**
   * Constructor
   * Creates a new Vertex on the position 0,0 and an empty label
   */
  public Vertex() {
    this (0,0, null);
  }

  /**
   * Constructor
   * Creates a new Vertex on the position x,y and with an empty label
   *
   * @param x is the x-coordinate
   * @param y is the y-coordiate
   */
  public Vertex(int x, int y) {
    this(x, y, null);
  }

  /**
   * Constructor
   * Creates a new Vertex on the position x,y and with the label
   *
   * @param x is the x-coordinate
   * @param y is the y-coordiate
   * @param label is the name of the vertex
   */
  public Vertex(int x, int y, String label) {
    position = new Point(x, y);
    this.label = label;
    this.equation = new Equation();
    this.nonNegative = false;
    this.editorOpen = false;
    defaultLabel();
  }

  /**
   * Method to calculate the Vertex incoming degree
   *
   * @return an integer that represent the number of incomming edges to this vertex
   */
  public int inDegree() {
    int sum = loop;
    int n = inedges.size();
    Object a[] = inedges.toArray();
    for (int i = 0; i < n; i++) {
      sum += ((Edge) a[i]).multi;
    }
    return sum;
  }

  /**
   * Method to calculate the Vertex outcominng degree
   *
   * @return an integer that represent the number of outcoming edges to this vertex
   */
  public int outDegree() {
    int sum = loop;
    int n = outedges.size();
    Object a[] = outedges.toArray();
    for (int i = 0; i < n; i++) {
      sum += ((Edge) a[i]).multi;
    }
    return sum;
  }

  /**
   * Method to change the between a fill shape or lined shape
   */
  public final void alter() {
    defaultLabel();
  }

  /**
   * Move
   *
   * @param x
   * @param y
   */
  public final void move(int x, int y) {
    position.x = x - width/2;
    position.y = y - height/2;
    defaultLabel();
    int n = inedges.size();
    Object a[] = inedges.toArray();
    for (int j = 0; j < n; j++) {
      Edge et = ((Edge) a[j]);
      et.revalidate();
      et.defaultLabel();
    }
    n = outedges.size();
    a = outedges.toArray();
    for (int j = 0; j < n; j++) {
      Edge et = ((Edge) a[j]);
      et.revalidate();
      et.defaultLabel();
    }
  }

  /**
   * Method to define How far is the position x,y from the center of the Vertex
   * 
   * @param x
   * @param y
   * @return
   */
  @Override
  public final double distance(double x, double y) {
    double a = x - position.x;
    double b = y - position.y;
    return Math.sqrt(a * a + b * b);
  }

  /**
   *  Method to verify if we are hiting a Vertex with the mouse.
   *
   *  @param a is the x-coordinate of the mouse
   *  @param b is the y-coordinate of the mouse
   *  @return true if we hit the mouse inside a Vertex
   *
   */
  public final boolean hit(int a, int b) {
    int x = position.x;
    int y = position.y;
    if(type.equals("constant")){
        double areaOfConstant = ((width + 5) * (height + 5) )/2;
        double a1 = Math.abs( a*(y + height/2) + b*(x + width/2) + x*(y+height)-a*(y+height)-b*x-(y+height/2)*(x+width/2))/2;
        double a2 = Math.abs(( a*y + b*x + (x+width/2)*(y+height/2)-a*(y+height/2)-b*(x+width/2)-y*x))/2;
        double a3 = Math.abs((a*(y+height/2) + b*(x+width/2) + (x+width)*y - a*y - b *(x +  width) - (x+width/2) * (y+height/2)))/2;
        double a4 = Math.abs(a * (y+height) + b*(x + width) + (x + width/2) * (y+height/2) - a* (y + height/2) -  b*(x+width/2)- (y+height)*(x+ width))/2;
          if (areaOfConstant >=(a1+a2+a3+a4))
              return true;
    }
    if (type.equals("none") || type.equals("stock")) {
      if (a >= (x - 5) && a<= (x+width+5) && b >= (y - 5) && b<=(y+height+5))
          return true;
    }
    else if (type.equals("auxiliary") || type.equals("flow")) {
        x = x + width/2;
        y = y + height/2;
        double r = Math.sqrt((a-x)*(a-x) + (b-y)*(b-y));
        if (r<= (height/2 +5))
           return true;
    }
    return false;
  }

  /**
   * This method returns whether the menu for a vertex has been hit
   *
   * @param a is the x coordinate of where the mouse is
   * @param b is the y coordinate of where the mouse is
   * @param x is the x coordinate of the center of the vertex
   * @param y is the y coordinate of the center of the vertex
   * @return
   */
  public final boolean hitMenu(int a, int b, int x, int y) {
    x = x + width/2;
    y = y + height/2;

    //vertex menu
    int space = 4;
    int iconWidth = 24;
    int iconHeight = 24;

    if(a <= x + iconWidth*3/2 + space && a >= x - iconWidth*3/2 - space
       && b <= y + iconHeight/2 && b >= y - iconHeight/2) {
       return true;
    } else
      return false;
  }

  /**
   * Change the size of the selected Vertex by d.
   *
   * @param d is the value to be added to the Vertex current size.
   */
  @Override
  public final void adjustSize(int d) {
    int r = size + d;
    if (r >= 0) {
      size = r;
    }
    defaultLabel();
  }

  /**
   * Method to place the label at the bottom part of the Vertex.
   * The label always appear at the bottom.
   */
  @Override
  public final void defaultLabel() {
    int x = position.x;
    int y = position.y;
    int centerx = x + width /2;
    int centery = y + height/2;
    final int GAP_VERTICAL = 15;
    // if (solid)
    // {
    // changing the label below instead of right of vertex.
    // moveLabel(x+size+2,y);
    int medium = 0;
    //if(VERSIONID.equals("112"))
    if(Main.professorVersion != Main.VERSION2 && Main.professorVersion != Main.DEMO_VERSION2)
    {
      if (label != null)
        medium = labelFontMetrics.charsWidth(label.toCharArray(), 0, label.length())/2;
      moveLabel(centerx-medium, y+height+GAP_VERTICAL);
    }
    else
    {
      if (!nodeName.equals(""))
        medium = labelFontMetrics.charsWidth(nodeName.toCharArray(), 0, nodeName.length())/2;
      moveLabel(centerx-medium, y+height+GAP_VERTICAL);
    }
  }
  
  /**
   * Paint node type NONE (dashed rectangle).
   * The rectangle is specified by the x, y, widht, height, centerx, centery arguments.
   * Size is set to 10 in selectable.java.
   * 
   * @param g
  */
  public void paintNone(Graphics g) {
    int x = position.x;
    int y = position.y;
    int centerx = x + width /2;
    int centery = y + paintNoneHeight/2;
    Graphics2D g2d = (Graphics2D) g;    
    Stroke currentStroke = g2d.getStroke();
    // begin shadow
    Color sc = getColor(color);
    int re = sc.getRed() + (255 - sc.getRed())*2/3;
    int gr = sc.getGreen() + (255 - sc.getGreen())*2/3;
    int bl = sc.getBlue() + (255 - sc.getBlue())*2/3;
    g2d.setColor(new Color(re, gr, bl));
    g2d.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND,5f, new float[] {5f}, 0f));
    g2d.drawRect(x, y, width, paintNoneHeight);
    // end shadow
    // begin shape
    g2d.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND,5f, new float[] {5f}, 0f));
    g2d.setColor(sc);
    g2d.drawRect(x, y, width, paintNoneHeight);
    //draw the internal square
    g2d.setColor(Color.WHITE);
    g2d.fillRect(x+2, y+2, width-4, paintNoneHeight-4);
    g2d.setColor(new Color(re, gr, bl));
    g2d.drawLine(centerx-3, centery, centerx+3, centery);
    g2d.drawLine(centerx, centery+3, centerx, centery-3);
    // end shape
    g2d.setStroke(currentStroke);
  }

  /**
   * Paint node type STOCK (dashed rectangle).
   * The rectangle is specified by the x, y, widht, height, centerx, centery arguments.
   * Size is set to 10 in selectable.java.
   *
   * @param g
  */
  public void paintStock(Graphics g) {
    int x = position.x;
    int y = position.y;
    int centerx = x + width /2;
    int centery = y + height/2;
    Graphics2D g2d = (Graphics2D) g;
    Stroke currentStroke = g2d.getStroke();
    // begin shadow
    if((equation == null) || (equation.getIsCorrect() == false) || (equation.toString().equals(""))) {
        Color sc = getColor(color);
        int re = sc.getRed() + (255 - sc.getRed())*2/3;
        int gr = sc.getGreen() + (255 - sc.getGreen())*2/3;
        int bl = sc.getBlue() + (255 - sc.getBlue())*2/3;
        g2d.setColor(new Color(re, gr, bl));
        g2d.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        g2d.drawRect(x, y, width, height);
        g2d.drawRect(x+4, y+4, width-8, height-8);
    } else {
        Color sc = getColor(Color.blue);
        int re = sc.getRed() + (255 - sc.getRed())*2/3;
        int gr = sc.getGreen() + (255 - sc.getGreen())*2/3;
        int bl = sc.getBlue() + (255 - sc.getBlue())*2/3;
        g2d.setColor(new Color(re, gr, bl));
        g2d.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        g2d.drawRect(x, y, width, height);
        g2d.drawRect(x+4, y+4, width-8, height-8);
    }
    // end shadow
    // begin shape
    g2d.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
    g2d.setColor(getColor(color));
    g2d.drawRect(x,   y,   width,   height);
    g2d.drawRect(x+4, y+4, width-8, height-8);
    // Draw internal square
    g2d.setColor(Color.WHITE);
    g2d.fillRect(x+6,   y+6,   width-12,   height-12);
    g2d.setColor(Color.gray);
    //g2d.setColor(new Color(re, gr, bl));
    g2d.drawLine(centerx-3, centery, centerx+3, centery);
    g2d.drawLine(centerx, centery+3, centerx, centery-3);
    // end shape
    g2d.setStroke(currentStroke);
  }

  /**
   * Paint node type AUXILIARY (double-line oval).
   * The rectangle is specified by the x, y, widht, height, centerx, centery arguments.
   * Size is set to 10 in selectable.java.
   *
   * @param g
  */
  public void paintAuxiliary(Graphics g) {
    int x = position.x;
    int y = position.y;
    int centerx = x + width /2;
    int centery = y + height/2;
    Graphics2D g2d = (Graphics2D) g;
    Stroke currentStroke = g2d.getStroke();
    // begin shadow
    if((equation == null) || (equation.getIsCorrect() == false)) {
        Color sc = getColor(color);
        int re = sc.getRed() + (255 - sc.getRed())*2/3;
        int gr = sc.getGreen() + (255 - sc.getGreen())*2/3;
        int bl = sc.getBlue() + (255 - sc.getBlue())*2/3;
        g2d.setColor(new Color(re, gr, bl));
        g2d.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        g2d.drawOval(width/4+x,   y,   width/2,   height);
    } else {
        Color sc = getColor(Color.BLUE);
        int re = sc.getRed() + (255 - sc.getRed())*2/3;
        int gr = sc.getGreen() + (255 - sc.getGreen())*2/3;
        int bl = sc.getBlue() + (255 - sc.getBlue())*2/3;
        g2d.setColor(new Color(re, gr, bl));
        g2d.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        g2d.drawOval(width/4+x,   y,   width/2,   height);
    }
    // end shadow
    // begin shape    
    g2d.setStroke(currentStroke);
    g2d.setColor(getColor(color));
    g2d.drawOval(width/4+x,   y,   width/2,   height);
    // Draw the internal circle
    g2d.setColor(Color.WHITE);
    g2d.fillOval(width/4+x+2,   y+2,   width/2-4,   height-4);
    g2d.setColor(Color.GRAY);
    //g2d.setColor(new Color(re, gr, bl));
    g2d.drawLine(centerx-3, centery, centerx+3, centery);
    g2d.drawLine(centerx, centery+3, centerx, centery-3);
    // end shape
  }

  /**
   * This method is used to paint an diamond
   * the diamond is inside a rectagle that is specified by the
   * x,y,width and height arguments.
   * size is set to 10 in selectable.java
   * position.x() returns the value of x.
   * position.y() returns the value of y.
   */
  public void paintConstant(Graphics g) {
    int x = position.x;
    int y = position.y;
    int centerx = x + width /2;
    int centery = y + height/2;
    int border = size/2;

    Graphics2D g2d = (Graphics2D) g;
    Stroke currentStroke = g2d.getStroke();
    // begin shadow
    if((equation == null) || (equation.getIsCorrect() == false) || (equation.toString().equals(""))) {
        Color sc = getColor(color);
        int re = sc.getRed() + (255 - sc.getRed())*2/3;
        int gr = sc.getGreen() + (255 - sc.getGreen())*2/3;
        int bl = sc.getBlue() + (255 - sc.getBlue())*2/3;
        g2d.setColor(new Color(re, gr, bl));
        g2d.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        g2d.drawLine(x,        y+height/2, x+width/2, y);
        g2d.drawLine(x,        y+height/2, x+width/2, y+height);
        g2d.drawLine(x+width/2,y,          x+width,   y+height/2);
        g2d.drawLine(x+width,  y+height/2, x+width/2, y+height);
    } else {
        Color sc = getColor(Color.BLUE);
        int re = sc.getRed() + (255 - sc.getRed())*2/3;
        int gr = sc.getGreen() + (255 - sc.getGreen())*2/3;
        int bl = sc.getBlue() + (255 - sc.getBlue())*2/3;
        g2d.setColor(new Color(re, gr, bl));
        g2d.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        g2d.drawLine(x,        y+height/2, x+width/2, y);
        g2d.drawLine(x,        y+height/2, x+width/2, y+height);
        g2d.drawLine(x+width/2,y,          x+width,   y+height/2);
        g2d.drawLine(x+width,  y+height/2, x+width/2, y+height);
    }
    // end shadow
    g2d.setStroke(currentStroke);
    g.setColor(getColor(color));
    g.drawLine(centerx-3, centery, centerx+3, centery);
    g.drawLine(centerx, centery+3, centerx, centery-3);
    //first black diamond
    g.drawLine(x,        y+height/2, x+width/2, y);
    g.drawLine(x,        y+height/2, x+width/2, y+height);
    g.drawLine(x+width/2,y,          x+width,   y+height/2);
    g.drawLine(x+width,  y+height/2, x+width/2, y+height);
    //draw internal diamond
    g2d.setColor(Color.WHITE);
    int xDiamondPoints[] = {x+4, x + width/2, x + width-4, x + width/2};
    int yDiamondPoints[] = {y + height/2, y+2, y + height/2, y + height-2};
    g.fillPolygon(xDiamondPoints, yDiamondPoints, 4);
    g2d.setColor(Color.GRAY);
    //g2d.setColor(new Color(re, gr, bl));
    g2d.drawLine(centerx-3, centery, centerx+3, centery);
    g2d.drawLine(centerx, centery+3, centerx, centery-3);
  }

  /**
   * This method is used to paint an flow
   * which fits within the rectangle specified by the
   * x,y,widht and height arguments.
   * size is set to 10 in selectable.java
   * position.x() returns the value of x.
   * position.y() returns the value of y.
   */
  public void paintFlow(Graphics g) {
    int x = position.x;
    int y = position.y;
    int centerx = x + width /2;
    int centery = y + height/2;
    Graphics2D g2d = (Graphics2D) g;
    Stroke currentStroke = g2d.getStroke();
    // begin shadow
    if((equation == null) || (equation.getIsCorrect() == false) || (equation.toString().equals(""))) {
        Color sc = getColor(color);
        int re = sc.getRed() + (255 - sc.getRed())*2/3;
        int gr = sc.getGreen() + (255 - sc.getGreen())*2/3;
        int bl = sc.getBlue() + (255 - sc.getBlue())*2/3;
        g2d.setColor(new Color(re, gr, bl));
        g2d.drawLine(centerx-3, centery, centerx+3, centery);
        g2d.drawLine(centerx, centery+3, centerx, centery-3);
        g2d.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        g2d.drawOval(width/4+x,   y,   width/2,   height);
        if(Main.professorVersion != Main.VERSION2 && Main.professorVersion != Main.DEMO_VERSION2) {
          g2d.setColor(new Color(re, gr, bl));
          g2d.drawLine(x+width/2, y,          x+width/2,     y-height/8);
          g.drawLine(x+width/4, y-height/8-2, x + width/2, y-height/8-2);
          g.drawLine(x+width/2+width/4, y-height/8-2, x + width*1/4 + width/4, y-height/8-2);
          g.drawLine(x+width/2+width/4, y-height/8, x + width*1/4 + width/4, y-height/8);
        }
    } else {
        Color sc = getColor(Color.BLUE);
        int re = sc.getRed() + (255 - sc.getRed())*2/3;
        int gr = sc.getGreen() + (255 - sc.getGreen())*2/3;
        int bl = sc.getBlue() + (255 - sc.getBlue())*2/3;
        g2d.setColor(new Color(re, gr, bl));
        g2d.drawLine(centerx-3, centery, centerx+3, centery);
        g2d.drawLine(centerx, centery+3, centerx, centery-3);
        g2d.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        g2d.drawOval(width/4+x,   y,   width/2,   height);
        if(Main.professorVersion != Main.VERSION2 && Main.professorVersion != Main.DEMO_VERSION2) {
          g2d.setColor(new Color(re, gr, bl));
          g2d.drawLine(x+width/2, y,          x+width/2,     y-height/8);
          g.drawLine(x+width/4, y-height/8-2, x + width/2, y-height/8-2);
          g.drawLine(x+width/2+width/4, y-height/8-2, x + width*1/4 + width/4, y-height/8-2);
          g.drawLine(x+width/2+width/4, y-height/8, x + width*1/4 + width/4, y-height/8);
        }
    }
    // end shadow
    g2d.setStroke(currentStroke);
    g2d.setColor(getColor(color));
    //draw vertex
    g.drawOval(width/4+x,   y,   width/2,   height);
    if (Main.professorVersion != Main.VERSION2 && Main.professorVersion != Main.DEMO_VERSION2) {
      g.drawLine(x + width / 2, y, x + width / 2, y - height / 8);
      g.drawLine(x + width / 4, y - height / 8 - 2, x + width / 2, y - height / 8 - 2);
      g.drawLine(x + width / 2 + width / 4, y - height / 8 - 2, x + width * 1 / 4 + width / 4, y - height / 8 - 2);
      g.drawLine(x + width / 2 + width / 4, y - height / 8, x + width * 1 / 4 + width / 4, y - height / 8);
    }
    //draw internal circle
    g2d.setColor(Color.WHITE);
    g2d.fillOval(width/4+x+2,   y+2,   width/2-4,   height-4);
    //draw cross in center of vertex
    g2d.setColor(Color.GRAY);
    //g2d.setColor(new Color(re, gr, bl));
    g.drawLine(centerx-3, centery, centerx+3, centery);
    g.drawLine(centerx, centery+3, centerx, centery-3);
  }

  /**
   * This method sets whether the stock node equation is non-negative
   *
   * @param n is a boolean value returning true if stock is non-negative
   */
  public void setNonNegative(boolean n) {
    this.nonNegative = n;
  }

  /**
   * This method returns whether the stock node's equation is non-negative
   *
   * @return whether the stock node is non-negative
   */
  public boolean getNonNegative() {
    return this.nonNegative;
  }

  /**
   * This method returns whether a node's equation editor is open
   *
   * @param o is true if there is an equation editor open
   */
  public void setEditorOpen(boolean o) {
      this.editorOpen = o;
  }
  /**
   * This method returns whether the equation editor is open
   *
   * @return whether the editor is open
   */
  public boolean getEditorOpen() {
      return this.editorOpen;
  }

  /**
   * This method returns whether a node's graph is open
   *
   * @param o is true if there is a graph open
   */
  public void setGraphOpen(boolean o) {
      this.graphOpen = o;
  }
  /**
   * This method returns whether the graph is open
   *
   * @return whether the graph is open
   */
  public boolean getGraphOpen() {
      return this.graphOpen;
  }

  /**
   * Method to paint the Vertex depending on the type of the vertex.
   * 
   * @param g
   */
  @Override
  public final void paint(Graphics g) {
    if (size <= 0) return;
    g.setColor(getColor(color));
    if (type.equals("none"))      paintNone(g);
    if (type.equals("flow"))      paintFlow(g);
    if (type.equals("stock"))     paintStock(g);
    if (type.equals("auxiliary")) paintAuxiliary(g);
    if (type.equals("constant"))  paintConstant(g);
  }

  /**
   * Method to add an incoming edges of a Vertex
   *
   * @param e is the edge to add
   */
  public final void addInEdge(Edge e) {
    for (int i = 0; i < inedges.size(); i++) {
      Edge et = inEdge(i);
      if (e.start.equals(et.start)) {
        et.multi += e.multi;
        et.length = -1;
        e.length = -1;
        return;
      }
    }
    inedges.push(e);
    e.length = -1;
  }

  /**
   * Method to add an outcomming edges of a Vertex
   *
   * @param e is the edge to add
   */
  public final void addOutEdge(Edge e) {
    for (int i = 0; i < outedges.size(); i++) {
      Edge et = outEdge(i);
      if (e.end.equals(et.end)) {
        et.multi += e.multi;
        et.length = -1;
        e.length = -1;
        return;
      }
    }
    outedges.push(e);
    e.length = -1;
  }

  /**
   * Method to delete an incomming edge to this vertex
   *
   * @param e the edge to be deleted
   */
  public final void delInEdge(Edge e) {
    for (int i = 0; i < inedges.size(); i++) {
      Edge et = inEdge(i);
      if (e.end.equals(et.end)) {
        et.multi -= e.multi;
        if (et.multi <= 0) {
          inedges.remove(et);
        }
      }
    }
  }

  /**
   * Method to delete an outcomming edge to this vertex
   *
   * @param e the edge to be deleted
   */
  public final void delOutEdge(Edge e) {
    for (int i = 0; i < outedges.size(); i++) {
      Edge et = outEdge(i);
      if (e.start.equals(et.start)) {
        et.multi -= e.multi;
        if (et.multi <= 0) {
          outedges.remove(et);
        }
      }
    }
  }

  /**
   * Method to delete an equation of this vertex
   */
  public final void delEquation()
  {
      equation = null;
  }

  /**
   * Method to find the incomming edge in the position p
   *
   * @param p is the position of the incomming edge
   * @return the Edge in the positon p
   */
  public final Edge inEdge(int p) {
    return ((Edge) inedges.toArray()[p]);
  }

  /**
   * Method to find the outcoming edge in the position p
   *
   * @param p is the position of the outcomming edge
   * @return the Edge in the positon p
   */
  public final Edge outEdge(int p) {
    return ((Edge) outedges.toArray()[p]);
  }

  /**
   * Method to move the Vertex and its label
   *
   * @param a is the x-coordinated
   * @param b is the y-coordinated
   */
  public final void moveRelative(double a, double b) {
    int x = (int) a;
    int y = (int) b;
    moveLabelRelative(x, y);
    position.x += x;
    position.y += y;
  }

  /**
   * Method to get the string that represent the Vertex
   * @return a String with all the information of a Vertex
   */
  @Override
  public String toString() {
    String s = "(Vertex ";
    s += String.valueOf(hashCode());
    s += " " + position;
    s += " " + super.toString() + " ";
    s += " )";
    return s;
  }
public String getType()
{
  return this.type;
}
}