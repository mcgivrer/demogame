package core.object;

public class BBox {
    public float x;
    public float y;
    public float width;
    public float height;

    public BBox(float x, float y, float w, float h) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
    }

    public void fromGameObject(GameObject g) {
        this.x = g.x;
        this.y = g.y;
        this.width = g.width;
        this.height = g.height;
    }
}
