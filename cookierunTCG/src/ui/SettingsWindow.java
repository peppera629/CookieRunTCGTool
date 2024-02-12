package ui;

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

import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.ListModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class SettingsWindow {
	private JFrame frame;
	private JList<String> mSortList, mNotSortList;
	DefaultListModel<String> mSortListModel, mNotSortListModel;
	private ConfigChangedCallback mListener;
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
		        		mNotSortListModel.addElement(Config.SORT_NAME_TYPE);
		        	} 
		        	if (Config.CARD_SORT_ORDER_FLIP == 0) {
		        		mNotSortListModel.addElement(Config.SORT_NAME_FLIP);
		        	} 
		        	if (Config.CARD_SORT_ORDER_LEVEL == 0) {
		        		mNotSortListModel.addElement(Config.SORT_NAME_LEVEL);
		        	} 
		        	if (Config.CARD_SORT_ORDER_COLOR == 0) {
		        		mNotSortListModel.addElement(Config.SORT_NAME_COLOR);
		        	}
		        	
		        	int id = 0;
			        for (int i=1; i<5; i++) {
			        	if (Config.CARD_SORT_ORDER_TYPE == i) {
			        		mSortListModel.add(id++, Config.SORT_NAME_TYPE);
			        	} else if (Config.CARD_SORT_ORDER_FLIP == i) {
			        		mSortListModel.add(id++, Config.SORT_NAME_FLIP);
			        	} else if (Config.CARD_SORT_ORDER_LEVEL == i) {
			        		mSortListModel.add(id++, Config.SORT_NAME_LEVEL);
			        	} else if (Config.CARD_SORT_ORDER_COLOR == i) {
			        		mSortListModel.add(id++, Config.SORT_NAME_COLOR);
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
	public SettingsWindow() {
		initialize();
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
		mSortList.setBounds(10, 55, 161, 111);
		frame.getContentPane().add(mSortList);
		
		mNotSortList = new JList<String>(mNotSortListModel);
		mNotSortList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		mNotSortList.setBounds(242, 55, 161, 111);
		frame.getContentPane().add(mNotSortList);
		
		
		JLabel lblNewLabel = new JLabel("排序規則:");
		lblNewLabel.setBounds(10, 30, 161, 15);
		frame.getContentPane().add(lblNewLabel);
		
		JButton btnMoveUp = new JButton("^");
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
		
		JButton btnMoveDown = new JButton("v");
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
		
		JButton btnRemove = new JButton(">>");
		btnRemove.setBounds(181, 85, 51, 23);
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mNotSortListModel.addElement(mSortList.getSelectedValue());
				mSortListModel.removeElement(mSortList.getSelectedValue());
			}
		});
		frame.getContentPane().add(btnRemove);
		
		JButton btnAdd = new JButton("<<");
		btnAdd.setBounds(181, 113, 51, 23);
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mSortListModel.addElement(mNotSortList.getSelectedValue());
				mNotSortListModel.removeElement(mNotSortList.getSelectedValue());
			}
		});
		frame.getContentPane().add(btnAdd);
		
		JLabel lblNewLabel_1 = new JLabel("不排序");
		lblNewLabel_1.setBounds(242, 30, 161, 15);
		frame.getContentPane().add(lblNewLabel_1);
		
		JButton btnConfirm = new JButton("確認");
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
			if(s.equals(Config.SORT_NAME_TYPE)) {
				Config.CARD_SORT_ORDER_TYPE = i + 1;

			} else if (s.equals(Config.SORT_NAME_FLIP)) {
				Config.CARD_SORT_ORDER_FLIP = i + 1;

			} else if (s.equals(Config.SORT_NAME_LEVEL)) {
				Config.CARD_SORT_ORDER_LEVEL = i + 1;

			} else if (s.equals(Config.SORT_NAME_COLOR)) {
				Config.CARD_SORT_ORDER_COLOR = i + 1;
			}
		}

		for (int i = 0; i < mNotSortListModel.getSize(); i++) {
			String s = mNotSortListModel.get(i);
			if (s == null) {
				continue;
			}
			if (s.equals(Config.SORT_NAME_TYPE)) {
				Config.CARD_SORT_ORDER_TYPE = 0;
				
			} else if(s.equals(Config.SORT_NAME_FLIP)) {
				Config.CARD_SORT_ORDER_FLIP = 0;
				
			} else if(s.equals(Config.SORT_NAME_LEVEL)) {
				Config.CARD_SORT_ORDER_LEVEL = 0;
				
			} else if(s.equals(Config.SORT_NAME_COLOR)) {
				Config.CARD_SORT_ORDER_COLOR = 0;
			}
		}
	} 
}
