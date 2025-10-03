package no.ntnu.idatx1005.service;

import static no.ntnu.idatx1005.service.DatabaseUtils.generateSalt;
import static no.ntnu.idatx1005.service.DatabaseUtils.hashPassword;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import no.ntnu.idatx1005.dao.AssigneesDao;
import no.ntnu.idatx1005.dao.TaskChangeDao;
import no.ntnu.idatx1005.dao.TaskDao;
import no.ntnu.idatx1005.dao.UserDao;
import no.ntnu.idatx1005.dao.exceptions.TaskDoesNotExistException;
import no.ntnu.idatx1005.dao.exceptions.UserDoesNotExistException;
import no.ntnu.idatx1005.dao.exceptions.UserWithEmailAlreadyExistsException;
import no.ntnu.idatx1005.database.DbConnectionProvider;
import no.ntnu.idatx1005.model.task.Priority;
import no.ntnu.idatx1005.model.task.Size;
import no.ntnu.idatx1005.model.task.Task;
import no.ntnu.idatx1005.model.task.TaskChange;
import no.ntnu.idatx1005.model.user.User;
import no.ntnu.idatx1005.storage.H2Manager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h3>Service class for database operations.</h3>
 *
 * <p>This class provides methods for accessing and manipulating data in the database. It follows
 * the Singleton pattern to ensure only one instance of the class exists.
 *
 * @author William Holtsdalen
 * @author Hector Mendana Morales
 * @since V0.1.0
 */
public class DatabaseService {
  private static final Logger logger = LoggerFactory.getLogger(DatabaseService.class);
  private static DatabaseService instance;
  private final AssigneesDao assigneesDao;
  private final TaskDao taskDao;
  private final UserDao userDao;
  private final TaskChangeDao taskChangeDao;

  /**
   * Constructs a new DatabaseService instance.
   */
  private DatabaseService() {
    logger.info("Initializing DatabaseService");
    DbConnectionProvider remoteConnectionProvider = DbConnectionProvider.instance();
    this.assigneesDao = new AssigneesDao(remoteConnectionProvider);
    this.taskDao = new TaskDao(remoteConnectionProvider, assigneesDao);
    this.userDao = new UserDao(remoteConnectionProvider);
    this.taskChangeDao = new TaskChangeDao(remoteConnectionProvider);
    logger.info("DatabaseService initialization completed");
  }

  /**
   * Gets the singleton instance of the DatabaseService.
   *
   * @return The DatabaseService instance
   */
  public static synchronized DatabaseService getInstance() {
    if (instance == null) {
      logger.debug("Creating new DatabaseService instance");
      instance = new DatabaseService();
    }
    return instance;
  }

  /**
   * Retrieves all unassigned tasks, sorted by priority and size.
   *
   * @return a list of unassigned tasks
   */
  public List<Task> getUnassignedTasksBySizeAndPriority() {
    List<Task> tasks = new ArrayList<>(H2Manager.getOpenTasks());
    tasks.sort(Comparator
        .<Task>comparingInt(task -> task.getPriority().ordinal())
        .reversed()
        .thenComparingInt(task -> task.getSize().ordinal())
        .reversed()
    );
    return tasks.stream().filter(task -> task.getAssignedUserIds().isEmpty()).toList();
  }


  /**
   * Retrieves all tasks that were completed today.
   *
   * @return a list of tasks that were completed today
   */
  public List<Task> getTasksCompletedToday() {
    LocalDate today = LocalDate.now();
    return H2Manager.getAllTasks().stream().filter(task ->
            task.getDueDate().toLocalDate().isEqual(today))
        .toList();
  } // TODO: Must update this to use future completedDate field in Task, and not due dates.

  /**
   * Retrieves all tasks that were completed this week.
   *
   * @return a list of tasks that were completed this week.
   */
  public List<Task> getTasksCompletedThisWeek() {
    LocalDate today = LocalDate.now();
    LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

    return H2Manager.getAllTasks().stream().filter(task ->
            task.getDueDate().toLocalDate().isAfter(startOfWeek)
                && task.getDueDate().toLocalDate().isBefore(endOfWeek))
        .toList();
  } // TODO: Must update this to use future completedDate field in Task, and not due dates.


  /**
   * Retrieves the list of tasks that are assigned to the given user.
   *
   * @param user the user to find assigned tasks for.
   * @return a list of tasks that are assigned to the user.
   */
  public List<Task> getTasksAssignedToUser(User user) {
    List<Task> assignedTasks = new ArrayList<>();
    H2Manager.getAllAssignments().forEach(pair -> {
      if (H2Manager.getUserById(pair.getValue()).equals(user)) {
        assignedTasks.add(H2Manager.getTaskById(pair.getKey()));
      }
    });
    return assignedTasks;
  }

  /**
   * Adds a new task to the database with the given parameters, and updates the cache afterwards.
   *
   * @param title the title of the task
   * @param description the description of the task
   * @param dueDate the due date of the task
   * @param priority the priority of the task
   * @param size the size of the task
   * @param assignedUserIds the list of user IDs assigned to the task
   */
  public void addTask(String title, String description, LocalDateTime dueDate, Priority priority,
      Size size, List<UUID> assignedUserIds) {
    logger.info("Adding new task: {}", title);
    UUID taskId = UUID.randomUUID();
    boolean completed = false;
    LocalDateTime createdDate = LocalDateTime.now();
    Task task = new Task(taskId, completed, title, description, createdDate, dueDate, priority,
        size);
    for (UUID userId : assignedUserIds) {
      User user = H2Manager.getUserById(userId);
      if (user != null) {
        task.addAssignedUserId(user.getId());
        logger.debug("Assigned user {} to task {}", user.getEmail(), title);
      }
    }
    taskDao.addTask(task);
    H2Manager.updateLocalStorage();
    logger.info("Task {} added successfully", title);
  }

  /**
   * Logs a change to a task in the changelog.
   *
   * @param taskId the ID of the task that was changed
   * @param description the description of the change
   * @param changedBy the ID of the user who made the change
   */
  public void logTaskChange(UUID taskId, String description, UUID changedBy) {
    TaskChange change = new TaskChange(
        UUID.randomUUID(),
        taskId,
        description,
        changedBy,
        LocalDateTime.now()
    );
    taskChangeDao.addChange(change);
  }

  /**
   * Retrieves all changes made to a specific task.
   *
   * @param taskId the ID of the task to get changes for
   * @return a list of changes made to the task
   */
  public List<TaskChange> getTaskChanges(UUID taskId) {
    return taskChangeDao.getChangesByTaskId(taskId);
  }

  /**
   * Updates the task in the database with the given task's id, with the properties of the given
   * task, and updates the cache afterwards. Also logs the change in the changelog.
   *
   * @param task the task to update
   * @param changedBy the ID of the user who made the change
   */
  public void updateTask(Task task, UUID changedBy) {
    logger.info("Updating task: {}", task.getName());
    Task storedTask = H2Manager.getTaskById(task.getId());
    if (storedTask == null) {
      logger.error("Task with ID {} not found", task.getId());
      throw new TaskDoesNotExistException("Task with ID " + task.getId() + " not found.");
    }

    // Check for changes in each field and log them
    if (!storedTask.getName().equals(task.getName())) {
      logTaskChange(task.getId(),
          String.format("Name was changed from '%s' to '%s'",
              storedTask.getName(), task.getName()),
          changedBy);
    }

    if (!storedTask.getDescription().equals(task.getDescription())) {
      logTaskChange(task.getId(),
          String.format("Description was updated from '%s' to '%s'",
              storedTask.getDescription(), task.getDescription()),
          changedBy);
    }

    if (!storedTask.getDueDate().equals(task.getDueDate())) {
      logTaskChange(task.getId(),
          String.format("Due date was changed from %s to %s",
              storedTask.getDueDate().format(java.time.format.DateTimeFormatter
                  .ofPattern("dd/MM/yy")),
              task.getDueDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yy"))),
          changedBy);
    }

    if (storedTask.getPriority() != task.getPriority()) {
      logTaskChange(task.getId(),
          String.format("Priority was changed from %s to %s",
              storedTask.getPriority(), task.getPriority()),
          changedBy);
    }

    if (storedTask.getSize() != task.getSize()) {
      logTaskChange(task.getId(),
          String.format("Size was changed from %s to %s",
              storedTask.getSize(), task.getSize()),
          changedBy);
    }

    if (storedTask.isCompleted() != task.isCompleted()) {
      logTaskChange(task.getId(),
          task.isCompleted() ? "Task was completed" : "Task was restored",
          changedBy);
    }

    // Check for changes in assigned users
    List<UUID> oldAssignees = new ArrayList<>(storedTask.getAssignedUserIds());
    List<UUID> newAssignees = new ArrayList<>(task.getAssignedUserIds());

    for (UUID userId : newAssignees) {
      if (!oldAssignees.contains(userId)) {
        User user = H2Manager.getUserById(userId);
        if (user != null) {
          logTaskChange(task.getId(),
              String.format("%s %s was added as an assignee",
                  user.getFirstName(), user.getLastName()),
              changedBy);
        }
      }
    }

    for (UUID userId : oldAssignees) {
      if (!newAssignees.contains(userId)) {
        User user = H2Manager.getUserById(userId);
        if (user != null) {
          logTaskChange(task.getId(),
              String.format("%s %s was removed from this task",
                  user.getFirstName(), user.getLastName()),
              changedBy);
        }
      }
    }

    taskDao.updateTask(task);
    logger.info("Task {} updated successfully", task.getName());
    H2Manager.updateLocalStorage();
  }

  /**
   * Deletes the given task from the database.
   *
   * @param task the task to delete
   */
  public void deleteTask(Task task) {
    taskDao.deleteTask(task);
    logger.info("Task {} deleted successfully", task.getName());
    H2Manager.updateLocalStorage();

  }

  /**
   * Retrieves all users in the database and returns them as a list, sorted by their available
   * capacity in descending order.
   *
   * @return a list of all users in the database, sorted by their available capacity
   */
  public List<User> getAllUsersByCapacity() {
    List<User> users = new ArrayList<>(H2Manager.getAllUsers());
    users.sort((user1, user2) -> {
      int user1Capacity = getUserAvailableCapacity(user1);
      int user2Capacity = getUserAvailableCapacity(user2);
      return Integer.compare(user2Capacity, user1Capacity);
    });
    return users;
  }


  /**
   * Retrieves the list of users that are assigned to the given task.
   *
   * @param task the task to find assigned users for.
   * @return a list of users that are assigned to the task.
   */
  public List<User> getTaskAssignees(Task task) {
    List<User> assignees = new ArrayList<>();
    task.getAssignedUserIds().forEach(userId -> assignees.add(H2Manager.getUserById(userId)));
    return assignees;
  }

  /**
   * Retrieves the workload as an integer for the given user. The workload is calculated by
   * summarizing the sizes of all tasks assigned to the user.
   *
   * @param user the user to retrieve the workload for.
   * @return the integer representing the user's workload.
   */
  public int getUserAvailableCapacity(User user) {
    int workload = H2Manager.getAllTasks().stream()
        .filter(task -> task.getAssignedUserIds().contains(user.getId())
            && !task.isCompleted()).mapToInt(task -> task.getSize().getValue()).sum();
    return user.getTaskCapacity() - workload;
  }

  /**
   * Retrieves the workload for the given user.
   *
   * @param user the user to retrieve the workload for.
   * @return the integer representing the user's workload.
   */
  public int getUserWorkload(User user) {
    return getTasksAssignedToUser(user)
        .stream().filter(task -> !task.isCompleted()).mapToInt(task ->
            task.getSize().getValue()).sum();
  }

  /**
   * Adds a new user to the database with the given credentials, and updates the cache afterwards.
   *
   * @param firstName the user's first name
   * @param lastName the user's last name
   * @param email the user's email
   * @param passwordText the user's password in plain text for salting and hashing
   * @return true if the user was successfully added, false otherwise
   */
  public boolean addUser(String firstName, String lastName, String email, String passwordText) {
    try {
      UUID userId = UUID.randomUUID();
      byte[] salt = generateSalt();
      byte[] passwordHash = hashPassword(passwordText, salt);
      int workloadCapacity = 100;
      boolean isSick = false;
      User user = new User(userId, firstName, lastName, email, passwordHash, salt,
          workloadCapacity, isSick);
      userDao.addUser(user);
    } catch (UserWithEmailAlreadyExistsException e) {
      logger.error("User with email {} already exists", email);
      return false;
    }
    H2Manager.updateLocalStorage();
    return true;
  }

  /**
   * Assigns the given task to the given user and updates the database accordingly.
   *
   * @param task the task to assign
   * @param user the user to assign the task to
   * @param changedBy the ID of the user who made the change
   */
  public void assignTaskToUser(Task task, User user, UUID changedBy) {
    Task storedTask = H2Manager.getTaskById(task.getId());
    if (storedTask != null) {
      storedTask.addAssignedUserId(user.getId());
      updateTask(storedTask, changedBy);
      logger.info("Task {} assigned to user {} successfully", task.getName(), user.getEmail());
    }
  }

  /**
   * Completes the given task and updates it in the database.
   *
   * @param task the task to complete
   * @param changedBy the ID of the user who made the change
   */
  public void completeTask(Task task, UUID changedBy) {
    Task storedTask = H2Manager.getTaskById(task.getId());
    if (storedTask != null) {
      storedTask.setCompleted(true);
      updateTask(storedTask, changedBy);
      logger.info("Task {} completed successfully", task.getName());
    }
  }

  /**
   * Updates the user in the database with the given user's id, with the properties of the given
   * user, and updates the cache afterwards.
   *
   * @param user the user to update
   */
  public void updateUser(User user) {
    userDao.updateUser(user);
    H2Manager.updateLocalStorage();
    logger.info("User {} updated successfully", user.getEmail());
  }

  /**
   * Removes the given user from the database.
   *
   * @param user the user to remove
   */
  public void removeUser(User user) {
    userDao.removeUser(user);
    H2Manager.updateLocalStorage();
    logger.info("User {} removed successfully", user.getEmail());
  }

  /**
   * Validates if the provided password matches the user's current password.
   *
   * @param user the user to validate password for
   * @param password the password to validate
   * @return true if password matches, false otherwise
   */
  public boolean validatePassword(User user, String password) {
    byte[] salt = user.getSalt();
    byte[] hashedPassword = hashPassword(password, salt);
    return Arrays.equals(hashedPassword, user.getPasswordHash());
  }

  /**
   * Updates the user's password in the database.
   *
   * @param user the user to update password for
   * @param newPassword the new password
   * @throws UserDoesNotExistException if user does not exist
   */
  public void updateUserPassword(User user, String newPassword) throws UserDoesNotExistException {
    if (!userExists(user.getId())) {
      throw new UserDoesNotExistException("User does not exist");
    }
    byte[] salt = generateSalt();
    byte[] hashedPassword = hashPassword(newPassword, salt);
    user.setPasswordHash(hashedPassword);
    user.setSalt(salt);
    updateUser(user);
    logger.info("User {} password updated successfully", user.getEmail());
  }

  /**
   * Checks if a user with the given ID exists in the database.
   *
   * @param userId the ID of the user to check
   * @return true if user exists, false otherwise
   */
  private boolean userExists(UUID userId) {
    return H2Manager.getAllUsers().stream().anyMatch(user -> user.getId().equals(userId));
  }

  /**
   * Authenticates a user with the given email and password.
   *
   * @param email the email of the user to authenticate
   * @param password the password of the user to authenticate
   * @return the authenticated user, or null if the user could not be authenticated.
   */
  public User authenticateUser(String email, String password) {
    logger.debug("Attempting to authenticate user: {}", email);
    User user = H2Manager.getUserByEmail(email);
    if (user != null && validatePassword(user, password)) {
      logger.info("User {} authenticated successfully", email);
      return user;
    }
    logger.warn("Authentication failed for user: {}", email);
    return null;
  }
}