package no.ntnu.idatx1005.model.task;

/**
 * <h3>Size Enum</h3>
 *
 * <p>The enum {@code Size} is used to represent the size of a task.
 *
 * @author William Holtsdalen
 * @since V1.1.0
 */
public enum Size {
  XS(2),
  S(4),
  M(6),
  L(8),
  XL(10);

  private final int value;

  /**
   * Constructs a new {@code Size} enum with the given value.
   *
   * @param value the value of the size
   */
  Size(int value) {
    this.value = value;
  }

  /**
   * Returns the value of the size.
   *
   * @return the value of the size
   */
  public int getValue() {
    return value;
  }
}
