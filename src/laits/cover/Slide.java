/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package laits.cover;

import laits.Main;
import java.awt.*;
import java.io.File;
import java.net.URL;
import javax.swing.*;

/**
 *
 * @author Storm
 */
public class Slide {
  
  private Point position;
  private static int FRAMES = 64; //Number of slides in the deck
  private Image[] animation = new Image[FRAMES];
  private JPanel jpanel;

  public static int getFrames() {
    return FRAMES;
  }


/**
 * Default constructor
 * @param int x ,int y, Jpanel jpanel
 */

  public Slide(int x , int y, JPanel jpanel){

    this.jpanel = jpanel;
    String temp = "";
    int digitos = 2;
    position = new Point(x, y);
    Toolkit tools = java.awt.Toolkit.getDefaultToolkit();
      if (FRAMES >=100)
        digitos = 3;
      
    for (int i = 0; i < FRAMES; i++) {
      if ((digitos==2) && i<9)
        temp = "/amt/images/Slide0" + (i + 1) + ".png";
      else if ((digitos==2) && i<100)
        temp = "/amt/images/Slide" + (i + 1) + ".png";
      else if ((digitos==3) && i<9)
        temp = "/amt/images/Slide00" + (i + 1) + ".png";
      else if ((digitos==3) && i<99)
        temp = "/amt/images/Slide0" + (i + 1) + ".png";
      else if (digitos==3 && i>=99)
        temp = "/amt/images/Slide" + (i + 1) + ".png";
    
      
      URL imageURL = Main.class.getResource(temp);
      animation[i] = tools.createImage(imageURL);
    }
  }

/**
 * This method is to paint the slide 
 * @param Graphics g, int index
 */
  
  public void paintSlide(Graphics g, int index){
    g.drawImage(animation[index], position.x, position.y, jpanel);
  }
}
