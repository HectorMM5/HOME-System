package no.ntnu.idatx1005.dao.exceptions;

/**
 * The class {@code UserWithEmailAlreadyExistsException} is a custom exception that extends
 * {@code RuntimeException}. It is thrown when a user with the same email already exists in the
 * database.
 *
 * @see RuntimeException
 * @author William Holtsdalen
 * @since V0.1.0
 */
public class UserWithEmailAlreadyExistsException extends RuntimeException {

  /**
   * Constructs a new {@code UserWithEmailAlreadyExistsException} with the given message.
   *
   * @param message the message of the exception
   */
  public UserWithEmailAlreadyExistsException(String message) {
    super(message);
  }

}
