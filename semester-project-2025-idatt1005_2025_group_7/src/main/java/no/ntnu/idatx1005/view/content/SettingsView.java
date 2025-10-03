package no.ntnu.idatx1005.view.content;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Spinner;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import no.ntnu.idatx1005.model.task.Task;
import no.ntnu.idatx1005.model.user.User;
import no.ntnu.idatx1005.observer.ButtonClickObserver;
import no.ntnu.idatx1005.observer.ButtonClickSubject;

/**
 * <h3>View class for the settings view.</h3>
 *
 * <p>The view extends the {@link VBox} class. The view is meant to be used as a child of the
 * {@link ContentView} class. It implements the {@link ButtonClickSubject} interface to notify
 * observers when a button in the view is clicked.
 *
 * @see VBox
 * @see ContentView
 * @see ButtonClickSubject
 * @author William Holtsdalen
 * @since V1.1.0
 */
public class SettingsView extends VBox implements ButtonClickSubject {
  private final User currentUser;
  private final List<ButtonClickObserver> observers;

  private MFXTextField firstNameField;
  private MFXTextField lastNameField;
  private MFXTextField emailField;
  private MFXPasswordField currentPasswordField;
  private MFXPasswordField newPasswordField;
  private MFXPasswordField confirmPasswordField;
  private Spinner<Integer> taskCapacitySpinner;
  private ToggleButton sicknessToggle;

  /**
   * Constructs the settings view for the given user.
   *
   * @param currentUser the current user
   */
  public SettingsView(User currentUser) {
    this.currentUser = currentUser;
    this.observers = new ArrayList<>();

    this.getStyleClass().add("content");
    this.setPadding(new Insets(25));
    this.setAlignment(Pos.CENTER);
    VBox.setVgrow(this, Priority.ALWAYS);

    initialize();
  }

  /**
   * Initializes the settings view.
   */
  private void initialize() {
    final HBox mainSettingsContainer = new HBox(25);
    mainSettingsContainer.setAlignment(Pos.TOP_CENTER);
    mainSettingsContainer.setPadding(new Insets(0));

    final HBox columnsContainer = new HBox(25);
    final VBox leftColumn = new VBox(25);
    final VBox rightColumn = new VBox(25);
    leftColumn.setPrefWidth(400);
    rightColumn.setPrefWidth(400);
    leftColumn.setAlignment(Pos.TOP_LEFT);
    rightColumn.setAlignment(Pos.TOP_LEFT);

    final VBox personalInfoSection = new VBox(20);
    personalInfoSection.getStyleClass().add("settings-section");
    personalInfoSection.setAlignment(Pos.TOP_LEFT);

    final Text personalInfoTitle = new Text("Personal Information");
    personalInfoTitle.getStyleClass().add("section-title");
    personalInfoTitle.setFill(Color.WHITE);

    firstNameField = new MFXTextField();
    firstNameField.setPromptText("First Name");
    firstNameField.setFloatingText("First Name");
    firstNameField.setText(currentUser.getFirstName());
    firstNameField.getStyleClass().add("mfx-text-field");
    firstNameField.getStyleClass().add("settings-text");
    firstNameField.setStyle("-fx-border-color: #D9D9D9; -fx-border-width: 1px; "
        + "-fx-border-radius: 5;");

    lastNameField = new MFXTextField();
    lastNameField.setPromptText("Last Name");
    lastNameField.setFloatingText("Last Name");
    lastNameField.setText(currentUser.getLastName());
    lastNameField.getStyleClass().add("mfx-text-field");
    lastNameField.getStyleClass().add("settings-text");
    lastNameField.setStyle("-fx-border-color: #D9D9D9; -fx-border-width: 1px; "
        + "-fx-border-radius: 5;");

    emailField = new MFXTextField();
    emailField.setPromptText("Email");
    emailField.setFloatingText("Email");
    emailField.setText(currentUser.getEmail());
    emailField.getStyleClass().add("mfx-text-field");
    emailField.getStyleClass().add("settings-text");
    emailField.setStyle("-fx-border-color: #D9D9D9; -fx-border-width: 1px; -fx-border-radius: 5;");

    personalInfoSection.getChildren().addAll(
        personalInfoTitle,
        firstNameField,
        lastNameField,
        emailField
    );

    VBox workSettingsSection = new VBox(20);
    workSettingsSection.getStyleClass().add("settings-section");
    workSettingsSection.setAlignment(Pos.TOP_LEFT);

    Text workSettingsTitle = new Text("Work Settings");
    workSettingsTitle.getStyleClass().add("section-title");
    workSettingsTitle.setFill(Color.WHITE);

    HBox taskCapacityBox = new HBox(15);
    taskCapacityBox.setAlignment(Pos.CENTER_LEFT);
    Text taskCapacityLabel = new Text("Task Capacity:");
    taskCapacityLabel.getStyleClass().add("settings-label");
    taskCapacityLabel.setFill(Color.WHITE);

    taskCapacitySpinner = new Spinner<>(0, 100,
        currentUser.getTaskCapacity(), 5);
    taskCapacitySpinner.setEditable(true);
    taskCapacitySpinner.setPrefWidth(100);
    taskCapacitySpinner.getStyleClass().add("settings-spinner");

    taskCapacityBox.getChildren().addAll(taskCapacityLabel, taskCapacitySpinner);

    HBox availabilityBox = new HBox(15);
    availabilityBox.setAlignment(Pos.CENTER_LEFT);
    Text availabilityLabel = new Text("Availability:");
    availabilityLabel.getStyleClass().add("settings-label");
    availabilityLabel.setFill(Color.WHITE);

    sicknessToggle = new ToggleButton(currentUser.getSickness() ? "Sick" : "Available");
    sicknessToggle.setSelected(currentUser.getSickness());
    sicknessToggle.getStyleClass().add("health-toggle");
    sicknessToggle.setPrefWidth(100);
    sicknessToggle.selectedProperty().addListener((obs, oldVal, newVal) ->
        sicknessToggle.setText(newVal ? "Sick" : "Available")
    );

    availabilityBox.getChildren().addAll(availabilityLabel, sicknessToggle);
    workSettingsSection.getChildren().addAll(workSettingsTitle, taskCapacityBox, availabilityBox);

    // Password Section
    VBox passwordSection = new VBox(20);
    passwordSection.getStyleClass().add("settings-section");
    passwordSection.setAlignment(Pos.TOP_LEFT);

    Text passwordTitle = new Text("Change Password");
    passwordTitle.getStyleClass().add("section-title");
    passwordTitle.setFill(Color.WHITE);

    currentPasswordField = new MFXPasswordField();
    currentPasswordField.setPromptText("Current Password");
    currentPasswordField.setFloatingText("Current Password");
    currentPasswordField.getStyleClass().add("mfx-text-field");
    currentPasswordField.getStyleClass().add("settings-text");
    currentPasswordField.setStyle("-fx-border-color: #D9D9D9; -fx-border-width: 1px; "
        + "-fx-border-radius: 5;");

    newPasswordField = new MFXPasswordField();
    newPasswordField.setPromptText("New Password");
    newPasswordField.setFloatingText("New Password");
    newPasswordField.getStyleClass().add("mfx-text-field");
    newPasswordField.getStyleClass().add("settings-text");
    newPasswordField.setStyle("-fx-border-color: #D9D9D9; -fx-border-width: 1px; "
        + "-fx-border-radius: 5;");

    confirmPasswordField = new MFXPasswordField();
    confirmPasswordField.setPromptText("Confirm New Password");
    confirmPasswordField.setFloatingText("Confirm New Password");
    confirmPasswordField.getStyleClass().add("mfx-text-field");
    confirmPasswordField.getStyleClass().add("settings-text");
    confirmPasswordField.setStyle("-fx-border-color: #D9D9D9; -fx-border-width: 1px; "
        + "-fx-border-radius: 5;");

    MFXButton changePasswordButton = new MFXButton("Change Password");
    changePasswordButton.getStyleClass().add("mfx-button");
    changePasswordButton.setStyle("-fx-border-color: #D9D9D9; -fx-border-width: 1px; "
        + "-fx-border-radius: 5;");
    changePasswordButton.setOnAction(event ->
        notifyObservers("change_password"));

    passwordSection.getChildren().addAll(
        passwordTitle,
        currentPasswordField,
        newPasswordField,
        confirmPasswordField,
        changePasswordButton
    );

    VBox accountSection = new VBox(20);
    accountSection.getStyleClass().add("settings-section");
    accountSection.setAlignment(Pos.TOP_LEFT);

    Text accountTitle = new Text("Account Management");
    accountTitle.getStyleClass().add("section-title");
    accountTitle.setFill(Color.WHITE);

    Region separator = new Region();
    separator.setPrefHeight(1);
    separator.setStyle("-fx-background-color: #131F2F;");
    separator.setMaxWidth(Double.MAX_VALUE);
    HBox.setHgrow(separator, Priority.ALWAYS);

    MFXButton deleteAccountButton = new MFXButton("Delete Account");
    deleteAccountButton.getStyleClass().addAll("danger-button");
    deleteAccountButton.setOnAction(event -> notifyObservers("delete_account"));

    deleteAccountButton.getStyleClass().add("delete-button");
    accountSection.getChildren().addAll(accountTitle, separator, deleteAccountButton);

    leftColumn.getChildren().addAll(personalInfoSection, workSettingsSection);
    rightColumn.getChildren().addAll(passwordSection, accountSection);

    columnsContainer.getChildren().addAll(leftColumn, rightColumn);
    mainSettingsContainer.getChildren().add(columnsContainer);
    this.getChildren().add(mainSettingsContainer);
  }

  /**
   * Adds an observer to the settings view.
   *
   * @param observer the observer to add
   */
  @Override
  public void addObserver(ButtonClickObserver observer) {
    observers.add(observer);
  }

  /**
   * Removes an observer from the settings view.
   *
   * @param observer the observer to remove
   */
  @Override
  public void removeObserver(ButtonClickObserver observer) {
    observers.remove(observer);
  }

  /**
   * Notifies all observers with a button id.
   *
   * @param buttonId the button id
   */
  @Override
  public void notifyObservers(String buttonId) {
    new ArrayList<>(observers).forEach(observer -> observer.onButtonClicked(buttonId));
  }

  /**
   * Notifies all observers with a task.
   *
   * @param buttonId the button id
   * @param task the task
   */
  @Override
  public void notifyObserversWithTask(String buttonId, Task task) {
    // Not needed
  }

  /**
   * Gets the current user.
   *
   * @return the current user
   */
  public User getCurrentUser() {
    return currentUser;
  }

  /**
   * Gets the first name.
   *
   * @return the first name
   */
  public String getFirstName() {
    return firstNameField.getText().trim();
  }

  /**
   * Gets the last name.
   *
   * @return the last name
   */
  public String getLastName() {
    return lastNameField.getText().trim();
  }

  /**
   * Gets the email.
   *
   * @return the email
   */
  public String getEmail() {
    return emailField.getText().trim();
  }

  /**
   * Gets the current password.
   *
   * @return the current password
   */
  public String getCurrentPassword() {
    return currentPasswordField.getText();
  }

  /**
   * Gets the new password.
   *
   * @return the new password
   */
  public String getNewPassword() {
    return newPasswordField.getText();
  }

  /**
   * Gets the confirm password.
   *
   * @return the confirm password
   */
  public String getConfirmPassword() {
    return confirmPasswordField.getText();
  }

  /**
   * Gets the task capacity.
   *
   * @return the task capacity
   */
  public int getTaskCapacity() {
    return taskCapacitySpinner.getValue();
  }

  /**
   * Gets the sickness status.
   *
   * @return the sickness status
   */
  public boolean getSicknessStatus() {
    return sicknessToggle.isSelected();
  }

  /**
   * Shows an error dialog.
   *
   * @param message the message to show
   */
  public void showErrorDialog(String message) {
    Platform.runLater(() -> {
      Alert alert = new Alert(AlertType.ERROR);
      alert.setTitle("Error");
      alert.setHeaderText("Could not save changes");
      alert.setContentText(message);
      alert.showAndWait();
    });
  }

  /**
   * Shows a success dialog.
   *
   * @param message the message to show
   */
  public void showSuccessDialog(String message) {
    Platform.runLater(() -> {
      Alert alert = new Alert(AlertType.INFORMATION);
      alert.setTitle("Success");
      alert.setHeaderText("Operation successful");
      alert.setContentText(message);
      alert.showAndWait();
    });
  }

  /**
   * Shows a delete confirmation dialog.
   *
   * @return true if the user confirms the deletion, false otherwise
   */
  public boolean showDeleteConfirmDialog() {
    Alert alert = new Alert(AlertType.CONFIRMATION);
    alert.setTitle("Delete Account");
    alert.setHeaderText("Are you sure you want to delete your account?");
    alert.setContentText("This action cannot be undone. All your data will be permanently "
        + "deleted.");
    return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
  }
}
