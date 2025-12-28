package ui;

import java.awt.BorderLayout;
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

import dataStructure.Card;
import dataStructure.CardList;
import dataStructure.Collection;
import util.CardUtil;

// Dialog box

public class CollectionSummaryDialog {
    private JFrame frame;
    private int count, total;

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
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(MainUI.CRnormal);

        int[] totalOwnedPerPack = new int[CardUtil.CardPack.size()];
        int[] totalPerPack = new int[CardUtil.CardPack.size()];
        int[] totalOwnedPerPackSec = new int[CardUtil.CardPack.size()-1];
        int[] totalPerPackSec = new int[CardUtil.CardPack.size()-1];
        int[] totalOwnedPerRarity = new int[CardUtil.RARITY_MAX];
        int[] totalPerRarity = new int[CardUtil.RARITY_MAX];
        int[] totalOwnedPerColor = new int[CardUtil.COLOR_MAX];
        int[] totalPerColor = new int[CardUtil.COLOR_MAX];
        int[] totalOwnedPerType = new int[CardUtil.TYPE_MAX];
        int[] totalPerType = new int[CardUtil.TYPE_MAX];
        
        // Fill in total owned per pack, total per pack
        for (int i = 0; i < CardUtil.RARITY_MAX; i++) {
            for (int j = 0; j < CardUtil.CardPack.size()-1; j++) {
                count = (i == 5 ? totalOwnedPerPack[j] : Collection.getInstance().getCardOwnedCount(-1, CardUtil.CardPack.get(j), CardUtil.CardRarity.fromValue(i), null, null, false));
                total = (i == 5 ? totalPerPack[j] : CardList.getInstance().getCardCountByCondition(CardUtil.CardPack.get(j), CardUtil.CardRarity.fromValue(i), null, null));
                
                if (i != 5) {
                    totalOwnedPerPack[j] += count;
                    totalPerPack[j] += total;
                }
            }
        }

        // Fill in total owned per pack (Secret Rare), total per pack (Secret Rare)
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < CardUtil.CardPack.size()-1; j++) {
                if (CardUtil.CardPack.get(j).contains("ST")) {
                    continue;
                }
                count = (i == 4 ? totalOwnedPerPackSec[j] : Collection.getInstance().getCardOwnedCount(-1, CardUtil.CardPack.get(j), CardUtil.CardRarity.fromValue(6+i), null, null, false));
                int total = (i == 4 ? totalPerPackSec[j] : CardList.getInstance().getCardCountByCondition(CardUtil.CardPack.get(j), CardUtil.CardRarity.fromValue(6+i), null, null));

                if (i != 4) {
                    totalOwnedPerPackSec[j] += count;
                    totalPerPackSec[j] += total;
                }
            }
        }

        // Fill in total owned per rarity, total per rarity
        for (int i = 0; i < CardUtil.RARITY_MAX; i++) {
            totalOwnedPerRarity[i] = Collection.getInstance().getCardOwnedCount(-1, null, CardUtil.CardRarity.fromValue(i), null, null, false);
            totalPerRarity[i] = CardList.getInstance().getCardCountByCondition(null, CardUtil.CardRarity.fromValue(i), null, null);
        }
        // Fill in total owned per color, total per color
        for (int i = 0; i < CardUtil.COLOR_MAX; i++) {
            totalOwnedPerColor[i] = Collection.getInstance().getCardOwnedCount(-1, null, null, CardUtil.CardColor.fromValue(i), null, false);
            totalPerColor[i] = CardList.getInstance().getCardCountByCondition(null, null, CardUtil.CardColor.fromValue(i), null);
        }
        // Fill in total owned per type, total per type
        for (int i = 0; i < CardUtil.TYPE_MAX; i++) {
            totalOwnedPerType[i] = Collection.getInstance().getCardOwnedCount(-1, null, null, null, CardUtil.CardType.values()[i], false);
            totalPerType[i] = CardList.getInstance().getCardCountByCondition(null, null, null, CardUtil.CardType.values()[i]);
        }

        // By Rarity tab =============================================

        JPanel byRarity = new JPanel();
        byRarity.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        byRarity.setLayout(new GridBagLayout());
        tabbedPane.addTab(CardUtil.getTranslation("collection.summary.rarity"), byRarity);
        GridBagConstraints gbc_byRarity = new GridBagConstraints();
        gbc_byRarity.fill = GridBagConstraints.BOTH;

        gbc_byRarity.weightx = 0.5;
        gbc_byRarity.weighty = 1;
        gbc_byRarity.gridx = 0;
        gbc_byRarity.gridy = 1;
        
        for (int i = 0; i < CardUtil.CardPack.size()+1; i++) {
            JLabel label;
            if (i == 0) {
                label = new JLabel("Total ", JLabel.RIGHT);
            } else {
                label = new JLabel(CardUtil.CardPack.get(i-1) + " ", JLabel.RIGHT);
            }
            label.setAlignmentX(Component.RIGHT_ALIGNMENT);
            label.setFont(MainUI.CRnormal);
            byRarity.add(label, gbc_byRarity);
            gbc_byRarity.gridy++;
        }


        gbc_byRarity.gridx = 1;
        gbc_byRarity.gridy = 0;
        gbc_byRarity.weightx = 1;
        gbc_byRarity.weighty = 0.5;
        for (int i = 0; i < 5; i++) {
            JLabel label = new JLabel("<html><img src=\"file:" + new File("resources/icons_rarity/24px/" + CardUtil.CardRarity.fromValue(i).getName() + ".png").getAbsolutePath() + "\"></html>", JLabel.CENTER);
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            label.setFont(MainUI.CRnormal);
            byRarity.add(label, gbc_byRarity);
            gbc_byRarity.gridx++;
        }

        JLabel label_total = new JLabel("Total", JLabel.CENTER);
        label_total.setAlignmentX(Component.CENTER_ALIGNMENT);
        label_total.setFont(MainUI.CRnormal);
        byRarity.add(label_total, gbc_byRarity);
        
        gbc_byRarity.gridx = 1;
        gbc_byRarity.gridy = 1;
        for (int i = 0; i < 6; i++) {
            gbc_byRarity.gridy = 1;
            int totalOwnedPerPackTotal = 0;
            int totalPerPackTotal = 0;
            for (int k = 0; k < 5; k++) {
                if (k != 4) {
                    totalOwnedPerPackTotal += Collection.getInstance().getCardOwnedCount(-1, null, CardUtil.CardRarity.fromValue(k), null, null, false);
                    totalPerPackTotal += CardList.getInstance().getCardCountByCondition(null, CardUtil.CardRarity.fromValue(k), null, null);
                }
            }

            int countLocal = (i == 5 ? totalOwnedPerPackTotal : Collection.getInstance().getCardOwnedCount(-1, null, CardUtil.CardRarity.fromValue(i), null, null, false));
            int totalLocal = (i == 5 ? totalPerPackTotal : CardList.getInstance().getCardCountByCondition(null, CardUtil.CardRarity.fromValue(i), null, null));
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
                    label.setForeground(Color.BLACK);
                }
            }
            label.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
            byRarity.add(label, gbc_byRarity);
            gbc_byRarity.gridx++;
        }
        gbc_byRarity.gridx = 1;
        for (int i = 0; i < 6; i++) {
            gbc_byRarity.gridy = 2;
            for (int j = 0; j < CardUtil.CardPack.size()-1; j++) {
                int countLocal = (i == 5 ? totalOwnedPerPack[j] : Collection.getInstance().getCardOwnedCount(-1, CardUtil.CardPack.get(j), CardUtil.CardRarity.fromValue(i), null, null, false));
                int totalLocal = (i == 5 ? totalPerPack[j] : CardList.getInstance().getCardCountByCondition(CardUtil.CardPack.get(j), CardUtil.CardRarity.fromValue(i), null, null));
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
                        label.setForeground(Color.BLACK);
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
        JLabel label_P = new JLabel(Integer.toString(count), JLabel.CENTER);
        label_P.setAlignmentX(Component.CENTER_ALIGNMENT);
        label_P.setFont(MainUI.CRboldLarge);
        label_P.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        byRarity.add(label_P, gbc_byRarity);

        gbc_byRarity.gridwidth = 6;
        gbc_byRarity.gridheight = CardUtil.CardPack.size() + 1;
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
        for (int i = 0; i < 4; i++) {
            JLabel label = new JLabel("<html><img src=\"file:" + new File("resources/icons_rarity/24px/" + CardUtil.CardRarity.fromValue(6+i).getName() + ".png").getAbsolutePath() + "\"></html>", JLabel.CENTER);
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

        for (int i = 0; i < 5; i++) {
            gbc_byRaritySecret.gridy = 1;
            for (int j = 0; j < CardUtil.CardPack.size()-1; j++) {
                if (CardUtil.CardPack.get(j).contains("ST")) {
                    continue;
                }

                int countLocal = (i == 4 ? totalOwnedPerPackSec[j] : Collection.getInstance().getCardOwnedCount(-1, CardUtil.CardPack.get(j), CardUtil.CardRarity.fromValue(i+6), null, null, false));
                int totalLocal = (i == 4 ? totalPerPackSec[j] : CardList.getInstance().getCardCountByCondition(CardUtil.CardPack.get(j), CardUtil.CardRarity.fromValue(i+6), null, null));

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
                        label.setForeground(Color.BLACK);
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
