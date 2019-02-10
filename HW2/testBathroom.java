import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class testBathroom implements Runnable {
	final static int SIZE = 35;
	final FairUnifanBathroom bthrm;
	public testBathroom (FairUnifanBathroom f) {
		this.bthrm = f;
		
		
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		//int school = ThreadLocalRandom.current().nextInt(0, 2);
		//int sleepTime = ThreadLocalRandom.current().nextInt(0, 4);
		int sleepTime = 5;
		if(Thread.currentThread().getId()%2 == 0) {
			System.out.println("OU Thread "+ Thread.currentThread().getId() + " running" );
			bthrm.enterBathroomOU();
			System.out.println("OU Thread " + Thread.currentThread().getId() + " enters room");
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			bthrm.leaveBathroomOU();
			System.out.println("OU Thread " + Thread.currentThread().getId() + " leaves room");
			
			
		}
		else {
			System.out.println("UT Thread "+ Thread.currentThread().getId() + " running");
			bthrm.enterBathroomUT();
			System.out.println("UT Thread " + Thread.currentThread().getId() + " enters room");
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			bthrm.leaveBathroomUT();
			System.out.println("UT Thread " + Thread.currentThread().getId() + " left room");
				
		}
		
	}
	public static void main(String[] args) {
		
		FairUnifanBathroom test = new FairUnifanBathroom();
		Thread[] t = new Thread[SIZE];
		for(int i = 0; i < SIZE; i++) {
			Thread myThread = new Thread(new testBathroom(test));
			
			 t[i] = myThread;
			 
		}
		for (int i = 0; i < SIZE; ++i) {

			t[i].start();
		}
	}

}
