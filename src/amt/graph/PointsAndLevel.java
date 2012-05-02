package amt.graph;

import amt.comm.Database;
import amt.data.TaskFactory;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import javax.swing.JPanel;

/**
 * This method draws the point and level values
 *
 * @author Megana
 */
public class PointsAndLevel {

    private int userPoints, currentLevel;
    private TaskFactory server;
    private GraphCanvas gc;

    public PointsAndLevel(int userPoints, int currentLevel, TaskFactory server, GraphCanvas gc)
    {
        this.userPoints = userPoints;
        this.currentLevel = currentLevel;
        this.server = server;
        this.gc = gc;
    }

    public void paintPointsAndLevel(Graphics g)
    {
        int boxHeight = 50;
        int boxWidth = 100;
        int componentWidth = gc.getParent().getWidth();
        int indent = 5;
        Font f = new Font("Arial", Font.BOLD, 22);
        Font num = new Font("Arial", Font.BOLD, 30);
        FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(f);
        FontMetrics numMetrics = Toolkit.getDefaultToolkit().getFontMetrics(num);

        //POINTS
        g.setColor(Color.BLACK);
        g.setFont(f);
        g.drawRect(componentWidth - boxWidth - indent*6, indent*4, boxWidth, boxHeight);
        g.setColor(Color.WHITE);
        g.drawLine(componentWidth - boxWidth - indent*4, indent*4, componentWidth - boxWidth*3/4 + fm.stringWidth("LEVEL")/2, indent*4);
        g.setColor(Color.BLACK);
        g.drawString("LEVEL", componentWidth - indent - boxHeight*3/2 - fm.stringWidth("LEVEL")/2, indent*6);
        g.setFont(num);
        g.drawString(server.getActualTask().getLevel(), componentWidth - boxHeight*3/2 - numMetrics.stringWidth(server.getActualTask().getLevel())/2, indent*2 + boxHeight);

        //LEVEL
        g.setFont(f);
        g.drawRect(componentWidth - boxWidth - indent*6, indent*10 + boxHeight, boxWidth, boxHeight);
        g.setColor(Color.WHITE);
        g.drawLine(componentWidth - boxWidth - indent*5, indent*10 + boxHeight, componentWidth - boxWidth*3/4 + fm.stringWidth("POINTS")/2, boxHeight + indent*10);
        g.setColor(Color.BLACK);
        g.drawString("POINTS", componentWidth - indent - boxHeight*3/2 - fm.stringWidth("POINTS")/2, indent*10 + boxHeight + indent*2);
        g.setFont(num);
        g.drawString(userPoints + "", componentWidth - indent - boxHeight*3/2 - numMetrics.stringWidth(userPoints + "")/2, boxHeight*2 + indent*10 - 10);
    }

    public void setUserPoints(int userPoints)
    {
        this.userPoints = userPoints;
    }

    public int getUserPoints()
    {
        return userPoints;
    }

    public void setCurrentLevel(int currentLevel)
    {
        this.currentLevel = currentLevel;
    }
}
