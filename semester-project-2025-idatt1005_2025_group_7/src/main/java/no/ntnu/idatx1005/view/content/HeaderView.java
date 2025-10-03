package no.ntnu.idatx1005.view.content;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.util.Duration;
import no.ntnu.idatx1005.model.task.Task;
import no.ntnu.idatx1005.observer.ButtonClickObserver;
import no.ntnu.idatx1005.observer.ButtonClickSubject;
import no.ntnu.idatx1005.storage.H2Manager;
import no.ntnu.idatx1005.view.container.MainView;

/**
 * <h3>View for the header.</h3>
 *
 * <p>The view extends the {@link HBox} class. The view is meant to be used as a child of the
 * {@link MainView} class. It implements the {@link ButtonClickSubject} interface to notify
 * observers when a button in the view is clicked.
 *
 * @see HBox
 * @see MainView
 * @see ButtonClickSubject
 * @author Tord Fosse
 * @author William Holtsdalen
 * @since V0.1.0
 */
public class HeaderView extends HBox implements ButtonClickSubject {
  private static final String DEFAULT_STYLE_CLASS = "header";
  private static final Color TEXT_COLOR = Color.WHITE;
  private static final Color ICON_COLOR = Color.rgb(255, 255, 255);

  private Region spacer;
  private MFXButton backButton;
  private MFXButton newTaskButton;
  private MFXButton createTaskButton;
  private MFXButton editTaskButton;
  private MFXButton distributeButton;
  private MFXButton autoDistributeButton;
  private MFXButton userButton;
  private MFXButton refreshStorageButton;
  private MFXButton saveSettingsButton;
  private MFXTextField searchBar;
  private Popup searchResultsPopup;
  private ListView<HBox> searchResultsList;

  private final List<ButtonClickObserver> observers;

  /**
   * Constructs a new header view.
   */
  public HeaderView() {
    this.observers = new ArrayList<>();

    this.getStyleClass().add(DEFAULT_STYLE_CLASS);

    initialize();
    initializeSearchComponents();
  }

  /**
   * Initializes the header view.
   */
  private void initialize() {
    this.spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    this.backButton = initializeIconButton(new MFXButton(), new MFXFontIcon(
        "fas-arrow-left", 16), actionEvent -> notifyObservers("back_button"));
    this.newTaskButton = initializeTextButton(new MFXButton(),
        "+ New Task", actionEvent -> notifyObservers("new_task"));
    this.distributeButton = initializeTextButton(new MFXButton(),
        "Distribute", actionEvent -> notifyObservers("distribute"));
    this.createTaskButton = initializeTextButton(new MFXButton(),
        "Create Task", actionEvent -> notifyObservers("save_new_task"));
    this.editTaskButton = initializeTextButton(new MFXButton(),
        "Save Changes", actionEvent -> notifyObservers("save_task_changes"));
    this.autoDistributeButton = initializeTextButton(new MFXButton(),
        "Auto Distribute", actionEvent -> notifyObservers("auto_distribute"));
    this.userButton = initializeIconButton(new MFXButton(), new MFXFontIcon(
        "fas-user", 25), actionEvent -> notifyObservers("settings"));
    this.refreshStorageButton = initializeTextButton(new MFXButton(),
        "Refresh Tasks", actionEvent -> notifyObservers("refresh_cache"));
    this.saveSettingsButton = initializeTextButton(new MFXButton(),
        "Save Settings", actionEvent -> notifyObservers("save_settings"));

    backButton.setStyle("-fx-border-color: #D9D9D9; -fx-border-width: 1px; "
        + "-fx-border-radius: 5;");
    newTaskButton.setStyle("-fx-border-color: #D9D9D9; -fx-border-width: 1px; "
        + "-fx-border-radius: 5;");
    distributeButton.setStyle("-fx-border-color: #D9D9D9; -fx-border-width: 1px; "
        + "-fx-border-radius: 5;");
    createTaskButton.setStyle("-fx-border-color: #D9D9D9; -fx-border-width: 1px; "
        + "-fx-border-radius: 5;");
    editTaskButton.setStyle("-fx-border-color: #D9D9D9; -fx-border-width: 1px; "
        + "-fx-border-radius: 5;");
    autoDistributeButton.setStyle("-fx-border-color: #D9D9D9; -fx-border-width: 1px; "
        + "-fx-border-radius: 5;");
    userButton.setStyle("-fx-border-color: #D9D9D9; -fx-border-width: 1px; "
        + "-fx-border-radius: 5;");
    refreshStorageButton.setStyle("-fx-border-color: #D9D9D9; -fx-border-width: 1px; "
        + "-fx-border-radius: 5;");
    saveSettingsButton.setStyle("-fx-border-color: #D9D9D9; -fx-border-width: 1px; "
        + "-fx-border-radius: 5;");
  }

  /**
   * Initializes the search components.
   */
  private void initializeSearchComponents() {
    searchResultsList = new ListView<>();
    searchResultsList.setMaxHeight(300);
    searchResultsList.setMinWidth(400);
    searchResultsList.setStyle("-fx-background-color: #2B2B2B; -fx-text-fill: white;");
    
    VBox popupContent = new VBox(searchResultsList);
    popupContent.setStyle("-fx-background-color: #2B2B2B; -fx-padding: 5;");

    searchResultsPopup = new Popup();
    searchResultsPopup.getContent().setAll(popupContent);
    searchResultsPopup.setAutoHide(true);
    searchResultsPopup.setHideOnEscape(true);
  }

  /**
   * Handles the event when the text in the search bar is changed.
   *
   * @param searchText the text in the search bar
   */
  private void handleSearchTextChanged(String searchText) {
    if (searchText == null || searchText.trim().isEmpty()) {
      searchResultsPopup.hide();
      return;
    }

    List<Task> matchingTasks = H2Manager.getAllTasks().stream()
        .filter(task -> task.getName().toLowerCase().contains(searchText.toLowerCase()))
        .toList();

    searchResultsList.getItems().clear();

    matchingTasks.forEach(task -> 
        searchResultsList.getItems().add(createSearchResultItem(task)));

    if (!matchingTasks.isEmpty() && !searchResultsPopup.isShowing()) {
      searchResultsPopup.show(searchBar, 
          searchBar.localToScreen(0, 0).getX(),
          searchBar.localToScreen(0, 0).getY() + searchBar.getHeight());
    } else if (matchingTasks.isEmpty()) {
      searchResultsPopup.hide();
    }
  }

  /**
   * Creates a search result item for a task that is displayed in the search results popup.
   *
   * @param task the task
   * @return the search result item
   */
  private HBox createSearchResultItem(Task task) {
    Text statusLabel = new Text(task.isCompleted() ? "✓" : "◯");
    statusLabel.setFill(task.isCompleted() ? Color.rgb(0, 200, 0) : Color.rgb(255, 165, 0));
    statusLabel.setStyle("-fx-font-size: 16;");

    Text taskName = new Text(task.getName());
    taskName.setFill(Color.WHITE);
  
    Text taskDate;
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    if (task.isCompleted()) {
      taskDate = new Text("Completed: " + task.getDueDate().format(dateFormatter));
      taskDate.setFill(Color.LIGHTGRAY);
    } else {
      taskDate = new Text("Due: " + task.getDueDate().format(dateFormatter));
      taskDate.setFill(Color.LIGHTGRAY);
    }

    Region textSpacer = new Region();
    HBox.setHgrow(textSpacer, Priority.ALWAYS);

    HBox item = new HBox(statusLabel, taskName, textSpacer, taskDate);
    item.setAlignment(Pos.CENTER_LEFT);
    item.setPadding(new Insets(12));
    item.setSpacing(12);
    item.setStyle("-fx-background-color: #2B2B2B;");
    item.setOnMouseEntered(e -> item.setStyle("-fx-background-color: #3D3D3D;"
        + "-fx-scale-x: 1.03; -fx-scale-y: 1.03;"));
    item.setOnMouseExited(e -> item.setStyle("-fx-background-color: #2B2B2B; "
        + "-fx-scale-x: 1; -fx-scale-y: 1;"));

    String tooltipText = String.format("""
        %s
        
        Priority: %s  •  Size: %s
        
        %s""", 
        task.getName(),
        task.getPriority(),
        task.getSize(),
        task.getDescription());
    Tooltip tooltip = new Tooltip(tooltipText);
    tooltip.setStyle("-fx-font-size: 12; -fx-background-color: #2B2B2B; -fx-text-fill: white; "
        + "-fx-wrap-text: true; -fx-max-width: 300;");
    tooltip.setShowDelay(Duration.millis(200));
    Tooltip.install(item, tooltip);

    item.setOnMouseClicked(e -> {
      notifyObserversWithTask("edit_task", task);
      searchResultsPopup.hide();
      searchBar.clear();
    });

    return item;
  }

  /**
   * Returns the header view, with the main view header.
   *
   * @return the header view
   */
  public HeaderView getView() {
    return this;
  }

  /**
   * Adds an observer to the header view.
   *
   * @param observer the observer to add
   */
  @Override
  public void addObserver(ButtonClickObserver observer) {
    observers.add(observer);
  }

  /**
   * Removes an observer from the header view.
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
    new ArrayList<>(observers).forEach(observer -> observer.onButtonClickedWithTask(buttonId,
        task));
  }

  /**
   * Notifies all observers with a button id.
   *
   * @param buttonId the button id
   */
  @Override
  public void notifyObservers(String buttonId) {
    new ArrayList<>(observers).forEach(observer -> observer.onButtonClicked(buttonId));
  }

  /**
   * Shows the main view header.
   */
  public void showMainViewHeader() {
    MFXFontIcon icon = new MFXFontIcon("fas-magnifying-glass", 16);
    icon.setColor(ICON_COLOR);
    
    searchBar = new MFXTextField();
    searchBar.setPromptText("Search tasks...");
    searchBar.setLeadingIcon(icon);
    searchBar.setPadding(new Insets(0, 5, 0, 5));
    searchBar.setStyle("-fx-border-color: #D9D9D9; -fx-border-width: 1px; -fx-border-radius: 5;");
    
    searchBar.textProperty().addListener((observable, oldValue,
        newValue) ->
        handleSearchTextChanged(newValue));

    HBox leftOptions = new HBox(backButton, searchBar);
    leftOptions.setAlignment(Pos.CENTER_LEFT);
    leftOptions.getStyleClass().add(DEFAULT_STYLE_CLASS);

    HBox rightOptions = new HBox(refreshStorageButton, newTaskButton, distributeButton, userButton);
    rightOptions.setAlignment(Pos.CENTER_RIGHT);
    rightOptions.getStyleClass().add(DEFAULT_STYLE_CLASS);

    this.getChildren().setAll(leftOptions, spacer, rightOptions);
  }

  /**
   * Shows the new task view header.
   */
  public void showNewTaskViewHeader() {
    Text title = new Text("Add new task");
    title.setFill(TEXT_COLOR);
    title.setStyle("-fx-font-size: 20");

    HBox leftOptions = new HBox(backButton, title);
    leftOptions.setAlignment(Pos.CENTER_LEFT);
    leftOptions.getStyleClass().add(DEFAULT_STYLE_CLASS);

    HBox rightOptions = new HBox(createTaskButton, userButton);
    rightOptions.setAlignment(Pos.CENTER_RIGHT);
    rightOptions.getStyleClass().add(DEFAULT_STYLE_CLASS);

    this.getChildren().setAll(leftOptions, spacer, rightOptions);
  }

  /**
   * Shows the edit task view header.
   */
  public void showEditTaskViewHeader() {
    Text title = new Text("Edit task");
    title.setFill(TEXT_COLOR);
    title.setStyle("-fx-font-size: 20");

    HBox leftOptions = new HBox(backButton, title);
    leftOptions.setAlignment(Pos.CENTER_LEFT);
    leftOptions.getStyleClass().add(DEFAULT_STYLE_CLASS);

    HBox rightOptions = new HBox(editTaskButton, userButton);
    rightOptions.setAlignment(Pos.CENTER_RIGHT);
    rightOptions.getStyleClass().add(DEFAULT_STYLE_CLASS);

    this.getChildren().setAll(leftOptions, spacer, rightOptions);
  }

  /**
   * Shows the distribution view header.
   */
  public void showDistributionViewHeader() {
    Text title = new Text("Task distribution");
    title.setFill(TEXT_COLOR);
    title.setStyle("-fx-font-size: 24");

    MFXFontIcon helpIcon = new MFXFontIcon("fas-circle-info");
    helpIcon.getStyleClass().add("help-circle-icon");
    helpIcon.setStyle("-fx-font-size: 24;");
    helpIcon.setColor(Color.WHITE);

    String helpText = """
        Drag a task card from the right side onto a user card on
        the left side to assign the task to the user.
      
        Or, click the 'Auto Distribute' button to automatically distribute
        the unassigned tasks to the users in the list. The largest tasks will
        be assigned to the users with the largest available capacity.""";
    Tooltip tooltip = new Tooltip(helpText);
    tooltip.setShowDelay(Duration.millis(50));
    Tooltip.install(helpIcon, tooltip);

    HBox leftOptions = new HBox(backButton, title, helpIcon);
    leftOptions.setAlignment(Pos.CENTER_LEFT);
    leftOptions.getStyleClass().add(DEFAULT_STYLE_CLASS);

    HBox rightOptions = new HBox(refreshStorageButton, autoDistributeButton, userButton);
    rightOptions.setAlignment(Pos.CENTER_RIGHT);
    rightOptions.getStyleClass().add(DEFAULT_STYLE_CLASS);

    this.getChildren().setAll(leftOptions, spacer, rightOptions);
  }

  /**
   * Shows the settings view header.
   */
  public void showSettingsViewHeader() {
    Text title = new Text("Settings");
    title.setFill(TEXT_COLOR);
    title.setStyle("-fx-font-size: 24");

    HBox leftOptions = new HBox(backButton, title);
    leftOptions.setAlignment(Pos.CENTER_LEFT);
    leftOptions.getStyleClass().add(DEFAULT_STYLE_CLASS);

    HBox rightOptions = new HBox(saveSettingsButton, userButton);
    rightOptions.setAlignment(Pos.CENTER_RIGHT);
    rightOptions.getStyleClass().add(DEFAULT_STYLE_CLASS);

    this.getChildren().setAll(leftOptions, spacer, rightOptions);
  }

  /**
   * Initializes a new text-only button with the given text and event handler.
   *
   * @param button the button to initialize
   * @param text the text to set on the button
   * @param eventHandler the event handler to set on the button
   * @return the initialized button
   */
  private MFXButton initializeTextButton(MFXButton button, String text, EventHandler<ActionEvent>
      eventHandler) {
    button.setText(text);
    button.setOnAction(eventHandler);
    button.setPadding(new Insets(10));
    button.setBackground(new Background(new BackgroundFill(Color.rgb(64, 64, 64),
        new CornerRadii(10), Insets.EMPTY)));
    button.setTextFill(TEXT_COLOR);
    return button;
  }

  /**
   * Initializes a new icon-only button with the given icon and event handler.
   *
   * @param button the button to initialize
   * @param icon the icon to set on the button
   * @param eventHandler the event handler to set on the button
   * @return the initialized button
   */
  private MFXButton initializeIconButton(MFXButton button, MFXFontIcon icon,
      EventHandler<ActionEvent> eventHandler) {
    button.setText(null);
    icon.setColor(ICON_COLOR);
    button.setGraphic(icon);
    button.setOnAction(eventHandler);
    button.getStyleClass().add("icon-button");
    return button;
  }
}
