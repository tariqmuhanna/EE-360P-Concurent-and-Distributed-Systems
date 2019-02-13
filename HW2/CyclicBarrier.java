/*
 * EID's of group members
 * 
 */
import java.util.concurrent.Semaphore; // for implementation using Semaphores
import java.util.concurrent.atomic.AtomicInteger;

public class CyclicBarrier {
	Semaphore holdTillFull;
	Semaphore waitParties;
	int numParties = 0;
	AtomicInteger count = new AtomicInteger(0);
	boolean flag = false;
	public CyclicBarrier(int parties) {
		holdTillFull = new Semaphore(parties, true);
		//wait until all permits from waitParties are acquired before trying to obtain a holdTillFull permit,
		waitParties = new Semaphore(parties, true);
		numParties = parties;
		
	}
	
	public int await() throws InterruptedException {
		
	
		waitParties.acquire();	//try to get a permit
		int place = count.getAndIncrement();
		System.out.println("Thread" + Thread.currentThread().getId() + " has gotten waitParties permit at " + place);
		
		
		while(count.get() < numParties) {
			//wait here until 5 threads have gotten permits
		}
		System.out.println("Thread" + Thread.currentThread().getId() + " has obtained full party");
		
		holdTillFull.acquire();
		System.out.println("Thread" + Thread.currentThread().getId() + " has obtained holdTillFull ");
		count.getAndDecrement();
		System.out.println("Thread" + Thread.currentThread().getId() + " has count--");
		while(count.get() != 0);
		waitParties.release();
		holdTillFull.release();
        
	    return place;
	}
}
