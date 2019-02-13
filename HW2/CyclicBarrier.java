/*
 * EID's of group members
 *
 */
import java.util.concurrent.Semaphore; // for implementation using Semaphores

// Conceptually, a semaphore maintains a set of permits.
// Each acquire() blocks if necessary until a permit is available,
// and then takes it.

// Each release() adds a permit, potentially releasing a blocking acquirer

public class CyclicBarrier {
    int parties;
    int count;
    Semaphore mutex;
    Semaphore barrier1, barrier2;

    public CyclicBarrier(int parties) {
        // Creates a new CyclicBarrier that will release threads only when
        // the given number of threads are waiting upon it
        this.parties = parties;              // number of threads
        this.count = 0;	                 // start with zero threads in the beginning
        mutex = new Semaphore(1);	 // mutual exclusion semaphore
        barrier1 = new Semaphore(0);
        barrier2 = new Semaphore(1);
    }

    public int await() throws InterruptedException {
        // Waits until all parties have invoked await on this CyclicBarrier.
        // If the current thread is not the last to arrive then it is
        // disabled for thread scheduling purposes and lies dormant until
        // the last thread arrives.
        // Returns: the arrival index of the current thread, where index
        // (parties - 1) indicates the first to arrive and zero indicates
        // the last to arrive.
        int index = 0;

        mutex.acquire();        // holds lock
        index = count;          // index = count
        count++;

        if(count == parties) {  // if # of parties = count, begin releasing
            barrier2.acquire();
            barrier1.release();
        }

        mutex.release();        // release lock

        barrier1.acquire();     // will block until another thread calls release()
        barrier1.release();     // n threads will wait while threads release on another



        mutex.acquire();
        count--; //All threads decrement, resetting by one

        //Nth thread closes barrier1 and opens barrier 2(resets Cyclic Barrier)
        if(count == 0){
            barrier1.acquire(); //Close barrier 1
            barrier2.release(); //Let one thread go through barrier
        }
        mutex.release();

        barrier2.acquire(); //All the N thread will wait here after resetting
        barrier2.release(); //Each thread passes through will signal one other thread

        return index;
    }
}
