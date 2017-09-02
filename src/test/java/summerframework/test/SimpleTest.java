package summerframework.test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotSame;
import static junit.framework.TestCase.assertNull;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import summerframework.context.ApplicationContext;
import summerframework.context.ClassPathXmlApplicationContext;
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
    public void testPropertyDI() {
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

    }
    
    @Test
    public void testDefaultScope() {
        Cat cat = (Cat)context.getBean("cat");
        Body catBody = (Body)context.getBean("catBody");
        assertEquals(cat.getBody(), catBody);
        
        Cat myWhiteCat1 = (Cat)context.getBean("myWhiteCat");
        Cat myWhiteCat2 = (Cat)context.getBean("myWhiteCat");
        
        assertEquals(myWhiteCat1, myWhiteCat2);
    }
    
    @Test
    public void testPrototypeScope() {
        Cat cat1 = (Cat)context.getBean("myBlueCat");
        Cat cat2 = (Cat)context.getBean("myBlueCat");
        assertNotSame(cat1, cat2);
        
        Cat cat3 = (Cat)context.getBean("myBlackCat");
        Cat cat4 = (Cat)context.getBean("myBlackCat");
        assertNotSame(cat3, cat4);
    }
}
