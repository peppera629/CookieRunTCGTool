package util;

import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import dataStructure.Card;
import dataStructure.Deck;
import util.CardUtil.CardColor;
import util.CardUtil.CardType;

public class DefaultState {
	static final String CONFIG_PATH = "config/default_state.txt";
	public String DeckName = "NewDeck";
	public boolean[] color;
	public boolean[] type;
	public boolean[] lv;
	public boolean flip, extra;
	private static DefaultState instance;
	private List<String> _search_pack_list;

	public static DefaultState getInstance() {
		if (instance == null) {
			instance = new DefaultState();
			instance.createCardBackLabel();
			instance.loadDefaultState();
			instance.updateSortConfig();
		}
		return instance;
	}

	private DefaultState() {
		color = new boolean[CardUtil.COLOR_MAX];
		type = new boolean[CardUtil.TYPE_MAX];
		lv = new boolean[CardUtil.LEVEL_MAX + 1];
		_search_pack_list = new ArrayList<String>();
	}

	private void createCardBackLabel() {
        ImageIcon cardIcon = new ImageIcon("resources/cards/cardback.png");
        
        Image image = cardIcon.getImage().getScaledInstance(Config.SMALL_CARD_WIDTH, Config.SMALL_CARD_HEIGHT,  java.awt.Image.SCALE_SMOOTH);
        CardUtil.CardBack = new ImageIcon(image);
	}
	
	private void loadDefaultState() {
		System.out.println("loadDefaultState");
		try {
			File file = new File(CONFIG_PATH);
			FileInputStream reader = new FileInputStream(file);
			BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
			String data;

			// 卡組名稱
			if ((data = input.readLine()) != null) {
				DeckName = data;
			}

			// color
			if ((data = input.readLine()) != null) {
				String[] flags = data.split(",");
				for (int i = 0; i < flags.length; i++) {
					if (color.length > i) {
						if (flags[i].equals("v")) {
							color[i] = true;
						}
					}
				}
			}

			// type
			if ((data = input.readLine()) != null) {
				String[] flags = data.split(",");
				for (int i = 0; i < flags.length; i++) {
					if (type.length > i) {
						if (flags[i].equals("v")) {
							type[i] = true;
						}
					}
				}
			}

			// flip
			if ((data = input.readLine()) != null) {
				if (data.equals("v")) {
					flip = true;
				}
			}

			// lv
			if ((data = input.readLine()) != null) {
				System.out.println(data);
				String[] flags = data.split(",");
				for (int i = 0; i < flags.length; i++) {
					if (lv.length > i + 1) {
						if (flags[i].equals("v")) {
							lv[i + 1] = true;
						}
					}
				}
			}

			// pack
			if ((data = input.readLine()) != null) {
				String[] flags = data.split(",");
				for (int i = 0; i < flags.length; i++) {
					_search_pack_list.add(flags[i]);
				}
			}

			// sort
			if ((data = input.readLine()) != null) {
				String[] flags = data.split(",");
				Config.CARD_SORT_ORDER_TYPE = Integer.valueOf(flags[0]);
				Config.CARD_SORT_ORDER_FLIP = Integer.valueOf(flags[1]);
				Config.CARD_SORT_ORDER_LEVEL = Integer.valueOf(flags[2]);
				Config.CARD_SORT_ORDER_COLOR = Integer.valueOf(flags[3]);
			}
			
			input.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void cleanSearchFlag() {
		for (int i = 0; i < color.length; i++) {
			color[i] = false;
		}

		for (int i = 0; i < type.length; i++) {
			type[i] = false;
		}
		flip = false;
	}

	public void saveDefaultState() {
		FileWriter fw;
		try {
			fw = new FileWriter(CONFIG_PATH);
			fw.write(DeckName + "\n");

			for (int i = 0; i < color.length; i++) {
				if (i > 0) {
					fw.write(",");
				}
				if (color[i]) {
					fw.write("v");
				} else {
					fw.write("_");
				}
			}
			fw.write("\n");

			for (int i = 0; i < type.length; i++) {
				if (i > 0) {
					fw.write(",");
				}
				if (type[i]) {
					fw.write("v");
				} else {
					fw.write("_");
				}
			}
			fw.write("\n");

			if (flip) {
				fw.write("v\n");
			} else {
				fw.write("_\n");
			}

			for (int i = 1; i < lv.length; i++) {
				if (i > 1) {
					fw.write(",");
				}
				if (lv[i]) {
					fw.write("v");
				} else {
					fw.write("_");
				}
			}
			fw.write("\n");

			// pack
			int count = 0;
			for (String s : _search_pack_list) {
				if (count > 0) {
					fw.write(",");
				}
				fw.write(s);
				count++;
			}
			fw.write("\n");

			//sort order
			fw.write(Config.CARD_SORT_ORDER_TYPE+",");
			fw.write(Config.CARD_SORT_ORDER_FLIP+",");
			fw.write(Config.CARD_SORT_ORDER_LEVEL+",");
			fw.write(Config.CARD_SORT_ORDER_COLOR+"\n");
			
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getDeckDefaultName() {
		return DeckName;
	}

	public boolean getDefaultColorFlag(int i) {
		return color[i];
	}

	public boolean getDefaultTypeFlag(int i) {
		return type[i];
	}

	public boolean getDefaultFlipFlag() {
		return flip;
	}

	public boolean getDefaultExtraFlag() {
		return extra;
	}

	public boolean getDefaultLvFlag(int i) {
		return lv[i];
	}

	public boolean getDefaultPackFlag(String pack) {
		return _search_pack_list.contains(pack);
	}

	public void setDefaultDeckName(String name) {
		DeckName = name;
	}

	public void setDefaultColorFlag(int i, boolean selected) {
		color[i] = selected;
	}

	public void setDefaultTypeFlag(int i, boolean selected) {
		type[i] = selected;
	}

	public void setDefaultExtraFlag(boolean selected) {
		extra = selected;
	}

	public void setDefaultFlipFlag(boolean selected) {
		flip = selected;
	}

	public void setDefaultLvFlag(int i, boolean selected) {
		lv[i] = selected;
	}

	public void setDefaultPackFlag(String pack, boolean selected) {
		if (selected && !_search_pack_list.contains(pack)) {
			_search_pack_list.add(pack);
		}
		if (!selected && _search_pack_list.contains(pack)) {
			_search_pack_list.remove(pack);
		}
	}
	
	public void updateSortConfig() {

		int currentPosition = 29;
        System.out.println("CARD_SORT_ORDER_TYPE = "+Config.CARD_SORT_ORDER_TYPE);
        System.out.println("CARD_SORT_ORDER_FLIP = "+Config.CARD_SORT_ORDER_FLIP);
        System.out.println("CARD_SORT_ORDER_LEVEL = "+Config.CARD_SORT_ORDER_LEVEL);
        System.out.println("CARD_SORT_ORDER_COLOR = "+Config.CARD_SORT_ORDER_COLOR);
        System.out.println("");
		for(int i = 1; i <= 4; i++) {
			if(Config.CARD_SORT_ORDER_TYPE == i) {
				currentPosition -= Config.CARD_SORT_SIZE_TYPE;
				Config.CARD_SORT_VALUE_TYPE = 2 << currentPosition;
		        System.out.println("CARD_SORT_VALUE_TYPE = "+Config.CARD_SORT_VALUE_TYPE);
				
			} else if(Config.CARD_SORT_ORDER_FLIP == i) {
				currentPosition -= Config.CARD_SORT_SIZE_FLIP;
				Config.CARD_SORT_VALUE_FLIP = 2 << currentPosition;
		        System.out.println("CARD_SORT_VALUE_FLIP = "+Config.CARD_SORT_VALUE_FLIP);

			} else if(Config.CARD_SORT_ORDER_LEVEL == i) {
				currentPosition -= Config.CARD_SORT_SIZE_LEVEL;
				Config.CARD_SORT_VALUE_LEVEL = 2 << currentPosition;
		        System.out.println("CARD_SORT_VALUE_LEVEL = "+Config.CARD_SORT_VALUE_LEVEL);

			} else if(Config.CARD_SORT_ORDER_COLOR == i) {
				currentPosition -= Config.CARD_SORT_SIZE_COLOR;
				Config.CARD_SORT_VALUE_COLOR = 2 << currentPosition;
		        System.out.println("CARD_SORT_VALUE_COLOR = "+Config.CARD_SORT_VALUE_COLOR);
			}
		}

		if(Config.CARD_SORT_ORDER_TYPE == 0) {
			Config.CARD_SORT_VALUE_TYPE = 0;
		}
		if(Config.CARD_SORT_ORDER_FLIP == 0) {
			Config.CARD_SORT_VALUE_FLIP = 0;
		}
		if(Config.CARD_SORT_ORDER_LEVEL == 0) {
			Config.CARD_SORT_VALUE_LEVEL = 0;
		}
		if(Config.CARD_SORT_ORDER_COLOR == 0) {
			Config.CARD_SORT_VALUE_COLOR = 0;
		}
	}
}
