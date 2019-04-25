package kvpaxos;
import paxos.*;
// You are allowed to call Paxos.Status to check if agreement was made.

import static org.junit.Assert.assertFalse;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class Server implements KVPaxosRMI {
	int sleepTime = 10;
    ReentrantLock mutex;
    Registry registry;
    Paxos px;
    int me;
    int curr_seq; //highest sequence number decided upon

    String[] servers;
    int[] ports;
    KVPaxosRMI stub;

    Map<Integer, Op> respLog;
    Map<String, Integer> dict;
    // Your definitions here
    public Server(String[] servers, int[] ports, int me){
    	curr_seq = 0; //start off at 0;
        this.me = me;
        this.servers = servers;
        this.ports = ports;
        this.mutex = new ReentrantLock();
        this.px = new Paxos(me, servers, ports);
        respLog = Collections.synchronizedMap(new HashMap<Integer, Op>());
        dict = Collections.synchronizedMap(new HashMap<String, Integer>());
        // Your initialization code here



        try{
            System.setProperty("java.rmi.server.hostname", this.servers[this.me]);
            registry = LocateRegistry.getRegistry(this.ports[this.me]);
            stub = (KVPaxosRMI) UnicastRemoteObject.exportObject(this, this.ports[this.me]);
            registry.rebind("KVPaxos", stub);
        } catch(Exception e){
            e.printStackTrace();
        }
    }


    // RMI handlers
    public kvpaxos.Response Get(kvpaxos.Request req){
        // Your code here
    	System.out.println("Get: Server " + me);

    	Op returnedOp = null;
    	kvpaxos.Response getResp = null;
    	
    	while(returnedOp == null || !returnedOp.key.equals(req.oper.key) || !(returnedOp.value == req.oper.value)) {
    		px.Start(curr_seq, req.oper);
    		
        	if(!(waitDecided(curr_seq)))
        		System.out.println("time out");
        	
        	Paxos.retStatus ret = px.Status(curr_seq);
	    	if(ret.state == State.Decided) {
	    		returnedOp = (Op)ret.v;
	    		getResp = new kvpaxos.Response(req, returnedOp, true);
	    		System.out.println("Get Server " + me + ": " + curr_seq + " Op: " + returnedOp.key + returnedOp.value);

	    		//possibly use instance
	    		//ret.v is the operation
		        mutex.lock();
		        //last response in log
		        //check if last response from other paxos is the same some how
	//	        kvpaxos.Response lastResp = null;
	//	        if(respLog.size() > 0)
	//	        	lastResp = resplog.get(respLog.size()-1);
		        //pass map to paxos
		        respLog.put(curr_seq, returnedOp);
		        //while loop to keep proposing if ret.v != req.seq
		        mutex.unlock();
		        
	    	}
	    	curr_seq ++;
	    	
	    	
    	}
    	px.Done(req.seq);
    	Integer getVal = dict.get(returnedOp.key);
		return new kvpaxos.Response(req, new Op("Get", curr_seq, returnedOp.key, getVal), true);
    }
    private boolean waitDecided(int seq) {
    	
    	  int to = 10;
          int nd = 0;
          for(int i = 0; i < 50; i++){
        	  Object v = null;
              Paxos.retStatus ret;
              
              ret = px.Status(seq);
              if(ret.state == State.Decided) {
            	  return true;
              }
                     
              try {
                  Thread.sleep(to);
              } catch (Exception e){
                  e.printStackTrace();
              }
              if(to < 1000){
                  to = to * 2;
              }
          }
          return false;
    	
    }
    public kvpaxos.Response Put(Request req){
        // Your code here
    	System.out.println("Put: Server " + me);
    	Op returnedOp = null;
    	kvpaxos.Response getResp = null;
    	
    	while(returnedOp == null || !returnedOp.key.equals(req.oper.key) || !(returnedOp.value == req.oper.value)) {
    		px.Start(curr_seq, req.oper);
    		
        	if(!(waitDecided(curr_seq)))
        		System.out.println("time out");
        	
        	Paxos.retStatus ret = px.Status(curr_seq);
	    	if(ret.state == State.Decided) {
	    		returnedOp = (Op)ret.v;
	    		getResp = new kvpaxos.Response(req, returnedOp, true);
	    		System.out.println("Put Server " + me + ": " + curr_seq + " Op: " + returnedOp.key + returnedOp.value);
	    		//possibly use instance
	    		//ret.v is the operation
		        mutex.lock();
		        //last response in log
		        //check if last response from other paxos is the same somehow
	//	        kvpaxos.Response lastResp = null;
	//	        if(respLog.size() > 0)
	//	        	lastResp = resplog.get(respLog.size()-1);
		        //pass map to paxos
		        respLog.put(curr_seq, returnedOp);
		        dict.put(returnedOp.key, returnedOp.value);
		        //while loop to keep proposing if ret.v != req.seq
		        mutex.unlock();
		        
	    	}
	    	curr_seq ++;
	    	
    	}
    	px.Done(req.seq);
		return getResp;
    }


}
