package ui;

import java.awt.Component;
import java.awt.EventQueue;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import util.Config;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import java.util.ArrayList;
import java.util.List;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.DefaultComboBoxModel;

import util.CardUtil;
import util.Config;
import util.LanguageChangeListener;

public class SettingsWindow implements LanguageChangeListener{
	private static JFrame frame;
	DefaultListModel<String> mSortListModel, mNotSortListModel;
	private static List<LanguageChangeListener> listeners = new ArrayList<>();
    private JLabel settingsLabel;
    private JLabel languageLabel, cardLanguageLabel;
    private JComboBox<String> languageDropdown, cardLanguageDropdown;
    private JButton btnConfirm;

	public static void addLanguageChangeListener(LanguageChangeListener listener) {
        listeners.add(listener);
    }

	private void notifyLanguageChange() {
        for (LanguageChangeListener listener : listeners) {
            listener.onLanguageChange();
        }
        onLanguageChange();
    }

	/**
	 * Launch the application.
	 */
	public void show() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					initialize();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public SettingsWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(150, 150, 450, 250);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;

		settingsLabel = new JLabel(CardUtil.getTranslation("settings"), JLabel.CENTER);
		settingsLabel.setFont(MainUI.CRnormalLarge);
        MainUI.componentFontMap.put(settingsLabel, "CRnormalLarge");
		gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        frame.getContentPane().add(settingsLabel, gbc);

        // ==== Language Label ====
        languageLabel = new JLabel(CardUtil.getTranslation("settings.language"));
        languageLabel.setFont(MainUI.CRnormal);
        MainUI.componentFontMap.put(languageLabel, "CRnormal");
        gbc.gridx = 0;
        gbc.gridy = 1; 
        gbc.gridwidth = 1;
        frame.getContentPane().add(languageLabel, gbc);

        // ==== Card Language Label ====
        cardLanguageLabel = new JLabel(CardUtil.getTranslation("settings.cardlanguage"));
        cardLanguageLabel.setFont(MainUI.CRnormal);
        MainUI.componentFontMap.put(cardLanguageLabel, "CRnormal");
        gbc.gridx = 0;
        gbc.gridy = 2; 
        gbc.gridwidth = 1;
        frame.getContentPane().add(cardLanguageLabel, gbc);

        // ==== Language Dropdown ====
        String[] languages = { CardUtil.getTranslation("settings.language.en"), CardUtil.getTranslation("settings.language.zh_TW") };
        languageDropdown = new JComboBox<>(languages);
        languageDropdown.setFont(MainUI.CRnormal);
        MainUI.componentFontMap.put(languageDropdown, "CRnormal");
        gbc.gridx = 1;
        gbc.gridy = 1;
        frame.getContentPane().add(languageDropdown, gbc);

        // Set the current language as the selected item
        if (Config.LANGUAGE.equals("en")) {
            languageDropdown.setSelectedIndex(0);
        } else if (Config.LANGUAGE.equals("zh_TW")) {
            languageDropdown.setSelectedIndex(1);
        }
        
        // ==== Language Dropdown ====
        String[] cardlanguages = { CardUtil.getTranslation("settings.cardlanguage.en"), CardUtil.getTranslation("settings.cardlanguage.zh_TW") };
        cardLanguageDropdown = new JComboBox<>(cardlanguages);
        cardLanguageDropdown.setFont(MainUI.CRnormal);
        MainUI.componentFontMap.put(cardLanguageDropdown, "CRnormal");
        gbc.gridx = 1;
        gbc.gridy = 2;
        frame.getContentPane().add(cardLanguageDropdown, gbc);

        // Set the current language as the selected item
        if (Config.CARD_LANGUAGE.equals("en")) {
            cardLanguageDropdown.setSelectedIndex(0);
        } else if (Config.CARD_LANGUAGE.equals("zh_TW")) {
            cardLanguageDropdown.setSelectedIndex(1);
        }

        // ==== Confirm Button ====
        btnConfirm = new JButton(CardUtil.getTranslation("settings.confirm"));
        btnConfirm.setFont(MainUI.CRnormal);
        MainUI.componentFontMap.put(btnConfirm, "CRnormal");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        frame.getContentPane().add(btnConfirm, gbc);

        // Action listener for the confirm button
        btnConfirm.addActionListener(e -> {
            // Update the language setting
            int selectedIndex = languageDropdown.getSelectedIndex();
            int cardSelectedIndex = cardLanguageDropdown.getSelectedIndex();
            if (selectedIndex == 0) {
                Config.LANGUAGE = "en";
            } else if (selectedIndex == 1) {
                Config.LANGUAGE = "zh_TW";
            }
            if (cardSelectedIndex == 0) {
                Config.CARD_LANGUAGE = "en";
            } else if (cardSelectedIndex == 1) {
                Config.CARD_LANGUAGE = "zh_TW";
            }

            CardUtil.loadLanguage();
            notifyLanguageChange();
        });
	}

	@Override
    public void onLanguageChange() {
        MainUI.loadFont();

		// Update all components with the new font and translations
        settingsLabel.setText(CardUtil.getTranslation("settings"));
        languageLabel.setText(CardUtil.getTranslation("settings.language"));
        cardLanguageLabel.setText(CardUtil.getTranslation("settings.cardlanguage"));
        btnConfirm.setText(CardUtil.getTranslation("settings.confirm"));
        languageDropdown.setModel(new DefaultComboBoxModel<>(new String[]{
            CardUtil.getTranslation("settings.language.en"),
            CardUtil.getTranslation("settings.language.zh_TW")
        }));
        cardLanguageDropdown.setModel(new DefaultComboBoxModel<>(new String[]{
            CardUtil.getTranslation("settings.cardlanguage.en"),
            CardUtil.getTranslation("settings.cardlanguage.zh_TW")
        }));
        languageDropdown.setSelectedItem(CardUtil.getTranslation("settings.language." + Config.LANGUAGE));
        cardLanguageDropdown.setSelectedItem(CardUtil.getTranslation("settings.cardlanguage." + Config.CARD_LANGUAGE));

        for (var entry : MainUI.componentFontMap.entrySet()) {
			Component component = entry.getKey();
            String fontKey = entry.getValue();

			// Map the fontKey to the appropriate Font object
			Font newFont = null;
			switch (fontKey) {
				case "CRnormal":
					newFont = MainUI.CRnormal;
					break;
				case "CRnormalLarge":
					newFont = MainUI.CRnormalLarge;
					break;
				case "CRnormalSmall":
					newFont = MainUI.CRnormalSmall;
					break;
				case "CRbold":
					newFont = MainUI.CRbold;
					break;
			}

			// Update the font for the component
			if (newFont != null) {
				component.setFont(newFont);
			}
        }

        // Revalidate and repaint the frame to apply changes
        frame.revalidate();
        frame.repaint();
    }
}
