package paxos;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//import static com.sun.tools.doclint.Entity.mu;
//import static sun.tools.jstat.Alignment.keySet;

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
    Map<Integer, Object> value_map =
            new ConcurrentHashMap<Integer, Object>();

    int max_seq_seen;
    int sequence_number;                    // Your own sequence number
    Object value;                           // Your own value
    ArrayList<Integer> done_list;           // List of finished peers
    int majority;
    State state;
    int NULL = -1;

    int prepreq_cnt = 0;
    int prepres_cnt = 0;
    int accreq_cnt = 0;
    int accres_cnt = 0;
    int dreq_cnt = 0;
    int dres_cnt = 0;


    private Instance getInstance(int sequence_number) {
        mutex.lock();                                       // Mutex

        if(!instance_map.containsKey(sequence_number)) {    // Check if contains key
            Instance instance = new Instance();             // Make new instance if not
            instance.highest_accepted = -1;
            instance.highest_proposal = -1;
            instance.value = null;
            instance_map.put(sequence_number, instance);
        }

        mutex.unlock();
        return instance_map.get(sequence_number);
    }

    private Instance getInstance2(int sequence_number) {
        if(!instance_map.containsKey(sequence_number)) {    // Check if contains key
            Instance instance = new Instance();             // Make new instance if not
            instance.highest_accepted = -1;
            instance.highest_proposal = -1;
            instance.value = null;
            instance_map.put(sequence_number, instance);
        }
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
        done_list = new ArrayList<>();
        for (int i=0; i<peers.length; i++){
            done_list.add(-1);        // Init complete list to all -1
        }
        majority = peers.length/2 + 1;  // Number require to have majority votes
//        highest_proposal = Integer.MIN_VALUE;
//        highest_accepted = Integer.MIN_VALUE;
        state = State.Pending;          // Initially the stat is pending
        value = null;                   // A value has yet to be set
        max_seq_seen = -1;

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
        if (seq < Min())
            return;
        mutex.lock();
        updateMaxSeqSeen(seq);
        this.sequence_number = seq;             // Set seq number
        this.value = value;                     // Set object value
        Thread thread = new Thread(this);// Make new thread
        mutex.unlock();

        thread.start();                        // Start

    }

    private void updateMaxSeqSeen(int seq) {
        if (seq > this.max_seq_seen) {
            this.max_seq_seen = seq;
        }
    }

    private boolean updateProposalNumber(int seq, int n){
        mutex.lock();
        Instance p_instance = this.getInstance(seq);

        if (n > p_instance.highest_proposal) {
            p_instance.highest_proposal = n;
            mutex.unlock();
            return true;
        }
        mutex.unlock();
        return false;
    }

    private int getProposalNumber(int seq){
        Instance instance = getInstance(sequence_number);
        return instance.highest_proposal+1;
    }

    // n_p (highest prepare seen)
    // n_a, v_a (highest accept seen)

    int generateProposalNumber(Instance instance) {
        return
                instance.highest_proposal == -1 ?
                        this.me+1 : (instance.highest_proposal / this.peers.length+1) * this.peers.length + this.me+1;
    }

    int generateProposalNumber2() {
        return Max()*100 + this.me;
    }

    @Override
    public void run(){      // PROPOSER
        // while not decided, loop
        while (this.getInstance(this.sequence_number).state != State.Decided) {

        //*****PREPARE PHASE*****
            // Makes unique proposal number
            //int n = generateProposalNumber(this.getInstance(this.sequence_number));
            //int n = generateProposalNumber2();
            //int n = this.me+1;
            mutex.lock();
            int n = (this.max_seq_seen + this.sequence_number + this.me*this.sequence_number);
            System.out.println("seq=" + this.sequence_number + " n=" + n + " max=" + this.max_seq_seen + " me=" +this.me +'\n');
            this.updateMaxSeqSeen(n);
            mutex.unlock();
            if (this.sequence_number == 7)
                System.out.println("7 before prepare");
            Response proposalResponse = sendPrepareToAll(this.sequence_number, n, this.value);


            //*****ACCEPTOR PHASE*****
            // If majority acquired, then begin making and sending accept request
            if (proposalResponse != null && proposalResponse.majority){
                Object v = proposalResponse.value;
                n = proposalResponse.proposal_num;
                if (this.sequence_number == 7)
                    System.out.println("7 before accept");
                boolean result = sendAcceptToAll(this.sequence_number, n, proposalResponse.value);

                // Sending decided msg
                // Check if majority has been reached
                if (result){
                    sendDecidedToAll(this.sequence_number, n, v);
                }
            }
        }
    }


    public Response sendPrepareToAll(int seq, int n, Object value) {
        int n_a = 0;
        Object v_a = value;
        int accepted_cnt = 0;           // # of prepare msgs accepted
        Request prepRequest = new Request(seq, n, value);

        // Broadcast prepare msg
        for (int i=0; i<this.peers.length; i++) {
            Response prepResponse;
            prepreq_cnt++;
            if (i != this.me)
                prepResponse = this.Call("Prepare", prepRequest, i);// Rmi msg to everyone
            else
                prepResponse = this.Prepare(prepRequest);               // Msg to myself

            // Check Response to the prepare msg
            if (prepResponse != null){
                this.updateMaxSeqSeen(prepResponse.proposal_num);
                if (prepResponse.proposal_accepted) {   // Check response didnt fail (null) & accepted

                    accepted_cnt++;                         // Increment number of accepted
                    if (prepResponse.proposal_num > n_a) {  // Update parameters
                        n_a = prepResponse.proposal_num;
                        v_a = prepResponse.value;
                    }
                }
//                else {
//                    this.updateMaxSeqSeen(prepResponse.proposal_num);
//                    if (updateProposalNumber(this.sequence_number, n_a)) {
//                        return null;
//                    }
//                }
            }
        }

        this.updateMaxSeqSeen(n_a);
        // Check if majority has been reached
        Response proposalResponse = new Response(); // Create response msg
        if(accepted_cnt >= majority){
            if (n_a == 0)
                v_a = value;
            proposalResponse.proposal_num = n_a;    // Max proposal number
            proposalResponse.value = v_a;           // Value associated with max proposal number
            proposalResponse.majority = true;       // Set flag true
        }
        else {
            proposalResponse.majority = false;       // Set flag false
        }

        return proposalResponse;
    }


    // RMI handler
    public Response Prepare(Request req){
        // your code here
        mutex.lock();
        prepres_cnt++;
        Response proposalResponse = new Response();
        Instance instance = this.getInstance2(req.sequence_number);
        this.updateMaxSeqSeen(req.proposal_num);
        this.updateMaxSeqSeen(instance.highest_proposal);
        // Send the acceptance message if the received proposal number is greater than any this instance has seen
        if (req.proposal_num > instance.highest_proposal) {
            instance.highest_proposal = req.proposal_num;
            instance.value = instance.value == null ? req.value : instance.value;
            proposalResponse.sequence_number = req.sequence_number;
            proposalResponse.proposal_num = instance.highest_proposal;
            proposalResponse.value = instance.value;
            proposalResponse.proposal_accepted = true;
//            if (req.sequence_number == 6)
//                System.out.println("6 prepare accepted");
        }

        // Send rejection
        else {
            proposalResponse.proposal_accepted = false;
            proposalResponse.proposal_num = instance.highest_proposal;
        }
        mutex.unlock();
        return proposalResponse;

    }



    private boolean sendAcceptToAll(int seq, int n, Object value) {
        //Instance instance = this.getInstance(seq);
        Request acceptedRequest = new Request(seq, n, value);

        // Broadcast accept msg
        int accepted_cnt = 0;
        for (int i=0; i<this.peers.length; i++) {
            Response accecptedResponse;
            accreq_cnt++;
            if (i != this.me)
                accecptedResponse = this.Call("Accept", acceptedRequest, i);// Rmi msg to everyone
            else {
                accecptedResponse = this.Accept(acceptedRequest);               // Msg to myself
            }

            if ((accecptedResponse != null) &&
                    accecptedResponse.accept_accepted) {   // Check response didnt fail (null) & accepted
                accepted_cnt++;
            }
        }
        this.updateMaxSeqSeen(n);
        return accepted_cnt >= majority;
    }



    public Response Accept(Request req){
        // your code here
        mutex.lock();
        Response acceptorResponse;
        Instance instance = this.getInstance2(req.sequence_number);
        accres_cnt++;
        this.updateMaxSeqSeen(req.sequence_number);
        this.updateMaxSeqSeen(instance.highest_proposal);

        // Send the acceptance message if the received proposal number is greater than any this instance has seen
        if(req.proposal_num >= instance.highest_proposal) {
            instance.highest_proposal = req.proposal_num;
            instance.highest_accepted = req.proposal_num;
            instance.value = req.value;
            acceptorResponse = new Response();
            acceptorResponse.accept_accepted = true;
        }
        // Send rejection msg
        else {
            acceptorResponse = new Response();
            acceptorResponse.accept_accepted = false;

        }
        mutex.unlock();
        return acceptorResponse;

    }

    private void sendDecidedToAll(int seq, int n, Object v){
//        // Update Instance
        //Instance d_instance = this.instance_map.get(seq);
//        d_instance.highest_proposal = n;//proposalResponse.proposal_num;
//        d_instance.highest_accepted = n;//proposalResponse.proposal_num;
//        d_instance.value =v;// proposalResponse.value;
        //d_instance.state = State.Decided;

        int done = this.done_list.get(this.me);
        for (int i=0; i<this.peers.length; i++) {
            dreq_cnt++;
            //mutex.lock();
            Response decidedResponse;

            if (i == this.me) {
                this.value_map.put(this.me, v);
                Request decideRequest = new Request(seq, n, v, this.me, done);
                //mutex.unlock();
                decidedResponse = this.Decide(decideRequest);
            }
            else {
                Request decideRequest = new Request(seq, n, v, this.me, done);
                //mutex.unlock();
                decidedResponse = this.Call("Decide", decideRequest, i);
            }

        }

    }

    public Response Decide(Request req){
        // your code here
        mutex.lock();
        dres_cnt++;
        Instance instance = this.getInstance2(req.sequence_number);
//        instance.highest_proposal = req.proposal_num;
//        instance.highest_proposal = req.proposal_num;
        instance.value = req.value;
        instance.state = State.Decided;

//        if (req.sequence_number == 6)
//            System.out.println("6 decided");

        if (this.done_list.get(req.me) < req.done)
            this.done_list.set(req.me, req.done);
        mutex.unlock();
        return new Response();
    }

    /**
     * The application on this machine is done with
     * all instances <= seq.
     *
     * see the comments for Min() for more explanation.
     */
    public void Done(int seq) {
        // Your code here
        mutex.lock();
        if(seq > this.done_list.get(this.me))
            this.done_list.set(this.me, seq);   // Update done list
        mutex.unlock();
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

        int min = done_list.get(this.me);

        // Min is the minimum among z_i, the highest number
        // ever passed to Done() on peer i
        int size = this.done_list.size();
        for (int i = 0; i<size; i++){
            if(min > i)
                min = i;                    // Update min
        }

        // Paxos is required to have forgotten all information
        // about any instances it knows that are < Min().
        Set<Integer> key_size = instance_map.keySet();
        for(int seq_num : key_size) {

            // Greater than min, keep
            if (seq_num > min)
                continue;

                // State has not been decided, keep
            else if (instance_map.get(seq_num).state != State.Decided)
                continue;

                // Less than or equal to min or state = decided, forget
//            else
//                instance_map.remove(seq_num);       // Free up memory
            else if (seq_num <= min || instance_map.get(seq_num).state == State.Decided)
                instance_map.remove(seq_num);       // Free up memory
        }

        return min + 1;
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