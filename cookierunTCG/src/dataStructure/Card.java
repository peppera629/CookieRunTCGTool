package dataStructure;
import java.awt.Image;

import javax.swing.ImageIcon;

import ui.ClickableCardPanel;
import util.CardUtil.CardColor;
import util.CardUtil.CardType;
import util.Config;

public class Card {
	private static int SERIAL_NUMBER = 0;
	private int _serial_number;
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
        System.out.println(_pack + ", " + _id + ", " + _name + ", " + _color + ", " + _type + ", " + _isFlip + ", " + _rare + ", " + _mark);
		return _pack + ", " + _id + ", " + _name + ", " + _color + ", " + _type + ", " + _isFlip + ", " + _rare + ", " + _mark +", lv = "+_lv;
	}

	public int compareTo(Card card) {
		if (getSerialNumber() == card.getSerialNumber()) {
			return 0;
		} else if (getSerialNumber() > card.getSerialNumber()) {
			return 1;
		} else {
			return -1;
		}
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
