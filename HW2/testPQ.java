import java.util.concurrent.ThreadLocalRandom;
import java.util.Random;

public class testPQ implements Runnable{
	final static int SIZE = 20;
	final PriorityQueue queue;
	public testPQ(PriorityQueue q) {
		queue = q;
	}
	@Override
	public void run() {
		// TODO Auto-gnerated method stub
		
		int rand = ThreadLocalRandom.current().nextInt(0, 10);
		try {
			
			queue.add("id=" +Thread.currentThread().getId(), rand);
			
			Thread.sleep(rand/2);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("first in line: " + queue.getFirst());
		
	}
	
	public static void main(String[] args) {
		    Random r = new Random();
			PriorityQueue test = new PriorityQueue(9);
			Thread[] t = new Thread[SIZE];
			for(int i = 0; i < SIZE; i++) {
				Thread myThread = new Thread(new testPQ(test));
				
				 t[i] = myThread;
				 
			}
			for (int i = 0; i < SIZE; i++) {
				
				int rand = r.nextInt(20);
				try {
					if(rand%3 == 0)
						Thread.sleep(rand);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				t[i].start();
			}
	}

}

