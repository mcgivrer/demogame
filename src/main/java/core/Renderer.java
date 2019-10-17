package core;

import core.map.MapLevel;
import core.map.MapRenderer;
import core.object.Camera;
import core.object.GameObject;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This core.Renderer class is the main rendering component for all objects managed by its parent core.Game instance.
 *
 * @author Frédéric Delorme<frederic.delorme@gmail.com>
 * @year 2019
 */
public class Renderer {

    private JFrame jf;
    private List<GameObject> renderingObjectPipeline = new ArrayList<>();
    private MapRenderer mapRenderer = new MapRenderer();

    public BufferedImage screenBuffer;

    /**
     * Create the Game renderer.
     *
     * @param dg the core.Game instance parent for this core.Renderer.
     */
    public Renderer(Game dg) {
        jf = createWindow(dg);
        screenBuffer = new BufferedImage(dg.config.screenWidth, dg.config.screenHeight, BufferedImage.TYPE_INT_ARGB);
    }

    /**
     * create a WXindow to host the game display according to core.Config object.
     *
     * @param dg the core.Game object to access the configuration instance.
     * @return a JFrame initialized conforming to config attributes.
     */
    public JFrame createWindow(Game dg) {
        jf = new JFrame(dg.config.title);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setBackground(Color.BLACK);
        Insets ins = jf.getInsets();
        Dimension dim = new Dimension((int) (dg.config.screenWidth * dg.config.screenScale) - (ins.left + ins.right),
                (int) (dg.config.screenHeight * dg.config.screenScale) - (ins.top + ins.bottom));
        jf.setSize(dim);
        jf.setPreferredSize(dim);
        jf.pack();

        jf.addKeyListener(dg);

        jf.setLocationByPlatform(true);
        jf.setLocationRelativeTo(null);
        jf.setVisible(true);
        return jf;
    }

    /**
     * Render all objects !
     */
    public void render(Game dg) {
        Graphics2D g = screenBuffer.createGraphics();

        // activate Antialiasing for image and text rendering.

        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

        // clear image
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, dg.config.screenWidth, dg.config.screenHeight);

        // if a camera is set, use it.
        if (dg.camera != null) {
            g.translate(-dg.camera.x, -dg.camera.y);
        }

        // draw all objects
        for (GameObject go : renderingObjectPipeline) {
            if (go.enable) {
                if (go instanceof MapLevel) {

                    if (dg.config.debug > 2) {
                        g.setColor(Color.BLUE);
                        g.fillRect(0, 0, (int) go.width, (int) go.height);
                    }
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                    mapRenderer.render(dg, g, (MapLevel) go, dg.camera);
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                } else if (go instanceof GameObject) {
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    go.render(dg, g);

                }
            }
        }

        // if a camera is set, use it.
        if (dg.camera != null) {
            g.translate(dg.camera.x, dg.camera.y);
        }

        // draw score
        dg.drawHUD(this, g);
        g.dispose();

        // render image to real screen (applying scale factor)
        renderToScreen(dg);
    }


    public void renderToScreen(Game dg) {
        if (jf != null) {
            Graphics2D g = (Graphics2D) jf.getGraphics();
            float sX = jf.getWidth() / dg.config.screenWidth;
            float sY = jf.getHeight() / dg.config.screenHeight;

            if (g != null) {
                g.drawImage(screenBuffer, 0, 0, jf.getWidth(), jf.getHeight(), 0, 0, dg.config.screenWidth, dg.config.screenHeight,
                        Color.BLACK, null);
                if (dg.config.debug > 0) {
                    g.setColor(Color.ORANGE);
                    g.drawString(
                            String.format("debug:%d | cam:(%03.1f,%03.1f)",
                                    dg.config.debug,
                                    dg.camera.x, dg.camera.y),
                            4,
                            jf.getHeight() - 20);

                    for (GameObject go : renderingObjectPipeline) {
                        displayDebugInfo(dg, g, go, dg.camera, sX, sY);
                    }
                    if (dg.config.debug > 2) {
                        g.setColor(Color.ORANGE);
                        g.drawString("cam:" + dg.camera.name, (int) (20 + sX), (int) (20 * sY));
                        g.drawRect((int) ((10) * sX), (int) ((10) * sY), (int) ((dg.config.screenWidth - 20) * sX), (int) ((dg.config.screenHeight - 20) * sY));
                    }
                }
                g.dispose();
            }
        }
    }

    public void displayDebugInfo(Game dg, Graphics2D g, GameObject go, Camera cam, float sX, float sY) {
        Font debugFont = g.getFont().deriveFont(5.0f);
        if (dg.config.debug > 1) {
            float offsetX = go.x + go.width + 2 - cam.x;
            float offsetY = go.y - cam.y;

            g.setColor(Color.LIGHT_GRAY);
            g.drawString(
                    String.format("name:%s", go.name),
                    (offsetX * sX), offsetY * sY);
            g.drawString(
                    String.format("pos:(%03.1f,%03.1f)",
                            go.x, go.y),
                    (offsetX * sX), (offsetY + 10) * sY);
            g.drawString(
                    String.format("vel:(%03.1f,%03.1f)",
                            go.dx, go.dy),
                    (offsetX * sX), (offsetY + 20) * sY);
            g.drawString(
                    String.format("debug:%d",
                            dg.config.debug),
                    (offsetX) * sX, (offsetY + 30) * sY);
        }
    }


    /**
     * draw an outline text at (x,y) with textColor and a borderColor.
     *
     * @param g
     * @param text
     * @param x
     * @param y
     * @param textColor
     * @param borderColor
     */
    public void drawOutLinedText(Graphics2D g, String text, int x, int y, Color textColor, Color borderColor) {
        g.setColor(borderColor);
        g.drawString(text, x - 1, y);
        g.drawString(text, x, y - 1);
        g.drawString(text, x + 1, y);
        g.drawString(text, x, y + 1);

        g.setColor(textColor);
        g.drawString(text, x, y);
    }


    public void add(GameObject go) {
        if (!renderingObjectPipeline.contains(go)) {
            renderingObjectPipeline.add(go);
            Collections.sort(renderingObjectPipeline, new Comparator<GameObject>() {
                public int compare(GameObject g1, GameObject g2) {
                    return (g1.priority < g2.priority ? (g1.layer < g2.layer ? 1 : -1) : -1);
                }
            });
        } else {
            System.out.println(String.format("Error : core.object.GameObject %s already exists in rendering pipeline.", go.name));
        }

    }

    public void remove(GameObject go) {
        renderingObjectPipeline.remove(go);
    }

    public void removeAll(List<GameObject> toBeRemoved) {
        renderingObjectPipeline.removeAll(toBeRemoved);
    }
}
