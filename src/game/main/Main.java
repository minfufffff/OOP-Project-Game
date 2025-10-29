package game.main;

import javax.swing.*;
import java.awt.*;
import game.component.PanelGame;
import game.component.PanelMenu;
import game.obj.sound.Sound;

public class Main extends JFrame {

    private PanelGame panelGame;
    private PanelMenu panelMenu;
    private Sound sound;

    public Main() {
        setTitle("Space Battle");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        GraphicsDevice gd = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice();
        gd.setFullScreenWindow(this);

        setLayout(new BorderLayout());

        panelGame = new PanelGame();
        panelMenu = new PanelMenu(this);

        add(panelMenu, BorderLayout.CENTER);

        setVisible(true);
        
        sound = new Sound();
        sound.soundBg();
    }

    public void startGame() {
        getContentPane().removeAll();
        add(panelGame, BorderLayout.CENTER);
        revalidate();
        repaint();
        panelGame.start();
        panelGame.requestFocusInWindow();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main());
    }
}
