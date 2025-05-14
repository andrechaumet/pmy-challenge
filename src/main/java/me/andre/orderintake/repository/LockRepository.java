package me.andre.orderintake.repository;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.andre.orderintake.exception.LockTimeoutException;
import me.andre.orderintake.models.Lockable;
import org.springframework.stereotype.Repository;

/**
 * Locking mechanisms on {@link Lockable} resources.
 * <p>
 * Provides a contract to implement locking and unlocking logic for resources that implement
 * {@code Lockable}, typically to ensure mutual exclusion when performing operations on shared
 * interest entities.
 * </p>
 *
 * @param <T> the type of lockable resource
 */

@Slf4j
@Repository
@RequiredArgsConstructor
public class LockRepository<T extends Lockable> {

  private final ConcurrentHashMap<String, ReentrantLock> locksMemory;

  /**
   * Executes the given {@link Runnable} while holding a lock on the specified {@code lockable}
   * resource. All lockable resources must be of the same type.
   * <p>
   * Ensures that the lock is properly released after the operation, even if an exception occurs.
   * </p>
   *
   * @param runnable the operation to execute while holding the lock
   * @param lockable the resource to lock during the operation
   */
  public void locked(Runnable runnable, T lockable) {
    var lock = doLock(lockable);
    try {
      runnable.run();
    } finally {
      doUnlock(lock, lockable);
    }
  }

  private ReentrantLock doLock(Lockable lockable) {
    ReentrantLock lock = locksMemory.computeIfAbsent(lockable.lockKey(),
        key -> new ReentrantLock());
    if (lock.tryLock()) {
      return lock;
    }
    throw LockTimeoutException.instance;
  }

  private void doUnlock(ReentrantLock lock, T lockable) {
    lock.unlock();
    if (!lock.isLocked() || !lock.hasQueuedThreads()) {
      locksMemory.remove(lockable.lockKey());
    }
  }
}