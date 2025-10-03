package no.ntnu.idatx1005.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import no.ntnu.idatx1005.model.task.Task;
import no.ntnu.idatx1005.model.user.User;
import no.ntnu.idatx1005.observer.ButtonClickObserver;
import no.ntnu.idatx1005.observer.TaskEventManager;
import no.ntnu.idatx1005.storage.H2Manager;
import no.ntnu.idatx1005.view.content.TasksView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h3>Tasks View Controller</h3>
 *
 * <p>This class is responsible for handling the tasks view and the task-related events.
 * It extends the {@link BaseViewController} class and implements the {@link ButtonClickObserver} 
 * interface.
 *
 * @see BaseViewController
 * @see ButtonClickObserver
 * @author William Holtsdalen
 * @since V1.1.0
 */
public class TasksViewController extends BaseViewController implements ButtonClickObserver {
  private static final Logger logger = LoggerFactory.getLogger(TasksViewController.class);
  private final TasksView tasksView;

  /**
   * Constructs a new TasksViewController. 
   *
   * @param taskManager the task event manager
   * @param tasksView the tasks view
   */
  public TasksViewController(TaskEventManager taskManager, TasksView tasksView) {
    super(taskManager);
    this.tasksView = tasksView;
    
    initializeView();
  }

  /**
   * Initializes the tasks view, and sets up the callbacks for task-clicks 
   * and for formatting assignee names.
   */
  private void initializeView() {
    logger.debug("Setting up TasksView callbacks");
    tasksView.setTaskClickCallback(this::handleTaskClick);
    tasksView.setFormatAssigneeNames(this::formatAssigneeNames);
    refreshTasks();
  }

  /**
   * Handles the event of a task being clicked.
   *
   * @param task the task
   */
  private void handleTaskClick(Task task) {
    if (task != null) {
      logger.debug("Task clicked: {}", task.getName());
      tasksView.notifyObserversWithTask("edit_task", task);
    }
  }

  /**
   * Refreshes the tasks.
   */
  public void refreshTasks() {
    ObservableList<Task> todaysTasks = fetchTasks(H2Manager.getTasksDueToday().stream().filter(
        task -> task.getAssignedUserIds().contains(super.taskManager.getLoggedInUser().getId()))
        .toList());
    ObservableList<Task> weeklyTasks = fetchTasks(H2Manager.getTasksDueThisWeek().stream().filter(
        task -> task.getAssignedUserIds().contains(super.taskManager.getLoggedInUser().getId()))
        .toList());
    ObservableList<Task> allTasks = fetchTasks(H2Manager.getAllTasks());
    ObservableList<Task> completedTasks = fetchTasks(H2Manager.getCompletedTasks());
    ObservableList<Task> openTasks = fetchTasks(H2Manager.getOpenTasks());
    tasksView.refreshTasksTable(todaysTasks, weeklyTasks, allTasks, completedTasks, openTasks);
    logger.debug("Task lists refreshed - Today: {}, Week: {}, All: {}, Completed: {}, Open: {}", 
        todaysTasks.size(), weeklyTasks.size(), allTasks.size(), completedTasks.size(), 
        openTasks.size());
  }

  /**
   * Fetches and sorts the tasks. The tasks are sorted by due date, from earliest to latest.
   *
   * @param tasks the tasks to fetch and sort
   * @return a list of sorted tasks 
   */
  private ObservableList<Task> fetchTasks(List<Task> tasks) {
    logger.trace("Fetching and sorting {} tasks", tasks.size());
    List<Task> sortedTasks = new ArrayList<>(tasks);
    sortedTasks.sort(Comparator.comparing(Task::getDueDate));
    return FXCollections.observableArrayList(sortedTasks);
  }

  /**
   * Formats the assignee names for a task from its assigned user IDs. The names are formatted as a 
   * comma-separated list of first names. 
   *
   * @param task the task to format assignee names for
   * @return a formatted string of assignee names or an empty string if the task has no assignees
   */
  public String formatAssigneeNames(Task task) {
    logger.trace("Formatting assignee names for task: {}", task.getName());
    if (task.getAssignedUserIds().isEmpty()) {
      return "";
    }
    StringBuilder assigneeNames = new StringBuilder();
    for (UUID userId : task.getAssignedUserIds()) {
      if (!assigneeNames.isEmpty()) {
        assigneeNames.append(", ");
      }
      User user = H2Manager.getUserById(userId);
      assigneeNames.append(user.getFirstName());
    }
    return assigneeNames.toString();
  }

  /**
   * Handles the event of a button being clicked.
   *
   * @param buttonId the button id
   */
  @Override
  public void onButtonClicked(String buttonId) {
    if (buttonId.equals("refresh_cache")) {
      handleCacheRefreshed();
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
    // Not needed
  }

  /**
   * Handles the event of a task being created.
   *
   * @param task the task
   */
  @Override
  protected void handleTaskCreated(Task task) {
    refreshTasks();
  }

  /**
   * Handles the event of a task being updated.
   *
   * @param task the task
   */
  @Override
  protected void handleTaskUpdated(Task task) {
    refreshTasks();
  }

  /**
   * Handles the event of a task being deleted.
   *
   * @param task the task
   */
  @Override
  protected void handleTaskDeleted(Task task) {
    refreshTasks();
  }

  /**
   * Handles the event of a task being assigned to a user.
   *
   * @param task the task
   * @param user the user
   */
  @Override
  protected void handleTaskAssigned(Task task, User user) {
    refreshTasks();
  }

  /**
   * Handles the event of a task being completed.
   *
   * @param task the task
   */
  @Override
  protected void handleTaskCompleted(Task task) {
    refreshTasks();
  }

  /**
   * Handles the event of the cache being refreshed.
   */
  @Override
  protected void handleCacheRefreshed() {
    refreshTasks();
  }
} 