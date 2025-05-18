
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class TTLCache<T> {
    ConcurrentSkipListMap<Instant, ConcurrentLinkedQueue<String>> ttlMap = new ConcurrentSkipListMap<>();
    ConcurrentHashMap<String, CacheValue<T>> cacheMap = new ConcurrentHashMap<>();
    Duration TTL = Duration.ofSeconds(10);

    public TTLCache() {
        ttlMap.clear();
        cacheMap.clear();
        startEvictor();
    }

    private void startEvictor() {
        CacheEvictor evictor = new CacheEvictor();
        Thread t1 = new Thread(evictor);
        t1.start();
    }

    class CacheValue<T> {
        public T value;
        public Instant expiryTime;
    
        public CacheValue(T value, Instant expiryTime) {
            this.value = value;
            this.expiryTime = expiryTime;
        }
    }

    class CacheEvictor implements Runnable {

        @Override
        public void run() {
            while (true) { 
                ConcurrentNavigableMap<Instant, ConcurrentLinkedQueue<String>> expired = ttlMap.headMap(Instant.now());
                List<Instant> toBeRemoved = new ArrayList<>();
                for (Map.Entry<Instant, ConcurrentLinkedQueue<String>> entry : expired.entrySet()) {
                    entry.getValue().stream().forEach(value -> cacheMap.remove(value));
                    toBeRemoved.add(entry.getKey());
                }
                toBeRemoved.stream().forEach(key -> ttlMap.remove(key));
                try {
                    System.out.println("Thread going to sleep at Instant "+ Instant.now());
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }
            }
        }

    }

    public void put(String id, T value) {
        if (cacheMap.contains(id)) {
            ttlMap.get(cacheMap.get(id).expiryTime).remove(id);
        }

        Instant expiryTime = Instant.now().plus(TTL);
        cacheMap.put(id, new CacheValue<>(value, expiryTime));
        ttlMap.putIfAbsent(expiryTime, new ConcurrentLinkedQueue<>());
        ttlMap.get(expiryTime).add(id);
        System.out.println("Key "+id+" inserted to be expired at "+expiryTime);
    }

    public T get(String id) throws NoSuchElementException{
        if (!cacheMap.containsKey(id)) {
            throw new NoSuchElementException("Key "+id+" does not exist in cache.");
        }
        return cacheMap.get(id).value;
    }

    public static void main(String[] args) {
        TTLCache<String> cache = new TTLCache<>();

        cache.put("A", "Apple");
        cache.put("B", "Ball");

        System.out.println(cache.get("A"));
        try {
            Thread.sleep(Duration.ofSeconds(12));
        } catch (InterruptedException ex) {
        }
        System.out.println(cache.get("A"));
    }
}
