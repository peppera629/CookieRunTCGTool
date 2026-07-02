package ui;
import util.Config;
import java.awt.EventQueue;
import java.awt.BorderLayout;
import java.nio.file.Paths;
import java.awt.Frame;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JButton;
import javax.swing.JScrollBar;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.Border;

import dataStructure.Card;
import dataStructure.CardList;
import dataStructure.CardLoader;
import dataStructure.Deck;
import dataStructure.Collection;

import java.util.List;

import ui.ClickableCardPanel.CardListCallBack;
import ui.SortSettingsWindow.ConfigChangedCallback;
import util.CardUtil.CardColor;
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

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatDarkLaf;

// FEATURE: Add "Credits" popup
// FEATURE: Add undo/redo/undo all
// FEATURE: For collection summary, add totals per rarity and overall

public class MainUI implements CardListCallBack, ConfigChangedCallback, LanguageChangeListener {

    private JFrame frame;
    public static boolean DEBUG = false;
    // Secret features:
    // 1. Highlight translation-available cards
    // 2. Tournament mode in normal mode (after searching, if there's only 1 card after filtering, show that card immediately)
    public static boolean[] secretFeatures = {false, false};
    public static Color foregroundColor;
    public static Color highlightColor = new Color(60, 60, 255,255);
    public static Color extraColor = new Color(110, 36, 133, 255);

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        
        System.setProperty("sun.java2d.uiScale", "1.0");
        System.setProperty("sun.java2d.dpiaware", "true");
        System.setProperty("file.encoding", "UTF-8");
        Config.loadConfig();
        if (Config.THEME.equals("dark")) {
            FlatDarkLaf.setup();
            try {
                UIManager.setLookAndFeel(new FlatDarkLaf());
                foregroundColor = UIManager.getColor("Label.foreground");
                extraColor = new Color(191, 134, 209, 255);
                highlightColor = new Color(150, 150, 255,255);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            FlatLightLaf.setup();
            try {
                UIManager.setLookAndFeel(new FlatLightLaf());
                foregroundColor = UIManager.getColor("Label.foreground");
                extraColor = new Color(110, 36, 133, 255);
                highlightColor = new Color(60, 60, 255,255);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
    private DefaultState defaultState;
    private JPanel deckDetailSummaryPanel, deckDetailButtonsPanel;
    private ScrollablePanel cardList, deckList, filterPanel;

    private boolean globalKeyBindings = true;
    private KeyEventDispatcher keyEventDispatcher;
    private boolean quickEdit = false;
    private int[] collectionChangeValue = new int[2]; // positive, negative
    
    // Filter Panel (search box, filter checkboxes)
    private JPanel filterPanelOuter, cardPreviewPanel;
    private JToggleButton[] cb_color;
    private JCheckBox[] cb_flipType, cb_level, cb_pack, cb_rarity, cb_HP, cb_HPAwaken, cb_skillType, cb_keyword, cb_attackDMG, cb_attackCost, cb_avgDMG, cb_peakDMG, cb_status;
    private JCheckBox cb_type_cookie, cb_type_item, cb_type_trap, cb_type_stage, cb_extra, cb_specialplay, cb_variant_sec, cb_variant_promo;
    private JRadioButton[] rb_flip_condition;
    private final Filter filter = new Filter(); 
    private JLabel controlsHint, labelColor, labelType, labelSeries, labelRarity, labelLV, labelHP, labelHPAwaken, labelFlip, labelSkillType, labelKeyword, labelAttackAttr, labelAttackDMG, labelAttackCost, labelAvgDMG, labelPeakDMG, labelStatus;

    // Deck and Card List Panels
    private Deck currentDeck;
    private JPanel deckPanel, cardListPanel, keywordLabelPanel, keywordOuterPanel, skillTypeLabelPanel, skillTypeOuterPanel, attackAttrLabelPanel, attackAttrOuterPanel, attackAttrBasePanel, statusLabelPanel, statusOuterPanel;
    private JPanel fileOperationsPanel, fileOperationsButtonsPanel, deckDetailPanel, centerPanel, skillTypeBtnPanel;
    private JLabel currentDeckName, collectionChangeLabel;
    private JTextField searchBox;
    private JButton saveBtn, saveAsBtn, selectBtn, importBtn, hideSearchPaneBtn, hidePreviewPaneBtn, quickSelectBtnBS, quickSelectBtnST, quickSelectBtnSkillType;
    private JButton clearDeckBtn, randomDrawSimBtn, searchBtn, clearFilterBtn, sortSettingsBtn, settingsBtn;
    private JToggleButton button_collection;
    private JLabel cardCountHintTxt, flipCountHintTxt, extraCountHintTxt, deckCookieSummaryHintTxt, levelCountTxt, flipTypeCountTxt, filterResults, labelSearch;
    private JLabel deckItemHintTxt, deckTrapHintTxt, deckStageHintTxt, deckPanelLabel, cardListPanelLabel;
    private JLabel cardCountTxt, flipCountTxt, extraCountTxt, deckCookieSummaryTxt;
    private JLabel deckItemTxt, deckTrapTxt, deckStageTxt;
    private JSplitPane splitPane;
    private JButton showDeckBtn, showDeckDifferentialBtn;
    private static ImageIcon cardIcon;
    private JScrollPane scrollDeckPanel, scrollCardListPanel, scrollFilterPanel;

    public static Font CRnormal, CRbold, CRnormalLarge, CRnormalSmall, CRnormalEXLarge, CRboldLarge, CRboldSmall, CRboldEXLarge, CRtranslation, CRtranslationBold, CRboldEXLargeFilter;
    public static InputStream fontStream, fontStreamBold;
    public static Map<java.awt.Component, String> componentFontMap = new HashMap<>();
    private int columns = 6, divLoc = 400;
    public static int currentSelectedCardLanguage = 0;
    public static int prevLangIdx = currentSelectedCardLanguage;
    private JPanel deckDistributionPanel;
    private JLabel deckDistCookie1, deckDistCookie2, deckDistCookie3, deckDistCookie4, deckDistCookie5, deckDistFlipHeal, deckDistFlipDraw, deckDistFlipSpecial, deckDistItem, deckDistTrap, deckDistStage, deckDistEmpty, deckDistExtra1, deckDistExtra2, deckDistExtra3, deckDistExtra4, deckDistExtra5;
    private JLabel deckDistCookieBorder, deckDistFlipBorder, deckDistOtherBorder, deckDistExtraBorder;
    private static int collectionAddVariant = 0;
    public static boolean isCollectionMode = false, deckChanged = false;
    private Collection collection = Collection.getInstance();
    private Card currentCard;
    private String currentDeckDirectory;

    private CardDetailView cardDetailView;
    private CardDetailWindow cardDetailWindow;
    private boolean previewPopout = false;
    private JPanel cardPreviewHost;

    private void initialize() {
        Config.loadConfig();
    	initialData();
    	initialUI();
        enableKeyOverrides(); // Overrides space key behavior (toggle checkbox and press button -> quick edit)
        keyBindingsToggle(true);
    }

    private void initialData() {
    	CardLoader.loadAllPacks();
    	defaultState = DefaultState.getInstance();
        currentDeck = new Deck();
        frame = new JFrame();
        
        CardLoader.loadCardAvailability();
        CardLoader.preloadCardThumbnails(CardList.getInstance().getAllCards(), false);
    }

    private void keyBindingsToggle(boolean enabled) {
        globalKeyBindings = enabled;

        InputMap inputMap = frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        InputMap cardDetailInputMap = cardDetailWindow.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = frame.getRootPane().getActionMap();
        ActionMap cardDetailActionMap = cardDetailWindow.getRootPane().getActionMap();
        
        // Key bindings for changing variants in collection mode
        for (int i = 1; i <= 9; i++) { // (I would do anything to replace typing out every function manually)
            final int variant = i - 1; // key 1 for base, key 2 for variant 1, ...
            String key = Integer.toString(i);
            if (!enabled) {
                inputMap.remove(KeyStroke.getKeyStroke(key));
                cardDetailInputMap.remove(KeyStroke.getKeyStroke(key));
                actionMap.remove("variant" + key);
                cardDetailActionMap.remove("variant" + key);
            } else {
                inputMap.put(KeyStroke.getKeyStroke(key), "variant" + key);
                cardDetailInputMap.put(KeyStroke.getKeyStroke(key), "variant" + key);
                Action variantChange = new javax.swing.AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        collectionAddVariant = variant;
                        int displayVariant = getDisplayVariant(currentCard);
                        while (!currentCard.getAvailability(displayVariant)[Config.COLLECTION_LANGUAGE_INDICES[currentSelectedCardLanguage]]) {
                            currentSelectedCardLanguage = (currentSelectedCardLanguage + 1) % Config.ALL_CARD_LANGUAGES.length;
                        }
                        cardDetailView.updateLangLabels();
                        if (currentCard != null) {
                            cardDetailView.showCard(currentCard, displayVariant);
                        }
                    }
                };
                actionMap.put("variant" + key, variantChange);
                cardDetailActionMap.put("variant" + key, variantChange);
            }
        }

        Action quickEditEnable = new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ClickableCardPanel.setQuickEditMode(true);
                scrollCardListPanel.setWheelScrollingEnabled(false);
            }
        };
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "quickedit");
        cardDetailInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "quickedit");
        actionMap.put("quickedit", quickEditEnable);
        cardDetailActionMap.put("quickedit", quickEditEnable);

        Action quickEditDisable = new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ClickableCardPanel.setQuickEditMode(false);
                scrollCardListPanel.setWheelScrollingEnabled(true);
            }
        };
        inputMap.put(KeyStroke.getKeyStroke("released SPACE"), "quickedit_release");
        cardDetailInputMap.put(KeyStroke.getKeyStroke("released SPACE"), "quickedit_release");
        actionMap.put("quickedit_release", quickEditDisable);
        cardDetailActionMap.put("quickedit_release", quickEditDisable);

        Action langSwitch = new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int displayVariant = getDisplayVariant(currentCard);
                currentSelectedCardLanguage = (currentSelectedCardLanguage + 1) % Config.ALL_CARD_LANGUAGES.length;
                prevLangIdx = currentSelectedCardLanguage;
                while (currentCard != null && !currentCard.getAvailability(displayVariant)[Config.COLLECTION_LANGUAGE_INDICES[currentSelectedCardLanguage]]) {
                    currentSelectedCardLanguage = (currentSelectedCardLanguage + 1) % Config.ALL_CARD_LANGUAGES.length;
                    prevLangIdx = currentSelectedCardLanguage;
                }
                cardDetailView.updateLangLabels();
                cardDetailView.updateCardOwnedInfoHighlight(displayVariant);
                //System.out.println("Switched selected language to " + Config.ALL_CARD_LANGUAGES[Config.COLLECTION_LANGUAGE_INDICES[currentSelectedCardLanguage]]);
            }
        };
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_QUOTE, 0), "langswitch");
        cardDetailInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_QUOTE, 0), "langswitch");
        actionMap.put("langswitch", langSwitch);
        cardDetailActionMap.put("langswitch", langSwitch);

        Action searchAction = new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCardList();
                if (isCollectionMode) {
                    updateCardListForCollection();
                }
                defaultState.saveDefaultState();
            }
        };
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "search");
        cardDetailInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "search");
        actionMap.put("search", searchAction);
        cardDetailActionMap.put("search", searchAction);

        Action secFeature0 = new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!searchBox.isFocusOwner()) {
                    secretFeatures[0] = !secretFeatures[0];
                    ClickableCardPanel.setHighlightTranslationAvailable(secretFeatures[0]);
                    CardList.getInstance().updateAllCardPanels();
                    cardList.revalidate();
                    cardList.repaint();
                    deckList.revalidate();
                    deckList.repaint();
                    updateTitle();
                }
            }
        };
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_T, 0), "secFeature0");
        cardDetailInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_T, 0), "secFeature0");
        actionMap.put("secFeature0", secFeature0);
        cardDetailActionMap.put("secFeature0", secFeature0);

        Action secFeature1 = new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!searchBox.isFocusOwner()) {
                    secretFeatures[1] = !secretFeatures[1];
                    updateTitle();
                }
            }
        };
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, 0), "secFeature1");
        cardDetailInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, 0), "secFeature1");
        actionMap.put("secFeature1", secFeature1);
        cardDetailActionMap.put("secFeature1", secFeature1);
    }

    private void updateTitle() {
        String title = CardUtil.getTranslation("app.title") + " v." + Constant.VERSION + " | Bundle v." + Constant.DATA_VERSION;
        if (secretFeatures[0]) {
            title += " [TH]";
        }
        if (secretFeatures[1]) {
            title += " [TM]";
        }
        frame.setTitle(title);
    }

    public static Image getPreviewCardImage() {
        return cardIcon != null ? cardIcon.getImage() : null;
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
                        if (scrollCardListPanel != null) scrollCardListPanel.setWheelScrollingEnabled(false);
                    }
                    e.consume();
                    return true; // stop JButton/JCheckBox default SPACE action (toggle check/press button)
                }

                // Released
                if (e.getID() == KeyEvent.KEY_RELEASED) {
                    if (quickEdit) {
                        quickEdit = false;
                        ClickableCardPanel.setQuickEditMode(false);
                        if (scrollCardListPanel != null) scrollCardListPanel.setWheelScrollingEnabled(true);
                    }
                    e.consume();
                    return true;
                }

                return false;
            }
        };

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keyEventDispatcher);
    }

    public int getDisplayVariant(Card card) {
        if (card == null) return 0;
        return (collectionAddVariant < card.getVariants().length) ? collectionAddVariant : 0;
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

        frame.setTitle(CardUtil.getTranslation("app.title") + " v." + Constant.VERSION + " | Bundle v." + Constant.DATA_VERSION);
        frame.setBounds(0, 0, (int) (1600 * Config.UI_SCALE), (int) (900 * Config.UI_SCALE));
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (deckChanged) {
                    ChoiceDialog dialog = new ChoiceDialog();
                    int result = dialog.show(CardUtil.getTranslation("confirmation"));
                    //System.out.println(result);
                    if (result == 0) {
                        CardLoader.saveDeck(currentDeckDirectory, currentDeckName.getText(), currentDeck);
                        defaultState.setDefaultDeckName(currentDeckName.getText());
                        defaultState.setDefaultDeckPath(AppPaths.userDataDir().resolve("deck").relativize(Paths.get(currentDeckDirectory)).toString());
                        defaultState.saveDefaultState();
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

        // ===== Search Pane (left)
        
        filterPanelOuter = new JPanel(new BorderLayout());
        filterPanelOuter.setPreferredSize(new Dimension(Math.max((int) (125 + 225 * Config.UI_SCALE), (int) (350 * Config.UI_SCALE)), (int) (200 * Config.UI_SCALE)));
        //System.out.println(Config.UI_SCALE);
        filterPanel = new ScrollablePanel();
        filterPanel.setFocusable(true);
        filterPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                filterPanel.requestFocusInWindow();
            }
        });
        
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));
        filterPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        scrollFilterPanel = new JScrollPane(filterPanel);
        scrollFilterPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollFilterPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollFilterPanel.setBorder(null);
        JScrollBar searchScrollBar = scrollFilterPanel.getVerticalScrollBar();
        searchScrollBar.setUnitIncrement(16);
        filterPanelOuter.add(scrollFilterPanel, BorderLayout.CENTER);
        frame.getContentPane().add(filterPanelOuter, BorderLayout.WEST);

        initCheckBox();

        JPanel searchPanelBottom = new JPanel();
        searchPanelBottom.setLayout(new BoxLayout(searchPanelBottom, BoxLayout.Y_AXIS));
        filterPanelOuter.add(searchPanelBottom, BorderLayout.SOUTH);

        JPanel searchPanelButtons = new JPanel();
        searchPanelButtons.setLayout(new GridBagLayout());
        searchPanelButtons.setBorder(new EmptyBorder(3, 3, 3, 3));
        searchPanelBottom.add(searchPanelButtons);

        // ===== File Operations
        fileOperationsPanel = new JPanel(new GridBagLayout());
        fileOperationsPanel.setPreferredSize(new Dimension(Config.CARD_PREVIEW_WIDTH, (int) (60 * Config.UI_SCALE)));
        searchPanelBottom.add(fileOperationsPanel);
        
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
        
        searchBtn = new JButton(CardUtil.getTranslation("search"));
        searchBtn.setRequestFocusEnabled(false);
        searchBtn.setFont(CRnormalLarge);
        componentFontMap.put(searchBtn, "CRnormalLarge"); // Store the font type as a String
        searchPanelButtons.add(searchBtn, gbc_buttons);
        searchBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateCardList();
                if (isCollectionMode) {
                    updateCardListForCollection();
                }
                defaultState.saveDefaultState();
            }
        });

        gbc_buttons.gridx = 1;
        clearFilterBtn = new JButton(CardUtil.getTranslation("clear"));
        clearFilterBtn.setRequestFocusEnabled(false);
        clearFilterBtn.setFont(CRnormalLarge);
        componentFontMap.put(clearFilterBtn, "CRnormalLarge"); // Store the font type as a String
        searchPanelButtons.add(clearFilterBtn, gbc_buttons);
        
        clearFilterBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	cleanCheckBox();
            	updateCardList();
                if (isCollectionMode) {
                    updateCardListForCollection();
                }
                defaultState.cleanSearchFlag();
                defaultState.saveDefaultState();
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
        sortSettingsBtn = new JButton(CardUtil.getTranslation("sort.settings"));
        sortSettingsBtn.setRequestFocusEnabled(false);
        sortSettingsBtn.setFont(CRnormal);
        componentFontMap.put(sortSettingsBtn, "CRnormal"); // Store the font type as a String
        searchPanelButtons.add(sortSettingsBtn, gbc_buttons);
        sortSettingsBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	sortSettingsWindow.show();
            }
        });

        gbc_buttons.gridx = 1;
        settingsBtn = new JButton(CardUtil.getTranslation("settings"));
        settingsBtn.setRequestFocusEnabled(false);
        settingsBtn.setFont(CRnormal);
        componentFontMap.put(settingsBtn, "CRnormal"); // Store the font type as a String
        searchPanelButtons.add(settingsBtn, gbc_buttons);
        settingsBtn.addActionListener(new ActionListener() {
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
        controlsHint.setVisible(false);
        componentFontMap.put(controlsHint, "CRnormal");
        searchPanelBottom.add(controlsHint, gbc_buttons);

        // ===== Content Panes (deck/card list) =====

        centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        frame.getContentPane().add(centerPanel, BorderLayout.CENTER);

        // ==== Deck Pane (middle top)
        deckPanel = new JPanel(new BorderLayout());
        deckPanelLabel = new JLabel(CardUtil.getTranslation("deck"));
        deckPanelLabel.setFont(CRboldSmall);
        deckPanelLabel.setOpaque(true);
        deckPanelLabel.setBorder(null);
        deckPanelLabel.setBackground(new Color(10, 10, 10));
        deckPanelLabel.setForeground(new Color(255,255,255));
        componentFontMap.put(deckPanelLabel, "CRboldSmall");
        deckPanel.add(deckPanelLabel, BorderLayout.NORTH);

        deckList = new ScrollablePanel();
        deckList.setLayout(new GridLayout(0, 6, 5, 5));
        scrollDeckPanel = new JScrollPane(deckList);
        scrollDeckPanel.setMinimumSize(new Dimension(0, 0));
        scrollDeckPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollDeckPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JScrollBar deckScrollBar = scrollDeckPanel.getVerticalScrollBar();
        deckScrollBar.setUnitIncrement(16);
        deckPanel.add(scrollDeckPanel, BorderLayout.CENTER);

        // ==== Card List (middle bottom)
        cardListPanel = new JPanel(new BorderLayout());
        cardListPanelLabel = new JLabel(CardUtil.getTranslation("cardlist"));
        cardListPanelLabel.setFont(CRboldSmall);
        cardListPanelLabel.setOpaque(true);
        cardListPanelLabel.setBorder(null);
        cardListPanelLabel.setBackground(new Color(10, 10, 10));
        cardListPanelLabel.setForeground(new Color(255,255,255));
        componentFontMap.put(cardListPanelLabel, "CRboldSmall");
        cardListPanel.add(cardListPanelLabel, BorderLayout.NORTH);

        cardList = new ScrollablePanel();
        cardList.setLayout(new GridLayout(0, 4, 5, 5));
        
        scrollCardListPanel = new JScrollPane(cardList);
        scrollCardListPanel.setBackground(new Color(255, 255, 255));
        scrollCardListPanel.setMinimumSize(new Dimension(0, 0));
        scrollCardListPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollCardListPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JScrollBar cardListScrollBar = scrollCardListPanel.getVerticalScrollBar();
        cardListScrollBar.setUnitIncrement(16);
        cardListPanel.add(scrollCardListPanel, BorderLayout.CENTER);

        // ==== JSplitPane (between deck and card list)
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, deckPanel, cardListPanel);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerSize(16);
        splitPane.setContinuousLayout(true);
        splitPane.setOneTouchExpandable(true);
        centerPanel.add(splitPane, BorderLayout.CENTER);

        // ==== Deck Details (bottom)
        deckDetailPanel = new JPanel();
        deckDetailPanel.setLayout(new BorderLayout());
        centerPanel.add(deckDetailPanel, BorderLayout.SOUTH);

        deckDetailButtonsPanel = new JPanel();
        deckDetailButtonsPanel.setLayout(new GridBagLayout());
        deckDetailPanel.add(deckDetailButtonsPanel, BorderLayout.SOUTH);
        GridBagConstraints gbc_deckbuttons = new GridBagConstraints();
        gbc_deckbuttons.fill = GridBagConstraints.BOTH;

        gbc_deckbuttons.gridx = 0;
        gbc_deckbuttons.gridy = 0;
        gbc_deckbuttons.gridwidth = 4;
        collectionChangeLabel = new JLabel("", JLabel.CENTER);
        collectionChangeValue = collection.getTotalCollectionChange();
        collectionChangeLabel.setText(CardUtil.getTranslation("collection.change") + " +" + collectionChangeValue[0] + " -" + collectionChangeValue[1]);
        collectionChangeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        collectionChangeLabel.setFont(CRnormal);
        collectionChangeLabel.setVisible(false);
        componentFontMap.put(collectionChangeLabel, "CRnormal");
        deckDetailButtonsPanel.add(collectionChangeLabel, gbc_deckbuttons);

        gbc_deckbuttons.gridx = 0;
        gbc_deckbuttons.gridy = 1;
        gbc_deckbuttons.weightx = 1;
        gbc_deckbuttons.gridwidth = 1;
        hideSearchPaneBtn = new JButton();
        hideSearchPaneBtn.setRequestFocusEnabled(false);
        if (filterPanelOuter.isVisible()) {
            hideSearchPaneBtn.setText("<< " + CardUtil.getTranslation("filter"));
        } else {
            hideSearchPaneBtn.setText(">> " + CardUtil.getTranslation("filter"));
        }
        hideSearchPaneBtn.setFont(CRnormalSmall);
        componentFontMap.put(hideSearchPaneBtn, "CRnormalSmall"); // Store the font type as a String
        deckDetailButtonsPanel.add(hideSearchPaneBtn, gbc_deckbuttons);
        hideSearchPaneBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (filterPanelOuter.isVisible()) {
                    filterPanelOuter.setVisible(false);
                    hideSearchPaneBtn.setText(">> " + CardUtil.getTranslation("filter"));
                } else {
                    filterPanelOuter.setVisible(true);
                    hideSearchPaneBtn.setText("<< " + CardUtil.getTranslation("filter"));
                }
                frame.revalidate();
                frame.repaint();
                frame.getComponentListeners()[0].componentResized(null);
            }
        });

        gbc_deckbuttons.gridx = 1;
        gbc_deckbuttons.weightx = 5;
        clearDeckBtn = new JButton(CardUtil.getTranslation("deck.clear"));
        clearDeckBtn.setRequestFocusEnabled(false);
        clearDeckBtn.setFont(CRnormalLarge);
        componentFontMap.put(clearDeckBtn, "CRnormalLarge"); // Store the font type as a String
        deckDetailButtonsPanel.add(clearDeckBtn, gbc_deckbuttons);

        clearDeckBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (isCollectionMode) {
                    CollectionSummaryDialog dialog = new CollectionSummaryDialog();
                    dialog.show();
                } else {
                    currentDeck.clear();
                    CardList.getInstance().clearCardListCount();
                    updateDeck();
                    CardList.getInstance().updateAllCardPanels();
                    deckChanged = true;
                }
            }
        });

        gbc_deckbuttons.gridx = 2;
        gbc_deckbuttons.weightx = 5;
        randomDrawSimBtn = new JButton(CardUtil.getTranslation("deck.drawsim"));
        randomDrawSimBtn.setRequestFocusEnabled(false);
        randomDrawSimBtn.setFont(CRnormalLarge);
        componentFontMap.put(randomDrawSimBtn, "CRnormalLarge"); // Store the font type as a String
        deckDetailButtonsPanel.add(randomDrawSimBtn, gbc_deckbuttons);
        randomDrawSimBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                randomDrawSimWindow.show(currentDeck, currentDeckName.getText());
            }
        });

        gbc_deckbuttons.gridx = 3;
        gbc_deckbuttons.weightx = 1;
        hidePreviewPaneBtn = new JButton();
        hidePreviewPaneBtn.setRequestFocusEnabled(false);
        if (cardPreviewPanel == null || cardPreviewPanel.isVisible()) {
            hidePreviewPaneBtn.setText(">> " + CardUtil.getTranslation("preview.popout"));
        } else {
            hidePreviewPaneBtn.setText("<< " + CardUtil.getTranslation("preview.sidebar"));
        }
        hidePreviewPaneBtn.setFont(CRnormalSmall);
        componentFontMap.put(hidePreviewPaneBtn, "CRnormalSmall"); // Store the font type as a String
        deckDetailButtonsPanel.add(hidePreviewPaneBtn, gbc_deckbuttons);
        hidePreviewPaneBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                togglePreviewHost();
                if (cardPreviewPanel.isVisible()) {
                    cardPreviewPanel.setVisible(false);
                    hidePreviewPaneBtn.setText("<< " + CardUtil.getTranslation("preview.sidebar"));
                } else {
                    cardPreviewPanel.setVisible(true);
                    hidePreviewPaneBtn.setText(">> " + CardUtil.getTranslation("preview.popout"));
                }
                frame.revalidate();
                frame.repaint();
                frame.getComponentListeners()[0].componentResized(null);
            }
        });

        deckDistributionPanel = new JPanel();
        deckDistributionPanel.setLayout(new GridBagLayout());
        deckDetailPanel.add(deckDistributionPanel, BorderLayout.NORTH);

        updateDeckDistribution();

        deckDetailSummaryPanel = new JPanel();
        deckDetailSummaryPanel.setLayout(new GridBagLayout());
        deckDetailPanel.add(deckDetailSummaryPanel, BorderLayout.CENTER);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 2;
        gbc.weighty = 0.3;
        gbc.gridheight = 1;
        cardCountHintTxt = new JLabel(CardUtil.getTranslation("deck.cards"));
        cardCountHintTxt.setFont(CRnormalSmall);
        componentFontMap.put(cardCountHintTxt, "CRnormalSmall"); // Store the font type as a String
        deckDetailSummaryPanel.add(cardCountHintTxt, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.75;
        flipCountHintTxt = new JLabel(CardUtil.getTranslation("deck.flip"));
        flipCountHintTxt.setFont(CRnormalSmall);
        componentFontMap.put(flipCountHintTxt, "CRnormalSmall"); // Store the font type as a String
        deckDetailSummaryPanel.add(flipCountHintTxt, gbc);

        gbc.gridx = 2;
        gbc.gridheight = 2;
        gbc.weightx = 1.25;
        flipTypeCountTxt = new JLabel();
        flipTypeCountTxt.setFont(CRnormalSmall);
        componentFontMap.put(flipTypeCountTxt, "CRnormalSmall"); // Store the font type as a String
        deckDetailSummaryPanel.add(flipTypeCountTxt, gbc);

        gbc.gridx = 3;
        gbc.gridheight = 1;
        gbc.weightx = 2;
        extraCountHintTxt = new JLabel(CardUtil.getTranslation("deck.extra"));
        extraCountHintTxt.setFont(CRnormalSmall);
        componentFontMap.put(extraCountHintTxt, "CRnormalSmall"); // Store the font type as a String
        deckDetailSummaryPanel.add(extraCountHintTxt, gbc);

        gbc.gridx = 4;
        gbc.weightx = 0.75;
        deckCookieSummaryHintTxt = new JLabel(CardUtil.getTranslation("deck.cookies"));
        deckCookieSummaryHintTxt.setFont(CRnormalSmall);
        componentFontMap.put(deckCookieSummaryHintTxt, "CRnormalSmall"); // Store the font type as a String
        deckDetailSummaryPanel.add(deckCookieSummaryHintTxt, gbc);

        gbc.gridx = 5;
        gbc.gridheight = 2;
        gbc.weightx = 1.25;
        levelCountTxt = new JLabel();
        levelCountTxt.setFont(CRnormalSmall);
        componentFontMap.put(levelCountTxt, "CRnormalSmall"); // Store the font type as a String
        deckDetailSummaryPanel.add(levelCountTxt, gbc);

        gbc.gridx = 6;
        gbc.gridheight = 1;
        gbc.weightx = 2;
        deckItemHintTxt = new JLabel(CardUtil.getTranslation("deck.items"));
        deckItemHintTxt.setFont(CRnormalSmall);
        componentFontMap.put(deckItemHintTxt, "CRnormalSmall"); // Store the font type as a String
        deckDetailSummaryPanel.add(deckItemHintTxt, gbc);

        gbc.gridx = 7;
        gbc.weightx = 2;
        deckTrapHintTxt = new JLabel(CardUtil.getTranslation("deck.traps"));
        deckTrapHintTxt.setFont(CRnormalSmall);
        componentFontMap.put(deckTrapHintTxt, "CRnormalSmall"); // Store the font type as a String
        deckDetailSummaryPanel.add(deckTrapHintTxt, gbc);

        gbc.gridx = 8;
        gbc.weightx = 2;
        deckStageHintTxt = new JLabel(CardUtil.getTranslation("deck.stages"));
        deckStageHintTxt.setFont(CRnormalSmall);
        componentFontMap.put(deckStageHintTxt, "CRnormalSmall"); // Store the font type as a String
        deckDetailSummaryPanel.add(deckStageHintTxt, gbc);



        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.weightx = 2;
        gbc.weighty = 0.7;
        gbc.gridheight = 1;
        cardCountTxt = new JLabel("0/60");
        cardCountTxt.setFont(CRnormalEXLarge);
        componentFontMap.put(cardCountTxt, "CRnormalEXLarge"); // Store the font type as a String
        deckDetailSummaryPanel.add(cardCountTxt, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.75;
        flipCountTxt = new JLabel("0/16");
        flipCountTxt.setFont(CRnormalEXLarge);
        componentFontMap.put(flipCountTxt, "CRnormalEXLarge"); // Store the font type as a String
        deckDetailSummaryPanel.add(flipCountTxt, gbc);

        gbc.gridx = 3;
        gbc.weightx = 2;
        extraCountTxt = new JLabel("0/6");
        extraCountTxt.setFont(CRnormalEXLarge);
        componentFontMap.put(extraCountTxt, "CRnormalEXLarge"); // Store the font type as a String
        deckDetailSummaryPanel.add(extraCountTxt, gbc);

        gbc.gridx = 4;
        gbc.weightx = 0.75;
        deckCookieSummaryTxt = new JLabel("0");
        deckCookieSummaryTxt.setFont(CRnormalEXLarge);
        componentFontMap.put(deckCookieSummaryTxt, "CRnormalEXLarge"); // Store the font type as a String
        deckDetailSummaryPanel.add(deckCookieSummaryTxt, gbc);

        gbc.gridx = 6;
        gbc.weightx = 2;
        deckItemTxt = new JLabel("0");
        deckItemTxt.setFont(CRnormalEXLarge);
        componentFontMap.put(deckItemTxt, "CRnormalEXLarge"); // Store the font type as a String
        deckDetailSummaryPanel.add(deckItemTxt, gbc);

        gbc.gridx = 7;
        gbc.weightx = 2;
        deckTrapTxt = new JLabel("0");
        deckTrapTxt.setFont(CRnormalEXLarge);
        componentFontMap.put(deckTrapTxt, "CRnormalEXLarge"); // Store the font type as a String
        deckDetailSummaryPanel.add(deckTrapTxt, gbc);

        gbc.gridx = 8;
        gbc.weightx = 2;
        deckStageTxt = new JLabel("0");
        deckStageTxt.setFont(CRnormalEXLarge);
        componentFontMap.put(deckStageTxt, "CRnormalEXLarge"); // Store the font type as a String
        deckDetailSummaryPanel.add(deckStageTxt, gbc);

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
                scrollDeckPanel.setBounds(0, 0, width, deckPaneHeight);
                deckDetailPanel.setBounds(0, deckPaneHeight, width, deckDetailPaneHeight);
                scrollCardListPanel.setBounds(0, deckPaneHeight + deckDetailPaneHeight, width, cardsPaneHeight);

                // Update the layouts with the new column count
                GridLayout deckLayout = (GridLayout) deckList.getLayout();
                GridLayout cardsLayout = (GridLayout) cardList.getLayout();
                
                if (deckLayout.getColumns() != columns) {
                    deckLayout.setColumns(columns);
                    deckList.revalidate();
                    deckList.repaint();
                }

                if (cardsLayout.getColumns() != columns) {
                    cardsLayout.setColumns(columns);
                    cardList.revalidate();
                    cardList.repaint();
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

                if (cardDetailView != null) {
                    cardDetailView.revalidate();
                    cardDetailView.repaint();
                    cardDetailView.refreshLayout();
                }
            }
        });

        // Trigger an initial resize to set the correct sizes
        frame.getComponentListeners()[0].componentResized(null);

        frame.getContentPane().add(centerPanel, BorderLayout.CENTER);
        
        // ==== Sidebar Panel (right, detachable)

        cardPreviewPanel = new JPanel();
        cardPreviewPanel.setPreferredSize(new Dimension(Config.CARD_PREVIEW_WIDTH, (int) frame.getBounds().getHeight()));
        cardPreviewPanel.setLayout(new BorderLayout());

        cardDetailView = new CardDetailView();
        cardPreviewHost = new JPanel(new BorderLayout());
        cardPreviewHost.add(cardDetailView, BorderLayout.CENTER);
        cardPreviewPanel.add(cardPreviewHost, BorderLayout.CENTER);

        cardDetailWindow = new CardDetailWindow();

        GridBagConstraints gbc_fileOp = new GridBagConstraints();
        gbc_fileOp.fill = GridBagConstraints.BOTH;
        gbc_fileOp.gridx = 0;
        gbc_fileOp.gridy = 0;

        currentDeckName = new JLabel(defaultState.getDefaultDeckName(), JLabel.CENTER);
        currentDeckName.setAlignmentX(Component.CENTER_ALIGNMENT);
        currentDeckName.setFont(CRnormal);
        currentDeckName.setPreferredSize(new Dimension(Config.CARD_PREVIEW_WIDTH, CRnormal.getSize()));
        componentFontMap.put(currentDeckName, "CRnormal");
        fileOperationsPanel.add(currentDeckName, gbc_fileOp);

        fileOperationsButtonsPanel = new JPanel();
        fileOperationsButtonsPanel.setLayout(new GridBagLayout());
        gbc_fileOp.gridy = 1;
        fileOperationsPanel.add(fileOperationsButtonsPanel, gbc_fileOp);

        GridBagConstraints gbc_panel = new GridBagConstraints();
        gbc_panel.fill = GridBagConstraints.BOTH;
        gbc_panel.gridwidth = 1;
        gbc_panel.gridx = 0;
        gbc_panel.gridy = 0;
        ImageIcon saveIcon = new ImageIcon(AppPaths.dataDir().resolve("icons_ui/save_" + Config.THEME + ".png").toString());
        saveBtn = new JButton(saveIcon);
        saveBtn.setToolTipText(CardUtil.getTranslation("save"));
        saveBtn.setRequestFocusEnabled(false);
        componentFontMap.put(saveBtn, "CRnormal"); // Store the font type as a String
        fileOperationsButtonsPanel.add(saveBtn, gbc_panel);
        saveBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CardLoader.saveDeck(currentDeckDirectory, currentDeckName.getText(), currentDeck);
                defaultState.setDefaultDeckName(currentDeckName.getText());
                defaultState.setDefaultDeckPath(AppPaths.userDataDir().resolve("deck").relativize(Paths.get(currentDeckDirectory)).toString());
                defaultState.saveDefaultState();
                Dialog dialog = new Dialog();
                deckChanged = false;
                dialog.show(CardUtil.getTranslation("deck.saved"));
            }
        });

        gbc_panel.gridwidth = 1;
        gbc_panel.gridx = 1;
        ImageIcon saveAsIcon = new ImageIcon(AppPaths.dataDir().resolve("icons_ui/saveas_" + Config.THEME + ".png").toString());
        saveAsBtn = new JButton(saveAsIcon);
        saveAsBtn.setToolTipText(CardUtil.getTranslation("saveas"));
        saveAsBtn.setRequestFocusEnabled(false);
        componentFontMap.put(saveAsBtn, "CRnormalSmall"); // Store the font type as a String
        fileOperationsButtonsPanel.add(saveAsBtn, gbc_panel);
        saveAsBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                FilePicker filePicker = new FilePicker();
                String pickedDirectory = filePicker.show("save");
                if (pickedDirectory != null) {
                    currentDeckDirectory = pickedDirectory;
                    CardLoader.saveDeck(pickedDirectory, pickedDirectory.substring(pickedDirectory.lastIndexOf(File.separator) + 1), currentDeck);
                    currentDeckName.setText(pickedDirectory.substring(pickedDirectory.lastIndexOf(File.separator) + 1, pickedDirectory.length() - 4));
                    defaultState.setDefaultDeckName(currentDeckName.getText());
                    defaultState.setDefaultDeckPath(AppPaths.userDataDir().resolve("deck").relativize(Paths.get(pickedDirectory)).toString());
                    defaultState.saveDefaultState();
                    Dialog dialog = new Dialog();
                    deckChanged = false;
                    dialog.show(CardUtil.getTranslation("deck.saved"));
                }
            }
        });

        gbc_panel.gridx = 2;
        ImageIcon selectIcon = new ImageIcon(AppPaths.dataDir().resolve("icons_ui/load_" + Config.THEME + ".png").toString());
        selectBtn = new JButton(selectIcon);
        selectBtn.setToolTipText(CardUtil.getTranslation("select.file"));
        selectBtn.setRequestFocusEnabled(false);
        componentFontMap.put(selectBtn, "CRnormalSmall"); // Store the font type as a String
        selectBtn.setActionCommand("Select File");
        fileOperationsButtonsPanel.add(selectBtn, gbc_panel);
        selectBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				FilePicker filePicker = new FilePicker();
                String pickedDirectory = filePicker.show("load");
				if (pickedDirectory != null) {
                    if (deckChanged) {
                        ChoiceDialog dialog = new ChoiceDialog();
                        int result = dialog.show(CardUtil.getTranslation("confirmation"));
                        //System.out.println(result);
                        if (result == 0) {
                            CardLoader.saveDeck(currentDeckDirectory, currentDeckName.getText(), currentDeck);
                            defaultState.setDefaultDeckName(currentDeckName.getText());
                            defaultState.setDefaultDeckPath(AppPaths.userDataDir().resolve("deck").relativize(Paths.get(currentDeckDirectory)).toString());
                            defaultState.saveDefaultState();
                        } else if (result == 2) {
                            return; // Cancel the file selection
                        }
                    }
                    File selectedFile = new File(pickedDirectory);
                    String filename = pickedDirectory.substring(pickedDirectory.lastIndexOf(File.separator) + 1);
                    //System.out.println(filename);
                    currentDeckName.setText(filename.substring(0, filename.length() - 4));
                    currentDeck.clear();
                    CardList.getInstance().clearCardListCount();
                    currentDeckDirectory = pickedDirectory;
                    currentDeck = CardLoader.loadDeck(pickedDirectory, filename.substring(0, filename.length() - 4));
                    currentDeck.sort();
                    updateDeck();
                    CardList.getInstance().updateAllCardPanels();
                    defaultState.setDefaultDeckName(currentDeckName.getText());
                    defaultState.setDefaultDeckPath(AppPaths.userDataDir().resolve("deck").relativize(selectedFile.toPath()).toString());
                    defaultState.saveDefaultState();
                    deckChanged = false;
				} 
            }
        });

        gbc_panel.gridx = 3;
        ImageIcon importIcon = new ImageIcon(AppPaths.dataDir().resolve("icons_ui/import_" + Config.THEME + ".png").toString());
        importBtn = new JButton(importIcon);
        importBtn.setToolTipText(CardUtil.getTranslation("deck.import"));
        importBtn.setRequestFocusEnabled(false);
        componentFontMap.put(importBtn, "CRnormalSmall"); // Store the font type as a String
        fileOperationsButtonsPanel.add(importBtn, gbc_panel);
        importBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
                ImportDialog importDialog = new ImportDialog();
        		List<String> importedDeck = importDialog.show();
                if (importedDeck != null) {
                    currentDeck.clear();
                    CardList.getInstance().clearCardListCount();
                    updateDeck();
                    for (String id : importedDeck) {
                        Card current = CardList.getInstance().getCardById(id);
                        if (current != null) addCard(current);
                    }
                    CardList.getInstance().updateAllCardPanels();
                    deckChanged = true;
                }
        	}
        });
        
        gbc_panel.gridx = 4;
        ImageIcon showDeckIcon = new ImageIcon(AppPaths.dataDir().resolve("icons_ui/show_" + Config.THEME + ".png").toString());
        showDeckBtn = new JButton(showDeckIcon);
        showDeckBtn.setToolTipText(CardUtil.getTranslation("deck.show"));
        showDeckBtn.setRequestFocusEnabled(false);
        componentFontMap.put(showDeckBtn, "CRnormalSmall"); // Store the font type as a String
        fileOperationsButtonsPanel.add(showDeckBtn, gbc_panel);
        showDeckBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		deckWindow.show(currentDeck, currentDeckName.getText());
        	}
        });

        gbc_panel.gridx = 5;
        ImageIcon showDeckDifferentialIcon = new ImageIcon(AppPaths.dataDir().resolve("icons_ui/compare_" + Config.THEME + ".png").toString());
        showDeckDifferentialBtn = new JButton(showDeckDifferentialIcon);
        showDeckDifferentialBtn.setToolTipText(CardUtil.getTranslation("deck.compare"));
        showDeckDifferentialBtn.setRequestFocusEnabled(false);
        componentFontMap.put(showDeckDifferentialBtn, "CRnormalSmall"); // Store the font type as a String
        fileOperationsButtonsPanel.add(showDeckDifferentialBtn, gbc_panel);
        showDeckDifferentialBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
                FilePicker filePicker = new FilePicker();
                String[] pickedDirectory = filePicker.showForCompare();
				if (pickedDirectory != null) {
                    boolean compareMode = pickedDirectory[1].equals("to");
                    File selectedFile = new File(pickedDirectory[0]);
                    String filename = pickedDirectory[0].substring(pickedDirectory[0].lastIndexOf(File.separator) + 1);
                    Map<String, Integer> mDeck2 = CardLoader.loadDeckTemp(filename.substring(0, filename.length() - 4));
                    deckDifferentialWindow.show(currentDeck, currentDeckName.getText(), mDeck2, filename.substring(0, filename.length() - 4), compareMode);
				}
            }
        });

        frame.getContentPane().add(cardPreviewPanel, BorderLayout.EAST);

        updateCardList();
        currentDeckDirectory = AppPaths.userDataDir().resolve("deck").resolve(defaultState.getDefaultDeckPath()).toString();
        //System.out.println("Loading deck from: " + currentDeckDirectory);
        currentDeck = CardLoader.loadDeck(currentDeckDirectory, currentDeckName.getText());
        currentDeck.sort();
        updateDeck();
    }
	
    private void initCheckBox() {
        Border filterBorder = BorderFactory.createEmptyBorder(0, 0, 20, 0);

        // ========================= search by name =========================
        JPanel searchLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // Wrap the search box
        filterPanel.add(searchLabelPanel);

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
        filterPanel.add(searchBox);
    	
        // ========================= color ==================================
        labelColor = new JLabel(CardUtil.getTranslation("color"), JLabel.LEFT);
        labelColor.setFont(CRboldEXLargeFilter);
        componentFontMap.put(labelColor, "CRboldEXLargeFilter"); // Store the font type as a String
        JPanel colorLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // Wrap the label
        colorLabelPanel.add(labelColor);
        filterPanel.add(colorLabelPanel);

        JPanel colorOuterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // Wrap the grid
        JPanel colorCheckboxGroup = new JPanel();
        colorCheckboxGroup.setLayout(new GridLayout(1, 0));
        colorCheckboxGroup.setBorder(filterBorder);
        colorOuterPanel.add(colorCheckboxGroup);
        filterPanel.add(colorOuterPanel);

        cb_color = new JToggleButton[CardUtil.COLOR_MAX];
        for(int i=0; i<CardUtil.COLOR_MAX; i++) {
            ImageIcon icon = new ImageIcon(AppPaths.dataDir().resolve("icons/36px/" + CardUtil.CardColor.fromValue(i).getColorShort() + ".png").toString());
        	cb_color[i] = new JToggleButton(icon);
            cb_color[i].setToolTipText(CardUtil.CardColor.fromValue(i).getDisplayName());
        	cb_color[i].setSelected(defaultState.getDefaultColorFlag(i));
            cb_color[i].setRequestFocusEnabled(false);
            cb_color[i].setFont(CRnormal);
            componentFontMap.put(cb_color[i], "CRnormal"); // Store the font type as a String
            colorCheckboxGroup.add(cb_color[i]);
            final int id = i;
            cb_color[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	defaultState.setDefaultColorFlag(id, cb_color[id].isSelected());
                }
            });
        }
        
        // ========================= type ==================================
        labelType = new JLabel(CardUtil.getTranslation("type"), JLabel.LEFT);
        labelType.setFont(CRboldEXLargeFilter);
        componentFontMap.put(labelType, "CRboldEXLargeFilter"); // Store the font type as a String
        JPanel typeLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // Wrap the label
        typeLabelPanel.add(labelType);
        filterPanel.add(typeLabelPanel);

        JPanel typeOuterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // Wrap the grid
        JPanel typeCheckboxGroup = new JPanel();
        typeCheckboxGroup.setLayout(new GridBagLayout());
        typeCheckboxGroup.setBorder(filterBorder);
        typeOuterPanel.add(typeCheckboxGroup);
        filterPanel.add(typeOuterPanel);

        GridBagConstraints gbc_type = new GridBagConstraints();
        gbc_type.anchor = GridBagConstraints.WEST;
        gbc_type.gridx = 0;

        // Rows: Cookie + Levels, Cookie HP, Flip + Flip Types, [Extra, Item, Trap, Stage]
        JPanel[] typeCheckboxGroupRows = new JPanel[6];
        for (int i=0; i<6; i++) {
            gbc_type.gridy = i;
        	typeCheckboxGroupRows[i] = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        	typeCheckboxGroup.add(typeCheckboxGroupRows[i], gbc_type);
        } 

        cb_type_cookie = new JCheckBox(CardUtil.getTranslation("filter.cookie"));
		cb_type_cookie.setSelected(defaultState.getDefaultTypeFlag(0));
        cb_type_cookie.setFont(CRnormal);
        componentFontMap.put(cb_type_cookie, "CRnormal"); // Store the font type as a String
        typeCheckboxGroupRows[0].add(cb_type_cookie);
        cb_type_cookie.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	defaultState.setDefaultTypeFlag(0, cb_type_cookie.isSelected());
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

        JPanel lvCheckboxGroups = new JPanel(new GridLayout(0, 1));
        typeCheckboxGroupRows[0].add(lvCheckboxGroups);

        JPanel lvPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        lvCheckboxGroups.add(lvPanel);

        labelLV = new JLabel("LV.");
        labelLV.setFont(CRnormal);
        componentFontMap.put(labelLV, "CRnormal"); // Store the font type as a String
        lvPanel.add(labelLV);

        cb_level = new JCheckBox[CardUtil.LEVEL_MAX];
        for(int i=0; i<CardUtil.LEVEL_MAX; i++) {
            final int id = i;
        	final int lv = i + 1;
        	cb_level[i] = new JCheckBox(Integer.toString(lv));
        	cb_level[i].setSelected(defaultState.getDefaultLvFlag(id));
            cb_level[i].setFont(CRnormal);
            componentFontMap.put(cb_level[i], "CRnormal"); // Store the font type as a String
            if (CardUtil.LEVELS.indexOf(lv) == -1) {
                cb_level[i].setEnabled(false);
                cb_level[i].setVisible(false);
            }
            typeCheckboxGroupRows[0].add(cb_level[i]);
            cb_level[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	defaultState.setDefaultLvFlag(lv, cb_level[id].isSelected());
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
        	cb_HP[i].setSelected(defaultState.getDefaultHPFlag(hp));
            cb_HP[i].setFont(CRnormal);
            componentFontMap.put(cb_HP[i], "CRnormal"); // Store the font type as a String
            hpPanel.add(cb_HP[i]);
            
            cb_HP[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	defaultState.setDefaultHPFlag(hp, cb_HP[id].isSelected());
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
        	cb_HPAwaken[i].setSelected(defaultState.getDefaultHPAwakenFlag(i));
            cb_HPAwaken[i].setFont(CRnormal);
            componentFontMap.put(cb_HPAwaken[i], "CRnormal"); // Store the font type as a String
            hpAwakenPanel.add(cb_HPAwaken[i]);
            
            cb_HPAwaken[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	defaultState.setDefaultHPAwakenFlag(id, cb_HPAwaken[id].isSelected());
                }
            });
            cb_HPAwaken[i].setEnabled(cb_type_cookie.isSelected());
        }
        
        labelFlip = new JLabel(CardUtil.getTranslation("filter.flip") + ":");
        labelFlip.setFont(CRnormal);
        componentFontMap.put(labelFlip, "CRnormal"); // Store the font type as a String
        typeCheckboxGroupRows[2].add(labelFlip);

        ButtonGroup flipConditionGroup = new ButtonGroup();
        rb_flip_condition = new JRadioButton[3];
        String[] flipConditions = {"include", "only", "exclude"};

        for (int i=0; i<3; i++) {
            final int id = i;
            rb_flip_condition[i] = new JRadioButton(CardUtil.getTranslation("filter.flip." + flipConditions[id]));
            flipConditionGroup.add(rb_flip_condition[i]);
            rb_flip_condition[i].setSelected(defaultState.getDefaultFlipFlag() == i);
            rb_flip_condition[i].setFont(CRnormal);
            componentFontMap.put(rb_flip_condition[i], "CRnormal"); // Store the font type as a String
            typeCheckboxGroupRows[2].add(rb_flip_condition[i]);
            rb_flip_condition[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    defaultState.setDefaultFlipFlag(id);
                    if (id == 1) {
                        for (JCheckBox cb : cb_flipType) {
                            cb.setEnabled(true);
                        }
                    } else {
                        for (JCheckBox cb : cb_flipType) {
                            cb.setEnabled(false);
                        }
                    }
                }
            });
        }

        cb_flipType = new JCheckBox[3];
        cb_flipType[0] = new JCheckBox(CardUtil.getTranslation("flip.heal"));
        cb_flipType[1] = new JCheckBox(CardUtil.getTranslation("flip.draw"));
        cb_flipType[2] = new JCheckBox(CardUtil.getTranslation("flip.special"));
        for (int i=0; i<3; i++) {
        	final int id = i;
        	cb_flipType[i].setSelected(defaultState.getDefaultFlipTypeFlag(i));
            cb_flipType[i].setFont(CRnormal);
            componentFontMap.put(cb_flipType[i], "CRnormal"); // Store the font type as a String
            typeCheckboxGroupRows[3].add(cb_flipType[i]);
            cb_flipType[i].setEnabled(rb_flip_condition[1].isSelected());
            cb_flipType[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	defaultState.setDefaultFlipTypeFlag(id, cb_flipType[id].isSelected());
                }
            });
        }

        cb_extra = new JCheckBox(CardUtil.getTranslation("filter.extra"));
        cb_extra.setSelected(defaultState.getDefaultExtraFlag());
        cb_extra.setFont(CRnormal);
        componentFontMap.put(cb_extra, "CRnormal"); // Store the font type as a String
        typeCheckboxGroupRows[4].add(cb_extra);
        cb_extra.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	defaultState.setDefaultExtraFlag(cb_extra.isSelected());
            }
        });

        cb_specialplay = new JCheckBox(CardUtil.SkillType.fromValue(CardUtil.SKILL_TYPE_MAX - 1).getDisplayName());
        cb_specialplay.setFont(CRnormal);
        componentFontMap.put(cb_specialplay, "CRnormal"); // Store the font type as a String
        typeCheckboxGroupRows[4].add(cb_specialplay);
        /*cb_specialplay.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	mDefaultState.setDefaultSpecialPlayFlag(cb_specialplay.isSelected());
            }
        });*/
        
        cb_type_item = new JCheckBox(CardUtil.getTranslation("filter.item"));
        cb_type_item.setSelected(defaultState.getDefaultTypeFlag(1));
        cb_type_item.setFont(CRnormal);
        componentFontMap.put(cb_type_item, "CRnormal"); // Store the font type as a String
        typeCheckboxGroupRows[5].add(cb_type_item);
        cb_type_item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	defaultState.setDefaultTypeFlag(1, cb_type_item.isSelected());
            }
        });


        cb_type_trap = new JCheckBox(CardUtil.getTranslation("filter.trap"));
        cb_type_trap.setSelected(defaultState.getDefaultTypeFlag(2));
        cb_type_trap.setFont(CRnormal);
        componentFontMap.put(cb_type_trap, "CRnormal"); // Store the font type as a String
        typeCheckboxGroupRows[5].add(cb_type_trap);
        cb_type_trap.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	defaultState.setDefaultTypeFlag(2, cb_type_trap.isSelected());
            }
        });


        cb_type_stage = new JCheckBox(CardUtil.getTranslation("filter.stage"));
        cb_type_stage.setSelected(defaultState.getDefaultTypeFlag(3));
        cb_type_stage.setFont(CRnormal);
        componentFontMap.put(cb_type_stage, "CRnormal"); // Store the font type as a String
        typeCheckboxGroupRows[5].add(cb_type_stage);
        cb_type_stage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	defaultState.setDefaultTypeFlag(3, cb_type_stage.isSelected());
            }
        });

        // ========================= pack ==================================

        labelSeries = new JLabel(CardUtil.getTranslation("series"), JLabel.LEFT);
        labelSeries.setFont(CRboldEXLargeFilter);
        componentFontMap.put(labelSeries, "CRboldEXLargeFilter"); // Store the font type as a String
        JPanel seriesLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // Wrap the label
        seriesLabelPanel.add(labelSeries);
        filterPanel.add(seriesLabelPanel);

        JPanel quickSelectBtnGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        filterPanel.add(quickSelectBtnGroup);

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
        packCheckboxGroup.setLayout(new GridLayout(0, 4));
        packCheckboxGroup.setBorder(filterBorder);
        packOuterPanel.add(packCheckboxGroup);
        filterPanel.add(packOuterPanel);

        cb_pack = new JCheckBox[CardUtil.CardPack.size()];
        for(int i=0; i<CardUtil.CardPack.size(); i++) {
        	final int id = i;
        	cb_pack[i] = new JCheckBox(CardUtil.CardPack.get(i).replace("_", ""));
        	cb_pack[i].setSelected(defaultState.getDefaultPackFlag(CardUtil.CardPack.get(i)));
            if (!CardUtil.CardPackAvailability.get(CardUtil.CardPack.get(i)).get(Config.REGION)) {
                cb_pack[i].setEnabled(false);
                cb_pack[i].setSelected(false);
            }
            //System.out.println("Pack " + CardUtil.CardPack.get(i) + CardUtil.CardPackAvailability.get(CardUtil.CardPack.get(i)).get(Config.REGION));
            cb_pack[i].setFont(CRnormal);
            componentFontMap.put(cb_pack[i], "CRnormal"); // Store the font type as a String
            packCheckboxGroup.add(cb_pack[i]);
            cb_pack[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	defaultState.setDefaultPackFlag(CardUtil.CardPack.get(id), cb_pack[id].isSelected());
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
                        defaultState.setDefaultPackFlag(cb.getText(), quickSelectMode);
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
                        defaultState.setDefaultPackFlag(cb.getText(), quickSelectMode);
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
        filterPanel.add(rarityLabelPanel);

        JPanel rarityOuterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // Wrap the grid
        filterPanel.add(rarityOuterPanel);
        JPanel rarityCheckboxGroup = new JPanel(new GridBagLayout());
        rarityCheckboxGroup.setBorder(filterBorder);

        GridBagConstraints gbc_rarity = new GridBagConstraints();
        gbc_rarity.anchor = GridBagConstraints.WEST;
        gbc_rarity.gridy = 0;

        cb_rarity = new JCheckBox[CardUtil.RARITY_MAX];
        for(int i=0; i<CardUtil.RARITY_MAX; i++) {
            gbc_rarity.gridx = i;
        	cb_rarity[i] = new JCheckBox(CardUtil.CardRarity.fromValue(i).getDisplayName());
        	cb_rarity[i].setSelected(defaultState.getDefaultRarityFlag(i));
            cb_rarity[i].setFont(CRnormal);
            componentFontMap.put(cb_rarity[i], "CRnormal"); // Store the font type as a String
            rarityCheckboxGroup.add(cb_rarity[i], gbc_rarity);
            final int id = i;
            cb_rarity[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	defaultState.setDefaultRarityFlag(id, cb_rarity[id].isSelected());
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
        filterPanel.add(attackAttrLabelPanel);

        attackAttrBasePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // Wrap the grid
        attackAttrBasePanel.setVisible(Config.ADVANCED_FILTERING);
        filterPanel.add(attackAttrBasePanel);

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

        labelAvgDMG = new JLabel("<html><u>" + CardUtil.getTranslation("filter.avgdmg") + "</u></html>", JLabel.LEFT);
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

        labelPeakDMG = new JLabel("<html><u>" + CardUtil.getTranslation("filter.peakdmg") + "</u></html>", JLabel.LEFT);
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
        filterPanel.add(skillTypeLabelPanel);

        skillTypeBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        filterPanel.add(skillTypeBtnPanel);

        skillTypeOuterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // Wrap the grid
        filterPanel.add(skillTypeOuterPanel);
        JPanel skillTypeCheckboxGroup = new JPanel(new GridLayout(0, 1));
        skillTypeCheckboxGroup.setBorder(filterBorder);

        quickSelectBtnSkillType = new JButton(CardUtil.getTranslation("filter.skilltypetoggle"));
        quickSelectBtnSkillType.setRequestFocusEnabled(false);
        quickSelectBtnSkillType.setFont(CRnormal);
        componentFontMap.put(quickSelectBtnSkillType, "CRnormal");
        skillTypeBtnPanel.add(quickSelectBtnSkillType);
        skillTypeBtnPanel.setVisible(Config.ADVANCED_FILTERING);

        cb_skillType = new JCheckBox[CardUtil.SKILL_TYPE_MAX - 1];
        
        for(int i=0; i<CardUtil.SKILL_TYPE_MAX - 1; i++) {
        	cb_skillType[i] = new JCheckBox(CardUtil.SkillType.fromValue(i).getDisplayName());
        	//cb_skillType[i].setSelected(mDefaultState.getDefaultSkillTypeFlag(i));
            cb_skillType[i].setFont(CRnormal);
            componentFontMap.put(cb_skillType[i], "CRnormal"); // Store the font type as a String
            skillTypeCheckboxGroup.add(cb_skillType[i]);
        }
        skillTypeOuterPanel.add(skillTypeCheckboxGroup);
        skillTypeLabelPanel.setVisible(Config.ADVANCED_FILTERING);
        skillTypeOuterPanel.setVisible(Config.ADVANCED_FILTERING);

        quickSelectBtnSkillType.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean quickSelectMode = false; // All selected, disable all
                for (JCheckBox cb : cb_skillType) {
                    if (!cb.isSelected() && cb.isEnabled()) {
                        quickSelectMode = true; // At least one is unselected, enable all
                        break;
                    }
                }

                for (JCheckBox cb : cb_skillType) {
                    if (cb.isEnabled()) {
                        cb.setSelected(quickSelectMode);
                    }
                }
            }
        });

        // ========================= keyword filtering =========================
        labelKeyword = new JLabel(CardUtil.getTranslation("filter.keyword"), JLabel.LEFT);
        labelKeyword.setFont(CRboldEXLargeFilter);
        componentFontMap.put(labelKeyword, "CRboldEXLargeFilter"); // Store the font type as a String
        keywordLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // Wrap the label
        keywordLabelPanel.add(labelKeyword);
        filterPanel.add(keywordLabelPanel);

        keywordOuterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // Wrap the grid
        filterPanel.add(keywordOuterPanel);
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
        filterPanel.add(statusLabelPanel);

        statusOuterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // Wrap the grid
        filterPanel.add(statusOuterPanel);
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
    	for (JToggleButton cb : cb_color) {
    		cb.setSelected(false);
    	}
    	
    	cb_type_cookie.setSelected(false);
    	cb_type_item.setSelected(false);
    	cb_type_trap.setSelected(false);
        rb_flip_condition[0].setSelected(true);
        rb_flip_condition[1].setSelected(false);
        rb_flip_condition[2].setSelected(false);
        cb_extra.setSelected(false);
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
            cb.setEnabled(rb_flip_condition[1].isSelected());
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
    	
        cardList.removeAll();
        List<Card> currentList = list.getSelectCards(false);
        UIUtil.showDeck(this, cardList, currentList, null, 13, columns, UIUtil.CARD_SIZE_SMALL, 1.0f, (isCollectionMode ? 3 : (Config.DECK_BUILD_FROM_COLLECTION ? 4 : 0)), false);
        if (currentList.size() == 0) {
            filterResults.setText(CardUtil.getTranslation("displaycount.empty"));
            filterResults.setForeground(Color.RED);
        } else {
            filterResults.setText(String.format(CardUtil.getTranslation("displaycount"), currentList.size()));
            filterResults.setForeground(foregroundColor);
        }

        if (secretFeatures[1] && currentList.size() == 1) {
            Card singleCard = currentList.get(0);
            showCard(singleCard);
        }
        
        cardList.revalidate();
        cardList.repaint();
    }
    
    private void updateDeck() {
        deckList.removeAll();
        UIUtil.showDeck(this, deckList, currentDeck.getAllCards(), null, 18, columns, UIUtil.CARD_SIZE_SMALL, 1.0f, (Config.DECK_BUILD_FROM_COLLECTION ? 4 : 1), false);

        deckList.revalidate();
        deckList.repaint();
        if (Config.DECK_BUILD_FROM_COLLECTION) {
            cardList.revalidate();
            cardList.repaint();
        }

        int[] cookieSummary = currentDeck.getCookieSummary();
        int[] flipTypeSummary = currentDeck.getFlipTypeSummary();
        int[] otherSummary = currentDeck.getOtherSummary();
        int[] extraSummary = currentDeck.getExtraSummary();

        boolean allReleased = true;
        for (Card card : currentDeck.getAllCards()) {
            if (!CardUtil.CardPackAvailability.get(card.getPack()).get(Config.REGION)) {
                allReleased = false;
                break;
            }
        }

        if ((currentDeck.getCardCount()-extraSummary[0] > 60) || (!currentDeck.getLegality()) || (Config.DECK_BUILD_FROM_COLLECTION && !currentDeck.getOwnershipLegality().isEmpty()) || !allReleased || (cookieSummary[0] == currentDeck.getSpecialPlayCount() && cookieSummary[0] > 0)) {
        	cardCountTxt.setForeground(Color.RED);
            cardCountTxt.setText("<html><u>" + (currentDeck.getCardCount()-extraSummary[0]) + "/60</u></html>");
            String invalidReasonString = ((currentDeck.getCardCount()-extraSummary[0] > 60) ? CardUtil.getTranslation("warning.overlimit") : "");
            invalidReasonString = invalidReasonString + (!currentDeck.getLegality() ? ((invalidReasonString.isEmpty()) ? CardUtil.getTranslation("warning.bannedoverlimit") : "<br>" + CardUtil.getTranslation("warning.bannedoverlimit")) : "");
            invalidReasonString = invalidReasonString + (!allReleased ? ((invalidReasonString.isEmpty()) ? CardUtil.getTranslation("warning.unreleased") : "<br>" + CardUtil.getTranslation("warning.unreleased")) : "");
            invalidReasonString = invalidReasonString + (cookieSummary[0] == currentDeck.getSpecialPlayCount() && cookieSummary[0] > 0 ? ((invalidReasonString.isEmpty()) ? CardUtil.getTranslation("warning.allspecialplay") : "<br>" + CardUtil.getTranslation("warning.allspecialplay")) : "");
            if (Config.DECK_BUILD_FROM_COLLECTION && !currentDeck.getOwnershipLegality().isEmpty()) {
                invalidReasonString = invalidReasonString + ((invalidReasonString.isEmpty()) ? CardUtil.getTranslation("warning.collectionoverlimit") : "<br>" + CardUtil.getTranslation("warning.collectionoverlimit"));
                for (Card entry : currentDeck.getOwnershipLegality()) {
                    invalidReasonString = invalidReasonString + "<br>- " + entry.getId() + " " + entry.getName();
                }
            }

            //System.out.println(mDeck.getOwnershipLegality());
            cardCountTxt.setToolTipText("<html>" + invalidReasonString + "</html>");
        } else {
            cardCountTxt.setText(currentDeck.getCardCount()-extraSummary[0]+"/60");
        	cardCountTxt.setForeground(foregroundColor);
            cardCountTxt.setToolTipText(null);
        }

        
        if (currentDeck.getFlipCount() > 16) {
            flipCountTxt.setText("<html><u>" + currentDeck.getFlipCount() + "/16</u></html>");
        	flipCountTxt.setForeground(Color.RED);
            flipCountTxt.setToolTipText("<html>" + CardUtil.getTranslation("warning.flipoverlimit") + "</html>");
        } else {
            flipCountTxt.setText(currentDeck.getFlipCount()+"/16");
        	flipCountTxt.setForeground(foregroundColor);
            flipCountTxt.setToolTipText(null);
        }

        if (currentDeck.getExtraSummary()[0] > 6) {
            extraCountTxt.setText("<html><u>" + currentDeck.getExtraSummary()[0] + "/6</u></html>");
        	extraCountTxt.setForeground(Color.RED);
            extraCountTxt.setToolTipText("<html>" + CardUtil.getTranslation("warning.extraoverlimit") + "</html>");
        } else {
            extraCountTxt.setText(currentDeck.getExtraSummary()[0]+"/6");
        	extraCountTxt.setForeground(foregroundColor);
            extraCountTxt.setToolTipText(null);
        }

        deckCookieSummaryTxt.setText(String.valueOf(cookieSummary[0]));
        
        levelCountTxt.setText("<html>"+CardUtil.getTranslation("deck.lv1")+" "+cookieSummary[1]+"<br>"+
                CardUtil.getTranslation("deck.lv2")+" "+cookieSummary[2]+"<br>"+
                CardUtil.getTranslation("deck.lv3")+" "+cookieSummary[3]+"<br>"+
                //CardUtil.getTranslation("deck.lv4")+" "+cookieSummary[4]+"<br>"+
                CardUtil.getTranslation("deck.lv5")+" "+cookieSummary[5]+"</html>");

        flipTypeCountTxt.setText("<html>"+CardUtil.getTranslation("flip.heal")+": "+flipTypeSummary[0]+"<br>"+
                CardUtil.getTranslation("flip.draw")+": "+flipTypeSummary[1]+"<br>"+
                CardUtil.getTranslation("flip.special")+": "+flipTypeSummary[2]+"</html>");
        
        deckItemTxt.setText(String.valueOf(otherSummary[0]));
        deckTrapTxt.setText(String.valueOf(otherSummary[1]));
        deckStageTxt.setText(String.valueOf(otherSummary[2]));

        updateDeckDistribution();

        CardColor dominantColor = currentDeck.getDominantDeckColor();
        deckPanelLabel.setBackground(dominantColor.getAccentColor());
        deckPanelLabel.setForeground(dominantColor.getForegroundColor());
    }

    @Override
    public void addCard(Card card) {
        System.out.println("addCard : "+card.getName());
        if (currentDeck.addCard(card)) {
            currentDeck.sort();
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
        if (currentDeck.removeCard(card)) {
            currentDeck.sort();
            updateDeck();
            for (ClickableCardPanel panel : card.getPanels()) {
                panel.updateCountsForCardList();
            }
        }
        deckChanged = true;
    }

    @Override
    public void showCard(Card card) {
        currentCard = card;
        int displayVariant = getDisplayVariant(card);
        System.out.println("showCard : "+card.getName() + " variant: " + displayVariant);
        cardDetailView.showCard(card, displayVariant);
    }

	@Override
	public void onSortConfigChanged() {
        currentDeck.sort();
        updateDeck();
	}

    @Override
    public void onLanguageChange() {
        isCollectionMode = false;

        for (int i=0; i<secretFeatures.length; i++) {
            secretFeatures[i] = false;
        }
        updateTitle();
        
        // Reload fonts and translations
        loadFont();
        button_collection.setSelected(false);
        button_collection.setText(CardUtil.getTranslation("collectionedit.enable"));

        for(int i=0; i<CardUtil.CardPack.size(); i++) {
        	if (!CardUtil.CardPackAvailability.get(CardUtil.CardPack.get(i)).get(Config.REGION)) {
                cb_pack[i].setEnabled(false);
                cb_pack[i].setSelected(false);
            } else {
                cb_pack[i].setEnabled(true);
            }
        }

        // Update all components with the new translations
        frame.setTitle(CardUtil.getTranslation("app.title") + " v." + Constant.VERSION + " | Bundle v." + Constant.DATA_VERSION);
        searchBox.setText("");
        labelSearch.setText(CardUtil.getTranslation("search.name"));
        searchBtn.setText(CardUtil.getTranslation("search"));
        clearFilterBtn.setText(CardUtil.getTranslation("clear"));
        controlsHint.setText("<html><img src=\"file:" + new File(AppPaths.dataDir().resolve("keyicons/24px/space.png").toString()).getAbsolutePath() + "\"> + <img src=\"file:" + new File(AppPaths.dataDir().resolve("keyicons/24px/mousewheel.png").toString()).getAbsolutePath() + "\">:&nbsp;" + CardUtil.getTranslation("hint.quickedit") + "</html>");
        
        deckPanelLabel.setText(CardUtil.getTranslation("deck"));
        cardListPanelLabel.setText(CardUtil.getTranslation("cardlist"));
        clearDeckBtn.setText(CardUtil.getTranslation("deck.clear"));
        randomDrawSimBtn.setText(CardUtil.getTranslation("deck.drawsim"));
        cardCountHintTxt.setText(CardUtil.getTranslation("deck.cards"));
        flipCountHintTxt.setText(CardUtil.getTranslation("deck.flip"));
        extraCountHintTxt.setText(CardUtil.getTranslation("deck.extra"));
        deckCookieSummaryHintTxt.setText(CardUtil.getTranslation("deck.cookies"));
        deckItemHintTxt.setText(CardUtil.getTranslation("deck.items"));
        deckTrapHintTxt.setText(CardUtil.getTranslation("deck.traps"));
        deckStageHintTxt.setText(CardUtil.getTranslation("deck.stages"));
        saveBtn.setToolTipText(CardUtil.getTranslation("save"));
        saveAsBtn.setToolTipText(CardUtil.getTranslation("saveas"));
        selectBtn.setToolTipText(CardUtil.getTranslation("select.file"));
        showDeckBtn.setToolTipText(CardUtil.getTranslation("deck.show"));
        showDeckDifferentialBtn.setToolTipText(CardUtil.getTranslation("deck.compare"));

        ImageIcon saveIcon = cardIcon = new ImageIcon(AppPaths.dataDir().resolve("icons_ui/save_" + Config.THEME + ".png").toString());
        ImageIcon saveAsIcon = cardIcon = new ImageIcon(AppPaths.dataDir().resolve("icons_ui/saveas_" + Config.THEME + ".png").toString());
        ImageIcon selectIcon = cardIcon = new ImageIcon(AppPaths.dataDir().resolve("icons_ui/load_" + Config.THEME + ".png").toString());
        ImageIcon showDeckIcon = cardIcon = new ImageIcon(AppPaths.dataDir().resolve("icons_ui/show_" + Config.THEME + ".png").toString());
        ImageIcon showDeckDifferentialIcon = cardIcon = new ImageIcon(AppPaths.dataDir().resolve("icons_ui/compare_" + Config.THEME + ".png").toString());
        saveBtn.setIcon(saveIcon);
        saveAsBtn.setIcon(saveAsIcon);
        selectBtn.setIcon(selectIcon);
        showDeckBtn.setIcon(showDeckIcon);
        showDeckDifferentialBtn.setIcon(showDeckDifferentialIcon);

        quickSelectBtnBS.setText(CardUtil.getTranslation("filter.BS"));
        quickSelectBtnST.setText(CardUtil.getTranslation("filter.ST"));
        quickSelectBtnSkillType.setText(CardUtil.getTranslation("filter.skilltypetoggle"));
        labelColor.setText(CardUtil.getTranslation("color"));
        for (int i = 0; i < CardUtil.COLOR_MAX; i++) {
            cb_color[i].setToolTipText(CardUtil.CardColor.fromValue(i).getDisplayName());
        }
        labelType.setText(CardUtil.getTranslation("type"));
        cb_type_cookie.setText(CardUtil.getTranslation("filter.cookie"));
        labelFlip.setText(CardUtil.getTranslation("filter.flip") + ":");
        String[] flipConditions = {"include", "only", "exclude"};
        for (int i = 0; i < 3; i++) {
            rb_flip_condition[i].setText(CardUtil.getTranslation("filter.flip." + flipConditions[i]));
        }
        cb_flipType[0].setText(CardUtil.getTranslation("flip.heal"));
        cb_flipType[1].setText(CardUtil.getTranslation("flip.draw"));
        cb_flipType[2].setText(CardUtil.getTranslation("flip.special"));
        cb_extra.setText(CardUtil.getTranslation("filter.extra"));
        cb_specialplay.setText(CardUtil.SkillType.fromValue(CardUtil.SKILL_TYPE_MAX - 1).getDisplayName());
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
        collectionChangeLabel.setText(CardUtil.getTranslation("collection.change") + " +" + collectionChangeValue[0] + " -" + collectionChangeValue[1]);

        // Set visibility depending on advanced filtering option
        cb_variant_sec.setVisible(Config.ADVANCED_FILTERING);
        cb_variant_promo.setVisible(Config.ADVANCED_FILTERING);
        keywordLabelPanel.setVisible(Config.ADVANCED_FILTERING);
        keywordOuterPanel.setVisible(Config.ADVANCED_FILTERING);
        skillTypeBtnPanel.setVisible(Config.ADVANCED_FILTERING);
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
        sortSettingsBtn.setText(CardUtil.getTranslation("sort.settings"));
        settingsBtn.setText(CardUtil.getTranslation("settings"));
        if (filterPanelOuter.isVisible()) {
            hideSearchPaneBtn.setText("<< " + CardUtil.getTranslation("filter"));
        } else {
            hideSearchPaneBtn.setText(">> " + CardUtil.getTranslation("filter"));
        }
        if (cardPreviewPanel.isVisible()) {
            hidePreviewPaneBtn.setText(">> " + CardUtil.getTranslation("preview.popout"));
        } else {
            hidePreviewPaneBtn.setText("<< " + CardUtil.getTranslation("preview.sidebar"));
        }

        cardDetailView.clearCard();

        updateComponents(frame.getContentPane());
        cardDetailView.clearPanel();

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

        cardPreviewPanel.setPreferredSize(new Dimension(Config.CARD_PREVIEW_WIDTH, (int) frame.getBounds().getHeight()));
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

    private void updateDeckDistribution() {

        deckDistributionPanel.removeAll();

        int[] cookieSummary = currentDeck.getCookieSummary(false, false);
        int flipCount = currentDeck.getFlipCount();
        int[] flipTypeSummary = currentDeck.getFlipTypeSummary();
        int[] otherSummary = currentDeck.getOtherSummary();
        int[] extraSummary = currentDeck.getExtraSummary();
        int cardCount = currentDeck.getCardCount() - extraSummary[0]; // Excludes EXTRA cards
        //int extraCount = mDeck.getExtraCount();
        
        GridBagConstraints gbc_deckdist = new GridBagConstraints();
        //gbc_deckdist.insets = new Insets(5, 5, 5, 5);
        gbc_deckdist.fill = GridBagConstraints.BOTH;
        gbc_deckdist.gridy = 0;

        gbc_deckdist.gridx = 0;
        gbc_deckdist.weightx = cookieSummary[1];
        if (cookieSummary[1] > 0) {
            deckDistCookie1 = new JLabel(" " + String.valueOf(cookieSummary[1]));
            deckDistCookie1.setFont(CRboldSmall);
            deckDistCookie1.setOpaque(true);
            deckDistCookie1.setToolTipText(CardUtil.getTranslation("deck.distribution.lv1") + " " + cookieSummary[1]);
            deckDistCookie1.setBackground(new Color(135, 223, 255));
            deckDistCookie1.setForeground(Color.BLACK);
            componentFontMap.put(deckDistCookie1, "CRboldSmall"); // Store the font type as a String
            deckDistributionPanel.add(deckDistCookie1, gbc_deckdist);
        }

        gbc_deckdist.gridx = cookieSummary[1];
        gbc_deckdist.weightx = cookieSummary[2];
        if (cookieSummary[2] > 0) {
            deckDistCookie2 = new JLabel(" " + String.valueOf(cookieSummary[2]));
            deckDistCookie2.setFont(CRboldSmall);
            deckDistCookie2.setOpaque(true);
            deckDistCookie2.setToolTipText(CardUtil.getTranslation("deck.distribution.lv2") + " " + cookieSummary[2]);
            deckDistCookie2.setBackground(new Color(135, 193, 255));
            deckDistCookie2.setForeground(Color.BLACK);
            componentFontMap.put(deckDistCookie2, "CRboldSmall"); // Store the font type as a String
            deckDistributionPanel.add(deckDistCookie2, gbc_deckdist);
        }

        gbc_deckdist.gridx = cookieSummary[1] + cookieSummary[2];
        gbc_deckdist.weightx = cookieSummary[3];
        if (cookieSummary[3] > 0) {
            deckDistCookie3 = new JLabel(" " + String.valueOf(cookieSummary[3]));
            deckDistCookie3.setFont(CRboldSmall);
            deckDistCookie3.setOpaque(true);
            deckDistCookie3.setToolTipText(CardUtil.getTranslation("deck.distribution.lv3") + " " + cookieSummary[3]);
            deckDistCookie3.setBackground(new Color(135, 163, 255));
            deckDistCookie3.setForeground(Color.BLACK);
            componentFontMap.put(deckDistCookie3, "CRboldSmall"); // Store the font type as a String
            deckDistributionPanel.add(deckDistCookie3, gbc_deckdist);
        }

        gbc_deckdist.gridx = cookieSummary[1] + cookieSummary[2] + cookieSummary[3];
        gbc_deckdist.weightx = cookieSummary[4];
        if (cookieSummary[4] > 0) {
            deckDistCookie4 = new JLabel(" " + String.valueOf(cookieSummary[4]));
            deckDistCookie4.setFont(CRboldSmall);
            deckDistCookie4.setOpaque(true);
            deckDistCookie4.setToolTipText(CardUtil.getTranslation("deck.distribution.lv4") + " " + cookieSummary[4]);
            deckDistCookie4.setBackground(new Color(135, 133, 255));
            deckDistCookie4.setForeground(Color.BLACK);
            componentFontMap.put(deckDistCookie4, "CRboldSmall"); // Store the font type as a String
            deckDistributionPanel.add(deckDistCookie4, gbc_deckdist);
        }

        gbc_deckdist.gridx = cookieSummary[1] + cookieSummary[2] + cookieSummary[3] + cookieSummary[4];
        gbc_deckdist.weightx = cookieSummary[5];
        if (cookieSummary[5] > 0) {
            deckDistCookie5 = new JLabel(" " + String.valueOf(cookieSummary[5]));
            deckDistCookie5.setFont(CRboldSmall);
            deckDistCookie5.setOpaque(true);
            deckDistCookie5.setToolTipText(CardUtil.getTranslation("deck.distribution.lv5") + " " + cookieSummary[5]);
            deckDistCookie5.setBackground(new Color(135, 103, 255));
            deckDistCookie5.setForeground(Color.BLACK);
            componentFontMap.put(deckDistCookie5, "CRboldSmall"); // Store the font type as a String
            deckDistributionPanel.add(deckDistCookie5, gbc_deckdist);
        }

        gbc_deckdist.gridx = cookieSummary[0];
        gbc_deckdist.weightx = flipTypeSummary[0];
        if (flipTypeSummary[0] > 0) {
            deckDistFlipHeal = new JLabel(" " + String.valueOf(flipTypeSummary[0]));
            deckDistFlipHeal.setFont(CRboldSmall);
            deckDistFlipHeal.setOpaque(true);
            deckDistFlipHeal.setToolTipText(CardUtil.getTranslation("deck.distribution.flipheal") + " " + flipTypeSummary[0]);
            deckDistFlipHeal.setBackground(new Color(255, 235, 84));
            deckDistFlipHeal.setForeground(Color.BLACK);
            componentFontMap.put(deckDistFlipHeal, "CRboldSmall"); // Store the font type as a String
            deckDistributionPanel.add(deckDistFlipHeal, gbc_deckdist);
        } 

        gbc_deckdist.gridx = cookieSummary[0] + flipTypeSummary[0];
        gbc_deckdist.weightx = flipTypeSummary[1];
        if(flipTypeSummary[1] > 0) {
            deckDistFlipDraw = new JLabel(" " + String.valueOf(flipTypeSummary[1]));
            deckDistFlipDraw.setFont(CRboldSmall);
            deckDistFlipDraw.setOpaque(true);
            deckDistFlipDraw.setToolTipText(CardUtil.getTranslation("deck.distribution.flipdraw") + " " + flipTypeSummary[1]);
            deckDistFlipDraw.setBackground(new Color(255, 205, 84));
            deckDistFlipDraw.setForeground(Color.BLACK);
            componentFontMap.put(deckDistFlipDraw, "CRboldSmall"); // Store the font type as a String
            deckDistributionPanel.add(deckDistFlipDraw, gbc_deckdist);
        }

        gbc_deckdist.gridx = cookieSummary[0] + flipTypeSummary[0] + flipTypeSummary[1];
        gbc_deckdist.weightx = flipTypeSummary[2];
        if (flipTypeSummary[2] > 0) {
            deckDistFlipSpecial = new JLabel(" " + String.valueOf(flipTypeSummary[2]));
            deckDistFlipSpecial.setFont(CRboldSmall);
            deckDistFlipSpecial.setOpaque(true);
            deckDistFlipSpecial.setToolTipText(CardUtil.getTranslation("deck.distribution.flipspecial") + " " + flipTypeSummary[2]);
            deckDistFlipSpecial.setBackground(new Color(255, 175, 84));
            deckDistFlipSpecial.setForeground(Color.BLACK);
            componentFontMap.put(deckDistFlipSpecial, "CRboldSmall"); // Store the font type as a String
            deckDistributionPanel.add(deckDistFlipSpecial, gbc_deckdist);
        }

        gbc_deckdist.gridx = cookieSummary[0] + flipTypeSummary[0] + flipTypeSummary[1] + flipTypeSummary[2];
        gbc_deckdist.weightx = otherSummary[0];
        if (otherSummary[0] > 0) {
            deckDistItem = new JLabel(" " + String.valueOf(otherSummary[0]));
            deckDistItem.setFont(CRboldSmall);
            deckDistItem.setOpaque(true);
            deckDistItem.setToolTipText(CardUtil.getTranslation("deck.distribution.item") + " " + otherSummary[0]);
            deckDistItem.setBackground(new Color(64, 247, 183));
            deckDistItem.setForeground(Color.BLACK);
            componentFontMap.put(deckDistItem, "CRboldSmall"); // Store the font type as a String
            deckDistributionPanel.add(deckDistItem, gbc_deckdist);
        }

        gbc_deckdist.gridx = cookieSummary[0] + flipTypeSummary[0] + flipTypeSummary[1] + flipTypeSummary[2] + otherSummary[0];
        gbc_deckdist.weightx = otherSummary[1];
        if (otherSummary[1] > 0) {
            deckDistTrap = new JLabel(" " + String.valueOf(otherSummary[1]));
            deckDistTrap.setFont(CRboldSmall);
            deckDistTrap.setOpaque(true);
            deckDistTrap.setToolTipText(CardUtil.getTranslation("deck.distribution.trap") + " " + otherSummary[1]);
            deckDistTrap.setBackground(new Color(64, 217, 183));
            deckDistTrap.setForeground(Color.BLACK);
            componentFontMap.put(deckDistTrap, "CRboldSmall"); // Store the font type as a String
            deckDistributionPanel.add(deckDistTrap, gbc_deckdist);
        }

        gbc_deckdist.gridx = cookieSummary[0] + flipTypeSummary[0] + flipTypeSummary[1] + flipTypeSummary[2] + otherSummary[0] + otherSummary[1];
        gbc_deckdist.weightx = otherSummary[2];
        if (otherSummary[2] > 0) {
            deckDistStage = new JLabel(" " + String.valueOf(otherSummary[2]));
            deckDistStage.setFont(CRboldSmall);
            deckDistStage.setOpaque(true);
            deckDistStage.setToolTipText(CardUtil.getTranslation("deck.distribution.stage") + " " + otherSummary[2]);
            deckDistStage.setBackground(new Color(64, 187, 183));
            deckDistStage.setForeground(Color.BLACK);
            componentFontMap.put(deckDistStage, "CRboldSmall"); // Store the font type as a String
            deckDistributionPanel.add(deckDistStage, gbc_deckdist);
        }
        
        gbc_deckdist.gridx = cardCount;
        int emptyCount = 60 - cardCount;
        gbc_deckdist.weightx = emptyCount;
        if (emptyCount > 0) {
            deckDistEmpty = new JLabel(" " + String.valueOf(emptyCount));
            deckDistEmpty.setOpaque(true);
            deckDistEmpty.setForeground(new Color(220, 220, 220));
            deckDistEmpty.setBackground(new Color(220, 220, 220));
            deckDistributionPanel.add(deckDistEmpty, gbc_deckdist);
        }

        gbc_deckdist.gridx = 60;
        gbc_deckdist.weightx = extraSummary[1];
        if (extraSummary[1] > 0) {
            deckDistExtra1 = new JLabel(" " + String.valueOf(extraSummary[1]));
            deckDistExtra1.setFont(CRboldSmall);
            deckDistExtra1.setOpaque(true);
            deckDistExtra1.setToolTipText(CardUtil.getTranslation("deck.distribution.exlv1") + " " + extraSummary[1]);
            deckDistExtra1.setBackground(new Color(255, 103, 178));
            deckDistExtra1.setForeground(Color.WHITE);
            componentFontMap.put(deckDistExtra1, "CRboldSmall"); // Store the font type as a String
            deckDistributionPanel.add(deckDistExtra1, gbc_deckdist);
        }

        gbc_deckdist.gridx = 60 + extraSummary[1];
        gbc_deckdist.weightx = extraSummary[2];
        if (extraSummary[2] > 0) {
            deckDistExtra2 = new JLabel(" " + String.valueOf(extraSummary[2]));
            deckDistExtra2.setFont(CRboldSmall);
            deckDistExtra2.setOpaque(true);
            deckDistExtra2.setToolTipText(CardUtil.getTranslation("deck.distribution.exlv2") + " " + extraSummary[2]);
            deckDistExtra2.setBackground(new Color(195, 83, 198));
            deckDistExtra2.setForeground(Color.WHITE);
            componentFontMap.put(deckDistExtra2, "CRboldSmall"); // Store the font type as a String
            deckDistributionPanel.add(deckDistExtra2, gbc_deckdist);
        }

        gbc_deckdist.gridx = 60 + extraSummary[1] + extraSummary[2];
        gbc_deckdist.weightx = extraSummary[3];
        if (extraSummary[3] > 0) {
            deckDistExtra3 = new JLabel(" " + String.valueOf(extraSummary[3]));
            deckDistExtra3.setFont(CRboldSmall);
            deckDistExtra3.setOpaque(true);
            deckDistExtra3.setToolTipText(CardUtil.getTranslation("deck.distribution.exlv3") + " " + extraSummary[3]);
            deckDistExtra3.setBackground(new Color(155, 63, 218));
            deckDistExtra3.setForeground(Color.WHITE);
            componentFontMap.put(deckDistExtra3, "CRboldSmall"); // Store the font type as a String
            deckDistributionPanel.add(deckDistExtra3, gbc_deckdist);
        }

        gbc_deckdist.gridx = 60 + extraSummary[1] + extraSummary[2] + extraSummary[3];
        gbc_deckdist.weightx = extraSummary[4];
        if (extraSummary[4] > 0) {
            deckDistExtra4 = new JLabel(" " + String.valueOf(extraSummary[4]));
            deckDistExtra4.setFont(CRboldSmall);
            deckDistExtra4.setOpaque(true);
            deckDistExtra4.setToolTipText(CardUtil.getTranslation("deck.distribution.exlv4") + " " + extraSummary[4]);
            deckDistExtra4.setBackground(new Color(115, 43, 198));
            deckDistExtra4.setForeground(Color.WHITE);
            componentFontMap.put(deckDistExtra4, "CRboldSmall"); // Store the font type as a String
            deckDistributionPanel.add(deckDistExtra4, gbc_deckdist);
        }

        gbc_deckdist.gridx = 60 + extraSummary[1] + extraSummary[2] + extraSummary[3] + extraSummary[4];
        gbc_deckdist.weightx = extraSummary[5];
        if (extraSummary[5] > 0) {
            deckDistExtra5 = new JLabel(" " + String.valueOf(extraSummary[5]));
            deckDistExtra5.setFont(CRboldSmall);
            deckDistExtra5.setOpaque(true);
            deckDistExtra5.setToolTipText(CardUtil.getTranslation("deck.distribution.exlv5") + " " + extraSummary[5]);
            deckDistExtra5.setBackground(new Color(75, 23, 178));
            deckDistExtra5.setForeground(Color.WHITE);
            componentFontMap.put(deckDistExtra5, "CRboldSmall"); // Store the font type as a String
            deckDistributionPanel.add(deckDistExtra5, gbc_deckdist);
        }

        deckDistributionPanel.revalidate();
        deckDistributionPanel.repaint();
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
            
            collectionChangeLabel.setVisible(true);
            randomDrawSimBtn.setVisible(false);
            cardDetailView.toggleCollectionMode(true);
            
            deckDetailSummaryPanel.setVisible(false);
            deckDistributionPanel.setVisible(false);
            deckPanelLabel.setVisible(false);
            splitPane.setTopComponent(null);
            cardListPanelLabel.setText(CardUtil.getTranslation("collection"));
            clearDeckBtn.setText(CardUtil.getTranslation("collection.summary"));
            controlsHint.setText("<html><img src=\"file:" + new File(AppPaths.dataDir().resolve("keyicons/24px/space.png").toString()).getAbsolutePath() + "\"> + <img src=\"file:" + new File(AppPaths.dataDir().resolve("keyicons/24px/mousewheel.png").toString()).getAbsolutePath() + "\">:&nbsp;" + CardUtil.getTranslation("hint.quickedit") + "&nbsp;<img src=\"file:" + new File(AppPaths.dataDir().resolve("keyicons/24px/backtick.png").toString()).getAbsolutePath() + "\">:&nbsp;" + CardUtil.getTranslation("hint.langswitch")+ "</html>");
            splitPane.setResizeWeight(0.0);
            splitPane.setDividerSize(0);
            splitPane.setEnabled(false);
            cardDetailView.updateLangLabels();
            cardDetailView.enableOwnedInfoLabels();
            //mFileOpPane.setVisible(false);
            for (Component comp : fileOperationsPanel.getComponents()) {
                if (comp instanceof JButton) {
                    comp.setEnabled(false);
                }
            }
            updateCardListForCollection();
            splitPane.revalidate();
            splitPane.repaint();
            javax.swing.SwingUtilities.invokeLater(() -> {
                splitPane.setDividerLocation(0); // topmost
                splitPane.revalidate();
                splitPane.repaint();
            });
        } else {
            collectionChangeLabel.setVisible(false);
            collectionChangeValue = new int[]{0, 0};
            collectionChangeLabel.setText(CardUtil.getTranslation("collection.change") + " +" + collectionChangeValue[0] + " -" + collectionChangeValue[1]);
            splitPane.setTopComponent(deckPanel);
            randomDrawSimBtn.setVisible(true);
            cardDetailView.toggleCollectionMode(false);
            deckDetailSummaryPanel.setVisible(true);
            deckDistributionPanel.setVisible(true);
            deckPanelLabel.setVisible(true);
            cardListPanelLabel.setText(CardUtil.getTranslation("cardlist"));
            clearDeckBtn.setText(CardUtil.getTranslation("deck.clear"));
            controlsHint.setText("<html><img src=\"file:" + new File(AppPaths.dataDir().resolve("keyicons/24px/space.png").toString()).getAbsolutePath() + "\"> + <img src=\"file:" + new File(AppPaths.dataDir().resolve("keyicons/24px/mousewheel.png").toString()).getAbsolutePath() + "\">:&nbsp;" + CardUtil.getTranslation("hint.quickedit") + "</html>");
            splitPane.setDividerSize(8);
            splitPane.setEnabled(true);
            splitPane.setResizeWeight(0.5);
            splitPane.setDividerLocation(divLoc);

            collection.saveCollection();
            cardDetailView.clearLabels();
            //mFileOpPane.setVisible(true);
            for (Component comp : fileOperationsPanel.getComponents()) {
                if (comp instanceof JButton) {
                    comp.setEnabled(true);
                }
            }
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
            int displayVariant = getDisplayVariant(card);
            cardDetailView.updateCardOwnedInfoLabel(card, displayVariant);
            int newCount = collection.getCardOwnedCount(Config.COLLECTION_LANGUAGE_INDICES[currentSelectedCardLanguage], card.getId(), displayVariant) + 1;
            collection.setCardOwnedCount(Config.COLLECTION_LANGUAGE_INDICES[currentSelectedCardLanguage], card.getId(), displayVariant, newCount);
            collection.setCardChangeCount(card.getId(), (collection.getCardTotalChangeCount(card.getId())+1));
            updateCardListForCollection(); // Refresh the card list to show the updated count
            cardDetailView.updateCardOwnedInfoLabel(card, displayVariant);
            cardDetailView.updateCardOwnedInfoHighlight(displayVariant);
            collectionChangeValue = collection.getTotalCollectionChange();
            collectionChangeLabel.setText(CardUtil.getTranslation("collection.change") + " +" + collectionChangeValue[0] + " -" + collectionChangeValue[1]);
        }

        @Override
        public void removeCard(Card card) {
            // Decrement the collection count
            int displayVariant = getDisplayVariant(card);
            cardDetailView.updateCardOwnedInfoLabel(card, displayVariant);
            int newCount = collection.getCardOwnedCount(Config.COLLECTION_LANGUAGE_INDICES[currentSelectedCardLanguage], card.getId(), displayVariant) - 1;
            collection.setCardOwnedCount(Config.COLLECTION_LANGUAGE_INDICES[currentSelectedCardLanguage], card.getId(), displayVariant, newCount);
            if (newCount >= 0) {
                collection.setCardChangeCount(card.getId(), (collection.getCardTotalChangeCount(card.getId())-1));
            }
            updateCardListForCollection(); // Refresh the card list to show the updated count
            cardDetailView.updateCardOwnedInfoLabel(card, displayVariant);
            cardDetailView.updateCardOwnedInfoHighlight(displayVariant);
            collectionChangeValue = collection.getTotalCollectionChange();
            collectionChangeLabel.setText(CardUtil.getTranslation("collection.change") + " +" + collectionChangeValue[0] + " -" + collectionChangeValue[1]);
        }

        @Override
        public void showCard(Card card) {
            currentCard = card;
            int displayVariant = getDisplayVariant(card);
            cardDetailView.showCard(card, displayVariant);
        }
    }

    private final class Filter {
        public boolean[] color = new boolean[CardUtil.COLOR_MAX];
        public boolean[] type = new boolean[CardUtil.TYPE_MAX];
        public boolean[] level = new boolean[CardUtil.LEVEL_MAX + 1];
        public int flip = 0; // 0: all, 1: flip only, 2: non-flip only
        public boolean[] flipType = new boolean[3];
        public boolean extra = false;
        public boolean specialPlay = false;
        public boolean[] rarity = new boolean[CardUtil.RARITY_MAX];
        public boolean[] variants = new boolean[2];
        public boolean[] hp = new boolean[CardUtil.HP_MAX + 1];
        public boolean[] hpAwaken = new boolean[CardUtil.AWAKEN_HP.size()];
        public boolean[] skillType = new boolean[CardUtil.SKILL_TYPE_MAX - 1];
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
            this.flip = 0;
            if (rb_flip_condition[1].isSelected()) {
                this.flip = 1;
            } else if (rb_flip_condition[2].isSelected()) {
                this.flip = 2;
            }
            for (int i=0; i< cb_flipType.length; i++) {
                this.flipType[i] = cb_flipType[i].isSelected();
            }
            this.extra = cb_extra.isSelected();
            this.specialPlay = cb_specialplay.isSelected();
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
            this.flip = 0;
            for (int i=0; i<this.flipType.length; i++) {
                this.flipType[i] = false;
            }
            this.extra = false;
            this.specialPlay = false;
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
            list.setSkillType(CardUtil.SKILL_TYPE_MAX - 1, this.specialPlay); // 9 is special play (hard coded)
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
        cardList.removeAll();
        CardList list = CardList.getInstance();
        List<Card> filteredCards = list.getSelectCards(true); // Ignore ownership for collection mode view

        UIUtil.showDeck(new CollectionModeCallback(), cardList, filteredCards, null, 13, columns, UIUtil.CARD_SIZE_SMALL, 1.0f, 3, false);
        
        for (Card card : filteredCards) {
            for (ClickableCardPanel panel : card.getPanels()) {
                panel.updateCountsForCardList();
            }
        }
        
        cardList.revalidate();
        cardList.repaint();
    }

    /*
    private void updateCardPreview() {
        int displayVariant = getDisplayVariant(currentCard);
        if (isCollectionMode) {
            for (String lang : Config.FALLBACK_ORDER) {
                if (displayVariant == 0) {
                    cardIcon = new ImageIcon(AppPaths.dataDir().resolve("cards/" + lang + "/" + currentCard.getPack() + "/" + currentCard.getId() + ".png").toString());
                } else {
                    cardIcon = new ImageIcon(AppPaths.dataDir().resolve("cards_variant/" + lang + "/" + currentCard.getPack() + "/" + currentCard.getId() + "@" + displayVariant + ".png").toString());
                }
                if (cardIcon.getIconWidth() > 0) {
                    break;
                }
            }
            cardLabel.setIcon(new ImageIcon(cardIcon.getImage().getScaledInstance((int) (previewHeight / Config.CARD_RATIO), previewHeight, java.awt.Image.SCALE_SMOOTH)));
            if (currentCard.getAltNames().size() > 0) {
                if (displayVariant == 0) {
                    cardName.setText(currentCard.getName());
                    cardName.setForeground(foregroundColor);
                } else {
                    List<String> altNamesForVariant = currentCard.getAltNames().get(displayVariant-1);
                    if (altNamesForVariant != null && altNamesForVariant.size() > Config.getLangIndex(Config.LANGUAGE)) {
                        cardName.setText(altNamesForVariant.get(Config.getLangIndex(Config.LANGUAGE)));
                        cardName.setForeground(highlightColor);
                    } else {
                        cardName.setText(currentCard.getName());
                        cardName.setForeground(foregroundColor);
                    }
                }
            } else {
                cardName.setText(currentCard.getName());
                cardName.setForeground(foregroundColor);
            }
            mCardDetailPane.revalidate();
            mCardDetailPane.repaint();
        }
    } */

    public static boolean isCollectionMode() {
        return isCollectionMode;
    }

    public static void updateTheme() {
        Config.loadConfig();
        if (Config.THEME.equals("dark")) {
            FlatDarkLaf.setup();
            try {
                UIManager.setLookAndFeel(new FlatDarkLaf());
                foregroundColor = UIManager.getColor("Label.foreground");
                extraColor = new Color(191, 134, 209, 255);
                highlightColor = new Color(150, 150, 255,255);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            FlatLightLaf.setup();
            try {
                UIManager.setLookAndFeel(new FlatLightLaf());
                foregroundColor = UIManager.getColor("Label.foreground");
                extraColor = new Color(110, 36, 133, 255);
                highlightColor = new Color(60, 60, 255,255);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            for (Frame frame : Frame.getFrames()) {
                SwingUtilities.updateComponentTreeUI(frame);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void togglePreviewHost() {
        if (previewPopout) {
            cardDetailWindow.setVisible(false);
            cardPreviewHost.remove(cardDetailView);
            cardPreviewHost.add(cardDetailView, BorderLayout.CENTER);
            cardPreviewHost.revalidate();
            cardPreviewHost.repaint();
            previewPopout = false;
        } else {
            cardPreviewHost.remove(cardDetailView);
            cardDetailWindow.setPreviewComponent(cardDetailView);
            cardDetailWindow.showWindow();
            cardPreviewHost.revalidate();
            cardPreviewHost.repaint();
            previewPopout = true;
        }
    }
}