package summerframework.core.context;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import summerframework.core.bean.Bean;
import summerframework.core.bean.Property;

/**
 * 上下文
 * @author hulang
 */
public abstract class ApplicationContext {
    private Map<String, Object> beanMap = new HashMap<String, Object>();

    public void setBean(String name, Bean bean) {
        beanMap.put(name, bean);
    }
    
    public Object getBean(String name) {
        return createBean(beanMap.get(name));
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
                // 注入属性值
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
