package samples.input;

import java.awt.Color;
import java.awt.Graphics2D;

import samples.Sample;
import samples.object.GameObject;

public class MouseCursor extends GameObject {

    public MouseCursor(String name) {
        super(name);
        color = Color.WHITE;
    }

    @Override
    public void draw(Sample ga, Graphics2D g) {
        g.setColor(color);
        g.drawLine((int) (x - (width / 2)), (int) (y), (int) (x + (width / 2)), (int) (y));
        g.drawLine((int) (x), (int) (y - (height / 2)), (int) (x), (int) (y + (height / 2)));
    }

    @Override
    public void update(Sample ga, double elapsed) {
        offsetX = ga.getActiveCamera().x;
        offsetY = ga.getActiveCamera().y;
    }
}