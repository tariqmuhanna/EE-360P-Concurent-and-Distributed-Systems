/*
 * EID's of group members
 * 
 */

public class MonitorCyclicBarrier {
	int NumParties; //total parties
	int count; //parties yet to arrive

	public MonitorCyclicBarrier(int parties) {
		NumParties=parties;
        count=parties;
	}

	/**
	 *  If the current thread is not the last to arrive(i.e. call await() method) then
	 it waits until one of the following things happens -
	 - The last thread to call arrive(i,.e. call await() method), or
	 - Some other thread interrupts the current thread, or
	 - Some other thread interrupts one of the other waiting threads, or
	 - Some other thread times out while waiting for barrier, or
	 - Some other thread invokes reset() method on this cyclicBarrier.
	 */
	public synchronized int await() throws InterruptedException {

        count--; //decrements awaiting parties by 1.

		// If the current thread is not the last to arrive, thread will wait.
        // Threads will wait till we have reach the capacity
		if(count>0){
			this.wait();
		}
		// Once the num of parties limit has been reached, we will begin notifying
        // the threads to wake up

		else{
        // All parties have arrive, so reset count to NumParties'
        // thus forming a "cyclic" barrier
            count = NumParties;
			notifyAll(); //notify all waiting threads
		}
		return count;
	}
}

