package kvpaxos;
import paxos.*;
// You are allowed to call Paxos.Status to check if agreement was made.

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
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
    // Your definitions here
    public Server(String[] servers, int[] ports, int me){
    	curr_seq = 0; //start off at 0;
        this.me = me;
        this.servers = servers;
        this.ports = ports;
        this.mutex = new ReentrantLock();
        this.px = new Paxos(me, servers, ports);
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
    	
 
    	//server should create req.seq as the highest unique sequence number
    	//map sequence number to operation
    	
    	px.Start(curr_seq, req.oper);	//if this server is behind, curr_seq should update at the end
    	Paxos.retStatus ret = null;
    	Op returnedOp = null;
    	kvpaxos.Response getResp = null;
    	
    	while(returnedOp == null || !returnedOp.key.equals(req.oper.key) || !(returnedOp.value == req.oper.value)) {
    		ret = px.Status(curr_seq);
	    	while(ret.state == State.Pending) {
	    		try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
	    	if(ret.state == State.Decided) {
	    		getResp = new kvpaxos.Response(req, ret.v, true);
	    		//possibly use instance
	    		//ret.v is the operation
		        mutex.lock();
		        //last response in log
		        //check if last response from other paxos is the same some how
	//	        kvpaxos.Response lastResp = null;
	//	        if(respLog.size() > 0)
	//	        	lastResp = resplog.get(respLog.size()-1);
		        //pass map to paxos
		        respLog.put(curr_seq, (Op)ret.v);
		        //while loop to keep proposing if ret.v != req.seq
		        mutex.unlock();
		        
	    	}
	    	curr_seq ++;
	    	px.Start(curr_seq, req.oper);
	    	
    	}
    	px.Done(req.seq);
		return getResp;
    }

    public kvpaxos.Response Put(Request req){
        // Your code here
    	System.out.println("Server " + me + " got put request");
    	px.Start(curr_seq, req.oper);	//if this server is behind, curr_seq should update at the end
    	Paxos.retStatus ret = null;
    	Op returnedOp = null;
    	kvpaxos.Response getResp = null;
    	
    	while(returnedOp == null || !returnedOp.key.equals(req.oper.key) || !(returnedOp.value == req.oper.value)) {
    		ret = px.Status(curr_seq);

    		System.out.println("Server " + me + " curr_seq: " + curr_seq);
	    	while(ret.state == State.Pending) {

	    		
	    		try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
	    	System.out.println("Server " + me + " finished pending for " + curr_seq);
	    	if(ret.state == State.Decided) {
	    		returnedOp = (Op)ret.v;
	    		getResp = new kvpaxos.Response(req, returnedOp, true);
	    		System.out.println("Server " + me + " has decided for " + curr_seq);
	    		System.out.println("Op: " + returnedOp.key + returnedOp.value);
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
	    	px.Start(curr_seq, req.oper);
	    	
    	}
    	px.Done(req.seq);
		return getResp;
    }


}
