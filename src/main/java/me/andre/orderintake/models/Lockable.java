package me.andre.orderintake.models;

/**
 * Represents a resource that can be locked.
 * <p>
 * Implementations of this interface provide a unique key that identifies the resource to be locked.
 * This key is typically used to ensure mutual exclusion when accessing or modifying resources of
 * the same type.
 * </p>
 */
public interface Lockable {

  /**
   * Returns a unique key representing the resource to be locked.
   *
   * @return the lock key used for identifying and locking the resource
   */
  String lockKey();
}
