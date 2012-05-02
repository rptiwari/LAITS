package laits.data;

import laits.Main;
import laits.comm.CommException;
import laits.graph.Vertex;
import java.util.*;

/**
 * Factory of Tasks
 *
 * @author Javier Gonzalez Sanchez
 * @version 20101114
 */
public final class TaskFactory extends Factory{

  private Task actualTask = new Task();
  private LinkedList<Task> tasks = new LinkedList<Task>();

  private static TaskFactory entity;
  private LinkedList<int[]> tasksPerLevel;

  /**
   * 
   * @return
   */
  public LinkedList<int[]> getTasksPerLevel() {
    return tasksPerLevel;
  }


  /**
   * Getter method to get a Database object. The Database object represents the
   * connection with the online database in order to get the information of the
   * available task in the server.
   *
   * @return a Database object
   */
  public static TaskFactory getInstance() throws CommException{
    if (entity == null) {
      entity = new TaskFactory();
    }
    return entity;
  }

  /**
   * Constructor
   * 
   * @param database
   */
  private TaskFactory() throws CommException {
    super();
  }

  /**
   * Get all the available tasks in the server or local stored in a file
   *
   * @return
   */
  public LinkedList<Task> getTasks() throws DataException {
    tasks.clear();
    try {
      tasks = database.selectTasksFromDB();
    } catch(CommException de1) {
      try {
        tasks = archive.selectTasksFromFile();
      } catch (CommException de2) {
        throw new DataException ("TaskFactory.getTasks.1");
      }
    }
    archive.saveTasksToFile(tasks);
    randomizeTasks();
    return tasks;
  }

  /**
   * This method get one Task from database or file
   *
   * @param id
   * @return
   */
  public Task getTasks(int id) throws DataException {
    Task task = searchTask(id);
    try {
      database.selectTasksFromDB(task);
      if (Main.professorVersion==Main.VERSION2 || Main.professorVersion==Main.DEMO_VERSION2){
        archive.getTreeFromFile(task);
        archive.getTaskTypeFromFile(task);
        archive.getExtraNodesFromFile(task);

        if(Main.getMemo()!=null)
          Main.getMemo().setText("");
      }
    } catch (CommException de1) {
      try {
        archive.selectTasksFromFile(task);
      } catch (CommException de2) {
        throw new DataException ("TaskFactory.getTasksInt.1");
      }
    } 
    archive.saveTasksToFile(task);
    this.setActualTask(task);

    if (Main.professorVersion==Main.VERSION2 || Main.professorVersion==Main.DEMO_VERSION2)
      archive.getVertexesFromFile(task);

    return actualTask;
  }

  /**
   * This method perform a searching in the list of available task in order to get
   * the task with the specified id
   *
   * @param idtask in the database
   * @return Task object with the selected task to display
   */
  public Task searchTask(int idtask) {
    for (int i = 0; i < tasks.size(); i++) {
      if (tasks.get(i).getId() == idtask) {
        return tasks.get(i);
      }
    }
    return null;
  }

  /**
   * Get the selected task
   *
   * @return a Task object
   */
  public Task getActualTask() {
    return actualTask;
  }

  /**
   * Setter method for the current Task
   *
   * @param actualTask is a Task object
   */
  public void setActualTask(Task actualTask) {
    this.actualTask = actualTask;
  }

  /**
   * This method is used to generate a linked list of string arrays containing
   * the task id for each level in random order
   */
  private void randomizeTasks() {
    LinkedList<Task> taskList = tasks;

    LinkedList<LinkedList> probsForLevels = new LinkedList<LinkedList>();
    tasksPerLevel = new LinkedList<int[]>();
    int highestLevel = -2;
    // find the highest level
    for (int i = 0; i < taskList.size(); i++) {
      if (Integer.parseInt(taskList.get(i).getLevel()) > highestLevel) {
        highestLevel = Integer.parseInt(taskList.get(i).getLevel());
        highestLevel += 1; //this is the only way to prevent errors when we have level -1
      }
    }
    // initialize the linked list of problems
    for (int i = 0; i <= highestLevel; i++) {
      LinkedList<Integer> intList = new LinkedList<Integer>();
      probsForLevels.add(intList);
    }
    //for each task in the task list, add it to the appropriate level's linkedlist
    for (int i = 0; i < taskList.size(); i++) {
      int level = Integer.parseInt((taskList.get(i).getLevel()));
      level += 1;
      (probsForLevels.get(level)).add(taskList.get(i).getId());
    }
    //shuffle each index and add the resulting string array to problem list
    for (int i = 0; i < probsForLevels.size(); i++) {
      int[] temp = null;
      if (!probsForLevels.get(i).isEmpty()) {
        temp = shuffleProblems(probsForLevels.get(i));
        for (int j = 0; j < temp.length; j++) {
          int n = temp[j];
          temp[j] = Integer.parseInt((probsForLevels.get(i)).get(n).toString());
        }
        tasksPerLevel.add(temp);
      } else {
        tasksPerLevel.add(temp);
      }
    }
  }

  /**
   * Return an array with the indexes of the problems shuffled
   *
   * @return
   */
  private int[] shuffleProblems(LinkedList<Integer> probList) {
    int tempIndex = 0;
    int tempHolder = 0;
    Random generator = new Random();
    // loaded is a list of which vertexes have already been loaded
    int indices[] = new int[probList.size()];
    for (int i = 0; i < indices.length; i++) {
      indices[i] = i;
    }
    // shuffle the numbers around using Fisher-Yates shuffle
    if (indices.length > 1) {
      for (int i = indices.length - 1; i >= 0; i--) {
        tempIndex = generator.nextInt(indices.length - 1);
        tempHolder = indices[tempIndex];
        indices[tempIndex] = indices[i];
        indices[i] = tempHolder;
      }
    }
    return indices;
  }
 
}
