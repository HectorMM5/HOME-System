package no.ntnu.idatx1005.view.content;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import no.ntnu.idatx1005.model.task.Task;
import no.ntnu.idatx1005.observer.ButtonClickObserver;
import no.ntnu.idatx1005.observer.ButtonClickSubject;
import no.ntnu.idatx1005.view.container.MainView;

/**
 * <h3>View for the sidebar.</h3>
 *
 * <p>The view extends the {@link VBox} class. The view is meant to be used as a child of the
 * {@link MainView} class. It implements the {@link ButtonClickSubject} interface to notify
 * observers when a button in the view is clicked.
 *
 * @see VBox
 * @see MainView
 * @see ButtonClickSubject
 * @author Tord Fosse
 * @author William Holtsdalen
 * @since V0.1.0
 */
public class SidebarView extends VBox implements ButtonClickSubject {
  private static final int ICON_SIZE = 20;
  private static final Color ICON_COLOR = Color.rgb(255, 255, 255);

  private final List<ButtonClickObserver> observers;
  
  /**
   * Constructs a new sidebar view.
   *
   */
  public SidebarView() {
    this.observers = new ArrayList<>();

    this.getStyleClass().add("sidebar");
    createComponents();
  }

  /**
   * Returns the sidebar view.
   *
   * @return the sidebar view
   */
  public VBox getView() {
    return this;
  }

  /**
   * Adds an observer to the sidebar view.
   *
   * @param observer the observer to add
   */
  @Override
  public void addObserver(ButtonClickObserver observer) {
    observers.add(observer);
  }

  /**
   * Removes an observer from the sidebar view.
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
    // Not needed
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
   * Creates the components for the sidebar view.
   */
  private void createComponents() {
    Text text = new Text("H.O.M.E.");
    text.getStyleClass().add("title");

    VBox title = new VBox();
    title.setAlignment(Pos.CENTER);
    title.setPadding(new Insets(10, 0, 5, 0));
    title.getChildren().add(text);

    MFXButton allTasksButton = new MFXButton();
    allTasksButton.setStyle("-fx-border-color: #D9D9D9; -fx-border-width: 1px; "
        + "-fx-border-radius: 5;");
    initializeButton(allTasksButton, "All Tasks", "fas-user-group",
        actionEvent -> notifyObservers("all_tasks"));

    MFXButton openTasksButton = new MFXButton();
    openTasksButton.setStyle("-fx-border-color: #D9D9D9; -fx-border-width: 1px; "
        + "-fx-border-radius: 5;");
    initializeButton(openTasksButton, "Open Tasks", "fas-list-check",
        actionEvent -> notifyObservers("open_tasks"));

    MFXButton myTasksButton = new MFXButton();
    myTasksButton.setStyle("-fx-border-color: #D9D9D9; -fx-border-width: 1px; "
        + "-fx-border-radius: 5;");
    initializeButton(myTasksButton, "My Tasks", "fas-user-group",
        actionEvent -> notifyObservers("my_tasks"));

    MFXButton completedTasksButton = new MFXButton();
    completedTasksButton.setStyle("-fx-border-color: #D9D9D9; -fx-border-width: 1px; "
        + "-fx-border-radius: 5;");
    initializeButton(completedTasksButton, "Completed tasks", "fas-check-double",
        actionEvent -> notifyObservers("completed_tasks"));

    MFXButton insightsButton = new MFXButton();
    insightsButton.setStyle("-fx-border-color: #D9D9D9; -fx-border-width: 1px; "
        + "-fx-border-radius: 5;");
    initializeButton(insightsButton, "Insights", "fas-chart-simple",
        actionEvent -> notifyObservers("insights"));

    MFXButton settingsButton = new MFXButton();
    settingsButton.setStyle("-fx-border-color: #D9D9D9; -fx-border-width: 1px; "
        + "-fx-border-radius: 5;");
    initializeButton(settingsButton, "Settings", "fas-gear",
        actionEvent -> notifyObservers("settings"));

    MFXButton logoutButton = new MFXButton();
    logoutButton.setStyle("-fx-border-color: #D9D9D9; -fx-border-width: 1px; "
        + "-fx-border-radius: 5;");
    initializeButton(logoutButton, "Log out", "fas-right-from-bracket",
        actionEvent -> notifyObservers("log_out"));
    VBox.setMargin(logoutButton, new Insets(0, 10, 10, 10));

    VBox sidebarButtons = new VBox(allTasksButton, openTasksButton, myTasksButton,
        completedTasksButton, insightsButton, settingsButton);
    sidebarButtons.setSpacing(5);
    VBox.setMargin(sidebarButtons, new Insets(0, 10, 0, 10));

    Region spacer = new Region();
    VBox.setVgrow(spacer, Priority.ALWAYS);

    this.getChildren().setAll(title, sidebarButtons, spacer, logoutButton);
  }

  /**
   * Initializes the given button with the given text and event handler.
   *
   * @param button the button to initialize
   * @param text the text to set on the button
   * @param eventHandler the event handler to set on the button
   */
  private void initializeButton(
      MFXButton button, String text, String iconName, EventHandler<ActionEvent> eventHandler) {
    button.setText(text);
    button.setGraphic(initializeIcon(iconName));
    button.setAlignment(Pos.CENTER_LEFT);
    button.setPrefWidth(Double.MAX_VALUE);
    button.setPrefHeight(50);
    button.setOnAction(eventHandler);
  }

  /**
   * Initializes the icon for the given icon name.
   *
   * @param iconName the name of the icon
   * @return the initialized icon
   */
  private MFXFontIcon initializeIcon(String iconName) {
    MFXFontIcon icon = new MFXFontIcon(iconName, ICON_SIZE);
    icon.setColor(ICON_COLOR);
    return icon;
  }
}
