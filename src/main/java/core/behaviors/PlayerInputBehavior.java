package core.behaviors;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import core.Game;
import core.io.InputHandler;
import core.math.PhysicEngineSystem;
import core.math.Vector2D;
import core.object.GameObject;
import core.object.GameObject.GameAction;
import core.object.World;
import core.system.SystemManager;
import lombok.extern.slf4j.Slf4j;

/**
 * The PlayerInputBehavior is the input manager for the player manage by the
 * user.
 */
@Slf4j
public class PlayerInputBehavior implements Behavior {

    private static GameAction idleAction = GameAction.IDLE;
    private InputHandler inputHandler;
    private PhysicEngineSystem pes;
    private static int lastIdleChange = 0;
    private static int lastIdleChangePace = 120;

    public PlayerInputBehavior() {
        lastIdleChange = 0;
        lastIdleChangePace = 120;
    }

    @Override
    public void initialize(Game dg) {
        inputHandler = SystemManager.get(InputHandler.class);
        pes = SystemManager.get(PhysicEngineSystem.class);
        log.debug("inputHandler and pes system are kept");
    }

    @Override
    public void create(Game dg, World w, GameObject go) {
    }

    @Override
    public void input(Game dg, GameObject go) {
        go.forces.clear();

        final double defaultAcc = 30;
        // go.setAcc(new Vector2D(0.0, 0.0));
        if (go.action == GameAction.FALL) {
            go.forces.add(pes.getWorld().getGravity());
        } else {
            go.action = idleAction;
        }

        if (inputHandler != null) {
            // reset horizontal speed if falling.
            if (inputHandler.keys[KeyEvent.VK_UP]) {
                go.forces.add(pes.getWorld().getGravity().multiply(-4));
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
            for(int k=KeyEvent.VK_1;k<KeyEvent.VK_5;k++){
                if (inputHandler.keys[k] && itemsNb <= k-KeyEvent.VK_1-1) {
                    go.attributes.put("selectedItem", k-KeyEvent.VK_1);
                }    
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