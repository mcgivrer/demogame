package core.cli;

import java.util.HashMap;
import java.util.Map;

import core.Game;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * The `CliManager` is a Command Line argument parser.
 * <p>
 * Receiving an array of String, all those strings will parsed according to a
 * set of [Typed]ArgParser to be converted to the right object, and initialized
 * the right parent game Attribute.
 * 
 * @author Frédéric Delorme
 * @see IArgParser
 * @see ArgParser
 *
 */
@Slf4j
public class CliManager {

	/**
	 * the parent Game.
	 */
	@SuppressWarnings("unused")
	private Game game;
	/**
	 * the map of arguments to be parsed
	 */
	private Map<String, IArgParser<?>> argParsers = new HashMap<>();
	/**
	 * the detected values after parsing.
	 */
	private Map<String, Object> values = new HashMap<>();

	/**
	 * Initialize the CliManager object.
	 * 
	 * @param g
	 */
	public CliManager(Game g) {
		this.game = g;
	}

	/**
	 * Add a new Argument parser the to CliManager arguments list.
	 * 
	 * @param ap the argument parser to be parsed.
	 */
	public void add(IArgParser<?> ap) {
		argParsers.put(ap.getName(), ap);
		log.debug("add cli parser for " + ap.getDescription());
	}

	/**
	 * Start parsing all the values from the args coming directly from the Java
	 * command line.
	 * 
	 * @param args list of arguments to be parsed.
	 */
	public void parse(String[] args) {
		for (String arg : args) {
			if (arg.equals("h") || arg.equals("help")) {
				System.out.println("Command Usage:\n---------------");
				for (IArgParser<?> ap : argParsers.values()) {
					log.debug("- " + ap.getDescription());
				}
			} else {
				String[] itemValue = arg.split("=");
				for (IArgParser<?> ap : argParsers.values()) {
					if (ap.getShortKey().equals(itemValue[0]) || ap.getLongKey().equals(itemValue[0])) {
						if (ap.validate(itemValue[1])) {
							values.put(ap.getName(), ap.getValue());
						} else {
							log.error(ap.getErrorMessage(null));
						}
					}
				}
			}
		}
	}

	/**
	 * retrieve a value of a specific attribute by its key name.
	 * 
	 * @param key
	 * @return the value of this argument (if exists)
	 * @throws ArgumentUnknownException
	 */
	public Object getValue(String key) throws ArgumentUnknownException {
		if (values.containsKey(key)) {
			return values.get(key);
		} else {
			return argParsers.get(key).getDefaultValue();
		}
	}

	/**
	 * Return if the expected attributed has been initialized.
	 * @param key
	 * @return
	 */
	public boolean isExists(String key) {
		return values.containsKey(key);
	}

}