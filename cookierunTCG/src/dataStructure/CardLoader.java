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
import util.CardUtil.FlipType;
import util.CardUtil.SkillType;
import util.CardUtil.CardRarity;
import util.CardUtil.Keyword;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.ImageIcon;
public class CardLoader {	
	public static ExecutorService cardImageLoadExecutor = Executors.newFixedThreadPool(10);

	private static String iconPathR, iconPathY, iconPathG, iconPathB, iconPathP, iconPathW;
	private static String iconPathActivate, iconPathYourTurn, iconPathOncePerTurn, iconPathOnPlay;
	private static String iconPathBlocker, iconPathEquip, iconPathExtra, iconPathAwaken, iconPathFlip;

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
			if (!CardUtil.CardPack.get(i).endsWith("_")) { // Unreleased packs can be denoted with a trailing underscore
				loadPack(CardUtil.CardPack.get(i), cardList);
				loadCardNames(CardUtil.CardPack.get(i), cardList);
				loadPackTranslations(CardUtil.CardPack.get(i), cardList);
				loadRestrictedCards(CardUtil.CardPack.get(i), cardList);
				loadVariants(CardUtil.CardPack.get(i), cardList);
			}
		}
	    return cardList;
	}

	public static void reloadTranslations(List<Card> cardList) {
		for (int i=0; i<CardUtil.CardPack.size() ;i++) {
			loadPackTranslations(CardUtil.CardPack.get(i), cardList);
		}
	}

	public static void reloadCardNames(List<Card> cardList) {
		for (int i=0; i<CardUtil.CardPack.size() ;i++) {
			loadCardNames(CardUtil.CardPack.get(i), cardList);
		}
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
					//                0   1      2     3                4       5                6      7   8           9
					// For each row: [ID, Color, Type, FLIP Type/EXTRA, Rarity, Regulation Mark, Level, HP, Skill Type, Keyword]
	            	CardColor color = CardColor.Green;
	            	for (int i=0; i<CardUtil.COLOR_MAX; i++) {
	            		CardColor c = CardColor.fromValue(i);
	            		if (cardData[1].equals(c.getName())) {
	            			color = c;
	            			break;
	            		}
	            	}
	            	
	            	int level = 0;
					int hp = 0;
	            	CardType type;
	            	if (cardData[2].equals("Cookie")) {
	            		type = CardType.Cookie;
	            		if (cardData.length >6) {
							if (!cardData[6].equals("_")) {
	            				level = Integer.parseInt(cardData[6]);
							}
							if (cardData.length >7) {
								// Awaken HP bonus is kept for later use
								cardData[7] = cardData[7].replace("+", "");
								if (!cardData[7].equals("_")) {
									hp = Integer.parseInt(cardData[7]);
								}
							}
	            		}
	            	} else if (cardData[2].equals("Item")) {
	            		type = CardType.Item;
	            	} else if (cardData[2].equals("Trap")) {
	            		type = CardType.Trap;
	            	} else if (cardData[2].equals("Stage")) {
	            		type = CardType.Stage;
	            	} else {
	            		type = CardType.Cookie;
	            	}

					boolean isFlip = (cardData[3].equals("F") || cardData[3].equals("H") || cardData[3].equals("D")) || cardData[3].equals("S");
	            	
					List<SkillType> skillType = new ArrayList<SkillType>();
	            	if (cardData.length >8) {
						if (cardData[8].equals("_")) {
							skillType.add(SkillType.None);
						} else {
							if (cardData[8].contains("PY")) {
								skillType.add(SkillType.PassiveOwnTurn);
							} else if (cardData[8].contains("P")) {
								skillType.add(SkillType.Passive);
							}
							if (cardData[8].contains("OY")) {
								skillType.add(SkillType.OnPlayOwnTurn);
							} else if (cardData[8].contains("O")) {
								skillType.add(SkillType.OnPlay);
							}
							if (cardData[8].contains("A")) {
								skillType.add(SkillType.Activate);
							}
							if (cardData[8].contains("B")) {
								skillType.add(SkillType.Blocker);
							}
							if (cardData[8].contains("T")) {
								skillType.add(SkillType.ThenEffect);
							}
						}
	            	} else {
	            		skillType.add(SkillType.None);
	            	}

					Keyword keyword = (cardData.length >9) ? Keyword.fromString(cardData[9]) : Keyword.None;

					// Name will be loaded later
					//System.out.println(packName);
	            	Card c = new Card(packName, cardData[0], "", color, type, isFlip, (isFlip ? FlipType.fromString(cardData[3]) : null), cardData[3].equals("EX"), CardUtil.CardRarity.fromString(cardData[4]), cardData[5], level, hp, skillType, keyword);
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

	private static void loadCardNames(String packName, List<Card> cardList) {
	    try {
	        File file = new File("resources/card_config/names/"+Config.LANGUAGE+"/"+packName+".txt");
			FileInputStream reader = new FileInputStream(file);
	        BufferedReader input = new BufferedReader(
	                new InputStreamReader(new FileInputStream(file), "utf-8")); 
			String data;
	        while((data= input.readLine())!=null) {
	            if (!data.equals("") && !data.startsWith("//")) {
	            	String[] cardData = data.split(",");
					// For each row: [ID, Name]
	            	for (Card c : cardList) {
	            		if (c.getPack().equals(packName) && c.getId().equals(cardData[0])) {
	            			c.setName(cardData[1]);
	            			break;
	            		}
	            	}
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
		iconPathR = "<img src=\"file:" + new File("resources/icons/" + (Config.LARGE_TRANSLATION_TEXT ? "24px/" : "16px/") + "R.png").getAbsolutePath() + "\">";
		iconPathY = "<img src=\"file:" + new File("resources/icons/" + (Config.LARGE_TRANSLATION_TEXT ? "24px/" : "16px/") + "Y.png").getAbsolutePath() + "\">";
		iconPathG = "<img src=\"file:" + new File("resources/icons/" + (Config.LARGE_TRANSLATION_TEXT ? "24px/" : "16px/") + "G.png").getAbsolutePath() + "\">";
		iconPathB = "<img src=\"file:" + new File("resources/icons/" + (Config.LARGE_TRANSLATION_TEXT ? "24px/" : "16px/") + "B.png").getAbsolutePath() + "\">";
		iconPathP = "<img src=\"file:" + new File("resources/icons/" + (Config.LARGE_TRANSLATION_TEXT ? "24px/" : "16px/") + "P.png").getAbsolutePath() + "\">";
		iconPathW = "<img src=\"file:" + new File("resources/icons/" + (Config.LARGE_TRANSLATION_TEXT ? "24px/" : "16px/") + "W.png").getAbsolutePath() + "\">";
		iconPathActivate = "<img src=\"file:" + new File("resources/icons/" + Config.LANGUAGE + "/" + (Config.LARGE_TRANSLATION_TEXT ? "24px/" : "16px/") + "Activate.png").getAbsolutePath() + "\">";
		iconPathYourTurn = "<img src=\"file:" + new File("resources/icons/" + Config.LANGUAGE + "/" + (Config.LARGE_TRANSLATION_TEXT ? "24px/" : "16px/") + "YourTurn.png").getAbsolutePath() + "\">";
		iconPathOncePerTurn = "<img src=\"file:" + new File("resources/icons/" + Config.LANGUAGE + "/" + (Config.LARGE_TRANSLATION_TEXT ? "24px/" : "16px/") + "OncePerTurn.png").getAbsolutePath() + "\">";
		iconPathOnPlay = "<img src=\"file:" + new File("resources/icons/" + Config.LANGUAGE + "/" + (Config.LARGE_TRANSLATION_TEXT ? "24px/" : "16px/") + "OnPlay.png").getAbsolutePath() + "\">";
		iconPathBlocker = "<img src=\"file:" + new File("resources/icons/" + Config.LANGUAGE + "/" + (Config.LARGE_TRANSLATION_TEXT ? "24px/" : "16px/") + "Blocker.png").getAbsolutePath() + "\">";
		iconPathEquip = "<img src=\"file:" + new File("resources/icons/" + Config.LANGUAGE + "/" + (Config.LARGE_TRANSLATION_TEXT ? "24px/" : "16px/") + "Equip.png").getAbsolutePath() + "\">";
		iconPathExtra = "<img src=\"file:" + new File("resources/icons/" + Config.LANGUAGE + "/" + (Config.LARGE_TRANSLATION_TEXT ? "24px/" : "16px/") + "Extra.png").getAbsolutePath() + "\">";
		iconPathAwaken = "<img src=\"file:" + new File("resources/icons/" + Config.LANGUAGE + "/" + (Config.LARGE_TRANSLATION_TEXT ? "24px/" : "16px/") + "Awaken.png").getAbsolutePath() + "\">";
		iconPathFlip = "<img src=\"file:" + new File("resources/icons/" + Config.LANGUAGE + "/" + (Config.LARGE_TRANSLATION_TEXT ? "24px/" : "16px/") + "FLIP.png").getAbsolutePath() + "\">";

	    try {
	        File translationFile = new File("resources/card_config/translations/"+Config.LANGUAGE+"/"+packName+".txt");
			if (translationFile.exists()) {
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
										 .replace("[Activate]", iconPathActivate)
										 .replace("【1回合1次】", iconPathOncePerTurn)
										 .replace("[Once Per Turn]", iconPathOncePerTurn)
										 .replace("【阻擋】", iconPathBlocker)
										 .replace("[Blocker]", iconPathBlocker)
										 .replace("【裝載】", iconPathEquip)
										 .replace("[Equip]", iconPathEquip)
										 .replace("【額外】", iconPathExtra)
										 .replace("[EXTRA]", iconPathExtra)
										 .replace("【覺醒】", iconPathAwaken)
										 .replace("[Awaken]", iconPathAwaken)
										 .replace("【在自己的回合中】", iconPathYourTurn)
										 .replace("[Your Turn]", iconPathYourTurn)
										 .replace("【登場時】", iconPathOnPlay)
										 .replace("[On Play]", iconPathOnPlay)
										 .replace("\\n", "<br>");
						}
						//                0   1            2      3            4            5           6                   7
						// For each row: [ID, Skill Name, Skill, Attack Cost, Attack Name, Attack DMG, Attack Then Effect, FLIP]
						for (Card c : cardList) {
							if (c.getPack().equals(packName) && c.getId().equals(cardData[0])) {
								// Set card translations
								// System.out.println(cardData[1] + ", " + cardData[2] + ", " + cardData[3] + ", " + cardData[4] + ", " + cardData[5] + ", " + cardData[6] + ", " + cardData[7]);
								c.setCardTranslation(cardData[1], cardData[2], cardData[3], cardData[4], cardData[5], cardData[6], (cardData[7] == "" ? cardData[7] : iconPathFlip + cardData[7]));
								break;
							}
						}
					}
				}
				input.close();
			} else {
				for (Card c : cardList) {
					if (c.getPack().equals(packName)) {
						c.clearCardTranslation();
					}
				}
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

	public static void loadRestrictedCards(String packName, List<Card> cardList) {
		try {
			File banListFile = new File("resources/card_config/bans.txt");
			if (banListFile.exists()) {
				BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(banListFile), StandardCharsets.UTF_8));
				String data;
				while((data = input.readLine()) != null) {
					if (!data.equals("") && !data.startsWith("//")) {
						String[] banData = data.split(",", -1);
						for (Card c : cardList) {
							if (c.getPack().equals(packName) && c.getId().equals(banData[0])) {
								c.setMaxCount(Integer.parseInt(banData[1]));
							}
						}
					}
				}
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

	public static void loadVariants(String packName, List<Card> cardList) {
		try {
			File rarityListFile = new File("resources/card_config/rarity.txt");
			File rarityDescListFile = new File("resources/card_config/rarity_desc.txt");
			if (rarityListFile.exists() && rarityDescListFile.exists()) {
				BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(rarityListFile), StandardCharsets.UTF_8));
				BufferedReader inputDesc = new BufferedReader(new InputStreamReader(new FileInputStream(rarityDescListFile), StandardCharsets.UTF_8));
				String data, dataDesc;
				while((data = input.readLine()) != null && (dataDesc = inputDesc.readLine()) != null) {
					if (!data.equals("") && !data.startsWith("//")) {
						String[] variantData = data.split(",", -1);
						String[] variantNames = Arrays.asList(dataDesc.split(",", -1)).subList(1, variantData.length).toArray(new String[0]);
						variantData = Arrays.stream(variantData).filter(Objects::nonNull).filter(s -> !s.trim().isEmpty()).toArray(String[]::new);
						CardRarity[] variantRarity = new CardRarity[variantData.length - 1];
						for (int i = 1; i < variantData.length; i++) {
							if (variantData[i].isEmpty()) {
								continue;
							}
							variantRarity[i - 1] = CardRarity.fromString(variantData[i]);
						}
						//String[] variantDescData = dataDesc.split(",", -1);
						for (Card c : cardList) {
							if (c.getPack().equals(packName) && c.getId().equals(variantData[0])) {
								for (int i = 1; i < variantData.length; i++) {
									if (variantData[i].isEmpty()) {
										continue;
									}
									//System.out.println("Variant for " + c.getId() + ": " + variantRarity[i - 1].getValue() + " - " + variantNames[i - 1]);
								}
								c.setVariantInfo(variantRarity, variantNames);
							}
						}
					}
				}
				input.close();
				inputDesc.close();
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
						//System.out.println("Loading card ID: " + data);
		            	Card card = cardList.getCardById(data);
						if (card == null) {
							System.err.println("Card ID not found: " + data);
							continue; // Skip this card
						}
		            	deck.addCard(card);
		            }
		        } 
		        reader.close();
		        input.close();
	        }
        } catch (FileNotFoundException e) {
            // Load empty deck if file not found
        } catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    return deck;
	}

	public static Map<String, Integer> loadDeckTemp(String deckName) {
		Map<String, Integer> counts = new HashMap<>();
	    try {
	        File file = new File("deck/"+deckName+".txt");
	        if (file.exists()) {
				FileInputStream reader = new FileInputStream(file);
		        BufferedReader input = new BufferedReader(
		                new InputStreamReader(new FileInputStream(file), "utf-8")); 
		        String data;
		        while((data= input.readLine())!=null) {	
		            if (!data.equals("") && !data.startsWith("//")) {
						System.out.println("Loading card ID: " + data);
		            	if (counts.containsKey(data)) {
		            		counts.put(data, counts.get(data) + 1);
		            	} else {
		            		counts.put(data, 1);
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
	    return counts;
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
		//System.out.println("All card names have been refreshed.");
	}
}
