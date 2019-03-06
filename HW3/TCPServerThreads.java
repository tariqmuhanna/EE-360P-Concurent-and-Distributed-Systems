import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
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
		
		try {
			Scanner sc = new Scanner(client.getInputStream());
			boolean exit = true;
			while(exit && sc.hasNextLine()) {
				String data = sc.nextLine();
				String[] tokens = data.split(" ");
				/*for(int i = 0; i < tokens.length; i++) {
					tokens[i] = tokens[i].replaceAll("^[^a-zA-Z0-9\\s]+|[^a-zA-Z0-9\\s]+$", "");
				}*/
				if (tokens[0].equals("rent")) {
					String name = tokens[1].trim();				// trims extra spaces
					String model = tokens[2].trim();
					String color = tokens[3].trim();
					int status = CarServer.rentCar(name, model, color);  	// does the actually renting procedure
					String outcome;
	
					if(status == -1)							// renting failed
						outcome = "Request Failed - We do not have this car";
					else if(status == -2)
						outcome = "Request Failed - Car not available";
					else										// renting succedded
						outcome = "Your request has been approved, " + status + " " + name + " " + model + " " + color;
				
					TCPMessage(outcome, client, tcpPort);
				}else if (tokens[0].equals("list")) {
            		String outcome = "";
            		int addSep = 0;
                	if(rentingList.get(tokens[1]) != null) {
                		ArrayList<Integer> ids = rentingList.get(tokens[1]);
                		for(Integer i: ids) {
                			if(addSep != 0)
                				outcome += System.getProperty("line.separator");
                    		String[] log = recordBook.get(i);	//is a log;
                    		//System.out.println(Arrays.toString(log));
                    		outcome += i + " " + log[1] + " " + log[2];
                    		addSep++;
                		}
                		
                	}
                	else {
                		outcome = "No record found for " + tokens[1];
                	}
                	TCPMessage(outcome, client, tcpPort);
                	
                	
				}else if (tokens[0].equals("inventory")) {
	            	String outcome = CarServer.invState();
	            	TCPMessage(outcome, client, tcpPort);
	            	
	            } else if (tokens[0].equals("return")) {
                    int id = Integer.parseInt(tokens[1].trim());    // Extract id
                    String outcome;

                    if(CarServer.returnRental(id))                            // If rental id exists, update stock
                        outcome = id + " is returned";
                    else                                            // Else error
                        outcome = id + " not found, no such rental record";
					TCPMessage(outcome, client, tcpPort);
                    
                } else {
                	FileWriter fwriter = new FileWriter(new File("inventory.txt"));
                    PrintWriter pwriter = new PrintWriter(fwriter, true);
                    client.close();
                	pwriter.write(CarServer.invState());
                	pwriter.close();
                	fwriter.close();
                	exit = false;
                }
			}
		}catch (IOException e) {
			System.err.println(e);
		}
		
	}
	
    private static void TCPMessage(String input, Socket cli, int port) throws IOException {
        PrintWriter out = new PrintWriter(cli.getOutputStream(), true); // to send out to server
        out.print(input);
        out.flush();
      
    }
}
