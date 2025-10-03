package no.ntnu.idatx1005.dao;

import static no.ntnu.idatx1005.database.DbConnectionProvider.close;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import no.ntnu.idatx1005.dao.exceptions.UserDoesNotExistException;
import no.ntnu.idatx1005.dao.exceptions.UserWithEmailAlreadyExistsException;
import no.ntnu.idatx1005.database.DbConnectionProvider;
import no.ntnu.idatx1005.model.user.User;
import no.ntnu.idatx1005.storage.H2Manager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h3>Data access object for the User model</h3>
 *
 * <p>A data access object for the User model. It provides methods to add, get, and update users in
 * the database.
 *
 * @see User
 * @author William Holtsdalen
 * @since V0.1.0
 */
public class UserDao {
  private static final Logger logger = LoggerFactory.getLogger(UserDao.class);
  private final DbConnectionProvider connectionProvider;

  /**
   * Constructs a new user data access object.
   *
   * @param connectionProvider the connection provider
   */
  public UserDao(DbConnectionProvider connectionProvider) {
    logger.debug("Initializing UserDao");
    this.connectionProvider = connectionProvider;
  }
  
  /**
   * Adds a new user to the database.
   *
   * @param user The user to add.
   * @throws UserWithEmailAlreadyExistsException If a user with the same email already exists.
   */
  public void addUser(User user) {
    logger.info("Adding new user: {}", user.getEmail());
    //Updates users before checking if one with given email exists
    H2Manager.updateLocalStorage();
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    if (H2Manager.getUserByEmail(user.getEmail()) != null) {
      logger.error("User with email {} already exists", user.getEmail());
      throw new UserWithEmailAlreadyExistsException("User with email " + user.getEmail()
          + " already exists.");
    }

    try {
      connection = connectionProvider.getConnection();
      preparedStatement = connection.prepareStatement(
          "INSERT INTO user "
              + "( userId, firstName, lastName, email, passwordHash, salt, workloadCapacity, "
              + "sickness) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
          Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setString(1, user.getId().toString());
      preparedStatement.setString(2, user.getFirstName());
      preparedStatement.setString(3, user.getLastName());
      preparedStatement.setString(4, user.getEmail());
      preparedStatement.setBytes(5, user.getPasswordHash());
      preparedStatement.setBytes(6, user.getSalt());
      preparedStatement.setInt(7, user.getTaskCapacity());
      preparedStatement.setInt(8, user.getSickness() ? 1 : 0);

      preparedStatement.executeUpdate();
      logger.info("User {} added successfully", user.getEmail());
    } catch (SQLException e) {
      logger.error("SQL error while adding user {}: {}", user.getEmail(), e.getMessage());
    } finally {
      close(connection, preparedStatement, null);
    }
  }

  /**
   * Removes the given user from the database.
   *
   * @param user The user to remove.
   * @throws UserDoesNotExistException If the user does not exist in the database.
   */
  public void removeUser(User user) throws UserDoesNotExistException {
    logger.info("Removing user: {}", user.getEmail());
    if (H2Manager.getUserById(user.getId()) == null) {
      logger.error("User with ID {} not found", user.getId());
      throw new UserDoesNotExistException("User with ID " + user.getId() + " not found.");
    }

    Connection connection = null;
    PreparedStatement preparedStatement = null;
    try {
      connection = connectionProvider.getConnection();
      preparedStatement = connection.prepareStatement(
          "DELETE FROM user WHERE userId = ?");
      preparedStatement.setString(1, user.getId().toString());
      preparedStatement.executeUpdate();
      logger.info("User {} removed successfully", user.getEmail());
    } catch (SQLException e) {
      logger.error("SQL error while removing user {}: {}", user.getEmail(), e.getMessage());
    } finally {
      close(connection, preparedStatement, null);
    }
  }

  /**
   * Updates the user in the database with the given user's id, with the properties of the given
   * user.
   *
   * @param user the user to update
   */
  public void updateUser(User user) {
    logger.info("Updating user: {}", user.getEmail());
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    try {
      connection = connectionProvider.getConnection();
      preparedStatement = connection.prepareStatement(
          "UPDATE user SET firstName = ?, lastName = ?, email = ?, passwordHash = ?, salt = ?, "
              + "workloadCapacity = ?, sickness = ? WHERE userId = ?");
      preparedStatement.setString(1, user.getFirstName());
      preparedStatement.setString(2, user.getLastName());
      preparedStatement.setString(3, user.getEmail());
      preparedStatement.setBytes(4, user.getPasswordHash());
      preparedStatement.setBytes(5, user.getSalt());
      preparedStatement.setInt(6, user.getTaskCapacity());
      preparedStatement.setInt(7, user.getSickness() ? 1 : 0);
      preparedStatement.setString(8, user.getId().toString());
      preparedStatement.executeUpdate();
      logger.info("User {} updated successfully", user.getEmail());
    } catch (SQLException e) {
      logger.error("SQL error while updating user {}: {}", user.getEmail(), e.getMessage());
    } finally {
      close(connection, preparedStatement, null);
    }
  }
}
