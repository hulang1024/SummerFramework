package summerframework.test;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.*;
import summerframework.core.context.ApplicationContext;
import summerframework.core.context.ClassPathXmlApplicationContext;


public class SimpleTest {
    private ApplicationContext context;
    
    @Before
    public void initContext() {
        context = new ClassPathXmlApplicationContext("summer-app.xml");
    }
    
    @Test
    public void test() {
        Cat cat = (Cat)context.getBean("cat");
        
        assertEquals(cat.getNickName(), "orange");
        assertEquals((int)cat.getLegs(), 4);
        assertEquals(cat.getBody().getColor(), "yellow");
        
        CatBody catBody = (CatBody)context.getBean("body");
        assertNull(catBody);
        catBody = (CatBody)context.getBean("catBody");
        assertEquals(catBody.getColor(), "yellow");
        
        assertNotSame(cat.getBody(), catBody);
    }
}
