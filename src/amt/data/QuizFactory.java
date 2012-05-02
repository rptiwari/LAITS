package amt.data;

import amt.comm.CommException;
import amt.comm.Database;
import java.util.LinkedList;

/**
 * Factory of Quiz
 *
 * @author Javier Gonzalez Sanchez
 * @version 20101114
 */
public class QuizFactory extends Factory{

  private LinkedList<Quiz> quizzes = new LinkedList<Quiz>();
  private Quiz actualQuiz = new Quiz();
  private static QuizFactory entity;

  /**
   *
   * @return a Database object
   */
  public static QuizFactory getInstance() throws CommException{
    if (entity == null) {
      entity = new QuizFactory();
    }
    return entity;
  }

  /**
   * Constructor
   *
   * @param database
   */
  private QuizFactory() throws CommException {
    super();
  }

  /**
   * This method perform a searching in the list of available quizzes in order to get
   * the quiz with the specified id
   *
   * @param idtask in the database
   * @return Quiz object
   */
  public Quiz searchQuiz(int idtask) {
    for (int i = 0; i < quizzes.size(); i++) {
      if (quizzes.get(i).getIdTask() == idtask) {
        return quizzes.get(i);
      }
    }
    return null;
  }

  /**
   * Get the selected quiz
   *
   * @return a Quiz object
   */
  public Quiz getActualQuiz() {
    return actualQuiz;
  }

  /**
   * Setter method for the current Quiz
   *
   * @param actualQuiz is a Quiz object
   */
  public void setActualQuiz(Quiz actualQuiz) {
    this.actualQuiz = actualQuiz;
  }

}