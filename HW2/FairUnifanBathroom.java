// EID 1
// EID 2

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class FairUnifanBathroom {
    int size = 0;
    int bathroom_limit = 5;
    int flag = 0; // 0 = UT, 1 = OU
//	ReentrantLock monitorLock = new ReentrantLock();
//	Condition ut = monitorLock.newCondition();
//    Condition ou = monitorLock.newCondition();

  public synchronized void enterBathroomUT() {
    // Called when a UT fan wants to enter bathroom
      if (size != 0) {
          while (flag == 1 || size == bathroom_limit) {
              try {
                  wait();
              } catch (InterruptedException e) {
                  e.printStackTrace();
              }
          }
      }
      else
          flag = 0;
      size++;
  }
	
	public synchronized void enterBathroomOU() {
    // Called when a OU fan wants to enter bathroom
        if (size != 0) {
            while (flag == 0 || size == bathroom_limit) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        else
            flag = 1;
        size++;
  }
	
	public synchronized void leaveBathroomUT() {
    // Called when a UT fan wants to leave bathroom
        size--;
        notifyAll();
	}

	public synchronized void leaveBathroomOU() {
    // Called when a OU fan wants to leave bathroom
        size--;
        notifyAll();
	}
}
	
