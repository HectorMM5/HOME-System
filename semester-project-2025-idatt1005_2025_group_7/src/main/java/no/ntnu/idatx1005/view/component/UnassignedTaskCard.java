package no.ntnu.idatx1005.view.component;

import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import no.ntnu.idatx1005.model.task.Size;
import no.ntnu.idatx1005.model.task.Task;

/**
 * <h3>Unassigned Task Card</h3>
 *
 * <p>This class represents a card that displays an unassigned task. It allows for
 * dragging and dropping itself onto other cards such as {@link AvailableUserCard}.
 *
 * @author William Holtsdalen
 * @since V1.1.0
 */
public class UnassignedTaskCard extends HBox {
  private static final String HIGH_PRIORITY = "high-priority";
  private static final String MEDIUM_PRIORITY = "medium-priority";
  private static final String LOW_PRIORITY = "low-priority";
  private static final String[] PRIORITY_COLOR_CLASSES = {HIGH_PRIORITY, MEDIUM_PRIORITY, 
    LOW_PRIORITY};

  private final Task task;

  /**
   * Constructs an UnassignedTaskCard with the given task.
   *
   * @param task the task to display on the card
   */
  public UnassignedTaskCard(Task task) {
    this.task = task;

    this.getStyleClass().add("unassigned-task-card");
    initialize();
    configureDragProperties();
  }

  /**
   * Initializes the card with the given task.
   */
  private void initialize() {
    Label taskNameLabel = new Label(task.getName());
    taskNameLabel.getStyleClass().add("unassigned-task-name-label");
    Label taskDescriptionLabel = new Label(task.getDescription());
    taskDescriptionLabel.getStyleClass().add("unassigned-task-description-label");
    final VBox taskInfoBox = new VBox(taskNameLabel, taskDescriptionLabel);

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    Label taskPriorityLabel = new Label(task.getPriority().toString());
    taskPriorityLabel.getStyleClass().add("unassigned-task-priority-label");
    setPriorityStyleClass(taskPriorityLabel,
        task.getPriority().toString().toLowerCase() + "-priority");

    taskPriorityLabel.textProperty().addListener((observable, oldValue, newValue) -> {
      String priorityStringValue = newValue.toLowerCase() + "-priority";
      for (String priorityStyleClass : PRIORITY_COLOR_CLASSES) {
        if (priorityStringValue.equals(priorityStyleClass)) {
          setPriorityStyleClass(taskPriorityLabel, priorityStyleClass);
          break;
        }
      }
    });

    Label taskSizeLabel = new Label();
    taskSizeLabel.getStyleClass().add("unassigned-task-size-label");
    final StringConverter<Size> sizeStringConverter = new StringConverter<>() {
      @Override
      public String toString(Size size) {
        return switch (size) {
          case XS -> "X-Small (+" + size.getValue() + ")";
          case S -> "Small (+" + size.getValue() + ")";
          case M -> "Medium (+" + size.getValue() + ")";
          case L -> "Large (+" + size.getValue() + ")";
          case XL -> "X-Large (+" + size.getValue() + ")";
        };
      }

      @Override
      public Size fromString(String string) {
        return null;
      }
    };
    taskSizeLabel.setText(sizeStringConverter.toString(task.getSize()));

    VBox taskSizePriorityBox = new VBox(taskPriorityLabel, taskSizeLabel);
    taskSizePriorityBox.getStyleClass().add("task-size-priority-box");

    this.getChildren().addAll(taskInfoBox, spacer, taskSizePriorityBox);
  }

  /**
   * Sets the style class of the given label to the given priority style class.
   *
   * @param label the label to set the style class of
   * @param priorityStyleClass the priority style class to set
   */
  private void setPriorityStyleClass(Label label, String priorityStyleClass) {
    label.getStyleClass().removeAll(PRIORITY_COLOR_CLASSES);
    label.getStyleClass().add(priorityStyleClass);
  }

  /**
   * Configures the drag properties of the card. This includes setting the drag detected event.
   */
  private void configureDragProperties() {
    this.setOnDragDetected(event -> {
      Dragboard db = this.startDragAndDrop(TransferMode.ANY);

      ClipboardContent content = new ClipboardContent();
      content.putString(task.getId().toString());
      db.setContent(content);

      SnapshotParameters parameters = new SnapshotParameters();
      parameters.setFill(javafx.scene.paint.Color.TRANSPARENT);
      db.setDragView(this.snapshot(parameters, null));

      event.consume();
    });
  }
}
