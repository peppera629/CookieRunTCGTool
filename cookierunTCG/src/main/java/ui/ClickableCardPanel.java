package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import dataStructure.Card;
import dataStructure.CardLoader;
import dataStructure.Collection;

import util.Config;

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
	private float mCardSizeModifier = 1.0f;
	private int mDifferential;
	private int collectionChange;
	private Dimension cardListSize;
	private static boolean quickEditMode = false;
    ImageIcon mCardIcon;

	public ClickableCardPanel(Card card, int showCountMode, int cardSize, float cardSizeModifier, int differential) {
        mCard = card;
		mShowCountMode = showCountMode;
		mCardSize = cardSize;
		mCardSizeModifier = cardSizeModifier;
		mDifferential = differential;
		collectionChange = Collection.getInstance().getCardTotalChangeCount(card.getId());
    	mCardIcon = CardLoader.createCardImage(mCard, mCardSize, mCardSizeModifier);
		if (mCardIcon == null) {
            System.err.println("[ClickableCardPanel] NULL icon for id=" + card.getId()
                + " (CardUtil.CardBack might be null / init order issue)");
            mCardIcon = new ImageIcon(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));
        }
		cardListSize = new Dimension(mCardIcon.getIconWidth(), mCardIcon.getIconHeight());
        setPreferredSize(cardListSize);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (mCardListCallBack != null) {
                    	mCardListCallBack.addCard(mCard);
                    }
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    if (mCardListCallBack != null) {
                    	mCardListCallBack.removeCard(mCard);
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

		addMouseWheelListener(new MouseAdapter() {
			@Override
			public void mouseWheelMoved(java.awt.event.MouseWheelEvent e) {
				if (quickEditMode) {
					if (e.getWheelRotation() < 0) { // Scroll up
						if (mCardListCallBack != null) {
							mCardListCallBack.addCard(mCard);
						}
					} else { // Scroll down
						if (mCardListCallBack != null) {
							mCardListCallBack.removeCard(mCard);
						}
					}
					e.consume();
				} else {
					forwardMouseWheelEvent(e);
				}
			};
		});
    }

	private void forwardMouseWheelEvent(java.awt.event.MouseWheelEvent e) {
		JScrollPane scrollPane = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, this);
        if (scrollPane == null) {
            return;
        }

        Point p = SwingUtilities.convertPoint(this, e.getPoint(), scrollPane);

        MouseWheelEvent forwarded = new MouseWheelEvent(
            scrollPane,
            e.getID(),
            e.getWhen(),
            e.getModifiersEx(),
            p.x,
            p.y,
            e.getXOnScreen(),
            e.getYOnScreen(),
            e.getClickCount(),
            e.isPopupTrigger(),
            e.getScrollType(),
            e.getScrollAmount(),
            e.getWheelRotation()
        );

        scrollPane.dispatchEvent(forwarded);
    }

    public static void setQuickEditMode(boolean enabled) {
        quickEditMode = enabled;
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

				int changeBoxWidth = boxWidth;
				int changeBoxHeight = boxHeight/2;
				int changeBoxX = getWidth() - changeBoxWidth;
				int changeBoxY = boxY - changeBoxHeight;
				Color changeBoxColor = new Color(64, 64, 64, 192); // Default: translucent gray

				if (mCard.getMaxCount() == 0) { // Banned card
					boxColor = new Color(160, 0, 0, 192); // Translucent red
				} else if (mCard.getMaxCount() == 1) { // Restricted card
					boxColor = new Color(160, 128, 0, 192); // Translucent yellow
				}

				if (collectionChange > 0) {
					changeBoxColor = new Color(0, 160, 0, 192); // Translucent green
				} else if (collectionChange < 0) {
					changeBoxColor = new Color(160, 0, 0, 192); // Translucent red
				}
				
				GradientPaint extraGradient = new GradientPaint(boxX, boxY, new Color(80, 140, 241, 192), boxX + boxWidth, boxY + boxHeight, new Color(180, 60, 206, 192));

				if (mCard.isExtra()) {
					g2d.setPaint(extraGradient);
				} else {
					g2d.setColor(boxColor);
				}
				g2d.fillRect(boxX, boxY, boxWidth, boxHeight);

				if (mShowCountMode == 3 && Config.SHOW_COLLECTION_CHANGE) { // Collection Change Count
					g2d.setColor(changeBoxColor);
					g2d.fillRect(changeBoxX, changeBoxY, changeBoxWidth, changeBoxHeight);
				}
				
				// 在方塊上顯示白色數字
				g2d.setColor(Color.WHITE);
				Font cardPanelFont = MainUI.CRbold.deriveFont(mCardIcon.getIconWidth()/5f);
				Font cardPanelFontSmall = MainUI.CRnormal.deriveFont(mCardIcon.getIconWidth()/8f);
				String text = "";
				String ownedText = "";
				String collectionChangeText = "";
				boolean unchanged = false;
				switch (mShowCountMode) {
					case 1: // Deck Count
						text = Integer.toString(mCard.getCount());
						break;
					case 2: // Deck Count (with red indication for unowned/insufficient)
						text = Integer.toString(mCard.getCount());
						if (mCard.getCount() > Collection.getInstance().getCardTotalOwnedCount(mCard.getId(), true)) {
							g2d.setColor(new Color(255, 128, 128));
						}
						break;
					case 3: // Collection Count
						text = Integer.toString(Collection.getInstance().getCardTotalOwnedCount(mCard.getId(), Config.SHOW_ONLY_LEGAL_IN_COLLECTION));
						collectionChangeText = Integer.toString(collectionChange);
						if (collectionChange > 0) {
							collectionChangeText = "+" + collectionChangeText;
						} else if (collectionChange == 0) {
							collectionChangeText = "±" + collectionChangeText;
							unchanged = true;
						}
						break;
					case 4: // Both (for "build from collection" mode)
						text = Integer.toString(mCard.getCount());
						ownedText = "/" + Integer.toString(Collection.getInstance().getCardTotalOwnedCount(mCard.getId(), true));
						if (mCard.getCount() > Collection.getInstance().getCardTotalOwnedCount(mCard.getId(), true)) {
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

				g2d.setFont(cardPanelFontSmall);
				FontMetrics metricsOwned = g2d.getFontMetrics(cardPanelFontSmall);
				int ownedTextWidth = metricsOwned.stringWidth(ownedText);

				FontMetrics metricsChange = g2d.getFontMetrics(cardPanelFontSmall);
				int changeTextWidth = metricsChange.stringWidth(collectionChangeText);

				int totalTextWidth = textWidth + ownedTextWidth;
				int textX = boxX + (boxWidth - totalTextWidth) / 2;
				int textY = boxY + (boxHeight - metrics.getHeight()) / 2 + metrics.getAscent();

				int changeTextX = changeBoxX + (changeBoxWidth - changeTextWidth) / 2;
				int changeTextY = changeBoxY + (changeBoxHeight - metricsChange.getHeight()) / 2 + metricsChange.getAscent();
				
				g2d.setFont(cardPanelFont);
				g2d.drawString(text, textX, textY);

				g2d.setFont(cardPanelFontSmall);
				g2d.drawString(ownedText, textX + textWidth, textY);

				if (mShowCountMode == 3 && Config.SHOW_COLLECTION_CHANGE) { // Collection Change Count
					if (unchanged) {
						g2d.setColor(new Color(192, 192, 192));
					}
					g2d.drawString(collectionChangeText, changeTextX, changeTextY);
				}

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
    	mCardIcon = CardLoader.createCardImage(mCard, mCardSize, mCardSizeModifier);
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