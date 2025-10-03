package no.ntnu.idatx1005.view.content;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import no.ntnu.idatx1005.model.task.Priority;
import no.ntnu.idatx1005.model.task.Size;
import no.ntnu.idatx1005.model.user.User;
import no.ntnu.idatx1005.service.DatabaseService;
import no.ntnu.idatx1005.view.container.ContentView;

/**
 * <h3>View for the new task page.</h3>
 *
 * <p>The view extends the {@link HBox} class. The view is meant to be used as a child of the
 * {@link ContentView} class.
 *
 * @author Tord Fosse
 * @author William Holtsdalen
 * @since V0.1.0
 */
public class NewTaskView extends HBox {
  private static final Color TEXT_COLOR = Color.WHITE;
  private static final String ADD_USER_ICON_NAME = "fas-plus";
  private static final String REMOVE_USER_ICON_NAME = "fas-minus";
  private static final int ICON_SIZE = 20;
  private static final Color ADD_USER_BUTTON_ICON_COLOR = Color.rgb(0, 160, 0);
  private static final Color REMOVE_USER_BUTTON_ICON_COLOR = Color.rgb(140, 0, 0);

  private MFXTextField taskTitleField;
  private TextArea taskDescriptionField;
  private MFXComboBox<Size> sizeField;
  private MFXComboBox<Priority> priorityField;
  private MFXDatePicker dueDateField;
  private ListView<HBox> assignedUsersListView;
  private ListView<HBox> availableUsersListView;

  private final List<User> assignedUsersList;
  private final DatabaseService dbService;

  /**
   * Constructs a new new task view.
   */
  public NewTaskView() {
    this.dbService = DatabaseService.getInstance();
    this.assignedUsersList = new ArrayList<>();

    this.getStyleClass().add("content");
    this.setSpacing(10);

    initializeContainers();
    initializeAvailableUsers();

    // For test
    taskTitleField.setId("taskTitleField");
    taskDescriptionField.setId("taskDescriptionField");
    sizeField.setId("sizeField");
    priorityField.setId("priorityField");
    dueDateField.setId("dueDateField");
  }

  /**
   * Returns the new task view.
   *
   * @return the new task view
   */
  public HBox getView() {
    return this;
  }

  /**
   * Initializes the containers for the new task view.
   */
  private void initializeContainers() {
    // Task details container

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

    // Size and priority container
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

    // Due date container
    final Text dueDateTitle = new Text("Due date");
    dueDateTitle.setFill(TEXT_COLOR);

    dueDateField = new MFXDatePicker();
    final HBox dueDateContainer = new HBox(dueDateTitle, dueDateField);
    dueDateContainer.setAlignment(Pos.CENTER_LEFT);
    dueDateContainer.setSpacing(10);

    VBox taskInfoContainer = new VBox(taskTitleTitle, taskTitleField, taskDescriptionTitle, 
        taskDescriptionField, sizePriorityContainer, dueDateContainer);
    taskInfoContainer.setSpacing(10);
    taskInfoContainer.setStyle("-fx-pref-width: 50em");

    // Task assignment container
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

    VBox taskAssignmentContainer = new VBox(assignedUsersTitle, assignedUsersListView,
        availableUsersTitle, availableUsersListView);
    taskAssignmentContainer.setStyle("-fx-pref-width: 30em");
    taskAssignmentContainer.setSpacing(10);

    this.getChildren().clear();
    this.getChildren().addAll(taskInfoContainer, taskAssignmentContainer);
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
      button.setOnAction(actionEvent -> handleRemoveUserButtonAction(userContainer,
          user));
    }

    Text userNameText = new Text(user.getFirstName() + " " + user.getLastName());
    userNameText.getStyleClass().add("user-name-text");
    userContainer.getChildren().addAll(button, userNameText);
    userContainer.setAlignment(Pos.CENTER_LEFT);
    userContainer.setSpacing(10);
    return userContainer;
  }

  /**
   * Initialized the available users list view with all users in the database.
   */
  private void initializeAvailableUsers() {
    dbService.getAllUsersByCapacity().forEach(user -> availableUsersListView.getItems()
        .add(constructUserContainer(ADD_USER_ICON_NAME, user)));
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
   * Get the task due date value.
   *
   * @return the task due date value
   */
  public LocalDateTime getTaskDueDate() {
    return LocalDateTime.parse(dueDateField.getValue().toString() + " 00:00:00",
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
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
   * Resets the form to its initial state.
   */
  public void resetForm() {
    taskTitleField.clear();
    taskDescriptionField.clear();
    dueDateField.clear();
    sizeField.clear();
    priorityField.clear();
    assignedUsersListView.getItems().clear();
    refreshUserAssignments();
  }

  /**
   * Refreshes the user assignment lists.
   */
  public void refreshUserAssignments() {
    assignedUsersListView.getItems().clear();
    availableUsersListView.getItems().clear();
    initializeAvailableUsers();
  }
}
