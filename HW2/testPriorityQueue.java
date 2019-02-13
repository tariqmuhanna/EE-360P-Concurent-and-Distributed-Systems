
import java.util.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;

public class testPriorityQueue
{
  @Test
  public void serialAdd_test()
  {
    final PriorityQueue q = new PriorityQueue(10); 
	
    int first = q.add("first", 9);
    int second = q.add("second", 0);
    int third = q.add("third", 0);

    Assert.assertTrue(first == 0 
      && second == 1
      && third == 2);
  }

  @Test
  public void serialSearch_test()
  {
    final PriorityQueue q = new PriorityQueue(10); 
	
    q.add("first", 9);
    q.add("second", 0);
    q.add("a", 0);
    q.add("b", 0);
    q.add("c", 1);

    Assert.assertTrue(q.search("second") == 2 && q.search("") == -1);
  }

  @Test
  public void serialGetFirst_test()
  {
    final PriorityQueue q = new PriorityQueue(10);

    q.add("zero", 0);
    q.add("one", 1);
    q.add("two", 2);

    Assert.assertTrue(q.getFirst().equals("two")); 
  }
}
