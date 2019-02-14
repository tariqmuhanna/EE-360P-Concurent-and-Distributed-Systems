// EID 1
// EID 2

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class FairUnifanBathroom {
    int roomSession = 0; //for testing purposes
    int size = 0;
    int bathroom_limit = 5;
    private int free_resources = bathroom_limit;
    int flag = 0; // 0 = UT, 1 = OU
    int lastSwitch = 0; // 1 = last person leaves
    boolean utWait = false;
    boolean ouWait = false; // 0 = nobody waiting, 1 = opposing team waiting
    ReentrantLock monitorLock = new ReentrantLock();
    Condition ut = monitorLock.newCondition();
    Condition ou = monitorLock.newCondition();
    private int utWaitingN = 0;
    private int ouWaitingN = 0;
    private int utUsingN = 0;
    private int ouUsingN = 0;
    Queue<Integer> line = new LinkedList<>();


    private int peekHelper (Queue q) {
        int result = 0;
        try {
            result = line.peek();
        }
        catch (NullPointerException e) {
            return -1;
        }
        return result;
    }


    public void enterBathroomUT() {
        // Called when a UT fan wants to enter bathroom
        monitorLock.lock();
        if (free_resources == 0 || ouUsingN > 0 || peekHelper(line) == 1) {
            utWaitingN++;
            line.add(0);
//            System.out.println("UT using: " + utUsingN + " UT waitng: " + utWaitingN);
//            System.out.println("OU using: " + ouUsingN + " OU waiting: " + ouWaitingN + '\n');
        }
        while (free_resources == 0 || ouUsingN > 0 || peekHelper(line) == 1) {
            try {
                ut.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (utWaitingN != 0) {
            utWaitingN--;
            line.remove();
        }
        free_resources--;
        utUsingN++;
//        System.out.println("UT using: " + utUsingN + " UT waitng: " + utWaitingN);
//        System.out.println("OU using: " + ouUsingN + " OU waiting: " + ouWaitingN + '\n');
        monitorLock.unlock();
    }

    public void enterBathroomOU() {
        // Called when a OU fan wants to enter bathroom
        monitorLock.lock();

        if (free_resources == 0 || utUsingN > 0 || peekHelper(line) == 0) {
            ouWaitingN++;
            line.add(1);
//            System.out.println("UT using: " + utUsingN + " UT waitng: " + utWaitingN);
//            System.out.println("OU using: " + ouUsingN + " OU waiting: " + ouWaitingN + '\n');
        }

        while (free_resources == 0 || utUsingN > 0 || peekHelper(line) == 0) {
            try {
                ou.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (ouWaitingN != 0) {
            ouWaitingN--;
            line.remove();
        }
        free_resources--;
        ouUsingN++;
//        System.out.println("UT using: " + utUsingN + " UT waitng: " + utWaitingN);
//        System.out.println("OU using: " + ouUsingN + " OU waiting: " + ouWaitingN + '\n');
        monitorLock.unlock();
    }


    public void leaveBathroomUT() {
        // Called when a UT fan wants to leave bathroom
        monitorLock.lock();
        utUsingN--;
        free_resources++;
//        System.out.println("UT using: " + utUsingN + " UT waitng: " + utWaitingN);
//        System.out.println("OU using: " + ouUsingN + " OU waiting: " + ouWaitingN + '\n');
//        if (peekHelper(line) != -1) {
            ou.signalAll();
            ut.signalAll();
//            if (peekHelper(line) == 1) {
//                ou.signalAll();
//            }
//            else
//                ut.signalAll();
//        }
//        if(ouWaitingN>0)
//            ou.signal();
        monitorLock.unlock();
    }

    public void leaveBathroomOU() {
        // Called when a OU fan wants to leave bathroom
        monitorLock.lock();
        ouUsingN--;
        free_resources++;
//        System.out.println("UT using: " + utUsingN + " UT waitng: " + utWaitingN);
//        System.out.println("OU using: " + ouUsingN + " OU waiting: " + ouWaitingN);
//        if (peekHelper(line) != -1) {
            ou.signalAll();
            ut.signalAll();
//            if (peekHelper(line) == 1) {
//                ou.signalAll();
//            }
//            else
//                ut.signalAll();
//        }
        monitorLock.unlock();
    }
}