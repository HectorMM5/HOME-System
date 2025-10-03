package no.ntnu.idatx1005.dao;

import static no.ntnu.idatx1005.database.DbConnectionProvider.close;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import no.ntnu.idatx1005.dao.exceptions.TaskDoesNotExistException;
import no.ntnu.idatx1005.dao.exceptions.UserDoesNotExistException;
import no.ntnu.idatx1005.database.DbConnectionProvider;
import no.ntnu.idatx1005.storage.H2Manager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h3>Data access object for assignments between tasks and users.</h3>
 *
 * <p>This class provides methods to retrieve, add, remove, and update assignments between tasks and
 * users in the database.
 *
 * @author Hector Mendana Morales
 * @since V0.1.0
 */
public class AssigneesDao {
  private static final Logger logger = LoggerFactory.getLogger(AssigneesDao.class);
  private final DbConnectionProvider connectionProvider;

  /**
   * Constructs a new assignees data access object.
   *
   * @param connectionProvider the connection provider
   */
  public AssigneesDao(DbConnectionProvider connectionProvider) {
    logger.debug("Initializing AssigneesDao");
    this.connectionProvider = connectionProvider;
  }

  /**
   * Assigns a given user to a given task.
   *
   * @param taskId the task ID
   * @param userId the user ID
   * @throws TaskDoesNotExistException if a task with the given ID does not exist in the database.
   * @throws UserDoesNotExistException if a user with the given ID does not exist in the database.
   */
  public void assignUserToTask(UUID taskId, UUID userId) throws TaskDoesNotExistException,
      UserDoesNotExistException {
    logger.info("Assigning user {} to task {}", userId, taskId);
    if (H2Manager.getTaskById(taskId) == null) {
      logger.error("Task with ID {} not found", taskId);
      throw new TaskDoesNotExistException("Task with ID " + taskId + " not found.");
    }
    if (H2Manager.getUserById(userId) == null) {
      logger.error("User with ID {} not found", userId);
      throw new UserDoesNotExistException("User with ID " + userId + " not found.");
    }

    Connection connection = null;
    PreparedStatement preparedStatement = null;
    try {
      connection = connectionProvider.getConnection();
      preparedStatement = connection.prepareStatement(
          "INSERT INTO task_assignees (taskId, userId) VALUES (?, ?)",
          Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setString(1, taskId.toString());
      preparedStatement.setString(2, userId.toString());
      preparedStatement.executeUpdate();
      logger.info("User {} assigned to task {} successfully", userId, taskId);
    } catch (SQLException e) {
      logger.error("SQL error while assigning user {} to task {}: {}", 
          userId, taskId, e.getMessage());
    } finally {
      close(connection, preparedStatement, null);
    }
  }

  /**
   * Removes all assignments for the given task from the database.
   *
   * @param taskId the ID of the task to remove assignments for
   * @throws TaskDoesNotExistException if a task with the given ID does not exist in the database.
   */
  public void removeAllTaskAssignees(UUID taskId) throws TaskDoesNotExistException {
    logger.info("Removing all assignees from task {}", taskId);
    if (H2Manager.getTaskById(taskId) == null) {
      logger.error("Task with ID {} not found", taskId);
      throw new TaskDoesNotExistException("Task with ID " + taskId + " not found.");
    }

    Connection connection = null;
    PreparedStatement preparedStatement = null;
    try {
      connection = connectionProvider.getConnection();
      preparedStatement = connection.prepareStatement(
          "DELETE FROM task_assignees WHERE taskId = ?");
      preparedStatement.setString(1, taskId.toString());
      preparedStatement.executeUpdate();
      logger.info("All assignees removed from task {} successfully", taskId);
    } catch (SQLException e) {
      logger.error("SQL error while removing assignees from task {}: {}", 
          taskId, e.getMessage());
    } finally {
      close(connection, preparedStatement, null);
    }
  }
}
