package no.ntnu.idatx1005.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import no.ntnu.idatx1005.database.DbConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h3>Database Cloner</h3>
 *
 * <p>The class serves as a middle-point between the remote and in-memory databases.
 * Its primary function is to clone all the rows in the remote database into the in-memory 
 * database while ensuring no duplicate rows are created.
 *
 * @author Hector Mendana Morales
 * @since V1.1.0
 */
public class DatabaseCloner {
  private static final Logger logger = LoggerFactory.getLogger(DatabaseCloner.class);
  private static final H2ConnectionProvider localDatabase = H2ConnectionProvider.instance();
  private static final DbConnectionProvider remoteDatabase = DbConnectionProvider.instance();

  /**
   * Updates the in-memory database with the data from the remote database.
   */
  public static void updateLocalDatabase() {
    logger.info("Starting the database cloning process");
    PreparedStatement remotePreparedStatement = null;
    ResultSet resultSet = null;
    PreparedStatement localPreparedStatement = null;
    Connection remoteConnection = null;
    Connection localConnection = null;

    try {
      remoteConnection = remoteDatabase.getConnection();
      localConnection = localDatabase.getConnection();
      try {
        // Clear local tables first (in correct order due to foreign key constraints)
        localConnection.prepareStatement("DELETE FROM task_assignees").executeUpdate();
        localConnection.prepareStatement("DELETE FROM task_changelog").executeUpdate();
        localConnection.prepareStatement("DELETE FROM task").executeUpdate();
        localConnection.prepareStatement("DELETE FROM \"user\"").executeUpdate();
        logger.debug("Local database tables cleared successfully");

        // Copy users
        remotePreparedStatement = remoteConnection.prepareStatement("SELECT * FROM user");
        resultSet = remotePreparedStatement.executeQuery();
        localPreparedStatement = localConnection.prepareStatement(
            "INSERT INTO \"user\" (userId, firstName, lastName, email, passwordHash, salt, "
                + "workloadCapacity, sickness) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");

        int userCount = 0;
        while (resultSet.next()) {
          localPreparedStatement.setString(1, resultSet.getString(
              "userId"));
          localPreparedStatement.setString(2, resultSet.getString(
              "firstName"));
          localPreparedStatement.setString(3, resultSet.getString(
              "lastName"));
          localPreparedStatement.setString(4, resultSet.getString(
              "email"));
          localPreparedStatement.setBytes(5, resultSet.getBytes(
              "passwordHash"));
          localPreparedStatement.setBytes(6, resultSet.getBytes(
              "salt"));
          localPreparedStatement.setInt(7, resultSet.getInt(
              "workloadCapacity"));
          localPreparedStatement.setBoolean(8, resultSet.getBoolean(
              "sickness"));
          localPreparedStatement.executeUpdate();
          userCount++;
        }
        logger.debug("Copied {} users to local database", userCount);
        remotePreparedStatement.close();
        resultSet.close();

        // Copy tasks
        remotePreparedStatement = remoteConnection.prepareStatement("SELECT * FROM task");
        resultSet = remotePreparedStatement.executeQuery();
        localPreparedStatement = localConnection.prepareStatement(
            "INSERT INTO task (taskId, name, description, completed, createdDate, dueDate, "
                + "completedDate, priority, taskSize, taskWeight) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        int taskCount = 0;
        while (resultSet.next()) {
          localPreparedStatement.setString(1, resultSet.getString(
              "taskId"));
          localPreparedStatement.setString(2, resultSet.getString(
              "name"));
          localPreparedStatement.setString(3, resultSet.getString(
              "description"));
          localPreparedStatement.setBoolean(4, resultSet.getBoolean(
              "completed"));
          localPreparedStatement.setTimestamp(5, resultSet.getTimestamp(
              "createdDate"));
          localPreparedStatement.setTimestamp(6, resultSet.getTimestamp(
              "dueDate"));
          localPreparedStatement.setTimestamp(7, resultSet.getTimestamp(
              "completedDate"));
          localPreparedStatement.setString(8, resultSet.getString(
              "priority"));
          localPreparedStatement.setString(9, resultSet.getString(
              "taskSize"));
          localPreparedStatement.setInt(10, resultSet.getInt(
              "taskWeight"));
          localPreparedStatement.executeUpdate();
          taskCount++;
        }
        logger.debug("Copied {} tasks to local database", taskCount);
        remotePreparedStatement.close();
        resultSet.close();

        // Copy assignments
        remotePreparedStatement = remoteConnection.prepareStatement(
            "SELECT * FROM task_assignees");
        resultSet = remotePreparedStatement.executeQuery();
        localPreparedStatement = localConnection.prepareStatement(
            "INSERT INTO task_assignees (taskId, userId) VALUES (?, ?)");

        int assignmentCount = 0;
        while (resultSet.next()) {
          localPreparedStatement.setString(1, resultSet.getString(
              "taskId"));
          localPreparedStatement.setString(2, resultSet.getString(
              "userId"));
          localPreparedStatement.executeUpdate();
          assignmentCount++;
        }
        logger.debug("Copied {} task assignments to local database", assignmentCount);

        remotePreparedStatement.close();
        resultSet.close();

        // Copy changelog
        remotePreparedStatement = remoteConnection.prepareStatement(
            "SELECT * FROM task_changelog");
        resultSet = remotePreparedStatement.executeQuery();
        localPreparedStatement = localConnection.prepareStatement(
            "INSERT INTO task_changelog (id, taskId, description, changedBy, changedAt) "
                + "VALUES (?, ?, ?, ?, ?)");

        int changelogCount = 0;
        while (resultSet.next()) {
          localPreparedStatement.setString(1, resultSet.getString(
              "id"));
          localPreparedStatement.setString(2, resultSet.getString(
              "taskId"));
          localPreparedStatement.setString(3, resultSet.getString(
              "description"));
          localPreparedStatement.setString(4, resultSet.getString(
              "changedBy"));
          localPreparedStatement.setString(5, resultSet.getString(
              "changedAt"));
          localPreparedStatement.executeUpdate();
          changelogCount++;
        }
        logger.debug("Copied {} changelog entries to local database", changelogCount);
        logger.info("Local database update completed successfully");
      } catch (SQLException e) {
        logger.error("SQL error during database synchronization: {}", e.getMessage());
        throw e;
      }
    } catch (SQLException e) {
      logger.error("Failed to update local database: {}", e.getMessage());
      throw new RuntimeException("Failed to update local database", e);
    } finally {
      logger.debug("Cleaning up database resources");
      if (resultSet != null) {
        try {
          resultSet.close();
        } catch (SQLException e) {
          logger.error("Failed to close ResultSet: {}", e.getMessage());
        }
      }
      if (remotePreparedStatement != null) {
        try {
          remotePreparedStatement.close();
        } catch (SQLException e) {
          logger.error("Failed to close remote PreparedStatement: {}", e.getMessage());
        }
      }
      if (localPreparedStatement != null) {
        try {
          localPreparedStatement.close();
        } catch (SQLException e) {
          logger.error("Failed to close local PreparedStatement: {}", e.getMessage());
        }
      }
      if (remoteConnection != null) {
        try {
          remoteConnection.close();
        } catch (SQLException e) {
          logger.error("Failed to close remote Connection: {}", e.getMessage());
        }
      }
      if (localConnection != null) {
        try {
          localConnection.close();
        } catch (SQLException e) {
          logger.error("Failed to close local Connection: {}", e.getMessage());
        }
      }
    }
  }
}
