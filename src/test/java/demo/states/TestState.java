package demo.states;

import core.Game;
import core.gfx.Renderer;
import core.state.AbstractState;
import core.state.State;

import java.awt.*;

public class TestState extends AbstractState implements State {

    public TestState(Game g) {
        super(g);
    }

    @Override
    public void input(Game g) {
    }

    @Override
    public void initialize(Game g) {
    }

    @Override
    public void load(Game g) {
    }

    @Override
    public boolean isLoaded() {
        return false;
    }

    @Override
    public void update(Game g, float elapsed) {
    }

    @Override
    public void render(Game g, Renderer r, double elapsed) {

    }

    @Override
    public void dispose(Game g) {

    }

    public void drawHUD(Game ga, Renderer r, Graphics2D g) {
    }
}
