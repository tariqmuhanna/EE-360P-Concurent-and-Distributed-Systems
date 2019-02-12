
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
	private Node head;	//head of linked list
	private int index = 0; //index is more like count; 0 is empty, max is full
	final ReentrantLock lockList = new ReentrantLock();
	final Condition notFull = lockList.newCondition();
	final Condition notEmpty = lockList.newCondition();
	
	public PriorityQueue(int maxSize) {
        // Creates a Priority queue with maximum allowed size as capacity
		max = maxSize;
		head = new Node(Integer.MAX_VALUE, null); 
		
	}

	public int add(String name, int priority)  {
		if(search(name) != -1)
			return -1;

		lockList.lock();
		while(index == max) { 
			try {
				notFull.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//wait for the queue to empty out if index is already out of bounds
		}
		lockList.unlock();
		
		int pos = 0; 
		Node ins = new Node(priority, name); //list is not full, ins is new node to be inserted
		ins.next = null;
		Node looper = head; //start at the head and put ins at the first place it's priority is higher than the next node
		
		while(looper.next != null) { 
			looper.nodeLock.lock();
			Node nextLooper = looper.next;
			nextLooper.nodeLock.lock();
			try {
				if(nextLooper.pri < priority) {
					ins.next = nextLooper;
					looper.next = ins;
					index++;
					lockList.lock();
					try{
						if(index == 1)
							notEmpty.signal();	// should i only signal if index = 1?
						System.out.println(priority + " name " + name + " inserted to list at " + pos );
					}finally {
						lockList.unlock();
					}
				
					return pos; //not returning correct pos rn
					
				}
				pos++;
			}finally {
				looper.nodeLock.unlock();
				nextLooper.nodeLock.unlock();
			}
			looper = looper.next;
		}
		if(looper.next == null) {
			looper.nodeLock.lock();
			try {
				looper.next = ins;
				index++;
				if(index == 1) {
					lockList.lock();
					try {
						notEmpty.signal();
					}finally {
						lockList.unlock();
					}
				
				}
			}finally {
				looper.nodeLock.unlock();
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
			looper.nodeLock.lock();
			try {
				if(looper.name.equals(name)) {
					return pos;
			}
			}finally {
				looper.nodeLock.unlock();
			}
			looper = looper.next;
			pos++;
		}
		return -1;
        // Returns the position of the name in the list;
        // otherwise, returns -1 if the name is not found.
	}

	public String getFirst()  {
		lockList.lock();
		while(index == 0) {
			try {
				notEmpty.await();	//wait for list not to be empty
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String retName = null;
		Node first;
		try {
			first = head.next;
			if(first == null) {
				System.out.println("list empty");
				
			}
			else {	
				first.nodeLock.lock();
				head.nodeLock.lock();
			}
		}finally {
		lockList.unlock();
		}
		
		try {
			if(first != null) {
				retName = first.name;	//pop the first person
				head.next= first.next;	//set head.next to be second guy
				index--;	//decrement size
				
				lockList.lock();
				try {
					if(index == max-1)
						notFull.signal();
				}finally {
					lockList.unlock();
				}
			}
		}finally {
			head.nodeLock.unlock();
			first.nodeLock.unlock();
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


