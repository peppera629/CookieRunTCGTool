package ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;

import dataStructure.Card;
import dataStructure.CardList;
import dataStructure.CardLoader;
import dataStructure.Deck;

import javax.swing.JPanel;
import java.awt.Panel;
import java.util.List;
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
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.FlowLayout;
import java.awt.Color;
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
    private JPanel mCardsPane, mDeckPane;
    
    //search panel
    private Panel mSearchPane;
    private JCheckBox[] cb_color;
    private JCheckBox[] cb_level;
    private JCheckBox cb_type_cookie, cb_type_item, cb_type_trap, cb_type_stage;
    private JCheckBox cb_flip;
    private JCheckBox cb_version_BS1, cb_version_ST1, cb_version_ST2, cb_version_ST3;
    private JLabel label_special, label_version;

    private Deck mDeck;
    private ScrollPane scrollPane;
    private Panel mCardDetailPane;
    private Panel panel;
    private TextField mDeckText;
    private JButton loadBtn, saveBtn, selectBtn;
    private JButton mClearDeckBtn;
    private JLabel mCardCountTxt, mFlipCountTxt, mDeckCookieSummaryTxt, mDeckOtherSummaryTxt;
    private JButton showDeckBtn;

    private void initialize() {
    	initialData();
    	initialUI();
    }

    private void initialData() {
    	mDefaultState = DefaultState.getInstance();
        mDeck = new Deck();
        frame = new JFrame();
    }
    
    private void initialUI() {
        
        frame.setTitle("薑餅人組牌系統   V "+Constant.VERSION);
        frame.setBounds(100, 100, 1080, 660);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        
        mSearchPane = new Panel();
        mSearchPane.setBounds(10, 10, 229, 603);
        frame.getContentPane().add(mSearchPane);
        mSearchPane.setLayout(null);

        initCheckBox();
        
        JButton button_search = new JButton("搜尋");
        button_search.setBounds(1, 360, 60, 25);
        mSearchPane.add(button_search);
        button_search.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	updateCardList();
                mDefaultState.saveDefaultState();
            }
        });
        
        JButton button_clean = new JButton("清除");
        button_clean.setBounds(159, 360, 60, 25);
        mSearchPane.add(button_clean);
        
        button_clean.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	cleanCheckBox();
            	updateCardList();
                mDefaultState.cleanSearchFlag();
                mDefaultState.saveDefaultState();
            }
        });
        
        
        // ==== 卡組
        mDeckPane = new JPanel();
        mDeckPane.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        
        JScrollPane scrollDeckPane = new JScrollPane(mDeckPane);
        scrollDeckPane.setBounds(245, 10, 415, 311);
        scrollDeckPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollDeckPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        frame.getContentPane().add(scrollDeckPane);
        
        
        // ==== 卡片列表
        mCardsPane = new JPanel();
        mCardsPane.setLayout(new GridLayout(0, 6, 5, 5));
        
        JScrollPane scrollCardsPane = new JScrollPane(mCardsPane);
        scrollCardsPane.setBackground(new Color(255, 255, 255));
        scrollCardsPane.setBounds(245, 364, 415, 249);
        scrollCardsPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollCardsPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        frame.getContentPane().add(scrollCardsPane);
        
        mCardDetailPane = new Panel();
        mCardDetailPane.setLayout(new BorderLayout());
        mCardDetailPane.setBounds(666, 10, 390, 438);
        frame.getContentPane().add(mCardDetailPane);
        
        
        // ===== 檔案
        panel = new Panel();
        panel.setBackground(new Color(255, 255, 255));
        panel.setBounds(666, 455, 390, 38);
        frame.getContentPane().add(panel);
        panel.setLayout(null);
        
        mDeckText = new TextField();
        mDeckText.setText(mDefaultState.getDeckDefaultName());
        mDeckText.setBounds(9, 5, 147, 22);
        panel.add(mDeckText);
        
        loadBtn = new JButton("讀取");
        loadBtn.setBounds(256, 2, 60, 25);
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
        saveBtn.setBounds(320, 2, 60, 25);
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
        selectBtn.setBounds(162, 2, 90, 25);
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
        
        mClearDeckBtn = new JButton("清除卡組");
        mClearDeckBtn.setBounds(575, 333, 85, 23);
        frame.getContentPane().add(mClearDeckBtn);
        
        mCardCountTxt = new JLabel("0/60");
        mCardCountTxt.setBounds(533, 333, 36, 22);
        frame.getContentPane().add(mCardCountTxt);
        
        mFlipCountTxt = new JLabel("0/16");
        mFlipCountTxt.setBounds(491, 333, 36, 22);
        frame.getContentPane().add(mFlipCountTxt);
        
        mDeckCookieSummaryTxt = new JLabel("餅乾 : 0   ( LV1 : 0   LV2 : 0   LV3 : 0 )");
        mDeckCookieSummaryTxt.setBounds(245, 322, 236, 22);
        frame.getContentPane().add(mDeckCookieSummaryTxt);
        
        mDeckOtherSummaryTxt = new JLabel("物品 : 0   陷阱 : 0   場地 : 0");
        mDeckOtherSummaryTxt.setBounds(245, 339, 236, 22);
        frame.getContentPane().add(mDeckOtherSummaryTxt);
        
        showDeckBtn = new JButton("展示完整卡表");
        showDeckBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		deckWindow.show(mDeck, mDeckText.getText());
        	}
        });
        
        showDeckBtn.setBounds(676, 499, 120, 25);
        frame.getContentPane().add(showDeckBtn);
        mClearDeckBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mDeck.clear();
                updateDeck();
            }
        });

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
	

	private void nextLine() {
		x = paddingToWindow;
		y += height + padding;
	}
	
	private void nextObject(int width) {
		x += width + padding;
	}
    
    private void initCheckBox() {
    	
        // ========================= color ==================================
        JLabel label = new JLabel("顏色");
        label.setBounds(x, y, widthFull, height);
        mSearchPane.add(label);
        
        nextLine();
        

        System.out.println("紅 : "+x+", "+ y+", "+ widthOneWord+", "+ height);
        cb_color = new JCheckBox[CardUtil.COLOR_MAX];
        for(int i=0; i<CardUtil.COLOR_MAX; i++) {
        	cb_color[i] = new JCheckBox(CardUtil.CardColor.fromValue(i).getName());
        	cb_color[i].setFont(new Font("新細明體", Font.PLAIN, 12));
        	cb_color[i].setBounds(x, y, widthOneWord, height);
        	cb_color[i].setSelected(mDefaultState.getDefaultColorFlag(i));
            mSearchPane.add(cb_color[i]);
            final int id = i;
            cb_color[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	mDefaultState.setDefaultColorFlag(id, cb_color[id].isSelected());
                }
            });
            
            nextObject(widthOneWord);
        }
        
        nextLine();
        
        // ========================= type ==================================
        JLabel label_1 = new JLabel("卡片類型");
        label_1.setBounds(x, y, widthFull, height);
        mSearchPane.add(label_1);

        nextLine();
        
        cb_type_cookie = new JCheckBox("餅乾");
        cb_type_cookie.setBounds(x, y, widthTwoWord, height);
		cb_type_cookie.setSelected(mDefaultState.getDefaultTypeFlag(0));
        mSearchPane.add(cb_type_cookie);
        cb_type_cookie.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	mDefaultState.setDefaultTypeFlag(0, cb_type_cookie.isSelected());

            	for (JCheckBox cb : cb_level) {
            		cb.setEnabled(cb_type_cookie.isSelected());
            	}
            }
        });

        
        nextObject(widthTwoWord);
        
        cb_level = new JCheckBox[CardUtil.LEVEL_MAX];
        for(int i=0; i<CardUtil.LEVEL_MAX; i++) {
        	final int lv = i+1;
            final int id = i;
        	cb_level[i] = new JCheckBox("Lv" + lv);
        	cb_level[i].setSelected(mDefaultState.getDefaultLvFlag(lv));
        	cb_level[i].setBounds(x, y, widthTwoWord, height);
            mSearchPane.add(cb_level[i]);
            cb_level[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	mDefaultState.setDefaultLvFlag(lv, cb_level[id].isSelected());
                }
            });
            cb_level[i].setEnabled(cb_type_cookie.isSelected());
            
            nextObject(widthTwoWord);
        }

        nextLine();
        
        cb_type_item = new JCheckBox("物品");
        cb_type_item.setBounds(x, y, widthTwoWord, height);
        cb_type_item.setSelected(mDefaultState.getDefaultTypeFlag(1));
        mSearchPane.add(cb_type_item);
        cb_type_item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	mDefaultState.setDefaultTypeFlag(1, cb_type_item.isSelected());
            }
        });

        nextObject(widthTwoWord);
        
        cb_type_trap = new JCheckBox("陷阱");
        cb_type_trap.setBounds(x, y, widthTwoWord, height);
        cb_type_trap.setSelected(mDefaultState.getDefaultTypeFlag(2));
        mSearchPane.add(cb_type_trap);
        cb_type_trap.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	mDefaultState.setDefaultTypeFlag(2, cb_type_trap.isSelected());
            }
        });

        nextObject(widthTwoWord);
        
        cb_type_stage = new JCheckBox("場景");
        cb_type_stage.setBounds(x, y, widthTwoWord, height);
        cb_type_stage.setSelected(mDefaultState.getDefaultTypeFlag(3));
        mSearchPane.add(cb_type_stage);
        cb_type_stage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	mDefaultState.setDefaultTypeFlag(3, cb_type_stage.isSelected());
            }
        });

        nextObject(widthTwoWord);
        

        // ========================= flip ==================================
        nextLine();
        label_special = new JLabel("特殊");
        label_special.setBounds(x, y, widthTwoWord, height);
        mSearchPane.add(label_special);
        
        nextLine();
        
        cb_flip = new JCheckBox("Flip");
        cb_flip.setBounds(x, y, widthTwoWord, height);
        cb_flip.setSelected(mDefaultState.getDefaultFlipFlag());
        mSearchPane.add(cb_flip);
        cb_flip.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	mDefaultState.setDefaultFlipFlag(cb_flip.isSelected());
            }
        });
        
        nextLine();
        nextLine();


        // ========================= pack ==================================
        label_version = new JLabel("來源");
        label_version.setBounds(x, y, widthFull, height);
        mSearchPane.add(label_version);

        nextLine();
        
        cb_version_BS1 = new JCheckBox("BS1");
        cb_version_BS1.setSelected(mDefaultState.getDefaultPackFlag("BS1"));
        cb_version_BS1.setFont(new Font("新細明體", Font.PLAIN, 12));
        cb_version_BS1.setBounds(x, y, widthTwoWord, height);
        mSearchPane.add(cb_version_BS1);
        cb_version_BS1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	mDefaultState.setDefaultPackFlag("BS1", cb_version_BS1.isSelected());
            }
        });
        nextLine();
        
        cb_version_ST1 = new JCheckBox("ST1");
        cb_version_ST1.setSelected(mDefaultState.getDefaultPackFlag("ST1"));
        cb_version_ST1.setFont(new Font("新細明體", Font.PLAIN, 12));
        cb_version_ST1.setBounds(x, y, widthTwoWord, height);
        mSearchPane.add(cb_version_ST1);
        cb_version_ST1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	mDefaultState.setDefaultPackFlag("ST1", cb_version_ST1.isSelected());
            }
        });
        nextObject(widthTwoWord);
        
        cb_version_ST2 = new JCheckBox("ST2");
        cb_version_ST2.setSelected(mDefaultState.getDefaultPackFlag("ST2"));
        cb_version_ST2.setFont(new Font("新細明體", Font.PLAIN, 12));
        cb_version_ST2.setBounds(x, y, widthTwoWord, height);
        mSearchPane.add(cb_version_ST2);
        cb_version_ST2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	mDefaultState.setDefaultPackFlag("ST2", cb_version_ST2.isSelected());
            }
        });
        nextObject(widthTwoWord);
        
        cb_version_ST3 = new JCheckBox("ST3");
        cb_version_ST3.setSelected(mDefaultState.getDefaultPackFlag("ST3"));
        cb_version_ST3.setFont(new Font("新細明體", Font.PLAIN, 12));
        cb_version_ST3.setBounds(x, y, widthTwoWord, height);
        mSearchPane.add(cb_version_ST3);
        cb_version_ST3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	mDefaultState.setDefaultPackFlag("ST3", cb_version_ST3.isSelected());
            }
        });
        
        createMenu();
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
        list.setPack("BS1", cb_version_BS1.isSelected());
        list.setPack("ST1", cb_version_ST1.isSelected());
        list.setPack("ST2", cb_version_ST2.isSelected());
        list.setPack("ST3", cb_version_ST3.isSelected());
        list.setFlip(cb_flip.isSelected());
        
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
        

        mDeckCookieSummaryTxt.setText(mDeck.getCookieSummary());
        mDeckOtherSummaryTxt.setText(mDeck.getOtherSummary());
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
        ImageIcon cardIcon = new ImageIcon("resources/cards/"+card.getPack()+"/"+card.getId()+".png");
            
        Image image = cardIcon.getImage().getScaledInstance(342, 469, java.awt.Image.SCALE_SMOOTH);
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
