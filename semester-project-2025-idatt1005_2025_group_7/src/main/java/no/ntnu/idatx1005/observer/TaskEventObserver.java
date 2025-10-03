package no.ntnu.idatx1005.observer;

import no.ntnu.idatx1005.model.task.Task;
import no.ntnu.idatx1005.model.user.User;

/**
 * <h3>TaskEventObserver interface</h3>
 *
 * <p>This interface defines the methods for observing task-related events.
 *
 * @author William Holtsdalen
 * @since V1.1.0
 */
public interface TaskEventObserver {

  /**
   * Called when a task is created.
   *
   * @param task the task that was created
   */
  void onTaskCreated(Task task);

  /**
   * Called when a task is updated.
   *
   * @param task the task that was updated
   */
  void onTaskUpdated(Task task);

  /**
   * Called when a task is deleted.
   *
   * @param task the task that was deleted
   */
  void onTaskDeleted(Task task);

  /**
   * Called when a task is assigned to a user.
   *
   * @param task the task that was assigned
   * @param user the user that the task was assigned to
   */
  void onTaskAssigned(Task task, User user);

  /**
   * Called when a task is completed.
   *
   * @param task the task that was completed
   */
  void onTaskCompleted(Task task);

  /**
   * Called when the cache is refreshed.
   */
  void onCacheRefreshed();
} 