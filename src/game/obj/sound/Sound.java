package game.obj.sound;

import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

public class Sound {
    
    private final URL small;
    private final URL big;
    private final URL boom;
    private final URL shortboom;
    private final URL heal;
    private final URL bg;
    
    public Sound(){
        this.small = this.getClass().getClassLoader().getResource("game/obj/sound/small.wav");
        this.big = this.getClass().getClassLoader().getResource("game/obj/sound/big.wav");
        this.boom = this.getClass().getClassLoader().getResource("game/obj/sound/boom.wav");
        this.shortboom = this.getClass().getClassLoader().getResource("game/obj/sound/shortboom.wav");
        this.heal = this.getClass().getClassLoader().getResource("game/obj/sound/heal.wav");
        this.bg = this.getClass().getClassLoader().getResource("game/obj/sound/Endless void.wav");
    }
    
    public void soundSmall(){
        play(small,.7f);
    }
    
    public void soundBig(){
        play(big,.7f);
    }
    
    public void soundBoom(){
        play(boom,.7f);
    }
    
    public void soundshortBoom(){
        play(shortboom,.7f);
    }
    
    public void soundheal(){
        play(heal,.5f);
    }
    
    public void soundBg(){
        playLoop(bg,.5f);
    }
    
    private void play(URL url, float volume){ // volume: 0.0f = ปิดเสียง, 1.0f = เต็ม
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);

            if (clip.isControlSupported(javax.sound.sampled.FloatControl.Type.MASTER_GAIN)) {
                FloatControl gainControl = 
                    (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float dB = (float)(20.0 * Math.log10(volume <= 0.0 ? 0.0001 : volume));
                gainControl.setValue(dB);
            }

            clip.addLineListener(new LineListener(){
                @Override
                public void update(LineEvent event){
                    if(event.getType()==LineEvent.Type.STOP){
                        clip.close();
                    }
                }
            });

            audioIn.close();
            clip.start();
        } catch (Exception e) {
            System.err.println(e);
        }
    }
    
    private void playLoop(URL url, float volume){
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);

            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gainControl = 
                    (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float dB = (float)(20.0 * Math.log10(volume <= 0.0 ? 0.0001 : volume));
                gainControl.setValue(dB);
            }

            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
