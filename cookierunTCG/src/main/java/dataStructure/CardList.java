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
	private int _search_flip;
	private boolean _search_flip_type[];
	private boolean _search_extra;
	private boolean _search_rarity[];
	private boolean _search_hp[];
	private boolean _search_awaken_hp[];
	private boolean _search_skill_type[];
	private boolean _search_keyword[];
	private boolean _search_attackDMG[];
	private boolean _search_attackCost[];
	private boolean _search_avgDMG[];
	private boolean _search_peakDMG[];
	private boolean _search_status[];
	private String _search_name;
	private List<String> _search_pack_list;
	private boolean _search_variants_sec;
	private boolean _search_variants_promo;
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
		_search_flip = 0;
		_search_flip_type = new boolean[3];
		_search_extra = false;
		_search_rarity = new boolean[CardUtil.RARITY_MAX];
		_search_variants_sec = false;
		_search_variants_promo = false;
		_search_hp = new boolean[CardUtil.HP_MAX + 1];
		_search_awaken_hp = new boolean[CardUtil.AWAKEN_HP.size()];
		_search_skill_type = new boolean[CardUtil.SKILL_TYPE_MAX];
		_search_keyword = new boolean[CardUtil.KEYWORD_MAX];
		_search_attackDMG = new boolean[CardUtil.ATTACK_MAX + 1];
		_search_attackCost = new boolean[CardUtil.ATTACK_COST_MAX + 1];
		_search_avgDMG = new boolean[CardUtil.PEAK_MAX + 1];
		_search_peakDMG = new boolean[CardUtil.PEAK_MAX + 1];
		_search_status = new boolean[3];
		_search_pack_list = new ArrayList<String>();
	}
	
	public List<Card> getAllCards() {
		return cardList;
	}

	public int getCardCountByCondition(String packId, CardRarity rarity, CardColor color, CardType type, boolean legalityConstraint) {
		int total = 0;
		for (Card c : cardList) {
			if (((packId == null && !c.getPack().equals("P")) || c.getPack().equals(packId)) &&
				(rarity == null || (rarity.getValue() >= 6 ? Arrays.asList(c.getVariants()).contains(rarity) : c.getRarity() == rarity)) && // If requested rarity is a Secret Rare, check if it has that rarity
				(color == null || c.getColor() == color) &&
				(type == null || c.getType() == type) &&
				(!legalityConstraint || c.isLegal())) {
				total++;
			}
		}
		return total;
	}

	public List<Card> getOwnedCards() {
		ownedList.clear();
		for (Card c : cardList) {
			int ownedCount = Collection.getInstance().getCardTotalOwnedCount(c.getId(), true);
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
		boolean selectAwakenHP = isSelectedAwakenHP();
		boolean selectRarity = isSelectedRarity();
		boolean selectSkillType = isSelectedSkillType();
		boolean selectKeyword = isSelectedKeyword();
		boolean selectAttackDMG = isSelectedAttackDMG();
		boolean selectAttackCost = isSelectedAttackCost();
		boolean selectAvgDMG = isSelectedAvgDMG();
		boolean selectPeakDMG = isSelectedPeakDMG();
		boolean selectStatus = isSelectedStatus();
		
		if (!selectColor && !selectType && _search_flip == 0 && !_search_extra && !selectRarity && !selectHP && !selectAwakenHP && !selectSkillType && !selectKeyword && !selectAttackDMG && !selectAttackCost && !selectAvgDMG && !selectPeakDMG && !selectStatus && !_search_variants_sec && !_search_variants_promo && _search_name.equals("") && _search_pack_list.size() == 0) {
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
		boolean awakenHPCorrect;
		boolean packCorrect;
		boolean nameCorrect;
		boolean skillTypeCorrect;
		boolean keywordCorrect;
		boolean attackDMGCorrect;
		boolean attackCostCorrect;
		boolean avgDMGCorrect;
		boolean peakDMGCorrect;
		boolean statusCorrect;
		//boolean peakEfficiencyCorrect;
		boolean owned;
		//dumpPackList();
		for (Card c: cardList) {
			colorCorrect = !selectColor || _search_color[c.getColor().getValue()];
			typeCorrect = !selectType || _search_type[c.getType().getValue()];
			// Lv. and HP conditions: if Cookie type is not selected, ignore Lv. and HP conditions
			lvCorrect = !selectLv || !_search_type[CardType.Cookie.getValue()]
					|| c.getType() != CardType.Cookie || _search_lv[c.getLv()];
			hpCorrect = (!selectHP && !selectAwakenHP) || !_search_type[CardType.Cookie.getValue()]
					|| c.getType() != CardType.Cookie || (_search_hp[c.getHP()] && !c.isAwaken());
			int awakenHPIndex = CardUtil.AWAKEN_HP.indexOf(c.getHP());
			if (!c.isAwaken()) {
				awakenHPCorrect = false;
			} else {
				awakenHPCorrect = (!selectHP && !selectAwakenHP) || !_search_type[CardType.Cookie.getValue()]
					|| c.getType() != CardType.Cookie || _search_awaken_hp[awakenHPIndex];

			}
			flipCorrect = _search_flip == 0 || ( _search_flip == 1 && c.isFlip() ) || ( _search_flip == 2 && !c.isFlip() );
			flipTypeCorrect = !selectFlipType || _search_flip == 0 || !c.isFlip() || _search_flip_type[c.getFlipType().getValue()];
			extraCorrect = !_search_extra || c.isExtra();
			rarityCorrect = (c.getPack().equals("P") ? (_search_rarity[_search_rarity.length-1] || !isSelectedRarity() ? true : false) : (!selectRarity || _search_rarity[c.getRarity().getValue()]));
			boolean skillTypeCorrectCheck = false;
			if (c.getType() == CardType.Cookie) {
				for (SkillType st : c.getSkillType()) {
					if (_search_skill_type[st.getValue()]) {
						skillTypeCorrectCheck = true;
						break;
					}
				}
			}
			skillTypeCorrect = !selectSkillType || skillTypeCorrectCheck;
			keywordCorrect = !selectKeyword || _search_keyword[c.getKeyword().getValue()];
			attackDMGCorrect = !selectAttackDMG || _search_attackDMG[c.getAttackDMG()];
			attackCostCorrect = !selectAttackCost || _search_attackCost[c.getAttackCost()];
			avgDMGCorrect = !selectAvgDMG || _search_avgDMG[c.getAvgDMG()];
			peakDMGCorrect = !selectPeakDMG || _search_peakDMG[c.getPeakDMG()];
			statusCorrect = !selectStatus || (_search_status[0] && c.getMaxCount() == 4) || (_search_status[1] && c.getMaxCount() == 1) || (_search_status[2] && c.getMaxCount() == 0);
			packCorrect = _search_pack_list.size() == 0 || _search_pack_list.contains(c.getPack());
			nameCorrect = _search_name.equals("") || Normalizer.normalize(String.join(" ", c.getNameByLang()).toLowerCase() + " " + c.getId().toLowerCase(), Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "").contains(_search_name);
			hasVariants = (!_search_variants_sec && !_search_variants_promo)
					|| (c.getVariants().length > 1 && (_search_variants_sec && (Arrays.asList(c.getVariants()).contains(CardRarity.SEC) || Arrays.asList(c.getVariants()).contains(CardRarity.SSR) || Arrays.asList(c.getVariants()).contains(CardRarity.SUR) || Arrays.asList(c.getVariants()).contains(CardRarity.EXR)) 
					|| _search_variants_promo && Arrays.asList(c.getVariants()).contains(CardRarity.P)));

			owned = Collection.getInstance().getCardTotalOwnedCount(c.getId(), true) > 0;
			if (forceShowAll) {
				if (colorCorrect && lvCorrect && (hpCorrect || awakenHPCorrect) && typeCorrect && flipCorrect && flipTypeCorrect && extraCorrect && rarityCorrect && skillTypeCorrect && keywordCorrect && attackDMGCorrect && attackCostCorrect && avgDMGCorrect && peakDMGCorrect && statusCorrect && packCorrect && nameCorrect && hasVariants) {
					selectList.add(c);
				}
			} else {
				if (colorCorrect && lvCorrect && (hpCorrect || awakenHPCorrect) && typeCorrect && flipCorrect && flipTypeCorrect && extraCorrect && rarityCorrect && skillTypeCorrect && keywordCorrect && attackDMGCorrect && attackCostCorrect && avgDMGCorrect && peakDMGCorrect && statusCorrect && packCorrect && nameCorrect
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

	private boolean isSelectedAwakenHP() {
		for (int i=0; i<CardUtil.AWAKEN_HP.size(); i++) {
			if(_search_awaken_hp[i]) {
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

	private boolean isSelectedAttackDMG() {
		for (int i=0; i<CardUtil.ATTACK_MAX+1; i++) {
			if(_search_attackDMG[i]) {
				return true;
			}
		}
		return false;
	}

	private boolean isSelectedAttackCost() {
		for (int i=0; i<CardUtil.ATTACK_COST_MAX+1; i++) {
			if(_search_attackCost[i]) {
				return true;
			}
		}
		return false;
	}

	private boolean isSelectedAvgDMG() {
		for (int i=0; i<CardUtil.PEAK_MAX+1; i++) {
			if(_search_avgDMG[i]) {
				return true;
			}
		}
		return false;
	}

	private boolean isSelectedPeakDMG() {
		for (int i=0; i<CardUtil.PEAK_MAX+1; i++) {
			if(_search_peakDMG[i]) {
				return true;
			}
		}
		return false;
	}

	private boolean isSelectedStatus() {
		for (int i=0; i<3; i++) {
			if(_search_status[i]) {
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

	public void setFlip(int condition) {
		_search_flip = condition;
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

	public void setHPAwaken(int index, boolean enabled) {
		_search_awaken_hp[index] = enabled;
	}

	public void setSkillType(int id, boolean enabled) {
		_search_skill_type[id] = enabled;
	}

	public void setKeyword(int id, boolean enabled) {
		_search_keyword[id] = enabled;
	}

	public void setAttackDMG(int dmg, boolean enabled) {
		_search_attackDMG[dmg] = enabled;
	}

	public void setAttackCost(int cost, boolean enabled) {
		_search_attackCost[cost] = enabled;
	}

	public void setAvgDMG(int dmg, boolean enabled) {
		_search_avgDMG[dmg] = enabled;
	}

	public void setPeakDMG(int dmg, boolean enabled) {
		_search_peakDMG[dmg] = enabled;
	}

	public void setStatus(int statusIndex, boolean enabled) {
		_search_status[statusIndex] = enabled;
	}

	public void setPack(String pack, boolean enabled) {
		if (enabled && !_search_pack_list.contains(pack)) {
			_search_pack_list.add(pack);
		}
		if (!enabled && _search_pack_list.contains(pack)) {
			_search_pack_list.remove(pack);
		}
	}

	public void setHasSecretOnly(boolean enabled) {
		_search_variants_sec = enabled;
	}

	public void setHasPromoVariantOnly(boolean enabled) {
		_search_variants_promo = enabled;
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
