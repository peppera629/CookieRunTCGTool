package ui;

import util.Config;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.awt.BorderLayout;
import java.awt.Choice;

import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.KeyStroke;

import dataStructure.Card;
import dataStructure.CardList;
import dataStructure.CardLoader;
import dataStructure.Deck;
import dataStructure.Collection;

import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.Border;

import java.util.List;
import java.util.concurrent.Flow;
import java.awt.ScrollPane;

import ui.ClickableCardPanel.CardListCallBack;
import ui.SortSettingsWindow.ConfigChangedCallback;
import util.CardUtil.CardColor;
import util.CardUtil.CardRarity;
import util.CardUtil.CardType;
import util.CardUtil;

import util.Constant;
import util.DefaultState;
import util.LanguageChangeListener;
import util.UIUtil;
import util.ScrollablePanel;

import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.KeyAdapter;
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

// FEATURE: Add more views for collection summary in collection mode (by color, by promo set, etc.)
// FIX: When comparing decks, categorize cards by positive/negative change
// FIX: Add way to sort EXTRA cards in deck separately
// FIX: Add auto-resize to deck overview
// FIX: Pause detection for collection mode variant toggles when typing in search box
// FIX: Remove starter decks from collection summary secret rare view
// FIX: Change ways of compiling (JAR, or fix command prompt window not closing)
// OPTIMIZATION: Reduce memory usage (somehow)

public class MainUI implements CardListCallBack, ConfigChangedCallback, LanguageChangeListener {

	private static boolean DEBUG = false;
    private JFrame frame;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        System.setProperty("sun.java2d.uiScale", "1.0");
        System.setProperty("sun.java2d.dpiaware", "true");
        System.setProperty("file.encoding", "UTF-8");
        Config.loadConfig();

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

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
        //System.out.println(Config.CARD_ICON_SCALE + " " + Config.CARD_PREVIEW_SCALE);
        //System.out.println(Config.CARD_PREVIEW_WIDTH + " " + Config.CARD_PREVIEW_HEIGHT);
        loadFont();
		deckWindow = new DeckWindow();
        deckDifferentialWindow = new DeckWindowDifferential();
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
    private DeckWindowDifferential deckDifferentialWindow;
    private SettingsWindow settingsWindow;
	private SortSettingsWindow sortSettingsWindow;
    private DefaultState mDefaultState;
    private JPanel mTextsPane, mDeckDetailButtonsPane;
    private ScrollablePanel mCardsPane, mDeckPane, mSearchPane;
    
    //search panel
    private JPanel mSearchPaneOuter, sidebarPanel;
    private JCheckBox[] cb_color;
    private JCheckBox[] cb_flipType;
    private JCheckBox[] cb_level;
    private JCheckBox[] cb_pack;
    private JCheckBox[] cb_rarity;
    private JCheckBox[] cb_HP;
    private JCheckBox[] cb_skillType;
    private JCheckBox[] cb_keyword;
    private JCheckBox cb_type_cookie, cb_type_item, cb_type_trap, cb_type_stage;
    private JCheckBox cb_flip, cb_extra, cb_variant;
    private JLabel labelColor, labelType, labelSeries, labelRarity, labelHP, labelSkillType, labelKeyword;

    private Deck mDeck;
    private ScrollPane scrollPane;
    private JPanel mCardDetailPane, mCardTranslationPane, deckPane, cardListPane, ownedInfoPanel, keywordLabelPanel, keywordOuterPanel, skillTypeLabelPanel, skillTypeOuterPanel;
    private JPanel mFileOpPane, cardTranslationAttackGroup, cardTranslationFlavorTextGroup, deckDetailPane, centerPanel;
    private JTextField mDeckText, searchBox;
    private JButton saveBtn, selectBtn, hideSearchPaneBtn, hidePreviewPaneBtn, quickSelectBtnBS, quickSelectBtnST;
    private JButton mClearDeckBtn, button_search, button_clean, button_sort, button_settings;
    private JToggleButton button_collection;
    private JLabel mCardCountHintTxt, mFlipCountHintTxt, mExtraCountHintTxt, mDeckCookieSummaryHintTxt, mDeckCookieLv1HintTxt, mDeckCookieLv2HintTxt, mDeckCookieLv3HintTxt, 
        mLevelCountTxt, mFlipTypeCountTxt, cardLabel, filterResults, labelSearch;
    private JLabel mDeckItemHintTxt, mDeckTrapHintTxt, mDeckStageHintTxt, mDeckPaneLabel, mCardsPaneLabel;
    private JLabel mCardCountTxt, mFlipCountTxt, mExtraCountTxt, mDeckCookieSummaryTxt, mDeckCookieLv1Txt, mDeckCookieLv2Txt, mDeckCookieLv3Txt;
    private JLabel mDeckItemTxt, mDeckTrapTxt, mDeckStageTxt, cardId, cardName, cardTranslationSkill, cardTranslationAttackCost;
    private JLabel cardTranslationAttack, cardTranslationAttackIcon, cardTranslationAttackThen, cardTranslationFlip, cardTranslationSkillFlavorText, cardTranslationSkillIcon, cardTranslationAttackFlavorText;
    private JLabel[] ownedInfoRarityRows, ownedInfoCountRows;
    private JSplitPane splitPane;
    private JButton showDeckBtn, showDeckDifferentialBtn;
    private JMenuItem settingsMenuItem, sortSettingsMenuItem;
    private ImageIcon cardIcon;
    private JScrollPane scrollDeckPane, scrollCardsPane, scrollSearchPane;
    public static Font CRnormal, CRbold, CRnormalLarge, CRnormalSmall, CRnormalEXLarge, CRboldLarge, CRboldSmall, CRboldEXLarge, CRtranslation, CRtranslationBold, CRboldEXLargeFilter;
    public static InputStream fontStream, fontStreamBold;
    public static Map<java.awt.Component, String> componentFontMap = new HashMap<>();
    private int columns = 6, previewHeight, divLoc = 400;
    private static int collectionAddVariant = 0;
    private double previousSplitLocation = 0.3d;
    private boolean isCollectionMode = false, deckChanged = false;
    private Collection collection = Collection.getInstance();
    private Card currentCard;

    private void initialize() {
        Config.loadConfig();
    	initialData();
    	initialUI();
        keyBindingsSetup();
    }

    private void initialData() {
    	CardLoader.loadAllPacks();
    	mDefaultState = DefaultState.getInstance();
        mDeck = new Deck();
        frame = new JFrame();
    }

    private void keyBindingsSetup() {
        InputMap inputMap = frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = frame.getRootPane().getActionMap();

        // Key bindings for changing variants in collection mode
        for (int i = 0; i <= 9; i++) { // (I would do anything to replace typing out every function manually)
            final int variant = i;
            String key = Integer.toString(i);
            if (i >= 1) {
                inputMap.put(KeyStroke.getKeyStroke(key), "variant" + key);
            }
            inputMap.put(KeyStroke.getKeyStroke("released " + key), "variant0");
            actionMap.put("variant" + key, new javax.swing.AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int prevVariant = collectionAddVariant;
                    collectionAddVariant = variant;
                    if (prevVariant != collectionAddVariant) {
                        updateCardOwnedInfoHighlight(variant);
                        updateCardPreview();
                    }
                }
            });
        }

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "search");
        actionMap.put("search", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCardList();
                if (isCollectionMode) {
                    updateCardListForCollection();
                }
                mDefaultState.saveDefaultState();
            }
        });

    }
    
    public static void loadFont() {
        try {
            // Use ClassLoader to load the font as a resource
            switch (Config.LANGUAGE) {
                case "zh_TW":
                    fontStream = MainUI.class.getClassLoader().getResourceAsStream("fonts/NotoSansTC-SemiBold.ttf");
                    fontStreamBold = MainUI.class.getClassLoader().getResourceAsStream("fonts/NotoSansTC-ExtraBold.ttf");
                    break;
                default:
                    fontStream = MainUI.class.getClassLoader().getResourceAsStream("fonts/CookieRunRegular.ttf");
                    fontStreamBold = MainUI.class.getClassLoader().getResourceAsStream("fonts/CookieRunBold.ttf");
            }
            if (fontStream == null) {
                throw new IOException("Font file not found");
            }
            CRnormal = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(16f);
            CRnormalSmall = CRnormal.deriveFont(12f);
            CRnormalLarge = CRnormal.deriveFont(20f);
            CRnormalEXLarge = CRnormal.deriveFont(28f);
            CRbold = Font.createFont(Font.TRUETYPE_FONT, fontStreamBold).deriveFont(16f);
            CRboldLarge = CRbold.deriveFont(20f);
            CRboldSmall = CRbold.deriveFont(12f);
            CRboldEXLarge = CRbold.deriveFont(28f);
            CRboldEXLargeFilter = CRbold.deriveFont(28f);
            /*
            if (Config.ADVANCED_FILTERING) {
                CRboldEXLargeFilter = CRbold.deriveFont(20f);
            } else {
                CRboldEXLargeFilter = CRbold.deriveFont(28f);
            } */
            if (Config.LARGE_TRANSLATION_TEXT) {
                CRtranslation = CRnormalLarge;
                CRtranslationBold = CRboldEXLarge;
            } else {
                CRtranslation = CRnormal;
                CRtranslationBold = CRboldLarge;
            }
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(CRnormal);
            ge.registerFont(CRnormalSmall);
            ge.registerFont(CRnormalLarge);
            ge.registerFont(CRbold);
        } catch (Exception e) {
            e.printStackTrace();
            CRnormal = new Font("Arial", Font.PLAIN, 16); // Fallback font
            CRnormalLarge = CRnormal.deriveFont(20f);
        }
    }

    private void initialUI() {

        frame.setTitle(CardUtil.getTranslation("app.title") + " v." + Constant.VERSION);
        frame.setBounds(0, 0, 1600, 1000);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (deckChanged) {
                    ChoiceDialog dialog = new ChoiceDialog();
                    int result = dialog.show(CardUtil.getTranslation("confirmation"));
                    System.out.println(result);
                    if (result == 0) {
                        CardLoader.saveDeck(mDeckText.getText(), mDeck);
                        mDefaultState.setDefaultDeckName(mDeckText.getText());
                        mDefaultState.saveDefaultState();
                        System.exit(0);
                    } else if (result == 1) {
                        System.exit(0);
                    }
                } else {
                    System.exit(0);
                }
            }
        });
        frame.getContentPane().setLayout(new BorderLayout());
        
        mSearchPaneOuter = new JPanel(new BorderLayout());
        mSearchPane = new ScrollablePanel();
        
        mSearchPane.setLayout(new BoxLayout(mSearchPane, BoxLayout.Y_AXIS));
        mSearchPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        scrollSearchPane = new JScrollPane(mSearchPane);
        scrollSearchPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollSearchPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollSearchPane.setBorder(null);
        JScrollBar searchScrollBar = scrollSearchPane.getVerticalScrollBar();
        searchScrollBar.setUnitIncrement(16);
        mSearchPaneOuter.add(scrollSearchPane, BorderLayout.CENTER);
        frame.getContentPane().add(mSearchPaneOuter, BorderLayout.WEST);

        initCheckBox();

        JPanel searchPanelButtons = new JPanel();
        searchPanelButtons.setLayout(new GridBagLayout());
        searchPanelButtons.setBorder(new EmptyBorder(3, 3, 3, 3));
        mSearchPaneOuter.add(searchPanelButtons, BorderLayout.SOUTH);
        
        GridBagConstraints gbc_buttons = new GridBagConstraints();
        gbc_buttons.fill = GridBagConstraints.BOTH;

        gbc_buttons.gridx = 0;
        gbc_buttons.weightx = 1.0;
        gbc_buttons.gridy = 0;
        gbc_buttons.gridwidth = 2;

        filterResults = new JLabel("", JLabel.CENTER);
        filterResults.setAlignmentX(Component.CENTER_ALIGNMENT);
        filterResults.setFont(CRnormal);
        componentFontMap.put(filterResults, "CRnormal");
        searchPanelButtons.add(filterResults, gbc_buttons);

        gbc_buttons.gridx = 0;
        gbc_buttons.weightx = 1.0;
        gbc_buttons.gridy = 1;
        gbc_buttons.gridwidth = 1;
        gbc_buttons.insets = new Insets(3, 3, 3, 3);
        
        button_search = new JButton(CardUtil.getTranslation("search"));
        button_search.setFont(CRnormalLarge);
        componentFontMap.put(button_search, "CRnormalLarge"); // Store the font type as a String
        searchPanelButtons.add(button_search, gbc_buttons);
        button_search.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateCardList();
                if (isCollectionMode) {
                    updateCardListForCollection();
                }
                mDefaultState.saveDefaultState();
            }
        });

        gbc_buttons.gridx = 1;
        button_clean = new JButton(CardUtil.getTranslation("clear"));
        button_clean.setFont(CRnormalLarge);
        componentFontMap.put(button_clean, "CRnormalLarge"); // Store the font type as a String
        searchPanelButtons.add(button_clean, gbc_buttons);
        
        button_clean.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	cleanCheckBox();
            	updateCardList();
                if (isCollectionMode) {
                    updateCardListForCollection();
                }
                mDefaultState.cleanSearchFlag();
                mDefaultState.saveDefaultState();
            }
        });

        gbc_buttons.gridy = 2;
        gbc_buttons.gridx = 0;
        gbc_buttons.gridwidth = 2;
        
        button_collection = new JToggleButton();
        if (isCollectionMode) {
            button_collection.setText(CardUtil.getTranslation("collectionedit.disable"));
        } else {
            button_collection.setText(CardUtil.getTranslation("collectionedit.enable"));
        }
        button_collection.setFont(CRnormal);
        componentFontMap.put(button_collection, "CRnormal"); // Store the font type as a String
        searchPanelButtons.add(button_collection, gbc_buttons);
        
        button_collection.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	isCollectionMode = button_collection.isSelected();
            	if (isCollectionMode) {
            		button_collection.setText(CardUtil.getTranslation("collectionedit.disable"));
            	} else {
            		button_collection.setText(CardUtil.getTranslation("collectionedit.enable"));
            	}
            	updateUIForCollectionMode();
                frame.getComponentListeners()[0].componentResized(null);
            }
        });

        gbc_buttons.gridx = 0;
        gbc_buttons.gridwidth = 1;
        gbc_buttons.gridy = 3;
        button_sort = new JButton(CardUtil.getTranslation("sort.settings"));
        button_sort.setFont(CRnormal);
        componentFontMap.put(button_sort, "CRnormal"); // Store the font type as a String
        searchPanelButtons.add(button_sort, gbc_buttons);
        button_sort.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	sortSettingsWindow.show();
            }
        });

        gbc_buttons.gridx = 1;
        button_settings = new JButton(CardUtil.getTranslation("settings"));
        button_settings.setFont(CRnormal);
        componentFontMap.put(button_settings, "CRnormal"); // Store the font type as a String
        searchPanelButtons.add(button_settings, gbc_buttons);
        button_settings.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                settingsWindow.show();
            }
        });

        // ===== 中間區域 =====

        centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        frame.getContentPane().add(centerPanel, BorderLayout.CENTER);

        // ==== 卡組
        deckPane = new JPanel(new BorderLayout());
        mDeckPaneLabel = new JLabel(CardUtil.getTranslation("deck"));
        mDeckPaneLabel.setFont(CRboldSmall);
        mDeckPaneLabel.setOpaque(true);
        mDeckPaneLabel.setBackground(new Color(10, 10, 10));
        mDeckPaneLabel.setForeground(new Color(255,255,255));
        componentFontMap.put(mDeckPaneLabel, "CRboldSmall");
        deckPane.add(mDeckPaneLabel, BorderLayout.NORTH);

        mDeckPane = new ScrollablePanel();
        mDeckPane.setLayout(new GridLayout(0, 6, 5, 5));
        scrollDeckPane = new JScrollPane(mDeckPane);
        scrollDeckPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollDeckPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JScrollBar deckScrollBar = scrollDeckPane.getVerticalScrollBar();
        deckScrollBar.setUnitIncrement(16);
        deckPane.add(scrollDeckPane, BorderLayout.CENTER);

        // ==== 卡片列表
        cardListPane = new JPanel(new BorderLayout());
        mCardsPaneLabel = new JLabel(CardUtil.getTranslation("cardlist"));
        mCardsPaneLabel.setFont(CRboldSmall);
        mCardsPaneLabel.setOpaque(true);
        mCardsPaneLabel.setBackground(new Color(10, 10, 10));
        mCardsPaneLabel.setForeground(new Color(255,255,255));
        componentFontMap.put(mCardsPaneLabel, "CRboldSmall");
        cardListPane.add(mCardsPaneLabel, BorderLayout.NORTH);

        mCardsPane = new ScrollablePanel();
        mCardsPane.setLayout(new GridLayout(0, 4, 5, 5));
        
        scrollCardsPane = new JScrollPane(mCardsPane);
        scrollCardsPane.setBackground(new Color(255, 255, 255));
        scrollCardsPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollCardsPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JScrollBar cardListScrollBar = scrollCardsPane.getVerticalScrollBar();
        cardListScrollBar.setUnitIncrement(16);
        cardListPane.add(scrollCardsPane, BorderLayout.CENTER);

        // ==== JSplitPane
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, deckPane, cardListPane);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerSize(8);
        splitPane.setContinuousLayout(true);
        splitPane.setOneTouchExpandable(true);
        centerPanel.add(splitPane, BorderLayout.CENTER);

        // ==== 卡組資訊
        deckDetailPane = new JPanel();
        deckDetailPane.setLayout(new BorderLayout());
        centerPanel.add(deckDetailPane, BorderLayout.SOUTH);

        mDeckDetailButtonsPane = new JPanel();
        mDeckDetailButtonsPane.setLayout(new GridBagLayout());
        deckDetailPane.add(mDeckDetailButtonsPane, BorderLayout.SOUTH);
        GridBagConstraints gbc_deckbuttons = new GridBagConstraints();
        gbc_deckbuttons.fill = GridBagConstraints.BOTH;

        gbc_deckbuttons.gridx = 0;
        gbc_deckbuttons.gridy = 0;
        gbc_deckbuttons.weightx = 1;
        hideSearchPaneBtn = new JButton();
        if (mSearchPaneOuter.isVisible()) {
            hideSearchPaneBtn.setText("<< " + CardUtil.getTranslation("filter"));
        } else {
            hideSearchPaneBtn.setText(">> " + CardUtil.getTranslation("filter"));
        }
        hideSearchPaneBtn.setFont(CRnormalSmall);
        componentFontMap.put(hideSearchPaneBtn, "CRnormalSmall"); // Store the font type as a String
        mDeckDetailButtonsPane.add(hideSearchPaneBtn, gbc_deckbuttons);
        hideSearchPaneBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (mSearchPaneOuter.isVisible()) {
                    mSearchPaneOuter.setVisible(false);
                    hideSearchPaneBtn.setText(">> " + CardUtil.getTranslation("filter"));
                } else {
                    mSearchPaneOuter.setVisible(true);
                    hideSearchPaneBtn.setText("<< " + CardUtil.getTranslation("filter"));
                }
                frame.revalidate();
                frame.repaint();
                frame.getComponentListeners()[0].componentResized(null);
            }
        });

        gbc_deckbuttons.gridx = 1;
        gbc_deckbuttons.weightx = 10;
        mClearDeckBtn = new JButton(CardUtil.getTranslation("deck.clear"));
        mClearDeckBtn.setFont(CRnormalLarge);
        componentFontMap.put(mClearDeckBtn, "CRnormalLarge"); // Store the font type as a String
        mDeckDetailButtonsPane.add(mClearDeckBtn, gbc_deckbuttons);

        mClearDeckBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (isCollectionMode) {
                    CollectionSummaryDialog dialog = new CollectionSummaryDialog();
                    dialog.show();
                } else {
                    mDeck.clear();
                    CardList.getInstance().clearCardListCount();
                    updateDeck();
                    CardList.getInstance().updateAllCardPanels();
                    deckChanged = true;
                }
            }
        });

        gbc_deckbuttons.gridx = 2;
        gbc_deckbuttons.weightx = 1;
        hidePreviewPaneBtn = new JButton();
        if (sidebarPanel == null || sidebarPanel.isVisible()) {
            hidePreviewPaneBtn.setText(">> " + CardUtil.getTranslation("preview"));
        } else {
            hidePreviewPaneBtn.setText("<< " + CardUtil.getTranslation("preview"));
        }
        hidePreviewPaneBtn.setFont(CRnormalSmall);
        componentFontMap.put(hidePreviewPaneBtn, "CRnormalSmall"); // Store the font type as a String
        mDeckDetailButtonsPane.add(hidePreviewPaneBtn, gbc_deckbuttons);
        hidePreviewPaneBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (sidebarPanel.isVisible()) {
                    sidebarPanel.setVisible(false);
                    hidePreviewPaneBtn.setText("<< " + CardUtil.getTranslation("preview"));
                } else {
                    sidebarPanel.setVisible(true);
                    hidePreviewPaneBtn.setText(">> " + CardUtil.getTranslation("preview"));
                }
                frame.revalidate();
                frame.repaint();
                frame.getComponentListeners()[0].componentResized(null);
            }
        });

        mTextsPane = new JPanel();
        mTextsPane.setLayout(new GridBagLayout());
        deckDetailPane.add(mTextsPane, BorderLayout.CENTER);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;

        
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 2;
        gbc.weighty = 0.3;
        gbc.gridheight = 1;
        mCardCountHintTxt = new JLabel(CardUtil.getTranslation("deck.cards"));
        mCardCountHintTxt.setFont(CRnormalSmall);
        componentFontMap.put(mCardCountHintTxt, "CRnormalSmall"); // Store the font type as a String
        mTextsPane.add(mCardCountHintTxt, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.75;
        mFlipCountHintTxt = new JLabel(CardUtil.getTranslation("deck.flip"));
        mFlipCountHintTxt.setFont(CRnormalSmall);
        componentFontMap.put(mFlipCountHintTxt, "CRnormalSmall"); // Store the font type as a String
        mTextsPane.add(mFlipCountHintTxt, gbc);

        gbc.gridx = 2;
        gbc.gridheight = 2;
        gbc.weightx = 1.25;
        mFlipTypeCountTxt = new JLabel();
        mFlipTypeCountTxt.setFont(CRnormalSmall);
        componentFontMap.put(mFlipTypeCountTxt, "CRnormalSmall"); // Store the font type as a String
        mTextsPane.add(mFlipTypeCountTxt, gbc);

        gbc.gridx = 3;
        gbc.gridheight = 1;
        gbc.weightx = 2;
        mExtraCountHintTxt = new JLabel(CardUtil.getTranslation("deck.extra"));
        mExtraCountHintTxt.setFont(CRnormalSmall);
        componentFontMap.put(mExtraCountHintTxt, "CRnormalSmall"); // Store the font type as a String
        mTextsPane.add(mExtraCountHintTxt, gbc);

        gbc.gridx = 4;
        gbc.weightx = 0.75;
        mDeckCookieSummaryHintTxt = new JLabel(CardUtil.getTranslation("deck.cookies"));
        mDeckCookieSummaryHintTxt.setFont(CRnormalSmall);
        componentFontMap.put(mDeckCookieSummaryHintTxt, "CRnormalSmall"); // Store the font type as a String
        mTextsPane.add(mDeckCookieSummaryHintTxt, gbc);

        gbc.gridx = 5;
        gbc.gridheight = 2;
        gbc.weightx = 1.25;
        mLevelCountTxt = new JLabel();
        mLevelCountTxt.setFont(CRnormalSmall);
        componentFontMap.put(mLevelCountTxt, "CRnormalSmall"); // Store the font type as a String
        mTextsPane.add(mLevelCountTxt, gbc);

        gbc.gridx = 6;
        gbc.gridheight = 1;
        gbc.weightx = 2;
        mDeckItemHintTxt = new JLabel(CardUtil.getTranslation("deck.items"));
        mDeckItemHintTxt.setFont(CRnormalSmall);
        componentFontMap.put(mDeckItemHintTxt, "CRnormalSmall"); // Store the font type as a String
        mTextsPane.add(mDeckItemHintTxt, gbc);

        gbc.gridx = 7;
        gbc.weightx = 2;
        mDeckTrapHintTxt = new JLabel(CardUtil.getTranslation("deck.traps"));
        mDeckTrapHintTxt.setFont(CRnormalSmall);
        componentFontMap.put(mDeckTrapHintTxt, "CRnormalSmall"); // Store the font type as a String
        mTextsPane.add(mDeckTrapHintTxt, gbc);

        gbc.gridx = 8;
        gbc.weightx = 2;
        mDeckStageHintTxt = new JLabel(CardUtil.getTranslation("deck.stages"));
        mDeckStageHintTxt.setFont(CRnormalSmall);
        componentFontMap.put(mDeckStageHintTxt, "CRnormalSmall"); // Store the font type as a String
        mTextsPane.add(mDeckStageHintTxt, gbc);



        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.weightx = 2;
        gbc.weighty = 0.7;
        gbc.gridheight = 1;
        mCardCountTxt = new JLabel("0/60");
        mCardCountTxt.setFont(CRnormalEXLarge);
        componentFontMap.put(mCardCountTxt, "CRnormalEXLarge"); // Store the font type as a String
        mTextsPane.add(mCardCountTxt, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.75;
        mFlipCountTxt = new JLabel("0/16");
        mFlipCountTxt.setFont(CRnormalEXLarge);
        componentFontMap.put(mFlipCountTxt, "CRnormalEXLarge"); // Store the font type as a String
        mTextsPane.add(mFlipCountTxt, gbc);

        gbc.gridx = 3;
        gbc.weightx = 2;
        mExtraCountTxt = new JLabel("0/6");
        mExtraCountTxt.setFont(CRnormalEXLarge);
        componentFontMap.put(mExtraCountTxt, "CRnormalEXLarge"); // Store the font type as a String
        mTextsPane.add(mExtraCountTxt, gbc);

        gbc.gridx = 4;
        gbc.weightx = 0.75;
        mDeckCookieSummaryTxt = new JLabel("0");
        mDeckCookieSummaryTxt.setFont(CRnormalEXLarge);
        componentFontMap.put(mDeckCookieSummaryTxt, "CRnormalEXLarge"); // Store the font type as a String
        mTextsPane.add(mDeckCookieSummaryTxt, gbc);

        gbc.gridx = 6;
        gbc.weightx = 2;
        mDeckItemTxt = new JLabel("0");
        mDeckItemTxt.setFont(CRnormalEXLarge);
        componentFontMap.put(mDeckItemTxt, "CRnormalEXLarge"); // Store the font type as a String
        mTextsPane.add(mDeckItemTxt, gbc);

        gbc.gridx = 7;
        gbc.weightx = 2;
        mDeckTrapTxt = new JLabel("0");
        mDeckTrapTxt.setFont(CRnormalEXLarge);
        componentFontMap.put(mDeckTrapTxt, "CRnormalEXLarge"); // Store the font type as a String
        mTextsPane.add(mDeckTrapTxt, gbc);

        gbc.gridx = 8;
        gbc.weightx = 2;
        mDeckStageTxt = new JLabel("0");
        mDeckStageTxt.setFont(CRnormalEXLarge);
        componentFontMap.put(mDeckStageTxt, "CRnormalEXLarge"); // Store the font type as a String
        mTextsPane.add(mDeckStageTxt, gbc);

        // Add a ComponentListener to dynamically resize panes
        frame.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int width = centerPanel.getWidth();
                int height = centerPanel.getHeight();
                //System.out.println(width + "x" + height);

                // Calculate heights for each pane
                int deckDetailPaneHeight = 120; // Fixed height
                columns = Math.max(1, (int) Math.floor(width / (Config.SMALL_CARD_WIDTH + 10))); // At least 1 column
                int deckPaneHeight = (int) ((height - deckDetailPaneHeight) * 0.5f); // 50% of the height
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

                if (!isCollectionMode && divLoc > 0) {
                    // Refresh saved location after a resize
                    divLoc = splitPane.getDividerLocation();
                }
                
                // Revalidate and repaint to apply changes
                splitPane.revalidate();
                splitPane.repaint();
                centerPanel.revalidate();
                centerPanel.repaint();
            }
        });

        // Trigger an initial resize to set the correct sizes
        frame.getComponentListeners()[0].componentResized(null);

        frame.getContentPane().add(centerPanel, BorderLayout.CENTER);
        
        sidebarPanel = new JPanel();
        sidebarPanel.setPreferredSize(new Dimension(Config.CARD_PREVIEW_WIDTH, (int) frame.getBounds().getHeight()));
        sidebarPanel.setLayout(new BorderLayout());

        // ==== Card ID and Name
        JPanel cardInfo = new JPanel();
        cardInfo.setPreferredSize(new Dimension(Config.CARD_PREVIEW_WIDTH, 200));
        cardInfo.setLayout(new BoxLayout(cardInfo, BoxLayout.Y_AXIS));
        cardId = new JLabel("", JLabel.CENTER);
        cardId.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardId.setFont(CRnormal);
        componentFontMap.put(cardId, "CRnormal"); // Store the font type as a String
        cardName = new JLabel("", JLabel.CENTER);
        cardName.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardName.setFont(CRboldLarge);
        componentFontMap.put(cardName, "CRboldLarge"); // Store the font type as a String
        cardInfo.add(cardId);
        cardInfo.add(cardName);

        // ==== Card Preview
        mCardDetailPane = new JPanel();
        mCardDetailPane.setLayout(new BorderLayout());
        mCardDetailPane.setPreferredSize(new Dimension(Config.CARD_PREVIEW_WIDTH, (int) frame.getBounds().getHeight()-60));
        cardInfo.add(mCardDetailPane);

        // ==== Card Ownership Info (when Collection Mode is active)
        ownedInfoPanel = new JPanel();
        ownedInfoPanel.setLayout(new GridBagLayout());
        ownedInfoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        cardInfo.add(ownedInfoPanel);

        ownedInfoRarityRows = new JLabel[] {
            new JLabel("", JLabel.LEFT),
            new JLabel("", JLabel.LEFT),
            new JLabel("", JLabel.LEFT),
            new JLabel("", JLabel.LEFT),
            new JLabel("", JLabel.LEFT),
            new JLabel("", JLabel.LEFT),
            new JLabel("", JLabel.LEFT)
        };

        ownedInfoCountRows = new JLabel[] {
            new JLabel("", JLabel.RIGHT),
            new JLabel("", JLabel.RIGHT),
            new JLabel("", JLabel.RIGHT),
            new JLabel("", JLabel.RIGHT),
            new JLabel("", JLabel.RIGHT),
            new JLabel("", JLabel.RIGHT),
            new JLabel("", JLabel.RIGHT)
        };

        GridBagConstraints gbc_owned = new GridBagConstraints();
        gbc_owned.fill = GridBagConstraints.BOTH;
        gbc_owned.gridx = 0;
        gbc_owned.gridy = 0;
        
        for (int i = 0; i < ownedInfoRarityRows.length; i++) {
            gbc_owned.gridx = 0;
            gbc_owned.weightx = 5;
            ownedInfoRarityRows[i].setFont(CRnormal);
            componentFontMap.put(ownedInfoRarityRows[i], "CRnormal"); // Store the font type
            ownedInfoPanel.add(ownedInfoRarityRows[i], gbc_owned);
            gbc_owned.gridx = 1;
            gbc_owned.weightx = 1;
            ownedInfoCountRows[i].setFont(CRboldEXLarge);
            componentFontMap.put(ownedInfoCountRows[i], "CRboldEXLarge"); // Store the font type
            ownedInfoPanel.add(ownedInfoCountRows[i], gbc_owned);
            gbc_owned.gridy++;
        }

        // ==== Card Translations (when available)
        mCardTranslationPane = new JPanel();
        mCardTranslationPane.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 5, 0, 5); // Add some padding
        gbc.fill = GridBagConstraints.HORIZONTAL; // Ensure components stretch horizontally
        gbc.weightx = 1.0; // Allow components to take full width

        cardTranslationFlavorTextGroup = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        cardTranslationSkillIcon = new JLabel("");
        cardTranslationFlavorTextGroup.add(cardTranslationSkillIcon);
        cardTranslationSkillFlavorText = new JLabel("", JLabel.CENTER);
        cardTranslationSkillFlavorText.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardTranslationSkillFlavorText.setFont(CRtranslation);
        componentFontMap.put(cardTranslationSkillFlavorText, "CRtranslation"); // Store the font type
        cardTranslationFlavorTextGroup.add(cardTranslationSkillFlavorText);

        cardTranslationSkill = new JLabel("", JLabel.LEFT);
        cardTranslationSkill.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardTranslationSkill.setFont(CRtranslation);
        componentFontMap.put(cardTranslationSkill, "CRtranslation"); // Store the font type

        cardTranslationAttackGroup = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        cardTranslationAttackCost = new JLabel("", JLabel.LEFT);
        cardTranslationAttackCost.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardTranslationAttackCost.setFont(CRtranslation);
        componentFontMap.put(cardTranslationAttackCost, "CRtranslation"); // Store the font type
        cardTranslationAttackFlavorText = new JLabel("", JLabel.LEFT);
        cardTranslationAttackFlavorText.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardTranslationAttackFlavorText.setFont(CRtranslation);
        componentFontMap.put(cardTranslationAttackFlavorText, "CRtranslation"); // Store the font type
        cardTranslationAttackGroup.add(cardTranslationAttackCost);
        cardTranslationAttackGroup.add(cardTranslationAttackFlavorText);

        cardTranslationAttackIcon = new JLabel("");
        cardTranslationAttackGroup.add(cardTranslationAttackIcon);

        cardTranslationAttack = new JLabel("", JLabel.LEFT);
        cardTranslationAttack.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardTranslationAttack.setFont(CRtranslationBold);
        componentFontMap.put(cardTranslationAttack, "CRtranslationBold"); // Store the font type
        cardTranslationAttackGroup.add(cardTranslationAttack);

        cardTranslationAttackThen = new JLabel("", JLabel.LEFT);
        cardTranslationAttackThen.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardTranslationAttackThen.setFont(CRtranslation);
        componentFontMap.put(cardTranslationAttackThen, "CRtranslation"); // Store the font type

        cardTranslationFlip = new JLabel("", JLabel.LEFT);
        cardTranslationFlip.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardTranslationFlip.setFont(CRtranslation);
        componentFontMap.put(cardTranslationFlip, "CRtranslation"); // Store the font type

        // Add cardTranslationFlavorTextGroup (centered)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER; // Center the flavor text group
        mCardTranslationPane.add(cardTranslationFlavorTextGroup, gbc);

        // Add cardTranslationSkill (left-aligned to the sidebar)
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST; // Left-align the text
        mCardTranslationPane.add(cardTranslationSkill, gbc);

        // Add cardTranslationAttackGroup (centered)
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER; // Center the attack group
        mCardTranslationPane.add(cardTranslationAttackGroup, gbc);

        // Add cardTranslationAttackThen (left-aligned to the sidebar)
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST; // Left-align the text
        mCardTranslationPane.add(cardTranslationAttackThen, gbc);

        // Add cardTranslationFlip (left-aligned to the sidebar)
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST; // Left-align the text
        mCardTranslationPane.add(cardTranslationFlip, gbc);

        cardInfo.add(mCardTranslationPane);

        sidebarPanel.add(cardInfo, BorderLayout.CENTER);

        // ===== File Operations
        mFileOpPane = new JPanel();
        mFileOpPane.setLayout(new GridBagLayout());
        mFileOpPane.setPreferredSize(new Dimension(Config.CARD_PREVIEW_WIDTH, 60));
        sidebarPanel.add(mFileOpPane, BorderLayout.SOUTH);

        GridBagConstraints gbc_panel = new GridBagConstraints();
        gbc_panel.fill = GridBagConstraints.BOTH;
        gbc_panel.gridx = 0;
        gbc_panel.gridwidth = 4;
        gbc_panel.gridy = 0;
        mDeckText = new JTextField();
        mDeckText.setText(mDefaultState.getDeckDefaultName());
        mDeckText.setFont(CRnormal);
        mDeckText.setPreferredSize(new Dimension(Config.CARD_PREVIEW_WIDTH, CRnormal.getSize()+11));
        componentFontMap.put(mDeckText, "CRnormal");
        mFileOpPane.add(mDeckText, gbc_panel);

        /*
        gbc_panel.gridwidth = 1;
        gbc_panel.weightx = 0.25;
        gbc_panel.gridy = 1;
        loadBtn = new JButton(CardUtil.getTranslation("load"));
        loadBtn.setFont(CRnormal);
        componentFontMap.put(loadBtn, "CRnormal"); // Store the font type as a String
        panel.add(loadBtn, gbc_panel);
        loadBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mDeck = CardLoader.loadDeck(mDeckText.getText());
                mDeck.sort();
                updateDeck();
                mDefaultState.setDefaultDeckName(mDeckText.getText());
                mDefaultState.saveDefaultState();
            }
        });
        */

        gbc_panel.gridwidth = 1;
        gbc_panel.gridx = 0;
        gbc_panel.gridy = 1;
        saveBtn = new JButton(CardUtil.getTranslation("save"));
        saveBtn.setFont(CRnormal);
        componentFontMap.put(saveBtn, "CRnormal"); // Store the font type as a String
        mFileOpPane.add(saveBtn, gbc_panel);
        saveBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CardLoader.saveDeck(mDeckText.getText(), mDeck);
                mDefaultState.setDefaultDeckName(mDeckText.getText());
                mDefaultState.saveDefaultState();
                Dialog dialog = new Dialog();
                deckChanged = false;
                dialog.show(CardUtil.getTranslation("deck.saved"));
            }
        });

        gbc_panel.gridx = 1;
        selectBtn = new JButton(CardUtil.getTranslation("select.file"));
        selectBtn.setFont(CRnormal);
        componentFontMap.put(selectBtn, "CRnormal"); // Store the font type as a String
        selectBtn.setActionCommand("Select File");
        mFileOpPane.add(selectBtn, gbc_panel);
        selectBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File("deck/"));
				int returnValue = fileChooser.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
                    if (deckChanged) {
                        ChoiceDialog dialog = new ChoiceDialog();
                        int result = dialog.show(CardUtil.getTranslation("confirmation"));
                        System.out.println(result);
                        if (result == 0) {
                            CardLoader.saveDeck(mDeckText.getText(), mDeck);
                            mDefaultState.setDefaultDeckName(mDeckText.getText());
                            mDefaultState.saveDefaultState();
                        } else if (result == 2) {
                            return; // Cancel the file selection
                        }
                    }
                    File selectedFile = fileChooser.getSelectedFile();
                    String filename = selectedFile.getName();
                    //System.out.println(selectedFile.getName());
                    mDeckText.setText(filename.substring(0, filename.length() - 4));
                    mDeck.clear();
                    CardList.getInstance().clearCardListCount();
                    mDeck = CardLoader.loadDeck(mDeckText.getText());
                    mDeck.sort();
                    updateDeck();
                    CardList.getInstance().updateAllCardPanels();
                    mDefaultState.setDefaultDeckName(mDeckText.getText());
                    mDefaultState.saveDefaultState();
                    deckChanged = false;
				} 
            }
        });
        
        gbc_panel.gridx = 2;
        showDeckBtn = new JButton(CardUtil.getTranslation("deck.show"));
        showDeckBtn.setFont(CRnormal);
        componentFontMap.put(showDeckBtn, "CRnormal"); // Store the font type as a String
        mFileOpPane.add(showDeckBtn, gbc_panel);
        showDeckBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		deckWindow.show(mDeck, mDeckText.getText());
        	}
        });

        gbc_panel.gridx = 3;
        showDeckDifferentialBtn = new JButton(CardUtil.getTranslation("deck.compare"));
        showDeckDifferentialBtn.setFont(CRnormal);
        componentFontMap.put(showDeckDifferentialBtn, "CRnormal"); // Store the font type as a String
        mFileOpPane.add(showDeckDifferentialBtn, gbc_panel);
        showDeckDifferentialBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
                JPanel compareModePanel = new JPanel(new GridLayout(0, 1));
                ButtonGroup compareModeGroup = new ButtonGroup();
                JRadioButton compareModeFrom = new JRadioButton(CardUtil.getTranslation("deck.compare.from"));
                compareModeGroup.add(compareModeFrom);
                JRadioButton compareModeTo = new JRadioButton(CardUtil.getTranslation("deck.compare.to"));
                compareModeGroup.add(compareModeTo);
                compareModeTo.setSelected(true);
                compareModePanel.add(compareModeFrom);
                compareModePanel.add(compareModeTo);
                fileChooser.setAccessory(compareModePanel);
				fileChooser.setCurrentDirectory(new File("deck/"));
				int returnValue = fileChooser.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
                    boolean compareMode = compareModeTo.isSelected();
                    File selectedFile = fileChooser.getSelectedFile();
                    String filename = selectedFile.getName();
                    Map<String, Integer> mDeck2 = CardLoader.loadDeckTemp(filename.substring(0, filename.length() - 4));
                    deckDifferentialWindow.show(mDeck, mDeckText.getText(), mDeck2, filename.substring(0, filename.length() - 4), compareMode);
				} 
            }
        });

        frame.getContentPane().add(sidebarPanel, BorderLayout.EAST);

        updateCardList();
        
        mDeck = CardLoader.loadDeck(mDeckText.getText());
        mDeck.sort();
        updateDeck();
    }
	
    private void initCheckBox() {
        Border filterBorder = BorderFactory.createEmptyBorder(0, 0, 20, 0);

        // ========================= search by name =========================
        JPanel searchLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // Wrap the search box
        mSearchPane.add(searchLabelPanel);

        labelSearch = new JLabel(CardUtil.getTranslation("search.name"), JLabel.LEFT);
        labelSearch.setFont(CRboldEXLargeFilter);
        componentFontMap.put(labelSearch, "CRboldEXLargeFilter"); // Store the font type as a String
        searchLabelPanel.add(labelSearch);
        
        searchBox = new JTextField();
        //searchBox.setMaximumSize(new Dimension(300, CRnormal.getSize()+5));
        searchBox.setFont(CRnormal);
        componentFontMap.put(searchBox, "CRnormal"); // Store the font type as a String
        mSearchPane.add(searchBox);
    	
        // ========================= color ==================================
        labelColor = new JLabel(CardUtil.getTranslation("color"), JLabel.LEFT);
        labelColor.setFont(CRboldEXLargeFilter);
        componentFontMap.put(labelColor, "CRboldEXLargeFilter"); // Store the font type as a String
        JPanel colorLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // Wrap the label
        colorLabelPanel.add(labelColor);
        mSearchPane.add(colorLabelPanel);

        JPanel colorOuterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // Wrap the grid
        JPanel colorCheckboxGroup = new JPanel();
        colorCheckboxGroup.setLayout(new GridLayout(0, 3));
        colorCheckboxGroup.setBorder(filterBorder);
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
        
        // ========================= type ==================================
        labelType = new JLabel(CardUtil.getTranslation("type"), JLabel.LEFT);
        labelType.setFont(CRboldEXLargeFilter);
        componentFontMap.put(labelType, "CRboldEXLargeFilter"); // Store the font type as a String
        JPanel typeLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // Wrap the label
        typeLabelPanel.add(labelType);
        mSearchPane.add(typeLabelPanel);

        JPanel typeOuterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // Wrap the grid
        JPanel typeCheckboxGroup = new JPanel();
        typeCheckboxGroup.setLayout(new GridLayout(0, 4));
        typeCheckboxGroup.setBorder(filterBorder);
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
                for (JCheckBox cb : cb_HP) {
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
                for (JCheckBox cb : cb_flipType) {
            		cb.setEnabled(cb_flip.isSelected());
            	}
            }
        });

        cb_flipType = new JCheckBox[3];
        cb_flipType[0] = new JCheckBox(CardUtil.getTranslation("flip.heal"));
        cb_flipType[1] = new JCheckBox(CardUtil.getTranslation("flip.draw"));
        cb_flipType[2] = new JCheckBox(CardUtil.getTranslation("flip.special"));
        for (int i=0; i<3; i++) {
        	final int id = i;
        	cb_flipType[i].setSelected(mDefaultState.getDefaultFlipTypeFlag(i));
            cb_flipType[i].setFont(CRnormal);
            componentFontMap.put(cb_flipType[i], "CRnormal"); // Store the font type as a String
            typeCheckboxGroup.add(cb_flipType[i]);
            cb_flipType[i].setEnabled(cb_flip.isSelected());
            cb_flipType[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	mDefaultState.setDefaultFlipTypeFlag(id, cb_flipType[id].isSelected());
                }
            });
        }

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

        // ========================= HP ==================================
        labelHP = new JLabel("HP", JLabel.LEFT);
        labelHP.setFont(CRboldEXLargeFilter);
        componentFontMap.put(labelHP, "CRboldEXLargeFilter"); // Store the font type as a String
        JPanel HPLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // Wrap the label
        HPLabelPanel.add(labelHP);
        mSearchPane.add(HPLabelPanel);

        JPanel HPOuterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // Wrap the grid
        JPanel HPCheckboxGroup = new JPanel();
        HPCheckboxGroup.setLayout(new GridLayout(0, 6));
        HPCheckboxGroup.setBorder(filterBorder);
        HPOuterPanel.add(HPCheckboxGroup);
        mSearchPane.add(HPOuterPanel);

        cb_HP = new JCheckBox[CardUtil.HP_MAX];
        for(int i=0; i<CardUtil.HP_MAX; i++) {
            final int id = i;
            final int hp = i+1;
        	cb_HP[i] = new JCheckBox(Integer.toString(hp));
        	cb_HP[i].setSelected(mDefaultState.getDefaultHPFlag(hp));
            cb_HP[i].setFont(CRnormal);
            componentFontMap.put(cb_HP[i], "CRnormal"); // Store the font type as a String
            HPCheckboxGroup.add(cb_HP[i]);
            
            cb_HP[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	mDefaultState.setDefaultHPFlag(hp, cb_HP[id].isSelected());
                }
            });
            cb_HP[i].setEnabled(cb_type_cookie.isSelected());
        }

        // ========================= pack ==================================

        labelSeries = new JLabel(CardUtil.getTranslation("series"), JLabel.LEFT);
        labelSeries.setFont(CRboldEXLargeFilter);
        componentFontMap.put(labelSeries, "CRboldEXLargeFilter"); // Store the font type as a String
        JPanel seriesLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // Wrap the label
        seriesLabelPanel.add(labelSeries);
        mSearchPane.add(seriesLabelPanel);

        JPanel quickSelectBtnGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        mSearchPane.add(quickSelectBtnGroup);
        
        quickSelectBtnST = new JButton(CardUtil.getTranslation("filter.ST"));
        quickSelectBtnST.setFont(CRnormal);
        componentFontMap.put(quickSelectBtnST, "CRnormal");
        quickSelectBtnGroup.add(quickSelectBtnST);

        quickSelectBtnBS = new JButton(CardUtil.getTranslation("filter.BS"));
        quickSelectBtnBS.setFont(CRnormal);
        componentFontMap.put(quickSelectBtnBS, "CRnormal");
        quickSelectBtnGroup.add(quickSelectBtnBS);

        JPanel packOuterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // Wrap the grid
        JPanel packCheckboxGroup = new JPanel();
        packCheckboxGroup.setLayout(new GridLayout(0, 5));
        packCheckboxGroup.setBorder(filterBorder);
        packOuterPanel.add(packCheckboxGroup);
        mSearchPane.add(packOuterPanel);

        cb_pack = new JCheckBox[CardUtil.CardPack.size()];
        for(int i=0; i<CardUtil.CardPack.size(); i++) {
        	final int id = i;
        	cb_pack[i] = new JCheckBox(CardUtil.CardPack.get(i).replace("_", ""));
        	cb_pack[i].setSelected(mDefaultState.getDefaultPackFlag(CardUtil.CardPack.get(i)));
            cb_pack[i].setEnabled(!CardUtil.CardPack.get(i).endsWith("_"));
            cb_pack[i].setFont(CRnormal);
            componentFontMap.put(cb_pack[i], "CRnormal"); // Store the font type as a String
            packCheckboxGroup.add(cb_pack[i]);
            cb_pack[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	mDefaultState.setDefaultPackFlag(CardUtil.CardPack.get(id), cb_pack[id].isSelected());
                }
            });
        }

        
        quickSelectBtnBS.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean quickSelectMode = false;
                for (JCheckBox cb : cb_pack) {
                    if (cb.getText().contains("BS") && !cb.isSelected()) {
                        quickSelectMode = true;
                        break;
                    }
                }

                for (JCheckBox cb : cb_pack) {
                    if (cb.getText().contains("BS")) {
                        cb.setSelected(quickSelectMode);
                        mDefaultState.setDefaultPackFlag(cb.getText(), quickSelectMode);
                    }
                }
            }
        });

        quickSelectBtnST.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean quickSelectMode = false;
                for (JCheckBox cb : cb_pack) {
                    if (cb.getText().contains("ST") && !cb.isSelected()) {
                        quickSelectMode = true;
                        break;
                    }
                }

                for (JCheckBox cb : cb_pack) {
                    if (cb.getText().contains("ST")) {
                        cb.setSelected(quickSelectMode);
                        mDefaultState.setDefaultPackFlag(cb.getText(), quickSelectMode);
                    }
                }
            }
        });

        // ========================= rarity ==================================
        labelRarity = new JLabel(CardUtil.getTranslation("rarity"), JLabel.LEFT);
        labelRarity.setFont(CRboldEXLargeFilter);
        componentFontMap.put(labelRarity, "CRboldEXLargeFilter"); // Store the font type as a String
        JPanel rarityLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // Wrap the label
        rarityLabelPanel.add(labelRarity);
        mSearchPane.add(rarityLabelPanel);

        JPanel rarityOuterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // Wrap the grid
        mSearchPane.add(rarityOuterPanel);
        JPanel rarityCheckboxGroup = new JPanel(new GridBagLayout());
        rarityCheckboxGroup.setBorder(filterBorder);

        GridBagConstraints gbc_rarity = new GridBagConstraints();
        gbc_rarity.anchor = GridBagConstraints.WEST;
        gbc_rarity.gridy = 0;

        cb_rarity = new JCheckBox[CardUtil.RARITY_MAX];
        for(int i=0; i<CardUtil.RARITY_MAX; i++) {
            gbc_rarity.gridx = i;
        	cb_rarity[i] = new JCheckBox(CardUtil.CardRarity.fromValue(i).getDisplayName());
        	cb_rarity[i].setSelected(mDefaultState.getDefaultRarityFlag(i));
            cb_rarity[i].setFont(CRnormal);
            componentFontMap.put(cb_rarity[i], "CRnormal"); // Store the font type as a String
            rarityCheckboxGroup.add(cb_rarity[i], gbc_rarity);
            final int id = i;
            cb_rarity[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	mDefaultState.setDefaultRarityFlag(id, cb_rarity[id].isSelected());
                }
            });
        }

        gbc_rarity.gridx = 0;
        gbc_rarity.gridy = 1;
        gbc_rarity.gridwidth = 5;
        cb_variant = new JCheckBox(CardUtil.getTranslation("rarity.variant"));
        cb_variant.setFont(CRnormal);
        componentFontMap.put(cb_variant, "CRnormal"); // Store the font type as a String
        rarityCheckboxGroup.add(cb_variant, gbc_rarity);
        rarityOuterPanel.add(rarityCheckboxGroup);
        cb_variant.setVisible(Config.ADVANCED_FILTERING);
        if (!cb_variant.isVisible()) {
            cb_variant.setSelected(false);
        }

        // ========================= skill type filtering =========================
        labelSkillType = new JLabel(CardUtil.getTranslation("filter.skilltype"), JLabel.LEFT);
        labelSkillType.setFont(CRboldEXLargeFilter);
        componentFontMap.put(labelSkillType, "CRboldEXLargeFilter"); // Store the font type as a String
        skillTypeLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // Wrap the label
        skillTypeLabelPanel.add(labelSkillType);
        mSearchPane.add(skillTypeLabelPanel);

        skillTypeOuterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // Wrap the grid
        mSearchPane.add(skillTypeOuterPanel);
        JPanel skillTypeCheckboxGroup = new JPanel(new GridLayout(0, 1));
        skillTypeCheckboxGroup.setBorder(filterBorder);

        cb_skillType = new JCheckBox[CardUtil.SKILL_TYPE_MAX];
        
        for(int i=0; i<CardUtil.SKILL_TYPE_MAX; i++) {
        	cb_skillType[i] = new JCheckBox(CardUtil.SkillType.fromValue(i).getDisplayName());
        	//cb_skillType[i].setSelected(mDefaultState.getDefaultSkillTypeFlag(i));
            cb_skillType[i].setFont(CRnormal);
            componentFontMap.put(cb_skillType[i], "CRnormal"); // Store the font type as a String
            skillTypeCheckboxGroup.add(cb_skillType[i]);
            final int id = i;
            /*
            cb_skillType[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	mDefaultState.setDefaultSkillTypeFlag(id, cb_skillType[id].isSelected());
                }
            }); */
        }
        skillTypeOuterPanel.add(skillTypeCheckboxGroup);
        skillTypeLabelPanel.setVisible(Config.ADVANCED_FILTERING);
        skillTypeOuterPanel.setVisible(Config.ADVANCED_FILTERING);

        // ========================= keyword filtering =========================
        labelKeyword = new JLabel(CardUtil.getTranslation("filter.keyword"), JLabel.LEFT);
        labelKeyword.setFont(CRboldEXLargeFilter);
        componentFontMap.put(labelKeyword, "CRboldEXLargeFilter"); // Store the font type as a String
        keywordLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // Wrap the label
        keywordLabelPanel.add(labelKeyword);
        mSearchPane.add(keywordLabelPanel);

        keywordOuterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // Wrap the grid
        mSearchPane.add(keywordOuterPanel);
        JPanel keywordCheckboxGroup = new JPanel(new GridLayout(0, 3));
        keywordCheckboxGroup.setBorder(filterBorder);

        cb_keyword = new JCheckBox[CardUtil.KEYWORD_MAX];
        
        for(int i=0; i<CardUtil.KEYWORD_MAX; i++) {
        	cb_keyword[i] = new JCheckBox(CardUtil.Keyword.fromValue(i).getDisplayName());
        	//cb_keyword[i].setSelected(mDefaultState.getDefaultKeywordFlag(i));
            cb_keyword[i].setFont(CRnormal);
            componentFontMap.put(cb_keyword[i], "CRnormal"); // Store the font type as a String
            keywordCheckboxGroup.add(cb_keyword[i]);
            final int id = i;
            /*
            cb_keyword[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	mDefaultState.setDefaultKeywordFlag(id, cb_keyword[id].isSelected());
                }
            }); */
        }
        keywordOuterPanel.add(keywordCheckboxGroup);

        keywordLabelPanel.setVisible(Config.ADVANCED_FILTERING);
        keywordOuterPanel.setVisible(Config.ADVANCED_FILTERING);
    }

    private void cleanCheckBox() {
    	for (JCheckBox cb : cb_color) {
    		cb.setSelected(false);
    	}
    	
    	cb_type_cookie.setSelected(false);
    	cb_type_item.setSelected(false);
    	cb_type_trap.setSelected(false);
        cb_extra.setSelected(false);
        cb_flip.setSelected(false);
        cb_variant.setSelected(false);
    	cb_type_stage.setSelected(false);

    	for (JCheckBox cb : cb_level) {
    		cb.setSelected(false);
    	}

        for (JCheckBox cb : cb_HP) {
        	cb.setSelected(false);
        }

    	cb_flip.setSelected(false);

        for (JCheckBox cb : cb_pack) {
        	cb.setSelected(false);
        }

        for (JCheckBox cb : cb_rarity) {
        	cb.setSelected(false);
        }

        for (JCheckBox cb : cb_level) {
            cb.setEnabled(cb_type_cookie.isSelected());
        }
        for (JCheckBox cb : cb_HP) {
            cb.setEnabled(cb_type_cookie.isSelected());
        }
        for (JCheckBox cb : cb_flipType) {
            cb.setEnabled(cb_flip.isSelected());
        }
        for (JCheckBox cb : cb_keyword) {
            cb.setSelected(false);
        }
        for (JCheckBox cb : cb_skillType) {
            cb.setSelected(false);
        }
        searchBox.setText("");
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
        for (int i=0; i< cb_HP.length; i++) {
            list.setHP(i+1, cb_HP[i].isSelected());
        }
        for (int i=0; i< cb_skillType.length; i++) {
            list.setSkillType(i, cb_skillType[i].isSelected());
        }
        for (int i=0; i< cb_keyword.length; i++) {
            list.setKeyword(i, cb_keyword[i].isSelected());
        }
    	for (int i=0; i< CardUtil.CardPack.size(); i++) {
            list.setPack(CardUtil.CardPack.get(i), cb_pack[i].isSelected());
    	}
        list.setFlip(cb_flip.isSelected());
        for (int i=0; i< cb_flipType.length; i++) {
            list.setFlipType(i, cb_flipType[i].isSelected());
        }
        list.setExtra(cb_extra.isSelected());
        for (int i=0; i< cb_rarity.length; i++) {
            list.setRarity(i, cb_rarity[i].isSelected());
    	}
        list.setHasVariantsOnly(cb_variant.isSelected());
        list.setSearchTerm(searchBox.getText().trim().equals("Search by Card Name...") ? "" : searchBox.getText().trim());
        
        mCardsPane.removeAll();
        List<Card> currentList = list.getSelectCards(false);
        UIUtil.showDeck(this, mCardsPane, currentList, null, 13, columns, UIUtil.CARD_SIZE_SMALL, (isCollectionMode ? 3 : (Config.DECK_BUILD_FROM_COLLECTION ? 4 : 0)), false);
        if (currentList.size() == 0) {
            filterResults.setText(CardUtil.getTranslation("displaycount.empty"));
            filterResults.setForeground(Color.RED);
        } else {
            filterResults.setText(String.format(CardUtil.getTranslation("displaycount"), currentList.size()));
            filterResults.setForeground(Color.BLACK);
        }
        
        mCardsPane.revalidate();
        mCardsPane.repaint();
    }
    
    private void updateDeck() {
        mDeckPane.removeAll();
        UIUtil.showDeck(this, mDeckPane, mDeck.getAllCards(), null, 18, columns, UIUtil.CARD_SIZE_SMALL, (Config.DECK_BUILD_FROM_COLLECTION ? 4 : 1), false);

        mDeckPane.revalidate();
        mDeckPane.repaint();
        mCardCountTxt.setText(mDeck.getCardCount()-mDeck.getExtraCount()+"/60");
        if ((mDeck.getCardCount()-mDeck.getExtraCount() > 60) || (!mDeck.getLegality()) || (Config.DECK_BUILD_FROM_COLLECTION && !mDeck.getOwnershipLegality().isEmpty())) {
        	mCardCountTxt.setForeground(Color.RED);
            String invalidReasonString = ((mDeck.getCardCount()-mDeck.getExtraCount() > 60) ? CardUtil.getTranslation("warning.overlimit") : "");
            invalidReasonString = invalidReasonString + (!mDeck.getLegality() ? ((invalidReasonString.isEmpty()) ? CardUtil.getTranslation("warning.bannedoverlimit") : "<br>" + CardUtil.getTranslation("warning.bannedoverlimit")) : "");
            if (Config.DECK_BUILD_FROM_COLLECTION && !mDeck.getOwnershipLegality().isEmpty()) {
                invalidReasonString = invalidReasonString + ((invalidReasonString.isEmpty()) ? CardUtil.getTranslation("warning.collectionoverlimit") : "<br>" + CardUtil.getTranslation("warning.collectionoverlimit"));
                for (Card entry : mDeck.getOwnershipLegality()) {
                    invalidReasonString = invalidReasonString + "<br>- " + entry.getId() + " " + entry.getName();
                }
            }

            //System.out.println(mDeck.getOwnershipLegality());
            mCardCountTxt.setToolTipText("<html>" + invalidReasonString + "</html>");
        } else {
        	mCardCountTxt.setForeground(Color.BLACK);
            mCardCountTxt.setToolTipText(null);
        }

        mFlipCountTxt.setText(mDeck.getFlipCount()+"/16");
        if (mDeck.getFlipCount() > 16) {
        	mFlipCountTxt.setForeground(Color.RED);
            mFlipCountTxt.setToolTipText("<html>" + CardUtil.getTranslation("warning.flipoverlimit") + "</html>");
        } else {
        	mFlipCountTxt.setForeground(Color.BLACK);
            mFlipCountTxt.setToolTipText(null);
        }

        mExtraCountTxt.setText(mDeck.getExtraCount()+"/6");
        if (mDeck.getExtraCount() > 6) {
        	mExtraCountTxt.setForeground(Color.RED);
            mExtraCountTxt.setToolTipText("<html>" + CardUtil.getTranslation("warning.extraoverlimit") + "</html>");
        } else {
        	mExtraCountTxt.setForeground(Color.BLACK);
            mExtraCountTxt.setToolTipText(null);
        }
        
        int[] cookieSummary = mDeck.getCookieSummary();
        int[] flipTypeSummary = mDeck.getFlipTypeSummary();
        int[] otherSummary = mDeck.getOtherSummary();

        mDeckCookieSummaryTxt.setText(String.valueOf(cookieSummary[0]));
        
        mLevelCountTxt.setText("<html>"+CardUtil.getTranslation("deck.lv1")+" "+cookieSummary[1]+"<br>"+
                CardUtil.getTranslation("deck.lv2")+" "+cookieSummary[2]+"<br>"+
                CardUtil.getTranslation("deck.lv3")+" "+cookieSummary[3]+"</html>");

        mFlipTypeCountTxt.setText("<html>"+CardUtil.getTranslation("flip.heal")+": "+flipTypeSummary[0]+"<br>"+
                CardUtil.getTranslation("flip.draw")+": "+flipTypeSummary[1]+"<br>"+
                CardUtil.getTranslation("flip.special")+": "+flipTypeSummary[2]+"</html>");
        
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
            for (ClickableCardPanel panel : card.getPanels()) {
                panel.updateCountsForCardList();
            }
        }
        deckChanged = true;
    }

    @Override
    public void removeCard(Card card) {
        System.out.println("removeCard : "+card.getName());
        if (mDeck.removeCard(card)) {
            mDeck.sort();
            updateDeck();
            for (ClickableCardPanel panel : card.getPanels()) {
                panel.updateCountsForCardList();
            }
        }
        deckChanged = true;
    }

    @Override
    public void showCard(Card card) {
        mCardDetailPane.removeAll();

        currentCard = card;

        for (String lang : Config.FALLBACK_ORDER) {
            cardIcon = new ImageIcon("resources/cards/"+lang+"/"+card.getPack()+"/"+card.getId()+".png");
            if (cardIcon.getIconWidth() > 0) {
                break;
            }
        }
        
        //System.out.println("resources/cards/"+Config.CARD_LANGUAGE+"/"+card.getPack()+"/"+card.getId()+".png");
        //System.out.println(card.getHP());

        if (card.getMaxCount() == 1) {
            cardId.setText(card.getId() + " [" + CardUtil.getTranslation("restricted") + "]");
            cardId.setForeground(new Color(160, 128, 0));
        } else if (card.getMaxCount() == 0) {
            cardId.setText(card.getId() + " [" + CardUtil.getTranslation("banned") + "]");
            cardId.setForeground(new Color(160, 0, 0));
        } else {
            cardId.setText(card.getId());
            cardId.setForeground(Color.BLACK);
        }

        cardName.setText("<html>" + card.getName() + " " + "<img src=\"file:" + new File("resources/icons_rarity/16px/" + card.getRarity().getName() + ".png").getAbsolutePath() + "\">" + "</html>");
        if (card.getCardTranslation() != null && Config.CARD_TRANSLATION_ENABLED) {
            cardTranslationSkill.setText("<html>" + card.getCardTranslation()[1] + "</html>");
            if (card.getCardTranslation()[0].isEmpty()) {
                cardTranslationSkillIcon.setIcon(null);
                cardTranslationSkillFlavorText.setText("");
            } else {
                cardTranslationSkillIcon.setIcon(new ImageIcon("resources/icons/" + (Config.LARGE_TRANSLATION_TEXT ? "24px/" : "16px/") + "SKILL.png"));
                cardTranslationSkillFlavorText.setText("<html>" + card.getCardTranslation()[0] + "</html>");
            }
            if (card.getCardTranslation()[2].isEmpty()) {
                cardTranslationAttackCost.setText("");
                cardTranslationAttackFlavorText.setText("");
                cardTranslationAttackIcon.setIcon(null);
                cardTranslationAttack.setText("");
            } else {
                cardTranslationAttackFlavorText.setText("<html>" + card.getCardTranslation()[3] + "</html>");
                cardTranslationAttackCost.setText("<html>&lt;" + card.getCardTranslation()[2] + "&gt;</html>");
                cardTranslationAttackIcon.setIcon(new ImageIcon("resources/icons/" + (Config.LARGE_TRANSLATION_TEXT ? "24px/" : "16px/") + "ATK.png"));
                cardTranslationAttack.setText("<html>  " + card.getCardTranslation()[4] + "</html>");
            }
            cardTranslationAttackThen.setText("<html>" + card.getCardTranslation()[5] + "</html>");
            cardTranslationFlip.setText("<html>" + card.getCardTranslation()[6] + "</html>");
        } else {
            cardTranslationAttackFlavorText.setText(null);
            cardTranslationAttackIcon.setIcon(null);
            cardTranslationSkill.setText(null);
            cardTranslationAttackCost.setText(null);
            cardTranslationAttackIcon.setIcon(null);
            cardTranslationAttack.setText(null);
            cardTranslationAttackThen.setText(null);
            cardTranslationFlip.setText(null);
        }

        mCardTranslationPane.revalidate();
        mCardTranslationPane.repaint();
        sidebarPanel.revalidate();
        sidebarPanel.repaint();

        int translationHeight = mCardTranslationPane.getPreferredSize().height;
        int fileOpHeight = mFileOpPane.getHeight();
        int sidebarHeight = sidebarPanel.getHeight();
        int cardInfoHeight = cardId.getPreferredSize().height + cardName.getPreferredSize().height;

        int textPadding = (Config.LARGE_TRANSLATION_TEXT ? 100 : 50);
        previewHeight = Math.min(sidebarHeight - cardInfoHeight - (card.getCardTranslation() == null ? 0 : translationHeight) - fileOpHeight - textPadding, Config.CARD_PREVIEW_HEIGHT);

        Image image = cardIcon.getImage().getScaledInstance((int) (previewHeight / Config.CARD_RATIO), previewHeight, java.awt.Image.SCALE_SMOOTH);
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
        isCollectionMode = false;
        
        // Reload fonts and translations
        loadFont();
        button_collection.setSelected(false);
        button_collection.setText(CardUtil.getTranslation("collectionedit.enable"));

        // Update all components with the new translations
        frame.setTitle(CardUtil.getTranslation("app.title") + " v." + Constant.VERSION);
        searchBox.setText("");
        labelSearch.setText(CardUtil.getTranslation("search.name"));
        button_search.setText(CardUtil.getTranslation("search"));
        button_clean.setText(CardUtil.getTranslation("clear"));
        showDeckDifferentialBtn.setText(CardUtil.getTranslation("deck.compare"));
        mDeckPaneLabel.setText(CardUtil.getTranslation("deck"));
        mCardsPaneLabel.setText(CardUtil.getTranslation("cardlist"));
        mClearDeckBtn.setText(CardUtil.getTranslation("deck.clear"));
        mCardCountHintTxt.setText(CardUtil.getTranslation("deck.cards"));
        mFlipCountHintTxt.setText(CardUtil.getTranslation("deck.flip"));
        mExtraCountHintTxt.setText(CardUtil.getTranslation("deck.extra"));
        mDeckCookieSummaryHintTxt.setText(CardUtil.getTranslation("deck.cookies"));
        mDeckItemHintTxt.setText(CardUtil.getTranslation("deck.items"));
        mDeckTrapHintTxt.setText(CardUtil.getTranslation("deck.traps"));
        mDeckStageHintTxt.setText(CardUtil.getTranslation("deck.stages"));
        //loadBtn.setText(CardUtil.getTranslation("load"));
        saveBtn.setText(CardUtil.getTranslation("save"));
        selectBtn.setText(CardUtil.getTranslation("select.file"));
        showDeckBtn.setText(CardUtil.getTranslation("deck.show"));
        quickSelectBtnBS.setText(CardUtil.getTranslation("filter.BS"));
        quickSelectBtnST.setText(CardUtil.getTranslation("filter.ST"));
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
        cb_flipType[0].setText(CardUtil.getTranslation("flip.heal"));
        cb_flipType[1].setText(CardUtil.getTranslation("flip.draw"));
        cb_flipType[2].setText(CardUtil.getTranslation("flip.special"));
        cb_extra.setText(CardUtil.getTranslation("filter.extra"));
        cb_type_item.setText(CardUtil.getTranslation("filter.item"));
        cb_type_trap.setText(CardUtil.getTranslation("filter.trap"));
        cb_type_stage.setText(CardUtil.getTranslation("filter.stage"));
        cb_variant.setText(CardUtil.getTranslation("rarity.variant"));


        // Set visibility depending on advanced filtering option
        cb_variant.setVisible(Config.ADVANCED_FILTERING);
        keywordLabelPanel.setVisible(Config.ADVANCED_FILTERING);
        keywordOuterPanel.setVisible(Config.ADVANCED_FILTERING);
        skillTypeLabelPanel.setVisible(Config.ADVANCED_FILTERING);
        skillTypeOuterPanel.setVisible(Config.ADVANCED_FILTERING);

        labelRarity.setText(CardUtil.getTranslation("rarity"));
        labelKeyword.setText(CardUtil.getTranslation("filter.keyword"));
        labelSkillType.setText(CardUtil.getTranslation("filter.skilltype"));
        for(int i=0; i< cb_rarity.length; i++) {
            cb_rarity[i].setText(CardUtil.CardRarity.fromValue(i).getDisplayName());
        }
        for(int i=0; i< cb_keyword.length; i++) {
            cb_keyword[i].setText(CardUtil.Keyword.fromValue(i).getDisplayName());
            cb_keyword[i].setSelected(false);
        }
        for(int i=0; i< cb_skillType.length; i++) {
            cb_skillType[i].setText(CardUtil.SkillType.fromValue(i).getDisplayName());
            cb_skillType[i].setSelected(false);
        }
        labelSeries.setText(CardUtil.getTranslation("series"));
        button_sort.setText(CardUtil.getTranslation("sort.settings"));
        button_settings.setText(CardUtil.getTranslation("settings"));
        if (mSearchPaneOuter.isVisible()) {
            hideSearchPaneBtn.setText("<< " + CardUtil.getTranslation("filter"));
        } else {
            hideSearchPaneBtn.setText(">> " + CardUtil.getTranslation("filter"));
        }
        if (sidebarPanel.isVisible()) {
            hidePreviewPaneBtn.setText(">> " + CardUtil.getTranslation("preview"));
        } else {
            hidePreviewPaneBtn.setText("<< " + CardUtil.getTranslation("preview"));
        }
        clearTranslations();

        cardId.setText(null);
        cardName.setText(null);

        updateComponents(frame.getContentPane());
        mCardDetailPane.removeAll();

        // Refresh all ClickableCardPanel instances (for updating card images)
        for (Card card : CardList.getInstance().getAllCards()) {
            for (ClickableCardPanel panel : card.getPanels()) {
                panel.updateImage();
            }
        }

        CardLoader.refreshAllCardNames();
        CardLoader.reloadTranslations(CardList.getInstance().getAllCards());
        CardLoader.reloadCardNames(CardList.getInstance().getAllCards());

        sidebarPanel.setPreferredSize(new Dimension(Config.CARD_PREVIEW_WIDTH, (int) frame.getBounds().getHeight()));
        mCardDetailPane.setPreferredSize(new Dimension(Config.CARD_PREVIEW_WIDTH, (int) frame.getBounds().getHeight()-60));
        //System.out.println(Config.CARD_PREVIEW_WIDTH + "x" + (frame.getBounds().getHeight()-60));
        //System.out.println(mCardDetailPane.getWidth() + "x" + mCardDetailPane.getHeight());

        updateUIForCollectionMode();
        updateCardList();
        updateDeck();
        
        // Revalidate and repaint the frame
        frame.revalidate();
        frame.repaint();
        frame.getComponentListeners()[0].componentResized(null);
    }

    public void clearTranslations() {
        cardTranslationAttackFlavorText.setText(null);
        cardTranslationSkillIcon.setIcon(null);
        cardTranslationSkillFlavorText.setText(null);
        cardTranslationSkill.setText(null);
        cardTranslationAttackCost.setText(null);
        cardTranslationAttackIcon.setIcon(null);
        cardTranslationAttack.setText(null);
        cardTranslationAttackThen.setText(null);
        cardTranslationFlip.setText(null);
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
                    case "CRboldEXLarge":
                        newFont = CRboldEXLarge;
                        break;
                    case "CRboldSmall":
                        newFont = CRboldSmall;
                        break;
                    case "CRboldLarge":
                        newFont = CRboldLarge;
                        break;
                    case "CRtranslation":
                        newFont = CRtranslation;
                        break;
                    case "CRtranslationBold":
                        newFont = CRtranslationBold;
                        break;
                    case "CRboldEXLargeFilter":
                        newFont = CRboldEXLargeFilter;
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

    private void updateUIForCollectionMode() {
        if (isCollectionMode) {
            if (splitPane.getTopComponent() != null) {
                divLoc = splitPane.getDividerLocation();
            }
            
            mTextsPane.setVisible(false);
            mDeckPaneLabel.setVisible(false);
            splitPane.setTopComponent(null);
            mCardsPaneLabel.setText(CardUtil.getTranslation("collection"));
            mClearDeckBtn.setText(CardUtil.getTranslation("collection.summary"));
            splitPane.setResizeWeight(0.0);
            splitPane.setDividerSize(0);
            splitPane.setEnabled(false);
            
            for (JLabel label : ownedInfoCountRows) {
                label.setText("");
                label.setVisible(true);
            }
            for (JLabel label : ownedInfoRarityRows) {
                label.setText("");
                label.setVisible(true);
            }
            mFileOpPane.setVisible(false);
            updateCardListForCollection();
            splitPane.revalidate();
            splitPane.repaint();
            javax.swing.SwingUtilities.invokeLater(() -> {
                splitPane.setDividerLocation(0); // topmost
                splitPane.revalidate();
                splitPane.repaint();
            });
        } else {
            splitPane.setTopComponent(deckPane);
            mTextsPane.setVisible(true);
            mDeckPaneLabel.setVisible(true);
            mCardsPaneLabel.setText(CardUtil.getTranslation("cardlist"));
            mClearDeckBtn.setText(CardUtil.getTranslation("deck.clear"));
            splitPane.setDividerSize(8);
            splitPane.setEnabled(true);
            splitPane.setResizeWeight(0.5);
            splitPane.setDividerLocation(divLoc);

            collection.saveCollection();
            for (JLabel label : ownedInfoCountRows) {
                label.setText("");
                label.setVisible(false);
            }
            for (JLabel label : ownedInfoRarityRows) {
                label.setText("");
                label.setVisible(false);
            }
            mFileOpPane.setVisible(true);
            updateCardList();

            javax.swing.SwingUtilities.invokeLater(() -> {
                splitPane.revalidate();
                splitPane.repaint();
                /*
                if (divLoc > 0) {
                    // Only restore after components exist and are laid out
                    splitPane.setDividerLocation(divLoc);
                } else {
                    splitPane.setDividerLocation(0.5); 
                }*/
            });
        }
    }

    private class CollectionModeCallback implements CardListCallBack { // For redefining callback functions just for collection mode
        @Override
        public void addCard(Card card) {
            // Increment the collection count
            int newCount = collection.getCardOwnedCount(card.getId(), collectionAddVariant) + 1;
            collection.setCardOwnedCount(card.getId(), collectionAddVariant, newCount);
            updateCardListForCollection(); // Refresh the card list to show the updated count
            updateCardOwnedInfoLabel(card);
        }

        @Override
        public void removeCard(Card card) {
            // Decrement the collection count
            int newCount = collection.getCardOwnedCount(card.getId(), collectionAddVariant) - 1;
            collection.setCardOwnedCount(card.getId(), collectionAddVariant, newCount);
            updateCardListForCollection(); // Refresh the card list to show the updated count
            updateCardOwnedInfoLabel(card);
        }

        @Override
        public void showCard(Card card) {
            // Show card details (same as in collection mode)
            mCardDetailPane.removeAll();

            currentCard = card;

            for (String lang : Config.FALLBACK_ORDER) {
                if (collectionAddVariant == 0 || collectionAddVariant >= currentCard.getVariants().length) {
                    cardIcon = new ImageIcon("resources/cards/" + lang + "/" + card.getPack() + "/" + card.getId() + ".png");
                } else {
                    cardIcon = new ImageIcon("resources/cards_variant/" + lang + "/" + card.getPack() + "/" + card.getId() + "@" + collectionAddVariant + ".png");
                }
                if (cardIcon.getIconWidth() > 0) {
                    break;
                }
            }
            
            if (card.getMaxCount() == 1) {
                cardId.setText(card.getId() + " [" + CardUtil.getTranslation("restricted") + "]");
                cardId.setForeground(new Color(160, 128, 0));
            } else if (card.getMaxCount() == 0) {
                cardId.setText(card.getId() + " [" + CardUtil.getTranslation("banned") + "]");
                cardId.setForeground(new Color(160, 0, 0));
            } else {
                cardId.setText(card.getId());
                cardId.setForeground(Color.BLACK);
            }

            cardName.setText(card.getName());
            clearTranslations();

            updateCardOwnedInfoLabel(card);

            mCardTranslationPane.revalidate();
            mCardTranslationPane.repaint();
            sidebarPanel.revalidate();
            sidebarPanel.repaint();

            int translationHeight = mCardTranslationPane.getPreferredSize().height;
            int fileOpHeight = mFileOpPane.getHeight();
            int sidebarHeight = sidebarPanel.getHeight();
            int cardInfoHeight = cardId.getPreferredSize().height + cardName.getPreferredSize().height;

            int textPadding = (Config.LARGE_TRANSLATION_TEXT ? 100 : 50);
            previewHeight = Math.min(sidebarHeight - cardInfoHeight - (card.getCardTranslation() == null ? 0 : translationHeight) - fileOpHeight - textPadding, Config.CARD_PREVIEW_HEIGHT);

            Image image = cardIcon.getImage().getScaledInstance((int) (previewHeight / Config.CARD_RATIO), previewHeight, java.awt.Image.SCALE_SMOOTH);
            cardIcon = new ImageIcon(image);
            cardLabel = new JLabel(cardIcon);
            mCardDetailPane.add(cardLabel, BorderLayout.CENTER);
            mCardDetailPane.revalidate();
            mCardDetailPane.repaint();
        }
    }

    private void updateCardListForCollection() {
        mCardsPane.removeAll();
        CardList list = CardList.getInstance();
        List<Card> filteredCards = list.getSelectCards(true); // Ignore ownership for collection mode view

        UIUtil.showDeck(new CollectionModeCallback(), mCardsPane, filteredCards, null, 13, columns, UIUtil.CARD_SIZE_SMALL, 3, false);
        
        for (Card card : filteredCards) {
            for (ClickableCardPanel panel : card.getPanels()) {
                panel.updateCountsForCardList();
            }
        }
        
        mCardsPane.revalidate();
        mCardsPane.repaint();
    }

    private void updateCardOwnedInfoLabel(Card card) {
        
        CardRarity[] rarities = card.getVariants();
        String[] variantNames = card.getVariantNames();
        if (rarities == null || variantNames == null) {
            System.out.println("Card " + card.getId() + " has no variants. Maybe you saved as CSV instead of TXT?");
            return;
        }
        for (int i = 0; i < 7; i++) {
            if (i < rarities.length) {
                StringBuilder ownedInfo = new StringBuilder();
                int ownedCount = collection.getCardOwnedCount(card.getId(), i);
                //System.out.println(rarities[i].getName());
                //System.out.println(ownedCount);
                ownedInfo.append("<html>");
                ownedInfo.append("<img src=\"file:" + new File("resources/keyicons/24px/" + i + ".png").getAbsolutePath() + "\">").append("&nbsp;");
                ownedInfo.append("<img src=\"file:" + new File("resources/icons_rarity/24px/" + rarities[i].getName() + ".png").getAbsolutePath() + "\">");
                ownedInfo.append("&nbsp;").append(variantNames[i]);
                if (i < rarities.length - 1) {
                    ownedInfo.append("<br>");
                }
                ownedInfo.append("</html>");
                ownedInfoRarityRows[i].setText(ownedInfo.toString());
                ownedInfoCountRows[i].setText("×" + String.valueOf(ownedCount));
            } else {
                ownedInfoRarityRows[i].setText("");
                ownedInfoCountRows[i].setText("");
            }
        }

        sidebarPanel.revalidate();
        sidebarPanel.repaint();
    }

    private void updateCardOwnedInfoHighlight(int variantIndex) {
        for (int i = 0; i < ownedInfoCountRows.length; i++) {
            if (i == variantIndex && variantIndex > 0) {
                ownedInfoCountRows[i].setForeground(new Color(60,60,255,255));
            } else {
                ownedInfoCountRows[i].setForeground(Color.BLACK);
            }
        }

        sidebarPanel.revalidate();
        sidebarPanel.repaint();
    }

    private void updateCardPreview() {
        if (isCollectionMode) {
            for (String lang : Config.FALLBACK_ORDER) {
                if (collectionAddVariant == 0 || collectionAddVariant >= currentCard.getVariants().length) {
                    cardIcon = new ImageIcon("resources/cards/" + lang + "/" + currentCard.getPack() + "/" + currentCard.getId() + ".png");
                    
                } else {
                    cardIcon = new ImageIcon("resources/cards_variant/" + lang + "/" + currentCard.getPack() + "/" + currentCard.getId() + "@" + collectionAddVariant + ".png");
                }
                if (cardIcon.getIconWidth() > 0) {
                    break;
                }
            }
            cardLabel.setIcon(new ImageIcon(cardIcon.getImage().getScaledInstance((int) (previewHeight / Config.CARD_RATIO), previewHeight, java.awt.Image.SCALE_SMOOTH)));
            mCardDetailPane.revalidate();
            mCardDetailPane.repaint();
        }
    }
}