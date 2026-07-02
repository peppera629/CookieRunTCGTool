package ui;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import util.CardUtil;
import util.LanguageChangeListener;

public final class CardDetailWindow extends JFrame implements LanguageChangeListener {
    private final JPanel base = new JPanel(new BorderLayout());

    public CardDetailWindow() {
        setTitle(CardUtil.getTranslation("preview"));
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setContentPane(base);
        setSize(420, 900);
        setLocationByPlatform(true);
        SettingsWindow.addLanguageChangeListener(this);
    }

    public void setPreviewComponent(java.awt.Component component) {
        base.removeAll();
        if (component != null) {
            base.add(component, BorderLayout.CENTER);
        }
        base.revalidate();
        base.repaint();
    }

    public void showWindow() {
        setVisible(true);
    }

    public void hideWindow() {
        setVisible(false);
    }

    @Override
    public void onLanguageChange() {
        setTitle(CardUtil.getTranslation("preview"));
    }
}
