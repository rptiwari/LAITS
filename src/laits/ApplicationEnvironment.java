package laits;

import java.util.HashMap;

/**
 *
 * @author Ram
 */
public class ApplicationEnvironment {

    public static final String VERSION = "Version 1.12 release July 21th, 2010";
    public static String VERSIONID = "112";
    public static int PROFESSORVERSION = 0;
    public static int STUDENTVERSION = 1;
    public static int VERSION2 = 2;
    public static int DEMO = 3;
    public static int DEMO_VERSION2 = 4;
    public static int LAITS = 10;
    public static int professorVersion = STUDENTVERSION;
    public static boolean MetaTutorIsOn = true;
    public static boolean ReadModelFromFile = true;
    public static boolean alreadyRan = false;
    public static String authorWindowTitle = "LAITS Authoring Tool";
    public static String amtWindowTitle = "Affective Meta Tutor";
    public static int applicationMode; // 1= Student Mode, 2 = Author Mode
    
    public static HashMap<Integer,Integer> planTree;
    
    public static String windowTitle = "";
    
    
    public static String getWindowTitle(){
        return windowTitle;
    }
    public static void setWindowTitle(String title){
        windowTitle=title;
    }
    
    public static void putIntoMap(int a, int b){
        if(planTree==null)
            planTree = new HashMap<Integer, Integer>();
        planTree.put(a, b);
    }
    
    public static int getFromMap(int a){
        if(planTree==null)
            planTree = new HashMap<Integer, Integer>();
        if(planTree.size()==0)
            return -1;
        return planTree.get(Integer.valueOf(a));
    }
}
