package util;

import javax.swing.ImageIcon;

public class CardUtil {
	public static int LEVEL_MAX = 3;
	public static int COLOR_MAX = 3;
	public enum CardColor {
	    Red(0), Yellow(1), Green(2);
	    private final int value;
	    private CardColor(int value) {
	        this.value = value;
	    }

	    public int getValue() {
	        return value;
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
}
