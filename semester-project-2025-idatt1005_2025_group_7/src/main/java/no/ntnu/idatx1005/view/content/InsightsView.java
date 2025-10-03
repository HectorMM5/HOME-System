package no.ntnu.idatx1005.view.content;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import no.ntnu.idatx1005.model.task.Priority;
import no.ntnu.idatx1005.model.task.Task;
import no.ntnu.idatx1005.storage.H2Manager;
import no.ntnu.idatx1005.view.container.ContentView;

/**
 * <h3>View for showing insights about tasks.</h3>
 *
 * <p>The view extends the {@link VBox} class. The view is meant to be used as a child of the
 * {@link ContentView} class.
 *
 * <p>For more information about the BarChart, see
 * <a href="https://docs.oracle.com/javafx/2/charts/bar-chart.htm">BarChart</a>.
 *
 * @see VBox
 * @see ContentView
 * @author Ola Syrstad Berg
 * @since V1.1.0
 */
public class InsightsView extends VBox {
  private ObservableList<Task> openTasks;

  /**
   * Constructs a new insights view.
   */
  public InsightsView() {

    this.getStyleClass().add("content");
    VBox.setVgrow(this, javafx.scene.layout.Priority.ALWAYS);

    fetchTasksFromDatabase();
    initializeInsightsLayout();
  }

  /**
   * Creates the layout of Insights View.
   */
  public void initializeInsightsLayout() {
    final HBox insightsLayout = new HBox(20);
    final VBox leftColumn = new VBox(15);
    final VBox rightColumn = new VBox(15);

    leftColumn.getChildren().addAll(completedTasksTodayView(), completedTasksWeekView());
    rightColumn.getChildren().addAll(tasksByPriorityView(), pendingTasksView());

    leftColumn.setPrefWidth(500);
    rightColumn.setPrefWidth(500);

    insightsLayout.getChildren().addAll(leftColumn, rightColumn);
    insightsLayout.getStyleClass().add("insights-layout");
    insightsLayout.setAlignment(Pos.TOP_CENTER);

    this.getChildren().setAll(insightsLayout);
  }

  /**
   * Creates the view for completed tasks today.
   *
   * @return The view for completed tasks today.
   */
  private VBox completedTasksTodayView() {
    VBox completedTasksTodayBox = new VBox();
    completedTasksTodayBox.getStyleClass().add("completed-tasks-today-box");
    completedTasksTodayBox.setPadding(new Insets(15));

    Label titleLabel = new Label("Completed tasks today");
    titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
    titleLabel.setAlignment(Pos.CENTER);
    titleLabel.setMaxWidth(Double.MAX_VALUE);

    completedTasksTodayBox.getChildren().addAll(titleLabel);

    List<Task> completedTasksToday = H2Manager.getTasksCompletedToday();

    if (completedTasksToday.isEmpty()) {
      Label noTasksLabel = new Label("No tasks completed today");
      noTasksLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px");
      noTasksLabel.setAlignment(Pos.CENTER);
      noTasksLabel.setMaxWidth(Double.MAX_VALUE);
      completedTasksTodayBox.getChildren().addAll(noTasksLabel);
    } else {

      for (Task task : completedTasksToday) {
        VBox taskItem = createTaskItem(task.getName());
        completedTasksTodayBox.getChildren().add(taskItem);
      }
    }

    return completedTasksTodayBox;
  }

  /**
   * Creates the view for completed tasks this week.
   *
   * @return The view for completed tasks this week.
   */
  private VBox completedTasksWeekView() {
    VBox completedTasksWeekBox = new VBox();
    completedTasksWeekBox.getStyleClass().add("completed-tasks-week-box");
    completedTasksWeekBox.setPadding(new Insets(15));

    Label titleLabel = new Label("Completed tasks this week");
    titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
    titleLabel.setAlignment(Pos.CENTER);
    titleLabel.setMaxWidth(Double.MAX_VALUE);

    completedTasksWeekBox.getChildren().addAll(titleLabel);

    List<Task> completedTasksWeekly = H2Manager.getTasksCompletedThisWeek();

    if (completedTasksWeekly.isEmpty()) {
      Label noTasksLabel = new Label("No tasks completed this week");
      noTasksLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14;");
      noTasksLabel.setAlignment(Pos.CENTER);
      noTasksLabel.setMaxWidth(Double.MAX_VALUE);
      completedTasksWeekBox.getChildren().addAll(noTasksLabel);
    } else {

      for (Task task : completedTasksWeekly) {
        VBox taskItem = createTaskItem(task.getName());
        completedTasksWeekBox.getChildren().add(taskItem);
      }
    }

    return completedTasksWeekBox;
  }

  /**
   * Temporary method for displaying tasks.
   *
   * @param taskName The name of the task displayed.
   * @return The task container.
   */
  private VBox createTaskItem(String taskName) {
    HBox taskItem = new HBox(10);
    taskItem.setAlignment(Pos.CENTER_LEFT);

    Label checkmark = new Label("✓");
    checkmark.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
    checkmark.setPadding(new Insets(4, 7, 4, 7));

    Label taskLabel = new Label(taskName);
    taskLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

    taskItem.getChildren().addAll(checkmark, taskLabel);

    VBox taskContainer = new VBox(taskItem);
    taskContainer.setPadding(new Insets(5, 0, 5, 0));

    return taskContainer;
  }

  /**
   * Creates the view for pending tasks.
   *
   * @return The pending tasks.
   */
  private VBox pendingTasksView() {
    VBox pendingTasksBox = new VBox();
    pendingTasksBox.getStyleClass().add("pending-tasks-box");
    pendingTasksBox.setPadding(new Insets(15));

    Label titleLabel = new Label("Pending tasks");
    titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
    titleLabel.setAlignment(Pos.CENTER);
    titleLabel.setMaxWidth(Double.MAX_VALUE);

    pendingTasksBox.getChildren().addAll(titleLabel);

    List<Task> pendingTasks = H2Manager.getOpenTasks();

    if (pendingTasks.isEmpty()) {
      Label noTasksLabel = new Label("No pending tasks");
      noTasksLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
      noTasksLabel.setAlignment(Pos.CENTER);
      noTasksLabel.setMaxWidth(Double.MAX_VALUE);
      pendingTasksBox.getChildren().addAll(noTasksLabel);
    } else {
      int count = 0;
      for (Task task : pendingTasks) {
        Label taskLabel = new Label(task.getName());
        taskLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        pendingTasksBox.getChildren().add(taskLabel);
        count++;
        //Limits overflow.
        if (count >= 5) {
          int remaining = pendingTasks.size() - 5;
          if (remaining > 0) {
            Label moreLabel = new Label("+" + remaining + " more");
            moreLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
            pendingTasksBox.getChildren().add(moreLabel);
          }
          break;
        }
      }
    }

    return pendingTasksBox;
  }

  /**
   * Returns the insights view with task distribution by priority.
   *
   * @return The insights view with task distribution by priority.
   */
  public VBox tasksByPriorityView() {
    final CategoryAxis xAxis = new CategoryAxis();
    final NumberAxis yAxis = new NumberAxis();
    xAxis.setLabel("Priority");
    yAxis.setLabel("Number of Tasks");

    BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
    barChart.setTitle("Pending Tasks by Priority");
    barChart.getStyleClass().add("bar-chart");

    Map<Priority, Integer> taskCountByPriority = countTaskByPriority();

    // Creates a data series
    XYChart.Series<String, Number> series = new XYChart.Series<>();
    series.setName("Tasks");

    // Adds data to series
    for (Priority priority : Priority.values()) {
      series.getData().add(new XYChart.Data<>(
          priority.toString(), taskCountByPriority.getOrDefault(priority, 0)
      ));
    }

    // Adds series to chart
    barChart.getData().add(series);
    barChart.setAnimated(false);

    // Bar colors
    barChart.lookupAll(".data0.chart-bar").forEach(node ->
        node.setStyle("-fx-bar-fill: #B5584E;")); // HIGH priority

    barChart.lookupAll(".data1.chart-bar").forEach(node ->
        node.setStyle("-fx-bar-fill: #7B7123;")); // MEDIUM priority

    barChart.lookupAll(".data2.chart-bar").forEach(node ->
        node.setStyle("-fx-bar-fill: #2E6D21;")); // LOW priority

    xAxis.setTickLabelFill(javafx.scene.paint.Color.WHITE);
    yAxis.setTickLabelFill(javafx.scene.paint.Color.WHITE);

    xAxis.setStyle("-fx-text-fill: white;");
    yAxis.setStyle("-fx-text-fill: white;");

    final Node yAxisLabel = yAxis.lookup(".axis-label");
    if (yAxisLabel != null) {
      yAxisLabel.setStyle("-fx-text-fill: white;");
    }

    final Node xAxisLabel = xAxis.lookup(".axis-label");
    if (xAxisLabel != null) {
      xAxisLabel.setStyle("-fx-text-fill: white;");
    }

    final Node chartTitle = barChart.lookup(".chart-title");
    if (chartTitle != null) {
      chartTitle.setStyle("-fx-text-fill: white;");
    }

    return new VBox(barChart);
  }

  /**
   * Counts the number of tasks for each priority.
   *
   * @return A map with priority as key and task count as value
   */
  private Map<Priority, Integer> countTaskByPriority() {
    Map<Priority, Integer> taskCountByPriority = new EnumMap<>(Priority.class);

    // Initialize counts for all priorities.
    for (Priority priority : Priority.values()) {
      taskCountByPriority.put(priority, 0);
    }

    // Counts tasks for each priority
    for (Task task : openTasks) {
      Priority priority = task.getPriority();
      taskCountByPriority.put(priority, taskCountByPriority.get(priority) + 1);
    }
    return taskCountByPriority;
  }

  /**
   * Fetches the tasks from the database.
   */
  private void fetchTasksFromDatabase() {
    List<Task> openTasksFromDb = H2Manager.getOpenTasks();
    openTasks = FXCollections.observableList(openTasksFromDb);
  }
}
