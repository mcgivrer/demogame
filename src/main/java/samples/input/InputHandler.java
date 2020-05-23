package samples.input;

import java.awt.MouseInfo;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import lombok.extern.slf4j.Slf4j;
import samples.Sample;
import samples.system.AbstractGameSystem;

@Slf4j
public class InputHandler extends AbstractGameSystem
        implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

    private List<InputHandlerListener> listeners = new ArrayList<>();

    private Queue<InputEvent> events;

    private boolean[] keys;
    private boolean[] prevKeys;
    private boolean[] mouseButtons;
    private double mouseWheelRotation=0;
    private double mouseX = 0;
    private double mouseY = 0;

    public InputHandler(Sample game) {
        super(game);
    }

    @Override
    public int initialize(Sample game) {
        super.initialize(game);
        keys = new boolean[65536];
        prevKeys = new boolean[65536];
        events = new LinkedBlockingQueue<InputEvent>(100);
        int mouseNumberOfButtons = MouseInfo.getNumberOfButtons();
        mouseButtons = new boolean[mouseNumberOfButtons];
        return 0;
    }

    public void register(InputHandlerListener ihl) {
        if (!listeners.contains(ihl)) {
            listeners.add(ihl);

        } else {
            log.info("Input Handler list already contains {}", ihl.getClass().getName());
        }
    }

    private void pushEvent(InputEvent e) {
        if(events.size()==100){
            events.poll();
        }
        events.add(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        pushEvent(e);
        prevKeys[e.getKeyCode()] = keys[e.getKeyCode()];
        keys[e.getKeyCode()] = true;
        for (InputHandlerListener ihl : listeners) {
            ihl.keyPressed(e);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        pushEvent(e);
        prevKeys[e.getKeyCode()] = keys[e.getKeyCode()];
        keys[e.getKeyCode()] = false;
        for (InputHandlerListener ihl : listeners) {
            ihl.keyReleased(e);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        pushEvent(e);
        for (InputHandlerListener ihl : listeners) {
            ihl.keyTyped(e);
        }
    }

    public boolean getKey(int code) {
        return keys[code];
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        pushEvent(e);
        mouseButtons[e.getButton()] = true;
        mouseX = e.getX();
        mouseY = e.getY();

    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        pushEvent(e);
        mouseButtons[e.getButton()] = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        pushEvent(e);
        mouseButtons[e.getButton()] = false;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        pushEvent(e);
        mouseX = e.getX();
        mouseY = e.getY();
    }
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        mouseWheelRotation = e.getWheelRotation();
    }

    public InputEvent getEvent() {
        return events.poll();
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    public double getMouseX() {
        return mouseX;
    }

    public double getMouseY() {
        return mouseY;
    }

    public double getMouseWheelRotation() {
        return mouseWheelRotation;
    }

    @Override
    public void dispose() {
        keys     = null;
        prevKeys = null;
        events   = null;
    }
}