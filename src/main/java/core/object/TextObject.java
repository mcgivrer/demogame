package core.object;

import core.Game;

import java.awt.*;

/**
 * The TextObject is a new GameObject to display only Text.
 * This GO is managed as other GameObject but can be Fixed (or sticked) to camera view.
 *
 * @author Frédéric Delorme <frederic.delorme@gmail.com>
 * @since 2019
 */
public class TextObject extends GameObject {

    // text to be displayed
    public String text;
    // font to be used to render text
    public Font font;
    // shadow color
    public Color shadowColor;
    // border color
    public Color borderColor;

    public TextObject(String name, double x, double y,
                      Color foreground, Color border, Color shadow,
                      Font font, boolean fixed, int layer) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.foregroundColor = foreground;
        this.borderColor = border;
        this.shadowColor = shadow;
        this.font = font;
        this.layer = layer;
        this.fixed = fixed;

    }


    /**
     * recompute text to be displayed on each frame.
     * <p>
     * TODO this would be change to recompute only when needed
     *
     * @param dg      the core.Game containing the object.
     * @param elapsed the elapsed time since previous call.
     */
    @Override
    public void update(Game dg, double elapsed) {
        if(attributes.containsKey("text")){
            this.text = attributes.get("text").toString();
        }
    }

    /**
     * Specific rendering for the text of TextObject.
     *
     * @param dg the core.Game containing the object.
     * @param g  the graphics API.
     */
    @Override
    public void render(Game dg, Graphics2D g) {
        if (font != null && text != null) {
            Font b = g.getFont();
            g.setFont(font);
            FontMetrics fm = g.getFontMetrics(font);
            width = fm.stringWidth(text);
            height = fm.getHeight();

            if (shadowColor != null) {
                g.setColor(shadowColor);
                g.drawString(text, (int) x + 1, (int) y + 1);
                g.drawString(text, (int) x + 2, (int) y + 2);
            }
            if (borderColor != null) {
                g.setColor(borderColor);
                g.drawString(text, (int) x + 1, (int) y);
                g.drawString(text, (int) x, (int) y + 1);
                g.drawString(text, (int) x - 1, (int) y);
                g.drawString(text, (int) x, (int) y - 1);
            }
            g.setColor(foregroundColor);
            g.drawString(text, (int) x, (int) y);
            g.setFont(b);
        }
    }

    public void setText(String text, Object... attributes){
        this.text = String.format(text,attributes);
    }


    public void setText(String text){
        this.text = text;
    }
}
