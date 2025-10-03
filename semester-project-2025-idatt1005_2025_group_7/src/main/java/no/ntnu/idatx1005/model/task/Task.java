package no.ntnu.idatx1005.model.task;

import static no.ntnu.idatx1005.model.validators.ArgumentValidator.taskAddAssignedUserIdValidator;
import static no.ntnu.idatx1005.model.validators.ArgumentValidator.taskSetAssignedUserIdsValidator;
import static no.ntnu.idatx1005.model.validators.ArgumentValidator.taskSetCreatedDateValidator;
import static no.ntnu.idatx1005.model.validators.ArgumentValidator.taskSetDescriptionValidator;
import static no.ntnu.idatx1005.model.validators.ArgumentValidator.taskSetDueDateValidator;
import static no.ntnu.idatx1005.model.validators.ArgumentValidator.taskSetIdValidator;
import static no.ntnu.idatx1005.model.validators.ArgumentValidator.taskSetNameValidator;
import static no.ntnu.idatx1005.model.validators.ArgumentValidator.taskSetPriorityValidator;
import static no.ntnu.idatx1005.model.validators.ArgumentValidator.taskSetSizeValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import no.ntnu.idatx1005.model.user.User;

/**
 * <h3>Represents a task.</h3>
 *
 * <p>A task has a unique id, a name, a description, a due date, a priority, and a task weight. The
 * task can also have a list of assigned users.
 *
 * @see UUID
 * @see User
 * @author Hector Mendana Morales
 * @author William Holtsdalen
 * @since V0.1.0
 */
public class Task {
  private UUID id;
  private boolean completed;
  private String name;
  private String description;
  private LocalDateTime createdDate;
  private LocalDateTime dueDate;
  private LocalDateTime completedDate;
  private Priority priority;
  private Size size;
  private List<UUID> assignedUserIds;

  /**
   * Constructs a new task with the given id, name, description, due date, priority, and task
   * size.
   *
   * @param id the task id
   * @param completed the task completed status
   * @param name the task name
   * @param description the task description
   * @param dueDate the task due date
   * @param priority the task priority
   * @param size the task size
   */
  public Task(UUID id, boolean completed, String name, String description,
      LocalDateTime createdDate, LocalDateTime dueDate, Priority priority, Size size) {
    setId(id);
    setCompleted(completed);
    setName(name);
    setDescription(description);
    setCreatedDate(createdDate);
    setDueDate(dueDate);
    setPriority(priority);
    setSize(size);
    setAssignedUserIds(new ArrayList<>());
  }

  /**
   * Returns the task id.
   *
   * @return the task id
   */
  public UUID getId() {
    return id;
  }

  /**
   * Returns the task completed status.
   *
   * @return the task completed status
   */
  public boolean isCompleted() {
    return completed;
  }

  /**
   * Returns the task name.
   *
   * @return the task name
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the task description.
   *
   * @return the task description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Returns the date when the task was created.
   *
   * @return the date when the task was created
   */
  public LocalDateTime getCreatedDate() {
    return createdDate;
  }

  /**
   * Returns the task due date.
   *
   * @return the task due date
   */
  public LocalDateTime getDueDate() {
    return dueDate;
  }

  /**
   * Returns the task completed date.
   *
   * @return the task completed date
   */
  public LocalDateTime getCompletedDate() {
    return completedDate;
  }

  /**
   * Returns the task priority.
   *
   * @return the task priority
   */
  public Priority getPriority() {
    return priority;
  }

  /**
   * Returns the task size.
   *
   * @return the task size
   */
  public Size getSize() {
    return size;
  }

  /**
   * Returns the task's list of assigned user IDs.
   *
   * @return the task's list of assigned user IDs.
   */
  public List<UUID> getAssignedUserIds() {
    return assignedUserIds;
  }

  /**
   * Sets the task id.
   *
   * @param id the task id
   */
  public void setId(UUID id) {
    taskSetIdValidator(id);
    this.id = id;
  }

  /**
   * Sets the task completed status.
   *
   * @param completed the task completed status
   */
  public void setCompleted(boolean completed) {
    this.completed = completed;
  }

  /**
   * Sets the task name.
   *
   * @param name the task name
   */
  public void setName(String name) {
    taskSetNameValidator(name);
    this.name = name;
  }

  /**
   * Sets the task description.
   *
   * @param description the task description
   */
  public void setDescription(String description) {
    taskSetDescriptionValidator(description);
    this.description = description;
  }

  /**
   * Sets the date when the task was created.
   *
   * @param createdDate the date when the task was created
   */
  public void setCreatedDate(LocalDateTime createdDate) {
    taskSetCreatedDateValidator(createdDate);
    this.createdDate = createdDate;
  }

  /**
   * Sets the task due date.
   *
   * @param dueDate the task due date
   */
  public void setDueDate(LocalDateTime dueDate) {
    taskSetDueDateValidator(dueDate);
    this.dueDate = dueDate;
  }

  /**
   * Sets the task completed date.
   *
   * @param completedDate the task completed date
   */
  public void setCompletedDate(LocalDateTime completedDate) {
    this.completedDate = completedDate;
  }

  /**
   * Sets the task priority.
   *
   * @param priority the task priority
   */
  public void setPriority(Priority priority) {
    taskSetPriorityValidator(priority);
    this.priority = priority;
  }

  /**
   * Sets the task weight.
   *
   * @param size the task size
   */
  public void setSize(Size size) {
    taskSetSizeValidator(size);
    this.size = size;
  }

  /**
   * Sets the task's list of assigned user IDs.
   *
   * @param assignedUserIds the task's list of assigned user IDs
   */
  public void setAssignedUserIds(List<UUID> assignedUserIds) {
    taskSetAssignedUserIdsValidator(assignedUserIds);
    this.assignedUserIds = new ArrayList<>(assignedUserIds);
  }

  /**
   * Adds a user ID to the task's list of assigned user IDs.
   *
   * @param userId the user ID to add
   */
  public void addAssignedUserId(UUID userId) {
    taskAddAssignedUserIdValidator(userId);
    this.assignedUserIds.add(userId);
  }
}