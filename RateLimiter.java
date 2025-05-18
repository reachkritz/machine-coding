
import java.nio.file.AccessDeniedException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimiter {
    ConcurrentHashMap<Requestor, Integer> tokenBuckets = new ConcurrentHashMap<>();
    private static final Integer THRESHOLD = 50;
    private static Duration REFRESH_INTERVAL = Duration.ofSeconds(10);

    class Requestor {
        String id;
        String serviceId;

        public Requestor(String id, String serviceId) {
            this.id = id;
            this.serviceId = serviceId;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;

            if (!(o instanceof Requestor)) return false;

            Requestor r = (Requestor) o;
            return this.id.equals(r.id) && this.serviceId.equals(r.serviceId);
        }

        @Override
        public final int hashCode() {
            return this.id.hashCode()+this.serviceId.hashCode();
        }
    }

    class AccessToken {
        public String token;

        public AccessToken(String serviceId) {
            this.token = serviceId+Instant.now();
        }
    }

    public RateLimiter() {
        tokenBuckets.clear();
        initializeRefresher();
    }

    private void initializeRefresher() {
        Thread thread = new Thread(new TokenRefresher());
        thread.start();
    }

    class TokenRefresher implements Runnable {

        @Override
        public void run() {
            while (true) { 
                try {
                    Thread.sleep(REFRESH_INTERVAL);
                } catch (InterruptedException ex) {
                }
                System.out.println("Refreshing tokens.");
                tokenBuckets.clear();
            }
        }

    }

    AccessToken getToken(String requestorId, String serviceId) throws AccessDeniedException {
        Requestor requestor = new Requestor(requestorId, serviceId);
        if (Objects.equals(tokenBuckets.get(requestor), THRESHOLD)) {
            throw new AccessDeniedException("Limit Exhausted");
        }
        tokenBuckets.put(requestor, tokenBuckets.getOrDefault(requestor, 0) + 1);
        return new AccessToken(serviceId);
    }

    List<AccessToken> getBulkTokens(String requestorId, String serviceId) throws AccessDeniedException {
        Requestor requestor = new Requestor(requestorId, serviceId);
        if (Objects.equals(tokenBuckets.get(requestor), THRESHOLD)) {
            throw new AccessDeniedException("Limit Exhausted");
        }
        int count = (THRESHOLD - tokenBuckets.getOrDefault(requestor, 0)) < 10 ? THRESHOLD - tokenBuckets.getOrDefault(requestor, 0) : 10;
        tokenBuckets.put(requestor, tokenBuckets.getOrDefault(requestor, 0) + count);
        List<AccessToken> tokenList = new ArrayList<>();
        for (int i=0 ; i< count; i++) {
            tokenList.add(new AccessToken(serviceId));
        }
        return tokenList;
    }

    public static void main(String[] args) {
        RateLimiter rateLimiter = new RateLimiter();

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                        System.out.println("Access Token count = "+ rateLimiter.getBulkTokens("user1", "serviceA").size());
                    } catch (AccessDeniedException | InterruptedException ex) {
                        System.out.println("Exception "+ ex);
                    }
                }
            }
        });

        t1.start();
    }
}
