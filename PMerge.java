import java.util.*;
import java.util.concurrent.*;


public class PMerge{

    private class PMergeHelper implements Callable<Integer> {
        private int x;
        private int[] y_arr;
        private Set<Integer> duplicate_check;

        public PMergeHelper(int x, int[] y_arr, Set<Integer> duplicate_check){
            this.x = x;
            this.y_arr = y_arr;
            this.duplicate_check = duplicate_check;
        }

        @Override
        public Integer call() throws Exception {
//            int count = 0;
//            while (count < y_arr.length && x > y_arr[count]) {
//                count++;
//            }
//            return count;

            int count = 0; // records how many #s x is less than compared to other array
            while (count < y_arr.length && x > y_arr[count]) { // compares x to other array
                count++;
            }
            if (!duplicate_check.add(x)) // checks for duplicates
                count++;
            return y_arr.length - count; // sorts in reverse order
        }
    }

    public static void parallelMerge(int[] A, int[] B, int[]C, int numThreads){
        // TODO: Implement your parallel merge function
        final ExecutorService es = Executors.newFixedThreadPool(numThreads);
        final Set<Integer> duplicate_check = Collections.synchronizedSet(new HashSet<>());

        // loops through all elements in A
        for (int i = 0; i < A.length; i++) {
            Future<Integer> compare_idx = es.submit(new PMerge().new PMergeHelper(A[i], B, duplicate_check));

            try {
                C[compare_idx.get()+A.length-i-1] = A[i]; // records elements placement into merge array
            } catch (InterruptedException | ExecutionException e) {}
        }

        // loops through all elements in B
        for (int i = 0; i < B.length; i++) {
            Future<Integer> compare_idx = es.submit(new PMerge().new PMergeHelper(B[i], A, duplicate_check));

            try {
                C[compare_idx.get()+B.length-i-1] = B[i]; // records elements placement into merge array
            } catch (InterruptedException | ExecutionException e) {}
        }

        // Tasks complete
        es.shutdown();
        try {
            es.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}