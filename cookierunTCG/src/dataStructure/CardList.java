package dataStructure;

import java.util.ArrayList;
import java.util.List;

import util.CardUtil;

public class CardList {
	private static CardList instance;
	private List<Card> cardList;
	private List<Card> selectList;

	private String _id;
	private String _name;
	private boolean _search_color[];
	private boolean _search_type[];
	private boolean _search_flip;
	
	public static CardList getInstance() {
		if (instance == null) {
			instance = new CardList();
		}
		return instance;
	}
	
	private CardList() {
		selectList = new ArrayList<Card>();
		cardList = CardLoader.loadAllCards();
		_search_color = new boolean[CardUtil.COLOR_MAX];
		_search_type = new boolean[CardUtil.TYPE_MAX];
		_search_flip = false;
	}
	
	public List<Card> getAllCards() {
		return cardList;
	}
	
	public List<Card> getSelectCards() {	
		boolean selectColor = isSelectedColor();
		boolean selectType = isSelectedType();
		if (!selectColor && !selectType && !_search_flip) {
			return getAllCards();
		}
		
		selectList.clear();
		boolean colorCorrect;
		boolean typeCorrect;
		boolean flipCorrect;
		for (Card c: cardList) {
			colorCorrect = !selectColor || _search_color[c.getColor().getValue()];
			typeCorrect = !selectType || _search_type[c.getType().getValue()];
			flipCorrect = !_search_flip || c.isFlip();
			c.dump();
//			System.out.println("colorCorrect : "+colorCorrect+"   typeCorrect : "+typeCorrect+"\n\n");
			if (colorCorrect && typeCorrect && flipCorrect) {
//				System.out.println("color is "+c.getColor()+"   added");
				selectList.add(c);
			}
		}
		System.out.println("selectList size : "+selectList.size());
		return selectList;
	}
	
	private boolean isSelectedColor() {
		for (int i=0; i<CardUtil.COLOR_MAX; i++) {
			if(_search_color[i]) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isSelectedType() {
		for (int i=0; i<CardUtil.TYPE_MAX; i++) {
			if(_search_type[i]) {
				return true;
			}
		}
		return false;
	}

	public void setColor(int id, boolean enabled) {
		_search_color[id] = enabled;
	}
	
	public void setType(int id, boolean enabled) {
		_search_type[id] = enabled;
	}
	
	public void setFlip(boolean enabled) {
		_search_flip = enabled;
	}
	
	public Card getCardById(String id) {
		return cardList.stream().filter(card -> id.equals(card.getId())).findFirst().orElse(null);
	}
}
