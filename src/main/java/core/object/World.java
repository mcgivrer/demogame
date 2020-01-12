/**
 *
 */
package core.object;

import core.math.Vector2D;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * A World object containing anything from the world.
 *
 * @author Frédéric Delorme
 *
 */
@Data
public class World {

	private Map<String, Object> attributes = new HashMap<>();
	private Vector2D gravity;

	public World() {
		gravity = new Vector2D(0, -0.981f);
	}
}
