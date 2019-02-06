import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;


public class PSort extends RecursiveAction{
    private int[] A;
    private int begin;
    private int end;

    public PSort(int[] A, int begin, int end) {
        this.A = A;
        this.begin = begin;
        this.end = end;
    }

    public static void parallelSort(int[] A, int begin, int end) {
        final ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
        if((A.length == 0 || begin >= end))
            return;

        pool.invoke(new PSort(A, begin, end));
        pool.shutdown();
    }

    
    @Override
    protected void compute() {
        if(begin < end){
            int pivot = partition(A, begin, end);
//            invokeAll(new PSort(A, begin, pivot),
//                    new PSort(A, pivot + 1, end));
            PSort t1 = new PSort(A, begin,
                    pivot - 1);
            PSort t2 = new PSort(A, pivot + 1,
                    end);
            t1.fork();
            t2.compute();
            t1.join();
        }

    }

    private int partition(int[] arr, int low, int high) {
        int pivot = arr[high];
        int ind = low - 1;
        for(int j = low; j < high; j++){

            if (arr[j] <= pivot){
                ind++;
                swap(arr, ind, j);
            }
        }

        swap(arr, ind+1, high);

        return ind+1;
    }
//    int pivot = arr[low];
//    int i = low - 1;
//    int j  = high + 1;
//
//        while (true){
//        do { i++; }
//        while (arr[i] < pivot);
//
//        do { j--; }
//        while (arr[j] > pivot);
//
//        if (i >= j) return j;
//        swap(arr, i, j);
//    }


    private void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

}