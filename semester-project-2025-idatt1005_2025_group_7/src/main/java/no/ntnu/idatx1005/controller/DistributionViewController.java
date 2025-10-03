package no.ntnu.idatx1005.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import javafx.util.Pair;
import no.ntnu.idatx1005.model.task.Task;
import no.ntnu.idatx1005.model.user.User;
import no.ntnu.idatx1005.observer.ButtonClickObserver;
import no.ntnu.idatx1005.observer.TaskEventManager;
import no.ntnu.idatx1005.service.DatabaseService;
import no.ntnu.idatx1005.storage.H2Manager;
import no.ntnu.idatx1005.view.content.DistributionView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h3>Distribution View Controller</h3>
 *
 * <p>This class is responsible for handling the distribution view and the auto-distribution 
 * process. It also handles the task drop event and displays the appropriate dialogs.
 * It extends the {@link BaseViewController} class and implements the {@link ButtonClickObserver} 
 * interface.
 *
 * @see BaseViewController
 * @see ButtonClickObserver
 * @author William Holtsdalen
 * @since V1.1.0
 */
public class DistributionViewController extends BaseViewController implements ButtonClickObserver {
  private static final Logger logger = LoggerFactory.getLogger(DistributionViewController.class);
  private final DistributionView distributionView;
  private final DatabaseService dbService;

  /**
   * Constructs a new DistributionViewController.
   *
   * @param taskManager the task event manager
   * @param distributionView the distribution view
   */
  public DistributionViewController(TaskEventManager taskManager, DistributionView 
      distributionView) {
    super(taskManager);
    this.distributionView = distributionView;
    this.dbService  = DatabaseService.getInstance();

    initializeView();
  }

  private void initializeView() {
    logger.debug("Setting up DistributionView callbacks");
    distributionView.setTaskDropCallback(this::handleTaskDrop);
  }

  /**
   * Handles the event of a task being dropped onto a user.
   *
   * @param user the user
   * @param taskid the task id
   */
  public void handleTaskDrop(User user, String taskid) {
    logger.debug("Handling task drop - User: {}, Task ID: {}", user.getEmail(), taskid);
    Task task = H2Manager.getTaskById(UUID.fromString(taskid));
    int numericalTaskSize = task.getSize().getValue();
    
    if (dbService.getUserAvailableCapacity(user) < numericalTaskSize) {
      logger.warn("Invalid task assignment - User: {}, Task: {}, Required capacity: {}, "
              + "Available: {}", user.getEmail(), task.getName(), numericalTaskSize,
          dbService.getUserAvailableCapacity(user));
      distributionView.showInvalidAssignmentDialog(user, task);
      return;
    }

    if (distributionView.showConfirmAssignmentDialog(user, task)) {
      logger.info("Assigning task - User: {}, Task: {}", user.getEmail(), task.getName());
      taskManager.assignTask(task, user);
    }
  }

  /**
   * Handles the auto-distribution process.
   */
  public void handleAutoDistribution() {
    logger.debug("Starting auto-distribution process");
    List<Task> unassignedTasks = dbService.getUnassignedTasksBySizeAndPriority();
    List<User> availableUsers = dbService.getAllUsersByCapacity();
    List<Pair<Task, User>> assignments = calculateOptimalAssignments(unassignedTasks,
        availableUsers);

    if (assignments.isEmpty()) {
      logger.warn("No possible assignments found in auto-distribution");
      distributionView.showNoAssignmentsPossibleDialog();
      return;
    }

    if (distributionView.showConfirmAutoDistributionDialog(assignments)) {
      logger.info("Executing auto-distribution with {} assignments", assignments.size());
      assignments.forEach(pair -> taskManager.assignTask(pair.getKey(), pair.getValue()));
    }
  }

  /**
   * Calculates the optimal assignments for the given tasks and users.
   *
   * @param tasks the tasks to assign
   * @param users the users to assign to
   * @return the optimal assignments
   */
  private List<Pair<Task, User>> calculateOptimalAssignments(List<Task> tasks, List<User> users) {
    logger.debug("Calculating optimal assignments - Tasks: {}, Users: {}", tasks.size(),
        users.size());
    List<Pair<Task, User>> assignments = new ArrayList<>();
    List<Task> taskList = new ArrayList<>(tasks);
    List<User> userList = new ArrayList<>(users);

    // Sort tasks by priority (high to low) and size (large to small)
    taskList.sort(Comparator
        .<Task>comparingInt(task -> task.getPriority().ordinal())
        .reversed()
        .thenComparingInt(task -> task.getSize().ordinal())
        .reversed()
    );

    for (Task task : taskList) {
      int taskSize = task.getSize().getValue();
      User bestUser = null;
      int maxCapacity = -1;

      for (User user : userList) {
        int availableCapacity = dbService.getUserAvailableCapacity(user);
        if (availableCapacity >= taskSize && availableCapacity > maxCapacity) {
          bestUser = user;
          maxCapacity = availableCapacity;
        }
      }

      if (bestUser != null) {
        logger.trace("Found assignment - Task: {}, User: {}, Capacity: {}", 
            task.getName(), bestUser.getEmail(), maxCapacity);
        assignments.add(new Pair<>(task, bestUser));
        userList.remove(bestUser);
      }
    }

    logger.debug("Calculated {} optimal assignments", assignments.size());
    return assignments;
  }

  /**
   * Handles the event of a button being clicked.
   *
   * @param buttonId the button id
   */
  @Override
  public void onButtonClicked(String buttonId) {
    if ("auto_distribute".equals(buttonId)) {
      handleAutoDistribution();
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
    // Not needed
  }

  /**
   * Handles the event of a task being updated.
   *
   * @param task the task
   */
  @Override
  protected void handleTaskUpdated(Task task) {
    // Not needed
    distributionView.refreshView();
  }

  /**
   * Handles the event of a task being deleted.
   *
   * @param task the task
   */
  @Override
  protected void handleTaskDeleted(Task task) {
    distributionView.refreshView();
  }

  /**
   * Handles the event of a task being assigned to a user.
   *
   * @param task the task
   * @param user the user
   */
  @Override
  protected void handleTaskAssigned(Task task, User user) {
    distributionView.refreshView();
  }

  /**
   * Handles the event of a task being completed.
   *
   * @param task the task
   */
  @Override
  protected void handleTaskCompleted(Task task) {
    distributionView.refreshView();
  }

  /**
   * Handles the event of the cache being refreshed.
   */
  @Override
  protected void handleCacheRefreshed() {
    distributionView.refreshView();
  }
} 