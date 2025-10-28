package util;

import java.awt.GridLayout;
import java.awt.Image;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import dataStructure.Card;
import dataStructure.Deck;
import ui.ClickableCardPanel;
import ui.ClickableCardPanel.CardListCallBack;

public class UIUtil {
	
	public static final int CARD_SIZE_SMALL = 0;
	public static final int CARD_SIZE_DECK = 1;
	public static final int CARD_SIZE_OUTPUT = 2;
	public static final int CARD_SIZE_FULL = 3;
	public static void showDeck(CardListCallBack callback, JPanel panel, List<Card> cardList, int minSize, int rowSize, int cardSize, boolean showCount) {
		panel.removeAll();
		panel.setLayout(new GridLayout(0, rowSize, 5, 5));
		System.out.println("========== start updateDeck =============");
		for (Card card : cardList) {
			ClickableCardPanel cardPanel;
			cardPanel = new ClickableCardPanel( card, Config.SHOW_CARD_COUNT && showCount, cardSize);
			if(callback != null) {
				cardPanel.addClickListener(callback);
			}
			panel.add(cardPanel);
			card.addPanel(cardPanel);
		}

		String path = "resources/cards/empty.png";
		ImageIcon cardIcon = new ImageIcon(path);
		Image image = cardIcon.getImage().getScaledInstance(60, 84, java.awt.Image.SCALE_SMOOTH);
		for (int i = cardList.size(); i < minSize; i++) {
			cardIcon = new ImageIcon(image);
			JLabel cardLabel = new JLabel(cardIcon);
			panel.add(cardLabel);
		}

		panel.revalidate();
		panel.repaint();
	}
}
