package ui;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.TransferHandler.TransferSupport;

import dataStructure.Card;
import util.Config;
import util.DefaultState;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.ListModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;

import util.CardUtil;

public class SortSettingsWindow implements util.LanguageChangeListener {
	private JFrame frame;
	private JList<String> mSortList, mNotSortList;
	DefaultListModel<String> mSortListModel, mNotSortListModel;
	private ConfigChangedCallback mListener;
	private JLabel lblNewLabel, lblNewLabel_1;
	private JButton btnConfirm;
	
	public interface ConfigChangedCallback{
		public void onSortConfigChanged();
	}
	
	public void setConfigChangedCallback(ConfigChangedCallback callback) {
		mListener = callback;
	}

	/**
	 * Launch the application.
	 */
	public void show() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					mSortListModel.clear();
					mNotSortListModel.clear();
		        	if (Config.CARD_SORT_ORDER_TYPE == 0) {
		        		mNotSortListModel.addElement(CardUtil.getTranslation("sort.name.type"));
		        	} 
		        	if (Config.CARD_SORT_ORDER_FLIP == 0) {
		        		mNotSortListModel.addElement(CardUtil.getTranslation("sort.name.flip"));
		        	} 
		        	if (Config.CARD_SORT_ORDER_LEVEL == 0) {
		        		mNotSortListModel.addElement(CardUtil.getTranslation("sort.name.level"));
		        	} 
		        	if (Config.CARD_SORT_ORDER_COLOR == 0) {
		        		mNotSortListModel.addElement(CardUtil.getTranslation("sort.name.color"));
		        	}
		        	
		        	int id = 0;
			        for (int i=1; i<5; i++) {
			        	if (Config.CARD_SORT_ORDER_TYPE == i) {
			        		mSortListModel.add(id++, CardUtil.getTranslation("sort.name.type"));
			        	} else if (Config.CARD_SORT_ORDER_FLIP == i) {
			        		mSortListModel.add(id++, CardUtil.getTranslation("sort.name.flip"));
			        	} else if (Config.CARD_SORT_ORDER_LEVEL == i) {
			        		mSortListModel.add(id++, CardUtil.getTranslation("sort.name.level"));
			        	} else if (Config.CARD_SORT_ORDER_COLOR == i) {
			        		mSortListModel.add(id++, CardUtil.getTranslation("sort.name.color"));
			        	}
			        }
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public SortSettingsWindow() {
		initialize();
		SettingsWindow.addLanguageChangeListener(this);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(150, 150, 450, 300);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		mSortListModel = new DefaultListModel<>();
        mNotSortListModel = new DefaultListModel<>();
/*        
		if(Config.CARD_SORT_ORDER_TYPE == 0) {
			mNotSortListModel.addElement(Config.SORT_NAME_TYPE);
		} else {
			mSortListModel.addElement(Config.SORT_NAME_TYPE);
		}
        mSortListModel.addElement(Config.SORT_NAME_TYPE);
        mSortListModel.addElement(Config.SORT_NAME_FLIP);
        mSortListModel.addElement(Config.SORT_NAME_LEVEL);
        mSortListModel.addElement(Config.SORT_NAME_COLOR);*/
        
		mSortList = new JList<String>(mSortListModel);
        mSortList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		mSortList.setFont(MainUI.CRnormal);
		MainUI.componentFontMap.put(mSortList, "CRnormal");
		mSortList.setBounds(10, 55, 161, 111);
		frame.getContentPane().add(mSortList);
		
		mNotSortList = new JList<String>(mNotSortListModel);
		mNotSortList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		mNotSortList.setFont(MainUI.CRnormal);
		MainUI.componentFontMap.put(mNotSortList, "CRnormal");
		mNotSortList.setBounds(242, 55, 161, 111);
		frame.getContentPane().add(mNotSortList);
		
		
		lblNewLabel = new JLabel(CardUtil.getTranslation("sort.rules"));
		lblNewLabel.setFont(MainUI.CRnormal);
		MainUI.componentFontMap.put(lblNewLabel, "CRnormal");
		lblNewLabel.setBounds(10, 30, 161, 15);
		frame.getContentPane().add(lblNewLabel);
		
		JButton btnMoveUp = new JButton("↑");
		btnMoveUp.setBounds(181, 52, 51, 23);
		btnMoveUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = mSortList.getSelectedIndex();
				if (index > 0) {
					String value = mSortList.getSelectedValue();
					mSortListModel.removeElement(mSortList.getSelectedValue());
					mSortListModel.add(index-1, value);
					mSortList.setSelectedIndex(index-1);
				}
			}
		});
		frame.getContentPane().add(btnMoveUp);
		
		JButton btnMoveDown = new JButton("↓");
		btnMoveDown.setBounds(181, 143, 51, 23);
		btnMoveDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = mSortList.getSelectedIndex();
				if (index < mSortListModel.size()-1) {
					String value = mSortList.getSelectedValue();
					mSortListModel.removeElement(mSortList.getSelectedValue());
					mSortListModel.add(index+1, value);
					mSortList.setSelectedIndex(index+1);
				}
			}
		});
		frame.getContentPane().add(btnMoveDown);
		
		JButton btnRemove = new JButton("→");
		btnRemove.setBounds(181, 85, 51, 23);
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mNotSortListModel.addElement(mSortList.getSelectedValue());
				mSortListModel.removeElement(mSortList.getSelectedValue());
			}
		});
		frame.getContentPane().add(btnRemove);
		
		JButton btnAdd = new JButton("←");
		btnAdd.setBounds(181, 113, 51, 23);
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mSortListModel.addElement(mNotSortList.getSelectedValue());
				mNotSortListModel.removeElement(mNotSortList.getSelectedValue());
			}
		});
		frame.getContentPane().add(btnAdd);
		
		lblNewLabel_1 = new JLabel(CardUtil.getTranslation("sort.none"));
		lblNewLabel_1.setFont(MainUI.CRnormal);
		MainUI.componentFontMap.put(lblNewLabel_1, "CRnormal");
		lblNewLabel_1.setBounds(242, 30, 161, 15);
		frame.getContentPane().add(lblNewLabel_1);
		
		btnConfirm = new JButton(CardUtil.getTranslation("sort.confirm"));
		btnConfirm.setFont(MainUI.CRnormal);
		MainUI.componentFontMap.put(btnConfirm, "CRnormal");
		btnConfirm.setBounds(341, 230, 85, 23);
		frame.getContentPane().add(btnConfirm);
		btnConfirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateSort();

		    	DefaultState.getInstance().updateSortConfig();
		    	DefaultState.getInstance().saveDefaultState();
		    	mListener.onSortConfigChanged();
			}
		});
	}
	
	public void updateSort() {
		for(int i = 0; i < mSortListModel.getSize(); i++) {
			String s = mSortListModel.get(i);
			if(s.equals(CardUtil.getTranslation("sort.name.type"))) {
				Config.CARD_SORT_ORDER_TYPE = i + 1;

			} else if (s.equals(CardUtil.getTranslation("sort.name.flip"))) {
				Config.CARD_SORT_ORDER_FLIP = i + 1;

			} else if (s.equals(CardUtil.getTranslation("sort.name.level"))) {
				Config.CARD_SORT_ORDER_LEVEL = i + 1;

			} else if (s.equals(CardUtil.getTranslation("sort.name.color"))) {
				Config.CARD_SORT_ORDER_COLOR = i + 1;
			}
		}

		for (int i = 0; i < mNotSortListModel.getSize(); i++) {
			String s = mNotSortListModel.get(i);
			if (s == null) {
				continue;
			}
			if (s.equals(CardUtil.getTranslation("sort.name.type"))) {
				Config.CARD_SORT_ORDER_TYPE = 0;

			} else if(s.equals(CardUtil.getTranslation("sort.name.flip"))) {
				Config.CARD_SORT_ORDER_FLIP = 0;

			} else if(s.equals(CardUtil.getTranslation("sort.name.level"))) {
				Config.CARD_SORT_ORDER_LEVEL = 0;

			} else if(s.equals(CardUtil.getTranslation("sort.name.color"))) {
				Config.CARD_SORT_ORDER_COLOR = 0;
			}
		}
	}

	@Override
    public void onLanguageChange() {
        // Reload translations for all components
        lblNewLabel.setText(CardUtil.getTranslation("sort.rules"));
        lblNewLabel_1.setText(CardUtil.getTranslation("sort.none"));
        btnConfirm.setText(CardUtil.getTranslation("sort.confirm"));

		for (var entry : MainUI.componentFontMap.entrySet()) {
			Component component = entry.getKey();
            String fontKey = entry.getValue();

			// Map the fontKey to the appropriate Font object
			Font newFont = null;
			switch (fontKey) {
				case "CRnormal":
					newFont = MainUI.CRnormal;
					break;
				case "CRnormalLarge":
					newFont = MainUI.CRnormalLarge;
					break;
				case "CRnormalSmall":
					newFont = MainUI.CRnormalSmall;
					break;
				case "CRbold":
					newFont = MainUI.CRbold;
					break;
			}

			// Update the font for the component
			if (newFont != null) {
				component.setFont(newFont);
			}
        }

        // Revalidate and repaint the frame
        frame.revalidate();
        frame.repaint();
    }
}
