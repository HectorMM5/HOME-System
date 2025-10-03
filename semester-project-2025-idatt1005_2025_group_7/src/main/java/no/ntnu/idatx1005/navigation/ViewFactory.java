package no.ntnu.idatx1005.navigation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javafx.scene.Node;
import no.ntnu.idatx1005.MainApp;
import no.ntnu.idatx1005.controller.DistributionViewController;
import no.ntnu.idatx1005.controller.EditTaskViewController;
import no.ntnu.idatx1005.controller.InsightsViewController;
import no.ntnu.idatx1005.controller.LoginViewController;
import no.ntnu.idatx1005.controller.NewTaskViewController;
import no.ntnu.idatx1005.controller.SettingsViewController;
import no.ntnu.idatx1005.controller.SignUpViewController;
import no.ntnu.idatx1005.controller.TasksViewController;
import no.ntnu.idatx1005.model.task.Task;
import no.ntnu.idatx1005.observer.ButtonClickObserver;
import no.ntnu.idatx1005.observer.TaskEventManager;
import no.ntnu.idatx1005.storage.H2Manager;
import no.ntnu.idatx1005.view.container.LoginView;
import no.ntnu.idatx1005.view.container.SignUpView;
import no.ntnu.idatx1005.view.content.DistributionView;
import no.ntnu.idatx1005.view.content.EditTaskView;
import no.ntnu.idatx1005.view.content.InsightsView;
import no.ntnu.idatx1005.view.content.NewTaskView;
import no.ntnu.idatx1005.view.content.SettingsView;
import no.ntnu.idatx1005.view.content.TasksView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h3>ViewFactory class</h3>
 *
 * <p>This is a class following the Factory design pattern, responsible for creating views based
 * on view type and parameters. The purpose of this class is to centralize view creation logic and
 * ensure consistent view initialization.
 *
 * @author William Holtsdalen
 * @since V1.1.0
 */
public class ViewFactory {
  private static final Logger logger = LoggerFactory.getLogger(ViewFactory.class);
  private final MainApp mainApp;
  private final TaskEventManager taskManager;
  private final List<ButtonClickObserver> buttonClickObservers;

  /**
   * Constructs a new ViewFactory.
   *
   * @param mainApp the main application
   */
  public ViewFactory(MainApp mainApp) {
    logger.debug("Initializing ViewFactory");
    this.mainApp = mainApp;
    this.taskManager = mainApp.getTaskManager();

    this.buttonClickObservers = new ArrayList<>();
  }

  /**
   * Adds a list of button click observers to the ViewFactory.
   *
   * @param buttonClickObservers the list of button click observers to add
   */
  public void addButtonClickObservers(List<ButtonClickObserver> buttonClickObservers) {
    logger.debug("Adding {} button click observers", buttonClickObservers.size());
    this.buttonClickObservers.addAll(buttonClickObservers);
  }

  /**
   * Creates a view based on the view type and parameters.
   *
   * @param type the type of view to create
   * @param params parameters needed for view creation
   * @return the created view node
   */
  public Node createView(ViewType type, Map<String, Object> params) {
    logger.debug("Creating view of type: {}", type);
    return switch (type) {
      case TASKS -> createTasksView(params);
      case NEW_TASK -> createNewTaskView();
      case EDIT_TASK -> createEditTaskView(params);
      case DISTRIBUTION -> createDistributionView();
      case INSIGHTS -> createInsightsView();
      case SETTINGS -> createSettingsView();
      case LOGIN -> createLoginView();
      case SIGNUP -> createSignUpView();
      default -> {
        logger.error("Unsupported view type: {}", type);
        throw new IllegalArgumentException("Unsupported view type: " + type);
      }
    };
  }

  /**
   * Creates a tasks view based on the filter parameter.
   *
   * @param params the parameters needed for the view creation
   * @return the created tasks view
   */
  private Node createTasksView(Map<String, Object> params) {
    logger.debug("Creating tasks view with params: {}", params);
    TasksView view = new TasksView();
    buttonClickObservers.forEach(view::addObserver);

    String filter = (String) params.getOrDefault("filter", "all");
    logger.debug("Applying filter: {}", filter);
    switch (filter) {
      case "all" -> view.allTasksView();
      case "open" -> view.openTasksView();
      case "my" -> view.myTasksView();
      case "completed" -> view.completedTasksView();
      default -> {
        logger.error("Invalid filter: {}", filter);
        throw new IllegalArgumentException("Invalid filter: " + filter);
      }
    }

    TasksViewController controller = new TasksViewController(taskManager, view);
    view.addObserver(controller);
    mainApp.initializeButtonClickObserver(controller);
    return view;
  }

  /**
   * Creates a new task view.
   *
   * @return the created new task view
   */
  private Node createNewTaskView() {
    logger.debug("Creating new task view");
    NewTaskView view = new NewTaskView();
    NewTaskViewController controller = new NewTaskViewController(taskManager, view);
    mainApp.initializeButtonClickObserver(controller);
    return view;
  }

  /**
   * Creates an edit task view based on the task ID parameter.
   *
   * @param params the parameters needed for the view creation
   * @return the created edit task view
   */
  private Node createEditTaskView(Map<String, Object> params) {
    logger.debug("Creating edit task view with params: {}", params);
    UUID taskId = (UUID) params.get("taskId");
    if (taskId == null) {
      logger.error("Task ID is required for edit view");
      throw new IllegalArgumentException("Task ID is required for edit view");
    }
    Task task = H2Manager.getTaskById(taskId);
    if (task == null) {
      logger.error("Task not found: {}", taskId);
      throw new IllegalArgumentException("Task not found: " + taskId);
    }
    EditTaskView view = new EditTaskView(task);
    EditTaskViewController controller = new EditTaskViewController(taskManager, view);
    mainApp.initializeButtonClickObserver(controller);
    return view;
  }

  /**
   * Creates a distribution view.
   *
   * @return the created distribution view
   */
  private Node createDistributionView() {
    logger.debug("Creating distribution view");
    DistributionView view = new DistributionView();
    buttonClickObservers.forEach(view::addObserver);
    DistributionViewController controller = new DistributionViewController(taskManager, view);
    view.addObserver(controller);
    mainApp.initializeButtonClickObserver(controller);
    return view;
  }

  /**
   * Creates an insights view.
   *
   * @return the created insights view
   */
  private Node createInsightsView() {
    logger.debug("Creating insights view");
    InsightsView view = new InsightsView();
    InsightsViewController controller = new InsightsViewController(taskManager, view);
    mainApp.initializeButtonClickObserver(controller);
    return view;
  }

  /**
   * Creates a settings view.
   *
   * @return the created settings view
   */
  private Node createSettingsView() {
    logger.debug("Creating settings view");
    SettingsView view = new SettingsView(mainApp.getUser());
    buttonClickObservers.forEach(view::addObserver);
    SettingsViewController controller = new SettingsViewController(taskManager, view);
    controller.setOnUpdateLoggedInUser(mainApp::setUser);
    view.addObserver(controller);
    mainApp.initializeButtonClickObserver(controller);
    return view;
  }

  /**
   * Creates a login view.
   *
   * @return the created login view
   */
  public Node createLoginView() {
    logger.debug("Creating login view");
    LoginView view = new LoginView();
    buttonClickObservers.forEach(view::addObserver);
    LoginViewController controller = new LoginViewController(view);
    controller.setOnLoginUser(mainApp::setUser);
    view.addObserver(controller);
    mainApp.initializeButtonClickObserver(controller);
    return view;
  }

  /**
   * Creates a sign up view.
   *
   * @return the created sign up view
   */
  public Node createSignUpView() {
    logger.debug("Creating sign up view");
    SignUpView view = new SignUpView();
    buttonClickObservers.forEach(view::addObserver);
    SignUpViewController controller = new SignUpViewController(view);
    view.addObserver(controller);
    mainApp.initializeButtonClickObserver(controller);
    return view;
  }
} 