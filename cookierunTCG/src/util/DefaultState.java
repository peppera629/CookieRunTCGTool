package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import dataStructure.Card;
import dataStructure.Deck;
import util.CardUtil.CardColor;
import util.CardUtil.CardType;

public class DefaultState {
	static final String CONFIG_PATH = "config/default_state.txt";
	public String DeckName = "NewDeck";
	public boolean[] color;
	public boolean[] type;
	public boolean flip;
	private static DefaultState instance;
	public static DefaultState getInstance() {
		if (instance == null) {
			instance = new DefaultState();
			instance.loadDefaultState();
		}
		return instance;
	}
	private DefaultState() {
		color = new boolean[CardUtil.COLOR_MAX];
		type = new boolean[CardUtil.TYPE_MAX];
	}

	private void loadDefaultState() {
        System.out.println("loadDefaultState");
	    try {
	        File file = new File(CONFIG_PATH);
			FileInputStream reader = new FileInputStream(file);
	        BufferedReader input = new BufferedReader(
	                new InputStreamReader(new FileInputStream(file), "utf-8")); 
            String data;
            
            //卡組名稱
	        if((data = input.readLine())!=null) {
	        	DeckName = data;
	        }

	        //color
	        if((data = input.readLine())!=null) {
	        	String[] flags = data.split(",");
	        	for (int i=0;i<flags.length;i++) {
	        		if (color.length > i) {
	        			if (flags[i].equals("v")) {
	        				color[i] = true;
	        			}
	        		}
	        	}
	        }

	        //type
	        if((data = input.readLine())!=null) {
	        	String[] flags = data.split(",");
	        	for (int i=0;i<flags.length;i++) {
	        		if (type.length > i) {
	        			if (flags[i].equals("v")) {
	        				type[i] = true;
	        			}
	        		}
	        	}
	        }

	        //flip
	        if((data = input.readLine())!=null) {
    			if (data.equals("v")) {
    				flip = true;
    			}
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
		for(int i=0; i<color.length; i++) {
			color[i] = false;
		}

		for(int i=0; i<type.length; i++) {
			type[i] = false;
		}
		flip = false;
	}
		
	public void saveDefaultState() {
		FileWriter fw;
		try {
			fw = new FileWriter(CONFIG_PATH);
			fw.write(DeckName+"\n");

			for(int i=0; i<color.length; i++) {
				if (i>0) {
					fw.write(",");
				}
				if (color[i]) {
					fw.write("v");
				} else {
					fw.write("_");
				}
			}
			fw.write("\n");

			for(int i=0; i<type.length; i++) {
				if (i>0) {
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
			
	        fw.flush();
	        fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	
	public String getDeckDefaultName () {
		return DeckName;
	}
	
	public boolean getDefaultColorFlag (int i) {
		return color[i];
	}
	
	public boolean getDefaultTypeFlag (int i) {
		return type[i];
	}
	
	public boolean getDefaultFlipFlag () {
		return flip;
	}

	public void setDefaultDeckName (String name) {
		DeckName = name;
	}
	
	public void setDefaultColorFlag (int i, boolean selected) {
		color[i] = selected;
	}
	
	public void setDefaultTypeFlag (int i, boolean selected) {
		type[i] = selected;
	}
	
	public void setDefaultFlipFlag (boolean selected) {
		flip = selected;
	}
}
