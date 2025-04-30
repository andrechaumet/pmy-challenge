package me.andre.orderintake.repository;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.andre.orderintake.exception.LockTimeoutException;
import me.andre.orderintake.models.Lockable;
import me.andre.orderintake.models.domains.Order;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@AllArgsConstructor
public class OrderMemoryLock implements LockRepository<Order> {

  private final ConcurrentHashMap<String, ReentrantLock> locksMemory;

  @Override
  public void doLock(Lockable lockable) {
    ReentrantLock lock = locksMemory.computeIfAbsent(lockable.lockKey(), key -> new ReentrantLock());
    try {
      if (!lock.tryLock(10, MILLISECONDS)) {
        throw LockTimeoutException.instance;
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw LockTimeoutException.instance;
    }
  }

  @Override
  public void unlock(Lockable lockable) {
    Optional.ofNullable(locksMemory.get(lockable.lockKey())).ifPresent(ReentrantLock::unlock);
  }
}