package no.ntnu.idatx1005.observer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import no.ntnu.idatx1005.model.task.Priority;
import no.ntnu.idatx1005.model.task.Size;
import no.ntnu.idatx1005.model.task.Task;
import no.ntnu.idatx1005.model.user.User;
import no.ntnu.idatx1005.service.DatabaseService;
import no.ntnu.idatx1005.storage.H2Manager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h3>TaskEventManager class</h3>
 *
 * <p>Manages task-related events and notifications in the application.
 * This class coordinates between the data layer (DatabaseService) and UI components
 * by providing event notifications when task-related changes occur.
 *
 * @author William Holtsdalen
 * @since V1.1.0
 */
public class TaskEventManager implements TaskEventSubject {
  private static final Logger logger = LoggerFactory.getLogger(TaskEventManager.class);
  private final DatabaseService databaseService;
  private final List<TaskEventObserver> observers;
  private User loggedInUser;

  /**
   * Constructs a new TaskEventManager instance.
   *
   * @param databaseService the database service instance to use for data access
   */
  public TaskEventManager(DatabaseService databaseService) {
    logger.debug("Initializing TaskEventManager");
    this.databaseService = databaseService;
    this.observers = new ArrayList<>();
  }

  /**
   * Adds an observer to the list of observers if its not already in the list.
   *
   * @param observer the observer implementation to add
   */
  @Override
  public void addObserver(TaskEventObserver observer) {
    if (!observers.contains(observer)) {
      logger.trace("Adding observer: {}", observer.getClass().getSimpleName());
      observers.add(observer);
    }
  }

  /**
   * Removes the given observer from the list of observers.
   *
   * @param observer the observer implementation to remove
   */
  @Override
  public void removeObserver(TaskEventObserver observer) {
    logger.trace("Removing observer: {}", observer.getClass().getSimpleName());
    observers.remove(observer);
  }

  /**
   * Method for notifying all observers of task-related events. This method is not directly used
   * as we have specific methods for each event type.
   */
  @Override
  public void notifyObservers() {
    /* This method is not directly used for now as there are specific notification methods for each
    * event type. */
  }

  /**
   * Sets the logged in user to the given user.
   *
   * @param user the user to set as logged in
   */
  public void setLoggedInUser(User user) {
    logger.debug("Setting logged in user: {} {}", user.getFirstName(), user.getLastName());
    this.loggedInUser = user;
  }

  /**
   * Returns the logged in user.
   *
   * @return the logged in user
   */
  public User getLoggedInUser() {
    return loggedInUser;
  }

  /**
   * Creates a new task in the database with the given parameters, by calling the appropriate method
   * in the {@link DatabaseService} class. It then notifies all observers of the task creation.
   *
   * @param title the title of the task to create
   * @param description the description of the task to create
   * @param dueDate the due date of the task to create
   * @param priority the priority of the task to create
   * @param size the size of the task to create
   * @param assignedUserIds the list of assigned user ids of the task to create
   */
  public void createTask(String title, String description, LocalDateTime dueDate, Priority priority,
      Size size, List<UUID> assignedUserIds) {
    logger.info("Creating new task - Title: {}, Priority: {}, Size: {}", title, priority, size);
    databaseService.addTask(title, description, dueDate, priority, size, assignedUserIds);

    // Get the created task from the database and notify observers
    H2Manager.getAllTasks().stream()
        .filter(t -> t.getName().equals(title)).findFirst()
        .ifPresent(task -> {
          logger.debug("Task created successfully with ID: {}", task.getId());
          notifyTaskCreated(task);
        });
  }

  /**
   * Updates the task in the database with the given task's id, by calling the appropriate method
   * in the {@link DatabaseService} class. It then notifies all observers of the task update.
   *
   * @param task the task to update
   */
  public void updateTask(Task task) {
    logger.info("Updating task: {} (ID: {})", task.getName(), task.getId());
    databaseService.updateTask(task, loggedInUser.getId());
    logger.debug("Task updated successfully");
    notifyTaskUpdated(task);
  }

  /**
   * Deletes the given task from the database, by calling the appropriate method
   * in the {@link DatabaseService} class. It then notifies all observers of the task deletion.
   *
   * @param task the task to delete
   */
  public void deleteTask(Task task) {
    logger.info("Deleting task: {} (ID: {})", task.getName(), task.getId());
    databaseService.deleteTask(task);
    logger.debug("Task deleted successfully");
    notifyTaskDeleted(task);
  }

  /**
   * Assigns the given task to the given user, by calling the appropriate method
   * in the {@link DatabaseService} class. It then notifies all observers of the task assignment.
   *
   * @param task the task to assign
   * @param user the user to assign the task to
   */
  public void assignTask(Task task, User user) {
    logger.info("Assigning task {} to user {} {}", task.getName(), user.getFirstName(),
        user.getLastName());
    databaseService.assignTaskToUser(task, user, loggedInUser.getId());
    logger.debug("Task assigned successfully");
    notifyTaskAssigned(task, user);
  }

  /**
   * Marks the given task as completed, by calling the appropriate method
   * in the {@link DatabaseService} class. It then notifies all observers of the task completion.
   *
   * @param task the task to mark as completed
   */
  public void completeTask(Task task) {
    logger.info("Marking task as completed: {} (ID: {})", task.getName(), task.getId());
    databaseService.completeTask(task, loggedInUser.getId());
    logger.debug("Task marked as completed successfully");
    notifyTaskCompleted(task);
  }

  /**
   * Refreshes the cache, by calling the appropriate method
   * in the {@link H2Manager} class. It then notifies all observers of the cache refresh.
   */
  public void refreshCache() {
    H2Manager.updateLocalStorage();
    notifyCacheRefreshed();
  }

  /**
   * Notifies all observers of the task creation.
   *
   * @param task the task to notify observers of
   */
  private void notifyTaskCreated(Task task) {
    logger.debug("Notifying observers of task creation: {}", task.getName());
    new ArrayList<>(observers).forEach(o -> {
      try {
        o.onTaskCreated(task);
      } catch (Exception e) {
        logger.error("Error notifying observer {} of task creation: {}", 
            o.getClass().getSimpleName(), e.getMessage());
      }
    });
  }

  /**
   * Notifies all observers of the task update.
   *
   * @param task the task to notify observers of
   */
  private void notifyTaskUpdated(Task task) {
    logger.debug("Notifying observers of task update: {}", task.getName());
    new ArrayList<>(observers).forEach(o -> {
      try {
        o.onTaskUpdated(task);
      } catch (Exception e) {
        logger.error("Error notifying observer {} of task update: {}", 
            o.getClass().getSimpleName(), e.getMessage());
      }
    });
  }

  /**
   * Notifies all observers of the task deletion.
   *
   * @param task the task to notify observers of
   */
  private void notifyTaskDeleted(Task task) {
    logger.debug("Notifying observers of task deletion: {}", task.getName());
    new ArrayList<>(observers).forEach(o -> {
      try {
        o.onTaskDeleted(task);
      } catch (Exception e) {
        logger.error("Error notifying observer {} of task deletion: {}", 
            o.getClass().getSimpleName(), e.getMessage());
      }
    });
  }

  /**
   * Notifies all observers of the task assignment.
   *
   * @param task the task to notify observers of
   * @param user the user to notify observers of
   */
  private void notifyTaskAssigned(Task task, User user) {
    logger.debug("Notifying observers of task assignment: {} to {}", task.getName(), user
        .getFirstName());
    new ArrayList<>(observers).forEach(o -> {
      try {
        o.onTaskAssigned(task, user);
      } catch (Exception e) {
        logger.error("Error notifying observer {} of task assignment: {}", 
            o.getClass().getSimpleName(), e.getMessage());
      }
    });
  }

  /**
   * Notifies all observers of the task completion.
   *
   * @param task the task to notify observers of
   */
  private void notifyTaskCompleted(Task task) {
    logger.debug("Notifying observers of task completion: {}", task.getName());
    new ArrayList<>(observers).forEach(o -> {
      try {
        o.onTaskCompleted(task);
      } catch (Exception e) {
        logger.error("Error notifying observer {} of task completion: {}", 
            o.getClass().getSimpleName(), e.getMessage());
      }
    });
  }

  /**
   * Notifies all observers of the cache refresh.
   */
  private void notifyCacheRefreshed() {
    logger.debug("Notifying observers of cache refresh");
    new ArrayList<>(observers).forEach(o -> {
      try {
        o.onCacheRefreshed();
      } catch (Exception e) {
        logger.error("Error notifying observer {} of cache refresh: {}", 
            o.getClass().getSimpleName(), e.getMessage());
      }
    });
  }
}