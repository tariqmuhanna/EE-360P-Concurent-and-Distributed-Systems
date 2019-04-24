package kvpaxos;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This is a subset of entire test cases
 * For your reference only.
 */
public class KVPaxosTest {


    public void check(Client ck, String key, Integer value){
        Integer v = ck.Get(key);
        assertTrue("Get(" + key + ")->" + v + ", expected " + value, v.equals(value));
    }

    @Test
    public void TestBasic(){
        final int npaxos = 5;
        String host = "127.0.0.1";
        String[] peers = new String[npaxos];
        int[] ports = new int[npaxos];

        Server[] kva = new Server[npaxos];
        for(int i = 0 ; i < npaxos; i++){
            ports[i] = 1100+i;
            peers[i] = host;
        }
        for(int i = 0; i < npaxos; i++){
            kva[i] = new Server(peers, ports, i);
        }

        Client ck = new Client(peers, ports);
        Client ck2 = new Client(peers, ports);
        
        System.out.println("Test: Basic put/get ...");
        ck.Put("app", 6);
        check(ck, "app", 6);
        ck.Put("a", 70);
        check(ck, "a", 70);

        System.out.println("... Passed");
        System.out.println("Test: 2 client put/get ...");
        ck.Put("clash", 1);
        ck2.Put("clash", 2);
        Integer c1 = ck.Get("clash");
        Integer c2 = ck2.Get("clash");
        assertTrue("No consensus", c1.equals(c2));

        System.out.println("... Passed");
        
        System.out.println("Test: sequential client put/get ...");
        ck.Put("first", 1);
        ck.Put("second", 2);
        ck.Put("third", 3);
        ck2.Put("fourth", 4);
        
        c1 = ck.Get("fourth");
        c2 = ck2.Get("first");
        assertTrue("No consensus", c1.equals(4));
        assertTrue("No consensus", c2.equals(1));

        System.out.println("... Passed");

    }

}
