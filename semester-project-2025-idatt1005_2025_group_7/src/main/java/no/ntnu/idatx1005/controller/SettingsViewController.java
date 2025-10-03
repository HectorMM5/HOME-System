package no.ntnu.idatx1005.controller;

import java.util.UUID;
import java.util.function.Consumer;
import no.ntnu.idatx1005.model.task.Task;
import no.ntnu.idatx1005.model.user.User;
import no.ntnu.idatx1005.observer.ButtonClickObserver;
import no.ntnu.idatx1005.observer.TaskEventManager;
import no.ntnu.idatx1005.service.DatabaseService;
import no.ntnu.idatx1005.view.content.SettingsView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h3>Settings View Controller</h3>
 *
 * <p>This class is responsible for handling the settings view and the user settings.
 * It extends the {@link BaseViewController} class and implements the {@link ButtonClickObserver} 
 * interface.
 *
 * @see BaseViewController
 * @see ButtonClickObserver
 * @author William Holtsdalen
 * @since V1.1.0
 */
public class SettingsViewController extends BaseViewController implements ButtonClickObserver {
  private static final Logger logger = LoggerFactory.getLogger(SettingsViewController.class);
  private final SettingsView settingsView;
  private final DatabaseService dbService;
  private Consumer<User> onUpdateLoggedInUser;

  /**
   * Constructs a new SettingsViewController.
   *
   * @param taskManager the task event manager
   * @param settingsView the settings view
   */
  public SettingsViewController(TaskEventManager taskManager, SettingsView settingsView) {
    super(taskManager);
    this.settingsView = settingsView;
    this.dbService = DatabaseService.getInstance();
  }

  /**
   * Handles the event of a button being clicked.
   *
   * @param buttonId the button id
   */
  @Override
  public void onButtonClicked(String buttonId) {
    logger.debug("Button clicked: {}", buttonId);
    switch (buttonId) {
      case "save_settings" -> handleSaveSettings();
      case "delete_account" -> handleDeleteAccount();
      case "change_password" -> handleChangePassword();
      default -> {
      }
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
    logger.debug("Button clicked with task - Button: {}, Task: {}", buttonId, task.getName());
    // Not needed for settings view
  }

  /**
   * Sets the callback for when updating the logged in user.
   *
   * @param onUpdateLoggedInUser the callback
   */
  public void setOnUpdateLoggedInUser(Consumer<User> onUpdateLoggedInUser) {
    logger.debug("Setting update logged in user callback");
    this.onUpdateLoggedInUser = onUpdateLoggedInUser;
  }

  /**
   * Handles the saving of settings.
   */
  private void handleSaveSettings() {
    UUID userId = settingsView.getCurrentUser().getId();
    String firstName = settingsView.getFirstName();
    String lastName = settingsView.getLastName();
    String email = settingsView.getEmail();
    int taskCapacity = settingsView.getTaskCapacity();
    boolean sicknessStatus = settingsView.getSicknessStatus();

    logger.debug("Attempting to save settings for user: {}", email);

    if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
      logger.warn("Save settings attempt with empty fields for user: {}", email);
      settingsView.showErrorDialog("Please fill in all required fields.");
      return;
    }

    try {
      User updatedUser = new User(
          userId,
          firstName,
          lastName,
          email,
          settingsView.getCurrentUser().getPasswordHash(),
          settingsView.getCurrentUser().getSalt(),
          taskCapacity,
          sicknessStatus
      );

      dbService.updateUser(updatedUser);
      logger.info("Settings saved successfully for user: {}", email);
      settingsView.showSuccessDialog("Settings saved successfully!");
      onUpdateLoggedInUser.accept(updatedUser);
    } catch (Exception e) {
      logger.error("Failed to save settings for user: {} - {}", email, e.getMessage());
      settingsView.showErrorDialog("Failed to save settings: " + e.getMessage());
    }
  }

  /**
   * Handles the changing of password.
   */
  private void handleChangePassword() {
    String currentPassword = settingsView.getCurrentPassword();
    String newPassword = settingsView.getNewPassword();
    String confirmPassword = settingsView.getConfirmPassword();
    String email = settingsView.getCurrentUser().getEmail();

    logger.debug("Attempting to change password for user: {}", email);

    if (currentPassword.isEmpty()) {
      logger.warn("Password change attempt with empty current password for user: {}", email);
      settingsView.showErrorDialog("Please enter your current password.");
      return;
    }
    if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
      logger.warn("Password change attempt with empty new password for user: {}", email);
      settingsView.showErrorDialog("Please enter and confirm your new password.");
      return;
    }
    if (!newPassword.equals(confirmPassword)) {
      logger.warn("Password change attempt with mismatched passwords for user: {}", email);
      settingsView.showErrorDialog("New passwords do not match.");
      return;
    }
    if (!isValidPassword(newPassword)) {
      logger.warn("Password change attempt with invalid password format for user: {}", email);
      settingsView.showErrorDialog("Password must be 8-20 characters long and contain uppercase, "
          + "lowercase, and numbers.");
      return;
    }
    if (!dbService.validatePassword(settingsView.getCurrentUser(), currentPassword)) {
      logger.warn("Password change attempt with incorrect current password for user: {}", email);
      settingsView.showErrorDialog("Current password is incorrect.");
      return;
    }

    try {
      dbService.updateUserPassword(settingsView.getCurrentUser(), newPassword);
      logger.info("Password changed successfully for user: {}", email);
      settingsView.showSuccessDialog("Password changed successfully!");
    } catch (Exception e) {
      logger.error("Failed to change password for user: {} - {}", email, e.getMessage());
      settingsView.showErrorDialog("Failed to change password: " + e.getMessage());
    }
  }

  /**
   * Validates the password format.
   *
   * @param password the password to validate
   * @return true if the password is valid, false otherwise
   */
  private boolean isValidPassword(String password) {
    logger.trace("Validating password format");
    if (password.length() < 8 || password.length() > 20) {
      return false;
    }

    boolean hasUppercase = false;
    boolean hasLowercase = false;
    boolean hasNumber = false;

    for (char c : password.toCharArray()) {
      if (Character.isUpperCase(c)) {
        hasUppercase = true;
      }
      if (Character.isLowerCase(c)) {
        hasLowercase = true;
      }
      if (Character.isDigit(c)) {
        hasNumber = true;
      }
    }

    return hasUppercase && hasLowercase && hasNumber;
  }

  /**
   * Handles the deletion of the account.
   */
  private void handleDeleteAccount() {
    User currentUser = settingsView.getCurrentUser();
    logger.debug("Attempting to delete account for user: {}", currentUser.getEmail());
    
    if (settingsView.showDeleteConfirmDialog()) {
      try {
        dbService.removeUser(currentUser);
        logger.info("Account deleted successfully for user: {}", currentUser.getEmail());
        settingsView.showSuccessDialog("Account deleted successfully");
        // The ButtonClickHandler will handle navigation after account deletion
        settingsView.notifyObservers("log_out");
      } catch (Exception e) {
        logger.error("Failed to delete account for user: {} - {}", currentUser.getEmail(), 
            e.getMessage());
        settingsView.showErrorDialog("Failed to delete account: " + e.getMessage());
      }
    }
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
  }

  /**
   * Handles the event of a task being deleted.
   *
   * @param task the task
   */
  @Override
  protected void handleTaskDeleted(Task task) {
    // Not needed
  }

  /**
   * Handles the event of a task being assigned to a user.
   *
   * @param task the task
   * @param user the user
   */
  @Override
  protected void handleTaskAssigned(Task task, User user) {
    // Not needed
  }

  /**
   * Handles the event of a task being completed.
   *
   * @param task the task
   */
  @Override
  protected void handleTaskCompleted(Task task) {
    // Not needed
  }

  /**
   * Handles the event of the cache being refreshed.
   */
  @Override
  protected void handleCacheRefreshed() {
    // Not needed
  }
} 
