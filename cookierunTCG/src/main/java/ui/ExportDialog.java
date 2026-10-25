package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.List;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import dataStructure.Card;
import dataStructure.CardList;
import dataStructure.Collection;
import dataStructure.Deck;
import util.CardUtil;
import util.Config;
import util.AppPaths;

// Dialog box

public class ExportDialog {
    private JFrame frame;

    /**
     * Launch the application.
     */
    public void show(Deck deck) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    initialize(deck);
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initialize(Deck deck) {
        frame = new JFrame(CardUtil.getTranslation("deck.exportlist"));
        frame.setSize(800, 800);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(MainUI.CRnormal);

        JPanel mainTab = new JPanel();
        mainTab.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        mainTab.setLayout(new BorderLayout());
        tabbedPane.addTab(CardUtil.getTranslation("deck.exportlist.standard"), mainTab);
        GridBagConstraints gbc_mainTab = new GridBagConstraints();

        // Message label
        JLabel messageLabel = new JLabel("Placeholder", JLabel.CENTER);
        messageLabel.setFont(MainUI.CRnormal);
        mainTab.add(messageLabel, BorderLayout.NORTH);

        // Text box
        JTextArea contents = new JTextArea(40, 80);
        contents.setEditable(false);
        contents.setFont(MainUI.CRnormal);
        JScrollPane scrollPane = new JScrollPane(contents);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        mainTab.add(scrollPane, BorderLayout.CENTER);

        Map<String, Integer> cardCounts = new java.util.HashMap<>();
        Map<String, Integer> cardCountsExtra = new java.util.HashMap<>();

        for (Card card : deck.getAllCards()) {
            if (card.isExtra()) {
                cardCountsExtra.put(card.getId(), card.getCount());
                System.out.println("Card: " + card.getNameByLang().get(0) + ", Count: " + cardCountsExtra.get(card.getId()));
            } else {
                cardCounts.put(card.getId(), card.getCount());
                System.out.println("Card: " + card.getNameByLang().get(0) + ", Count: " + cardCounts.get(card.getId()));
            }
        }

        StringBuilder sbStandard = new StringBuilder();
        sbStandard.append("Main Deck: ").append("\n");
        for (Map.Entry<String, Integer> entry : cardCounts.entrySet()) {
            // Get card's EN name regardless of user settings for compatibility
            sbStandard.append(entry.getValue()).append(" ").append(entry.getKey()).append(" ").append(CardList.getInstance().getCardById(entry.getKey()).getNameByLang().get(0)).append("\n");
        }
        sbStandard.append("\n").append("Extra Deck: ").append("\n");
        for (Map.Entry<String, Integer> entry : cardCountsExtra.entrySet()) {
            // Get card's EN name regardless of user settings for compatibility
            sbStandard.append(entry.getValue()).append(" ").append(entry.getKey()).append(" ").append(CardList.getInstance().getCardById(entry.getKey()).getNameByLang().get(0)).append("\n");
        }
        // Remove trailing newline if present
        if (sbStandard.length() > 0 && sbStandard.charAt(sbStandard.length() - 1) == '\n') {
            sbStandard.deleteCharAt(sbStandard.length() - 1);
        }

        contents.setText(sbStandard.toString());

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton copyToClipboardButton = new JButton(CardUtil.getTranslation("copytoclipboard"));
        copyToClipboardButton.setFont(MainUI.CRnormal);
        copyToClipboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StringSelection stringSelection = new StringSelection(contents.getText());
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
            }
        });
        buttonPanel.add(copyToClipboardButton);
        mainTab.add(buttonPanel, BorderLayout.SOUTH);

        frame.getContentPane().add(tabbedPane);
    }
}
