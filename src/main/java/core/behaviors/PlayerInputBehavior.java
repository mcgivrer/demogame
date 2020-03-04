package core.behaviors;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import core.Game;
import core.io.InputHandler;
import core.math.Vector2D;
import core.object.GameObject;
import core.object.GameObject.GameAction;
import core.object.World;

public class PlayerInputBehavior implements Behavior {

    private static GameAction idleAction = GameAction.IDLE;
    InputHandler inputHandler;
    private static int lastIdleChange = 0;
    private static int lastIdleChangePace = 120;

    public PlayerInputBehavior() {

    }

    @Override
    public void initialize(Game dg) {
        inputHandler = dg.sysMan.getSystem(InputHandler.class);
    }

    @Override
    public void create(Game dg, World w, GameObject go) {
    }

    @Override
    public void input(Game dg, GameObject go) {
        go.forces.clear();

        final double defaultAcc = 0.2;
        // go.setAcc(new Vector2D(0.0, 0.0));
        if (go.action == GameAction.FALL) {
            go.forces.add(new Vector2D(0.0, defaultAcc*3));
        } else {
            go.action = idleAction;
        }

        if (inputHandler != null) {
            // reset horizontal speed if falling.
            if (inputHandler.keys[KeyEvent.VK_UP]) {
                go.forces.add(new Vector2D(0.0, -defaultAcc));
                go.action = GameAction.JUMP;
            }
            if (inputHandler.keys[KeyEvent.VK_DOWN]) {
                go.acc.y = 0.1f;
                go.action = GameAction.DOWN;
            }

            if (inputHandler.keys[KeyEvent.VK_LEFT]) {
                go.forces.add(new Vector2D(-defaultAcc, 0.0));
                go.direction = -1;
                go.action = (!inputHandler.shift ? GameAction.WALK : GameAction.RUN);
            } else if (inputHandler.keys[KeyEvent.VK_RIGHT]) {
                go.forces.add(new Vector2D(defaultAcc, 0.0));
                go.direction = 1;
                go.action = (!inputHandler.shift ? GameAction.WALK : GameAction.RUN);
            }

            int itemsNb = go.items.size();
            if (inputHandler.keys[KeyEvent.VK_1] && itemsNb <= 1) {
                go.attributes.put("selectedItem", 1.0);
            }
            if (inputHandler.keys[KeyEvent.VK_2] && itemsNb <= 2) {
                go.attributes.put("selectedItem", 2.0);
            }
            if (inputHandler.keys[KeyEvent.VK_3] && itemsNb <= 3) {
                go.attributes.put("selectedItem", 3.0);
            }
            if (inputHandler.keys[KeyEvent.VK_4] && itemsNb <= 4) {
                go.attributes.put("selectedItem", 4.0);
            }
            if (inputHandler.keys[KeyEvent.VK_5] && itemsNb <= 5) {
                go.attributes.put("selectedItem", 5.0);
            }
        }
    }

    @Override
    public void update(Game dg, GameObject go, double elapsed) {
        // compute next value for Idle
        lastIdleChange++;
        if (lastIdleChange > lastIdleChangePace) {
            double rndAction = (Math.random() * 1.0) + 0.5;
            idleAction = go.action = (rndAction > 1.0 ? GameAction.IDLE : GameAction.IDLE2);
            lastIdleChange = 0;
            lastIdleChangePace = (int) ((Math.random() * 100.0) + 100.0);
        }
    }

    @Override
    public void render(Game dg, GameObject go, Graphics2D g) {
    }

    @Override
    public void dispose(Game dg, World w, GameObject go) {
    }

}