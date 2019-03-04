import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class TCPServerThreads extends Thread{
	static int record_count;
	static HashMap<String, ArrayList<Integer>> rentingList; 
	static HashMap<Integer, String[]> recordBook;
	private static List<inventory> stock;
	Socket client;
	public TCPServerThreads(Socket c, int count, HashMap list, HashMap book, List stock) {
		record_count = count;
		rentingList = list;
		recordBook = book;
		this.stock = stock;

		client = c;
	}
	
	public void run() {

		System.out.println("client specific thread running");
		try {
			Scanner sc = new Scanner(client.getInputStream());
			PrintWriter pout = new PrintWriter(client.getOutputStream());
			String command = sc.nextLine();
			System.out.println("received:" + command);
			Scanner st = new Scanner(command);          
			String tag = st.next();
		}catch (IOException e) {
			System.err.println(e);
		}
		
	}
}
