package no.ntnu.idatx1005.view.content;

import static org.junit.jupiter.api.Assertions.*;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

class InsightsViewTest extends ApplicationTest {

  private InsightsView insightsView;

  @Override
  public void start(Stage stage) {
    insightsView = new InsightsView();
    Scene scene = new Scene(insightsView, 800, 600);
    stage.setScene(scene);
    stage.show();
  }

  @Test
  void initializeInsightsLayout() {
    assertEquals(1, insightsView.getChildren().size(), "InsightsView should have one child");
  }

  @Test
  void tasksByPriorityView() {
    VBox tasksByPriorityView = insightsView.tasksByPriorityView();
    assertInstanceOf(VBox.class, tasksByPriorityView,
        "tasksByPriorityView() should return a VBox");
  }
}