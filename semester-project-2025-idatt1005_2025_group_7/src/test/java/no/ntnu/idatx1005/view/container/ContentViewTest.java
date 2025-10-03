package no.ntnu.idatx1005.view.container;

import static org.junit.jupiter.api.Assertions.*;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import no.ntnu.idatx1005.view.content.HeaderView;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

class ContentViewTest extends ApplicationTest {

  private ContentView contentView;

  @Override
  public void start(Stage stage) {
    contentView = new ContentView();
    StackPane root = new StackPane(contentView);
    Scene scene = new Scene(root, 800, 600);
    stage.setScene(scene);
    stage.show();
  }

  @Test
  void showView() {
    Node view = new VBox();
    interact(() -> contentView.showView(view));

    assertTrue(contentView.getChildren().contains(view),
        "ContentView should contain view after showView() is called");

    long headerViewCount = contentView.getChildren().stream()
        .filter(node -> node instanceof HeaderView).count();
    assertEquals(1, headerViewCount,
        "ContentView should contain exactly one HeaderView");
  }

  @Test
  void getView() {
    assertEquals(contentView, contentView.getView(),
        "getView() should return the view itself");
  }

  @Test
  void getHeaderView() {
    assertNotNull(contentView.getHeaderView(),
        "HeaderView should be initialized");
  }

  @Test
  void verifyInitialState() {
    assertEquals(0, contentView.getChildren().size(),
        "ContentView should not contain any nodes");

    assertNotNull(contentView, "ContentView should not be null");
  }

  @Test
  void multipleShowViewCalls() {
    Node firstView = new VBox();
    Node secondView = new VBox();

    interact(() -> contentView.showView(firstView));
    interact(() -> contentView.showView(secondView));

    assertFalse(contentView.getChildren().contains(firstView),
        "ContentView should not contain the first view after showing a second view");
    assertTrue(contentView.getChildren().contains(secondView),
        "ContentView should contain the second view");
    assertEquals(2, contentView.getChildren().size(),
        "ContentView should contain exactly 2 nodes (HeaderView and the current view)");
  }
}