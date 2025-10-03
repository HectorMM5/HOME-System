package no.ntnu.idatx1005.view.content;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import no.ntnu.idatx1005.model.task.Task;
import no.ntnu.idatx1005.model.user.User;
import no.ntnu.idatx1005.observer.ButtonClickObserver;
import no.ntnu.idatx1005.observer.ButtonClickSubject;
import no.ntnu.idatx1005.service.DatabaseService;
import no.ntnu.idatx1005.view.component.AvailableUserCard;
import no.ntnu.idatx1005.view.component.UnassignedTaskCard;
import no.ntnu.idatx1005.view.container.ContentView;

/**
 * <h3>View for the distribution view.</h3>
 *
 * <p>The view is meant to be used as a child of the {@link ContentView} class. The view is a
 * {@link HBox} that contains two {@link VBox} containers: one for the available users and one for
 * the unassigned tasks. It implements the {@link ButtonClickSubject} interface to notify observers
 * when a button in the view is clicked.
 *
 * @see HBox
 * @see ContentView
 * @see ButtonClickSubject
 * @author William Holtsdalen
 * @since V1.1.0
 */
public class DistributionView extends HBox implements ButtonClickSubject {
  private final DatabaseService dbService;
  private final List<ButtonClickObserver> observers;

  private BiConsumer<User, String> taskDropCallback;

  /**
   * Constructs a new distribution view.
   */
  public DistributionView() {
    this.dbService = DatabaseService.getInstance();
    this.observers = new ArrayList<>();

    this.getStyleClass().add("content");
    this.setSpacing(10);

    initialize();
  }

  /**
   * Adds an observer to the distribution view.
   *
   * @param observer the observer to add
   */
  @Override
  public void addObserver(ButtonClickObserver observer) {
    observers.add(observer);
  }

  /**
   * Removes an observer from the distribution view.
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
   * Initializes the containers in the distribution view.
   */
  private void initialize() {
    Label availableUsersLabel = new Label("Available users");
    availableUsersLabel.getStyleClass().add("distribution-available-users-label");
    VBox usersContainer = new VBox(availableUsersLabel, createUsersContainer());
    usersContainer.getStyleClass().add("distribution-users-container");

    Label unassignedTasksLabel = new Label("Unassigned tasks");
    unassignedTasksLabel.getStyleClass().add("distribution-unassigned-tasks-label");
    VBox tasksContainer = new VBox(unassignedTasksLabel, createUnassignedTasksContainer());
    tasksContainer.getStyleClass().add("distribution-unassigned-tasks-container");
    HBox.setHgrow(tasksContainer, Priority.ALWAYS);
    VBox.setVgrow(tasksContainer, Priority.ALWAYS);

    this.getChildren().setAll(usersContainer, tasksContainer);
  }

  /**
   * Returns the distribution view.
   *
   * @return the distribution view
   */
  public HBox getView() {
    return this;
  }

  /**
   * Creates the users container.
   *
   * @return the users container
   */
  private VBox createUsersContainer() {
    VBox usersContainer = new VBox();
    HBox.setHgrow(usersContainer, Priority.ALWAYS);
    VBox.setVgrow(usersContainer, Priority.ALWAYS);
    usersContainer.getStyleClass().add("distribution-users-container");

    ScrollPane usersScrollPane = new ScrollPane();
    usersScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    usersScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    usersScrollPane.getStyleClass().add("distribution-users-scroll-pane");
    usersScrollPane.setFitToWidth(true);

    VBox usersScrollPaneContent = new VBox();
    usersScrollPaneContent.getStyleClass().add("distribution-users-scroll-pane-content");
    for (User user : dbService.getAllUsersByCapacity()) {
      if (dbService.getUserAvailableCapacity(user) == 0) {
        continue;
      }
      AvailableUserCard userCard = new AvailableUserCard(user, dbService.getUserWorkload(user));
      // Using runLater here to make sure the callback has been set before setting it on the card.
      Platform.runLater(() -> userCard.setOnDragDropped(taskDropCallback));
      usersScrollPaneContent.getChildren().add(userCard);
    }
    usersScrollPane.setContent(usersScrollPaneContent);
    usersContainer.getChildren().add(usersScrollPane);
    return new VBox(usersContainer);
  }

  /**
   * Creates the unassigned tasks container.
   *
   * @return the unassigned tasks container
   */
  private VBox createUnassignedTasksContainer() {
    VBox tasksContainer = new VBox();
    HBox.setHgrow(tasksContainer, Priority.ALWAYS);
    VBox.setVgrow(tasksContainer, Priority.ALWAYS);
    tasksContainer.getStyleClass().add("distribution-unassigned-tasks-container");

    ScrollPane tasksScrollPane = new ScrollPane();
    tasksScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    tasksScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    tasksScrollPane.getStyleClass().add("distribution-tasks-scroll-pane");
    tasksScrollPane.setFitToWidth(true);
    tasksScrollPane.setFitToHeight(true);
    VBox.setVgrow(tasksScrollPane, Priority.ALWAYS);


    VBox tasksScrollPaneContent = new VBox();
    VBox.setVgrow(tasksScrollPaneContent, Priority.ALWAYS);
    tasksScrollPaneContent.getStyleClass().add("distribution-tasks-scroll-pane-content");
    for (Task task : dbService.getUnassignedTasksBySizeAndPriority()) {
      UnassignedTaskCard taskCard = new UnassignedTaskCard(task);
      taskCard.setOnMouseClicked(mouseEvent ->
          notifyObserversWithTask("edit_task", task));
      tasksScrollPaneContent.getChildren().add(taskCard);
    }
    tasksScrollPane.setContent(tasksScrollPaneContent);
    tasksContainer.getChildren().add(tasksScrollPane);
    return tasksContainer;
  }

  /**
   * Shows a confirmation dialog for an assignment.
   *
   * @param user the user
   * @param task the task
   * @return true if the user confirms the assignment, false otherwise
   */
  public boolean showConfirmAssignmentDialog(User user, Task task) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Confirm Assignment");
    alert.setHeaderText("Confirm task assignment");
    alert.setContentText(String.format("Do you want to assign task '%s' to %s %s?",
            task.getName(), user.getFirstName(), user.getLastName()));
    
    return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
  }

  /**
   * Shows a dialog when an invalid assignment is attempted.
   *
   * @param user the user
   * @param task the task
   */
  public void showInvalidAssignmentDialog(User user, Task task) {
    Platform.runLater(() -> {
      Alert alert = new Alert(Alert.AlertType.WARNING);
      alert.setTitle("Invalid Assignment");
      alert.setHeaderText(String.format("Cannot assign task '%s' to user", task.getName()));
      alert.setContentText(String.format("User %s %s does not have enough capacity for this task.",
              user.getFirstName(), user.getLastName()));
      alert.showAndWait();
    });
  }

  /**
   * Sets the task drop callback.
   *
   * @param callback the callback to set
   */
  public void setTaskDropCallback(BiConsumer<User, String> callback) {
    this.taskDropCallback = callback;
  }

  /**
   * Shows a dialog when no assignments are possible.
   */
  public void showNoAssignmentsPossibleDialog() {
    Platform.runLater(() -> {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("No Assignments Possible");
      alert.setHeaderText("Cannot distribute tasks");
      alert.setContentText("No suitable assignments could be found. Users may not have enough "
          + "capacity.");
      alert.showAndWait();
    });
  }

  /**
   * Shows a confirmation dialog for auto-distribution.
   *
   * @param assignments the assignments
   * @return true if the user confirms the auto-distribution, false otherwise
   */
  public boolean showConfirmAutoDistributionDialog(List<Pair<Task, User>> assignments) {
    StringBuilder message = new StringBuilder("The following assignments will be made:\n\n");
    assignments.forEach(pair -> {
      Task task = pair.getKey();
      User user = pair.getValue();
      message.append(String.format("- Task '%s' to %s %s%n",
              task.getName(), user.getFirstName(), user.getLastName()));
    });
    
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Confirm Auto-Distribution");
    alert.setHeaderText("Confirm task assignments");
    alert.setContentText(message.toString());
    
    return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
  }

  /**
   * Refreshes the view with current cached data.
   */
  public void refreshView() {
    Platform.runLater(this::initialize);
  }
}
