package core.io;

import core.Game;
import core.system.AbstractSystem;
import core.system.System;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

/**
 * InputHandler gaols consists in managing all input from any connected devices.
 * First is keyboard.
 */
public class InputHandler extends AbstractSystem implements KeyListener, System {

    /**
     * The current key states
     */
    public boolean[] keys = new boolean[65536];
    /**
     * The previous state of each keys.
     */
    public boolean[] previousKeys = new boolean[65536];

    /**
     * Game input key listeners.
     */
    public List<KeyListener> listeners = new ArrayList<>();

    /**
     * The parent game.
     */
    public Game game;

    /**
     * Create the InputHandler system.
     *
     * @param g the parent game.
     */
    public InputHandler(Game g) {
        super(g);
    }


    @Override
    public String getName() {
        return InputHandler.class.getCanonicalName();
    }

    @Override
    public int initialize(Game game) {
        return 0;
    }

    @Override
    public void dispose() {

    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        this.previousKeys[e.getKeyCode()] = this.keys[e.getKeyCode()];
        this.keys[e.getKeyCode()] = true;

        for (KeyListener kl : listeners) {
            kl.keyPressed(e);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        this.previousKeys[e.getKeyCode()] = this.keys[e.getKeyCode()];
        this.keys[e.getKeyCode()] = false;
        for (KeyListener kl : listeners) {
            kl.keyReleased(e);
        }
    }

    /**
     * Add a game key listener to the system.
     *
     * @param kl the KeyListener to be added.
     */
    public void addListener(KeyListener kl) {
        if (!listeners.contains(kl)) {
            listeners.add(kl);
        }
    }

    /**
     * Remove a specific game key listener.
     *
     * @param kl the KeyListener to be removed.
     */
    public void remove(KeyListener kl) {
        if (!listeners.contains(kl)) {
            listeners.remove(kl);
        }
    }
}
