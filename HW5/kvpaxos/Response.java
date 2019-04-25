package kvpaxos;

import java.io.Serializable;

import paxos.PID;

/**
 * Please fill in the data structure you use to represent the response message for each RMI call.
 * Hint: Make it more generic such that you can use it for each RMI call.
 */
public class Response implements Serializable {
    static final long serialVersionUID=22L;
    // your data here
    Request trig; //prob unnecessary 
    Object value;
    boolean success;

    Response () {
        trig =  null;
        value = null;
        success = false;
    }


    Response (Request t, Object value, boolean success){
    	trig = t;
    	this.value = value;
    	this.success = success;
    }
    // Your constructor and methods here
}
