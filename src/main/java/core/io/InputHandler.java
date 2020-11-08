package core.io;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import core.Game;
import core.system.AbstractSystem;
import lombok.extern.slf4j.Slf4j;

/**
 * InputHandler gaols consists in managing all input from any connected devices.
 * First is keyboard.
 *
 * @author Frédéric Delorme <frederic.delorme@gmail.com>
 * @since 2019
 */
@Slf4j
public class InputHandler extends AbstractSystem implements KeyListener {

    /**
     * The current key states
     */
    public boolean[] keys = new boolean[65536];

    // flag to represent state f the CTRL key
    public boolean control;
    // flag to represent state f the SHIFT key
    public boolean shift;
    // flag to represent state f the ALT key
    public boolean alt;
    // flag to represent state f the ALTGr key
    public boolean altGr;

    /**
     * The previous state of each keys.
     */
    public boolean[] previousKeys = new boolean[65536];

    /**
     * Game input key listeners.
     */
    public List<KeyListener> listeners = new CopyOnWriteArrayList<>();

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

    /**
     * retrieve name fo this system.
     *
     * @return
     */
    @Override
    public String getName() {
        return InputHandler.class.getCanonicalName();
    }

    /**
     * Initialize the system for the <code>game</code> !
     *
     * @param game
     * @return
     */
    @Override
    public int initialize(Game game) {
        return 0;
    }

    @Override
    public void dispose() {

    }

    /**
     * @see java.awt.event.KeyListener#keyTyped(KeyEvent)
     */
    @Override
    public void keyTyped(KeyEvent e) {
    }

    /**
     * @see java.awt.event.KeyListener#keyPressed(KeyEvent)
     */
    @Override
    public void keyPressed(KeyEvent e) {
        this.previousKeys[e.getKeyCode()] = this.keys[e.getKeyCode()];
        this.keys[e.getKeyCode()] = true;

        control = e.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK;
        shift = e.getModifiersEx() == KeyEvent.SHIFT_DOWN_MASK;
        alt = e.getModifiersEx() == KeyEvent.ALT_DOWN_MASK;
        altGr = e.getModifiersEx() == KeyEvent.ALT_GRAPH_DOWN_MASK;

        for (KeyListener kl : listeners) {
            kl.keyPressed(e);
        }

        log.debug("key {}:{} pressed", e.getKeyCode(), e.getKeyChar());
    }

    /**
     * @see java.awt.event.KeyListener#keyReleased(KeyEvent)
     */
    @Override
    public void keyReleased(KeyEvent e) {
        this.previousKeys[e.getKeyCode()] = this.keys[e.getKeyCode()];
        this.keys[e.getKeyCode()] = false;

        control = e.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK;
        shift = e.getModifiersEx() == KeyEvent.SHIFT_DOWN_MASK;
        alt = e.getModifiersEx() == KeyEvent.ALT_DOWN_MASK;
        altGr = e.getModifiersEx() == KeyEvent.ALT_GRAPH_DOWN_MASK;

        for (KeyListener kl : listeners) {
            kl.keyReleased(e);
        }
        log.debug("key {}:{} released", e.getKeyCode(), e.getKeyChar());
    }

    /**
     * Add a game key listener to the system.
     *
     * @param kl the KeyListener to be added.
     */
    public void addListener(KeyListener kl) {
        if (!listeners.contains(kl)) {
            listeners.add(kl);
        }else{
            log.warn("the listener {} is already set.",kl.getClass().getName());
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
