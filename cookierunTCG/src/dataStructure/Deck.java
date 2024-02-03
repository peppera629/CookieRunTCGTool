package dataStructure;

import java.util.ArrayList;
import java.util.List;

public class Deck {
	private List<Card> cardList;
	private List<Card> flipList;
	public Deck() {
		cardList = new ArrayList<Card>();
		flipList = new ArrayList<Card>();
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
			if(card.isFlip()) {
				flipList.add(card);
			}
			return true;
		}
		return false;
	}
	
	public boolean removeCard(Card card) {
		if(card.isFlip()) {
			flipList.remove(card);
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
}
