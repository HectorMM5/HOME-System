package no.ntnu.idatx1005.controller;

import java.time.LocalDateTime;
import no.ntnu.idatx1005.model.task.Task;
import no.ntnu.idatx1005.model.user.User;
import no.ntnu.idatx1005.observer.ButtonClickObserver;
import no.ntnu.idatx1005.observer.TaskEventManager;
import no.ntnu.idatx1005.view.content.EditTaskView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h3>Edit Task View Controller</h3>
 *
 * <p>This class is responsible for handling the edit task view and the task updates.
 * It also handles button click events and the task drop event. It extends the 
 * {@link BaseViewController} class and implements the {@link ButtonClickObserver} interface.
 *
 * @see BaseViewController
 * @see ButtonClickObserver
 * @author William Holtsdalen
 * @since V1.1.0
 */
public class EditTaskViewController extends BaseViewController implements ButtonClickObserver {
  private static final Logger logger = LoggerFactory.getLogger(EditTaskViewController.class);
  private final EditTaskView editTaskView;

  /**
   * Constructs a new EditTaskViewController.
   *
   * @param taskManager the task event manager
   * @param editTaskView the edit task view
   */
  public EditTaskViewController(TaskEventManager taskManager, EditTaskView editTaskView) {
    super(taskManager);
    this.editTaskView = editTaskView;
  }

  /**
   * Handles the event of a button being clicked.
   *
   * @param buttonId the button id
   */
  @Override
  public void onButtonClicked(String buttonId) {
    logger.debug("Button clicked: {}", buttonId);
    if (!buttonId.equals("save_task_changes")) {
      return;
    }
    Task updatedTask = new Task(editTaskView.getTaskId(), editTaskView.getTaskCompleted(),
        editTaskView.getTaskTitle(), editTaskView.getTaskDescription(),
        editTaskView.getTaskCreatedDate(), editTaskView.getTaskDueDate(),
        editTaskView.getTaskPriority(), editTaskView.getTaskSize());
      
    if (editTaskView.getTaskCompleted() && editTaskView.getCompletedDate() == null) {
      updatedTask.setCompletedDate(LocalDateTime.now());
    } else if (!editTaskView.getTaskCompleted()) {
      updatedTask.setCompletedDate(null);
    }
    updatedTask.setAssignedUserIds(editTaskView.getTaskAssignedUserIds());
    logger.debug("Saving changes to task: {}", updatedTask.getName());
    taskManager.updateTask(updatedTask);
    editTaskView.setTask(updatedTask);
    editTaskView.refreshView();
    logger.debug("Task changes saved and view refreshed");
  }

  /**
   * Handles the event of a button being clicked with a task object.
   *
   * @param buttonId the button id
   * @param task the task
   */
  @Override
  public void onButtonClickedWithTask(String buttonId, Task task) {
    // Not needed
  }

  /**
   * Handles the event of a task being created.
   *
   * @param task the task
   */
  @Override
  protected void handleTaskCreated(Task task) {
    // Not needed for edit view
  }

  /**
   * Handles the event of a task being updated.
   *
   * @param task the task
   */
  @Override
  protected void handleTaskUpdated(Task task) {
    // Not needed for edit view
  }

  /**
   * Handles the event of a task being deleted.
   *
   * @param task the task
   */
  @Override
  protected void handleTaskDeleted(Task task) {
    // Not needed for edit view
  }

  /**
   * Handles the event of a task being assigned to a user.
   *
   * @param task the task
   * @param user the user
   */
  @Override
  protected void handleTaskAssigned(Task task, User user) {
    // Not needed for edit view
  }

  /**
   * Handles the event of a task being completed.
   *
   * @param task the task
   */
  @Override
  protected void handleTaskCompleted(Task task) {
    // Not needed for edit view
  }

  /**
   * Handles the event of the cache being refreshed.
   */
  @Override
  protected void handleCacheRefreshed() {
    logger.debug("Cache refreshed event received");
    editTaskView.refreshView();
  }
} 