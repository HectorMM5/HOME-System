package no.ntnu.idatx1005.view.container;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import no.ntnu.idatx1005.observer.ButtonClickObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

class LoginViewTest extends ApplicationTest {

  private LoginView loginView;
  private MFXTextField emailField;
  private MFXPasswordField passwordField;
  private Text errorMessage;
  private ButtonClickObserver observer;

  @Override
  public void start(Stage stage) {
    loginView = new LoginView();
    StackPane root = new StackPane(loginView);
    Scene scene = new Scene(root, 800, 600);
    stage.setScene(scene);
    stage.show();
  }

  @BeforeEach
  public void setUp() {
    observer = mock(ButtonClickObserver.class);
    emailField = lookup("#emailField").queryAs(MFXTextField.class);
    passwordField = lookup("#passwordField").queryAs(MFXPasswordField.class);
    errorMessage = lookup("#errorMessage").queryAs(Text.class);
  }

  @Test
  void getViewTest() {
    assertEquals(loginView, loginView.getView(),
        "getView() should return the LoginView instance itself");
  }

  @Test
  void getEmailTest() {
    interact(() -> emailField.setText("test@example.com"));
    assertEquals("test@example.com", loginView.getEmail(),
        "getEmail() should return the text in the email field");
  }

  @Test
  void getPasswordTest() {
    interact(() -> passwordField.setText("password123"));
    assertEquals("password123", loginView.getPassword(),
        "getPassword() should return the text in the password field");
  }

  @Test
  void clearFieldsTest() {
    interact(() -> {
      emailField.setText("test@example.com");
      passwordField.setText("password123");
    });

    interact(() -> loginView.clearFields());

    assertEquals("", loginView.getEmail(),
        "Email field should be empty after clearFields()");
    assertEquals("", loginView.getPassword(),
        "Password field should be empty after clearFields()");
  }

  @Test
  void showErrorMessageTest() {
    String errorMessage = "Invalid email or password";
    interact(() -> loginView.showErrorMessage(errorMessage));
    assertEquals(errorMessage, this.errorMessage.getText(),
        "showErrorMessage() should set the error message text");
  }

  @Test
  void addObserverAndNotifyOfLoginTest() {
    loginView.addObserver(observer);
    loginView.notifyObservers("log_in");
    loginView.notifyOfLogin();
    verify(observer, times(2)).onButtonClicked("log_in");
  }

  @Test
  void removeObserverTest() {
    loginView.addObserver(observer);
    loginView.removeObserver(observer);
    loginView.notifyObservers("log_in");
    verify(observer, times(0)).onButtonClicked("log_in");
  }
}