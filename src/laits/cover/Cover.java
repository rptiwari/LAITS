package laits.cover;

import laits.Main;
import laits.comm.CommException;
import laits.data.TaskFactory;
import laits.graph.Graph;
import laits.graph.GraphCanvas;
import laits.graph.MenuBar;
import laits.graph.PointsAndLevel;
import laits.graph.Vertex;
import java.awt.*;
import java.awt.Graphics;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import java.util.Calendar;
import java.util.LinkedList;

/**
 * Invisible layout to show overlapped elements over the graph. E.g. Duke
 *
 * @author Javier Gonzalez Sanchez
 * @author Megan Kearl
 * @version 20100430
 */
public class Cover implements Runnable {

  private Thread thread;
  private JPanel jpanel;
  private Font n = new Font("Normal", Font.PLAIN, 20); //font in the speech bubbles
  private GraphCanvas gc;
  private MenuBar menuBar;
  private Avatar avatar, avatar2, avatar3;
  private PointsAndLevel pointsAndLevel;
  private Face face;
  private Clock clock;
  private Image image = null;
  private Dimension imageSize = new Dimension(0, 0);
  private int professorVersion = 1;
  private boolean hideComponents = true;
  private Calendar cal;
  private long startTime, endTime = 0;


  /**
   * Constructor
   * @param jpanel
   */
  public Cover (GraphCanvas jpanel, Graph graph, int professorVersion, Frame frame){
    this.jpanel = jpanel;
    this.gc = jpanel;
    this.professorVersion = professorVersion;

    TaskFactory server = null;
    try {
      server = TaskFactory.getInstance();
    } catch (CommException ex) {
    }
    avatar = new Avatar(20, 500, jpanel, n, false, false);
    gc.getAvatarList().add(avatar);
    clock = new Clock(gc, "graphCanvas");
    cal = Calendar.getInstance();
    startTime = cal.getTimeInMillis();
//    promptDialog = new PromptDialog(gc.getFrame(),true);
    
    menuBar = new MenuBar(gc, graph, n, professorVersion, frame);
    //avatar2= new Avatar(300, 300, jpanel, n);
    //avatar3 = new Avatar(20, 200, jpanel, n);
    //if(gc.VERSIONID.equals("112"))
    if(professorVersion != Main.VERSION2 && professorVersion != Main.DEMO_VERSION2)
    {
        pointsAndLevel = new PointsAndLevel(0,0,server, jpanel);
        face = new Face(jpanel);
    }
    thread = new Thread(this);
    thread.start();
  }

  /**
   * This method returns the Menu Bar
   */
  public MenuBar getMenuBar()
  {
      return menuBar;
  }

  /**
   * This method returns the points and level
   * @return
   */
  public PointsAndLevel getPointsAndLevel()
  {
      return pointsAndLevel;
  }

  /**
   * This method sets the font
   * @param f
   */
  public void setFont(Font f) {
    n = f;
  }


  /**
   * This method paints the avatar and the boxes around the hints and help
   * @param g
   */
  public void paint(Graphics g){
     
    //Paint the clock to all screens
    clock.setHeight(gc.getHeight());
    clock.paint(g);
    //Paint the avatars
    for(int i = 0; i < gc.getAvatarList().size(); i++)
    {
        if(gc.getAvatarList().get(i).getWaving() == true)
        {
            gc.getAvatarList().get(i).paintWavingAnimation(g);
        }
        else
        {
            gc.getAvatarList().get(i).paintStandingAvatar(g);
        }
        //Update the timer if the avatar has one
        if(gc.getAvatarList().get(i).getHasTimer() == true && gc.getAvatarList().get(i).getTimer() > 0)
        {
            int currentTime = Calendar.getInstance().get(Calendar.SECOND);
            if(currentTime < gc.getAvatarList().get(i).getStartTime())
            {
                currentTime += 60;
            }

            if(currentTime - gc.getAvatarList().get(i).getStartTime() > gc.getAvatarList().get(i).getTimer())
            {
                //The timer is up!
                gc.getAvatarList().get(i).setVisible(false);
                gc.getAvatarList().get(i).setHasTimer(false);
            }
        }
    }

    //Determine whether the model can be run
    if(professorVersion == Main.VERSION2 || professorVersion == Main.DEMO_VERSION2)
    {
      try {
        if(gc.canRun())
        {
          //menuBar.getPredictButton().setEnabled(true);
        }
        else
        {
          //menuBar.getPredictButton().setEnabled(false);
          gc.setModelHasBeenRun(false);
        }
        //System.out.println(gc.getModelHasBeenRun());

        if (gc.getGraph().getVertexes().size() != 0) {
          menuBar.getPredictButton().setEnabled(true);
        } else {
          menuBar.getPredictButton().setEnabled(false);
        }
        LinkedList<String> extraNodes = TaskFactory.getInstance().getActualTask().getExtraNodes();

        
        int extranodecount = 0;
        for(String extra: extraNodes)
        {
          for(int i=0; i<gc.getGraph().getVertexes().size();i++)
          {
            if(((Vertex)gc.getGraph().getVertexes().get(i)).nodeName.equals(extra))
            {
              extranodecount++;
            }
          }
        }
        //if (extranodecount==0 && gc.getGraph().getVertexes().size() == gc.listOfVertexes.size()-extraNodes.size() || gc.getGraph().getVertexes().size()-extranodecount == gc.listOfVertexes.size()-extraNodes.size() && extranodecount>0) {
//        if (gc.getGraph().getVertexes().size() == gc.listOfVertexes.size()){  
        if(gc.getGraph().getVertexes().size()-extranodecount == gc.listOfVertexes.size()-extraNodes.size()){
          boolean allCorrect = true;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        
          for (int i = 0; i < gc.getGraph().getVertexes().size(); i++) {
            Vertex v = (Vertex)gc.getGraph().getVertexes().get(i);
            if (v.getGraphsButtonStatus() != v.CORRECT && v.getGraphsButtonStatus() != v.GAVEUP) {
              allCorrect = false;
            }
          }

          menuBar.getDoneButton().setEnabled(allCorrect ? true : false);
        } else {
          menuBar.getDoneButton().setEnabled(false);
        }
      } catch (CommException ex) {
        Logger.getLogger(Cover.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    
    //if(gc.VERSIONID.equals("112"))
    if(professorVersion != Main.VERSION2 && hideComponents == false && professorVersion != Main.DEMO_VERSION2)
    {
      Font header = new Font("Arial", Font.BOLD, 22);
      //HINT BUTTONS
      menuBar.drawHintBox(g, header);

      //HELP BUTTONS
      menuBar.drawHelpBox(g, header);

      //POINTS AND LEVEL
      pointsAndLevel.paintPointsAndLevel(g);

      //PAINT THE FACE
      face.paintFace(g, menuBar.getButton(), 80);
    }
    
  }

  /**
   * This method returns the clock so a height can be set
   * @return
   */
  public Clock getClock()
  {
    return clock;
  }

  /**
   * This method runs the thread for the animation
   */
  public void run() {
    while (!Main.windowIsClosing()){
        // Disabling User Interest popup - Ram
     // popupPromptDialog();
      jpanel.repaint();
      try {
          Thread.sleep(150);
      } catch (InterruptedException ex) {
      }  
    }
  }

  public void hideSomeComponents(boolean hideComponents) {
    this.hideComponents = hideComponents;
  }

}