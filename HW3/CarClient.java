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
            InetAddress IA = InetAddress.getByName(hostAddress);    // Establish Inet address
            DatagramSocket dataSocket = new DatagramSocket();       // UDP protocol
            Socket tcpSocket = new Socket(hostAddress, tcpPort);    // TCP protocol
            
            
            // Continues reading while scanner is open
            while (sc.hasNextLine()) {

                String cmd = sc.nextLine();
                String[] tokens = cmd.split(" ");

                if (tokens[0].equals("setmode")) {
                    // set the mode of communication for sending commands to the server
                    if(tokens[1].equals("U"))      protocol = "U";
                    else if(tokens[1].equals("T")) protocol = "T";


                } else if (tokens[0].equals("rent")) {
                    // Inputs the name of customer, brand of car,
                    // and color required for car rental
                    if(protocol.equals("U"))
                        UDPMessage(cmd,dataSocket,IA,udpPort,pwriter,len);
                    else if(protocol.equals("T")){}
                        TCPMessage(cmd,tcpSocket,tcpPort,pwriter,len);


                } else if (tokens[0].equals("return")) {
                    // Returns the car associated with the <record-id>
                    if(protocol.equals("U")) {
                        System.out.println(cmd);
                        UDPMessage(cmd,dataSocket,IA,udpPort,pwriter,len);
                    }
                    else if(protocol.equals("T")){}
                        TCPMessage(cmd,tcpSocket,tcpPort,pwriter,len);


                } else if (tokens[0].equals("inventory")) {
                    // Lists all available cars in the rental service
                    if(protocol.equals("U"))
                        UDPMessage(cmd,dataSocket,IA,udpPort,pwriter,len);
                    else if(protocol.equals("T")){}
                        TCPMessage(cmd,tcpSocket,tcpPort,pwriter,len);


                } else if (tokens[0].equals("list")) {
                    // Lists all cars borrowed by the customer along with their color
                    if(protocol.equals("U"))
                        UDPMessage(cmd,dataSocket,IA,udpPort,pwriter,len);
                    else if(protocol.equals("T")){}
                        TCPMessage(cmd,tcpSocket,tcpPort,pwriter,len);


                } else if (tokens[0].equals("exit")) {
                    // Informs server to stop processing commands from
                    // this client and print the current state of the
                    // inventory to inventory file named as “inventory.txt.”
//                    PrintWriter exit_msg = new PrintWriter(tcpSocket.getOutputStream(), true);
//                    exit_msg.println(cmd);
//                    exit_msg.close();

                    byte[] buffer = new byte[cmd.length()];
                    buffer = cmd.getBytes();
                    DatagramPacket sPacket = new DatagramPacket(buffer, cmd.length(), IA, udpPort);
                    dataSocket.send(sPacket);
                    dataSocket.close();
                    sc.close();

                    fwriter.close();
                    pwriter.close();
                    System.exit(0);
                    break;

                } else {
                    System.out.println("ERROR: No such command");
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
    public static void UDPMessage(String input, DatagramSocket datasocket, InetAddress ia, int port, PrintWriter pwriter, int buff_length) throws IOException {
        byte[] buffer = new byte[input.length()];
        byte[] rbuffer = new byte[buff_length];
        buffer = input.getBytes();
        DatagramPacket sPacket = new DatagramPacket(buffer,         // Create sending packet
                buffer.length, ia, port);
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

