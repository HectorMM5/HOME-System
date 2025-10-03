package no.ntnu.idatx1005.observer;

/**
 * <h3>TaskSubject interface</h3>
 *
 * <p>This is an interface for subjects that can be observed for task-related events.
 * It is a part of the Observer pattern implementation.
 *
 * @author William Holtsdalen
 * @since V1.1.0
 */
public interface TaskEventSubject {

  /**
   * Adds an observer to the subject.
   *
   * @param listener the observer to add
   */
  void addObserver(TaskEventObserver listener);

  /**
   * Removes an observer from the subject.
   *
   * @param listener the observer to remove
   */
  void removeObserver(TaskEventObserver listener);

  /**
   * Notifies all observers of the subject.
   */
  void notifyObservers();
}