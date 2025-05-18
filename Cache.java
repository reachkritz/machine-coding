import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

interface CacheStorage<T> {
    public T getById(String id);
    public void put(String id, T value);
    public void delete(String id);
    public boolean isCacheFull();
}

class MapCacheStorage<T> implements CacheStorage<T> {
    private Map<String, T> storage;
    private Integer maxSize;

    public MapCacheStorage() {
        storage = new HashMap<>();
        maxSize = 1000;
    }

    @Override
    public T getById(String id){
        return storage.getOrDefault(id, null);
    }

    @Override
    public void put(String id, T value) {
        storage.put(id, value);
    }

    @Override
    public void delete(String id) {
        storage.remove(id);
    }

    @Override
    public boolean isCacheFull() {
        return storage.size() == maxSize;
    }
}
enum Action {
    READ,
    WRITE,
    DELETE
}
interface EvictionStrategy {
    public void recordAction(Action action, String id, Timestamp t);
    public String getIdToEvict();
}

class LRUEvictionStrategy implements EvictionStrategy {
    Map<String, Timestamp> actionStorage;

    @Override
    public void recordAction(Action action, String id, Timestamp t) {
        if (action == Action.DELETE){
            actionStorage.remove(id);
        } else {
            actionStorage.put(id, t);
        }
    }

    @Override
    public String getIdToEvict() {
       String id = "";
       Timestamp minTimestamp = Timestamp.from(Instant.now());
       for (Map.Entry<String, Timestamp> entry : actionStorage.entrySet()) {
        if (entry.getValue().before(minTimestamp)){
            minTimestamp = entry.getValue();
            id = entry.getKey();
        }
       }
       return id;
    }

}

public class Cache<T> {
    CacheStorage<T> storage;
    EvictionStrategy evictionStrategy;

    public Cache() {
        storage = new MapCacheStorage<T>();
        evictionStrategy = new LRUEvictionStrategy();
    }

    public Cache(EvictionStrategy strategy) {
        storage = new MapCacheStorage<T>();
        evictionStrategy = strategy;
    }

    public T getById (String id) throws ElementNotFoundException {
        T value = storage.getById(id);
        if (value == null) throw new ElementNotFoundException("Key "+id+" not found in cache");
        evictionStrategy.recordAction(Action.READ, id, Timestamp.from(Instant.now()));
        return value;
    }

    public void put (String id, T value) {
        if (storage.isCacheFull()) {
            String idToEvict = evictionStrategy.getIdToEvict();
            evictionStrategy.recordAction(Action.DELETE, idToEvict, Timestamp.from(Instant.now()));
            storage.delete(evictionStrategy.getIdToEvict());
        }
        evictionStrategy.recordAction(Action.WRITE, id, Timestamp.from(Instant.now()));
        storage.put(id, value);
    }
}
