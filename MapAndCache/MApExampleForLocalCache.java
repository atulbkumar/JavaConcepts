import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Example program showing several ways to use a Map as a simple local cache.
 *
 * The goal is educational: each method shows a different trade-off.
 * - Simple HashMap (no eviction)
 * - LinkedHashMap LRU (automatic eviction)
 * - ConcurrentHashMap with computeIfAbsent (thread-safe lazy load)
 * - Tiny TTL cache (per-entry expiration)
 *
 * Read the inline comments for explanations and caveats.
 */
public class MApExampleForLocalCache {

	// ------------------------------------------------------------------
	// 1) Simple cache with HashMap (no eviction)
	// ------------------------------------------------------------------
	// Very simple: put values and get them back. No eviction or concurrency.
	// Use only when you have strong control over lifetime and size.
	public static void simpleHashMapDemo() {
		Map<Integer, String> cache = new HashMap<>();
		cache.put(1, "one");
		cache.put(2, "two");
		System.out.println("simpleHashMap: " + cache);
		// Caveat: this will grow forever if you keep inserting.
	}

	// ------------------------------------------------------------------
	// 2) LRU cache using LinkedHashMap
	// ------------------------------------------------------------------
	// LinkedHashMap supports access-order iteration. By overriding
	// removeEldestEntry we can evict the oldest entry when size exceeds
	// a capacity — this yields an LRU cache.
	public static void lruLinkedHashMapDemo() {
		final int capacity = 2;
		Map<Integer, String> lru = new LinkedHashMap<Integer, String>(capacity, 0.75f, true) {
			private static final long serialVersionUID = 1L;

			@Override
			protected boolean removeEldestEntry(Map.Entry<Integer, String> eldest) {
				return size() > capacity; // evict when size > capacity
			}
		};

		lru.put(1, "one"); // order: 1
		lru.put(2, "two"); // order: 1,2
		System.out.println("lru before access: " + lru);

		lru.get(1); // access 1 -> order becomes 2,1 (1 is MRU)
		lru.put(3, "three"); // inserting 3 evicts eldest (which is 2)
		System.out.println("lru after eviction (expect 1 and 3): " + lru);
	}

	// ------------------------------------------------------------------
	// 3) Thread-safe lazy-loading cache with ConcurrentHashMap
	// ------------------------------------------------------------------
	// computeIfAbsent allows atomically loading a value only once when
	// missing. This pattern is useful for simple memoization without
	// external synchronization.
	public static void concurrentComputeIfAbsentDemo() {
		ConcurrentHashMap<Integer, String> cache = new ConcurrentHashMap<>();

		// loader simulating an expensive operation
		java.util.function.Function<Integer, String> loader = key -> {
			try { Thread.sleep(50); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
			return "val-" + key;
		};

		// computeIfAbsent is thread-safe: if multiple threads request the
		// same key, only one will run the loader function.
		String v1 = cache.computeIfAbsent(10, loader);
		String v2 = cache.computeIfAbsent(10, loader);
		System.out.println("concurrentComputeIfAbsent: v1=" + v1 + ", v2=" + v2 + ", map=" + cache);
	}

	// ------------------------------------------------------------------
	// 4) Very small TTL cache example
	// ------------------------------------------------------------------
	// This cache stores an expiry time with each value and returns null
	// when the entry is expired. It performs cleanup lazily on get.
	// Note: For production use prefer a well-tested library; this is just
	// an educational minimal example.
	public static class TTLCache<K, V> {
		private static class Entry<V> {
			final V value;
			final long expiresAtMillis;

			Entry(V value, long expiresAtMillis) {
				this.value = value;
				this.expiresAtMillis = expiresAtMillis;
			}
		}

		private final Map<K, Entry<V>> map = new HashMap<>();

		// Put with TTL in milliseconds
		public synchronized void put(K key, V value, long ttlMillis) {
			long expiresAt = System.currentTimeMillis() + ttlMillis;
			map.put(key, new Entry<>(value, expiresAt));
		}

		// Get value or null if absent/expired. Expired entries are removed lazily.
		public synchronized V get(K key) {
			Entry<V> e = map.get(key);
			if (e == null) return null;
			if (System.currentTimeMillis() > e.expiresAtMillis) {
				map.remove(key); // remove expired
				return null;
			}
			return e.value;
		}

		public synchronized int size() { return map.size(); }
	}

	public static void ttlDemo() throws InterruptedException {
		TTLCache<String, String> cache = new TTLCache<>();
		cache.put("a", "apple", TimeUnit.SECONDS.toMillis(1)); // 1 second TTL
		System.out.println("ttl immediate: " + cache.get("a") + ", size=" + cache.size());
		Thread.sleep(1200);
		System.out.println("ttl after expiry: " + cache.get("a") + ", size=" + cache.size());
	}

	// ------------------------------------------------------------------
	// Main: run the small demos
	// ------------------------------------------------------------------
	public static void main(String[] args) throws InterruptedException {
		System.out.println("--- Simple HashMap demo ---");
		simpleHashMapDemo();

		System.out.println("--- LRU LinkedHashMap demo ---");
		lruLinkedHashMapDemo();

		System.out.println("--- Concurrent computeIfAbsent demo ---");
		concurrentComputeIfAbsentDemo();

		System.out.println("--- TTL cache demo ---");
		ttlDemo();
	}
}
