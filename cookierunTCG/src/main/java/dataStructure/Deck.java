package dataStructure;

import java.util.ArrayList;
import java.util.List;

import util.Config;
import util.CardUtil.CardColor;

public class Deck {
	private List<Card> cardList;
	private List<Card> flipList;
	private List<Card> extraList;
	private List<List<Card>> CookieList;
	private List<List<Card>> flipTypeList;
	private List<Card> ItemList;
	private List<Card> TrapList;
	private List<Card> StageList;
	
	public Deck() {
		cardList = new ArrayList<Card>();
		flipList = new ArrayList<Card>();
		flipTypeList = new ArrayList<>();
		extraList = new ArrayList<Card>();
		CookieList = new ArrayList<>();
		for(int i=0;i<=3;i++) {
			CookieList.add(new ArrayList<Card>());
		}
		for(int i=0;i<=2;i++) {
			flipTypeList.add(new ArrayList<Card>());
		}
		ItemList = new ArrayList<Card>();
		TrapList = new ArrayList<Card>();
		StageList = new ArrayList<Card>();
	}

	public List<Card> getAllCards() {
		return cardList;
	}
	
	public boolean addCard(Card card) {
		if(!Config.SHOW_CARD_COUNT) {
			int count = 0;
			for (Card c : cardList) {
				if (c.getSerialNumber() == card.getSerialNumber()) {
					count++;
				}
			}
			if (count < 4) {
				cardList.add(card);
				addToTargetList(card);
				return true;
			}
		} else {
			if (!cardList.contains(card)) {
				card.setCount(1);
				cardList.add(card);
				addToTargetList(card);
				return true;
			} else {
				if (card.getCount()<4) {
					card.add();
					return true;
				}
			}
		}
		return false;
	}
	
	private void addToTargetList(Card card) {
		switch(card.getType()) {
			case Cookie:
				if(card.isFlip()) {
					flipList.add(card);
					if(card.getFlipType() != null){
						flipTypeList.get(card.getFlipType().getValue()).add(card);
					}
				}
				if(card.isExtra()) {
					extraList.add(card);
				}
				CookieList.get(card.getLv()).add(card);
				break;
			case Item:
				ItemList.add(card);
				break;
			case Trap:
				TrapList.add(card);
				break;
			case Stage:
				StageList.add(card);
				break;
		}
	}
	
	public boolean removeCard(Card card) {
		if(!Config.SHOW_CARD_COUNT) {
			return removeFromTargetList(card);
		} else {
			if (card.getCount() > 0) {
				card.minus();
				if (card.getCount() <= 0) {
					removeFromTargetList(card);
				}
				return true;
			}
		}
		return false;
	}
		
	private boolean removeFromTargetList(Card card) {
		switch(card.getType()) {
			case Cookie:
				if(card.isFlip()) {
					flipList.remove(card);
					if(card.getFlipType() != null){
						flipTypeList.get(card.getFlipType().getValue()).remove(card);
					}
				}
				if(card.isExtra()) {
					extraList.remove(card);
				}
				CookieList.get(card.getLv()).remove(card);
				break;
			case Item:
				ItemList.remove(card);
				break;
			case Trap:
				TrapList.remove(card);
				break;
			case Stage:
				StageList.remove(card);
				break;
		}
		return cardList.remove(card);
	}

	public void clear() {
		cardList.clear();
		flipList.clear();
		for(int i=0;i<=2;i++) {
			flipTypeList.get(i).clear();
		}
		extraList.clear();
		for(int i=0;i<=3;i++) {
			CookieList.get(i).clear();
		}
		ItemList.clear();
		TrapList.clear();
		StageList.clear();
	}

    public void sort() {
    	cardList.sort((o1, o2)
                  -> o1.compareTo(o2));
    }

    public int getCardArrayListSize() {
    	return cardList.size();
    }

    public int getCardCount() {
    	return getTargetCardCount(cardList);
    }
    
    public int getFlipCount() {
    	return getTargetCardCount(flipList);
    }

	public int getExtraCount() {
		return getTargetCardCount(extraList);
	}
    
    public int getTargetCardCount(List<Card> cards) {
    	if (!Config.SHOW_CARD_COUNT) {
    		return cards.size();
    	} else {
    		int count = 0;
			for (Card card : cards) {
				count += card.getCount();
			}
			return count;
    	}
    }
    
    public int[] getCookieSummary() {
    	int L0Count = getTargetCardCount(CookieList.get(0));
    	int L1Count = getTargetCardCount(CookieList.get(1));
    	int L2Count = getTargetCardCount(CookieList.get(2));
    	int L3Count = getTargetCardCount(CookieList.get(3));
		return new int[] {(L0Count + L1Count + L2Count + L3Count), L1Count, L2Count, L3Count};
    }

	public int[] getFlipTypeSummary() {
		int FlipType0Count = getTargetCardCount(flipTypeList.get(0));
		int FlipType1Count = getTargetCardCount(flipTypeList.get(1));
		int FlipType2Count = getTargetCardCount(flipTypeList.get(2));
		return new int[] {FlipType0Count, FlipType1Count, FlipType2Count};
	}

    public int[] getOtherSummary() {
    	int ItemCount = getTargetCardCount(ItemList);
    	int TrapCount = getTargetCardCount(TrapList);
    	int StageCount = getTargetCardCount(StageList);
    	return new int[] {ItemCount, TrapCount, StageCount};
    }

	public boolean getLegality() {
		for (Card card : cardList) {
			if (card.getCount() > card.getMaxCount()) {
				return false;
			} 
		}
		return true;
	}

	public List<Card> getOwnershipLegality() {
		List<Card> invalidCards = new ArrayList<Card>();
		for (Card card : cardList) {
			if (card.getCount() > Collection.getInstance().getCardTotalOwnedCount(card.getId(), true)) {
				invalidCards.add(card);
			}
		}
		return invalidCards;
	}

	public CardColor getDominantDeckColor() {
		int[] colorCount = new int[CardColor.values().length];
		for (Card card : cardList) {
			colorCount[card.getColor().getValue()] += card.getCount();
		}
		int nonZeroColors = 0;
		for (int i=0; i<colorCount.length-1; i++) {
			if (colorCount[i] > 0) {
				nonZeroColors++;
			}
		}
		int dominantColorIndex = 0;
		int maxCount = 0;
		for (int i=0; i<colorCount.length-1; i++) { // Excluding colorless
			if (colorCount[i] > maxCount) {
				maxCount = colorCount[i];
				dominantColorIndex = i;
			}
		}

		return (nonZeroColors == 1 ? CardColor.fromValue(dominantColorIndex) : CardColor.Colorless);
	}
}
