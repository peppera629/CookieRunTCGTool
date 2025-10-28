package util;

public class Config {
	public static boolean SHOW_CARD_COUNT = true;

	public static String LANGUAGE = "zh_TW"; // en or zh_TW

	public static float CARD_RATIO = 1.4F;
	
	public static int DW_ROW_SIZE = 8;
	
	public static int SMALL_CARD_WIDTH = 120;
	public static int SMALL_CARD_HEIGHT = (int) (SMALL_CARD_WIDTH * CARD_RATIO);
	
	public static int DW_CARD_WIDTH = 120;
	public static int DW_CARD_HEIGHT = (int) (DW_CARD_WIDTH * CARD_RATIO);
	
	public static int DW_OUTPUT_WIDTH = 300;
	public static int DW_OUTPUT_HEIGHT = (int) (DW_OUTPUT_WIDTH * CARD_RATIO);

	// ========================= sort config ========================
	public static final String SORT_NAME_TYPE = "卡片類型"; 
	public static final String SORT_NAME_FLIP = "是否FLIP"; 
	public static final String SORT_NAME_LEVEL = "卡片等級"; 
	public static final String SORT_NAME_COLOR = "卡片顏色"; 
	
	public static final int CARD_SORT_SIZE_TYPE = 3;
	public static final int CARD_SORT_SIZE_FLIP = 1;
	public static final int CARD_SORT_SIZE_LEVEL = 2;
	public static final int CARD_SORT_SIZE_COLOR = 3;

	public static int CARD_SORT_VALUE_TYPE = 2 << 26;
	public static int CARD_SORT_VALUE_FLIP = 2 << 25;
	public static int CARD_SORT_VALUE_LEVEL = 2 << 23;
	public static int CARD_SORT_VALUE_COLOR = 2 << 20;
	
	public static int CARD_SORT_ORDER_TYPE = 1;
	public static int CARD_SORT_ORDER_FLIP = 2;
	public static int CARD_SORT_ORDER_LEVEL = 3;
	public static int CARD_SORT_ORDER_COLOR = 4;
}
