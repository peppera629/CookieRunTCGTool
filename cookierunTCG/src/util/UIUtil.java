package util;

import java.awt.GridLayout;
import java.awt.Image;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import dataStructure.Card;
import dataStructure.CardList;
import dataStructure.Deck;
import ui.ClickableCardPanel;
import ui.ClickableCardPanel.CardListCallBack;

public class UIUtil {
	
	public static final int CARD_SIZE_SMALL = 0;
	public static final int CARD_SIZE_DECK = 1;
	public static final int CARD_SIZE_OUTPUT = 2;
	public static final int CARD_SIZE_FULL = 3;
	public static void showDeck(CardListCallBack callback, JPanel panel, List<Card> cardList, Map<String, Integer> counts2, int minSize, int rowSize, int cardSize, int showCountMode) {
		// showCountMode: 0 = None (false, false), 1 = Deck Count (true, false), 2 = Collection Count (true, true),
		// 3 = Both (for "build from collection" mode), 4 = Differential Mode (what cards to change from one deck to another)
		panel.removeAll();
		panel.setLayout(new GridLayout(0, rowSize, 5, 5));
		System.out.println("========== start updateDeck =============");

		if (showCountMode == 4 && counts2 != null) {
			Map<String, Integer> counts1 = new HashMap<>();
			CardList globalCardList = CardList.getInstance();
			
			for (Card card : cardList) {
				counts1.merge(card.getId(), card.getCount(), Integer::sum);
				System.out.println("Card in deck 1: " + card.getId() + " Count: " + card.getCount());
			}

			LinkedHashSet<String> allIds = new LinkedHashSet<>();
            for (Card c : cardList) allIds.add(c.getId());
            for (String id : counts2.keySet()) allIds.add(id);

			for (String id : allIds) {
				if (id == null || id.isEmpty()) {
					System.out.println("Skipping empty card id");
					continue;
				}

				int count1 = counts1.getOrDefault(id, 0);
				int count2 = counts2.getOrDefault(id, 0);
				int differential = count2 - count1;

				System.out.println(id + " Differential: " + differential);
                if (differential == 0) {
                    continue;
                }

				Card cardInstance = null;

                // Find any card instance to render, current deck (deck 1) takes priority
                for (Card c : cardList) {
					if (id.equals(c.getId())) {
						cardInstance = c;
						break;
					}
				}
                if (cardInstance == null) {
                    for (String id2 : counts2.keySet()) {
						if (id.equals(id2)) {
							cardInstance = globalCardList.getCardById(id);
							break;
						}
					}
                }
                if (cardInstance == null) {
                    System.out.println("No card instance found for id: " + id);
                    continue;
                }

                ClickableCardPanel cardPanel = new ClickableCardPanel(cardInstance, showCountMode, cardSize, differential);
                panel.add(cardPanel);
			}
		} else {
			for (Card card : cardList) {
				ClickableCardPanel cardPanel;
				cardPanel = new ClickableCardPanel(card, showCountMode, cardSize, 0);
				if(callback != null) {
					cardPanel.addClickListener(callback);
				}
				panel.add(cardPanel);
				card.addPanel(cardPanel);
			}
		}

		String path = "resources/cards/empty.png";
		ImageIcon cardIcon = new ImageIcon(path);
		Image image = cardIcon.getImage().getScaledInstance(60, 84, java.awt.Image.SCALE_SMOOTH);
		for (int i = panel.getComponentCount(); i < minSize; i++) {
			cardIcon = new ImageIcon(image);
			JLabel cardLabel = new JLabel(cardIcon);
			panel.add(cardLabel);
		}

		panel.revalidate();
		panel.repaint();
	}
}
