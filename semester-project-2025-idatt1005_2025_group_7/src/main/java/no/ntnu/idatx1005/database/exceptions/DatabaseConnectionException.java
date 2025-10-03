package no.ntnu.idatx1005.database.exceptions;

/**
 * The class {@code DatabaseConnectionException} is a custom exception that extends
 * {@code RuntimeException}. It is thrown when a database connection fails.
 *
 * @see RuntimeException
 * @author William Holtsdalen
 * @since V0.1.0
 */
public class DatabaseConnectionException extends RuntimeException {

  /**
   * Constructs a new {@code DatabaseConnectionException} with the given message.
   *
   * @param message the message of the exception
   */
  public DatabaseConnectionException(String message) {
    super(message);
  }
}
