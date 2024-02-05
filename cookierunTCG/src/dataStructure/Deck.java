package dataStructure;

import java.util.ArrayList;
import java.util.List;

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
		int count = 0;
		for (Card c : cardList) {
			if (c.getSerialNumber() == card.getSerialNumber()) {
				count++;
			}
		}
		if (count < 4) {
			cardList.add(card);
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
			return true;
		}
		return false;
	}
	
	public boolean removeCard(Card card) {
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
	}
	
    public void sort() {
    	cardList.sort((o1, o2)
                  -> o1.compareTo(o2));
    }

    public int getCardCount() {
    	return cardList.size();
    }
    
    public int getFlipCount() {
    	return flipList.size();
    }
    
    public String getCookieSummary() {
    	String ret = "";
    	ret += "餅乾 : "+(CookieList[1].size()+CookieList[2].size()+CookieList[3].size()+CookieList[0].size());
    	ret += " ( L1 : "+CookieList[1].size();
    	ret += "   L2 : "+CookieList[2].size();
    	ret += "   L3 : "+CookieList[3].size()+" )";
    	return ret;
    }
    public String getOtherSummary() {
    	String ret = "";
    	ret += "物品 : "+ItemList.size();
    	ret += "   陷阱 : "+TrapList.size();
    	ret += "   場地 : "+StageList.size();
    	return ret;
    }
}
