package ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import dataStructure.Card;

public class ClickableCardLabel extends JLabel {
	interface CardListCallBack{
		public void addCard(Card card);
		public void removeCard(Card card);
		public void showCard(Card card);
	}

    private int leftClickCount;
    private int rightClickCount;
    private Card _card;
    private CardListCallBack mCardListCallBack;

    public ClickableCardLabel(ImageIcon cardIcon, Card card) {
        super(cardIcon);
        _card = card;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    leftClickCount++;
                    System.out.println("Card Left Clicks: " + leftClickCount);
                    if (mCardListCallBack != null) {
                    	mCardListCallBack.addCard(card);
                    }
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    rightClickCount++;
                    System.out.println("Card Right Clicks: " + rightClickCount);
                    if (mCardListCallBack != null) {
                    	mCardListCallBack.removeCard(card);
                    }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (mCardListCallBack != null) {
                	mCardListCallBack.showCard(card);
                }
            }
        });
    }
    
    public void addClickListener(CardListCallBack callback) {
    	mCardListCallBack = callback;
    }
}