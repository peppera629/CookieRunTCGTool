package dataStructure;

import java.io.BufferedReader;
import java.io.File;  // Import the File class
import java.io.FileInputStream;
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

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
			loadPackTranslations(CardUtil.CardPack.get(i), cardList);
		}
	    return cardList;
	}
	
	private static void loadPack(String packName, List<Card> cardList) {
	    try {
	        File file = new File("resources/card_config/pack/"+Config.LANGUAGE+"/"+packName+".txt");
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
	            	Card c = new Card(packName, cardData[0], cardData[1], color, type, (cardData[4].equals("F") || cardData[4].equals("H") || cardData[4].equals("D")) || cardData[4].equals("S"),
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

	private static void loadPackTranslations(String packName, List<Card> cardList) {
		String iconPathR = "<img src=\"file:" + new File("resources/icons/R.png").getAbsolutePath() + "\" width='" + (int) (Config.COST_ICON_SCALE * 100) + "%' height='" + (int) (Config.COST_ICON_SCALE * 100) + "%'>";
		String iconPathY = "<img src=\"file:" + new File("resources/icons/Y.png").getAbsolutePath() + "\" width='" + (int) (Config.COST_ICON_SCALE * 100) + "%' height='" + (int) (Config.COST_ICON_SCALE * 100) + "%'>";
		String iconPathG = "<img src=\"file:" + new File("resources/icons/G.png").getAbsolutePath() + "\" width='" + (int) (Config.COST_ICON_SCALE * 100) + "%' height='" + (int) (Config.COST_ICON_SCALE * 100) + "%'>";
		String iconPathB = "<img src=\"file:" + new File("resources/icons/B.png").getAbsolutePath() + "\" width='" + (int) (Config.COST_ICON_SCALE * 100) + "%' height='" + (int) (Config.COST_ICON_SCALE * 100) + "%'>";
		String iconPathP = "<img src=\"file:" + new File("resources/icons/P.png").getAbsolutePath() + "\" width='" + (int) (Config.COST_ICON_SCALE * 100) + "%' height='" + (int) (Config.COST_ICON_SCALE * 41008) + "%'>";
		String iconPathW = "<img src=\"file:" + new File("resources/icons/W.png").getAbsolutePath() + "\" width='" + (int) (Config.COST_ICON_SCALE * 100) + "%' height='" + (int) (Config.COST_ICON_SCALE * 100) + "%'>";
		String iconPathActivate = "<img src=\"file:" + new File("resources/icons/Activate.png").getAbsolutePath() + "\" width='" + (int) (Config.COST_ICON_SCALE * 100) + "%' height='" + (int) (Config.COST_ICON_SCALE * 100) + "%'>";
		String iconPathYourTurn = "<img src=\"file:" + new File("resources/icons/YourTurn.png").getAbsolutePath() + "\" width='" + (int) (Config.COST_ICON_SCALE * 100) + "%' height='" + (int) (Config.COST_ICON_SCALE * 100) + "%'>";
		String iconPathOncePerTurn = "<img src=\"file:" + new File("resources/icons/OncePerTurn.png").getAbsolutePath() + "\" width='" + (int) (Config.COST_ICON_SCALE * 100) + "%' height='" + (int) (Config.COST_ICON_SCALE * 100) + "%'>";
		String iconPathOnPlay = "<img src=\"file:" + new File("resources/icons/OnPlay.png").getAbsolutePath() + "\" width='" + (int) (Config.COST_ICON_SCALE * 100) + "%' height='" + (int) (Config.COST_ICON_SCALE * 100) + "%'>";
		String iconPathBlocker = "<img src=\"file:" + new File("resources/icons/Blocker.png").getAbsolutePath() + "\" width='" + (int) (Config.COST_ICON_SCALE * 100) + "%' height='" + (int) (Config.COST_ICON_SCALE * 100) + "%'>";
		String iconPathEquip = "<img src=\"file:" + new File("resources/icons/Equip.png").getAbsolutePath() + "\" width='" + (int) (Config.COST_ICON_SCALE * 100) + "%' height='" + (int) (Config.COST_ICON_SCALE * 100) + "%'>";
		String iconPathExtra = "<img src=\"file:" + new File("resources/icons/Extra.png").getAbsolutePath() + "\" width='" + (int) (Config.COST_ICON_SCALE * 100) + "%' height='" + (int) (Config.COST_ICON_SCALE * 100) + "%'>";
		String iconPathAwaken = "<img src=\"file:" + new File("resources/icons/Awaken.png").getAbsolutePath() + "\" width='" + (int) (Config.COST_ICON_SCALE * 100) + "%' height='" + (int) (Config.COST_ICON_SCALE * 100) + "%'>";
		String iconPathFlip = "<img src=\"file:" + new File("resources/icons/FLIP.png").getAbsolutePath() + "\" width='" + (int) (Config.COST_ICON_SCALE * 100) + "%' height='" + (int) (Config.COST_ICON_SCALE * 100) + "%'>";

	    try {
	        File translationFile = new File("resources/card_config/translations/"+Config.LANGUAGE+"/"+packName+".txt");
			if (translationFile.exists()) {
				FileInputStream reader = new FileInputStream(translationFile);
		        BufferedReader input = new BufferedReader(
		                new InputStreamReader(new FileInputStream(translationFile), StandardCharsets.UTF_8));
				String data;
				while((data= input.readLine())!=null) {
					if (!data.equals("") && !data.startsWith("//")) {
						String[] cardData = data.split(",", -1);
						for (int i = 1; i < cardData.length; i++) {
							cardData[i] = cardData[i].replace("&", "&amp;")
                                         .replace("<", "&lt;")
                                         .replace(">", "&gt;")
										 .replace("[R]", iconPathR)
										 .replace("[Y]", iconPathY)
										 .replace("[G]", iconPathG)
										 .replace("[B]", iconPathB)
										 .replace("[P]", iconPathP)
										 .replace("[W]", iconPathW)
										 .replace("【啟動】", iconPathActivate)
										 .replace("【1回合1次】", iconPathOncePerTurn)
										 .replace("【阻擋】", iconPathBlocker)
										 .replace("【裝載】", iconPathEquip)
										 .replace("【額外】", iconPathExtra)
										 .replace("【覺醒】", iconPathAwaken)
										 .replace("【在自己的回合中】", iconPathYourTurn)
										 .replace("【登場時】", iconPathOnPlay)
										 .replace("\\n", "<br>");
						}
						System.out.println("Translation length: " + cardData.length);
						//                0   1            2      3            4            5           6                   7
						// For each row: [ID, Skill Name, Skill, Attack Cost, Attack Name, Attack DMG, Attack Then Effect, FLIP]
						for (Card c : cardList) {
							if (c.getPack().equals(packName) && c.getId().equals(cardData[0])) {
								// Set card translations
								System.out.println(cardData[1] + ", " + cardData[2] + ", " + cardData[3] + ", " + cardData[4] + ", " + cardData[5] + ", " + cardData[6] + ", " + cardData[7]);
								c.setCardTranslation(cardData[1], cardData[2], cardData[3], cardData[4], cardData[5], cardData[6], (cardData[7] == "" ? cardData[7] : iconPathFlip + cardData[7]));
								break;
							}
						}
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

	public static void refreshAllCardNames() {
		// Get the singleton instance of CardList
		CardList cardList = CardList.getInstance();

		// Iterate through all currently-loaded cards
		for (Card card : cardList.getAllCards()) {
			try {
				// Reload the card's data from the appropriate file
				String packName = card.getPack();
				File file = new File("resources/card_config/pack/" + Config.LANGUAGE + "/" + packName + ".txt");
				if (file.exists()) {
					BufferedReader input = new BufferedReader(
							new InputStreamReader(new FileInputStream(file), "utf-8"));
					String data;
					while ((data = input.readLine()) != null) {
						if (!data.equals("") && !data.startsWith("//")) {
							String[] cardData = data.split(",");
							if (cardData[0].equals(card.getId())) {
								// Update the card's name
								card.setName(cardData[1]);
								break;
							}
						}
					}
					input.close();
				}
			} catch (IOException e) {
				System.err.println("Error refreshing card name for ID: " + card.getId());
				e.printStackTrace();
			}
		}
		System.out.println("All card names have been refreshed.");
	}
}
