package no.ntnu.idatx1005.view.container;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import no.ntnu.idatx1005.model.task.Task;
import no.ntnu.idatx1005.observer.ButtonClickObserver;
import no.ntnu.idatx1005.observer.ButtonClickSubject;

/**
 * <h3>View for the sign up page.</h3>
 *
 * <p>The view extends the {@link VBox} class. The view is meant to be used as a child of the
 * {@link MainView} class.
 *
 * @see VBox
 * @see MainView
 * @author Tord Fosse
 * @author William Holtsdalen
 * @see VBox
 * @see MainView
 * @since V0.1.0
 */
public class SignUpView extends VBox implements ButtonClickSubject {

  private MFXTextField firstNameField;
  private MFXTextField lastNameField;
  private MFXTextField emailField;
  private MFXPasswordField passwordField;
  private MFXPasswordField repeatPasswordField;
  private Text errorMessage;
  private VBox signUpBox;

  private final List<ButtonClickObserver> observers;

  /**
   * Constructs a new sign up view.
   */
  public SignUpView() {
    this.observers = new ArrayList<>();

    initializeSignUpBox();

    // For test
    firstNameField.setId("firstNameField");
    lastNameField.setId("lastNameField");
    emailField.setId("emailField");
    passwordField.setId("passwordField");
    repeatPasswordField.setId("repeatPasswordField");
    errorMessage.setId("errorMessage");

    this.setAlignment(Pos.CENTER);
    this.getChildren().setAll(signUpBox);
  }

  /**
   * Returns the sign up view.
   *
   * @return the sign up view
   */
  public VBox getView() {
    return this;
  }

  /**
   * Adds an observer to the sign up view.
   *
   * @param observer the observer to add
   */
  @Override
  public void addObserver(ButtonClickObserver observer) {
    observers.add(observer);
  }

  /**
   * Removes an observer from the sign up view.
   *
   * @param observer the observer to remove
   */
  @Override
  public void removeObserver(ButtonClickObserver observer) {
    observers.remove(observer);
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
   * Notifies all observers with a button id.
   *
   * @param buttonId the button id
   */
  @Override
  public void notifyObservers(String buttonId) {
    new ArrayList<>(observers).forEach(observer -> observer.onButtonClicked(buttonId));
  }

  /**
   * Initializes the sign up box.
   */
  private void initializeSignUpBox() {
    final Text title = new Text("H.O.M.E");
    title.getStyleClass().add("login-title");

    final Region spacer = new Region();
    spacer.setMinHeight(40);

    firstNameField = new MFXTextField();
    firstNameField.setPromptText("First Name");
    firstNameField.setFloatingText("First Name");
    firstNameField.getStyleClass().add("login-field");
    firstNameField.setStyle("-fx-border-color: #D9D9D9; -fx-border-width: 1px; "
        + "-fx-border-radius: 5;");

    lastNameField = new MFXTextField();
    lastNameField.setPromptText("Last Name");
    lastNameField.setFloatingText("Last Name");
    lastNameField.getStyleClass().add("login-field");
    lastNameField.setStyle("-fx-border-color: #D9D9D9; -fx-border-width: 1px; "
        + "-fx-border-radius: 5;");

    emailField = new MFXTextField();
    emailField.setPromptText("Email");
    emailField.setFloatingText("Email");
    emailField.getStyleClass().add("login-field");
    emailField.setStyle("-fx-border-color: #D9D9D9; -fx-border-width: 1px; "
        + "-fx-border-radius: 5;");

    passwordField = new MFXPasswordField();
    passwordField.setPromptText("Password");
    passwordField.setFloatingText("Password");
    passwordField.getStyleClass().add("login-field");
    passwordField.setStyle("-fx-border-color: #D9D9D9; -fx-border-width: 1px; "
        + "-fx-border-radius: 5;");

    repeatPasswordField = new MFXPasswordField();
    repeatPasswordField.setPromptText("Repeat Password");
    repeatPasswordField.setFloatingText("Repeat Password");
    repeatPasswordField.getStyleClass().add("login-field");
    repeatPasswordField.setStyle("-fx-border-color: #D9D9D9; -fx-border-width: 1px; "
        + "-fx-border-radius: 5;");

    final Text passwordRequirements = new Text("""
        The password must:
        • be 8-20 characters
        • contain uppercase and lowercase letters
        • contain at least one number""");
    passwordRequirements.getStyleClass().add("login-text");

    final VBox passwordRequirementsContainer = new VBox(passwordRequirements);
    passwordRequirementsContainer.setAlignment(Pos.CENTER);

    errorMessage = new Text();
    errorMessage.getStyleClass().add("signup-error-text");

    final MFXButton signUpButton = new MFXButton("Sign up");
    signUpButton.setOnAction(event -> notifyObservers("execute_signup"));
    signUpButton.setStyle("-fx-border-color: #D9D9D9; -fx-border-width: 1px; "
        + "-fx-border-radius: 5;");

    Text loginText = new Text("Already have an account? Log in ");
    loginText.getStyleClass().add("login-text");
    Hyperlink loginLink = new Hyperlink("here");
    // Using log_out here to redirect to the login view, just like when logging out.
    loginLink.setOnAction(event -> notifyObservers("log_out"));

    HBox loginContainer = new HBox(loginText, loginLink);
    loginContainer.setAlignment(Pos.CENTER);

    signUpBox = new VBox(title, spacer, firstNameField, lastNameField, emailField, passwordField,
        repeatPasswordField, passwordRequirementsContainer, errorMessage, signUpButton,
        loginContainer);
    signUpBox.setAlignment(Pos.CENTER);
    signUpBox.setSpacing(10);
    signUpBox.getStyleClass().add("login-box");
  }

  /**
   * Returns the first name from the sign up field.
   *
   * @return the first name
   */
  public String getFirstName() {
    return firstNameField.getText();
  }

  /**
   * Returns the last name from the sign up field.
   *
   * @return the last name
   */
  public String getLastName() {
    return lastNameField.getText();
  }

  /**
   * Returns the email from the sign up field.
   *
   * @return the email
   */
  public String getEmail() {
    return emailField.getText();
  }

  /**
   * Returns the password from the sign up field.
   *
   * @return the password
   */
  public String getPassword() {
    return passwordField.getText();
  }

  /**
   * Returns the repeated password from the sign up field.
   *
   * @return the repeated password
   */
  public String getRepeatPassword() {
    return repeatPasswordField.getText();
  }

  /**
   * Notifies all observers that the user has signed up successfully, so the user can be redirected
   * to the login view.
   */
  public void notifyOfSignup() {
    notifyObservers("log_out");
  }

  /**
   * Clears the fields in the sign up view.
   */
  public void clearFields() {
    firstNameField.clear();
    lastNameField.clear();
    emailField.clear();
    passwordField.clear();
    repeatPasswordField.clear();
  }

  /**
   * Handles the error message by setting the text of the error message text field.
   *
   * @param message the message to set the error message text field to
   */
  public void showErrorMessage(String message) {
    errorMessage.setText(message);
  }
}