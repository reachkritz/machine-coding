
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class MultithreadedMergeSort {
    private static ExecutorService threadPool = Executors.newCachedThreadPool();

    private static List<Integer> merge(List<Integer> left, List<Integer> right) {
        List<Integer> merged = new ArrayList<>();
        int l = 0;
        int r = 0;
        while(l<left.size() && r<right.size()) {
            if (left.get(l) <= right.get(r)) {
                merged.add(left.get(l++));
            } else {
                merged.add(right.get(r++));
            }
        }

        while (l<left.size()) merged.add(left.get(l++));
        while (r<right.size()) merged.add(right.get(r++));

        return merged;
    }

    private static List<Integer> mergeSort(List<Integer> input) {
        int size = input.size();
        if (size == 1) return input;

        int mid = size/2;
        Future<List<Integer>> left = threadPool.submit(() -> mergeSort(input.subList(0, mid)));
        Future<List<Integer>> right = threadPool.submit(() -> mergeSort(input.subList(mid, size)));

        try {
            return merge(left.get(), right.get());
        } catch (InterruptedException | ExecutionException ex) {
            System.out.println("Error in getting results.");
            return Arrays.asList();
        }
    }

    public static void main(String[] args) {
        List<Integer> list = Arrays.asList(1,2,4,100, -1, 89, -3, 7,2,0,3,6,10,1);
        System.out.println("Sorted list : "+ mergeSort(list));
    }
}
