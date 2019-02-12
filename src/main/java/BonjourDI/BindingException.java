package BonjourDI;

public class BindingException extends Exception{
    private static final long serialVersionUID = 7718828512143293558L;

    public BindingException() {
        super();
    }

    public BindingException(String message, Throwable cause) {
        super(message, cause);
    }

    public BindingException(String message) {
        super(message);
    }

    public BindingException(Throwable cause) {
        super(cause);
    }
}
