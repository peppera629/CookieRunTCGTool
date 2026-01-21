package util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.awt.Toolkit;

public class Config {

	private static final String CONFIG_FILE = AppPaths.configDir().resolve("config.txt").toString();
	public static double CARD_PREVIEW_SCALE;
	public static double CARD_ICON_SCALE;
	// UI scale is relative to 1080p (1920x1080 is 1x scale, but 0.1x minimum)
	public static double UI_SCALE = Math.max(0.5f + 0.5f * Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 1080.0, Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 1080.0); 

	// Load the language setting from the config file
    public static void loadConfig() {
		System.out.println("Loading config from " + CONFIG_FILE);
        Properties properties = new Properties();
        try {
			InputStream input = new FileInputStream(CONFIG_FILE);
			properties.load(input);
			input.close();
		} catch (IOException e) {
			System.err.println("Could not load config file. Using default settings.");
		}
		LANGUAGE = properties.getProperty("language", "en"); // Default to "en" if not found
		CARD_LANGUAGE = properties.getProperty("card_language", "en"); // Default to "en" if not found
		REGION = properties.getProperty("region", "NA"); // Default to "NA" if not found
		System.out.println("Current region: " + REGION);
		switch (CARD_LANGUAGE) {
			case "en":
				FALLBACK_ORDER = new String[] {"en", "kr", "zh_TW"};
				break;
			case "zh_TW":
				FALLBACK_ORDER = new String[] {"zh_TW", "en", "kr"};
				break;
			case "kr":
				FALLBACK_ORDER = new String[] {"kr", "en", "zh_TW"};
				break;
		}
		switch (REGION) {
			case "KR":
				LEGAL_LANGUAGES = new String[] {"kr"};
				COLLECTION_LANGUAGE_ORDER = new String[] {"kr", "en", "zh_TW"};
				COLLECTION_LANGUAGE_INDICES = new int[] {2, 0, 1};
				break;
			case "TW":
				LEGAL_LANGUAGES = new String[] {"zh_TW", "en"};
				COLLECTION_LANGUAGE_ORDER = new String[] {"zh_TW", "en", "kr"};
				COLLECTION_LANGUAGE_INDICES = new int[] {1, 0, 2};
				break;
			case "SEA":
				LEGAL_LANGUAGES = new String[] {"en"};
				COLLECTION_LANGUAGE_ORDER = new String[] {"en", "kr", "zh_TW"};
				COLLECTION_LANGUAGE_INDICES = new int[] {0, 2, 1};
				break;
			case "NA":
				LEGAL_LANGUAGES = new String[] {"en"};
				COLLECTION_LANGUAGE_ORDER = new String[] {"en", "kr", "zh_TW"};
				COLLECTION_LANGUAGE_INDICES = new int[] {0, 2, 1};
				break;
		}
		System.out.println("Legal card languages: " + String.join(", ", LEGAL_LANGUAGES));
		FALLBACK_ORDER = (CARD_LANGUAGE.equals("en")) ? new String[] {"en", "kr", "zh_TW"} : new String[] {"zh_TW", "en", "kr"};
		CARD_PREVIEW_SCALE = Double.parseDouble(properties.getProperty("card_preview_scale", "1.0")); // Default to 1.0 if not found
		CARD_ICON_SCALE = Double.parseDouble(properties.getProperty("card_icon_scale", "1.0")); // Default to 1.0 if not found
		CARD_PREVIEW_WIDTH = (int) (400 * Config.CARD_PREVIEW_SCALE * UI_SCALE);
		CARD_PREVIEW_HEIGHT = (int) (Config.CARD_PREVIEW_WIDTH * Config.CARD_RATIO);
		CARD_TRANSLATION_ENABLED = Boolean.parseBoolean(properties.getProperty("card_translation", "true")); // Default to true if not found
		LARGE_TRANSLATION_TEXT = Boolean.parseBoolean(properties.getProperty("large_translation_text", "false")); // Default to false if not found
		DECK_BUILD_FROM_COLLECTION = Boolean.parseBoolean(properties.getProperty("deck_build_from_collection", "false")); // Default to false if not found
		SHOW_COLLECTION_CHANGE = Boolean.parseBoolean(properties.getProperty("show_collection_change", "true")); // Default to true if not found
		SHOW_OWNED_ONLY = Boolean.parseBoolean(properties.getProperty("show_owned_only", "false")); // Default to false if not found
		ADVANCED_FILTERING = Boolean.parseBoolean(properties.getProperty("advanced_filtering", "false")); // Default to false if not found
		SHOW_ONLY_LEGAL_IN_COLLECTION = Boolean.parseBoolean(properties.getProperty("show_only_legal_in_collection", "false")); // Default to false if not found
    }

    // Save the language setting to the config file
    public static void saveConfig() {
        Properties properties = new Properties();
        properties.setProperty("language", LANGUAGE);
        properties.setProperty("card_language", CARD_LANGUAGE);
		properties.setProperty("region", REGION);
		switch (CARD_LANGUAGE) {
			case "en":
				FALLBACK_ORDER = new String[] {"en", "kr", "zh_TW"};
				break;
			case "zh_TW":
				FALLBACK_ORDER = new String[] {"zh_TW", "en", "kr"};
				break;
			case "kr":
				FALLBACK_ORDER = new String[] {"kr", "en", "zh_TW"};
				break;
		}
		switch (REGION) {
			case "KR":
				LEGAL_LANGUAGES = new String[] {"kr"};
				COLLECTION_LANGUAGE_ORDER = new String[] {"kr", "en", "zh_TW"};
				COLLECTION_LANGUAGE_INDICES = new int[] {2, 0, 1};
				break;
			case "TW":
				LEGAL_LANGUAGES = new String[] {"zh_TW", "en"};
				COLLECTION_LANGUAGE_ORDER = new String[] {"zh_TW", "en", "kr"};
				COLLECTION_LANGUAGE_INDICES = new int[] {1, 0, 2};
				break;
			case "SEA":
				LEGAL_LANGUAGES = new String[] {"en"};
				COLLECTION_LANGUAGE_ORDER = new String[] {"en", "kr", "zh_TW"};
				COLLECTION_LANGUAGE_INDICES = new int[] {0, 2, 1};
				break;
			case "NA":
				LEGAL_LANGUAGES = new String[] {"en"};
				COLLECTION_LANGUAGE_ORDER = new String[] {"en", "kr", "zh_TW"};
				COLLECTION_LANGUAGE_INDICES = new int[] {0, 2, 1};
				break;
		}
        properties.setProperty("card_preview_scale", String.valueOf(CARD_PREVIEW_SCALE));
        properties.setProperty("card_icon_scale", String.valueOf(CARD_ICON_SCALE));
		Config.CARD_PREVIEW_WIDTH = (int) (400 * Config.CARD_PREVIEW_SCALE * UI_SCALE);
        Config.CARD_PREVIEW_HEIGHT = (int) (Config.CARD_PREVIEW_WIDTH * Config.CARD_RATIO);
		properties.setProperty("card_translation", String.valueOf(CARD_TRANSLATION_ENABLED));
		properties.setProperty("large_translation_text", String.valueOf(LARGE_TRANSLATION_TEXT));
		properties.setProperty("deck_build_from_collection", String.valueOf(DECK_BUILD_FROM_COLLECTION));
		properties.setProperty("show_collection_change", String.valueOf(SHOW_COLLECTION_CHANGE));
		properties.setProperty("show_owned_only", String.valueOf(SHOW_OWNED_ONLY));
		properties.setProperty("advanced_filtering", String.valueOf(ADVANCED_FILTERING));
		properties.setProperty("show_only_legal_in_collection", String.valueOf(SHOW_ONLY_LEGAL_IN_COLLECTION));
        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            properties.store(output, "Application Configuration");
        } catch (IOException e) {
            System.err.println("Could not save config file.");
        }
    }

	public static int getLangIndex(String lang) {
		for (int i = 0; i < ALL_LANGUAGES.length; i++) {
			if (ALL_LANGUAGES[i].equals(lang)) {
				return i;
			}
		}
		return -1; // Not found
	}

	public static boolean SHOW_CARD_COUNT = true;

	public static String LANGUAGE; // en or zh_TW
	public static String CARD_LANGUAGE; // en or zh_TW
	public static String REGION; // KR, TW, SEA, NA
	public static String[] ALL_LANGUAGES = {"en", "zh_TW"};
	public static String[] ALL_CARD_LANGUAGES = {"en", "zh_TW", "kr"};
	public static String[] LEGAL_LANGUAGES;
	public static String[] COLLECTION_LANGUAGE_ORDER;
	public static int[] COLLECTION_LANGUAGE_INDICES;
	public static String[] FALLBACK_ORDER;

	public static float CARD_RATIO = 1.3859F;
	public static float COST_ICON_SCALE = 0.5F; // Original size is 48px high

	public static int DW_ROW_SIZE = 8;

	public static int CARD_PREVIEW_WIDTH = ((int) (400 * CARD_PREVIEW_SCALE * UI_SCALE) == 0 ? 400 : (int) (400 * CARD_PREVIEW_SCALE * UI_SCALE));
	public static int CARD_PREVIEW_HEIGHT = (int) (CARD_PREVIEW_WIDTH * CARD_RATIO);

	public static int SMALL_CARD_WIDTH = ((int) (120 * CARD_ICON_SCALE * UI_SCALE) == 0 ? 120 : (int) (120 * CARD_ICON_SCALE * UI_SCALE));
	public static int SMALL_CARD_HEIGHT = (int) (SMALL_CARD_WIDTH * CARD_RATIO);
	
	public static int DW_CARD_WIDTH = (int) (150 * UI_SCALE);
	public static int DW_CARD_HEIGHT = (int) (DW_CARD_WIDTH * CARD_RATIO);
	
	public static int DW_OUTPUT_WIDTH = 400;
	public static int DW_OUTPUT_HEIGHT = (int) (DW_OUTPUT_WIDTH * CARD_RATIO);

	public static boolean CARD_TRANSLATION_ENABLED = true;
	public static boolean LARGE_TRANSLATION_TEXT = false;
	public static boolean DECK_BUILD_FROM_COLLECTION = false;
	public static boolean SHOW_COLLECTION_CHANGE = true;
	public static boolean SHOW_ONLY_LEGAL_IN_COLLECTION = false;
	public static boolean SHOW_OWNED_ONLY = false;
	public static boolean ADVANCED_FILTERING = false;

	// ========================= sort config ========================
	public static final String SORT_NAME_TYPE = "卡片類型"; 
	public static final String SORT_NAME_FLIP = "是否為FLIP"; 
	public static final String SORT_NAME_EXTRA = "是否為EXTRA";
	public static final String SORT_NAME_LEVEL = "卡片等級"; 
	public static final String SORT_NAME_COLOR = "卡片顏色"; 
	
	public static final int CARD_SORT_SIZE_TYPE = 3;
	public static final int CARD_SORT_SIZE_FLIP = 1;
	public static final int CARD_SORT_SIZE_EXTRA = 1;
	public static final int CARD_SORT_SIZE_LEVEL = 2;
	public static final int CARD_SORT_SIZE_COLOR = 3;

	public static int CARD_SORT_VALUE_EXTRA = 2 << 29;
	public static int CARD_SORT_VALUE_TYPE = 2 << 26;
	public static int CARD_SORT_VALUE_FLIP = 2 << 25;
	public static int CARD_SORT_VALUE_LEVEL = 2 << 23;
	public static int CARD_SORT_VALUE_COLOR = 2 << 20;
	
	public static int CARD_SORT_ORDER_TYPE = 2;
	public static int CARD_SORT_ORDER_FLIP = 3;
	public static int CARD_SORT_ORDER_EXTRA = 1;
	public static int CARD_SORT_ORDER_LEVEL = 4;
	public static int CARD_SORT_ORDER_COLOR = 5;
}
