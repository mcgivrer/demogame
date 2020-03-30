package samples.cli;

public class IntArrayArgParser extends ArgParser<Integer[]>{

public IntArrayArgParser() {
        super();
    }

    public IntArrayArgParser(
        String name, 
        String shortKey, 
        String longKey, 
        Integer[] defaultValue, 
        Integer[] min, 
        Integer[] max,
        String description, 
        String errorMessage) {
            super(name, shortKey, longKey, defaultValue, min, max, description, errorMessage);
    }

    @Override
    public boolean validate(String strValue) {
        value = defaultValue;
        try {
            value = parse(strValue);
            for(int i=0;i<value.length;i++){
                if ((min[i] != null && value[i] < min[i]) || (max[i] != null && value[i] > max[i])) {
                    errorMessage += String.format(
                            "%s attribute %d for %s must be between %s and %s. Value has been limited to min/max", 
                            (errorMessage.equals("")?"":"\n"),
                            i, 
                            name, 
                            min[i], 
                            max[i],
                            defaultValue[i]);
                    value[i] = (value[i] < min[i] ? min[i] : (value[i] > max[i] ? max[i] : value[i]));
                }    
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
    public Integer[] parse(String strArgValue) {
        String[] strValues  = strArgValue.split("x");
        Integer[] values = new Integer[strValues.length];
        int i=0;
        for(String v:strValues){
            values[i++] = Integer.parseInt(v);
        }
        return values;
    }

}