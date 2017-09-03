package summerframework.context.support;

import java.io.InputStream;

import summerframework.beans.factory.BeanFactory;
import summerframework.beans.factory.XMLBeanFactory;
import summerframework.context.ApplicationContext;

/**
 * 从类路径加载xml上下文
 * @author hulang
 */
public class ClassPathXmlApplicationContext implements ApplicationContext {
    private BeanFactory factory;
    
    public ClassPathXmlApplicationContext(String xmlPath) {
        InputStream in = getClass().getClassLoader().getResourceAsStream(xmlPath);
        factory = new XMLBeanFactory(in);
    }
    
    @Override
    public Object getBean(String name) {
        return factory.getBean(name);
    }
}
