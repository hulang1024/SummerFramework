package summerframework.beans;

/**
 * @author hulang
 */
public class BeansException extends RuntimeException {
    public BeansException(String message) {
        super(message);
    }
    
    public BeansException(Throwable cause) {
        super(cause);
    }
}
