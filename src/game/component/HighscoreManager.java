package game.component;

import java.io.*;

public class HighscoreManager {
    
    private static final String FILE_NAME = "highscore.txt";
    private int highscore;

    public HighscoreManager() {
        highscore = load();
    }
    
    private int load() {
        int score = 0;
        File file = new File(FILE_NAME);
        if(file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line = br.readLine();
                if(line != null) {
                    score = Integer.parseInt(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return score;
    }
    
    public void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            bw.write(String.valueOf(highscore));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getHighscore() {
        return highscore;
    }

    public void updateHighscore(int score) {
        if(score > highscore) {
            highscore = score;
            save();
        }
    }
}
