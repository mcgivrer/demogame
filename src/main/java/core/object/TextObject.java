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

    public enum TextAlign {
        LEFT,
        CENTER,
        RIGHT
    }

    public TextAlign align = TextAlign.CENTER;

    // text to be displayed
    public String text;
    // font to be used to render text
    public Font font;
    // shadow color
    public Color shadowColor;
    // border color
    public Color borderColor;
    // text outlined color
    public Color outlinedColor;

    /**
     * Create a new TextObject with some default attributes values.
     *
     * @param name
     * @param x
     * @param y
     * @param foreground
     * @param border
     * @param shadow
     * @param font
     * @param fixed
     * @param layer
     * @param align
     */
    public TextObject(String name,
                      double x, double y,
                      Color foreground, Color border, Color shadow,
                      Font font,
                      boolean fixed,
                      int layer,
                      TextAlign align) {
        super(name, x, y, 0, 0);
        this.foregroundColor = foreground;
        this.borderColor = border;
        this.shadowColor = shadow;
        this.font = font;
        this.layer = layer;
        this.fixed = fixed;
        this.align = align;
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
    	super.update(dg, elapsed);
        if (attributes.containsKey("text")) {
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
    }

    public void setText(String text, Object... attributes) {
        this.text = String.format(text, attributes);
    }


    public void setText(String text) {
        this.text = text;
    }
}
