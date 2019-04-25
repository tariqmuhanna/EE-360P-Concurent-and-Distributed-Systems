package kvpaxos;
import paxos.*;
import java.io.Serializable;

/**
 * Please fill in the data structure you use to represent the request message for each RMI call.
 * Hint: Make it more generic such that you can use it for each RMI call.
 * Hint: Easier to make each variable public
 */
public class Request implements Serializable {
    static final long serialVersionUID=11L;
    // Your data here
    Op oper;
    int seq;
    Request(){
    	this.oper = null;
    	seq = -1;
    }
    
    Request(Op op, int seq){
        this.oper = op;
        this.seq = seq;
    }
    

    // Your constructor and methods here

}
