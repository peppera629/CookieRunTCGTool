package ui;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import dataStructure.Card;
import dataStructure.Deck;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class DeckWindow {

	private JFrame frame;
	private static DeckWindow window = new DeckWindow();
	private Deck mDeck;
	private String mDeckName;

	/**
	 * Launch the application.
	 */
	public static void show(Deck deck, String deckname) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					window.frame.setVisible(true);
					window.setDeck(deck, deckname);
					window.updateDeck();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private void setDeck(Deck deck, String deckname) {
		mDeck = deck;
		mDeckName = deckname;
		frame.setTitle(mDeckName);
	}

	/**
	 * Create the application.
	 */
	public DeckWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
    private JPanel mDeckPane;
	private JButton btnNewButton;
	private int w = 670;
	private int h = 550;
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(50, 50, w+10, h+80);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
        // ==== 卡組
        mDeckPane = new JPanel();
        mDeckPane.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        
        JScrollPane scrollDeckPane = new JScrollPane(mDeckPane);
        scrollDeckPane.setBounds(0, 0, w, h);
        scrollDeckPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollDeckPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        frame.getContentPane().add(scrollDeckPane);
        
        btnNewButton = new JButton("輸出圖檔");
        btnNewButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		outputImage();
        	}
        });
        btnNewButton.setBounds(5, h+10, w-15, 23);
        frame.getContentPane().add(btnNewButton);
	}


    private void updateDeck() {
        mDeckPane.removeAll();
        mDeckPane.setLayout(new GridLayout(0, 10, 5, 5));
        System.out.println("========== start updateDeck =============");
        for (Card card: mDeck.getAllCards()) {
            ClickableCardLabel cardLabel = new ClickableCardLabel(card.getcardIcon(), card);
            mDeckPane.add(cardLabel);
        }
        
        for(int i=mDeck.getCardCount(); i<18; i++) {
            String path = "resources/cards/empty.png";
            ImageIcon cardIcon = new ImageIcon(path);
            Image image = cardIcon.getImage().getScaledInstance(60, 84,  java.awt.Image.SCALE_SMOOTH);
            cardIcon = new ImageIcon(image);
            JLabel cardLabel = new JLabel(cardIcon);
            mDeckPane.add(cardLabel);
        }
        
        mDeckPane.revalidate();
        mDeckPane.repaint();
    }
    
    private void outputImage() {
        // 將 JPanel 內容繪製到 BufferedImage 上
        BufferedImage image = new BufferedImage(mDeckPane.getWidth(), mDeckPane.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        mDeckPane.printAll(g2d);
        g2d.dispose();
        
        // 將 BufferedImage 保存為圖檔
        File outputFile = new File("deck_image/"+mDeckName+".png");
        try {
            ImageIO.write(image, "png", outputFile);
            System.out.println("圖片已保存至 " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("保存圖片時出現錯誤: " + e.getMessage());
        }
    }

}
