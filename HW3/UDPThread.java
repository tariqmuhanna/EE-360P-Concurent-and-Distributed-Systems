import java.net.*;
import java.io.*;
import java.util.*;
public class UDPThread extends Thread {
	static int record_count;
	static HashMap<String, ArrayList<Integer>> rentingList; 
	static HashMap<Integer, String[]> recordBook;
	private static List<inventory> stock;
	String[] args;
	public UDPThread(int count, HashMap list, HashMap book, List stock, String[] args) {
		record_count = count;
		rentingList = list;
		recordBook = book;
		this.stock = stock;
		this.args = args;
	}
	
	public void run() {
		
		
		int tcpPort;
		int udpPort;
		if (args.length != 1) {
			System.out.println("ERROR: Provide 1 argument: input file containing initial inventory");
			System.exit(-1);
		}
		String fileName = args[0];
		Scanner sc;
		try {
			sc = new Scanner(new FileReader(fileName));
			while(sc.hasNextLine()) {
				String nxtLine = sc.nextLine();
				String[] descrip = nxtLine.split(" ");
				if(descrip.length != 3) throw new Exception();
				inventory newInv = new inventory(descrip[0], descrip[1], Integer.parseInt(descrip[2]));
				stock.add(newInv);

			}

			sc.close();


		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Bad input");
			e.printStackTrace();
		}


		tcpPort = 7000;
		udpPort = 8000;
		for(inventory i : stock) {
			System.out.println(i.name);
		}
		// parse the inventory file

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
				System.out.println("Input received: " + data);
				String[] tokens = data.split(" ");
				System.out.println(Arrays.toString(tokens));

				if (tokens[0].equals("rent")) {
					System.out.println("Renting...");
					String name = tokens[1].trim();				// trims extra spaces
					String model = tokens[2].trim();
					String color = tokens[3].trim();
					System.out.println("Name: " + name +
							'\n' + "Model: " + model +
							'\n' + "Color: " + color);
					int status = rentCar(name, model, color);  	// does the actually renting procedure
					String outcome;

					if(status == -1)							// renting failed
						outcome = "transaction failed";
					else										// renting succedded
						outcome = "Success, " + status + " " + model + " " + color;
					System.out.println(invState());
					UDPMessage(datapacket.getAddress(), datapacket.getPort(), outcome, datasocket);
				}

                else if (tokens[0].equals("return")) {
                    System.out.println("Return processing...");
                    int id = Integer.parseInt(tokens[1].trim());    // Extract id
                    String outcome;

                    if(returnRental(id))                            // If rental id exists, update stock
                        outcome = id + " is returned";
                    else                                            // Else error
                        outcome = id + " not found, no such rental record";
					System.out.println(invState());
                    UDPMessage(datapacket.getAddress(), datapacket.getPort(), outcome, datasocket);
                    
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
                	UDPMessage(datapacket.getAddress(), datapacket.getPort(), outcome, datasocket);
                	
                	
                }else if (tokens[0].equals("inventory                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       ")) {

                	String outcome = invState();
                	UDPMessage(datapacket.getAddress(), datapacket.getPort(), outcome, datasocket);
                	
//                } else 
                }else{
                	System.out.println(invState());
                	FileWriter fwriter = new FileWriter(new File("inventory.txt"));
                    PrintWriter pwriter = new PrintWriter(fwriter, true);
                	pwriter.write(invState());
                	pwriter.close();
                	fwriter.close();
                	
                }
			}
		} catch (SocketException e) {
			System.err.println(e);
			// TCP:




		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
}

public static String invState() {
	String outcome = "";
	for(inventory x: stock) {
		
		outcome += x.name + " " + x.color + " " + x.q;
		outcome += System.lineSeparator();
		
		
	}
	return outcome;
}
private static void UDPMessage(InetAddress ia, int port, String outcome, DatagramSocket dataSocket) {
	System.out.println("Sending response: " + outcome);
	byte[] sendData = outcome.getBytes();
	DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ia, port);
	try {
		dataSocket.send(sendPacket);
	} catch (IOException e) {
		e.printStackTrace();
	}
}


static int searchStock(String model, String color){
	for(int i=0; i<stock.size(); i++){
		if(stock.get(i).name.equals(model) &&
				stock.get(i).color.equals(color)){
			if (stock.get(i).q > 0){
				stock.get(i).q--;
				return i;
			}
		}
	}
	return -1;
}

public static synchronized int rentCar(String name, String model, String color) {
	int inventory_num = searchStock(model, color);
	if (inventory_num == -1)
		return -1;  								// Car is not available

	else{
		record_count++;
		String[] log = {name, model, color};
		recordBook.put(record_count, log);         // Add checkout to record book
		// Add to student reading list
		ArrayList<Integer> list;
		if (!rentingList.containsKey(name))         // Create new entry if one doesn't exist
			list = new ArrayList<Integer>();
		else                                        // Retrieve existing entry
			list = rentingList.get(name);

		list.add(record_count);               		// Update
		rentingList.put(name, list);
		return record_count;
	}
}

static int stockReplace(String model, String color){
    for(int i=0; i<stock.size(); i++){
        if(stock.get(i).name.equals(model) &&
                stock.get(i).color.equals(color)){
                stock.get(i).q++;
                return i;
        }
    }
    return -1;
}


public static synchronized Boolean returnRental(int id) {
    if(!recordBook.containsKey(id))             // If id doesn't exist, exit
        return false;
    else {
        String[] log = recordBook.get(id);      // Grabs info regarding record id
        String name = log[0];                   // Separate components
        String model = log[1];
        String color = log[2];
        int status = stockReplace(model, color);// Add car back to inventory if found
        if(status == -1)                        // if foreign to inventory, exit
            return false;

        ArrayList<Integer> record = rentingList.get(name);
        if(record.size() == 1)                  // Remove from record list
            rentingList.remove(name);
        else{
            record.remove(id);
            rentingList.put(name, record);
        }
        return true;                            // Success

    }

}


	
	
}
