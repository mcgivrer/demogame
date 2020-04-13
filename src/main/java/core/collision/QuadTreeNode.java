package core.collision;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import core.Game;
import core.object.BBox;

/**
 * A lot of this code was stolen from this article: <br>
 * http://gamedevelopment.tutsplus.com/tutorials/quick-tip-use-quadtrees-to-detect-likely-collisions-in-2d-space--gamedev-374
 * <br>
 * <br>
 * created by chrislo27
 *
 */
public class QuadTreeNode {

    private int MAX_OBJECTS = 10;
    private int MAX_LEVELS = 12;

    private int level;
    private List<Collidable> objects;
    private float posX, posY, width, height;
    private QuadTreeNode[] nodes;
    private static int identifiedIndex;

    /**
     * ideal constructor for making a quadtree that's empty <br>
     * simply calls the normal constructor with <code>
     * this(0, 0, 0, width, height)
     * </code>
     * 
     * @param width  your game world width in units
     * @param height your game world height in units
     */
    public QuadTreeNode(float width, float height) {
        this(0, 0, 0, width, height);
    }

    /**
     * 
     * @param pLevel start at level 0 if you're creating an empty quadtree
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public QuadTreeNode(int pLevel, float x, float y, float width, float height) {
        level = pLevel;
        objects = new ArrayList<>();
        posX = x;
        posY = y;
        this.width = width;
        this.height = height;
        nodes = new QuadTreeNode[4];
    }

    public QuadTreeNode setMaxObjects(int o) {
        MAX_OBJECTS = o;
        return this;
    }

    public QuadTreeNode setMaxLevels(int l) {
        MAX_LEVELS = l;
        return this;
    }

    public int getChecks() {
        int num = 0;

        num += (objects.size() * objects.size());

        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] != null) {
                num += nodes[i].getChecks();
            }
        }

        return num;
    }

    /*
     * Clears the quadtree
     */
    public void clear() {
        objects.clear();

        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] != null) {
                nodes[i].clear();
                nodes[i] = null;
            }
        }
    }

    /*
     * Splits the node into 4 subnodes
     */
    private void split() {
        float subWidth = (width / 2);
        float subHeight = (height / 2);
        float x = posX;
        float y = posY;

        nodes[0] = new QuadTreeNode(level + 1, x + subWidth, y, subWidth, subHeight);
        nodes[1] = new QuadTreeNode(level + 1, x, y, subWidth, subHeight);
        nodes[2] = new QuadTreeNode(level + 1, x, y + subHeight, subWidth, subHeight);
        nodes[3] = new QuadTreeNode(level + 1, x + subWidth, y + subHeight, subWidth, subHeight);
    }

    /**
     * <p>
     * Determine which node the object belongs to.
     * </p>
     * <p>
     * -1 means object cannot completely fit within a child node and is part of the
     * parent node
     * </p>
     * 
     * @param col the Collidable object to find index for.
     */
    private int getIndex(Collidable col) {
        BBox bb = col.getBoundingBox();
        int index = -1;
        double verticalMidpoint = posX + (width / 2);
        double horizontalMidpoint = posY + (width / 2);

        // Object can completely fit within the top quadrants
        boolean topQuadrant = (bb.pos.x < horizontalMidpoint && bb.pos.y + bb.size.y < horizontalMidpoint);
        // Object can completely fit within the bottom quadrants
        boolean bottomQuadrant = (bb.pos.y > horizontalMidpoint);

        // Object can completely fit within the left quadrants
        if (bb.pos.x < verticalMidpoint && bb.pos.x + bb.size.x < verticalMidpoint) {
            if (topQuadrant) {
                index = 1;
            } else if (bottomQuadrant) {
                index = 2;
            }
        }
        // Object can completely fit within the right quadrants
        else if (bb.pos.x > verticalMidpoint) {
            if (topQuadrant) {
                index = 0;
            } else if (bottomQuadrant) {
                index = 3;
            }
        }
        identifiedIndex = index;

        return index;
    }

    /*
     * Insert the object into the quadtree. If the node exceeds the capacity, it
     * will split and add all objects to their corresponding nodes.
     */
    public void insert(Collidable col) {
        if (nodes[0] != null) {
            int index = getIndex(col);

            if (index != -1) {
                nodes[index].insert(col);

                return;
            }
        }

        objects.add(col);

        if (objects.size() > MAX_OBJECTS && level < MAX_LEVELS) {
            if (nodes[0] == null) {
                split();
            }

            int i = 0;
            while (i < objects.size()) {
                int index = getIndex(objects.get(i));
                if (index != -1) {
                    nodes[index].insert(objects.remove(i));
                } else {
                    i++;
                }
            }
        }
    }

    /*
     * Return all objects that could collide with the given object
     */
    public List<Collidable> retrieve(List<Collidable> returnObjects, Collidable pRect) {
        int index = getIndex(pRect);
        if (index != -1 && nodes[0] != null) {
            nodes[index].retrieve(returnObjects, pRect);
        }

        returnObjects.addAll(objects);

        return returnObjects;
    }

    /**
     * for debugging purpose, this will draw all the quadtree nodes.
     */
    public void draw(Game game, Graphics2D g) {

        for (int i = 0; i < nodes.length; i++) {
            QuadTreeNode n = nodes[i];
            if (n != null) {
                if (identifiedIndex != 0) {
                    g.setColor(Color.DARK_GRAY);
                } else {
                    g.setColor(Color.GRAY);
                }
                g.drawRect((int) n.posX, (int) n.posY, (int) n.width, (int) n.height);
                g.drawString("nb:" + objects.size(), n.posX + 2, n.posY + 10);
                n.draw(game, g);
            }
        }
    }

}
