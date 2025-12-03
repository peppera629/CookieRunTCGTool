package dataStructure;

import java.io.*;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import util.CardUtil;
import dataStructure.*;

public class Collection {
    private Map<String, List<Integer>> collection; // Map of card ID to count
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
                if (parts.length == 2) {
                    String cardId = parts[0].trim();
                    String[] variantCounts = parts[1].trim().split(";");
                    List<Integer> counts = new ArrayList<>();
                    for (String countStr : variantCounts) {
                        counts.add(Integer.parseInt(countStr.trim()));
                    }
                    collection.put(cardId, counts);
                }
            }
            System.out.println("Collection loaded from " + COLLECTION_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveCollection() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(COLLECTION_FILE))) {
            collection.entrySet().stream()
            .sorted(Map.Entry.comparingByKey()) // Sort by card ID (key)
            .forEach(entry -> {
                try {
                    List<Integer> counts = entry.getValue();
                    StringBuilder countsStr = new StringBuilder();
                    for (int i = 0; i < counts.size(); i++) {
                        countsStr.append(counts.get(i));
                        if (i < counts.size() - 1) {
                            countsStr.append(";");
                        }
                    }
                    bw.write(entry.getKey() + "," + countsStr.toString());
                    bw.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            System.out.println("Collection saved to " + COLLECTION_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getCardOwnedCount(String cardId, int variantIndex) {
        List<Integer> counts = collection.get(cardId);
        if (counts == null || variantIndex < 0 || variantIndex >= counts.size()) {
            return 0;
        }
        return counts.get(variantIndex);
    }

    public int getCardTotalOwnedCount(String cardId) {
        List<Integer> counts = collection.get(cardId);
        if (counts == null) {
            return 0;
        }
        int total = 0;
        for (int count : counts) {
            total += count;
        }
        return total;
    }

    public int getCardOwnedCount(String packId, CardUtil.CardRarity rarity, boolean countMode) {
        int total = 0;
        for (Map.Entry<String, List<Integer>> entry : collection.entrySet()) {
            String cardId = entry.getKey();
            Card card = CardList.getInstance().getCardById(cardId);
            List<CardUtil.CardRarity> variants = Arrays.asList(card.getVariants());
            try {
                boolean hasSpecifiedVariant = (rarity.getValue() >= 6 && variants.contains(rarity)); // Assumes that a Secret Rare is requested
                int variantIndex = -1;
                if (hasSpecifiedVariant) {
                    for (int i = 0; i < variants.size(); i++) {
                        if (variants.get(i) == rarity) {
                            variantIndex = i;
                            break;
                        }
                    }
                    if (variantIndex == -1) {
                        continue; // Specified Secret Rare variant not found
                    }
                }
                if (card != null && 
                    ((packId == null && card.getPack() != "P") || card.getPack().equals(packId)) &&
                    (rarity.getValue() >= 6 ? hasSpecifiedVariant : card.getRarity() == rarity)) {
                    if (countMode) {// countMode: true = num. of copies, false = owned or not
                        if (hasSpecifiedVariant && variantIndex != -1) {
                            if (variantIndex >= entry.getValue().size()) {
                                System.out.println("Index out of bounds for counts list, assuming 0");
                            } else {
                                total += entry.getValue().get(variantIndex);
                            }
                        } else {
                            for (int count : entry.getValue()) {
                                total += count;
                            }
                        }
                    } else {
                        if (hasSpecifiedVariant && variantIndex != -1) {
                            if (variantIndex >= entry.getValue().size()) {
                                System.out.println("Index out of bounds for counts list, assuming 0");
                            } else {
                                if (entry.getValue().get(variantIndex) > 0) {
                                    total += 1;
                                }
                            }
                        } else {
                            boolean owned = false;
                            for (int count : entry.getValue()) {
                                if (count > 0) {
                                    owned = true;
                                    break;
                                }
                            }
                            total += (owned ? 1 : 0);
                        }
                    }
                }
            } catch (NullPointerException e) {
                // Some parameter is null, skip
            }
        }
        return total;
    }

    public void setCardOwnedCount(String cardId, int variantIndex, int count) {
        List<Integer> counts = collection.getOrDefault(cardId, new ArrayList<>());
        while (counts.size() <= variantIndex) {
            counts.add(0); // Initialize missing variants with 0
        }
        if (count <= 0) {
            counts.set(variantIndex, 0);
        } else {
            counts.set(variantIndex, count);
        }
        collection.put(cardId, counts);
    }

    public Map<String, List<Integer>> getCollection() {
        return collection;
    }
}
