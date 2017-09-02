package summerframework.beans;

import java.util.List;

import javax.xml.ws.handler.MessageContext.Scope;

/**
 * Bean描述
 * @author hulang
 */
public class Bean {
    private String name;
    private String className;
    private List<Property> properties;
    private Scope scope = Scope.SINGLETON;
    private boolean singleton = true;
    
    public static enum Scope {
        SINGLETON,
        PROTOTYPE;
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

}
