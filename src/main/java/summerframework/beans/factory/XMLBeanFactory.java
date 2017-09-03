package summerframework.beans.factory;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import summerframework.beans.Bean;
import summerframework.beans.Bean.Autowire;
import summerframework.beans.Bean.Scope;
import summerframework.beans.BeansException;
import summerframework.beans.Property;
import summerframework.beans.Property.ValueType;

/**
 * 从XML文件装入bean的工厂
 * @author hulang
 */
public class XMLBeanFactory implements BeanFactory {
    /**
     *  按定义顺序加入
     */
    private List<Bean> beanDefinitionList = new ArrayList<Bean>();
    
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

            addBean(bean);
        }
        
        instancetiateSingletons();
    }
    
    private void instancetiateSingletons() {
        Collection<Bean> allDefinedBeans = beanDefinitionList;
        for (Bean bean : allDefinedBeans) {
            if (bean.isSingleton())
                createBean(bean);
        }
    }
    
    @Override
    public Object getBean(String name) {
        Bean bean = findByName(name);
        if (bean == null)
            throw new NoSuchBeanDefintionException("No bean named '" + name + "' available");
        
        if (bean.isSingleton())
            return bean.getObject();
        
        return createBean(bean);
    }

    private void addBean(Bean bean) {
        beanDefinitionList.add(bean);
    }
    
    private Bean findByName(String name) {
        for (Bean bean : beanDefinitionList)
            if (bean.getName().equals(name))
                return bean;
        return null;
    }
    
    private Object createBean(Object object) {
        if (object == null)
            return null;
        if (!(object instanceof Bean))
            return object;
        
        Bean bean = (Bean)object;
        
        // 加载bean定义的类
        Class<?> clazz;
        if (bean.getClazz() == null) {
            try {
                clazz = Class.forName(bean.getClassName());
            } catch (ClassNotFoundException e) {
                throw new BeansException(e);
            }
            if (clazz == null)
                throw new BeansException("");
            bean.setClazz(clazz);
        } else {
            clazz = bean.getClazz();
        }
        
        try {
            object = clazz.newInstance();
            bean.setObject(object);

            doBeanAutowire(bean);
            setBeanProperty(bean);
        }catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        
        return object;
    }
    
    private void setBeanProperty(Bean bean) {
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
            
            try {
                BeanUtils.setProperty(bean.getObject(), property.getName(), valueForSet);
            } catch (IllegalAccessException e) {
                throw new BeansException(e);
            } catch (InvocationTargetException e) {
                throw new BeansException(e);
            }
        }
    }
    
    private void doBeanAutowire(Bean bean) {
        switch (bean.getAutowire()) {
        case BY_NAME:
            autowiredByName(bean);
            break;
        case BY_TYPE:
            autowiredByType(bean);
            break;
        case CONSTRUCTOR:
            autowiredByConstructor(bean);
            break;
        case NO:
            break;
        default:
            ;
        }
    }
    
    private void autowiredByName(Bean bean) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClazz());
            PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
            
            for (PropertyDescriptor desc : descriptors) {
                for (Bean definedBean : beanDefinitionList) {
                    if (desc.getName().equals(definedBean.getName())) {
                        desc.getWriteMethod().invoke(bean.getObject(), createBean(definedBean) );
                        break;
                    }
                }
            }
        } catch (Exception e) {
            throw new BeansException(e);
        }
    }
    
    private void autowiredByType(Bean bean) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClazz());
            PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();

            PropertyDescriptor usePd = null;
            List<Bean> foundBeans = new ArrayList<Bean>();
            for (PropertyDescriptor desc : descriptors) {
                for (Bean definedBean : beanDefinitionList) {
                    if (desc.getPropertyType().equals(definedBean.getClazz())) {
                        foundBeans.add(definedBean);
                        usePd = desc;
                    }
                }
            }
            
            if (!foundBeans.isEmpty()) {
                if (foundBeans.size() > 1)
                    throw new BeansException("too many");
                usePd.getWriteMethod().invoke(bean.getObject(), createBean(foundBeans.get(0)) );
            }
        } catch (Exception e) {
            throw new BeansException(e);
        }
    }
    
    private void autowiredByConstructor(Bean bean) {
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
        
        // 解析bean元素autowire属性
        String autowire = beanElem.attributeValue("autowire");
        if ("byName".equals(autowire))
            bean.setAutowire(Autowire.BY_NAME);
        else if ("byType".equals(autowire))
            bean.setAutowire(Autowire.BY_TYPE);
        else if ("constructor".equals(autowire))
            bean.setAutowire(Autowire.CONSTRUCTOR);
        
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
                        throw new BeansException("parse error");
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
