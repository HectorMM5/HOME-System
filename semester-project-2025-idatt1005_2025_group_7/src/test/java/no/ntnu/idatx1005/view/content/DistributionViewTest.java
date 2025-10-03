package no.ntnu.idatx1005.view.content;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.UUID;
import javafx.scene.Scene;
import javafx.stage.Stage;
import no.ntnu.idatx1005.model.task.Priority;
import no.ntnu.idatx1005.model.task.Size;
import no.ntnu.idatx1005.model.task.Task;
import no.ntnu.idatx1005.model.user.User;
import no.ntnu.idatx1005.observer.ButtonClickObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

class DistributionViewTest extends ApplicationTest {

  private DistributionView distributionView;
  private ButtonClickObserver observer;
  private Task task;
  private User testUser;

  @Override
  public void start(Stage stage) {
    distributionView = new DistributionView();
    Scene scene = new Scene(distributionView, 800, 600);
    stage.setScene(scene);
    stage.show();
  }

  @BeforeEach
  void setUp() {
    testUser = new User(UUID.randomUUID(), "Test", "Test", "test@test.com", new byte[]{1, 2, 3},
        new byte[]{4, 5, 6}, 100, false);
    observer = mock(ButtonClickObserver.class);
    task = new Task(UUID.randomUUID(), false, "Test task", "Test description", LocalDateTime.now(),
        LocalDateTime.now().plusDays(1), Priority.HIGH, Size.S);
  }

  @Test
  void getViewTest() {
    assertEquals(DistributionView.class, distributionView.getClass(),
        "DistributionView should be a instance of itself.");
  }

  @Test
  void testInitialization() {
    assertEquals(2, distributionView.getChildren().size(),
        "DistributionView should have 2 main sections (users and tasks).");
  }

  @Test
  void testRefreshView() {
    distributionView.refreshView();
    assertEquals(2, distributionView.getChildren().size(),
        "DistributionView should have 2 main sections after refresh.");
  }

  @Test
  void addObserverAndTestNotification() {
    distributionView.addObserver(observer);
    String buttonId = "testButton";
    distributionView.notifyObserversWithTask(buttonId, task);
    verify(observer).onButtonClickedWithTask(buttonId, task);
  }

  @Test
  void removeObserver() {
    distributionView.addObserver(observer);
    distributionView.removeObserver(observer);
    distributionView.notifyObserversWithTask("testButton", task);
    verify(observer, times(0)).onButtonClickedWithTask("testButton", task);
  }

  @Test
  void setTaskDropCallback() {
    distributionView.setTaskDropCallback((user, task) -> assertEquals(testUser, user,
        "The user should be the same as the one passed to the callback."));
  }
}