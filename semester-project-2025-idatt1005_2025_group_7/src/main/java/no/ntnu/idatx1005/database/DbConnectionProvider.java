package no.ntnu.idatx1005.database;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import no.ntnu.idatx1005.database.exceptions.DatabaseConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h3>Database Connection Provider</h3>
 *
 * <p>The class provides a connection to the remote database. It uses the Singleton
 * design pattern to ensure that only one instance of the class is created.
 *
 * @author William Holtsdalen
 * @since V0.1.0
 */
public class DbConnectionProvider {
  private static final Logger logger = LoggerFactory.getLogger(DbConnectionProvider.class);
  private static DbConnectionProvider databaseConnectionProvider;

  private final String url;
  private final String username;
  private final String password;

  /**
   * Constructs a new database connection provider.
   *
   * <p>Initializes the database connection provider with the necessary database
   * connection information.
   */
  public DbConnectionProvider() {
    logger.debug("Initializing database connection provider");
    Dotenv dotenv = Dotenv.load();

    final String dbName = "hectome";
    final String dbIpAddress = "namox.idi.ntnu.no";
    final int dbPort = 3306;
    final String dbKeyRetrieval = "?allowPublicKeyRetrieval=true";
    final String dbUseSsl = "useSSL=false";
    this.url = "jdbc:mysql://" + dbIpAddress + ":" + dbPort + "/" + dbName + dbKeyRetrieval + "&"
        + dbUseSsl;

    this.username = dotenv.get("DATABASE_USERNAME");
    this.password = dotenv.get("DATABASE_PASSWORD");

    if (username == null || password == null) {
      logger.error("Database credentials not found in environment variables");
      throw new DatabaseConnectionException("Database credentials not found in environment "
          + "variables");
    }
    logger.debug("Database connection provider initialized with URL: {}", url);
  }

  /**
   * Returns a connection to the database.
   *
   * @return a connection to the database
   */
  public Connection getConnection() {
    try {
      logger.debug("Establishing database connection to {}", url);
      Connection conn = DriverManager.getConnection(url, username, password);
      logger.debug("Database connection established successfully");
      return conn;
    } catch (Exception e) {
      logger.error("Failed to establish database connection", e);
      throw new DatabaseConnectionException(e.getMessage());
    }
  }

  /**
   * Returns the singleton instance of the database connection provider.
   *
   * @return the singleton instance of the database connection provider
   */
  public static DbConnectionProvider instance() {
    if (databaseConnectionProvider == null) {
      logger.debug("Creating new DbConnectionProvider instance");
      databaseConnectionProvider = new DbConnectionProvider();
    }
    return databaseConnectionProvider;
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
