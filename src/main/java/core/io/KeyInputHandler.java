package core.io;

import core.Game;
import core.system.AbstractSystem;
import core.system.System;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyInputHandler extends AbstractSystem implements KeyListener, System {

    public boolean[] keys = new boolean[65536];
    public boolean[] previousKeys = new boolean[65536];

    public Game game;

    public KeyInputHandler(Game g) {
        super(g);
    }

    @Override
    public String getName() {
        return KeyInputHandler.class.getCanonicalName();
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
        game.onKeyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        this.previousKeys[e.getKeyCode()] = this.keys[e.getKeyCode()];
        this.keys[e.getKeyCode()] = false;
        game.onKeyReleased(e);
    }

}
