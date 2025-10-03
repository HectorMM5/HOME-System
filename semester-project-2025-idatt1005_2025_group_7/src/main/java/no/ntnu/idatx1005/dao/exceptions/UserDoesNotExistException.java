package no.ntnu.idatx1005.dao.exceptions;

/**
 * The class {@code UserDoesNotExistException} is a custom exception that extends
 * {@code RuntimeException}. It is thrown when a user with a given ID does not exist in the
 * database.
 *
 * @see RuntimeException
 * @author William Holtsdalen
 * @since V0.1.0
 */
public class UserDoesNotExistException extends RuntimeException {

  /**
   * Constructs a new {@code UserDoesNotExistException} with the given message.
   *
   * @param message the message of the exception
   */
  public UserDoesNotExistException(String message) {
    super(message);
  }
}
