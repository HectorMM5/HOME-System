package no.ntnu.idatx1005.model.task;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * <h3>Task Change Record</h3>
 *
 * <p>A record that represents a change made to a task.
 *
 * @author William Holtsdalen
 * @since V1.1.0
 */
public record TaskChange(UUID id, UUID taskId, String description, UUID changedBy, 
    LocalDateTime changedAt) {} 