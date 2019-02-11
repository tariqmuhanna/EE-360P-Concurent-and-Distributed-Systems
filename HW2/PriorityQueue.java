
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
		lockList.lock(); //I think I'm doing a global lock here, not hand over hand?
		if(search(name) != -1)
			return -1;


		while(index == max) { 
			try {
				notFull.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//wait for the queue to empty out if index is already out of bounds
		}
		
		int pos = 0; //this is how i'm trying to keep track of position, but i think it doesn't work becuase
		//too many threads are changing it's value at one time. should i lock this, or is there a better way?
		Node ins = new Node(priority, name); //list is not full, ins is new node to be inserted
		ins.next = null;
		Node looper = head; //start at the head and put ins at the first place it's priority is higher than the next node
		
		while(looper.next != null) { 
			//while looper and looper.next are not null, we check if we can do: looper -> ins -> looper.next
			Node nextLooper = looper.next;
			if(nextLooper.pri < priority) {
				
				//looper.nodeLock.lock();	//locking the node individual causes many exceptions
				//nextLooper.nodeLock.lock();
				
				
				try {
					looper.next = ins;
					ins.next = nextLooper;
					index++;
					if(index == 1)
						notEmpty.signal();	// should i only signal if index = 1?
					System.out.println(priority + " name " + name + " inserted to list at " + pos );
				}finally {
					lockList.unlock();
					//looper.nodeLock.unlock();
					//nextLooper.nodeLock.unlock();
				}
				
				return pos; //not returning correct pos rn
				
			}
			pos++;
			looper = looper.next;
		}
		if(looper.next == null) {//add to end of list (either the list is empty, or ins has lowest priority
			//looper.nodeLock.lock();
			
			try {
				looper.next = ins;
				index++;
				if(index == 1)
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
		lockList.lock();
		while(index == 0) {
			try {
				notEmpty.await();	//wait for list not to be empty
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
		/*Lock a = head.next.nodeLock;
		a.lock();
		Lock b = head.next.next.nodeLock;
		b.lock();*/
		
		String retName = null;
		try {
			if(head.next != null) {
				retName = head.next.name;	//pop the first person
				head.next= head.next.next;	//set head.next to be second guy
				index--;	//decrement size
				if(index == max-1)
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
		//this is also questionable, can I lock the node itself? 
		public Node (int p, String s) {
			pri = p;
			name = s;
		}
		
		
		
	}
}


