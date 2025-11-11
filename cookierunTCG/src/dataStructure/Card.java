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
import util.CardUtil;
import util.Config;

public class Card {
	private static int SERIAL_NUMBER = 0;
	private int _serial_number;
	private int _position;
	private String _pack;
	private String _id;
	private String _name;
	private CardColor _color;
	private CardType _type;
	private boolean _isFlip;
	private boolean _isExtra;
	private CardRarity _rarity;
	private String _mark;
	private int _lv;
	private int _hp;
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
			boolean flip, boolean extra, CardRarity rarity, String mark, int lv, int hp) {
		_PanelList = new ArrayList<ClickableCardPanel>();
		_serial_number = SERIAL_NUMBER++;
		_pack = pack;
		_id = id;
		_name = name;
		_color = color;
		_type = type;
		_isFlip = flip;
		_isExtra = extra;
		_rarity = rarity;
		_mark = mark;
		_lv = lv;
		_hp = hp;
		_maxCount = 4; // 4: Normal, 1: Restricted, 0: Banned
		_cardCount = 0;
		int lv_weight = CardUtil.LEVEL_MAX  - _lv + 1;
		_position = _serial_number
				+ (CardUtil.TYPE_MAX - _type.getValue()) * Config.CARD_SORT_VALUE_TYPE
				+ (_isFlip ? 0 : Config.CARD_SORT_VALUE_FLIP)
				+ lv_weight * Config.CARD_SORT_VALUE_LEVEL
				+ (CardUtil.COLOR_MAX - _color.getValue()) * Config.CARD_SORT_VALUE_COLOR 
				;
//		dump();

		_cardIcon = CardUtil.CardBack;
	}

	public synchronized void createCardLabel() {
		if (!_isImageLoaded || (_cardLanguage != null && !_cardLanguage.equals(Config.CARD_LANGUAGE))) {
			_cardImagePath = "resources/cards/"+Config.CARD_LANGUAGE+"/"+getPack()+"/"+getId()+".png";
	        ImageIcon cardIcon = new ImageIcon(_cardImagePath);
	        
	        Image image = cardIcon.getImage().getScaledInstance(Config.SMALL_CARD_WIDTH, Config.SMALL_CARD_HEIGHT,  java.awt.Image.SCALE_SMOOTH);
	        _cardIcon = new ImageIcon(image);
	        _isImageLoaded = true;
			_cardLanguage = Config.CARD_LANGUAGE;
		    for (ClickableCardPanel panel : _PanelList) {
				SwingUtilities.invokeLater(() -> {
					if (panel != null) {
						panel.updateImage();
					}
				});
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
	
	public CardColor getColor() {
		return _color;
	}

	public CardType getType() {
		return _type;
	}

	public CardRarity getRarity() {
		return _rarity;
	}

	public int getLv() {
		return _lv;
	}

	public int getHP() {
		return _hp;
	}
	
	public boolean isFlip() {
		return _isFlip;
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
	
	public void setCount(int count) {
		_cardCount = count;
	}

	public void setMaxCount(int count) {
		_maxCount = count;
	}

	public void setName(String name) {
		_name = name;
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
}
