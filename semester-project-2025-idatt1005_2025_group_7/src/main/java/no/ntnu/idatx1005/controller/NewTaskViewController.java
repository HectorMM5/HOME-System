package no.ntnu.idatx1005.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import no.ntnu.idatx1005.model.task.Priority;
import no.ntnu.idatx1005.model.task.Size;
import no.ntnu.idatx1005.model.task.Task;
import no.ntnu.idatx1005.model.user.User;
import no.ntnu.idatx1005.observer.ButtonClickObserver;
import no.ntnu.idatx1005.observer.TaskEventManager;
import no.ntnu.idatx1005.view.content.NewTaskView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h3>New Task View Controller</h3>
 *
 * <p>This class is responsible for handling the new task view and the task creation.
 * It extends the {@link BaseViewController} class and implements the {@link ButtonClickObserver}
 * interface.
 *
 * @see BaseViewController
 * @see ButtonClickObserver
 * @author William Holtsdalen
 * @since V1.1.0
 */
public class NewTaskViewController extends BaseViewController implements ButtonClickObserver {
  private static final Logger logger = LoggerFactory.getLogger(NewTaskViewController.class);
  private final NewTaskView newTaskView;

  /**
   * Constructs a new NewTaskViewController.
   *
   * @param taskManager the task event manager
   * @param newTaskView the new task view
   */
  public NewTaskViewController(TaskEventManager taskManager, NewTaskView newTaskView) {
    super(taskManager);
    this.newTaskView = newTaskView;
  }

  /**
   * Handles the creation of a new task.
   *
   * @param name the name of the task
   * @param description the description of the task
   * @param dueDate the due date of the task
   * @param priority the priority of the task
   * @param size the size of the task
   * @param assignedUserIds the list of user ids assigned to the task
   */
  public void handleCreateTask(String name, String description, LocalDateTime dueDate,
      Priority priority, Size size, List<UUID> assignedUserIds) {
    logger.debug("Creating new task - Name: {}, Priority: {}, Size: {}, Due: {}, Assignees: {}",
        name, priority, size, dueDate, assignedUserIds.size());
    taskManager.createTask(name, description, dueDate, priority, size, assignedUserIds);
    logger.info("Task created successfully.");
  }

  /**
   * Handles the event of a button being clicked.
   *
   * @param buttonId the button id
   */
  @Override
  public void onButtonClicked(String buttonId) {
    logger.debug("Button clicked: {}", buttonId);
    if (buttonId.equals("save_new_task")) {
      handleCreateTask(newTaskView.getTaskTitle(), newTaskView.getTaskDescription(),
          newTaskView.getTaskDueDate(), newTaskView.getTaskPriority(), newTaskView.getTaskSize(),
          newTaskView.getTaskAssignedUserIds());
    }
  }

  /**
   * Handles the event of a button being clicked with a task.
   *
   * @param buttonId the button id
   * @param task the task
   */
  @Override
  public void onButtonClickedWithTask(String buttonId, Task task) {
    // Not needed for new task view
  }

  /**
   * Handles the event of a task being created.
   *
   * @param task the task
   */
  @Override
  protected void handleTaskCreated(Task task) {
    newTaskView.resetForm();
    logger.debug("Cleared form field in view");
  }

  /**
   * Handles the event of a task being updated.
   *
   * @param task the task
   */
  @Override
  protected void handleTaskUpdated(Task task) {
    // Not needed for new task view
  }

  /**
   * Handles the event of a task being deleted.
   *
   * @param task the task
   */
  @Override
  protected void handleTaskDeleted(Task task) {
    // Not needed for new task view
  }

  /**
   * Handles the event of a task being assigned to a user.
   *
   * @param task the task
   * @param user the user
   */
  @Override 
  protected void handleTaskAssigned(Task task, User user) {
    // Not needed for new task view
  }

  /**
   * Handles the event of a task being completed.
   *
   * @param task the task
   */
  @Override
  protected void handleTaskCompleted(Task task) {
    // Not needed for new task view
  }

  /**
   * Handles the event of the cache being refreshed.
   */
  @Override
  protected void handleCacheRefreshed() {
    newTaskView.refreshUserAssignments();
  }
} 