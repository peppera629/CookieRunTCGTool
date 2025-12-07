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
import util.Config;
import util.UIUtil;
import util.CardUtil;
import ui.MainUI;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class DeckWindow {

	private JFrame frame;
	private Deck mDeck;
	private String mDeckName;

	/**
	 * Launch the application.
	 */
	public void show(Deck deck, String deckname) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					setDeck(deck, deckname);
					initialize();
					frame.setVisible(true);
					updateDeck();
					updateOutputDeck();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void setDeck(Deck deck, String deckname) {
		mDeck = deck;
		mDeckName = deckname;
		if (frame != null) {
			frame.setTitle(mDeckName);
		}
	}

	/**
	 * Create the application.
	 */
	public DeckWindow() {
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private JPanel mDeckPane;
	private JPanel mOutputPane;
	private JScrollPane scrollDeckPane;
	private JButton btnNewButton;
	private int w = 670;
	private int h = 550;

	public void initialize() {
		if(frame != null) {
			frame.setVisible(false);
		}
		frame = new JFrame();
		frame.setTitle(mDeckName);
		frame.setResizable(false);
		int anotherLine = 0;
		if ((mDeck.getCardArrayListSize() % Config.DW_ROW_SIZE) > 0) {
			anotherLine++;
		}
		w = (Config.DW_CARD_WIDTH + 5) * Config.DW_ROW_SIZE + 20;
		h = (Config.DW_CARD_HEIGHT + 5) * ((mDeck.getCardArrayListSize() / Config.DW_ROW_SIZE) + anotherLine) + 20;

		frame.setSize(w + 10, h + 70);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		// ==== 卡組
		mDeckPane = new JPanel();

		JScrollPane scrollDeckPane = new JScrollPane(mDeckPane);
		scrollDeckPane.setBounds(0, 0, w, h);
		scrollDeckPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollDeckPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		frame.getContentPane().add(scrollDeckPane);

		btnNewButton = new JButton(CardUtil.getTranslation("deck.export"));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				outputImage();
			}
		});
		btnNewButton.setBounds(4, h, w - 15, 30);
		btnNewButton.setFont(MainUI.CRbold);
		frame.getContentPane().add(btnNewButton);
		createOutputWindow();
	}

	private void updateDeck() {
		mDeckPane.removeAll();

        UIUtil.showDeck(null, mDeckPane, mDeck.getAllCards(), null, 6, Config.DW_ROW_SIZE, UIUtil.CARD_SIZE_DECK, 1, false);

		mDeckPane.revalidate();
		mDeckPane.repaint();
	}

	private void outputImage() {
		// 將 JPanel 內容繪製到 BufferedImage 上
		BufferedImage image = new BufferedImage(mOutputPane.getWidth(), mOutputPane.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = image.createGraphics();
		mOutputPane.printAll(g2d);
		g2d.dispose();

		// 將 BufferedImage 保存為圖檔
		if (!new File("deck_image").exists()) {
			new File("deck_image").mkdirs();
		}
		File outputFile = new File("deck_image/" + mDeckName + ".png");
		try {
			ImageIO.write(image, "png", outputFile);
			System.out.println("Deck overview saved to " + outputFile.getAbsolutePath());
			Dialog dialog = new Dialog();
			dialog.show(CardUtil.getTranslation("deck.imagesaved") + outputFile.getAbsolutePath());
		} catch (IOException e) {
			System.out.println("Deck saving failed: " + e.getMessage());
		}
	}

	private void createOutputWindow() {
		int anotherLine = 0;
		if ((mDeck.getCardArrayListSize() % Config.DW_ROW_SIZE) > 0) {
			anotherLine++;
		}
		int output_w = (Config.DW_OUTPUT_WIDTH + 5) * Config.DW_ROW_SIZE + 20;
		int output_h = (Config.DW_OUTPUT_HEIGHT + 5) * ((mDeck.getCardArrayListSize() / Config.DW_ROW_SIZE) + anotherLine) + 20;

		mOutputPane = new JPanel();
		mOutputPane.setLayout(new GridLayout(0, Config.DW_ROW_SIZE, 5, 5));
		mOutputPane.setBounds(w + 100, h+100, output_w + 10, output_h + 80);

		frame.getContentPane().add(mOutputPane);
	}
	
	private void updateOutputDeck() {

		mOutputPane.removeAll();
		
        UIUtil.showDeck(null, mOutputPane, mDeck.getAllCards(), null, 6, Config.DW_ROW_SIZE, UIUtil.CARD_SIZE_OUTPUT, 1, false);

		mOutputPane.revalidate();
		mOutputPane.repaint();
	}

}
