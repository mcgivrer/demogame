package samples.cli;

public abstract class ArgParser<T> implements IArgParser<T> {

    public String name;
    public String shortKey;
    public String longKey;
    public Class<?> type;
    public T value;
    public T defaultValue;
    public T min;
    public T max;
    public String description;
    public String errorMessage;
    public T[] listValues;

    public ArgParser() {

    }

    public ArgParser(String name, String shortKey, String longKey, T defaultValue, T min, T max, String description,
            String errorMessage) {
        this.name = name;
        this.shortKey = shortKey;
        this.longKey = longKey;
        this.defaultValue = defaultValue;
        this.min = min;
        this.max = max;
        this.description = description;
        this.errorMessage = errorMessage;
    }

    public abstract T parse(String strValue);

    public abstract boolean validate(String strValue);

    /**
     * @return the longKey
     */
    public String getLongKey() {
        return longKey;
    }

    @Override
    public T getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String getDescription() {
        return "["+shortKey +" / "+ longKey+ "] : " + description + " ( min:"+min+", max:"+max+", default:"+defaultValue+" )";
    }

    @Override
    public String getErrorMessage(Object[] args) {
        return String.format(errorMessage, args);
    }

    @Override
    public T[] getListOfValue() {
        return null;
    }

    @Override
    public T getMaxValue() {
        return max;
    }

    @Override
    public T getMinValue() {
        return min;
    }

    @Override
    public String getShortKey() {
        return shortKey;
    }

    @Override
    public String getName() {
        return name;
    }

    public T getValue(){
        return value;
    }
}