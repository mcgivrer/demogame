package core.gfx.opengl;

import java.util.HashMap;
import java.util.Map;

import core.Game;
import core.system.AbstractSystem;

public class TextureManager extends AbstractSystem {

    Map<String, Texture> textures = new HashMap<>();

    protected TextureManager(Game game) {
        super(game);
    }

    @Override
    public void dispose() {
        textures.clear();
        textures = null;

    }

    @Override
    public String getName() {
        return "texturemanager";
    }

}