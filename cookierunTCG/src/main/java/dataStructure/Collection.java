package dataStructure;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import util.CardUtil;
import util.Config;
import util.AppPaths;

public class Collection {
    private List<Map<String, List<Integer>>> collection; // Map of card ID to count
    private Map<String, Integer> change; // Current collection change compared to last save
    private Map<String, Boolean> countedCards = new HashMap<>();
    private int currentPackCompletionStatus;
    private static Collection instance;
    private static List<String> collection_files;
    private static final String COLLECTION_FILE_BASE = AppPaths.userDataDir().resolve("collection/collection").toString();

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
                System.out.println("Collection file not found: " + file.getAbsolutePath() + " (skipping)");
                continue;
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
            
            boolean langIsLegal = !legalityConstraint || Arrays.asList(Config.LEGAL_LANGUAGES).contains(Config.ALL_CARD_LANGUAGES[langIndex]);
            if (!langIsLegal) {
                continue;
            }

            for (int count : counts) {
                total += count;
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

    public int[] getTotalCollectionChange() {
        int positiveChange = 0;
        int negativeChange = 0;
        for (Integer changeCount : change.values()) {
            if (changeCount > 0) {
                positiveChange += changeCount;
            } else {
                negativeChange += changeCount;
            }
        }
        return new int[]{positiveChange, negativeChange};
    }

    public int getCardOwnedCount(int langIndex, String packId, CardUtil.CardRarity rarity, CardUtil.CardColor color, CardUtil.CardType type, boolean countMode) {
        int total = 0;
        if (langIndex == -1) { // Aggregate across all languages
            countedCards.clear(); 
            for (int i = 0; i < collection.size(); i++) {
                getCardOwnedCount(i, packId, rarity, color, type, countMode);
            }
            return countedCards.size();
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
                                    if (countedCards.getOrDefault(cardId, false)) {
                                        continue;
                                    } else {
                                        countedCards.put(cardId, true);
                                    }
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
                            if (owned) {
                                total += 1;
                                if (countedCards.getOrDefault(cardId, false)) {
                                    continue;
                                } else {
                                    countedCards.put(cardId, true);
                                }
                            }
                        }
                    }
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        return total;
    }

    public int getPackCompletion(int langIndex, String packId) { // Incomplete for now
        if (langIndex == -1) {
            List<Integer> completionStatusList = new ArrayList<>();
            for (int i = 0; i < collection.size(); i++) {
                int status = getPackCompletion(i, packId);
                completionStatusList.add(status);
            }
            for (int i = 0; i < completionStatusList.size(); i++) {
                System.out.println("Pack completion status for language " + Config.ALL_CARD_LANGUAGES[Config.COLLECTION_LANGUAGE_INDICES[i]] + ": " + completionStatusList.get(i));
            }
            return completionStatusList.stream().max(Integer::compareTo).orElse(0);
        }
        currentPackCompletionStatus = 3; // 0 = Incomplete, 1 = Complete, 2 = Master Set, 3 = Grandmaster Set
        int cardTally = 0;
        for (Map.Entry<String, List<Integer>> entry : collection.get(langIndex).entrySet()) {
            String cardId = entry.getKey();
            Card card = CardList.getInstance().getCardById(cardId);
            System.out.println(card.getName());
            if (card != null && ((packId == null && card.getPack() != "P") || card.getPack().equals(packId))) {
                cardTally++;
                if (card.getCount() > 0) {
                    boolean masterEligible = false;
                    boolean grandmasterEligible = false;
                    List<Integer> counts = entry.getValue();
                    for (int i = 0; i < counts.size(); i++) {
                        int count = counts.get(i);
                        if (count <= 0) {
                            if (card.getVariants()[i].getValue() == 5) { // Promo (only invalidates Grandmaster completion)
                                currentPackCompletionStatus = Math.min(currentPackCompletionStatus, 2); // Downgrade to at most Master Set
                                System.out.println(card.getName() + " - 2");
                            } else {
                                currentPackCompletionStatus = Math.min(currentPackCompletionStatus, 1); // Downgrade to at most Complete
                                System.out.println(card.getName() + " - 1");
                            }
                        }
                    }
                } else {
                    currentPackCompletionStatus = 0; // Incomplete
                    System.out.println(card.getName() + " - 0");
                    break;
                }
            }
        }
        return ((cardTally == 0) ? 0 : currentPackCompletionStatus);
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
