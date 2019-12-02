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
        this.x = g.x + left;
        this.y = g.y + top;
        this.width = g.width - (left + right);
        this.height = g.height - (top + bottom);
    }

    public boolean intersect(BBox other) {
        return other != null
                && this.x + this.right >= other.x + other.left
                && this.x + this.left <= other.x + other.right
                && this.y + this.top >= other.y + other.bottom
                && this.y + this.bottom <= other.y + other.top;
    }
}