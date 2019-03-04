import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class TCPServerThreads extends Thread{
	
	int tcpPort = 7000;
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
			String data = sc.nextLine();
			System.out.println("Input received: " + data);
			String[] tokens = data.split(" ");
			
			//if (tokens[0].equals("inventory                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       ")) {

            	String outcome = CarServer.invState();

    			System.out.println(CarServer.invState());
            	TCPMessage(outcome, client, tcpPort);
            	
            //} 
		}catch (IOException e) {
			System.err.println(e);
		}
		
	}
    private static void TCPMessage(String input, Socket cli, int port) throws IOException {
        PrintWriter out = new PrintWriter(cli.getOutputStream(), true); // to send out to server
        out.println(input);
      
    }
}
