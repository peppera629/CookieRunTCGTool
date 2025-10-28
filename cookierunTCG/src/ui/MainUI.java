package ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import java.awt.BorderLayout;

import javax.swing.Box;
import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;

import dataStructure.Card;
import dataStructure.CardList;
import dataStructure.CardLoader;
import dataStructure.Deck;

import javax.swing.JPanel;
import javax.swing.JScrollBar;

import java.awt.Panel;
import java.util.List;
import java.util.concurrent.Flow;
import java.awt.ScrollPane;
import java.awt.TextField;

import ui.ClickableCardPanel.CardListCallBack;
import ui.SortSettingsWindow.ConfigChangedCallback;
import util.CardUtil.CardColor;
import util.CardUtil.CardType;
import util.CardUtil;
import util.Config;
import util.Constant;
import util.DefaultState;
import util.LanguageChangeListener;
import util.UIUtil;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JButton;

// TODO: Add EN card images for BS1~BS7, ST1~ST5, P
// TODO: Fix filtering sidebar alignment
// TODO: Add EXTRA card counter
// TODO: Add EXTRA card count constraint

public class MainUI implements CardListCallBack, ConfigChangedCallback, LanguageChangeListener {

	private static boolean DEBUG = false;
    private JFrame frame;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        System.setProperty("file.encoding", "UTF-8");

    	
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    MainUI window = new MainUI();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public MainUI() {
        loadFont();
		deckWindow = new DeckWindow();
        settingsWindow = new SettingsWindow();
		sortSettingsWindow = new SortSettingsWindow();
		sortSettingsWindow.setConfigChangedCallback(this);
        SettingsWindow.addLanguageChangeListener(this);
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */

	private DeckWindow deckWindow;
    private SettingsWindow settingsWindow;
	private SortSettingsWindow sortSettingsWindow;
    private DefaultState mDefaultState;
    private JPanel mCardsPane, mDeckPane, mTextsPane;
    
    //search panel
    private Panel mSearchPane;
    private JCheckBox[] cb_color;
    private JCheckBox[] cb_level;
    private JCheckBox[] cb_pack;
    private JCheckBox cb_type_cookie, cb_type_item, cb_type_trap, cb_type_stage;
    private JCheckBox cb_flip, cb_extra;
    private JLabel labelColor, labelType, labelSeries;

    private Deck mDeck;
    private ScrollPane scrollPane;
    private Panel mCardDetailPane;
    private Panel panel;
    private TextField mDeckText;
    private JButton loadBtn, saveBtn, selectBtn;
    private JButton mClearDeckBtn, button_search, button_clean;
    private JLabel mCardCountHintTxt, mFlipCountHintTxt, mDeckCookieSummaryHintTxt, mDeckCookieLv1HintTxt, mDeckCookieLv2HintTxt, mDeckCookieLv3HintTxt;
    private JLabel mDeckItemHintTxt, mDeckTrapHintTxt, mDeckStageHintTxt;
    private JLabel mCardCountTxt, mFlipCountTxt, mDeckCookieSummaryTxt, mDeckCookieLv1Txt, mDeckCookieLv2Txt, mDeckCookieLv3Txt;
    private JLabel mDeckItemTxt, mDeckTrapTxt, mDeckStageTxt;
    private JButton showDeckBtn;
    private JMenuItem settingsMenuItem, sortSettingsMenuItem;
    public static Font CRnormal, CRbold, CRnormalLarge, CRnormalSmall, CRnormalEXLarge;
    public static InputStream fontStream, fontStreamBold;
    public static Map<java.awt.Component, String> componentFontMap = new HashMap<>();
    private int columns = 6;

    private void initialize() {
    	initialData();
    	initialUI();
    }

    private void initialData() {
    	CardLoader.loadAllPacks();
    	mDefaultState = DefaultState.getInstance();
        mDeck = new Deck();
        frame = new JFrame();
    }
    
    public static void loadFont() {
        try {
            // Use ClassLoader to load the font as a resource
            switch (Config.LANGUAGE) {
                case "zh_TW":
                    fontStream = MainUI.class.getClassLoader().getResourceAsStream("fonts/NotoSansTC-SemiBold.ttf");
                    fontStreamBold = MainUI.class.getClassLoader().getResourceAsStream("fonts/NotoSansTC-Bold.ttf");
                    break;
                default:
                    fontStream = MainUI.class.getClassLoader().getResourceAsStream("fonts/CookieRunRegular.ttf");
                    fontStreamBold = MainUI.class.getClassLoader().getResourceAsStream("fonts/CookieRunBold.ttf");
            }
            if (fontStream == null) {
                throw new IOException("Font file not found");
            }
            CRnormal = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(14f);
            CRnormalSmall = CRnormal.deriveFont(10f);
            CRnormalLarge = CRnormal.deriveFont(18f);
            CRnormalEXLarge = CRnormal.deriveFont(24f);
            CRbold = Font.createFont(Font.TRUETYPE_FONT, fontStreamBold).deriveFont(14f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(CRnormal);
            ge.registerFont(CRnormalSmall);
            ge.registerFont(CRnormalLarge);
            ge.registerFont(CRbold);
        } catch (Exception e) {
            e.printStackTrace();
            CRnormal = new Font("Arial", Font.PLAIN, 14); // Fallback font
            CRnormalLarge = CRnormal.deriveFont(18f);
        }
    }

    private void initialUI() {

        frame.setTitle(CardUtil.getTranslation("app.title") + " v." + Constant.VERSION);
        frame.setBounds(100, 100, 1440, 720);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        
        mSearchPane = new Panel();
        frame.getContentPane().add(mSearchPane, BorderLayout.WEST);
        mSearchPane.setLayout(new BoxLayout(mSearchPane, BoxLayout.Y_AXIS));

        initCheckBox();
        createMenu();

        JPanel searchPanelButtons = new JPanel();
        searchPanelButtons.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        mSearchPane.add(searchPanelButtons);
        
        button_search = new JButton(CardUtil.getTranslation("search"));
        button_search.setFont(CRnormalLarge);
        componentFontMap.put(button_search, "CRnormalLarge"); // Store the font type as a String
        searchPanelButtons.add(button_search);
        button_search.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	updateCardList();
                mDefaultState.saveDefaultState();
            }
        });
        
        button_clean = new JButton(CardUtil.getTranslation("clear"));
        button_clean.setFont(CRnormalLarge);
        componentFontMap.put(button_clean, "CRnormalLarge"); // Store the font type as a String
        searchPanelButtons.add(button_clean);
        
        button_clean.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	cleanCheckBox();
            	updateCardList();
                mDefaultState.cleanSearchFlag();
                mDefaultState.saveDefaultState();
            }
        });
        
        // ===== 中間區域 =====

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(null);
        frame.getContentPane().add(centerPanel, BorderLayout.CENTER);

        // ==== 卡組
        mDeckPane = new JPanel();
        mDeckPane.setLayout(new GridLayout(0, 6, 5, 5));
        JScrollPane scrollDeckPane = new JScrollPane(mDeckPane);
        scrollDeckPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollDeckPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JScrollBar deckScrollBar = scrollDeckPane.getVerticalScrollBar();
        deckScrollBar.setUnitIncrement(16);
        centerPanel.add(scrollDeckPane);

        // ==== 卡組資訊
        JPanel deckDetailPane = new JPanel();
        deckDetailPane.setLayout(new BorderLayout());
        centerPanel.add(deckDetailPane);

        mClearDeckBtn = new JButton(CardUtil.getTranslation("deck.clear"));
        mClearDeckBtn.setFont(CRnormalLarge);
        componentFontMap.put(mClearDeckBtn, "CRnormalLarge"); // Store the font type as a String
        deckDetailPane.add(mClearDeckBtn, BorderLayout.SOUTH);

        mTextsPane = new JPanel();
        mTextsPane.setLayout(new GridBagLayout());
        deckDetailPane.add(mTextsPane, BorderLayout.CENTER);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.15;
        gbc.weighty = 0.3;
        mCardCountHintTxt = new JLabel(CardUtil.getTranslation("deck.cards"));
        mCardCountHintTxt.setFont(CRnormalSmall);
        componentFontMap.put(mCardCountHintTxt, "CRnormalSmall"); // Store the font type as a String
        mTextsPane.add(mCardCountHintTxt, gbc);

        gbc.gridx = 1;
        mFlipCountHintTxt = new JLabel(CardUtil.getTranslation("deck.flip"));
        mFlipCountHintTxt.setFont(CRnormalSmall);
        componentFontMap.put(mFlipCountHintTxt, "CRnormalSmall"); // Store the font type as a String
        mTextsPane.add(mFlipCountHintTxt, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.1;
        mDeckCookieSummaryHintTxt = new JLabel(CardUtil.getTranslation("deck.cookies"));
        mDeckCookieSummaryHintTxt.setFont(CRnormalSmall);
        componentFontMap.put(mDeckCookieSummaryHintTxt, "CRnormalSmall"); // Store the font type as a String
        mTextsPane.add(mDeckCookieSummaryHintTxt, gbc);

        gbc.gridx = 3;
        mDeckCookieLv1HintTxt = new JLabel(CardUtil.getTranslation("deck.lv1"));
        mDeckCookieLv1HintTxt.setFont(CRnormalSmall);
        componentFontMap.put(mDeckCookieLv1HintTxt, "CRnormalSmall"); // Store the font type as a String
        mTextsPane.add(mDeckCookieLv1HintTxt, gbc);

        gbc.gridx = 4;
        mDeckCookieLv2HintTxt = new JLabel(CardUtil.getTranslation("deck.lv2"));
        mDeckCookieLv2HintTxt.setFont(CRnormalSmall);
        componentFontMap.put(mDeckCookieLv2HintTxt, "CRnormalSmall"); // Store the font type as a String
        mTextsPane.add(mDeckCookieLv2HintTxt, gbc);

        gbc.gridx = 5;
        mDeckCookieLv3HintTxt = new JLabel(CardUtil.getTranslation("deck.lv3"));
        mDeckCookieLv3HintTxt.setFont(CRnormalSmall);
        componentFontMap.put(mDeckCookieLv3HintTxt, "CRnormalSmall"); // Store the font type as a String
        mTextsPane.add(mDeckCookieLv3HintTxt, gbc);

        gbc.gridx = 6;
        mDeckItemHintTxt = new JLabel(CardUtil.getTranslation("deck.items"));
        mDeckItemHintTxt.setFont(CRnormalSmall);
        componentFontMap.put(mDeckItemHintTxt, "CRnormalSmall"); // Store the font type as a String
        mTextsPane.add(mDeckItemHintTxt, gbc);

        gbc.gridx = 7;
        mDeckTrapHintTxt = new JLabel(CardUtil.getTranslation("deck.traps"));
        mDeckTrapHintTxt.setFont(CRnormalSmall);
        componentFontMap.put(mDeckTrapHintTxt, "CRnormalSmall"); // Store the font type as a String
        mTextsPane.add(mDeckTrapHintTxt, gbc);

        gbc.gridx = 8;
        mDeckStageHintTxt = new JLabel(CardUtil.getTranslation("deck.stages"));
        mDeckStageHintTxt.setFont(CRnormalSmall);
        componentFontMap.put(mDeckStageHintTxt, "CRnormalSmall"); // Store the font type as a String
        mTextsPane.add(mDeckStageHintTxt, gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.weightx = 0.15;
        gbc.weighty = 0.7;
        mCardCountTxt = new JLabel("0/60");
        mCardCountTxt.setFont(CRnormalEXLarge);
        componentFontMap.put(mCardCountTxt, "CRnormalEXLarge"); // Store the font type as a String
        mTextsPane.add(mCardCountTxt, gbc);

        gbc.gridx = 1;
        mFlipCountTxt = new JLabel("0/16");
        mFlipCountTxt.setFont(CRnormalEXLarge);
        componentFontMap.put(mFlipCountTxt, "CRnormalEXLarge"); // Store the font type as a String
        mTextsPane.add(mFlipCountTxt, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.1;
        mDeckCookieSummaryTxt = new JLabel("0");
        mDeckCookieSummaryTxt.setFont(CRnormalEXLarge);
        componentFontMap.put(mDeckCookieSummaryTxt, "CRnormalEXLarge"); // Store the font type as a String
        mTextsPane.add(mDeckCookieSummaryTxt, gbc);

        gbc.gridx = 3;
        mDeckCookieLv1Txt = new JLabel("0");
        mDeckCookieLv1Txt.setFont(CRnormalEXLarge);
        componentFontMap.put(mDeckCookieLv1Txt, "CRnormalEXLarge"); // Store the font type as a String
        mTextsPane.add(mDeckCookieLv1Txt, gbc);

        gbc.gridx = 4;
        mDeckCookieLv2Txt = new JLabel("0");
        mDeckCookieLv2Txt.setFont(CRnormalEXLarge);
        componentFontMap.put(mDeckCookieLv2Txt, "CRnormalEXLarge"); // Store the font type as a String
        mTextsPane.add(mDeckCookieLv2Txt, gbc);

        gbc.gridx = 5;
        mDeckCookieLv3Txt = new JLabel("0");
        mDeckCookieLv3Txt.setFont(CRnormalEXLarge);
        componentFontMap.put(mDeckCookieLv3Txt, "CRnormalEXLarge"); // Store the font type as a String
        mTextsPane.add(mDeckCookieLv3Txt, gbc);

        gbc.gridx = 6;
        mDeckItemTxt = new JLabel("0");
        mDeckItemTxt.setFont(CRnormalEXLarge);
        componentFontMap.put(mDeckItemTxt, "CRnormalEXLarge"); // Store the font type as a String
        mTextsPane.add(mDeckItemTxt, gbc);

        gbc.gridx = 7;
        mDeckTrapTxt = new JLabel("0");
        mDeckTrapTxt.setFont(CRnormalEXLarge);
        componentFontMap.put(mDeckTrapTxt, "CRnormalEXLarge"); // Store the font type as a String
        mTextsPane.add(mDeckTrapTxt, gbc);

        gbc.gridx = 8;
        mDeckStageTxt = new JLabel("0");
        mDeckStageTxt.setFont(CRnormalEXLarge);
        componentFontMap.put(mDeckStageTxt, "CRnormalEXLarge"); // Store the font type as a String
        mTextsPane.add(mDeckStageTxt, gbc);

        // ==== 卡片列表
        mCardsPane = new JPanel();
        mCardsPane.setLayout(new GridLayout(0, 4, 5, 5));
        
        JScrollPane scrollCardsPane = new JScrollPane(mCardsPane);
        scrollCardsPane.setBackground(new Color(255, 255, 255));
        scrollCardsPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollCardsPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JScrollBar cardListScrollBar = scrollCardsPane.getVerticalScrollBar();
        cardListScrollBar.setUnitIncrement(16);
        centerPanel.add(scrollCardsPane);

        // Add a ComponentListener to dynamically resize panes
        frame.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int width = centerPanel.getWidth();
                int height = centerPanel.getHeight();
                System.out.println(width + "x" + height);

                // Calculate heights for each pane
                int deckDetailPaneHeight = 120; // Fixed height
                columns = Math.max(1, (int) Math.floor(width / (Config.SMALL_CARD_WIDTH + 10))); // At least 1 column
                int deckPaneHeight = (int) (height - deckDetailPaneHeight) / 2; // 50% of the height
                int cardsPaneHeight = height - deckPaneHeight - deckDetailPaneHeight; // Remaining height

                // Set bounds for each pane
                scrollDeckPane.setBounds(0, 0, width, deckPaneHeight);
                deckDetailPane.setBounds(0, deckPaneHeight, width, deckDetailPaneHeight);
                scrollCardsPane.setBounds(0, deckPaneHeight + deckDetailPaneHeight, width, cardsPaneHeight);

                // Update the layouts with the new column count
                GridLayout deckLayout = (GridLayout) mDeckPane.getLayout();
                GridLayout cardsLayout = (GridLayout) mCardsPane.getLayout();
                
                if (deckLayout.getColumns() != columns) {
                    deckLayout.setColumns(columns);
                    mDeckPane.revalidate();
                    mDeckPane.repaint();
                }

                if (cardsLayout.getColumns() != columns) {
                    cardsLayout.setColumns(columns);
                    mCardsPane.revalidate();
                    mCardsPane.repaint();
                }
                
                // Revalidate and repaint to apply changes
                centerPanel.revalidate();
                centerPanel.repaint();
            }
        });

        // Trigger an initial resize to set the correct sizes
        frame.getComponentListeners()[0].componentResized(null);

        frame.getContentPane().add(centerPanel, BorderLayout.CENTER);
        
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));

        // ==== 卡片預覽
        mCardDetailPane = new Panel();
        mCardDetailPane.setLayout(new BorderLayout());
        sidebarPanel.add(mCardDetailPane);

        // ===== 檔案
        panel = new Panel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panel.setBackground(new Color(255, 255, 255));
        sidebarPanel.add(panel);
        
        mDeckText = new TextField();
        mDeckText.setText(mDefaultState.getDeckDefaultName());
        panel.add(mDeckText);
        
        loadBtn = new JButton(CardUtil.getTranslation("load"));
        loadBtn.setFont(CRnormal);
        componentFontMap.put(loadBtn, "CRnormal"); // Store the font type as a String
        panel.add(loadBtn);
        loadBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mDeck = CardLoader.loadDeck(mDeckText.getText());
                mDeck.sort();
                updateDeck();
                mDefaultState.setDefaultDeckName(mDeckText.getText());
                mDefaultState.saveDefaultState();
            }
        });
        
        saveBtn = new JButton(CardUtil.getTranslation("save"));
        saveBtn.setFont(CRnormal);
        componentFontMap.put(saveBtn, "CRnormal"); // Store the font type as a String
        panel.add(saveBtn);
        saveBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CardLoader.saveDeck(mDeckText.getText(), mDeck);
                mDefaultState.setDefaultDeckName(mDeckText.getText());
                mDefaultState.saveDefaultState();
            }
        });

        selectBtn = new JButton(CardUtil.getTranslation("select.file"));
        selectBtn.setFont(CRnormal);
        componentFontMap.put(selectBtn, "CRnormal"); // Store the font type as a String
        selectBtn.setActionCommand("Select File");
        panel.add(selectBtn);
        selectBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File("deck/"));
				int returnValue = fileChooser.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION)
				{ 
					File selectedFile = fileChooser.getSelectedFile();
					String filename = selectedFile.getName();
					System.out.println(selectedFile.getName());
					mDeckText.setText(filename.substring(0, filename.length() - 4));
				} 
            }
        });
        
        showDeckBtn = new JButton(CardUtil.getTranslation("deck.show"));
        showDeckBtn.setFont(CRnormal);
        componentFontMap.put(showDeckBtn, "CRnormal"); // Store the font type as a String
        panel.add(showDeckBtn);
        showDeckBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		deckWindow.show(mDeck, mDeckText.getText());
        	}
        });
        
        panel.add(showDeckBtn);
        mClearDeckBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mDeck.clear();
                updateDeck();
            }
        });

        frame.getContentPane().add(sidebarPanel, BorderLayout.EAST);

        updateCardList();
        
        mDeck = CardLoader.loadDeck(mDeckText.getText());
        mDeck.sort();
        updateDeck();
    }

    
	int padding = 5;
	int paddingToWindow = 10;
	int widthFull = 210;
	int widthOneWord = 40;
	int widthTwoWord = 50;
	int height = 22;
	int x = paddingToWindow, y = paddingToWindow;
	int maxObjectForALine = 4;
	
    private void initCheckBox() {
    	
        // ========================= color ==================================
        labelColor = new JLabel(CardUtil.getTranslation("color"), JLabel.LEFT);
        labelColor.setFont(CRnormalLarge);
        componentFontMap.put(labelColor, "CRnormalLarge"); // Store the font type as a String
        labelColor.setAlignmentX(Component.LEFT_ALIGNMENT);
        mSearchPane.add(labelColor);

        JPanel colorOuterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // Wrap the grid
        JPanel colorCheckboxGroup = new JPanel();
        colorCheckboxGroup.setLayout(new GridLayout(0, 3));
        colorOuterPanel.add(colorCheckboxGroup);
        mSearchPane.add(colorOuterPanel);

        cb_color = new JCheckBox[CardUtil.COLOR_MAX];
        for(int i=0; i<CardUtil.COLOR_MAX; i++) {
        	cb_color[i] = new JCheckBox(CardUtil.CardColor.fromValue(i).getDisplayName());
        	cb_color[i].setSelected(mDefaultState.getDefaultColorFlag(i));
            cb_color[i].setFont(CRnormal);
            componentFontMap.put(cb_color[i], "CRnormal"); // Store the font type as a String
            colorCheckboxGroup.add(cb_color[i]);
            final int id = i;
            cb_color[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	mDefaultState.setDefaultColorFlag(id, cb_color[id].isSelected());
                }
            });
        }

        mSearchPane.add(Box.createRigidArea(new Dimension(0, 10))); // 10px vertical space
        
        // ========================= type ==================================
        labelType = new JLabel(CardUtil.getTranslation("type"), JLabel.LEFT);
        labelType.setFont(CRnormalLarge);
        componentFontMap.put(labelType, "CRnormalLarge"); // Store the font type as a String
        mSearchPane.add(labelType);

        JPanel typeOuterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // Wrap the grid
        JPanel typeCheckboxGroup = new JPanel();
        typeCheckboxGroup.setLayout(new GridLayout(0, 4));
        typeOuterPanel.add(typeCheckboxGroup);
        mSearchPane.add(typeOuterPanel);

        cb_type_cookie = new JCheckBox(CardUtil.getTranslation("filter.cookie"));
		cb_type_cookie.setSelected(mDefaultState.getDefaultTypeFlag(0));
        cb_type_cookie.setFont(CRnormal);
        componentFontMap.put(cb_type_cookie, "CRnormal"); // Store the font type as a String
        typeCheckboxGroup.add(cb_type_cookie);
        cb_type_cookie.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	mDefaultState.setDefaultTypeFlag(0, cb_type_cookie.isSelected());

            	for (JCheckBox cb : cb_level) {
            		cb.setEnabled(cb_type_cookie.isSelected());
            	}
            }
        });
        
        cb_level = new JCheckBox[CardUtil.LEVEL_MAX];
        for(int i=0; i<CardUtil.LEVEL_MAX; i++) {
        	final int lv = i+1;
            final int id = i;
        	cb_level[i] = new JCheckBox("Lv." + lv);
        	cb_level[i].setSelected(mDefaultState.getDefaultLvFlag(lv));
            cb_level[i].setFont(CRnormal);
            componentFontMap.put(cb_level[i], "CRnormal"); // Store the font type as a String
            typeCheckboxGroup.add(cb_level[i]);
            cb_level[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	mDefaultState.setDefaultLvFlag(lv, cb_level[id].isSelected());
                }
            });
            cb_level[i].setEnabled(cb_type_cookie.isSelected());
            
        }

        cb_flip = new JCheckBox(CardUtil.getTranslation("filter.flip"));
        cb_flip.setSelected(mDefaultState.getDefaultFlipFlag());
        cb_flip.setFont(CRnormal);
        componentFontMap.put(cb_flip, "CRnormal"); // Store the font type as a String
        typeCheckboxGroup.add(cb_flip);
        cb_flip.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	mDefaultState.setDefaultFlipFlag(cb_flip.isSelected());
            }
        });

        cb_extra = new JCheckBox(CardUtil.getTranslation("filter.extra"));
        cb_extra.setSelected(mDefaultState.getDefaultExtraFlag());
        cb_extra.setFont(CRnormal);
        componentFontMap.put(cb_extra, "CRnormal"); // Store the font type as a String
        typeCheckboxGroup.add(cb_extra);
        cb_extra.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	mDefaultState.setDefaultExtraFlag(cb_extra.isSelected());
            }
        });

        cb_type_item = new JCheckBox(CardUtil.getTranslation("filter.item"));
        cb_type_item.setSelected(mDefaultState.getDefaultTypeFlag(1));
        cb_type_item.setFont(CRnormal);
        componentFontMap.put(cb_type_item, "CRnormal"); // Store the font type as a String
        typeCheckboxGroup.add(cb_type_item);
        cb_type_item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	mDefaultState.setDefaultTypeFlag(1, cb_type_item.isSelected());
            }
        });


        cb_type_trap = new JCheckBox(CardUtil.getTranslation("filter.trap"));
        cb_type_trap.setSelected(mDefaultState.getDefaultTypeFlag(2));
        cb_type_trap.setFont(CRnormal);
        componentFontMap.put(cb_type_trap, "CRnormal"); // Store the font type as a String
        typeCheckboxGroup.add(cb_type_trap);
        cb_type_trap.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	mDefaultState.setDefaultTypeFlag(2, cb_type_trap.isSelected());
            }
        });


        cb_type_stage = new JCheckBox(CardUtil.getTranslation("filter.stage"));
        cb_type_stage.setSelected(mDefaultState.getDefaultTypeFlag(3));
        cb_type_stage.setFont(CRnormal);
        componentFontMap.put(cb_type_stage, "CRnormal"); // Store the font type as a String
        typeCheckboxGroup.add(cb_type_stage);
        cb_type_stage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	mDefaultState.setDefaultTypeFlag(3, cb_type_stage.isSelected());
            }
        });

        mSearchPane.add(Box.createRigidArea(new Dimension(0, 10))); // 10px vertical space

        // ========================= pack ==================================


        labelSeries = new JLabel(CardUtil.getTranslation("series"), JLabel.LEFT);
        labelSeries.setFont(CRnormalLarge);
        componentFontMap.put(labelSeries, "CRnormalLarge"); // Store the font type as a String
        mSearchPane.add(labelSeries);

        JPanel packOuterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // Wrap the grid
        JPanel packCheckboxGroup = new JPanel();
        packCheckboxGroup.setLayout(new GridLayout(0, 4));
        packOuterPanel.add(packCheckboxGroup);
        mSearchPane.add(packOuterPanel);

        cb_pack = new JCheckBox[CardUtil.CardPack.size()];
        for(int i=0; i<CardUtil.CardPack.size(); i++) {
        	final int id = i;
        	cb_pack[i] = new JCheckBox(CardUtil.CardPack.get(i));
        	cb_pack[i].setSelected(mDefaultState.getDefaultPackFlag(CardUtil.CardPack.get(i)));
            cb_pack[i].setFont(CRnormal);
            componentFontMap.put(cb_pack[i], "CRnormal"); // Store the font type as a String
            packCheckboxGroup.add(cb_pack[i]);
            cb_pack[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	mDefaultState.setDefaultPackFlag(CardUtil.CardPack.get(id), cb_pack[id].isSelected());
                }
            });
        }
    }
    
    private void createMenu() {

        // 創建選單列
        JMenuBar menuBar = new JMenuBar();
        
        // 創建選單
//        JMenu settingsMenu = new JMenu("設定");
        sortSettingsMenuItem = new JMenuItem(CardUtil.getTranslation("sort.settings"));
        sortSettingsMenuItem.setFont(CRnormal);
        componentFontMap.put(sortSettingsMenuItem, "CRnormal"); // Store the font type as a String
        menuBar.add(sortSettingsMenuItem);
//        JMenuItem aboutMenu = new JMenuItem("關於");
//        menuBar.add(aboutMenu);
        
        // 創建一個 ActionListener 來處理 Settings 選項的事件
        sortSettingsMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Sort settings clicked");
            	sortSettingsWindow.show();
            }
        });

        settingsMenuItem = new JMenuItem(CardUtil.getTranslation("settings"));
        settingsMenuItem.setFont(CRnormal);
        componentFontMap.put(settingsMenuItem, "CRnormal"); // Store the font type as a String
        menuBar.add(settingsMenuItem);
//        JMenuItem aboutMenu = new JMenuItem("關於");
//        menuBar.add(aboutMenu);
        
        // 創建一個 ActionListener 來處理 Settings 選項的事件
        settingsMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Settings clicked");
            	settingsWindow.show();
            }
        });
        
        // 設置選單列
        frame.setJMenuBar(menuBar);
    }

    private void cleanCheckBox() {
    	for (JCheckBox cb : cb_color) {
    		cb.setSelected(false);
    	}
    	
    	cb_type_cookie.setSelected(false);
    	cb_type_item.setSelected(false);
    	cb_type_trap.setSelected(false);
    	cb_type_stage.setSelected(false);

    	for (JCheckBox cb : cb_level) {
    		cb.setSelected(false);
    	}

    	cb_flip.setSelected(false);

        for (JCheckBox cb : cb_pack) {
        	cb.setSelected(false);
        }

    }
    
    private void updateCardList() {
        System.out.println("========== start getSelectCards =============");
        CardList list = CardList.getInstance();
    	for (int i=0; i< cb_color.length; i++) {
            list.setColor(i, cb_color[i].isSelected());
    	}
        list.setType(CardType.Cookie.getValue(), cb_type_cookie.isSelected());
        list.setType(CardType.Item.getValue(), cb_type_item.isSelected());
        list.setType(CardType.Trap.getValue(), cb_type_trap.isSelected());
        list.setType(CardType.Stage.getValue(), cb_type_stage.isSelected());
    	for (int i=0; i< cb_level.length; i++) {
            list.setLv(i+1, cb_level[i].isSelected());
    	}
    	for (int i=0; i< CardUtil.CardPack.size(); i++) {
            list.setPack(CardUtil.CardPack.get(i), cb_pack[i].isSelected());
    	}
        list.setFlip(cb_flip.isSelected());
        list.setExtra(cb_extra.isSelected());
        
        mCardsPane.removeAll();
        UIUtil.showDeck(this, mCardsPane, list.getSelectCards(), 13, columns, UIUtil.CARD_SIZE_SMALL, false);
        mCardsPane.revalidate();
        mCardsPane.repaint();
    }
    
    private void updateDeck() {
        mDeckPane.removeAll();
        UIUtil.showDeck(this, mDeckPane, mDeck.getAllCards(), 18, columns, UIUtil.CARD_SIZE_SMALL, true);

        mDeckPane.revalidate();
        mDeckPane.repaint();
        mCardCountTxt.setText(mDeck.getCardCount()+"/60");
        if (mDeck.getCardCount() > 60) {
        	mCardCountTxt.setForeground(Color.RED);
        } else {
        	mCardCountTxt.setForeground(Color.BLACK);
        }

        mFlipCountTxt.setText(mDeck.getFlipCount()+"/16");
        if (mDeck.getFlipCount() > 16) {
        	mFlipCountTxt.setForeground(Color.RED);
        } else {
        	mFlipCountTxt.setForeground(Color.BLACK);
        }
        
        int[] cookieSummary = mDeck.getCookieSummary();
        int[] otherSummary = mDeck.getOtherSummary();

        mDeckCookieSummaryTxt.setText(String.valueOf(cookieSummary[0]));
        mDeckCookieLv1Txt.setText(String.valueOf(cookieSummary[1]));
        mDeckCookieLv2Txt.setText(String.valueOf(cookieSummary[2]));
        mDeckCookieLv3Txt.setText(String.valueOf(cookieSummary[3]));
        mDeckItemTxt.setText(String.valueOf(otherSummary[0]));
        mDeckTrapTxt.setText(String.valueOf(otherSummary[1]));
        mDeckStageTxt.setText(String.valueOf(otherSummary[2]));
    }

    @Override
    public void addCard(Card card) {
        System.out.println("addCard : "+card.getName());
        if (mDeck.addCard(card)) {
            mDeck.sort();
            updateDeck();
        }
    }

    @Override
    public void removeCard(Card card) {
        System.out.println("removeCard : "+card.getName());
        if (mDeck.removeCard(card)) {
            mDeck.sort();
            updateDeck();
        }
    }

    @Override
    public void showCard(Card card) {
        mCardDetailPane.removeAll();
        ImageIcon cardIcon = new ImageIcon("resources/cards/"+Config.LANGUAGE+"/"+card.getPack()+"/"+card.getId()+".png");
        System.out.println("resources/cards/"+Config.LANGUAGE+"/"+card.getPack()+"/"+card.getId()+".png");
            
        Image image = cardIcon.getImage().getScaledInstance(342, 474, java.awt.Image.SCALE_SMOOTH);
        cardIcon = new ImageIcon(image);
        JLabel cardLabel = new JLabel(cardIcon);
        mCardDetailPane.add(cardLabel, BorderLayout.CENTER);
        mCardDetailPane.revalidate();
        mCardDetailPane.repaint();
    }

	@Override
	public void onSortConfigChanged() {
        mDeck.sort();
        updateDeck();
	}

    @Override
    public void onLanguageChange() {
        // Reload fonts and translations
        loadFont();

        // Update all components with the new translations
        frame.setTitle(CardUtil.getTranslation("app.title") + " v." + Constant.VERSION);
        button_search.setText(CardUtil.getTranslation("search"));
        button_clean.setText(CardUtil.getTranslation("clear"));
        mClearDeckBtn.setText(CardUtil.getTranslation("deck.clear"));
        mCardCountHintTxt.setText(CardUtil.getTranslation("deck.cards"));
        mFlipCountHintTxt.setText(CardUtil.getTranslation("deck.flip"));
        mDeckCookieSummaryHintTxt.setText(CardUtil.getTranslation("deck.cookies"));
        mDeckCookieLv1HintTxt.setText(CardUtil.getTranslation("deck.lv1"));
        mDeckCookieLv2HintTxt.setText(CardUtil.getTranslation("deck.lv2"));
        mDeckCookieLv3HintTxt.setText(CardUtil.getTranslation("deck.lv3"));
        mDeckItemHintTxt.setText(CardUtil.getTranslation("deck.items"));
        mDeckTrapHintTxt.setText(CardUtil.getTranslation("deck.traps"));
        mDeckStageHintTxt.setText(CardUtil.getTranslation("deck.stages"));
        loadBtn.setText(CardUtil.getTranslation("load"));
        saveBtn.setText(CardUtil.getTranslation("save"));
        selectBtn.setText(CardUtil.getTranslation("select.file"));
        showDeckBtn.setText(CardUtil.getTranslation("deck.show"));
        labelColor.setText(CardUtil.getTranslation("color"));
        cb_color[0].setText(CardUtil.CardColor.Red.getDisplayName());
        cb_color[1].setText(CardUtil.CardColor.Yellow.getDisplayName());
        cb_color[2].setText(CardUtil.CardColor.Green.getDisplayName());
        cb_color[3].setText(CardUtil.CardColor.Blue.getDisplayName());
        cb_color[4].setText(CardUtil.CardColor.Purple.getDisplayName());
        cb_color[5].setText(CardUtil.CardColor.Colorless.getDisplayName());
        labelType.setText(CardUtil.getTranslation("type"));
        cb_type_cookie.setText(CardUtil.getTranslation("filter.cookie"));
        cb_flip.setText(CardUtil.getTranslation("filter.flip"));
        cb_extra.setText(CardUtil.getTranslation("filter.extra"));
        cb_type_item.setText(CardUtil.getTranslation("filter.item"));
        cb_type_trap.setText(CardUtil.getTranslation("filter.trap"));
        cb_type_stage.setText(CardUtil.getTranslation("filter.stage"));
        labelSeries.setText(CardUtil.getTranslation("series"));
        sortSettingsMenuItem.setText(CardUtil.getTranslation("sort.settings"));
        settingsMenuItem.setText(CardUtil.getTranslation("settings"));

        updateComponents(frame.getContentPane());

        // Refresh all ClickableCardPanel instances (for updating card images)
        for (Card card : CardList.getInstance().getAllCards()) {
            for (ClickableCardPanel panel : card.getPanels()) {
                panel.updateImage();
            }
        }

        updateCardList();
        updateDeck();
        
        // Revalidate and repaint the frame
        frame.revalidate();
        frame.repaint();
        frame.getComponentListeners()[0].componentResized(null);
    }

    private void updateComponents(java.awt.Container container) {
        for (java.awt.Component component : container.getComponents()) {
            if (componentFontMap.containsKey(component)) {
                String fontKey = componentFontMap.get(component);

                // Map the fontKey to the appropriate Font object
                Font newFont = null;
                switch (fontKey) {
                    case "CRnormal":
                        newFont = CRnormal;
                        break;
                    case "CRnormalLarge":
                        newFont = CRnormalLarge;
                        break;
                    case "CRnormalEXLarge":
                        newFont = CRnormalEXLarge;
                        break;
                    case "CRnormalSmall":
                        newFont = CRnormalSmall;
                        break;
                    case "CRbold":
                        newFont = CRbold;
                        break;
                }

                // Update the font for the component
                if (newFont != null) {
                    component.setFont(newFont);
                }
            }

            // Recursively update child components
            if (component instanceof java.awt.Container) {
                updateComponents((java.awt.Container) component);
            }
        }
    }
    
    public static Font getFontByKey(String key) {
        switch (key) {
            case "CRnormal":
                return CRnormal;
            case "CRnormalLarge":
                return CRnormalLarge;
            case "CRbold":
                return CRbold;
            default:
                return null;
        }
    }
}
