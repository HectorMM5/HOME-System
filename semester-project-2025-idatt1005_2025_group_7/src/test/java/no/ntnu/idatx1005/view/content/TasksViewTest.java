package no.ntnu.idatx1005.view.content;

import io.github.palexdev.materialfx.controls.MFXTableView;
import java.time.LocalDateTime;
import java.util.UUID;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.stage.Stage;
import no.ntnu.idatx1005.model.task.Priority;
import no.ntnu.idatx1005.model.task.Size;
import no.ntnu.idatx1005.model.task.Task;
import no.ntnu.idatx1005.observer.ButtonClickObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TasksViewTest extends ApplicationTest {

  private TasksView tasksView;

  private ObservableList<Task> testTodaysTasks;
  private ObservableList<Task> testWeeklyTasks;
  private ObservableList<Task> testAllTasks;
  private ObservableList<Task> testCompletedTasks;
  private ObservableList<Task> testOpenTasks;

  private ButtonClickObserver observer;
  private Task testTask;

  @Override
  public void start(Stage stage) {
    tasksView = new TasksView();
    Scene scene = new Scene(tasksView, 800, 600);
    stage.setScene(scene);
    stage.show();
  }

  @BeforeEach
  void setUp() {
    observer = mock(ButtonClickObserver.class);

    Task task1 = new Task(UUID.randomUUID(), false, "Task 1", "Description 1", LocalDateTime.now(),
        LocalDateTime.now().plusDays(1), Priority.HIGH, Size.M);
    Task task2 = new Task(UUID.randomUUID(), false, "Task 2", "Description 2", LocalDateTime.now(),
        LocalDateTime.now().plusDays(7), Priority.MEDIUM, Size.S);
    Task task3 = new Task(UUID.randomUUID(), true, "Task 3", "Description 3",
        LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(3), Priority.LOW, Size.L);

    testTodaysTasks = FXCollections.observableArrayList(task1);
    testWeeklyTasks = FXCollections.observableArrayList(task2);
    testAllTasks = FXCollections.observableArrayList(task1, task2, task3);
    testCompletedTasks = FXCollections.observableArrayList(task3);
    testOpenTasks = FXCollections.observableArrayList(task1, task2);
  }

  @Test
  void addRemoveAndNotifyObserver() {
    tasksView.addObserver(observer);
    tasksView.notifyObserversWithTask("button1", testTask);
    verify(observer, times(1)).onButtonClickedWithTask("button1", testTask);

    tasksView.removeObserver(observer);
    tasksView.notifyObserversWithTask("button2", testTask);
    verify(observer, times(0)).onButtonClickedWithTask("button2", testTask);
  }

  @Test
  void testInitialization() {
    assertNotNull(tasksView);
    assertTrue(tasksView.getStyleClass().contains("content"));
  }

  @Test
  void testPopulateTables() {
    tasksView.setFormatAssigneeNames(obj -> obj != null ? obj.toString() : "No Assignee");
    Platform.runLater(() -> {
      tasksView.refreshTasksTable(testTodaysTasks, testWeeklyTasks, testAllTasks,
          testCompletedTasks, testOpenTasks);

      // Test Today's Tasks Table
      tasksView.myTasksView();
      MFXTableView<Task> todaysTasksTable = (MFXTableView<Task>) tasksView.getChildren().getFirst();
      assertEquals(1, todaysTasksTable.getItems().size(),
          "Today's tasks table should have 1 task.");
      assertEquals("Task 1", todaysTasksTable.getItems().getFirst().getName(),
          "Task name should match.");

      // Test Weekly Tasks Table
      MFXTableView<Task> weeklyTasksTable = (MFXTableView<Task>) tasksView.getChildren().get(1);
      assertEquals(1, weeklyTasksTable.getItems().size(), "Weekly tasks table should have 1 task.");
      assertEquals("Task 2", weeklyTasksTable.getItems().getFirst().getName(),
          "Task name should match.");

      // Test All Tasks Table
      tasksView.allTasksView();
      MFXTableView<Task> allTasksTable = (MFXTableView<Task>) tasksView.getChildren().getFirst();
      assertEquals(3, allTasksTable.getItems().size(), "All tasks table should have 3 tasks.");

      // Test Completed Tasks Table
      tasksView.completedTasksView();
      MFXTableView<Task> completedTasksTable = (MFXTableView<Task>) tasksView.getChildren().getFirst();
      assertEquals(1, completedTasksTable.getItems().size(),
          "Completed tasks table should have 1 task.");
      assertEquals("Task 3", completedTasksTable.getItems().getFirst().getName(),
          "Task name should match.");

      // Test Open Tasks Table
      tasksView.openTasksView();
      MFXTableView<Task> openTasksTable = (MFXTableView<Task>) tasksView.getChildren().getFirst();
      assertEquals(2, openTasksTable.getItems().size(), "Open tasks table should have 2 tasks.");
    });
  }
}