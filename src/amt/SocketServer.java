package amt;

import amt.cover.Avatar;
import amt.graph.GraphCanvas;
import java.awt.Graphics;
import javax.swing.*;

import java.io.*;
import java.net.*;
import java.util.StringTokenizer;

/**
 * ESTE SERA EL AMT
 * @author JavierGS
 * @author Megan Kearl
 */
class SocketServer extends JFrame implements Runnable
{

    ServerSocket server = null;
    Socket client = null;
    BufferedReader in = null;
    PrintWriter out = null;
    String line;
    GraphCanvas gc;
    Thread thread;

    /**
     *
     */
    SocketServer(GraphCanvas gc)
    {
        this.gc = gc;
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void paint(Graphics g)
    {
        gc.getCover().paint(g);
    }

    /**
     *
     */
    public void listenSocket()
    {
        try
        {
            server = new ServerSocket(4444);
        } catch (IOException e)
        {
            System.out.println("Could not listen on port 4444");
            System.exit(-1);
        }

        try
        {
            client = server.accept();
        } catch (IOException e)
        {
            System.out.println("Accept failed: 4444");
            System.exit(-1);
        }

        try
        {
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);
        } catch (IOException e)
        {
            System.out.println("Accept failed: 4444");
            System.exit(-1);
        }

        while (true)
        {
            try
            {
                line = in.readLine();
                out.println(line);

                if(line==null)
                  break;
                if(line.contains("::")){
                  System.out.println(line);//debug
                  StringTokenizer stForMT=new StringTokenizer(line, "::");
                  String command=stForMT.nextToken();
                  if(command.equals("questionFromMT")){
                    String quesTxt=stForMT.nextToken();
                    String optionsStr=stForMT.nextToken();
                    String []options=optionsStr.split(",");
                    new MetaTutorQues(quesTxt,options).setVisible(true);
                  }
                  else if(command.equals("messageFromMT")){
                    String quesTxt=stForMT.nextToken();
                    new MetaTutorMsg(quesTxt,true).setVisible(true);
                  }
                  else if(command.equals("warning")){
                    String quesTxt=stForMT.nextToken();
                    if(GraphCanvas.openTabs.isEmpty())
                      JOptionPane.showMessageDialog(null, quesTxt);
                    else
                      JOptionPane.showMessageDialog(GraphCanvas.openTabs.get(0), quesTxt);
                    //new MetaTutorMsg(quesTxt,false).setVisible(true);
                  }
                }
                else{
                StringTokenizer st = new StringTokenizer(line, ",");
                String action = st.nextToken();
                if (action.equals("waving"))
                {
                    int avatarNum = 0;
                    try
                    {
                        avatarNum = Integer.parseInt(st.nextToken());
                    } catch (Exception ex)
                    {
                        System.out.println(ex.toString());
                    }
                    if (avatarNum < gc.getAvatarList().size())
                    {
                        if (st.nextToken().equals("true"))
                        {
                            gc.getAvatarList().get(avatarNum).setWaving(true);
                        } else
                        {
                            gc.getAvatarList().get(avatarNum).setWaving(false);
                        }
                    }

                } else if (action.equals("position"))
                {
                    int avatarNum = 0;
                    try
                    {
                        avatarNum = Integer.parseInt(st.nextToken());
                    } catch (Exception ex)
                    {
                        System.out.println(ex.toString());
                    }
                    if (avatarNum < gc.getAvatarList().size())
                    {
                        gc.getAvatarList().get(avatarNum).move(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
                    }
                } else if (action.equals("visible"))
                {
                    int avatarNum = 0;
                    try
                    {
                        avatarNum = Integer.parseInt(st.nextToken());
                    } catch (Exception ex)
                    {
                        System.out.println(ex.toString());
                    }
                    if (avatarNum < gc.getAvatarList().size())
                    {
                        if (st.nextToken().equals("true"))
                        {
                            gc.getAvatarList().get(avatarNum).setVisible(true);
                        } else
                        {
                            gc.getAvatarList().get(avatarNum).setVisible(false);
                        }
                    }
                } else if (action.equals("new"))
                {
                    Avatar a = new Avatar(gc);
                    gc.getAvatarList().add(a);
                } else if (action.equals("message"))
                {
                    int avatarNum = 0;
                    try
                    {
                        avatarNum = Integer.parseInt(st.nextToken());
                    } catch (Exception ex)
                    {
                        System.out.println(ex.toString());
                    }
                    if (avatarNum < gc.getAvatarList().size())
                    {
                        if (st.hasMoreTokens())
                        {
                            gc.getAvatarList().get(avatarNum).setMessage(st.nextToken());
                        } else
                        {
                            gc.getAvatarList().get(avatarNum).setMessage("");
                        }
                    }
                } else if (action.equals("timer"))
                {
                    int avatarNum = 0;
                    try
                    {
                        avatarNum = Integer.parseInt(st.nextToken());
                    } catch (Exception ex)
                    {
                        System.out.println(ex.toString());
                    }
                    if (avatarNum < gc.getAvatarList().size())
                    {
                        int time = 0;
                        try
                        {
                            time = Integer.parseInt(st.nextToken());
                        } catch (Exception ex)
                        {
                            System.out.println(ex.toString());
                        }
                        gc.getAvatarList().get(avatarNum).setTimer(time);
                    }
                }
                repaint();
              }
            } catch (IOException e)
            {
              e.printStackTrace();
              System.out.println("Read failed in SocketServer.java");
                //System.exit(-1);
            }
        }
    }

    /**
     *
     */
    protected void finalize()
    {
        try
        {
            in.close();
            out.close();
            server.close();
        } catch (IOException e)
        {
            System.out.println("Could not close.");
            System.exit(-1);
        }
    }

    public void run()
    {
        this.listenSocket();
        while (true)
        {
            try
            {
                Thread.sleep(150);
            } catch (InterruptedException ex)
            {
            }
        }
    }
}
