package core.object;

import core.Game;

import java.awt.*;
import java.awt.font.FontRenderContext;

public class TextObject extends GameObject {

    public String text;
    public Font font;
    public Color shadowColor;
    public Color borderColor;


    public TextObject() {
        super();
    }

    @Override
    public void update(Game dg, float elapsed) {
        text = (String) attributes.get("text");

    }

    @Override
    public void render(Game dg, Graphics2D g) {
        if (font != null && text != null) {
            g.setFont(font);
            FontMetrics fm = g.getFontMetrics(font);
            width = fm.stringWidth(text);
            height = fm.getHeight();

            if(shadowColor!=null){
                g.setColor(shadowColor);
                g.drawString(text,x+1,y+1);
                g.drawString(text,x+2,y+2);
            }
            if(borderColor!=null){
                g.setColor(borderColor);
                g.drawString(text,x+1,y);
                g.drawString(text,x,y+1);
                g.drawString(text,x-1,y);
                g.drawString(text,x,y-1);
            }
            g.setColor(foregroundColor);
            g.drawString(text,x,y);


        }
    }
}
