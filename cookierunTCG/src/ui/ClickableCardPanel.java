package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import dataStructure.Card;
import dataStructure.CardLoader;
import dataStructure.Collection;
import util.Config;
import util.UIUtil;

public class ClickableCardPanel extends JPanel {
	
	public interface CardListCallBack{
		public void addCard(Card card);
		public void removeCard(Card card);
		public void showCard(Card card);
	}

    private Card mCard;
    private CardListCallBack mCardListCallBack;
    private int mShowCountMode;
    private int mCardSize;
	private int mDifferential;
	private Dimension cardListSize;
    ImageIcon mCardIcon;

	public ClickableCardPanel(Card card, int showCountMode, int cardSize, int differential) {
        mCard = card;
		mShowCountMode = showCountMode;
		mCardSize = cardSize;
		mDifferential = differential;
    	mCardIcon = CardLoader.createCardImage(mCard, mCardSize);
		cardListSize = new Dimension(mCardIcon.getIconWidth(), mCardIcon.getIconHeight());
        setPreferredSize(cardListSize);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (mCardListCallBack != null) {
                    	mCardListCallBack.addCard(card);
                    }
                } else if (e.getButton() == MouseEvent.BUTTON3) {
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

		if (g == null) {
        	return; // Skip painting if Graphics is null
    	}
        
        // 繪製卡片的 ImageIcon
		mCardIcon.paintIcon(this, g, 0, 0);

        if(mShowCountMode != 0) {
	        Graphics2D g2d = (Graphics2D) g.create();
			try {
				g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING, java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				int boxWidth;
				if (mShowCountMode == 4) {
					boxWidth = mCardIcon.getIconWidth()/2;
				} else {
					boxWidth = mCardIcon.getIconWidth()/3;
				}
				int boxHeight = mCardIcon.getIconWidth()/3;
				int boxX = getWidth() - boxWidth;
				int boxY = getHeight() - boxHeight;
				Color boxColor = new Color(64, 64, 64, 192); // Default: translucent gray

				if (mCard.getMaxCount() == 0) { // Banned card
					boxColor = new Color(160, 0, 0, 192); // Translucent red
				} else if (mCard.getMaxCount() == 1) { // Restricted card
					boxColor = new Color(160, 128, 0, 192); // Translucent yellow
				}
				
				g2d.setColor(boxColor);
				g2d.fillRect(boxX, boxY, boxWidth, boxHeight);
				
				// 在方塊上顯示白色數字
				g2d.setColor(Color.WHITE);
				Font cardPanelFont = MainUI.CRbold.deriveFont(mCardIcon.getIconWidth()/5f);
				Font cardPanelFontSmall = MainUI.CRnormal.deriveFont(mCardIcon.getIconWidth()/8f);
				String text = "";
				String ownedText = "";
				switch (mShowCountMode) {
					case 1: // Deck Count
						text = Integer.toString(mCard.getCount());
						break;
					case 2: // Deck Count (with red indication for unowned/insufficient)
						text = Integer.toString(mCard.getCount());
						if (mCard.getCount() > Collection.getInstance().getCardTotalOwnedCount(mCard.getId())) {
							g2d.setColor(new Color(255, 128, 128));
						}
						break;
					case 3: // Collection Count
						text = Integer.toString(Collection.getInstance().getCardTotalOwnedCount(mCard.getId()));
						break;
					case 4: // Both (for "build from collection" mode)
						text = Integer.toString(mCard.getCount());
						ownedText = "/" + Integer.toString(Collection.getInstance().getCardTotalOwnedCount(mCard.getId()));
						if (mCard.getCount() > Collection.getInstance().getCardTotalOwnedCount(mCard.getId())) {
							g2d.setColor(new Color(255, 128, 128));
						}
						break;
					case 5: // Differential Mode (what cards to change from one deck to another)
						text = Integer.toString(mDifferential);
						if (mDifferential > 0) {
							text = "+" + text;
							g2d.setColor(new Color(128, 255, 128));
						} else if (text.equals("0")) {
							g2d.setColor(new Color(192, 192, 192));
						} else {
							g2d.setColor(new Color(255, 128, 128));
						}
						break;
				}

				g2d.setFont(cardPanelFont);
				FontMetrics metrics = g2d.getFontMetrics(cardPanelFont);
				int textWidth = metrics.stringWidth(text);
				int textHeight = metrics.getHeight();

				g2d.setFont(cardPanelFontSmall);
				FontMetrics metricsOwned = g2d.getFontMetrics(cardPanelFontSmall);
				int ownedTextWidth = metricsOwned.stringWidth(ownedText);

				int totalTextWidth = textWidth + ownedTextWidth;
				int textX = boxX + (boxWidth - totalTextWidth) / 2;
				int textY = boxY + (boxHeight - metrics.getHeight()) / 2 + metrics.getAscent();
				
				g2d.setFont(cardPanelFont);
				g2d.drawString(text, textX, textY);

				g2d.setFont(cardPanelFontSmall);
				g2d.drawString(ownedText, textX + textWidth, textY);

			} finally {
				g2d.dispose();
			}
        } else {
			Graphics2D g2d = (Graphics2D) g.create();
			try {
				g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING, java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				int boxWidth = mCardIcon.getIconWidth()/4;
				int boxHeight = mCardIcon.getIconWidth()/4;
				int boxX = getWidth() - boxWidth;
				int boxY = getHeight() - boxHeight;
				Color boxColor = new Color(0, 0, 0, 0); // Default: translucent gray

				if (mCard.getMaxCount() == 0) { // Banned card
					boxColor = new Color(160, 0, 0, 192); // Translucent red
				} else if (mCard.getMaxCount() == 1) { // Restricted card
					boxColor = new Color(160, 128, 0, 192); // Translucent yellow
				}
				
				g2d.setColor(boxColor);
				g2d.fillRect(boxX, boxY, boxWidth, boxHeight);
				
				// 在方塊上顯示白色數字
				g2d.setColor(Color.WHITE);
				Font cardPanelFont = MainUI.CRbold.deriveFont(mCardIcon.getIconWidth()/5f);
				g2d.setFont(cardPanelFont);
				String text = "";
				if (mCard.getMaxCount() == 1) {
					text = "!";
				} else if (mCard.getMaxCount() == 0) {
					text = "×";
				}
				FontMetrics metrics = g2d.getFontMetrics(cardPanelFont);
				int textWidth = metrics.stringWidth(text);
				int textHeight = metrics.getHeight();
				int textX = boxX + (boxWidth - textWidth) / 2;
				int textY = boxY + (boxHeight - textHeight) / 2 + metrics.getAscent();
				g2d.drawString(text, textX, textY);
			} finally {
				g2d.dispose();
			}
		}
    }
    
    public void addClickListener(CardListCallBack callback) {
    	mCardListCallBack = callback;
    }
    
    public void updateImage() {
    	mCardIcon = CardLoader.createCardImage(mCard, mCardSize);
		repaint();
    }

	public int getCountShowMode() {
		return mShowCountMode;
	}

	public void updateCountsForCardList() {
		if (mShowCountMode == 4) {
			repaint();
		}
	}

    public void repaintImage() {
    	updateImage();
		System.out.println("========== updateImage "+mCard.getName()+" =============");
    }
}