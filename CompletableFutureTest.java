
import java.util.*;
import java.util.concurrent.*;

public class CompletableFutureTest {

    public static void main(String[] args) {
        CompletableFuture<Void> future1
                = CompletableFuture.supplyAsync(() -> "Hello").thenAccept(s -> {
                    System.out.println(s);
                });
        CompletableFuture<Void> future2
                = CompletableFuture.supplyAsync(() -> "Beautiful").thenAccept(s -> {
                    System.out.println(s);
                });
        CompletableFuture<Void> future3
                = CompletableFuture.supplyAsync(() -> "World").thenAccept(s -> {
                    System.out.println(s);
                });

        CompletableFuture<Void> combinedFuture
                = CompletableFuture.allOf(future1, future2, future3);

        CompletableFuture.supplyAsync(() -> "Hello")
                .thenCombine(CompletableFuture.supplyAsync(() -> " World!"), (s1, s2) -> s1 + s2)
                .thenAccept(s -> {
                    System.out.println(s);
                }).join();
        try {
            combinedFuture.get();
            Callable<String> callableTask = () -> {
                TimeUnit.MILLISECONDS.sleep(1000);
                System.out.println("La la la");
                return "Task's execution";
            };
            Runnable task = () -> {
                System.out.println("Runnable here!");
            };

            List<Callable<String>> callableTasks = new ArrayList<>();
            callableTasks.add(callableTask);
            callableTasks.add(callableTask);
            callableTasks.add(callableTask);
            ExecutorService service = Executors.newCachedThreadPool();
            List<Future<String>> results = service.invokeAll(callableTasks);
            service.execute(task);

        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Exception");
        }
    }
}
