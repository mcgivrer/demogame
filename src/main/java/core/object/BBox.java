package core.object;

public class BBox {
    public float x;
    public float y;
    public float width;
    public float height;

    public float top, bottom, left, right;

    public BBox(float x, float y, float w, float h) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
    }

    public void fromGameObject(GameObject g) {
        this.x = g.x+4;
        this.y = g.y;
        this.width = g.width-8;
        this.height = g.height;
        update();
    }

    public void update() {
        this.top = y + height;
        this.bottom = y;
        this.left = x+4;
        this.right = x + width -8;
    }

    public boolean intersect(BBox other) {
        return other != null
                && this.right >= other.left
                && this.left <= other.right
                && this.top >= other.bottom
                && this.bottom <= other.top;
    }
}