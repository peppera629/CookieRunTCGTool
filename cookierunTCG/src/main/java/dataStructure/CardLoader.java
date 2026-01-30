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
import util.AppPaths;

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
	        File file = new File(AppPaths.dataDir().resolve("card_config/pack.txt").toString());
	        if (file.exists()) {
				FileInputStream reader = new FileInputStream(file);
		        BufferedReader input = new BufferedReader(
		                new InputStreamReader(new FileInputStream(file), "utf-8")); 
		        String data;
		        while((data= input.readLine())!=null) {
		            if (!data.equals("") && !data.startsWith("//")) {
						String[] packData = data.split(",");
						System.out.println("Loading pack: " + data);
		            	CardUtil.CardPack.add(packData[0]);
						CardUtil.CardPackAvailability.put(packData[0], new HashMap<String, Boolean>());
						CardUtil.CardPackAvailability.get(packData[0]).put("KR", packData[1].contains("K"));
						CardUtil.CardPackAvailability.get(packData[0]).put("TW", packData[1].contains("A"));
						CardUtil.CardPackAvailability.get(packData[0]).put("SEA", packData[1].contains("A"));
						CardUtil.CardPackAvailability.get(packData[0]).put("NA", packData[1].contains("N"));
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
				//loadVariantNames(CardUtil.CardPack.get(i), cardList);
			}
		}
	    return cardList;
	}

	public static void reloadTranslations(List<Card> cardList) {
		for (Card c : cardList) {
			c.clearCardTranslation();
		}
		for (int i=0; i<CardUtil.CardPack.size() ;i++) {
			loadPackTranslations(CardUtil.CardPack.get(i), cardList);
		}
	}

	public static void reloadCardNames(List<Card> cardList) {
		for (int i=0; i<CardUtil.CardPack.size() ;i++) {
			loadCardNames(CardUtil.CardPack.get(i), cardList);
		}
	}
	
	public static void reloadVariants(List<Card> cardList) {
		for (int i=0; i<CardUtil.CardPack.size() ;i++) {
			loadVariants(CardUtil.CardPack.get(i), cardList);
		}
	}

	private static void loadPack(String packName, List<Card> cardList) {
	    try {
	        File file = new File(AppPaths.dataDir().resolve("card_config/pack/"+packName+".csv").toString());
			FileInputStream reader = new FileInputStream(file);
	        BufferedReader input = new BufferedReader(
	                new InputStreamReader(new FileInputStream(file), "utf-8")); 
            String data;
	        while((data= input.readLine())!=null) {
	            if (!data.equals("") && !data.startsWith("//")) {
	            	String[] cardData = data.split(",");
					//                0   1      2     3                4       5                6      7   8           9        10		     11           12          13
					// For each row: [ID, Color, Type, FLIP Type/EXTRA, Rarity, Regulation Mark, Level, HP, Skill Type, Keyword, Attack DMG, Attack Cost, (Peak DMG), (Peak Cost)]
					// If the Cookie doesn't have a DMG-dealing skill or then effect, Peak DMG = Attack DMG, Peak Cost = Attack Cost
					// Includes: DMG dealt during the opponent's turn, ATK-increasing skills, HP-trashing skills, HP-stealing skills
					// Excludes: Self-damage, items, traps, and stages for now

	            	CardColor color = CardColor.Green;
					boolean isAwaken = false;
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
								if (cardData[7].contains("+")) {
									cardData[7] = cardData[7].replace("+", "");
									isAwaken = true;
								}
								
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
	            	Card c = new Card(packName, cardData[0], "", color, type, isFlip, (isFlip ? FlipType.fromString(cardData[3]) : null), cardData[3].equals("EX"), isAwaken, CardUtil.CardRarity.fromString(cardData[4]), cardData[5], level, hp, skillType, keyword);
	            	
					if (cardData.length >10) {
						int attackDMG = 0;
						int attackCost = 0;
						int peakDMG = 0;
						int peakCost = 0;
						int avgDMG = 0;
						int avgCost = 0;
						if (!cardData[10].equals("_") && !cardData[11].equals("_")) {
							attackCost = Integer.parseInt(cardData[10]);
							attackDMG = Integer.parseInt(cardData[11]);
							if (cardData.length >12) {
								if (!cardData[12].equals("_") && !cardData[13].equals("_")) {
									peakCost = Integer.parseInt(cardData[12]);
									peakDMG = Integer.parseInt(cardData[13]);
									if (cardData.length > 14) {
										if (!cardData[14].equals("_") && !cardData[15].equals("_")) {
											avgCost = Integer.parseInt(cardData[14]);
											avgDMG = Integer.parseInt(cardData[15]);
										} else {
											avgCost = peakCost;
											avgDMG = peakDMG;
										}
									} else {
										avgCost = peakCost;
										avgDMG = peakDMG;
									}
								} else {
									peakCost = attackCost;
									peakDMG = attackDMG;
									avgCost = attackCost;
									avgDMG = attackDMG;
								}
							} else {
								peakCost = attackCost;
								peakDMG = attackDMG;
								avgCost = attackCost;
								avgDMG = attackDMG;
							}
						}
						c.setAttackAttributes(attackCost, attackDMG, peakCost, peakDMG, avgCost, avgDMG);
					}
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
			File file = new File(AppPaths.dataDir().resolve("card_config/names/"+packName+".txt").toString());
			FileInputStream reader = new FileInputStream(file);
			BufferedReader input = new BufferedReader(
					new InputStreamReader(new FileInputStream(file), "utf-8")); 
			String data;
			while((data = input.readLine()) != null) {
				if (!data.equals("") && !data.startsWith("//")) {
					String[] cardData = data.split(",");
					// For each row: [ID, Name (EN), Name (zh_TW, or EN if not available)]

					// Is an alt. name
					if (cardData[0].contains("@")) {
						// Add alternate name
						for (Card c : cardList) {
							if (c.getPack().equals(packName) && c.getId().equals(cardData[0].split("@")[0])) {
								List<String> altNames = new ArrayList<String>();
								for (int i = 1; i <= Config.ALL_LANGUAGES.length ; i++) {
									// Add to alt name list: ? if no name, EN by default
									if (cardData.length == 1) {
										altNames.add("?");
									} else if (i >= cardData.length) {
										altNames.add(cardData[1]);
									} else {
										altNames.add(cardData[i]);
									}
								}
								c.addAltNames(altNames);
							}
						}
					} else {
						// Is not an alt. name
						for (Card c : cardList) {
							if (c.getPack().equals(packName) && c.getId().equals(cardData[0])) {
								for (int i = 1; i <= Config.ALL_LANGUAGES.length ; i++) {
									
									// Add to name list: ? if no name, EN by default
									if (cardData.length == 1) {
										c.addToNameByLang("?");
									} else if (i >= cardData.length) {
										c.addToNameByLang(cardData[1]);
									} else {
										c.addToNameByLang(cardData[i]);
									}

									if (Config.ALL_LANGUAGES[i-1].equals(Config.CARD_LANGUAGE)) {
										c.setName(c.getNameByLang().get(i-1));
									}
								}
							}
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
		iconPathR = "<img src=\"file:" + new File(AppPaths.dataDir().resolve("icons/" + (Config.LARGE_TRANSLATION_TEXT ? "24px/" : "16px/") + "R.png").toString()).getAbsolutePath() + "\">";
		iconPathY = "<img src=\"file:" + new File(AppPaths.dataDir().resolve("icons/" + (Config.LARGE_TRANSLATION_TEXT ? "24px/" : "16px/") + "Y.png").toString()).getAbsolutePath() + "\">";
		iconPathG = "<img src=\"file:" + new File(AppPaths.dataDir().resolve("icons/" + (Config.LARGE_TRANSLATION_TEXT ? "24px/" : "16px/") + "G.png").toString()).getAbsolutePath() + "\">";
		iconPathB = "<img src=\"file:" + new File(AppPaths.dataDir().resolve("icons/" + (Config.LARGE_TRANSLATION_TEXT ? "24px/" : "16px/") + "B.png").toString()).getAbsolutePath() + "\">";
		iconPathP = "<img src=\"file:" + new File(AppPaths.dataDir().resolve("icons/" + (Config.LARGE_TRANSLATION_TEXT ? "24px/" : "16px/") + "P.png").toString()).getAbsolutePath() + "\">";
		iconPathW = "<img src=\"file:" + new File(AppPaths.dataDir().resolve("icons/" + (Config.LARGE_TRANSLATION_TEXT ? "24px/" : "16px/") + "W.png").toString()).getAbsolutePath() + "\">";
		iconPathActivate = "<img src=\"file:" + new File(AppPaths.dataDir().resolve("icons/" + Config.LANGUAGE + "/" + (Config.LARGE_TRANSLATION_TEXT ? "24px/" : "16px/") + "Activate.png").toString()).getAbsolutePath() + "\">";
		iconPathYourTurn = "<img src=\"file:" + new File(AppPaths.dataDir().resolve("icons/" + Config.LANGUAGE + "/" + (Config.LARGE_TRANSLATION_TEXT ? "24px/" : "16px/") + "YourTurn.png").toString()).getAbsolutePath() + "\">";
		iconPathOncePerTurn = "<img src=\"file:" + new File(AppPaths.dataDir().resolve("icons/" + Config.LANGUAGE + "/" + (Config.LARGE_TRANSLATION_TEXT ? "24px/" : "16px/") + "OncePerTurn.png").toString()).getAbsolutePath() + "\">";
		iconPathOnPlay = "<img src=\"file:" + new File(AppPaths.dataDir().resolve("icons/" + Config.LANGUAGE + "/" + (Config.LARGE_TRANSLATION_TEXT ? "24px/" : "16px/") + "OnPlay.png").toString()).getAbsolutePath() + "\">";
		iconPathBlocker = "<img src=\"file:" + new File(AppPaths.dataDir().resolve("icons/" + Config.LANGUAGE + "/" + (Config.LARGE_TRANSLATION_TEXT ? "24px/" : "16px/") + "Blocker.png").toString()).getAbsolutePath() + "\">";
		iconPathEquip = "<img src=\"file:" + new File(AppPaths.dataDir().resolve("icons/" + Config.LANGUAGE + "/" + (Config.LARGE_TRANSLATION_TEXT ? "24px/" : "16px/") + "Equip.png").toString()).getAbsolutePath() + "\">";
		iconPathExtra = "<img src=\"file:" + new File(AppPaths.dataDir().resolve("icons/" + Config.LANGUAGE + "/" + (Config.LARGE_TRANSLATION_TEXT ? "24px/" : "16px/") + "Extra.png").toString()).getAbsolutePath() + "\">";
		iconPathAwaken = "<img src=\"file:" + new File(AppPaths.dataDir().resolve("icons/" + Config.LANGUAGE + "/" + (Config.LARGE_TRANSLATION_TEXT ? "24px/" : "16px/") + "Awaken.png").toString()).getAbsolutePath() + "\">";
		iconPathFlip = "<img src=\"file:" + new File(AppPaths.dataDir().resolve("icons/" + Config.LANGUAGE + "/" + (Config.LARGE_TRANSLATION_TEXT ? "24px/" : "16px/") + "FLIP.png").toString()).getAbsolutePath() + "\">";
	    try {
	        File translationFile = new File(AppPaths.dataDir().resolve("card_config/translations/"+Config.LANGUAGE+"/"+packName+".txt").toString());
			if (translationFile.exists()) {
		        BufferedReader input = new BufferedReader(
		                new InputStreamReader(new FileInputStream(translationFile), StandardCharsets.UTF_8));
				String data;
				while((data= input.readLine())!=null) {
					if (!data.equals("") && !data.startsWith("//")) {
						String[] cardData = data.split(";", -1);
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
										 .replace("\\,", ",")
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
			File banListFile = new File(AppPaths.dataDir().resolve("card_config/bans.txt").toString());
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
			File rarityListFile = new File(AppPaths.dataDir().resolve("card_config/rarity.csv").toString());
			
			if (rarityListFile.exists()) {
				BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(rarityListFile), StandardCharsets.UTF_8));
				
				String data;
				while((data = input.readLine()) != null) {
					if (!data.equals("") && !data.startsWith("//")) {
						List<String> variantDataListForm = new ArrayList<>(Arrays.asList(data.split(",", -1)));
						int currIdx = variantDataListForm.size() - 1;
						while (variantDataListForm.get(currIdx).isEmpty()) {
							variantDataListForm.remove(currIdx);
							currIdx--;
						}
						if (currIdx % 2 == 1) { // Extra empty space was removed (in the case of last variant not having a name)
							variantDataListForm.add("");
						}
						String[] variantData = variantDataListForm.toArray(new String[0]);
						CardRarity[] variantRarity = new CardRarity[(int) variantData.length / 2];
						String[] variantNames = new String[(int) variantData.length / 2];
						String[] variantNamesLocalized = new String[variantNames.length];
						for (int i = 1; i < variantData.length; i = i + 2) {
							if (variantData[i].isEmpty()) {
								continue;
							}
							variantRarity[(int) (i / 2)] = CardRarity.fromString(variantData[i]);
							if (i >= variantData.length - 1) {
								variantNames[(int) (i / 2)] = "";
							} else {
								variantNames[(int) (i / 2)] = variantData[i + 1];
							}
						}
						for (Card c : cardList) {
							if (c.getPack().equals(packName) && c.getId().equals(variantData[0])) {

								c.setVariantTypes(variantRarity);
								for (int i = 0; i < variantNames.length; i++) {
									System.out.println("Processing card " + c.getId() + " variant " + i);
									if (!variantNames[i].isEmpty() && variantNames[i] != null) {
										variantNamesLocalized[i] = variantNames[i];
									} else {
										variantNamesLocalized[i] = "";
									}
								}
								c.setVariantNames(variantNamesLocalized);
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
	/* 
	public static void loadVariantNames(String packName, List<Card> cardList) {
		try {
			File rarityDescListFile = new File(AppPaths.dataDir().resolve("card_config/rarity_desc.csv").toString());
			if (rarityDescListFile.exists()) {
				BufferedReader inputDesc = new BufferedReader(new InputStreamReader(new FileInputStream(rarityDescListFile), StandardCharsets.UTF_8));
				String dataDesc;
				while ((dataDesc = inputDesc.readLine()) != null) {
					if (!dataDesc.equals("") && !dataDesc.startsWith("//")) {
						String[] variantNames = Arrays.asList(dataDesc.split(",", -1)).toArray(new String[0]);
						String[] variantNamesLocalized = new String[variantNames.length];
						for (int i = 1; i < variantNames.length; i++) {
							if (variantNames[i].isEmpty()) {
								continue;
							}
						}
						//String[] variantDescData = dataDesc.split(",", -1);
						for (Card c : cardList) {
							if (c.getPack().equals(packName) && c.getId().equals(variantNames[0])) {
								for (int i = 1; i < variantNames.length; i++) {
									if (!variantNames[i].isEmpty() && variantNames[i] != null) {
										variantNamesLocalized[i - 1] = variantNames[i];
									} else {
										variantNamesLocalized[i - 1] = "";
									}
								}
								c.setVariantNames(variantNamesLocalized);
							}
						}
					
					}
				}
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
	*/

	public static void loadCardAvailability() {
	    try {
	        File file = new File(AppPaths.dataDir().resolve("card_config/availability.txt").toString());
	        if (file.exists()) {
				FileInputStream reader = new FileInputStream(file);
		        BufferedReader input = new BufferedReader(
		                new InputStreamReader(new FileInputStream(file), "utf-8")); 
		        String data;
		        while((data= input.readLine())!=null) {
		            if (!data.equals("") && !data.startsWith("//")) {
		            	String[] cardData = data.split(",");
						// For each row: [ID / Promo Tag / ID & Variant ID, Availability: 3 digits in binary representing EN, TC, KR]
						if (cardData[0].contains("@")) { // ID & Variant ID
							String[] idVariant = cardData[0].split("@");
							String cardId = idVariant[0];
							int variantId = Integer.parseInt(idVariant[1]);
							boolean[] availability;
							if (cardData[1].equals("0")) {
								availability = new boolean[] {true, true, true};
							} else {
								String availabilityStr = String.format("%3s", Integer.toBinaryString(Integer.parseInt(cardData[1]))).replace(' ', '0');
								availability = new boolean[] {
									availabilityStr.charAt(0) == '1', // EN
									availabilityStr.charAt(1) == '1', // TC
									availabilityStr.charAt(2) == '1'  // KR
								};
							}
							for (Card c : CardList.getInstance().getAllCards()) {
								if (c.getId().equals(cardId)) {
									c.setAvailability(variantId, availability);
									break;
								}
							}
						} else if (cardData[0].contains(".")) { // Promo tag
							//System.out.println("Setting availability for promo tag: " + cardData[0]);
							boolean[] availability;
							if (cardData[1].equals("0")) {
								availability = new boolean[] {true, true, true};
							} else {
								String availabilityStr = String.format("%3s", Integer.toBinaryString(Integer.parseInt(cardData[1]))).replace(' ', '0');
								availability = new boolean[] {
									availabilityStr.charAt(0) == '1', // EN
									availabilityStr.charAt(1) == '1', // TC
									availabilityStr.charAt(2) == '1'  // KR
								};
							}
							for (Card c : CardList.getInstance().getAllCards()) {
								for (int i = 0 ; i < c.getVariants().length; i++) {
									if (c.getVariantNames()[i].equals(cardData[0])) {
										c.setAvailability(i, availability);
										//System.out.println("set " + c.getId() + " variant " + i + ": " + cardData[0] + " " + Arrays.toString(availability));
									}
								}
							}
						} else if (cardData[0].contains("-")) { // Base card only
							boolean[] availability;
							if (cardData[1].equals("0")) {
								availability = new boolean[] {true, true, true};
							} else {
								String availabilityStr = String.format("%3s", Integer.toBinaryString(Integer.parseInt(cardData[1]))).replace(' ', '0');
								availability = new boolean[] {
									availabilityStr.charAt(0) == '1', // EN
									availabilityStr.charAt(1) == '1', // TC
									availabilityStr.charAt(2) == '1'  // KR
								};
							}
							for (Card c : CardList.getInstance().getAllCards()) {
								if (c.getId().equals(cardData[0])) {
									c.setAvailability(0, availability);
								}
							}
						} else { // Neither: whole pack
							boolean[] availability;
							if (cardData[1].equals("0")) {
								availability = new boolean[] {true, true, true};
								//System.out.println("All available");
							} else {
								String availabilityStr = String.format("%3s", Integer.toBinaryString(Integer.parseInt(cardData[1]))).replace(' ', '0');
								//System.out.println(availabilityStr);
								availability = new boolean[] {
									availabilityStr.charAt(0) == '1', // EN
									availabilityStr.charAt(1) == '1', // TC
									availabilityStr.charAt(2) == '1'  // KR
								};
							}
							//System.out.println("Setting availability for pack: " + cardData[0]);
							for (Card c : CardList.getInstance().getAllCards()) {
								if (c.getPack().equals(cardData[0])) {
									for (int i = 0 ; i < c.getVariants().length; i++) {
										c.setAvailability(i, availability);
										//System.out.println("set " + c.getId() + " variant " + i);
									}
								}
							}
							//System.out.println("set complete: " + cardData[0]);
						}
		            	//CardList.getInstance().setCardAvailability(cardData[0], Boolean.parseBoolean(cardData[1]));
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

	public static Deck loadDeck(String deckDirectory, String deckName) {
		Deck deck = new Deck();
		CardList cardList = CardList.getInstance();
	    try {
	        File file = new File(deckDirectory);
			System.out.println("Loading deck from: " + file.getAbsolutePath());
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
	        File file = new File(AppPaths.userDataDir().resolve("deck/"+deckName+".txt").toString());
	        if (file.exists()) {
				FileInputStream reader = new FileInputStream(file);
		        BufferedReader input = new BufferedReader(
		                new InputStreamReader(new FileInputStream(file), "utf-8")); 
		        String data;
		        while((data= input.readLine())!=null) {	
		            if (!data.equals("") && !data.startsWith("//")) {
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
	
	public static void saveDeck(String deckDirectory, String deckName, Deck deck) {
		FileWriter fw;
		try {
			if (!new File(AppPaths.userDataDir().resolve("deck").toString()).exists()) {
				new File(AppPaths.userDataDir().resolve("deck").toString()).mkdirs();
			}
			fw = new FileWriter((deckDirectory).toString());
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
		//saveReadableDeck(deckName, deck);
	}
	
	public static String saveReadableDeck(String deckName, Deck deck) {
		FileWriter fw;
		try {
			if (!new File(AppPaths.userDataDir().resolve("deck_readable").toString()).exists()) {
				new File(AppPaths.userDataDir().resolve("deck_readable").toString()).mkdirs();
			}
			fw = new FileWriter(AppPaths.userDataDir().resolve("deck_readable/"+deckName+".txt").toString());
			List<Card> cardList = deck.getAllCards();
			int lastCardCount = 0;
			for (Card c : cardList) {
				fw.write("["+c.getId()+"] "+c.getName());
				if (c.getCount() > 1) {
					fw.write(" ×"+c.getCount()+"\n");
				} else {
					fw.write("\n");
				}
			}
			if (lastCardCount == 1) {
				fw.write("\n");
			} else if (lastCardCount > 1) {
				fw.write(" ×"+lastCardCount+"\n");
			}
	        fw.flush();
	        fw.close();
	        return AppPaths.userDataDir().resolve("deck_readable/"+deckName+".txt").toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static ImageIcon createCardImage(Card card, int cardSize, float cardSizeModifier) {
		switch (cardSize) {
			case UIUtil.CARD_SIZE_SMALL:
				return card.getcardIcon();
			case UIUtil.CARD_SIZE_DECK:
				return card.getResizedCardImage((int)(Config.DW_CARD_WIDTH * cardSizeModifier), (int)(Config.DW_CARD_HEIGHT * cardSizeModifier));
			case UIUtil.CARD_SIZE_OUTPUT:
				return card.getResizedCardImage((int)(Config.DW_OUTPUT_WIDTH * cardSizeModifier), (int)(Config.DW_OUTPUT_HEIGHT * cardSizeModifier));
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
				File file = new File(AppPaths.dataDir().resolve("card_config/pack/" + Config.LANGUAGE + "/" + packName + ".csv").toString());
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
