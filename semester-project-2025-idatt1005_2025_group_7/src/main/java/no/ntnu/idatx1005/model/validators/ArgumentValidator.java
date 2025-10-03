package no.ntnu.idatx1005.model.validators;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import no.ntnu.idatx1005.model.task.Priority;
import no.ntnu.idatx1005.model.task.Size;

/**
 * <h3>Argument Validator</h3>
 *
 * <p>A utility class for validating arguments passed to constructors and methods. The class has
 * a private constructor to prevent instantiation.
 *
 * @author William Holtsdalen
 * @since V0.1.0
 */
public class ArgumentValidator {

  /**
   * Prevents instantiation of the ArgumentValidator class.
   */
  private ArgumentValidator() {}

  /**
   * Validates the arguments for the setId method of the User class.
   *
   * @param id the user's id
   * @throws IllegalArgumentException if the user's id is null
   */
  public static void userSetIdValidator(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("User id cannot be null.");
    }
  }

  /**
   * Validates the arguments for the setFirstName method of the User class.
   *
   * @param firstName the user's first name
   * @throws IllegalArgumentException if the user's first name is null or an empty string
   */
  public static void userSetFirstNameValidator(String firstName) {
    if (firstName == null || firstName.isEmpty()) {
      throw new IllegalArgumentException("User first name cannot be null or an empty string.");
    }
  }

  /**
   * Validates the arguments for the setLastName method of the User class.
   *
   * @param lastName the user's last name
   * @throws IllegalArgumentException if the user's last name is null or an empty string
   */
  public static void userSetLastNameValidator(String lastName) {
    if (lastName == null || lastName.isEmpty()) {
      throw new IllegalArgumentException("User last name cannot be null or an empty string.");
    }
  }

  /**
   * Validates the arguments for the setEmail method of the User class.
   *
   * @param email the user's email
   * @throws IllegalArgumentException if the user's email is null or an empty string
   *
   */
  public static void userSetEmailValidator(String email) {
    if (email == null || email.isEmpty()) {
      throw new IllegalArgumentException("User email cannot be null or an empty string.");
    }
  }

  /**
   * Validates the arguments for the setPasswordHash method of the User class.
   *
   * @param passwordHash the user's password hash
   * @throws IllegalArgumentException if the user's password hash is null or empty
   */
  public static void userSetPasswordHashValidator(byte[] passwordHash) {
    if (passwordHash == null || passwordHash.length == 0) {
      throw new IllegalArgumentException("User password hash cannot be null or empty.");
    }
  }

  /**
   * Validates the arguments for the setSalt method of the User class.
   *
   * @param salt the user's salt
   * @throws IllegalArgumentException if the user's salt is null
   */
  public static void userSetSaltValidator(byte[] salt) {
    if (salt == null) {
      throw new IllegalArgumentException("User salt cannot be null.");
    }
  }

  /**
   * Validates the arguments for the setTaskCapacity method of the User class.
   *
   * @param taskCapacity the user's workload capacity
   * @throws IllegalArgumentException if the user's task capacity is negative
   */
  public static void userSetTaskCapacityValidator(int taskCapacity) {
    if (taskCapacity < 0) {
      throw new IllegalArgumentException("User workload capacity cannot be negative.");
    }
  }

  /**
   * Validates the arguments for the setId method of the Task class.
   *
   * @param id the task's id
   * @throws IllegalArgumentException if the task's id is null
   */
  public static void taskSetIdValidator(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("Task id cannot be null.");
    }
  }

  /**
   * Validates the arguments for the setName method of the Task class.
   *
   * @param name the task's name
   * @throws IllegalArgumentException if the task's name is null or an empty string
   */
  public static void taskSetNameValidator(String name) {
    if (name == null || name.isEmpty()) {
      throw new IllegalArgumentException("Task name cannot be null or an empty string.");
    }
  }

  /**
   * Validates the arguments for the setDescription method of the Task class.
   *
   * @param description the task's description
   * @throws IllegalArgumentException if the task's description is null
   */
  public static void taskSetDescriptionValidator(String description) {
    if (description == null) {
      throw new IllegalArgumentException("Task description cannot be null.");
    }
  }

  /**
   * Validates the arguments for the setCreatedDate method of the Task class.
   *
   * @param createdDate the task's created date
   * @throws IllegalArgumentException if the task's created date is null
   */
  public static void taskSetCreatedDateValidator(LocalDateTime createdDate) {
    if (createdDate == null) {
      throw new IllegalArgumentException("Task created date cannot be null.");
    }
  }

  /**
   * Validates the arguments for the setDueDate method of the Task class.
   *
   * @param dueDate the task's due date
   * @throws IllegalArgumentException if the task's due date is null
   */
  public static void taskSetDueDateValidator(LocalDateTime dueDate) {
    if (dueDate == null) {
      throw new IllegalArgumentException("Task due date cannot be null.");
    }
  }

  /**
   * Validates the arguments for the setPriority method of the Task class.
   *
   * @param priority the task's priority
   * @throws IllegalArgumentException if the task's priority is null
   */
  public static void taskSetPriorityValidator(Priority priority) {
    if (priority == null) {
      throw new IllegalArgumentException("Task priority cannot be null.");
    }
  }

  /**
   * Validates the arguments for the setTaskSize method of the Task class.
   *
   * @param taskSize the task's size
   * @throws IllegalArgumentException if the task's size is negative
   */
  public static void taskSetSizeValidator(Size taskSize) {
    if (taskSize == null) {
      throw new IllegalArgumentException("Task task size cannot be null.");
    }
  }

  /**
   * Validates the arguments for the setAssignedUserIds method of the Task class.
   *
   * @param assignedUserIds the task's list of assigned user IDs
   * @throws IllegalArgumentException if the task's list of assigned user IDs is null
   */
  public static void taskSetAssignedUserIdsValidator(List<UUID> assignedUserIds) {
    if (assignedUserIds == null) {
      throw new IllegalArgumentException("Task assigned users cannot be null.");
    }
  }

  /**
   * Validates the arguments for the addAssignedUserId method of the Task class.
   *
   * @param userId the user ID to add
   * @throws IllegalArgumentException if the user ID is null
   */
  public static void taskAddAssignedUserIdValidator(UUID userId) {
    if (userId == null) {
      throw new IllegalArgumentException("Task assigned user ID cannot be null.");
    }
  }

  /**
   * Validates the arguments for the setAssignedUserNames method of the Task class.
   *
   * @param assignedUserNames the task's assigned user names
   * @throws IllegalArgumentException if the task's assigned user names is null
   */
  public static void taskSetAssignedUserNamesValidator(String assignedUserNames) {
    if (assignedUserNames == null) {
      throw new IllegalArgumentException("Task assigned users cannot be null.");
    }
  }

}
