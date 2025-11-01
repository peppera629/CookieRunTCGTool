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
import util.CardUtil;
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

	public static void loadAllPacks() {
		CardUtil.CardPack = new ArrayList<String>();
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
		            	CardUtil.CardPack.add(data);
		            }
		        } 
				reader.close();
		        input.close();
	        }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
			System.out.println("An error occurred (Unsupported Encoding).");
            e.printStackTrace();
		} catch (IOException e) {
			System.out.println("An error occurred (IO Exception).");
			e.printStackTrace();
		}
	}
	
	public static List<Card> loadAllCards() {
		List<Card> cardList = new ArrayList<Card>();
		for (int i=0; i<CardUtil.CardPack.size() ;i++) {
        	loadPack(CardUtil.CardPack.get(i), cardList);
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
					//                0   1     2      3     4           5       6                7      8
					// For each row: [ID, Name, Color, Type, FLIP/EXTRA, Rarity, Regulation Mark, Level, HP]
	            	CardColor color = CardColor.Green;
	            	for (int i=0; i<CardUtil.COLOR_MAX; i++) {
	            		CardColor c = CardColor.fromValue(i);
	            		if (cardData[2].equals(c.getName())) {
	            			color = c;
	            			break;
	            		}
	            	}
	            	
	            	int level = 0;
	            	CardType type;
	            	if (cardData[3].equals("Cookie")) {
	            		type = CardType.Cookie;
	            		if (cardData.length >7) {
	            			level = Integer.parseInt(cardData[7]);
	            		}
	            	} else if (cardData[3].equals("Item")) {
	            		type = CardType.Item;
	            	} else if (cardData[3].equals("Trap")) {
	            		type = CardType.Trap;
	            	} else if (cardData[3].equals("Stage")) {
	            		type = CardType.Stage;
	            	} else {
	            		type = CardType.Cookie;
	            	}
	            	
					System.out.println(cardData[0]);
	            	Card c = new Card(packName, cardData[0], cardData[1], color, type, cardData[4].equals("F"),
					cardData[4].equals("EX"), CardUtil.CardRarity.fromString(cardData[5]), cardData[6], level);

	            	cardList.add(c);
	            }
	        }
			reader.close();
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
						if (card == null) {
							System.err.println("Card ID not found: " + data);
							continue; // Skip this card
						}
						System.out.println(card.getId());
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
			if (!new File("deck").exists()) {
				new File("deck").mkdirs();
			}
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
			if (!new File("deck_readable").exists()) {
				new File("deck_readable").mkdirs();
			}
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
