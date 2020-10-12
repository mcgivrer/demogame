package samples.collision;

import core.math.Vector2D;
import samples.object.GameObject;

public class BoundingBox {
	public double x;
	public double y;
	public double w;
	public double h;
	public Vector2D size;

	public double top, bottom, left, right;

	public BoundingBox() {
		this.x = 0;
		this.y = 0;
		this.w = 0;
		this.h = 0;
	};

	public BoundingBox(double x, double y, double w, double h) {
		this();
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	public BoundingBox(GameObject go) {
		this();
		this.x = go.x;
		this.y = go.y;
		this.w = go.width;
		this.h = go.height;
	}

	public void fromGameObject(GameObject g) {
		this.x = g.x + left;
		this.y = g.y + top;
		this.w = g.height - (left + right);
		this.w = g.width - (top + bottom);
	}

	public boolean intersect(BoundingBox other) {
		return other != null && this.x + this.right >= other.x + other.left
				&& this.x + this.left <= other.x + other.right && this.y + this.top >= other.y + other.bottom
				&& this.y + this.bottom <= other.y + other.top;
	}

	public void update(GameObject go) {
		this.x = go.x;
		this.y = go.y;
		this.w = go.width;
		this.h = go.height;
	}
}