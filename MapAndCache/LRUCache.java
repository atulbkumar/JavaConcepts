import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Simple, thread-safe LRU (least-recently-used) cache implemented on top of
 * LinkedHashMap. This class demonstrates a compact way to get LRU behavior
 * by using LinkedHashMap's access-order mode and overriding
 * removeEldestEntry.
 *
 * Key points:
 * - The LinkedHashMap is created with accessOrder=true so that get/put
 *   move entries to the end of the internal order when accessed.
 * - removeEldestEntry is overridden to evict the eldest entry when the
 *   cache size exceeds the configured capacity.
 * - Public methods are synchronized for simple thread-safety. For higher
 *   concurrency, consider using a segmented cache or ConcurrentLinkedHashMap.
 */
public class LRUCache<K, V> {
	// Maximum number of entries the cache will hold
	private final int capacity;

	/*
	 * LinkedHashMap with accessOrder=true maintains entries from least
	 * recently accessed to most recently accessed. We override
	 * removeEldestEntry to drop the oldest entry when size > capacity.
	 */
	private final LinkedHashMap<K, V> map;

	/**
	 * Create an LRU cache with the given capacity.
	 *
	 * @param capacity maximum number of entries (must be > 0)
	 */
	public LRUCache(int capacity) {
		if (capacity <= 0) throw new IllegalArgumentException("capacity must be > 0");
		this.capacity = capacity;

		// initialCapacity is a hint; 0.75f is standard load factor.
		// accessOrder=true enables LRU ordering.
		this.map = new LinkedHashMap<K, V>(capacity, 0.75f, true) {
			private static final long serialVersionUID = 1L;

			// Called by LinkedHashMap after each put; return true to remove
			// the eldest entry. Here we remove when size exceeds capacity.
			@Override
			protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
				return size() > LRUCache.this.capacity;
			}
		};
	}

	/**
	 * Return the value for the key, or null if absent. Accessing the key
	 * updates its recency (moves it to most-recently-used).
	 */
	public synchronized V get(K key) {
		return map.get(key);
	}

	/** Insert or update a value. This also updates recency for the key. */
	public synchronized void put(K key, V value) {
		map.put(key, value);
	}

	/** Return the current number of entries in the cache. */
	public synchronized int size() {
		return map.size();
	}

	/** Check whether the cache contains a mapping for the given key. */
	public synchronized boolean containsKey(K key) {
		return map.containsKey(key);
	}

	/** Return a short string representation showing entries from least- to most-recent. */
	@Override
	public synchronized String toString() {
		return map.toString();
	}

	// --------------------------------------------------
	// Small demo (not a unit test) to show eviction behavior
	// --------------------------------------------------
	public static void main(String[] args) {
		LRUCache<Integer, Integer> cache = new LRUCache<>(2);
		cache.put(1, 1); // cache: {1=1}
		cache.put(2, 2); // cache: {1=1, 2=2}
		System.out.println(cache); // prints {1=1, 2=2}

		cache.get(1);      // access key 1 -> now most-recent; order {2=2, 1=1}
		cache.put(3, 3);   // insert 3, capacity exceeded -> evict eldest (2)
		System.out.println(cache); // expected {1=1, 3=3}

		cache.get(1);      // access 1 again
		cache.put(4, 4);   // evict eldest (3)
		System.out.println(cache); // expected {1=1, 4=4}
	}
}