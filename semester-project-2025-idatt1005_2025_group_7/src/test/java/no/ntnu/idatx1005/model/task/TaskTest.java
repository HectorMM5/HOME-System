package no.ntnu.idatx1005.model.task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Test class for the Task model.
 */
public class TaskTest {
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
  private Task task;

  @BeforeEach
  void setUp() {
    id = UUID.randomUUID();
    completed = false;
    name = "Test Task";
    description = "Test Description";
    createdDate = LocalDateTime.now();
    dueDate = LocalDateTime.now().plusDays(1);
    completedDate = null;
    priority = Priority.HIGH;
    size = Size.M;
    assignedUserIds = new ArrayList<>();
    task = new Task(id, completed, name, description, createdDate, dueDate, priority, size);
  }

  @Nested
  @DisplayName("Constructor Tests")
  class ConstructorTests {
    @Test
    @DisplayName("Constructor with valid arguments creates task successfully")
    void constructorWithValidArgumentsCreatesTaskSuccessfully() {
      assertNotNull(task);
      assertEquals(id, task.getId());
      assertEquals(completed, task.isCompleted());
      assertEquals(name, task.getName());
      assertEquals(description, task.getDescription());
      assertEquals(createdDate, task.getCreatedDate());
      assertEquals(dueDate, task.getDueDate());
      assertEquals(priority, task.getPriority());
      assertEquals(size, task.getSize());
      assertTrue(task.getAssignedUserIds().isEmpty());
    }

    @Test
    @DisplayName("Constructor throws IllegalArgumentException when id is null")
    void constructorThrowsIllegalArgumentExceptionWhenIdIsNull() {
      assertThrows(IllegalArgumentException.class, () -> 
          new Task(null, completed, name, description, createdDate, dueDate, priority, size));
    }

    @Test
    @DisplayName("Constructor throws IllegalArgumentException when name is null")
    void constructorThrowsIllegalArgumentExceptionWhenNameIsNull() {
      assertThrows(IllegalArgumentException.class, () -> 
          new Task(id, completed, null, description, createdDate, dueDate, priority, size));
    }

    @Test
    @DisplayName("Constructor throws IllegalArgumentException when name is empty")
    void constructorThrowsIllegalArgumentExceptionWhenNameIsEmpty() {
      assertThrows(IllegalArgumentException.class, () -> 
          new Task(id, completed, "", description, createdDate, dueDate, priority, size));
    }

    @Test
    @DisplayName("Constructor throws IllegalArgumentException when description is null")
    void constructorThrowsIllegalArgumentExceptionWhenDescriptionIsNull() {
      assertThrows(IllegalArgumentException.class, () -> 
          new Task(id, completed, name, null, createdDate, dueDate, priority, size));
    }

    @Test
    @DisplayName("Constructor throws IllegalArgumentException when createdDate is null")
    void constructorThrowsIllegalArgumentExceptionWhenCreatedDateIsNull() {
      assertThrows(IllegalArgumentException.class, () -> 
          new Task(id, completed, name, description, null, dueDate, priority, size));
    }

    @Test
    @DisplayName("Constructor throws IllegalArgumentException when dueDate is null")
    void constructorThrowsIllegalArgumentExceptionWhenDueDateIsNull() {
      assertThrows(IllegalArgumentException.class, () -> 
          new Task(id, completed, name, description, createdDate, null, priority, size));
    }

    @Test
    @DisplayName("Constructor throws IllegalArgumentException when priority is null")
    void constructorThrowsIllegalArgumentExceptionWhenPriorityIsNull() {
      assertThrows(IllegalArgumentException.class, () -> 
          new Task(id, completed, name, description, createdDate, dueDate, null, size));
    }

    @Test
    @DisplayName("Constructor throws IllegalArgumentException when size is null")
    void constructorThrowsIllegalArgumentExceptionWhenSizeIsNull() {
      assertThrows(IllegalArgumentException.class, () -> 
          new Task(id, completed, name, description, createdDate, dueDate, priority, null));
    }
  }

  @Nested
  @DisplayName("Getter Tests")
  class GetterTests {
    @Test
    @DisplayName("getId returns correct id")
    void getIdReturnsCorrectId() {
      assertEquals(id, task.getId());
    }

    @Test
    @DisplayName("isCompleted returns correct completed status")
    void isCompletedReturnsCorrectCompletedStatus() {
      assertEquals(completed, task.isCompleted());
    }

    @Test
    @DisplayName("getName returns correct name")
    void getNameReturnsCorrectName() {
      assertEquals(name, task.getName());
    }

    @Test
    @DisplayName("getDescription returns correct description")
    void getDescriptionReturnsCorrectDescription() {
      assertEquals(description, task.getDescription());
    }

    @Test
    @DisplayName("getCreatedDate returns correct created date")
    void getCreatedDateReturnsCorrectCreatedDate() {
      assertEquals(createdDate, task.getCreatedDate());
    }

    @Test
    @DisplayName("getDueDate returns correct due date")
    void getDueDateReturnsCorrectDueDate() {
      assertEquals(dueDate, task.getDueDate());
    }

    @Test
    @DisplayName("getCompletedDate returns correct completed date")
    void getCompletedDateReturnsCorrectCompletedDate() {
      assertEquals(completedDate, task.getCompletedDate());
    }

    @Test
    @DisplayName("getPriority returns correct priority")
    void getPriorityReturnsCorrectPriority() {
      assertEquals(priority, task.getPriority());
    }

    @Test
    @DisplayName("getSize returns correct size")
    void getSizeReturnsCorrectSize() {
      assertEquals(size, task.getSize());
    }

    @Test
    @DisplayName("getAssignedUserIds returns correct assigned user ids")
    void getAssignedUserIdsReturnsCorrectAssignedUserIds() {
      assertEquals(assignedUserIds, task.getAssignedUserIds());
    }
  }

  @Nested
  @DisplayName("Setter Tests")
  class SetterTests {
    @Test
    @DisplayName("setId sets correct id")
    void setIdSetsCorrectId() {
      UUID newId = UUID.randomUUID();
      task.setId(newId);
      assertEquals(newId, task.getId());
    }

    @Test
    @DisplayName("setId throws IllegalArgumentException when id is null")
    void setIdThrowsIllegalArgumentExceptionWhenIdIsNull() {
      assertThrows(IllegalArgumentException.class, () -> task.setId(null));
    }

    @Test
    @DisplayName("setCompleted sets correct completed status")
    void setCompletedSetsCorrectCompletedStatus() {
      task.setCompleted(true);
      assertTrue(task.isCompleted());
    }

    @Test
    @DisplayName("setName sets correct name")
    void setNameSetsCorrectName() {
      String newName = "New Task Name";
      task.setName(newName);
      assertEquals(newName, task.getName());
    }

    @Test
    @DisplayName("setName throws IllegalArgumentException when name is null")
    void setNameThrowsIllegalArgumentExceptionWhenNameIsNull() {
      assertThrows(IllegalArgumentException.class, () -> task.setName(null));
    }

    @Test
    @DisplayName("setName throws IllegalArgumentException when name is empty")
    void setNameThrowsIllegalArgumentExceptionWhenNameIsEmpty() {
      assertThrows(IllegalArgumentException.class, () -> task.setName(""));
    }

    @Test
    @DisplayName("setDescription sets correct description")
    void setDescriptionSetsCorrectDescription() {
      String newDescription = "New Description";
      task.setDescription(newDescription);
      assertEquals(newDescription, task.getDescription());
    }

    @Test
    @DisplayName("setDescription throws IllegalArgumentException when description is null")
    void setDescriptionThrowsIllegalArgumentExceptionWhenDescriptionIsNull() {
      assertThrows(IllegalArgumentException.class, () -> task.setDescription(null));
    }

    @Test
    @DisplayName("setCreatedDate sets correct created date")
    void setCreatedDateSetsCorrectCreatedDate() {
      LocalDateTime newCreatedDate = LocalDateTime.now().plusDays(1);
      task.setCreatedDate(newCreatedDate);
      assertEquals(newCreatedDate, task.getCreatedDate());
    }

    @Test
    @DisplayName("setCreatedDate throws IllegalArgumentException when created date is null")
    void setCreatedDateThrowsIllegalArgumentExceptionWhenCreatedDateIsNull() {
      assertThrows(IllegalArgumentException.class, () -> task.setCreatedDate(null));
    }

    @Test
    @DisplayName("setDueDate sets correct due date")
    void setDueDateSetsCorrectDueDate() {
      LocalDateTime newDueDate = LocalDateTime.now().plusDays(2);
      task.setDueDate(newDueDate);
      assertEquals(newDueDate, task.getDueDate());
    }

    @Test
    @DisplayName("setDueDate throws IllegalArgumentException when due date is null")
    void setDueDateThrowsIllegalArgumentExceptionWhenDueDateIsNull() {
      assertThrows(IllegalArgumentException.class, () -> task.setDueDate(null));
    }

    @Test
    @DisplayName("setCompletedDate sets correct completed date")
    void setCompletedDateSetsCorrectCompletedDate() {
      LocalDateTime newCompletedDate = LocalDateTime.now();
      task.setCompletedDate(newCompletedDate);
      assertEquals(newCompletedDate, task.getCompletedDate());
    }

    @Test
    @DisplayName("setPriority sets correct priority")
    void setPrioritySetsCorrectPriority() {
      Priority newPriority = Priority.MEDIUM;
      task.setPriority(newPriority);
      assertEquals(newPriority, task.getPriority());
    }

    @Test
    @DisplayName("setPriority throws IllegalArgumentException when priority is null")
    void setPriorityThrowsIllegalArgumentExceptionWhenPriorityIsNull() {
      assertThrows(IllegalArgumentException.class, () -> task.setPriority(null));
    }

    @Test
    @DisplayName("setSize sets correct size")
    void setSizeSetsCorrectSize() {
      Size newSize = Size.L;
      task.setSize(newSize);
      assertEquals(newSize, task.getSize());
    }

    @Test
    @DisplayName("setSize throws IllegalArgumentException when size is null")
    void setSizeThrowsIllegalArgumentExceptionWhenSizeIsNull() {
      assertThrows(IllegalArgumentException.class, () -> task.setSize(null));
    }

    @Test
    @DisplayName("setAssignedUserIds sets correct assigned user ids")
    void setAssignedUserIdsSetsCorrectAssignedUserIds() {
      List<UUID> newAssignedUserIds = List.of(UUID.randomUUID(), UUID.randomUUID());
      task.setAssignedUserIds(newAssignedUserIds);
      assertEquals(newAssignedUserIds, task.getAssignedUserIds());
    }

    @Test
    @DisplayName("setAssignedUserIds throws IllegalArgumentException when assigned user ids is null")
    void setAssignedUserIdsThrowsIllegalArgumentExceptionWhenAssignedUserIdsIsNull() {
      assertThrows(IllegalArgumentException.class, () -> task.setAssignedUserIds(null));
    }

    @Test
    @DisplayName("addAssignedUserId adds user id correctly")
    void addAssignedUserIdAddsUserIdCorrectly() {
      UUID userId = UUID.randomUUID();
      task.addAssignedUserId(userId);
      assertTrue(task.getAssignedUserIds().contains(userId));
    }

    @Test
    @DisplayName("addAssignedUserId throws IllegalArgumentException when user id is null")
    void addAssignedUserIdThrowsIllegalArgumentExceptionWhenUserIdIsNull() {
      assertThrows(IllegalArgumentException.class, () -> task.addAssignedUserId(null));
    }
  }

  @Nested
  @DisplayName("Size Enum Tests")
  class SizeEnumTests {
    @Test
    @DisplayName("getValue returns correct value for XS")
    void getValueReturnsCorrectValueForXS() {
      assertEquals(2, Size.XS.getValue());
    }

    @Test
    @DisplayName("getValue returns correct value for S")
    void getValueReturnsCorrectValueForS() {
      assertEquals(4, Size.S.getValue());
    }

    @Test
    @DisplayName("getValue returns correct value for M")
    void getValueReturnsCorrectValueForM() {
      assertEquals(6, Size.M.getValue());
    }

    @Test
    @DisplayName("getValue returns correct value for L")
    void getValueReturnsCorrectValueForL() {
      assertEquals(8, Size.L.getValue());
    }

    @Test
    @DisplayName("getValue returns correct value for XL")
    void getValueReturnsCorrectValueForXL() {
      assertEquals(10, Size.XL.getValue());
    }
  }
}