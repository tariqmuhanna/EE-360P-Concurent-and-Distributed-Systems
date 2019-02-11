import java.util.concurrent.ThreadLocalRandom;

public class testPQ implements Runnable{
	final static int SIZE = 10;
	final PriorityQueue queue;
	public testPQ(PriorityQueue q) {
		queue = q;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		int rand = ThreadLocalRandom.current().nextInt(0, 10);
		queue.add("id=" +Thread.currentThread().getId(), rand);
		try {
			Thread.sleep(rand);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("first in line: " + queue.getFirst());
		
	}
	
	public static void main(String[] args) {
			
			PriorityQueue test = new PriorityQueue(10);
			Thread[] t = new Thread[SIZE];
			for(int i = 0; i < SIZE; i++) {
				Thread myThread = new Thread(new testPQ(test));
				
				 t[i] = myThread;
				 
			}
			for (int i = 0; i < SIZE; i++) {
	
				t[i].start();
			}
	}

}

