package no.ntnu.idatx1005.view.content;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import no.ntnu.idatx1005.model.task.Priority;
import no.ntnu.idatx1005.model.task.Size;
import no.ntnu.idatx1005.model.task.Task;
import no.ntnu.idatx1005.model.task.TaskChange;
import no.ntnu.idatx1005.model.user.User;
import no.ntnu.idatx1005.storage.H2Manager;
import no.ntnu.idatx1005.view.container.ContentView;

/**
 * <h3>View for the edit task page.</h3>
 *
 * <p>The view extends the {@link HBox} class. The view is meant to be used as a child of the
 * {@link ContentView} class.
 *
 * @author William Holtsdalen
 * @since V0.1.0
 */
public class EditTaskView extends HBox {
  private static final Color TEXT_COLOR = Color.WHITE;
  private static final String ADD_USER_ICON_NAME = "fas-plus";
  private static final String REMOVE_USER_ICON_NAME = "fas-minus";
  private static final int ICON_SIZE = 20;
  private static final Color ADD_USER_BUTTON_ICON_COLOR = Color.rgb(0, 160, 0);
  private static final Color REMOVE_USER_BUTTON_ICON_COLOR = Color.rgb(140, 0, 0);
  
  private Text completedText;
  private MFXButton toggleCompletedButton;
  private MFXTextField taskTitleField;
  private TextArea taskDescriptionField;
  private MFXComboBox<Size> sizeField;
  private MFXComboBox<Priority> priorityField;
  private MFXDatePicker createdDateField;
  private MFXDatePicker dueDateField;
  private ListView<HBox> assignedUsersListView;
  private ListView<HBox> availableUsersListView;
  private MFXTableView<TaskChange> changelogTable;
  private HBox paginationControls;
  
  private List<User> assignedUsersList;
  private final ObservableList<TaskChange> changelogEntries;
  private Task task;
  private static final int ROWS_PER_PAGE = 5;
  private int currentPage = 0;


  /**
   * Constructs a new edit task view.
   */
  public EditTaskView(Task task) {
    this.assignedUsersList = new ArrayList<>();
    this.task = task;
    this.changelogEntries = FXCollections.observableArrayList();

    this.getStyleClass().add("content");
    this.setSpacing(10);

    initializeContainers();
    initializeValues();
  }

  /**
   * Returns the edit task view.
   *
   * @return the edit task view
   */
  public EditTaskView getView() {
    return this;
  }

  /**
   * Sets the task for the edit task view.
   *
   * @param task the task to set
   */
  public void setTask(Task task) {
    this.task = task;
  }

  /**
   * Initializes the containers in the edit task view.
   */
  private void initializeContainers() {
    VBox mainContent = new VBox(30);
    mainContent.setStyle("-fx-background-color: #0A121A;");

    VBox taskInfoContainer = createTaskInfoContainer();
    VBox taskAssignmentContainer = createTaskAssignmentContainer();
    taskAssignmentContainer.maxHeightProperty().bind(taskInfoContainer.heightProperty());

    HBox topSection = new HBox(10);
    topSection.getChildren().addAll(taskInfoContainer, taskAssignmentContainer);

    VBox changelogSection = createChangelogSection();

    mainContent.getChildren().addAll(topSection, changelogSection);

    ScrollPane scrollPane = new ScrollPane();
    scrollPane.setContent(mainContent);
    scrollPane.setFitToWidth(true);
    scrollPane.setStyle("-fx-background-color: transparent; -fx-padding: 15;");
    
    this.getChildren().setAll(scrollPane);
  }

  /**
   * Creates the task info container.
   *
   * @return the task info container
   */
  private VBox createTaskInfoContainer() {
    completedText = new Text("Completed: ❌");
    completedText.setFill(TEXT_COLOR);
    completedText.setStyle("-fx-font-size: 18");
    toggleCompletedButton = new MFXButton("Complete task");
    toggleCompletedButton.setOnAction(actionEvent -> handleCompleteTaskButtonAction());

    HBox toggleCompletedContainer = new HBox(completedText, toggleCompletedButton);
    toggleCompletedContainer.setAlignment(Pos.CENTER_LEFT);
    toggleCompletedContainer.setSpacing(10);
    toggleCompletedContainer.setPadding(new Insets(0, 0, 15, 0));

    final Text taskTitleTitle = new Text("Title");
    taskTitleTitle.setFill(TEXT_COLOR);
    taskTitleTitle.setStyle("-fx-font-size: 20");

    taskTitleField = new MFXTextField();
    taskTitleField.setPromptText("Title");
    taskTitleField.setStyle("-fx-pref-width: 50em");

    final Text taskDescriptionTitle = new Text("Description");
    taskDescriptionTitle.setFill(TEXT_COLOR);
    taskDescriptionTitle.setStyle("-fx-font-size: 20");

    taskDescriptionField = new TextArea();
    taskDescriptionField.getStyleClass().add("task-description");
    taskDescriptionField.setPromptText("Description");
    taskDescriptionField.setStyle("-fx-pref-width: 50em; -fx-pref-height: 20em");
    taskDescriptionField.setWrapText(true);

    final Text sizeTitle = new Text("Size");
    sizeTitle.setFill(TEXT_COLOR);
    ObservableList<Size> sizeChoicesList = FXCollections.observableArrayList(Size.values());
    sizeField = new MFXComboBox<>(sizeChoicesList);

    final Text priorityTitle = new Text("Priority");
    priorityTitle.setFill(TEXT_COLOR);
    ObservableList<Priority> priorityChoicesList = FXCollections.observableArrayList(
        Priority.values());
    priorityField = new MFXComboBox<>(priorityChoicesList);

    final HBox sizePriorityContainer = new HBox(
        sizeTitle, sizeField, priorityTitle, priorityField);
    sizePriorityContainer.setAlignment(Pos.CENTER_LEFT);
    sizePriorityContainer.setSpacing(10);

    final Text createdDateTitle = new Text("Created date");
    createdDateTitle.setFill(TEXT_COLOR);
    createdDateField = new MFXDatePicker();

    final Text dueDateTitle = new Text("Due date");
    dueDateTitle.setFill(TEXT_COLOR);
    dueDateField = new MFXDatePicker();

    final HBox datesContainer = new HBox(createdDateTitle, createdDateField,
        dueDateTitle, dueDateField);
    datesContainer.setAlignment(Pos.CENTER_LEFT);
    datesContainer.setSpacing(10);

    VBox container = new VBox(toggleCompletedContainer, taskTitleTitle, taskTitleField,
        taskDescriptionTitle, taskDescriptionField, sizePriorityContainer, datesContainer);
    container.setSpacing(10);
    container.setStyle("-fx-pref-width: 50em");

    return container;
  }

  /**
   * Creates the task assignment container.
   *
   * @return the task assignment container
   */
  private VBox createTaskAssignmentContainer() {
    final Text assignedUsersTitle = new Text("Assigned users");
    assignedUsersTitle.setStyle("-fx-font-size: 20");
    assignedUsersTitle.setFill(TEXT_COLOR);

    assignedUsersListView = new ListView<>();
    assignedUsersListView.setPrefWidth(Double.MAX_VALUE);

    final Text availableUsersTitle = new Text("Available users");
    availableUsersTitle.setStyle("-fx-font-size: 20");
    availableUsersTitle.setFill(TEXT_COLOR);

    availableUsersListView = new ListView<>();
    availableUsersListView.setPrefWidth(Double.MAX_VALUE);

    VBox container = new VBox(assignedUsersTitle, assignedUsersListView,
        availableUsersTitle, availableUsersListView);
    container.setStyle("-fx-pref-width: 30em");
    container.setSpacing(10);
    
    return container;
  }

  /**
   * Creates the changelog section.
   *
   * @return the changelog section
   */
  private VBox createChangelogSection() {
    Text changelogTitle = new Text("Change History");
    changelogTitle.setStyle("-fx-font-size: 20");
    changelogTitle.setFill(TEXT_COLOR);

    changelogTable = new MFXTableView<>(changelogEntries);
    changelogTable.setFooterVisible(false);
    changelogTable.setStyle("-fx-pref-height: 225; -fx-max-width: infinity;");

    MFXTableColumn<TaskChange> dateColumn = new MFXTableColumn<>("Date", false);
    dateColumn.setMinWidth(150);
    dateColumn.setMaxWidth(150);
    dateColumn.setRowCellFactory(change -> new MFXTableRowCell<>(taskChange -> 
        taskChange.changedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));

    MFXTableColumn<TaskChange> userColumn = new MFXTableColumn<>("Changed By", false);
    userColumn.setMinWidth(150);
    userColumn.setMaxWidth(150);
    userColumn.setRowCellFactory(change -> new MFXTableRowCell<>(taskChange -> {
      User user = H2Manager.getUserById(taskChange.changedBy());
      return user != null ? user.getFirstName() + " " + user.getLastName() : "Unknown";
    }));

    MFXTableColumn<TaskChange> descriptionColumn = 
        new MFXTableColumn<>("Change Description", false);
    descriptionColumn.setMinWidth(300);
    descriptionColumn.setPrefWidth(500);
    descriptionColumn.setRowCellFactory(change -> {
      final MFXTableRowCell<TaskChange, String> cell = 
          new MFXTableRowCell<>(TaskChange::description);
      Tooltip tooltip = new Tooltip();
      tooltip.setShowDelay(javafx.util.Duration.millis(200));
      tooltip.setWrapText(true);
      tooltip.setMaxWidth(650);
      tooltip.setStyle("-fx-text-fill: white;");
      cell.setTooltip(tooltip);
      
      cell.textProperty().addListener((observable, oldValue, newValue) ->
          tooltip.setText(newValue)
      );
      
      return cell;
    });

    changelogTable.getTableColumns().addAll(dateColumn, userColumn, descriptionColumn);

    paginationControls = new HBox(10);
    paginationControls.setAlignment(Pos.CENTER);
    
    MFXButton prevButton = new MFXButton("Previous");
    prevButton.setOnAction(e -> {
      if (currentPage > 0) {
        currentPage--;
        updateChangelogPage();
      }
    });
    
    Text pageInfo = new Text();
    pageInfo.setFill(TEXT_COLOR);
    
    MFXButton nextButton = new MFXButton("Next");
    nextButton.setOnAction(e -> {
      if ((currentPage + 1) * ROWS_PER_PAGE < changelogEntries.size()) {
        currentPage++;
        updateChangelogPage();
      }
    });
    
    paginationControls.getChildren().addAll(prevButton, pageInfo, nextButton);

    VBox container = new VBox(10);
    container.getChildren().addAll(changelogTitle, changelogTable, paginationControls);
    container.setStyle("-fx-max-width: infinity;");
    return container;
  }

  /**
   * Updates the changelog page.
   */
  private void updateChangelogPage() {
    int startIndex = currentPage * ROWS_PER_PAGE;
    int endIndex = Math.min(startIndex + ROWS_PER_PAGE, changelogEntries.size());
    int totalPages = (int) Math.ceil((double) changelogEntries.size() / ROWS_PER_PAGE);
    
    ObservableList<TaskChange> pageEntries = FXCollections.observableArrayList(
        changelogEntries.subList(startIndex, endIndex)
    );
    
    changelogTable.setItems(pageEntries);
    changelogTable.update();
    
    Text pageInfo = (Text) paginationControls.getChildren().get(1);
    pageInfo.setText(String.format("Page %d of %d", currentPage + 1, totalPages));
    
    MFXButton prevButton = (MFXButton) paginationControls.getChildren().get(0);
    MFXButton nextButton = (MFXButton) paginationControls.getChildren().get(2);
    prevButton.setDisable(currentPage == 0);
    nextButton.setDisable((currentPage + 1) * ROWS_PER_PAGE >= changelogEntries.size());
  }

  /**
   * Constructs a new user container with a button and text. The button is either an add or remove
   * button, depending on the given icon name. The text is the provided user's name.
   *
   * @param iconName the name of the icon to use for the button ("fas-plus" or "fas-minus")
   * @param user the user to construct the container for (used to get the user's name)
   * @return the constructed user container
   */
  private HBox constructUserContainer(String iconName, User user) {
    HBox userContainer = new HBox();
    MFXFontIcon icon = new MFXFontIcon(iconName, ICON_SIZE);
    MFXButton button = new MFXButton("", icon);
    if (iconName.equals(ADD_USER_ICON_NAME)) {
      icon.setColor(ADD_USER_BUTTON_ICON_COLOR);
      button.getStyleClass().add("add-user-button");
      button.setOnAction(actionEvent -> handleAddUserButtonAction(userContainer, user));
    } else if (iconName.equals(REMOVE_USER_ICON_NAME)) {
      icon.setColor(REMOVE_USER_BUTTON_ICON_COLOR);
      button.getStyleClass().add("remove-user-button");
      button.setOnAction(actionEvent -> handleRemoveUserButtonAction(userContainer, user));
    }
    Text userNameText = new Text(user.getFirstName() + " " + user.getLastName());
    userNameText.getStyleClass().add("user-name-text");
    userContainer.getChildren().addAll(button, userNameText);
    userContainer.setAlignment(Pos.CENTER_LEFT);
    userContainer.setSpacing(10);
    return userContainer;
  }

  /**
   * Initializes the assigned users list view with the users that are assigned to the task.
   */
  private void initializeAssignedUsers() {
    task.getAssignedUserIds().forEach(userId -> {
      User user = H2Manager.getUserById(userId);
      assignedUsersListView.getItems().add(
          constructUserContainer(REMOVE_USER_ICON_NAME, user));
      assignedUsersList.add(user);
    });
  }

  /**
   * Initialized the available users list view with all users in the database.
   */
  private void initializeAvailableUsers() {
    H2Manager.getAllUsers().forEach(user -> {
      if (!task.getAssignedUserIds().contains(user.getId())) {
        availableUsersListView.getItems().add(constructUserContainer(ADD_USER_ICON_NAME, user));
      }
    });
  }

  /**
   * Initializes all values in the view.
   */
  private void initializeValues() {
    Platform.runLater(() -> {
      taskTitleField.setText(task.getName());
      taskDescriptionField.setText(task.getDescription());
      createdDateField.setValue(task.getCreatedDate().toLocalDate());
      dueDateField.setValue(task.getDueDate().toLocalDate());
      sizeField.setValue(task.getSize());
      priorityField.setValue(task.getPriority());
      updateCompletionStatus(task.isCompleted());
      refreshUserAssignments();
      loadChangelogEntries();
    });
  }

  /**
   * Loads the changelog entries for the task.
   */
  private void loadChangelogEntries() {
    List<TaskChange> changes = H2Manager.getTaskChanges(task.getId());
    changelogEntries.setAll(changes);
    currentPage = 0;
    updateChangelogPage();
  }

  /**
   * Handles the action of adding a user to the assigned users list view.
   *
   * @param userContainer the user container to add
   * @param user the user object for the userContainer
   */
  private void handleAddUserButtonAction(HBox userContainer, User user) {
    assignedUsersListView.getItems().add(constructUserContainer(REMOVE_USER_ICON_NAME, user));
    availableUsersListView.getItems().remove(userContainer);
    assignedUsersList.add(user);
  }

  /**
   * Handles the action of removing a user from the assigned users list view.
   *
   * @param userContainer the user container to remove
   * @param user the user object for the userContainer
   */
  private void handleRemoveUserButtonAction(HBox userContainer, User user) {
    availableUsersListView.getItems().add(constructUserContainer(ADD_USER_ICON_NAME, user));
    assignedUsersListView.getItems().remove(userContainer);
    assignedUsersList.remove(user);
  }

  /**
   * Get the view's task id.
   *
   * @return the id of the view's task.
   */
  public UUID getTaskId() {
    return task.getId();
  }

  /**
   * Get the view's task completed status value.
   *
   * @return the view's task completed status value
   */
  public boolean getTaskCompleted() {
    return completedText.getText().equals("Completed: ✅");
  }

  /**
   * Get the task title value.
   *
   * @return the task title value
   */
  public String getTaskTitle() {
    return taskTitleField.getText();
  }

  /**
   * Get the task description value.
   *
   * @return the task description value
   */
  public String getTaskDescription() {
    return taskDescriptionField.getText();
  }

  /**
   * Get the task size value.
   *
   * @return the task size value
   */
  public Size getTaskSize() {
    return sizeField.getValue();
  }

  /**
   * Get the task priority value.
   *
   * @return the task priority value
   */
  public Priority getTaskPriority() {
    return priorityField.getValue();
  }

  /**
   * Get the task created date value.
   *
   * @return the task created date value
   */
  public LocalDateTime getTaskCreatedDate() {
    return LocalDateTime.parse(createdDateField.getValue().toString() + " 00:00:00",
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
  }

  /**
   * Get the task due date value.
   *
   * @return the task due date value
   */
  public LocalDateTime getTaskDueDate() {
    return LocalDateTime.parse(dueDateField.getValue().toString() + " 00:00:00",
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
  }

  /**
   * Get the task completed date value.
   *
   * @return the task completed date value
   */
  public LocalDateTime getCompletedDate() {
    return task.getCompletedDate();
  }

  /**
   * Get the new list of assigned users from the assigned users list view.
   *
   * @return the new list of assigned users
   */
  public List<UUID> getTaskAssignedUserIds() {
    return assignedUsersList.stream().map(User::getId).toList();
  }

  /**
   * Handles the click event for the complete/restore task button.
   */
  private void handleCompleteTaskButtonAction() {
    if (getTaskCompleted()) {
      completedText.setText("Completed: ❌");
      toggleCompletedButton.setText("Complete task");
      return;
    }
    completedText.setText("Completed: ✅");
    toggleCompletedButton.setText("Restore task");
  }

  /**
   * Refreshes the entire view with current task data.
   */
  public void refreshView() {
    initializeValues();
  }

  /**
   * Refreshes the user assignments and available users in the view.
   */
  public void refreshUserAssignments() {
    Platform.runLater(() -> {
      assignedUsersList = new ArrayList<>();
      assignedUsersListView.getItems().clear();
      availableUsersListView.getItems().clear();
      initializeAssignedUsers();
      initializeAvailableUsers();
    });
  }

  /**
   * Updates the completion status display.
   *
   * @param completed the completion status
   */
  public void updateCompletionStatus(boolean completed) {
    Platform.runLater(() -> {
      completedText.setText(completed ? "Completed: ✅" : "Completed: ❌");
      toggleCompletedButton.setText(completed ? "Restore task" : "Complete task");
    });
  }
}
