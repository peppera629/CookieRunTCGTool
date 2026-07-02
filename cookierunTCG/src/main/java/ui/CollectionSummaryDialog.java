package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import javax.swing.JPanel;

import dataStructure.CardList;
import dataStructure.Collection;
import util.CardUtil;
import util.Config;
import util.AppPaths;

// Dialog box

public class CollectionSummaryDialog {
    private JFrame frame;
    private int num, count, total;

    /**
     * Launch the application.
     */
    public void show() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    initialize(false);
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initialize(boolean choiceMode) {
        frame = new JFrame(CardUtil.getTranslation("collection.summary"));
        frame.setSize(800, 800);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(MainUI.CRnormal);

        int[] totalOwnedPerPack = new int[CardUtil.CardPack.size()];
        int[] totalOwnedCountPerPack = new int[CardUtil.CardPack.size()];
        int[] totalPerPack = new int[CardUtil.CardPack.size()];

        int[] totalOwnedPerPackSec = new int[CardUtil.CardPack.size()-1];
        int[] totalOwnedCountPerPackSec = new int[CardUtil.CardPack.size()-1];
        int[] totalPerPackSec = new int[CardUtil.CardPack.size()-1];

        int[] totalOwnedPerRarity = new int[CardUtil.RARITY_MAX];
        int[] totalOwnedCountPerRarity = new int[CardUtil.RARITY_MAX];
        int[] totalPerRarity = new int[CardUtil.RARITY_MAX];

        int[] totalOwnedPerColor = new int[CardUtil.COLOR_MAX];
        int[] totalPerColor = new int[CardUtil.COLOR_MAX];

        int[] totalOwnedPerType = new int[CardUtil.TYPE_MAX];
        int[] totalPerType = new int[CardUtil.TYPE_MAX];

        int cardsOwnedAll = 0; // sum of totalOwnedPerPack (excluding P)
        int cardsOwnedCountAll = 0; // sum of totalOwnedCountPerPack (excluding P)
        int cardsAll = 0; // sum of totalPerPack (excluding P)
        
        // Fill in total owned per pack, total per pack
        for (int i = 0; i < CardUtil.RARITY_MAX; i++) {
            for (int j = 0; j < CardUtil.CardPack.size()-1; j++) {
                num = (i == 5 ? totalOwnedPerPack[j] : Collection.getInstance().getCardOwnedCount(-1, CardUtil.CardPack.get(j), CardUtil.CardRarity.fromValue(i), null, null, false));
                count = (Collection.getInstance().getCardOwnedCount(-1, CardUtil.CardPack.get(j), CardUtil.CardRarity.fromValue(i), null, null, true));
                total = (i == 5 ? totalPerPack[j] : CardList.getInstance().getCardCountByCondition(CardUtil.CardPack.get(j), CardUtil.CardRarity.fromValue(i), null, null, Config.SHOW_ONLY_LEGAL_IN_COLLECTION));
                
                if (i != 5) {
                    totalOwnedPerPack[j] += num;
                    
                    totalPerPack[j] += total;
                }
                totalOwnedCountPerPack[j] += count;
            }
        }

        System.out.println(totalOwnedCountPerPack[0]);

        // Fill in total owned per pack (Secret Rare), total per pack (Secret Rare)
        for (int i = 0; i < CardUtil.RARITY_MAX_ALL - CardUtil.RARITY_MAX; i++) {
            for (int j = 0; j < CardUtil.CardPack.size()-1; j++) {
                if (CardUtil.CardPack.get(j).contains("ST")) {
                    continue;
                }
                num = (i == 4 ? totalOwnedPerPackSec[j] : Collection.getInstance().getCardOwnedCount(-1, CardUtil.CardPack.get(j), CardUtil.CardRarity.fromValue(6+i), null, null, false));
                count = (i == 4 ? totalOwnedCountPerPackSec[j] : Collection.getInstance().getCardOwnedCount(-1, CardUtil.CardPack.get(j), CardUtil.CardRarity.fromValue(6+i), null, null, true));
                total = (i == 4 ? totalPerPackSec[j] : CardList.getInstance().getCardCountByCondition(CardUtil.CardPack.get(j), CardUtil.CardRarity.fromValue(6+i), null, null, Config.SHOW_ONLY_LEGAL_IN_COLLECTION));

                if (i != 4) {
                    totalOwnedPerPackSec[j] += num;
                    totalOwnedCountPerPackSec[j] += count;
                    totalPerPackSec[j] += total;
                }
            }
        }

        // Fill in total owned per rarity, owned count per rarity, total per rarity
        for (int i = 0; i < CardUtil.RARITY_MAX; i++) {
            totalOwnedPerRarity[i] = Collection.getInstance().getCardOwnedCount(-1, null, CardUtil.CardRarity.fromValue(i), null, null, false);
            totalOwnedCountPerRarity[i] = Collection.getInstance().getCardOwnedCount(-1, null, CardUtil.CardRarity.fromValue(i), null, null, true);
            totalPerRarity[i] = CardList.getInstance().getCardCountByCondition(null, CardUtil.CardRarity.fromValue(i), null, null, Config.SHOW_ONLY_LEGAL_IN_COLLECTION);
        }
        // Fill in total owned per color, total per color
        for (int i = 0; i < CardUtil.COLOR_MAX; i++) {
            totalOwnedPerColor[i] = Collection.getInstance().getCardOwnedCount(-1, null, null, CardUtil.CardColor.fromValue(i), null, false);
            totalPerColor[i] = CardList.getInstance().getCardCountByCondition(null, null, CardUtil.CardColor.fromValue(i), null, Config.SHOW_ONLY_LEGAL_IN_COLLECTION);
        }
        // Fill in total owned per type, total per type
        for (int i = 0; i < CardUtil.TYPE_MAX; i++) {
            totalOwnedPerType[i] = Collection.getInstance().getCardOwnedCount(-1, null, null, null, CardUtil.CardType.values()[i], false);
            totalPerType[i] = CardList.getInstance().getCardCountByCondition(null, null, null, CardUtil.CardType.values()[i], Config.SHOW_ONLY_LEGAL_IN_COLLECTION);
        }

        for (int i = 0; i < CardUtil.CardPack.size() - 1; i++) {
            cardsOwnedAll += totalOwnedPerPack[i];
            cardsOwnedCountAll += totalOwnedCountPerPack[i];
            cardsAll += totalPerPack[i];
        }

        // Main tab (total owned count, total owned count by rarity) ========================
        // Each row: label, owned, total, percentage, count

        JPanel mainTab = new JPanel();
        mainTab.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        mainTab.setLayout(new GridBagLayout());
        tabbedPane.addTab(CardUtil.getTranslation("collection.summary.overview"), mainTab);
        GridBagConstraints gbc_mainTab = new GridBagConstraints();

        // Labels (leftmost)
        gbc_mainTab.fill = GridBagConstraints.BOTH;
        gbc_mainTab.weightx = 0.5;
        gbc_mainTab.weighty = 1;
        gbc_mainTab.gridx = 0;
        gbc_mainTab.gridy = 1;
        JLabel totalOwnedLabel = new JLabel(CardUtil.getTranslation("collection.summary.total") + " ", JLabel.RIGHT);
        totalOwnedLabel.setFont(MainUI.CRnormal);
        totalOwnedLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        mainTab.add(totalOwnedLabel, gbc_mainTab);

        gbc_mainTab.gridy = 2;
        for (int i = 0; i < 6; i++) {
            //System.out.println(AppPaths.dataDir().resolve("icons_rarity/24px/" + CardUtil.CardRarity.fromValue(i).getName() + ".png"));
            JLabel label = new JLabel("<html><img src=\"file:" + new File(AppPaths.dataDir().resolve("icons_rarity/24px/" + CardUtil.CardRarity.fromValue(i).getName() + ".png").toString()).getAbsolutePath() + "\">&nbsp;</html>", JLabel.RIGHT);
            label.setAlignmentX(Component.RIGHT_ALIGNMENT);
            label.setFont(MainUI.CRnormal);
            mainTab.add(label, gbc_mainTab);
            gbc_mainTab.gridy++;
        }

        // Labels (top)
        gbc_mainTab.gridx = 1;
        gbc_mainTab.gridy = 0;
        gbc_mainTab.weightx = 1;
        gbc_mainTab.weighty = 0.5;
        String[] columnLabels = {CardUtil.getTranslation("collection.summary.owned"), CardUtil.getTranslation("collection.summary.ownedpercent"), CardUtil.getTranslation("collection.summary.count")};
        for (int i = 0; i < columnLabels.length; i++) {
            JLabel label = new JLabel(columnLabels[i], JLabel.CENTER);
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            label.setFont(MainUI.CRnormal);
            mainTab.add(label, gbc_mainTab);
            gbc_mainTab.gridx++;
        }

        // Data

        gbc_mainTab.weightx = 1;
        gbc_mainTab.gridx = 1;
        gbc_mainTab.gridy = 1;

        JLabel totalNumber = new JLabel(String.format("%,d", cardsOwnedAll) + " / " + String.format("%,d", cardsAll), JLabel.CENTER);
        totalNumber.setAlignmentX(Component.CENTER_ALIGNMENT);
        totalNumber.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        totalNumber.setFont(MainUI.CRboldEXLarge);
        mainTab.add(totalNumber, gbc_mainTab);

        gbc_mainTab.gridy = 2;
        for (int i = 0; i < CardUtil.RARITY_MAX; i++) {
            JLabel label;
            if (i == CardUtil.RARITY_MAX - 1) {
                gbc_mainTab.gridwidth = 2;
                label = new JLabel(String.format("%,d", totalOwnedPerRarity[i]), JLabel.CENTER);
            } else {
                gbc_mainTab.gridwidth = 1;
                label = new JLabel(String.format("%,d", totalOwnedPerRarity[i]) + " / " + String.format("%,d", totalPerRarity[i]), JLabel.CENTER);
            }
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            label.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
            label.setFont(MainUI.CRboldEXLarge);
            mainTab.add(label, gbc_mainTab);
            gbc_mainTab.gridy++;
        }

        // Percentage

        gbc_mainTab.weightx = 1;
        gbc_mainTab.gridx = 2;
        gbc_mainTab.gridy = 1;
        gbc_mainTab.gridwidth = 1;

        JLabel totalPercent = new JLabel(String.format("%.2f", Math.round((double) cardsOwnedAll * 100 / cardsAll * 100) / 100.0) + "%", JLabel.CENTER);
        totalPercent.setAlignmentX(Component.CENTER_ALIGNMENT);
        totalPercent.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        totalPercent.setFont(MainUI.CRboldEXLarge);
        mainTab.add(totalPercent, gbc_mainTab);

        gbc_mainTab.gridy = 2;
        for (int i = 0; i < CardUtil.RARITY_MAX - 1; i++) {
            JLabel label = new JLabel(String.format("%.2f", Math.round((double) totalOwnedPerRarity[i] * 100 / totalPerRarity[i] * 100) / 100.0) + "%", JLabel.CENTER);
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            label.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
            label.setFont(MainUI.CRboldEXLarge);
            mainTab.add(label, gbc_mainTab);
            gbc_mainTab.gridy++;
        }

        // Count

        gbc_mainTab.weightx = 1;
        gbc_mainTab.gridx = 3;
        gbc_mainTab.gridy = 1;

        JLabel totalCount = new JLabel(String.format("%,d", cardsOwnedCountAll + totalOwnedCountPerRarity[CardUtil.RARITY_MAX - 1]), JLabel.CENTER);
        totalCount.setAlignmentX(Component.CENTER_ALIGNMENT);
        totalCount.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        totalCount.setFont(MainUI.CRboldEXLarge);
        mainTab.add(totalCount, gbc_mainTab);

        gbc_mainTab.gridy = 2;
        for (int i = 0; i < CardUtil.RARITY_MAX; i++) {
            JLabel label = new JLabel(String.format("%,d", totalOwnedCountPerRarity[i]), JLabel.CENTER);
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            label.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
            label.setFont(MainUI.CRboldEXLarge);
            mainTab.add(label, gbc_mainTab);
            gbc_mainTab.gridy++;
        }

        gbc_mainTab.gridwidth = 3;
        gbc_mainTab.gridheight = CardUtil.RARITY_MAX + 1;
        gbc_mainTab.gridx = 1;
        gbc_mainTab.gridy = 1;
        JPanel tableBorderOverview = new JPanel();
        tableBorderOverview.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        tableBorderOverview.setOpaque(false);
        tableBorderOverview.setLayout(null);
        mainTab.add(tableBorderOverview, gbc_mainTab);
        mainTab.setComponentZOrder(tableBorderOverview, 0);

        // By Rarity tab =============================================

        JPanel byRarity = new JPanel();
        byRarity.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        byRarity.setLayout(new GridBagLayout());
        tabbedPane.addTab(CardUtil.getTranslation("collection.summary.rarity"), byRarity);
        GridBagConstraints gbc_byRarity = new GridBagConstraints();
        gbc_byRarity.fill = GridBagConstraints.BOTH;

        // Labels (left)

        gbc_byRarity.weightx = 0.5;
        gbc_byRarity.weighty = 1;
        gbc_byRarity.gridx = 0;
        gbc_byRarity.gridy = 1;
        
        for (int i = 0; i < CardUtil.CardPack.size(); i++) {
            JLabel label = new JLabel(CardUtil.CardPack.get(i) + " ", JLabel.RIGHT);
            label.setAlignmentX(Component.RIGHT_ALIGNMENT);
            label.setFont(MainUI.CRnormal);
            byRarity.add(label, gbc_byRarity);
            gbc_byRarity.gridy++;
        }

        // Labels (top)
        gbc_byRarity.gridx = 1;
        gbc_byRarity.gridy = 0;
        gbc_byRarity.weightx = 1;
        gbc_byRarity.weighty = 0.5;
        for (int i = 0; i < CardUtil.RARITY_MAX - 1; i++) {
            //System.out.println(AppPaths.dataDir().resolve("icons_rarity/24px/" + CardUtil.CardRarity.fromValue(i).getName() + ".png"));
            JLabel label = new JLabel("<html><img src=\"file:" + new File(AppPaths.dataDir().resolve("icons_rarity/24px/" + CardUtil.CardRarity.fromValue(i).getName() + ".png").toString()).getAbsolutePath() + "\"></html>", JLabel.CENTER);
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            label.setFont(MainUI.CRnormal);
            byRarity.add(label, gbc_byRarity);
            gbc_byRarity.gridx++;
        }

        JLabel label_total = new JLabel("Total", JLabel.CENTER);
        label_total.setAlignmentX(Component.CENTER_ALIGNMENT);
        label_total.setFont(MainUI.CRnormal);
        byRarity.add(label_total, gbc_byRarity);
        
        // Data
        gbc_byRarity.gridx = 1;
        gbc_byRarity.gridy = 0;
        /*for (int i = 0; i < CardUtil.RARITY_MAX; i++) {
            gbc_byRarity.gridy = 0;
            int countLocal = (i == 5 ? cardsOwnedAll : Collection.getInstance().getCardOwnedCount(-1, null, CardUtil.CardRarity.fromValue(i), null, null, false));
            int totalLocal = (i == 5 ? cardsAll : CardList.getInstance().getCardCountByCondition(null, CardUtil.CardRarity.fromValue(i), null, null));
            JLabel label = new JLabel(Integer.toString(countLocal) + " / " + Integer.toString(totalLocal), JLabel.CENTER);
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            label.setFont(MainUI.CRboldLarge);
            label.setOpaque(true);
            if (totalLocal == 0) {
                label.setBackground(Color.GRAY);
                label.setForeground(Color.LIGHT_GRAY);
            } else {
                if (countLocal >= totalLocal) {
                    label.setForeground(new Color(255, 226, 84));
                    label.setOpaque(true);
                    label.setBackground(new Color(191, 142, 0));
                } else {
                    label.setForeground(MainUI.foregroundColor);
                }
            }
            label.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
            byRarity.add(label, gbc_byRarity);
            gbc_byRarity.gridx++;
        }*/
        gbc_byRarity.gridx = 1;
        for (int i = 0; i < CardUtil.RARITY_MAX; i++) {
            gbc_byRarity.gridy = 1;
            for (int j = 0; j < CardUtil.CardPack.size()-1; j++) {
                int countLocal = (i == 5 ? totalOwnedPerPack[j] : Collection.getInstance().getCardOwnedCount(-1, CardUtil.CardPack.get(j), CardUtil.CardRarity.fromValue(i), null, null, false));
                int totalLocal = (i == 5 ? totalPerPack[j] : CardList.getInstance().getCardCountByCondition(CardUtil.CardPack.get(j), CardUtil.CardRarity.fromValue(i), null, null, Config.SHOW_ONLY_LEGAL_IN_COLLECTION));
                JLabel label = new JLabel(String.format("%,d", countLocal) + " / " + String.format("%,d", totalLocal), JLabel.CENTER);
                label.setAlignmentX(Component.CENTER_ALIGNMENT);
                label.setFont(MainUI.CRboldLarge);
                label.setOpaque(true);
                if (totalLocal == 0) {
                    label.setBackground(Color.GRAY);
                    label.setForeground(Color.LIGHT_GRAY);
                } else {
                    if (countLocal >= totalLocal) {
                        label.setForeground(new Color(255, 226, 84));
                        label.setOpaque(true);
                        label.setBackground(new Color(191, 142, 0));
                    } else {
                        label.setForeground(MainUI.foregroundColor);
                    }
                }
                label.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
                byRarity.add(label, gbc_byRarity);
                gbc_byRarity.gridy++;
            }
            gbc_byRarity.gridx++;
        }

        gbc_byRarity.gridx = 1;
        gbc_byRarity.gridwidth = 6;
        int count = Collection.getInstance().getCardOwnedCount(-1, null, CardUtil.CardRarity.P, null, null, false);
        JLabel label_P = new JLabel(String.format("%,d", count), JLabel.CENTER);
        label_P.setAlignmentX(Component.CENTER_ALIGNMENT);
        label_P.setFont(MainUI.CRboldLarge);
        label_P.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        byRarity.add(label_P, gbc_byRarity);

        gbc_byRarity.gridwidth = 6;
        gbc_byRarity.gridheight = CardUtil.CardPack.size();
        gbc_byRarity.gridx = 1;
        gbc_byRarity.gridy = 1;
        JPanel tableBorder = new JPanel();
        tableBorder.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        tableBorder.setOpaque(false);
        tableBorder.setLayout(null);
        byRarity.add(tableBorder, gbc_byRarity);
        byRarity.setComponentZOrder(tableBorder, 0);

        // By Rarity (Secret Rare) tab ============================

        JPanel byRaritySecret = new JPanel();
        byRaritySecret.setLayout(new GridBagLayout());
        tabbedPane.addTab(CardUtil.getTranslation("collection.summary.rarity_sec"), byRaritySecret);
        byRaritySecret.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        GridBagConstraints gbc_byRaritySecret = new GridBagConstraints();
        gbc_byRaritySecret.fill = GridBagConstraints.BOTH;

        gbc_byRaritySecret.weightx = 0.5;
        gbc_byRaritySecret.weighty = 1;
        gbc_byRaritySecret.gridx = 0;
        gbc_byRaritySecret.gridy = 1;
        for (int i = 0; i < CardUtil.CardPack.size()-1; i++) {
            if (CardUtil.CardPack.get(i).contains("ST")) {
                continue;
            }
            JLabel label = new JLabel(CardUtil.CardPack.get(i) + " ", JLabel.RIGHT);
            label.setAlignmentX(Component.RIGHT_ALIGNMENT);
            label.setFont(MainUI.CRnormal);
            byRaritySecret.add(label, gbc_byRaritySecret);
            gbc_byRaritySecret.gridy++;
        }


        gbc_byRaritySecret.gridx = 1;
        gbc_byRaritySecret.gridy = 0;
        gbc_byRaritySecret.weightx = 1;
        gbc_byRaritySecret.weighty = 0.5;
        for (int i = 0; i < (CardUtil.RARITY_MAX_ALL - CardUtil.RARITY_MAX); i++) {
            JLabel label = new JLabel("<html><img src=\"file:" + new File(AppPaths.dataDir().resolve("icons_rarity/24px/" + CardUtil.CardRarity.fromValue(CardUtil.RARITY_MAX+i).getName() + ".png").toString()).getAbsolutePath() + "\"></html>", JLabel.CENTER);
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            label.setFont(MainUI.CRnormal);
            byRaritySecret.add(label, gbc_byRaritySecret);
            gbc_byRaritySecret.gridx++;
        }

        JLabel label_total_sec = new JLabel("Total", JLabel.CENTER);
        label_total_sec.setAlignmentX(Component.CENTER_ALIGNMENT);
        label_total_sec.setFont(MainUI.CRnormal);
        byRaritySecret.add(label_total_sec, gbc_byRaritySecret);
        
        gbc_byRaritySecret.gridx = 1;

        for (int i = 0; i <= (CardUtil.RARITY_MAX_ALL - CardUtil.RARITY_MAX); i++) {
            gbc_byRaritySecret.gridy = 1;
            for (int j = 0; j < CardUtil.CardPack.size()-1; j++) {
                if (CardUtil.CardPack.get(j).contains("ST")) {
                    continue;
                }

                int countLocal = (i == (CardUtil.RARITY_MAX_ALL - CardUtil.RARITY_MAX) ? totalOwnedPerPackSec[j] : Collection.getInstance().getCardOwnedCount(-1, CardUtil.CardPack.get(j), CardUtil.CardRarity.fromValue(CardUtil.RARITY_MAX+i), null, null, false));
                int totalLocal = (i == (CardUtil.RARITY_MAX_ALL - CardUtil.RARITY_MAX) ? totalPerPackSec[j] : CardList.getInstance().getCardCountByCondition(CardUtil.CardPack.get(j), CardUtil.CardRarity.fromValue(CardUtil.RARITY_MAX+i), null, null, Config.SHOW_ONLY_LEGAL_IN_COLLECTION));

                JLabel label = new JLabel(String.format("%,d", countLocal) + " / " + String.format("%,d", totalLocal), JLabel.CENTER);
                label.setAlignmentX(Component.CENTER_ALIGNMENT);
                label.setFont(MainUI.CRboldLarge);
                label.setOpaque(true);
                if (totalLocal == 0) {
                    label.setBackground(Color.GRAY);
                    label.setForeground(Color.LIGHT_GRAY);
                } else {
                    if (countLocal >= totalLocal) {
                        label.setForeground(new Color(255, 226, 84));
                        label.setOpaque(true);
                        label.setBackground(new Color(191, 142, 0));
                    } else {
                        label.setForeground(MainUI.foregroundColor);
                    }
                }
                label.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
                byRaritySecret.add(label, gbc_byRaritySecret);
                gbc_byRaritySecret.gridy++;
            }
            gbc_byRaritySecret.gridx++;
        }

        gbc_byRaritySecret.gridwidth = 6;
        gbc_byRaritySecret.gridheight = CardUtil.CardPack.size()-1;
        gbc_byRaritySecret.gridx = 1;
        gbc_byRaritySecret.gridy = 1;
        JPanel tableBorder_sec = new JPanel();
        tableBorder_sec.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        tableBorder_sec.setOpaque(false);
        tableBorder_sec.setLayout(null);
        byRaritySecret.add(tableBorder_sec, gbc_byRaritySecret);
        byRaritySecret.setComponentZOrder(tableBorder_sec, 0);
        /*
        JPanel byPromoSet = new JPanel();
        byPromoSet.setLayout(new GridBagLayout());
        tabbedPane.addTab(CardUtil.getTranslation("collection.summary.promo"), byPromoSet);
         */
        frame.getContentPane().add(tabbedPane);
    }
}
