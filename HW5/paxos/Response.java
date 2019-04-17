//package paxos;
//import java.io.Serializable;
//
///**
// * Please fill in the data structure you use to represent the response message for each RMI call.
// * Hint: You may need a boolean variable to indicate ack of acceptors and also you may need proposal number and value.
// * Hint: Make it more generic such that you can use it for each RMI call.
// */
//public class Response implements Serializable {
//    static final long serialVersionUID=2L;
//    public boolean accept_accepted;
//    // your data here
//
//    int proposal_num;
//    boolean proposal_accepted;
//    boolean majority;
//    int sequence_number;
//    Object value;
//
//    // Your constructor and methods here
//    public Response() {
//        this.sequence_number = -2;
//        this.proposal_num = Integer.MIN_VALUE;
//        this.value = null;
//        this.proposal_accepted = false;
//        this.accept_accepted = false;
//    }
//
//    public Response(int seq, int num, Object val) {
//        this.sequence_number = seq;
//        this.proposal_num = num;
//        this.value = val;
//    }
//}


package paxos;
import java.io.Serializable;

/**
 * Please fill in the data structure you use to represent the response message for each RMI call.
 * Hint: You may need a boolean variable to indicate ack of acceptors and also you may need proposal number and value.
 * Hint: Make it more generic such that you can use it for each RMI call.
 */
public class Response implements Serializable, Comparable<Response> {
    static final long serialVersionUID=2L;

    boolean accepted;
    PID pid;
    int peer;
    Object value;
    int done;

    Response () {
        this.accepted = false;
        this.pid = null;
        this.value = null;
        this.done = -1;
    }

    Response (boolean ok, PID pid, Object value, int done) {
        this.accepted = ok;
        this.pid = pid;
        this.value = value;
        this.done = done;
    }

    @Override
    public int compareTo(Response other) {
        // Make sure enteries are valid
        if (other == null || other.pid == null || pid == null)
            return -1;
        return pid.compareTo(other.pid);
    }

}