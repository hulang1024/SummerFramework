package summerframework.beans;

import java.util.List;

/**
 * Bean定义
 * @author hulang
 */
public class Bean {
    private String name;
    private String className;
    private Class<?> clazz; // 根据类名加载的类的缓存
    private Object object;
    
    /**
     * 默认单例
     */
    private Scope scope = Scope.SINGLETON;
    
    private boolean singleton = true;
    
    private Autowire autowire = Autowire.DEFAULT;
    
    private Boolean autowireCandidate;
    
    private List<Property> properties;

    
    /**
     * 作用域
     */
    public static enum Scope {
        SINGLETON,
        PROTOTYPE
    }
    
    /**
     * 自动装配
     */
    public static enum Autowire {
        NO,
        BY_NAME,
        BY_TYPE,
        CONSTRUCTOR,
        DEFAULT
    }
    
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getClassName() {
        return className;
    }
    
    public void setClassName(String className) {
        this.className = className;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public List<Property> getProperties() {
        return properties;
    }
    
    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
        singleton = scope == Scope.SINGLETON;
    }

    public boolean isSingleton() {
        return singleton;
    }

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
        if (singleton)
            scope = Scope.SINGLETON;
    }

    public Autowire getAutowire() {
        return autowire;
    }

    public void setAutowire(Autowire autowire) {
        this.autowire = autowire;
    }

    public Boolean getAutowireCandidate() {
        return autowireCandidate;
    }

    public void setAutowireCandidate(Boolean autowireCandidate) {
        this.autowireCandidate = autowireCandidate;
    }
}
