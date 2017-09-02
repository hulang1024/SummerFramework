package summerframework.context;

import summerframework.beans.factory.BeanFactory;

/**
 * 应用上下文
 * @author hulang
 */
public abstract class ApplicationContext implements BeanFactory {
    protected BeanFactory factory;

    @Override
    public Object getBean(String name) {
        return factory.getBean(name);
    }
}
