package no.ntnu.idatx1005.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import no.ntnu.idatx1005.database.exceptions.DatabaseConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h3>H2 Connection Provider</h3>
 *
 * <p>The class provides a connection to the in-memory database. It uses the Singleton
 * design pattern to ensure that only one instance of the class is created.
 *
 * @author Hector Mendana Morales
 * @since V0.1.0
 */
public class H2ConnectionProvider {
  private static final Logger logger = LoggerFactory.getLogger(H2ConnectionProvider.class);
  private final String url;
  private final Connection connection;
  private static H2ConnectionProvider localDatabase;

  /**
   * Passes a series of queries into the in-memory database, making its structure identical to the 
   * remote database.
   */
  public H2ConnectionProvider() {
    logger.debug("Initializing H2 database connection");
    this.url = "jdbc:h2:mem:localdb";
    this.connection = getConnection();

    try {
      logger.debug("Creating database tables");
      StringBuilder createTaskTable = new StringBuilder();
      createTaskTable.append("CREATE TABLE IF NOT EXISTS task (");
      createTaskTable.append("taskId VARCHAR(36) PRIMARY KEY NOT NULL,");
      createTaskTable.append("name VARCHAR(255) NOT NULL,");
      createTaskTable.append("description TEXT,");
      createTaskTable.append("completed BOOLEAN,");
      createTaskTable.append("createdDate DATETIME,");
      createTaskTable.append("dueDate DATETIME,");
      createTaskTable.append("completedDate DATETIME,");
      createTaskTable.append("priority VARCHAR(255),");
      createTaskTable.append("taskSize VARCHAR(255),");
      createTaskTable.append("taskWeight INT");
      createTaskTable.append(");");

      StringBuilder createUserTable = new StringBuilder();
      createUserTable.append("CREATE TABLE IF NOT EXISTS \"user\" (");
      createUserTable.append("userId VARCHAR(36) PRIMARY KEY NOT NULL,");
      createUserTable.append("firstName VARCHAR(255),"); 
      createUserTable.append("lastName VARCHAR(255),");  
      createUserTable.append("email VARCHAR(255) NOT NULL,");
      createUserTable.append("passwordHash BLOB NOT NULL,");
      createUserTable.append("salt BLOB NOT NULL,");
      createUserTable.append("workloadCapacity INT NOT NULL,");
      createUserTable.append("sickness BOOLEAN");
      createUserTable.append(");");

      StringBuilder createAssigneesTable = new StringBuilder();
      createAssigneesTable.append("CREATE TABLE IF NOT EXISTS task_assignees (");
      createAssigneesTable.append("taskId VARCHAR(36) NOT NULL,");
      createAssigneesTable.append("userId VARCHAR(36) NOT NULL,");
      createAssigneesTable.append("PRIMARY KEY (taskId, userId),");
      createAssigneesTable.append("FOREIGN KEY (taskId) REFERENCES task(taskId),");
      createAssigneesTable.append("FOREIGN KEY (userId) REFERENCES \"user\"(userId)");
      createAssigneesTable.append(");");

      StringBuilder createChangelogTable = new StringBuilder();
      createChangelogTable.append("CREATE TABLE IF NOT EXISTS task_changelog (");
      createChangelogTable.append("id VARCHAR(36) PRIMARY KEY NOT NULL,");
      createChangelogTable.append("taskId VARCHAR(36) NOT NULL,");
      createChangelogTable.append("description TEXT NOT NULL,");
      createChangelogTable.append("changedBy VARCHAR(36) NOT NULL,");
      createChangelogTable.append("changedAt DATETIME NOT NULL,");
      createChangelogTable.append("FOREIGN KEY (taskId) REFERENCES task(taskId),");
      createChangelogTable.append("FOREIGN KEY (changedBy) REFERENCES \"user\"(userId)");
      createChangelogTable.append(");");

      connection.prepareStatement(createUserTable.toString()).execute();
      connection.prepareStatement(createTaskTable.toString()).execute();
      connection.prepareStatement(createAssigneesTable.toString()).execute();
      connection.prepareStatement(createChangelogTable.toString()).execute();
      logger.info("Database tables creation query successfully executed");

    } catch (Exception e) {
      logger.error("Failed to create database tables: {}", e.getMessage());
      throw new DatabaseConnectionException(e.getMessage());
    }
  }

  /**
   * Returns a connection to the in-memory database.
   *
   * @return a connection to the database
   */
  public final Connection getConnection() {
    try {
      // Ensure the H2 database driver is loaded (needed for running app with java -jar command)
      Class.forName("org.h2.Driver");

      return DriverManager.getConnection(url, "local", "");
    } catch (Exception e) {
      logger.error("Failed to establish database connection: {}", e.getMessage());
      throw new DatabaseConnectionException(e.getMessage());
    }
  }

  /**
   * Returns the singleton instance of the database connection provider.
   *
   * @return the singleton instance of the database connection provider
   */
  public static H2ConnectionProvider instance() {
    if (localDatabase == null) {
      logger.debug("Creating new H2ConnectionProvider instance");
      localDatabase = new H2ConnectionProvider();
    }
    return localDatabase;
  }

  /**
   * Closes connections to database, along with resultSets, and statements.
   *
   * @param connection the connection to be closed
   * @param preparedStatement the preparedStatement to be closed
   * @param resultSet the resultSet to be closed
   */
  public static void close(Connection connection, PreparedStatement preparedStatement,
      ResultSet resultSet) {
    if (resultSet != null) {
      try {
        resultSet.close();
      } catch (SQLException e) {
        logger.error("Failed to close ResultSet: {}", e.getMessage());
      }
    }
    if (preparedStatement != null) {
      try {
        preparedStatement.close();
      } catch (SQLException e) {
        logger.error("Failed to close PreparedStatement: {}", e.getMessage());
      }
    }
    if (connection != null) {
      try {
        connection.close();
      } catch (SQLException e) {
        logger.error("Failed to close Connection: {}", e.getMessage());
      }
    }
  }
}