import java.util.Scanner;


import java.net.*;
import java.io.*;
import java.util.*;
public class CarClient {

    public static void main(String[] args) throws IOException {
        String hostAddress;
        int clientId;
        int tcpPort;
        int udpPort;
        int len = 1024;

        if (args.length != 2) {
            System.out.println("ERROR: Provide 2 arguments: commandFile, clientId");
            System.out.println("\t(1) <command-file>: file with commands to the server");
            System.out.println("\t(2) client id: an integer between 1..9");
            System.exit(-1);
        }

        // Set up variables
        String commandFile = args[0];           // parse commands
        System.out.println(commandFile);
        clientId = Integer.parseInt(args[1]);   // parse client id
        hostAddress = "localhost";              // localHost
        tcpPort = 7000;                         // hardcoded -- must match the server's tcp port
        udpPort = 8000;                         // hardcoded -- must match the server's udp port
        String protocol = "U";                  // default protocol
//        byte[] rbuffer = new byte[len];
//        DatagramPacket sPacket, rPacket;

        // Setup output file actions
        String fileName   = "out_" + clientId + ".txt";
        String fileOutput = "";
        File outputFile = new File(fileName);
        FileWriter fwriter = new FileWriter(outputFile);
        PrintWriter pwriter = new PrintWriter(fwriter, true);


        // Initialize communication ports and reading set up
        try {
            Scanner sc = new Scanner(new FileReader(commandFile));  // Scanner of reading input

            // Continues reading while scanner is open
            if (sc.hasNextLine()) {

                String cmd = sc.nextLine();
                String[] tokens = cmd.split(" ");

                if (tokens[0].equals("setmode")) {
                    // set the mode of communication for sending commands to the server
                   if(tokens[1].equals("T")) { 
                    	protocol = "T";
                    }
                }
                
                if(protocol.equals("U")) {
                    InetAddress IA = InetAddress.getByName(hostAddress);    // Establish Inet address
                    DatagramSocket dataSocket = new DatagramSocket();       // UDP protocol
                    while(sc.hasNextLine()) {
                    	cmd = sc.nextLine();
                        tokens = cmd.split(" ");
                        if (tokens[0].equals("rent")) {
                            // Inputs the name of customer, brand of car,
                            // and color required for car rental
                        	UDPMessage(cmd,dataSocket,IA,udpPort,pwriter,len);


                        } else if (tokens[0].equals("return")) {
                            // Returns the car associated with the <record-id>
                            UDPMessage(cmd,dataSocket,IA,udpPort,pwriter,len);
                           

                        } else if (tokens[0].equals("inventory")) {
                            // Lists all available cars in the rental service
                            UDPMessage(cmd,dataSocket,IA,udpPort,pwriter,len);
                            

                        } else if (tokens[0].equals("list")) {
                            // Lists all cars borrowed by the customer along with their color
                           
                                UDPMessage(cmd,dataSocket,IA,udpPort,pwriter,len);
                    

                        } else if (tokens[0].equals("exit")) {
                            // Informs server to stop processing commands from
                            // this client and print the current state of the
                            // inventory to inventory file named as “inventory.txt.”
//                            PrintWriter exit_msg = new PrintWriter(tcpSocket.getOutputStream(), true);
//                            exit_msg.println(cmd);
//                            exit_msg.close();

                            byte[] buffer = new byte[cmd.length()];
                            buffer = cmd.getBytes();
                            DatagramPacket sPacket = new DatagramPacket(buffer, cmd.length(), IA, udpPort);
                            dataSocket.send(sPacket);
                            dataSocket.close();
                            sc.close();

                            break;

                        } else {
                            System.out.println("ERROR: No such command");
                        }
                    }
                }
            
                        
                    	
                    
                    
                    
                    
                
                else {
                	Socket tcpSocket = new Socket(hostAddress, tcpPort);    // TCP protocol
                	while(sc.hasNextLine()) {
                    	cmd = sc.nextLine();
                        tokens = cmd.split(" ");
	                	if (tokens[0].equals("rent")) {
	                        // Inputs the name of customer, brand of car,
	                        // and color required for car rental
	                       
	                            TCPMessage(cmd,tcpSocket,tcpPort,pwriter,len);
	
	
	                    } else if (tokens[0].equals("return")) {
	                        // Returns the car associated with the <record-id>
	                            TCPMessage(cmd,tcpSocket,tcpPort,pwriter,len);
	
	
	                    } else if (tokens[0].equals("inventory")) {
	                        // Lists all available cars in the rental service
	                            TCPMessage(cmd,tcpSocket,tcpPort,pwriter,len);
	
	
	                    } else if (tokens[0].equals("list")) {
	                        // Lists all cars borrowed by the customer along with their color
	                       
	                            TCPMessage(cmd,tcpSocket,tcpPort,pwriter,len);
	
	
	                    } else if (tokens[0].equals("exit")) {
	                        // Informs server to stop processing commands from
	                        // this client and print the current state of the
	                        // inventory to inventory file named as “inventory.txt.”
	//                        PrintWriter exit_msg = new PrintWriter(tcpSocket.getOutputStream(), true);
	//                        exit_msg.println(cmd);
	//                        exit_msg.close();
	
	                        fwriter.close();
	                        pwriter.close();
	                        System.exit(0);
	                        break;
	
	                    } else {
	                        System.out.println("ERROR: No such command");
	                    }
                	}
                	
                	
                }
                
                
            }         
                
                
                
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Compose and receive TCP protocol type messages
    private static void TCPMessage(String input, Socket tcpSocket, int port, PrintWriter pwriter, int len) throws IOException {
        PrintWriter out = new PrintWriter(tcpSocket.getOutputStream(), true); // to send out to server
        out.println(input);

        BufferedReader in = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream())); // to read from server
        String server_in;
        if ((server_in = in.readLine().trim()) != null) {   // printing ack
            System.out.println(server_in);
            pwriter.println(server_in);
        }
    }


    // Compose and receive UDP protocol type messages
    public static void UDPMessage(String input, DatagramSocket datasocket, InetAddress inet, int port, PrintWriter pwriter, int buff_length) throws IOException {
        byte[] buffer = new byte[input.length()];
        byte[] rbuffer = new byte[buff_length];
        buffer = input.getBytes();
        DatagramPacket sPacket = new DatagramPacket(buffer,         // Create sending packet
                buffer.length, inet, port);
        datasocket = new DatagramSocket();
        datasocket.send(sPacket);                                   // Send packet
        DatagramPacket rPacket = new DatagramPacket(rbuffer,        // Create receiving packet
                rbuffer.length);
        datasocket.receive(rPacket);
        String ret_string = new String(rPacket.getData(), 0,  // unpack packet
                rPacket.getLength());
        System.out.println("Received from Server:" + ret_string);
        pwriter.println(ret_string);

    }
}

//-----------FIRST CLIENT ATTEMPT OF READING AND WRITING------------------------------------------
//import java.util.Scanner;
//
//
//        import java.net.*;
//        import java.io.*;
//        import java.util.*;
//public class CarClient {
//    public static void main (String[] args) {
//        String hostAddress;
//        int clientId;
//
//        int port = 8000;
//        int len = 1024;
//        byte[] rbuffer = new byte[len];
//        DatagramPacket sPacket, rPacket;
//        if (args.length > 0)
//            hostAddress = args[0];
//        else
//            hostAddress = "localhost";
//        try {
//            InetAddress ia = InetAddress.getByName(hostAddress);
//            DatagramSocket datasocket = new DatagramSocket();
//
//
//
//            byte[] buffer = new byte[1];
//            buffer[0] = 0;
//            sPacket = new DatagramPacket(buffer, buffer.length, ia, port);
//            datasocket.send(sPacket);
//            rPacket = new DatagramPacket(rbuffer, rbuffer.length);
//            datasocket.receive(rPacket);
//            String retstring = new String(rPacket.getData(), 0,
//                    rPacket.getLength());
//            System.out.println("Received from Server:" + retstring);
//
//        } catch (UnknownHostException e) {
//            System.err.println(e);
//        } catch (SocketException e) {
//            System.err.println(e);
//        } catch (IOException e) {
//            System.err.println(e);
//        }
//
//
//
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
//    }
//}
