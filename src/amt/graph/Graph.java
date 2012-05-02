package amt.graph;

import amt.Main;
import amt.comm.CommException;
import amt.data.TaskFactory;
import amt.gui.HintDialog;
import amt.gui.MessageDialog;
import amt.gui.PlotDialog;
import amt.gui.QuizDialog;
import java.awt.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import amt.log.*;
import amt.parser.Equation;
import amt.parser.Parser;
import amt.parser.Scanner;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

/**
 * This class create a graph for the activity.
 *
 * A Graph is a collection of vertexes and edges.
 * The vertexes are the nodes of the graph and are called "Vertex" represented by different shapes
 * (dashed-line rectangle, double-line rectangle, circle, double-line circle, diamond, or a valve),
 * that represents a different type of vertex.
 * The edges are the links between the nodes of the graph and are called "Edges". There are represented
 * by straight lines, curved lines, or double-line lines, depending on the type of edge.
 *
 * @author Javier Gonzalez Sanchez
 * @author Patrick Lu
 * @version 20100116
 */
public class Graph extends Selectable {

  private Selectable selected = null;
  private LinkedList<Vertex> vertexes = new LinkedList<Vertex>();
  private LinkedList<Edge> edges = new LinkedList<Edge>();
  private TaskFactory server;
  public int taskID;
  private static Logger logs = Logger.getLogger();
  private LinkedList<PlotDialog> plots = new LinkedList<PlotDialog>();
  private LinkedList<QuizDialog> quiz = new LinkedList<QuizDialog>();
  private LinkedList<HintDialog> hint = new LinkedList<HintDialog>();
  private LinkedList<String> professorOutputFile = new LinkedList<String>();
  private int n;
  private static int professorVersion = 1;
  public boolean errorRun;

  /**
   * Method to set an object as the one currently selected
   * 
   * @param s
   */
  public void select(Selectable s) {
    if (selected!=null ) selected.isSelected = false;
    if (s!=null ) s.isSelected = true;

    selected = s;
  }

  /**
   * Method to unselect the object
   */
  public void unselect() {
    if (selected!=null ) selected.isSelected = false;
    selected = null;
  }

  /**
   * Method to find out if there is a selected object
   *
   * @return true or false
   */
  public boolean isSelected(Selectable s) {
    if (s!=null) 
      return s.equals(selected);
    return false;
  }

  /**
   * This method sets the value for n which is the value from yes/no dialogs
   */
  public void setN(int newN)
  {
      this.n = newN;
  }

  /**
   * This method returns the value for n which is the value from yes/no dialogs
   */
  public int getN()
  {
      return n;
  }

  /**
   * Method to get the sum of all the input degrees of the vertexes on the graph
   * @return sum of all input degrees of the vertexes on the graph
   */
  public int getSumOfInputDegrees() {
    int sum = 0;
    int n = vertexes.size();
    Object a[] = vertexes.toArray();
    for (int i = 0; i < n; i++) {
      sum = sum + ((Vertex) a[i]).inDegree();
    }
    return sum;
  }

  /**
   * Default Constructor
   */
  public Graph(int professorVersion) {
      this.professorVersion = professorVersion;
      try {
        this.server = TaskFactory.getInstance();
      } catch (CommException ex) {
        // catch exception
      }
  }

  /**
   * Constructor
   * Creates a graph with the elements in a file
   * @param filename is the name of the file that contents the data for the graph
   */
  public Graph(String filename) {
    try {
      FileInputStream f = new FileInputStream(filename);
      ObjectInputStream s = new ObjectInputStream(f);
      Graph g = (Graph) s.readObject();
      f.close();
      vertexes = g.vertexes;
      edges = g.edges;
    } catch (Exception ex) {
      logs.concatOut(Logger.DEBUG, "Graph.Graph.1", ex.toString());
    }
  }

  /**
   * Method to get the vertex in the given position
   * @param position of the desirable vertex
   * @return the vertex in the given position
   */
  public final Vertex vertex(int position) {
    return ((Vertex) (vertexes.toArray()[position]));
  }

  /**
   * Method to get the edge in the given position
   * @param position of the desirable edge
   * @return the edge in the given position
   */
  public final Edge edge(int position) {
    return ((Edge) (edges.toArray()[position]));
  }

  /**
   * Inserts a new vertex v in the graph. This vertex already has data on it
   * @param v the vertex to be inserted
   * @return the inserted vertex
   */
  public final Vertex addVertex(Vertex v) {
    vertexes.push(v);
    return v;
  }

  /**
   * Inserts a new empty vertex in the graph
   * @return the inserted empty vertex
   */
  public final Vertex addVertex() {
    Vertex v = new Vertex();
    vertexes.push(v);
    return v;
  }

  /**
   * Inserts a new vertex in the graph with the x and y coordinates as position
   * @param x the x-axis coordenate of the vertex
   * @param y the y-axis coordenate of the vertes
   * @return the new inserted vertex
   */
  public final Vertex addVertex(int x, int y) {
    Vertex v = new Vertex(x, y);
    vertexes.push(v);
    return v;
  }

  /**
   * Deletes an edge e of the graph. Before deleting the edge it also delete the edge from
   * the start and end vertex that this edge connects.
   * @param e is the edge to be removed from the graph.
   */
  public final void delEdge(Edge e) {
    e.start.delOutEdge(e);
    e.end.delInEdge(e);

    if(professorVersion != Main.VERSION2 && professorVersion != Main.DEMO_VERSION2 ) {
      //TODO: Do I need to delete equation on the start vertex? Or just on the end vertex
      // e.start.equation = null;
      if(e.edgetype.equals("invalid")) {
        //do nothing
      } else {
        e.end.equation = null;
      }
    }

    edges.remove(e);
  }

  /**
   * Deletes a vertex v from the graph. While deleting a vertex we delete its edges
   * and also we delete the vertex from the edges that connect it with other vertexes.
   * @param v is the vertex to be deleted
   */
  public final void delVertex(Vertex v) {
    int n = v.inedges.size();
    Object a[] = v.inedges.toArray();
    for (int j = 0; j < n; j++) {
      Edge e = (Edge) a[j];
      edges.remove(e);
      e.start.delOutEdge(e);
    }
    n = v.outedges.size();
    Object b[] = v.outedges.toArray();
    for (int j = 0; j < n; j++) {
      Edge e = (Edge) b[j];
      edges.remove(e);
      e.end.delInEdge(e);
    }
    vertexes.remove(v);
  }

  /**
   * This method adds a new edge
   * @param e is the edge
   * @return the newly added edge
   */
  public final Edge addEdge(Edge e) {
    edges.push(e);
    return e;
  }

  /**
   * Add an edge in the graph that connects the vertexes a and b
   *
   * @param a is the initial vertex
   * @param b is the ending vertex
   * @return the new inserted edge
   */
  public final Edge addEdge(Vertex a, Vertex b) {
    Edge e = new Edge(a, b, getEdges());

    /* JGS20100308
    for (int i = 0; i < edges.length; i++) {
      if (e.start.equals(((Edge) edges.elements[i]).start) && e.end.equals(((Edge) edges.elements[i]).end)) {
        ((Edge) edges.elements[i]).multi += e.multi;
        a.addOutEdge(e);
        b.addInEdge(e);
        return ((Edge) edges.elements[i]);
      }
    }
    */

    edges.push(e);
    a.addOutEdge(e);
    b.addInEdge(e);
    //log.out(LogType.DEBUG_LOCAL, "vertex a:"+a.type);
    //log.out(LogType.DEBUG_LOCAL, "vertex b:"+b.type);
    //logs.out(DEBUG, "vertex a:"+a.type);
    //logs.out(DEBUG, "vertex b:"+b.type);
    return e;
  }

  /**
   * Add an edge in the graph that connects the vertexes a and b with a specified type
   *
   * @param a is the initial vertex
   * @param b is the ending vertex
   * @return the new inserted edge
   */
  public final Edge addEdge(Vertex a, Vertex b, String type) {
    Edge e = new Edge(a, b, getEdges());
    e.edgetype = type;
    edges.push(e);
    a.addOutEdge(e);
    b.addInEdge(e);
    //log.out(LogType.DEBUG_LOCAL, "vertex a:"+a.type);
    //log.out(LogType.DEBUG_LOCAL, "vertex b:"+b.type);
    //logs.out(DEBUG, "vertex a:"+a.type);
    //logs.out(DEBUG, "vertex b:"+b.type);
    return e;
  }

  /**
   * Add an edge in the graph that connects the vertexes at the positions a and b in the graph
   * @param a is the position in the array of elements to the initial vertex
   * @param b is the position in the array of elements to the ending vertex
   * @return the new inserted edge
   */
  public final Edge addEdge(int a, int b) {
    Vertex v1 = (Vertex) vertexes.toArray()[a];
    Vertex v2 = (Vertex) vertexes.toArray()[b];
    return addEdge(v1, v2);
  }

  /**
   * Method to print all the vertexes and edges in the graph
   * @return a String with all the information of the graph
   */
  @Override
  public String toString() {
    String s = "(graph\n";
    s += vertexes.toString() + "\n";
    s += edges.toString();
    s = s + "\n)";
    return s;
  }

  /**
   * Paints all the vertexes, vertexes' labels and edges of the graph
   * @param g
   */
  @Override
  public final void paint(Graphics g) {
    int n = edges.size();
    Object x[] = edges.toArray();
    int j;
    for (j = 0; j < n; j++) {
      ((Edge) x[j]).paint(g);
    }
    n = vertexes.size();
    x = vertexes.toArray();
    for (j = 0; j < n; j++) {
      ((Vertex) x[j]).paint(g);
    }
    for (j = 0; j < n; j++) {
      ((Vertex) x[j]).paintLabel(g);
//      if(((Vertex) x[j]).equation != null)
//        System.out.println(((Vertex)x[j]).nodeName + " " + ((Vertex) x[j]).equation.toString() + " Eq2: " + ((Vertex) x[j]).equation2.toString());
//      else System.out.println("vertex eq is null");
    }
    n = edges.size();
    x = edges.toArray();
    for (j = 0; j < n; j++) {
      ((Edge) x[j]).paintLabel(g);
    }
  }

  /**
   * This method is to set the graph linked list
   * @param pd is the graph linked list
   */
  public void setPlots(LinkedList<PlotDialog> pd)
  {
      plots = pd;
  }

  /**
   * This method is to return the linked list of graphs
   * @return
   */
  public LinkedList<PlotDialog> getPlots()
  {
      return plots;
  }

    /**
   * This method is to set the graph linked list
   * @param qd is the graph linked list
   */
  public void setQuiz(LinkedList<QuizDialog> qd)
  {
      quiz = qd;
  }

    /**
   * This method is to return the linked list of graphs
   * @return
   */
  public LinkedList<QuizDialog> getQuiz()
  {
      return quiz;
  }

  public void setHint(LinkedList<HintDialog> ht)
  {
      hint = ht;
  }

    /**
   * This method is to return the linked list of graphs
   * @return
   */
  public LinkedList<HintDialog> getHint()
  {
      return hint;
  }
  /**
   * Move all the graph x distance in the x-axis and y distance in the y-axis
   * @param x distance in x-axis
   * @param y distance in y-axis
   */
  public final void moveRelative(double x, double y) {
    int n = edges.size();
    Object a[] = edges.toArray();
    int j;
    for (j = 0; j < n; j++) {
      ((Edge) a[j]).moveRelative(x, y);
    }
    n = vertexes.size();
    a = vertexes.toArray();
    for (j = 0; j < n; j++) {
      ((Vertex) a[j]).moveRelative(x, y);
    }
  }
    
  /**
   * Save all the information in Graph.xml
   *
   * @throws IOException
   */
    public final void save(File f) throws IOException {
        System.out.println("INISAVE");
        //FY DataBase server;
        TaskFactory server;
        try {
        server = TaskFactory.getInstance();
        } catch (CommException de) {
return;
        }

        Document doc = DocumentHelper.createDocument();
        //TaskID
        Element xml_graph = doc.addElement("graph");
        Element xml_vertexes = xml_graph.addElement("vertexes");
        Element xml_edges = xml_graph.addElement("edges");
        Element xml_task = xml_graph.addElement("Task");
        Object[] a = getVertexes().toArray();
        //vertex
        for (int i = 0; i < getVertexes().size(); i++) {
            Element xml_vertex = xml_vertexes.addElement("vertex");
            Element xml_id = xml_vertex.addElement("id");
            Element xml_label = xml_vertex.addElement("label");
            xml_label.setText(((Vertex) a[i]).label);
            xml_id.setText(((Vertex) a[i]).hashCode() + "");
            Element xml_inedges = xml_vertex.addElement("inedges");
            Object[] b = ((Vertex) a[i]).inedges.toArray();
            for (int j = 0; j < b.length; j++) {
                if (((Edge) b[j]) != null) {
                    Element xml_inedge = xml_inedges.addElement("inedge");
                    xml_inedge.setText(((Edge) b[j]).hashCode() + "");
                }
            }
            Object[] c = ((Vertex) a[i]).outedges.toArray();
            Element xml_outedges = xml_vertex.addElement("outedges");
            for (int k = 0; k < c.length; k++) {
                if (((Edge) c[k]) != null) {
                    Element xml_outedge = xml_outedges.addElement("outedge");
                    xml_outedge.setText(((Edge) c[k]).hashCode() + "");
                }
            }
            Element xml_position = xml_vertex.addElement("position");
            Element xml_x = xml_position.addElement("x");
            xml_x.setText(((Vertex) a[i]).position.x + "");
            Element xml_y = xml_position.addElement("y");
            xml_y.setText(((Vertex) a[i]).position.y + "");
            Element xml_type = xml_vertex.addElement("type");
            xml_type.setText(((Vertex) a[i]).type);
            Element xml_equation = xml_vertex.addElement("equation");
            if (((Vertex) a[i]).equation != null) {
                xml_equation.setText(((Vertex) a[i]).equation.toString());
            }
        }
        // edges
        LinkedList edges1 = getEdges();
        Object[] e = edges1.toArray();
        for (int x = 0; x < edges1.size(); x++) {
            Element xml_edge = xml_edges.addElement("edge");
            Edge e3 = (Edge) e[x];
            Vertex start1 = e3.start;
            Vertex end1 = e3.end;
            String edgetype1 = e3.edgetype;
            Element xml_id = xml_edge.addElement("id");
            xml_id.setText(e3.hashCode() + "");
            Element xml_start = xml_edge.addElement("start");
            xml_start.setText(start1.hashCode() + "");
            Element xml_end = xml_edge.addElement("end");
            xml_end.setText(end1.hashCode() + "");
            Element xml_edgetype = xml_edge.addElement("type");
            xml_edgetype.setText(edgetype1);
        }
        Element xml_taskID = xml_task.addElement("TaskID");

       
        //FY xml_taskID.setText(String.valueOf(server.getActualTask().getId()));
     xml_taskID.setText(String.valueOf(server.getActualTask().getId()));

        Writer out = new OutputStreamWriter(new FileOutputStream(f), "utf-8");
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = new XMLWriter(out, format);
        writer.write(doc);
        out.close();
        // System.out.println("END");
    }

  /**
   *
   * @param name
   * @return
   */
  public Vertex searchVertexByName(String name) {
    for (int i=0; i<vertexes.size();i++) {
      Vertex v = ((Vertex)vertexes.toArray()[i]);
      if (name.toUpperCase().equals(v.label.toUpperCase())) {
        return v;
      }
    }
    return null;
  }

  public int countNodesOfType (String nodeType){
    int counter = 0;
    for (int i=0; i<vertexes.size(); i++){
      Vertex v = ((Vertex)vertexes.toArray()[i]);
      if (nodeType.toUpperCase().equals(v.type.toUpperCase())) {
        counter++;
      }
    }
    return counter;
  }

  public LinkedList <String> getLabelsOfNodesType(String nodeType){
    LinkedList <String> labels = new LinkedList<String>();
    LinkedList <Edge> inEdges = new LinkedList<Edge>();
    LinkedList <Edge> outEdges = new LinkedList<Edge>();

    for (int i=0; i<vertexes.size(); i++){
      Vertex v = ((Vertex)vertexes.toArray()[i]);

      if (nodeType.toUpperCase().equals("INFLOW")){
        if (v.type.toUpperCase().equals("FLOW")){
          for (int o =0; o<v.outedges.size(); o ++){
            Edge e = (Edge)v.outedges.toArray()[o];
            if (e.edgetype.equals("flowlink"))
              labels.add(v.label.toUpperCase());
          }
        }
      } else if (nodeType.toUpperCase().equals("OUTFLOW")){
        if (v.type.toUpperCase().equals("FLOW")){
          for (int o =0; o<v.inedges.size(); o ++){
            Edge e = (Edge)v.inedges.toArray()[o];
            if (e.edgetype.equals("flowlink"))
              labels.add(v.label.toUpperCase());
          }
        }

      } else if (nodeType.toUpperCase().equals(v.type.toUpperCase())) {
        labels.add(v.label.toUpperCase());
      }
    }
    return labels;
  }

  /**
   *
   * @param f
   * @throws DocumentException
   */
    public void load(File f) throws DocumentException {
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(f);
        Element Graph = document.getRootElement();
        Element vertexes = Graph.element("vertexes");
        java.util.List<Element> vertexNode = vertexes.elements("vertex");
        HashMap<Integer, Vertex> map = new HashMap<Integer, Vertex>();
        for (Element e : vertexNode) {
            int id = Integer.parseInt(e.elementText("id"));
            Element label = e.element("label");
            Element position = e.element("position");
            Element xe = position.element("x");
            Element ye = position.element("y");
            Element inedges = e.element("inedges");
            Element outedges = e.element("outedges");
            Element type = e.element("type");
            Element equation = e.element("equation");
            int x = 0;
            int y = 0;
            try {
                x = (int) Float.parseFloat(xe.getText());
                y = (int) Float.parseFloat(ye.getText());
            } catch (Exception ex) {
            }
            java.util.List<Element> il = inedges.elements("inedge");
            if (il != null) {
                for (Element se : il) {
                    int iid = Integer.parseInt(se.getText());
                }
            }
            Vertex v = new Vertex(x, y, label.getText());
            v.type = type.getText();
            // System.out.println(v);
            // v.type = type.getText();
            // System.out.println(v.type);
            map.put(id, v);
            Parser parser = new Parser();
            Equation eq = new Equation();
            Scanner sc = new Scanner(eq);
            String eqStr = "";
            if (equation != null) {
                eqStr = equation.getText().trim();
            }
            int ttype = 0;
            String tmp = "";
            for (int i = 0; i < eqStr.length(); i++) {
                int scx = 0;
                char c = eqStr.charAt(i);
                String letter = String.valueOf(c);
                switch (c) {
                    case '(':
                    case ')':
                        letter = String.valueOf(c);
                        ttype = 5;
                        break;
                    case '.':
                        letter = ".";
                        ttype = 2;
                        break;
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                        letter = String.valueOf(c);
                        ttype = 1;
                        break;
                    case '+':
                    case '-':
                    case '*':
                    case '/':
                        letter = String.valueOf(c);
                        ttype = 4;

                        break;
                    default:
                        scx = 1;
                }
                if (scx == 1) {
                    tmp += letter;
                } else {
                    if (tmp.length() > 0) {
                        sc.addInput(tmp, 3);
                        tmp = "";
                    }
                    sc.addInput(letter, ttype);
                }
            }
            if (tmp.length() > 0) {
                sc.addInput(tmp, 3);
            }
            v.equation = eq;
            parser.setEquation(eq);
            parser.parse();
            this.addVertex(v);
        }
        Element edges = Graph.element("edges");
        java.util.List<Element> edgeNode = edges.elements("edge");
        for (Element e : edgeNode) {
            int id = Integer.parseInt(e.elementText("id"));
            Element starte = e.element("start");
            Element ende = e.element("end");
            Element type = e.element("type");
            int start = 0;
            int end = 0;
            try {
                start = Integer.parseInt(starte.getText());
                end = Integer.parseInt(ende.getText());
            } catch (Exception ex) {
            }
            Vertex v1 = map.get(start);
            Vertex v2 = map.get(end);
            Edge edge = this.addEdge(v1, v2);
            v2.addInEdge(edge);
            v1.addOutEdge(edge);
            edge.edgetype = type.getText();
            // System.out.println(((Edge)this.edges.elements[0]).edgetype);
        }
        Element task = Graph.element("Task");
        String taskNode = task.elementText("TaskID");
        // System.out.println(taskNode);
        this.taskID = Integer.valueOf(taskNode);
    }

    /**
     * This method creates an output file for the professor. It is only called when
     * a quiz is successfully completed.
     */
    public void createProfessorOutput() {
      try {
        TaskFactory server = TaskFactory.getInstance();
        String fileName = server.getActualTask().getLevel() + "-" + server.getActualTask().getTitle().replace(" ", "") + "Solution.txt";
        BufferedWriter professorWriter = new BufferedWriter(new FileWriter(fileName));
        //Write each node's type, inputs, and outputs
        professorWriter.write("======== NODE TYPES, INPUTS, AND OUTPUTS =========");
        professorWriter.newLine();
        for (int i = 0; i < vertexes.size(); i++) {
          Vertex current = vertexes.get(i);
          professorWriter.write(current.label + " type: " + current.type);
          professorWriter.newLine();
          //find inputs
          String allInputs = "";
          //System.out.println("current = " + current.label);
          for(int j = 0; j < current.inedges.size(); j++) {
            if(j == 0) {
              allInputs = current.inedges.get(j).edgetype + " - " + current.inedges.get(j).start.label;
            } else allInputs += ", " + current.inedges.get(j).edgetype + " - " + current.inedges.get(j).start.label;
          }
          professorWriter.write(current.label + " inputs: " + allInputs);
          professorWriter.newLine();
          //find outputs
          String allOutputs = "";
          for(int j = 0; j < current.outedges.size(); j++) {
            if(j == 0) {
              allOutputs = current.outedges.get(j).edgetype + " - " + current.outedges.get(j).end.label;
            } else allOutputs += ", " + current.outedges.get(j).edgetype + " - " + current.outedges.get(j).end.label;
          }
          professorWriter.write(current.label + " outputs: " + allOutputs);
          professorWriter.newLine();
        }
        professorWriter.newLine();
        //Write the equations and values to the file
        for (int i = 0; i < professorOutputFile.size(); i++) {
          professorWriter.write(professorOutputFile.get(i));
          professorWriter.newLine();
        }
        professorWriter.flush();
        professorWriter.close();
      } catch (Exception ex) {
        //ADD CORRECT LOGGER
        java.util.logging.Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

    /**
   * Method to run the Model
   */
  public boolean run (TaskFactory server, GraphCanvas graphCanvas){

    // We obtain this values from the Task object
//    Timer timer = new Timer(1000, new UpdateActionListener());
//    timer.setInitialDelay(0);
//    timer.start();
    int count = 0;
    // VALIDATE THE PARSING TO INT
    int startTime = server.getActualTask().getStartTime();
    int endTime = server.getActualTask().getEndTime();
    professorOutputFile = new LinkedList<String>();

    //delete the graphs that are open
    for(int i = 0; i < plots.size(); i++)
    {
        plots.get(i).dispose();
    }
    //delete the quizDialog that are open
    for(int i = 0; i < quiz.size(); i++)
    {
        quiz.get(i).dispose();
    }
    //delete the HintDialog that are open
    for(int i = 0; i < hint.size(); i++)
    {
        hint.get(i).dispose();
    }
    // HELEN - adjust the times to handle array indexes
    int newStartTime = startTime, newEndTime = endTime;
    if (startTime != 0){
      newStartTime = 0;
      newEndTime = endTime - startTime + 1;
    }

    String runLabel = "Model running...";
    errorRun = false;
    boolean enableQuiz;

    //Assure that the values are  empty
    for (int i=0; i<this.getVertexes().size(); i++){
      if (!this.vertex(i).type.equals("none"))
        if (!this.vertex(i).equation.value.isEmpty() || this.vertex(i).equation.value == null)
          this.vertex(i).equation.value = new LinkedList<Double>();
    }
    graphCanvas.setButtonLabel(runLabel);
    LinkedList<Vertex> constantList = new LinkedList<Vertex>();
    LinkedList<Vertex> auxiliaryList = new LinkedList<Vertex>();
    LinkedList<Vertex> flowList = new LinkedList<Vertex>();
    LinkedList<Vertex> stockList = new LinkedList<Vertex>();

    for (int i=0; i<this.getVertexes().size(); i++){
      if (this.vertex(i).type.equals("constant")){
        constantList.add(this.vertex(i));
      } else if (this.vertex(i).type.equals("auxiliary")){
        if (auxiliaryList.isEmpty()){
          auxiliaryList.add(this.vertex(i));
        } else {
          int size = auxiliaryList.size();
          for (int j=0; j<size; j++){
            for (int k=0; k<auxiliaryList.get(j).inedges.size(); k++){
              if (((Edge)(auxiliaryList.get(j).inedges.toArray()[k])).edgetype.equals("regularlink")) {
                Vertex v = ((Edge)(auxiliaryList.get(j).inedges.toArray()[k])).start;
                if (v == this.vertex(i)){
                  auxiliaryList.add(j,this.vertex(i));
                } else {
                  int temp = 0;
                  for(int n = 0; n<auxiliaryList.size(); n++){
                    if(auxiliaryList.get(n)== this.vertex(i))
                        break;
                    else
                        temp++;
                  }
                    if(temp == auxiliaryList.size())
                        auxiliaryList.add(j+1,this.vertex(i));
                }
              }
            }
          }
        }
      }
       else if (this.vertex(i).type.equals("flow")) {
        if (professorVersion != Main.VERSION2 && professorVersion != Main.DEMO_VERSION2 ) {
          flowList.add(this.vertex(i));
        } else {
          if (flowList.isEmpty()) {
            flowList.add(this.vertex(i));
          } else {
            //for every flow in the flowlist
            boolean alreadyAdded = false;
            for (int j = 0; j < flowList.size(); j++) {
              //Look through all of that flow's edges
              if (alreadyAdded == false) {
                for (int k = 0; k < flowList.get(j).inedges.size(); k++) {
                  //if there is a regularlink edge
                  if (((Edge) (flowList.get(j).inedges.toArray()[k])).edgetype.equals("regularlink")) {
                    //set vertex v equal to the start of that edge
                    Vertex v = ((Edge) (flowList.get(j).inedges.toArray()[k])).start;
                    //if this vertex equals the vertex that needs to be added
                    if (v == this.vertex(i)) {
                      //add it at a specific location
                      flowList.add(j, this.vertex(i));
                      alreadyAdded = true;
                      break;
                    } 
                  }
                }
              }
            }
            if(alreadyAdded == false) {
                flowList.add(this.vertex(i));
              }
          }
        }

      } else if (this.vertex(i).type.equals("stock")) {
              if (stockList.isEmpty()) {
                  stockList.add(this.vertex(i));
              } else {
                  size = stockList.size();
                  for (int j = 0; j < size; j++) {
                      for (int k = 0; k < stockList.get(j).inedges.size(); k++) {
                          if (((Edge) (stockList.get(j).inedges.toArray()[k])).edgetype.equals("regularlink")) {
                              Vertex v = ((Edge) (stockList.get(j).inedges.toArray()[k])).start;
                              if (v == this.vertex(i)) {
                                  stockList.add(j, this.vertex(i));
                                  break;
                              } else {
                                  stockList.addLast(this.vertex(i));
                                  break;
                              }
                          }
                      }
                      stockList.add(this.vertex(i));
                  }
              }
          }
      }
    try{
       //If the version is the professor version, print the values to the console
        if(professorVersion == Main.PROFESSORVERSION) {
           String lineToPrint = "";
           lineToPrint = "======== EQUATIONS =========";
           professorOutputFile.add(lineToPrint);
           //Add all correct equations
           for (int i = 0; i < constantList.size(); i++) {
              Vertex node = constantList.get(i);
              lineToPrint = "Equation: " + node.label + " = " + node.equation.toString();
              professorOutputFile.add(lineToPrint);
            }
           for (int i = 0; i < auxiliaryList.size(); i++) {
              Vertex node = auxiliaryList.get(i);
              lineToPrint = "Equation: " + node.label + " = " + node.equation.toString();
              professorOutputFile.add(lineToPrint);
            }
           for (int i = 0; i < flowList.size(); i++) {
              Vertex node = flowList.get(i);
              lineToPrint = "Equation: " + node.label + " = " + node.equation.toString();
              professorOutputFile.add(lineToPrint);
            }
           for (int i = 0; i < stockList.size(); i++) {
              Vertex node = stockList.get(i);
              lineToPrint = "Equation: " + node.label + " = " + node.equation.toString();
              professorOutputFile.add(lineToPrint);
            }
           lineToPrint = "\n";
           professorOutputFile.add(lineToPrint);
           
           // Evaluate every list
           for (int x=newStartTime; x<=newEndTime; x++){
              lineToPrint = "======== TIME:  " + x + "==========";
              System.out.println("\n" + lineToPrint);
              professorOutputFile.add(lineToPrint);

              lineToPrint = "CONSTANTS: " + constantList.size();
              System.out.println("\n" + lineToPrint);
              professorOutputFile.add(lineToPrint);

              for (int a=0; a<constantList.size(); a++){
                //double temp = constantList.get(a).equation.execute(graph);
                double temp = constantList.get(a).execute(this);
                //constantList.get(a).equation.value.add(temp);
                constantList.get(a).setAlreadyRun(true);

                lineToPrint = constantList.get(a).label + " = " + temp + ",";
                System.out.print(lineToPrint);
                professorOutputFile.add(lineToPrint);
              }

              lineToPrint = "AUXILIARIES: " + auxiliaryList.size();
              System.out.println("\n" + lineToPrint);
              professorOutputFile.add(lineToPrint);

              for (int a=0; a<auxiliaryList.size(); a++){
                //double temp = auxiliaryList.get(a).equation.execute(graph);
                double temp = auxiliaryList.get(a).execute(this);
                //auxiliaryList.get(a).equation.value.add(temp);
                lineToPrint = auxiliaryList.get(a).label + " = " + temp + ",";
                System.out.println(lineToPrint);
                professorOutputFile.add(lineToPrint);
                auxiliaryList.get(a).setAlreadyRun(true);
              }
              lineToPrint = "FLOWS: " + flowList.size();
              System.out.println("\n" + lineToPrint);
              professorOutputFile.add(lineToPrint);

              for (int a=0; a<flowList.size(); a++){
//                System.out.println("before execute flow");
                System.out.println(flowList.get(a).nodeName);
                System.out.println(flowList.get(a).equation.toString());
                //double temp = flowList.get(a).equation.execute(graph);
                double temp = flowList.get(a).execute(this);
//                System.out.println("after execute flow");
                //flowList.get(a).equation.value.add(temp);
               lineToPrint = flowList.get(a).label + " = " + temp + ",";
               System.out.println(lineToPrint);
               professorOutputFile.add(lineToPrint);
                flowList.get(a).setAlreadyRun(true);
              }
              lineToPrint = "STOCKS: " + stockList.size();
              System.out.println("\n" + lineToPrint);
              professorOutputFile.add(lineToPrint);;

              for (int a=0; a<stockList.size(); a++){
                //double temp = stockList.get(a).equation.execute(graph);
                double temp = stockList.get(a).execute(this);
                System.out.println("Si pude calcular el valor: " + temp);
                if(stockList.get(a).getNonNegative() == true)
                {
                    if(temp < 0)
                    {
                        stockList.get(a).equation.value.add(x, 0.0);
                        temp = 0;
                    }
                    else stockList.get(a).equation.value.add(x, temp);
                }
                else stockList.get(a).equation.value.add(x, temp);
                //stockList.get(a).equation.value.add(temp);
                lineToPrint = stockList.get(a).label + " = " + temp + ",";
                System.out.print(lineToPrint);
                professorOutputFile.add(lineToPrint);

                stockList.get(a).setAlreadyRun(true);
              }
            }
           //HELEN 20110222 - CREATES THE OUTPUT FILE WHILE RUNNING THE MODEL. 
           this.createProfessorOutput();
          }
          //Else the student version is running
          else
          {
            for (int x=newStartTime; x<=newEndTime; x++){
              for (int a=0; a<constantList.size(); a++){
                double temp = constantList.get(a).execute(this);
                constantList.get(a).setAlreadyRun(true);
              }
              for (int a=0; a<auxiliaryList.size(); a++){
                double temp = auxiliaryList.get(a).execute(this);
                auxiliaryList.get(a).setAlreadyRun(true);
              }
              for (int a=0; a<flowList.size(); a++){
                double temp = flowList.get(a).execute(this);
                flowList.get(a).setAlreadyRun(true);
              }
              for (int a=0; a<stockList.size(); a++){
                double temp = stockList.get(a).execute(this);
                if(stockList.get(a).getNonNegative() == true)
                {
                    if(temp < 0)
                    {
                        stockList.get(a).equation.value.add(x, 0.0);
                        temp = 0;
                    }
                    else stockList.get(a).equation.value.add(x, temp);
                }
                else stockList.get(a).equation.value.add(x, temp);
                stockList.get(a).setAlreadyRun(true);
              }
           }
        }
    } catch(Exception ex) {
      ex.printStackTrace();
      System.out.println(ex.toString());
      errorRun = true;
    } finally {
      if(errorRun == true) {
        //MessageDialog.showMessageDialog(null, true, "Error in running the model!", this);
        //Change all of the graph circles to red if the previous status was no status
        //for(int i = 0; i < getVertexes().size(); i++) {
        //  Vertex v = (Vertex)getVertexes().get(i);
        //  if(v.getGraphsButtonStatus() == v.NOSTATUS) {
        //    v.setGraphsButtonStatus(v.WRONG);
        //  }
        //}
        logs.out(Logger.ACTIVITY, "Graph.run.1");
        runLabel = "";
        enableQuiz = false;
        System.out.println("error in graph.run()");
      } else {    
        //MessageDialog.showMessageDialog(null, true, "Model run complete!", this);
        logs.out(Logger.ACTIVITY, "Graph.run.2");
        runLabel = "";
        graphCanvas.setModelChanged(false);
        enableQuiz = true;
        graphCanvas.setModelHasBeenRun(true);
      }    
      graphCanvas.setButtonLabel(runLabel);
      return enableQuiz;
    }
  }





  /* GETTER AN SETTER ------------------------------------------------------- */

  public Selectable getSelected() {
    return selected;
  }

  /**
   * Getter method to get the list of Vertexes in the graph
   *
   * @return a List with all the vertexes in the graph
   */
  public LinkedList getVertexes() {
    return vertexes;
  }

  public void setVertexes(LinkedList vertexes) {
    this.vertexes = vertexes;
  }

  /**
   * Getter method to get the edges in the graph
   * @return a List with all the edges in the graph
   */
  public LinkedList<Edge> getEdges() {
    return edges;
  }

  public void setEdges(LinkedList edges) {
    this.edges = edges;
  }

}