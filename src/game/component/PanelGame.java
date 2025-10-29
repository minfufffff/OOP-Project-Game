package game.component;

import game.obj.Bullet;
import game.obj.Effect;
import game.obj.Medkit;
import game.obj.Meteor;
import game.obj.Player;
import game.obj.Rocket;
import game.obj.sound.Sound;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.util.Random;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.io.*;


public class PanelGame extends JComponent {

    private Graphics2D g2;
    private BufferedImage image;
    private int width;
    private int height;
    private Thread thread;
    private boolean start = true;
    private Key key;
    private int shotTimeMouse = 0;
    private int shotTimeKey = 0;
    private int shotTimeMouseBig = 0;
    private int shotTimeKeyBig = 0;
    private long colorTimer = System.currentTimeMillis();
    private Color gameOverColor = Color.WHITE;
    private Random rand = new Random();
    private final int FPS = 60;
    private final int TARGET_TIME = 1000000000 / FPS;
    // Game Object
    private Player player;
    private List<Bullet> bullets;
    private List<Rocket> rockets;
    private List<Meteor> meteors;
    private List<Effect> boomEffects;
    private List<Medkit> medkits = new ArrayList<>();
    private long lastMedkitTime = 0;
    private static final long MEDKIT_COOLDOWN = 5000;
    private static final long MEDKIT_LIFETIME = 15000;
    private HighscoreManager highscoreManager;
    public int score=0;
    public static int highscore=0;
    private Sound sound;

    private List<Point> stars;
    private List<Integer> starSizes;
    public Font DayDream;


    public void start() {
        initFont();
        highscoreManager = new HighscoreManager();
        width = getWidth();
        height = getHeight();
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        initStars();

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (start) {
                    long startTime = System.nanoTime();
                    drawBackground();
                    drawGame();
                    render();
                    long time = System.nanoTime() - startTime;
                    if (time < TARGET_TIME) {
                        long sleep = (TARGET_TIME - time) / 1000000;
                        sleep(sleep);
                    }
                }
            }
        });

        intObjectGame();
        initKeyboard();
        initMouse();
        initBullet();
        thread.start();
    }
    
    private void initFont() {
    try {
        DayDream = Font.createFont(
            Font.TRUETYPE_FONT,
            getClass().getResourceAsStream("/font/Minecraft.ttf")
        ).deriveFont(15f);
    } catch (Exception e) {
        e.printStackTrace();
        DayDream = new Font("Monospaced", Font.PLAIN, 15); // fallback
        }
    }

    private void initStars() {
        stars = new ArrayList<>();
        starSizes = new ArrayList<>();
        Random random = new Random();
        int numDots = random.nextInt(500);
        for (int i = 0; i < numDots; i++) {
            int x = random.nextInt(Math.max(width, 1));
            int y = random.nextInt(Math.max(height, 1));
            int size = random.nextInt(4) + 1;
            stars.add(new Point(x, y));
            starSizes.add(size);
        }
    }

    private void addRocket() {
        Random ran = new Random();
        int locationY = ran.nextInt(height - 50) + 25;
        Rocket rocket = new Rocket();
        rocket.changeLocation(0, locationY);
        rocket.changeAngle(0);
        rockets.add(rocket);
        int locationY2 = ran.nextInt(height - 50) + 25;
        Rocket rocket2 = new Rocket();
        rocket2.changeLocation(width, locationY2);
        rocket2.changeAngle(180);
        rockets.add(rocket2);
    }
    
    private void addMeteor() {
        Random ran = new Random();
        int locationY = ran.nextInt(height - 50) + 25;
        Meteor meteor = new Meteor();
        meteor.changeLocation(0, locationY);
        meteor.changeAngle(0);
        meteors.add(meteor);
        int locationY2 = ran.nextInt(height - 50) + 25;
        Meteor meteor2 = new Meteor();
        meteor2.changeLocation(width, locationY2);
        meteor2.changeAngle(180);
        meteors.add(meteor2);
    }
    
    private void spawnMedkit() {
        if (System.currentTimeMillis() - lastMedkitTime < MEDKIT_COOLDOWN) {
            return;
        }
        Random rand = new Random();
        double medSize = Medkit.MED_SIZE;
        double x, y;
        int maxTry = 50;

        for (int i = 0; i < maxTry; i++) {
            x = rand.nextInt(getWidth() - (int) medSize);
            y = rand.nextInt(getHeight() - (int) medSize);

            Medkit newMed = new Medkit(x, y);

            if (!player.getShape().intersects(newMed.getShape().getBounds2D())) {
                medkits.add(newMed);
                lastMedkitTime = System.currentTimeMillis();
                break;
               }
        }
    }
    
    private void checkMedkitPickup() {
        for (int i = 0; i < medkits.size(); i++) {
            Medkit med = medkits.get(i);
            if (player.getShape().intersects(med.getShape().getBounds2D())) {
                medkits.remove(i);
                i--;
                player.getHp().addHP();
                sound.soundheal();
            }
        }
    }
    
    private void removeExpiredMedkits() {
    long now = System.currentTimeMillis();
    for (int i = 0; i < medkits.size(); i++) {
        Medkit med = medkits.get(i);
        if (now - med.getSpawnTime() > MEDKIT_LIFETIME) {
            medkits.remove(i);
            i--;
        }
    }
}

    private void intObjectGame() {
        sound = new Sound();
        player = new Player();
        player.changeLocation(150, 150);
        rockets = new ArrayList<>();
        meteors = new ArrayList<>();
        boomEffects = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (start) {
                    addRocket();
                    addMeteor();
                    spawnMedkit();
                    sleep(3000);
                }
            }
        }).start();
    }
    
    private void resetGame(){
        highscoreManager.updateHighscore(score);
        score = 0;
        score = 0;
        Random ran = new Random();
        int locaX = ran.nextInt(width+1);
        int locaY = ran.nextInt(height+1);
        int newAngle = ran.nextInt(361);
        rockets.clear();
        meteors.clear();
        bullets.clear();
        player.changeLocation(locaX,locaY);
        player.reset();
        player.changeAngle(newAngle);
        medkits.clear();
        boomEffects.clear();
        initStars();
    }

    private void initMouse() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();

                if (e.getButton() == MouseEvent.BUTTON1) {
                    key.setKey_mouse1(true);
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    key.setKey_mouse2(true);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    key.setKey_mouse1(false);
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    key.setKey_mouse2(false);
                }
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (start) {
                    if(player.isAlive()){
                        if (key.isKey_mouse1()) {
                            if (shotTimeKey == 0) {
                                bullets.add(new Bullet(player.getX(), player.getY(), player.getAngle(), 3, 3f));
                                sound.soundSmall();
                                shotTimeKey = 1;
                            }
                        }

                        if (key.isKey_mouse2()) {
                            if (shotTimeKeyBig == 0) {
                                bullets.add(new Bullet(player.getX(), player.getY(), player.getAngle(), 10, 3f));
                                sound.soundBig();
                                shotTimeKeyBig = 1;
                            }
                        }

                        if (shotTimeMouse > 0) {
                            shotTimeMouse++;
                            if (shotTimeMouse >= 20) shotTimeMouse = 0;
                        }

                        if (shotTimeMouseBig > 0) {
                            shotTimeMouseBig++;
                            if (shotTimeMouseBig >= 100) shotTimeMouseBig = 0;
                        }
                    } else {
                        
                    }
                    removeExpiredMedkits();
                    checkMedkitPickup();
                    sleep(5);
                }
            }
        }).start();
    }

    private void initKeyboard() {
        key = new Key();
        requestFocus();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_A) {
                    key.setKey_left(true);
                } else if (e.getKeyCode() == KeyEvent.VK_D) {
                    key.setKey_right(true);
                } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    key.setKey_space(true);
                } else if (e.getKeyCode() == KeyEvent.VK_J) {
                    key.setKey_j(true);
                } else if (e.getKeyCode() == KeyEvent.VK_K) {
                    key.setKey_k(true);
                } else if (e.getKeyCode() == KeyEvent.VK_W) {
                    key.setKey_w(true);
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    key.setKey_enter(true);
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    key.setKey_escape(true);
                } else if (e.getKeyCode() == KeyEvent.VK_R) {
                    key.setKey_r(true);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_A) {
                    key.setKey_left(false);
                } else if (e.getKeyCode() == KeyEvent.VK_D) {
                    key.setKey_right(false);
                } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    key.setKey_space(false);
                } else if (e.getKeyCode() == KeyEvent.VK_J) {
                    key.setKey_j(false);
                } else if (e.getKeyCode() == KeyEvent.VK_K) {
                    key.setKey_k(false);
                } else if (e.getKeyCode() == KeyEvent.VK_W) {
                    key.setKey_w(false);
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    key.setKey_enter(false);
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    key.setKey_escape(false);
                } else if (e.getKeyCode() == KeyEvent.VK_R) {
                    key.setKey_r(false);
                }
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                float s = 0.5f;
                while (start) {
                    if(player.isAlive()){
                        float angle = player.getAngle();
                        if (key.isKey_left()) {
                            angle -= s;
                        }
                        if (key.isKey_right()) {
                            angle += s;
                        }
                        if (key.isKey_j()) {
                            if (shotTimeKey == 0) {
                                bullets.add(new Bullet(player.getX(), player.getY(), player.getAngle(), 3, 3f));
                                sound.soundSmall();
                                shotTimeKey = 1;
                            }
                        }

                        if (key.isKey_k()) {
                            if (shotTimeKeyBig == 0) {
                                bullets.add(new Bullet(player.getX(), player.getY(), player.getAngle(), 10, 3f));
                                sound.soundBig();
                                shotTimeKeyBig = 1;
                            }
                        }
                        
                        if (key.isKey_r()){
                            resetGame();
                            sleep(1000);
                        }

                        if (shotTimeKey > 0) {
                            shotTimeKey++;
                            if (shotTimeKey >= 20) shotTimeKey = 0;
                        }

                        if (shotTimeKeyBig > 0) {
                            shotTimeKeyBig++;
                            if (shotTimeKeyBig >= 100) shotTimeKeyBig = 0;
                        }
                        
                        if (key.isKey_space() || key.isKey_w()) {
                            player.speedUp();
                        } else {
                            player.speedDown();
                        }
                        if(key.isKey_escape()){
                            System.exit(0);
                        }
                        player.update();
                        player.changeAngle(angle);
                        player.wrapAround(width, height);
                    }else{
                        if(key.isKey_enter()){
                            resetGame();
                        }
                        if(key.isKey_escape()){
                            System.exit(0);
                        }
                    }
                    for (int i = 0; i < rockets.size(); i++) {
                        Rocket rocket = rockets.get(i);
                        if (rocket != null) {
                            rocket.update();
                            if (!rocket.check(width, height)) {
                                rockets.remove(rocket);
                            }else{
                                if(player.isAlive()){
                                    checkPlayer(rocket);
                                }
                            }
                        }
                    }
                    for (int i = 0; i < meteors.size(); i++) {
                        Meteor meteor = meteors.get(i);
                        if (meteor != null) {
                            meteor.update();
                            if (!meteor.check(width, height)) {
                                meteors.remove(meteor);
                            }else{
                                if(player.isAlive()){
                                    checkPlayer(meteor);
                                }
                            }
                        }
                    }
                    removeExpiredMedkits();
                    checkMedkitPickup();
                    sleep(5);
                }
            }
        }).start();
    }

    private void drawBackground() {
        g2.setColor(new Color(30, 30, 30));
        g2.fillRect(0, 0, width, height);

        g2.setColor(Color.WHITE);
        for (int i = 0; i < stars.size(); i++) {
            Point p = stars.get(i);
            int size = starSizes.get(i);
            g2.fillOval(p.x, p.y, size, size);
        }
    }

    private void initBullet() {
        bullets = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (start) {
                    for (int i = 0; i < bullets.size(); i++) {
                        Bullet bullet = bullets.get(i);
                        if (bullets != null) {
                            bullet.update();
                            checkBullets(bullet);
                            if (!bullet.check(width, height)) {
                                bullets.remove(bullet);
                            }
                        } else {
                            bullets.remove(bullet);
                        }
                    }
                    for (int i = 0; i < boomEffects.size(); i++) {
                        Effect boomEffect = boomEffects.get(i);
                        if (boomEffect != null) {
                            boomEffect.update();
                            if (!boomEffect.check()) {
                                boomEffects.remove(boomEffect);
                            }
                        } else {
                            boomEffects.remove(boomEffect);
                        }
                    }
                    sleep(1);
                }
            }
        }).start();
    }

    private void checkBullets(Bullet bullet) {
        for (int i = 0; i < rockets.size(); i++) {
            Rocket rocket = rockets.get(i);
            if (rocket != null) {
                Area area = new Area(rocket.getShape());
                area.intersect(new Area(bullet.getShape()));
                if (!area.isEmpty()) {
                    boomEffects.add(new Effect(bullet.getCenterX(), bullet.getCenterY(), 3, 5, 60, 0.5f, new Color(130, 207, 105)));
                    if (!rocket.updateHP(bullet.getSize())) {
                        score++;
                        rockets.remove(rocket);
                        sound.soundshortBoom();
                        double x = rocket.getX() + Rocket.ROCKET_SIZE / 2;
                        double y = rocket.getY() + Rocket.ROCKET_SIZE / 2;
                        boomEffects.add(new Effect(x, y, 5, 5, 75, 0.05f, new Color(255, 70, 70)));
                        boomEffects.add(new Effect(x, y, 5, 5, 75, 0.1f, new Color(32, 178, 169)));
                        boomEffects.add(new Effect(x, y, 10, 5, 100, 0.05f, new Color(255, 70, 70)));
                        boomEffects.add(new Effect(x, y, 10, 5, 150, 0.02f, new Color(32, 178, 169)));
                    }
                    bullets.remove(bullet);
                }
            }
        }
        for (int i = 0; i < meteors.size(); i++) {
            Meteor meteor = meteors.get(i);
            if (meteor != null) {
                Area area = new Area(meteor.getShape());
                area.intersect(new Area(bullet.getShape()));
                if (!area.isEmpty()) {
                    boomEffects.add(new Effect(bullet.getCenterX(), bullet.getCenterY(), 3, 5, 60, 0.5f, new Color(130, 207, 105)));
                    if (!meteor.updateHP(bullet.getSize())) {
                        score += 2;
                        meteors.remove(meteor);
                        sound.soundshortBoom();
                        double x = meteor.getX() + Rocket.ROCKET_SIZE / 2;
                        double y = meteor.getY() + Rocket.ROCKET_SIZE / 2;
                        boomEffects.add(new Effect(x, y, 5, 5, 75, 0.05f, new Color(137, 145, 140)));
                        boomEffects.add(new Effect(x, y, 5, 5, 75, 0.1f, new Color(116, 124, 119)));
                        boomEffects.add(new Effect(x, y, 10, 5, 100, 0.05f, new Color(137, 145, 140)));
                        boomEffects.add(new Effect(x, y, 10, 5, 150, 0.02f, new Color(116, 124, 119)));
                    }
                    bullets.remove(bullet);
                }
            }
        }
    }
    
    private void checkPlayer(Rocket rocket) {
            if (rocket != null) {
                Area area = new Area(player.getShape());
                area.intersect(new Area(rocket.getShape()));
                if (!area.isEmpty()) {
                    double rocketHp=rocket.getHP();
                    if (!rocket.updateHP(player.getHP())) {
                        rockets.remove(rocket);
                        sound.soundBoom();
                        double x = rocket.getX() + Rocket.ROCKET_SIZE / 2;
                        double y = rocket.getY() + Rocket.ROCKET_SIZE / 2;
                        boomEffects.add(new Effect(x, y, 5, 5, 75, 0.05f, new Color(255, 70, 70)));
                        boomEffects.add(new Effect(x, y, 5, 5, 75, 0.1f, new Color(32, 178, 169)));
                        boomEffects.add(new Effect(x, y, 10, 5, 100, 0.05f, new Color(255, 70, 70)));
                        boomEffects.add(new Effect(x, y, 10, 5, 150, 0.02f, new Color(32, 178, 169)));
                    }
                    if (!player.updateHP(rocketHp)) {
                        player.setAlive(false);
                        sound.soundBoom();
                        double x = rocket.getX() + Rocket.ROCKET_SIZE / 2;
                        double y = rocket.getY() + Rocket.ROCKET_SIZE / 2;
                        boomEffects.add(new Effect(x, y, 5, 5, 75, 0.05f, new Color(255, 70, 70)));
                        boomEffects.add(new Effect(x, y, 5, 5, 75, 0.1f, new Color(32, 178, 169)));
                        boomEffects.add(new Effect(x, y, 10, 5, 100, 0.05f, new Color(255, 70, 70)));
                        boomEffects.add(new Effect(x, y, 10, 5, 150, 0.02f, new Color(32, 178, 169)));
                    }
                }
            }
        }
    
    private void checkPlayer(Meteor meteor) {
            if (meteor != null) {
                Area area = new Area(player.getShape());
                area.intersect(new Area(meteor.getShape()));
                if (!area.isEmpty()) {
                    double meteorHp=meteor.getHP();
                    if (!meteor.updateHP(player.getHP())) {
                        meteors.remove(meteor);
                        sound.soundBoom();
                        double x = meteor.getX() + Meteor.METEOR_SIZE / 2;
                        double y = meteor.getX() + Meteor.METEOR_SIZE / 2;
                        boomEffects.add(new Effect(x, y, 5, 5, 75, 0.05f, new Color(255, 70, 70)));
                        boomEffects.add(new Effect(x, y, 5, 5, 75, 0.1f, new Color(32, 178, 169)));
                        boomEffects.add(new Effect(x, y, 10, 5, 100, 0.05f, new Color(255, 70, 70)));
                        boomEffects.add(new Effect(x, y, 10, 5, 150, 0.02f, new Color(32, 178, 169)));
                    }
                    if (!player.updateHP(meteorHp)) {
                        player.setAlive(false);
                        sound.soundBoom();
                        double x = meteor.getX() + Meteor.METEOR_SIZE / 2;
                        double y = meteor.getY() + Meteor.METEOR_SIZE / 2;
                        boomEffects.add(new Effect(x, y, 5, 5, 75, 0.05f, new Color(255, 70, 70)));
                        boomEffects.add(new Effect(x, y, 5, 5, 75, 0.1f, new Color(32, 178, 169)));
                        boomEffects.add(new Effect(x, y, 10, 5, 100, 0.05f, new Color(255, 70, 70)));
                        boomEffects.add(new Effect(x, y, 10, 5, 150, 0.02f, new Color(32, 178, 169)));
                    }
                }
            }
        }
    private void drawGame() {
        if(player.isAlive()){
            player.draw(g2);
        }
        for (Medkit med : medkits) {
            med.draw(g2);
        }
        for (int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);
            if (bullet != null) {
                bullet.draw(g2);
            }
        }
        for (int i = 0; i < rockets.size(); i++) {
            Rocket rocket = rockets.get(i);
            if (rocket != null) {
                rocket.draw(g2);
            }
        }
        for (int i = 0; i < meteors.size(); i++) {
            Meteor meteor = meteors.get(i);
            if (meteor != null) {
                meteor.draw(g2);
            }
        }
        for (int i = 0; i < boomEffects.size(); i++) {
            Effect boomEffect = boomEffects.get(i);
            if (boomEffect != null) {
                boomEffect.draw(g2);
            }
        }
        g2.setColor(Color.white);
        g2.setFont(DayDream.deriveFont(20f));
        g2.drawString("Score : " + score, 10, 20);
        g2.drawString("High Score : " + highscoreManager.getHighscore(), 120, 20);
        //g2.setFont(getFont().deriveFont(Font.BOLD, 15f));
        
        
        if (!player.isAlive()) {
            long now = System.currentTimeMillis();
            if (now - colorTimer > 1000) {
            colorTimer = now;
            gameOverColor = new Color(
                rand.nextInt(256),
                rand.nextInt(256),
                rand.nextInt(256)
            );
        }
            String text = "GAME OVER";
            String textKey = "Press ENTER to Restart...";
            String textKey2 = "Press ESC to Exit...";
            g2.setColor(gameOverColor);
            g2.setFont(DayDream.deriveFont(70f));
            FontMetrics fm = g2.getFontMetrics();
            Rectangle2D r2 = fm.getStringBounds(text, g2);
            double textWidth = r2.getWidth();
            double textHeight = r2.getHeight();
            double x = (width - textWidth) / 2;
            double y = (height - textHeight) / 2;
            g2.drawString(text, (int) x, (int) y + fm.getAscent() - 70);
            g2.setFont(DayDream.deriveFont(50f));
            fm = g2.getFontMetrics();
            r2 = fm.getStringBounds(textKey, g2);
            textWidth = r2.getWidth();
            textHeight = r2.getHeight();
            x = (width - textWidth) / 2;
            y = (height - textHeight) / 2;
            g2.drawString(textKey, (int) x, (int) y + fm.getAscent() + 0);
            r2 = fm.getStringBounds(textKey2, g2);
            textWidth = r2.getWidth();
            textHeight = r2.getHeight();
            x = (width - textWidth) / 2;
            y = (height - textHeight) / 2;
            g2.drawString(textKey2, (int) x, (int) y + fm.getAscent() + 70);
            
        }
    }

    private void render() {
        Graphics g = getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
    }

    private void sleep(long speed) {
        try {
            Thread.sleep(speed);
        } catch (InterruptedException ex) {
            System.err.println(ex);
        }
    }
}