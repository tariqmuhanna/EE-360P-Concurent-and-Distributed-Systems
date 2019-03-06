import java.io.IOException;

public class ClientTester {
	public static void main(String[] args) {
		int len = 4;
		Thread[] toruns = new testMultiThread[len];
		for(int i = 0; i < len/2; i++) {
			toruns[i] = new testMultiThread(i);
		}
		for(int i = len/2; i < len; i++)
			toruns[i] = new testMultiThread(i);
		for(int i = 0; i < toruns.length; i++) {
			toruns[i].start();
		}
//		file = "cmdFileT";
//		String[] inputT = new String[2];
//		inputT[0] = file;
//		for(int i = 3; i < 6; i++) {
//			String id = String.valueOf(i);
//			inputT[1] = id;
//			testMultiThread n = new testMultiThread(inputT);
//			toruns[i] = n;
//		}
//		
	}
}

class testMultiThread extends Thread{
	int i;
	CarClient x;
	public testMultiThread(int i) {
		this.i = i;
	}
	public void run() {
		try {
			String[] args = new String[2];
			if(i % 2 == 0) {
				args[0]="src/cmdFile";
			}
			else
				args[0]="src/cmdFileT";
			args[1] = String.valueOf(i);
				
			x = new CarClient();
			x.main(args);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
