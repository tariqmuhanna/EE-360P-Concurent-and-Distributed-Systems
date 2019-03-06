import java.net.*;
import java.io.*;
import java.util.*;
public class TCPThread extends Thread{
	static int record_count;
	static HashMap<String, ArrayList<Integer>> rentingList; 
	static HashMap<Integer, String[]> recordBook;
	private static List<inventory> stock;

	public TCPThread(int count, HashMap list, HashMap book, List stock) {
		record_count = count;
		rentingList = list;
		recordBook = book;
		this.stock = stock;
	}
	public void run() {
		try {

			int tcpPort = 7000;
			int udpPort = 8000;
			ServerSocket listener = new ServerSocket(tcpPort);
			Socket s;
			while ( (s = listener.accept()) != null) {
				Thread t = new TCPServerThreads(s, record_count, rentingList, recordBook, stock);
				//t is a new client that has setmode = T
				t.start();
			}
		} catch (IOException e) {
			System.err.println("Server aborted:" + e);
		}
	}
	
	
}
