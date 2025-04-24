package me.andre.orderintake.repository;


import me.andre.orderintake.models.Lockable;

/**
 * Interface for locking mechanisms on {@link Lockable} resources.
 * <p>
 * Provides a contract to implement locking and unlocking logic for resources that implement
 * {@code Lockable}, typically to ensure mutual exclusion when performing operations on shared
 * entities.
 * </p>
 *
 * @param <T> the type of lockable resource
 */
public interface LockRepository<T extends Lockable> {

  /**
   * Executes the given {@link Runnable} while holding a lock on the specified {@code lockable}
   * resource.
   * <p>
   * Ensures that the lock is properly released after the operation, even if an exception occurs.
   * </p>
   *
   * @param runnable the operation to execute while holding the lock
   * @param lockable the resource to lock during the operation
   */
  default void locked(Runnable runnable, T lockable) {
    try {
      doLock(lockable);
      runnable.run();
    } finally {
      unlock(lockable);
    }
  }

  /**
   * Performs the locking logic for the specified {@code lockable} resource.
   *
   * @param lockable the resource to lock
   */
  void doLock(Lockable lockable);

  /**
   * Releases the lock for the specified {@code lockable} resource.
   *
   * @param lockable the resource to unlock
   */
  void unlock(Lockable lockable);
}
