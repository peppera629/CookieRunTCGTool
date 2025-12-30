package ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import util.CardUtil;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

// Dialog box

public class ChoiceDialog {
    private JDialog dialog;
    private int userChoice; // 0 = Yes, 1 = No, 2 = Cancel

    public ChoiceDialog() {
        
    }

    public int show(String msg) {
        // Create a modal JDialog
        dialog = new JDialog((JFrame) null, CardUtil.getTranslation("dialog.title"), true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(200 + msg.length() * 10, 200);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(null);

        // Message label
        JLabel messageLabel = new JLabel(msg, JLabel.CENTER);
        messageLabel.setFont(MainUI.CRnormal);
        dialog.add(messageLabel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton yesButton = new JButton(CardUtil.getTranslation("confirmation.save"));
        yesButton.setFont(MainUI.CRnormal);
        yesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userChoice = 0; // Save
                dialog.dispose();
            }
        });
        buttonPanel.add(yesButton);

        JButton noButton = new JButton(CardUtil.getTranslation("confirmation.discard"));
        noButton.setFont(MainUI.CRnormal);
        noButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userChoice = 1; // Discard
                dialog.dispose();
            }
        });
        buttonPanel.add(noButton);

        JButton cancelButton = new JButton(CardUtil.getTranslation("confirmation.cancel"));
        cancelButton.setFont(MainUI.CRnormal);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userChoice = 2; // Cancel
                dialog.dispose();
            }
        });
        buttonPanel.add(cancelButton);

        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Center the dialog on the screen
        dialog.setLocationRelativeTo(null);

        // Show the dialog (this will block until the dialog is closed)
        dialog.setVisible(true);

        return userChoice;
    }
}