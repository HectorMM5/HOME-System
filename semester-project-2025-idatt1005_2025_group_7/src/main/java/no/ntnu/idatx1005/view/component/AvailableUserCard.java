package no.ntnu.idatx1005.view.component;

import java.util.function.BiConsumer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import no.ntnu.idatx1005.model.user.User;

/**
 * <h3>Available User Card</h3>
 *
 * <p>This class represents a card that displays a user's workload and capacity. It allows for
 * dragging and dropping other task cards onto the card.
 *
 * @author William Holtsdalen
 * @since V1.1.0
 */
public class AvailableUserCard extends HBox {
  private static final String RED_BAR    = "red-bar";
  private static final String YELLOW_BAR = "yellow-bar";
  private static final String ORANGE_BAR = "orange-bar";
  private static final String GREEN_BAR  = "green-bar";
  private static final String[] barColorStyleClasses = {RED_BAR, ORANGE_BAR, YELLOW_BAR, GREEN_BAR};

  private final User user;
  private final String userName;
  private int userWorkLoad;
  private int totalCapacity;

  private Label userWorkloadLabel;
  private ProgressBar userWorkloadProgressBar;

  /* Callback for when a task is dropped on this card. The first argument is this card's user, and
   * the second argument is the dropped task's id as a string. */
  private BiConsumer<User, String> onDragDropped;

  /**
   * Constructs an AvailableUserCard with the given user and current workload.
   *
   * @param user the user to display on the card
   * @param currentWorkload the current workload of the user
   */
  public AvailableUserCard(User user, int currentWorkload) {
    this.user = user;
    this.userName = user.getFirstName() + " " + user.getLastName();
    this.userWorkLoad = currentWorkload;
    this.totalCapacity = user.getTaskCapacity();

    this.getStyleClass().add("available-user-card");
    initialize();
    configureDragProperties();
    updateUserWorkload(userWorkLoad, totalCapacity);
  }

  /**
   * Initializes the card with the given user and current workload.
   */
  private void initialize() {
    Label userLabel = new Label(userName);
    userLabel.getStyleClass().add("distribution-user-label");

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    VBox userCapacityVbox = new VBox();
    userCapacityVbox.getStyleClass().add("distribution-user-workload-box");
    userWorkloadLabel = new Label();
    userWorkloadLabel.getStyleClass().add("distribution-user-workload-label");
    userWorkloadProgressBar = new ProgressBar();
    userWorkloadProgressBar.getStyleClass().add("distribution-user-workload-progress-bar");

    userWorkloadProgressBar.progressProperty().addListener(new ChangeListener<>() {
      /**
       * Updates the style class of the progress bar based on the progress.
       *
       * @param observable the observable value
       * @param oldValue the old value
       * @param newValue the new value
       */
      @Override public void changed(ObservableValue<? extends Number>
          observable, Number oldValue, Number newValue) {
        double progress = newValue == null ? 0 : newValue.doubleValue();
        if (progress < 0.25) {
          setBarStyleClass(userWorkloadProgressBar, GREEN_BAR);
        } else if (progress < 0.5) {
          setBarStyleClass(userWorkloadProgressBar, YELLOW_BAR);
        } else if (progress < 0.75) {
          setBarStyleClass(userWorkloadProgressBar, ORANGE_BAR);
        } else {
          setBarStyleClass(userWorkloadProgressBar, RED_BAR);
        }
      }

      /**
       * Sets the style class of the given progress bar to the given style class.
       *
       * @param bar the progress bar to set the style class of
       * @param barStyleClass the style class to set the progress bar to
       */
      private void setBarStyleClass(ProgressBar bar, String barStyleClass) {
        bar.getStyleClass().removeAll(barColorStyleClasses);
        bar.getStyleClass().add(barStyleClass);
      }
    });
    userCapacityVbox.getChildren().addAll(userWorkloadLabel, userWorkloadProgressBar);

    this.getChildren().addAll(userLabel, spacer, userCapacityVbox);
  }

  /**
   * Configures the drag properties of the card. This includes setting the drag over, drag entered,
   * drag exited, and drag dropped events.
   */
  private void configureDragProperties() {
    this.setOnDragOver(event -> {
      if (event.getGestureSource() != this && event.getDragboard().hasString()) {
        event.acceptTransferModes(TransferMode.MOVE);
      }

      event.consume();
    });

    this.setOnDragEntered(event -> {
      if (event.getGestureSource() != this && event.getDragboard().hasString()) {
        this.getStyleClass().add("available-user-card-drag-over");
      }
    });

    this.setOnDragExited(event -> {
      if (event.getGestureSource() != this && event.getDragboard().hasString()) {
        this.getStyleClass().remove("available-user-card-drag-over");
      }
    });

    this.setOnDragDropped(event -> {
      Dragboard db = event.getDragboard();
      boolean success = false;
      if (db.hasString() && onDragDropped != null) {
        onDragDropped.accept(user, db.getString());
        success = true;
      }
      event.setDropCompleted(success);
      event.consume();
    });
  }

  /**
   * Updates the user's workload and capacity.
   *
   * @param userWorkLoad the user's current workload
   * @param totalCapacity the user's total capacity
   */
  public void updateUserWorkload(int userWorkLoad, int totalCapacity) {
    this.userWorkLoad = userWorkLoad;
    this.totalCapacity = totalCapacity;

    userWorkloadLabel.setText("Workload  " + userWorkLoad + "/" + totalCapacity);
    userWorkloadProgressBar.setProgress((double) userWorkLoad / totalCapacity);
  }

  /**
   * Sets the callback for when a task is dropped on this card.
   *
   * @param onDragDropped the callback to set
   */
  public void setOnDragDropped(BiConsumer<User, String> onDragDropped) {
    this.onDragDropped = onDragDropped;
  }
}
