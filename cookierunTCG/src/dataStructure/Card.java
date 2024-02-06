package dataStructure;
import java.awt.Image;

import javax.swing.ImageIcon;

import ui.ClickableCardLabel;
import util.CardUtil.CardColor;
import util.CardUtil.CardType;

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
		dump();
		createCardLabel();
	}

	private void createCardLabel() {
        String path = "resources/cards/"+getPack()+"/"+getId()+".png";
        System.out.println("read : "+path);
        ImageIcon cardIcon = new ImageIcon(path);
        
        Image image = cardIcon.getImage().getScaledInstance(60, 84,  java.awt.Image.SCALE_SMOOTH);
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
	
}
