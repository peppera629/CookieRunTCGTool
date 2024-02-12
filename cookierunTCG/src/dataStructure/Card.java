package dataStructure;
import java.awt.Image;

import javax.swing.ImageIcon;

import ui.ClickableCardPanel;
import util.CardUtil.CardColor;
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
	private String _rare;
	private String _mark;
	private int _lv;
	private ImageIcon _cardIcon;
	private String _cardImagePath;
	private int _cardCount;
	public Card(String pack, String id, String name, CardColor color, CardType type,
			boolean flip, String rare, String mark, int lv) {
		_serial_number = SERIAL_NUMBER++;
		_pack = pack;
		_id = id;
		_name = name;
		_color = color;
		_type = type;
		_isFlip = flip;
		_rare = rare;
		_mark = mark;
		_lv = lv;
		_cardCount = 0;
		_position = _serial_number
				+ (CardUtil.TYPE_MAX - _type.getValue()) * 100000000
				+ (_isFlip ? 0 : 10000000)
				+ (CardUtil.LEVEL_MAX - _lv) * 1000000
				+ (CardUtil.COLOR_MAX - _color.getValue()) * 100000 
				;
//		dump();
		createCardLabel();
	}

	private void createCardLabel() {
		_cardImagePath = "resources/cards/"+getPack()+"/"+getId()+".png";
        ImageIcon cardIcon = new ImageIcon(_cardImagePath);
        
        Image image = cardIcon.getImage().getScaledInstance(Config.SMALL_CARD_WIDTH, Config.SMALL_CARD_HEIGHT,  java.awt.Image.SCALE_SMOOTH);
        _cardIcon = new ImageIcon(image);
	}

	public String dump() {
        System.out.println(_pack + ", " + _id + ", " + _name + ", " + _color + ", " + _type + ", " 
	+ _isFlip + ", " + _rare + ", " + _mark +", lv = "+_lv+"      : "+_position);
		return _pack + ", " + _id + ", " + _name + ", " + _color + ", " + _type + ", " 
	+ _isFlip + ", " + _rare + ", " + _mark +", lv = "+_lv+"      : "+_position;
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

	public int getLv() {
		return _lv;
	}
	
	public boolean isFlip() {
		return _isFlip;
	}

	public ImageIcon getcardIcon() {
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
	
	public void setCount(int count) {
		_cardCount = count;
	}
	
	public void add() {
		_cardCount++;
	}
	
	public void minus() {
		_cardCount--;
	}
	
}
