package kvpaxos;
import paxos.*;
import paxos.Request;
import paxos.Response;

import static org.junit.Assert.assertFalse;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class Client {
    String[] servers;	//peers are hostnames
    int[] ports;
    
    // Your data here
    int cliSeq = this.hashCode();
    public Client() {
    	servers = null;
    	ports = null;
    	
    	
    }
    
    public Client(String[] servers, int[] ports){
        this.servers = servers;
        this.ports = ports;
        // Your initialization code here
    }

    /**
     * Call() sends an RMI to the RMI handler on server with
     * arguments rmi name, request message, and server id. It
     * waits for the reply and return a response message if
     * the server responded, and return null if Call() was not
     * be able to contact the server.
     *
     * You should assume that Call() will time out and return
     * null after a while if it doesn't get a reply from the server.
     *
     * Please use Call() to send all RMIs and please don't change
     * this function.
     */
    public kvpaxos.Response Call(String rmi, kvpaxos.Request req, int id){
       kvpaxos.Response callReply = null;
        KVPaxosRMI stub;
        try{
            Registry registry= LocateRegistry.getRegistry(this.ports[id]);
            stub=(KVPaxosRMI) registry.lookup("KVPaxos");
            if(rmi.equals("Get"))
                callReply = stub.Get(req);
            else if(rmi.equals("Put")){
                callReply = stub.Put(req);}
            else
                System.out.println("Wrong parameters!");
        } catch(Exception e){
            return null;
        }
        return callReply;
    }

    // RMI handlers
    public Integer Get(String key){
        // Your code here
    	Op getOp = new Op("Get", cliSeq, key, -1);
    	kvpaxos.Request getCmd = new kvpaxos.Request(getOp, cliSeq);	//server should come up with the cliSeq (unique proposal number)
    	kvpaxos.Response getResp;
    	
		System.out.println("client calls get ");
		getResp = Call("Get", getCmd, 0); //0 is hardcode should be flexible
		
		//call for each server
		if(getResp != null) {
			System.out.println("client gets: " + getResp.value);
			Op temp = (Op)getResp.value;
			return temp.value;
		}
    	
    	
    	
    	
        return -1;
        //-1 means error, 0 means no paxos
    }

    public boolean Put(String key, Integer value){
        // Your code here
    	Op getOp = new Op("Put", cliSeq, key, value);
    	kvpaxos.Request getCmd = new kvpaxos.Request(getOp, cliSeq);
    	kvpaxos.Response getResp;
    	//for(int i = 0; i < servers.length; i++) {
    		getResp = this.Call("Put", getCmd, 0); //0 index to which server to go to, idk which one rn default 0
    		//call for each server
    		if(getResp != null) {
    			return getResp.success;
    		}
    	//}
    	
//    	pxa[0].Start(cliSeq, putCmd);
//    	Paxos.retStatus ret;
//    	Object lastVal = null;
//    	int numDec = 0;
//    	while(numDec < pxa.length) {
//	    	for(int i = 0; i < pxa.length; i++){
//	    		if(pxa[i] != null){
//	                ret = pxa[i].Status(cliSeq);
//	                
//	                if(ret.state == State.Decided) {
//	                	if(numDec > 0 && lastVal != ret.v)
//	                		return false;
//	                    numDec ++;
//	                    lastVal = ret.v;
//	                }
//	
//	            }
//	    	}
//    	}
        return false;
    	
    }

}
