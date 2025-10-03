package no.ntnu.idatx1005.view.content;

import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import no.ntnu.idatx1005.model.task.Task;
import no.ntnu.idatx1005.observer.ButtonClickObserver;
import no.ntnu.idatx1005.observer.ButtonClickSubject;
import no.ntnu.idatx1005.view.container.ContentView;

/**
 * <h3>View for showing tables of tasks.</h3>
 *
 * <p>The view extends the {@link VBox} class. The view is meant to be used as a child of the
 * {@link ContentView} class. It implements the {@link ButtonClickSubject} interface to notify
 * observers when a button in the view is clicked.
 *
 * @see VBox
 * @see ContentView
 * @see ButtonClickSubject
 * @author Tord Fosse
 * @author William Holtsdalen
 * @since V0.1.0
 */
public class TasksView extends VBox implements ButtonClickSubject {
  private static final Duration TOOLTIP_DELAY = Duration.millis(300);

  private ObservableList<Task> todaysTasks = FXCollections.observableArrayList();
  private ObservableList<Task> weeklyTasks = FXCollections.observableArrayList();
  private ObservableList<Task> allTasks = FXCollections.observableArrayList();
  private ObservableList<Task> completedTasks = FXCollections.observableArrayList();
  private ObservableList<Task> openTasks = FXCollections.observableArrayList();
  private MFXTableView<Task> todaysTasksTable;
  private MFXTableView<Task> weeklyTasksTable;
  private MFXTableView<Task> allTasksTable;
  private MFXTableView<Task> completedTasksTable;
  private MFXTableView<Task> openTasksTable;
  private final LinkedHashMap<String, String> todaysTasksColumns;
  private final LinkedHashMap<String, String> weeklyTasksColumns;
  private final LinkedHashMap<String, String> allTasksColumns;
  private final LinkedHashMap<String, String> completedTasksColumns;
  private final LinkedHashMap<String, String> openTasksColumns;

  private final List<ButtonClickObserver> observers;
  private Consumer<Task> taskClickCallback;
  private Function<Task, String> formatAssigneeNames;

  /**
   * Constructs a new tasks view.
   */
  public TasksView() {
    this.observers = new ArrayList<>();

    this.todaysTasksColumns = setColumns("Today's tasks", false);
    this.weeklyTasksColumns = setColumns("This week's tasks", false);
    this.allTasksColumns = setColumns("All tasks", false);
    this.completedTasksColumns = setColumns("Completed tasks", true);
    this.openTasksColumns = setColumns("Open tasks", false);

    this.getStyleClass().add("content");
  }

  /**
   * Adds an observer to the tasks view.
   *
   * @param observer the observer to add
   */
  @Override
  public void addObserver(ButtonClickObserver observer) {
    observers.add(observer);
  }

  /**
   * Removes an observer from the tasks view.
   *
   * @param observer the observer to remove
   */
  @Override
  public void removeObserver(ButtonClickObserver observer) {
    observers.remove(observer);
  }

  /**
   * Notifies all observers with a task.
   *
   * @param buttonId the button id
   * @param task the task
   */
  @Override
  public void notifyObserversWithTask(String buttonId, Task task) {
    new ArrayList<>(observers).forEach(observer ->
        observer.onButtonClickedWithTask(buttonId, task));
  }

  /**
   * Notifies all observers with a button id.
   *
   * @param buttonId the button id
   */
  @Override
  public void notifyObservers(String buttonId) {
    // Not needed
  }

  /**
   * Sets the task click callback.
   *
   * @param callback the callback to set
   */
  public void setTaskClickCallback(Consumer<Task> callback) {
    this.taskClickCallback = callback;
  }

  /**
   * Sets the format assignee names.
   *
   * @param formatAssigneeNames the format assignee names
   */
  public void setFormatAssigneeNames(Function<Task, String> formatAssigneeNames) {
    this.formatAssigneeNames = formatAssigneeNames;
  }

  /**
   * Refreshes the tasks table.
   *
   * @param todaysTasks the todays tasks
   * @param weeklyTasks the weekly tasks
   * @param allTasks the all tasks
   * @param completedTasks the completed tasks
   * @param openTasks the open tasks
   */
  public void refreshTasksTable(ObservableList<Task> todaysTasks, ObservableList<Task> weeklyTasks,
      ObservableList<Task> allTasks, ObservableList<Task> completedTasks,
      ObservableList<Task> openTasks) {
    this.todaysTasks = todaysTasks;
    this.weeklyTasks = weeklyTasks;
    this.allTasks = allTasks;
    this.completedTasks = completedTasks;
    this.openTasks = openTasks;

    Platform.runLater(() -> {
      if (this.getChildren().contains(completedTasksTable)) {
        completedTasksView();
      }
      if (this.getChildren().contains(todaysTasksTable) && this.getChildren()
          .contains(weeklyTasksTable)) {
        myTasksView();
      }
      if (this.getChildren().contains(allTasksTable)) {
        allTasksView();
      }
      if (this.getChildren().contains(completedTasksTable)) {
        completedTasksView();
      }
      if (this.getChildren().contains(openTasksTable)) {
        openTasksView();
      }
    });
  }

  /**
   * Displays the my tasks view.
   */
  public void myTasksView() {
    todaysTasksTable = new MFXTableView<>(todaysTasks);
    todaysTasksTable.setFooterVisible(false);
    populateTable(todaysTasksTable, todaysTasksColumns);
    setClickAction(todaysTasksTable);

    weeklyTasksTable = new MFXTableView<>(weeklyTasks);
    weeklyTasksTable.setFooterVisible(false);
    populateTable(weeklyTasksTable, weeklyTasksColumns);
    setClickAction(weeklyTasksTable);

    this.getChildren().setAll(todaysTasksTable, weeklyTasksTable);
  }

  /**
   * Displays the all tasks view.
   */
  public void allTasksView() {
    allTasksTable = new MFXTableView<>(allTasks);
    allTasksTable.setFooterVisible(false);
    populateTable(allTasksTable, allTasksColumns);
    setClickAction(allTasksTable);

    this.getChildren().setAll(allTasksTable);
  }

  /**
   * Displays the completed tasks view.
   */
  public void completedTasksView() {
    completedTasksTable = new MFXTableView<>(completedTasks);
    completedTasksTable.setFooterVisible(false);
    populateTable(completedTasksTable, completedTasksColumns);
    setClickAction(completedTasksTable);

    this.getChildren().setAll(completedTasksTable);
  }

  /**
   * Displays the open tasks view.
   */
  public void openTasksView() {
    openTasksTable = new MFXTableView<>(openTasks);
    openTasksTable.setFooterVisible(false);
    populateTable(openTasksTable, openTasksColumns);
    setClickAction(openTasksTable);

    this.getChildren().setAll(openTasksTable);
  }

  /**
   * Sets the columns for the tables.
   *
   * @param tableName the name of the table
   * @return the columns for the table
   */
  private LinkedHashMap<String, String> setColumns(String tableName, boolean isCompleted) {
    LinkedHashMap<String, String> columns = new LinkedHashMap<>();
    columns.put(tableName, "name");
    columns.put("Description", "description");
    columns.put("Created date", "createdDate");
    if (isCompleted) {
      columns.put("Completed date", "completedDate");
    } else {
      columns.put("Due date", "dueDate");
    }
    columns.put("Priority", "priority");
    columns.put("Size", "size");
    columns.put("Assignee(s)", "assignedUserNames");
    return columns;
  }

  /**
   * Populates the given table with the given columns.
   *
   * @param table the table to populate
   * @param columns the columns to populate
   */
  private void populateTable(MFXTableView<Task> table, LinkedHashMap<String, String> columns) {
    for (Map.Entry<String, String> entry : columns.entrySet()) {
      MFXTableColumn<Task> column = new MFXTableColumn<>(entry.getKey(), false);
      column.setRowCellFactory(task -> {
        MFXTableRowCell<Task, String> rowCell = new MFXTableRowCell<>(taskProperty -> {
          try {
            String methodName = "get" + capitalize(entry.getValue());
            switch (methodName) {
              case "getPriority", "getSize" -> {
                return Task.class.getMethod(methodName).invoke(taskProperty)
                    .toString();
              }
              case "getDueDate", "getCreatedDate", "getCompletedDate" -> {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDateTime dateTime = (LocalDateTime) Task.class.getMethod(methodName)
                    .invoke(taskProperty);
                return dateTime.format(formatter);
              }
              case "getAssignedUserNames" -> {
                return formatAssigneeNames.apply(taskProperty);
              }
              default -> {
                return (String) Task.class.getMethod(methodName).invoke(taskProperty);
              }
            }
          } catch (Exception e) {
            return "Error";
          }
        });
        switch (entry.getValue()) {
          case "name" -> setTooltip(rowCell, task.getName());
          case "description" -> setTooltip(rowCell, task.getDescription());
          case "assignedUserNames" ->
              setTooltip(rowCell, formatAssigneeNames.apply(task));
          default -> {
            break;
          }
        }
        return rowCell;
      });
      table.getTableColumns().add(column);
    }
  }

  /**
   * Sets the tooltip for the given node.
   *
   * @param node the node to set the tooltip for
   * @param text the text to set the tooltip to
   */
  private void setTooltip(Node node, String text) {
    Tooltip tooltip = new Tooltip(text);
    tooltip.setShowDelay(TOOLTIP_DELAY);
    Tooltip.install(node, tooltip);
  }

  /**
   * Sets the click action for the given table.
   *
   * @param table the table to set the click action for
   */
  private void setClickAction(MFXTableView<Task> table) {
    table.getSelectionModel().selectionProperty().addListener((
        observable, oldSelection,
        newSelection) -> {
      if (!table.getSelectionModel().getSelectedValues().isEmpty()) {
        Task selectedTask = table.getSelectionModel().getSelectedValues().getFirst();
        if (taskClickCallback != null) {
          taskClickCallback.accept(selectedTask);
        }
      }
    });
  }

  /**
   * Capitalizes the given string.
   *
   * @param str the string to capitalize
   * @return the capitalized string
   */
  private String capitalize(String str) {
    if (str == null || str.isEmpty()) {
      return str;
    }
    return str.substring(0, 1).toUpperCase() + str.substring(1);
  }
}
