import java.net.*;
import java.nio.charset.StandardCharsets;
import java.io.*;
import java.util.*;
public class UDPThread extends Thread {
	static int record_count;
	static HashMap<String, ArrayList<Integer>> rentingList; 
	static HashMap<Integer, String[]> recordBook;
	private static List<inventory> stock;
	public UDPThread(int count, HashMap list, HashMap book, List stock) {
		record_count = count;
		rentingList = list;
		recordBook = book;
		this.stock = stock;
	}
	
	public void run() {


		int tcpPort = 7000;
		int udpPort = 8000;


		// TODO: handle request from clients
		// UDP:
//		DatagramPacket datapacket, returnpacket;
		int len = 1024;

		try {
			DatagramSocket datasocket = new DatagramSocket(udpPort);
			byte[] buf = new byte[len];
			DatagramPacket datapacket = new DatagramPacket(buf, buf.length);
			
			while (true) {
				buf = new byte[len];
				datapacket.setData(buf);
				datasocket.receive(datapacket);
//				InetAddress IA = datapacket.getAddress();
//				byte[] recmsg = datapacket.getData();
//				System.out.println(recmsg[0]);
				String data = new String(datapacket.getData());
				String[] tokens = data.split(" ");
				tokens[0] = tokens[0].trim();
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
					UDPMessage(datapacket.getAddress(), datapacket.getPort(), outcome, datasocket);
				}

                else if (tokens[0].equals("return")) {
                    int id = Integer.parseInt(tokens[1].trim());    // Extract id
                    String outcome;

                    if(CarServer.returnRental(id))                            // If rental id exists, update stock
                        outcome = id + " is returned";
                    else                                            // Else error
                        outcome = id + " not found, no such rental record";
                    UDPMessage(datapacket.getAddress(), datapacket.getPort(), outcome, datasocket);
                    
                }else if (tokens[0].equals("list")) {
                	String outcome = "";
            		int addSep = 0;
                	if(rentingList.get(tokens[1].trim()) != null) {
                		ArrayList<Integer> ids = rentingList.get(tokens[1].trim());
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
                	UDPMessage(datapacket.getAddress(), datapacket.getPort(), outcome, datasocket);
                	
                	
                }else if (tokens[0].equals("inventory")) {

                	String outcome = CarServer.invState();
                	UDPMessage(datapacket.getAddress(), datapacket.getPort(), outcome, datasocket);
                	
                }else{
                	FileWriter fwriter = new FileWriter(new File("inventory.txt"));
                    PrintWriter pwriter = new PrintWriter(fwriter, true);
                	pwriter.write(CarServer.invState());
                	pwriter.close();
                	fwriter.close();
                }
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
}

	private static void UDPMessage(InetAddress ia, int port, String outcome, DatagramSocket dataSocket) {
		//System.out.println("Sending response: " + outcome);
		byte[] sendData = outcome.getBytes(StandardCharsets.UTF_8);
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ia, port);
		try {
			dataSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}

