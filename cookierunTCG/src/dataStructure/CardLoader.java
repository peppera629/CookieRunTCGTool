package dataStructure;

import java.io.BufferedReader;
import java.io.File;  // Import the File class
import java.io.FileInputStream;
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import util.CardUtil.CardColor;
import util.CardUtil.CardType;

import java.util.ArrayList;
import java.util.List;

public class CardLoader {
	
	public static List<Card> loadAllCards() {
		List<Card> cardList = new ArrayList<Card>();
	    try {
	        File file = new File("resources/pack.txt");
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
        System.out.println(packName);
	    try {
	        System.out.println("loadPack 1");
	        File file = new File("resources/BS1.txt");
			FileInputStream reader = new FileInputStream(file);
	        BufferedReader input = new BufferedReader(
	                new InputStreamReader(new FileInputStream(file), "utf-8")); 
            String data;
	        while((data= input.readLine())!=null) {
                System.out.println(data);
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

	            	CardType type;
	            	if (cardData[3].equals("餅乾")) {
	            		type = CardType.Cookie;
	            	} else if (cardData[3].equals("物品")) {
	            		type = CardType.Item;
	            	} else if (cardData[3].equals("陷阱")) {
	            		type = CardType.Trap;
	            	} else if (cardData[3].equals("場地")) {
	            		type = CardType.Stage;
	            	} else {
	            		type = CardType.Cookie;
	            	}
	            	
	            	Card c = new Card(packName, cardData[0], cardData[1], color, type, cardData[4].equals("F") ,cardData[5], cardData[6]);
	            	cardList.add(c);
	            }
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
	}

	
	public static Deck loadDeck(String deckName) {
		Deck deck = new Deck();
		CardList cardList = CardList.getInstance();
	    try {
	        File file = new File("resources/deck/"+deckName+".txt");
			FileInputStream reader = new FileInputStream(file);
	        BufferedReader input = new BufferedReader(
	                new InputStreamReader(new FileInputStream(file), "utf-8")); 
	        String data;
	        while((data= input.readLine())!=null) {
	            System.out.println(data);

	            if (!data.equals("") && !data.startsWith("//")) {
	            	Card card = cardList.getCardById(data);
	            	deck.addCard(card);
	            }
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
	    return deck;
	}
	
	public static void saveDeck(String deckName, Deck deck) {
		FileWriter fw;
		try {
			fw = new FileWriter("resources/deck/"+deckName+".txt");
			List<Card> cardList = deck.getAllCards();
			for (Card c : cardList) {
		        fw.write(c.getId()+"\n");
			}
	        fw.flush();
	        fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
