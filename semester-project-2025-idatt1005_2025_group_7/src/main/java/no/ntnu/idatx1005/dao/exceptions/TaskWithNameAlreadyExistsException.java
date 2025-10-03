package no.ntnu.idatx1005.dao.exceptions;

/**
 * The class {@code TaskWithNameAlreadyExistsException} is a custom exception that extends
 * {@code RuntimeException}. It is thrown when a task with the same name already exists in the
 * database.
 *
 * @see RuntimeException
 * @author William Holtsdalen
 * @since V0.1.0
 */
public class TaskWithNameAlreadyExistsException extends RuntimeException {

  /**
   * Constructs a new {@code TaskWithNameAlreadyExistsException} with the given message.
   *
   * @param message the message of the exception
   */
  public TaskWithNameAlreadyExistsException(String message) {
    super(message);
  }

}
