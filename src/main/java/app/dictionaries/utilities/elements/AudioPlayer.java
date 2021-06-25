package app.dictionaries.utilities.elements;

import app.AppMain;
import javazoom.jl.player.Player;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

/**
 * Class AudioPlayer
 */
public class AudioPlayer {
    private static AudioPlayer instance = new AudioPlayer();

    private Player player;

    public static final String filename = AppMain.pathToDirectory + ("resources/music/").replaceAll("/", File.separator);

    public static String currentTarget = "";

    /**
     * Default constructor
     */
    private AudioPlayer() {
    }

    /**
     * Get instance singleton
     *
     * @return instance
     */
    public static AudioPlayer getInstance() {
        return instance;
    }

    /**
     * Play sound
     */
    public void play() {
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filename + currentTarget + ".mp3"));
            player = new Player(bis);
        } catch (Exception ex) {
            System.out.println(ex);
        }

        new Thread(() -> {
            try {
                player.play();
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }).start();
    }

    /**
     * Play sound with path given
     *
     * @param path path to sound
     */
    public void play(String path) {
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path));
            player = new Player(bis);
        } catch (Exception ex) {
            System.out.println(ex);
        }

        new Thread(() -> {
            try {
                player.play();
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }).start();
    }
}
