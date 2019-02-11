
import java.util.concurrent.locks.*;
import java.util.List.*;
import java.util.Map;
import java.util.Map.*;
import java.util.AbstractMap.*;
import java.util.HashMap;
// EID 1
// EID 2

public class PriorityQueue {
	private int max = 0;
	private Entry<Integer, String>[] queue;
	private int index = 0;
	final ReentrantLock lockList = new ReentrantLock();
	final Condition notFull = lockList.newCondition();
	final Condition notEmpty = lockList.newCondition();

	public PriorityQueue(int maxSize) {
        // Creates a Priority queue with maximum allowed size as capacity
		max = maxSize;
		queue = new SimpleEntry[max];
	}

	public int add(String name, int priority) throws InterruptedException {
		if(search(name) == -1)
			return -1;
		//returns -1 if name is present
		
		while(index == max) {
			notFull.await();
			//wait for the queue to empty out if index is already out of bounds
		}

		Entry<Integer,String> newEntry = new SimpleEntry<>(priority, name);
		for(int i = 0; i < max; i ++) {	//loop through list
			if(queue[i].getKey() < priority) {	//if the current priority is bigger than next in list
				for(int k = index; k > i; k--) {	//move everything after insertion point back
					queue[k] = queue[k-1];
				}
				queue[i] = newEntry;
				break;
			}
		}
		index++;
		notEmpty.signalAll();	//not empty list. not sure if signal always or signal if queue.length == 1
		return priority;
        // Adds the name with its priority to this queue.
        // Returns the current position in the list where the name was inserted;
        // otherwise, returns -1 if the name is already present in the list.
        // This method blocks when the list is full.
	}

	public int search(String name) {
		int pos = -1;
		for(Entry<Integer, String> e : queue) {
			if(e.getValue().equals(name))
				pos = e.getKey();
			
		}
		return pos;
        // Returns the position of the name in the list;
        // otherwise, returns -1 if the name is not found.
	}

	public String getFirst() throws InterruptedException {
		while(index == 0) {
			notEmpty.await();
		}
		
		String retName = queue[0].getValue();
		for(int i = 0; i < index-1; i++) {
			queue[i] = queue[i+1];
		}
		index--;
		notFull.signalAll();
		
		return retName;
        // Retrieves and removes the name with the highest priority in the list,
        // or blocks the thread if the list is empty.
	}
}

