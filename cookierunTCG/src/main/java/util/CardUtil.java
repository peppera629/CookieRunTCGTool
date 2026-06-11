package util;

import java.util.List;

import javax.swing.ImageIcon;
import java.util.HashMap;
import java.util.Map;
import java.awt.Color;

import java.util.Locale;
import java.util.ResourceBundle;

public class CardUtil {
	public static int LEVEL_MAX = 5;
	public static List<Integer> LEVELS = List.of(1, 2, 3, 5);
	public static int COLOR_MAX = 7;
	public static int RARITY_MAX = 6;
	public static int RARITY_MAX_ALL = 11;
	public static int HP_MAX = 6;
	public static int SKILL_TYPE_MAX = 10;
	public static int KEYWORD_MAX = 5;
	public static int TYPE_MAX = 4;
	public static int ATTACK_MAX = 7;
	public static int ATTACK_COST_MAX = 5;
	public static int PEAK_MAX = 8;
	public static List<Integer> AWAKEN_HP = List.of(0, 1, 2);
	
	// For language translation
	private static ResourceBundle messages, messages_promo;

	public static void loadLanguage() {
        Locale locale;
        switch (Config.LANGUAGE) {
            case "en":
                locale = Locale.ENGLISH;
                break;
            case "zh_TW":
                locale = Locale.TRADITIONAL_CHINESE;
                break;
            default:
                locale = Locale.getDefault();
                break;
        }
        messages = ResourceBundle.getBundle("lang", locale);
		messages_promo = ResourceBundle.getBundle("lang_promo", locale);
    }

	// Static method to get translated text
    public static String getTranslation(String key) {
        if (messages == null) {
            loadLanguage(); // Ensure messages is loaded
        }
		if (!messages.containsKey(key)) {
			return "";
		}
        return messages.getString(key);
    }
	public static String getTranslationPromo(String key) {
		if (messages_promo == null) {
			loadLanguage(); // Ensure messages is loaded
		}
		if (!messages_promo.containsKey(key)) {
			return "";
		}
		return messages_promo.getString(key);
	}

	public enum CardColor {
	    Red(0), Yellow(1), Green(2), Blue(3), Purple(4), Black(5), Pure(6);
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
				case Black:
		        	return "Black";
				case Pure:
		        	return "Pure";
	        }
	        return null;
	    }

		public String getColorShort() {
	        switch(this){
		        case Red:
		        	return "R";
		        case Yellow:
		        	return "Y";
		        case Green:
		        	return "G";
		        case Blue:
		        	return "B";
		        case Purple:
		        	return "P";
				case Black:
		        	return "K";
				case Pure:
		        	return "W";
	        }
	        return null;
	    }

		public Color getAccentColor() {
			switch (this) {
				case Red:
					return new Color(215, 14, 26);
				case Yellow:
					return new Color(254, 220, 0);
				case Green:
					return new Color(0, 139, 64);
				case Blue:
					return new Color(0, 134, 207);
				case Purple:
					return new Color(89, 32, 133);
				case Black:
					return new Color(25, 25, 25);
				case Pure:
					return new Color(226, 224, 226);
			}
			return null;
		}

		public Color getForegroundColor() {
			switch(this) {
				case Yellow:
					return Color.BLACK;
				case Pure:
					return Color.BLACK;
				case Red:
					return Color.WHITE;
				case Green:
					return Color.WHITE;
				case Blue:
					return Color.WHITE;
				case Purple:
					return Color.WHITE;
				case Black:
					return Color.WHITE;
			}
			return null;
		}

		public String getDisplayName() {
            return CardUtil.getTranslation("color." + this.name().toLowerCase());
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

	public enum CardRarity {
		C(0), U(1), R(2), SR(3), UR(4), P(5), SEC(6), SSR(7), SUR(8), EXR(9), GXR(10);
	    private final int value;

	    private CardRarity(int value) {
	        this.value = value;
	    }

	    public int getValue() {
	        return value;
	    }

		public String getName() {
			return this.name();
		}

		public static CardRarity fromString(String rarityStr) {
			for (CardRarity rarity : CardRarity.values()) {
	            if (rarity.name().equals(rarityStr)) {
	                return rarity;
	            }
	        }
	        throw new IllegalArgumentException("No enum constant with name " + rarityStr);
		}

		public static CardRarity fromValue(int value) {
	        for (CardRarity rarity : CardRarity.values()) {
	            if (rarity.getValue() == value) {
	                return rarity;
	            }
	        }
	        throw new IllegalArgumentException("No enum constant with value " + value);
	    }

		public String getDisplayName() {
            return CardUtil.getTranslation("rarity." + this.name().toLowerCase());
        }

	}

	public enum FlipType {
		H(0), D(1), S(2);
	    private final int value;

	    private FlipType(int value) {
	        this.value = value;
	    }

	    public int getValue() {
	        return value;
	    }

		public static FlipType fromString(String flipTypeStr) {
			for (FlipType type : FlipType.values()) {
	            if (type.name().equals(flipTypeStr)) {
	                return type;
	            }
	        }
	        throw new IllegalArgumentException("No enum constant with name " + flipTypeStr);
		}

		public static FlipType fromValue(int value) {
	        for (FlipType type : FlipType.values()) {
	            if (type.getValue() == value) {
	                return type;
	            }
	        }
	        throw new IllegalArgumentException("No enum constant with value " + value);
		}
	}

	public enum Keyword {
		None(0), Ancient(1), Dragon(2), Arena(3), Beast(4);
		private final int value;

		private Keyword(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static Keyword fromValue(int value) {
			for (Keyword keyword : Keyword.values()) {
				if (keyword.getValue() == value) {
					return keyword;
				}
			}
			throw new IllegalArgumentException("No enum constant with value " + value);
		}

		public static Keyword fromString(String keywordStr) {
			for (Keyword keyword : Keyword.values()) {
				if (keyword.name().equals(keywordStr)) {
					return keyword;
				}
			}
			if (keywordStr.equals("None") || keywordStr.equals("_") || keywordStr.equals("")) {
				return Keyword.None;
			}
			throw new IllegalArgumentException("No enum constant with name " + keywordStr);
		}

		public String getDisplayName() {
			return CardUtil.getTranslation("keyword." + this.name().toLowerCase());
		}
	}

	public enum SkillType {
		// Represented in the card data as: _, P, PY, O, OY, A, a, B, Y, S
		None(0), Passive(1), PassiveOwnTurn(2), OnPlay(3), OnPlayOwnTurn(4), Activate(5), ActivateOncePerTurn(6), Blocker(7), ThenEffect(8), SpecialPlay(9);
		private final int value;

		private SkillType(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static SkillType fromValue(int value) {
			for (SkillType skillType : SkillType.values()) {
				if (skillType.getValue() == value) {
					return skillType;
				}
			}
			throw new IllegalArgumentException("No enum constant with value " + value);
		}

		public String getDisplayName() {
			return CardUtil.getTranslation("skilltype." + this.name().toLowerCase());
		}
	}

	public static ImageIcon CardBack;
	
	
	public static List<String> CardPack;
	public static Map<String, Map<String, Boolean>> CardPackAvailability = new HashMap<>();
}
