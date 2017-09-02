package summerframework.core.context;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import summerframework.core.Bean;
import summerframework.core.property.Property;
import summerframework.core.property.RefProperty;
import summerframework.core.property.ValueProperty;

public class ClassPathXmlApplicationContext implements ApplicationContext {
    private Map<String, Bean> beanMap = new HashMap<String, Bean>();
    
    public ClassPathXmlApplicationContext(String xmlPath) {
        // parse
        SAXReader reader = new SAXReader();
        InputStream in = getClass().getClassLoader().getResourceAsStream(xmlPath);
        Document doc = null;
        try {
            doc = reader.read(in);
            if (doc == null)
                return;
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        Element root = doc.getRootElement();
                
        @SuppressWarnings("unchecked")
        List<Element> beanElems = root.elements();
        for (Element beanElem : beanElems) {
            Bean bean = new Bean();
            
            String beanName = beanElem.attributeValue("id");
            String className = beanElem.attributeValue("class");
            
            if (beanName == null)
                beanName = beanElem.attributeValue("name");
            if (beanName == null)
                beanName = className;
            
            bean.setName(beanName);
            bean.setClassName(className);
            
            List<Property> beanProperties = new ArrayList<Property>();
            
            @SuppressWarnings("unchecked")
            List<Element> propElems = beanElem.elements();
            for (Element propElem : propElems) {
                // TODO: property factory
                Property property = null;
                
                String propName = propElem.attributeValue("name");
                Attribute valueAttr = propElem.attribute("value");
                if (valueAttr != null) {
                    ValueProperty valueProp = new ValueProperty();
                    valueProp.setName(propName);
                    valueProp.setValue(valueAttr.getValue()); //TODO: more types
                    property = valueProp;
                } else {
                    Attribute refAttr = propElem.attribute("ref");
                    if (refAttr != null) {
                        RefProperty refProp = new RefProperty();
                        refProp.setName(propName);
                        refProp.setRef(refAttr.getValue());
                        property = refProp;
                    }
                }
                
                if (property != null) {
                    beanProperties.add(property);
                }
            }
            
            bean.setProperties(beanProperties);
            
            beanMap.put(bean.getName(), bean);
        }
        
    }
    
    public Object getBean(String name) {
        return createBeanObject(beanMap.get(name));
    }

    private Object createBeanObject(Bean bean) {
        if (bean == null)
            return null;
        String className = bean.getClassName();
        Class<?> clazz = null;
        Object object = null;
        try {
            clazz = Class.forName(className);
            if (clazz == null)
                return null;
            object = clazz.newInstance();

            List<Property> properties = bean.getProperties();
            for (Property property : properties) {
                Object valueForSet = null;
                if (property instanceof ValueProperty)
                    valueForSet = property.getValue();
                else if (property instanceof RefProperty)
                    valueForSet = getBean( (String)property.getValue() );
                BeanUtils.setProperty(object, property.getName(), valueForSet);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        
        return object;
    }
}
