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
			while(sc.hasNextLine()) {
				String data = sc.nextLine();
				String[] tokens = data.split("\\s+");
				System.out.println("Input received: " + tokens[0]);
				System.out.println("Input received: " + tokens[0]);
				/*for(int i = 0; i < tokens.length; i++) {
					tokens[i] = tokens[i].replaceAll("^[^a-zA-Z0-9\\s]+|[^a-zA-Z0-9\\s]+$", "");
				}*/
				if (tokens[0].equals("rent")) {
					System.out.println("Renting...");
					String name = tokens[1].trim();				// trims extra spaces
					String model = tokens[2].trim();
					String color = tokens[3].trim();
					System.out.println("Name: " + name +
							'\n' + "Model: " + model +
							'\n' + "Color: " + color);
					int status = CarServer.rentCar(name, model, color);  	// does the actually renting procedure
					String outcome;
	
					if(status == -1)							// renting failed
						outcome = "transaction failed";
					else										// renting succedded
						outcome = "Success, " + status + " " + model + " " + color;
					System.out.println(CarServer.invState());
					TCPMessage(outcome, client, tcpPort);
				}else if (tokens[0].equals("list")) {
            		String outcome = "";
                	if(rentingList.get(tokens[1]) != null) {
                		ArrayList<Integer> ids = rentingList.get(tokens[1]);
                		for(Integer i: ids) {
                    		String[] log = recordBook.get(i);	//is a log;
                    		System.out.println(Arrays.toString(log));
                    		outcome += log[0] + " " + log[1] + " " + log[2];
                    		outcome += System.lineSeparator();
                		}
                		
                	}
                	else {
                		outcome = "No record found for " + tokens[1];
                	}
                	TCPMessage(outcome, client, tcpPort);
                	
                	
				}else if (tokens[0].equals("inventory")) {
	            	String outcome = CarServer.invState();
	    			System.out.println(CarServer.invState());
	            	TCPMessage(outcome, client, tcpPort);
	            	
	            } else if (tokens[0].equals("return")) {
                    System.out.println("Return processing...");
                    int id = Integer.parseInt(tokens[1].trim());    // Extract id
                    String outcome;

                    if(CarServer.returnRental(id))                            // If rental id exists, update stock
                        outcome = id + " is returned";
                    else                                            // Else error
                        outcome = id + " not found, no such rental record";
					System.out.println(CarServer.invState());
					TCPMessage(outcome, client, tcpPort);
                    
                }
			}
		}catch (IOException e) {
			System.err.println(e);
		}
		
	}
	
    private static void TCPMessage(String input, Socket cli, int port) throws IOException {
        PrintWriter out = new PrintWriter(cli.getOutputStream(), true); // to send out to server
        out.println(input);
        out.flush();
      
    }
}
