package ui;

import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import dataStructure.Deck;
import util.Config;
import util.UIUtil;
import util.CardUtil;
import util.AppPaths;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class RandomDrawSim {

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
					updateStartingHand(false);
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
	public RandomDrawSim() {
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private JPanel randomDrawPane;
	private JButton redrawButton, mulliganButton;
	private int w = 670;
	private int h = 550;

	public void initialize() {
		if(frame != null) {
			frame.setVisible(false);
		}
		frame = new JFrame();
		frame.setTitle(mDeckName + " - " + CardUtil.getTranslation("deck.drawsim"));
		frame.setResizable(false);
		w = (int)(Config.DW_CARD_WIDTH * 1.5f + 5) * 6 + 20;
		h = (int)(Config.DW_CARD_HEIGHT * 1.5f + 5);

		frame.setSize(w + 10, h + 70);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		randomDrawPane = new JPanel();
		randomDrawPane.setBounds(0, 0, w, h);
		frame.getContentPane().add(randomDrawPane);

		JPanel drawSimButtons = new JPanel();
		drawSimButtons.setBounds(2, h, w - 10, 30);
		frame.getContentPane().add(drawSimButtons);
		drawSimButtons.setLayout(new GridLayout(1, 1, 0, 0));

		redrawButton = new JButton(CardUtil.getTranslation("deck.redraw"));
		redrawButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateStartingHand(false);
				//mulliganButton.setText(CardUtil.getTranslation("deck.mulligan") + " (#1)");
				//mulliganButton.setEnabled(true);
			}
		});
		redrawButton.setFont(MainUI.CRbold);
		drawSimButtons.add(redrawButton);
		
		// This has the wrong logic, disabled for now (not that it's needed)
		// Can be repurposed to make a "draw 1 card" function though (make it possible to stack card panels first instead of it being in a grid)
		/* 
		mulliganButton = new JButton(CardUtil.getTranslation("deck.mulligan") + " (#" + (mDeck.getMulliganCount() + 1) + ")");
		mulliganButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateStartingHand(true);
				mulliganButton.setText(CardUtil.getTranslation("deck.mulligan") + " (#" + (mDeck.getMulliganCount() + 1) + ")");
				if (mDeck.isValidHand() || mDeck.getMulliganCount() >= 9) {
					if (mDeck.getMulliganCount() >= 9) {
						mulliganButton.setText(CardUtil.getTranslation("deck.mulligan"));
					}
					mulliganButton.setEnabled(false);
				}
			}
		});
		mulliganButton.setFont(MainUI.CRbold);
		drawSimButtons.add(mulliganButton);
		 */
	}

	private void updateStartingHand(boolean mulligan) {
		randomDrawPane.removeAll();

        UIUtil.showDeck(null, randomDrawPane, mDeck.getRandomStartingHand(mulligan), null, 6, 6, UIUtil.CARD_SIZE_DECK, 1.5f, 0, false);

		randomDrawPane.revalidate();
		randomDrawPane.repaint();
	}
}
