package summerframework.core.context;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import summerframework.core.bean.Bean;
import summerframework.core.bean.Property;
import summerframework.core.bean.Property.ValueType;
import summerframework.core.exception.BeanDefineUnexpectedException;

/**
 * 从类路径加载xml上下文
 * @author hulang
 */
public class ClassPathXmlApplicationContext extends ApplicationContext {
    
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
            Bean bean = parseElement(beanElem);
            
            // 注册bean到上下文
            setBean(bean.getName(), bean);
        }
    }
    
    private Bean parseElement(Element elem) {
        return parseBeanElement(elem);
    }
    
    private Bean parseBeanElement(Element beanElem) {
        Bean bean = new Bean();
        
        // 解析bean 名称
        String beanName = beanElem.attributeValue("id");
        String className = beanElem.attributeValue("class");
        
        if (beanName == null)
            beanName = beanElem.attributeValue("name");
        if (beanName == null)
            beanName = className;
        
        bean.setName(beanName);
        // 设置类名
        bean.setClassName(className);
        
        // 解析bean属性
        List<Property> beanProperties = new ArrayList<Property>();
        
        @SuppressWarnings("unchecked")
        List<Element> propElems = beanElem.elements();
        for (Element propElem : propElems) {
            Property property = new Property();
            
            // 解析bean属性名称
            String propName = propElem.attributeValue("name");
            property.setName(propName);
            
            /// 解析bean属性值
            Attribute valueAttr = propElem.attribute("value");
            if (valueAttr != null) {
                property.setValueType(ValueType.SIMPLE);
                property.setValue(valueAttr.getValue());
            } else {
                Attribute refAttr = propElem.attribute("ref");
                if (refAttr != null) {
                    property.setValueType(ValueType.REF);
                    property.setValue(refAttr.getValue());
                } else {
                    List<Element> elems = propElem.elements();
                    if (elems.size() > 1)
                        throw new BeanDefineUnexpectedException();
                    Element valueElem = elems.get(0);
                    switch( valueElem.getName() ) {
                    case "value":
                        property.setValueType(ValueType.SIMPLE);
                        property.setValue( parseValueElement(valueElem) );
                        break;
                    case "bean":
                        property.setValueType(ValueType.BEAN);
                        property.setValue( parseBeanElement(valueElem) );
                        break;
                    case "list":
                        property.setValueType(ValueType.LIST);
                        property.setValue( parseListElement(valueElem) );
                        break;
                    }
                }
            }
            
            if (property != null) {
                beanProperties.add(property);
            }
        }
        
        bean.setProperties(beanProperties);
        
        return bean;
    }

    private Object parseValueElement(Element valueElem) {
        return valueElem.getData();
    }
   
    private Object parseListElement(Element listElem) {
        List<Object> list = new ArrayList<Object>();
        List<Element> elements = listElem.elements();
        for (Element elem : elements)
            list.add( parseElement(elem) );
        return list;
    }
}
