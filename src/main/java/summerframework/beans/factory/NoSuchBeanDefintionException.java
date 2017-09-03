package summerframework.beans.factory;

import summerframework.beans.BeansException;

/**
 * @author hulang
 */
public class NoSuchBeanDefintionException extends BeansException {
    public NoSuchBeanDefintionException(String message) {
        super(message);
    }
}
