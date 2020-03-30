package core.scripts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptException;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import core.Game;
import core.object.GameObject;
import core.object.World;
import core.resource.ResourceManager;
import core.system.AbstractSystem;
import lombok.extern.slf4j.Slf4j;

/**
 * Lua Script manager and executor.
 *
 * @author Frédéric Delorme<frederic.delorme@gmail.com>
 * @since 2019
 */
@Slf4j
public class LuaScriptSystem extends AbstractSystem {

	private Map<String, String> scripts = new HashMap<>();
	private Globals globals;
	private PrintStream printStream;
	private ByteArrayOutputStream baos;
	private boolean scriptingOn = false;

	public LuaScriptSystem(Game g) {
		super(g);
		PrintStream printStream;
		ByteArrayOutputStream baos;
		globals = JsePlatform.standardGlobals();
		try {
			baos = new ByteArrayOutputStream();
			printStream = new PrintStream(baos, true, "utf-8");
			globals.STDOUT = printStream;
		} catch (UnsupportedEncodingException e) {
			log.error("unable to output luaj error message to console", e);
		}
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
		if (scriptingOn) {
			for (String path : paths) {
				try {
					load(path);
					LuaValue chunk = globals.load(scripts.get(path), path);
					chunk.call();
				} catch (IOException e) {
					e.printStackTrace();
				}
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
		log.debug("load script {}", path);
	}

	public Object execute(Game g, World world, String scriptName, Object o, Map<String, GameObject> map)
			throws ScriptException {
		if (scriptingOn) {
			log.debug("execute script {} on object {}", scriptName, ((GameObject) o).name);

			LuaValue gameLua = CoerceJavaToLua.coerce(g);
			LuaValue worldLua = CoerceJavaToLua.coerce(world);
			LuaValue objectLua = CoerceJavaToLua.coerce(o);
			if (map == null) {
				map = new HashMap<>();
			}
			LuaValue mapLua = CoerceJavaToLua.coerce(map);

			LuaValue initMtd = globals.get("init");
			initMtd.invoke(new LuaValue[] { gameLua, worldLua });
			LuaValue updateMtd = globals.get("update");
			updateMtd.invoke(new LuaValue[] { gameLua, worldLua, objectLua, mapLua });
			logLuaConsole(scriptName);
		}
		return o;
	}

	private void logLuaConsole(String scriptName) {
		if (baos != null) {
			String content = new String(baos.toByteArray(), StandardCharsets.UTF_8);
			printStream.flush();
			log.info("lua:{}:{}", scriptName, content);
		}
	}

	@Override
	public int initialize(Game game) {
		return 0;
	}

	@Override
	public void dispose() {
		scripts.clear();
	}
}
