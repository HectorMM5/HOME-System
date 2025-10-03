package no.ntnu.idatx1005.view.content;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import no.ntnu.idatx1005.observer.ButtonClickObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;


class SidebarViewTest extends ApplicationTest {

  private SidebarView sidebarView;
  private ButtonClickObserver observer;

  @Override
  public void start(Stage stage) {
    sidebarView = new SidebarView();
    Scene scene = new Scene(sidebarView, 800, 600);
    stage.setScene(scene);
    stage.show();
  }

  @BeforeEach
  void setUp() {
    observer = mock(ButtonClickObserver.class);
  }

  @Test
  void testInitialization() {
    assertInstanceOf(VBox.class, sidebarView, "SidebarView should be an instance of VBox.");
    assertEquals(4, sidebarView.getChildren().size(),
        "SidebarView should have 3 main sections (title, buttons, spacer/logout).");
  }

  @Test
  void addRemoveAndNotifyObserverTest() {
    sidebarView.addObserver(observer);
    sidebarView.notifyObservers("all_tasks");
    verify(observer).onButtonClicked("all_tasks");
    sidebarView.removeObserver(observer);
    sidebarView.notifyObservers("open_tasks");
    verify(observer, times(0)).onButtonClicked("open_tasks");
  }

  @Test
  void testAllButtonNotifiers() {
    sidebarView.addObserver(observer);
    sidebarView.notifyObservers("all_tasks");
    verify(observer).onButtonClicked("all_tasks");

    sidebarView.notifyObservers("open_tasks");
    verify(observer).onButtonClicked("open_tasks");

    sidebarView.notifyObservers("my_tasks");
    verify(observer).onButtonClicked("my_tasks");

    sidebarView.notifyObservers("completed_tasks");
    verify(observer).onButtonClicked("completed_tasks");

    sidebarView.notifyObservers("insights");
    verify(observer).onButtonClicked("insights");

    sidebarView.notifyObservers("settings");
    verify(observer).onButtonClicked("settings");

    sidebarView.notifyObservers("logout");
    verify(observer).onButtonClicked("logout");
  }

  @Test
  void testButtonActions() {
    Platform.runLater(() -> {
      // Find and click the "All Tasks" button
      sidebarView.getChildren().stream().filter(node -> node instanceof VBox)
          .flatMap(node -> ((VBox) node).getChildren().stream()).filter(
              node -> node instanceof MFXButton && ((MFXButton) node).getText().equals("All Tasks"))
          .findFirst().ifPresent(button -> ((MFXButton) button).fire());

      ButtonClickObserver observer = mock(ButtonClickObserver.class);
      sidebarView.addObserver(observer);
      sidebarView.notifyObservers("all_tasks");
      verify(observer).onButtonClicked("all_tasks");
    });
  }
}