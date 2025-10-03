package no.ntnu.idatx1005.view.container;

import static org.junit.jupiter.api.Assertions.*;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

class MainViewTest extends ApplicationTest {

  private MainView mainView;

  @Override
  public void start(Stage stage) {
    mainView = new MainView();
    Scene scene = new Scene(mainView, 800, 600);
    stage.setScene(scene);
    stage.show();
  }

  @Test
  void mainViewIsNotNull() {
    assertNotNull(mainView, "MainView should not be null");
  }

  @Test
  void containsSidebarAndContentView() {
    assertTrue(mainView.getChildren().contains(mainView.getSidebarView().getView()),
        "MainView should contain the SidebarView");
    assertTrue(mainView.getChildren().stream()
            .anyMatch(node -> node instanceof VBox && ((VBox) node).getChildren()
                .contains(mainView.getContentView().getView())),
        "MainView should contain the ContentView inside a VBox");
  }

  @Test
  void getViewReturnsMainView() {
    assertEquals(mainView, mainView.getView(),
        "getView() should return the MainView instance itself");
  }
}