package core.object;

public class BBox {
    public double x;
    public double y;
    public double width;
    public double height;

    public double top, bottom, left, right;

    public BBox(double x, double y, double w, double h) {
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