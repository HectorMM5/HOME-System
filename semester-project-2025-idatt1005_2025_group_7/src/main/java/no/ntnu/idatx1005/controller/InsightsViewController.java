package no.ntnu.idatx1005.controller;

import no.ntnu.idatx1005.model.task.Task;
import no.ntnu.idatx1005.model.user.User;
import no.ntnu.idatx1005.observer.ButtonClickObserver;
import no.ntnu.idatx1005.observer.TaskEventManager;
import no.ntnu.idatx1005.view.content.InsightsView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h3>Insights View Controller</h3>
 *
 * <p>This class is responsible for handling the insights view.
 * It extends the {@link BaseViewController} class and implements the {@link ButtonClickObserver}
 * interface.
 *
 * @see BaseViewController
 * @see ButtonClickObserver
 * @author William Holtsdalen
 * @since V1.1.0
 */
public class InsightsViewController extends BaseViewController implements ButtonClickObserver {
  private static final Logger logger = LoggerFactory.getLogger(InsightsViewController.class);
  private final InsightsView insightsView;

  /**
   * Constructs a new InsightsViewController.
   *
   * @param taskManager the task event manager
   * @param insightsView the insights view
   */
  public InsightsViewController(TaskEventManager taskManager, InsightsView insightsView) {
    super(taskManager);
    this.insightsView = insightsView;
    initializeView();
  }

  /**
   * Initializes the insights view.
   */
  private void initializeView() {
    logger.debug("Initializing insights view");
    updateCharts();
  }

  /**
   * Updates the insights charts.
   */
  private void updateCharts() {
    logger.debug("Updating insights charts");
    insightsView.initializeInsightsLayout();
  }

  /**
   * Handles the event of a button being clicked.
   *
   * @param buttonId the button id
   */
  @Override
  public void onButtonClicked(String buttonId) {
    logger.debug("Button clicked: {}", buttonId);
    // Not needed
  }

  /**
   * Handles the event of a button being clicked with a task.
   *
   * @param buttonId the button id
   * @param task the task
   */
  @Override
  public void onButtonClickedWithTask(String buttonId, Task task) {
    logger.debug("Button clicked with task - Button: {}, Task: {}", buttonId, task.getName());
    // Not needed
  }

  /**
   * Handles the event of a task being created.
   *
   * @param task the task
   */
  @Override
  protected void handleTaskCreated(Task task) {
    logger.debug("Task created: {}", task.getName());
    updateCharts();
  }

  /**
   * Handles the event of a task being updated.
   *
   * @param task the task
   */
  @Override
  protected void handleTaskUpdated(Task task) {
    logger.debug("Task updated: {}", task.getName());
    updateCharts();
  }

  /**
   * Handles the event of a task being deleted.
   *
   * @param task the task
   */
  @Override
  protected void handleTaskDeleted(Task task) {
    logger.debug("Task deleted: {}", task.getName());
    updateCharts();
  }

  /**
   * Handles the event of a task being assigned to a user.
   *
   * @param task the task
   * @param user the user
   */
  @Override
  protected void handleTaskAssigned(Task task, User user) {
    logger.debug("Task assigned - Task: {}, User: {}", task.getName(), user.getEmail());
    updateCharts();
  }

  /**
   * Handles the event of a task being completed.
   *
   * @param task the task
   */
  @Override
  protected void handleTaskCompleted(Task task) {
    logger.debug("Task completed: {}", task.getName());
    updateCharts();
  }

  /**
   * Handles the event of the cache being refreshed.
   */
  @Override
  protected void handleCacheRefreshed() {
    logger.debug("Cache refreshed");
    updateCharts();
  }
} 