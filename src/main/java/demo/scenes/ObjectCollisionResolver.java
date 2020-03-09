package demo.scenes;

import core.Game;
import core.audio.SoundSystem;
import core.collision.CollisionEvent;
import core.collision.OnCollision;
import core.object.GameObject.GameAction;
import core.system.SystemManager;
import lombok.extern.slf4j.Slf4j;

/**
 * ObjectCollision Resolver is the implmentation for OnColission interface to
 * match the DemoScene needs.
 */
@Slf4j
public class ObjectCollisionResolver implements OnCollision {

    private SoundSystem soundSystem;
    private Game game;

    public ObjectCollisionResolver(Game g) {
        this.game = g;
        soundSystem = SystemManager.get(SoundSystem.class);
    }

    /**
     * Collision Listener
     *
     * @param e Collision Event to manage.
     */
    public void collide(CollisionEvent e) {
        switch (e.type) {
            case OBJECT:
                collectCoin(e);
                break;
            case ITEM:
                collectItem(e);
                break;
            case TILE:
                manageTileCollision(e);
                break;
            default:
                break;
        }
    }

    /**
     * A GameObject <code>go</code> collects a MapObject <code>mo</code> item
     *
     * @param e the CollisionEvent when the GameObject is moving
     */
    private void collectItem(CollisionEvent e) {
        if (e.o1.attributes.containsKey("maxItems") && e.m2.collectible && e.o1.canCollect) {
            double maxItems = (Double) e.o1.attributes.get("maxItems");
            if (e.o1.items.size() <= maxItems) {
                e.o1.items.add(e.m2);
                e.map.tiles[e.mapX][e.mapY] = null;
                soundSystem.play("item-1", (float) game.config.attributes.get("sound_volume"));
                log.debug("Collect {}:{} at {},{}", e.m2.type, e.m2.name, e.mapX, e.mapY);
            }
        }
    }

    /**
     * A GameObject <code>go</code> collect a MapObject <code>mo</code> as Coins.
     *
     * @param e the CollisionEvent when the GameObject is moving
     */
    private void collectCoin(CollisionEvent e) {

        if (e.m2.collectible && e.o1.canCollect && e.m2.money > 0) {
            double value = (double) (e.o1.attributes.get("coins"));
            e.o1.attributes.put("coins", (double) e.m2.money + value);
            e.map.tiles[e.mapX][e.mapY] = null;
            soundSystem.play("coins", (float) game.config.attributes.get("sound_volume"));
            log.debug("Collect {}:{} at {},{}", e.m2.type, e.m2.money, e.mapX, e.mapY);
        }
    }

    private void manageTileCollision(CollisionEvent e) {
        if (e.m2.block) {
            e.o1.setTileCollisionObject(e.m2);
            log.debug("player  pos:{},{} collide with {}:{}", e.mapX, e.mapY, e.m2.type, e.m2.name);
            if (Math.abs(e.o1.vel.x) > 0) {
                e.o1.vel.x = 0;
                e.o1.forces.clear();
            }
            if (Math.abs(e.o1.vel.y) > 0) {
                e.o1.vel.y = 0;
                e.o1.forces.clear();
                e.o1.pos.y = e.mapY * e.map.assetsObjects.get(0).tileHeight;
                e.o1.bbox.fromGameObject(e.o1);
                if (e.o1.name.equals("player")) {
                    log.info("player  pos:{},{}", e.mapX, e.mapY);
                }
                if (e.o1.action == GameAction.FALL) {
                    e.o1.action = GameAction.IDLE;
                }
            }

        }
    }

}