package core.cli;

/**
 * <p>
 * The {@link IArgParser} interface define the contract for {@link CliManager}
 * to parse Command line argument of type <code>T</code>.
 * <p>
 * An argument parser must be defined by soime parameters:
 * <ul>
 * <li><code>shortKey</code> a short option name to define the argument,</li>
 * <li><code>longKey</code> a long option name to define the argument,</li>
 * <li><code>name</code> the real name of the argument,</li>
 * <li><code>description</code> the description of the argument,</li>
 * <li><code>defaultValue</code> a default value for the option for the
 * argument,</li>
 * <li><code>min</code> a minimum value for the option for the argument,</li>
 * <li><code>max</code> a maximum value for the option for the argument,</li>
 * <li><code>listOfValues</code> a list of possible values for the
 * argument,</li>
 * </ul>
 * 
 * <p>
 * Some default processing are define in the {@link ArgParser} class.
 * 
 * @param <T>
 * 
 * @author Frédéric Delorme
 * 
 * @see ArgParser
 * 
 */
public interface IArgParser<T> {

    /**
     * Validate the input value accroding to the min/max or list values.
     * 
     * @param strValue the input argument value
     * @return true if value is compatible with argument rules, or false.
     */
    public boolean validate(String strValue);

    /**
     * Return the parsed and formated/transformed value of the argument.
     * 
     * @return
     */
    public T getValue();

    /**
     * Define the short key name for this argument.
     * 
     * @return the short key for this argument
     */
    public String getShortKey();

    /**
     * Retrieve the long name for this argument.
     * 
     * @return the long key for this argument.
     */
    public String getLongKey();

    /**
     * Define the full and complent/real name of the argument.
     * 
     * @return the name of this argument.
     */
    public String getName();

    /**
     * retrieve the defintion of this argument
     * 
     * @return the definition of this argument.
     */
    public String getDescription();

    /**
     * return the analyzed and formated message for this arguments.
     * 
     * @param args the list of arguments from definition to set default, min and max
     *             values.
     * @return
     */
    public String getErrorMessage(Object[] args);

    /**
     * return the default value of the argument.
     * 
     * @return the default value.
     */
    public T getDefaultValue();

    /**
     * Return the minimum value (of needed/possible) for this argument.
     * 
     * @return a T value corresponding to the min value for this argument.
     */
    public T getMinValue();

    /**
     * Return the maximum value (of needed/possible) for this argument.
     * 
     * @return a T value corresponding to the max value for this argument.
     */
    public T getMaxValue();

    /**
     * If the type of argument is a list, return the list of possible values.
     * 
     * @return a list of T values defining the possible values of the argument.
     */
    public T[] getListOfValue();
}