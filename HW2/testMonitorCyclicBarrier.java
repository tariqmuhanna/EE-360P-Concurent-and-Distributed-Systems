
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;

public class testMonitorCyclicBarrier {
	// THIS TEST WILL ONLY WORK CORRECTLY IF NUMTHREADS = PARTYSIZE!!!
	final static int NUMTHREADS = 1000;
	final static int PARTYSIZE = 1000;
	final static int ROUND = 500;
	
	final static AtomicInteger aliveCount = new AtomicInteger(NUMTHREADS);

  enum Status { ENTER, EXIT };
  class MarkedStatus
  {
  	int id; Status status;
  	public MarkedStatus (int id, Status status)
  	{
  		this.id = id;
  		this.status = status;
  	}
  	
  	public String toString()
  	{
  		return Integer.toString(id) + ": " + (status == Status.ENTER ? "ENTER" : "EXIT");
  	}
  }

  Queue<MarkedStatus> history = new ConcurrentLinkedQueue<MarkedStatus>();
	
	class SimpleTester implements Runnable
	{
		final MonitorCyclicBarrier gate;
		final int id;
		public SimpleTester(MonitorCyclicBarrier gate, int id) 
		{
			this.gate = gate;
			this.id = id;
		}
		
		public void run()
	  {
			for (int round = 0; round < ROUND; ++round)
	    {
				try
	      {
	        history.add(new MarkedStatus(id, Status.ENTER));
					gate.await();
	        history.add(new MarkedStatus(id, Status.EXIT));
				} 
	      catch (InterruptedException e)
	      {
					break;
				}
			}
			
			aliveCount.getAndDecrement();
		}
	}
	
  @Test
  public void simple_test()
  {
    MonitorCyclicBarrier gate = new MonitorCyclicBarrier(PARTYSIZE); 
		Thread[] t = new Thread[NUMTHREADS];
		
		int exit_cnt = 0;
	  int enter_cnt = 0;
	  int[] enter_steps = new int[NUMTHREADS];
	  int[] exit_steps = new int[NUMTHREADS];
	  for (int i = 0; i < NUMTHREADS; ++i)
	  {
	  	enter_steps[i] = 0; // initialize
	  }
		
		for (int i = 0; i < NUMTHREADS; ++i)
    {
			t[i] = new Thread(new SimpleTester(gate, i));
		}
		
		for (int i = 0; i < NUMTHREADS; ++i)
    {
			t[i].start();
		}

    while (aliveCount.get() >= PARTYSIZE || !history.isEmpty())
    {
      MarkedStatus s = history.poll();
      if (s != null)
      {
        if (s.status == Status.ENTER)
        {
          enter_cnt++;
        }
        else
        {
          exit_cnt++;
          // assert that a thread leaving hasn't already left with the current leaving
          // party
          Assert.assertTrue(enter_cnt/PARTYSIZE != enter_steps[s.id]/PARTYSIZE);
         	enter_steps[s.id]= enter_cnt; 
        }
      }
    }
    
    // clean up any hanging threads
    for(int i = 0; i < NUMTHREADS; ++i)
    {
    	t[i].interrupt();
    }
  }
}
