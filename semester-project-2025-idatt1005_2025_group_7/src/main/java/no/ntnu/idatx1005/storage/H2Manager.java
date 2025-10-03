package no.ntnu.idatx1005.storage;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javafx.util.Pair;
import no.ntnu.idatx1005.model.task.Priority;
import no.ntnu.idatx1005.model.task.Size;
import no.ntnu.idatx1005.model.task.Task;
import no.ntnu.idatx1005.model.task.TaskChange;
import no.ntnu.idatx1005.model.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h3>Storage for the database.</h3>
 *
 * <p>A local storage class for caching the data from the database.
 *
 * @author Hector Mendana Morales
 * @since V0.1.0
 */
public class H2Manager {
  private static final Logger logger = LoggerFactory.getLogger(H2Manager.class);
  private static final H2ConnectionProvider h2database = H2ConnectionProvider.instance();

  /**
   * Private constructor to prevent instantiation.
   */
  private H2Manager() {}

  /**
   * Updates the local database with the data from the remote database.
   */
  public static void updateLocalStorage() {
    DatabaseCloner.updateLocalDatabase();
    logger.info("Local database updated successfully");
  }

  /**
   * Constructs a Task object from a ResultSet.
   *
   * @param resultSet the ResultSet to construct the Task from
   * @return the constructed Task object
   * @throws SQLException if an error occurs while constructing the Task
   */
  private static Task constructTaskFromResultSet(ResultSet resultSet) throws SQLException {
    if (resultSet == null) {
      logger.debug("Null ResultSet provided to constructTaskFromResultSet");
      return null;
    }

    UUID id = UUID.fromString(resultSet.getString("taskId"));
    boolean completed = resultSet.getBoolean("completed");
    String name = resultSet.getString("name");
    String description = resultSet.getString("description") == null ? "" : 
        resultSet.getString("description");
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    LocalDateTime createdDate = LocalDateTime.parse(resultSet.getString("createdDate"), formatter);
    LocalDateTime dueDate = LocalDateTime.parse(resultSet.getString("dueDate"), formatter);
    String completedDateString = resultSet.getString("completedDate");
    LocalDateTime completedDate = completedDateString == null ? null :
        LocalDateTime.parse(completedDateString, formatter);
    Priority priority = Priority.valueOf(resultSet.getString("priority"));
    Size taskSize = Size.valueOf(resultSet.getString("taskSize"));

    List<UUID> assignedUserIds = getAllAssignments().stream()
        .filter(pair -> pair.getKey().equals(id))
        .map(Pair::getValue)
        .toList();

    Task task = new Task(id, completed, name, description, createdDate, dueDate, priority,
        taskSize);
    task.setCompletedDate(completedDate);
    task.setAssignedUserIds(assignedUserIds);
    return task;
  }

  /**
   * Retrieves all tasks from the local database.
   *
   * @return a list of all tasks in the local database
   */
  public static List<Task> getAllTasks() {
    List<Task> tasks = new ArrayList<>();

    try (
        Connection connection = h2database.getConnection();
        PreparedStatement selectAll = connection.prepareStatement("SELECT * FROM task");
        ResultSet rs = selectAll.executeQuery()
    ) {
      while (rs.next()) { 
        tasks.add(constructTaskFromResultSet(rs));
      }

    } catch (SQLException e) {
      logger.error("Error retrieving all tasks: {}", e.getMessage());
    }
    return tasks;
  }

  /**
   * Retrieves a task from the local database by its ID.
   *
   * @param taskId the ID of the task to retrieve
   * @return the task with the given ID, or null if it does not exist
   */
  public static Task getTaskById(UUID taskId) {
    Task task = null;

    try (
        Connection connection = h2database.getConnection();
        PreparedStatement selectTask = connection.prepareStatement("SELECT * FROM task "
            + "WHERE taskId = ?")
    ) {
      selectTask.setString(1, taskId.toString());

      try (ResultSet rs = selectTask.executeQuery()) {
        if (rs.next()) { 
          task = constructTaskFromResultSet(rs);
        } else {
          logger.debug("No task found with ID: {}", taskId);
        }
      }

    } catch (SQLException e) {
      logger.error("Error retrieving task with ID {}: {}", taskId, e.getMessage());
    }
    return task;
  }

  /**
   * Retrieves all tasks from the local database by their name.
   *
   * @param name the name of the tasks to retrieve
   * @return a list of all tasks with the given name
   */
  public static List<Task> getTaskByName(String name) {
    List<Task> tasksWithName = new ArrayList<>();

    try (
        Connection connection = h2database.getConnection();
        PreparedStatement selectTask = connection.prepareStatement("SELECT * FROM task "
            + "WHERE name = ?")
    ) {
      selectTask.setString(1, name);

      try (ResultSet rs = selectTask.executeQuery()) {
        while (rs.next()) { 
          tasksWithName.add(constructTaskFromResultSet(rs));
        }
      }

    } catch (SQLException e) {
      logger.error("Error retrieving tasks with name {}: {}", name, e.getMessage());
    }
    return tasksWithName;
  }

  /**
   * Retrieves all tasks from the local database that are due today.
   *
   * @return a list of all tasks due today
   */
  public static List<Task> getTasksDueToday() {
    List<Task> tasksDueToday = new ArrayList<>();

    try (
        Connection connection = h2database.getConnection();
        PreparedStatement todaysTasks = connection.prepareStatement("SELECT * FROM task "
            + "WHERE CAST(dueDate AS DATE) = CURRENT_DATE")
    ) {
      try (ResultSet rs = todaysTasks.executeQuery()) {
        while (rs.next()) { 
          tasksDueToday.add(constructTaskFromResultSet(rs));
        }
      }

    } catch (SQLException e) {
      logger.error("Error retrieving tasks due today: {}", e.getMessage());
    }
    return tasksDueToday;
  }

  /**
   * Retrieves all tasks from the local database that are due this week.
   *
   * @return a list of all tasks due this week
   */
  public static List<Task> getTasksDueThisWeek() {
    List<Task> tasksDueThisWeek = new ArrayList<>();
    LocalDate today = LocalDate.now();
    LocalDate monday = today.minusDays(today.getDayOfWeek().getValue() - 1);
    LocalDate sunday = monday.plusDays(6);

    try (
        Connection connection = h2database.getConnection();
        PreparedStatement dueThisWeek = connection.prepareStatement("SELECT * FROM task "
            + "WHERE CAST(dueDate AS DATE) BETWEEN ? AND ?")
    ) {
      dueThisWeek.setDate(1, Date.valueOf(monday));
      dueThisWeek.setDate(2, Date.valueOf(sunday));

      try (ResultSet rs = dueThisWeek.executeQuery()) {
        while (rs.next()) { 
          tasksDueThisWeek.add(constructTaskFromResultSet(rs));
        }
      }

    } catch (SQLException e) {
      logger.error("Error retrieving tasks due this week: {}", e.getMessage());
    }
    return tasksDueThisWeek;
  }

  /**
   * Retrieves all tasks from the local database that have been completed today.
   *
   * @return a list of all tasks completed today
   */
  public static List<Task> getTasksCompletedToday() {
    List<Task> tasksCompletedToday = new ArrayList<>();

    try (
        Connection connection = h2database.getConnection();
        PreparedStatement completedToday = connection.prepareStatement("SELECT * FROM task "
            + "WHERE CAST(completedDate AS DATE) = CURRENT_DATE")
    ) {
      try (ResultSet rs = completedToday.executeQuery()) {
        while (rs.next()) {
          tasksCompletedToday.add(constructTaskFromResultSet(rs));
        }
      }
    } catch (SQLException e) {
      logger.error("Error retrieving tasks completed today: {}", e.getMessage());
    }
    return tasksCompletedToday;
  }

  /**
   * Retrieves all tasks from the local database that have been completed this week.
   *
   * @return a list of all tasks completed this week
   */
  public static List<Task> getTasksCompletedThisWeek() {
    List<Task> tasksCompletedThisWeek = new ArrayList<>();
    LocalDate today = LocalDate.now();
    LocalDate monday = today.minusDays(today.getDayOfWeek().getValue() - 1);
    LocalDate sunday = monday.plusDays(6);
    
    try (
        Connection connection = h2database.getConnection();
        PreparedStatement completedThisWeek = connection.prepareStatement("SELECT * FROM task "
            + "WHERE CAST(completedDate AS DATE) BETWEEN ? AND ?")
    ) {
      completedThisWeek.setDate(1, Date.valueOf(monday));
      completedThisWeek.setDate(2, Date.valueOf(sunday));

      try (ResultSet rs = completedThisWeek.executeQuery()) {
        while (rs.next()) {
          tasksCompletedThisWeek.add(constructTaskFromResultSet(rs));
        }
      }
    } catch (SQLException e) {
      logger.error("Error retrieving tasks completed this week: {}", e.getMessage());
    }
    return tasksCompletedThisWeek;
  }

  /**
   * Retrieves all tasks from the local database that are completed.
   *
   * @return a list of all completed tasks
   */
  public static List<Task> getCompletedTasks() {
    List<Task> completedTasks = new ArrayList<>();

    try (
        Connection connection = h2database.getConnection();
        PreparedStatement completed = connection.prepareStatement("SELECT * FROM task "
            + "WHERE completed = ?")
    ) {
      completed.setBoolean(1, true);

      try (ResultSet rs = completed.executeQuery()) {
        while (rs.next()) { 
          completedTasks.add(constructTaskFromResultSet(rs));
        }
      }

    } catch (SQLException e) {
      logger.error("Error retrieving completed tasks: {}", e.getMessage());
    }
    return completedTasks;
  }

  /**
   * Retrieves all tasks from the local database that are not completed.
   *
   * @return a list of all tasks that are not completed
   */
  public static List<Task> getOpenTasks() {
    List<Task> uncompleteTasks = new ArrayList<>();
    
    try (
        Connection connection = h2database.getConnection();
        PreparedStatement uncompleted = connection.prepareStatement("SELECT * FROM task "
            + "WHERE completed = ?")
    ) {
      uncompleted.setBoolean(1, false);

      try (ResultSet rs = uncompleted.executeQuery()) {
        while (rs.next()) { 
          uncompleteTasks.add(constructTaskFromResultSet(rs));
        }
      }
      
    } catch (SQLException e) {
      logger.error("Error retrieving open tasks: {}", e.getMessage());
    }
    return uncompleteTasks;
  }

  /**
   * Deletes a task from the local database with the given ID.
   *
   * @param taskId the ID of the task to delete
   */
  public static void deleteTask(UUID taskId) {
    String query = "DELETE FROM task WHERE taskId = ?";
    try (
        Connection connection = h2database.getConnection();
        PreparedStatement deleteTask = connection.prepareStatement(query)
    ) {
      deleteTask.setString(1, taskId.toString());
      deleteTask.executeUpdate();
      logger.info("Task with ID {} deleted successfully", taskId);
    } catch (SQLException e) {
      logger.error("Error deleting task with ID {}: {}", taskId, e.getMessage());
    }
  }

  /**
   * Constructs a User object from a ResultSet.
   *
   * @param resultSet the ResultSet to construct the User from
   * @return the constructed User object
   * @throws SQLException if an error occurs while constructing the User
   */
  private static User constructUserFromResultSet(ResultSet resultSet) throws SQLException {
    if (resultSet == null) {
      logger.debug("Null ResultSet provided to constructUserFromResultSet");
      return null;
    }

    UUID id = UUID.fromString(resultSet.getString("userId"));
    String firstName = resultSet.getString("firstName");
    String lastName = resultSet.getString("lastName");
    String email = resultSet.getString("email");
    byte[] passwordHash = resultSet.getBytes("passwordHash");
    byte[] salt = resultSet.getBytes("salt");
    int workloadCapacity = resultSet.getInt("workloadCapacity");
    boolean isSick = resultSet.getInt("sickness") == 1;
    return new User(id, firstName, lastName, email, passwordHash, salt, workloadCapacity, isSick);
  }

  /**
   * Retrieves all users from the local database.
   *
   * @return a list of all users in the local database
   */
  public static List<User> getAllUsers() {
    List<User> users = new ArrayList<>();
    try (
        Connection connection = h2database.getConnection();
        PreparedStatement allUsers = connection.prepareStatement("SELECT * FROM \"user\"");
        ResultSet rs = allUsers.executeQuery()
    ) {
      while (rs.next()) { 
        users.add(constructUserFromResultSet(rs));
      }

    } catch (SQLException e) {
      logger.error("Error retrieving all users: {}", e.getMessage());
    }
    return users;
  }

  /**
   * Retrieves a user from the local database by their ID.
   *
   * @param id the ID of the user to retrieve
   * @return the user with the given ID, or null if it does not exist
   */
  public static User getUserById(UUID id) {
    User user = null;

    try (
        Connection connection = h2database.getConnection();
        PreparedStatement findUser = connection.prepareStatement("SELECT * FROM \"user\""
            + " WHERE userId = ?")
    ) {
      findUser.setString(1, id.toString());

      try (ResultSet rs = findUser.executeQuery()) {
        if (rs.next()) {
          user = constructUserFromResultSet(rs);
        }
      }

    } catch (SQLException e) {
      logger.error("Error retrieving user with ID {}: {}", id, e.getMessage());
    }
    return user;
  }

  /**
   * Retrieves a user from the local database by their email.
   *
   * @param email the email of the user to retrieve
   * @return the user with the given email, or null if it does not exist
   */
  public static User getUserByEmail(String email) {
    User user = null;

    try (
        Connection connection = h2database.getConnection();
        PreparedStatement findUser = connection.prepareStatement("SELECT * FROM \"user\" "
            + "WHERE email = ?")
    ) {
      findUser.setString(1, email);

      try (ResultSet rs = findUser.executeQuery()) {
        if (rs.next()) {
          user = constructUserFromResultSet(rs);
        }
      }

    } catch (SQLException e) {
      logger.error("Error retrieving user with email {}: {}", email, e.getMessage());
    }
    return user;
  }

  /**
   * Deletes a user from the local database with the given ID.
   *
   * @param userId the ID of the user to delete
   */
  public static void deleteUser(UUID userId) {
    String query = "DELETE FROM user WHERE userId = ?";
    try (
        Connection connection = h2database.getConnection();
        PreparedStatement deleteUser = connection.prepareStatement(query)
    ) {
      deleteUser.setString(1, userId.toString());
      deleteUser.executeUpdate();
      logger.debug("Deleted user with ID: {}", userId);

    } catch (SQLException e) {
      logger.error("Error deleting user with ID {}: {}", userId, e.getMessage());
    }
  }

  /**
   * Retrieves all assignments from the local database.
   *
   * @return a list of all assignments in the local database
   */
  public static List<Pair<UUID, UUID>> getAllAssignments() {
    List<Pair<UUID, UUID>> assignmentsList = new ArrayList<>();

    try (
        Connection connection = h2database.getConnection();
        PreparedStatement getAssignees = connection.prepareStatement("SELECT * FROM "
            + "task_assignees");
        ResultSet rs = getAssignees.executeQuery()
    ) {
      while (rs.next()) {
        UUID taskId = UUID.fromString(rs.getString("taskId"));
        UUID userId = UUID.fromString(rs.getString("userId"));
        assignmentsList.add(new Pair<>(taskId, userId));
      }

    } catch (SQLException e) {
      logger.error("Error retrieving task assignments: {}", e.getMessage());
    }
    return assignmentsList;
  }

  /**
   * Checks if an assignment exists in the local database for a given task and user.
   *
   * @param taskId the ID of the task
   * @param userId the ID of the user
   * @return true if the assignment exists, false otherwise
   */
  public static boolean assignmentExists(UUID taskId, UUID userId) {
    String query = "SELECT 1 FROM task_assignees WHERE taskId = ? AND userId = ?";
    try (
        Connection conn = h2database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query)
    ) {
      stmt.setString(1, taskId.toString());
      stmt.setString(2, userId.toString());

      try (ResultSet rs = stmt.executeQuery()) {
        return rs.next();
      }

    } catch (SQLException e) {
      logger.error("Error checking assignment existence - Task: {}, User: {}: {}", 
          taskId, userId, e.getMessage());
      return false;
    }
  }

  /**
   * Retrieves all changes for a given task from the local database.
   *
   * @param taskId the ID of the task
   * @return a list of all changes for the given task
   */
  public static List<TaskChange> getTaskChanges(UUID taskId) {
    List<TaskChange> changes = new ArrayList<>();

    try (
        Connection connection = h2database.getConnection();
        PreparedStatement selectChanges = connection.prepareStatement(
            "SELECT * FROM task_changelog WHERE taskId = ? ORDER BY changedAt DESC")
    ) {
      selectChanges.setString(1, taskId.toString());

      try (ResultSet rs = selectChanges.executeQuery()) {
        while (rs.next()) {
          changes.add(constructTaskChangeFromResultSet(rs));
        }
      }

    } catch (SQLException e) {
      logger.error("Error retrieving changes for task {}: {}", taskId, e.getMessage());
    }
    return changes;
  }

  /**
   * Constructs a TaskChange object from a ResultSet.
   *
   * @param resultSet the ResultSet to construct the TaskChange from
   * @return the constructed TaskChange object
   * @throws SQLException if an error occurs while constructing the TaskChange
   */
  private static TaskChange constructTaskChangeFromResultSet(ResultSet resultSet) 
      throws SQLException {
    if (resultSet == null) {
      logger.debug("Null ResultSet provided to constructTaskChangeFromResultSet");
      return null;
    }

    UUID id = UUID.fromString(resultSet.getString("id"));
    UUID taskId = UUID.fromString(resultSet.getString("taskId"));
    String description = resultSet.getString("description");
    UUID changedBy = UUID.fromString(resultSet.getString("changedBy"));
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    LocalDateTime changedAt = LocalDateTime.parse(resultSet.getString("changedAt"), formatter);

    return new TaskChange(id, taskId, description, changedBy, changedAt);
  }
}
