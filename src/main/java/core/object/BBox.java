package core.object;

public class BBox {
	Vector2D pos;
	Vector2D size;

    public double top, bottom, left, right;

	public BBox(){
		this.pos = new Vector2D();
		this.size = new Vector2D();
	};

    public BBox(double x, double y, double w, double h) {
		this();
        this.pos.x = x;
        this.pos.y = y;
        this.size.x = w;
        this.size.y = h;
    }
    public BBox(GameObject go) {
		this();
        this.pos = go.pos;
  		this.size = go.size;
    }

    public void fromGameObject(GameObject g) {
        this.pos.x = g.pos.x + left;
        this.pos.y = g.pos.y + top;
        this.size.x = g.size.x - (left + right);
        this.size.y = g.size.y - (top + bottom);
    }

    public boolean intersect(BBox other) {
        return other != null
                && this.pos.x + this.right >= other.pos.x + other.left
                && this.pos.x + this.left <= other.pos.x + other.right
                && this.pos.y + this.top >= other.pos.y + other.bottom
                && this.pos.y + this.bottom <= other.pos.y + other.top;
    }
}
