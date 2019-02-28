import java.util.Scanner;


import java.net.*;
import java.io.*;
import java.util.*;
public class CarClient {
  public static void main (String[] args) {
    String hostAddress;
    int clientId;

    int port = 8000;
    int len = 1024;
    byte[] rbuffer = new byte[len];
    DatagramPacket sPacket, rPacket;
    if (args.length > 0)
        hostAddress = args[0];
    else
        hostAddress = "localhost";
    try {
        InetAddress ia = InetAddress.getByName(hostAddress);
        DatagramSocket datasocket = new DatagramSocket();
        
        
        	
        	byte[] buffer = new byte[1];
        	buffer[0] = 0;
        	sPacket = new DatagramPacket(buffer, buffer.length, ia, port);
        	datasocket.send(sPacket);            	
        	rPacket = new DatagramPacket(rbuffer, rbuffer.length);
        	datasocket.receive(rPacket);
        	String retstring = new String(rPacket.getData(), 0,
        			rPacket.getLength());
        	System.out.println("Received from Server:" + retstring);
        
    } catch (UnknownHostException e) {
        System.err.println(e);
    } catch (SocketException e) {
        System.err.println(e);
    } catch (IOException e) {
        System.err.println(e);
    }
    
    
    
//    if (args.length != 2) {
//      System.out.println("ERROR: Provide 2 arguments: commandFile, clientId");
//      System.out.println("\t(1) <command-file>: file with commands to the server");
//      System.out.println("\t(2) client id: an integer between 1..9");
//      System.exit(-1);
//    }
//
//    String commandFile = args[0];
//    clientId = Integer.parseInt(args[1]);
//    hostAddress = "localhost";
//    tcpPort = 7000;// hardcoded -- must match the server's tcp port
//    udpPort = 8000;// hardcoded -- must match the server's udp port
//
//    try {
//        Scanner sc = new Scanner(new FileReader(commandFile));
//
//        while(sc.hasNextLine()) {
//          String cmd = sc.nextLine();
//          String[] tokens = cmd.split(" ");
//
//          if (tokens[0].equals("setmode")) {
//            // TODO: set the mode of communication for sending commands to the server 
//          }
//          else if (tokens[0].equals("rent")) {
//            // TODO: send appropriate command to the server and display the
//            // appropriate responses form the server
//          } else if (tokens[0].equals("return")) {
//            // TODO: send appropriate command to the server and display the
//            // appropriate responses form the server
//          } else if (tokens[0].equals("inventory")) {
//            // TODO: send appropriate command to the server and display the
//            // appropriate responses form the server
//          } else if (tokens[0].equals("list")) {
//            // TODO: send appropriate command to the server and display the
//            // appropriate responses form the server
//          } else if (tokens[0].equals("exit")) {
//            // TODO: send appropriate command to the server 
//          } else {
//            System.out.println("ERROR: No such command");
//          }
//        }
//    } catch (FileNotFoundException e) {
//	e.printStackTrace();
//    }
  }
}

