package core.cli;

/**
 * String argument parser. extract and convert Streing argument.
 */
public class StringArgParser extends ArgParser<String>{

	/**
	 * Default constructor.
	 */
	public StringArgParser() {
		super();
	}

	/**
	 * 
	 * @param name
	 * @param shortKey
	 * @param longKey
	 * @param defaultValue
	 * @param description
	 * @param errorMessage
	 */
	public StringArgParser(String name, String shortKey, String longKey, String defaultValue, String description,
			String errorMessage) {
		super(name, shortKey, longKey, defaultValue, "", "", description, errorMessage);
	}

	/**
	 * Validate input data.
	 */
	@Override
	public boolean validate(String strValue) {
		value = defaultValue;
		try {
			value = parse(strValue);
			if ((value.equals(null) || value.equals(""))) {
				errorMessage += String.format("value for %s must not be empty or null. Value set to default one %s",
						name, defaultValue);
				value = defaultValue;
			}
		} catch (Exception e) {
			value = defaultValue;
			errorMessage += String.format("value %s for argument %s is not possible.reset to default Value %s",
					strValue, name, defaultValue);
			return false;
		}
		return true;
	}

	/**
	 * Convert argument value to attribute data.
	 */
	@Override
	public String parse(String strValue) {
		return strValue;
	}

}