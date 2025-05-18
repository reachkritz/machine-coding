
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskScheduler implements Runnable {

    private static AtomicInteger threadCount = new AtomicInteger(3);
    ExecutorService service = Executors.newFixedThreadPool(3);
    private static LinkedBlockingQueue<FutureTask<String>> queue = new LinkedBlockingQueue<>();

    @Override
    public void run() {
        while (true) {
            if (threadCount.get() > 0 && !queue.isEmpty()) {
                System.out.println("Popping task from queue.");
                FutureTask<String> task = queue.remove();
                service.submit(task);
            } else if (threadCount.get() <= 0) {
                System.out.println("Task count exhausted.");
            }
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            }
        }
    }

    private static FutureTask<String> submitTask(Callable<String> callable) {
        FutureTask<String> result = new FutureTask<>(wrapFunction(callable));
        queue.add(result);
        return result;
    }

    private static Callable<String> wrapFunction(Callable<String> callable) {
        return () -> {
            System.out.println("start : "+threadCount.decrementAndGet());
            String result = callable.call();
            System.out.println("finish : "+threadCount.incrementAndGet());
            return result;
        };
    }

    public static void main(String[] args) {
        TaskScheduler scheduler = new TaskScheduler();
        Thread t1 = new Thread(scheduler);
        t1.start(); 

        Callable<String> callableTask = () -> {
            Thread.sleep(4000);
            System.out.println("In Task");
            return "Task's execution";
        };

        FutureTask<String> f1 = submitTask(callableTask);
        FutureTask<String> f2 = submitTask(callableTask);
        FutureTask<String> f3 = submitTask(callableTask);
        FutureTask<String> f4 = submitTask(callableTask);
        FutureTask<String> f5 = submitTask(callableTask);

        try {
            System.out.println(f1.get());
            System.out.println(f2.get());
            System.out.println(f3.get());
            System.out.println(f4.get());
            System.out.println(f5.get());
            
        } catch (Exception e) {
        }
    }

}
