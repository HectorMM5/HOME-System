package no.ntnu.idatx1005.observer;

import no.ntnu.idatx1005.model.task.Task;

/**
 * <h3>ButtonClickSubject interface</h3>
 *
 * <p>This interface defines the methods for classes that can be observed 
 * for button click events. 
 *
 * @author William Holtsdalen
 * @since V1.1.0
 */
public interface ButtonClickSubject {
  /**
   * Adds an observer to the subject.
   *
   * @param observer the observer to add
   */
  void addObserver(ButtonClickObserver observer);

  /**
   * Removes an observer from the subject.
   *
   * @param observer the observer to remove
   */
  void removeObserver(ButtonClickObserver observer);

  /**
   * Notifies all observers with a task.
   *
   * @param buttonId the ID of the button clicked
   * @param task the task
   */
  void notifyObserversWithTask(String buttonId, Task task);

  /**
   * Notifies all observers.
   *
   * @param buttonId the ID of the button clicked
   */
  void notifyObservers(String buttonId);
}
