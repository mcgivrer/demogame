package samples.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.MouseInputListener;

import lombok.extern.slf4j.Slf4j;
import samples.Sample;
import samples.system.AbstractGameSystem;

@Slf4j
public class InputHandler extends AbstractGameSystem implements KeyListener, MouseInputListener {

    private List<InputHandlerListener> listeners = new ArrayList<>();

    protected InputHandler(Sample game) {
        super(game);
    }

    private boolean[] keys;
    private boolean[] prevKeys;
    private boolean[] mouseButton;
    private double mouseX = 0;
    private double mouseY = 0;
    private boolean shiftKey;
    private boolean ctrlKey;
    private boolean altKey;

    public void add(InputHandlerListener ihl) {
        if (!listeners.contains(ihl)) {
            listeners.add(ihl);

        } else {
            log.info("Input Handler list already contains {}", ihl.getClass().getName());
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        prevKeys[e.getKeyCode()] = keys[e.getKeyCode()];
        keys[e.getKeyCode()] = true;
        for(InputHandlerListener ihl:listeners){
            ihl.keyPressed(e);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        prevKeys[e.getKeyCode()] = keys[e.getKeyCode()];
        keys[e.getKeyCode()] = false;
        for(InputHandlerListener ihl:listeners){
            ihl.keyReleased(e);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        for(InputHandlerListener ihl:listeners){
            ihl.keyTyped(e);
        }
    }

    public boolean getKey(int code) {
        return keys[code];
    }

    public boolean isShift() {
        return false;
    }

    public boolean isCtrl() {
        return false;
    }

    public boolean isAlt() {
        return false;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public int initialize(Sample game) {
        super.initialize(game);
        keys = new boolean[65536];
        prevKeys = new boolean[65536];

        mouseButton = new boolean[2];
        return 0;
    }

    @Override
    public void dispose() {

    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

}