import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.net.*;
public class CarServer {
	static int record_count = 0;
	static HashMap<String, ArrayList<Integer>> rentingList = new HashMap<String, ArrayList<Integer>>(); 
	static HashMap<Integer, String[]> recordBook = new HashMap<Integer, String[]>();
	private static List<inventory> stock =  Collections.synchronizedList(new ArrayList<inventory>());

	public static void main (String[] args) {
		
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

		
		UDPThread udpMain = new UDPThread(record_count, rentingList, recordBook, stock);
		udpMain.start();
		TCPThread tcpMain = new TCPThread(record_count, rentingList, recordBook, stock);
		tcpMain.start();
	}
	
	
	public static String invState() {
		String outcome = "";
		for(inventory x: stock) {
			
			outcome += x.name + " " + x.color + " " + x.q;
			outcome += System.getProperty("line.separator");
			
			
		}
		return outcome;
	}
	


	static int searchStock(String model, String color){
		for(int i=0; i<stock.size(); i++){
			if(stock.get(i).name.equals(model) &&
					stock.get(i).color.equals(color)){
				if (stock.get(i).q == 0)
					return -2;
				if (stock.get(i).q > 0){
					stock.get(i).q--;
					return i;
				}
			}
		}
		return -1;
	}

	public static synchronized int rentCar(String nameO, String model, String color) {
		String name = nameO.trim();
		int inventory_num = searchStock(model, color);
		if (inventory_num == -1)
			return -1;  								// Car is not available
		if(inventory_num == -2)
			return -2;
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

	public static int stockReplace(String model, String color){
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
	        String name = log[0].trim();                   // Separate components
	        String model = log[1].trim();
	        String color = log[2].trim();
	       
	        int status = stockReplace(model, color);// Add car back to inventory if found
	        if(status == -1)                        // if foreign to inventory, exit
	            return false;
	        ArrayList<Integer> record = rentingList.get(name);
	        if(record != null && record.size() == 1) {                  // Remove from record list
	            rentingList.remove(name);
	            recordBook.keySet().removeIf(n -> n == id);
	        }
	        else if(record != null && record.size() > 1){
	            recordBook.keySet().removeIf(n -> n == id);
	        	record.removeIf(n -> (n == id));
	            rentingList.put(name, record);
	        }
	        return true;                            // Success

	    }

	}


}



