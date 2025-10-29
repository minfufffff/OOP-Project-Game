package game.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import game.main.Main;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.border.EmptyBorder;

public class PanelMenu extends JPanel {

    private Main main;
    private Font DayDream;
    private Key key;

    private void initFont() {
        try {
            DayDream = Font.createFont(
                Font.TRUETYPE_FONT,
                getClass().getResourceAsStream("/font/Minecraft.ttf")
            ).deriveFont(15f);
        } catch (Exception e) {
            e.printStackTrace();
            DayDream = new Font("Monospaced", Font.PLAIN, 15);
        }
    }
    
    private void initKeyboard() {
        key = new Key();
        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                System.exit(0);
                }
            }    
        });
    }
        
    public PanelMenu(Main main) {
        this.main = main;
        initFont();
        initKeyboard();
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        // Title
        JPanel panelTitle = new JPanel();
        panelTitle.setLayout(new GridLayout(2, 1));
        panelTitle.setOpaque(false);
        panelTitle.setBorder(new EmptyBorder(50, 0, 0, 0));

        JLabel line1 = new JLabel("SPACE", SwingConstants.CENTER);
        line1.setFont(DayDream.deriveFont(72f));
        line1.setForeground(Color.WHITE);

        JLabel line2 = new JLabel("BATTLE", SwingConstants.CENTER);
        line2.setFont(DayDream.deriveFont(72f));
        line2.setForeground(Color.WHITE);

        panelTitle.add(line1);
        panelTitle.add(line2);

        add(panelTitle, BorderLayout.NORTH);

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 20, 20));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        JButton startButton = new JButton("Start");
        JButton exitButton = new JButton("Exit");

        startButton.setFont(DayDream.deriveFont(32f));
        exitButton.setFont(DayDream.deriveFont(32f));

        buttonPanel.add(startButton);
        buttonPanel.add(exitButton);

        // Center panel with GridBagLayout for positioning
        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(0, 0, 0, 0);

        center.add(buttonPanel, gbc);
        add(center, BorderLayout.CENTER);

        // Action listeners
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.startGame();
            }
        });

        exitButton.addActionListener(e -> System.exit(0));
    }
}
