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
		UDPThread udpMain = new UDPThread(record_count, rentingList, recordBook, stock, args);
		udpMain.start();
	}
}



