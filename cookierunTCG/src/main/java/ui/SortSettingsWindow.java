package ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

import util.Config;
import util.DefaultState;

import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.awt.GridBagConstraints;

import util.CardUtil;

public class SortSettingsWindow implements util.LanguageChangeListener {
	private JFrame frame;
	private JPanel deckSort, deckSortedGroup, deckUnsortedGroup, buttonGroup;
	private JList<String> mSortedList, mUnsortedList;
	DefaultListModel<String> mSortedListModel, mUnsortedListModel;
	private ConfigChangedCallback mListener;
	private JLabel deckSortedLabel, deckUnsortedLabel;
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
					mSortedListModel.clear();
					mUnsortedListModel.clear();
		        	if (Config.CARD_SORT_ORDER_TYPE == 0) {
		        		mUnsortedListModel.addElement(CardUtil.getTranslation("sort.name.type"));
		        	} 
		        	if (Config.CARD_SORT_ORDER_FLIP == 0) {
		        		mUnsortedListModel.addElement(CardUtil.getTranslation("sort.name.flip"));
		        	} 
		        	if (Config.CARD_SORT_ORDER_EXTRA == 0) {
		        		mUnsortedListModel.addElement(CardUtil.getTranslation("sort.name.extra"));
		        	}
		        	if (Config.CARD_SORT_ORDER_LEVEL == 0) {
		        		mUnsortedListModel.addElement(CardUtil.getTranslation("sort.name.level"));
		        	} 
		        	if (Config.CARD_SORT_ORDER_COLOR == 0) {
		        		mUnsortedListModel.addElement(CardUtil.getTranslation("sort.name.color"));
		        	}
		        	
		        	int id = 0;
			        for (int i=1; i<6; i++) {
			        	if (Config.CARD_SORT_ORDER_TYPE == i) {
			        		mSortedListModel.add(id++, CardUtil.getTranslation("sort.name.type"));
			        	} else if (Config.CARD_SORT_ORDER_FLIP == i) {
			        		mSortedListModel.add(id++, CardUtil.getTranslation("sort.name.flip"));
						} else if (Config.CARD_SORT_ORDER_EXTRA == i) {
			        		mSortedListModel.add(id++, CardUtil.getTranslation("sort.name.extra"));
			        	} else if (Config.CARD_SORT_ORDER_LEVEL == i) {
			        		mSortedListModel.add(id++, CardUtil.getTranslation("sort.name.level"));
			        	} else if (Config.CARD_SORT_ORDER_COLOR == i) {
			        		mSortedListModel.add(id++, CardUtil.getTranslation("sort.name.color"));
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
		frame.setLocation(150, 150);
		frame.setPreferredSize(new Dimension(400, 300));
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());

		deckSort = new JPanel(new GridBagLayout());
		GridBagConstraints gbc_deckSort = new GridBagConstraints();
		deckSort.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		frame.getContentPane().add(deckSort, BorderLayout.CENTER);

		mSortedListModel = new DefaultListModel<>();
        mUnsortedListModel = new DefaultListModel<>();

		gbc_deckSort.fill = GridBagConstraints.BOTH;
		gbc_deckSort.gridx = 0;
		gbc_deckSort.gridy = 0;
		gbc_deckSort.weightx = 5.0;
		gbc_deckSort.weighty = 7.0;

		deckSortedGroup = new JPanel(new BorderLayout());
		deckSort.add(deckSortedGroup, gbc_deckSort);

		deckSortedLabel = new JLabel(CardUtil.getTranslation("sort.rules"));
		deckSortedLabel.setFont(MainUI.CRnormal);
		MainUI.componentFontMap.put(deckSortedLabel, "CRnormal");
		//deckSortedLabel.setBounds(10, 30, 161, 15);
		deckSortedGroup.add(deckSortedLabel, BorderLayout.NORTH);

		mSortedList = new JList<String>(mSortedListModel);
        mSortedList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		mSortedList.setFont(MainUI.CRnormal);
		MainUI.componentFontMap.put(mSortedList, "CRnormal");
		//mSortedList.setBounds(10, 55, 161, 135);
		deckSortedGroup.add(mSortedList, BorderLayout.CENTER);

		gbc_deckSort.gridx = 2;
		gbc_deckSort.gridy = 0;
		gbc_deckSort.weightx = 5.0;
		gbc_deckSort.weighty = 7.0;

		deckUnsortedGroup = new JPanel(new BorderLayout());
		deckSort.add(deckUnsortedGroup, gbc_deckSort);
		
		deckUnsortedLabel = new JLabel(CardUtil.getTranslation("sort.none"));
		deckUnsortedLabel.setFont(MainUI.CRnormal);
		MainUI.componentFontMap.put(deckUnsortedLabel, "CRnormal");
		//deckUnsortedLabel.setBounds(242, 30, 161, 15);
		deckUnsortedGroup.add(deckUnsortedLabel, BorderLayout.NORTH);

		mUnsortedList = new JList<String>(mUnsortedListModel);
		mUnsortedList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		mUnsortedList.setFont(MainUI.CRnormal);
		MainUI.componentFontMap.put(mUnsortedList, "CRnormal");
		//mUnsortedList.setBounds(242, 55, 161, 135);
		deckUnsortedGroup.add(mUnsortedList, BorderLayout.CENTER);
		
		gbc_deckSort.gridx = 1;
		gbc_deckSort.gridy = 0;
		gbc_deckSort.weightx = 1.0;
		gbc_deckSort.weighty = 7.0;

		JPanel buttonGroupFlow = new JPanel();
		buttonGroupFlow.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		deckSort.add(buttonGroupFlow, gbc_deckSort);

		buttonGroup = new JPanel(new GridLayout(4, 1, 5, 5));
		buttonGroup.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		buttonGroupFlow.add(buttonGroup);

		JButton btnMoveUp = new JButton("↑");
		//btnMoveUp.setBounds(181, 52, 51, 23);
		btnMoveUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = mSortedList.getSelectedIndex();
				if (index > 0) {
					String value = mSortedList.getSelectedValue();
					mSortedListModel.removeElement(mSortedList.getSelectedValue());
					mSortedListModel.add(index-1, value);
					mSortedList.setSelectedIndex(index-1);
				}
			}
		});
		buttonGroup.add(btnMoveUp);
		
		JButton btnRemove = new JButton("→");
		//btnRemove.setBounds(181, 85, 51, 23);
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mUnsortedListModel.addElement(mSortedList.getSelectedValue());
				mSortedListModel.removeElement(mSortedList.getSelectedValue());
			}
		});
		buttonGroup.add(btnRemove);
		
		JButton btnAdd = new JButton("←");
		//btnAdd.setBounds(181, 113, 51, 23);
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mSortedListModel.addElement(mUnsortedList.getSelectedValue());
				mUnsortedListModel.removeElement(mUnsortedList.getSelectedValue());
			}
		});
		buttonGroup.add(btnAdd);
		
		JButton btnMoveDown = new JButton("↓");
		//btnMoveDown.setBounds(181, 143, 51, 23);
		btnMoveDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = mSortedList.getSelectedIndex();
				if (index < mSortedListModel.size()-1) {
					String value = mSortedList.getSelectedValue();
					mSortedListModel.removeElement(mSortedList.getSelectedValue());
					mSortedListModel.add(index+1, value);
					mSortedList.setSelectedIndex(index+1);
				}
			}
		});
		buttonGroup.add(btnMoveDown);

		btnConfirm = new JButton(CardUtil.getTranslation("sort.confirm"));
		btnConfirm.setFont(MainUI.CRnormal);
		MainUI.componentFontMap.put(btnConfirm, "CRnormal");
		//btnConfirm.setBounds(321, 221, 105, 32);
		frame.getContentPane().add(btnConfirm, BorderLayout.SOUTH);
		btnConfirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateSort();

		    	DefaultState.getInstance().updateSortConfig();
		    	DefaultState.getInstance().saveDefaultState();
		    	mListener.onSortConfigChanged();
			}
		});

		frame.pack();
	}
	
	public void updateSort() {
		for(int i = 0; i < mSortedListModel.getSize(); i++) {
			String s = mSortedListModel.get(i);
			if(s.equals(CardUtil.getTranslation("sort.name.type"))) {
				Config.CARD_SORT_ORDER_TYPE = i + 1;

			} else if (s.equals(CardUtil.getTranslation("sort.name.flip"))) {
				Config.CARD_SORT_ORDER_FLIP = i + 1;

			} else if (s.equals(CardUtil.getTranslation("sort.name.extra"))) {
				Config.CARD_SORT_ORDER_EXTRA = i + 1;

			} else if (s.equals(CardUtil.getTranslation("sort.name.level"))) {
				Config.CARD_SORT_ORDER_LEVEL = i + 1;

			} else if (s.equals(CardUtil.getTranslation("sort.name.color"))) {
				Config.CARD_SORT_ORDER_COLOR = i + 1;
			}
		}

		for (int i = 0; i < mUnsortedListModel.getSize(); i++) {
			String s = mUnsortedListModel.get(i);
			if (s == null) {
				continue;
			}
			if (s.equals(CardUtil.getTranslation("sort.name.type"))) {
				Config.CARD_SORT_ORDER_TYPE = 0;

			} else if(s.equals(CardUtil.getTranslation("sort.name.flip"))) {
				Config.CARD_SORT_ORDER_FLIP = 0;

			} else if(s.equals(CardUtil.getTranslation("sort.name.extra"))) {
				Config.CARD_SORT_ORDER_EXTRA = 0;

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
        deckSortedLabel.setText(CardUtil.getTranslation("sort.rules"));
        deckUnsortedLabel.setText(CardUtil.getTranslation("sort.none"));
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
				case "CRnormalEXLarge":
					newFont = MainUI.CRnormalEXLarge;
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
