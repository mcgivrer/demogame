package samples.input;

import java.awt.Color;
import java.awt.Graphics2D;

import samples.Sample;
import samples.object.GameObject;

public class MouseCursor extends GameObject {

    public MouseCursor(String name) {
        super(name);
        color = Color.WHITE;
        width = 16;
        height = 16;
        type = GameObjectType.OTHER;
    }

    @Override
    public void draw(Sample ga, Graphics2D g) {
        g.setColor(Color.WHITE);
        g.drawLine((int) (x - (width / 2)), (int) (y), (int) (x + (width / 2)), (int) (y));
        g.drawLine((int) (x), (int) (y - (height / 2)), (int) (x), (int) (y + (height / 2)));
        g.setColor(Color.GRAY);
        g.drawLine((int) x, (int) y, (int) x, (int) y);
    }

    @Override
    public void update(Sample ga, double elapsed) {
        x += ga.getActiveCamera().x;
        x += ga.getActiveCamera().y;
    }
}