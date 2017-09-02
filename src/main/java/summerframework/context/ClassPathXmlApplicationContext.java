package summerframework.context;

import java.io.InputStream;

import summerframework.beans.factory.XMLBeanFactory;

/**
 * 从类路径加载xml上下文
 * @author hulang
 */
public class ClassPathXmlApplicationContext extends ApplicationContext {
    public ClassPathXmlApplicationContext(String xmlPath) {
        InputStream in = getClass().getClassLoader().getResourceAsStream(xmlPath);
        factory = new XMLBeanFactory(in);
    }
}
