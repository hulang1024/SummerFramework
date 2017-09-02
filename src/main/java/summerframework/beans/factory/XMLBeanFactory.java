package summerframework.beans.factory;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import summerframework.beans.Bean;
import summerframework.beans.Bean.Scope;
import summerframework.beans.Property;
import summerframework.beans.Property.ValueType;
import summerframework.exception.BeanDefineUnexpectedException;

/**
 * 从XML文件装入bean的工厂
 * @author hulang
 */
public class XMLBeanFactory implements BeanFactory {
    private Map<String, Bean> beanMap = new HashMap<String, Bean>();
    private Map<String, Object> singletonCacheMap = new HashMap<String, Object>();
    
    public XMLBeanFactory() {}
    public XMLBeanFactory(InputStream input) {
        SAXReader reader = new SAXReader();
        Document doc = null;
        try {
            doc = reader.read(input);
            if (doc == null)
                return;
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        Element root = doc.getRootElement();
                
        @SuppressWarnings("unchecked")
        List<Element> beanElems = root.elements();
        for (Element beanElem : beanElems) {
            Bean bean = parseBeanElement(beanElem);
            
            // 注册bean到上下文
            setBean(bean.getName(), bean);
        }
    }
    
    @Override
    public Object getBean(String name) {
        // 判断如果存在于单例缓存中，返回该单例
        Object object = singletonCacheMap.get(name);
        if (object != null)
            return object;
        
        Bean bean = beanMap.get(name);
        if (bean == null)
            return null;
        
        // 创建bean描述的对象
        object = createBean(bean);
        // 判断bean描述单例作用域，就放到单例缓存中
        if (bean.isSingleton())
            singletonCacheMap.put(name, object);
        
        return object;
    }

    private void setBean(String name, Bean bean) {
        beanMap.put(name, bean);
    }
    
    private Object createBean(Object object) {
        if (object == null)
            return null;
        if (!(object instanceof Bean))
            return object;
        
        Bean bean = (Bean)object;
        String className = bean.getClassName();
        Class<?> clazz = null;

        try {
            clazz = Class.forName(className);
            if (clazz == null)
                return null;
            object = clazz.newInstance();

            // 求值bean依赖属性
            List<Property> properties = bean.getProperties();
            for (Property property : properties) {
                Object valueForSet = null;
                switch (property.getValueType()) {
                case SIMPLE:
                    valueForSet = property.getValue();
                    break;
                case REF:
                    valueForSet = getBean( (String)property.getValue() );
                    break;
                case BEAN:
                    valueForSet = createBean( property.getValue() );
                    break;
                case LIST:
                    List<Object> valueList = new ArrayList<Object>();
                    List<Object> exprList = (List)property.getValue();
                    for (Iterator iter = exprList.iterator(); iter.hasNext(); ) {
                        valueList.add( createBean( iter.next() ) );
                    }
                    valueForSet = valueList;
                    break;
                case SET:
                case MAP:
                    valueForSet = property.getValue();
                    break;
                default: ;
                }
                // 反射设置属性值
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
    
    private Bean parseBeanElement(Element beanElem) {
        Bean bean = new Bean();
        
        // 解析bean元素名称
        String beanName = beanElem.attributeValue("id");
        String className = beanElem.attributeValue("class");
        
        if (beanName == null)
            beanName = beanElem.attributeValue("name");
        if (beanName == null)
            beanName = className;
        
        bean.setName(beanName);
        // 设置bean元素类名属性
        bean.setClassName(className);
        
        // 解析bean元素scope属性
        String beanScope = beanElem.attributeValue("scope");
        if ("singleton".equalsIgnoreCase(beanScope))
            bean.setScope(Scope.SINGLETON);
        else if ("prototype".equalsIgnoreCase(beanScope))
            bean.setScope(Scope.PROTOTYPE);
        
        // 解析bean元素scope属性
        String singleton = beanElem.attributeValue("singleton");
        if ("false".equals(singleton))
            bean.setSingleton(false);
        
        // 解析bean依赖属性
        List<Property> beanProperties = new ArrayList<Property>();
        
        @SuppressWarnings("unchecked")
        List<Element> propElems = beanElem.elements();
        for (Element propElem : propElems) {
            Property property = new Property();
            
            // 解析bean依赖属性名称
            String propName = propElem.attributeValue("name");
            property.setName(propName);
            
            /// 解析bean依赖属性值
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
            list.add( parseBeanElement(elem) );
        return list;
    }

}
