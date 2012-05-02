package laits.data;

import laits.graph.Edge;
import laits.graph.Vertex;
import java.util.*;

/**
 * This object represent a Task (Problem).
 *
 * @author Javier Gonzalez Sanchez
 * @version 20101114
 */
public final class Task  implements Product {

  private int id;
  private String level;
  private String title;
  private String imageUrl;
  private String summary;
  private String description;
  private String type;
  private LinkedList<String> vertexNames;
  private int startTime;
  private int endTime;
  private String unitTime;
  //FOR VERSION 2, TASK HAVE A DECISION TREE
  private LinkedList<String> tree;
  private LinkedList<String> extranodes;
  
  public LinkedList<Vertex> listOfVertexes=null;
  public LinkedList<Edge> alledges=null;

  /**
   * Constructor
   */
  public Task() {
    setId(-1); //Initialize to -1 to indicate no task selected
    setLevel("1");
    setTitle("");
    setImageUrl("");
    setSummary("This should be the summary");
    setDescription("");
    vertexNames = new LinkedList<String>();
    setStartTime(0);
    setEndTime(100);
    setUnitTime("days");
    setType("");
    //Task in version 2 have a decision tree
    tree = new LinkedList<String>();
    
    extranodes = new LinkedList<String>();
  }

  /**
   *
   * @param id
   * @param level
   * @param title
   * @param type
   */
  public Task(int id, String level, String title) {
    setId(id);
    setLevel(level);
    setTitle(title);
    setType("");
  }

  /**
   * Getter method to get the ID of the task.
   *
   * @return an integer value
   */
  public int getId() {
    return this.id;
  }

  /**
   * Setter method to define the ID of a Task.
   * It is important to remember that this value should correspond with the one
   * in the database
   *
   * @param id an integer value
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * Getter method to get the level (difficulty) of the task
   *
   * @return an String with the level (difficulty) of the task
   */
  public String getLevel() {
    return this.level;
  }

  /**
   * Setter method to define the level (difficulty) of the task.
   *
   * @param an String with the level of the task
   */
  public void setLevel(String level) {
    this.level = level;
  }

  /**
   * Getter method to get the title of a task.
   *
   * @return a String
   */
  public String getTitle() {
    return this.title;
  }

  /**
   * Setter method to define the title of a task.
   * It is important to remember that this value should be coherent with the one
   * in the db.
   *
   * @param title
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Getter method to get the imageUrl of the task
   *
   * @return a String object with the imageUrl content
   */
  public String getImageUrl() {
    return this.imageUrl;
  }

  /**
   * Setter method to define the imageUrl of a task
   *
   * @param imageUrl a String with the imageUrl
   */
  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  /**
   * Getter method to get the short description of the task
   *
   * @return a String with the short description of the model
   */
  public String getSummary() {
    return summary;
  }

  /**
   * Setter method to define the short description of the task
   *
   * @param an String with the short description of the task
   */
  public void setSummary(String summary) {
    this.summary = summary;
  }

  /**
   * Getter method to get the description of the task (description content)
   *
   * @return a String object with the description content
   */
  public String getDescription() {
    return this.description;
  }

  /**
   * Setter method to define the description of a task (description content)
   *
   * @param description a String with the description content
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Getter method to get the task type (modeling, construction, debugging, and whole)
   * 
   * @return a String object with the task type
   */
  public String getType() {
    return this.type;
  }

  /**
   * Setter method to define the type of a task
   *
   * @param tpye: a String with the task type
   */
  public void setType(String type) {
    this.type = type;
  }


  /**
   * Getter method to get the vertexNames (nodes) in the task (model)
   *
   * @return a linked list with all the vertexNames
   */
  public LinkedList<String> getVertexNames() {
    return this.vertexNames;
  }

  /**
   * Setter method to define the list of vertexNames in the task (model)
   * It is important to remember thas this value should be coherent with the one 
   * in the db.
   *
   * @param vertexNames
   */
  public void setVertexNames(LinkedList<String> vertexes) {
    this.vertexNames = vertexes;
  }

  /**
   * Getter method to get the start time to run the model
   *
   * @return an integer with the value of the start time for the model
   */
  public int getStartTime() {
    return this.startTime;
  }

  /**
   * Setter method to define the start time to run the model
   *
   * @param an integer with the start time of the model
   */
  public void setStartTime(int startTime) {
    this.startTime = startTime;
  }

  /**
   * Getter method to get the endTime for running the model
   *
   * @return an integer with the value
   */
  public int getEndTime() {
    return endTime;
  }

  /**
   * Setter method to define end time to run the model.
   *
   * @param an integer endTime
   */
  public void setEndTime(int endTime) {
    this.endTime = endTime;
  }


  /**
   * Getter method to get the unitTime for the time of the model
   *
   * @return an String with the unitTime for the model
   */
  public String getUnitTime() {
    return this.unitTime;
  }

  /**
   * Setter method to define the unitTime for the time in our model
   * @param an String with the unitTime of the model
   */
  public void setUnitTime(String unitTime) {
    this.unitTime = unitTime;
  }

  public LinkedList<String> getTree() {
    return tree;
  }

  public void setTree(LinkedList<String> tree) {
    this.tree = tree;
  }
  public LinkedList<String> getExtraNodes() {
    return this.extranodes;
  }

  public void setExtraNodes(LinkedList<String> extranodes) {
    this.extranodes = extranodes;
  }
  

  /**
   *
   * @return
   */
  @Override
  public String toString() {
    String s = "";
    s += "title:" + getTitle() + "\n";
    s += "type:" + getType() + "\n";
    s += "html:" + getDescription() + "\n";
    s += "image:" + getImageUrl() + "\n";
    s += "vertex:" + getVertexNames() + "\n";
    s += "level:" + getLevel() + "\n";
    s += "summary:" + getSummary() + "\n";
    s += "startTime:" + getStartTime() + "\n";
    s += "endTime:" + getEndTime() + "\n";
    s += "unit:" + getUnitTime() + "\n";
    return s;
  }
  
}
