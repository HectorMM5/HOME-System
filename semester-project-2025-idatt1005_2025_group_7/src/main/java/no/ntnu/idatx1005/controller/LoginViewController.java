package no.ntnu.idatx1005.controller;

import java.util.function.Consumer;
import no.ntnu.idatx1005.model.task.Task;
import no.ntnu.idatx1005.model.user.User;
import no.ntnu.idatx1005.observer.ButtonClickObserver;
import no.ntnu.idatx1005.service.DatabaseService;
import no.ntnu.idatx1005.view.container.LoginView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h3>Login View Controller</h3>
 *
 * <p>This class is responsible for handling the login view and the user authentication.
 * It implements the {@link ButtonClickObserver} interface.
 *
 * @see ButtonClickObserver
 * @author William Holtsdalen
 * @since V1.1.0
 */
public class LoginViewController implements ButtonClickObserver {
  private static final Logger logger = LoggerFactory.getLogger(LoginViewController.class);
  private final LoginView loginView;
  private DatabaseService dbService;
  private Consumer<User> onLoginUser;

  /**
   * Constructs a new LoginViewController.
   *
   * @param loginView the login view
   */
  public LoginViewController(LoginView loginView) {
    this.loginView = loginView;
    this.dbService = DatabaseService.getInstance();
  }

  /**
   * Handles the event of a button being clicked.
   *
   * @param buttonId the button id
   */
  @Override
  public void onButtonClicked(String buttonId) {
    if (buttonId.equals("log_in_clicked")) {
      logger.debug("Button clicked: {}", buttonId);
      handleLoginButtonAction();
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
   * Handles the login button action. Verifies the credentials entered by the user, and if they
   * are valid, it calls the handleLogin method to authenticate the user.
   * If the user is successfully authenticated, it shows the main view, otherwise it displays an
   * error message.
   */
  private void handleLoginButtonAction() {
    String email = loginView.getEmail();
    logger.debug("Attempting login for user: {}", email);

    loginView.showErrorMessage("");
    if (email.isEmpty() || loginView.getPassword().isEmpty()) {
      logger.warn("Login attempted with empty fields");
      loginView.showErrorMessage("No fields can be empty");
      return;
    }

    User user = dbService.authenticateUser(email, loginView.getPassword());
    if (user == null) {
      logger.warn("Failed login attempt for user: {}", email);
      loginView.showErrorMessage("Invalid username or password");
    } else {
      logger.info("Successful login for user: {}", email);
      onLoginUser.accept(user);
      loginView.clearFields();
      loginView.notifyOfLogin();
    }
  }

  /**
   * Sets the callback for when a user is logged in.
   *
   * @param onLoginUser the callback
   */
  public void setOnLoginUser(Consumer<User> onLoginUser) {
    logger.debug("Setting login user callback");
    this.onLoginUser = onLoginUser;
  }
} 