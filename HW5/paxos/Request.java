//package paxos;
//import java.io.Serializable;
//
///**
// * Please fill in the data structure you use to represent the request message for each RMI call.
// * Hint: You may need the sequence number for each paxos instance and also you may need proposal number and value.
// * Hint: Make it more generic such that you can use it for each RMI call.
// * Hint: Easier to make each variable public
// */
//public class Request implements Serializable {
//    static final long serialVersionUID=1L;
//    // Your data here
//    int sequence_number;
//    int proposal_num;
//    Object value;
//    int me;
//    int done;
//
//
//    // Your constructor and methods here
//    public Request(int seq, int proposal_num, Object value) {
//        this.sequence_number = seq;
//        this.proposal_num = proposal_num;
//        this.value = value;
//    }
//
//    public Request(int seq, int proposal_num, Object value, int me, int done) {
//        this.sequence_number = seq;
//        this.proposal_num = proposal_num;
//        this.value = value;
//        this.me = me;
//        this.done = done;
//    }
//}


package paxos;
import java.io.Serializable;

/**
 * Please fill in the data structure you use to represent the request message for each RMI call.
 * Hint: You may need the sequence number for each paxos instance and also you may need proposal number and value.
 * Hint: Make it more generic such that you can use it for each RMI call.
 * Hint: Easier to make each variable public
 */
public class Request implements Serializable {
    static final long serialVersionUID=1L;

    int seq;
    Object value;
<<<<<<< HEAD
    int peer;
    PID pid;
    int done;

    Request() {
        this.seq = -1;
        this.pid = null;
        this.peer = -1;
        this.value = null;
        this.done = -1;
    }

    Request(int seq, PID pid, int peer, Object value, int done) {
        this.seq = seq;
        this.pid = pid;
        this.peer = peer;
=======
    int me;
    int done;

//
    // Your constructor and methods here
    public Request(int seq, int proposal_num, Object value) {
        this.sequence_number = seq;
        this.proposal_num = proposal_num;
>>>>>>> 92ad17805e1ce5159c1495cb28cb33a986df5510
        this.value = value;
        this.done = done;
    }
<<<<<<< HEAD
}
=======

    public Request(int seq, int proposal_num, Object value, int me, int done) {
        this.sequence_number = seq;
        this.proposal_num = proposal_num;
        this.value = value;
        this.me = me;
        this.done = done;
    }
}
>>>>>>> 92ad17805e1ce5159c1495cb28cb33a986df5510
