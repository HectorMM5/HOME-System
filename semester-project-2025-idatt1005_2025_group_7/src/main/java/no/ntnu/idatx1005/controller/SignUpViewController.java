package no.ntnu.idatx1005.controller;

import no.ntnu.idatx1005.model.task.Task;
import no.ntnu.idatx1005.observer.ButtonClickObserver;
import no.ntnu.idatx1005.service.DatabaseService;
import no.ntnu.idatx1005.storage.H2Manager;
import no.ntnu.idatx1005.view.container.SignUpView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h3>SignUp View Controller</h3>
 *
 * <p>This class is responsible for handling the sign up view and the user registration.
 * It implements the {@link ButtonClickObserver} interface.
 *
 * @see ButtonClickObserver
 * @author William Holtsdalen
 * @since V1.1.0
 */
public class SignUpViewController implements ButtonClickObserver {
  private static final Logger logger = LoggerFactory.getLogger(SignUpViewController.class);
  private final DatabaseService dbService;
  private final SignUpView signUpView;

  /**
   * Constructs a new SignUpViewController.
   *
   * @param signUpView the sign up view
   */
  public SignUpViewController(SignUpView signUpView) {
    this.dbService = DatabaseService.getInstance();
    this.signUpView = signUpView;
  }

  /**
   * Handles the event of a button being clicked.
   *
   * @param buttonId the button id
   */
  @Override
  public void onButtonClicked(String buttonId) {
    if (buttonId.equals("execute_signup")) {
      logger.debug("Button clicked: {}", buttonId);
      handleSignUpButtonAction();
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
   * Handles the sign up button action. Verifies the credentials entered by the user, and if they
   * are valid, it calls the handleSignUp method to add the user to the database.
   * If the user is successfully added, it shows the login view, otherwise it displays an error
   * message.
   */
  private void handleSignUpButtonAction() {
    logger.debug("Handling sign up button action");
    if (!verifyCredentials()) {
      return;
    }

    if (!handleSignUp()) {
      logger.warn("Failed to sign up user: {}", signUpView.getEmail());
      signUpView.showErrorMessage("Could not sign up.");
    } else {
      logger.info("Successfully signed up user: {}", signUpView.getEmail());
      signUpView.notifyOfSignup();
      signUpView.clearFields();
    }
  }

  /**
   * Verifies the credentials entered by the user. If the credentials are valid, the method returns
   * true, otherwise it returns false.
   *
   * @return true if the credentials are valid, false otherwise
   */
  private boolean verifyCredentials() {
    final String firstName = signUpView.getFirstName();
    final String lastName = signUpView.getLastName();
    final String email = signUpView.getEmail();
    final String password = signUpView.getPassword();
    final String repeatPassword = signUpView.getRepeatPassword();

    logger.debug("Verifying credentials for user: {}", email);

    if (firstName.isEmpty() || lastName.isEmpty()
        || email.isEmpty() || password.isEmpty()) {
      logger.warn("Sign up attempt with empty fields");
      signUpView.showErrorMessage("No fields can be empty");
      return false;
    }
    if (H2Manager.getUserByEmail(email) != null) {
      logger.warn("Sign up attempt with existing email: {}", email);
      signUpView.showErrorMessage("Email already exists");
      return false;
    }
    if (!password.equals(repeatPassword)) {
      logger.warn("Sign up attempt with mismatched passwords for user: {}", email);
      signUpView.showErrorMessage("Passwords do not match");
      return false;
    }
    if (password.length() < 8 || password.length() > 20) {
      logger.warn("Sign up attempt with invalid password length for user: {}", email);
      signUpView.showErrorMessage("Password must be between 8 and 20 characters");
      return false;
    }
    if (!password.matches(".*[A-Z].*")) {
      logger.warn("Sign up attempt with password missing uppercase for user: {}", email);
      signUpView.showErrorMessage("Password must contain at least one uppercase letter");
      return false;
    }
    if (!password.matches(".*[a-z].*")) {
      logger.warn("Sign up attempt with password missing lowercase for user: {}", email);
      signUpView.showErrorMessage("Password must contain at least one lowercase letter");
      return false;
    }
    if (!password.matches(".*\\d.*")) {
      logger.warn("Sign up attempt with password missing number for user: {}", email);
      signUpView.showErrorMessage("Password must contain at least one number");
      return false;
    }
    logger.debug("Credentials verified successfully for user: {}", email);
    return true;
  }

  /**
   * Handles the sign up action. Calls the appropriate method in {@link DatabaseService} to add the
   * user to the database. If the user is successfully added, the methods returns true, otherwise
   * it returns false.
   *
   * @return true if the user was successfully added, false otherwise
   */
  private boolean handleSignUp() {
    logger.debug("Attempting to add user to database: {}", signUpView.getEmail());
    return dbService.addUser(signUpView.getFirstName(), signUpView.getLastName(),
        signUpView.getEmail(), signUpView.getPassword());
  }
} 