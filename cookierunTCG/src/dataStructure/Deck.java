package dataStructure;

import java.util.ArrayList;
import java.util.List;

import util.Config;

public class Deck {
	private List<Card> cardList;
	private List<Card> flipList;
	private List<Card>[] CookieList;
	private List<Card> ItemList;
	private List<Card> TrapList;
	private List<Card> StageList;
	
	public Deck() {
		cardList = new ArrayList<Card>();
		flipList = new ArrayList<Card>();
		CookieList = new ArrayList[4];
		for(int i=0;i<=3;i++) {
			CookieList[i] = new ArrayList<Card>();
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
				}
				CookieList[card.getLv()].add(card);
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
				}
				CookieList[card.getLv()].remove(card);
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
		CookieList[0].clear();
		CookieList[1].clear();
		CookieList[2].clear();
		CookieList[3].clear();
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
    	int L0Count = getTargetCardCount(CookieList[0]);
    	int L1Count = getTargetCardCount(CookieList[1]);
    	int L2Count = getTargetCardCount(CookieList[2]);
    	int L3Count = getTargetCardCount(CookieList[3]);
		return new int[] {(L0Count + L1Count + L2Count + L3Count), L1Count, L2Count, L3Count};
    }
    public int[] getOtherSummary() {
    	int ItemCount = getTargetCardCount(ItemList);
    	int TrapCount = getTargetCardCount(TrapList);
    	int StageCount = getTargetCardCount(StageList);
    	return new int[] {ItemCount, TrapCount, StageCount};
    }
}
