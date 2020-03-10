package e.wrod.net.utils;

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.File;
import java.net.MalformedURLException;

public class MusicPlayer {

    public String url;
    public AudioClip ac;

    public MusicPlayer(String url) throws MalformedURLException {
        this.url = url;
        File file = new File("/music/9621.wav");
        ac = Applet.newAudioClip(file.toURL());
    }

    public void play() {
        ac.play();
    }

    public void stop() {
        ac.stop();
    }

    public void loop() {
        ac.loop();
    }

    public static void main(String[] args) throws MalformedURLException {
        new MusicPlayer("/").play();
    }
}
