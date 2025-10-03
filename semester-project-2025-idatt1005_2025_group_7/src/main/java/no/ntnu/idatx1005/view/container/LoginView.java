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
 * <h3>View for the login page.</h3>
 *
 * <p>The view extends the {@link VBox} class. The view is meant to be used as a child of the
 * {@link MainView} class.
 *
 * @see VBox
 * @see MainView
 * @author Tord Fosse
 * @author William Holtsdalen
 * @since V0.1.0
 */
public class LoginView extends VBox implements ButtonClickSubject {
  private final List<ButtonClickObserver> observers;

  private VBox loginBox;
  private MFXTextField emailField;
  private MFXPasswordField passwordField;
  private Text errorMessage;

  /**
   * Constructs a new login view.
   *
   */
  public LoginView() {
    this.observers = new ArrayList<>();

    initializeLoginBox();

    // For test
    emailField.setId("emailField");
    passwordField.setId("passwordField");
    errorMessage.setId("errorMessage");

    this.setAlignment(Pos.CENTER);
    this.getChildren().setAll(loginBox);
  }

  /**
   * Returns the login view.
   *
   * @return the login view
   */
  public VBox getView() {
    return this;
  }

  /**
   * Adds an observer to the login view.
   *
   * @param observer the observer to add
   */
  @Override
  public void addObserver(ButtonClickObserver observer) {
    observers.add(observer);
  }

  /**
   * Removes an observer from the login view.
   *
   * @param observer the observer to remove
   */
  @Override
  public void removeObserver(ButtonClickObserver observer) {
    observers.remove(observer);
  }

  /**
   * Notifies the observers with a task.
   *
   * @param buttonId the button id
   * @param task the task
   */
  @Override
  public void notifyObserversWithTask(String buttonId, Task task) {
    // Not needed
  }

  /**
   * Notifies the observers with only a button id.
   *
   * @param buttonId the button id
   */
  @Override
  public void notifyObservers(String buttonId) {
    new ArrayList<>(observers).forEach(observer -> observer.onButtonClicked(buttonId));
  }

  /**
   * Initializes the login box by creating all the components.
   */
  private void initializeLoginBox() {
    Text title = new Text("H.O.M.E");
    title.getStyleClass().add("login-title");

    Region spacer = new Region();
    spacer.setMinHeight(40);

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

    Text noAccount = new Text("Don't have an account? Sign up ");
    noAccount.getStyleClass().add("login-text");
    Hyperlink noAccountLink = new Hyperlink("here");
    noAccountLink.setOnAction(event -> notifyObservers("sign_up"));

    HBox noAccountContainer = new HBox(noAccount, noAccountLink);
    noAccountContainer.setAlignment(Pos.CENTER);

    MFXButton loginButton = new MFXButton("Log in");
    loginButton.getStyleClass().add("login-button");
    loginButton.setOnAction(event -> notifyObservers("log_in_clicked"));
    loginButton.setStyle("-fx-border-color: #D9D9D9; -fx-border-width: 1px; -fx-border-radius: 5;");

    errorMessage = new Text();
    errorMessage.getStyleClass().add("login-error-text");

    loginBox = new VBox(title, spacer, emailField, passwordField, errorMessage, loginButton,
        noAccountContainer);
    loginBox.setAlignment(Pos.CENTER);
    loginBox.setSpacing(10);
    loginBox.getStyleClass().add("login-box");
  }

  /**
   * Returns the email from the email field.
   *
   * @return the email from the email field
   */
  public String getEmail() {
    return emailField.getText();
  }

  /**
   * Returns the password from the password field.
   *
   * @return the password from the password field
   */
  public String getPassword() {
    return passwordField.getText();
  }

  /**
   * Clears the email and password fields.
   */
  public void clearFields() {
    emailField.clear();
    passwordField.clear();
  }
  
  /**
   * Handles the error message by setting the text of the error message text field.
   *
   * @param message the message to set the error message text field to
   */
  public void showErrorMessage(String message) {
    errorMessage.setText(message);
  }

  /**
   * Notifies the observers with a login.
   */
  public void notifyOfLogin() {
    notifyObservers("log_in");
  }
}
