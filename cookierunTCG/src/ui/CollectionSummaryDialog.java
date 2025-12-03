package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

import dataStructure.Card;
import dataStructure.CardList;
import dataStructure.Deck;
import dataStructure.Collection;
import util.Config;
import util.UIUtil;
import util.CardUtil;
import ui.MainUI;

import javax.swing.JButton;
import javax.swing.JCheckBox;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

// Dialog box

public class CollectionSummaryDialog {
    private JFrame frame;
    private String message;
    private int count;

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
        frame.setBounds(100, 100, 800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(MainUI.CRnormal);

        // By Rarity tab

        JPanel byRarity = new JPanel();
        byRarity.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        byRarity.setLayout(new GridBagLayout());
        tabbedPane.addTab(CardUtil.getTranslation("collection.summary.rarity"), byRarity);
        GridBagConstraints gbc_byRarity = new GridBagConstraints();
        gbc_byRarity.fill = GridBagConstraints.BOTH;

        int[] totalOwnedPerPack = new int[CardUtil.CardPack.size()];
        int[] totalPerPack = new int[CardUtil.CardPack.size()];

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

        for (int i = 0; i < 6; i++) {
            gbc_byRarity.gridy = 1;
            for (int j = 0; j < CardUtil.CardPack.size()-1; j++) {
                count = (i == 5 ? totalOwnedPerPack[j] : Collection.getInstance().getCardOwnedCount(CardUtil.CardPack.get(j), CardUtil.CardRarity.fromValue(i), false));
                int total = (i == 5 ? totalPerPack[j] : CardList.getInstance().getCardCountByCondition(CardUtil.CardPack.get(j), CardUtil.CardRarity.fromValue(i), null, null));
                
                if (i != 5) {
                    totalOwnedPerPack[j] += count;
                    totalPerPack[j] += total;
                }
                
                JLabel label = new JLabel(Integer.toString(count) + " / " + Integer.toString(total), JLabel.CENTER);
                label.setAlignmentX(Component.CENTER_ALIGNMENT);
                label.setFont(MainUI.CRboldLarge);
                label.setOpaque(true);
                if (total == 0) {
                    label.setBackground(Color.GRAY);
                    label.setForeground(Color.LIGHT_GRAY);
                } else {
                    if (count >= total) {
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
        int count = Collection.getInstance().getCardOwnedCount(null, CardUtil.CardRarity.fromValue(5), false);
        JLabel label_P = new JLabel(Integer.toString(count), JLabel.CENTER);
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

        // By Rarity (Secret Rare) tab

        JPanel byRaritySecret = new JPanel();
        byRaritySecret.setLayout(new GridBagLayout());
        tabbedPane.addTab(CardUtil.getTranslation("collection.summary.rarity_sec"), byRaritySecret);
        byRaritySecret.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        GridBagConstraints gbc_byRaritySecret = new GridBagConstraints();
        gbc_byRaritySecret.fill = GridBagConstraints.BOTH;

        int[] totalOwnedPerPackSec = new int[CardUtil.CardPack.size()-1];
        int[] totalPerPackSec = new int[CardUtil.CardPack.size()-1];

        gbc_byRaritySecret.weightx = 0.5;
        gbc_byRaritySecret.weighty = 1;
        gbc_byRaritySecret.gridx = 0;
        gbc_byRaritySecret.gridy = 1;
        for (int i = 0; i < CardUtil.CardPack.size()-1; i++) {
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
                count = (i == 4 ? totalOwnedPerPackSec[j] : Collection.getInstance().getCardOwnedCount(CardUtil.CardPack.get(j), CardUtil.CardRarity.fromValue(6+i), false));
                int total = (i == 4 ? totalPerPackSec[j] : CardList.getInstance().getCardCountByCondition(CardUtil.CardPack.get(j), CardUtil.CardRarity.fromValue(6+i), null, null));

                if (i != 4) {
                    totalOwnedPerPackSec[j] += count;
                    totalPerPackSec[j] += total;
                }

                JLabel label = new JLabel(Integer.toString(count) + " / " + Integer.toString(total), JLabel.CENTER);
                label.setAlignmentX(Component.CENTER_ALIGNMENT);
                label.setFont(MainUI.CRboldLarge);
                label.setOpaque(true);
                if (total == 0) {
                    label.setBackground(Color.GRAY);
                    label.setForeground(Color.LIGHT_GRAY);
                } else {
                    if (count >= total) {
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

        JPanel byPromoSet = new JPanel();
        byPromoSet.setLayout(new GridBagLayout());
        tabbedPane.addTab(CardUtil.getTranslation("collection.summary.promo"), byPromoSet);

        frame.getContentPane().add(tabbedPane);
    }
}
