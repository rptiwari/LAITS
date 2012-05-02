package laits.graph;

import laits.Main;
import java.awt.*;
import java.io.Serializable;
import java.util.LinkedList;

/**
 * Defines edges/links for the graph.
 *
 * @author Javier Gonzalez Sanchez
 * @author Lakshmi Sudha Marothu
 * @author Patrick Lu
 * @version 20100116
 */
public class Edge extends Selectable implements Serializable{

  // How many times the edge has been drawing between the vertices.
  public int multi = 1;
  // The current calculated <cos,sin> and length of this edge.
  public Point rotation = new Point(0, 0);
  // The Ctrl point for the curved link
  public Point pMid = new Point(0, 0);
  public Point control = new Point(0,0);
  public double length = -1;
  // These are used for drawing arrows, they are declared here to improve speed.
  static int[] xvals = new int[4];
  static int[] yvals = new int[4];
  public Vertex start;
  public Vertex end;
  int side = 2 * size;
  int p1x = 0, p1y = 0, p2x = 0, p2y = 0;
  int p1xmid = 0, p1ymid = 0, p2xmid = 0, p2ymid = 0;  
  public String edgetype = ""; // Can be either a flowlink or a regularlink
  public boolean set = true; // If set is true, use middle point of the two vertices as control point.
  public boolean showInListModel = true;
  private LinkedList allEdges;   // All edges in the graph, reference from graph.getEdges()
  int width  = 10 * size;
  int height = 5 * size;

  private boolean inside (int x, int y, double startX, double startY, double endX, double endY) {
      System.out.println("Entra inside");
    double xMayor = 0, xMenor = 0, yMayor = 0, yMenor =0;
    if (startX > endX){
      xMayor = startX;
      xMenor = endX;
    }
    else {
      xMenor = startX;
      xMayor = endX;
    }
    if (startY > endY){
      yMayor = startY;
      yMenor = endY;
    }
    else {
      yMenor = startY;
      yMayor = endY;
    }

    xMenor -= 5;
    yMenor -= 5;
    xMayor += 5;
    yMayor += 5;

    System.out.println("===============================");
    System.out.println("Start (" + startX + " , " + startY);
    System.out.println("End   (" + endX + " , " + endY);
    System.out.println("Menor (" + xMenor + " , " + yMenor);
    System.out.println("Mayor (" + xMayor + " , " + yMayor);
    System.out.println("Punto (" + x + " , " + y);
    System.out.println("===============================");
    if (x>=xMenor && x<=xMayor && y>=yMenor && y<=yMayor){
      return true;
    }
    return false;
  }


  /**
   * Check whether a point contains a curved link
   * @param x position of mouse
   * @param y position of mouse
   * @return true if the point contains curved link, false otherwise
   */
 public boolean contains(int x, int y) {
 System.out.println("Entra CONTAINS");
 
    if (!edgetype.equals("regularlink")) return false; //go out if not curveLink

    validate();
    // start and end
    Point p1 = start.position;
    Point p2 = end.position;
    double p1X = p1.x + width/2;
    double p1Y = p1.y + height/2;
    double p2X = p2.x + width/2;
    double p2Y = p2.y + height/2;
    // central point
    Point pm = JGScalculateControlPoint();

    // case one p1 to pm
    if (inside (x,y, p1X, p1Y, pm.x, pm.y)){
      double d = Math.sqrt((x - p1X) * (x - p1X) + (y - p1Y) * (y - p1Y));
      double angleA = Math.atan((y - p1Y)/(x - p1X));
      double angleB = Math.atan((pm.y - p1Y)/(pm.x - p1X));
      double angleC = angleB - angleA;
      //System.out.println("CURVELINK SEGMENT A:" + (Math.abs(d * Math.sin(angleC))<5)  );
      if (Math.abs(d * Math.sin(angleC))<5) return true;
    }
    
    if (inside (x,y, pm.x, pm.y, p2X, p2Y)){
      // case pm to p2
      double d = Math.sqrt((x - pm.x) * (x - pm.x) + (y - pm.y) * (y - pm.y));
      double angleA = Math.atan((y - pm.y)/(x - pm.x));
      double angleB = Math.atan((p2Y - pm.y)/(p2X - pm.x));
      double angleC = angleB - angleA;
      //System.out.println("CURVELINK SEGMENT A:" + (Math.abs(d * Math.sin(angleC))<5)  );
      return (Math.abs(d * Math.sin(angleC))<5);
    }
    return false;
  }

  /**
   * Method to validate that the vertexes linked by this edge are different vertexes,
   * and to compute and update the distance between them.
   */
  public final void validate() {
    if (length < 0) {
      Point p1 = start.position;
      Point p2 = end.position;
      length = Point.distance(p1.x, p1.y, p2.x, p2.y);
      if (length == 0) return;
      double dy = p2.y - p1.y;
      double dx = p2.x - p1.x;
      rotation.x = (int)(dx / length);
      rotation.y = (int)(dy / length);
    }
  }

  /**
   * Force recalculation of length/distance and rotation.
   */
  public final void revalidate() {
    length = -1;
    validate();
  }

  /**
   * Constructor
   * Creates a new Edge between Vertex a and Vertex b
   *
   * @param a is the starting Vertex
   * @param b is the ending Vertex
   * @param edges all edges in the Graph class
   */
  public Edge(Vertex a, Vertex b, LinkedList edges) {
    super();
    start = a;
    end = b;
    allEdges = edges;
    revalidate();
  }

  /**
   * Method to get all the information of the Edge
   * @return a String with all the data of the Edge
   */
  @Override
  public String toString() {
    String s = "(Edge de: ";
    s += " " + start.label + " a ";
    s += " " + end.label + " de tipo: ";
    s += " " + edgetype;
    return s + " )";
  }


  @Override
   public final boolean near(int x,int y,double limit) {
    //JGS
    double startX = start.position.x + width/2 ;
    double startY = start.position.y + height/2;
    double endX = end.position.x + width/2;
    double endY = end.position.y + height/2;

    System.out.println("Entra near");
    if (inside (x, y, startX, startY, endX, endY)){
      return distance(x,y)<limit;
    }
    return false;
    //END JSG
    //return distance(x,y)<limit;
  }
  /**
   * Method to define the length/distance of the shortest segment perpendicular
   * to this edge that touches the edge and the point x,y
   *
   * @param x is the x-coordinate where the perpendicular segment touches the edge
   * @param y is the y-coordinate where the perpendicualr segment touches the edge
   * @return a number >10000 if there is no such segment.
   */
  @Override
  public final double distance(double x, double y) {
    validate();
    // coloca start y end al centro de los nodos
    Point p1 = start.position;
    Point p2 = end.position;
    double p1X = p1.x + width/2;
    double p1Y = p1.y + height/2;
    double p2X = p2.x + width/2;
    double p2Y = p2.y + height/2;
    double d = Math.sqrt((x - p1X) * (x - p1X) + (y - p1Y) * (y - p1Y));
    double angleA = Math.atan((y - p1Y)/(x - p1X));
    double angleB = Math.atan((p2Y - p1Y)/(p2X - p1X));
    double angleC = angleB - angleA;
    System.out.println("=== Edge de " + start.label + " a " + end.label + "===");
    System.out.println("p1x: " + p1X + ", p1y: " + p1Y + ", p2X: " + p2X + ", p2Y: " + p2Y);
    System.out.println("x: " + x + "Y :" + y);
    System.out.println("d: " + d);
    System.out.println("angleA: " + angleA + ", angleB: " + angleB + ", angleC: " + angleC);
    System.out.println("distancia FINAL: " + d * Math.sin(angleC));
    return Math.abs(d * Math.sin(angleC));
    /*x -= (p.x + width/2);
    y -= (p.y + height/2);
    double cos = rotation.x;
    double sin = rotation.y;
    double tx = x * cos + y * sin;
    if ((length == 0) || (tx < 0) || (tx > length))
      return length + 10000;
    double ty = -x * sin + y * cos;
    ty = Math.abs(ty);
    return ty;*/
  }

  /**
   *
   * @param x
   * @param y
   * @return
   */
  public final boolean nearStart(int x, int y) {

System.out.println("near start");
    int limit = 10 + size + start.size;
    return start.near(x, y, limit);
  }

  /**
   *
   * @param x
   * @param y
   * @return
   */
  public final boolean nearEnd(int x, int y) {
System.out.println("near end");
    int limit = 10 + size + end.size;
    return end.near(x, y, limit);
  }

  /**
   *  FIXME: Decide how to point the arrowhead. Either using control point or start-end vertices
   * @param x1 start point of direction vector
   * @param y1 start point of direction vector
   * @param x2 end point of direction vector
   * @param y2 end point of direction vector
   * @param g2d
   */
  private void drawArrow(Graphics2D g2d) {
  
    if (set) calculateControlPoint();

    double length = 11;
    double angle = 77;
    double startX = start.position.x + width/2 ;
    double startY = start.position.y + height/2;
    double endX = end.position.x + width/2;
    double endY = end.position.y + height/2;
    double r = Math.sqrt((startX - endX) * (startX - endX) + (startY - endY) * ( startY - endY));
    double sinAngle = (endY - startY)/r;
    double cosAngle = (endX - startX)/r;
    //controlx = (p1xmid - pMid.x) * Math.cos(angle) - (p1y -  pMid.y) * Math.sin(angle) + pMid.x;
    //controly = (p1xmid - pMid.x) * Math.sin(angle) + (p1ymid -  pMid.y) * Math.cos(angle) + pMid.y;
    double pointAx = (pMid.x - control.x)/2 +control.x;
    double pointAy = (pMid.y - control.y)/2 +control.y;
    double pointMidx = pointAx - length * cosAngle;
    double pointMidy = pointAy - length * sinAngle;
    double pointBx = (pointAx - pointMidx) * Math.cos(angle) - (pointAy -  pointMidy) * Math.sin(angle) + pointMidx;
    double pointBy = (pointAx - pointMidx) * Math.sin(angle) + (pointAy -  pointMidy) * Math.cos(angle) + pointMidy;
    double pointCx = (pointAx - pointMidx) * Math.cos(-angle) - (pointAy -  pointMidy) * Math.sin(-angle) + pointMidx;
    double pointCy = (pointAx - pointMidx) * Math.sin(-angle) + (pointAy -  pointMidy) * Math.cos(-angle) + pointMidy;
    double pointDx = pointMidx +  length * cosAngle/3;
    double pointDy = pointMidy +  length * sinAngle/3;

    xvals[0] = (int) pointBx;
    xvals[1] = (int) pointAx;
    xvals[2] = (int) pointCx;
    xvals[3] = (int) pointDx;
    yvals[0] = (int) pointBy;
    yvals[1] = (int) pointAy;
    yvals[2] = (int) pointCy;
    yvals[3] = (int) pointDy;
    Color temp = g2d.getColor();
    //g2d.setColor(getColor(color));
    g2d.setColor(Color.magenta);
    // fillPolygon is used to filled arrow
    g2d.fillPolygon(xvals, yvals, 4);
    g2d.setColor(temp);
  }

  /**
   * 
   * @param d
   */
  @Override
  public final void adjustSize(int d) {
    int r = size + d;
    if (r > 2)
      size = r;
  }

  /**
   *  Shift the position.
   */
  public final void moveRelative(double x, double y) {
    moveLabelRelative((int) x, (int) y);
  }

  /**
   * Calculate the center position of each vertex type<br>
   * Store center position of start vertex in (p1x,p1y)<br>
   * Store center position of end vertex in (p2x,p2y)<br>
   */
  public final void calculateCenterPoint() {
    Point p1 = start.position;
    Point p2 = end.position;
    p1x = p1.x + 50;
    p1y = p1.y + 25;
    p2y = p2.y + 25;
    p2x = p2.x + 50;
  }

  /**
   * Set control point of a QuadCurve2D object to the middle straight line of
   * two vertices.
   */
  public final void calculateCtrlPoint() {
    //calculateBorderPoint();
      calculateCenterPoint();
    if (p1x > p2x)
      pMid.x = p2x + (p1x - p2x) / 2;
    else
      pMid.x = p1x + (p2x - p1x) / 2;
    if (p1y > p2y)
      pMid.y = p2y + (p1y - p2y) / 2;
    else
      pMid.y = p1y + (p2y - p1y) / 2;
  }

  public final void calculateControlPoint(){

    calculateCtrlPoint();
    double angle = 77;
    control.x = (int)((p1x - pMid.x) * Math.cos(angle) - (p1y -  pMid.y) * Math.sin(angle) + pMid.x);
    control.y = (int)((p1x - pMid.x) * Math.sin(angle) + (p1y -  pMid.y) * Math.cos(angle) + pMid.y);
  }

  /**
   *
   */
  public void calculateBorderPoint() {
    calculateCenterPoint();
    // Moving the link from midpoint to the border of the vertex.
    int dx, dy, wx, wy, sx, sy, tx, ty;
    dx = p1x - p2x;
    dy = p1y - p2y;
    int re = end.size;
    int rs = start.size;
    double radius = Math.sqrt(dx * dx + dy * dy);
    wx = (int) (((double) dy) / radius * size / 4.0);
    wy = (int) (-((double) dx) / radius * size / 4.0);
    sx = (int) (((double) dx) / radius * rs);
    sy = (int) (((double) dy) / radius * rs);
    tx = (int) (((double) dx) / radius * (re + size / 1.4));
    ty = (int) (((double) dy) / radius * (re + size / 1.4));
    int xtemp0 = p1x - wx - sx;
    int ytemp0 = p1y - wy - sy;
    int xtemp1 = p2x - wx + tx;
    int ytemp1 = p2y - wy + ty;
    int xtemp2 = p2x + wx + tx;
    int ytemp2 = p2y + wy + ty;
    int xtemp3 = p1x + wx - sx;
    int ytemp3 = p1y + wy - sy;
    p1xmid = (xtemp0 + xtemp3) / 2;
    p1ymid = (ytemp0 + ytemp3) / 2;
    p2xmid = (xtemp1 + xtemp2) / 2;
    p2ymid = (ytemp1 + ytemp2) / 2;
  }

  /**
   * This method is used to check the conditions edge
   * and returns the type of edge that needs to be drawn.
   *
   * @param start is the starting vertex
   * @param end is the ending vertex
   * @return the edge type (either invalid, flowlink or regularlink)
   */
  public final String edgeType(Vertex start, Vertex end) {
    if (start.type.equals("none") || end.type.equals("none")){
      //if(start.VERSIONID.equals("112"))
      if(Main.professorVersion != Main.VERSION2 && Main.professorVersion != Main.DEMO_VERSION2)
      {
          edgetype = "invalid";
      }
      else edgetype = "regularlink";
    } else if (existEdgeBetween(start, end)==0){
      // review that there is not a current edge
      if (start.type.equals("constant") && (end.type.equals("flow") || end.type.equals("auxiliary"))) {
        // CONSTANT TYPE. Output: Flow and Auxiliary. Inputs: None.
        edgetype =  "regularlink";
      } else if (start.type.equals("auxiliary") && (end.type.equals("flow") || end.type.equals("auxiliary"))){
        // AUXILIARY TYPE. Output: Flow, Auxiliary. Inputs: Constants, Auxiliary, Stock, Flow.
        edgetype =  "regularlink";
      } else if (start.type.equals("flow") && end.type.equals("stock")){
        // FLOW TYPE. Outputs: Stock (flowlink), Auxiliary. Inputs: Stock (flowlink / regularlink), auxiliary, constant.
        edgetype =  "flowlink";
      } else if (start.type.equals("flow") && end.type.equals("auxiliary")){
        edgetype =  "regularlink";
      } else if (start.type.equals("stock") && (end.type.equals("auxiliary") || end.type.equals("stock"))){
        //STOCK TYPE. Outputs: Flow, Auxiliary, Stock. Inputs: Flow, Stock.
        edgetype =  "regularlink";
      } else if (start.type.equals("stock") && end.type.equals("flow")){
        edgetype =  "flowlink";
      } else {
        // ANY OTHER CONNECTION IS INVALID
        edgetype =  "invalid";
      }
    } else {
      // There is already an edge between this two nodes
      if (existEdgeBetween(start,end)==1){
        Edge edge;
        if ((start.type.equals("stock") && end.type.equals("flow"))) {
          for (int i=0; i<this.allEdges.size(); i++){
            edge = (Edge) this.allEdges.toArray()[i];
            if (start.label.equals(edge.start.label) && end.label.equals(edge.end.label)){
              if (edge.edgetype.equals("regularlink")){
                edgetype = "flowlink";
                break;
              } else{
                edgetype = "regularlink";
                break;
              }
            }
          }
        } else if ((start.type.equals("flow") && end.type.equals("stock"))) {
          for (int i=0; i<this.allEdges.size(); i++){
            edge = (Edge) this.allEdges.toArray()[i];
            if (start.label.equals(edge.end.label) && end.label.equals(edge.start.label)){
              if (edge.edgetype.equals("regularlink")){
                edgetype = "flowlink";
                break;
              } else{
                edgetype = "invalid";
                break;
              }
            }
          }
        } else {
          edgetype =  "invalid";
        }
      } else {
        edgetype = "invalid";
      }
    }
    return edgetype;
  }

  /**
     * Method that review if there is an edge between Vertex a and Vertex b
     * @param a is a Vertex
     * @param b is a Vertex
     * @return true if there is an edge, false otherwise
     */
  private int existEdgeBetween(Vertex a, Vertex b){
       Object[] edges = allEdges.toArray();
       int lenE = allEdges.size();
       Edge edge;
       boolean exist = false;

       int cont = 0;

       for (int i=0; i<lenE; i++){
         edge = (Edge) edges[i];
         // There is an edge between these two nodes
         if ((a.label.equals(edge.start.label) && b.label.equals(edge.end.label)) || (a.label.equals(edge.end.label) && b.label.equals(edge.start.label))) {
           cont++;
           exist=true;
         }
       }
       if (exist && cont==1){
          //log.out(LogType.ACTIVITY, "There is NOT an edge between " + a.label + " y " + b.label);
          cont = 0;
          exist=false;
       } else{
          //log.out(LogType.ACTIVITY, "There is an edge between " + a.label + " y " + b.label);
          cont--;
          exist=true;
       }
       //return exist;
       System.out.println("EDGE Cont: " + cont);
       return cont;
    }

  /* PRIVATE AUXILIAR METHODS ----------------------------------------------- */

  /**
   * This method is used to draw the flow link
   *
   * @param g
   */
  private void paintFlowLink(Graphics g) {
    double startX = start.position.x + width/2 ;
    double startY = start.position.y + height/2;
    double endX = end.position.x + width/2;
    double endY = end.position.y + height/2;
    double angle = Math.atan(Math.abs(startY - endY) / Math.abs(startX - endX));
    g.setColor(getColor(color));
    if (angle > 0.5 ){
      g.drawLine((int)startX-2, (int)startY, (int)endX-2, (int)endY);
      g.drawLine((int)startX+2, (int)startY, (int)endX+2, (int)endY);
    }else{
      g.drawLine((int)startX, (int)startY+2, (int)endX, (int)endY+2);
      g.drawLine((int)startX, (int)startY-2, (int)endX, (int)endY-2);
    }
  }

  /**
   * This method is used to draw the regular link for version 2
   *
   * @param g
   */
  private void paintV2RegularLink(Graphics g) {
    double startX = start.position.x + width/2 ;
    double startY = start.position.y + height/2;
    double endX = end.position.x + width/2;
    double endY = end.position.y + height/2;
//    double angle = Math.atan(Math.abs(startY - endY) / Math.abs(startX - endX));
    g.setColor(getColor(color));
//    if (angle > 0.5 ){
//      g.drawLine((int)startX-2, (int)startY, (int)endX-2, (int)endY);
      g.drawLine((int)startX, (int)startY, (int)endX, (int)endY);
//    }else{
//      g.drawLine((int)startX, (int)startY+2, (int)endX, (int)endY+2);
//      g.drawLine((int)startX, (int)startY-2, (int)endX, (int)endY-2);
//    }
  }


  /**
   * Method used by flow link to draw an arrow.
   *
   * @param g Graphic object
   */
  private void paintFlowArrow(Graphics g) {
    revalidate();
    if (length == 0) return;
    int[] xval = new int[3];
    int[] yval = new int[3];
    double longitude = 11;
    double angle = 77;
    double startX = start.position.x + width/2 ;
    double startY = start.position.y + height/2;
    double endX = end.position.x + width/2;
    double endY = end.position.y + height/2;
    double midx =(startX + (endX - startX)/2);
    double midy =(startY + (endY - startY)/2);
    double r = Math.sqrt((startX - endX) * (startX - endX) + (startY - endY) * ( startY - endY));
    double sinAngle = (endY - startY)/r;
    double cosAngle = (endX - startX)/r;
    double pointAx = midx + longitude * cosAngle;
    double pointAy = midy + longitude * sinAngle;
    double pointBx = (pointAx - midx) * Math.cos(angle) - (pointAy -  midy) * Math.sin(angle) + midx;
    double pointBy = (pointAx - midx) * Math.sin(angle) + (pointAy -  midy) * Math.cos(angle) + midy;
    double pointCx = (pointAx - midx) * Math.cos(-angle) - (pointAy -  midy) * Math.sin(-angle) + midx;
    double pointCy = (pointAx - midx) * Math.sin(-angle) + (pointAy -  midy) * Math.cos(-angle) + midy;
    // double pointMidx = midx +  longitude * cosAngle/3;
    // double pointMidy = midy +  longitude * sinAngle/3;
    xval[0] = (int) pointBx;
    xval[1] = (int) pointAx;
    xval[2] = (int) pointCx;
    // xvals[3] = (int) pointMidx;
    yval[0] = (int) pointBy;
    yval[1] = (int) pointAy;
    yval[2] = (int) pointCy;
    // yvals[3] = (int) pointMidy;
    Color temp = g.getColor();
    g.setColor(getColor(color));
    g.fillPolygon(xval, yval, 3);
    g.setColor(temp);
  }

  /**
   *
   * @param g
   */
  private void paintTemporalLink(Graphics g) {
    validate();
    int x = start.position.x;
    int y = start.position.y;
    int widthx  = 10 * size;
    int heighx  = 5  * size;
    int centerx = x + widthx /2;
    int centery = y + heighx /2;
    Graphics2D g2d = (Graphics2D) g;
    g2d.setColor(getColor(color));
    Point p2 = end.position;
    float dashes[] = {6f, 3f };
    BasicStroke stroke = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1f, dashes, 0f);
    Stroke oldStroke = g2d.getStroke();
    g2d.setStroke(stroke);
    g2d.drawLine(centerx, centery, p2.x, p2.y);
    g2d.setStroke(oldStroke);
  }

  /**
   *
   * @param g
   */
  private void paintTemporalArrow(Graphics g) {
    revalidate();
    if (length == 0) return;
    int[] jxvals = new int[3];
    int[] jyvals = new int[3];
    int sizeArrow = 5;
    double jp1x = start.position.x;
    double jp1y = start.position.y;
    jp1x = jp1x + 50;
    jp1y = jp1y + 25;
    length = Point.distance(jp1x, jp1y, end.position.x, end.position.y);
    if (length == 0) return;
    double dy = end.position.y - jp1y;
    double dx = end.position.x - jp1x;
    double sin = dy / length;
    double cos = dx / length;
    // double sin = rotation.y;
    // double cos = rotation.x;
    // double ax = start.position.x + 0.5;
    // double ay = start.position.y + 0.5;
    double ax = start.position.x + 50; //width/2
    double ay = start.position.y + 25; //height/2
    // double px = length - (end.size + 1);
    double px = length;
    double py = 0;
    double lx = length - (sizeArrow*2);
    double ly = sizeArrow;
    double rx = lx;
    double ry = -ly;
    double tx, ty;
    tx = px * cos - py * sin;
    ty = px * sin + py * cos;
    px = tx;
    py = ty;
    tx = lx * cos - ly * sin;
    ty = lx * sin + ly * cos;
    lx = tx;
    ly = ty;
    tx = rx * cos - ry * sin;
    ty = rx * sin + ry * cos;
    rx = tx;
    ry = ty;
    px += ax;
    py += ay;
    lx += ax;
    ly += ay;
    rx += ax;
    ry += ay;
    jxvals[0] = (int) px;
    jxvals[1] = (int) lx;
    jxvals[2] = (int) rx;
    jyvals[0] = (int) py;
    jyvals[1] = (int) ly;
    jyvals[2] = (int) ry;
    Color temp = g.getColor();
    g.setColor(getColor(color));
    g.fillPolygon(jxvals, jyvals, 3);
    g.setColor(temp);
  }


  private Point JGScalculateControlPoint() {
    // calculate control point
    double longitude = 10;
    double angle = 77;
    double startX = start.position.x + width/2 ;
    double startY = start.position.y + height/2;
    double endX = end.position.x + width/2;
    double endY = end.position.y + height/2;
    double r = Math.sqrt( Math.pow(startX - endX, 2) + Math.pow (startY - endY, 2) );
    double sinAngle = (endY - startY)/r;
    double cosAngle = (endX - startX)/r;
    Point p1 = start.position;
    Point p2 = end.position;
    int p1xx = p1.x + 50;
    int p1yy = p1.y + 25;
    int p2yy = p2.y + 25;
    int p2xx = p2.x + 50;
    //Position pMidx = new Position(0, 0);
    Point pMidx = new Point (0,0);
    if (p1xx > p2xx) pMidx.x = p2xx + (p1xx - p2xx) / 2;
    else pMidx.x = p1xx + (p2xx - p1xx) / 2;
    if (p1yy > p2yy) pMidx.y = p2yy + (p1yy - p2yy) / 2;
    else pMidx.y = p1yy + (p2yy - p1yy) / 2;
    Point controlx  = new Point(0,0);
    controlx.x = (int)((p1xx - pMidx.x) * Math.cos(angle) - (p1yy -  pMidx.y) * Math.sin(angle) + pMidx.x);
    controlx.y = (int)((p1xx - pMidx.x) * Math.sin(angle) + (p1yy -  pMidx.y) * Math.cos(angle) + pMidx.y);
    int pointAx = (int)((pMidx.x - controlx.x)/2 +controlx.x);
    int pointAy = (int)((pMidx.y - controlx.y)/2 +controlx.y);
    int middleX = pointAx;
    int middleY = pointAy;
    return new Point (middleX, middleY);
  }

  /**
   *  This method will paint a curved link.
   *  This method is intended to replace the regular link (straight).
   *  The method takes two positions from both vertices and calculates its middle position.
   *  If the middle y axis still have smoothnesspixels, we substract it to make it as our ctrlY.
   *  Otherwise, we add smoothness pixels and make it as our ctrlY. Middle x axis is our ctrlX.
   *  A good graph representation for curved link @
   *  http://java.sun.com/developer/technicalArticles/GUI/java2d/Figure1.gif
   *
   *  @param g Graphic object
   */
  private void paintCurvedLink(Graphics g) {
    validate();
    double startX = start.position.x + width/2 ;
    double startY = start.position.y + height/2;
    double endX = end.position.x + width/2;
    double endY = end.position.y + height/2;
    Point p = JGScalculateControlPoint();
    // Set curved link thickness
    g.setColor(getColor(Color.MAGENTA));
    // g.setColor(Color.magenta);
    Graphics2D g2D = (Graphics2D) g;
    Stroke mystroke = g2D.getStroke();
    g2D.setStroke(new BasicStroke(1.0f));
    g2D.drawLine((int)startX, (int)startY, (int)p.x,(int)p.y);
    g2D.drawLine((int)p.x, (int)p.y, (int)endX, (int)endY);
    g2D.setStroke(mystroke);
  }

  /**
   *  This method will paint a curved link.
   *  This method is intended to replace the regular link (straight).
   *  The method takes two positions from both vertices and calculates its middle position.
   *  If the middle y axis still have smoothnesspixels, we substract it to make it as our ctrlY.
   *  Otherwise, we add smoothness pixels and make it as our ctrlY. Middle x axis is our ctrlX.
   *  A good graph representation for curved link @
   *  http://java.sun.com/developer/technicalArticles/GUI/java2d/Figure1.gif
   *
   *  @param g Graphic object
   */
  private void paintV2FlowLink(Graphics g) {
    validate();
    double startX = start.position.x + width / 2;
    double startY = start.position.y + height / 2;
    double endX = end.position.x + width / 2;
    double endY = end.position.y + height / 2;
    Point p = JGScalculateControlPoint();
    // Set curved link thickness
    Graphics2D g2D = (Graphics2D) g;
    g2D.setColor(Color.BLACK);
    Stroke mystroke = g2D.getStroke();
    g2D.setStroke(new BasicStroke(1.0f));
    g2D.drawLine((int) startX, (int) startY, (int) p.x, (int) p.y);
    g2D.drawLine((int) p.x, (int) p.y, (int) endX, (int) endY);
    g2D.setStroke(mystroke);
  }

  /**
   * Draw an arrow for curved link
   * @param g
   */
  private void paintCurvedArrow(Graphics g) {
      int[] xval = new int[3];
      int[] yval = new int[3];
      Point p = JGScalculateControlPoint();
      double longitude = 10;
      double angle = 77;
      double startX = start.position.x + width / 2;
      double startY = start.position.y + height / 2;
      double endX = end.position.x + width / 2;
      double endY = end.position.y + height / 2;
      double midx = (startX + (endX - startX) / 2);
      double midy = (startY + (endY - startY) / 2);
      double r = Math.sqrt((startX - endX) * (startX - endX) + (startY - endY) * (startY - endY));
      double sinAngle = (endY - startY) / r;
      double cosAngle = (endX - startX) / r;
      double pointAx = p.x;
      double pointAy = p.y;
      double pointMidx = pointAx - longitude * cosAngle;
      double pointMidy = pointAy - longitude * sinAngle;
      double pointBx = (pointAx - pointMidx) * Math.cos(angle) - (pointAy -  pointMidy) * Math.sin(angle) + pointMidx;
      double pointBy = (pointAx - pointMidx) * Math.sin(angle) + (pointAy -  pointMidy) * Math.cos(angle) + pointMidy;
      double pointCx = (pointAx - pointMidx) * Math.cos(-angle) - (pointAy -  pointMidy) * Math.sin(-angle) + pointMidx;
      double pointCy = (pointAx - pointMidx) * Math.sin(-angle) + (pointAy -  pointMidy) * Math.cos(-angle) + pointMidy;
      xval[0] = (int) pointBx;
      xval[1] = (int) pointAx;
      xval[2] = (int) pointCx;
      yval[0] = (int) pointBy;
      yval[1] = (int) pointAy;
      yval[2] = (int) pointCy;
    Graphics2D g2d = (Graphics2D) g;
    Color temp = g2d.getColor();
    if (Main.professorVersion != Main.VERSION2 && Main.professorVersion != Main.DEMO_VERSION2) {
      g2d.setColor(getColor(Color.MAGENTA));
    } else {
      g2d.setColor(getColor(Color.BLACK));
    }
    // g2d.setColor(Color.magenta);
    g2d.fillPolygon(xval, yval, 3);
    g2d.setColor(temp);
//    double startX = start.position.x + width/2 ;
//    double startY = start.position.y + height/2;
//    double endX = end.position.x + width/2;
//    double endY = end.position.y + height/2;
//    double r = Math.sqrt( Math.pow(startX - endX, 2) + Math.pow (startY - endY, 2) );
//    double sinAngle = (endY - startY)/r;
//    double cosAngle = (endX - startX)/r;
//    Point p = JGScalculateControlPoint();
//    double longitude = 10;
//    double angle = 77;
//    double pointAx = p.x;
//    double pointAy = p.y;
//    double pointMidx = pointAx - longitude * cosAngle;
//    double pointMidy = pointAy - longitude * sinAngle;
//    double pointBx = (pointAx - pointMidx) * Math.cos(angle) - (pointAy -  pointMidy) * Math.sin(angle) + pointMidx;
//    double pointBy = (pointAx - pointMidx) * Math.sin(angle) + (pointAy -  pointMidy) * Math.cos(angle) + pointMidy;
//    double pointCx = (pointAx - pointMidx) * Math.cos(-angle) - (pointAy -  pointMidy) * Math.sin(-angle) + pointMidx;
//    double pointCy = (pointAx - pointMidx) * Math.sin(-angle) + (pointAy -  pointMidy) * Math.cos(-angle) + pointMidy;
//    double pointDx = pointMidx +  longitude * cosAngle/3;
//    double pointDy = pointMidy +  longitude * sinAngle/3;
//    xvals[0] = (int) pointBx;
//    xvals[1] = (int) pointAx;
//    xvals[2] = (int) pointCx;
//    xvals[3] = (int) pointDx;
//    yvals[0] = (int) pointBy;
//    yvals[1] = (int) pointAy;
//    yvals[2] = (int) pointCy;
//    yvals[3] = (int) pointDy;
//    Graphics2D g2d = (Graphics2D) g;
//    Color temp = g2d.getColor();
//    g2d.setColor(getColor(Color.MAGENTA));
//    // g2d.setColor(Color.magenta);
//    g2d.fillPolygon(xvals, yvals, 4);
//    g2d.setColor(temp);
  }

  /* OVERRIDE METHODS ------------------------------------------------------- */

  /**
   * s edges as flow link or regular link.
   *
   * @param g
   */
  @Override
  public final void paint(Graphics g) {
    if ("flowlink".equals(edgetype)) {
      if(Main.professorVersion != Main.VERSION2 && Main.professorVersion != Main.DEMO_VERSION2) {
        paintFlowLink(g);
        paintFlowArrow(g);
      } else {
        paintV2FlowLink(g);
        paintCurvedArrow(g);
      }
    } else if ("regularlink".equals(edgetype)) {
      if(Main.professorVersion != Main.VERSION2 && Main.professorVersion != Main.DEMO_VERSION2) {
        paintCurvedLink(g);
        paintCurvedArrow(g);
      } else {
        paintV2RegularLink(g);
        paintFlowArrow(g);
      }
    } else {
      paintTemporalLink(g);
      paintTemporalArrow(g);
    }
  }

  public LinkedList getAllEdges() {
    return allEdges;
  }



 /* GETTER METHODS ------------------------------------------------------- */




  /* OLD METHODS ------------------------------------------------------------ */

  /**
   *  This method will paint a curved link.
   *  This method is intended to replace the regular link (straight).
   *  The method takes two positions from both vertices and calculates its middle position.
   *  If the middle y axis still have smoothnesspixels, we substract it to make it as our ctrlY.
   *  Otherwise, we add smoothness pixels and make it as our ctrlY. Middle x axis is our ctrlX.
   *  A good graph representation for curved link @
   *  http://java.sun.com/developer/technicalArticles/GUI/java2d/Figure1.gif
   *
   *  @param g Graphic object
   *
  private void paintCurvedLink(Graphics g) {
    // The bigger value the line is less smooth.
    // The bigger value the line is less smooth.
    //int smoothness = 100;
    double angle = 77;
    Color currentColor = getColor(color);
    Graphics2D g2D = (Graphics2D) g;
    validate();
    g2D.setColor(Color.magenta);
    if (set){
     calculateControlPoint();
    }
    calculateCtrlPoint();
    //calculateControlPoint();
    /*double pointBx = (pointAx - midx) * Math.cos(angle) - (pointAy -  midy) * Math.sin(angle) + midx;
    double pointBy = (pointAx - midx) * Math.sin(angle) + (pointAy -  midy) * Math.cos(angle) + midy;*
    // Save current stroke
    //controlx = (p1xmid - pMid.x) * Math.cos(angle) - (p1ymid -  pMid.y) * Math.sin(angle) + pMid.x;
    //controly = (p1xmid - pMid.x) * Math.sin(angle) + (p1ymid -  pMid.y) * Math.cos(angle) + pMid.y;
    Stroke mystroke = g2D.getStroke();
    // Set curved link thickness
    g2D.setStroke(new BasicStroke(1.0f));
    //g2D.drawLine((int)controlx, (int)controly, (int)controlx, (int)controly+50);
    //g2D.drawLine((int)pMid.x, (int)pMid.y, (int)pMid.x, (int)pMid.y+50);
    //g2D.drawLine((int)p1xmid, (int)p1ymid, (int)pMid.x, (int)pMid.y-50);
    //g2D.drawLine((int)p2xmid, (int)p2ymid, (int)pMid.x, (int)pMid.y-50);

    QuadCurve2D qc = new QuadCurve2D.Double(p1x, p1y, control.x, control.y, p2x, p2y);

    g2D.draw(qc);
    //g2D.drawLine((int)(pMid.x), (int)(pMid.y-smoothness),(int)(pMid.x+100),(int)(pMid.y-smoothness+100));
    // Restore stroke
    g2D.setStroke(mystroke);
  }
  */

    /**
   * Check whether a point contains a curved link
   * @param x position of mouse
   * @param y position of mouse
   * @return true if the point contains curved link, false otherwise
   *
  public boolean contains(int x, int y) {
    // Reject all non-curved link
    // log.out(LogType.DEBUG_LOCAL, "Edgetype:" + edgetype);
    // logs.out(ACTIVITY, "Edgetype:" + edgetype);

    if (!edgetype.equals("regularlink"))
      return false;

    calculateCenterPoint();
    //calculateBorderPoint();

    if (set) calculateCtrlPoint();
    double angle = 77;

    Point2D p = new Point2D.Float(x, y);
    QuadCurve2D.Double q = new QuadCurve2D.Double(p1x, p1y, control.x, control.y , p2x, p2y);
    return q.contains(p);
  }
  */

  /**
   * Check whether a point contains a curved link
   * @param x position of mouse
   * @param y position of mouse
   * @return true if the point contains curved link, false otherwise
   *
  public boolean contains(int x, int y) {

    if (!edgetype.equals("regularlink")) return false; //go out if not curveLink

    calculateCenterPoint();
    if (set) calculateCtrlPoint();
    double angle = 77;
    Point2D p = new Point2D.Float(x, y);
    QuadCurve2D.Double q = new QuadCurve2D.Double(p1x, p1y, control.x, control.y , p2x, p2y);
    return q.contains(p);

  }
  */


}