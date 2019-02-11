
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
	private Node head;
	private int index = 0;
	final ReentrantLock lockList = new ReentrantLock();
	final Condition notFull = lockList.newCondition();
	final Condition notEmpty = lockList.newCondition();

	public PriorityQueue(int maxSize) {
        // Creates a Priority queue with maximum allowed size as capacity
		max = maxSize;
		head = new Node(Integer.MAX_VALUE, null);
		queue = new SimpleEntry[max];
	}

	public int add(String name, int priority)  {
		if(search(name) != -1)
			return -1;
		//returns -1 if name is present
		
		while(index == max) {
			try {
				notFull.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//wait for the queue to empty out if index is already out of bounds
		}
		int pos = 0;
		Node ins = new Node(priority, name);
		ins.next = null;
		Node looper = head;
		
		while(looper.next != null) {
			Node nextLooper = looper.next;
			if(nextLooper.pri < priority) {
				//looper.nodeLock.lock();	 // each node can be individually locked, but this may cause deadlock :(
				//nextLooper.nodeLock.lock();
				lockList.lock();
				try {
					looper.next = ins;
					ins.next = nextLooper;
					index++;
					notEmpty.signal();	
					System.out.println(priority + " name " + name + " inserted to list at " + pos );
				}finally {
					lockList.unlock();
					//looper.nodeLock.unlock();
					//nextLooper.nodeLock.unlock();
				}
				
				return pos;
				
			}
			pos++;
			looper = looper.next;
		}
		if(looper.next == null) {//add to end of list
			//looper.nodeLock.lock();
			lockList.lock();
			try {
				looper.next = ins;
				index++;
				notEmpty.signal();
			}finally {
				lockList.unlock();
				//looper.nodeLock.unlock();
			}
			System.out.println(priority + " name " + name + " inserted to back/empty at " + pos );
		}
			//not empty list. not sure if signal always or signal if queue.length == 1
		return pos;
        // Adds the name with its priority to this queue.
        // Returns the current position in the list where the name was inserted;
        // otherwise, returns -1 if the name is already present in the list.
        // This method blocks when the list is full.
	}

	public int search(String name) {
		int pos = 0;
		Node looper = head.next;
		while(looper != null) {
			if(looper.name.equals(name)) {
				return pos;
			}
			looper = looper.next;
			pos++;
		}
		
		return -1;
        // Returns the position of the name in the list;
        // otherwise, returns -1 if the name is not found.
	}

	public String getFirst()  {
		while(index == 0 || head.next == null) {
			try {
				notEmpty.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
		/*Lock a = head.next.nodeLock;
		a.lock();
		Lock b = head.next.next.nodeLock;
		b.lock();*/
		lockList.lock();
		String retName = null;
		try {
			if(head.next != null) {
				retName = head.next.name;
				head.next= head.next.next;
				index--;
				notFull.signal();
			}
			
			/*b.unlock();
			a.unlock();*/
			
		}finally {
			lockList.unlock();
		}
		return retName;
        // Retrieves and removes the name with the highest priority in the list,
        // or blocks the thread if the list is empty.
	}
	
	class Node{
		private int pri;
		private String name = null;
		private Node next = null;
		private Lock nodeLock = new ReentrantLock();
		public Node (int p, String s) {
			pri = p;
			name = s;
		}
		
		
		
	}
}


