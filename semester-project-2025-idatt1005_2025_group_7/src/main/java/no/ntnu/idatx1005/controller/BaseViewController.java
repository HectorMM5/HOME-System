package no.ntnu.idatx1005.controller;

import javafx.application.Platform;
import no.ntnu.idatx1005.model.task.Task;
import no.ntnu.idatx1005.model.user.User;
import no.ntnu.idatx1005.observer.TaskEventManager;
import no.ntnu.idatx1005.observer.TaskEventObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <h3>Base controller class for views that need to observe task events.</h3>
 *
 * <p>This class implements the {@link TaskEventObserver} interface and provides a base 
 * implementation for handling task events.
 *
 * @see TaskEventObserver
 * @author William Holtsdalen
 * @since V1.1.0
 */
public abstract class BaseViewController implements TaskEventObserver {
  private static final Logger logger = LoggerFactory.getLogger(BaseViewController.class);
  protected final TaskEventManager taskManager;

  /**
   * Initializes the controller and adds it to the task event manager.
   *
   * @param taskManager the task event manager to add the controller to.
   */
  protected BaseViewController(TaskEventManager taskManager) {
    logger.debug("Initializing {} and adding to TaskEventManager", this.getClass().getSimpleName());
    this.taskManager = taskManager;
    taskManager.addObserver(this);
  }

  /**
   * Observes task creation events.
   */
  @Override
  public void onTaskCreated(Task task) {
    Platform.runLater(() -> handleTaskCreated(task));
  }

  /**
   * Observes task update events.
   */
  @Override
  public void onTaskUpdated(Task task) {
    Platform.runLater(() -> handleTaskUpdated(task));
  }

  /**
   * Observes task deletion events.
   */
  @Override
  public void onTaskDeleted(Task task) {
    Platform.runLater(() -> handleTaskDeleted(task));
  }

  /**
   * Observes task assignment events.
   *
   * @param task the task that was assigned.
   * @param user the user that was assigned the task.
   */
  @Override
  public void onTaskAssigned(Task task, User user) {
    Platform.runLater(() -> handleTaskAssigned(task, user));
  }

  /**
   * Observes task completion events.
   *
   * @param task the task that was completed.
   */
  @Override
  public void onTaskCompleted(Task task) {
    Platform.runLater(() -> handleTaskCompleted(task));
  }

  /**
   * Observes cache refresh events.
   */
  @Override
  public void onCacheRefreshed() {
    Platform.runLater(this::handleCacheRefreshed);
  }

  /**
   * Handles the event of a task being created.
   *
   * @param task the task that was created.
   */
  protected abstract void handleTaskCreated(Task task);

  /**
   * Handles the event of a task being updated.
   *
   * @param task the task that was updated.
   */
  protected abstract void handleTaskUpdated(Task task);

  /**
   * Handles the event of a task being deleted.
   *
   * @param task the task that was deleted.
   */
  protected abstract void handleTaskDeleted(Task task);

  /**
   * Handles the event of a task being assigned to a user.
   *
   * @param task the task that was assigned.
   * @param user the user that was assigned the task.
   */
  protected abstract void handleTaskAssigned(Task task, User user);

  /**
   * Handles the event of a task being completed.
   *
   * @param task the task that was completed.
   */ 
  protected abstract void handleTaskCompleted(Task task);

  /**
   * Handles the event of the cache being refreshed.
   */
  protected abstract void handleCacheRefreshed();
} 