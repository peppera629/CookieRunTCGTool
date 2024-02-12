package dataStructure;

import java.io.BufferedReader;
import java.io.File;  // Import the File class
import java.io.FileInputStream;
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import util.Config;
import util.UIUtil;
import util.CardUtil.CardColor;
import util.CardUtil.CardType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.ImageIcon;
public class CardLoader {	
	public static ExecutorService cardImageLoadExecutor = Executors.newFixedThreadPool(10);
	
	public static void loadCardImage(Card card) {
		cardImageLoadExecutor.submit(new cardImageLoadTask(card));
	}

    static class cardImageLoadTask implements Runnable {
        private Card _card;

        public cardImageLoadTask(Card card) {
        	_card = card;
        }

        @Override
        public void run() {
        	_card.createCardLabel();
        }
    }
    
	public static List<Card> loadAllCards() {
		List<Card> cardList = new ArrayList<Card>();
	    try {
	        File file = new File("resources/card_config/pack.txt");
	        if (file.exists()) {
				FileInputStream reader = new FileInputStream(file);
		        BufferedReader input = new BufferedReader(
		                new InputStreamReader(new FileInputStream(file), "utf-8")); 
		        String data;
		        while((data= input.readLine())!=null) {
		            System.out.println(data);
	
		            if (!data.equals("") && !data.startsWith("//")) {
		            	loadPack(data, cardList);
		            }
		        } 
		        input.close();
	        }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return cardList;
	}
	
	private static void loadPack(String packName, List<Card> cardList) {
	    try {
	        File file = new File("resources/card_config/pack/"+packName+".txt");
			FileInputStream reader = new FileInputStream(file);
	        BufferedReader input = new BufferedReader(
	                new InputStreamReader(new FileInputStream(file), "utf-8")); 
            String data;
	        while((data= input.readLine())!=null) {
	            if (!data.equals("") && !data.startsWith("//")) {
	            	String[] cardData = data.split(",");
	            	CardColor color;
	            	if (cardData[2].equals("紅")) {
	            		color = CardColor.Red;
	            	} else if (cardData[2].equals("黃")) {
	            		color = CardColor.Yellow;
	            	} else if (cardData[2].equals("綠")) {
	            		color = CardColor.Green;
	            	} else {
	            		color = CardColor.Green;
	            	}

	            	int level = 0;
	            	CardType type;
	            	if (cardData[3].equals("餅乾")) {
	            		type = CardType.Cookie;
	            		if (cardData.length >7) {
	            			level = Integer.parseInt(cardData[7]);
	            		}
	            	} else if (cardData[3].equals("物品")) {
	            		type = CardType.Item;
	            	} else if (cardData[3].equals("陷阱")) {
	            		type = CardType.Trap;
	            	} else if (cardData[3].equals("場地")) {
	            		type = CardType.Stage;
	            	} else {
	            		type = CardType.Cookie;
	            	}
	            	
	            	Card c = new Card(packName, cardData[0], cardData[1], color, type, cardData[4].equals("F") ,cardData[5], cardData[6], level);
	            	cardList.add(c);
	            }
	        }
	        input.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public static Deck loadDeck(String deckName) {
		Deck deck = new Deck();
		CardList cardList = CardList.getInstance();
	    try {
	        File file = new File("deck/"+deckName+".txt");
	        if (file.exists()) {
				FileInputStream reader = new FileInputStream(file);
		        BufferedReader input = new BufferedReader(
		                new InputStreamReader(new FileInputStream(file), "utf-8")); 
		        String data;
		        while((data= input.readLine())!=null) {	
		            if (!data.equals("") && !data.startsWith("//")) {
		            	Card card = cardList.getCardById(data);
		            	deck.addCard(card);
		            }
		        } 
		        reader.close();
		        input.close();
	        }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    return deck;
	}
	
	public static void saveDeck(String deckName, Deck deck) {
		FileWriter fw;
		try {
			fw = new FileWriter("deck/"+deckName+".txt");
			List<Card> cardList = deck.getAllCards();
			for (Card c : cardList) {
				for(int i=0; i<c.getCount(); i++) {
					fw.write(c.getId()+"\n");
				}
			}
	        fw.flush();
	        fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		saveReadableDeck(deckName, deck);
	}
	
	public static void saveReadableDeck(String deckName, Deck deck) {
		FileWriter fw;
		try {
			fw = new FileWriter("deck_readable/"+deckName+".txt");
			List<Card> cardList = deck.getAllCards();
			Card lastCard = null;
			int lastCardCount = 0;
			for (Card c : cardList) {
				fw.write("["+c.getId()+"] "+c.getName());
				if (c.getCount() > 1) {
					fw.write("  x "+c.getCount()+"\n");
				} else {
					fw.write("\n");
				}
			}
			if (lastCardCount == 1) {
				fw.write("\n");
			} else if (lastCardCount > 1) {
				fw.write("  x "+lastCardCount+"\n");
			}
	        fw.flush();
	        fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static ImageIcon createCardImage(Card card, int cardSize) {
		switch (cardSize) {
			case UIUtil.CARD_SIZE_SMALL:
				return card.getcardIcon();
			case UIUtil.CARD_SIZE_DECK:
				return card.getResizedCardImage(Config.DW_CARD_WIDTH, Config.DW_CARD_HEIGHT);
			case UIUtil.CARD_SIZE_OUTPUT:
				return card.getResizedCardImage(Config.DW_OUTPUT_WIDTH, Config.DW_OUTPUT_HEIGHT);
			default:
				return card.getOriginalSizeImage();
		}
	}
}
