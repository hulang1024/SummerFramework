package summerframework.test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotSame;
import static junit.framework.TestCase.assertNull;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import summerframework.core.context.ApplicationContext;
import summerframework.core.context.ClassPathXmlApplicationContext;
import summerframework.test.cat.Body;
import summerframework.test.cat.Cat;
import summerframework.test.cat.Leg;


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
        assertEquals((int)cat.getAge(), 5);
        assertEquals(cat.getBody().getColor(), "yellow");
        assertEquals(cat.getBody().getLegs().size(), 4);
        
        String[] colors = new String[] {"red", "green", "blue", "white"};
        Iterator<Leg> legsIter = cat.getBody().getLegs().iterator();
        for (String c : colors)
            assertEquals(legsIter.next().getColor(), c);
        
        Body catBody = (Body)context.getBean("body");
        assertNull(catBody);
        catBody = (Body)context.getBean("catBody");
        assertEquals(catBody.getColor(), "yellow");
        
        assertNotSame(cat.getBody(), catBody);
    }
}
