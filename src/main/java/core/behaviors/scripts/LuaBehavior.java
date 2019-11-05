package core.behaviors.scripts;

import core.Game;
import core.map.MapLevel;
import core.object.GameObject;

public class LuaBehavior implements Behavior {

    public String scriptsPath;

    public LuaBehavior(String scriptPath) {
        this.scriptsPath = scriptPath;
    }

    @Override
    public void update(Game dg, MapLevel map, GameObject go, float elapsed) {
        //script = LuaScriptEngine.read(scriptPath);
        //script.eval();
    }
}
