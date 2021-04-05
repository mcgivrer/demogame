package demo.collision;

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
        soundSystem = SystemManager.get(SoundSystem.class.getSimpleName());
    }

    /**
     * Collision Listener
     *
     * @param e Collision Event to manage.
     */
    public void collide(CollisionEvent e) {
        switch (e.tileType) {
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
        if (e.a.attributes.containsKey("maxItems") && e.m2.collectible && e.a.canCollect) {
            double maxItems = (Double) e.a.attributes.get("maxItems");
            if (e.a.items.size() <= maxItems) {
                e.a.items.add(e.m2);
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

        if (e.m2.collectible && e.a.canCollect && e.m2.money > 0) {
            double value = (double) (e.a.attributes.get("coins"));
            e.a.attributes.put("coins", (double) e.m2.money + value);
            e.map.tiles[e.mapX][e.mapY] = null;
            soundSystem.play("coins", (float) game.config.attributes.get("sound_volume"));
            log.debug("Collect {}:{} at {},{}", e.m2.type, e.m2.money, e.mapX, e.mapY);
        }
    }

    private void manageTileCollision(CollisionEvent e) {
        if (e.m2.block) {
            e.a.setTileCollisionObject(e.m2);
            log.debug("player  pos:{},{} collide with {}:{}", e.mapX, e.mapY, e.m2.type, e.m2.name);
            if (Math.abs(e.a.vel.x) > 0) {
                e.a.vel.x = 0;
                e.a.forces.clear();
            }
            if (Math.abs(e.a.vel.y) > 0) {
                e.a.vel.y = 0;
                e.a.forces.clear();
                e.a.pos.y = e.mapY * e.map.assetsObjects.get(0).tileHeight;
                e.a.bbox.fromGameObject(e.a);
                if (e.a.name.equals("player")) {
                    log.info("player  pos:{},{}", e.mapX, e.mapY);
                }
                if (e.a.action == GameAction.FALL) {
                    e.a.action = GameAction.IDLE;
                }
            }

        }
    }

}