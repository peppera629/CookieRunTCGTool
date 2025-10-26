package util;

import java.util.List;

import javax.swing.ImageIcon;

import dataStructure.Card;

public class CardUtil {
	public static int LEVEL_MAX = 3;
	public static int COLOR_MAX = 6;
	public enum CardColor {
	    Red(0), Yellow(1), Green(2), Blue(3), Purple(4), Colorless(5);
	    public final int value;
	    private CardColor(int value) {
	        this.value = value;
	    }

	    public int getValue() {
	        return value;
	    }
	    
	    public String getName() {
	        switch(this){
		        case Red:
		        	return "Red";
		        case Yellow:
		        	return "Yellow";
		        case Green:
		        	return "Green";
		        case Blue:
		        	return "Blue";
		        case Purple:
		        	return "Purple";
				case Colorless:
		        	return "Colorless";
	        }
	        return null;
	    }

	    public static CardColor fromValue(int value) {
	        for (CardColor color : CardColor.values()) {
	            if (color.getValue() == value) {
	                return color;
	            }
	        }
	        // 如果沒有找到對應的 enum，你可以選擇拋出一個異常，或者返回默認值
	        throw new IllegalArgumentException("No enum constant with value " + value);
	    }
	}

	public static int TYPE_MAX = 4;
	public enum CardType {
	    Cookie(0), Item(1), Trap(2), Stage(3);
	    private final int value;
	    private CardType(int value) {
	        this.value = value;
	    }

	    public int getValue() {
	        return value;
	    }
	}

	public static ImageIcon CardBack;
	
	
	public static List<String> CardPack;
}
