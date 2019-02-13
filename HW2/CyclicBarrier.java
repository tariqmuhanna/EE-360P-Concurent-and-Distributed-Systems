/*
 * EID's of group members
 * 
 */
import java.util.concurrent.Semaphore; // for implementation using Semaphores
import java.util.concurrent.atomic.AtomicInteger;

public class CyclicBarrier {
	Semaphore holdTillFull;
	Semaphore waitParties;
	boolean flag = false;
	int numParties = 0;
	AtomicInteger count = new AtomicInteger(0);
	
	public CyclicBarrier(int parties) {
		holdTillFull = new Semaphore(parties, true);
		//wait until all permits from waitParties are acquired before trying to obtain a holdTillFull permit,
		waitParties = new Semaphore(parties, true);
		numParties = parties;
		
	}
	
	public int await() throws InterruptedException {
		
	
		waitParties.acquire();	//try to get a permit
		
		//System.out.println("Thread" + Thread.currentThread().getId() + " has gotten waitParties permit ");
		
		while(flag || count.get() >= numParties) {
			//wait here until 5 threads have gotten permits
		}
		int place = count.getAndIncrement();
		holdTillFull.acquire();
		
		//System.out.println("Thread" + Thread.currentThread().getId() + " has obtained holdTillFull at " + place);
		
		
		while(count.get() != numParties && !flag) {}
		//System.out.println(place+ " Thread" + Thread.currentThread().getId() + " has count--");
		
		flag = true;
    	//System.out.println(place + " flag to true");
		//System.out.println(place + " Thread" + Thread.currentThread().getId() + " is releasing permits");
		waitParties.release();
		holdTillFull.release();
		count.getAndDecrement();
        if(holdTillFull.availablePermits() == numParties) {
        	flag = false;
        	//System.out.println(place +" flag to false");
        }
	    return place;
	}
}
