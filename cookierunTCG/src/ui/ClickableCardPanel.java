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
    private boolean mShouldShowCount;
    private int mCardSize;
	private Dimension cardListSize;
    ImageIcon mCardIcon;

	public ClickableCardPanel(Card card, boolean showCount, int cardSize) {
        mCard = card;
    	mShouldShowCount = showCount;
		mCardSize = cardSize;
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

        if(mShouldShowCount) {
	        Graphics2D g2d = (Graphics2D) g.create();
			try {
				g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING, java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				int boxWidth = mCardIcon.getIconWidth()/3;
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
				g2d.setFont(cardPanelFont);
				String text = Integer.toString(mCard.getCount());
				FontMetrics metrics = g2d.getFontMetrics(cardPanelFont);
				int textWidth = metrics.stringWidth(text);
				int textHeight = metrics.getHeight();
				int textX = boxX + (boxWidth - textWidth) / 2;
				int textY = boxY + (boxHeight - textHeight) / 2 + metrics.getAscent();
				g2d.drawString(text, textX, textY);
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
    public void repaintImage() {
    	updateImage();
		System.out.println("========== updateImage "+mCard.getName()+" =============");
    }
}