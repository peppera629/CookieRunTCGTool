package ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;

import util.CardUtil;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

// Dialog box

public class ControlHintDialog {
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
        frame.setSize(200 + message.length() * 10, 200);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        
        JLabel messageLabel = new JLabel(message);
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        messageLabel.setFont(MainUI.CRnormal);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(30,30,30,30));
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
