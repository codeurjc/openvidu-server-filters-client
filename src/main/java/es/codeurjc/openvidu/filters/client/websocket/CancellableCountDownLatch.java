package es.codeurjc.openvidu.filters.client.websocket;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * CountDownLatch that may be cancelled, throwing an InterruptedException on any
 * thread running the {@link CancellableCountDownLatch#await()} method. An error
 * message will be available in the object if the exception by cancelation is
 * thrown {@link CancellableCountDownLatch#getErrorMessage()}
 */
public class CancellableCountDownLatch {

	private final CountDownLatch latch;
	private final List<Thread> waiters;
	private boolean cancelled = false;
	private String errorMessage;

	public CancellableCountDownLatch(int count) {
		latch = new CountDownLatch(count);
		waiters = new ArrayList<Thread>();
	}

	public void await() throws InterruptedException {
		try {
			addWaiter();
			latch.await();
		} finally {
			removeWaiter();
		}
	}

	public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
		try {
			addWaiter();
			return latch.await(timeout, unit);
		} finally {
			removeWaiter();
		}
	}

	private synchronized void addWaiter() throws InterruptedException {
		if (cancelled) {
			Thread.currentThread().interrupt();
			throw new InterruptedException("Latch has already been cancelled");
		}
		waiters.add(Thread.currentThread());
	}

	private synchronized void removeWaiter() {
		waiters.remove(Thread.currentThread());
	}

	public void countDown() {
		latch.countDown();
	}

	public synchronized void cancel(String errorMessage) {
		this.errorMessage = errorMessage;
		if (!cancelled) {
			cancelled = true;
			for (Thread waiter : waiters) {
				waiter.interrupt();
			}
			waiters.clear();
		}
	}

	public long getCount() {
		return latch.getCount();
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	@Override
	public String toString() {
		return latch.toString();
	}
}