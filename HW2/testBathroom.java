
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;

public class testBathroom
{
  final static int ROUND = 100;
  final static int NUMTHREADS = 100;
  static AtomicBoolean noexcept = new AtomicBoolean(true);
  static AtomicInteger finishedThreads = new AtomicInteger(0);
  final static AtomicInteger count = new AtomicInteger(0);

  enum Team { UT, OU };
  
  class CapacityTester implements Runnable
  {
    final FairUnifanBathroom room;
    final Team team;

    public CapacityTester(FairUnifanBathroom room, Team team)
    {
			this.room = room;
			this.team = team;
    }
    
    public void run()
    {
    	for (int i = 0; i < ROUND; ++i)
    	{
	      if (team == Team.UT)
	      {
	        room.enterBathroomUT();
	      }
	      else
	      {
	        room.enterBathroomOU();
	      }
	      int occupants = count.incrementAndGet();
	      try 
	      {
					Thread.sleep(1);
				} 
	      catch (InterruptedException e1) 
	      {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	      try
	      {
	        Assert.assertTrue(occupants <= 5);
	      }
	      catch(AssertionError e)
	      {
	      	noexcept.set(false);
	      	break; // exit
	      }
	      
				count.decrementAndGet();
	
	      if (team == Team.UT)
	      {
	        room.leaveBathroomUT();
	      }
	      else
	      {
	        room.leaveBathroomOU();
	      }
    	}
    	finishedThreads.incrementAndGet();
    }
  }

  @Test
  public void capacity_test()
  {
    FairUnifanBathroom bathroom = new FairUnifanBathroom(); 
		Thread[] t = new Thread[NUMTHREADS];
		
		for (int i = 0; i < NUMTHREADS; ++i)
    {
			t[i] = new Thread(new CapacityTester(bathroom, Math.random() > 0.7 ? Team.OU : Team.UT));
		}
		
		for (int i = 0; i < NUMTHREADS; ++i)
    {
			t[i].start();
		}
		
		// instead of joining we will do this exception propagation scheme with a boolean flag
		while (finishedThreads.get() != NUMTHREADS) { Assert.assertTrue(noexcept.get()); }
  }	
}
