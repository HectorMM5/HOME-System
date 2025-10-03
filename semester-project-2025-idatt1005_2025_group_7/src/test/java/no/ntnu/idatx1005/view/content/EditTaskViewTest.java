package no.ntnu.idatx1005.view.content;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import javafx.scene.Scene;
import javafx.stage.Stage;
import no.ntnu.idatx1005.model.task.Priority;
import no.ntnu.idatx1005.model.task.Size;
import no.ntnu.idatx1005.model.task.Task;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.time.LocalDateTime;
import java.util.UUID;

class EditTaskViewTest extends ApplicationTest {

  private EditTaskView editTaskView;
  private Task testTask;

  @Override
  public void start(Stage stage) {
    // Create a test Task
    testTask = new Task(UUID.randomUUID(), false, "Test Task", "This is a test task description.",
        LocalDateTime.now(), LocalDateTime.now().plusDays(7), Priority.MEDIUM, Size.S
    );

    editTaskView = new EditTaskView(testTask);
    Scene scene = new Scene(editTaskView, 800, 600);
    stage.setScene(scene);
    stage.show();
  }

  @Test
  void testInitialization() {
    assertEquals(testTask.getName(), editTaskView.getTaskTitle(), "Task title should match.");
    assertEquals(testTask.getDescription(), editTaskView.getTaskDescription(),
        "Task description should match.");
    assertEquals(testTask.getSize(), editTaskView.getTaskSize(), "Task size should match.");
    assertEquals(testTask.getPriority(), editTaskView.getTaskPriority(),
        "Task priority should match.");
    assertEquals(testTask.getCreatedDate().toLocalDate(),
        editTaskView.getTaskCreatedDate().toLocalDate(), "Created date should match.");
    assertEquals(testTask.getDueDate().toLocalDate(), editTaskView.getTaskDueDate().toLocalDate(),
        "Due date should match.");
    assertFalse(editTaskView.getTaskCompleted(), "Task should not be completed initially.");
  }

  @Test
  void getViewTest() {
    assertEquals(editTaskView, editTaskView.getView(),
        "getView() should return the EditTaskView instance.");
  }
}