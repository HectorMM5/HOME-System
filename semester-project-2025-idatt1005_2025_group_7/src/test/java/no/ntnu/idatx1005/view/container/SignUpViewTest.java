package no.ntnu.idatx1005.view.container;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import no.ntnu.idatx1005.observer.ButtonClickObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

class SignUpViewTest extends ApplicationTest {

  private SignUpView signUpView;
  private MFXTextField firstNameField;
  private MFXTextField lastNameField;
  private MFXTextField emailField;
  private MFXPasswordField passwordField;
  private MFXPasswordField repeatPasswordField;
  private Text errorMessage;
  private ButtonClickObserver observer;

  @Override
  public void start(Stage stage) {
    signUpView = new SignUpView();
    Scene scene = new Scene(signUpView, 800, 600);
    stage.setScene(scene);
    stage.show();
  }

  @BeforeEach
  public void setUp() {
    observer = mock(ButtonClickObserver.class);
    firstNameField = lookup("#firstNameField").queryAs(MFXTextField.class);
    lastNameField = lookup("#lastNameField").queryAs(MFXTextField.class);
    emailField = lookup("#emailField").queryAs(MFXTextField.class);
    passwordField = lookup("#passwordField").queryAs(MFXPasswordField.class);
    repeatPasswordField = lookup("#repeatPasswordField").queryAs(MFXPasswordField.class);
    errorMessage = lookup("#errorMessage").queryAs(Text.class);
  }

  @Test
  void getViewIsNotNull() {
    assertNotNull(signUpView.getView(), "SignUpView should not be null");
  }

  @Test
  void signUpViewIsNotNull() {
    assertNotNull(signUpView, "SignUpView should not be null");
  }

  @Test
  void containsAllInputFieldsAndButton() {
    assertNotNull(firstNameField, "First name field should not be null");
    assertNotNull(lastNameField, "Last name field should not be null");
    assertNotNull(emailField, "Email field should not be null");
    assertNotNull(passwordField, "Password field should not be null");
    assertNotNull(repeatPasswordField, "Repeat password field should not be null");
  }

  @Test
  void userInputIsCapturedCorrectly() {
    // Simulate user input
    interact(() -> {
      firstNameField.setText("John");
      lastNameField.setText("Doe");
      emailField.setText("john.doe@example.com");
      passwordField.setText("Password123");
      repeatPasswordField.setText("Password123");
    });

    // Verify input values
    assertEquals("John", signUpView.getFirstName(), "First name should match input");
    assertEquals("Doe", signUpView.getLastName(), "Last name should match input");
    assertEquals("john.doe@example.com", signUpView.getEmail(), "Email should match input");
    assertEquals("Password123", signUpView.getPassword(), "Password should match input");
    assertEquals("Password123", signUpView.getRepeatPassword(),
        "Repeat password should match input");
  }

  @Test
  void addObserverAndTestNotification() {
    signUpView.addObserver(observer);
    signUpView.notifyObservers("log_in");
    signUpView.notifyOfSignup();
    verify(observer).onButtonClicked("log_in");
  }

  @Test
  void removeObserverTest() {
    signUpView.addObserver(observer);
    signUpView.removeObserver(observer);
    signUpView.notifyObservers("log_in");
    verify(observer, times(0)).onButtonClicked("log_in");
  }

  @Test
  void showErrorMessageTest() {
    String errorMessage = "Invalid input";
    interact(() -> signUpView.showErrorMessage(errorMessage));
    assertEquals(errorMessage, this.errorMessage.getText(),
        "showErrorMessage() should set the error message text");
  }
}