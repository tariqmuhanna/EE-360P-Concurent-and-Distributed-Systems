package paxos;
import java.io.Serializable;

public class PID implements Serializable, Comparable<PID> {
    static final long serialVersionUID = 3L;
    int proposal_num;
    int peer;

    public PID() {
        this.proposal_num = -1;
        this.peer = -1;
    }

    public PID(int number, int peer) {
        this.proposal_num = number;
        this.peer = peer;
    }

    public int compare(PID self, PID other){
        if (other == null)
            return -1;

        if (proposal_num == other.proposal_num)
            return - (peer - other.peer);

        else return -(proposal_num - other.proposal_num);
    }

    @Override
    public int compareTo(PID other) {
        if (other == null)
            return -1;

        if (proposal_num == other.proposal_num)
            return - (peer - other.peer);

        else return -(proposal_num - other.proposal_num);
    }
}

