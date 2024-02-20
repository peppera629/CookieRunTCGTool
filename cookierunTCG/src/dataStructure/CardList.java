package dataStructure;

import java.util.ArrayList;
import java.util.List;

import util.CardUtil;
import util.CardUtil.CardType;

public class CardList {
	private static CardList instance;
	private List<Card> cardList;
	private List<Card> selectList;

	private String _id;
	private String _name;
	private boolean _search_color[];
	private boolean _search_type[];
	private boolean _search_lv[];
	private boolean _search_flip;
	private List<String> _search_pack_list;
	
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
		_search_lv = new boolean[CardUtil.LEVEL_MAX + 1];
		_search_flip = false;
		_search_pack_list = new ArrayList<String>();
	}
	
	public List<Card> getAllCards() {
		return cardList;
	}
	
	public List<Card> getSelectCards() {	
		boolean selectColor = isSelectedColor();
		boolean selectType = isSelectedType();
		boolean selectLv = isSelectedLv();
		if (!selectColor && !selectType && !_search_flip && _search_pack_list.size() == 0) {
			return getAllCards();
		}
		
		selectList.clear();
		boolean colorCorrect;
		boolean typeCorrect;
		boolean flipCorrect;
		boolean lvCorrect;
		boolean packCorrect;
		dumpPackList();
		for (Card c: cardList) {
			colorCorrect = !selectColor || _search_color[c.getColor().getValue()];
			typeCorrect = !selectType || _search_type[c.getType().getValue()];
			lvCorrect = !selectLv || !_search_type[CardType.Cookie.getValue()]
					|| c.getType() != CardType.Cookie || _search_lv[c.getLv()];
			flipCorrect = !_search_flip || c.isFlip();
			packCorrect = _search_pack_list.size() == 0 || _search_pack_list.contains(c.getPack());
//			c.dump();
			if (colorCorrect && lvCorrect && typeCorrect && flipCorrect && packCorrect) {
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
	
	private boolean isSelectedLv() {
		for (int i=0; i<=CardUtil.LEVEL_MAX; i++) {
			if(_search_lv[i]) {
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
	
	public void setLv(int lv, boolean enabled) {
		_search_lv[lv] = enabled;
	}

	public void setFlip(boolean enabled) {
		_search_flip = enabled;
	}

	public void setPack(String pack, boolean enabled) {
		if (enabled && !_search_pack_list.contains(pack)) {
			_search_pack_list.add(pack);
		}
		if (!enabled && _search_pack_list.contains(pack)) {
			_search_pack_list.remove(pack);
		}
	}
	
	private void dumpPackList() {
		System.out.println(">>> "+_search_pack_list.size());
		for(String s : _search_pack_list) {
			System.out.println(">>> "+s);
		}
	}
	
	public Card getCardById(String id) {
		return cardList.stream().filter(card -> id.equals(card.getId())).findFirst().orElse(null);
	}
}
