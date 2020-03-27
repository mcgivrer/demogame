package samples.input;

import java.awt.Graphics2D;

import samples.object.GameObject;
import samples.object.SampleGameObject;

public class MouseCursor extends GameObject{

    public MouseCursor(String name){
        super(name);
    }

    @Override
    public void draw(SampleGameObject ga, Graphics2D g) {
        g.setColor(color);
        g.drawLine((int)(x-(width/2)),(int)(y),(int)(x+(width/2)),(int)(y+height));
        g.drawLine((int)(x),(int)(y-(height/2)),(int)(x+width),(int)(y+(height/2)));
    }

    @Override
    public void update(SampleGameObject ga, double elapsed) {
        
    }
}