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
    	Op getOp = new Op("get", cliSeq, key, -1);
    	kvpaxos.Request getCmd = new kvpaxos.Request(getOp, cliSeq);	//server should come up with the cliSeq (unique proposal number)
    	kvpaxos.Response getResp;
    	//for(int i = 0; i < servers.length; i++) {
    		System.out.println("calling get to client");
    		getResp = Call("Get", getCmd, 0); //0 is hardcode should be flexible
    		System.out.println(getResp.value);
    		//call for each server
    		if(getResp != null) {
    			return (Integer)getResp.value;
    		}
    	//}
    	
    	
    	
//    	if(pxa.length == 0)
//    		return 0;
//    	
//    	pxa[0].Start(cliSeq, getCmd);
//    	Paxos.retStatus ret;
//    	
//    	int numDec = 0;
//    	while(numDec < pxa.length) {
//	    	for(int i = 0; i < pxa.length; i++){
//	    		if(pxa[i] != null){
//	                ret = pxa[i].Status(cliSeq);
//	                
//	                if(ret.state == State.Decided) {
//	                	if(numDec > 0 && lastVal != ret.v)
//	                		return -1;
//	                    numDec ++;
//	                    lastVal = ret.v;
//	                }
//	
//	            }
//	    	}
//    	}
//    	if (lastVal != null) {
//    		Response finalOp = (Response)lastVal;
//    		return finalOp.value;
//    	}
    	
        return -1;
        //-1 means error, 0 means no paxos
    }

    public boolean Put(String key, Integer value){
        // Your code here
    	Op getOp = new Op("put", cliSeq, key, -1);
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
