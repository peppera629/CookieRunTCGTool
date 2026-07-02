package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.io.File;
import java.util.List;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.SwingUtilities;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.awt.Component;

import dataStructure.Card;
import dataStructure.Collection;
import ui.MainUI;
import util.AppPaths;
import util.Config;
import util.CardUtil;
import util.CardUtil.CardRarity;

public class CardDetailView extends JPanel {
    private JLabel cardId = new JLabel("", JLabel.CENTER);
    private JLabel cardName = new JLabel("", JLabel.CENTER);
    private JLabel cardAttackAttr = new JLabel("", JLabel.CENTER);
    private JLabel cardAttackCost = new JLabel("", JLabel.CENTER);
    private JLabel cardAttackAttrValue = new JLabel("", JLabel.CENTER);
    private JLabel cardTranslationSkill = new JLabel("", JLabel.CENTER);
    private JLabel cardTranslationAttackCost = new JLabel("", JLabel.CENTER);
    private JLabel cardTranslationAttack = new JLabel("", JLabel.CENTER);
    private JLabel cardTranslationAttackIcon = new JLabel("", JLabel.CENTER);
    private JLabel cardTranslationAttackThen = new JLabel("", JLabel.CENTER);
    private JLabel cardTranslationFlip = new JLabel("", JLabel.CENTER);
    private JLabel cardTranslationSkillFlavorText = new JLabel("", JLabel.CENTER);
    private JLabel cardTranslationSkillIcon = new JLabel("", JLabel.CENTER);
    private JLabel cardTranslationAttackFlavorText = new JLabel("", JLabel.CENTER);
    private JLabel[] langLabels;
    private	JLabel[] ownedInfoRarityRows;
    private JLabel[][] ownedInfoCountRows;

    private JPanel cardInfo, ownedInfoPanel, cardTranslationPanel;
    private JPanel cardInfoTop, cardInfoBottom;
    private JPanel previewPane = new JPanel(new BorderLayout());

    private Card currentCard;
    private ImageIcon cardIcon, sourceCardIcon;
    private boolean attackAttrShown = false;
    private int previewHeight = 1;
    private static final int MIN_PREVIEW_HEIGHT = 50;

    public CardDetailView() {
        // ==== Card ID and Name
        cardInfo = new JPanel();
        cardInfo.setLayout(new BorderLayout());
        cardId = new JLabel("", JLabel.CENTER);
        cardId.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardId.setFont(MainUI.CRnormal);
        MainUI.componentFontMap.put(cardId, "CRnormal"); // Store the font type as a String
        cardName = new JLabel("", JLabel.CENTER);
        cardName.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardName.setFont(MainUI.CRboldLarge);
        cardName.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (!MainUI.isCollectionMode) {
                        attackAttrShown = !attackAttrShown;
                        setCardAttackAttrVisibility(attackAttrShown);
                    }
                }
            }
        });
        MainUI.componentFontMap.put(cardName, "CRboldLarge"); // Store the font type as a String

        cardAttackAttr = new JLabel("", JLabel.CENTER);
        cardAttackAttr.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardAttackAttr.setVisible(attackAttrShown);
        cardAttackAttr.setFont(MainUI.CRboldLarge);
        MainUI.componentFontMap.put(cardAttackAttr, "CRboldLarge"); // Store the font type as a String

        cardAttackCost = new JLabel("", JLabel.CENTER);
        cardAttackCost.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardAttackCost.setVisible(attackAttrShown);
        cardAttackCost.setFont(MainUI.CRboldLarge);
        MainUI.componentFontMap.put(cardAttackCost, "CRboldLarge"); // Store the font type as a String

        cardAttackAttrValue = new JLabel("", JLabel.CENTER);
        cardAttackAttrValue.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardAttackAttrValue.setVisible(attackAttrShown);
        cardAttackAttrValue.setFont(MainUI.CRnormalSmall);
        MainUI.componentFontMap.put(cardAttackAttrValue, "CRnormalSmall"); // Store the font type as a String

        cardInfoTop = new JPanel();
        cardInfoTop.setLayout(new BoxLayout(cardInfoTop, BoxLayout.Y_AXIS));

        cardInfoTop.add(cardId);
        cardInfoTop.add(cardName);
        cardInfoTop.add(cardAttackCost);
        cardInfoTop.add(cardAttackAttr);
        cardInfoTop.add(cardAttackAttrValue);
        cardInfo.add(cardInfoTop, BorderLayout.NORTH);

        // ==== Card Preview
        cardInfo.add(previewPane, BorderLayout.CENTER);

        // ==== Card Ownership Info (when Collection Mode is active)
        cardInfoBottom = new JPanel();
        cardInfoBottom.setLayout(new BoxLayout(cardInfoBottom, BoxLayout.Y_AXIS));

        ownedInfoPanel = new JPanel();
        ownedInfoPanel.setLayout(new GridBagLayout());
        ownedInfoPanel.setVisible(false);
        ownedInfoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        cardInfoBottom.add(ownedInfoPanel);

        langLabels = new JLabel[Config.ALL_CARD_LANGUAGES.length];
        for (int i = 0; i < Config.ALL_CARD_LANGUAGES.length; i++) {
            langLabels[i] = new JLabel("", JLabel.CENTER);
            langLabels[i].setFont(MainUI.CRboldSmall);
            MainUI.componentFontMap.put(langLabels[i], "CRboldSmall"); // Store the font type
        }

        ownedInfoRarityRows = new JLabel[] {
            new JLabel("", JLabel.LEFT),
            new JLabel("", JLabel.LEFT),
            new JLabel("", JLabel.LEFT),
            new JLabel("", JLabel.LEFT),
            new JLabel("", JLabel.LEFT),
            new JLabel("", JLabel.LEFT),
            new JLabel("", JLabel.LEFT)
        };

        ownedInfoCountRows = new JLabel[][] {
            {new JLabel("", JLabel.CENTER), new JLabel("", JLabel.CENTER), new JLabel("", JLabel.CENTER)},
            {new JLabel("", JLabel.CENTER), new JLabel("", JLabel.CENTER), new JLabel("", JLabel.CENTER)},
            {new JLabel("", JLabel.CENTER), new JLabel("", JLabel.CENTER), new JLabel("", JLabel.CENTER)},
            {new JLabel("", JLabel.CENTER), new JLabel("", JLabel.CENTER), new JLabel("", JLabel.CENTER)},
            {new JLabel("", JLabel.CENTER), new JLabel("", JLabel.CENTER), new JLabel("", JLabel.CENTER)},
            {new JLabel("", JLabel.CENTER), new JLabel("", JLabel.CENTER), new JLabel("", JLabel.CENTER)},
            {new JLabel("", JLabel.CENTER), new JLabel("", JLabel.CENTER), new JLabel("", JLabel.CENTER)}
        };

        GridBagConstraints gbc_owned = new GridBagConstraints();
        gbc_owned.fill = GridBagConstraints.BOTH;
        gbc_owned.gridx = 1;
        gbc_owned.gridy = 0;
        for (int i = 0; i < Config.ALL_CARD_LANGUAGES.length; i++) {
            gbc_owned.weightx = 1;
            ownedInfoPanel.add(langLabels[i], gbc_owned);
            gbc_owned.gridx++;
        }
        
        gbc_owned.gridy = 1;
        for (int i = 0; i < ownedInfoRarityRows.length; i++) {
            gbc_owned.gridx = 0;
            gbc_owned.weightx = 5;
            ownedInfoRarityRows[i].setFont(MainUI.CRnormal);
            MainUI.componentFontMap.put(ownedInfoRarityRows[i], "CRnormal"); // Store the font type
            ownedInfoPanel.add(ownedInfoRarityRows[i], gbc_owned);
            gbc_owned.gridx = 1;
            gbc_owned.weightx = 1;
            for (int j = 0; j < ownedInfoCountRows[i].length; j++) {
                ownedInfoCountRows[i][j].setFont(MainUI.CRboldEXLarge);
                MainUI.componentFontMap.put(ownedInfoCountRows[i][j], "CRboldEXLarge"); // Store the font type
                ownedInfoPanel.add(ownedInfoCountRows[i][j], gbc_owned);
                gbc_owned.gridx++;
            }
            gbc_owned.gridy++;
        }

        // ==== Card Translations (when available)
        cardTranslationPanel = new JPanel();
        cardTranslationPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 5, 0, 5); // Add some padding
        gbc.fill = GridBagConstraints.HORIZONTAL; // Ensure components stretch horizontally
        gbc.weightx = 1.0; // Allow components to take full width

        JPanel cardTranslationFlavorTextGroup = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        cardTranslationSkillIcon = new JLabel("");
        cardTranslationFlavorTextGroup.add(cardTranslationSkillIcon);
        cardTranslationSkillFlavorText = new JLabel("", JLabel.CENTER);
        cardTranslationSkillFlavorText.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardTranslationSkillFlavorText.setFont(MainUI.CRtranslation);
        MainUI.componentFontMap.put(cardTranslationSkillFlavorText, "CRtranslation"); // Store the font type
        cardTranslationFlavorTextGroup.add(cardTranslationSkillFlavorText);

        cardTranslationSkill = new JLabel("", JLabel.LEFT);
        cardTranslationSkill.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardTranslationSkill.setFont(MainUI.CRtranslation);
        MainUI.componentFontMap.put(cardTranslationSkill, "CRtranslation"); // Store the font type

        JPanel cardTranslationAttackGroup = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        cardTranslationAttackCost = new JLabel("", JLabel.LEFT);
        cardTranslationAttackCost.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardTranslationAttackCost.setFont(MainUI.CRtranslation);
        MainUI.componentFontMap.put(cardTranslationAttackCost, "CRtranslation"); // Store the font type
        cardTranslationAttackFlavorText = new JLabel("", JLabel.LEFT);
        cardTranslationAttackFlavorText.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardTranslationAttackFlavorText.setFont(MainUI.CRtranslation);
        MainUI.componentFontMap.put(cardTranslationAttackFlavorText, "CRtranslation"); // Store the font type
        cardTranslationAttackGroup.add(cardTranslationAttackCost);
        cardTranslationAttackGroup.add(cardTranslationAttackFlavorText);

        cardTranslationAttackIcon = new JLabel("");
        cardTranslationAttackGroup.add(cardTranslationAttackIcon);

        cardTranslationAttack = new JLabel("", JLabel.LEFT);
        cardTranslationAttack.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardTranslationAttack.setFont(MainUI.CRtranslationBold);
        MainUI.componentFontMap.put(cardTranslationAttack, "CRtranslationBold"); // Store the font type
        cardTranslationAttackGroup.add(cardTranslationAttack);

        cardTranslationAttackThen = new JLabel("", JLabel.LEFT);
        cardTranslationAttackThen.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardTranslationAttackThen.setFont(MainUI.CRtranslation);
        MainUI.componentFontMap.put(cardTranslationAttackThen, "CRtranslation"); // Store the font type

        cardTranslationFlip = new JLabel("", JLabel.LEFT);
        cardTranslationFlip.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardTranslationFlip.setFont(MainUI.CRtranslation);
        MainUI.componentFontMap.put(cardTranslationFlip, "CRtranslation"); // Store the font type

        // Add cardTranslationFlavorTextGroup (centered)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER; // Center the flavor text group
        cardTranslationPanel.add(cardTranslationFlavorTextGroup, gbc);

        // Add cardTranslationSkill (left-aligned to the sidebar)
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST; // Left-align the text
        cardTranslationPanel.add(cardTranslationSkill, gbc);

        // Add cardTranslationAttackGroup (centered)
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER; // Center the attack group
        cardTranslationPanel.add(cardTranslationAttackGroup, gbc);

        // Add cardTranslationAttackThen (left-aligned to the sidebar)
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST; // Left-align the text
        cardTranslationPanel.add(cardTranslationAttackThen, gbc);

        // Add cardTranslationFlip (left-aligned to the sidebar)
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST; // Left-align the text
        cardTranslationPanel.add(cardTranslationFlip, gbc);

        cardInfoBottom.add(cardTranslationPanel);
        cardInfo.add(cardInfoBottom, BorderLayout.SOUTH);
        add(cardInfo, BorderLayout.CENTER);

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                if (currentCard != null) {
                    SwingUtilities.invokeLater(() -> refreshPreviewImage());
                }
            }
        });

    }

    public void showCard(Card card, int displayVariant) {
        currentCard = card;

        cardInfo.setPreferredSize(new Dimension(Config.CARD_PREVIEW_WIDTH, this.getHeight() - 10));
        
        loadCardImage(card, displayVariant);
        updateText(card, displayVariant);
        SwingUtilities.invokeLater(() -> refreshPreviewImage());
    }
    
    public void clearCard() {
        currentCard = null;
        cardId.setText("");
        cardName.setText("");
        cardAttackAttr.setText("");
        cardAttackCost.setText("");
        cardAttackAttrValue.setText("");
        cardTranslationSkill.setText("");
        cardTranslationAttackCost.setText("");
        cardTranslationAttack.setText("");
        cardTranslationAttackIcon.setIcon(null);
        cardTranslationAttackThen.setText("");
        cardTranslationFlip.setText("");
        cardTranslationSkillIcon.setIcon(null);
        cardTranslationSkillFlavorText.setText("");
        cardTranslationAttackFlavorText.setText("");
    }

    public void enableOwnedInfoLabels() {
        for (JLabel[] labels : ownedInfoCountRows) {
            for (JLabel label : labels) {
                label.setText("");
                label.setVisible(true);
            }
        }
        for (JLabel label : ownedInfoRarityRows) {
            label.setText("");
            label.setVisible(true);
        }
    }

    public void clearLabels() {
        for (JLabel label : langLabels) {
            label.setText("");
            label.setVisible(false);
        }
        for (JLabel[] labels : ownedInfoCountRows) {
            for (JLabel label : labels) {
                label.setText("");
                label.setVisible(false);
            }
        }
        for (JLabel label : ownedInfoRarityRows) {
            label.setText("");
            label.setVisible(false);
        }
    }

    public void clearPanel() {
        previewPane.removeAll();
    }
    private void clearTranslations() {
        cardTranslationSkill.setText("");
        cardTranslationAttackCost.setText("");
        cardTranslationAttack.setText("");
        cardTranslationAttackIcon.setIcon(null);
        cardTranslationAttackFlavorText.setText("");
        cardTranslationSkillIcon.setIcon(null);
        cardTranslationSkillFlavorText.setText("");
        cardTranslationAttackThen.setText("");
        cardTranslationFlip.setText("");
    }

    public void loadCardImage(Card card, int displayVariant) {
        String path = "";
        for (String lang : Config.FALLBACK_ORDER) {
            if (MainUI.isCollectionMode() && displayVariant > 0) {
                path = AppPaths.dataDir().resolve(
                    "cards_variant/" + lang + "/" + card.getPack() + "/" + card.getId() + "@" + displayVariant + ".png"
                ).toString();
            } else {
                path = AppPaths.dataDir().resolve(
                    "cards/" + lang + "/" + card.getPack() + "/" + card.getId() + ".png"
                ).toString();
            }

            sourceCardIcon = new ImageIcon(path);
            cardIcon = sourceCardIcon;
            if (sourceCardIcon.getIconWidth() > 0) {
                return;
            }
        }
    }

    public void updateText(Card card, int displayVariant) {
        if (card.getMaxCount() == 1) {
            cardId.setText("<html>" + card.getId() + "&nbsp;<img src=\"file:" + new File(AppPaths.dataDir().resolve("icons/12px/restricted.png").toString()).getAbsolutePath() + "\"></html>");
            cardId.setForeground(new Color(160, 128, 0));
        } else if (card.getMaxCount() == 0) {
            cardId.setText("<html>" + card.getId() + "&nbsp;<img src=\"file:" + new File(AppPaths.dataDir().resolve("icons/12px/banned.png").toString()).getAbsolutePath() + "\"></html>");
            cardId.setForeground(new Color(160, 0, 0));
        } else {
            cardId.setText(card.getId());
            cardId.setForeground(MainUI.foregroundColor);
        }

        if (card.getAttackDMG() > 0) {
            cardAttackAttr.setText("<html><img src=\"file:" + new File(AppPaths.dataDir().resolve("icons/24px/dmgATK.png").toString()).getAbsolutePath() + "\">&nbsp;" + card.getAttackDMG() +
            "&nbsp;<img src=\"file:" + new File(AppPaths.dataDir().resolve("icons/24px/dmgAVG.png").toString()).getAbsolutePath() + "\">&nbsp;" + card.getAvgDMG() +
            "&nbsp;<img src=\"file:" + new File(AppPaths.dataDir().resolve("icons/24px/dmgMAX.png").toString()).getAbsolutePath() + "\">&nbsp;" + card.getPeakDMG() + "</html>");
            cardAttackCost.setText("<html><img src=\"file:" + new File(AppPaths.dataDir().resolve("icons/24px/costATK.png").toString()).getAbsolutePath() + "\">&nbsp;" + card.getAttackCost() +
            "&nbsp;<img src=\"file:" + new File(AppPaths.dataDir().resolve("icons/24px/costAVG.png").toString()).getAbsolutePath() + "\">&nbsp;" + card.getAvgCost() +
            "&nbsp;<img src=\"file:" + new File(AppPaths.dataDir().resolve("icons/24px/costMAX.png").toString()).getAbsolutePath() + "\">&nbsp;" + card.getPeakCost() + "</html>");
            cardAttackAttrValue.setText("(" + card.getAttackEfficiency() + " / " + card.getAvgEfficiency() + " / " + card.getPeakEfficiency() + ")");
        } else {
            cardAttackAttr.setText("");
            cardAttackCost.setText("");
            cardAttackAttrValue.setText("");
        }
        if (card.getNameByLang().get(Config.getLangIndex(Config.LANGUAGE)).endsWith("*")) {
            cardName.setText("<html><u>" + card.getNameByLang().get(Config.getLangIndex(Config.LANGUAGE)).replace("*", "") + "</u> " + "<img src=\"file:" + new File(AppPaths.dataDir().resolve("icons_rarity/16px/" + card.getRarity().getName() + ".png").toString()).getAbsolutePath() + "\">" + "</html>");
            cardName.setToolTipText(String.format(CardUtil.getTranslation("tooltip.nametranslation"), "樂多綠"));
        } else {
            cardName.setText("<html>" + card.getNameByLang().get(Config.getLangIndex(Config.LANGUAGE)) + " " + "<img src=\"file:" + new File(AppPaths.dataDir().resolve("icons_rarity/16px/" + card.getRarity().getName() + ".png").toString()).getAbsolutePath() + "\">" + "</html>");
            cardName.setToolTipText(null);
        }
        if (card.isExtra()) {
            cardName.setForeground(MainUI.extraColor);
        } else {
            cardName.setForeground(MainUI.foregroundColor);
        }
        if (card.getCardTranslation() != null && Config.CARD_TRANSLATION_ENABLED) {
            cardTranslationSkill.setText("<html>" + card.getCardTranslation()[1] + "</html>");
            if (card.getCardTranslation()[1].isEmpty() || card.getType() != CardUtil.CardType.Cookie) {
                cardTranslationSkillIcon.setIcon(null);
                cardTranslationSkillFlavorText.setText("");
            } else {
                cardTranslationSkillIcon.setIcon(new ImageIcon(AppPaths.dataDir().resolve("icons/" + Config.LANGUAGE + "/" + (Config.LARGE_TRANSLATION_TEXT ? "24px/" : "16px/") + "SKILL.png").toString()));
                cardTranslationSkillFlavorText.setText("<html>" + card.getCardTranslation()[0] + "</html>");
            }
            if (card.getCardTranslation()[2].isEmpty()) {
                cardTranslationAttackCost.setText("");
                cardTranslationAttackFlavorText.setText("");
                cardTranslationAttackIcon.setIcon(null);
                cardTranslationAttack.setText("");
            } else {
                cardTranslationAttackFlavorText.setText("<html>" + card.getCardTranslation()[3] + "</html>");
                cardTranslationAttackCost.setText("<html>&lt;" + card.getCardTranslation()[2] + "&gt;</html>");
                cardTranslationAttackIcon.setIcon(new ImageIcon(AppPaths.dataDir().resolve("icons/" + (Config.LARGE_TRANSLATION_TEXT ? "24px/" : "16px/") + "ATK.png").toString()));
                cardTranslationAttack.setText("<html>  " + card.getCardTranslation()[4] + "</html>");
            }
            cardTranslationAttackThen.setText("<html>" + card.getCardTranslation()[5] + "</html>");
            cardTranslationFlip.setText("<html>" + card.getCardTranslation()[6] + "</html>");
        } else {
            cardTranslationAttackFlavorText.setText(null);
            cardTranslationAttackIcon.setIcon(null);
            cardTranslationSkill.setText(null);
            cardTranslationAttackCost.setText(null);
            cardTranslationAttackIcon.setIcon(null);
            cardTranslationAttack.setText(null);
            cardTranslationAttackThen.setText(null);
            cardTranslationFlip.setText(null);
        }

        if (MainUI.isCollectionMode()) {
            for (int i = 0; i < langLabels.length; i++) {
                langLabels[i].setText(Config.ALL_CARD_LANGUAGES[Config.COLLECTION_LANGUAGE_INDICES[i]].replace("zh_TW", "TC").toUpperCase());
                langLabels[i].setVisible(true);
            }

            if (displayVariant != 0 && card.getAltNames().size() > 0) {
                List<String> altNamesForVariant = card.getAltNames().get(displayVariant-1);;
                if (altNamesForVariant != null && altNamesForVariant.size() > Config.getLangIndex(Config.LANGUAGE)) {
                    cardName.setText(altNamesForVariant.get(Config.getLangIndex(Config.LANGUAGE)));
                    cardName.setForeground(MainUI.highlightColor);
                } else {
                    cardName.setText(card.getName());
                    cardName.setForeground(MainUI.foregroundColor);
                }
            } else {
                cardName.setText(card.getName());
                cardName.setForeground(MainUI.foregroundColor);
            }
            clearTranslations();
            updateCardOwnedInfoLabel(card, displayVariant);
            updateCardOwnedInfoHighlight(displayVariant);
        }
        cardInfo.revalidate();
        cardInfo.repaint();
        cardTranslationPanel.revalidate();
        cardTranslationPanel.repaint();
    }

    public void setCardAttackAttrVisibility(boolean visible) {
        cardAttackAttr.setVisible(visible);
        cardAttackCost.setVisible(visible);
        cardAttackAttrValue.setVisible(visible);   
        if (currentCard != null) {
            SwingUtilities.invokeLater(() -> {
                refreshPreviewImage();
            });
        }
    }

    public void refreshPreviewImage() {
        if (cardIcon == null || currentCard == null) {
            return;
        }

        cardInfo.revalidate();
        cardInfo.repaint();
        cardTranslationPanel.revalidate();
        cardTranslationPanel.repaint();

        int translationHeight = cardTranslationPanel.getPreferredSize().height;
        int containerHeight = cardInfo.getHeight() > 0 ? cardInfo.getHeight() : this.getHeight();
        int cardInfoHeight = cardInfoTop.getPreferredSize().height;
        int textPadding = (Config.LARGE_TRANSLATION_TEXT ? 100 : 50);

        previewHeight = Math.max(MIN_PREVIEW_HEIGHT, Math.min(
            containerHeight - cardInfoHeight - (currentCard.getCardTranslation() == null ? 0 : translationHeight) - textPadding,
            Config.CARD_PREVIEW_HEIGHT
        ));

        int previewWidth = Math.max(1, (int) (previewHeight / Config.CARD_RATIO));

        previewPane.setPreferredSize(new Dimension(previewWidth, previewHeight));
        previewPane.invalidate();

        Image image = sourceCardIcon.getImage().getScaledInstance(previewWidth, previewHeight, java.awt.Image.SCALE_SMOOTH);
        cardIcon = new ImageIcon(image);
        JLabel cardLabel = new JLabel(cardIcon);
        //cardLabel.setBorder(BorderFactory.createLineBorder(MainUI.foregroundColor)); // debugging
        
        previewPane.removeAll();
        previewPane.add(cardLabel, BorderLayout.CENTER);
        previewPane.revalidate();
        previewPane.repaint();
    }

    public void updateCardOwnedInfoLabel(Card card, int displayVariant) {
        CardRarity[] rarities = card.getVariants();
        String[] variantNames = card.getVariantNames();
        
        if (rarities == null || variantNames == null) {
            System.out.println("Card " + card.getId() + " has no variants. Maybe you saved as CSV instead of TXT?");
            return;
        }

        MainUI.currentSelectedCardLanguage = MainUI.prevLangIdx;
        if (!card.getAvailability(displayVariant)[Config.COLLECTION_LANGUAGE_INDICES[MainUI.currentSelectedCardLanguage]]) {
            // Auto-switch to next available language
            for (int i = 0; i < Config.COLLECTION_LANGUAGE_INDICES.length; i++) {
                MainUI.currentSelectedCardLanguage = (MainUI.currentSelectedCardLanguage + 1) % Config.COLLECTION_LANGUAGE_INDICES.length;
                if (card.getAvailability(displayVariant)[Config.COLLECTION_LANGUAGE_INDICES[MainUI.currentSelectedCardLanguage]]) {
                    updateLangLabels();
                    System.out.println("Autoswitch: Switched selected language to " + Config.ALL_CARD_LANGUAGES[Config.COLLECTION_LANGUAGE_INDICES[MainUI.currentSelectedCardLanguage]]);
                    break;
                }
            }
        } else {
            updateLangLabels();
        }

        System.out.println("Selected language: " + Config.ALL_CARD_LANGUAGES[Config.COLLECTION_LANGUAGE_INDICES[MainUI.currentSelectedCardLanguage]]);
        System.out.println("Prev language index: " + MainUI.prevLangIdx);

        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < Config.COLLECTION_LANGUAGE_INDICES.length; j++) {
                int langIdx = Config.COLLECTION_LANGUAGE_INDICES[j];
                if (i < rarities.length) {
                    StringBuilder ownedInfo = new StringBuilder();
                    int ownedCount = Collection.getInstance().getCardOwnedCount(langIdx, card.getId(), i);
                    //System.out.println(rarities[i].getName());
                    //System.out.println(ownedCount);
                    ownedInfo.append("<html>");
                    ownedInfo.append("<img src=\"file:" + new File(AppPaths.dataDir().resolve("keyicons/24px/" + (i + 1) + ".png").toString()).getAbsolutePath() + "\">").append("&nbsp;");
                    ownedInfo.append("<img src=\"file:" + new File(AppPaths.dataDir().resolve("icons_rarity/24px/" + rarities[i].getName() + ".png").toString()).getAbsolutePath() + "\">");
                    ownedInfo.append("&nbsp;").append(CardUtil.getTranslationPromo(variantNames[i]));
                    if (i < rarities.length - 1) {
                        ownedInfo.append("<br>");
                    }
                    ownedInfo.append("</html>");
                    ownedInfoRarityRows[i].setText(ownedInfo.toString());
                    ownedInfoCountRows[i][j].setText(String.valueOf(ownedCount));
                    if (card.getAvailability(i)[langIdx]) {
                        ownedInfoCountRows[i][j].setForeground(MainUI.foregroundColor);
                    } else {
                        ownedInfoCountRows[i][j].setForeground(Color.GRAY);
                    }
                } else {
                    ownedInfoRarityRows[i].setText("");
                    ownedInfoCountRows[i][j].setText("");
                }
            }
        }
    }

    public void updateCardOwnedInfoHighlight(int variantIndex) {
        for (int i = 0; i < ownedInfoCountRows.length; i++) {
            for (int j = 0; j < ownedInfoCountRows[i].length; j++) {
                if (i == variantIndex && j == MainUI.currentSelectedCardLanguage) {
                    ownedInfoCountRows[i][MainUI.currentSelectedCardLanguage].setForeground(MainUI.highlightColor);
                } else {
                    if (currentCard.getAvailability(i)[Config.COLLECTION_LANGUAGE_INDICES[j]]) {
                        ownedInfoCountRows[i][j].setForeground(MainUI.foregroundColor);
                    } else {
                        ownedInfoCountRows[i][j].setForeground(Color.GRAY);
                    }
                }
            }
        }
    }

    public void updateLangLabels() {
        for (int i = 0; i < langLabels.length; i++) {
            langLabels[i].setVisible(true);
            if (i == MainUI.currentSelectedCardLanguage) {
                langLabels[i].setOpaque(true);
                langLabels[i].setBackground(MainUI.highlightColor);
                langLabels[i].setForeground(Color.WHITE);
            } else {
                langLabels[i].setOpaque(false);
                langLabels[i].setBackground(new Color(0,0,0,0));
                langLabels[i].setForeground(MainUI.foregroundColor);
            }
        }
    }

    public void toggleCollectionMode(boolean isCollectionMode) {
        setCardAttackAttrVisibility(!isCollectionMode);
        ownedInfoPanel.setVisible(isCollectionMode);
        cardTranslationPanel.setVisible(!isCollectionMode && Config.CARD_TRANSLATION_ENABLED);
        if (!isCollectionMode) {
            clearLabels();
        }
    }

    public void refreshLayout() {
        cardInfo.setPreferredSize(new Dimension(Config.CARD_PREVIEW_WIDTH, this.getHeight() - 10));
        cardInfo.revalidate();
        cardInfo.repaint();
        cardTranslationPanel.revalidate();
        cardTranslationPanel.repaint();
    }
}
