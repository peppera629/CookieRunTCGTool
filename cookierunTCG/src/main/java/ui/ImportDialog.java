package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;

import util.CardUtil;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.List;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Dialog box

public class ImportDialog {
    private JDialog dialog;
    // private int format; // 0 = TCG Arena (Count ID@Var-Name), 1 = Jarin's Deck Builder (Count Name [ID@Var]) (unused as both formats have the count at the front)
    private List<String> deckList = null;

    public List<String> show() {
        String msg = CardUtil.getTranslation("deck.import.helptext");

        // Create a modal JDialog
        dialog = new JDialog((JFrame) null, CardUtil.getTranslation("deck.import"), true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(800, 600);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(null);

        // Message label
        JLabel messageLabel = new JLabel(msg, JLabel.CENTER);
        messageLabel.setFont(MainUI.CRnormal);
        dialog.add(messageLabel, BorderLayout.NORTH);

        // Text box
        JTextArea contents = new JTextArea(40, 80);
        contents.setFont(MainUI.CRnormal);
        dialog.add(contents, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton importButton = new JButton(CardUtil.getTranslation("import"));
        importButton.setFont(MainUI.CRnormal);
        importButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Pattern patternID = Pattern.compile("(BS\\d{1,2}|ST\\d{1,2}|P)-(\\d{3})");
                if (contents.getText().isEmpty()) {
                    dialog.dispose();
                }
                deckList = new java.util.ArrayList<>();
                for (String line : contents.getText().split("\\n")) {
                    String[] cardData = line.split(" ", 2);
                    if (cardData.length <= 1) continue;
                    System.out.println(cardData[0] + " | " + cardData[1]);
                    try {
                        int copies = Integer.parseInt(cardData[0]);
                        if (copies < 1 || copies > 4) continue;
                        Matcher matcherID = patternID.matcher(cardData[1]);
                        if (matcherID.find()) {
                            String id = matcherID.group(0);
                            for (int i = 0; i < copies; i++) {
                                deckList.add(id);
                                System.out.println(id);
                            }
                        } else {
                            System.out.println("No match found for line: " + line);
                        }
                    } catch (NumberFormatException ne) {
                        continue;
                    }
                }
                dialog.dispose();
            }
        });
        buttonPanel.add(importButton);

        JButton cancelButton = new JButton(CardUtil.getTranslation("cancel"));
        cancelButton.setFont(MainUI.CRnormal);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        buttonPanel.add(cancelButton);

        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Center the dialog on the screen
        dialog.setLocationRelativeTo(null);

        // Show the dialog (this will block until the dialog is closed)
        dialog.setVisible(true);

        return deckList;
    }
}