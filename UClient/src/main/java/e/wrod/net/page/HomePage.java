package e.wrod.net.page;

import e.wrod.net.Client;

import javax.swing.*;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;

/**
 * 首页界面
 */
public class HomePage extends JPanel {
    private Image image;
    private String music;
    private AudioClip audio;

    public HomePage(Image image, String music) {
        this.image = image;
        this.music = music;
        audio = Applet.newAudioClip(Client.class.getResource("/music/9621.wav"));
    }

    public void playMusic() {
        audio.play();
    }

    public void stopMusic() {
        audio.stop();
    }

    public void loopMusic() {
        audio.loop();
    }

    @Override
    public void paint(Graphics g) {
        // TODO Auto-generated method stub
        super.paint(g);
        g.drawImage(image, 0, 0, this);
    }
}
