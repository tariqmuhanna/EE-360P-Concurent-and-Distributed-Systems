import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

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
