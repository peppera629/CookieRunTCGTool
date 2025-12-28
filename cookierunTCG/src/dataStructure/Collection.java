package dataStructure;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import util.CardUtil;
import util.Config;

public class Collection {
    private List<Map<String, List<Integer>>> collection; // Map of card ID to count
    private Map<String, Integer> change; // Current collection change compared to last save
    private static Collection instance;
    private static List<String> collection_files;
    private static final String COLLECTION_FILE_BASE = "collection/collection";

    private Collection() {
        collection = new ArrayList<>();
        collection_files = new ArrayList<>();
        change = new HashMap<>();
        for (String lang : Config.ALL_CARD_LANGUAGES) {
            String filePath = COLLECTION_FILE_BASE + "_" + lang + ".txt";
            collection_files.add(filePath);
            collection.add(new HashMap<>());
        }
        System.out.println("Collection files to load: ");
        for (String f : collection_files) {
            System.out.println(f);
        }
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
        change.clear();
        for (int i = 0; i < collection_files.size(); i++) {
            collection.add(new HashMap<>());
            File file = new File(collection_files.get(i));
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
                        collection.get(i).put(cardId, counts);
                    }
                }
                System.out.println("Collection for language " + Config.ALL_CARD_LANGUAGES[Config.COLLECTION_LANGUAGE_INDICES[i]] + " loaded from " + collection_files.get(i));
            } catch (IOException e) {
            e.printStackTrace();
            }
        }
    }

    public void saveCollection() {
        for (int i = 0; i < collection_files.size(); i++) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(collection_files.get(i)))) {
                collection.get(i).entrySet().stream()
                .sorted(Map.Entry.comparingByKey()) // Sort by card ID (key)
                .forEach(entry -> {
                    try {
                        List<Integer> counts = entry.getValue();
                        StringBuilder countsStr = new StringBuilder();
                        for (int j = 0; j < counts.size(); j++) {
                            countsStr.append(counts.get(j));
                            if (j < counts.size() - 1) {
                                countsStr.append(";");
                            }
                        }
                        bw.write(entry.getKey() + "," + countsStr.toString());
                        bw.newLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                System.out.println("Collection saved to " + collection_files.get(i));
                change.clear(); // Clear the change map after saving
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Get owned count for a specific card variant
    public int getCardOwnedCount(int langIndex, String cardId, int variantIndex) {
        try {
            List<Integer> counts = collection.get(langIndex).get(cardId);
            if (counts == null || variantIndex < 0 || variantIndex >= counts.size()) {
                return 0;
            }
            return counts.get(variantIndex);
        } catch (IndexOutOfBoundsException e) {
            return 0;
        }
    }

    public int getCardTotalOwnedCount(String cardId, boolean legalityConstraint) {
        int total = 0;
        for (int langIndex = 0; langIndex < collection.size(); langIndex++) {
            List<Integer> counts = collection.get(langIndex).get(cardId);
            if (counts == null) {
                continue;
            }
            
            for (int count : counts) {
                if (!legalityConstraint || Arrays.asList(Config.LEGAL_LANGUAGES).contains(Config.ALL_CARD_LANGUAGES[Config.COLLECTION_LANGUAGE_INDICES[langIndex]])) {
                    total += count;
                }
            }
        }
        return total;
    }

    public int getCardTotalChangeCount(String cardId) {
        Integer count = change.get(cardId);
        if (count == null) {
            return 0;
        }
        return count;
    }

    public int getCardOwnedCount(int langIndex, String packId, CardUtil.CardRarity rarity, CardUtil.CardColor color, CardUtil.CardType type, boolean countMode) {
        int total = 0;
        if (langIndex == -1) { // Aggregate across all languages
            for (int i = 0; i < collection.size(); i++) {
                total += getCardOwnedCount(i, packId, rarity, color, type, countMode);
            }
            return total;
        }
        for (Map.Entry<String, List<Integer>> entry : collection.get(langIndex).entrySet()) {
            String cardId = entry.getKey();
            Card card = CardList.getInstance().getCardById(cardId);
            List<CardUtil.CardRarity> variants = Arrays.asList(card.getVariants());
            try {
                boolean hasSpecifiedVariant = (rarity != null && rarity.getValue() >= 6 && variants.contains(rarity)); // Assumes that a Secret Rare is requested
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
                    ((rarity == null || (rarity.getValue() >= 6 ? hasSpecifiedVariant : card.getRarity() == rarity))) && 
                    (color == null || card.getColor() == color) &&
                    (type == null || card.getType() == type)) {
                    if (countMode) {// countMode: true = num. of copies, false = owned or not
                        if (rarity != null && hasSpecifiedVariant && variantIndex != -1) {
                            if (variantIndex >= entry.getValue().size()) {
                                continue;
                                //System.out.println("Index out of bounds for counts list, assuming 0");
                            } else {
                                total += entry.getValue().get(variantIndex);
                            }
                        } else {
                            for (int count : entry.getValue()) {
                                total += count;
                            }
                        }
                    } else {
                        if (rarity != null && hasSpecifiedVariant && variantIndex != -1) {
                            if (variantIndex >= entry.getValue().size()) {
                                continue;
                                //System.out.println("Index out of bounds for counts list, assuming 0");
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
                e.printStackTrace();
            }
        }
        return total;
    }

    public void setCardOwnedCount(int langIndex, String cardId, int variantIndex, int count) {
        List<Integer> counts = collection.get(langIndex).getOrDefault(cardId, new ArrayList<>());
        while (counts.size() <= variantIndex) {
            counts.add(0); // Initialize missing variants with 0
        }
        if (count <= 0) {
            counts.set(variantIndex, 0);
        } else {
            counts.set(variantIndex, count);
        }
        collection.get(langIndex).put(cardId, counts);
    }

    public void setCardChangeCount(String cardId, int changeCount) {
        change.put(cardId, changeCount);
    }

    public Map<String, List<Integer>> getCollection(int langIndex) {
        return collection.get(langIndex);
    }

    public List<Map<String, List<Integer>>> getCollection() {
        return collection;
    }

    public Map<String, Integer> getChange() {
        return change;
    }
}
