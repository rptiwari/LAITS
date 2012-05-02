package amt;

import javax.swing.JOptionPane;

/**
 *
 * @author Ram
 */
public class ApplicationVersion {

    public static int PROFESSORVERSION = 0;
    public static int STUDENTVERSION = 1;
    public static int VERSION2 = 2;
    public static int DEMO = 3;
    public static int DEMO_VERSION2 = 4;
    public static int LAITS = 10;
    public static int applicationVersion = STUDENTVERSION;
    
  
    public ApplicationVersion(String args[]) {
        if(args==null){
            setApplicationVersion(LAITS);
        }
        else if (args.length > 0) {
            if (args[0].equals("professorVersion")) {
                Main.professorVersion = PROFESSORVERSION;
            } else if (args[0].equals("studentVersion")) {
                Main.professorVersion = STUDENTVERSION;
            } else if (args[0].equals("version2")) {
                Main.professorVersion = VERSION2;
            } else if (args[0].equals("demo")) {
                Main.professorVersion = DEMO;
            } else if (args[0].equals("demo_version2")) {
                Main.professorVersion = DEMO_VERSION2;
            }
        } else {
            setApplicationVersion(-1);
        }

    }

    public void setApplicationVersion(int version) {
        if(version==LAITS){
            Main.professorVersion = LAITS;
            Main.MetaTutorIsOn = false;
        }else{
        Object[] options = {"Student Version", "Professor Version", "Version 2", "Student Version Demo", "Version 2 Demo"};
        Object selectedValue = JOptionPane.showInputDialog(null, "Please choose a version", "Choose Version", JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (selectedValue == "Student Version") {
            Main.professorVersion = STUDENTVERSION;

        } else if (selectedValue == "Professor Version") {
            Main.professorVersion = PROFESSORVERSION;
        } else if (selectedValue == "Version 2") {
            Main.professorVersion = VERSION2;
        } else if (selectedValue == "Student Version Demo") {
            Main.professorVersion = DEMO;
        } else if (selectedValue == "Version 2 Demo") {
            Main.professorVersion = DEMO_VERSION2;
        } else {
            System.exit(0);
        }

        if (applicationVersion == VERSION2) {
            Main.VERSIONID = "2";
        } else {
            Main.VERSIONID = "112";
        }
        }
        
    }
    public int getApplicationVersion(){
        return applicationVersion;
    }
    
    
}
