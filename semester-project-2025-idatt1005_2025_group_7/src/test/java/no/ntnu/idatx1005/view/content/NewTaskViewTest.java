package no.ntnu.idatx1005.view.content;

import static org.junit.jupiter.api.Assertions.*;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.scene.Scene;

import javafx.stage.Stage;
import no.ntnu.idatx1005.model.task.Priority;
import no.ntnu.idatx1005.model.task.Size;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;


class NewTaskViewTest extends ApplicationTest {

  private NewTaskView newTaskView;
  private MFXComboBox<Size> sizeField;
  private MFXComboBox<Priority> priorityField;
  private MFXDatePicker dueDateField;

  @Override
  public void start(Stage stage) {
    newTaskView = new NewTaskView();
    Scene scene = new Scene(newTaskView, 800, 600);
    stage.setScene(scene);
    stage.show();
  }

  @BeforeEach
  void setUp() {
    sizeField = lookup("#sizeField").query();
    priorityField = lookup("#priorityField").query();
    dueDateField = lookup("#dueDateField").query();
  }

  @Test
  void getViewTest() {
    assertEquals(newTaskView, newTaskView.getView(),
        "getView() should return the NewTaskView instance");
  }

  @Test
  void testGetTaskTitle() {
    assertTrue(newTaskView.getTaskTitle().isEmpty());
  }

  @Test
  void testGetTaskDescription() {
    assertTrue(newTaskView.getTaskDescription().isEmpty());
  }

  @Test
  void testResetForm() {
    newTaskView.resetForm();

    assertTrue(newTaskView.getTaskTitle().isEmpty());
    assertTrue(newTaskView.getTaskDescription().isEmpty());
  }

  @Test
  void getSizePriorityAndDueDateTest() {
    interact(() -> {
      sizeField.setValue(Size.S);
      priorityField.setValue(Priority.LOW);
      dueDateField.setValue(java.time.LocalDate.now());
    });

    assertSame(newTaskView.getTaskSize(), sizeField.getValue());
    assertSame(newTaskView.getTaskPriority(), priorityField.getValue());
    assertEquals(LocalDateTime.parse(dueDateField.getValue().toString() + " 00:00:00",
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), newTaskView.getTaskDueDate());
  }
}