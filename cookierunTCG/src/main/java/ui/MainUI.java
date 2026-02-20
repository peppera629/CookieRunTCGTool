package ui;

import util.Config;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.BorderLayout;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.ActionMap;
import javax.swing.BorderFactory;
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

import ui.ClickableCardPanel.CardListCallBack;
import ui.SortSettingsWindow.ConfigChangedCallback;
import ui.FilePicker;
import util.CardUtil.CardColor;
import util.CardUtil.CardRarity;
import util.CardUtil.CardType;
import util.CardUtil;
import util.AppPaths;

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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.KeyboardFocusManager;
import java.awt.KeyEventDispatcher;
import java.awt.event.WindowAdapter;
import java.io.File;
import java.io.IOException;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;

// FIX: Variant highlight does not always work correctly (such as showing another card while holding down variant key can cause unavailable language to be selected)
// FEATURE: Restrict displayed cards based on language availability or pack release status based on region
// FEATURE: Add "Credits" popup
// FEATURE: Online saving (Google Drive) for decks and/or collection

public class MainUI implements CardListCallBack, ConfigChangedCallback, LanguageChangeListener {

    private JFrame frame;
    public static boolean DEBUG = false;
    // Secret features:
    // 1. Highlight translation-available cards
    public static boolean[] secretFeatures = {false};

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
        randomDrawSimWindow = new RandomDrawSim();
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
    private RandomDrawSim randomDrawSimWindow;
    private SettingsWindow settingsWindow;
	private SortSettingsWindow sortSettingsWindow;
    private DefaultState mDefaultState;
    private JPanel mTextsPane, mDeckDetailButtonsPane;
    private ScrollablePanel mCardsPane, mDeckPane, mSearchPane;

    private boolean globalKeyBindings = true;
    private KeyEventDispatcher keyEventDispatcher;
    private boolean quickEdit = false;
    private boolean attackAttrShown = false;
    
    //search panel
    private JPanel mSearchPaneOuter, sidebarPanel;
    private JLabel controlsHint, langChangeHint;
    private JCheckBox[] cb_color;
    private JCheckBox[] cb_flipType;
    private JCheckBox[] cb_level;
    private JCheckBox[] cb_pack;
    private JCheckBox[] cb_rarity;
    private JCheckBox[] cb_HP;
    private JCheckBox[] cb_HPAwaken;
    private JCheckBox[] cb_skillType;
    private JCheckBox[] cb_keyword;
    private JCheckBox[] cb_attackDMG;
    private JCheckBox[] cb_attackCost;
    private JCheckBox[] cb_avgDMG;
    private JCheckBox[] cb_peakDMG;
    private JCheckBox[] cb_status;
    private JCheckBox cb_type_cookie, cb_type_item, cb_type_trap, cb_type_stage;
    private JCheckBox cb_flip, cb_extra, cb_variant_sec, cb_variant_promo;
    private final Filter filter = new Filter(); 
    private JLabel labelColor, labelType, labelSeries, labelRarity, labelHP, labelHPAwaken, labelSkillType, labelKeyword, labelAttackAttr, labelAttackDMG, labelAttackCost, labelAvgDMG, labelPeakDMG, labelStatus;

    private Deck mDeck;
    private JPanel mCardDetailPane, mCardTranslationPane, deckPane, cardListPane, ownedInfoPanel, keywordLabelPanel, keywordOuterPanel, skillTypeLabelPanel, skillTypeOuterPanel, attackAttrLabelPanel, attackAttrOuterPanel, attackAttrBasePanel, statusLabelPanel, statusOuterPanel;
    private JPanel mFileOpPane, cardTranslationAttackGroup, cardTranslationFlavorTextGroup, deckDetailPane, centerPanel;
    private JLabel mDeckText;
    private JTextField searchBox;
    private JButton saveBtn, saveAsBtn, selectBtn, hideSearchPaneBtn, hidePreviewPaneBtn, quickSelectBtnBS, quickSelectBtnST;
    private JButton mClearDeckBtn, mRandomDrawSimBtn, button_search, button_clean, button_sort, button_settings;
    private JToggleButton button_collection;
    private JLabel mCardCountHintTxt, mFlipCountHintTxt, mExtraCountHintTxt, mDeckCookieSummaryHintTxt, 
        mLevelCountTxt, mFlipTypeCountTxt, cardLabel, filterResults, labelSearch;
    private JLabel mDeckItemHintTxt, mDeckTrapHintTxt, mDeckStageHintTxt, mDeckPaneLabel, mCardsPaneLabel;
    private JLabel mCardCountTxt, mFlipCountTxt, mExtraCountTxt, mDeckCookieSummaryTxt;
    private JLabel mDeckItemTxt, mDeckTrapTxt, mDeckStageTxt, cardId, cardName, cardAttackAttr, cardTranslationSkill, cardTranslationAttackCost;
    private JLabel cardTranslationAttack, cardTranslationAttackIcon, cardTranslationAttackThen, cardTranslationFlip, cardTranslationSkillFlavorText, cardTranslationSkillIcon, cardTranslationAttackFlavorText;
    private JLabel[] langLabels;
    private JLabel[] ownedInfoRarityRows;
    private JLabel[][] ownedInfoCountRows;
    private JSplitPane splitPane;
    private JButton showDeckBtn, showDeckDifferentialBtn;
    private ImageIcon cardIcon;
    private JScrollPane scrollDeckPane, scrollCardsPane, scrollSearchPane;
    public static Font CRnormal, CRbold, CRnormalLarge, CRnormalSmall, CRnormalEXLarge, CRboldLarge, CRboldSmall, CRboldEXLarge, CRtranslation, CRtranslationBold, CRboldEXLargeFilter;
    public static InputStream fontStream, fontStreamBold;
    public static Map<java.awt.Component, String> componentFontMap = new HashMap<>();
    private int columns = 6, previewHeight, divLoc = 400;
    private int currentSelectedCardLanguage = 0;
    private int prevLangIdx = currentSelectedCardLanguage;
    private JPanel mDeckDistributionPane;
    private JLabel mDeckDistCookie1, mDeckDistCookie2, mDeckDistCookie3, mDeckDistFlipHeal, mDeckDistFlipDraw, mDeckDistFlipSpecial, mDeckDistItem, mDeckDistTrap, mDeckDistStage, mDeckDistEmpty, mDeckDistExtra1, mDeckDistExtra2, mDeckDistExtra3;
    private JLabel mDeckDistCookieBorder, mDeckDistFlipBorder, mDeckDistOtherBorder, mDeckDistExtraBorder;
    private static int collectionAddVariant = 0;
    private boolean isCollectionMode = false, deckChanged = false;
    private Collection collection = Collection.getInstance();
    private Card currentCard;
    private Color highlightColor = new Color(60,60,255,255);
    private String currentDeckDirectory;

    private void initialize() {
        Config.loadConfig();
    	initialData();
    	initialUI();
        enableKeyOverrides(); // Overrides space key behavior (toggle checkbox and press button -> quick edit)
        keyBindingsToggle(true);
    }

    private void initialData() {
    	CardLoader.loadAllPacks();
    	mDefaultState = DefaultState.getInstance();
        mDeck = new Deck();
        frame = new JFrame();
        
        CardLoader.loadCardAvailability();
        CardLoader.preloadCardThumbnails(CardList.getInstance().getAllCards(), false);
    }

    private void keyBindingsToggle(boolean enabled) {
        globalKeyBindings = enabled;

        InputMap inputMap = frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = frame.getRootPane().getActionMap();
        
        // Key bindings for changing variants in collection mode
        for (int i = 1; i <= 9; i++) { // (I would do anything to replace typing out every function manually)
            final int variant = i - 1; // key 1 for base, key 2 for variant 1, ...
            String key = Integer.toString(i);
            if (!enabled) {
                inputMap.remove(KeyStroke.getKeyStroke(key));
                //inputMap.remove(KeyStroke.getKeyStroke("released " + key));
                actionMap.remove("variant" + key);
            } else {
                inputMap.put(KeyStroke.getKeyStroke(key), "variant" + key);
                //inputMap.put(KeyStroke.getKeyStroke("released " + key), "variant0");
                actionMap.put("variant" + key, new javax.swing.AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        int prevVariant = collectionAddVariant;
                        collectionAddVariant = variant;
                        while (currentCard != null && !currentCard.getAvailability(collectionAddVariant)[Config.COLLECTION_LANGUAGE_INDICES[currentSelectedCardLanguage]]) {
                            currentSelectedCardLanguage = (currentSelectedCardLanguage + 1) % Config.ALL_CARD_LANGUAGES.length;
                        }
                        updateLangLabels();
                        if (prevVariant != collectionAddVariant && currentCard != null && currentCard.getVariants().length > collectionAddVariant) {
                            updateCardOwnedInfoHighlight(variant);
                            updateCardPreview();
                        }
                    }
                });
            }
        }

        /*
        actionMap.put("variant0", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                collectionAddVariant = 0;
                currentSelectedCardLanguage = prevLangIdx;
                while (currentCard != null && !currentCard.getAvailability(collectionAddVariant)[Config.COLLECTION_LANGUAGE_INDICES[currentSelectedCardLanguage]]) {
                    currentSelectedCardLanguage = (currentSelectedCardLanguage + 1) % Config.ALL_CARD_LANGUAGES.length;
                }
                updateLangLabels();
                updateCardOwnedInfoHighlight(0);
                updateCardPreview();
            }
        });
        */
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "quickedit");
        actionMap.put("quickedit", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ClickableCardPanel.setQuickEditMode(true);
                scrollCardsPane.setWheelScrollingEnabled(false);
            }
        });
        inputMap.put(KeyStroke.getKeyStroke("released SPACE"), "quickedit_release");
        actionMap.put("quickedit_release", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ClickableCardPanel.setQuickEditMode(false);
                scrollCardsPane.setWheelScrollingEnabled(true);
            }
        });
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_QUOTE, 0), "langswitch");
        actionMap.put("langswitch", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentSelectedCardLanguage = (currentSelectedCardLanguage + 1) % Config.ALL_CARD_LANGUAGES.length;
                prevLangIdx = currentSelectedCardLanguage;
                while (currentCard != null && !currentCard.getAvailability(collectionAddVariant)[Config.COLLECTION_LANGUAGE_INDICES[currentSelectedCardLanguage]]) {
                    currentSelectedCardLanguage = (currentSelectedCardLanguage + 1) % Config.ALL_CARD_LANGUAGES.length;
                    prevLangIdx = currentSelectedCardLanguage;
                }
                updateLangLabels();
                updateCardOwnedInfoHighlight(collectionAddVariant);
                System.out.println("Switched selected language to " + Config.ALL_CARD_LANGUAGES[Config.COLLECTION_LANGUAGE_INDICES[currentSelectedCardLanguage]]);
            }
        });

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

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_T, 0), "secFeature0");
        actionMap.put("secFeature0", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!searchBox.isFocusOwner()) {
                    secretFeatures[0] = !secretFeatures[0];
                    ClickableCardPanel.setHighlightTranslationAvailable(secretFeatures[0]);
                    CardList.getInstance().updateAllCardPanels();
                    mCardsPane.revalidate();
                    mCardsPane.repaint();
                    mDeckPane.revalidate();
                    mDeckPane.repaint();
                }
            }
        });
    }

    private void enableKeyOverrides() {
        if (keyEventDispatcher != null) return;

        keyEventDispatcher = new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (!globalKeyBindings) return false;
                if (frame == null || !frame.isActive()) return false;
                if (e.isConsumed()) return false;
                if (e.getKeyCode() != KeyEvent.VK_SPACE) return false;

                // Allow typing spaces in text inputs
                var focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
                if (focusOwner instanceof JTextField) return false;

                // Pressed
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    if (!quickEdit) {
                        quickEdit = true;
                        ClickableCardPanel.setQuickEditMode(true);
                        if (scrollCardsPane != null) scrollCardsPane.setWheelScrollingEnabled(false);
                    }
                    e.consume();
                    return true; // stop JButton/JCheckBox default SPACE action (toggle check/press button)
                }

                // Released
                if (e.getID() == KeyEvent.KEY_RELEASED) {
                    if (quickEdit) {
                        quickEdit = false;
                        ClickableCardPanel.setQuickEditMode(false);
                        if (scrollCardsPane != null) scrollCardsPane.setWheelScrollingEnabled(true);
                    }
                    e.consume();
                    return true;
                }

                return false;
            }
        };

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keyEventDispatcher);
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
            CRnormal = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont((float) (16f * Config.UI_SCALE));
            CRnormalSmall = CRnormal.deriveFont((float) (12f * Config.UI_SCALE));
            CRnormalLarge = CRnormal.deriveFont((float) (20f * Config.UI_SCALE));
            CRnormalEXLarge = CRnormal.deriveFont((float) (28f * Config.UI_SCALE));
            CRbold = Font.createFont(Font.TRUETYPE_FONT, fontStreamBold).deriveFont((float) (16f * Config.UI_SCALE));
            CRboldLarge = CRbold.deriveFont((float) (20f * Config.UI_SCALE));
            CRboldSmall = CRbold.deriveFont((float) (12f * Config.UI_SCALE));
            CRboldEXLarge = CRbold.deriveFont((float) (28f * Config.UI_SCALE));
            CRboldEXLargeFilter = CRbold.deriveFont((float) (28f * Config.UI_SCALE));
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
        frame.setBounds(0, 0, (int) (1600 * Config.UI_SCALE), (int) (900 * Config.UI_SCALE));
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (deckChanged) {
                    ChoiceDialog dialog = new ChoiceDialog();
                    int result = dialog.show(CardUtil.getTranslation("confirmation"));
                    System.out.println(result);
                    if (result == 0) {
                        CardLoader.saveDeck(currentDeckDirectory, mDeckText.getText(), mDeck);
                        mDefaultState.setDefaultDeckName(mDeckText.getText());
                        mDefaultState.setDefaultDeckPath(AppPaths.userDataDir().resolve("deck").relativize(Paths.get(currentDeckDirectory)).toString());
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
        mSearchPaneOuter.setPreferredSize(new Dimension(Math.max((int) (125 + 225 * Config.UI_SCALE), (int) (350 * Config.UI_SCALE)), (int) (200 * Config.UI_SCALE)));
        //System.out.println(Config.UI_SCALE);
        mSearchPane = new ScrollablePanel();
        mSearchPane.setFocusable(true);
        mSearchPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mSearchPane.requestFocusInWindow();
            }
        });
        
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
        button_search.setRequestFocusEnabled(false);
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
        button_clean.setRequestFocusEnabled(false);
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
        button_collection.setRequestFocusEnabled(false);
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
        button_sort.setRequestFocusEnabled(false);
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
        button_settings.setRequestFocusEnabled(false);
        button_settings.setFont(CRnormal);
        componentFontMap.put(button_settings, "CRnormal"); // Store the font type as a String
        searchPanelButtons.add(button_settings, gbc_buttons);
        button_settings.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                settingsWindow.show();
            }
        });

        gbc_buttons.gridx = 0;
        gbc_buttons.weightx = 1.0;
        gbc_buttons.gridy = 4;
        gbc_buttons.gridwidth = 2;

        controlsHint = new JLabel("<html><img src=\"file:" + new File(AppPaths.dataDir().resolve("keyicons/24px/space.png").toString()).getAbsolutePath() + "\"> + <img src=\"file:" + new File(AppPaths.dataDir().resolve("keyicons/24px/mousewheel.png").toString()).getAbsolutePath() + "\">: " + CardUtil.getTranslation("hint.quickedit") + "</html>", JLabel.CENTER);
        controlsHint.setAlignmentX(Component.CENTER_ALIGNMENT);
        controlsHint.setFont(CRnormal);
        componentFontMap.put(controlsHint, "CRnormal");
        searchPanelButtons.add(controlsHint, gbc_buttons);

        // ===== 中間區域 =====

        centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        frame.getContentPane().add(centerPanel, BorderLayout.CENTER);

        // ==== 卡組
        deckPane = new JPanel(new BorderLayout());
        mDeckPaneLabel = new JLabel(CardUtil.getTranslation("deck"));
        mDeckPaneLabel.setFont(CRboldSmall);
        mDeckPaneLabel.setOpaque(true);
        mDeckPaneLabel.setBorder(null);
        mDeckPaneLabel.setBackground(new Color(10, 10, 10));
        mDeckPaneLabel.setForeground(new Color(255,255,255));
        componentFontMap.put(mDeckPaneLabel, "CRboldSmall");
        deckPane.add(mDeckPaneLabel, BorderLayout.NORTH);

        mDeckPane = new ScrollablePanel();
        mDeckPane.setLayout(new GridLayout(0, 6, 5, 5));
        scrollDeckPane = new JScrollPane(mDeckPane);
        scrollDeckPane.setMinimumSize(new Dimension(0, 0));
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
        mCardsPaneLabel.setBorder(null);
        mCardsPaneLabel.setBackground(new Color(10, 10, 10));
        mCardsPaneLabel.setForeground(new Color(255,255,255));
        componentFontMap.put(mCardsPaneLabel, "CRboldSmall");
        cardListPane.add(mCardsPaneLabel, BorderLayout.NORTH);

        mCardsPane = new ScrollablePanel();
        mCardsPane.setLayout(new GridLayout(0, 4, 5, 5));
        
        scrollCardsPane = new JScrollPane(mCardsPane);
        scrollCardsPane.setBackground(new Color(255, 255, 255));
        scrollCardsPane.setMinimumSize(new Dimension(0, 0));
        scrollCardsPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollCardsPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JScrollBar cardListScrollBar = scrollCardsPane.getVerticalScrollBar();
        cardListScrollBar.setUnitIncrement(16);
        cardListPane.add(scrollCardsPane, BorderLayout.CENTER);

        // ==== JSplitPane
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, deckPane, cardListPane);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerSize(16);
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
        hideSearchPaneBtn.setRequestFocusEnabled(false);
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
        gbc_deckbuttons.weightx = 5;
        mClearDeckBtn = new JButton(CardUtil.getTranslation("deck.clear"));
        mClearDeckBtn.setRequestFocusEnabled(false);
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
        gbc_deckbuttons.weightx = 5;
        mRandomDrawSimBtn = new JButton(CardUtil.getTranslation("deck.drawsim"));
        mRandomDrawSimBtn.setRequestFocusEnabled(false);
        mRandomDrawSimBtn.setFont(CRnormalLarge);
        componentFontMap.put(mRandomDrawSimBtn, "CRnormalLarge"); // Store the font type as a String
        mDeckDetailButtonsPane.add(mRandomDrawSimBtn, gbc_deckbuttons);
        mRandomDrawSimBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                randomDrawSimWindow.show(mDeck, mDeckText.getText());
            }
        });

        gbc_deckbuttons.gridx = 3;
        gbc_deckbuttons.weightx = 1;
        hidePreviewPaneBtn = new JButton();
        hidePreviewPaneBtn.setRequestFocusEnabled(false);
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

        mDeckDistributionPane = new JPanel();
        mDeckDistributionPane.setLayout(new GridBagLayout());
        deckDetailPane.add(mDeckDistributionPane, BorderLayout.NORTH);

        updateDeckDistribution();

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
        cardName.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (!isCollectionMode) {
                        attackAttrShown = !attackAttrShown;
                        cardAttackAttr.setVisible(attackAttrShown);
                    }
                }
            }
        });
        componentFontMap.put(cardName, "CRboldLarge"); // Store the font type as a String
        cardAttackAttr = new JLabel("", JLabel.CENTER);
        cardAttackAttr.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardAttackAttr.setVisible(attackAttrShown);
        cardAttackAttr.setFont(CRboldLarge);
        componentFontMap.put(cardAttackAttr, "CRboldLarge"); // Store the font type as a String

        cardInfo.add(cardId);
        cardInfo.add(cardName);
        cardInfo.add(cardAttackAttr);

        // ==== Card Preview
        mCardDetailPane = new JPanel();
        mCardDetailPane.setLayout(new BorderLayout());
        mCardDetailPane.setPreferredSize(new Dimension(Config.CARD_PREVIEW_WIDTH, (int) frame.getBounds().getHeight()- (int) (60 * Config.UI_SCALE)));
        cardInfo.add(mCardDetailPane);

        // ==== Card Ownership Info (when Collection Mode is active)
        ownedInfoPanel = new JPanel();
        ownedInfoPanel.setLayout(new GridBagLayout());
        ownedInfoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        cardInfo.add(ownedInfoPanel);

        langLabels = new JLabel[Config.ALL_CARD_LANGUAGES.length];
        for (int i = 0; i < Config.ALL_CARD_LANGUAGES.length; i++) {
            langLabels[i] = new JLabel("", JLabel.CENTER);
            langLabels[i].setFont(CRboldSmall);
            componentFontMap.put(langLabels[i], "CRboldSmall"); // Store the font type
        }

        ownedInfoRarityRows = new JLabel[] {
            new JLabel("", JLabel.LEFT),
            new JLabel("", JLabel.LEFT),
            new JLabel("", JLabel.LEFT),
            new JLabel("", JLabel.LEFT),
            new JLabel("", JLabel.LEFT),
            new JLabel("", JLabel.LEFT),
            new JLabel("", JLabel.LEFT)
        };

        ownedInfoCountRows = new JLabel[][] {
            {new JLabel("", JLabel.CENTER), new JLabel("", JLabel.CENTER), new JLabel("", JLabel.CENTER)},
            {new JLabel("", JLabel.CENTER), new JLabel("", JLabel.CENTER), new JLabel("", JLabel.CENTER)},
            {new JLabel("", JLabel.CENTER), new JLabel("", JLabel.CENTER), new JLabel("", JLabel.CENTER)},
            {new JLabel("", JLabel.CENTER), new JLabel("", JLabel.CENTER), new JLabel("", JLabel.CENTER)},
            {new JLabel("", JLabel.CENTER), new JLabel("", JLabel.CENTER), new JLabel("", JLabel.CENTER)},
            {new JLabel("", JLabel.CENTER), new JLabel("", JLabel.CENTER), new JLabel("", JLabel.CENTER)},
            {new JLabel("", JLabel.CENTER), new JLabel("", JLabel.CENTER), new JLabel("", JLabel.CENTER)}
        };

        GridBagConstraints gbc_owned = new GridBagConstraints();
        gbc_owned.fill = GridBagConstraints.BOTH;
        gbc_owned.gridx = 1;
        gbc_owned.gridy = 0;
        for (int i = 0; i < Config.ALL_CARD_LANGUAGES.length; i++) {
            gbc_owned.weightx = 1;
            ownedInfoPanel.add(langLabels[i], gbc_owned);
            gbc_owned.gridx++;
        }
        
        gbc_owned.gridy = 1;
        for (int i = 0; i < ownedInfoRarityRows.length; i++) {
            gbc_owned.gridx = 0;
            gbc_owned.weightx = 5;
            ownedInfoRarityRows[i].setFont(CRnormal);
            componentFontMap.put(ownedInfoRarityRows[i], "CRnormal"); // Store the font type
            ownedInfoPanel.add(ownedInfoRarityRows[i], gbc_owned);
            gbc_owned.gridx = 1;
            gbc_owned.weightx = 1;
            for (int j = 0; j < ownedInfoCountRows[i].length; j++) {
                ownedInfoCountRows[i][j].setFont(CRboldEXLarge);
                componentFontMap.put(ownedInfoCountRows[i][j], "CRboldEXLarge"); // Store the font type
                ownedInfoPanel.add(ownedInfoCountRows[i][j], gbc_owned);
                gbc_owned.gridx++;
            }
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
        mFileOpPane.setPreferredSize(new Dimension(Config.CARD_PREVIEW_WIDTH, (int) (60 * Config.UI_SCALE)));
        sidebarPanel.add(mFileOpPane, BorderLayout.SOUTH);

        GridBagConstraints gbc_panel = new GridBagConstraints();
        gbc_panel.fill = GridBagConstraints.BOTH;
        gbc_panel.gridx = 0;
        gbc_panel.gridwidth = 3;
        gbc_panel.gridy = 0;
        mDeckText = new JLabel();
        mDeckText.setText(mDefaultState.getDefaultDeckName());
        mDeckText.setFont(CRnormal);
        mDeckText.setPreferredSize(new Dimension(Config.CARD_PREVIEW_WIDTH, CRnormal.getSize()+11));
        componentFontMap.put(mDeckText, "CRnormal");
        mFileOpPane.add(mDeckText, gbc_panel);

        gbc_panel.gridwidth = 1;
        gbc_panel.gridx = 3;
        gbc_panel.gridy = 0;
        saveBtn = new JButton(CardUtil.getTranslation("save"));
        saveBtn.setRequestFocusEnabled(false);
        saveBtn.setFont(CRnormal);
        componentFontMap.put(saveBtn, "CRnormal"); // Store the font type as a String
        mFileOpPane.add(saveBtn, gbc_panel);
        saveBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CardLoader.saveDeck(currentDeckDirectory, mDeckText.getText(), mDeck);
                mDefaultState.setDefaultDeckName(mDeckText.getText());
                mDefaultState.setDefaultDeckPath(AppPaths.userDataDir().resolve("deck").relativize(Paths.get(currentDeckDirectory)).toString());
                mDefaultState.saveDefaultState();
                Dialog dialog = new Dialog();
                deckChanged = false;
                dialog.show(CardUtil.getTranslation("deck.saved"));
            }
        });

        gbc_panel.gridwidth = 1;
        gbc_panel.gridx = 0;
        gbc_panel.gridy = 1;
        saveAsBtn = new JButton(CardUtil.getTranslation("saveas"));
        saveAsBtn.setRequestFocusEnabled(false);
        saveAsBtn.setFont(CRnormalSmall);
        componentFontMap.put(saveAsBtn, "CRnormalSmall"); // Store the font type as a String
        mFileOpPane.add(saveAsBtn, gbc_panel);
        saveAsBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                FilePicker filePicker = new FilePicker();
                String pickedDirectory = filePicker.show("save");
                if (pickedDirectory != null) {
                    currentDeckDirectory = pickedDirectory;
                    CardLoader.saveDeck(pickedDirectory, pickedDirectory.substring(pickedDirectory.lastIndexOf(File.separator) + 1), mDeck);
                    mDeckText.setText(pickedDirectory.substring(pickedDirectory.lastIndexOf(File.separator) + 1, pickedDirectory.length() - 4));
                    mDefaultState.setDefaultDeckName(mDeckText.getText());
                    mDefaultState.setDefaultDeckPath(AppPaths.userDataDir().resolve("deck").relativize(Paths.get(pickedDirectory)).toString());
                    mDefaultState.saveDefaultState();
                    Dialog dialog = new Dialog();
                    deckChanged = false;
                    dialog.show(CardUtil.getTranslation("deck.saved"));
                }
            }
        });

        gbc_panel.gridx = 1;
        selectBtn = new JButton(CardUtil.getTranslation("select.file"));
        selectBtn.setRequestFocusEnabled(false);
        selectBtn.setFont(CRnormalSmall);
        componentFontMap.put(selectBtn, "CRnormalSmall"); // Store the font type as a String
        selectBtn.setActionCommand("Select File");
        mFileOpPane.add(selectBtn, gbc_panel);
        selectBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				FilePicker filePicker = new FilePicker();
                String pickedDirectory = filePicker.show("load");
				if (pickedDirectory != null) {
                    if (deckChanged) {
                        ChoiceDialog dialog = new ChoiceDialog();
                        int result = dialog.show(CardUtil.getTranslation("confirmation"));
                        System.out.println(result);
                        if (result == 0) {
                            CardLoader.saveDeck(currentDeckDirectory, mDeckText.getText(), mDeck);
                            mDefaultState.setDefaultDeckName(mDeckText.getText());
                            mDefaultState.setDefaultDeckPath(AppPaths.userDataDir().resolve("deck").relativize(Paths.get(currentDeckDirectory)).toString());
                            mDefaultState.saveDefaultState();
                        } else if (result == 2) {
                            return; // Cancel the file selection
                        }
                    }
                    File selectedFile = new File(pickedDirectory);
                    String filename = pickedDirectory.substring(pickedDirectory.lastIndexOf(File.separator) + 1);
                    System.out.println(filename);
                    mDeckText.setText(filename.substring(0, filename.length() - 4));
                    mDeck.clear();
                    CardList.getInstance().clearCardListCount();
                    currentDeckDirectory = pickedDirectory;
                    mDeck = CardLoader.loadDeck(pickedDirectory, filename.substring(0, filename.length() - 4));
                    mDeck.sort();
                    updateDeck();
                    CardList.getInstance().updateAllCardPanels();
                    mDefaultState.setDefaultDeckName(mDeckText.getText());
                    mDefaultState.setDefaultDeckPath(AppPaths.userDataDir().resolve("deck").relativize(selectedFile.toPath()).toString());
                    mDefaultState.saveDefaultState();
                    deckChanged = false;
				} 
            }
        });
        
        gbc_panel.gridx = 2;
        showDeckBtn = new JButton(CardUtil.getTranslation("deck.show"));
        showDeckBtn.setRequestFocusEnabled(false);
        showDeckBtn.setFont(CRnormalSmall);
        componentFontMap.put(showDeckBtn, "CRnormalSmall"); // Store the font type as a String
        mFileOpPane.add(showDeckBtn, gbc_panel);
        showDeckBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		deckWindow.show(mDeck, mDeckText.getText());
        	}
        });

        gbc_panel.gridx = 3;
        showDeckDifferentialBtn = new JButton(CardUtil.getTranslation("deck.compare"));
        showDeckDifferentialBtn.setRequestFocusEnabled(false);
        showDeckDifferentialBtn.setFont(CRnormalSmall);
        componentFontMap.put(showDeckDifferentialBtn, "CRnormalSmall"); // Store the font type as a String
        mFileOpPane.add(showDeckDifferentialBtn, gbc_panel);
        showDeckDifferentialBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
                FilePicker filePicker = new FilePicker();
                String[] pickedDirectory = filePicker.showForCompare();
				if (pickedDirectory != null) {
                    boolean compareMode = pickedDirectory[1].equals("to");
                    File selectedFile = new File(pickedDirectory[0]);
                    String filename = pickedDirectory[0].substring(pickedDirectory[0].lastIndexOf(File.separator) + 1);
                    Map<String, Integer> mDeck2 = CardLoader.loadDeckTemp(filename.substring(0, filename.length() - 4));
                    deckDifferentialWindow.show(mDeck, mDeckText.getText(), mDeck2, filename.substring(0, filename.length() - 4), compareMode);
				}
            }
        });

        frame.getContentPane().add(sidebarPanel, BorderLayout.EAST);

        updateCardList();
        currentDeckDirectory = AppPaths.userDataDir().resolve("deck").resolve(mDefaultState.getDefaultDeckPath()).toString();
        System.out.println("Loading deck from: " + currentDeckDirectory);
        mDeck = CardLoader.loadDeck(currentDeckDirectory, mDeckText.getText());
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
        searchBox.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                keyBindingsToggle(false);
            }

            public void focusLost(FocusEvent e) {
                keyBindingsToggle(true);
            }
        });
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
        typeCheckboxGroup.setLayout(new GridBagLayout());
        typeCheckboxGroup.setBorder(filterBorder);
        typeOuterPanel.add(typeCheckboxGroup);
        mSearchPane.add(typeOuterPanel);

        GridBagConstraints gbc_type = new GridBagConstraints();
        gbc_type.anchor = GridBagConstraints.WEST;
        gbc_type.gridx = 0;

        // Rows: Cookie + Levels, Cookie HP, Flip + Flip Types, [Extra, Item, Trap, Stage]
        JPanel[] typeCheckboxGroupRows = new JPanel[4];
        for (int i=0; i<4; i++) {
            gbc_type.gridy = i;
        	typeCheckboxGroupRows[i] = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        	typeCheckboxGroup.add(typeCheckboxGroupRows[i], gbc_type);
        } 

        cb_type_cookie = new JCheckBox(CardUtil.getTranslation("filter.cookie"));
		cb_type_cookie.setSelected(mDefaultState.getDefaultTypeFlag(0));
        cb_type_cookie.setFont(CRnormal);
        componentFontMap.put(cb_type_cookie, "CRnormal"); // Store the font type as a String
        typeCheckboxGroupRows[0].add(cb_type_cookie);
        cb_type_cookie.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	mDefaultState.setDefaultTypeFlag(0, cb_type_cookie.isSelected());
            	for (JCheckBox cb : cb_level) {
            		cb.setEnabled(cb_type_cookie.isSelected());

            	}
                for (JCheckBox cb : cb_HP) {
                    cb.setEnabled(cb_type_cookie.isSelected());
                }
                for (JCheckBox cb : cb_HPAwaken) {
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
            typeCheckboxGroupRows[0].add(cb_level[i]);
            cb_level[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	mDefaultState.setDefaultLvFlag(lv, cb_level[id].isSelected());
                }
            });
            cb_level[i].setEnabled(cb_type_cookie.isSelected());
            
        }

        JPanel hpCheckboxGroups = new JPanel(new GridLayout(0, 1));
        typeCheckboxGroupRows[1].add(hpCheckboxGroups);

        JPanel hpPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        hpCheckboxGroups.add(hpPanel);

        labelHP = new JLabel("HP:");
        labelHP.setFont(CRnormal);
        componentFontMap.put(labelHP, "CRnormal"); // Store the font type as a String
        hpPanel.add(labelHP);

        cb_HP = new JCheckBox[CardUtil.HP_MAX];
        for(int i=0; i<CardUtil.HP_MAX; i++) {
            final int id = i;
            final int hp = i+1;
        	cb_HP[i] = new JCheckBox(Integer.toString(hp));
        	cb_HP[i].setSelected(mDefaultState.getDefaultHPFlag(hp));
            cb_HP[i].setFont(CRnormal);
            componentFontMap.put(cb_HP[i], "CRnormal"); // Store the font type as a String
            hpPanel.add(cb_HP[i]);
            
            cb_HP[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	mDefaultState.setDefaultHPFlag(hp, cb_HP[id].isSelected());
                }
            });
            cb_HP[i].setEnabled(cb_type_cookie.isSelected());
        }

        JPanel hpAwakenPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        hpCheckboxGroups.add(hpAwakenPanel);

        labelHPAwaken = new JLabel("HP+:");
        labelHPAwaken.setFont(CRnormal);
        componentFontMap.put(labelHPAwaken, "CRnormal"); // Store the font type as a String
        hpAwakenPanel.add(labelHPAwaken);

        cb_HPAwaken = new JCheckBox[CardUtil.AWAKEN_HP.size()];
        for(int i=0; i<CardUtil.AWAKEN_HP.size(); i++) {
            final int id = i;
        	cb_HPAwaken[i] = new JCheckBox("+" + Integer.toString(CardUtil.AWAKEN_HP.get(i)));
        	cb_HPAwaken[i].setSelected(mDefaultState.getDefaultHPAwakenFlag(i));
            cb_HPAwaken[i].setFont(CRnormal);
            componentFontMap.put(cb_HPAwaken[i], "CRnormal"); // Store the font type as a String
            hpAwakenPanel.add(cb_HPAwaken[i]);
            
            cb_HPAwaken[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	mDefaultState.setDefaultHPAwakenFlag(id, cb_HPAwaken[id].isSelected());
                }
            });
            cb_HPAwaken[i].setEnabled(cb_type_cookie.isSelected());
        }
        
        cb_flip = new JCheckBox(CardUtil.getTranslation("filter.flip"));
        cb_flip.setSelected(mDefaultState.getDefaultFlipFlag());
        cb_flip.setFont(CRnormal);
        componentFontMap.put(cb_flip, "CRnormal"); // Store the font type as a String
        typeCheckboxGroupRows[2].add(cb_flip);
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
            typeCheckboxGroupRows[2].add(cb_flipType[i]);
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
        typeCheckboxGroupRows[3].add(cb_extra);
        cb_extra.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	mDefaultState.setDefaultExtraFlag(cb_extra.isSelected());
            }
        });
        
        cb_type_item = new JCheckBox(CardUtil.getTranslation("filter.item"));
        cb_type_item.setSelected(mDefaultState.getDefaultTypeFlag(1));
        cb_type_item.setFont(CRnormal);
        componentFontMap.put(cb_type_item, "CRnormal"); // Store the font type as a String
        typeCheckboxGroupRows[3].add(cb_type_item);
        cb_type_item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	mDefaultState.setDefaultTypeFlag(1, cb_type_item.isSelected());
            }
        });


        cb_type_trap = new JCheckBox(CardUtil.getTranslation("filter.trap"));
        cb_type_trap.setSelected(mDefaultState.getDefaultTypeFlag(2));
        cb_type_trap.setFont(CRnormal);
        componentFontMap.put(cb_type_trap, "CRnormal"); // Store the font type as a String
        typeCheckboxGroupRows[3].add(cb_type_trap);
        cb_type_trap.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	mDefaultState.setDefaultTypeFlag(2, cb_type_trap.isSelected());
            }
        });


        cb_type_stage = new JCheckBox(CardUtil.getTranslation("filter.stage"));
        cb_type_stage.setSelected(mDefaultState.getDefaultTypeFlag(3));
        cb_type_stage.setFont(CRnormal);
        componentFontMap.put(cb_type_stage, "CRnormal"); // Store the font type as a String
        typeCheckboxGroupRows[3].add(cb_type_stage);
        cb_type_stage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	mDefaultState.setDefaultTypeFlag(3, cb_type_stage.isSelected());
            }
        });

        // ========================= pack ==================================

        labelSeries = new JLabel(CardUtil.getTranslation("series"), JLabel.LEFT);
        labelSeries.setFont(CRboldEXLargeFilter);
        componentFontMap.put(labelSeries, "CRboldEXLargeFilter"); // Store the font type as a String
        JPanel seriesLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // Wrap the label
        seriesLabelPanel.add(labelSeries);
        mSearchPane.add(seriesLabelPanel);

        JPanel quickSelectBtnGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        mSearchPane.add(quickSelectBtnGroup);

        quickSelectBtnBS = new JButton(CardUtil.getTranslation("filter.BS"));
        quickSelectBtnBS.setRequestFocusEnabled(false);
        quickSelectBtnBS.setFont(CRnormal);
        componentFontMap.put(quickSelectBtnBS, "CRnormal");
        quickSelectBtnGroup.add(quickSelectBtnBS);
        
        quickSelectBtnST = new JButton(CardUtil.getTranslation("filter.ST"));
        quickSelectBtnST.setRequestFocusEnabled(false);
        quickSelectBtnST.setFont(CRnormal);
        componentFontMap.put(quickSelectBtnST, "CRnormal");
        quickSelectBtnGroup.add(quickSelectBtnST);
        
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
            if (!CardUtil.CardPackAvailability.get(CardUtil.CardPack.get(i)).get(Config.REGION)) {
                cb_pack[i].setEnabled(false);
                cb_pack[i].setSelected(false);
            }
            System.out.println("Pack " + CardUtil.CardPack.get(i) + CardUtil.CardPackAvailability.get(CardUtil.CardPack.get(i)).get(Config.REGION));
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
                    if (cb.getText().contains("BS") && !cb.isSelected() && cb.isEnabled()) {
                        quickSelectMode = true;
                        break;
                    }
                }

                for (JCheckBox cb : cb_pack) {
                    if (cb.getText().contains("BS") && cb.isEnabled()) {
                        cb.setSelected(quickSelectMode);
                        mDefaultState.setDefaultPackFlag(cb.getText(), quickSelectMode);
                    }
                }
            }
        });

        quickSelectBtnST.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean quickSelectMode = false; // All selected, disable all
                for (JCheckBox cb : cb_pack) {
                    if (cb.getText().contains("ST") && !cb.isSelected() && cb.isEnabled()) {
                        quickSelectMode = true; // At least one is unselected, enable all
                        break;
                    }
                }

                for (JCheckBox cb : cb_pack) {
                    if (cb.getText().contains("ST") && cb.isEnabled()) {
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
        gbc_rarity.gridwidth = 6;
        cb_variant_sec = new JCheckBox(CardUtil.getTranslation("rarity.variant.secret"));
        cb_variant_sec.setFont(CRnormal);
        componentFontMap.put(cb_variant_sec, "CRnormal"); // Store the font type as a String
        rarityCheckboxGroup.add(cb_variant_sec, gbc_rarity);
        rarityOuterPanel.add(rarityCheckboxGroup);
        cb_variant_sec.setVisible(Config.ADVANCED_FILTERING);
        if (!cb_variant_sec.isVisible()) {
            cb_variant_sec.setSelected(false);
        }

        gbc_rarity.gridx = 0;
        gbc_rarity.gridy = 2;
        gbc_rarity.gridwidth = 6;
        cb_variant_promo = new JCheckBox(CardUtil.getTranslation("rarity.variant.promo"));
        cb_variant_promo.setFont(CRnormal);
        componentFontMap.put(cb_variant_promo, "CRnormal"); // Store the font type as a String
        rarityCheckboxGroup.add(cb_variant_promo, gbc_rarity);
        rarityOuterPanel.add(rarityCheckboxGroup);
        cb_variant_promo.setVisible(Config.ADVANCED_FILTERING);
        if (!cb_variant_promo.isVisible()) {
            cb_variant_promo.setSelected(false);
        }

        // ========================= attack attribute filtering =========================
        labelAttackAttr = new JLabel(CardUtil.getTranslation("filter.attackattr"), JLabel.LEFT);
        labelAttackAttr.setFont(CRboldEXLargeFilter);
        
        componentFontMap.put(labelAttackAttr, "CRboldEXLargeFilter"); // Store the font type as a String
        attackAttrLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // Wrap the label
        attackAttrLabelPanel.add(labelAttackAttr);
        attackAttrLabelPanel.setVisible(Config.ADVANCED_FILTERING);
        mSearchPane.add(attackAttrLabelPanel);

        attackAttrBasePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // Wrap the grid
        attackAttrBasePanel.setVisible(Config.ADVANCED_FILTERING);
        mSearchPane.add(attackAttrBasePanel);

        attackAttrOuterPanel = new JPanel(); // Wrap the grid
        attackAttrOuterPanel.setLayout(new GridBagLayout());
        attackAttrOuterPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        attackAttrOuterPanel.setBorder(filterBorder);
        attackAttrOuterPanel.setVisible(Config.ADVANCED_FILTERING);
        GridBagConstraints gbc_attackAttrOuter = new GridBagConstraints();
        gbc_attackAttrOuter.gridx = 0;
        gbc_attackAttrOuter.gridy = 0;
        attackAttrBasePanel.add(attackAttrOuterPanel);
        JPanel attackCostPanel = new JPanel();
        attackCostPanel.setLayout(new BoxLayout(attackCostPanel, BoxLayout.Y_AXIS));
        attackCostPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        attackAttrOuterPanel.add(attackCostPanel, gbc_attackAttrOuter);

        labelAttackCost = new JLabel(CardUtil.getTranslation("filter.attackcost"), JLabel.LEFT);
        labelAttackCost.setAlignmentX(Component.LEFT_ALIGNMENT);
        labelAttackCost.setFont(CRnormal);
        componentFontMap.put(labelAttackCost, "CRnormal"); // Store the font type as a String
        attackCostPanel.add(labelAttackCost);

        JPanel attackCostCheckboxGroup = new JPanel();
        attackCostCheckboxGroup.setAlignmentX(Component.LEFT_ALIGNMENT);
        attackCostCheckboxGroup.setLayout(new GridBagLayout());
        GridBagConstraints gbc_attackCost = new GridBagConstraints();
        attackCostPanel.add(attackCostCheckboxGroup);
        gbc_attackCost.anchor = GridBagConstraints.WEST;
        gbc_attackCost.gridx = 0;
        gbc_attackCost.gridy = 0;

        cb_attackCost = new JCheckBox[CardUtil.ATTACK_COST_MAX+1];
        for(int i=0; i<CardUtil.ATTACK_COST_MAX+1; i++) {
        	cb_attackCost[i] = new JCheckBox(Integer.toString(i));
        	//cb.setSelected(mDefaultState.getDefaultAttackAttrFlag(i));
            cb_attackCost[i].setFont(CRnormal);
            componentFontMap.put(cb_attackCost[i], "CRnormal"); // Store the font type as a String
            gbc_attackCost.gridx = i % 6;
            gbc_attackCost.gridy = i / 6;
            attackCostCheckboxGroup.add(cb_attackCost[i], gbc_attackCost);
        }
        gbc_attackAttrOuter.gridy++;
        JPanel attackDMGPanel = new JPanel();
        attackDMGPanel.setLayout(new BoxLayout(attackDMGPanel, BoxLayout.Y_AXIS));
        attackDMGPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        attackAttrOuterPanel.add(attackDMGPanel, gbc_attackAttrOuter);

        labelAttackDMG = new JLabel(CardUtil.getTranslation("filter.attackdmg"), JLabel.LEFT);
        labelAttackDMG.setAlignmentX(Component.LEFT_ALIGNMENT);
        labelAttackDMG.setFont(CRnormal);
        componentFontMap.put(labelAttackDMG, "CRnormal"); // Store the font type as a String
        attackDMGPanel.add(labelAttackDMG);

        JPanel attackDMGCheckboxGroup = new JPanel();
        attackDMGCheckboxGroup.setAlignmentX(Component.LEFT_ALIGNMENT);
        attackDMGCheckboxGroup.setLayout(new GridBagLayout());
        GridBagConstraints gbc_attackDMG = new GridBagConstraints();
        attackDMGPanel.add(attackDMGCheckboxGroup);
        gbc_attackDMG.anchor = GridBagConstraints.WEST;
        gbc_attackDMG.gridx = 0;
        gbc_attackDMG.gridy = 0;

        cb_attackDMG = new JCheckBox[CardUtil.ATTACK_MAX+1];
        for(int i=0; i<CardUtil.ATTACK_MAX+1; i++) {
        	cb_attackDMG[i] = new JCheckBox(Integer.toString(i));
        	//cb.setSelected(mDefaultState.getDefaultAttackAttrFlag(i));
            cb_attackDMG[i].setFont(CRnormal);
            componentFontMap.put(cb_attackDMG[i], "CRnormal"); // Store the font type as a String
            gbc_attackDMG.gridx = i % 6;
            gbc_attackDMG.gridy = i / 6;
            attackDMGCheckboxGroup.add(cb_attackDMG[i], gbc_attackDMG);
        }

        gbc_attackAttrOuter.gridy++;
        JPanel avgDMGPanel = new JPanel();
        avgDMGPanel.setLayout(new BoxLayout(avgDMGPanel, BoxLayout.Y_AXIS));
        avgDMGPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        attackAttrOuterPanel.add(avgDMGPanel, gbc_attackAttrOuter);

        labelAvgDMG = new JLabel(CardUtil.getTranslation("filter.avgdmg"), JLabel.LEFT);
        labelAvgDMG.setToolTipText(CardUtil.getTranslation("filter.avgdmg.tooltip"));
        labelAvgDMG.setAlignmentX(Component.LEFT_ALIGNMENT);
        labelAvgDMG.setFont(CRnormal);
        componentFontMap.put(labelAvgDMG, "CRnormal"); // Store the font type as a String
        avgDMGPanel.add(labelAvgDMG);
        JPanel avgDMGCheckboxGroup = new JPanel();
        avgDMGCheckboxGroup.setAlignmentX(Component.LEFT_ALIGNMENT);
        avgDMGCheckboxGroup.setLayout(new GridBagLayout());
        GridBagConstraints gbc_avgDMG = new GridBagConstraints();
        avgDMGPanel.add(avgDMGCheckboxGroup);
        gbc_avgDMG.anchor = GridBagConstraints.WEST;
        gbc_avgDMG.gridx = 0;
        gbc_avgDMG.gridy = 0;

        cb_avgDMG = new JCheckBox[CardUtil.PEAK_MAX+1];
        for(int i=0; i<CardUtil.PEAK_MAX+1; i++) {
        	cb_avgDMG[i] = new JCheckBox(Integer.toString(i));
        	//cb.setSelected(mDefaultState.getDefaultAttackAttrFlag(i));
            cb_avgDMG[i].setFont(CRnormal);
            componentFontMap.put(cb_avgDMG[i], "CRnormal"); // Store the font type as a String
            gbc_avgDMG.gridx = i % 6;
            gbc_avgDMG.gridy = i / 6;
            avgDMGCheckboxGroup.add(cb_avgDMG[i], gbc_avgDMG);
        }

        gbc_attackAttrOuter.gridy++;
        JPanel peakDMGPanel = new JPanel();
        peakDMGPanel.setLayout(new BoxLayout(peakDMGPanel, BoxLayout.Y_AXIS));
        peakDMGPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        attackAttrOuterPanel.add(peakDMGPanel, gbc_attackAttrOuter);

        labelPeakDMG = new JLabel(CardUtil.getTranslation("filter.peakdmg"), JLabel.LEFT);
        labelPeakDMG.setToolTipText(CardUtil.getTranslation("filter.peakdmg.tooltip"));
        labelPeakDMG.setAlignmentX(Component.LEFT_ALIGNMENT);
        labelPeakDMG.setFont(CRnormal);
        componentFontMap.put(labelPeakDMG, "CRnormal"); // Store the font type as a String
        peakDMGPanel.add(labelPeakDMG);

        JPanel peakDMGCheckboxGroup = new JPanel();
        peakDMGCheckboxGroup.setAlignmentX(Component.LEFT_ALIGNMENT);
        peakDMGCheckboxGroup.setLayout(new GridBagLayout());
        GridBagConstraints gbc_peakDMG = new GridBagConstraints();
        peakDMGPanel.add(peakDMGCheckboxGroup);
        gbc_peakDMG.anchor = GridBagConstraints.WEST;
        gbc_peakDMG.gridx = 0;
        gbc_peakDMG.gridy = 0;

        cb_peakDMG = new JCheckBox[CardUtil.PEAK_MAX+1];
        for(int i=0; i<CardUtil.PEAK_MAX+1; i++) {
        	cb_peakDMG[i] = new JCheckBox(Integer.toString(i));
        	//cb.setSelected(mDefaultState.getDefaultAttackAttrFlag(i));
            cb_peakDMG[i].setFont(CRnormal);
            componentFontMap.put(cb_peakDMG[i], "CRnormal"); // Store the font type as a String
            gbc_peakDMG.gridx = i % 6;
            gbc_peakDMG.gridy = i / 6;
            peakDMGCheckboxGroup.add(cb_peakDMG[i], gbc_peakDMG);
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
        }
        keywordOuterPanel.add(keywordCheckboxGroup);

        keywordLabelPanel.setVisible(Config.ADVANCED_FILTERING);
        keywordOuterPanel.setVisible(Config.ADVANCED_FILTERING);

        // ========================= card legality =========================
        labelStatus = new JLabel(CardUtil.getTranslation("filter.status"), JLabel.LEFT);
        labelStatus.setFont(CRboldEXLargeFilter);
        componentFontMap.put(labelStatus, "CRboldEXLargeFilter"); // Store the font type as a String
        statusLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // Wrap the label
        statusLabelPanel.add(labelStatus);
        mSearchPane.add(statusLabelPanel);

        statusOuterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // Wrap the grid
        mSearchPane.add(statusOuterPanel);
        JPanel statusCheckboxGroup = new JPanel(new GridLayout(0, 1));
        statusCheckboxGroup.setBorder(filterBorder);

        cb_status = new JCheckBox[3];
        
        cb_status[0] = new JCheckBox(CardUtil.getTranslation("filter.status.normal"));
        cb_status[0].setFont(CRnormal);
        componentFontMap.put(cb_status[0], "CRnormal"); // Store the font type as a String
        statusCheckboxGroup.add(cb_status[0]);
        cb_status[1] = new JCheckBox(CardUtil.getTranslation("filter.status.restricted"));
        cb_status[1].setFont(CRnormal);
        componentFontMap.put(cb_status[1], "CRnormal"); // Store the font type as a String
        statusCheckboxGroup.add(cb_status[1]);
        cb_status[2] = new JCheckBox(CardUtil.getTranslation("filter.status.banned"));
        cb_status[2].setFont(CRnormal);
        componentFontMap.put(cb_status[2], "CRnormal"); // Store the font type as a String
        statusCheckboxGroup.add(cb_status[2]);

        statusOuterPanel.add(statusCheckboxGroup);
        statusLabelPanel.setVisible(Config.ADVANCED_FILTERING);
        statusOuterPanel.setVisible(Config.ADVANCED_FILTERING);
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
        cb_variant_sec.setSelected(false);
        cb_variant_promo.setSelected(false);
    	cb_type_stage.setSelected(false);

    	for (JCheckBox cb : cb_level) {
    		cb.setSelected(false);
    	}

        for (JCheckBox cb : cb_HP) {
        	cb.setSelected(false);
        }

        for (JCheckBox cb : cb_HPAwaken) {
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
        for (JCheckBox cb : cb_HPAwaken) {
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
        for (JCheckBox cb : cb_attackDMG) {
            cb.setSelected(false);
        }
        for (JCheckBox cb : cb_attackCost) {
            cb.setSelected(false);
        }
        for (JCheckBox cb : cb_avgDMG) {
            cb.setSelected(false);
        }
        for (JCheckBox cb : cb_peakDMG) {
            cb.setSelected(false);
        }
        for (JCheckBox cb : cb_status) {
            cb.setSelected(false);
        }
        searchBox.setText("");
    }
    
    private void updateCardList() {
        System.out.println("========== start getSelectCards =============");
        filter.update();
        CardList list = CardList.getInstance();
        filter.apply(list);
    	
        mCardsPane.removeAll();
        List<Card> currentList = list.getSelectCards(false);
        UIUtil.showDeck(this, mCardsPane, currentList, null, 13, columns, UIUtil.CARD_SIZE_SMALL, 1.0f, (isCollectionMode ? 3 : (Config.DECK_BUILD_FROM_COLLECTION ? 4 : 0)), false);
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
        UIUtil.showDeck(this, mDeckPane, mDeck.getAllCards(), null, 18, columns, UIUtil.CARD_SIZE_SMALL, 1.0f, (Config.DECK_BUILD_FROM_COLLECTION ? 4 : 1), false);

        mDeckPane.revalidate();
        mDeckPane.repaint();
        if (Config.DECK_BUILD_FROM_COLLECTION) {
            mCardsPane.revalidate();
            mCardsPane.repaint();
        }
        mCardCountTxt.setText(mDeck.getCardCount()-mDeck.getExtraSummary()[0]+"/60");
        if ((mDeck.getCardCount()-mDeck.getExtraSummary()[0] > 60) || (!mDeck.getLegality()) || (Config.DECK_BUILD_FROM_COLLECTION && !mDeck.getOwnershipLegality().isEmpty())) {
        	mCardCountTxt.setForeground(Color.RED);
            String invalidReasonString = ((mDeck.getCardCount()-mDeck.getExtraSummary()[0] > 60) ? CardUtil.getTranslation("warning.overlimit") : "");
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

        mExtraCountTxt.setText(mDeck.getExtraSummary()[0]+"/6");
        if (mDeck.getExtraSummary()[0] > 6) {
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

        updateDeckDistribution();

        CardColor dominantColor = mDeck.getDominantDeckColor();
        mDeckPaneLabel.setBackground(dominantColor.getAccentColor());
        mDeckPaneLabel.setForeground(dominantColor.getForegroundColor());
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
            cardIcon = new ImageIcon(AppPaths.dataDir().resolve("cards/"+lang+"/"+card.getPack()+"/"+card.getId()+".png").toString());
            if (cardIcon.getIconWidth() > 0) {
                break;
            }
        }
        
        //System.out.println("cards/"+Config.CARD_LANGUAGE+"/"+card.getPack()+"/"+card.getId()+".png");
        //System.out.println(card.getHP());
        if (Config.ADVANCED_FILTERING) {
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
        } else {
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
        }

        if (card.getAttackDMG() > 0) {
            cardAttackAttr.setText("<html><img src=\"file:" + new File(AppPaths.dataDir().resolve("icons/24px/ATK.png").toString()).getAbsolutePath() + "\">&nbsp;" + card.getAttackDMG() +
            "&nbsp;<img src=\"file:" + new File(AppPaths.dataDir().resolve("icons/24px/avgDMG.png").toString()).getAbsolutePath() + "\">&nbsp;" + card.getAvgDMG() +
            "&nbsp;<img src=\"file:" + new File(AppPaths.dataDir().resolve("icons/24px/peakDMG.png").toString()).getAbsolutePath() + "\">&nbsp;" + card.getPeakDMG() + "</html>");
        } else {
            cardAttackAttr.setText("");
        }

        cardName.setText("<html>" + card.getNameByLang().get(Config.getLangIndex(Config.LANGUAGE)) + " " + "<img src=\"file:" + new File(AppPaths.dataDir().resolve("icons_rarity/16px/" + card.getRarity().getName() + ".png").toString()).getAbsolutePath() + "\">" + "</html>");
        if (card.getCardTranslation() != null && Config.CARD_TRANSLATION_ENABLED) {
            cardTranslationSkill.setText("<html>" + card.getCardTranslation()[1] + "</html>");
            if (card.getCardTranslation()[0].isEmpty()) {
                cardTranslationSkillIcon.setIcon(null);
                cardTranslationSkillFlavorText.setText("");
            } else {
                cardTranslationSkillIcon.setIcon(new ImageIcon(AppPaths.dataDir().resolve("icons/" + Config.LANGUAGE + "/" + (Config.LARGE_TRANSLATION_TEXT ? "24px/" : "16px/") + "SKILL.png").toString()));
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
                cardTranslationAttackIcon.setIcon(new ImageIcon(AppPaths.dataDir().resolve("icons/" + (Config.LARGE_TRANSLATION_TEXT ? "24px/" : "16px/") + "ATK.png").toString()));
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

        for(int i=0; i<CardUtil.CardPack.size(); i++) {
        	if (!CardUtil.CardPackAvailability.get(CardUtil.CardPack.get(i)).get(Config.REGION)) {
                cb_pack[i].setEnabled(false);
                cb_pack[i].setSelected(false);
            }
        }

        // Update all components with the new translations
        frame.setTitle(CardUtil.getTranslation("app.title") + " v." + Constant.VERSION);
        searchBox.setText("");
        labelSearch.setText(CardUtil.getTranslation("search.name"));
        button_search.setText(CardUtil.getTranslation("search"));
        button_clean.setText(CardUtil.getTranslation("clear"));
        controlsHint.setText("<html><img src=\"file:" + new File(AppPaths.dataDir().resolve("keyicons/24px/space.png").toString()).getAbsolutePath() + "\"> + <img src=\"file:" + new File(AppPaths.dataDir().resolve("keyicons/24px/mousewheel.png").toString()).getAbsolutePath() + "\">:&nbsp;" + CardUtil.getTranslation("hint.quickedit") + "</html>");
        showDeckDifferentialBtn.setText(CardUtil.getTranslation("deck.compare"));
        mDeckPaneLabel.setText(CardUtil.getTranslation("deck"));
        mCardsPaneLabel.setText(CardUtil.getTranslation("cardlist"));
        mClearDeckBtn.setText(CardUtil.getTranslation("deck.clear"));
        mRandomDrawSimBtn.setText(CardUtil.getTranslation("deck.drawsim"));
        mCardCountHintTxt.setText(CardUtil.getTranslation("deck.cards"));
        mFlipCountHintTxt.setText(CardUtil.getTranslation("deck.flip"));
        mExtraCountHintTxt.setText(CardUtil.getTranslation("deck.extra"));
        mDeckCookieSummaryHintTxt.setText(CardUtil.getTranslation("deck.cookies"));
        mDeckItemHintTxt.setText(CardUtil.getTranslation("deck.items"));
        mDeckTrapHintTxt.setText(CardUtil.getTranslation("deck.traps"));
        mDeckStageHintTxt.setText(CardUtil.getTranslation("deck.stages"));
        //loadBtn.setText(CardUtil.getTranslation("load"));
        saveBtn.setText(CardUtil.getTranslation("save"));
        saveAsBtn.setText(CardUtil.getTranslation("saveas"));
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
        cb_color[5].setText(CardUtil.CardColor.Pure.getDisplayName());
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
        cb_variant_sec.setText(CardUtil.getTranslation("rarity.variant.secret"));
        cb_variant_promo.setText(CardUtil.getTranslation("rarity.variant.promo"));
        labelAttackAttr.setText(CardUtil.getTranslation("filter.attackattr"));
        labelAttackDMG.setText(CardUtil.getTranslation("filter.attackdmg"));
        labelAttackCost.setText(CardUtil.getTranslation("filter.attackcost"));
        labelAvgDMG.setText(CardUtil.getTranslation("filter.avgdmg"));
        labelAvgDMG.setToolTipText(CardUtil.getTranslation("filter.avgdmg.tooltip"));
        labelPeakDMG.setText(CardUtil.getTranslation("filter.peakdmg"));
        labelPeakDMG.setToolTipText(CardUtil.getTranslation("filter.peakdmg.tooltip"));
        labelStatus.setText(CardUtil.getTranslation("filter.status"));
        cb_status[0].setText(CardUtil.getTranslation("filter.status.normal"));
        cb_status[1].setText(CardUtil.getTranslation("filter.status.restricted"));
        cb_status[2].setText(CardUtil.getTranslation("filter.status.banned"));

        // Set visibility depending on advanced filtering option
        cb_variant_sec.setVisible(Config.ADVANCED_FILTERING);
        cb_variant_promo.setVisible(Config.ADVANCED_FILTERING);
        keywordLabelPanel.setVisible(Config.ADVANCED_FILTERING);
        keywordOuterPanel.setVisible(Config.ADVANCED_FILTERING);
        skillTypeLabelPanel.setVisible(Config.ADVANCED_FILTERING);
        skillTypeOuterPanel.setVisible(Config.ADVANCED_FILTERING);
        attackAttrLabelPanel.setVisible(Config.ADVANCED_FILTERING);
        attackAttrOuterPanel.setVisible(Config.ADVANCED_FILTERING);

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
        cardAttackAttr.setText(null);

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
        CardLoader.reloadVariants(CardList.getInstance().getAllCards());

        sidebarPanel.setPreferredSize(new Dimension(Config.CARD_PREVIEW_WIDTH, (int) frame.getBounds().getHeight()));
        mCardDetailPane.setPreferredSize(new Dimension(Config.CARD_PREVIEW_WIDTH, (int) frame.getBounds().getHeight()-60));
        //System.out.println(Config.CARD_PREVIEW_WIDTH + "x" + (frame.getBounds().getHeight()-60));
        //System.out.println(mCardDetailPane.getWidth() + "x" + mCardDetailPane.getHeight());

        updateUIForCollectionMode();
        updateCardList();
        updateDeck();
        updateDeckDistribution();
        
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

    private void updateDeckDistribution() {

        mDeckDistributionPane.removeAll();

        int[] cookieSummary = mDeck.getCookieSummary(false, false);
        int flipCount = mDeck.getFlipCount();
        int[] flipTypeSummary = mDeck.getFlipTypeSummary();
        int[] otherSummary = mDeck.getOtherSummary();
        int[] extraSummary = mDeck.getExtraSummary();
        int cardCount = mDeck.getCardCount() - extraSummary[0]; // Excludes EXTRA cards
        //int extraCount = mDeck.getExtraCount();
        
        GridBagConstraints gbc_deckdist = new GridBagConstraints();
        //gbc_deckdist.insets = new Insets(5, 5, 5, 5);
        gbc_deckdist.fill = GridBagConstraints.BOTH;
        gbc_deckdist.gridy = 0;

        gbc_deckdist.gridx = 0;
        gbc_deckdist.weightx = cookieSummary[1];
        if (cookieSummary[1] > 0) {
            mDeckDistCookie1 = new JLabel(" " + String.valueOf(cookieSummary[1]));
            mDeckDistCookie1.setFont(CRboldSmall);
            mDeckDistCookie1.setOpaque(true);
            mDeckDistCookie1.setToolTipText(CardUtil.getTranslation("deck.distribution.lv1") + " " + cookieSummary[1]);
            mDeckDistCookie1.setBackground(new Color(135, 223, 255));
            componentFontMap.put(mDeckDistCookie1, "CRboldSmall"); // Store the font type as a String
            mDeckDistributionPane.add(mDeckDistCookie1, gbc_deckdist);
        }

        gbc_deckdist.gridx = cookieSummary[1];
        gbc_deckdist.weightx = cookieSummary[2];
        if (cookieSummary[2] > 0) {
            mDeckDistCookie2 = new JLabel(" " + String.valueOf(cookieSummary[2]));
            mDeckDistCookie2.setFont(CRboldSmall);
            mDeckDistCookie2.setOpaque(true);
            mDeckDistCookie2.setToolTipText(CardUtil.getTranslation("deck.distribution.lv2") + " " + cookieSummary[2]);
            mDeckDistCookie2.setBackground(new Color(135, 193, 255));
            componentFontMap.put(mDeckDistCookie2, "CRboldSmall"); // Store the font type as a String
            mDeckDistributionPane.add(mDeckDistCookie2, gbc_deckdist);
        }

        gbc_deckdist.gridx = cookieSummary[1] + cookieSummary[2];
        gbc_deckdist.weightx = cookieSummary[3];
        if (cookieSummary[3] > 0) {
            mDeckDistCookie3 = new JLabel(" " + String.valueOf(cookieSummary[3]));
            mDeckDistCookie3.setFont(CRboldSmall);
            mDeckDistCookie3.setOpaque(true);
            mDeckDistCookie3.setToolTipText(CardUtil.getTranslation("deck.distribution.lv3") + " " + cookieSummary[3]);
            mDeckDistCookie3.setBackground(new Color(135, 163, 255));
            componentFontMap.put(mDeckDistCookie3, "CRboldSmall"); // Store the font type as a String
            mDeckDistributionPane.add(mDeckDistCookie3, gbc_deckdist);
        }

        gbc_deckdist.gridx = cookieSummary[0];
        gbc_deckdist.weightx = flipTypeSummary[0];
        if (flipTypeSummary[0] > 0) {
            mDeckDistFlipHeal = new JLabel(" " + String.valueOf(flipTypeSummary[0]));
            mDeckDistFlipHeal.setFont(CRboldSmall);
            mDeckDistFlipHeal.setOpaque(true);
            mDeckDistFlipHeal.setToolTipText(CardUtil.getTranslation("deck.distribution.flipheal") + " " + flipTypeSummary[0]);
            mDeckDistFlipHeal.setBackground(new Color(255, 235, 84));
            componentFontMap.put(mDeckDistFlipHeal, "CRboldSmall"); // Store the font type as a String
            mDeckDistributionPane.add(mDeckDistFlipHeal, gbc_deckdist);
        } 

        gbc_deckdist.gridx = cookieSummary[0] + flipTypeSummary[0];
        gbc_deckdist.weightx = flipTypeSummary[1];
        if(flipTypeSummary[1] > 0) {
            mDeckDistFlipDraw = new JLabel(" " + String.valueOf(flipTypeSummary[1]));
            mDeckDistFlipDraw.setFont(CRboldSmall);
            mDeckDistFlipDraw.setOpaque(true);
            mDeckDistFlipDraw.setToolTipText(CardUtil.getTranslation("deck.distribution.flipdraw") + " " + flipTypeSummary[1]);
            mDeckDistFlipDraw.setBackground(new Color(255, 205, 84));
            componentFontMap.put(mDeckDistFlipDraw, "CRboldSmall"); // Store the font type as a String
            mDeckDistributionPane.add(mDeckDistFlipDraw, gbc_deckdist);
        }

        gbc_deckdist.gridx = cookieSummary[0] + flipTypeSummary[0] + flipTypeSummary[1];
        gbc_deckdist.weightx = flipTypeSummary[2];
        if (flipTypeSummary[2] > 0) {
            mDeckDistFlipSpecial = new JLabel(" " + String.valueOf(flipTypeSummary[2]));
            mDeckDistFlipSpecial.setFont(CRboldSmall);
            mDeckDistFlipSpecial.setOpaque(true);
            mDeckDistFlipSpecial.setToolTipText(CardUtil.getTranslation("deck.distribution.flipspecial") + " " + flipTypeSummary[2]);
            mDeckDistFlipSpecial.setBackground(new Color(255, 175, 84));
            componentFontMap.put(mDeckDistFlipSpecial, "CRboldSmall"); // Store the font type as a String
            mDeckDistributionPane.add(mDeckDistFlipSpecial, gbc_deckdist);
        }

        gbc_deckdist.gridx = cookieSummary[0] + flipTypeSummary[0] + flipTypeSummary[1] + flipTypeSummary[2];
        gbc_deckdist.weightx = otherSummary[0];
        if (otherSummary[0] > 0) {
            mDeckDistItem = new JLabel(" " + String.valueOf(otherSummary[0]));
            mDeckDistItem.setFont(CRboldSmall);
            mDeckDistItem.setOpaque(true);
            mDeckDistItem.setToolTipText(CardUtil.getTranslation("deck.distribution.item") + " " + otherSummary[0]);
            mDeckDistItem.setBackground(new Color(64, 247, 183));
            componentFontMap.put(mDeckDistItem, "CRboldSmall"); // Store the font type as a String
            mDeckDistributionPane.add(mDeckDistItem, gbc_deckdist);
        }

        gbc_deckdist.gridx = cookieSummary[0] + flipTypeSummary[0] + flipTypeSummary[1] + flipTypeSummary[2] + otherSummary[0];
        gbc_deckdist.weightx = otherSummary[1];
        if (otherSummary[1] > 0) {
            mDeckDistTrap = new JLabel(" " + String.valueOf(otherSummary[1]));
            mDeckDistTrap.setFont(CRboldSmall);
            mDeckDistTrap.setOpaque(true);
            mDeckDistTrap.setToolTipText(CardUtil.getTranslation("deck.distribution.trap") + " " + otherSummary[1]);
            mDeckDistTrap.setBackground(new Color(64, 217, 183));
            componentFontMap.put(mDeckDistTrap, "CRboldSmall"); // Store the font type as a String
            mDeckDistributionPane.add(mDeckDistTrap, gbc_deckdist);
        }

        gbc_deckdist.gridx = cookieSummary[0] + flipTypeSummary[0] + flipTypeSummary[1] + flipTypeSummary[2] + otherSummary[0] + otherSummary[1];
        gbc_deckdist.weightx = otherSummary[2];
        if (otherSummary[2] > 0) {
            mDeckDistStage = new JLabel(" " + String.valueOf(otherSummary[2]));
            mDeckDistStage.setFont(CRboldSmall);
            mDeckDistStage.setOpaque(true);
            mDeckDistStage.setToolTipText(CardUtil.getTranslation("deck.distribution.stage") + " " + otherSummary[2]);
            mDeckDistStage.setBackground(new Color(64, 187, 183));
            componentFontMap.put(mDeckDistStage, "CRboldSmall"); // Store the font type as a String
            mDeckDistributionPane.add(mDeckDistStage, gbc_deckdist);
        }
        
        gbc_deckdist.gridx = cardCount;
        int emptyCount = 60 - cardCount;
        gbc_deckdist.weightx = emptyCount;
        if (emptyCount > 0) {
            mDeckDistEmpty = new JLabel(" " + String.valueOf(emptyCount));
            mDeckDistEmpty.setOpaque(true);
            mDeckDistEmpty.setForeground(new Color(220, 220, 220));
            mDeckDistEmpty.setBackground(new Color(220, 220, 220));
            mDeckDistributionPane.add(mDeckDistEmpty, gbc_deckdist);
        }

        gbc_deckdist.gridx = 60;
        gbc_deckdist.weightx = extraSummary[1];
        if (extraSummary[1] > 0) {
            mDeckDistExtra1 = new JLabel(" " + String.valueOf(extraSummary[1]));
            mDeckDistExtra1.setFont(CRboldSmall);
            mDeckDistExtra1.setOpaque(true);
            mDeckDistExtra1.setToolTipText(CardUtil.getTranslation("deck.distribution.exlv1") + " " + extraSummary[1]);
            mDeckDistExtra1.setBackground(new Color(255, 103, 178));
            componentFontMap.put(mDeckDistExtra1, "CRboldSmall"); // Store the font type as a String
            mDeckDistributionPane.add(mDeckDistExtra1, gbc_deckdist);
        }

        gbc_deckdist.gridx = 60 + extraSummary[1];
        gbc_deckdist.weightx = extraSummary[2];
        if (extraSummary[2] > 0) {
            mDeckDistExtra2 = new JLabel(" " + String.valueOf(extraSummary[2]));
            mDeckDistExtra2.setFont(CRboldSmall);
            mDeckDistExtra2.setOpaque(true);
            mDeckDistExtra2.setToolTipText(CardUtil.getTranslation("deck.distribution.exlv2") + " " + extraSummary[2]);
            mDeckDistExtra2.setBackground(new Color(195, 83, 198));
            componentFontMap.put(mDeckDistExtra2, "CRboldSmall"); // Store the font type as a String
            mDeckDistributionPane.add(mDeckDistExtra2, gbc_deckdist);
        }

        gbc_deckdist.gridx = 60 + extraSummary[1] + extraSummary[2];
        gbc_deckdist.weightx = extraSummary[3];
        if (extraSummary[3] > 0) {
            mDeckDistExtra3 = new JLabel(" " + String.valueOf(extraSummary[3]));
            mDeckDistExtra3.setFont(CRboldSmall);
            mDeckDistExtra3.setOpaque(true);
            mDeckDistExtra3.setToolTipText(CardUtil.getTranslation("deck.distribution.exlv3") + " " + extraSummary[3]);
            mDeckDistExtra3.setBackground(new Color(155, 63, 218));
            componentFontMap.put(mDeckDistExtra3, "CRboldSmall"); // Store the font type as a String
            mDeckDistributionPane.add(mDeckDistExtra3, gbc_deckdist);
        }

        mDeckDistributionPane.revalidate();
        mDeckDistributionPane.repaint();
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
            mRandomDrawSimBtn.setVisible(false);
            cardAttackAttr.setText("");
            cardAttackAttr.setVisible(false);
            mTextsPane.setVisible(false);
            mDeckDistributionPane.setVisible(false);
            mDeckPaneLabel.setVisible(false);
            splitPane.setTopComponent(null);
            mCardsPaneLabel.setText(CardUtil.getTranslation("collection"));
            mClearDeckBtn.setText(CardUtil.getTranslation("collection.summary"));
            controlsHint.setText("<html><img src=\"file:" + new File(AppPaths.dataDir().resolve("keyicons/24px/space.png").toString()).getAbsolutePath() + "\"> + <img src=\"file:" + new File(AppPaths.dataDir().resolve("keyicons/24px/mousewheel.png").toString()).getAbsolutePath() + "\">:&nbsp;" + CardUtil.getTranslation("hint.quickedit") + "&nbsp;<img src=\"file:" + new File(AppPaths.dataDir().resolve("keyicons/24px/backtick.png").toString()).getAbsolutePath() + "\">:&nbsp;" + CardUtil.getTranslation("hint.langswitch")+ "</html>");
            splitPane.setResizeWeight(0.0);
            splitPane.setDividerSize(0);
            splitPane.setEnabled(false);
            updateLangLabels();
            for (JLabel[] labels : ownedInfoCountRows) {
                for (JLabel label : labels) {
                    label.setText("");
                    label.setVisible(true);
                }
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
            mRandomDrawSimBtn.setVisible(true);
            cardAttackAttr.setVisible(true);
            mTextsPane.setVisible(true);
            mDeckDistributionPane.setVisible(true);
            mDeckPaneLabel.setVisible(true);
            mCardsPaneLabel.setText(CardUtil.getTranslation("cardlist"));
            mClearDeckBtn.setText(CardUtil.getTranslation("deck.clear"));
            controlsHint.setText("<html><img src=\"file:" + new File(AppPaths.dataDir().resolve("keyicons/24px/space.png").toString()).getAbsolutePath() + "\"> + <img src=\"file:" + new File(AppPaths.dataDir().resolve("keyicons/24px/mousewheel.png").toString()).getAbsolutePath() + "\">:&nbsp;" + CardUtil.getTranslation("hint.quickedit") + "</html>");
            splitPane.setDividerSize(8);
            splitPane.setEnabled(true);
            splitPane.setResizeWeight(0.5);
            splitPane.setDividerLocation(divLoc);

            collection.saveCollection();
            for (JLabel label : langLabels) {
                label.setText("");
                label.setVisible(false);
            }
            for (JLabel[] labels : ownedInfoCountRows) {
                for (JLabel label : labels) {
                    label.setText("");
                    label.setVisible(false);
                }
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
        Integer prevVariant = null;
        @Override
        public void addCard(Card card) {
            // Increment the collection count
            if (card.getVariants().length <= collectionAddVariant) {
                prevVariant = collectionAddVariant;
                collectionAddVariant = 0;
            }
            updateCardOwnedInfoLabel(card);
            int newCount = collection.getCardOwnedCount(Config.COLLECTION_LANGUAGE_INDICES[currentSelectedCardLanguage], card.getId(), collectionAddVariant) + 1;
            collection.setCardOwnedCount(Config.COLLECTION_LANGUAGE_INDICES[currentSelectedCardLanguage], card.getId(), collectionAddVariant, newCount);
            collection.setCardChangeCount(card.getId(), (collection.getCardTotalChangeCount(card.getId())+1));
            updateCardListForCollection(); // Refresh the card list to show the updated count
            updateCardOwnedInfoLabel(card);
            updateCardOwnedInfoHighlight(collectionAddVariant);
            if (prevVariant != null) {
                collectionAddVariant = prevVariant;
                prevVariant = null;
            }
        }

        @Override
        public void removeCard(Card card) {
            // Decrement the collection count
            if (card.getVariants().length <= collectionAddVariant) {
                prevVariant = collectionAddVariant;
                collectionAddVariant = 0;
            }
            updateCardOwnedInfoLabel(card);
            int newCount = collection.getCardOwnedCount(Config.COLLECTION_LANGUAGE_INDICES[currentSelectedCardLanguage], card.getId(), collectionAddVariant) - 1;
            collection.setCardOwnedCount(Config.COLLECTION_LANGUAGE_INDICES[currentSelectedCardLanguage], card.getId(), collectionAddVariant, newCount);
            if (newCount >= 0) {
                collection.setCardChangeCount(card.getId(), (collection.getCardTotalChangeCount(card.getId())-1));
            }
            updateCardListForCollection(); // Refresh the card list to show the updated count
            updateCardOwnedInfoLabel(card);
            updateCardOwnedInfoHighlight(collectionAddVariant);
            if (prevVariant != null) {
                collectionAddVariant = prevVariant;
                prevVariant = null;
            }
        }

        @Override
        public void showCard(Card card) {
            // Show card details (same as in collection mode)
            mCardDetailPane.removeAll();

            currentCard = card;

            for (String lang : Config.FALLBACK_ORDER) {
                if (collectionAddVariant == 0 || collectionAddVariant >= currentCard.getVariants().length) {
                    cardIcon = new ImageIcon(AppPaths.dataDir().resolve("cards/" + lang + "/" + card.getPack() + "/" + card.getId() + ".png").toString());
                } else {
                    cardIcon = new ImageIcon(AppPaths.dataDir().resolve("cards_variant/" + lang + "/" + card.getPack() + "/" + card.getId() + "@" + collectionAddVariant + ".png").toString());
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

            for (int i = 0; i < langLabels.length; i++) {
                langLabels[i].setText(Config.ALL_CARD_LANGUAGES[Config.COLLECTION_LANGUAGE_INDICES[i]].replace("zh_TW", "TC").toUpperCase());
                langLabels[i].setVisible(true);
            }

            if (collectionAddVariant != 0 && card.getAltNames().size() > 0) {
                List<String> altNamesForVariant = card.getAltNames().get(collectionAddVariant-1);;
                if (altNamesForVariant != null && altNamesForVariant.size() > Config.getLangIndex(Config.LANGUAGE)) {
                    cardName.setText(altNamesForVariant.get(Config.getLangIndex(Config.LANGUAGE)));
                    cardName.setForeground(highlightColor);
                } else {
                    cardName.setText(card.getName());
                    cardName.setForeground(Color.BLACK);
                }
            } else {
                cardName.setText(card.getName());
                cardName.setForeground(Color.BLACK);
            }
            clearTranslations();

            updateCardOwnedInfoLabel(card);
            if (card.getVariants().length <= collectionAddVariant) {
                updateCardOwnedInfoHighlight(0);
            } else {
                updateCardOwnedInfoHighlight(collectionAddVariant);
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
            cardLabel = new JLabel(cardIcon);
            mCardDetailPane.add(cardLabel, BorderLayout.CENTER);
            mCardDetailPane.revalidate();
            mCardDetailPane.repaint();
        }
    }

    private final class Filter {
        public boolean[] color = new boolean[CardUtil.COLOR_MAX];
        public boolean[] type = new boolean[CardUtil.TYPE_MAX];
        public boolean[] level = new boolean[CardUtil.LEVEL_MAX + 1];
        public boolean flip = false;
        public boolean[] flipType = new boolean[3];
        public boolean extra = false;
        public boolean[] rarity = new boolean[CardUtil.RARITY_MAX];
        public boolean[] variants = new boolean[2];
        public boolean[] hp = new boolean[CardUtil.HP_MAX + 1];
        public boolean[] hpAwaken = new boolean[CardUtil.AWAKEN_HP.size()];
        public boolean[] skillType = new boolean[CardUtil.SKILL_TYPE_MAX];
        public boolean[] keyword = new boolean[CardUtil.KEYWORD_MAX];
        public boolean[] attackDMG = new boolean[CardUtil.ATTACK_MAX + 1];
        public boolean[] attackCost = new boolean[CardUtil.ATTACK_COST_MAX + 1];
        public boolean[] avgDMG = new boolean[CardUtil.PEAK_MAX + 1];
        public boolean[] peakDMG = new boolean[CardUtil.PEAK_MAX + 1];
        public boolean[] status = new boolean[3];
        public List<String> series = new ArrayList<>();
        public String name = "";

        public void update() {
            for (int i=0; i< cb_color.length; i++) {
                this.color[i] = cb_color[i].isSelected();
            }
            this.type[CardType.Cookie.getValue()] = cb_type_cookie.isSelected();
            this.type[CardType.Item.getValue()] = cb_type_item.isSelected();
            this.type[CardType.Trap.getValue()] = cb_type_trap.isSelected();
            this.type[CardType.Stage.getValue()] = cb_type_stage.isSelected();
            for (int i=0; i< cb_level.length; i++) {
                this.level[i] = cb_level[i].isSelected();
            }
            this.flip = cb_flip.isSelected();
            for (int i=0; i< cb_flipType.length; i++) {
                this.flipType[i] = cb_flipType[i].isSelected();
            }
            this.extra = cb_extra.isSelected();
            for (int i=0; i< cb_rarity.length; i++) {
                this.rarity[i] = cb_rarity[i].isSelected();
            }
            this.variants[0] = cb_variant_sec.isSelected();
            this.variants[1] = cb_variant_promo.isSelected();
            for (int i=0; i< cb_HP.length; i++) {
                this.hp[i] = cb_HP[i].isSelected();
            }
            for (int i=0; i< cb_HPAwaken.length; i++) {
                this.hpAwaken[i] = cb_HPAwaken[i].isSelected();
            }
            for (int i=0; i< cb_skillType.length; i++) {
                this.skillType[i] = cb_skillType[i].isSelected();
            }
            for (int i=0; i< cb_keyword.length; i++) {
                this.keyword[i] = cb_keyword[i].isSelected();
            }
            for (int i=0; i< cb_attackDMG.length; i++) {
                this.attackDMG[i] = cb_attackDMG[i].isSelected();
            }
            for (int i=0; i< cb_attackCost.length; i++) {
                this.attackCost[i] = cb_attackCost[i].isSelected();
            }
            for (int i=0; i< cb_avgDMG.length; i++) {
                this.avgDMG[i] = cb_avgDMG[i].isSelected();
            }
            for (int i=0; i< cb_peakDMG.length; i++) {
                this.peakDMG[i] = cb_peakDMG[i].isSelected();
            }
            for (int i=0; i< cb_status.length; i++) {
                this.status[i] = cb_status[i].isSelected();
            }
            this.series.clear();
            for (int i=0; i< CardUtil.CardPack.size(); i++) {
                if (cb_pack[i].isSelected()) {
                    this.series.add(CardUtil.CardPack.get(i));
                }
            }
            this.name = searchBox.getText();
        }

        public void clear() {
            for (int i=0; i<this.color.length; i++) {
                this.color[i] = false;
            }
            for (int i=0; i<this.type.length; i++) {
                this.type[i] = false;
            }
            for (int i=0; i<this.level.length; i++) {
                this.level[i] = false;
            }
            this.flip = false;
            for (int i=0; i<this.flipType.length; i++) {
                this.flipType[i] = false;
            }
            this.extra = false;
            for (int i=0; i<this.rarity.length; i++) {
                this.rarity[i] = false;
            }
            this.variants[0] = false;
            this.variants[1] = false;
            for (int i=0; i<this.hp.length; i++) {
                this.hp[i] = false;
            }
            for (int i=0; i<this.hpAwaken.length; i++) {
                this.hpAwaken[i] = false;
            }
            for (int i=0; i<this.skillType.length; i++) {
                this.skillType[i] = false;
            }
            for (int i=0; i<this.keyword.length; i++) {
                this.keyword[i] = false;
            }
            for (int i=0; i<this.attackDMG.length; i++) {
                this.attackDMG[i] = false;
            }
            for (int i=0; i<this.attackCost.length; i++) {
                this.attackCost[i] = false;
            }
            for (int i=0; i<this.avgDMG.length; i++) {
                this.avgDMG[i] = false;
            }
            for (int i=0; i<this.peakDMG.length; i++) {
                this.peakDMG[i] = false;
            }
            for (int i=0; i<this.status.length; i++) {
                this.status[i] = false;
            }
            this.series.clear();
            this.name = "";
        }

        public void apply(CardList list) {
            for (int i=0; i< cb_color.length; i++) {
                list.setColor(i, this.color[i]);
            }
            list.setType(CardType.Cookie.getValue(), this.type[CardType.Cookie.getValue()]);
            list.setType(CardType.Item.getValue(), this.type[CardType.Item.getValue()]);
            list.setType(CardType.Trap.getValue(), this.type[CardType.Trap.getValue()]);
            list.setType(CardType.Stage.getValue(), this.type[CardType.Stage.getValue()]);
            for (int i=0; i< cb_level.length; i++) {
                list.setLv(i+1, this.level[i]);
            }
            for (int i=0; i< cb_HP.length; i++) {
                list.setHP(i+1, this.hp[i]);
            }
            for (int i=0; i< cb_HPAwaken.length; i++) {
                list.setHPAwaken(i, this.hpAwaken[i]);
            }
            for (int i=0; i< cb_skillType.length; i++) {
                list.setSkillType(i, this.skillType[i]);
            }
            for (int i=0; i< cb_keyword.length; i++) {
                list.setKeyword(i, this.keyword[i]);
            }
            for (int i=0; i< CardUtil.CardPack.size(); i++) {
                list.setPack(CardUtil.CardPack.get(i), this.series.contains(CardUtil.CardPack.get(i)));
            }
            list.setFlip(this.flip);
            for (int i=0; i< cb_flipType.length; i++) {
                list.setFlipType(i, this.flipType[i]);
            }
            list.setExtra(this.extra);
            for (int i=0; i< cb_rarity.length; i++) {
                list.setRarity(i, this.rarity[i]);
            }
            for (int i=0; i< cb_attackDMG.length; i++) {
                list.setAttackDMG(i, this.attackDMG[i]);
            }
            for (int i=0; i< cb_attackCost.length; i++) {
                list.setAttackCost(i, this.attackCost[i]);
            }
            for (int i=0; i< cb_avgDMG.length; i++) {
                list.setAvgDMG(i, this.avgDMG[i]);
            }
            for (int i=0; i< cb_peakDMG.length; i++) {
                list.setPeakDMG(i, this.peakDMG[i]);
            }
            for (int i=0; i< cb_status.length; i++) {
                list.setStatus(i, this.status[i]);
            }
            list.setHasSecretOnly(this.variants[0]);
            list.setHasPromoVariantOnly(this.variants[1]);
            list.setSearchTerm(this.name.trim().equals("Search by Card Name...") ? "" : this.name.trim());
            
        }
        
    }

    private void updateCardListForCollection() {
        mCardsPane.removeAll();
        CardList list = CardList.getInstance();
        List<Card> filteredCards = list.getSelectCards(true); // Ignore ownership for collection mode view

        UIUtil.showDeck(new CollectionModeCallback(), mCardsPane, filteredCards, null, 13, columns, UIUtil.CARD_SIZE_SMALL, 1.0f, 3, false);
        
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

        currentSelectedCardLanguage = prevLangIdx;
        if (!card.getAvailability(collectionAddVariant)[Config.COLLECTION_LANGUAGE_INDICES[currentSelectedCardLanguage]]) {
            // Auto-switch to next available language
            for (int i = 0; i < Config.COLLECTION_LANGUAGE_INDICES.length; i++) {
                currentSelectedCardLanguage = (currentSelectedCardLanguage + 1) % Config.COLLECTION_LANGUAGE_INDICES.length;
                if (card.getAvailability(collectionAddVariant)[Config.COLLECTION_LANGUAGE_INDICES[currentSelectedCardLanguage]]) {
                    updateLangLabels();
                    System.out.println("Autoswitch: Switched selected language to " + Config.ALL_CARD_LANGUAGES[Config.COLLECTION_LANGUAGE_INDICES[currentSelectedCardLanguage]]);
                    break;
                }
            }
        } else {
            updateLangLabels();
        }

        System.out.println("Selected language: " + Config.ALL_CARD_LANGUAGES[Config.COLLECTION_LANGUAGE_INDICES[currentSelectedCardLanguage]]);
        System.out.println("Prev language index: " + prevLangIdx);

        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < Config.COLLECTION_LANGUAGE_INDICES.length; j++) {
                int langIdx = Config.COLLECTION_LANGUAGE_INDICES[j];
                if (i < rarities.length) {
                    StringBuilder ownedInfo = new StringBuilder();
                    int ownedCount = collection.getCardOwnedCount(langIdx, card.getId(), i);
                    //System.out.println(rarities[i].getName());
                    //System.out.println(ownedCount);
                    ownedInfo.append("<html>");
                    ownedInfo.append("<img src=\"file:" + new File(AppPaths.dataDir().resolve("keyicons/24px/" + (i + 1) + ".png").toString()).getAbsolutePath() + "\">").append("&nbsp;");
                    ownedInfo.append("<img src=\"file:" + new File(AppPaths.dataDir().resolve("icons_rarity/24px/" + rarities[i].getName() + ".png").toString()).getAbsolutePath() + "\">");
                    ownedInfo.append("&nbsp;").append(CardUtil.getTranslationPromo(variantNames[i]));
                    if (i < rarities.length - 1) {
                        ownedInfo.append("<br>");
                    }
                    ownedInfo.append("</html>");
                    ownedInfoRarityRows[i].setText(ownedInfo.toString());
                    ownedInfoCountRows[i][j].setText(String.valueOf(ownedCount));
                    if (card.getAvailability(i)[langIdx]) {
                        ownedInfoCountRows[i][j].setForeground(Color.BLACK);
                    } else {
                        ownedInfoCountRows[i][j].setForeground(Color.GRAY);
                    }
                } else {
                    ownedInfoRarityRows[i].setText("");
                    ownedInfoCountRows[i][j].setText("");
                }
            }
        }

        sidebarPanel.revalidate();
        sidebarPanel.repaint();
    }

    private void updateCardOwnedInfoHighlight(int variantIndex) {
        for (int i = 0; i < ownedInfoCountRows.length; i++) {
            for (int j = 0; j < ownedInfoCountRows[i].length; j++) {
                if (i == variantIndex && j == currentSelectedCardLanguage) {
                    ownedInfoCountRows[i][currentSelectedCardLanguage].setForeground(highlightColor);
                } else {
                    if (currentCard.getAvailability(i)[Config.COLLECTION_LANGUAGE_INDICES[j]]) {
                        ownedInfoCountRows[i][j].setForeground(Color.BLACK);
                    } else {
                        ownedInfoCountRows[i][j].setForeground(Color.GRAY);
                    }
                }
            }
        }

        sidebarPanel.revalidate();
        sidebarPanel.repaint();
    }

    private void updateLangLabels() {
        for (int i = 0; i < langLabels.length; i++) {
            langLabels[i].setVisible(true);
            if (i == currentSelectedCardLanguage) {
                langLabels[i].setOpaque(true);
                langLabels[i].setBackground(highlightColor);
                langLabels[i].setForeground(Color.WHITE);
            } else {
                langLabels[i].setOpaque(false);
                langLabels[i].setBackground(new Color(0,0,0,0));
                langLabels[i].setForeground(Color.BLACK);
            }
        }
    }

    private void updateCardPreview() {
        if (isCollectionMode) {
            for (String lang : Config.FALLBACK_ORDER) {
                if (collectionAddVariant == 0 || collectionAddVariant >= currentCard.getVariants().length) {

                    cardIcon = new ImageIcon(AppPaths.dataDir().resolve("cards/" + lang + "/" + currentCard.getPack() + "/" + currentCard.getId() + ".png").toString());
                    
                } else {
                    cardIcon = new ImageIcon(AppPaths.dataDir().resolve("cards_variant/" + lang + "/" + currentCard.getPack() + "/" + currentCard.getId() + "@" + collectionAddVariant + ".png").toString());
                }
                if (cardIcon.getIconWidth() > 0) {
                    break;
                }
            }
            cardLabel.setIcon(new ImageIcon(cardIcon.getImage().getScaledInstance((int) (previewHeight / Config.CARD_RATIO), previewHeight, java.awt.Image.SCALE_SMOOTH)));
            if (currentCard.getAltNames().size() > 0) {
                if (collectionAddVariant == 0) {
                    cardName.setText(currentCard.getName());
                    cardName.setForeground(Color.BLACK);
                } else {
                    List<String> altNamesForVariant = currentCard.getAltNames().get(collectionAddVariant-1);
                    if (altNamesForVariant != null && altNamesForVariant.size() > Config.getLangIndex(Config.LANGUAGE)) {
                        cardName.setText(altNamesForVariant.get(Config.getLangIndex(Config.LANGUAGE)));
                        cardName.setForeground(highlightColor);
                    } else {
                        cardName.setText(currentCard.getName());
                        cardName.setForeground(Color.BLACK);
                    }
                }
            } else {
                cardName.setText(currentCard.getName());
                cardName.setForeground(Color.BLACK);
            }
            mCardDetailPane.revalidate();
            mCardDetailPane.repaint();
        }
    }
}