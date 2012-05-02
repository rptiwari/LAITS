package amt.comm;

import amt.Main;
import amt.data.Task;
import amt.data.TaskFactory;
import amt.graph.Edge;
import amt.graph.Vertex;
import amt.parser.Equation;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import amt.parser.Scanner;
import amt.parser.Parser;
import javax.swing.JOptionPane;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * This class make the connection with files
 *
 * @author Javier Gonzalez Sanchez
 * @version 20101115
 */
public class DataArchive {

  private static DataArchive entity;

  /**
   *
   */
  private DataArchive() {

  }

  /**
   *
   * @return
   */
  public static DataArchive getInstance() throws CommException{
    if (entity == null) {
      entity = new DataArchive();
    }
    return entity;
  }

  /**
   * Method to save task menu
   *
   */
  public void saveTasksToFile(LinkedList<Task> tasks) {
    try{
      Document doc = DocumentHelper.createDocument();
      Element allTask = doc.addElement("Tasks");
      for (int i = 0; i < tasks.size(); i++) {
        Element task = allTask.addElement("Task");
        Element taskId = task.addElement("TaskId");
        taskId.setText(String.valueOf(tasks.get(i).getId()));
        Element taskLevel = task.addElement("TaskLevel");
        taskLevel.setText(String.valueOf(tasks.get(i).getLevel()));
        Element taskName = task.addElement("TaskName");
        taskName.setText(String.valueOf(tasks.get(i).getTitle()));
        String pathName = "TaskMenu" + ".xml";
        File f = new File(pathName);
        Writer out = new OutputStreamWriter(new FileOutputStream(f), "utf-8");
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = new XMLWriter(out, format);
        writer.write(doc);
        out.close();
      }
    }catch (Exception e) {
    }
  }

  /**
   * Method to save task information
   *
   * @param idtask
   */
  public void saveTasksToFile(Task t) {
    try {
      Document doc = DocumentHelper.createDocument();
      Element task = doc.addElement("Task");
      Element url = task.addElement("URL");
      url.setText(t.getImageUrl());
      Element description = task.addElement("Description");
      description.setText(t.getDescription());
      Element shortDescription = task.addElement("ShortDescription");
      shortDescription.setText(t.getSummary());
      Element startTime = task.addElement("StartTime");
      startTime.setText(String.valueOf(t.getStartTime()));
      Element endTime = task.addElement("EndTime");
      endTime.setText(String.valueOf(t.getEndTime()));
      Element level = task.addElement("Level");
      level.setText(t.getLevel());
      Element units = task.addElement("Units");
      units.setText(t.getUnitTime());
      Element labels = task.addElement("Vertexes");
      for (int i = 0; i < t.getVertexNames().size(); i++) {
        Element verterLabel = labels.addElement("VertexLabel");
        verterLabel.setText(t.getVertexNames().get(i));
      }
      String pathName = "Task" + "/" + "Task" + t.getId() + ".xml";
      File f = new File(pathName);
      Writer out = new OutputStreamWriter(new FileOutputStream(f), "utf-8");
      OutputFormat format = OutputFormat.createPrettyPrint();
      XMLWriter writer = new XMLWriter(out, format);
      writer.write(doc);
      out.flush();
      out.close();
    }catch (Exception e) {
    }

  }

  /**
   * Select task from file
   *
   * @return
   */
  public LinkedList<Task> selectTasksFromFile() throws CommException {
    final String FILE = "TaskMenu.xml";
    LinkedList<Task> tasks = new LinkedList<Task>();
    SAXReader saxReader = new SAXReader();
    Document document = null;
    try {
      document = saxReader.read(FILE);
      Element Tasks = document.getRootElement();
      java.util.List<Element> taskNode = Tasks.elements("Task");
      for (Element e : taskNode) {
        tasks.add (new Task(Integer.valueOf(e.element("TaskId").getText()), e.element("TaskLevel").getText(), e.element("TaskName").getText()));
      }
      return tasks;
    } catch (DocumentException ex) {
      throw new CommException("DataArchive.selectTasksFromFile.1");
    }
  }


  /**
   * Method to select task from file
   *
   * @param idtask
   */
  public boolean selectTasksFromFile(Task task) throws CommException {
    LinkedList<String> listOfVertexes = new LinkedList<String>();
    SAXReader saxReader = new SAXReader();
    Document document = null;
    try {
      document = saxReader.read("Task" + "/" + "Task" + task.getId() + ".xml");
      Element taskNode = document.getRootElement();
      Element url = taskNode.element("URL");
      task.setImageUrl(url.getText());
      Element description = taskNode.element("Description");
      task.setDescription(description.getText());
      Element shortDescription = taskNode.element("ShortDescription");
      task.setSummary(shortDescription.getText());
      Element startTime = taskNode.element("StartTime");
      try {
        int n = Integer.parseInt(startTime.getText());
        task.setStartTime(n);
      } catch (NumberFormatException nfe) {
        task.setStartTime(0);
      }
      Element endTime = taskNode.element("EndTime");
      try {
        int n = Integer.parseInt(endTime.getText());
        task.setEndTime(n);
      } catch (NumberFormatException nfe) {
        task.setEndTime(100);
      }
      Element level = taskNode.element("Level");
      task.setLevel(level.getText());
      Element units = taskNode.element("Units");
      task.setUnitTime(units.getText());
      Element vertexes = taskNode.element("Vertexes");
      java.util.List<Element> vertexNode = vertexes.elements("VertexLabel");
      for (Element e : vertexNode) {
        String vertexLabel = e.getText();
        listOfVertexes.add(vertexLabel);
      }
      task.setVertexNames(listOfVertexes);
      return true;
    } catch (DocumentException ex) {
      throw new CommException("DataArchive.selectTasksFromFileTask.1");
    }
  }

  /**
   * Gets the decision tree for the task.
   * This tree only exist for the task in the version 2 of the tool.
   * And by now we are getting this information from a file.
   *
   */
    public void getTreeFromFile(Task task) {
      String fileName = "";
      String fileLine = "";
      BufferedReader TempReader = null;
      FileInputStream fi;
      LinkedList<String> tree = new LinkedList<String>();

      fileName = task.getLevel() + "-" + task.getTitle().replace(" ", "") + "Solution.txt";

      try {
        fi = new FileInputStream(fileName);
        TempReader = new BufferedReader(new InputStreamReader(fi));
        while ((fileLine = TempReader.readLine()) != null) {
          if (fileLine.startsWith("Tree: ")){
            fileLine = fileLine.replace("Tree: " , "");
            tree.add(fileLine);
          }
        }
        fi.close();
        TempReader.close();
      } catch (Exception ex) {
        //Add log
      }
      task.setTree(tree);
    }

    /**
     * Get the task type from the solution file
     *
     * @author Curt Tyler
     * @param task
     */
    public void getTaskTypeFromFile(Task task) {
      String fileName = "";

      fileName = task.getLevel() + "-" + task.getTitle().replace(" ", "") + "Solution.txt";
      String fileLine = "";
      String type = "";
      BufferedReader TempReader = null;
      FileInputStream fi;

      try {
        fi = new FileInputStream(fileName);
        TempReader = new BufferedReader(new InputStreamReader(fi));
        while ((fileLine = TempReader.readLine()) != null) {
          if (fileLine.startsWith("Type: ")) {
            fileLine = fileLine.replace("Type: ", "");
            type = fileLine.trim();
          }
        }
        fi.close();
        TempReader.close();
      } catch (Exception ex) {

      }
      task.setType(type);
    }

    private Vertex getVertexByName(LinkedList<Vertex> vertexes, String name){
      for(int i=0;i<vertexes.size();i++)
        if(vertexes.get(i).label.equals(name))
          return vertexes.get(i);
      return null;
    }

    private Vertex addVertexByName(LinkedList<Vertex> vertexes, String name){
      Vertex vertex=new Vertex();
      vertex.label=name;
      vertexes.add(vertex);
      return vertex;
    }

    private void createEdgeBetween (Vertex start, Vertex end, String edgeType, LinkedList<Edge> alledges){
      
      for(int i=0;i<alledges.size();i++){
        Edge edge=alledges.get(i);
        if(edge.start==start && edge.end==end)
          return;
      }
      Edge edge=new Edge(start, end, alledges);
      edge.showInListModel=false;
      edge.edgetype=edgeType;
      start.outedges.add(edge);
      end.inedges.add(edge);
      alledges.add(edge);
    }

    private String getStockEquationFromFile(String nodeName){
      String fileName = "";
      String stockEq="";
      try {
        TaskFactory server = TaskFactory.getInstance();
        fileName = server.getActualTask().getLevel() + "-" + server.getActualTask().getTitle().replace(" ", "") + "Solution.txt";
        FileInputStream f = new FileInputStream(fileName);
        BufferedReader fin=new BufferedReader(new InputStreamReader(f));

        String line="";
        while((line=fin.readLine())!=null){
          line=line.trim();
          if(line.startsWith("debug: "+nodeName+" inputs:")){
            line=line.replace("debug: "+nodeName+" inputs:", "");
            String []inputs_str=line.split(",");
            for(int i=0;i<inputs_str.length;i++){
              String type_str=inputs_str[i].split("-")[0].trim();
              String input_str=inputs_str[i].split("-")[1].trim();
              input_str=input_str.replace(" ", "_");
              if(type_str.equals("flowlink")){
                if(stockEq.equals(""))
                  stockEq=input_str;
                else
                  stockEq+=" + "+input_str;
              }
            }
          }
          else if(line.startsWith("debug: " + nodeName + " outputs:"))
          {
            line=line.replace("debug: "+nodeName+" outputs:", "");
            String []outputs_str=line.split(",");
            for(int i=0;i<outputs_str.length;i++){
              String type_str=outputs_str[i].split("-")[0].trim();
              String output_str=outputs_str[i].split("-")[1].trim();
              output_str=output_str.replace(" ", "_");
              if(type_str.equals("flowlink")){
                  stockEq+=" - "+output_str;
              }
            }
          }
          
        }
      }
      catch (IOException e){
        e.printStackTrace();
      }
      catch (CommException ex) {
        ex.printStackTrace();
      }
      return stockEq;


    }

    private void setStatusForAllVertexes(LinkedList<Vertex> vertexes){
      for(int i=0; i<vertexes.size();i++){
        Vertex vertex=vertexes.get(i);
        vertex.position.x=100+i*125;
        vertex.position.y=vertex.paintNoneHeight*2;
        vertex.defaultLabel();
        System.out.println("HEIGHT: "+vertex.position.y);

        vertex.setDescriptionButtonStatus(vertex.CORRECT);
        vertex.nodeName=vertex.label;
      try {
        DataArchive.getInstance().setVertexInfoBasedOnTaskFile(vertex);
        if(vertex.type.equals("stock")){
          vertex.stockEquation=this.getStockEquationFromFile(vertex.label);
        }
        else if(vertex.type.equals("constant")){
          vertex.setCalculationsButtonStatus(vertex.NOSTATUS);
          vertex.initialValueGiven=true;
          //Vertex.resetGraphStatus();
          Scanner scanner = new Scanner(vertex.equation);
          String eq_str=vertex.equation.toString();
          if ((scanner.getEquation() != null) && !vertex.equation.tokenList.isEmpty()) {
            while (scanner.removeInput());
          }
          scanner.addInput(eq_str, Scanner.DIGIT);
          Parser parser = new Parser();
          parser.setEquation(scanner.getEquation());
          if (parser.parse() ) {
            vertex.equation = parser.getEquation();
          }
        }
        System.out.println("correct equation: "+vertex.correctEquation);
        System.out.println("filled equation: "+vertex.equation);
        System.out.println("stock equation: "+vertex.stockEquation);
//debug        System.out.println("input: "+vertex.correctInputs);
//        System.out.println("output: "+vertex.correctOutputs);
//        System.out.println("correct desc: "+vertex.correctDescription);
//        System.out.println("filled desc: "+vertex.selectedDescription);
        vertex.selectedDescription=vertex.correctDescription;
      } catch (CommException ex) {
        Logger.getLogger(DataArchive.class.getName()).log(Level.SEVERE, null, ex);
      }
      }
    }

    public void getVertexesFromFile(Task task){
      String fileName = task.getLevel() + "-" + task.getTitle().replace(" ", "") + "Solution.txt";
      System.out.println("fileName: "+fileName);
      LinkedList<Vertex> vertexes=new LinkedList<Vertex>();
      LinkedList<Edge> alledges=new LinkedList<Edge>();
      try {
      FileReader fin = new FileReader(fileName);
      BufferedReader reader=new BufferedReader(fin);
      
      String line=null;
      boolean start=false;
      int attrType=0;
      String nodeName;
      Vertex vertex=null;
      while((line=reader.readLine())!=null){
        if(line.startsWith("Type:"))
          if(!line.replace("Type:", "").trim().equals("Debug")){
            Main.ReadModelFromFile=false;
            task.listOfVertexes=null;
            task.alledges=null;
            return;
          }
          else{
            Main.ReadModelFromFile=true;
            Main.alreadyRan=false;
          }
        if(line.startsWith("======== GIVEN MODEL ========")){
          start=true;
          continue;
        }
        if(!start)
          continue;
        if(start && line.trim().equals(""))
        {
          start = false;
          continue;
        }

        line=line.replace("debug: ", "");
        if(attrType==0) // this line indicates the node's type. locate or create the node in solution class
        {
          String[] words=line.split(" type: ");
          nodeName=words[0].trim();
          //add the node to solution or get the node from solution
          if((vertex=this.getVertexByName(vertexes,nodeName))==null)
            vertex=this.addVertexByName(vertexes,nodeName);
          vertex.type=words[1].trim();

        }
        else if (attrType==1){  //this line contains the node's input. It is only useful for function node.
          if(vertex==null)
            System.out.println("Cannot set the inputs for a null value");
          if((vertex.type.equals("stock") || vertex.type.equals("flow")) && line.split(" inputs: ").length>1){
            String[] parts=line.split(" inputs: ");
            String[] inputStr=parts[1].split(",");
            for(int i=0; i<inputStr.length; i++){
              String edgeType=inputStr[i].split("-")[0].trim();
              String inputName=inputStr[i].split("-")[1].trim();
              Vertex inputVertex=null;
              if((inputVertex=this.getVertexByName(vertexes,inputName))==null)
                inputVertex=this.addVertexByName(vertexes,inputName);
              if(inputVertex==null){
                System.out.println("Adding input node fails");
                return;
              }
              //get or add the input node successfully
              this.createEdgeBetween(inputVertex,vertex, edgeType, alledges);
            }
          }
        }
        else if (attrType==2){  //this line contains the node's input. It is only useful for function node.
          if(vertex==null)
          {
            System.out.println("Cannot set the inputs for a null value");
          }
          if((vertex.type.equals("stock") || vertex.type.equals("flow")) && line.split(" outputs: ").length>1){
            String[] parts=line.split(" outputs: ");
            String[] inputStr=parts[1].split(",");
            for(int i=0; i<inputStr.length; i++){
              String edgeType=inputStr[i].split("-")[0].trim();
              String outputName=inputStr[i].split("-")[1].trim();
              Vertex outputVertex=null;
              if((outputVertex=this.getVertexByName(vertexes, outputName))==null)
                outputVertex=this.addVertexByName(vertexes, outputName);
              if(outputVertex==null){
                System.out.println("Adding input node fails");
                return;
              }
              if(edgeType.trim().equals("flowlink"))
                this.createEdgeBetween(outputVertex, vertex, edgeType, alledges);
              else
                this.createEdgeBetween(vertex, outputVertex, edgeType, alledges);
            }
          }
       }
       attrType=(attrType+1)%3;
      }
      reader.close();
      fin.close();
    } catch (IOException ex) {
      //ex.printStackTrace();
      ;//Logger.getLogger(DataArchive.class.getName()).log(Level.SEVERE, null, ex);
    }
      setStatusForAllVertexes(vertexes);
      task.listOfVertexes=vertexes;
      task.alledges=alledges;

  }

    private void printVertexes(LinkedList<Vertex> vertexes){
      for (int i=0; i<vertexes.size();i++){
        Vertex vertex=vertexes.get(i);
        System.out.println("name: "+vertex.label);
        for (int j=0; j<vertex.inedges.size();j++){
          System.out.println("in start: "+vertex.inedges.get(j).start.label);
          System.out.println("in end: "+vertex.inedges.get(j).end.label);
        }
        for (int j=0; j<vertex.outedges.size();j++){
          System.out.println("out start: "+vertex.outedges.get(j).start.label);
          System.out.println("out end: "+vertex.outedges.get(j).end.label);
        }
      }
    }

      /**
   * Update displayed equation in the text area and the error messages
   *
   * @param notError
   * @return
   */
  private boolean updateEquation(Parser parser, Scanner scanner, Equation equation, boolean notError) {
    if (notError) {
      parser.setEquation(scanner.getEquation());
      if (!parser.parse()) {
      } else {
        equation = parser.getEquation();
        return true;
      }
    }
    return false;
  }

    public void setEquation(Equation equation, String eqStr) {
    //System.out.println("Correct string: " + eqStr);
    Scanner scanner = new Scanner(equation);
    //System.out.println("Initial correct equation: " + equation);
    Parser parser = new Parser();
    String currentWord = "";
    //Add correct Answer
    for (int i = 0; i < eqStr.length(); i++) {
      char currentChar = eqStr.charAt(i);
      if (Character.isDigit(currentChar)) {
        if (!currentWord.equals("")) {
          updateEquation(parser, scanner, equation, scanner.addInput(currentWord, Scanner.VARIABLE));
          currentWord = "";
        }
        updateEquation(parser, scanner, equation, scanner.addInput(currentChar + "", Scanner.DIGIT));
      } else if (currentChar == '+' || currentChar == '-' || currentChar == '*' || currentChar == '/') {
        if (!currentWord.equals("")) {
          updateEquation(parser, scanner, equation, scanner.addInput(currentWord, Scanner.VARIABLE));
          currentWord = "";
        }
        updateEquation(parser, scanner, equation, scanner.addInput(currentChar + "", Scanner.OPERATION));
      } else if (currentChar == ')' || currentChar == '(') {
        if (!currentWord.equals("")) {
          updateEquation(parser, scanner, equation, scanner.addInput(currentWord, Scanner.VARIABLE));
          currentWord = "";
        }
        updateEquation(parser, scanner, equation, scanner.addInput(currentChar + "", Scanner.PARENTHESIS));
      } else {
        currentWord += currentChar;
      }
    }
    if (!currentWord.equals("")) {
      updateEquation(parser, scanner, equation, scanner.addInput(currentWord, Scanner.VARIABLE));
      currentWord = "";
    }
    System.out.println("Final equation: " + equation);
  }

    public void setVertexInfoBasedOnTaskFile(Vertex vertex) {   
    String fileName = "";
    try {
      TaskFactory server = TaskFactory.getInstance();
      fileName = server.getActualTask().getLevel() + "-" + server.getActualTask().getTitle().replace(" ", "") + "Solution.txt";
    } catch (CommException ex) {
      //Add correct logger
    }

    String fileLine = "";
    String correct = "";
    String eqStr="";
    BufferedReader TempReader = null;
    FileInputStream fi;
    vertex.correctValues.clear();
    try {
      fi = new FileInputStream(fileName);
      TempReader = new BufferedReader(new InputStreamReader(fi));
      while ((fileLine = TempReader.readLine()) != null) {
        if (fileLine.startsWith("Equation: " + vertex.label)) {
          fileLine = fileLine.replace("Equation: " + vertex.label + " = ", "");
          correct = fileLine.trim();
        } else if (fileLine.startsWith("CorrectDescription: " + vertex.label)){
          fileLine = fileLine.replace("CorrectDescription: " + vertex.label + " = ", "");
          vertex.correctDescription = fileLine;
        } else if (fileLine.startsWith(vertex.label + " type: ")) {
          fileLine = fileLine.replace(vertex.label + " type: ", "");
          vertex.correctType = fileLine;
        } else if (fileLine.startsWith(vertex.label + " inputs: ")) {
          fileLine = fileLine.replace(vertex.label + " inputs: ", "");
          vertex.correctInputs = fileLine;
        } else if (fileLine.startsWith(vertex.label + " outputs: ")) {
          fileLine = fileLine.replace(vertex.label + " outputs: ", "");
          vertex.correctOutputs = fileLine;
        } else if (fileLine.startsWith("debug: Equation: " + vertex.label)) {
          fileLine = fileLine.replace("debug: Equation: " + vertex.label + " = ", "");
          eqStr = fileLine.trim();
        }  else {
          String splitLine[] = fileLine.split("=");
          if (splitLine[0].trim().equals(vertex.label)) {
            splitLine[1] = splitLine[1].replace(",", "");
            try
            {             
              vertex.correctValues.add(Double.parseDouble(splitLine[1].trim()));
            }
            catch(NumberFormatException nfe)
            {
              JOptionPane.showMessageDialog(null, "NumberFormatException", "Error", JOptionPane.ERROR_MESSAGE);
            }
          }
        }
      }
      fi.close();
      TempReader.close();
    } catch (Exception ex) {
      ex.printStackTrace();//Add log
    }
      if(vertex.correctEquation==null){
        vertex.correctEquation=new Equation();
        setEquation(vertex.correctEquation, correct);
        
      }

      if(!vertex.setEquationyet && Main.ReadModelFromFile)
      {
        setEquation(vertex.equation, eqStr);
        vertex.setEquationyet=true;
      }
  }
    
    public void getExtraNodesFromFile(Task task)
    {
      String fileName = "";

      fileName = task.getLevel() + "-" + task.getTitle().replace(" ", "") + "Solution.txt";
      String fileLine = "";
      LinkedList<String> extranodes= new LinkedList<String>();
      BufferedReader TempReader = null;
      FileInputStream fi;

      try {
        fi = new FileInputStream(fileName);
        TempReader = new BufferedReader(new InputStreamReader(fi));
        while ((fileLine = TempReader.readLine()) != null) {
          if (fileLine.startsWith("extra: ")) {
            fileLine = fileLine.replace("extra: ", "");
            extranodes.add(fileLine.trim());
          }
        }
        fi.close();
        TempReader.close();
      } catch (Exception ex) {

      }
      task.setExtraNodes(extranodes);
    }
}
