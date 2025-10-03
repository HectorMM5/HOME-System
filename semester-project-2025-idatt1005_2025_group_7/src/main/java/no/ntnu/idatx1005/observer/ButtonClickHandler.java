package no.ntnu.idatx1005.observer;

import java.util.Map;
import no.ntnu.idatx1005.MainApp;
import no.ntnu.idatx1005.model.task.Task;
import no.ntnu.idatx1005.navigation.ViewNavigator;
import no.ntnu.idatx1005.navigation.ViewType;
import no.ntnu.idatx1005.view.container.LoginView;
import no.ntnu.idatx1005.view.container.MainView;
import no.ntnu.idatx1005.view.container.SignUpView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h3>ButtonClickHandler class</h3>
 *
 * <p>This class is responsible for handling button click events and managing navigation through the
 * ViewNavigator. It also handles the logic for updating the view based on the button clicked. This 
 * class implements the {@link ButtonClickObserver} interface.
 *
 * @see ButtonClickObserver
 * @author Tord Fosse
 * @since V1.1.0
 */
public class ButtonClickHandler implements ButtonClickObserver {
  private static final Logger logger = LoggerFactory.getLogger(ButtonClickHandler.class);
  private final ViewNavigator navigator;
  private final MainApp mainApp;

  private final MainView mainView;
  private final LoginView loginView;
  private final SignUpView signUpView;

  /**
   * Constructs a new button click handler.
   *
   * @param navigator the view navigator
   * @param mainApp the main application
   */
  public ButtonClickHandler(ViewNavigator navigator, MainApp mainApp) {
    this.navigator = navigator;
    this.mainApp = mainApp;

    this.mainView = mainApp.getMainView();
    this.loginView = mainApp.getLoginView();
    this.signUpView = mainApp.getSignUpView();
    logger.debug("ButtonClickHandler initialized");
  }

  /**
   * Handles button click events.
   *
   * @param buttonId the ID of the button clicked
   */
  @Override
  public void onButtonClicked(String buttonId) {
    logger.debug("Button clicked: {}", buttonId);
    switch (buttonId) {
      case "all_tasks" -> navigator.navigateTo(ViewType.TASKS, Map.of("filter", "all"));
      case "open_tasks" -> navigator.navigateTo(ViewType.TASKS, Map.of("filter", "open"));
      case "my_tasks" -> navigator.navigateTo(ViewType.TASKS, Map.of("filter", "my"));
      case "completed_tasks" -> navigator.navigateTo(ViewType.TASKS, Map.of("filter", "completed"));
      case "insights" -> navigator.navigateTo(ViewType.INSIGHTS);
      case "settings" -> navigator.navigateTo(ViewType.SETTINGS);
      case "back_button" -> navigator.goBack();
      case "new_task" -> navigator.navigateTo(ViewType.NEW_TASK);
      case "distribute" -> navigator.navigateTo(ViewType.DISTRIBUTION);
      case "refresh_cache" -> handleRefreshCache();
      case "sign_up" -> handleSignUp();
      case "log_in" -> handleLogin();
      case "log_out" -> handleLogout();
      default -> { 
        break; 
      }
    }
  }

  /**
   * Handles button click events with a task.
   *
   * @param buttonId the ID of the button clicked
   * @param task the task
   */
  @Override
  public void onButtonClickedWithTask(String buttonId, Task task) {
    if ("edit_task".equals(buttonId)) {
      navigator.navigateTo(ViewType.EDIT_TASK, Map.of("taskId", task.getId()));
    } else {
      logger.warn("Unknown button clicked with task: {}", buttonId);
    }
  }

  /**
   * Handles the refresh cache button click event.
   */
  private void handleRefreshCache() {
    mainApp.getTaskManager().refreshCache();
  }

  /**
   * Handles the logout button click event.
   */
  private void handleLogout() {
    mainApp.getRoot().setCenter(loginView);
    navigator.resetNavHistory();
  }

  /**
   * Handles the sign up button click event.
   */
  private void handleSignUp() {
    mainApp.getRoot().setCenter(signUpView);
    navigator.resetNavHistory();
  }

  /**
   * Handles the login button click event.
   */
  private void handleLogin() {
    mainApp.getRoot().setCenter(mainView);
    navigator.resetToView(ViewType.TASKS, Map.of("filter", "all"));
  }
}