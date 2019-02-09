/*
 * EID's of group members
 * 
 */
import java.util.concurrent.Semaphore; // for implementation using Semaphores

public class CyclicBarrier {
	Semaphore waitParties;
	Semaphore mutex;
	int numParties = 0;
	int set = 0;
	boolean flag = false;
	public CyclicBarrier(int parties) {
		waitParties = new Semaphore(parties, true);
		mutex = new Semaphore(1, true);
		numParties = parties;
	}
	
	public int await() throws InterruptedException {
		mutex.acquire();
		int place = waitParties.availablePermits() -1;
		waitParties.acquire();
		mutex.release();
		
		
		
		while(waitParties.availablePermits() != 0 || flag == true);
		
		//mutex.acquire();
		flag = false;
		waitParties.release();
		if(waitParties.availablePermits() == 0)
			flag = true;
		//mutex.release();
		
          // you need to write this code
        
	    return place;
	}
}
