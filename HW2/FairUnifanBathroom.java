// EID 1
// EID 2

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class FairUnifanBathroom {
	int roomSession = 0; //for testing purposes
    int size = 0;
    int bathroom_limit = 5;
    int flag = 0; // 0 = UT, 1 = OU
    int lastSwitch = 0; // 1 = last person leaves
    boolean utWait = false;
    boolean ouWait = false; // 0 = nobody waiting, 1 = opposing team waiting
//	ReentrantLock monitorLock = new ReentrantLock();
//	Condition ut = monitorLock.newCondition();
//    Condition ou = monitorLock.newCondition();

  public synchronized void enterBathroomUT() {
    // Called when a UT fan wants to enter bathroom
      if (size != 0) {
          while (flag == 1 || size == bathroom_limit || ouWait) {
        	  if(flag == 1 && lastSwitch == 0)
        		  utWait = true;
        	  //utWait = true;
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
            while (flag == 0 || size == bathroom_limit || utWait) {
          	    if(flag == 0 && lastSwitch == 0)
          	    	ouWait = true;
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
		lastSwitch = 0;
		if(size == 1 && ouWait) {
			flag = 1;
			utWait = false;
			lastSwitch = 1;
			System.out.println("Switching rooms to session " + roomSession);
			roomSession++;
			 	//give bathroom to ou
		}
        size--;
        notifyAll();
	}

	public synchronized void leaveBathroomOU() {
    // Called when a OU fan wants to leave bathroom
		lastSwitch = 0;
		if(size == 1 && utWait) {
			flag = 0;
			ouWait = false;
			lastSwitch = 1; //this prevents another ou thread to set ouWait = true
			System.out.println("Switching rooms to session " + roomSession);
			roomSession++;
		}
        size--;
        notifyAll();
	}
}
	
