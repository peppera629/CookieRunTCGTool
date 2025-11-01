package util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class Config {

	private static final String CONFIG_FILE = "config/config.txt";
	public static double CARD_PREVIEW_SCALE;
	public static double CARD_ICON_SCALE;

	// Load the language setting from the config file
    public static void loadConfig() {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
            LANGUAGE = properties.getProperty("language", "en"); // Default to "en" if not found
			CARD_LANGUAGE = properties.getProperty("card_language", "en"); // Default to "en" if not found
			CARD_PREVIEW_SCALE = Double.parseDouble(properties.getProperty("card_preview_scale", "1.0")); // Default to 1.0 if not found
			CARD_ICON_SCALE = Double.parseDouble(properties.getProperty("card_icon_scale", "1.5")); // Default to 1.5 if not found
			Config.CARD_PREVIEW_WIDTH = (int) (400 * Config.CARD_PREVIEW_SCALE);
            Config.CARD_PREVIEW_HEIGHT = (int) (Config.CARD_PREVIEW_WIDTH * Config.CARD_RATIO);
			System.out.println(LANGUAGE);
        } catch (IOException e) {
            System.err.println("Could not load config file. Using default settings.");
        }
    }

    // Save the language setting to the config file
    public static void saveConfig() {
        Properties properties = new Properties();
        properties.setProperty("language", LANGUAGE);
        properties.setProperty("card_language", CARD_LANGUAGE);
        properties.setProperty("card_preview_scale", String.valueOf(CARD_PREVIEW_SCALE));
        properties.setProperty("card_icon_scale", String.valueOf(CARD_ICON_SCALE));
		Config.CARD_PREVIEW_WIDTH = (int) (400 * Config.CARD_PREVIEW_SCALE);
        Config.CARD_PREVIEW_HEIGHT = (int) (Config.CARD_PREVIEW_WIDTH * Config.CARD_RATIO);
        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            properties.store(output, "Application Configuration");
        } catch (IOException e) {
            System.err.println("Could not save config file.");
        }
    }

	public static boolean SHOW_CARD_COUNT = true;

	public static String LANGUAGE; // en or zh_TW
	public static String CARD_LANGUAGE; // en or zh_TW

	public static float CARD_RATIO = 1.3859F;
	
	public static int DW_ROW_SIZE = 8;

	public static int CARD_PREVIEW_WIDTH = ((int) (400 * CARD_PREVIEW_SCALE) == 0 ? 400 : (int) (400 * CARD_PREVIEW_SCALE));
	public static int CARD_PREVIEW_HEIGHT = (int) (CARD_PREVIEW_WIDTH * CARD_RATIO);

	public static int SMALL_CARD_WIDTH = ((int) (120 * CARD_ICON_SCALE) == 0 ? 120 : (int) (120 * CARD_ICON_SCALE));
	public static int SMALL_CARD_HEIGHT = (int) (SMALL_CARD_WIDTH * CARD_RATIO);
	
	public static int DW_CARD_WIDTH = 180;
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
