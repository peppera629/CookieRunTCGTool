package util;

public class Config {
	public static boolean SHOW_CARD_COUNT = true;


	public static float CARD_RATIO = 1.4F;
	
	public static int DW_ROW_SIZE = 8;
	
	public static int SMALL_CARD_WIDTH = 60;
	public static int SMALL_CARD_HEIGHT = (int) (SMALL_CARD_WIDTH * CARD_RATIO);
	
	public static int DW_CARD_WIDTH = 120;
	public static int DW_CARD_HEIGHT = (int) (DW_CARD_WIDTH * CARD_RATIO);
	
	public static int DW_OUTPUT_WIDTH = 300;
	public static int DW_OUTPUT_HEIGHT = (int) (DW_OUTPUT_WIDTH * CARD_RATIO);
}
