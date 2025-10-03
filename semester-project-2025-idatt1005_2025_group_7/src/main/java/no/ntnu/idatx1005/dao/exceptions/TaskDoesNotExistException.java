package no.ntnu.idatx1005.dao.exceptions;

/**
 * The class {@code TaskDoesNotExistException} is a custom exception that extends
 * {@code RuntimeException}. It is thrown when a task with a given ID does not exist in the
 * database.
 *
 * @see RuntimeException
 * @author William Holtsdalen
 * @since V0.1.0
 */
public class TaskDoesNotExistException extends RuntimeException {

  /**
   * Constructs a new {@code TaskDoesNotExistException} with the given message.
   *
   * @param message the message of the exception
   */
  public TaskDoesNotExistException(String message) {
    super(message);
  }
}
