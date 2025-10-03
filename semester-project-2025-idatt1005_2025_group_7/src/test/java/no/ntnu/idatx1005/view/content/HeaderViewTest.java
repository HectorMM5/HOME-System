package no.ntnu.idatx1005.view.content;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import no.ntnu.idatx1005.observer.ButtonClickObserver;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;


class HeaderViewTest extends ApplicationTest {

  private HeaderView headerView;

  @Override
  public void start(Stage stage) {
    headerView = new HeaderView();
    Scene scene = new Scene(headerView, 800, 600);
    stage.setScene(scene);
    stage.show();
  }

  @Test
  void testInitialization() {
    assertInstanceOf(HBox.class, headerView, "HeaderView should be an instance of HBox.");
    assertEquals(0, headerView.getChildren().size(),
        "HeaderView should initially have no children.");
  }

  @Test
  void testObserverNotification() {
    ButtonClickObserver observer = mock(ButtonClickObserver.class);

    headerView.addObserver(observer);
    headerView.notifyObservers("test_button");
    verify(observer).onButtonClicked("test_button");
  }

  @Test
  void testShowMainViewHeader() {
    Platform.runLater(() -> {
      headerView.showMainViewHeader();

      assertEquals(3, headerView.getChildren().size(),
          "HeaderView should have 3 children in the main view header.");
    });
  }

  @Test
  void addRemoveAndNotifyObserverTest() {
    ButtonClickObserver observer = mock(ButtonClickObserver.class);
    headerView.addObserver(observer);
    headerView.notifyObservers("back_button");
    verify(observer).onButtonClicked("back_button");

    headerView.removeObserver(observer);
    headerView.notifyObservers("new_task");
    verify(observer, times(0)).onButtonClicked("new_task");
  }

  @Test
  void showNewTaskViewHeaderTest() {
    Platform.runLater(() -> {
      headerView.showNewTaskViewHeader();
      assertEquals(3, headerView.getChildren().size(),
          "HeaderView should have 3 children in the new task view header.");
    });
  }

  @Test
  void showEditTaskViewHeaderTest() {
    Platform.runLater(() -> {
      headerView.showEditTaskViewHeader();
      assertEquals(3, headerView.getChildren().size(),
          "HeaderView should have 3 children in the edit task view header.");
    });
  }

  @Test
  void showDistributionViewHeaderTest() {
    Platform.runLater(() -> {
      headerView.showDistributionViewHeader();
      assertEquals(3, headerView.getChildren().size(),
          "HeaderView should have 3 children in the distribution view header.");
    });
  }
}