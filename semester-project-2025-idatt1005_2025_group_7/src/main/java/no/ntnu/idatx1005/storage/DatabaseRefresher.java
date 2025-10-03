package no.ntnu.idatx1005.storage;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h3>Database Refresher</h3>
 *
 * <p>The class provides a scheduled service to periodically refresh the local database
 * with data from the remote database. It uses the Singleton design pattern to ensure
 * that only one instance of the class is created.
 *
 * @author Hector Mendana Morales
 */
public class DatabaseRefresher {
  private static final Logger logger = LoggerFactory.getLogger(DatabaseRefresher.class);
  private static DatabaseRefresher instance;
  private final ScheduledExecutorService scheduler;
  private boolean isRunning;

  private DatabaseRefresher() {
    this.scheduler = Executors.newScheduledThreadPool(1);
    this.isRunning = false;
  }

  /**
   * Returns the singleton instance of the DatabaseRefresher.
   *
   * @return the singleton instance of the DatabaseRefresher
   */
  public static synchronized DatabaseRefresher getInstance() {
    if (instance == null) {
      logger.debug("Creating new DatabaseRefresher instance");
      instance = new DatabaseRefresher();
    }
    return instance;
  }

  /**
   * Starts the database refresh service if it's not already running.
   */
  public synchronized void start() {
    if (!isRunning) {
      Runnable refreshDatabase = H2Manager::updateLocalStorage;
      scheduler.scheduleAtFixedRate(refreshDatabase, 0, 60, TimeUnit.SECONDS);
      logger.info("Database refresh service started successfully");
      isRunning = true;
    }
  }
}
