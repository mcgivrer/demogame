package core.cli;

public class IntArgParser extends ArgParser<Integer>{

    public IntArgParser() {
        super();
    }

    public IntArgParser(
        String name, 
        String shortKey, 
        String longKey, 
        int defaultValue, 
        int min, 
        int max,
        String description, 
        String errorMessage) {
            super(name, shortKey, longKey, defaultValue, min, max, description, errorMessage);
    }

    @Override
    public boolean validate(String strValue) {
        value = defaultValue;
        try {
            value = parse(strValue);
            if ((min != null && value < min) || (max != null && value > max)) {
                errorMessage += String.format(
                        "value for %s must be between %s and %s. Value has been limited to min/max", name, min, max,
                        defaultValue);
                value = (value < min ? min : (value > max ? max : value));
            }
        } catch (Exception e) {
            value = defaultValue;
            errorMessage += String.format("value %s for argument %s is not possible.reset to default Value %s",
                    strValue, name, defaultValue);
            return false;
        }
        return true;
    }

    @Override
    public Integer parse(String strValue) {
        int value  = Integer.parseInt(strValue);
        return value;
    }

}