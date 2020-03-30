package samples.cli;


public class ArgumentUnknownException extends Exception{

    public ArgumentUnknownException(String string, String key) {
        super(String.format(string,key));
	}

	private static final long serialVersionUID = 1L;

}