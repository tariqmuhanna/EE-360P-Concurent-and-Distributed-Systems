import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.net.*;
public class CarServer {
	private static ArrayList<inventory> stock = new ArrayList<inventory>();
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
		DatagramPacket datapacket, returnpacket;
		int len = 1024;
		try {
			DatagramSocket datasocket = new DatagramSocket(udpPort);
			byte[] buf = new byte[len];
			while (true) {
				datapacket = new DatagramPacket(buf, buf.length);
				datasocket.receive(datapacket);
				byte[] recmsg = datapacket.getData();
				System.out.println(Arrays.toString(recmsg));
				
				
				returnpacket = new DatagramPacket(
						datapacket.getData(),
						datapacket.getLength(),
						datapacket.getAddress(),
						datapacket.getPort());
				datasocket.send(returnpacket);
			}
		} catch (SocketException e) {
			System.err.println(e);
	    // TCP:
	    
	  } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
