import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.net.*;
public class CarServer {
	static int record_count = 0;
	static Hashtable<String, ArrayList<Integer>> rentingList = new Hashtable<String, ArrayList<Integer>>(); 
	static Hashtable<Integer, String[]> recordBook = new Hashtable<Integer, String[]>();
	private static List<inventory> stock =  Collections.synchronizedList(new ArrayList<inventory>());

	public static void main (String[] args) {
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
			while (true) {
				DatagramPacket datapacket = new DatagramPacket(buf, buf.length);
				datasocket.receive(datapacket);
//				InetAddress IA = datapacket.getAddress();
//				byte[] recmsg = datapacket.getData();
//				System.out.println(recmsg[0]);
				String data = new String(datapacket.getData());
				System.out.println("Input received: " + data);
				String[] tokens = data.split(" ");

				InetAddress ip = datapacket.getAddress();
				int port = datapacket.getPort();

				if (tokens[0].equals("rent")) {
					System.out.println("Renting...");
					String name = tokens[1].trim();				// trims extra spaces
					String model = tokens[2].trim();
					String color = tokens[3].trim();
					System.out.println("Name: " + name +
							'\n' + "Model: " + model +
							'\n' + "Color: " + color);
					int status = rentCar(name, model, color);  	// does the actually renting procedure
					String outcome = "";
					if(status == -1)							// renting failed
						outcome = "transaction failed";
					else										// renting succedded
						outcome = "Success, " + status + " " + model + " " + color;
					UDPMessage(ip, port, outcome, datasocket);
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
			String[] info = {name, model, color};
			recordBook.put(record_count, info);         // Add checkout to record book
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

}
class inventory{
	String name;
	String color;
	int q;
	public inventory(String name, String color, int quant) {
		this.name = name;
		this.color = color;
		q = quant;
	}
}


