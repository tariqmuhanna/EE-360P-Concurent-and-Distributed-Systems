/*
 * EID's of group members
 * 
 */

public class MonitorCyclicBarrier {
	int initialParties; //total parties
	int partiesAwait; //parties yet to arrive

	public MonitorCyclicBarrier(int parties) {
		initialParties=parties;
		partiesAwait=parties;
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

		partiesAwait--; //decrements awaiting parties by 1.

		//If the current thread is not the last to arrive, thread will wait.
		if(partiesAwait>0){
			this.wait();
		}
           /*If the current thread is last to arrive, notify all waiting threads, and
            launch event*/
		else{
                  /* All parties have arrive, make partiesAwait equal to initialParties,
                    so that CyclicBarrier could become cyclic. */
			partiesAwait = initialParties;
			notifyAll(); //notify all waiting threads
		}
		return partiesAwait;
	}
}

