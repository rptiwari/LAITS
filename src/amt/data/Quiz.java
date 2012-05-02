package amt.data;

import amt.graph.Graph;
import amt.graph.Vertex;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.StringTokenizer;

/**
 *
 * @author Javier Gonzalez Sanchez
 * @author Megan Kearl
 * @version 20100409
 */
public class Quiz  implements Product {

  private LinkedList<String> question;
  private LinkedList<Answer> answer;
  private LinkedList<String> userAnswer;
  private LinkedList<String> correctAnswer;
  private int taskId = 0;
  private String questionString = "";
  private String node = "";
  private String typeOfQuestion = "";
  private String values = "";
  private String time = "0";
  private String correctValue = "0";
  String yourAnswer = "Student's Answer: ";
  private final NumberFormat nf = new DecimalFormat("0.###");

  /**
   *
   */
  public Quiz() {
    question = new LinkedList<String>();
    answer = new LinkedList<Answer>();
    userAnswer = new LinkedList<String>();
    correctAnswer = new LinkedList<String>();
  }

  /**
   *
   * @param id
   */
  public Quiz(int id) {
    this();
    createQuiz(id);
  }

  /**
   *
   * @param q
   */
  public void addQuestion(String q) {
    question.add("<html>" + q + "</html>");
  }

  /**
   *
   * @param a
   */
  public void addAnswer(Answer a) {
    answer.add(a);
    if (a.getVariable() == Answer.TYPE) {
      correctAnswer.add(a.getValue().toUpperCase());
    } else if (a.getVariable() == Answer.EQUATION) {
      correctAnswer.add(a.getValue().toUpperCase());
    } else if (a.getVariable() == Answer.VALUE) {
      parseValue(a.getValue());
      correctAnswer.add(correctValue.toUpperCase());
    } else if (a.getVariable() == Answer.COUNTTYPE) {
      correctAnswer.add(a.getValue().toUpperCase());
    } else if (a.getVariable() == Answer.LABELTYPE) {
      correctAnswer.add(a.getValue().toUpperCase());
    } else if (a.getVariable() == Answer.TIMEVALUE) {
      parseValue(a.getValue());
      correctAnswer.add(time.toUpperCase());
    }
  }

  /**
   *
   * @return
   */
  public LinkedList<String> getUserAnswer() {
    return userAnswer;
  }

  /**
   *
   * @param userAnswer
   */
  public void setUserAnswer(LinkedList<String> userAnswer) {
    this.userAnswer = userAnswer;
  }

  /**
   *
   * @return
   */
  public LinkedList<Answer> getAnswer() {
    return answer;
  }

  /**
   *
   * @return
   */
  public LinkedList<String> getCorrectAnswer() {
    return correctAnswer;
  }

  /**
   *
   * @return
   */
  public LinkedList<String> getQuestion() {
    return question;
  }

  /**
   * This method returns the task id
   * @return the task id
   */
  public int getIdTask() {
    return taskId;
  }

  /**
   * This method sets the task id
   * @param id is the new id for the task
   */
  public void setIdTask(int id) {
    this.taskId = id;
  }

  /**
   * This method returns the question string
   * @return the question string
   */
  public String getQuestionString() {
    return questionString;
  }

  /**
   * This method sets the question string
   * @param question is the question string
   */
  public void setQuestionString(String question) {
    this.questionString = question;
  }

  /**
   * This method returns the node for the question
   * @return the node
   */
  public String getNode() {
    return node;
  }

  /**
   * This method sets the node for the question
   * @param node is the node for the question
   */
  public void setNode(String node) {
    this.node = node;
  }

  /**
   * This method returns the type of question
   * @return the type of question
   */
  public String getTypeOfQuestion() {
    return typeOfQuestion;
  }

  /**
   * This method sets the type of question
   * @param type is the type of question
   */
  public void setTypeOfQuestion(String type) {
    this.typeOfQuestion = type;
  }

  /**
   * This method returns the values for the answer
   * @return the values for the answer
   */
  public String getValues() {
    return values;
  }

  /**
   * This method sets the value of the answer
   * @param values is the answer value
   */
  public void setValues(String values) {
    this.values = values;
  }
  
  /**
   * HERE IMPLEMENT THE SOLUTION ALGORITHM
   * 
   * @param graph
   */
  public void solve(Graph graph) {
        userAnswer.clear();       
        //add fields in answer
        for (int j = 0; j < answer.size(); j++) {
            userAnswer.add("Unable to answer question without more model detail");
        }
        for (int i = 0; i < answer.size(); i++) {
            // 1. CASE ONE VERTEX TYPE (IT IS WORKING!)
            if (answer.get(i).getVariable() == Answer.TYPE) {
                String name = answer.get(i).getNode();
                Vertex v = graph.searchVertexByName(name);
                if (v != null) {
                    userAnswer.add(i, yourAnswer + v.type);
                    if (v.type.toUpperCase().equals(answer.get(i).getValue().toUpperCase())) {
                        answer.get(i).setEvaluateCorrect(true);
                    } else {
                        answer.get(i).setEvaluateCorrect(false);
                    }
                } else {
                    answer.get(i).setEvaluateCorrect(false);
                }
            } // 2. COMPARE THE EQUATION INSTEAD OF THE TYPE (v.equation)
            else if (answer.get(i).getVariable() == Answer.EQUATION) {
                String name = answer.get(i).getNode();
                Vertex v = graph.searchVertexByName(name);
                if (v != null) {
//              System.out.println("parse equation called");
                    int index = 0;
                    boolean checker = false ;
                    String eq = (v.equation).toString();
                    for(index = 0 ; index < eq.length() ; index ++){
                        if(eq.charAt(index) == ')'){
                            equationParser(answer.get(i).getValue(), v, i);
                            checker = true;
                        }
                    }
                    if(checker == false){
                        parseEquation(answer.get(i).getValue(), v, i);
                    }
                } else {
                    answer.get(i).setEvaluateCorrect(false);
                }
            } // 3.COMPARE A VALUE IN THE EQUATION (v.equation.value.get(i)
            // El campo answer.get(i).getValue() contain a String with the format:
            // position::value
            else if (answer.get(i).getVariable() == Answer.VALUE) {
                String name = answer.get(i).getNode();
                Vertex v = graph.searchVertexByName(name);
                parseValue(answer.get(i).getValue());
                double val = Double.parseDouble(correctValue);

                if (v != null && v.equation.value.isEmpty() == false) {
                    userAnswer.add(i, yourAnswer + nf.format((v.equation.value.get(Integer.parseInt(time)))));
                    Double userValue = Double.parseDouble(nf.format(v.equation.value.get(Integer.parseInt(time))));
                    //System.out.println("v value: " + (v.equation.value.get(Integer.parseInt(searchedTime))));
                    if ((userValue).equals(val)) {
                        answer.get(i).setEvaluateCorrect(true);
                    } else {
                        answer.get(i).setEvaluateCorrect(false);
                    }
                } else {
                    answer.get(i).setEvaluateCorrect(false);
                }
            } // COUNT THE NODES WITH A CERTAIN TYPE
            else if (answer.get(i).getVariable() == Answer.COUNTTYPE) {
                int counter = graph.countNodesOfType(answer.get(i).getNode());
                if (Integer.parseInt(answer.get(i).getValue()) == counter) {
                    answer.get(i).setEvaluateCorrect(true);
                } else {
                    answer.get(i).setEvaluateCorrect(false);
                }
                userAnswer.add(i, yourAnswer + counter + "");
            } // RETURN THE LABELS OF THE NODES OF A CERTAIN TYPE
            else if (answer.get(i).getVariable() == Answer.LABELTYPE) {
                LinkedList<String> labels = graph.getLabelsOfNodesType(answer.get(i).getNode());
                if (listOfLabelsAreEqual(labels, answer.get(i).getValue().toUpperCase())) {
                    answer.get(i).setEvaluateCorrect(true);
                } else {
                    answer.get(i).setEvaluateCorrect(false);
                }
                String stringLabels = "";
                if (labels.size() != 0) {
                    for (int j = 0; j < labels.size(); j++) {
                        stringLabels = stringLabels + labels.get(j) + ", ";
                    }
                    stringLabels = stringLabels.substring(0, stringLabels.length() - 1);
                }
                userAnswer.add(i, yourAnswer + stringLabels);
            } // RETURNS THE TIME ON WHICH A NODE REACHS CERTAIN VALUE
            else if (answer.get(i).getVariable() == Answer.TIMEVALUE) {
                String name = answer.get(i).getNode();
                Vertex v = graph.searchVertexByName(name);
                parseValue(answer.get(i).getValue());
                double val = Double.parseDouble(correctValue);
                int searchedTime = 0;
                boolean isATime = false;
                if (v != null && !(v.equation.value.isEmpty())) {
                    for (int k = 0; k < v.equation.value.size(); k++) {
                        if (Double.parseDouble(nf.format(v.equation.value.get(k))) == val) {
                            searchedTime = k;
                            isATime = true;
                            break;
                        }
                    }
                    if (isATime) {
                        userAnswer.add(i, yourAnswer + searchedTime + "");
                        answer.get(i).setEvaluateCorrect(true);
                    } else {
                        userAnswer.add(i, yourAnswer + "Undefined");
                        answer.get(i).setEvaluateCorrect(false);
                    }
                }
            } else {
                answer.get(i).setEvaluateCorrect(false);
            }
        }
    }

    public boolean listOfLabelsAreEqual(LinkedList<String> modelLabels, String stringCorrectLabels) {
        LinkedList<String> correctLabels = new LinkedList<String>();
        StringTokenizer st;
        String label;

        st = new StringTokenizer(stringCorrectLabels, ",");

        for (int i = 0; i <= st.countTokens(); i++) {
            label = st.nextToken().toUpperCase().trim();
            System.out.print(label + ", ");
            correctLabels.add(label);
        }

        if (modelLabels.size() != correctLabels.size()) {
            return false;
        } else {
            Collections.sort(modelLabels);
            Collections.sort(correctLabels);
            for (int i = 0; i < modelLabels.size(); i++) {
                System.out.println("Model " + modelLabels.get(i));
                System.out.println("Correct " + correctLabels.get(i));
                if (!(modelLabels.get(i).equals(correctLabels.get(i)))) {
                    System.out.println("son diferentes");
                    return false;
                } else {
                    System.out.println("son iguales");
                }
            }
            System.out.println("Todos son iguales");
            return true;
        }
    }

  /**
   * Code for create quizes
   */
  private void createQuiz(int id) {

    Quiz quiz = new Quiz();
        // Quizes by July 20th, 2010
        if (id == 66 ) {
          //QUIZ RainBarrel
            addQuestion("1. How long will it take for the water in the barrel to exceed 40 gallons?");
            addAnswer(new Answer("BARREL","TIMEVALUE","11::41.171"));
            addQuestion("2. How much water is flowing into the barrel during the rainstorm? ");
            addAnswer(new Answer("WATER FLOWING INTO BARREL","VALUE","5::6.00"));
            addQuestion("3. What is the area of the roof?");
            addAnswer(new Answer("ROOF AREA","VALUE","5::300.00"));
            addQuestion("4. How much water is exiting the barrel after 17 minutes?");
            addAnswer(new Answer("BARREL","VALUE","17::49.994"));         
        } else if (id == 67 ) {
          //QUIZ TWO WATER BARRELS
          addQuestion("1. When does the water level in Barrel A first go below 26 inches?");
          addAnswer(new Answer("BARREL A WATER LEVEL","TIMEVALUE","8::25.748"));
          addQuestion("2. What is the water level in Barrel B at 5 minutes?");
          addAnswer(new Answer("BARREL B WATER LEVEL","VALUE","5::7.371"));
          addQuestion("3. How much water flows through the pipe during the 10th minute?");
          addAnswer(new Answer("WATER FLOWING THROUGH PIPE PER MINUTE","VALUE","10::0.697"));
          addQuestion("4. What is the difference in water levels after 30 seconds of flow?");
          addAnswer(new Answer("DIFFERENCE IN WATER LEVELS","VALUE","30::1.696"));
        } else if (id == 60 ) {
          //QUIZ BASIC DEER POPULATION
          addQuestion("1. How many deer are there initially in the park?");
          addAnswer(new Answer("POPULATION","VALUE","0::2000.0"));
          addQuestion("2. How many deer are born each year?");
          addAnswer(new Answer("BIRTHS ANNUALLY","VALUE","0::300.0"));
          addQuestion("3. How many deer die each year?");
          addAnswer(new Answer("DEATHS ANNUALLY","VALUE","0::200.0"));
        } else if (id == 62 ) {
          //QUIZ BASIC MOVIE STAR
          addQuestion("1. How many letters does Robert have to reply to?");
          addAnswer(new Answer("FAN LETTER PILE","VALUE","0::10000.00"));
          addQuestion("2. How many new letters does Robert receive each month?");
          addAnswer(new Answer("LETTERS RECEIVED MONTHLY","VALUE","0::2000.00"));
          addQuestion("3. How many letters can Robert and his assistant respond to each month?");
          addAnswer(new Answer("LETTERS REPLIED TO MONTHLY","VALUE","0::3600.00"));
        } else if (id == 61 ) {
          //QUIZ BASIC TRUST FUND
          addQuestion("1. How much is in Jay's trust fund?");
          addAnswer(new Answer("TRUST FUND","VALUE","0::12000000.00"));
          addQuestion("2. How much does Jay receive in interest each year?");
          addAnswer(new Answer("ANNUAL INTEREST PAYMENTS","VALUE","0::960000.00"));
          addQuestion("3. How much does Jay pay in fees?");
          addAnswer(new Answer("ANNUAL FEES","VALUE","0::360000.00"));
        } else if (id == 63) {
          //QUIZ SIMPLE TEMPE POPULATION
          addQuestion("1. What is Tempe's population before the mayor starts his time in office?");
          addAnswer(new Answer("POPULATION","VALUE","0::175000.00"));
          addQuestion("2. How many people move out of Tempe each year?");
          addAnswer(new Answer("MOVE OUT YEARLY","VALUE","0::12250.000"));
          addQuestion("3. What is the proportion of people who move out of Tempe each?");
          addAnswer(new Answer("TOTAL MOVE OUT PROPORTION","VALUE","0::0.07"));
        } else if (id == 64  ) {
          //QUIZ SIMPLE TEXTILE TRADE
          addQuestion("1. How many yards of fabric does Nina have in stock when she starts her inventory?");
          addAnswer(new Answer("FABRICS IN STOCK","VALUE","0::80000.00"));
          addQuestion("2. How much inventory is sold in the first month of Nina's new job?");
          addAnswer(new Answer("SELL MONTHLY","VALUE","0::40000.00"));
          addQuestion("3. What proportion of the initial stock was sold in the first month?");
          addAnswer(new Answer("PROPORTION OF STOCK SOLD","VALUE","0::0.50"));
        } else if (id == 65 ) {
          //QUIZ SIMPLE TOUR DE FRANCE
          addQuestion("1. Lance's body is made up of how many pounds of water?");
          addAnswer(new Answer("WATER IN BODY","VALUE","0::105.00"));
          addQuestion("2. In his first hour for biking, how many pounds of water will Lance lose due to sweat?");
          addAnswer(new Answer("SWEAT PER HOUR IN LBS","VALUE","0::4.20"));
          addQuestion("3. When it is  75 degrees Fahrenheit outside, what proportion of water in Lance's body is loss due to sweat?");
          addAnswer(new Answer("PROPORTION OF WATER SWEATED PER HOUR","VALUE","0::0.04"));
        } else if (id == 11 ) {
          //QUIZ BATHTUB
          addQuestion("1. How much water will be in the tub when Bob wakes up 8 hours later? ");
          addAnswer(new Answer("BATHTUB","VALUE","8::4.00"));
          addQuestion("2. Bob is interested in finding out how much water will accumulate in what overnight?");
          addAnswer(new Answer("STOCK","LABELTYPE","bathtub"));
          addQuestion("3. How long will it take for 2 gallons of water to accumulate in the bathtub?");
          addAnswer(new Answer("BATHTUB","TIMEVALUE","4::2.00"));
          addQuestion("4. By using the bucket, Bob was able to determine the rate at which the bathtub was filling to be how many gallons per hour?");
          addAnswer(new Answer("FILLING","VALUE","0::0.50"));
        } else if(id == 6) {
          //QUIZ LANDFILL
          addQuestion("1. How many cubic feet of plastics will the Boise landfill contain in twenty years time?");
          addAnswer(new Answer("PLASTICS IN LANDFILL","VALUE","20::36500000.00"));
          addQuestion("2. How much plastics are collected each day?");
          addAnswer(new Answer("PLASTICS COLLECTED PER DAY","VALUE","0::5000.00"));
          addQuestion("3. How long will it take for the landfill to have more than 20,075,000 cubic feet of plastic in it?");
          addAnswer(new Answer("PLASTICS IN LANDFILL","TIMEVALUE","11::20075000.00"));
          addQuestion("4. How much plastics are dumped into the landfill each year?");
          addAnswer(new Answer("DUMPING","VALUE","0::1825000.00"));
        } else if(id == 12) {
          //QUIZ REFORESTATION
          addQuestion("1. How many trees are planted each year?");
          addAnswer(new Answer("PLANTING","VALUE","0::400.00"));
          addQuestion("2. How many trips are made each year?");
          addAnswer(new Answer("TRIPS EACH YEAR","VALUE","0::2.00"));
          addQuestion("3. How long will it take for for the group to plant 4000 trees?");
          addAnswer(new Answer("TREES IN FOREST","TIMEVALUE","10::4000.00"));
          addQuestion("4. How many trees will be planted after 15 years?");
          addAnswer(new Answer("TREES IN FOREST","VALUE","15::6000.00"));
        } else if(id == 32) {
          //QUIZ PAYING OFF CREDIT CARD
          addQuestion("1. Lisa is interested in how much what has decreased over 12 months?");
          addAnswer(new Answer("STOCK","LABELTYPE","credit card balance"));
          addQuestion("2. Lisa wants to know how long will it take her to pay off at least half of what she owes and have a balance of $894.00?");
          addAnswer(new Answer("CREDIT CARD BALANCE","TIMEVALUE","7::894.00"));
          addQuestion("3. What was Lisa's budgeted amount for a single payment after 3 months?");
          addAnswer(new Answer("PAYMENT AMOUNT","VALUE","3::79.00"));
          addQuestion("4. How much did Lisa still owe on her credit card after 10 months?");
          addAnswer(new Answer("CREDIT CARD BALANCE","VALUE","10::420.00"));
        } else if(id == 16) {
          //QUIZ PYGMY OWL
          addQuestion("1. If nothing changes, how long before the Pygmy Owl is extinct?");
          addAnswer(new Answer("PYGMY OWLS","TIMEVALUE","8::0.00"));
          addQuestion("2. The pygmy owl population is being reduced by what?");
          addAnswer(new Answer("FLOW","LABELTYPE","deaths"));
          addQuestion("3. How much time was between observations?");
          addAnswer(new Answer("TIME BETWEEN OBSERVATIONS","VALUE","0::2.00"));
          addQuestion("4. How many deaths were observed between 2005 and 2006?");
          addAnswer(new Answer("OBSERVED DEATHS","VALUE","0::6.00"));
        } else if(id == 14) {
          //QUIZ SMALL POND
          addQuestion("1. In order to save Aaron's fish, he is interested in knowing the level of what after he returns?");
          addAnswer(new Answer("STOCK","LABELTYPE","water in pond"));
          addQuestion("2. Aaron's fish need at least 125 gallons of water in the pond to survive, how long will it take for the pond to reach that level?");
          addAnswer(new Answer("WATER IN POND","TIMEVALUE","36::125"));
          addQuestion("3. How long does it take the pond to completely evaporate?");
          addAnswer(new Answer("TIME TO EVAPORATE","VALUE","0::48.00"));
          addQuestion("4. How much water is evaporating from the pond every hour?");
          addAnswer(new Answer("EVAPORATING","VALUE","0::10.417"));
        } else if(id == 10) {
          //QUIZ BANK ACCOUNT INCOME AND SPENDING
          addQuestion("1. How much money will Stephanie have 16 weeks later at the end of the semester?");
          addAnswer(new Answer("CHECKING ACCOUNT BALANCE","VALUE","16::155.556"));
          addQuestion("2. How is Stephanie putting money into her checking account?");
          addAnswer(new Answer("INFLOW","LABELTYPE","deposits"));
          addQuestion("3. How much money is being taken out of Stephanie's account each week?");
          addAnswer(new Answer("WITHDRAWALS","VALUE","0::115.00"));
          addQuestion("4. How many weeks does Stephanie work during the summer?");
          addAnswer(new Answer("WEEKS WORKED","VALUE","0::9.00"));
        } else if(id == 18) {
          //QUIZ FIR TREES
          addQuestion("1. How many trees is the environmental group predicting that they can plant each year?");
          addAnswer(new Answer("ANNUAL PLANTING","VALUE","0::5000.00"));
          addQuestion("2. How long has the environmental group been working to save the fir trees?");
          addAnswer(new Answer("YEARS PLANTING","VALUE","0::3.00"));
          addQuestion("3. How many fir trees will exist after 20 years if each group continues to work at the same rate they have been?");
          addAnswer(new Answer("FIR TREES","VALUE","20::3100000.00"));
          addQuestion("4. How long will it take for the logging company to reduce the total number of fir trees from 5,000,000 to 3,955,000, even with the environmental groups efforts?");
          addAnswer(new Answer("FIR TREES","TIMEVALUE","11::3955000.00"));
        } else if(id == 25) {
          //QUIZ NUCLEAR WEAPONS
          addQuestion("1. How long will it take for Euromerica to decrease their Nuclear stock pile by 1,000 weapons to 9,000?");
          addAnswer(new Answer("NUCLEAR STOCK PILE","TIMEVALUE","10::9000.00"));
          addQuestion("2. How many nuclear weapons are being disarmed each year?");
          addAnswer(new Answer("ANNUAL DISARMING","VALUE","0::600.00"));
          addQuestion("3. What actions are directly influencing the level of the nuclear stock pile in Euromerica?");
          addAnswer(new Answer("FLOW","LABELTYPE","annual building, annual disarming"));
          addQuestion("4. How many years did it take Euromerica to build 1500 nuclear weapons?");
          addAnswer(new Answer("YEARS TO BUILD","VALUE","0::3.00"));
        } else if(id == 31) {
          //QUIZ HOSPITAL PATIENTS
          addQuestion("1. How long will it take for there to be 85 patients left in the hospital?");
          addAnswer(new Answer("PATIENTS IN HOSPITAL","VALUE","36::85"));
          addQuestion("2. On average, how many patients does a single doctor help each hour?");
          addAnswer(new Answer("PATIENTS DISCHARGED PER DOCTOR PER HOUR","VALUE","0::1.042"));
          addQuestion("3. On average how many patients are being discharged per hour?");
          addAnswer(new Answer("PATIENTS DISCHARGED PER HOUR","VALUE","10::10.417"));
          addQuestion("4. How many patients will be in the hospital after 24 hours?");
          addAnswer(new Answer("PATIENTS IN HOSPITAL","VALUE","24::90"));
        } else if(id == 34) {
          //QUIZ READINGS
          addQuestion("1. Lee has his finals for both his English and History class on the same day, if those finals are at the beginning of the 16th week, how many more pages will he have to read before the start of the final? Assume the instructors gave the same number of problems each month.");
          addAnswer(new Answer("PAGES TO READ","VALUE","16::46.00"));
          addQuestion("2. How many pages does Lee think he will be able to read on the 10th week of the semester?");
          addAnswer(new Answer("PAGES READ WEEKLY","VALUE","10::44.00"));
          addQuestion("3. How many pages is Lee estimating that he can read each time he sits down to read?");
          addAnswer(new Answer("PAGES READ PER DAY","VALUE","0::22.00"));
          addQuestion("4. How many pages are assigned to Lee to read each month from both his English and History courses?");
          addAnswer(new Answer("PAGES ASSIGNED MONTHLY","VALUE","0::185.00"));
        } else if(id == 33) {
          //QUIZ STORE INVENTORY
          addQuestion("1. If Kari's predictions are correct, how long will it take the store's inventory level to reach 520 items?");
          addAnswer(new Answer("INVENTORY","TIMEVALUE","6::520.00"));
          addQuestion("2. On average, how many items are sold each month?");
          addAnswer(new Answer("MONTHLY SALES","VALUE","0::400.00"));
          addQuestion("3. On average how many items does each salesperson sale each day?");
          addAnswer(new Answer("SALES EACH DAY","VALUE","0::20.00"));
          addQuestion("4. How many items in inventory will Kari have by the end of 12 months?");
          addAnswer(new Answer("INVENTORY","VALUE","12::40.00"));
        } else if (id == 44){
          //QUIZ ELECTION
          addQuestion("1. How many days does Martia need to invest to convince all 1000 undecided voters to vote for her?");
          addAnswer(new Answer("UNDECIDED VOTERS","TIMEVALUE","250::0.00"));
          addQuestion("2. After 10 days of work to prep and deliver these events, how many voters have decided to vote for Martia?");
          addAnswer(new Answer("MARTIA SUPPORTERS","VALUE","10::40.00"));
          addQuestion("3. On average, how many people are converted per day?");
          addAnswer(new Answer("CONVERTED PER DAY","VALUE","1::4.00"));
          addQuestion("4. The undecided voters are being reduced by what?");
          addAnswer(new Answer("FLOW","LABELTYPE","converted per day"));
        } else if (id == 42){
          //QUIZ MOVING DAY
          addQuestion("1. How long does it take before all the 1000 boxes are moved to John's new house?");
          addAnswer(new Answer("BOXES IN NEW HOUSE","TIMEVALUE","444::999.00"));
          addQuestion("2. After 24 minutes, exactly how many boxes are in the new house?");
          addAnswer(new Answer("BOXES IN NEW HOUSE","VALUE","24::54.00"));
          addQuestion("3. How many boxes can be moved at once if all cars are packed?");
          addAnswer(new Answer("BOXES PER TRIP","VALUE","0::45.00"));
          addQuestion("4. The boxes in the old house are being reduced by what?");
          addAnswer(new Answer("FLOW","LABELTYPE","moving"));
        } else if (id == 43){
          //QUIZ WATER PURIFICATION
          addQuestion("1. How long before all the 200 liters of dirty water water are gone?");
          addAnswer(new Answer("DIRTY WATER","TIMEVALUE","50::0.00"));
          addQuestion("2. How much total clean water is there after all the water has been purified?");
          addAnswer(new Answer("CLEAN WATER","VALUE","50::202.00"));
          addQuestion("3. How much clean water is there after 43 minutes?");
          addAnswer(new Answer("CLEAN WATER","VALUE","43::174.00"));
          addQuestion("4. The dirty water is being reduced by what?");
          addAnswer(new Answer("FLOW","LABELTYPE","cleaning per minute"));
        } else if (id == 38){
          //QUIZ HIENAS POPULATION
          addQuestion("1. How long does it take for the hyenas population to reach 200?");
          addAnswer(new Answer("POPULATION","TIMEVALUE","36::205.197"));
          addQuestion("2. How does the hyenas population grow over time?");
          addAnswer(new Answer("FLOW","LABELTYPE","births"));
          addQuestion("3. What's hyenas' birth rate in the wild?");
          addAnswer(new Answer("FERTILITY RATE IN WILD","VALUE","0::0.04"));
          addQuestion("4. What's the hyenas population in 50 years?");
          addAnswer(new Answer("POPULATION","VALUE","50::355.334"));
        } else if (id == 39){
          //QUIZ MAGAZINE SUBSCRIPTION
          addQuestion("1. What's the growth rate of the magazine, taking into account factors of word of mouth and cancellation due to deteriorating eye sight?");
          addAnswer(new Answer("NET ANNUAL GROWTH RATE","VALUE","0::0.076"));
          addQuestion("2. How long does it take for the new subscriptions to reach 80,000 per year?");
          addAnswer(new Answer("NEW SUBSCRIPTIONS PER YEAR","TIMEVALUE","11::79050.807"));
          addQuestion("3. How long does it take for the subscriptions to reach 1,500,000?");
          addAnswer(new Answer("SUBSCRIPTIONS","TIMEVALUE","15::1500216.972"));
          addQuestion("4. What's the total number of subscriptions in 20 years?");
          addAnswer(new Answer("SUBSCRIPTIONS","VALUE","20::2163791.602"));
        } else if (id == 37){
          //QUIZ SKIN CELL CULTURE
          addQuestion("1. How many skin cells are in the culture after 8 hours?");
          addAnswer(new Answer("SKIN CELLS","VALUE","8::3059.023"));
          addQuestion("2. How long does it take to grow 7076 skin cells?");
          addAnswer(new Answer("SKIN CELLS","TIMEVALUE","14::7075.706"));
          addQuestion("3. What's the skin cells growth rate per hour?");
          addAnswer(new Answer("GROWTH RATE","VALUE","0::0.15"));
          addQuestion("4. How does the skin cells increase over time?");
          addAnswer(new Answer("FLOW","LABELTYPE","grow"));
        } else if (id == 40){
          //QUIZ IPOD CAMPAIGN
          addQuestion("1. How many iPods will be bought in year 5?");
          addAnswer(new Answer("BUY IPOD","VALUE","5::1474803.96"));
          addQuestion("2. What's the total number of iPods sold in 10 years?");
          addAnswer(new Answer("IPODS","VALUE","10::41402921.695"));
          addQuestion("3. What's the repurchase rate in year 7?");
          addAnswer(new Answer("REPURCHASE RATE","VALUE","7::0.055"));
          addQuestion("4. How long does it take for the total number of iPods sold to reach 35,000,000?");
          addAnswer(new Answer("IPODS","TIMEVALUE","7::34644589.991"));
        } else if (id == 47){
          //QUIZ NICOTINE AND SMOKE
          addQuestion("1. How much Nicotine is in Jamison's body after 12 hours?");
          addAnswer(new Answer("NICOTINE","VALUE","12::0.029"));
          addQuestion("2. How long does it take for Jamison to have 5 mg of Nicotine in his body?");
          addAnswer(new Answer("NICOTINE","TIMEVALUE","5::0.005"));
          addQuestion("3. What's the Nicotine increase rate at the 7th hour?");
          addAnswer(new Answer("NICOTINE INCREASE RATE","VALUE","7::0.174"));
          addQuestion("4. How does the Nicotine level increase in Jamison's body?");
          addAnswer(new Answer("FLOW","LABELTYPE","smoke"));
        } else if (id == 46){
          //QUIZ TULIPS FARMING
          addQuestion("1. How many tulips will there be a year from now?");
          addAnswer(new Answer("TULIPS","VALUE","12::117652.636"));
          addQuestion("2. How long does it take for the tulips to grow to 40,000?");
          addAnswer(new Answer("TULIPS","TIMEVALUE","7::40515.728"));
          addQuestion("3. What's the tulips density in the 8th month?");
          addAnswer(new Answer("DENSITY","VALUE","9::9.416"));
          addQuestion("4. Besides the tulips density and conversion of density to reproduction rate, what other factor also determines the new tulips grown each month?");
          addAnswer(new Answer("STOCK","LABELTYPE","tulips"));
        } else if (id == 54){
          //QUIZ RUNNING
          addQuestion("1. How many minutes before all Bob has less than 100KJ of energy left?");
          addAnswer(new Answer("ENERGY","TIMEVALUE","27::94.064"));
          addQuestion("2. After 30 minutes, how much energy does Bob have left?");
          addAnswer(new Answer("ENERGY","VALUE","30::78.128"));
          addQuestion("3. What is reducing Bob's energy?");
          addAnswer(new Answer("FLOW","LABELTYPE","loss per minute"));
          addQuestion("4. What is Bob's loss rate?");
          addAnswer(new Answer("LOSS RATE","VALUE","1::0.06"));
        } else if (id == 45){
          //QUIZ CHEMISTRY
          addQuestion("1. How long before the compound completely decays?");
          addAnswer(new Answer("COMPOUND","TIMEVALUE","253::0.005"));
          addQuestion("2. After 30 minutes, how much of the compound is left?");
          addAnswer(new Answer("COMPOUND","VALUE","30::1.404"));
          addQuestion("3. What is reducing the amount of compound?");
          addAnswer(new Answer("FLOW","LABELTYPE","decay per minute"));
          addQuestion("4. What is the decay rate of the compound?");
          addAnswer(new Answer("DECAY RATE","VALUE","1::0.025"));
        } else if (id == 53){
          //QUIZ PRODUCTIVITY
          addQuestion("1. How many days will it take to finish all 100 tasks?");
          addAnswer(new Answer("TASKS","TIMEVALUE","161::0.005"));
          addQuestion("2. After 3 days how many tasks are left?");
          addAnswer(new Answer("TASKS","VALUE","3::83.058"));
          addQuestion("3. What is reducing the number of tasks that need to be done?");
          addAnswer(new Answer("FLOW","LABELTYPE","tasks completed per minute"));
          addQuestion("4. What is the task completion rate in this factor?");
          addAnswer(new Answer("NEW TASK COMPLETION RATE","VALUE","1::0.06"));
        } else if (id == 55){
          //QUIZ BUG INVESTATION
          addQuestion("1. How many minutes go by before there are less than 3 roaches killed per minute?");
          addAnswer(new Answer("ROACH DEATHS PER MINUTE","TIMEVALUE","77::2.962"));
          addQuestion("2. After 5 minutes, how many roaches are left?");
          addAnswer(new Answer("ROACHES","VALUE","5::2249.185"));
          addQuestion("3. At exactly minute 3, what is the roach death rate?");
          addAnswer(new Answer("ROACH DEATH RATE","VALUE","3::0.188"));
          addQuestion("4. What is reducing the number of roaches in Eddie's yard?");
          addAnswer(new Answer("FLOW","LABELTYPE","roach deaths per minute"));
        } else if (id == 56){
          //QUIZ FOOD RESOURCES
          addQuestion("1. How long before there less than 3 appetizers left?");
          addAnswer(new Answer("APPETIZERS","TIMEVALUE","160::2.994"));
          addQuestion("2. After 20 minutes, how many appetizers are left?");
          addAnswer(new Answer("APPETIZERS","VALUE","20::19.93"));
          addQuestion("3. What is reducing the number of appetizers?");
          addAnswer(new Answer("FLOW","LABELTYPE","taken per minute"));
          addQuestion("4. After exactly 40 minutes have passed, what is the eating ratio?");
          addAnswer(new Answer("EATING RATE","VALUE","40::0.022"));
        } else if (id == 57){
          //QUIZ VIDEO GAMES
          addQuestion("1. How long does before there are less than 10 sprites left?");
          addAnswer(new Answer("SPRITES","TIMEVALUE","25::10.007"));
          addQuestion("2. After 15 minutes, how many helper sprites are left?  ");
          addAnswer(new Answer("SPRITES","VALUE","15::15.212"));
          addQuestion("3. At exactly minute 4, what is the sprite death rate?  ");
          addAnswer(new Answer("SPRITE DEATH RATE","VALUE","4::0.144"));
          addQuestion("4. What is reducing the number of helper sprites?");
          addAnswer(new Answer("FLOW","LABELTYPE","vanishing per minute"));
        } else if (id == 48){
          //QUIZ TEMPE POPULATION
          addQuestion("1. What's Tempe's population 20 years from now?");
          addAnswer(new Answer("POPULATION","VALUE","20::95689.026"));
          addQuestion("2. How long does it take for Tempe's population to drop to 100,000?");
          addAnswer(new Answer("POPULATION","TIMEVALUE","18::99478.583"));
          addQuestion("3. How many people move in to Tempe each year?");
          addAnswer(new Answer("PEOPLE MOVE IN","VALUE","0::5000.00"));
          addQuestion("4. How many people move out of Tempe in year 12?");
          addAnswer(new Answer("MOVE OUT YEARLY","VALUE","12::8263.251"));
        } else if (id == 49){
          //QUIZ TEXTILE TRADE
          addQuestion("1. How much fabric is in stock in 1 year?");
          addAnswer(new Answer("fabrics in stock","VALUE","12::20014.648"));
          addQuestion("2. How long does it take for the fabrics in stock to reduce to 23,750 yards?");
          addAnswer(new Answer("fabrics in stock","TIMEVALUE","4::23750.00"));
          addQuestion("3. How much fabric does the company buy each month?");
          addAnswer(new Answer("fabrics buy each month","VALUE","0::10000.00"));
          addQuestion("4. How much fabric is sold through retail each month?");
          addAnswer(new Answer("sold through retail","VALUE","0::0.2"));
        } else if (id == 50){
          //QUIZ TOUR DE FRANCE
          addQuestion("1. How does Lance lose water in his body?");
          addAnswer(new Answer("OUTFLOW","LABELTYPE","sweat"));
          addQuestion("2. How much does Lance sweat in 75 degree weather?");
          addAnswer(new Answer("sweat seventy five degree","VALUE","0::0.04"));
          addQuestion("3. How much water is in Lance's body by the end of 8 hours?");
          addAnswer(new Answer("water in body","VALUE","8::89.676"));
          addQuestion("4. How much water does Lance drink each hour?");
          addAnswer(new Answer("drink per hour","VALUE","0::2.00"));
        } else if (id == 1){
          //QUIZ DEER POPULATION
          addQuestion("1. How long does it take for the deer population to reach 2500?");
          addAnswer(new Answer("POPULATION","TIMEVALUE","6::2508.184"));
          addQuestion("2. What's the death rate for the deer population in 10 years?");
          addAnswer(new Answer("DEATH RATE","VALUE","10::0.134"));
          addQuestion("3. What's the birth rate for the deer population in 12 years?");
          addAnswer(new Answer("BIRTH RATE","VALUE","12::0.15"));
          addQuestion("4. What's the deer population in 20 years?");
          addAnswer(new Answer("POPULATION","VALUE","20::2939.107"));
        } else if (id == 52){
          //QUIZ MOVIE STAR
          addQuestion("1. What's the rate Robert and his assistants reply mails each month?");
          addAnswer(new Answer("TOTAL REPLY RATE","VALUE","0::0.36"));
          addQuestion("2. How long does it take for the fan letters received to reduce to 1792 per month?");
          addAnswer(new Answer("LETTERS RECEIVED MONTHLY","TIMEVALUE","5::995.743"));
          addQuestion("3. How many letters are left to reply after 1 year?");
          addAnswer(new Answer("FAN LETTER PILE","VALUE","12::1234.103"));
          addQuestion("4. How many letters Robert and his assistants reply in month 5?");
          addAnswer(new Answer("LETTERS REPLIED TO MONTHLY","VALUE","5::1792.337"));
        } else if (id == 51){
          //QUIZ TRUST FUND
          addQuestion("1. How long does it take for the trust fund to reach 20 million dollars?");
          addAnswer(new Answer("TRUST FUND","TIMEVALUE","11::20524072.297"));
          addQuestion("2. What increases the money in the trust fund?");
          addAnswer(new Answer("INFLOW","LABELTYPE","annual interest payments"));
          addQuestion("3. When does the annual fees reach 500,000 dollars?");
          addAnswer(new Answer("ANNUAL FEES","TIMEVALUE","7::482434.431"));
          addQuestion("4. What's the total fee rate in year 13?");
          addAnswer(new Answer("TOTAL FEE RATE","VALUE","13::0.03"));
        }
  }

    /**
     * This method parses the format for a value answer into searchedTime and the value
     *
     * @param s is the string containing the searchedTime and value
     */
    public void parseValue(String s) {
        boolean after = false;
        char c;
        String tempTime = "";
        String tempValue = "";
        for (int i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            if (c == ':') {
                i++;
                if (i < s.length()) {
                    if (c == ':') {
                        after = true;
                    }
                }
            } else if (((int) c > 47 && (int) c < 58) || (int) c == 46) {
                if (after == false) {
                    tempTime += c;
                } else {
                    tempValue += c;
                }
            }
        }
        time = tempTime;
        correctValue = tempValue;
    }

    // equation with '(' and ')'
   
    public void equationParser(String value, Vertex v, int i) {
        userAnswer.add(i, (v.equation).toString());
        String userAns = v.equation.toString();
        String term = "";
        LinkedList<String> operator = new LinkedList();
        LinkedList<String> nodeList = new LinkedList();
        String subString = "";
        String f = "";
        int start = userAns.indexOf('(');
        int end = userAns.indexOf(')');
        String subStr = "";
        boolean first = true;
        boolean second = false;
        if (userAns.charAt(0) != '(') {
            for (int k = 0; k < userAns.length(); k++) {
                if (userAns.charAt(k) == '+' || userAns.charAt(k) == '*') {
                    if (first == true) {
                        first = false;
                        second = true;
                        operator.add(0, String.valueOf(userAns.charAt(k)));
                        f = term;
                        term = "";
                    }
                } else if (userAns.charAt(k) == ('(')) {
                    subString = userAns.substring(k);
                } else {
                    term += userAns.charAt(k);
                }
            }
            nodeList.add(0, f);
            nodeList.add(1, subString);
        }
        if (userAns.charAt(0) == '(') {
            first = false;
            second = true;
            subString = userAns.substring(start, end + 1);
            operator.add(0, String.valueOf(userAns.charAt(end + 1)));
            subStr = userAns.substring(end + 2);
            nodeList.add(0, subString);
            nodeList.add(1, subStr);
        }
        if (second == true) {
            if ((operator.get(0).equals("+") || (operator.get(0).equals("*")))) {
                String eq1 = nodeList.get(0) + operator.get(0) + nodeList.get(1);
                String eq2 = nodeList.get(1) + operator.get(0) + nodeList.get(0);
                if (answer.get(i).getValue().toUpperCase().equals(eq1.toUpperCase())) {
                    answer.get(i).setEvaluateCorrect(true);
                } else if (answer.get(i).getValue().toUpperCase().equals(eq2.toUpperCase())) {
                    answer.get(i).setEvaluateCorrect(true);
                } else {
                    answer.get(i).setEvaluateCorrect(false);
                }
            }
        } else if (v.equation.toString().toUpperCase().equals(answer.get(i).getValue().toUpperCase())) {
            answer.get(i).setEvaluateCorrect(true);
        } else {
            answer.get(i).setEvaluateCorrect(false);
        }
    }

    //equation without '(' and ')'
    public void parseEquation(String value, Vertex v, int i) {
        userAnswer.add(i, yourAnswer + (v.equation).toString());
        String ans = v.equation.toString();
        LinkedList<String> node = new LinkedList<String>();
        LinkedList<String> operator = new LinkedList<String>();
        String first = "";
        String second = "";
        String third = "";
        String onlyOneOperater = "";
        boolean f = true;
        boolean s = false;
        boolean t = false;

        char c;
        char ch;
        int checker = 0;
        for (int k = 0; k < ans.length(); k++) {
            ch = ans.charAt(k);
            if (ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '^') {
                checker++;
            }
        }
        //Only one operator and 2 parameters
        if (checker == 1 || checker == 0) {
            for (int j = 0; j < ans.length(); j++) {
                c = ans.charAt(j);
                if (c == '+' || c == '*') {
                    f = false;
                    s = true;
                    onlyOneOperater = c + "";
                } else {
                    if (f == true) {
                        first += c;
                    } else if (s == true) {
                        second += c;
                    }
                }
            }
            //multiplication or addition between two values
            if (s == true) {
                String eq1 = first + onlyOneOperater + second;
                String eq2 = second + onlyOneOperater + first;
                if (eq1.toUpperCase().equals(answer.get(i).getValue().toUpperCase())) {
                    answer.get(i).setEvaluateCorrect(true);
                } else if (eq2.toUpperCase().equals(answer.get(i).getValue().toUpperCase())) {
                    answer.get(i).setEvaluateCorrect(true);
                } else {
                    answer.get(i).setEvaluateCorrect(false);
                }
            } else if (v.equation.toString().toUpperCase().equals(answer.get(i).getValue().toUpperCase())) {
                answer.get(i).setEvaluateCorrect(true);
            } else {
                answer.get(i).setEvaluateCorrect(false);
            }
        }
        //when have 2 operators and 3 parameters
        if (checker == 2) {
            for (int j = 0; j < ans.length(); j++) {
                c = ans.charAt(j);
                if (c == '+' || c == '*'|| c == '/' || c == '^') {
                    if (f == true) {
                        f = false;
                        s = true;
                        operator.add(0, String.valueOf(c));
                        node.add(0, first);
                    } else if (s == true) {
                        s = false;
                        t = true;
                        operator.add(1, String.valueOf(c));
                        node.add(1, second);
                    }
                } else {
                    if (f == true) {
                        first += c;
                    } else if (s == true) {
                        second += c;
                    } else if (t == true) {
                        third += c;
                    }
                }
            }
            if(node.size() == 2){
                node.add(2, third);
            }
            //multiplication or addition between two values
            if (t == true) {
                if ((operator.get(0).equals("+") && operator.get(1).equals("+")) || (operator.get(0).equals("*") && operator.get(1).equals("*"))) {
                    String eq1 = node.get(0) + operator.get(0) + node.get(1) + operator.get(1) + node.get(2);
                    String eq2 = node.get(0) + operator.get(0) + node.get(2) + operator.get(1) + node.get(1);
                    String eq3 = node.get(1) + operator.get(0) + node.get(0) + operator.get(1) + node.get(2);
                    String eq4 = node.get(1) + operator.get(0) + node.get(2) + operator.get(1) + node.get(0);
                    String eq5 = node.get(2) + operator.get(0) + node.get(1) + operator.get(1) + node.get(0);
                    String eq6 = node.get(2) + operator.get(0) + node.get(0) + operator.get(1) + node.get(1);
                    if (answer.get(i).getValue().toUpperCase().equals(eq1.toUpperCase())) {
                        answer.get(i).setEvaluateCorrect(true);
                    } else if (answer.get(i).getValue().toUpperCase().equals(eq2.toUpperCase())) {
                        answer.get(i).setEvaluateCorrect(true);
                    } else if (answer.get(i).getValue().toUpperCase().equals(eq3.toUpperCase())) {
                        answer.get(i).setEvaluateCorrect(true);
                    } else if (answer.get(i).getValue().toUpperCase().equals(eq4.toUpperCase())) {
                        answer.get(i).setEvaluateCorrect(true);
                    } else if (answer.get(i).getValue().toUpperCase().equals(eq5.toUpperCase())) {
                        answer.get(i).setEvaluateCorrect(true);
                    } else if (answer.get(i).getValue().toUpperCase().equals(eq6.toUpperCase())) {
                        answer.get(i).setEvaluateCorrect(true);
                    } else {
                        answer.get(i).setEvaluateCorrect(false);
                    }

                }
                if((operator.get(0).equals("*") && operator.get(1).equals("+"))){
                    // a*b+c
                    String eq1 = node.get(0) + operator.get(0) + node.get(1) + operator.get(1) + node.get(2);
                    // b*a+c
                    String eq2 = node.get(1) + operator.get(0) + node.get(0) + operator.get(1) + node.get(2);
                    // c+a*b
                    String eq3 = node.get(2) + operator.get(1) + node.get(0) + operator.get(0) + node.get(1);
                    // c+b*a
                    String eq4 = node.get(2) + operator.get(1) + node.get(1) + operator.get(0) + node.get(0);
                    if (answer.get(i).getValue().toUpperCase().equals(eq1.toUpperCase())) {
                        answer.get(i).setEvaluateCorrect(true);
                    } else if (answer.get(i).getValue().toUpperCase().equals(eq2.toUpperCase())) {
                        answer.get(i).setEvaluateCorrect(true);
                    } else if (answer.get(i).getValue().toUpperCase().equals(eq3.toUpperCase())) {
                        answer.get(i).setEvaluateCorrect(true);
                    } else if (answer.get(i).getValue().toUpperCase().equals(eq4.toUpperCase())) {
                        answer.get(i).setEvaluateCorrect(true);
                    }else {
                        answer.get(i).setEvaluateCorrect(false);
                    }

                }
                if ((operator.get(0).equals("+") && operator.get(1).equals("*"))) {
                    String eq1 = node.get(0) + operator.get(0) + node.get(1) + operator.get(1) + node.get(2);
                    String eq2 = node.get(0) + operator.get(0) + node.get(2) + operator.get(1) + node.get(1);
                    String eq3 = node.get(2) + operator.get(1) + node.get(1) + operator.get(0) + node.get(0);
                    String eq4 = node.get(1) + operator.get(1) + node.get(2) + operator.get(0) + node.get(0);
                    if (answer.get(i).getValue().toUpperCase().equals(eq1.toUpperCase())) {
                        answer.get(i).setEvaluateCorrect(true);
                    } else if (answer.get(i).getValue().toUpperCase().equals(eq2.toUpperCase())) {
                        answer.get(i).setEvaluateCorrect(true);
                    } else if (answer.get(i).getValue().toUpperCase().equals(eq3.toUpperCase())) {
                        answer.get(i).setEvaluateCorrect(true);
                    } else if (answer.get(i).getValue().toUpperCase().equals(eq4.toUpperCase())) {
                        answer.get(i).setEvaluateCorrect(true);
                    } else {
                        answer.get(i).setEvaluateCorrect(false);
                    }

                }
                if((operator.get(0).equals("+") && operator.get(1).equals("/")) || (operator.get(0).equals("+") && operator.get(1).equals("^"))){
                    String eq1 = node.get(0) + operator.get(0) + node.get(1) + operator.get(1) + node.get(2);
                    String eq2 = node.get(1) + operator.get(1) + node.get(2) + operator.get(0) + node.get(0);
                    if (answer.get(i).getValue().toUpperCase().equals(eq1.toUpperCase())) {
                        answer.get(i).setEvaluateCorrect(true);
                    } else if (answer.get(i).getValue().toUpperCase().equals(eq2.toUpperCase())) {
                        answer.get(i).setEvaluateCorrect(true);
                    }else {
                        answer.get(i).setEvaluateCorrect(false);
                    }
                }
                if((operator.get(0).equals("/") && operator.get(1).equals("+")) || (operator.get(0).equals("^") && operator.get(1).equals("+"))){
                    String eq1 = node.get(0) + operator.get(0) + node.get(1) + operator.get(1) + node.get(2);
                    String eq2 = node.get(2) + operator.get(1) + node.get(0) + operator.get(0) + node.get(1);
                    if (answer.get(i).getValue().toUpperCase().equals(eq1.toUpperCase())) {
                        answer.get(i).setEvaluateCorrect(true);
                    } else if (answer.get(i).getValue().toUpperCase().equals(eq2.toUpperCase())) {
                        answer.get(i).setEvaluateCorrect(true);
                    }else {
                        answer.get(i).setEvaluateCorrect(false);
                    }
                }
            } else if (v.equation.toString().toUpperCase().equals(answer.get(i).getValue().toUpperCase())) {
                answer.get(i).setEvaluateCorrect(true);
            } else {
                answer.get(i).setEvaluateCorrect(false);
      }
    }
  }
    
}
