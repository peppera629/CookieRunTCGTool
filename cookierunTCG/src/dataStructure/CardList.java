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
	private boolean _search_extra;
	private boolean _search_rarity[];
	private boolean _search_hp[];
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
		_search_extra = false;
		_search_rarity = new boolean[CardUtil.RARITY_MAX];
		_search_hp = new boolean[CardUtil.HP_MAX + 1];
		_search_pack_list = new ArrayList<String>();
	}
	
	public List<Card> getAllCards() {
		return cardList;
	}
	
	public List<Card> getSelectCards() {	
		boolean selectColor = isSelectedColor();
		boolean selectType = isSelectedType();
		boolean selectLv = isSelectedLv();
		boolean selectHP = isSelectedHP();
		boolean selectRarity = isSelectedRarity();
		if (!selectColor && !selectType && !_search_flip && !_search_extra && !selectRarity && !selectHP && _search_pack_list.size() == 0) {
			return getAllCards();
		}
		
		selectList.clear();
		boolean colorCorrect;
		boolean typeCorrect;
		boolean flipCorrect;
		boolean extraCorrect;
		boolean rarityCorrect;
		boolean lvCorrect;
		boolean hpCorrect;
		boolean packCorrect;
		dumpPackList();
		for (Card c: cardList) {
			colorCorrect = !selectColor || _search_color[c.getColor().getValue()];
			typeCorrect = !selectType || _search_type[c.getType().getValue()];
			// Lv. and HP conditions: if Cookie type is not selected, ignore Lv. and HP conditions
			lvCorrect = !selectLv || !_search_type[CardType.Cookie.getValue()]
					|| c.getType() != CardType.Cookie || _search_lv[c.getLv()];
			hpCorrect = !selectHP || !_search_type[CardType.Cookie.getValue()]
					|| c.getType() != CardType.Cookie || _search_hp[c.getHP()];
			//System.out.println(!selectHP+" "+!_search_type[CardType.Cookie.getValue()]+" "+(c.getType() != CardType.Cookie)+" "+_search_hp[c.getHP()]);
			flipCorrect = !_search_flip || c.isFlip();
			extraCorrect = !_search_extra || c.isExtra();
			rarityCorrect = !selectRarity || _search_rarity[c.getRarity().getValue()];
			packCorrect = _search_pack_list.size() == 0 || _search_pack_list.contains(c.getPack());
			//System.out.println(_search_hp[0]+" "+_search_hp[1]+" "+_search_hp[2]+" "+_search_hp[3]+" "+_search_hp[4]+" "+_search_hp[5]+" "+_search_hp[6]);
			//c.dump();
			if (colorCorrect && lvCorrect && hpCorrect && typeCorrect && flipCorrect && extraCorrect && rarityCorrect && packCorrect) {
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

	private boolean isSelectedHP() {
		for (int i=1; i<CardUtil.HP_MAX+1; i++) {
			if(_search_hp[i]) {
				return true;
			}
		}
		return false;
	}

	private boolean isSelectedRarity() {
		for (int i=0; i<CardUtil.RARITY_MAX; i++) {
			if(_search_rarity[i]) {
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

	public void setExtra(boolean enabled) {
		_search_extra = enabled;
	}

	public void setRarity(int id, boolean enabled) {
		_search_rarity[id] = enabled;
	}

	public void setHP(int hp, boolean enabled) {
		_search_hp[hp] = enabled;
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
