package dataStructure;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Collection {
    private Map<String, Integer> collection; // Map of card ID to count
    private static Collection instance;
    private static final String COLLECTION_FILE = "collection/collection.txt";

    private Collection() {
        collection = new HashMap<>();
        loadCollection();
    }

    public static Collection getInstance() {
        if (instance == null) {
            instance = new Collection();
        }
        return instance;
    }
    
    public void loadCollection() {
        collection.clear();
        File file = new File(COLLECTION_FILE);
        if (!file.exists()) {
            System.out.println("Collection file not found, starting with empty collection");
            return; // No collection file found, start with empty collection
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) { // Assuming no variants
                    String cardId = parts[0].trim();
                    int count = Integer.parseInt(parts[1].trim());
                    collection.put(cardId, count);
                }
            }
            System.out.println("Collection loaded from " + COLLECTION_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveCollection() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(COLLECTION_FILE))) {
            for (Map.Entry<String, Integer> entry : collection.entrySet()) {
                bw.write(entry.getKey() + "," + entry.getValue());
                bw.newLine();
            }
            System.out.println("Collection saved to " + COLLECTION_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getCardOwnedCount(String cardId) {
        return collection.getOrDefault(cardId, 0);
    }

    public void setCardOwnedCount(String cardId, int count) {
        if (count <= 0) {
            collection.remove(cardId);
        } else {
            collection.put(cardId, count);
        }
    }

    public Map<String, Integer> getCollection() {
        return collection;
    }
}
