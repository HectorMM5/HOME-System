package no.ntnu.idatx1005.model.user;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import no.ntnu.idatx1005.service.DatabaseUtils;

/**
 * Test class for the User model.
 */
public class UserTest {
  private UUID id;
  private String firstName;
  private String lastName;
  private String email;
  private byte[] passwordHash;
  private byte[] salt;
  private int taskCapacity;
  private boolean sickness;
  private User user;

  @BeforeEach
  void setUp() {
    id = UUID.randomUUID();
    firstName = "Firstname";
    lastName = "Lastname";
    email = "test@test.com";
    passwordHash = DatabaseUtils.hashPassword("password", DatabaseUtils.generateSalt());
    salt = DatabaseUtils.generateSalt();
    taskCapacity = 100;
    sickness = false;
    user = new User(id, firstName, lastName, email, passwordHash, salt, taskCapacity, sickness);
  }

  @Nested
  @DisplayName("Constructor Tests")
  class ConstructorTests {
    @Test
    @DisplayName("Constructor with valid arguments creates user successfully")
    void constructorWithValidArgumentsCreatesUserSuccessfully() {
      assertNotNull(user);
      assertEquals(id, user.getId());
      assertEquals(firstName, user.getFirstName());
      assertEquals(lastName, user.getLastName());
      assertEquals(email, user.getEmail());
      assertEquals(passwordHash, user.getPasswordHash());
      assertEquals(salt, user.getSalt());
      assertEquals(taskCapacity, user.getTaskCapacity());
      assertEquals(sickness, user.getSickness());
    }

    @Test
    @DisplayName("Constructor throws IllegalArgumentException when id is null")
    void constructorThrowsIllegalArgumentExceptionWhenIdIsNull() {
      assertThrows(IllegalArgumentException.class, () -> 
          new User(null, firstName, lastName, email, passwordHash, salt, taskCapacity, sickness));
    }

    @Test
    @DisplayName("Constructor throws IllegalArgumentException when firstName is null")
    void constructorThrowsIllegalArgumentExceptionWhenFirstNameIsNull() {
      assertThrows(IllegalArgumentException.class, () -> 
          new User(id, null, lastName, email, passwordHash, salt, taskCapacity, sickness));
    }

    @Test
    @DisplayName("Constructor throws IllegalArgumentException when firstName is empty")
    void constructorThrowsIllegalArgumentExceptionWhenFirstNameIsEmpty() {
      assertThrows(IllegalArgumentException.class, () -> 
          new User(id, "", lastName, email, passwordHash, salt, taskCapacity, sickness));
    }

    @Test
    @DisplayName("Constructor throws IllegalArgumentException when lastName is null")
    void constructorThrowsIllegalArgumentExceptionWhenLastNameIsNull() {
      assertThrows(IllegalArgumentException.class, () -> 
          new User(id, firstName, null, email, passwordHash, salt, taskCapacity, sickness));
    }

    @Test
    @DisplayName("Constructor throws IllegalArgumentException when lastName is empty")
    void constructorThrowsIllegalArgumentExceptionWhenLastNameIsEmpty() {
      assertThrows(IllegalArgumentException.class, () -> 
          new User(id, firstName, "", email, passwordHash, salt, taskCapacity, sickness));
    }

    @Test
    @DisplayName("Constructor throws IllegalArgumentException when email is null")
    void constructorThrowsIllegalArgumentExceptionWhenEmailIsNull() {
      assertThrows(IllegalArgumentException.class, () -> 
          new User(id, firstName, lastName, null, passwordHash, salt, taskCapacity, sickness));
    }

    @Test
    @DisplayName("Constructor throws IllegalArgumentException when email is empty")
    void constructorThrowsIllegalArgumentExceptionWhenEmailIsEmpty() {
      assertThrows(IllegalArgumentException.class, () -> 
          new User(id, firstName, lastName, "", passwordHash, salt, taskCapacity, sickness));
    }

    @Test
    @DisplayName("Constructor throws IllegalArgumentException when passwordHash is null")
    void constructorThrowsIllegalArgumentExceptionWhenPasswordHashIsNull() {
      assertThrows(IllegalArgumentException.class, () -> 
          new User(id, firstName, lastName, email, null, salt, taskCapacity, sickness));
    }

    @Test
    @DisplayName("Constructor throws IllegalArgumentException when passwordHash is empty")
    void constructorThrowsIllegalArgumentExceptionWhenPasswordHashIsEmpty() {
      assertThrows(IllegalArgumentException.class, () -> 
          new User(id, firstName, lastName, email, new byte[0], salt, taskCapacity, sickness));
    }

    @Test
    @DisplayName("Constructor throws IllegalArgumentException when salt is null")
    void constructorThrowsIllegalArgumentExceptionWhenSaltIsNull() {
      assertThrows(IllegalArgumentException.class, () -> 
          new User(id, firstName, lastName, email, passwordHash, null, taskCapacity, sickness));
    }

    @Test
    @DisplayName("Constructor throws IllegalArgumentException when taskCapacity is negative")
    void constructorThrowsIllegalArgumentExceptionWhenTaskCapacityIsNegative() {
      assertThrows(IllegalArgumentException.class, () -> 
          new User(id, firstName, lastName, email, passwordHash, salt, -1, sickness));
    }
  }

  @Nested
  @DisplayName("Getter Tests")
  class GetterTests {
    @Test
    @DisplayName("getId returns correct id")
    void getIdReturnsCorrectId() {
      assertEquals(id, user.getId());
    }

    @Test
    @DisplayName("getFirstName returns correct first name")
    void getFirstNameReturnsCorrectFirstName() {
      assertEquals(firstName, user.getFirstName());
    }

    @Test
    @DisplayName("getLastName returns correct last name")
    void getLastNameReturnsCorrectLastName() {
      assertEquals(lastName, user.getLastName());
    }

    @Test
    @DisplayName("getEmail returns correct email")
    void getEmailReturnsCorrectEmail() {
      assertEquals(email, user.getEmail());
    }

    @Test
    @DisplayName("getPasswordHash returns correct password hash")
    void getPasswordHashReturnsCorrectPasswordHash() {
      assertEquals(passwordHash, user.getPasswordHash());
    }

    @Test
    @DisplayName("getSalt returns correct salt")
    void getSaltReturnsCorrectSalt() {
      assertEquals(salt, user.getSalt());
    }

    @Test
    @DisplayName("getTaskCapacity returns correct task capacity")
    void getTaskCapacityReturnsCorrectTaskCapacity() {
      assertEquals(taskCapacity, user.getTaskCapacity());
    }

    @Test
    @DisplayName("getSickness returns correct sickness status")
    void getSicknessReturnsCorrectSicknessStatus() {
      assertEquals(sickness, user.getSickness());
    }
  }

  @Nested
  @DisplayName("Setter Tests")
  class SetterTests {
    @Test
    @DisplayName("setId sets correct id")
    void setIdSetsCorrectId() {
      UUID newId = UUID.randomUUID();
      user.setId(newId);
      assertEquals(newId, user.getId());
    }

    @Test
    @DisplayName("setId throws IllegalArgumentException when id is null")
    void setIdThrowsIllegalArgumentExceptionWhenIdIsNull() {
      assertThrows(IllegalArgumentException.class, () -> user.setId(null));
    }

    @Test
    @DisplayName("setFirstName sets correct first name")
    void setFirstNameSetsCorrectFirstName() {
      String newFirstName = "New Firstname";
      user.setFirstName(newFirstName);
      assertEquals(newFirstName, user.getFirstName());
    }

    @Test
    @DisplayName("setFirstName throws IllegalArgumentException when first name is null")
    void setFirstNameThrowsIllegalArgumentExceptionWhenFirstNameIsNull() {
      assertThrows(IllegalArgumentException.class, () -> user.setFirstName(null));
    }

    @Test
    @DisplayName("setFirstName throws IllegalArgumentException when first name is empty")
    void setFirstNameThrowsIllegalArgumentExceptionWhenFirstNameIsEmpty() {
      assertThrows(IllegalArgumentException.class, () -> user.setFirstName(""));
    }

    @Test
    @DisplayName("setLastName sets correct last name")
    void setLastNameSetsCorrectLastName() {
      String newLastName = "New Lastname";
      user.setLastName(newLastName);
      assertEquals(newLastName, user.getLastName());
    }

    @Test
    @DisplayName("setLastName throws IllegalArgumentException when last name is null")
    void setLastNameThrowsIllegalArgumentExceptionWhenLastNameIsNull() {
      assertThrows(IllegalArgumentException.class, () -> user.setLastName(null));
    }

    @Test
    @DisplayName("setLastName throws IllegalArgumentException when last name is empty")
    void setLastNameThrowsIllegalArgumentExceptionWhenLastNameIsEmpty() {
      assertThrows(IllegalArgumentException.class, () -> user.setLastName(""));
    }

    @Test
    @DisplayName("setEmail sets correct email")
    void setEmailSetsCorrectEmail() {
      String newEmail = "new@test.com";
      user.setEmail(newEmail);
      assertEquals(newEmail, user.getEmail());
    }

    @Test
    @DisplayName("setEmail throws IllegalArgumentException when email is null")
    void setEmailThrowsIllegalArgumentExceptionWhenEmailIsNull() {
      assertThrows(IllegalArgumentException.class, () -> user.setEmail(null));
    }

    @Test
    @DisplayName("setEmail throws IllegalArgumentException when email is empty")
    void setEmailThrowsIllegalArgumentExceptionWhenEmailIsEmpty() {
      assertThrows(IllegalArgumentException.class, () -> user.setEmail(""));
    }

    @Test
    @DisplayName("setPasswordHash sets correct password hash")
    void setPasswordHashSetsCorrectPasswordHash() {
      byte[] newPasswordHash = DatabaseUtils.hashPassword("newpassword", DatabaseUtils.generateSalt());
      user.setPasswordHash(newPasswordHash);
      assertEquals(newPasswordHash, user.getPasswordHash());
    }

    @Test
    @DisplayName("setPasswordHash throws IllegalArgumentException when password hash is null")
    void setPasswordHashThrowsIllegalArgumentExceptionWhenPasswordHashIsNull() {
      assertThrows(IllegalArgumentException.class, () -> user.setPasswordHash(null));
    }

    @Test
    @DisplayName("setPasswordHash throws IllegalArgumentException when password hash is empty")
    void setPasswordHashThrowsIllegalArgumentExceptionWhenPasswordHashIsEmpty() {
      assertThrows(IllegalArgumentException.class, () -> user.setPasswordHash(new byte[0]));
    }

    @Test
    @DisplayName("setSalt sets correct salt")
    void setSaltSetsCorrectSalt() {
      byte[] newSalt = DatabaseUtils.generateSalt();
      user.setSalt(newSalt);
      assertEquals(newSalt, user.getSalt());
    }

    @Test
    @DisplayName("setSalt throws IllegalArgumentException when salt is null")
    void setSaltThrowsIllegalArgumentExceptionWhenSaltIsNull() {
      assertThrows(IllegalArgumentException.class, () -> user.setSalt(null));
    }

    @Test
    @DisplayName("setTaskCapacity sets correct task capacity")
    void setTaskCapacitySetsCorrectTaskCapacity() {
      int newTaskCapacity = 200;
      user.setTaskCapacity(newTaskCapacity);
      assertEquals(newTaskCapacity, user.getTaskCapacity());
    }

    @Test
    @DisplayName("setTaskCapacity throws IllegalArgumentException when task capacity is negative")
    void setTaskCapacityThrowsIllegalArgumentExceptionWhenTaskCapacityIsNegative() {
      assertThrows(IllegalArgumentException.class, () -> user.setTaskCapacity(-1));
    }

    @Test
    @DisplayName("setSickness sets correct sickness status")
    void setSicknessSetsCorrectSicknessStatus() {
      user.setSickness(true);
      assertEquals(true, user.getSickness());
    }
  }

  @Nested
  @DisplayName("Equals and HashCode Tests")
  class EqualsAndHashCodeTests {
    @Test
    @DisplayName("equals returns true when comparing with same object")
    void equalsReturnsTrueWhenComparingWithSameObject() {
      assertTrue(user.equals(user));
    }

    @Test
    @DisplayName("equals returns true when comparing with different object with same id")
    void equalsReturnsTrueWhenComparingWithDifferentObjectWithSameId() {
      User otherUser = new User(id, "Different", "Name", "different@test.com", 
          passwordHash, salt, 200, true);
      assertTrue(user.equals(otherUser));
    }

    @Test
    @DisplayName("equals returns false when comparing with null")
    void equalsReturnsFalseWhenComparingWithNull() {
      assertFalse(user.equals(null));
    }

    @Test
    @DisplayName("equals returns false when comparing with different class")
    void equalsReturnsFalseWhenComparingWithDifferentClass() {
      assertFalse(user.equals("Not a User object"));
    }

    @Test
    @DisplayName("equals returns false when comparing with different id")
    void equalsReturnsFalseWhenComparingWithDifferentId() {
      User otherUser = new User(UUID.randomUUID(), firstName, lastName, email, 
          passwordHash, salt, taskCapacity, sickness);
      assertFalse(user.equals(otherUser));
    }

    @Test
    @DisplayName("hashCode returns same value for objects with same id")
    void hashCodeReturnsSameValueForObjectsWithSameId() {
      User otherUser = new User(id, "Different", "Name", "different@test.com", 
          passwordHash, salt, 200, true);
      assertEquals(user.hashCode(), otherUser.hashCode());
    }

    @Test
    @DisplayName("hashCode returns different value for objects with different id")
    void hashCodeReturnsDifferentValueForObjectsWithDifferentId() {
      User otherUser = new User(UUID.randomUUID(), firstName, lastName, email, 
          passwordHash, salt, taskCapacity, sickness);
      assertFalse(user.hashCode() == otherUser.hashCode());
    }
  }
}
