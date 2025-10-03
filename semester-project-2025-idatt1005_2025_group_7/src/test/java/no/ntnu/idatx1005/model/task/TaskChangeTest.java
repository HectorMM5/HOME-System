package no.ntnu.idatx1005.model.task;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Test class for the TaskChange model.
 */
public class TaskChangeTest {
  private UUID id;
  private UUID taskId;
  private String description;
  private UUID changedBy;
  private LocalDateTime changedAt;
  private TaskChange taskChange;

  @BeforeEach
  void setUp() {
    id = UUID.randomUUID();
    taskId = UUID.randomUUID();
    description = "Task name was changed from 'Old Name' to 'New Name'";
    changedBy = UUID.randomUUID();
    changedAt = LocalDateTime.now();
    taskChange = new TaskChange(id, taskId, description, changedBy, changedAt);
  }

  @Nested
  @DisplayName("Constructor Tests")
  class ConstructorTests {
    @Test
    @DisplayName("Constructor with valid arguments creates task change successfully")
    void constructorWithValidArgumentsCreatesTaskChangeSuccessfully() {
      assertNotNull(taskChange);
      assertEquals(id, taskChange.id());
      assertEquals(taskId, taskChange.taskId());
      assertEquals(description, taskChange.description());
      assertEquals(changedBy, taskChange.changedBy());
      assertEquals(changedAt, taskChange.changedAt());
    }
  }

  @Nested
  @DisplayName("Getter Tests")
  class GetterTests {
    @Test
    @DisplayName("id returns correct id")
    void idReturnsCorrectId() {
      assertEquals(id, taskChange.id());
    }

    @Test
    @DisplayName("taskId returns correct task id")
    void taskIdReturnsCorrectTaskId() {
      assertEquals(taskId, taskChange.taskId());
    }

    @Test
    @DisplayName("description returns correct description")
    void descriptionReturnsCorrectDescription() {
      assertEquals(description, taskChange.description());
    }

    @Test
    @DisplayName("changedBy returns correct user id")
    void changedByReturnsCorrectUserId() {
      assertEquals(changedBy, taskChange.changedBy());
    }

    @Test
    @DisplayName("changedAt returns correct timestamp")
    void changedAtReturnsCorrectTimestamp() {
      assertEquals(changedAt, taskChange.changedAt());
    }
  }
} 