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

    String[] servers;
    int[] ports;
    KVPaxosRMI stub;

    // Your definitions here
    ArrayList<kvpaxos.Response> respLog;

    public Server(String[] servers, int[] ports, int me){
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
    	
 
  
    	px.Start(req.seq, req.oper);
    	Paxos.retStatus ret = px.Status(req.seq);
    	
    	int numDec = 0;
    	while(ret.state == State.Pending) {
    		try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
    	if(ret.state == State.Decided) {
    		kvpaxos.Response getResp = new kvpaxos.Response(req, ret.v, true);
    		
	        mutex.lock();
	        //last response in log
	        //check if last response from other paxos is the same some how
//	        kvpaxos.Response lastResp = null;
//	        if(respLog.size() > 0)
//	        	lastResp = respLog.get(respLog.size()-1);
	        
	        respLog.add(getResp);
	        mutex.unlock();

	        px.Done(req.seq);
    		return getResp;
    	}
    	
    	
    	px.Done(req.seq);
        return new kvpaxos.Response(req, null, false);
    }

    public kvpaxos.Response Put(Request req){
        // Your code here

    	px.Start(req.seq, req.oper);
    	Paxos.retStatus ret = px.Status(req.seq);
    	
    	int numDec = 0;
    	while(ret.state == State.Pending) {
    		try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    	} 
    	//wait in loop if state is pending
    	if(ret.state == State.Decided) {
    		kvpaxos.Response putResp = new kvpaxos.Response(req, ret.v, true);
    		mutex.lock();
            respLog.add(putResp); //put the response from putting into the log.
            mutex.unlock();
            px.Done(req.seq);
    		return putResp;
    	}
    	px.Done(req.seq);
        return new kvpaxos.Response(req, null, false);
    }


}
