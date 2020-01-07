package core.scripts;

import core.Game;
import core.object.GameObject;
import core.object.ObjectManager;
import core.resource.ResourceManager;
import core.system.AbstractSystem;
import core.system.System;
import lombok.extern.slf4j.Slf4j;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Lua Script manager and executor.
 *
 * @author Frédéric Delorme<frederic.delorme@gmail.com>
 * @since 2019
 */
@Slf4j
public class LuaScriptSystem extends AbstractSystem implements System {

    private ScriptEngineManager sem = new ScriptEngineManager();
    private ScriptEngine se;
    private Map<String, String> scripts = new HashMap<>();

    public LuaScriptSystem(Game g) {
        super(g);
    }

    @Override
    public String getName() {
        return LuaScriptSystem.class.getCanonicalName();
    }

    /**
     * Loas all scripts listed in the String array.
     *
     * @param paths list of path to Scripts in the "/res/scripts" path.
     */
    public void loadAll(String[] paths) {
        for (String path : paths) {
            try {
                load(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Load a script path.
     *
     * @param path
     * @throws IOException
     */
    private void load(String path) throws IOException {
        String f = ResourceManager.getString(path);
        scripts.put(path, f);
        log.debug("load script {}",path);
    }

    public Object execute(String scriptName, Object o, Map<String, Object> map) throws ScriptException {
        if(map!=null){
            se.createBindings().putAll(map);
        }
        se.getBindings(ScriptContext.ENGINE_SCOPE).put("o", o);
        log.debug("execute script {} on object {}",scriptName,((GameObject)o).name);
        o = se.eval(scripts.get(scriptName));
        return o;
    }

    @Override
    public int initialize(Game game) {
        ObjectManager om=game.sysMan.getSystem(ObjectManager.class);
        se = sem.getEngineByExtension("lua");
        se.getContext().setAttribute("game", game, ScriptContext.GLOBAL_SCOPE);
        se.getContext().setAttribute("objects", om, ScriptContext.GLOBAL_SCOPE);
        return 0;
    }

    @Override
    public void dispose() {
        scripts.clear();
        se = null;
        sem = null;
    }
}
