package e.word.net.component;

import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;

/**
 * 首页界面
 */
public class MusicPanel extends JPanel {
    private Image image;
    private AudioClip audio;

    public MusicPanel(String img, String music) {
        this.image = Toolkit.getDefaultToolkit().createImage(ClassLoader.getSystemResource(img));
        if (StringUtils.isNotEmpty(music)) {
            audio = Applet.newAudioClip(ClassLoader.getSystemResource(music));
        }
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
