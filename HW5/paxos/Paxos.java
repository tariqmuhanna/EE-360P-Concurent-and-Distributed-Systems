package paxos;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import static sun.tools.jstat.Alignment.keySet;

/**
 * This class is the main class you need to implement paxos instances.
 */

class Instance {

    int highest_proposal;
    int highest_accepted;
    int sequnce_number;
    Object value;
    State state;

    public Instance() {
        highest_proposal = -1;  // They can only be positive right?
        highest_accepted = -1;
        state = State.Pending;
        value = null;
    }
}

public class Paxos implements PaxosRMI, Runnable{

    ReentrantLock mutex;
    String[] peers; // hostname
    int[] ports; // host port
    int me; // index into peers[]

    Registry registry;
    PaxosRMI stub;

    AtomicBoolean dead;// for testing
    AtomicBoolean unreliable;// for testing

    // Your data here
    Map<Integer, Instance> instance_map =      // Stores & Keeps track of instances
            new ConcurrentHashMap<Integer, Instance>();

    int sequence_number;                    // Your own sequence number
    Object value;                           // Your own value
    ArrayList<Integer> done_list;           // List of finished peers
    int majority;
    State state;
    int NULL = -1;



    private Instance getInstance(int sequence_number) {
        mutex.lock();                                       // Mutex

        if(!instance_map.containsKey(sequence_number)) {    // Check if contains key
            Instance instance = new Instance();             // Make new instance if not
            instance_map.put(sequence_number, instance);
        }

        mutex.unlock();
        return instance_map.get(sequence_number);

    }

    /**
     * Call the constructor to create a Paxos peer.
     * The hostnames of all the Paxos peers (including this one)
     * are in peers[]. The ports are in ports[].
     */
    public Paxos(int me, String[] peers, int[] ports){

        this.me = me;
        this.peers = peers;
        this.ports = ports;
        this.mutex = new ReentrantLock();
        this.dead = new AtomicBoolean(false);
        this.unreliable = new AtomicBoolean(false);

        // Your initialization code here
        for (int i=0; i<peers.length; i++){
            done_list.add(NULL);        // Init complete list to all -1
        }
        majority = peers.length/2 + 1;  // Number require to have majority votes
//        highest_proposal = Integer.MIN_VALUE;
//        highest_accepted = Integer.MIN_VALUE;
        state = State.Pending;          // Initially the stat is pending
        value = null;                   // A value has yet to be set

        // register peers, do not modify this part
        try{
            System.setProperty("java.rmi.server.hostname", this.peers[this.me]);
            registry = LocateRegistry.createRegistry(this.ports[this.me]);
            stub = (PaxosRMI) UnicastRemoteObject.exportObject(this, this.ports[this.me]);
            registry.rebind("Paxos", stub);
        } catch(Exception e){
            e.printStackTrace();
        }
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
    public Response Call(String rmi, Request req, int id){
        Response callReply = null;

        PaxosRMI stub;
        try{
            Registry registry=LocateRegistry.getRegistry(this.ports[id]);
            stub=(PaxosRMI) registry.lookup("Paxos");
            if(rmi.equals("Prepare"))
                callReply = stub.Prepare(req);
            else if(rmi.equals("Accept"))
                callReply = stub.Accept(req);
            else if(rmi.equals("Decide"))
                callReply = stub.Decide(req);
            else
                System.out.println("Wrong parameters!");
        } catch(Exception e){
            return null;
        }
        return callReply;
    }


    /**
     * The application wants Paxos to start agreement on instance seq,
     * with proposed value v. Start() should start a new thread to run
     * Paxos on instance seq. Multiple instances can be run concurrently.
     *
     * Hint: You may start a thread using the runnable interface of
     * Paxos object. One Paxos object may have multiple instances, each
     * instance corresponds to one proposed value/command. Java does not
     * support passing arguments to a thread, so you may reset seq and v
     * in Paxos object before starting a new thread. There is one issue
     * that variable may change before the new thread actually reads it.
     * Test won't fail in this case.
     *
     * Start() just starts a new thread to initialize the agreement.
     * The application will call Status() to find out if/when agreement
     * is reached.
     */
    public void Start(int seq, Object value){
        // Your code here
        this.sequence_number = seq;             // Set seq number
        this.value = value;                     // Set object value
        Thread thread = new Thread(this);// Make new thread
        thread.start();                        // Start

    }

    // n_p (highest prepare seen)
    // n_a, v_a (highest accept seen)

//    proposer(v):
//            while not decided:
//    choose n, unique and higher than any n seen so far
//    send prepare(n) to all servers including self
//    if prepare_ok(n, n_a, v_a) from majority:
//    v' = v_a with highest n_a; choose own v otherwise
//    send accept(n, v') to all
//            if accept_ok(n) from majority:
//    send decided(v') to all
    @Override
    public void run(){      // PROPOSER
        //Your code here

        // Find the minimum & discard all instances with seq numbers ≤ minimum
        if (this.sequence_number <= this.Min())   // If seq # is smaller than minimum, no need
            return;                                 // to proceed further

        // while not decided, loop
        while (this.getInstance(this.sequence_number).state != State.Decided) {

        //*****PREPARE PHASE*****
            // Choose n, unique and higher than any n seen so far
                // send prepare(n) to all servers including self
            Instance instance = this.getInstance(sequence_number);
            Object v_a = value;			    // proposer's orig value
            int n_a = instance.highest_proposal;
            int accepted_cnt = 0;           // # of prepare msgs accepted

            // Compose prepare msg
            if (this.me+1 > instance.highest_proposal)
                instance.highest_proposal = this.me+1;
            Request prepRequest = new Request(this.sequence_number,
                    instance.highest_proposal, value);

            // Broadcast prepare msg
            for (int i=0; i<this.peers.length; i++) {
                Response prepResponse;

                if (i != this.me)
                    prepResponse = this.Call("Prepare", prepRequest, i);// Rmi msg to everyone
                else
                    prepResponse = this.Prepare(prepRequest);               // Msg to myself

                // Check Response to the prepare msg
                if ((prepResponse != null) &&
                        prepResponse.proposal_accepted) {   // Check response didnt fail (null) & accepted

                    accepted_cnt++;                         // Increment number of accepted
                    if (prepResponse.proposal_num > n_a) {  // Update parameters
                        n_a = prepResponse.proposal_num;
                        v_a = prepResponse.value;
                    }
                }
            }

            // Check if majority has been reached
            Response proposalResponse = new Response(); // Create response msg
            if(accepted_cnt >= majority){
                proposalResponse.proposal_num = n_a;    // Max proposal number
                proposalResponse.value = v_a;           // Value associated with max proposal number
                proposalResponse.majority = true;       // Set flag true
            }


        //*****ACCEPTOR PHASE*****


        }
    }


    // RMI handler
    public Response Prepare(Request req){
        // your code here

    }

    public Response Accept(Request req){
        // your code here

    }

    public Response Decide(Request req){
        // your code here

    }

    /**
     * The application on this machine is done with
     * all instances <= seq.
     *
     * see the comments for Min() for more explanation.
     */
    public void Done(int seq) {
        // Your code here
        if(seq > this.done_list.get(this.me))
            this.done_list.set(this.me, seq);   // Update done list
    }


    /**
     * The application wants to know the
     * highest instance sequence known to
     * this peer.
     */
    public int Max(){
        // Your code here
        int max = Integer.MIN_VALUE;                    // Set to minimum
        Set<Integer> key_list = instance_map.keySet();  // List of keys
        for(int key : key_list)
            if(key > max)
                max = key;                              // Update max
        return max;
    }

    /**
     * Min() should return one more than the minimum among z_i,
     * where z_i is the highest number ever passed
     * to Done() on peer i. A peers z_i is -1 if it has
     * never called Done().

     * Paxos is required to have forgotten all information
     * about any instances it knows that are < Min().
     * The point is to free up memory in long-running
     * Paxos-based servers.

     * Paxos peers need to exchange their highest Done()
     * arguments in order to implement Min(). These
     * exchanges can be piggybacked on ordinary Paxos
     * agreement protocol messages, so it is OK if one
     * peers Min does not reflect another Peers Done()
     * until after the next instance is agreed to.

     * The fact that Min() is defined as a minimum over
     * all Paxos peers means that Min() cannot increase until
     * all peers have been heard from. So if a peer is dead
     * or unreachable, other peers Min()s will not increase
     * even if all reachable peers call Done. The reason for
     * this is that when the unreachable peer comes back to
     * life, it will need to catch up on instances that it
     * missed -- the other peers therefore cannot forget these
     * instances.
     */
    public int Min(){
        // Your code here

    }



    /**
     * the application wants to know whether this
     * peer thinks an instance has been decided,
     * and if so what the agreed value is. Status()
     * should just inspect the local peer state;
     * it should not contact other Paxos peers.
     */
    public retStatus Status(int seq){
        // Your code here

        // If less than min, then msgs are forgotten
        if(seq < this.Min())
            return new retStatus(State.Forgotten, null);

        // If instances exists, return status
        else if (this.instance_map.containsKey(seq)){   // Check if instance exists
            Instance instance = instance_map.get(seq);  // If exists, send ret status
            return new retStatus(instance.state, instance.value);
        }

        // If instance doesn't exist, return null
        else{
            return new retStatus(State.Pending, null);
        }
    }

    /**
     * helper class for Status() return
     */
    public class retStatus{
        public State state;
        public Object v;

        public retStatus(State state, Object v){
            this.state = state;
            this.v = v;
        }
    }

    /**
     * Tell the peer to shut itself down.
     * For testing.
     * Please don't change these four functions.
     */
    public void Kill(){
        this.dead.getAndSet(true);
        if(this.registry != null){
            try {
                UnicastRemoteObject.unexportObject(this.registry, true);
            } catch(Exception e){
                System.out.println("None reference");
            }
        }
    }

    public boolean isDead(){
        return this.dead.get();
    }

    public void setUnreliable(){
        this.unreliable.getAndSet(true);
    }

    public boolean isunreliable(){
        return this.unreliable.get();
    }


}
