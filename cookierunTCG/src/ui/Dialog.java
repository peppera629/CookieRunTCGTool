package ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

import dataStructure.Card;
import dataStructure.Deck;
import util.Config;
import util.UIUtil;
import util.CardUtil;
import ui.MainUI;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

// Dialog box

public class Dialog {
    private JFrame frame;
    private String message;

    /**
     * Launch the application.
     */
    public void show(String msg) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    message = msg;
                    initialize(false);
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initialize(boolean choiceMode) {
        frame = new JFrame();
        frame.setTitle(CardUtil.getTranslation("dialog.title"));
        frame.setBounds(100, 100, message.length() * 10 + 50, 150);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        
        JLabel messageLabel = new JLabel(message);
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        messageLabel.setFont(MainUI.CRnormal);
        frame.getContentPane().add(messageLabel, BorderLayout.CENTER);

        JButton okButton = new JButton("OK");
        okButton.setFont(MainUI.CRnormal);
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });
        frame.getContentPane().add(okButton, BorderLayout.SOUTH);
    }
}
