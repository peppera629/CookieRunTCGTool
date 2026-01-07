package dataStructure;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import ui.ClickableCardPanel;
import util.CardUtil.CardColor;
import util.CardUtil.CardRarity;
import util.CardUtil.CardType;
import util.CardUtil.FlipType;
import util.CardUtil.SkillType;
import util.CardUtil.Keyword;
import util.CardUtil;
import util.Config;
import util.AppPaths;

public class Card {
	private static int SERIAL_NUMBER = 0;
	private int _serial_number;
	private int _position;
	private String _pack;
	private String _id;
	private String _name;
	private List<String> _name_by_lang = new ArrayList<String>();
	private List<List<String>> _alt_names = new ArrayList<List<String>>();
	private CardColor _color;
	private CardType _type;
	private boolean _isFlip;
	private FlipType _flipType;
	private boolean _isExtra;
	private CardRarity _rarity;
	private CardRarity[] _variants;
	private List<boolean[]> _availability; // [][0]: EN, [][1]: TW, [][2]: KR, list size = number of variants (inc. base)
	private String[] _variantNames;
	private String _mark;
	private int _lv;
	private int _hp;
	private int _attackDMG;
	private int _attackCost;
	private float _attackEfficiency = 0; // attackDMG / attackCost (0 if attack cost is not available)
	private int _peakDMG; // "All of your opponent's Cookies receive X damage" count as 2X, self-damage excluded 
	private int _peakCost; // Cost for best-case scenario damage
	private float _peakEfficiency = 0; // peakDMG / peakCost (0 if peak cost is not available)
	private List<SkillType> _skillType;
	private Keyword _keyword;
	private int _maxCount;
	private String _cardLanguage = "";
	private ImageIcon _cardIcon;
	private String _cardImagePath;
	private int _cardCount;
	private List<ClickableCardPanel> _PanelList;
	private boolean _isImageLoaded = false;
	private String _translationSkillName = "";
	private String _translationSkill = "";
	private String _translationAttackName = "";
	private String _translationAttackCost = "";
	private String _translationAttack = "";
	private String _translationAttackThen = "";
	private String _translationFlip = "";
	
	public Card(String pack, String id, String name, CardColor color, CardType type,
			boolean flip, FlipType flipType, boolean extra, CardRarity rarity, String mark, int lv, int hp, List<SkillType> skillType, Keyword keyword) {
		_PanelList = new ArrayList<ClickableCardPanel>();
		_serial_number = SERIAL_NUMBER++;
		_pack = pack;
		_id = id;
		_name = name;
		_color = color; // In order: 0 red, 1 yellow, 2 green, 3 blue, 4 purple, 5 colorless
		_type = type; // In order: 0 Cookie, 1 Item, 2 Trap, 3 Stage
		_isFlip = flip;
		_flipType = flipType;
		_isExtra = extra;
		_rarity = rarity;
		_mark = mark; // Currently unused
		_lv = lv;
		_hp = hp;
		_skillType = skillType;
		_keyword = keyword;
		_maxCount = 4; // 4: Normal, 1: Restricted, 0: Banned
		_cardCount = 0;
		int lv_weight = CardUtil.LEVEL_MAX  - _lv + 1; // Lv.1: 3, Lv2: 2, Lv.3: 1 (for ascending order)
		_position = _serial_number
				+ (CardUtil.TYPE_MAX - _type.getValue()) * Config.CARD_SORT_VALUE_TYPE // Type descending
				+ (_isFlip ? 0 : Config.CARD_SORT_VALUE_FLIP) // Flip first
				+ (_isExtra ? 0 : Config.CARD_SORT_VALUE_EXTRA) // Extra first
				+ lv_weight * Config.CARD_SORT_VALUE_LEVEL // Level descending
				+ (CardUtil.COLOR_MAX - _color.getValue()) * Config.CARD_SORT_VALUE_COLOR // Color descending 
				;
//		dump();

		_cardIcon = CardUtil.CardBack;
		//System.out.println("Created card: " + _id + " - " + _serial_number + " - Position: " + _position + "- Is Extra: " + _isExtra);
	}

	public synchronized void createCardLabel() {
		if (!_isImageLoaded || (_cardLanguage != null && !_cardLanguage.equals(Config.CARD_LANGUAGE))) {
			for (String lang : Config.FALLBACK_ORDER) {
				_cardImagePath = AppPaths.dataDir().resolve("cards/"+lang+"/"+getPack()+"/"+getId()+".png").toString();
		        ImageIcon cardIcon = new ImageIcon(_cardImagePath);
		        if (cardIcon.getIconWidth() > 0 && cardIcon.getIconHeight() > 0) {
			        Image image = cardIcon.getImage().getScaledInstance(Config.SMALL_CARD_WIDTH, Config.SMALL_CARD_HEIGHT,  java.awt.Image.SCALE_SMOOTH);
			        _cardIcon = new ImageIcon(image);
			        _isImageLoaded = true;
					_cardLanguage = Config.CARD_LANGUAGE;
					//System.out.println("Loaded image for card " + getId() + " in language: " + lang);
				    for (ClickableCardPanel panel : _PanelList) {
						SwingUtilities.invokeLater(() -> {
							if (panel != null) {
								panel.updateImage();
							}
						});
					}
					break;
		        }
			}
		}
	}

	public String dump() {
        System.out.println(_pack + ", " + _id + ", " + _name + ", " + _color + ", " + _type + ", " 
	+ _isFlip + ", " + _rarity + ", " + _mark +", lv = "+_lv+", HP = "+_hp+"      : "+_position);
		return _pack + ", " + _id + ", " + _name + ", " + _color + ", " + _type + ", " 
	+ _isFlip + ", " + _rarity + ", " + _mark +", lv = "+_lv+", HP = "+_hp+"      : "+_position;
	}

	public int compareTo(Card card) {
		if (getCardDefaultPosition() == card.getCardDefaultPosition()) {
			return 0;
		} else if (getCardDefaultPosition() < card.getCardDefaultPosition()) {
			return 1;
		} else {
			return -1;
		}
		/*if (getSerialNumber() == card.getSerialNumber()) {
			return 0;
		} else if (getSerialNumber() > card.getSerialNumber()) {
			return 1;
		} else {
			return -1;
		}*/
	}

	public int getCardDefaultPosition() {
		int lv_weight = CardUtil.LEVEL_MAX  - _lv + 1;
		if (_lv == 0) {
			lv_weight = 0;
		}
		_position = _serial_number
				+ (CardUtil.TYPE_MAX - _type.getValue()) * Config.CARD_SORT_VALUE_TYPE
				+ (_isFlip ? 0 : Config.CARD_SORT_VALUE_FLIP)
				+ (_isExtra ? 0 : Config.CARD_SORT_VALUE_EXTRA)
				+ lv_weight * Config.CARD_SORT_VALUE_LEVEL
				+ (CardUtil.COLOR_MAX - _color.getValue()) * Config.CARD_SORT_VALUE_COLOR 
				;
		// dump();
		return _position;
	}
	
	public int getSerialNumber() {
		return _serial_number;
	}
	
	public String getPack() {
		return _pack;
	}
	
	public String getId() {
		return _id;
	}
	
	public String getName() {
		return _name;
	}

	public List<String> getNameByLang() {
		return _name_by_lang;
	}

	public List<List<String>> getAltNames() {
		return _alt_names;
	}
	
	public CardColor getColor() {
		return _color;
	}

	public CardType getType() {
		return _type;
	}

	public CardRarity getRarity() {
		return _rarity;
	}

	public CardRarity[] getVariants() {
		return _variants;
	}

	public String[] getVariantNames() {
		return _variantNames;
	}

	public int getLv() {
		return _lv;
	}

	public int getHP() {
		return _hp;
	}

	public List<SkillType> getSkillType() {
		return _skillType;
	}

	public Keyword getKeyword() {
		return _keyword;
	}

	public int getAttackDMG() {
		return _attackDMG;
	}
	public int getAttackCost() {
		return _attackCost;
	}
	public int getPeakDMG() {
		return _peakDMG;
	}
	public int getPeakCost() {
		return _peakCost;
	}
	public float getAttackEfficiency() {
		return _attackEfficiency;
	}
	public float getPeakEfficiency() {
		return _peakEfficiency;
	}

	public boolean getOwnershipLegality() {
		return (Collection.getInstance().getCardTotalOwnedCount(_id, true) >= _cardCount);
	}
	
	public boolean isFlip() {
		return _isFlip;
	}

	public FlipType getFlipType() {
		return _flipType;
	}

	public boolean isExtra() {
		return _isExtra;
	}

	public ImageIcon getcardIcon() {
		if (!_isImageLoaded || (_cardLanguage != null && !_cardLanguage.equals(Config.CARD_LANGUAGE))) {
			CardLoader.loadCardImage(this);
		}
		return _cardIcon;
	}

	public ImageIcon getResizedCardImage(int w, int h) {
        ImageIcon cardIcon = new ImageIcon(_cardImagePath);
        
        Image image = cardIcon.getImage().getScaledInstance(w, h,  java.awt.Image.SCALE_SMOOTH);
		return new ImageIcon(image);
	}
	
	public ImageIcon getOriginalSizeImage() {
		return new ImageIcon(_cardImagePath);
	}
	
	public int getCount() {
		return _cardCount;
	}

	public int getMaxCount() {
		return _maxCount;
	}
	
	public boolean getImageLoadStatus() {
		return _isImageLoaded;
	}

	public List<boolean[]> getAvailabilityArray() {
		if (_availability == null) {
			_availability = new ArrayList<boolean[]>();
		}
		return _availability;
	}

	public boolean[] getAvailability(int variantId) {
		if (_availability == null) {
			_availability = new ArrayList<boolean[]>();
			while (_availability.size() < _variants.length + 1) { // Assume all previous variants are available in all languages
				_availability.add(new boolean[] {true, true, true});
			}
			return new boolean[] {true, true, true};
		}
		while (_availability.size() <= variantId) { // Assume all previous variants are available in all languages
			_availability.add(new boolean[] {true, true, true});
		}
		return _availability.get(variantId);
	}

	public void setAvailability(int variantId, boolean[] availability) {
		if (_availability == null) {
			_availability = new ArrayList<boolean[]>();
		}
		while (_availability.size() <= variantId) { // Assume all previous variants are available in all languages
			_availability.add(new boolean[] {true, true, true});
		}
		_availability.set(variantId, availability);
	}

	public void setCount(int count) {
		_cardCount = count;
	}

	public void setMaxCount(int count) {
		_maxCount = count;
	}

	public void setName(String name) {
		_name = name;
	}

	public void addAltNames(List<String> altNames) {
		_alt_names.add(altNames);
	}

	public void addToNameByLang(String name) {
		_name_by_lang.add(name);
	}

	public void setVariantTypes(CardRarity[] variants) {
		_variants = variants;
	}
	
	public void setVariantNames(String[] variantNames) {
		_variantNames = variantNames;
	}

	public void setAttackAttributes(int attackCost, int attackDMG, int peakCost, int peakDMG) {
		_attackCost = attackCost;
		_attackDMG = attackDMG;
		_peakCost = peakCost;
		_peakDMG = peakDMG;
		if (_attackCost > 0) {
			_attackEfficiency = Math.round((float)_attackDMG * 100.0f / (float)_attackCost) / 100.0f;
		}
		if (_peakCost > 0) {
			_peakEfficiency = Math.round((float)_peakDMG * 100.0f / (float)_peakCost) / 100.0f;
		}
	}
	
	public void add() {
		_cardCount++;
	}
	
	public void minus() {
		_cardCount--;
	}
	
	public void addPanel(ClickableCardPanel panel) {
		if (!_PanelList.contains(panel)){
			_PanelList.add(panel);
		}
	}
	
	public void removePanel(ClickableCardPanel panel) {
		_PanelList.remove(panel);
	}

	public List<ClickableCardPanel> getPanels() {
		return _PanelList;
	}

	public void setCardTranslation(String skillName, String skill, String attackName, String attackCost, String attack, String attackThen, String flip) {
		_translationSkillName = skillName;
		_translationSkill = skill;
		_translationAttackName = attackName;
		_translationAttackCost = attackCost;
		_translationAttack = attack;
		_translationAttackThen = attackThen;
		_translationFlip = flip;
	}

	public boolean cardTranslationAvailable() {
		return !_translationSkillName.equals("") || !_translationSkill.equals("") || !_translationAttackName.equals("")
				|| !_translationAttackCost.equals("") || !_translationAttack.equals("") || !_translationAttackThen.equals("")
				|| !_translationFlip.equals("");
	}

	public String[] getCardTranslation() {
		String[] translation = new String[7];
		translation[0] = _translationSkillName;
		translation[1] = _translationSkill;
		translation[2] = _translationAttackName;
		translation[3] = _translationAttackCost;
		translation[4] = _translationAttack;
		translation[5] = _translationAttackThen;
		translation[6] = _translationFlip;
		return translation;
	}

	public void clearCardTranslation() {
		_translationSkillName = "";
		_translationSkill = "";
		_translationAttackName = "";
		_translationAttackCost = "";
		_translationAttack = "";
		_translationAttackThen = "";
		_translationFlip = "";
	}
}
