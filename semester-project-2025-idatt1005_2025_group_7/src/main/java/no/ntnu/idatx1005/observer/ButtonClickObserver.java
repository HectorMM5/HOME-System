package no.ntnu.idatx1005.observer;

import no.ntnu.idatx1005.model.task.Task;

/**
 * <h3>ButtonClickObserver interface</h3>
 *
 * <p>This interface defines the methods for observing button click events.
 *
 * @author Tord Fosse
 * @since V1.1.0
 */
public interface ButtonClickObserver {

  /**
   * Handles button click events with a task.
   *
   * @param buttonId the ID of the button clicked
   * @param task the task
   */
  void onButtonClickedWithTask(String buttonId, Task task);

  /**
   * Handles button click events.
   *
   * @param buttonId the ID of the button clicked
   */
  void onButtonClicked(String buttonId);
}
