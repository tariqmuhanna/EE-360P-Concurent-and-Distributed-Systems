package kvpaxos;
import java.io.Serializable;

/**
 * You may find this class useful, free to use it.
 */
public class Op implements Serializable{
    static final long serialVersionUID=33L;
    String op; //op is the command 
    int ClientSeq;
    String key;	//key is the key
    Integer value;
    
    public Op(String op, int ClientSeq, String key, Integer value){
        this.op = op;
        this.ClientSeq = ClientSeq;
        this.key = key;
        this.value = value;
    
    }
    public String toString() {
    	
    	return key + " " + value;
    }
}
