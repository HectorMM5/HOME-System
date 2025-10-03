package no.ntnu.idatx1005.dao;

import static no.ntnu.idatx1005.database.DbConnectionProvider.close;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import no.ntnu.idatx1005.database.DbConnectionProvider;
import no.ntnu.idatx1005.model.task.TaskChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h3>Data access object for the TaskChange model</h3>
 *
 * <p>A data access object for the TaskChange model. It provides methods to retrieve and add
 * changelog entries in the database.
 *
 * @see TaskChange
 * @author William Holtsdalen
 * @since V0.1.0
 */
public class TaskChangeDao {
  private static final Logger logger = LoggerFactory.getLogger(TaskChangeDao.class);
  private final DbConnectionProvider connectionProvider;

  /**
   * Constructs a new task change data access object.
   *
   * @param connectionProvider the connection provider
   */
  public TaskChangeDao(DbConnectionProvider connectionProvider) {
    logger.debug("Initializing TaskChangeDao");
    this.connectionProvider = connectionProvider;
  }

  /**
   * Adds a new changelog entry to the database.
   *
   * @param taskChange the changelog entry to add
   */
  public void addChange(TaskChange taskChange) {
    logger.info("Adding change log entry for task {}: {}", taskChange.taskId(), 
        taskChange.description());
    Connection connection = null;
    PreparedStatement preparedStatement = null;

    try {
      connection = connectionProvider.getConnection();
      preparedStatement = connection.prepareStatement(
          "INSERT INTO task_changelog (id, taskId, description, changedBy, changedAt) "
              + "VALUES (?, ?, ?, ?, ?)",
          Statement.RETURN_GENERATED_KEYS);

      preparedStatement.setString(1, taskChange.id().toString());
      preparedStatement.setString(2, taskChange.taskId().toString());
      preparedStatement.setString(3, taskChange.description());
      preparedStatement.setString(4, taskChange.changedBy().toString());
      preparedStatement.setString(5, taskChange.changedAt().format(
          DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

      preparedStatement.executeUpdate();
      logger.debug("Change log entry added successfully for task {}", taskChange.taskId());
    } catch (SQLException e) {
      logger.error("SQL error while adding change log entry for task {}: {}", 
          taskChange.taskId(), e.getMessage());
    } finally {
      close(connection, preparedStatement, null);
    }
  }

  /**
   * Gets all changelog entries for a specific task.
   *
   * @param taskId the ID of the task to get changelog entries for
   * @return a list of changelog entries for the task
   */
  public List<TaskChange> getChangesByTaskId(UUID taskId) {
    logger.debug("Retrieving change log entries for task {}", taskId);
    List<TaskChange> changes = new ArrayList<>();
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;

    try {
      connection = connectionProvider.getConnection();
      preparedStatement = connection.prepareStatement(
          "SELECT * FROM task_changelog WHERE taskId = ? ORDER BY changedAt DESC");

      preparedStatement.setString(1, taskId.toString());
      resultSet = preparedStatement.executeQuery();

      while (resultSet.next()) {
        changes.add(constructTaskChangeFromResultSet(resultSet));
      }
      logger.debug("Retrieved {} change log entries for task {}", changes.size(), taskId);
    } catch (SQLException e) {
      logger.error("SQL error while retrieving change log entries for task {}: {}", 
          taskId, e.getMessage());
    } finally {
      close(connection, preparedStatement, resultSet);
    }

    return changes;
  }

  /**
   * Constructs a TaskChange object from a ResultSet.
   *
   * @param resultSet the ResultSet to construct from
   * @return the constructed TaskChange object
   * @throws SQLException if there's an error reading from the ResultSet
   */
  private TaskChange constructTaskChangeFromResultSet(ResultSet resultSet) throws SQLException {
    logger.trace("Constructing TaskChange from ResultSet");
    UUID id = UUID.fromString(resultSet.getString("id"));
    UUID taskId = UUID.fromString(resultSet.getString("taskId"));
    String description = resultSet.getString("description");
    UUID changedBy = UUID.fromString(resultSet.getString("changedBy"));
    LocalDateTime changedAt = LocalDateTime.parse(
        resultSet.getString("changedAt"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    );

    return new TaskChange(id, taskId, description, changedBy, changedAt);
  }
} 