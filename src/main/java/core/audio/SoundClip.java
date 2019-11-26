/**
 * SnapGames
 *
 * @see https://github.com//SnapGames/basic-game-framework/wiki
 * @since 2018
 */
package core.audio;

import lombok.extern.slf4j.Slf4j;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Class to play and manage a sound clip from file.
 *
 * @author Frédéric Delorme
 */
@Slf4j
public class SoundClip {

    private static int soundIndex = 0;

    private String code = "SOUND_" + (soundIndex++);
    /**
     * Java Sound clip to be read.
     */
    private Clip clip;
    /**
     * Volume control.
     */
    private FloatControl gainControl;
    /**
     * Pan Control.
     */
    private FloatControl panControl;
    /**
     * Balance Control.
     */
    private FloatControl balanceControl;

    public SoundClip(String code, InputStream is) {
        this.code = code;
        try {
            loadFromStream(code, is);
        } catch (Exception e) {
            log.error("unable to load sound file {}", code, e);
        }
    }

    /**
     * Initialize the sound clip ready to play from the file at <code>path</code>.
     *
     * @param path Path to the sound clip to be read.
     */
    public SoundClip(String path) {
        try {
            InputStream audioSrc = SoundClip.class.getResourceAsStream("/" + path);
            if (audioSrc == null) {
                log.error("unable to read the sound file {}", path);
            } else {
                loadFromStream(path, audioSrc);
            }
        } catch (Exception e) {
            log.error("unable to play the sound file {}", path, e);
        }

    }

    private void loadFromStream(String path, InputStream audioSrc)
            throws UnsupportedAudioFileException, IOException, LineUnavailableException {

        InputStream bufferedIn = new BufferedInputStream(audioSrc);
        AudioInputStream ais = AudioSystem.getAudioInputStream(bufferedIn);
        AudioFormat baseFormat = ais.getFormat();
        log.debug("SoundClip '{}' with Base format: [{}]", path, baseFormat);

        AudioFormat decodeFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(), 16,
                baseFormat.getChannels(), baseFormat.getChannels() * 2, baseFormat.getSampleRate(), false);
        AudioInputStream dais = AudioSystem.getAudioInputStream(decodeFormat, ais);
        clip = AudioSystem.getClip();
        /*
         * bugfix proposed
         *
         * @see
         * https://stackoverflow.com/questions/5808560/error-playing-sound-java-no-line-
         * matching-interface-clip-supporting-format#answer-41647865
         */
        clip.addLineListener(event -> {
            if (LineEvent.Type.STOP.equals(event.getType())) {
                clip.close();
            }
        });
        if (!clip.isActive() && !clip.isRunning()) {
            clip.open(dais);
        }

        if (clip.isControlSupported(FloatControl.Type.BALANCE)) {
            balanceControl = (FloatControl) clip.getControl(FloatControl.Type.BALANCE);
        } else {
            log.debug("BALANCE control is not supported");
        }

        if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        } else {
            log.debug("MASTER_GAIN control is not supported");
        }
        if (clip.isControlSupported(FloatControl.Type.PAN)) {
            panControl = (FloatControl) clip.getControl(FloatControl.Type.PAN);
        } else {
            log.debug("PAN control is not supported");
        }
    }

    /**
     * Start playing the clip.
     */
    public void play() {
        if (clip == null) {
            return;
        } else {
            while (clip.isActive()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                clip.flush();
                clip.stop();
            }
            clip.setFramePosition(0);
            clip.start();
        }
    }

    public void play(float pan, float volume) {
        setPan(pan);
        setVolume(volume);
        play();
    }

    public void play(float pan, float volume, float balance) {
        setPan(pan);
        setBalance(balance);
        setVolume(volume);
        play();
    }

    /**
     * Set balance for this sound clip.
     *
     * @param balance
     */
    private void setBalance(float balance) {
        balanceControl.setValue(balance);
    }

    /**
     * Set Panning for this sound clip.
     *
     * @param pan
     */
    public void setPan(float pan) {
        panControl.setValue(pan);
    }

    /**
     * Set Volume for this sound clip.
     *
     * @param volume
     */
    public void setVolume(float volume) {
        float min = gainControl.getMinimum() / 4;
        if (volume != 1) {
            gainControl.setValue(min * (1 - volume));
        }
    }

    /**
     * Stop playing the clip.
     */
    public void stop() {
        if (clip == null) {
            return;
        } else if (clip.isRunning()) {
            clip.stop();
        }
    }

    /**
     * Loop the clip continuously
     */
    public void loop() {
        clip.loop(Clip.LOOP_CONTINUOUSLY);
        while (!clip.isRunning()) {
            clip.start();
        }
    }

    /**
     * Close the clip.
     */
    public void close() {
        stop();
        clip.drain();
        clip.close();
    }

    /**
     * is the clip is playing ?
     *
     * @return
     */
    public boolean isPlaying() {
        return clip.isRunning();
    }

    /**
     * return the Code for this soundclip.
     *
     * @return
     */
    public Object getCode() {
        return this.code;
    }

}