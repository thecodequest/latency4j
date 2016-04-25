package org.latency4j.util;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

public class LockUtil {

	public static void acquireReadLock(final ReadWriteLock lock) {
		acquireLock(lock.readLock());
	}

	public static void releaseReadLock(final ReadWriteLock lock) {
		releaseLock(lock.readLock());
	}

	public static void acquireWriteLock(final ReadWriteLock lock) {
		acquireLock(lock.writeLock());
	}

	public static void releaseWriteLock(final ReadWriteLock lock) {
		releaseLock(lock.writeLock());
	}

	public static void acquireLock(final Lock lock) {
		lock.lock();
	}

	public static void releaseLock(final Lock lock) {
		lock.unlock();
	}

	public static void upgradeReadLock(final ReadWriteLock lock) {
		releaseReadLock(lock);
		acquireWriteLock(lock);
	}

	public static void downgradeWriteLock(final ReadWriteLock lock) {
		acquireReadLock(lock);
		releaseWriteLock(lock);
	}
}
