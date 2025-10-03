package no.ntnu.idatx1005.model.user;

import static no.ntnu.idatx1005.model.validators.ArgumentValidator.userSetEmailValidator;
import static no.ntnu.idatx1005.model.validators.ArgumentValidator.userSetFirstNameValidator;
import static no.ntnu.idatx1005.model.validators.ArgumentValidator.userSetIdValidator;
import static no.ntnu.idatx1005.model.validators.ArgumentValidator.userSetLastNameValidator;
import static no.ntnu.idatx1005.model.validators.ArgumentValidator.userSetPasswordHashValidator;
import static no.ntnu.idatx1005.model.validators.ArgumentValidator.userSetSaltValidator;
import static no.ntnu.idatx1005.model.validators.ArgumentValidator.userSetTaskCapacityValidator;

import java.util.UUID;
import no.ntnu.idatx1005.model.task.Task;


/**
 * <h3>Represents a user of the application.</h3>
 *
 * <p>A user has a unique id (UUID), a first name, a last name, an
 * email, a passwordHash, a passwordHash salt, a workload capacity, and a sickness status. The user
 * can also have a list of assigned tasks.
 *
 * @see UUID
 * @see Task
 * @author Hector Mendana Morales
 * @author William Holtsdalen
 * @since V0.1.0
 */
public class User {
  private UUID id;
  private String firstName;
  private String lastName;
  private String email;
  private byte[] passwordHash;
  private byte[] salt;
  private int taskCapacity;
  private boolean sickness;

  /**
   * Constructs a new user with the given id, first name, last name, email, passwordHash, and
   * workload capacity.
   *
   * @param id the user id
   * @param firstName the user's first name
   * @param lastName the user's last name
   * @param email the user's email
   * @param passwordHash the user's password hash
   * @param taskCapacity the user's workload capacity
   * @param isSick the user's sickness status
   */
  public User(UUID id, String firstName, String lastName, String email, byte[] passwordHash,
      byte[] salt, int taskCapacity, boolean isSick) {

    setId(id);
    setFirstName(firstName);
    setLastName(lastName);
    setEmail(email);
    setPasswordHash(passwordHash);
    setSalt(salt);
    setTaskCapacity(taskCapacity);
    setSickness(isSick);
  }

  /**
   * Returns the user id.
   *
   * @return the user id
   */
  public UUID getId() {
    return id;
  }

  /**
   * Returns the user's first name.
   *
   * @return the user's first name
   */
  public String getFirstName() {
    return firstName;
  }

  /**
   * Returns the user's last name.
   *
   * @return the user's last name
   */
  public String getLastName() {
    return lastName;
  }

  /**
   * Returns the user's email.
   *
   * @return the user's email
   */
  public String getEmail() {
    return email;
  }

  /**
   * Returns the user's password hash.
   *
   * @return the user's password hash
   */
  public byte[] getPasswordHash() {
    return passwordHash;
  }

  /**
   * Returns the user's salt.
   *
   * @return the user's salt
   */
  public byte[] getSalt() {
    return salt;
  }

  /**
   * Returns the user's workload capacity.
   *
   * @return the user's workload capacity
   */
  public int getTaskCapacity() {
    return taskCapacity;
  }

  /**
   * Returns the user's sickness.
   *
   * @return the user's sickness (true = sick, false = healthy)
   */
  public boolean getSickness() {
    return sickness;
  }

  /**
   * Sets the user's id.
   *
   * @param id the user's id
   */
  public void setId(UUID id) {
    userSetIdValidator(id);
    this.id = id;
  }

  /**
   * Sets the user's first name.
   *
   * @param firstName the user's first name
   */
  public void setFirstName(String firstName) {
    userSetFirstNameValidator(firstName);
    this.firstName = firstName;
  }

  /**
   * Sets the user's last name.
   *
   * @param lastName the user's last name
   */
  public void setLastName(String lastName) {
    userSetLastNameValidator(lastName);
    this.lastName = lastName;
  }

  /**
   * Sets the user's email.
   *
   * @param email the user's email
   */
  public void setEmail(String email) {
    userSetEmailValidator(email);
    this.email = email;
  }

  /**
   * Sets the user's password hash.
   *
   * @param passwordHash the user's password hash
   */
  public void setPasswordHash(byte[] passwordHash) {
    userSetPasswordHashValidator(passwordHash);
    this.passwordHash = passwordHash;
  }

  /**
   * Sets the user's salt.
   *
   * @param salt the user's salt
   */
  public void setSalt(byte[] salt) {
    userSetSaltValidator(salt);
    this.salt = salt;
  }

  /**
   * Sets the user's workload capacity.
   *
   * @param taskCapacity the user's workload capacity
   */
  public void setTaskCapacity(int taskCapacity) {
    userSetTaskCapacityValidator(taskCapacity);
    this.taskCapacity = taskCapacity;
  }

  /**
   * Sets the user's sickness.
   *
   * @param bool the user's sickness (true = sick, false = healthy)
   */
  public void setSickness(boolean bool) {
    this.sickness = bool;
  }

  /**
   * Checks if two users are equal, based on their id.
   *
   * @param o the object to compare to
   * @return true if the users are equal, false otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User user = (User) o;
    return id.equals(user.id);
  }

  /**
   * Returns the hash code of the user, based on their id.
   *
   * @return the hash code of the user
   */
  @Override
  public int hashCode() {
    return id.hashCode();
  }
}