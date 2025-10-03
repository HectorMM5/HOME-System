package no.ntnu.idatx1005.dao;

import static no.ntnu.idatx1005.database.DbConnectionProvider.close;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import no.ntnu.idatx1005.dao.exceptions.TaskDoesNotExistException;
import no.ntnu.idatx1005.dao.exceptions.TaskWithNameAlreadyExistsException;
import no.ntnu.idatx1005.database.DbConnectionProvider;
import no.ntnu.idatx1005.model.task.Task;
import no.ntnu.idatx1005.storage.H2Manager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h3>Data access object for the Task model</h3>
 *
 * <p>A data access object for the Task model. It provides methods to retrieve, add, delete, and
 * update tasks in the database.
 *
 * @see Task
 * @author William Holtsdalen
 * @since V0.1.0
 */
public class TaskDao {
  private static final Logger logger = LoggerFactory.getLogger(TaskDao.class);
  private static final String TASK_NOT_FOUND_MESSAGE = "Task not found.";
  private final DbConnectionProvider connectionProvider;
  private final AssigneesDao assigneesDao;

  /**
   * Constructs a new task data access object.
   *
   * @param connectionProvider the connection provider
   */
  public TaskDao(DbConnectionProvider connectionProvider, AssigneesDao assigneesDao) {
    logger.debug("Initializing TaskDao");
    this.connectionProvider = connectionProvider;
    this.assigneesDao = assigneesDao;
  }

  /**
   * Adds a new task to the database.
   *
   * @param task the task to add
   * @throws TaskWithNameAlreadyExistsException if a task with the name of the given task already
   *                                           exists in the database.
   */
  public void addTask(Task task) throws TaskWithNameAlreadyExistsException {
    logger.info("Adding new task: {}", task.getName());
    if (!H2Manager.getTaskByName(task.getName()).isEmpty()) {
      logger.error("Task with name {} already exists", task.getName());
      throw new TaskWithNameAlreadyExistsException("Task with name " + task.getName()
          + " already exists.");
    }

    Connection connection = null;
    PreparedStatement preparedStatement = null;
    try {
      connection = connectionProvider.getConnection();
      preparedStatement = connection.prepareStatement(
          "INSERT INTO task (taskId, name, description, createdDate, dueDate, priority, taskSize) "
              + "VALUES (?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setString(1, task.getId().toString());
      preparedStatement.setString(2, task.getName());
      preparedStatement.setString(3, task.getDescription());
      preparedStatement.setString(4, task.getCreatedDate().toString());
      preparedStatement.setString(5, task.getDueDate().toString());
      preparedStatement.setString(6, task.getPriority().toString());
      preparedStatement.setString(7, task.getSize().toString());
      preparedStatement.executeUpdate();
      logger.debug("Task {} inserted into database", task.getName());

      H2Manager.updateLocalStorage();
      for (UUID userId : task.getAssignedUserIds()) {
        logger.debug("Assigning user {} to task {}", userId, task.getName());
        assigneesDao.assignUserToTask(task.getId(), userId);
      }
      logger.info("Task {} added successfully with {} assignees", 
          task.getName(), task.getAssignedUserIds().size());

    } catch (SQLException e) {
      logger.error("SQL error while adding task {}: {}", task.getName(), e.getMessage());
    } finally {
      close(connection, preparedStatement, null);
    }
  }

  /**
   * Edits/updates a task in the database.
   *
   * @param task the task to update with new values (must have the same ID as the task to update)
   * @throws TaskDoesNotExistException if a task with the given ID does not exist in the database.
   */
  public void updateTask(Task task) throws TaskDoesNotExistException {
    logger.info("Updating task: {}", task.getName());
    if (H2Manager.getTaskById(task.getId()) == null) {
      logger.error("Task with ID {} not found", task.getId());
      throw new TaskDoesNotExistException(TASK_NOT_FOUND_MESSAGE);
    }

    Connection connection = null;
    PreparedStatement preparedStatement = null;
    try {
      connection = connectionProvider.getConnection();
      preparedStatement = connection.prepareStatement(
          "UPDATE task SET completed = ?, name = ?, description = ?, createdDate = ?, dueDate = ?, "
              + "completedDate = ?, priority = ?, taskSize = ? WHERE taskId = ?");
      preparedStatement.setString(1, task.isCompleted() ? "1" : "0");
      preparedStatement.setString(2, task.getName());
      preparedStatement.setString(3, task.getDescription());
      preparedStatement.setString(4, task.getCreatedDate().toString());
      preparedStatement.setString(5, task.getDueDate().toString());
      preparedStatement.setString(6, task.getCompletedDate() == null ? null :
          task.getCompletedDate().toString());
      preparedStatement.setString(7, task.getPriority().toString());
      preparedStatement.setString(8, task.getSize().toString());
      preparedStatement.setString(9, task.getId().toString());
      preparedStatement.executeUpdate();

      updateAssignments(task);
      logger.info("Task {} updated successfully in remote database", task.getName());
    } catch (SQLException e) {
      logger.error("SQL error while updating task {}: {}", task.getName(), e.getMessage());
    } finally {
      close(connection, preparedStatement, null);
    }
  }

  /**
   * Deletes the task from the database.
   *
   * @param task the task to delete.
   * @throws TaskDoesNotExistException if a task with the given ID does not exist in the database.
   */
  public void deleteTask(Task task) throws TaskDoesNotExistException {
    logger.info("Deleting task: {}", task.getName());
    if (H2Manager.getTaskById(task.getId()) == null) {
      logger.error("Task with ID {} not found", task.getId());
      throw new TaskDoesNotExistException(TASK_NOT_FOUND_MESSAGE);
    }

    Connection connection = null;
    PreparedStatement preparedStatement = null;
    try {
      connection = connectionProvider.getConnection();
      preparedStatement = connection.prepareStatement(
          "DELETE FROM task WHERE taskId = ?");
      preparedStatement.setString(1, task.getId().toString());
      preparedStatement.executeUpdate();
      logger.info("Task {} deleted successfully", task.getName());
    } catch (SQLException e) {
      logger.error("SQL error while deleting task {}: {}", task.getName(), e.getMessage());
    } finally {
      close(connection, preparedStatement, null);
    }
  }

  /**
   * Updates the task-user assignments in the database for the given task, by calling the
   * appropriate methods in the {@link AssigneesDao} class.
   *
   * @param task the task to update the assignments for
   * @throws TaskDoesNotExistException if a task with the given ID does not exist in the database.
   */
  private void updateAssignments(Task task) throws TaskDoesNotExistException {
    logger.debug("Updating assignments for task: {}", task.getName());
    if (H2Manager.getTaskById(task.getId()) == null) {
      logger.error("Task with ID {} not found", task.getId());
      throw new TaskDoesNotExistException(TASK_NOT_FOUND_MESSAGE);
    }
    if (task.getAssignedUserIds().isEmpty()) {
      logger.debug("There are no assignments to be updated for task {}", task.getName());
      return;
    }

    // Remove all current assignments for the task
    assigneesDao.removeAllTaskAssignees(task.getId());
    
    // Add new assignments for the task
    task.getAssignedUserIds().forEach(userId ->
        assigneesDao.assignUserToTask(task.getId(), userId)
    );
    logger.debug("Updated all assignments for task {}", task.getName());
  }
}
