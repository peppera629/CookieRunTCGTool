package dataStructure;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ui.ClickableCardPanel;
import util.Config;
import util.CardUtil;
import util.CardUtil.*;

// Card List: singleton class to manage all cards and selected cards based on search criteria

public class CardList {
	private static CardList instance;
	private List<Card> cardList;
	private List<Card> selectList;
	private List<Card> ownedList;

	private boolean _search_color[];
	private boolean _search_type[];
	private boolean _search_lv[];
	private boolean _search_flip;
	private boolean _search_flip_type[];
	private boolean _search_extra;
	private boolean _search_rarity[];
	private boolean _search_hp[];
	private boolean _search_skill_type[];
	private boolean _search_keyword[];
	private String _search_name;
	private List<String> _search_pack_list;
	private boolean _search_variants;
	public boolean hasVariants;
	public static CardList getInstance() {
		if (instance == null) {
			instance = new CardList();
		}
		return instance;
	}
	
	private CardList() {
		selectList = new ArrayList<Card>();
		ownedList = new ArrayList<Card>();
		cardList = CardLoader.loadAllCards();
		_search_color = new boolean[CardUtil.COLOR_MAX];
		_search_type = new boolean[CardUtil.TYPE_MAX];
		_search_lv = new boolean[CardUtil.LEVEL_MAX + 1];
		_search_flip = false;
		_search_flip_type = new boolean[3];
		_search_extra = false;
		_search_rarity = new boolean[CardUtil.RARITY_MAX];
		_search_variants = false;
		_search_hp = new boolean[CardUtil.HP_MAX + 1];
		_search_skill_type = new boolean[CardUtil.SKILL_TYPE_MAX];
		_search_keyword = new boolean[CardUtil.KEYWORD_MAX];
		_search_pack_list = new ArrayList<String>();
	}
	
	public List<Card> getAllCards() {
		return cardList;
	}

	public int getCardCountByCondition(String packId, CardRarity rarity, CardColor color, CardType type) {
		int total = 0;
		for (Card c : cardList) {
			if (((packId == null && packId != "P") || c.getPack().equals(packId)) &&
				(rarity == null || (rarity.getValue() >= 6 ? Arrays.asList(c.getVariants()).contains(rarity) : c.getRarity() == rarity)) && // If requested rarity is a Secret Rare, check if it has that rarity
				(color == null || c.getColor() == color) &&
				(type == null || c.getType() == type)) {
				total++;
			}
		}
		return total;
	}

	public List<Card> getOwnedCards() {
		for (Card c : cardList) {
			int ownedCount = Collection.getInstance().getCardTotalOwnedCount(c.getId());
			if (ownedCount > 0) {
				ownedList.add(c);
			}
		}
		return ownedList;
	}
	
	public List<Card> getSelectCards(boolean forceShowAll) {	
		boolean selectColor = isSelectedColor();
		boolean selectType = isSelectedType();
		boolean selectLv = isSelectedLv();
		boolean selectFlipType = isSelectedFlipType();
		boolean selectHP = isSelectedHP();
		boolean selectRarity = isSelectedRarity();
		boolean selectSkillType = isSelectedSkillType();
		boolean selectKeyword = isSelectedKeyword();
		
		if (!selectColor && !selectType && !_search_flip && !_search_extra && !selectRarity && !selectHP && !selectSkillType && !selectKeyword && !_search_variants && _search_name.equals("") && _search_pack_list.size() == 0) {
			if (Config.SHOW_OWNED_ONLY && !forceShowAll) {
				return getOwnedCards();
			} else {
				return getAllCards();
			}
		}
		
		selectList.clear();
		boolean colorCorrect;
		boolean typeCorrect;
		boolean flipCorrect;
		boolean flipTypeCorrect;
		boolean extraCorrect;
		boolean rarityCorrect;
		boolean lvCorrect;
		boolean hpCorrect;
		boolean packCorrect;
		boolean nameCorrect;
		boolean skillTypeCorrect;
		boolean keywordCorrect;
		boolean owned;
		//dumpPackList();
		for (Card c: cardList) {
			colorCorrect = !selectColor || _search_color[c.getColor().getValue()];
			typeCorrect = !selectType || _search_type[c.getType().getValue()];
			// Lv. and HP conditions: if Cookie type is not selected, ignore Lv. and HP conditions
			lvCorrect = !selectLv || !_search_type[CardType.Cookie.getValue()]
					|| c.getType() != CardType.Cookie || _search_lv[c.getLv()];
			hpCorrect = !selectHP || !_search_type[CardType.Cookie.getValue()]
					|| c.getType() != CardType.Cookie || _search_hp[c.getHP()];
			flipCorrect = !_search_flip || c.isFlip();
			flipTypeCorrect = !selectFlipType || !_search_flip || !c.isFlip() || _search_flip_type[c.getFlipType().getValue()];
			extraCorrect = !_search_extra || c.isExtra();
			rarityCorrect = (c.getPack().equals("P") ? (_search_rarity[_search_rarity.length-1] || !isSelectedRarity() ? true : false) : (!selectRarity || _search_rarity[c.getRarity().getValue()]));
			boolean skillTypeCorrectCheck = false;
			for (SkillType st : c.getSkillType()) {
				if (_search_skill_type[st.getValue()]) {
					skillTypeCorrectCheck = true;
					break;
				}
			}
			skillTypeCorrect = !selectSkillType || skillTypeCorrectCheck;
			keywordCorrect = !selectKeyword || _search_keyword[c.getKeyword().getValue()];
			packCorrect = _search_pack_list.size() == 0 || _search_pack_list.contains(c.getPack());
			nameCorrect = _search_name.equals("") || Normalizer.normalize(String.join(" ", c.getNameByLang()).toLowerCase(), Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "").contains(_search_name);
			hasVariants = !_search_variants || (c.getVariants().length > 1);
			owned = Collection.getInstance().getCardTotalOwnedCount(c.getId()) > 0;
			if (forceShowAll) {
				if (colorCorrect && lvCorrect && hpCorrect && typeCorrect && flipCorrect && flipTypeCorrect && extraCorrect && rarityCorrect && skillTypeCorrect && keywordCorrect && packCorrect && nameCorrect && hasVariants) {
					selectList.add(c);
				}
			} else {
				if (colorCorrect && lvCorrect && hpCorrect && typeCorrect && flipCorrect && flipTypeCorrect && extraCorrect && rarityCorrect && skillTypeCorrect && keywordCorrect && packCorrect && nameCorrect
					&& (!Config.SHOW_OWNED_ONLY || owned) && hasVariants) {
					selectList.add(c);
				}
			}
			
		}
		//System.out.println("selectList size : "+selectList.size());
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

	private boolean isSelectedFlipType() {
		for (int i=0; i<3; i++) {
			if(_search_flip_type[i]) {
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

	private boolean isSelectedSkillType() {
		for (int i=0; i<CardUtil.SKILL_TYPE_MAX; i++) {
			if(_search_skill_type[i]) {
				return true;
			}
		}
		return false;
	}

	private boolean isSelectedKeyword() {
		for (int i=0; i<CardUtil.KEYWORD_MAX; i++) {
			if(_search_keyword[i]) {
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

	public void setFlipType(int id, boolean enabled) {
		_search_flip_type[id] = enabled;
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

	public void setSkillType(int id, boolean enabled) {
		_search_skill_type[id] = enabled;
	}

	public void setKeyword(int id, boolean enabled) {
		_search_keyword[id] = enabled;
	}

	public void setPack(String pack, boolean enabled) {
		if (enabled && !_search_pack_list.contains(pack)) {
			_search_pack_list.add(pack);
		}
		if (!enabled && _search_pack_list.contains(pack)) {
			_search_pack_list.remove(pack);
		}
	}

	public void setHasVariantsOnly(boolean enabled) {
		_search_variants = enabled;
	}

	public void setSearchTerm(String term) {
		_search_name = term.toLowerCase();
	}
	
	public Card getCardById(String id) {
		return cardList.stream().filter(card -> id.equals(card.getId())).findFirst().orElse(null);
	}

	public void clearCardListCount() {
		for (Card card : cardList) {
			card.setCount(0);
		}
	}

	public void updateAllCardPanels() {
		for (Card card : cardList) {
			//System.out.println(card.getId() + "/" + card.getCount());
			for (ClickableCardPanel panel : card.getPanels()) {
				if (panel.getCountShowMode() == 3) {
					//System.out.println(card.getId());
					panel.revalidate();
					panel.repaint();
				}
			}
		}
	}
}
