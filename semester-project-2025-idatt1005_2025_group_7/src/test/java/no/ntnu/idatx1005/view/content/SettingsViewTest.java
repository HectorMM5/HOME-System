package no.ntnu.idatx1005.view.content;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.UUID;
import javafx.scene.Scene;
import javafx.stage.Stage;
import no.ntnu.idatx1005.model.user.User;
import no.ntnu.idatx1005.observer.ButtonClickObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

class SettingsViewTest extends ApplicationTest {

  private SettingsView settingsView;
  private ButtonClickObserver observer;
  private User testUser;

  @Override
  public void start(Stage stage) {
    testUser = new User(
        UUID.randomUUID(), "Test", "Test", "test@test.com", new byte[]{1, 2, 3},
        new byte[]{4, 5, 6}, 100, false);
    settingsView = new SettingsView(testUser);
    Scene scene = new Scene(settingsView, 800, 600);
    stage.setScene(scene);
    stage.show();
  }

  @BeforeEach
  void setUp() {
    observer = mock(ButtonClickObserver.class);
  }

  @Test
  void initializeTest() {
    assertNotNull(settingsView);
    assertEquals(1, settingsView.getChildren().size());
  }

  @Test
  void addObserverAndNotifyTest() {
    settingsView.addObserver(observer);
    settingsView.notifyObservers("change_password");
    verify(observer).onButtonClicked("change_password");
  }

  @Test
  void removeObserverTest() {
    settingsView.addObserver(observer);
    settingsView.removeObserver(observer);
    settingsView.notifyObservers("change_password");
    verify(observer, times(0)).onButtonClicked("change_password");
  }

  @Test
  void gettersTest() {
    assertEquals(settingsView.getCurrentUser(), testUser);
    assertEquals(settingsView.getFirstName(), "Test");
    assertEquals(settingsView.getLastName(), "Test");
    assertEquals(settingsView.getEmail(), "test@test.com");
    assertEquals(settingsView.getCurrentPassword(), "");
    assertEquals(settingsView.getNewPassword(), "");
    assertEquals(settingsView.getConfirmPassword(), "");
    assertEquals(settingsView.getTaskCapacity(), 100);
    assertFalse(settingsView.getSicknessStatus());
  }
}