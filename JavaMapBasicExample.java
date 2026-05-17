import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class JavaMapBasicExample{
    public static void main(String[] args) {
        // Create a HashMap to store key-value pairs
        Map<String, Integer> map = new HashMap<>();

        // Add some key-value pairs to the map
        map.put("Apple", 1);
        map.put("Banana", 2);
        map.put("Orange", 3);

        // Retrieve a value using its key
        int value = map.get("Banana");
        System.out.println("Value for key 'Banana': " + value);

        // Check if a key exists in the map
        boolean hasKey = map.containsKey("Apple");
        System.out.println("Does the map contain key 'Apple'? " + hasKey);

        // Remove a key-value pair from the map
        map.remove("Orange");

        // Iterate over the map keys using keySet()
        System.out.println("Keys in the map:");
        Set<String> keys = map.keySet();
        for (String key : keys) {
            System.out.println(key);
        }

        // Iterate over the entries in the map
        System.out.println("Entries in the map:");
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}