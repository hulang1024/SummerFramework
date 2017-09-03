package summerframework.test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotSame;

import java.util.Iterator;

import org.junit.Test;

import beans.cat.Body;
import beans.cat.Cat;
import beans.cat.Head;
import beans.cat.Leg;


public class SimpleTest {
//    private org.springframework.context.ApplicationContext context
//        = new org.springframework.context.support.ClassPathXmlApplicationContext("summer-app.xml");
    
    private summerframework.context.ApplicationContext context
        = new summerframework.context.support.ClassPathXmlApplicationContext("summer-app.xml");
    
    @Test
    public void startup() {
        /* console output first:
        new beans.cat.Eye
        new beans.cat.Head
        new beans.cat.Body
        new beans.cat.Leg
        new beans.cat.Leg
        new beans.cat.Leg
        new beans.cat.Leg
        load beans.cat.Cat
        new beans.cat.Cat
        new beans.cat.Cat
         */
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
        
        assertNotSame(cat.getHead(), null);
        assertNotSame(cat.getHead().getEye().getColor(), "black");
        
        Body catBody;
        
        boolean ex = false;
        try {
            catBody = (Body)context.getBean("body");
        } catch (Exception e) {
            ex = true;
            e.printStackTrace();
        }
        assertEquals(true, ex);

        catBody = (Body)context.getBean("catBody");
        assertEquals(catBody.getColor(), "yellow");
    }

    @Test
    public void testDefaultSingletonScope() {
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
    }
    
    @Test
    public void testByTypeAutowire() {
        Head head = (Head)context.getBean("head");
        assertNotSame(head.getEye().getColor(), "black");
    }
}
