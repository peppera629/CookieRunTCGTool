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
import ui.SettingsWindow.ConfigChangedCallback;
import util.CardUtil.CardColor;
import util.CardUtil.CardType;
import util.CardUtil;
import util.Config;
import util.Constant;
import util.DefaultState;
import util.UIUtil;

import java.awt.event.ActionListener;
import java.io.File;
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

import javax.swing.JButton;

public class MainUI implements CardListCallBack, ConfigChangedCallback {

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
		deckWindow = new DeckWindow();
		settingsWindow = new SettingsWindow();
		settingsWindow.setConfigChangedCallback(this);
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */

	private DeckWindow deckWindow;
	private SettingsWindow settingsWindow;
    private DefaultState mDefaultState;
    private JPanel mCardsPane, mDeckPane, mTextsPane;
    
    //search panel
    private Panel mSearchPane;
    private JCheckBox[] cb_color;
    private JCheckBox[] cb_level;
    private JCheckBox[] cb_pack;
    private JCheckBox cb_type_cookie, cb_type_item, cb_type_trap, cb_type_stage;
    private JCheckBox cb_flip, cb_extra;
//    private JCheckBox cb_version_BS1, cb_version_ST1, cb_version_ST2, cb_version_ST3;
    private JLabel label_special, label_version;

    private Deck mDeck;
    private ScrollPane scrollPane;
    private Panel mCardDetailPane;
    private Panel panel;
    private TextField mDeckText;
    private JButton loadBtn, saveBtn, selectBtn;
    private JButton mClearDeckBtn;
    private JLabel mCardCountHintTxt, mFlipCountHintTxt, mDeckCookieSummaryHintTxt, mDeckCookieLv1HintTxt, mDeckCookieLv2HintTxt, mDeckCookieLv3HintTxt;
    private JLabel mDeckItemHintTxt, mDeckTrapHintTxt, mDeckStageHintTxt;
    private JLabel mCardCountTxt, mFlipCountTxt, mDeckCookieSummaryTxt, mDeckCookieLv1Txt, mDeckCookieLv2Txt, mDeckCookieLv3Txt;
    private JLabel mDeckItemTxt, mDeckTrapTxt, mDeckStageTxt;
    private JButton showDeckBtn;
    private Font CRnormal, CRbold;

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
    
    private void initialUI() {
        
        frame.setTitle("薑餅人組牌系統   V "+Constant.VERSION);
        frame.setBounds(100, 100, 1080, 720);
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
        
        JButton button_search = new JButton("搜尋");
        searchPanelButtons.add(button_search);
        button_search.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	updateCardList();
                mDefaultState.saveDefaultState();
            }
        });
        
        JButton button_clean = new JButton("清除");
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
        centerPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; // Single column
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;

        // ==== 卡組
        gbc.gridy = 0; // First row
        gbc.weighty = 0.45; // Take 45% of the remaining vertical space
        mDeckPane = new JPanel();
        mDeckPane.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        JScrollPane scrollDeckPane = new JScrollPane(mDeckPane);
        scrollDeckPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollDeckPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JScrollBar deckScrollBar = scrollDeckPane.getVerticalScrollBar();
        deckScrollBar.setUnitIncrement(16);
        centerPanel.add(scrollDeckPane, gbc);

        // ==== 卡組資訊
        gbc.gridy = 1; // Second row
        gbc.weighty = 0.1; // Fixed proportion of vertical space (10%)
        JPanel deckDetailPane = new JPanel();
        deckDetailPane.setLayout(new BorderLayout());
        centerPanel.add(deckDetailPane, gbc);

        mClearDeckBtn = new JButton("清除卡組");
        deckDetailPane.add(mClearDeckBtn, BorderLayout.SOUTH);

        mTextsPane = new JPanel();
        mTextsPane.setLayout(new GridLayout(2, 9));
        deckDetailPane.add(mTextsPane, BorderLayout.CENTER);

        mCardCountHintTxt = new JLabel("牌組:");
        mTextsPane.add(mCardCountHintTxt);

        mFlipCountHintTxt = new JLabel("FLIP卡:");
        mTextsPane.add(mFlipCountHintTxt);

        mDeckCookieSummaryHintTxt = new JLabel("餅乾:");
        mTextsPane.add(mDeckCookieSummaryHintTxt);

        mDeckCookieLv1HintTxt = new JLabel("Lv.1:");
        mTextsPane.add(mDeckCookieLv1HintTxt);

        mDeckCookieLv2HintTxt = new JLabel("Lv.2:");
        mTextsPane.add(mDeckCookieLv2HintTxt);

        mDeckCookieLv3HintTxt = new JLabel("Lv.3:");
        mTextsPane.add(mDeckCookieLv3HintTxt);

        mDeckItemHintTxt = new JLabel("道具:");
        mTextsPane.add(mDeckItemHintTxt);

        mDeckTrapHintTxt = new JLabel("陷阱:");
        mTextsPane.add(mDeckTrapHintTxt);

        mDeckStageHintTxt = new JLabel("場地:");
        mTextsPane.add(mDeckStageHintTxt);

        mCardCountTxt = new JLabel("0/60");
        mTextsPane.add(mCardCountTxt);

        mFlipCountTxt = new JLabel("0/16");
        mTextsPane.add(mFlipCountTxt);

        mDeckCookieSummaryTxt = new JLabel("0");
        mTextsPane.add(mDeckCookieSummaryTxt);

        mDeckCookieLv1Txt = new JLabel("0");
        mTextsPane.add(mDeckCookieLv1Txt);

        mDeckCookieLv2Txt = new JLabel("0");
        mTextsPane.add(mDeckCookieLv2Txt);

        mDeckCookieLv3Txt = new JLabel("0");
        mTextsPane.add(mDeckCookieLv3Txt);

        mDeckItemTxt = new JLabel("0");
        mTextsPane.add(mDeckItemTxt);

        mDeckTrapTxt = new JLabel("0");
        mTextsPane.add(mDeckTrapTxt);

        mDeckStageTxt = new JLabel("0");
        mTextsPane.add(mDeckStageTxt);

        // ==== 卡片列表
        gbc.gridy = 2; // Third row
        gbc.weighty = 0.45; // Take the remaining 45% of the vertical space
        mCardsPane = new JPanel();
        mCardsPane.setLayout(new GridLayout(0, 4, 5, 5));
        
        JScrollPane scrollCardsPane = new JScrollPane(mCardsPane);
        scrollCardsPane.setBackground(new Color(255, 255, 255));
        scrollCardsPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollCardsPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JScrollBar cardListScrollBar = scrollCardsPane.getVerticalScrollBar();
        cardListScrollBar.setUnitIncrement(16);
        centerPanel.add(scrollCardsPane, gbc);
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
        
        loadBtn = new JButton("讀取");
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
        
        saveBtn = new JButton("儲存");
        panel.add(saveBtn);
        saveBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CardLoader.saveDeck(mDeckText.getText(), mDeck);
                mDefaultState.setDefaultDeckName(mDeckText.getText());
                mDefaultState.saveDefaultState();
            }
        });
        
        selectBtn = new JButton("選擇檔案");
        selectBtn.setActionCommand("選擇檔案");
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
        
        showDeckBtn = new JButton("展示完整卡表");
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
        JLabel labelColor = new JLabel("顏色", JLabel.LEFT);
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
        JLabel label_1 = new JLabel("卡片類型", JLabel.CENTER);
        mSearchPane.add(label_1);

        JPanel typeOuterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // Wrap the grid
        JPanel typeCheckboxGroup = new JPanel();
        typeCheckboxGroup.setLayout(new GridLayout(0, 4));
        typeOuterPanel.add(typeCheckboxGroup);
        mSearchPane.add(typeOuterPanel);

        cb_type_cookie = new JCheckBox("餅乾");
		cb_type_cookie.setSelected(mDefaultState.getDefaultTypeFlag(0));
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
        	cb_level[i] = new JCheckBox("Lv" + lv);
        	cb_level[i].setSelected(mDefaultState.getDefaultLvFlag(lv));
            typeCheckboxGroup.add(cb_level[i]);
            cb_level[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	mDefaultState.setDefaultLvFlag(lv, cb_level[id].isSelected());
                }
            });
            cb_level[i].setEnabled(cb_type_cookie.isSelected());
            
        }

        cb_flip = new JCheckBox("FLIP");
        cb_flip.setSelected(mDefaultState.getDefaultFlipFlag());
        typeCheckboxGroup.add(cb_flip);
        cb_flip.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	mDefaultState.setDefaultFlipFlag(cb_flip.isSelected());
            }
        });

        cb_extra = new JCheckBox("EXTRA");
        cb_extra.setSelected(mDefaultState.getDefaultExtraFlag());
        typeCheckboxGroup.add(cb_extra);
        cb_extra.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	mDefaultState.setDefaultExtraFlag(cb_extra.isSelected());
            }
        });

        cb_type_item = new JCheckBox("道具");
        cb_type_item.setSelected(mDefaultState.getDefaultTypeFlag(1));
        typeCheckboxGroup.add(cb_type_item);
        cb_type_item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	mDefaultState.setDefaultTypeFlag(1, cb_type_item.isSelected());
            }
        });

        
        cb_type_trap = new JCheckBox("陷阱");
        cb_type_trap.setSelected(mDefaultState.getDefaultTypeFlag(2));
        typeCheckboxGroup.add(cb_type_trap);
        cb_type_trap.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	mDefaultState.setDefaultTypeFlag(2, cb_type_trap.isSelected());
            }
        });

        
        cb_type_stage = new JCheckBox("場景");
        cb_type_stage.setSelected(mDefaultState.getDefaultTypeFlag(3));
        typeCheckboxGroup.add(cb_type_stage);
        cb_type_stage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	mDefaultState.setDefaultTypeFlag(3, cb_type_stage.isSelected());
            }
        });

        mSearchPane.add(Box.createRigidArea(new Dimension(0, 10))); // 10px vertical space

        // ========================= pack ==================================


        label_version = new JLabel("系列", JLabel.CENTER);
        mSearchPane.add(label_version);

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
        JMenuItem settingsMenuItem = new JMenuItem("設定");
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
        UIUtil.showDeck(this, mCardsPane, list.getSelectCards(), 13, 6, UIUtil.CARD_SIZE_SMALL, false);
        mCardsPane.revalidate();
        mCardsPane.repaint();
    }
    
    private void updateDeck() {
        mDeckPane.removeAll();
        UIUtil.showDeck(this, mDeckPane, mDeck.getAllCards(), 18, 6, UIUtil.CARD_SIZE_SMALL, true);

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
}
